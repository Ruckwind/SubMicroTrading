/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.admin.AdminAgent;
import com.rr.core.collections.SMTHashMap;
import com.rr.core.collections.SMTHashSet;
import com.rr.core.collections.SMTMap;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.thread.RunState;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.inst.InstrumentStore;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.type.SecurityUpdateAction;

import java.io.IOException;
import java.util.*;

/**
 * CME Market Data Controller
 * <p>
 * threadsafe handler
 * <p>
 * has an inboundDispatcher to allow POSSIBLE async processing of the event generating thread (depending which dispatcher used)
 * <p>
 * handleNow (and below) must only be invoked by dispatch thread
 * <p>
 * book owned by this controller and only mutated by this controller, hence all writing must be locked to protect against snapping of book while its updated
 * <p>
 * long securityId subscription market data controller
 * <p>
 * All subscriptions loaded from file are considered expected/supported instruments
 * <p>
 * THIS IS RELIANT ON CORRECT CONFIG AND PATCHING TOGETHER OF SESSION TO MKTDATACONTROLLER's
 */

public abstract class MktDataController<T extends MutableFixBook> implements Stopable, MktDataSrc<T> {

    protected static final Logger _log = LoggerFactory.create( MktDataController.class );

    private static final ErrorCode MKTERR1 = new ErrorCode( "MDC001", "MODEL CHANGED " );
    // error message ONLY for use on the dispatch callback
    protected final ReusableString _errMsg = new ReusableString();
    protected final T _dummyBook;
    protected final ZString _srcMIC;
    protected final ReusableString _logMsg = new ReusableString();
    private final EventDispatcher  _inboundDispatcher;
    private final SMTMap<Instrument, T> _instToMktDataItemMap = new SMTHashMap<>( 128, 0.75f );
    private final ReusableString _lastSecurityId = new ReusableString();
    private final BookFactory<T> _bookFactory;
    private final boolean _enqueueIncUpdatesOnGap;
    private final MktDataListener<T> _mktDataListener;
    private final InstrumentStore _instrumentStore;
    private final SMTHashSet<ZString> _subs    = new SMTHashSet<>( 128, 0.75f );
    private final SMTHashSet<ZString> _ignored = new SMTHashSet<>( 128, 0.75f );
    private final Set<ZString> _secDefs = new HashSet<>();
    private final Set<ZString> _pending = new HashSet<>();
    protected     AllEventRecycler _eventRecycler;
    protected T _lastBook;
    protected boolean _lastBookChanged;
    private       String           _id;
    private       boolean          _running = false;
    private Map<T, EventHandler> _books = new HashMap<>( 128 );
    private boolean _allowIntradaySecurityUpdates = false;
    // subscriptions
    private       String              _subscriptionFile;
    private List<String> _pipeLineIds = new ArrayList<>();

    // override the isSubscribed check to allow all updates
    private           boolean  _overrideSubscribeSet            = false;
    private           boolean  _disableDirtyAllBooksOnPacketGap = false;
    private           boolean  _ignoreDirtyOnGap                = false;
    private           int      _maxEnqueueIncUpdatesOnGap       = 10;
    private           int      _skipped;
    private transient RunState _runState                        = RunState.Unknown;

