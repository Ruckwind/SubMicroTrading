/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.factory.HeartbeatFactory;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.ETIConnectionGatewayRequest;
import com.rr.model.generated.internal.events.interfaces.ETIConnectionGatewayResponse;
import com.rr.model.generated.internal.events.interfaces.ETIRetransmitOrderEvents;
import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.model.generated.internal.type.ETIEurexDataStream;
import com.rr.model.generated.internal.type.ETIOrderProcessingType;
import com.rr.om.session.state.StatefulSessionFactory;

public class ETISessionFactory implements StatefulSessionFactory {

    private static final boolean ETI_ORDER_ROUTING = true;

    private final SuperPool<HeartbeatImpl> _heartbeatPool    = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory         _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final ETIConfig _config;

    public ETISessionFactory( ETIConfig cfg ) {
        _config = cfg;
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
    public Event getLogOut( ZString pwdExpiry, int code, Event logon, int nextOutSeqNum, int nextExpectedInSeqNum ) {

        ETISessionLogoutNotificationImpl logoutNotif = new ETISessionLogoutNotificationImpl();

        logoutNotif.getReasonForUpdate().append( "Code " ).append( code );

        return logoutNotif;
    }

    @Override public Event getResendRequest( int beginSeqNum, int endSeqNum )     { return null; }

    @Override
    public Event getSessionLogOn( int heartBtInt, int nextOutSeqNum, int nextExpInSeqNum ) {
        ETISessionLogonRequestImpl logon = new ETISessionLogonRequestImpl();

        logon.setHeartBtIntMS( _config.getHeartBeatIntSecs() * 1000 );
        logon.setPartyIDSessionID( _config.getPartyIDSessionID() );
        logon.getDefaultCstmApplVerIDForUpdate().copy( _config.getETIVersion() );
        logon.getPasswordForUpdate().copy( _config.getSessionLogonPassword() );
        logon.setApplUsageOrders( ETIOrderProcessingType.Automated );
        logon.setApplUsageQuotes( ETIOrderProcessingType.Automated );
        logon.setOrderRoutingIndicator( ETI_ORDER_ROUTING );
        logon.getApplicationSystemNameForUpdate().copy( _config.getAppSystemName() );
        logon.getApplicationSystemVerForUpdate().append( ETIConfig.VERSION );
        logon.getApplicationSystemVendorForUpdate().copy( "SMT" );

        return logon;
    }

    public ETIRetransmitOrderEvents createRetransmitOrderEventsRequest( ETIEurexDataStream stream, short partitionId, ApplMsgID lastApplMsgID ) {
        ETIRetransmitOrderEventsImpl req = new ETIRetransmitOrderEventsImpl();

        req.setRefApplID( stream );
        req.setPartitionID( partitionId );
        lastApplMsgID.toBytes( req.getApplBegMsgIDForUpdate() );

        return req;
    }

    public ETIConnectionGatewayRequest getConnectionGatewayRequest() {
        ETIConnectionGatewayRequestImpl req = new ETIConnectionGatewayRequestImpl();

        req.setPartyIDSessionID( _config.getPartyIDSessionID() );
        req.getPasswordForUpdate().copy( _config.getPassword() );
        return req;
    }

    public ETIConnectionGatewayResponse getConnectionGatewayResponse() {
        ETIConnectionGatewayResponseImpl req = new ETIConnectionGatewayResponseImpl();
        req.setTradSesMode( _config.getEnv() );
        req.setSessionMode( _config.getETISessionMode() );
        req.setGatewayID( _config.getEmulationTestHost() );
        req.setGatewaySubID( _config.getEmulationTestPort() );
        return req;
    }

    /**
     * getSessionLogonReply - only used by the exchange emulator
     */
    public Event getSessionLogonReply() {
        ETISessionLogonResponseImpl rep = new ETISessionLogonResponseImpl();

        rep.setThrottleTimeIntervalMS( ETIConfig.DEFAULT_THROTTLE_PERIOD_MS );
        rep.setHeartBtIntMS( _config.getHeartBeatIntSecs() * 1000 );
        rep.setThrottleNoMsgs( ETIConfig.DEFAULT_THROTTLE_MSGS );
        rep.setThrottleDisconnectLimit( ETIConfig.DEFAULT_THROTTLE_MSGS );
        rep.setTradSesMode( ETIEnv.Simulation );

        return rep;
    }

    public Event getUserLogOn() {
        ETIUserLogonRequestImpl logon = new ETIUserLogonRequestImpl();

        logon.setUserName( _config.getUserId() );
        logon.getPasswordForUpdate().copy( _config.getTraderPassword() );

        return logon;
    }

    /**
     * getUserLogonReply - only used by the exchange emulator
     */
    public Event getUserLogOnReply() {
        ETIUserLogonResponseImpl rep = new ETIUserLogonResponseImpl();

        return rep;
    }
}
