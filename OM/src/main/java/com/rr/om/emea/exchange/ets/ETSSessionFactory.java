/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.factory.HeartbeatFactory;
import com.rr.model.generated.internal.events.factory.UTPLogonFactory;
import com.rr.model.generated.internal.events.factory.UTPLogonRejectFactory;
import com.rr.model.generated.internal.events.impl.HeartbeatImpl;
import com.rr.model.generated.internal.events.impl.UTPLogonImpl;
import com.rr.model.generated.internal.events.impl.UTPLogonRejectImpl;
import com.rr.model.generated.internal.type.UTPRejCode;
import com.rr.om.session.state.StatefulSessionFactory;

public class ETSSessionFactory implements StatefulSessionFactory {

    private final SuperPool<HeartbeatImpl> _heartbeatPool    = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory         _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final SuperPool<UTPLogonImpl> _logonPool    = SuperpoolManager.instance().getSuperPool( UTPLogonImpl.class );
    private final UTPLogonFactory         _logonFactory = new UTPLogonFactory( _logonPool );

    private final SuperPool<UTPLogonRejectImpl> _logoutPool    = SuperpoolManager.instance().getSuperPool( UTPLogonRejectImpl.class );
    private final UTPLogonRejectFactory         _logoutFactory = new UTPLogonRejectFactory( _logoutPool );

    @SuppressWarnings( "unused" )
    private final ETSConfig _config;

    public ETSSessionFactory( ETSConfig utpConfig ) {
        _config = utpConfig;
    }

    @Override
    public Event createForceSeqNumResetMessage( int nextMsgSeqNoOut ) {
        return null;
    }

    @Override public Event createGapFillMessage( int beginSeqNo, int uptoSeqNum ) { return null; }

    @Override
    public Event getHeartbeat( ZString testReqID ) {

        HeartbeatImpl hb = _heartbeatFactory.get();
        hb.getTestReqIDForUpdate().setValue( testReqID );

        return hb;
    }

    @Override
    public Event getLogOut( ZString logMsg, int code, Event logon, int nextOutSeqNum, int nextExpectedInSeqNum ) {

        UTPLogonRejectImpl logout = _logoutFactory.get();

        logout.getRejectTextForUpdate().setValue( logMsg );
        logout.setLastMsgSeqNumRcvd( (nextExpectedInSeqNum > 0) ? (nextExpectedInSeqNum - 1) : 0 );
        logout.setLastMsgSeqNumSent( (nextOutSeqNum > 0) ? (nextOutSeqNum - 1) : 0 );
        logout.setRejectCode( UTPRejCode.InvalidSequenceNumber );

        return logout;
    }

    @Override public Event getResendRequest( int beginSeqNum, int endSeqNum )     { return null; }

    @Override
    public Event getSessionLogOn( int heartBtInt, int nextOutSeqNum, int nextExpectedInSeqNum ) {
        UTPLogonImpl logon = _logonFactory.get();

        logon.setLastMsgSeqNum( (nextExpectedInSeqNum > 0) ? (nextExpectedInSeqNum - 1) : 0 );

        return logon;
    }
}
