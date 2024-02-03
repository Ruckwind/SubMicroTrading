/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;

public class MilleniumSocketConfig extends SocketConfig implements MilleniumConfig {

    private ZString _recoveryHostname = null;
    private int     _recoveryPort     = 0;

    private boolean _disconnectOnMissedHB           = true;
    private int     _heartBeatIntSecs               = 3;
    private boolean _isRecoverFromLoginSeqNumTooLow = false;
    private ZString _userName                       = new ViewString( "" );
    private ZString _password                       = new ViewString( "" );
    private ZString _newPassword                    = new ViewString( "" );
    private boolean _isRecoverySession              = false;

    public MilleniumSocketConfig() {
        super( com.rr.model.generated.internal.events.factory.AllEventRecycler.class );
    }

    public MilleniumSocketConfig( boolean disconnectOnMissedHB,
                                  Class<? extends EventRecycler> recycler,
                                  boolean isServer,
                                  ZString host,
                                  ZString adapter,
                                  int port,
                                  ZString userName,
                                  ZString password,
                                  ZString newPassword,
                                  boolean isRecoverySession ) {

        super( recycler, isServer, host, adapter, port );

        _disconnectOnMissedHB = disconnectOnMissedHB;
        _userName             = userName;
        _password             = password;
        _newPassword          = newPassword;
        _isRecoverySession    = isRecoverySession;
    }

    public MilleniumSocketConfig( Class<? extends EventRecycler> recycler,
                                  boolean isServer,
                                  ZString host,
                                  ZString adapter,
                                  int port,
                                  ZString userName,
                                  ZString password,
                                  ZString newPassword,
                                  boolean isRecoverySession ) {

        super( recycler, isServer, host, adapter, port );

        _disconnectOnMissedHB = true;
        _userName             = userName;
        _password             = password;
        _newPassword          = newPassword;
        _isRecoverySession    = isRecoverySession;
    }

    @Override
    public ZString getUserName() {
        return _userName;
    }

    @Override
    public ZString getPassword() {
        return _password;
    }

    @Override
    public ZString getNewPassword() {
        return _newPassword;
    }

    public void setNewPassword( ZString newPassword ) {
        _newPassword = newPassword;
    }

    public void setPassword( ZString password ) {
        _password = password;
    }

    public void setUserName( ZString userName ) {
        _userName = userName;
    }

    @Override
    public String info() {
        return super.info() + ", userName=" + _userName + ", recoverFromLowSeqNum=" +
               _isRecoverFromLoginSeqNumTooLow + ", isRecoverySess=" + _isRecoverySession;
    }

    @Override
    public void validate() throws SMTRuntimeException {
        super.validate();

        if ( isUnset( _userName ) ) throw new SMTRuntimeException( "MillleniumSocketConfig missing userName" );
        if ( isUnset( _recoveryHostname ) ) throw new SMTRuntimeException( "MillleniumSocketConfig missing recoveryHostname" );
        if ( _recoveryPort == 0 ) throw new SMTRuntimeException( "MillleniumSocketConfig missing recoveryPort" );
    }

    @Override
    public boolean isDisconnectOnMissedHB() {
        return _disconnectOnMissedHB;
    }

    @Override
    public int getHeartBeatIntSecs() {
        return _heartBeatIntSecs;
    }

    @Override
    public void setHeartBeatIntSecs( int heartBeatIntSecs ) {
        _heartBeatIntSecs = heartBeatIntSecs;
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

    @Override
    public int getMaxSeqNum() {
        return 0;
    }

    public void setDisconnectOnMissedHB( boolean disconnectOnMissedHB ) {
        _disconnectOnMissedHB = disconnectOnMissedHB;
    }

    public ZString getRecoveryHostname() {
        return _recoveryHostname;
    }

    public void setRecoveryHostname( ZString recoveryHostname ) {
        _recoveryHostname = recoveryHostname;
    }

    public int getRecoveryPort() {
        return _recoveryPort;
    }

    public void setRecoveryPort( int recoveryPort ) {
        _recoveryPort = recoveryPort;
    }

    public boolean isRecoverySession() {
        return _isRecoverySession;
    }

    public void setRecoverySession( boolean isRecoverySession ) {
        _isRecoverySession = isRecoverySession;
    }

    private boolean isUnset( ZString val ) {
        return val == null || val.toString().length() == 0;
    }
}
