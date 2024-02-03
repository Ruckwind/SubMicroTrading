/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;

public class UTPSocketConfig extends SocketConfig implements UTPConfig {

    private boolean _disconnectOnMissedHB           = true;
    private int     _heartBeatIntSecs               = 60;
    private boolean _isRecoverFromLoginSeqNumTooLow = false;
    private ZString _userName                       = new ViewString( "" );

    public UTPSocketConfig() {
        super( com.rr.model.generated.internal.events.factory.AllEventRecycler.class );
    }

    public UTPSocketConfig( boolean disconnectOnMissedHB,
                            Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            ZString userName ) {

        super( recycler, isServer, host, adapter, port );

        _disconnectOnMissedHB = disconnectOnMissedHB;
        _userName             = userName;
    }

    public UTPSocketConfig( Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            ZString userName ) {

        super( recycler, isServer, host, adapter, port );

        _disconnectOnMissedHB = true;
        _userName             = userName;
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
    public ZString getUserName() {
        return _userName;
    }

    public void setUserName( ZString userName ) {
        _userName = userName;
    }

    @Override
    public String info() {
        return super.info() + ", userName=" + _userName + ", recoverFromLowSeqNum=" + _isRecoverFromLoginSeqNumTooLow;
    }

    @Override
    public void validate() throws SMTRuntimeException {
        super.validate();
        if ( isUnset( _userName ) ) throw new SMTRuntimeException( "UTPSocketConfig missing userName" );
    }

    private boolean isUnset( ZString val ) {
        return val == null || val.toString().length() == 0;
    }
}
