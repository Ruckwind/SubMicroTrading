/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium.recovery;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.emea.exchange.millenium.MilleniumController;
import com.rr.om.emea.exchange.millenium.MilleniumSessionFactory;
import com.rr.om.emea.exchange.millenium.MilleniumSocketConfig;

public final class MilleniumRecoveryController extends MilleniumController {

    private static final Logger _log = LoggerFactory.create( MilleniumRecoveryController.class );

    private int _lastRequestAppId = -1;

    public MilleniumRecoveryController( SeqNumSession session, MilleniumSocketConfig config ) {
        super( session, new MilleniumRecoveryStateFactory( config ), new MilleniumSessionFactory( config ) );
    }

    public void nextRetryRequest() {
        _lastRequestAppId = nextAppId( _lastRequestAppId + 1 );

        if ( _lastRequestAppId == -1 ) {
            _log.info( "MilleniumRecoveryController: All valid appId's have had messages requested ... disconnecting recovery session" );

            _session.disconnect( false );
        }

        request( _lastRequestAppId );
    }

    public void retryLastRerequest() {
        request( _lastRequestAppId );
    }

    public void sendReplayRequests() {
        _lastRequestAppId = -1;
        nextRetryRequest();
    }

    private int nextAppId( int nextAppId ) {
        for ( int curAppId = nextAppId; curAppId < MAX_SEQ_NUM; curAppId++ ) {
            int curSeqNum = _lastAppIdSeqNum[ curAppId ];

            if ( curSeqNum > 0 ) {
                return curAppId;
            }
        }

        return -1;
    }

    private void request( int curAppId ) {
        if ( curAppId >= 0 ) {
            int   curSeqNum = _lastAppIdSeqNum[ curAppId ];
            Event logon     = _sessionFactory.getRerequest( curAppId, curSeqNum );
            send( logon, false );
        }
    }
}