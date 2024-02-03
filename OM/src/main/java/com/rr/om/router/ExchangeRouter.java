/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.router;

import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.Instrument;
import com.rr.core.session.RecoverableSession;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.interfaces.BaseOrderRequest;
import com.rr.om.session.SessionManager;

/**
 * simple exchange router based on instruments exchange, only supports one session per exchange
 */
public final class ExchangeRouter implements OrderRouter {

    private static final Logger _log = LoggerFactory.create( ExchangeRouter.class );

    private final RecoverableSession[] _sessionByExchangeIdx; // array may be sparse
    private final RecoverableSession[] _origDownSess;         // pointer to original downstream array DO NOT MODIFY

    private final String _id;

    public ExchangeRouter( RecoverableSession[] downStream, SessionManager sessMgr ) {
        this( null, downStream, sessMgr );
    }

    public ExchangeRouter( String id, RecoverableSession[] downStream, SessionManager sessMgr ) {
        int maxIdx = -1;

        _id = id;

        _origDownSess = downStream;

        for ( RecoverableSession s : downStream ) {
            Exchange e = sessMgr.getExchange( s );

            if ( e == null ) {
                throw new SMTRuntimeException( "Session " + s.getComponentId() + " doesnt have configured REC so cant find exchange" );
            }

            if ( e.getId() > maxIdx ) maxIdx = e.getId();
        }

        _sessionByExchangeIdx = new RecoverableSession[ maxIdx + 1 ];

        for ( int i = 0; i <= maxIdx; i++ ) {
            _sessionByExchangeIdx[ i ] = null;
        }

        for ( RecoverableSession s : downStream ) {
            Exchange e   = sessMgr.getExchange( s );
            int      idx = e.getId();

            _log.info( "ExchangeRouter associate session " + s.getComponentId() + " with exchange " + e.getExchangeCode() + ", and exchangeIdx=" + idx );

            _sessionByExchangeIdx[ idx ] = s;
        }
    }

    @Override public final EventHandler[] getAllRoutes() {
        return _origDownSess;
    }

    @Override public final EventHandler getRoute( final BaseOrderRequest nos, final EventHandler replyHandler ) {
        final Instrument instrument = nos.getInstrument();
        final int        exId       = ((ExchangeInstrument) instrument).getExchange().getId();

        return _sessionByExchangeIdx[ exId ];
    }

    @Override public void purgeHandler( final EventHandler deadHandler )  { /* nothing */ }

    @Override public void purgeRoute( final ZString clOrdId, Logger log ) { /* nothing */ }

    @Override public String getComponentId() {
        return _id;
    }
}
