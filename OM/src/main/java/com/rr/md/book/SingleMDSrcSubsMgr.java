/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.collections.SMTHashMap;
import com.rr.core.collections.SMTMap;
import com.rr.core.component.CompRunState;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZConsumer;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.model.book.ListenerMktDataContextWrapper;
import com.rr.core.model.book.SingleOwnerListenerContextWrapperImpl;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;
import com.rr.inst.InstUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * allow multiple subscriptions to same book
 * <p>
 * each SimpleSubscriberMgr can have ONL 1 BookSource
 * <p>
 * when book changes invoke each registered callback
 * <p>
 * Uses the Context on a book to store the subscribers to avoid an extra map lookup
 * <p>
 * Threadsafe, note copy on write used to avoid blocking on the "changed" method which is invoked per tick
 * This means it IS possible to still receive an update after removing subscription
 * <p>
 * Calling MarketDataSource.getItem  causes the source to create an item for the instrument
 * the subsequent call to InstrumentSubscriptionListener.changed  is used by QHBarSession to start subscription
 * Both calls are needed to same session to make subscription work
 */
public final class SingleMDSrcSubsMgr<T extends MktDataWithContext, C extends Context> implements MktDataSubsMgr<T> {

    private static final Logger _log = LoggerFactory.create( SingleMDSrcSubsMgr.class );

    private final    String                                    _id;
    private final    SMTMap<Instrument, AtomicInteger>         _instrumentCounts = new SMTHashMap<>( 8192, 0.75f );
    /**
     * retain map of subscriber to books for cancellation
     */
    private final    Map<MktDataListener<T>, LinkedHashSet<T>> _subscribersBooks = new ConcurrentHashMap<>();
    private final    Set<Instrument>                           _tmpInstSet       = new HashSet<>( 128, 0.75f );
    private          MktDataSrc<T>                             _mdSrc;
    private          InstrumentSubscriptionListener[]          _listeners        = new InstrumentSubscriptionListener[ 0 ];
    private          NoArgsFactory<C>                          _factory;
    private volatile CompRunState                              _compRunState     = CompRunState.Initial;

    public SingleMDSrcSubsMgr( String id ) {
        _id = id;
    }

    public SingleMDSrcSubsMgr( String id, MktDataSrc<T> src, NoArgsFactory<C> factory ) {
        super();
        _id      = id;
        _mdSrc   = src;
        _factory = factory;
    }

    @Override public synchronized void addSubscriptionListener( final InstrumentSubscriptionListener listener ) {

        _listeners = Utils.arrayCopyAndAddEntry( _listeners, listener );
    }

    /**
     * read in subscriptions from file, with one securityDescription or int symbol id per line
     *
     * @param InstrumentLocator
     * @param subscriptionFile
     * @throws IOException
     */
    @Override public synchronized void addSubscriptions( InstrumentLocator InstrumentLocator, String subscriptionFile, long fromTimestamp ) throws IOException {

        final HashSet<Instrument> addedKeys = new HashSet<>( 128 );

        InstUtils.instLoadFromFile( InstrumentLocator, subscriptionFile, _instrumentCounts, ( inst ) -> bulkAddInst( inst, addedKeys ) );

        for ( InstrumentSubscriptionListener l : _listeners ) {
            notifySubscriptionChange( addedKeys, l, fromTimestamp );
        }
    }

    @Override public synchronized void getSubscribedInstruments( final Set<Instrument> dest ) {
        dest.addAll( _instrumentCounts.keySet() );
    }

    @Override @SuppressWarnings( "unchecked" ) public void clearMktData() {
        for ( Map.Entry<MktDataListener<T>, LinkedHashSet<T>> entry : _subscribersBooks.entrySet() ) {
            Set<T> books = entry.getValue();

            for ( T book : books ) {
                final ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();
                if ( ctx != null ) {
                    ctx.clear();
                }
                book.setContextWrapper( null );
            }
        }
        _subscribersBooks.clear();
    }