    public MktDataController( String id,
                              String mic,
                              EventDispatcher inboundDispatcher,
                              BookFactory<T> bookFactory,
                              MktDataListener<T> mktDataListener,
                              InstrumentStore instrumentStore,
                              boolean enqueueIncUpdatesOnGap ) {
        super();

        _inboundDispatcher = inboundDispatcher;
        _id                = id;
        _bookFactory       = bookFactory;
        _mktDataListener   = mktDataListener;
        _instrumentStore   = instrumentStore;
        _srcMIC            = new ViewString( mic );

        _enqueueIncUpdatesOnGap = enqueueIncUpdatesOnGap;

        _inboundDispatcher.setHandler( this );

        if ( Math.abs( EventIds.ID_MSGSEQNUMGAP - EventIds.ID_MDINCREFRESH ) != 1 ||
             Math.abs( EventIds.ID_MDINCREFRESH - EventIds.ID_MDSNAPSHOTFULLREFRESH ) != 1 ||
             Math.abs( EventIds.ID_MDSNAPSHOTFULLREFRESH - EventIds.ID_SECURITYDEFINITION ) != 1 ||
             Math.abs( EventIds.ID_SECURITYDEFINITION - EventIds.ID_SECURITYDEFINITIONUPDATE ) != 1 ||
             Math.abs( EventIds.ID_SECURITYDEFINITIONUPDATE - EventIds.ID_PRODUCTSNAPSHOT ) != 1 ||
             Math.abs( EventIds.ID_PRODUCTSNAPSHOT - EventIds.ID_SECURITYSTATUS ) != 1 ) {

            _log.error( MKTERR1, "REGENERATE MODEL AND PUT MDINCREFRESH EVENTS BACK TOGETHER !" );
        }

        _dummyBook = _bookFactory.create( _instrumentStore.getDummyExchInst() );

        MktDataControllerAdmin adminBean = new MktDataControllerAdmin( this );
        AdminAgent.register( adminBean );
    }

    @Override
    public final boolean canHandle() {
        return _running;
    }

    @Override
    public final void handle( final Event msg ) {
        if ( msg != null ) {
            _inboundDispatcher.dispatch( msg );
        }
    }

    @Override
    public final void handleNow( final Event msg ) {

        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_MDINCREFRESH:
            handleIncrementalRefresh( (MDIncRefreshImpl) msg );

            return; // RETURN DONT RECYCLE .. BSE for sample may hold onto whole message

        case EventIds.ID_MDSNAPSHOTFULLREFRESH:
            handleSnapshot( (MDSnapshotFullRefreshImpl) msg );
            break;
        case EventIds.ID_MSGSEQNUMGAP:
            handleGap( (MsgSeqNumGapImpl) msg );
            break;
        case EventIds.ID_SECURITYDEFINITION:
            if ( _allowIntradaySecurityUpdates ) {
                SecurityDefinitionImpl secDef = (SecurityDefinitionImpl) msg;
                applySecurity( secDef );
                // instrument store recycles
            } else {
                getEventRecycler().recycle( msg ); // DISABLE INTRADAY SECURITYDEF DUE TO LOCKING OVERHEAD
            }
            return;
        case EventIds.ID_SECURITYSTATUS:
            if ( _allowIntradaySecurityUpdates ) {
                statusUpdate( msg );
                // instrument store recycles
            } else {
                getEventRecycler().recycle( msg ); // DISABLE INTRADAY SECURITYDEF DUE TO LOCKING OVERHEAD
            }
            return;
        case EventIds.ID_SECURITYDEFINITIONUPDATE: // @TODO add support for security def update
        case EventIds.ID_PRODUCTSNAPSHOT:
        default:
            // dont care
            break;
        }

