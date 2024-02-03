/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.algo.t1;

import com.rr.algo.SimpleAlgo;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Book;
import com.rr.core.model.Event;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.book.BookEntryImpl;
import com.rr.core.model.book.BookLevelEntry;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.session.RecoverableSession;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.factory.NewOrderSingleFactory;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.model.generated.internal.type.HandlInst;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;
import com.rr.om.router.OrderRouter;

/**
 * Implementation of T1 handler .. cross the spread every X ticks
 * <p>
 * EventHandler could be EventProcesor or a direct exchange session
 */
public final class T1Algo implements SimpleAlgo {

    private static final Logger _log = LoggerFactory.create( T1Algo.class );

    private static final ZString CME       = new ViewString( "XMCE" );
    private static final ZString T1ACCOUNT = new ViewString( "T1SMT" );

    private final AllEventRecycler      _exchangeEventRecycler;
    private final NewOrderSingleFactory _nosFactory;
    private final int                   _nosMod;
    private final int                   _pow2Mask;
    private final BookLevelEntry        _tmpDest  = new BookEntryImpl();
    private final OrderRouter           _router;
    private final ReusableString        _debugMsg = new ReusableString();
    private final String _id;
    private boolean _debug = true;

    public T1Algo( int nosMod, OrderRouter router ) {
        this( null, nosMod, router );
    }

    public T1Algo( String id, int nosMod, OrderRouter router ) {
        _id = id;

        _exchangeEventRecycler = new AllEventRecycler();

        SuperPool<NewOrderSingleImpl> _nosPool = SuperpoolManager.instance().getSuperPool( NewOrderSingleImpl.class );
        _nosFactory = new NewOrderSingleFactory( _nosPool );

        _nosMod = nosMod;

        _pow2Mask = (isPowerOfTwo( _nosMod )) ? (_nosMod - 1) : 0;

        _router = router;
    }

    @Override
    public void changed( final Book book ) {
        if ( _debug ) {
            _debugMsg.copy( "T1Algo.changed() : " );
            book.dump( _debugMsg );

            _log.info( _debugMsg );
        }

        if ( book.isValid() ) {
            // we have top of book ... should check not crossed and not zero

            final int bookTickCount = (int) book.getTickCount();

            if ( _pow2Mask > 0 ) { // mod is a power of two

                if ( (bookTickCount & _pow2Mask) == 0 ) {
                    sendOrder( book );
                }
            } else if ( (bookTickCount % _nosMod) == 0 ) { // mod isnt power of 2 so have to use MOD
                sendOrder( book );
            }
        }
    }

    @Override
    public void handleMarketEvent( final Event mktMsg ) {
        _exchangeEventRecycler.recycle( mktMsg );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public int getNOSMod() {
        return _nosMod;
    }

    public boolean isDebug() {
        return _debug;
    }

    public void setDebug( boolean debug ) {
        _debug = debug;
    }

    private boolean isPowerOfTwo( int val ) {
        int nxtPow2 = 1;

        while( nxtPow2 < val ) {
            nxtPow2 <<= 1;
        }

        return val == nxtPow2;
    }

    private void sendOrder( final Book book ) {
        // cross the spread to trade
        book.getAskEntry( 0, _tmpDest );

        final NewOrderSingleImpl nos = _nosFactory.get();

        nos.setSide( Side.Buy );
        nos.getAccountForUpdate().setValue( T1ACCOUNT );
        nos.setOrdType( OrdType.Limit );
        nos.setHandlInst( HandlInst.AutoExecPrivate );
        nos.setOrderReceived( book.getEventTimestamp() );
        nos.setInstrument( ((ExchangeInstrument) book.getInstrument()) );
        nos.setOrderQty( _tmpDest.getQty() );
        nos.setPrice( _tmpDest.getPrice() );

        final ZString id = book.getInstrument().getExchangeSymbol();
        nos.getClOrdIdForUpdate().append( id ).append( '_' ).append( book.getMsgSeqNum() ).append( ' ' ).append( book.getEventTimestamp() );

        nos.getSymbolForUpdate().append( book.getInstrument().getExchangeSymbol() );
        nos.getExDestForUpdate().copy( CME );

        final RecoverableSession sess = (RecoverableSession) _router.getRoute( nos, null );
        nos.setEventHandler( sess );

        sess.handle( nos );
    }
}
