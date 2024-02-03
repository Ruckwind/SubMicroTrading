/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.instrument;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Auction;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;

import java.util.*;

public class MultiMktExchangeSession extends SingleExchangeSession {

    private final Map<ZString, ExchangeSession> _sessions;

    public MultiMktExchangeSession( ZString id,
                                    Calendar openCal,
                                    Calendar startContinuous,
                                    Calendar endContinuous,
                                    Calendar halfDayCal,
                                    Calendar endCal,
                                    Auction openAuction,
                                    Auction intraDayAuction,
                                    Auction closeAuction,
                                    Map<ZString, ExchangeSession> sessions,
                                    ExchangeCode exchangeCode ) {

        super( id, openCal, startContinuous, endContinuous, halfDayCal, endCal, openAuction, intraDayAuction, closeAuction, exchangeCode );

        _sessions = (sessions != null) ? sessions : new HashMap<>();
    }

    @Override
    public ExchangeSession getExchangeSession( ZString marketSegment ) {

        if ( marketSegment == null ) return this;

        ExchangeSession sess = _sessions.get( marketSegment );

        return (sess == null) ? this : sess;
    }

    @Override
    public void setToday() {
        super.setToday();

        if ( _sessions != null ) {
            for ( ExchangeSession es : _sessions.values() ) {
                es.setToday();
            }
        }
    }

    @Override
    public ReusableString dump( ReusableString buf ) {
        buf.append( "MultiMkttSession defaultSess {" );
        super.dump( buf );
        buf.append( "}\n      segment sessions\n" );
        if ( _sessions != null ) {
            Set<ExchangeSession> _vals = new HashSet<>( _sessions.values() );
            for ( ExchangeSession sess : _vals ) {
                buf.append( "        segmentSession {" );
                sess.dump( buf );
                buf.append( "}" );
            }

            buf.append( "\n    segmentToSessionIDMap\n" );

            for ( Map.Entry<ZString, ExchangeSession> entry : _sessions.entrySet() ) {
                ZString         segment = entry.getKey();
                ExchangeSession sess    = entry.getValue();
                buf.append( "        segment " ).append( segment ).append( " to " ).append( sess.getId() ).append( "\n" );
            }
        }

        return buf;
    }
}
