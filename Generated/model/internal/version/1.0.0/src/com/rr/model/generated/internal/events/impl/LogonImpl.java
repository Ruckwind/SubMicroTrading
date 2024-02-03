package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.EncryptMethod;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class LogonImpl implements SessionHeader, LogonWrite, Copyable<Logon>, Reusable<LogonImpl> {

   // Attrs

    private transient          LogonImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _senderCompId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private final ReusableString _senderSubId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private final ReusableString _targetCompId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private final ReusableString _targetSubId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private final ReusableString _onBehalfOfId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private int _heartBtInt = Constants.UNSET_INT;
    private int _rawDataLen = Constants.UNSET_INT;
    private final ReusableString _rawData = new ReusableString( SizeType.USERNAME_LENGTH.getSize() );
    private boolean _resetSeqNumFlag = false;
    private int _nextExpectedMsgSeqNum = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private EncryptMethod _encryptMethod = EncryptMethod.NoneOrOther;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSenderCompId() { return _senderCompId; }

    @Override public final void setSenderCompId( byte[] buf, int offset, int len ) { _senderCompId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSenderCompIdForUpdate() { return _senderCompId; }

    @Override public final ViewString getSenderSubId() { return _senderSubId; }

    @Override public final void setSenderSubId( byte[] buf, int offset, int len ) { _senderSubId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSenderSubIdForUpdate() { return _senderSubId; }

    @Override public final ViewString getTargetCompId() { return _targetCompId; }

    @Override public final void setTargetCompId( byte[] buf, int offset, int len ) { _targetCompId.setValue( buf, offset, len ); }
    @Override public final ReusableString getTargetCompIdForUpdate() { return _targetCompId; }

    @Override public final ViewString getTargetSubId() { return _targetSubId; }

    @Override public final void setTargetSubId( byte[] buf, int offset, int len ) { _targetSubId.setValue( buf, offset, len ); }
    @Override public final ReusableString getTargetSubIdForUpdate() { return _targetSubId; }

    @Override public final ViewString getOnBehalfOfId() { return _onBehalfOfId; }

    @Override public final void setOnBehalfOfId( byte[] buf, int offset, int len ) { _onBehalfOfId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOnBehalfOfIdForUpdate() { return _onBehalfOfId; }

    @Override public final EncryptMethod getEncryptMethod() { return _encryptMethod; }
    @Override public final void setEncryptMethod( EncryptMethod val ) { _encryptMethod = val; }

    @Override public final int getHeartBtInt() { return _heartBtInt; }
    @Override public final void setHeartBtInt( int val ) { _heartBtInt = val; }

    @Override public final int getRawDataLen() { return _rawDataLen; }
    @Override public final void setRawDataLen( int val ) { _rawDataLen = val; }

    @Override public final ViewString getRawData() { return _rawData; }

    @Override public final void setRawData( byte[] buf, int offset, int len ) { _rawData.setValue( buf, offset, len ); }
    @Override public final ReusableString getRawDataForUpdate() { return _rawData; }

    @Override public final boolean getResetSeqNumFlag() { return _resetSeqNumFlag; }
    @Override public final void setResetSeqNumFlag( boolean val ) { _resetSeqNumFlag = val; }

    @Override public final int getNextExpectedMsgSeqNum() { return _nextExpectedMsgSeqNum; }
    @Override public final void setNextExpectedMsgSeqNum( int val ) { _nextExpectedMsgSeqNum = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _senderCompId.reset();
        _senderSubId.reset();
        _targetCompId.reset();
        _targetSubId.reset();
        _onBehalfOfId.reset();
        _encryptMethod = EncryptMethod.NoneOrOther;
        _heartBtInt = Constants.UNSET_INT;
        _rawDataLen = Constants.UNSET_INT;
        _rawData.reset();
        _resetSeqNumFlag = false;
        _nextExpectedMsgSeqNum = Constants.UNSET_INT;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.Logon;
    }

    @Override
    public final LogonImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( LogonImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final void detachQueue() {
        _nextMessage = null;
    }

    @Override
    public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override
    public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override
    public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override
    public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }


   // Helper methods
    @Override
    public void setFlag( MsgFlag flag, boolean isOn ) {
        _flags = MsgFlag.setFlag( _flags, flag, isOn );
    }

    @Override
    public boolean isFlagSet( MsgFlag flag ) {
        return MsgFlag.isOn( _flags, flag );
    }

    @Override
    public int getFlags() {
        return _flags;
    }

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "LogonImpl" ).append( ' ' );
        if ( getSenderCompId().length() > 0 )             out.append( ", senderCompId=" ).append( getSenderCompId() );
        if ( getSenderSubId().length() > 0 )             out.append( ", senderSubId=" ).append( getSenderSubId() );
        if ( getTargetCompId().length() > 0 )             out.append( ", targetCompId=" ).append( getTargetCompId() );
        if ( getTargetSubId().length() > 0 )             out.append( ", targetSubId=" ).append( getTargetSubId() );
        if ( getOnBehalfOfId().length() > 0 )             out.append( ", onBehalfOfId=" ).append( getOnBehalfOfId() );
        if ( getEncryptMethod() != null )             out.append( ", encryptMethod=" ).append( getEncryptMethod() );
        if ( Constants.UNSET_INT != getHeartBtInt() && 0 != getHeartBtInt() )             out.append( ", heartBtInt=" ).append( getHeartBtInt() );
        if ( Constants.UNSET_INT != getRawDataLen() && 0 != getRawDataLen() )             out.append( ", rawDataLen=" ).append( getRawDataLen() );
        if ( getRawData().length() > 0 )             out.append( ", rawData=" ).append( getRawData() );
        out.append( ", resetSeqNumFlag=" ).append( getResetSeqNumFlag() );
        if ( Constants.UNSET_INT != getNextExpectedMsgSeqNum() && 0 != getNextExpectedMsgSeqNum() )             out.append( ", nextExpectedMsgSeqNum=" ).append( getNextExpectedMsgSeqNum() );
        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", possDupFlag=" ).append( getPossDupFlag() );
        if ( Constants.UNSET_LONG != getEventTimestamp() && 0 != getEventTimestamp() ) {
            out.append( ", eventTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEventTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEventTimestamp() );
            out.append( " ( " );
            out.append( getEventTimestamp() ).append( " ) " );
        }
    }

    @Override public final void snapTo( Logon dest ) {
        ((LogonImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( Logon src ) {
        getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        getSenderSubIdForUpdate().copy( src.getSenderSubId() );
        getTargetCompIdForUpdate().copy( src.getTargetCompId() );
        getTargetSubIdForUpdate().copy( src.getTargetSubId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setEncryptMethod( src.getEncryptMethod() );
        setHeartBtInt( src.getHeartBtInt() );
        setRawDataLen( src.getRawDataLen() );
        getRawDataForUpdate().copy( src.getRawData() );
        setResetSeqNumFlag( src.getResetSeqNumFlag() );
        setNextExpectedMsgSeqNum( src.getNextExpectedMsgSeqNum() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( Logon src ) {
        getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        getSenderSubIdForUpdate().copy( src.getSenderSubId() );
        getTargetCompIdForUpdate().copy( src.getTargetCompId() );
        getTargetSubIdForUpdate().copy( src.getTargetSubId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setEncryptMethod( src.getEncryptMethod() );
        setHeartBtInt( src.getHeartBtInt() );
        setRawDataLen( src.getRawDataLen() );
        getRawDataForUpdate().copy( src.getRawData() );
        setResetSeqNumFlag( src.getResetSeqNumFlag() );
        setNextExpectedMsgSeqNum( src.getNextExpectedMsgSeqNum() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( Logon src ) {
        if ( src.getSenderCompId().length() > 0 ) getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        if ( src.getSenderSubId().length() > 0 ) getSenderSubIdForUpdate().copy( src.getSenderSubId() );
        if ( src.getTargetCompId().length() > 0 ) getTargetCompIdForUpdate().copy( src.getTargetCompId() );
        if ( src.getTargetSubId().length() > 0 ) getTargetSubIdForUpdate().copy( src.getTargetSubId() );
        if ( src.getOnBehalfOfId().length() > 0 ) getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setEncryptMethod( src.getEncryptMethod() );
        if ( Constants.UNSET_INT != src.getHeartBtInt() ) setHeartBtInt( src.getHeartBtInt() );
        if ( Constants.UNSET_INT != src.getRawDataLen() ) setRawDataLen( src.getRawDataLen() );
        if ( src.getRawData().length() > 0 ) getRawDataForUpdate().copy( src.getRawData() );
        setResetSeqNumFlag( src.getResetSeqNumFlag() );
        if ( Constants.UNSET_INT != src.getNextExpectedMsgSeqNum() ) setNextExpectedMsgSeqNum( src.getNextExpectedMsgSeqNum() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