        getEventRecycler().recycle( msg );
    }

    @Override
    public final String getComponentId() {
        return _id;
    }

    @Override
    public synchronized T getItem( final Instrument instr ) {

        final ZString exchangeSymbol = instr.getExchangeSymbol();

        if ( !_subs.contains( exchangeSymbol ) ) {

            _logMsg.copy( "MktDataController: " + getComponentId() + " subscribe to instrument " ).append( instr.id() );

            _log.info( _logMsg );

            _subs.add( TLC.safeCopy( exchangeSymbol ) );

            _secDefs.add( instr.getSecurityDesc() );
        }

        return getBookForSubscription( instr );
    }

    @Override
    public final boolean hasPipeLineId( String pipeLineId ) {
        return _pipeLineIds.contains( pipeLineId );
    }

    @Override public void setSubscriptionFile( final String subscriptionFile ) {
        _subscriptionFile = subscriptionFile;
    }

    @Override
    public final boolean supports( Instrument inst ) {
        return _secDefs.contains( inst.getSecurityDesc() );
    }

    @Override
    public final List<String> getPipeLineIds() {
        return _pipeLineIds;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        try {
            if ( _subscriptionFile != null ) {
                @SuppressWarnings( "unchecked" )
                SingleMDSrcSubsMgr subscriberMgr = new SingleMDSrcSubsMgr( "simpeSubMgr", this, () -> new BookContextImpl() );

                subscriberMgr.addSubscriptions( _instrumentStore, _subscriptionFile, ctx.getRestoredTimestamp() );
            }
        } catch( IOException e ) {
            throw new SMTRuntimeException( "Error subscribing to file " + _subscriptionFile, e );
        }

        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void startWork() {
        // nothing
    }

    @Override
    public void stopWork() {
        stop();
    }

    @Override
    public final void stop() {
        _inboundDispatcher.setStopping();
        if ( _mktDataListener instanceof Stopable ) {
            ((Stopable) _mktDataListener).stop();
        }
    }

    @Override
    public final void threadedInit() {
        _eventRecycler = new AllEventRecycler();

        _running = true;
    }

    // invoked via reflection
    public final void clear() {
        _books.clear();
        _instToMktDataItemMap.clear();

        _lastSecurityId.reset();
        _lastBook        = null;
        _lastBookChanged = false;

        _mktDataListener.clearMktData();
    }

    public final int getMaxEnqueueIncUpdatesOnGap() {
        return _maxEnqueueIncUpdatesOnGap;
    }

    public final void setMaxEnqueueIncUpdatesOnGap( int maxEnqueueIncUpdatesOnGap ) {
        _maxEnqueueIncUpdatesOnGap = maxEnqueueIncUpdatesOnGap;
    }

    public final boolean isAllowIntradaySecurityUpdates() {
        return _allowIntradaySecurityUpdates;
    }

    public final void setAllowIntradaySecurityUpdates( boolean allowIntradaySecurityUpdates ) {
        _allowIntradaySecurityUpdates = allowIntradaySecurityUpdates;
    }

    public final boolean isDisableDirtyAllBooksOnPacketGap() {
        return _disableDirtyAllBooksOnPacketGap;
    }

    public final void setDisableDirtyAllBooksOnPacketGap( boolean dirtyAllBooksOnChannelOnPacketGap ) {
        _disableDirtyAllBooksOnPacketGap = dirtyAllBooksOnChannelOnPacketGap;
    }

    public final boolean isEnqueueIncUpdatesOnGap() {
        return _enqueueIncUpdatesOnGap;
    }

    public final boolean isIgnoreDirtyOnGap() {
        return _ignoreDirtyOnGap;
    }

    public final void setIgnoreDirtyOnGap( boolean ignoreDirtyOnGap ) {
        _ignoreDirtyOnGap = ignoreDirtyOnGap;
    }

    public final boolean isMarkDirtyEnabled() {
        return !_ignoreDirtyOnGap;
    }

    public final boolean isOverrideSubscribeSet() {
        return _overrideSubscribeSet;
    }

    /**
     * ignore subscription list and process all updates
     *
     * @param overrideSubscribeSet
     */
    public final void setOverrideSubscribeSet( boolean overrideSubscribeSet ) {
        _overrideSubscribeSet = overrideSubscribeSet;
    }

    public final void setPipeIdList( String pipeIdList ) {
        List<String> pipeLineIds = new ArrayList<>();

        if ( pipeIdList != null ) {
            String[] parts = pipeIdList.split( "," );

            for ( String part : parts ) {
                part = part.trim();

                if ( part.length() > 0 ) {
                    pipeLineIds.add( part );
                }
            }
        }

        _pipeLineIds = pipeLineIds;
    }

    protected final void checkForBookDispatch() {
        if ( _lastBookChanged ) {
            notifyUpdate( _lastBook );      // about to change book and flag indicating items of interest so dispatch update
            _lastBookChanged = false;
        }
    }

    protected final EventRecycler getEventRecycler() {
        return _eventRecycler;
    }

    protected final InstrumentStore getInstrumentStore() {
        return _instrumentStore;
    }

    protected final ZString getLastSecurityId() {
        return _lastSecurityId;
    }

    protected abstract void handleIncrementalRefresh( final MDIncRefreshImpl msg );

    protected abstract void handleSnapshot( final MDSnapshotFullRefreshImpl msg );

    protected final boolean isSubscribed( final MDIncRefreshImpl msg, final MDEntryImpl nextEntry ) {

        if ( nextEntry == null ) return false;

        return isSubscribed( nextEntry.getSecurityID() );
    }

    protected final boolean isSubscribed( final ZString securityId ) {

        if ( _overrideSubscribeSet ) {
            return true;
        }

        if ( _subs.contains( securityId ) ) {
            return true;
        }

        logIgnored( securityId );

        return false;
    }

    protected void notifyUpdate( final T book ) {
        if ( book != _dummyBook ) {
            _mktDataListener.marketDataChanged( book );
        } else {
            ++_skipped;
        }
    }

    protected void processBookChange( final EventHandler src, final ZString securityId, final SecurityIDSource idSrc, final ExchangeCode code ) {
        if ( securityId.equals( getLastSecurityId() ) ) {
            return;
        }

        Instrument inst = _instrumentStore.getExchInst( securityId, idSrc, code );
        T          book = getBook( src, inst );

        checkForBookDispatch();

        _lastSecurityId.copy( securityId );
        _lastBook = book;
    }

    protected final void setLastBookDirty() {
        if ( isMarkDirtyEnabled() ) {
            final OptimisticReadWriteLock lock  = _lastBook.getLock();
            long                          stamp = lock.writeLock();
            try {
                _lastBook.setDirty( true );
            } finally {
                lock.unlockWrite( stamp );
            }
        }
    }

    /**
     * Admin Command Control : TO BE USED MANUALLY ONLY DUE TO GC AND POSSIBLE THREAD CONTENTION
     */

    void clearAllBooks() {
        _log.info( "clearAllBooks invoked" );

        for ( MutableFixBook book : _books.keySet() ) {
            book.reset();
        }
    }

    boolean clearBook( String securityDesc ) {
        _log.info( "clearBook invoked " + securityDesc );

        for ( MutableFixBook book : _books.keySet() ) {
            //noinspection EqualsBetweenInconvertibleTypes
            if ( book.getInstrument().getSecurityDesc().equals( securityDesc ) ) {
                book.reset();

                return true;
            }
        }

        return false;
    }

    boolean getBook( String securityDesc, ApiMutatableBook dest ) {
        for ( Book book : _books.keySet() ) {
            //noinspection EqualsBetweenInconvertibleTypes
            if ( book.getInstrument().getSecurityDesc().equals( securityDesc ) ) {
                book.snapTo( dest );

                return true;
            }
        }

        return false;
    }

    Iterator<T> getBookIterator() {
        return _books.keySet().iterator();
    }

    private void applySecurity( final SecurityDefinitionImpl secDef ) {

        if ( !isSubscribed( secDef.getSecurityID() ) ) {
            return;
        }

        if ( secDef.getSecurityUpdateAction() == SecurityUpdateAction.Delete ) {
            _instrumentStore.remove( secDef );
        } else {
            _instrumentStore.add( secDef );
        }

        ZString secDes = secDef.getSecurityDesc();

        if ( _pending.contains( secDes ) ) {
            _logMsg.copy( "MktDataController: " + getComponentId() + " dynamically added instrument which had pending subscribe to instrument " ).
                   append( "secDes=" ).append( secDes );

            _log.info( _logMsg );

            final Instrument instr = getInstrumentStore().getExchInst( secDes, SecurityIDSource.SecurityDesc, secDef.getSecurityExchange() );

            if ( instr != null ) {
                if ( getItem( instr ) != null ) {
                    _pending.remove( secDes );
                }
            }
        }
    }

    private T createAndRegisterItem( final Instrument inst ) {
        T book = _bookFactory.create( inst );
        if ( book == null ) {
            book = _dummyBook;
        }
        _instToMktDataItemMap.put( inst, book );
        return book;
    }

    private void gapDetected( final EventHandler src, final int channelId, final int lastSeqNum, final int seqNum ) {
        if ( lastSeqNum == 0 ) {
            return; // first message, no real gap
        }

        if ( _disableDirtyAllBooksOnPacketGap == false ) {
            _errMsg.copy( "Packet Gap detected, mark books as dirty against src=" );
            _errMsg.append( src.getComponentId() ).append( ", channelId=" ).append( channelId ).
                   append( ", lastSeqNum=" ).append( lastSeqNum ).append( ", gapSeqNum=" ).append( seqNum );
            _errMsg.append( ", gap=" ).append( seqNum - lastSeqNum );

            _log.warn( _errMsg );

            markAllDirty( src, channelId, seqNum );

        } else {
            _errMsg.copy( "Packet Gap detected, IGNORING, against src=" );
            _errMsg.append( src.getComponentId() ).append( ", channelId=" ).append( channelId ).
                   append( ", lastSeqNum=" ).append( lastSeqNum ).append( ", gapSeqNum=" ).append( seqNum );
            _errMsg.append( ", gap=" ).append( seqNum - lastSeqNum );

            _log.warn( _errMsg );
        }
    }

    private T getBook( final EventHandler src, final Instrument inst ) {
        T book = _instToMktDataItemMap.get( inst );

        if ( book == null ) {
            book = createAndRegisterItem( inst );
            _books.put( book, src );
        }

        return book;
    }

    private T getBookForSubscription( final Instrument inst ) {
        T book = _instToMktDataItemMap.get( inst );

        if ( book == null ) {
            book = createAndRegisterItem( inst );
        }

        return book;
    }

    private void handleGap( final MsgSeqNumGapImpl msg ) {
        gapDetected( msg.getEventHandler(), msg.getChannelId(), msg.getPrevSeqNum(), msg.getMsgSeqNum() );
    }

    private void logIgnored( final ZString key ) {
        if ( _ignored.add( key ) ) {
            _logMsg.copy( "MktDataController: Ignoring market data event for instrument int key " ).append( key );
            _log.info( _logMsg );
        }
    }

    private void markAllDirty( final EventHandler src, final int channelId, final int seqNum ) {

        for ( Map.Entry<T, EventHandler> entry : _books.entrySet() ) {

            final EventHandler bookHandler = entry.getValue();
            if ( bookHandler == src ) {

                final T book = entry.getKey();

                if ( channelId > 0 ) {
                    // can filter by channelId

                    if ( ((ExchangeInstrument) book.getInstrument()).getIntSegment() != channelId ) {
                        continue;
                    }
                }

                final OptimisticReadWriteLock lock  = book.getLock();
                long                          stamp = lock.writeLock();
                try {
                    book.setDirty( true );

                    if ( seqNum == 0 ) {
                        book.setMsgSeqNum( 0 );
                        book.setDataSeqNum( 0 );
                    }
                } finally {
                    lock.unlockWrite( stamp );
                }

            }
        }
    }

    private void statusUpdate( final Event msg ) {
        final SecurityStatusImpl secStatus = (SecurityStatusImpl) msg;
        if ( _instrumentStore != null && _instrumentStore.updateStatus( secStatus ) ) {
            if ( secStatus.getSecurityIDSource() != SecurityIDSource.ExchangeSymbol ) {
                return;
            }

            processBookChange( msg.getEventHandler(), secStatus.getSecurityID(), secStatus.getSecurityIDSource(), ((SecurityStatusImpl) msg).getSecurityExchange() );
            _lastBookChanged = true;
        }
    }
}
