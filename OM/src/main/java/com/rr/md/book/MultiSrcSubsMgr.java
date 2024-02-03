/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.annotations.SMTPreRestore;
import com.rr.core.collections.SMTHashMap;
import com.rr.core.collections.SMTMap;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.model.book.ListenerMktDataContextWrapper;
import com.rr.core.model.book.SingleOwnerListenerContextWrapperImpl;
import com.rr.core.thread.RunState;
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
 * each SimpleSubscriberMgr can have 1 BookSource for each MIC
 * <p>
 * when book changes invoke each registered callback
 * <p>
 * Uses the Context on a book to store the subscribers to avoid an extra map lookup
 * <p>
 * Threadsafe, note copy on write used to avoid blocking on the "changed" method which is invoked per tick
 * This means it IS possible to still receive an update after removing subscription
 * <p>
 * QHBarSession only generates the bulk subscription via a subscription listener registered with the subscription manager
 * <p>
 * Calling MarketDataSource.getItem  causes the source to create an item for the instrument
 * the subsequent call to InstrumentSubscriptionListener.changed  is used by QHBarSession to start subscription
 * Both calls are needed to same session to make subscription work
 */
public class MultiSrcSubsMgr<T extends MktDataWithContext, C extends Context> implements MktDataSubsMgr<T> {

    private static final Logger _log = LoggerFactory.create( MultiSrcSubsMgr.class );

    private transient final String                                    _id;
    private transient final SMTMap<Instrument, AtomicInteger>         _instrumentCounts = new SMTHashMap<>( 8192, 0.75f );
    /**
     * retain map of subscriber to books for cancellation
     */
    private transient final Map<MktDataListener<T>, LinkedHashSet<T>> _subscribersBooks = new ConcurrentHashMap<>();
    private transient final Set<Instrument>                           _tmpInstSet       = new HashSet<>( 128, 0.75f );
    private transient       MktDataSrc<T>[]                           _mdSrcs           = new MktDataSrc[ 0 ];
    private transient       MktDataSrc<T>[]                           _allSrcs          = new MktDataSrc[ 0 ];
    private transient       InstrumentSubscriptionListener[]          _listeners        = new InstrumentSubscriptionListener[ 0 ];
    private transient       NoArgsFactory<C>                          _factory;
    private transient RunState _runState;

    public MultiSrcSubsMgr( String id ) {
        _id = id;
    }

    public MultiSrcSubsMgr( String id, MktDataSrc<T>[] srcs, NoArgsFactory<C> factory ) {
        super();
        _id      = id;
        _allSrcs = srcs;
        _factory = factory;
    }

    /**
     * read in subscriptions from file, with one securityDescription or int symbol id per line
     *
     * @param instrumentStore
     * @param subscriptionFile
     * @throws IOException
     */
    @Override public synchronized void addSubscriptions( InstrumentLocator instrumentStore, String subscriptionFile, long fromTimestamp ) throws IOException {

        final HashSet<Instrument> addedKeys = new HashSet<>( 128 );

        InstUtils.instLoadFromFile( instrumentStore, subscriptionFile, _instrumentCounts, ( inst ) -> bulkAddInst( inst, addedKeys ) );

        for ( InstrumentSubscriptionListener l : _listeners ) {
            notifySubscriptionChange( addedKeys, l, fromTimestamp );
        }
    }

