/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.FixVersion;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.fix.codec.CodecId;

public class FixSocketConfig extends SocketConfig implements FixConfig {

    private FixVersion _fixVersion                 = FixVersion.Fix4_4;
    private boolean    _useNewFix44GapFillProtocol = false;

    private boolean _disconnectOnSeqGap             = true;
    private boolean _disconnectOnMissedHB           = true;
    private int     _heartBeatIntSecs               = 60;
    private boolean _isRecoverFromLoginSeqNumTooLow = false;
    private boolean _allowMultipleChildren          = false; // only NonBlockingFixSession supports this option
    private boolean _isGapFillAllowed               = true;
    private ZString _encryptMethod                  = new ViewString( "" );
    private ZString _rawData                        = new ViewString( "" );
    private ZString _userName                       = new ViewString( "" );
    private ZString _password                       = new ViewString( "" );
    private ZString _senderCompId;
    private ZString _senderSubId                    = new ViewString( "" );
    private ZString _targetCompId;
    private ZString _targetSubId                    = new ViewString( "" );
    private ZString _senderLocationId               = new ViewString( "" );

    private CodecId _codecId;
    private boolean _reconnectOnLogout = false;

    public FixSocketConfig( String id ) {
        super( id );
    }

    public FixSocketConfig() {
        super( com.rr.model.generated.internal.events.factory.AllEventRecycler.class );
    }

    public FixSocketConfig( FixVersion fixVersion,
                            boolean useNewFix44GapFillProtocol,
                            boolean disconnectOnMissedHB,
                            Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            ZString senderCompId,
                            ZString senderSubId,
                            ZString targetCompId,
                            ZString targetSubId,
                            ZString userName,
                            ZString password ) {

        super( recycler, isServer, host, adapter, port );

        _useNewFix44GapFillProtocol = useNewFix44GapFillProtocol;
        _fixVersion                 = fixVersion;
        _disconnectOnMissedHB       = disconnectOnMissedHB;
        _userName                   = userName;
        _password                   = password;
        _senderCompId               = senderCompId;
        _senderSubId                = senderSubId;
        _targetCompId               = targetCompId;
        _targetSubId                = targetSubId;
    }

    public FixSocketConfig( Class<? extends EventRecycler> recycler,
                            boolean isServer,
                            ZString host,
                            ZString adapter,
                            int port,
                            ZString senderCompId,
                            ZString senderSubId,
                            ZString targetCompId,
                            ZString targetSubId,
                            ZString userName,
                            ZString password ) {

        super( recycler, isServer, host, adapter, port );

        _useNewFix44GapFillProtocol = false;
        _fixVersion                 = FixVersion.Fix4_4;
        _disconnectOnMissedHB       = true;
        _userName                   = userName;
        _password                   = password;
        _senderCompId               = senderCompId;
        _senderSubId                = senderSubId;
        _targetCompId               = targetCompId;
        _targetSubId                = targetSubId;
    }

    @Override
    public ZString getEncryptMethod() {
        return _encryptMethod;
    }

    @Override
    public ZString getPassword() {
        return _password;
    }

    public void setPassword( ZString pwd ) {
        _password = pwd;
    }

    @Override
    public ZString getRawData() {
        return _rawData;
    }

    @Override
    public ZString getSenderCompId() {
        return _senderCompId;
    }

    public void setSenderCompId( ZString senderCompId ) {
        _senderCompId = senderCompId;
    }

    @Override
    public ZString getSenderLocationId() {
        return _senderLocationId;
    }

    @Override
    public ZString getSenderSubId() {
        return _senderSubId;
    }

    @Override
    public ZString getTargetCompId() {
        return _targetCompId;
    }

    @Override public void setTargetCompId( ZString targetCompId ) {
        _targetCompId = targetCompId;
    }

    @Override
    public ZString getTargetSubId() {
        return _targetSubId;
    }

    @Override
    public ZString getUserName() {
        return _userName;
    }

    public void setUserName( ZString userName ) {
        _userName = userName;
    }

    public void setTargetSubId( ZString targetSubId ) {
        _targetSubId = targetSubId;
    }

    public void setSenderSubId( ZString senderSubId ) {
        _senderSubId = senderSubId;
    }

    public void setRawData( ZString rawData ) {
        _rawData = rawData;
    }

    public void setEncryptMethod( ZString encryptMethod ) {
        _encryptMethod = encryptMethod;
    }

    @Override
    public FixVersion getFixVersion() {
        return _fixVersion;
    }

    public void setFixVersion( FixVersion fixVersion ) {
        _fixVersion = fixVersion;
    }

    @Override
    public boolean isDisconnectOnSeqGap() {
        return _disconnectOnSeqGap;
    }

    @Override
    public boolean isUseNewFix44GapFillProtocol() {
        return _useNewFix44GapFillProtocol;
    }

    public void setUseNewFix44GapFillProtocol( boolean useNewFix44GapFillProtocol ) {
        _useNewFix44GapFillProtocol = useNewFix44GapFillProtocol;
    }

    public void setDisconnectOnSeqGap( boolean disconnectOnSeqGap ) {
        _disconnectOnSeqGap = disconnectOnSeqGap;
    }

    @Override
    public String info() {
        return super.info() + ", senderCompId=" + _senderCompId +
               ", targetCompId=" + _targetCompId +
               ", fixVersion=" + _fixVersion.toString() +
               ", disconnectOnMissedHB=" + _disconnectOnMissedHB +
               ", new44GapFill=" + _useNewFix44GapFillProtocol +
               ", recoverFromLowSeqNum=" + _isRecoverFromLoginSeqNumTooLow +
               ", disconnectOnSeqGap=" + _disconnectOnSeqGap +
               ", gapFillAllowed=" + _isGapFillAllowed +
               ", reconnectOnLogout=" + _reconnectOnLogout;
    }

    @Override
    public void validate() throws SMTRuntimeException {
        super.validate();
        if ( isUnset( _senderCompId ) ) throw new SMTRuntimeException( "FixSocketConfig missing senderCompId" );
        if ( isUnset( _targetCompId ) && !isAllowMultipleChildren() ) throw new SMTRuntimeException( "FixSocketConfig missing targetCompId" );
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
        return _fixVersion.getMaxSeqNum();
    }

    @Override public boolean isReconnectOnLogout() {
        return _reconnectOnLogout;
    }

    public void setDisconnectOnMissedHB( boolean disconnectOnMissedHB ) {
        _disconnectOnMissedHB = disconnectOnMissedHB;
    }

    @Override
    public boolean isGapFillAllowed() {
        return _isGapFillAllowed;
    }

    @Override
    public void setGapFillAllowed( boolean canGapFill ) {
        _isGapFillAllowed = canGapFill;
    }

    public CodecId getCodecId() {
        return _codecId;
    }

    public void setCodecId( CodecId codecId ) {
        _codecId = codecId;
    }

    public boolean isAllowMultipleChildren()                                    { return _allowMultipleChildren; }

    public void setAllowMultipleChildren( final boolean allowMultipleChildren ) { _allowMultipleChildren = allowMultipleChildren; }

    public void setSenderLocationID( ZString senderLocationId ) {
        _senderLocationId = senderLocationId;
    }

    private boolean isUnset( ZString val ) {
        return val == null || val.toString().length() == 0;
    }
}
