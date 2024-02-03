/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.model.generated.internal.type.ETISessionMode;

public class ETISocketConfig extends SocketConfig implements ETIConfig {

    private boolean _isGatewaySession = false;

    private boolean        _disconnectOnMissedHB           = true;
    private int            _heartBeatIntSecs               = 30;
    private boolean        _isRecoverFromLoginSeqNumTooLow = false;
    private int            _userId;
    private ZString        _password                       = new ViewString( "" );
    private ReusableString _etiVersion                     = new ReusableString( "1.0" );
    private ZString        _sessionLogonPassword           = new ViewString( "" );
    private ZString        _appSystemName                  = new ViewString( "SMT" );

    private ETIEnv         _env            = ETIEnv.Simulation;
    private ETISessionMode _etiSessionMode = ETISessionMode.HF;

    private long _locationId        = 0;
    private int  _partyIDSessionID;
    private int  _emulationTestPort = 24001;    // @TODO REMOVE HARD CODED PORT
    private int  _emulationTestHost = (127 << 24) + 1;

    private ZString _traderPassword   = new ViewString( "" );
    private ZString _uniqueClientCode = new ReusableString( "" );

    private int _expectedRequests = 10000; // used to presize map of seqNum to mktClOrdId

    private boolean _forceTradingServerLocalhost = false;

    public ETISocketConfig() {
        super( com.rr.model.generated.internal.events.factory.AllEventRecycler.class );
    }

    public ETISocketConfig( String id ) {
        super( id );
    }

    public ETISocketConfig( boolean disconnectOnMissedHB,
                            Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            int userId,
                            ZString password,
                            boolean isGwySession,
                            ETIEnv etiEnv,
                            ETISessionMode etiSessionMode ) {

        super( recycler, isServer, host, adapter, port );

        _disconnectOnMissedHB = disconnectOnMissedHB;
        _userId               = userId;
        _password             = password;
        _isGatewaySession     = isGwySession;
        _env                  = etiEnv;
        _etiSessionMode       = etiSessionMode;
    }

    public ETISocketConfig( Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            int userId,
                            ZString password,
                            boolean isGwySession,
                            ETIEnv etiEnv,
                            ETISessionMode etiSessionMode ) {

        this( true, recycler, isServer, host, adapter, port, userId, password,
              isGwySession, etiEnv, etiSessionMode );
    }

    @Override
    public ZString getAppSystemName() {
        return _appSystemName;
    }

    @Override
    public ETISessionMode getETISessionMode() {
        return _etiSessionMode;
    }

    @Override
    public ZString getETIVersion() {
        return _etiVersion;
    }

    @Override
    public int getEmulationTestHost() {
        return _emulationTestHost;
    }

    @Override
    public int getEmulationTestPort() {
        return _emulationTestPort;
    }

    @Override
    public ETIEnv getEnv() {
        return _env;
    }

    @Override
    public final long getLocationId() {
        return _locationId;
    }

    public final void setLocationId( long locationId ) {
        _locationId = locationId;
    }

    @Override
    public int getPartyIDSessionID() {
        return _partyIDSessionID;
    }

    @Override
    public ZString getPassword() {
        return _password;
    }

    public void setPassword( ZString password ) {
        _password = password;
    }

    @Override
    public ZString getSessionLogonPassword() {
        return _sessionLogonPassword;
    }

    public void setSessionLogonPassword( ZString sessionLogonPassword ) {
        _sessionLogonPassword = sessionLogonPassword;
    }

    @Override
    public ZString getTraderPassword() {
        return _traderPassword;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    public void setUserId( int userId ) {
        _userId = userId;
    }

    @Override
    public final boolean isForceTradingServerLocalhost() {
        return _forceTradingServerLocalhost;
    }

    public final void setForceTradingServerLocalhost( boolean forceTradingServerLocalhost ) {
        _forceTradingServerLocalhost = forceTradingServerLocalhost;
    }

    public void setTraderPassword( ZString traderPassword ) {
        _traderPassword = traderPassword;
    }

    public void setPartyIDSessionID( int partyIDSessionID ) {
        _partyIDSessionID = partyIDSessionID;
    }

    public void setEnv( ETIEnv etiEnv ) {
        _env = etiEnv;
    }

    public void setEmulationTestPort( int port ) {
        _emulationTestPort = port;
    }

    public void setETIVersion( String etiVersion ) {
        _etiVersion.copy( etiVersion );
    }

    public void setAppSystemName( ZString appSystemName ) {
        _appSystemName = appSystemName;
    }

    @Override
    public int getHeartBeatIntSecs() {
        return _heartBeatIntSecs;
    }

    @Override
    public void setHeartBeatIntSecs( int heartBeatIntSecs ) {
        _heartBeatIntSecs = heartBeatIntSecs;
    }

    @Override
    public int getMaxSeqNum() {
        return 0;
    }

    @Override
    public boolean isDisconnectOnMissedHB() {
        return _disconnectOnMissedHB;
    }

    /**
     * if other side send an nextSeqNum less than expected in logger on message then can optionally truncate down automatically
     * <p>
     * THIS IS NOT USUALLY ADVISABLE WITH EXCHANGE BUT MAYBE SO FOR CLIENTS
     *
     * @return true if should truncate down expected seq num from other side
     */
    @Override
    public boolean isRecoverFromLoginSeqNumTooLow() {
        return _isRecoverFromLoginSeqNumTooLow;
    }

    @Override
    public void setRecoverFromLoginSeqNumTooLow( boolean isRecoverFromLoginSeqNumTooLow ) {
        _isRecoverFromLoginSeqNumTooLow = isRecoverFromLoginSeqNumTooLow;
    }

    public void setDisconnectOnMissedHB( boolean disconnectOnMissedHB ) {
        _disconnectOnMissedHB = disconnectOnMissedHB;
    }

    @Override
    public String info() {
        return super.info() + ", useriI=" + _userId + ", recoverFromLowSeqNum=" +
               _isRecoverFromLoginSeqNumTooLow + ", isGwySess=" + _isGatewaySession;
    }

    @Override
    public void validate() throws SMTRuntimeException {
        super.validate();

        if ( _userId == 0 ) throw new SMTRuntimeException( "ETISocketConfig missing userId" );
        if ( _env == ETIEnv.Unknown ) throw new SMTRuntimeException( "ETISocketConfig missing etiEnv" );
        if ( _etiSessionMode == ETISessionMode.Unknown ) throw new SMTRuntimeException( "ETISocketConfig missing etiMode" );

        if ( isGatewaySession() && _partyIDSessionID == 0 ) throw new SMTRuntimeException( "ETISocketConfig missing partyIDSessionID" );
    }

    public int getExpectedRequests() {
        return _expectedRequests;
    }

    public void setExpectedRequests( int expectedRequests ) {
        _expectedRequests = expectedRequests;
    }

    public ZString getUniqueClientCode() {
        return _uniqueClientCode;
    }

    public void setUniqueClientCode( ZString uniqueClientCode ) {
        _uniqueClientCode = uniqueClientCode;
    }

    public boolean isGatewaySession() {
        return _isGatewaySession;
    }

    public void setGatewaySession( boolean isGwySession ) {
        _isGatewaySession = isGwySession;
    }

    public void setEtiSessionMode( ETISessionMode etiSessionMode ) {
        _etiSessionMode = etiSessionMode;
    }
}