    @Override public synchronized void addSubscriptionListener( final InstrumentSubscriptionListener listener ) {
        _listeners = Utils.arrayCopyAndAddEntry( _listeners, listener );

        final HashSet<Instrument> addedKeys = new HashSet<>( _instrumentCounts.keySet() );

        notifySubscriptionChange( addedKeys, listener, Constants.UNSET_LONG );
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

    /**
     * propogate the published event
     */
    @SuppressWarnings( "unchecked" )
    @Override public void receive( final ZString subject, final Instrument inst, final Object data, final long timestamp, final long seqNum ) {
        final T book = getMKtDataItem( inst );

        ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();

        if ( ctx != null ) {
            final MktDataListener<T>[] listener = ctx.getListeners();
            final int                  size     = listener.length;

            for ( int i = 0; i < size; i++ ) {
                listener[ i ].receive( subject, inst, data, timestamp, seqNum );
            }
        }
    }

    @Override public RunState getRunState() { return _runState; }

    @Override public RunState setRunState( final RunState newState ) {
        final RunState old = _runState;
        _runState = newState;
        return old;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        /* nothing */
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

    @Override public synchronized void addSource( MktDataSrc<T> src ) {
        final MktDataSrc<T>[] newArr = Utils.arrayCopyAndAddEntry( _allSrcs, src );

        if ( newArr == _allSrcs ) return;

        _allSrcs = newArr;

        boolean needSort = false;

        MktDataSrc.Priority lastPriority = MktDataSrc.Priority.High;

        for ( MktDataSrc m : _allSrcs ) {
            if ( m != null ) {
                if ( lastPriority.compareTo( m.getPriority() ) > 0 ) {
                    needSort = true;
                    break;
                }
                lastPriority = m.getPriority();
            }
        }

        if ( needSort ) {
            Arrays.sort( _allSrcs, 0, _allSrcs.length, Comparator.comparing( MktDataSrc::getPriority ) );
        }
    }

    @Override public synchronized void addSubscriptions( final InstrumentLocator instrumentStore, final Set<? extends Instrument> insts, final long fromTimestamp ) throws IOException {

        final HashSet<Instrument> addedKeys = new LinkedHashSet<>( 128 );

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
        final HashSet<Instrument> addedKeys = new LinkedHashSet<>( 128 );

        for ( Instrument inst : insts ) {
            T md = doSubscribe( callback, inst, ( i ) -> addedKeys.add( (Instrument) i ) );
            out.add( md );
        }

        if ( addedKeys.size() > 0 ) {
            /**
             * for multiple MDSrc listeners they must filter out those MIC's not supported
             */
            for ( InstrumentSubscriptionListener l : _listeners ) {
                notifySubscriptionChange( addedKeys, l, fromTimestamp );
            }
        }
    }

    @Override public MktDataSrc<T> findSource( final Instrument inst ) {
        for ( MktDataSrc<T> src : _allSrcs ) {
            if ( src.supports( inst ) ) {
                return src;
            }
        }

        return null;
    }

    @Override public synchronized void removeSource( MktDataSrc<T> src ) {

        _allSrcs = Utils.arrayCopyAndRemoveEntry( _allSrcs, src );
    }

    @Override @SuppressWarnings( "unchecked" ) public synchronized T subscribe( MktDataListener<T> callback, Instrument inst, long fromTimestamp ) {

        return doSubscribe( callback, inst, ( i ) -> firstSubscription( (Instrument) i, fromTimestamp ) );
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
        throw new SMTRuntimeException( getComponentId() + " cannot be used for publish on " + subject + " use the globalEventManager instead" );
    }

    @Override public String toString() {
        return "MultiSrcSubsMgr{ " + _id + '\'' +
               ", _allSrcs=" + Arrays.toString( _allSrcs ) +
               '}';
    }

    @SMTPreRestore public void preRestore( final SMTStartContext ctx ) {
        /**
         * mdSrcs is reflectively set from bootstrap and will wipe out any srcs added by other loaders
         */
        for ( MktDataSrc src : _mdSrcs ) {
            addSource( src );
        }
    }

    /**
     * for QHbarSrc the bulk subscription is via this subscription listener change
     *
     * @param addedKeys
     * @param l
     */
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

    @SuppressWarnings( "unchecked" )
    private synchronized T doSubscribe( MktDataListener<T> callback, Instrument inst, ZConsumer onNewAddition ) {

//        if ( inst instanceof BackTestProxy ) {
//            BackTestProxy<Instrument> proxy = (BackTestProxy<Instrument>)inst;
//
//            inst = proxy.getProxy();
//        }

        T book = getMKtDataItem( inst );

        if ( book == null ) {
            return null;
        }

        Set<T> books = getBookSet( callback );
        books.add( book );

        ListenerMktDataContextWrapper<T, C> ctx = (ListenerMktDataContextWrapper<T, C>) book.getContextWrapper();

        boolean addedContext = false;

        if ( ctx == null ) {
            C context = _factory.create();
            ctx = new SingleOwnerListenerContextWrapperImpl<>( this, context );
            book.setContextWrapper( ctx );
            addedContext = true;
        }

        if ( ctx.getOwner() != this ) {
            book = getMKtDataItem( inst );
            /**
             * cannot have a book shared across multiple subscribers (bookSrc's) as the subscription chain could cause
             * threading errors with multiple subscribers invoking the same listener concurrently
             * shouldnt be possible as the bookSrc owns the book
             */
            throw new SMTRuntimeException( "ERROR BOOK SUBSCRIBED TO ACROSS MULTIPLE SUBSCRIBER MANAGERS, instId=" + inst.id() );
        }

        if ( ctx.addListener( callback ) ) {
            AtomicInteger cnt = addNewInstEntry( inst, 0 );

            if ( cnt.incrementAndGet() == 1 || addedContext ) {
                onNewAddition.accept( inst );
            }
        }

        if ( _log.isEnabledFor( Level.info ) ) {
            ReusableString rs = TLC.instance().pop();
            rs.copy( getComponentId() ).append( " subscribe " ).append( callback.id() ).append( " to " );
            if ( book instanceof HasInstanceId ) {
                rs.append( "[#" ).append( ((HasInstanceId) book).getInstanceId() ).append( "] " );
            }
            rs.append( inst.id() )
              .append( ", book listener count=" ).append( ctx.getListeners().length );
            _log.info( rs );
            TLC.instance().pushback( rs );
        }

        return book;
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

    // O(n) but only used on initial subscription
    private T getMKtDataItem( final Instrument inst ) {

        for ( int idx = 0; idx < _allSrcs.length; ++idx ) {
            MktDataSrc<T> src = _allSrcs[ idx ];
            if ( src.supports( inst ) ) {
                T item = src.getItem( inst );

                if ( _log.isEnabledFor( Level.info ) ) {
                    ReusableString rs = TLC.instance().pop();
                    rs.copy( getComponentId() ).append( " getMktDataItem for " ).append( inst.id() ).append( " found supporting dataSrc of " ).append( src.id() );
                    if ( item instanceof HasInstanceId ) {
                        rs.append( " itemId [#" ).append( ((HasInstanceId) item).getInstanceId() ).append( "] " );
                    }
                    _log.log( Level.trace, rs );
                    TLC.instance().pushback( rs );
                }

                return item;
            }
        }

        return null;
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