    @Override public void receive( final ZString subject, final Instrument inst, final Object data, final long timestamp, final long seqNum ) {
        throw new SMTRuntimeException( getComponentId() + " cannot be used for publish on " + subject );
    }

    @Override public CompRunState getCompRunState() {  return _compRunState; }

    private synchronized boolean setCompRunState( CompRunState state ) {
        boolean changed = false;
        if ( CompRunState.procStateChange( id(), _compRunState, state ) ) {
            _compRunState = state;
            changed = true;
        }

        return changed;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        setCompRunState( CompRunState.Initialised );
    }

    @Override public String id() {
        return _id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public void marketDataChanged( T book ) {
        @SuppressWarnings( "unchecked" ) ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();
        if ( ctx != null ) {
            final MktDataListener<T>[] listener = ctx.getListeners();
            final int                  size     = listener.length;

            for ( int i = 0; i < size; i++ ) {
                listener[ i ].marketDataChanged( book );
            }
        }
    }

    @Override public void addSource( final MktDataSrc<T> src ) {
        throw new SMTRuntimeException( getComponentId() + " doesnt support addSource" );
    }

    @Override public synchronized void addSubscriptions( InstrumentLocator InstrumentLocator, Set<? extends Instrument> insts, long fromTimestamp ) throws IOException {

        final HashSet<Instrument> addedKeys = new HashSet<>( 128 );

        for ( Instrument inst : insts ) {
            if ( !_instrumentCounts.containsKey( inst ) ) {
                bulkAddInst( inst, addedKeys );
            }
        }

        for ( InstrumentSubscriptionListener l : _listeners ) {
            notifySubscriptionChange( addedKeys, l, fromTimestamp );
        }
    }

    @Override @SuppressWarnings( "unchecked" ) public synchronized void bulkSubscribe( MktDataListener<T> callback, List<? extends Instrument> insts, List<T> out, long fromTimestamp ) {
        final HashSet<Instrument> addedKeys = new HashSet<>( 128 );

        for ( Instrument inst : insts ) {
            T md = getItemFromSrc( callback, inst, ( i ) -> addedKeys.add( (Instrument) i ), fromTimestamp );
            out.add( md );
        }

        for ( InstrumentSubscriptionListener l : _listeners ) {
            notifySubscriptionChange( addedKeys, l, fromTimestamp );
        }
    }

    @Override public MktDataSrc<T> findSource( final Instrument inst ) {
        return _mdSrc;
    }

    @Override public void removeSource( final MktDataSrc<T> src ) {
        throw new SMTRuntimeException( getComponentId() + " doesnt support removeSource" );
    }

    @Override @SuppressWarnings( "unchecked" ) public synchronized T subscribe( MktDataListener<T> callback, Instrument inst, long fromTimestamp ) {
        return getItemFromSrc( callback, inst, ( i ) -> firstSubscription( (Instrument) i, fromTimestamp ), fromTimestamp );
    }

    @SuppressWarnings( "unchecked" )
    @Override public synchronized void unsubscribe( MktDataListener<T> callback, Instrument inst ) {

        if ( callback == null || inst == null ) return;

        Set<T> books = _subscribersBooks.get( callback );

        if ( books != null ) {
            T bookForInst = null;

            for ( T book : books ) {
                if ( inst.isSame( book.getInstrument() ) ) {
                    bookForInst = book;

                    removeListenerFromBook( callback, book );

                    break;
                }
            }

            if ( bookForInst != null ) books.remove( bookForInst );
        }
    }

    @Override public synchronized void unsubscribeAll( MktDataListener<T> callback ) {

        if ( callback == null ) return;

        Set<T> books = _subscribersBooks.get( callback );

        if ( books != null ) {
            for ( T book : books ) {
                removeListenerFromBook( callback, book );
            }

            books.clear();
        }
    }

    @Override public void publish( final ZString subject, final Instrument inst, final Object data, final long timestamp, final long seqNum ) {
        throw new SMTRuntimeException( getComponentId() + " cannot be used for publish on " + subject );
    }

    protected void notifySubscriptionChange( final HashSet<Instrument> addedKeys, final InstrumentSubscriptionListener l, long fromTimestamp ) {
        l.changed( addedKeys, true, fromTimestamp );
    }

    LinkedHashSet<T> getBookSet( MktDataListener<T> callback ) {
        LinkedHashSet<T> books = _subscribersBooks.computeIfAbsent( callback, k -> new LinkedHashSet<>() );
        return books;
    }

    private AtomicInteger addNewInstEntry( final Instrument inst, final int initialVal ) {
        AtomicInteger cnt = _instrumentCounts.get( inst );

        if ( cnt == null ) {
            cnt = new AtomicInteger( initialVal );
            _instrumentCounts.put( inst, cnt );
        }
        return cnt;
    }

    private AtomicInteger bulkAddInst( final Instrument inst, final HashSet<Instrument> addedKeys ) {
        addedKeys.add( inst );

        return addNewInstEntry( inst, 1 );
    }

    private AtomicInteger checkForFirstSubscription( final Instrument inst, long fromTimestamp ) {
        AtomicInteger cnt = addNewInstEntry( inst, 0 );

        if ( cnt.incrementAndGet() == 1 ) {
            firstSubscription( inst, fromTimestamp );
        }

        return cnt;
    }

    private void firstSubscription( final Instrument inst, long fromTimestamp ) {
        _tmpInstSet.clear();
        _tmpInstSet.add( inst );
        for ( InstrumentSubscriptionListener l : _listeners ) {
            l.changed( _tmpInstSet, true, fromTimestamp );
        }
    }

    private void fullyUnsubscribed( final Instrument inst ) {
        _tmpInstSet.clear();
        _tmpInstSet.add( inst );
        for ( InstrumentSubscriptionListener l : _listeners ) {
            l.changed( _tmpInstSet, false, 0 );
        }
    }

    @SuppressWarnings( "unchecked" )
    private synchronized T getItemFromSrc( MktDataListener<T> callback, Instrument inst, ZConsumer onNewAddition, long fromTimestamp ) {
        T book = _mdSrc.getItem( inst );

        if ( book == null ) {
            return null;
        }

        Set<T> books = getBookSet( callback );
        books.add( book );

        ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();

        if ( ctx == null ) {
            C context = _factory.create();
            ctx = new SingleOwnerListenerContextWrapperImpl<>( this, context );
            book.setContextWrapper( ctx );
        }

        if ( ctx.getOwner() != this ) {
            /**
             * cannot have a book shared across multiple subscribers (bookSrc's) as the subscription chain could cause
             * threading errors with multiple subscribers invoking the same listener concurrently
             * shouldnt be possible as the bookSrc owns the book
             */
            throw new SMTRuntimeException( "ERROR BOOK SUBSCRIBED TO ACROSS MULTIPLE SUBSCRIBER MANAGERS, " +
                                           "id=" + inst.id() + ", uniqueInstId=" + inst.getUniqueInstId() );
        }

        if ( ctx.addListener( callback ) ) {
            AtomicInteger cnt = addNewInstEntry( inst, 0 );

            if ( cnt.incrementAndGet() == 1 ) {
                onNewAddition.accept( inst );
            }
        }

        if ( _log.isEnabledFor( Level.info ) ) {
            ReusableString rs = TLC.instance().pop();
            rs.copy( getComponentId() ).append( " subscribe " ).append( callback.id() ).append( " to " ).append( inst.getSecurityDesc() ).append( ", book listener count=" ).append( ctx.getListeners().length );
            _log.info( rs );
            TLC.instance().pushback( rs );
        }

        return book;
    }

    private void removeListenerFromBook( final MktDataListener<T> callback, final T book ) {
        @SuppressWarnings( "unchecked" )
        ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();

        Instrument inst = book.getInstrument();

        if ( ctx != null ) {
            if ( ctx.removeListener( callback ) ) {
                AtomicInteger cnt = _instrumentCounts.get( inst );

                if ( cnt != null && cnt.decrementAndGet() == 0 ) {
                    fullyUnsubscribed( inst );
                }
            }
        }
    }
}
