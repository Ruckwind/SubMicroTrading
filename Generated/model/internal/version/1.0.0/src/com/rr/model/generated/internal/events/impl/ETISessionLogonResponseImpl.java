package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIEnv;
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

public final class ETISessionLogonResponseImpl implements BaseETIResponse, ETISessionLogonResponseWrite, Copyable<ETISessionLogonResponse>, Reusable<ETISessionLogonResponseImpl> {

   // Attrs

    private transient          ETISessionLogonResponseImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    @TimestampMS private long _requestTime = Constants.UNSET_LONG;
    private long _throttleTimeIntervalMS = Constants.UNSET_LONG;
    private int _throttleNoMsgs = Constants.UNSET_INT;
    private int _throttleDisconnectLimit = Constants.UNSET_INT;
    private int _heartBtIntMS = Constants.UNSET_INT;
    private int _sessionInstanceID = Constants.UNSET_INT;
    private final ReusableString _defaultCstmApplVerID = new ReusableString( SizeType.ETI_INTERFACE_VERSION_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ETIEnv _tradSesMode;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getRequestTime() { return _requestTime; }
    @Override public final void setRequestTime( long val ) { _requestTime = val; }

    @Override public final long getThrottleTimeIntervalMS() { return _throttleTimeIntervalMS; }
    @Override public final void setThrottleTimeIntervalMS( long val ) { _throttleTimeIntervalMS = val; }

    @Override public final int getThrottleNoMsgs() { return _throttleNoMsgs; }
    @Override public final void setThrottleNoMsgs( int val ) { _throttleNoMsgs = val; }

    @Override public final int getThrottleDisconnectLimit() { return _throttleDisconnectLimit; }
    @Override public final void setThrottleDisconnectLimit( int val ) { _throttleDisconnectLimit = val; }

    @Override public final int getHeartBtIntMS() { return _heartBtIntMS; }
    @Override public final void setHeartBtIntMS( int val ) { _heartBtIntMS = val; }

    @Override public final int getSessionInstanceID() { return _sessionInstanceID; }
    @Override public final void setSessionInstanceID( int val ) { _sessionInstanceID = val; }

    @Override public final ETIEnv getTradSesMode() { return _tradSesMode; }
    @Override public final void setTradSesMode( ETIEnv val ) { _tradSesMode = val; }

    @Override public final ViewString getDefaultCstmApplVerID() { return _defaultCstmApplVerID; }

    @Override public final void setDefaultCstmApplVerID( byte[] buf, int offset, int len ) { _defaultCstmApplVerID.setValue( buf, offset, len ); }
    @Override public final ReusableString getDefaultCstmApplVerIDForUpdate() { return _defaultCstmApplVerID; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _requestTime = Constants.UNSET_LONG;
        _throttleTimeIntervalMS = Constants.UNSET_LONG;
        _throttleNoMsgs = Constants.UNSET_INT;
        _throttleDisconnectLimit = Constants.UNSET_INT;
        _heartBtIntMS = Constants.UNSET_INT;
        _sessionInstanceID = Constants.UNSET_INT;
        _tradSesMode = null;
        _defaultCstmApplVerID.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETISessionLogonResponse;
    }

    @Override
    public final ETISessionLogonResponseImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETISessionLogonResponseImpl nxt ) {
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
        out.append( "ETISessionLogonResponseImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getRequestTime() && 0 != getRequestTime() ) {
            out.append( ", requestTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getRequestTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getRequestTime() );
            out.append( " ( " );
            out.append( getRequestTime() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getThrottleTimeIntervalMS() && 0 != getThrottleTimeIntervalMS() )             out.append( ", throttleTimeIntervalMS=" ).append( getThrottleTimeIntervalMS() );
        if ( Constants.UNSET_INT != getThrottleNoMsgs() && 0 != getThrottleNoMsgs() )             out.append( ", throttleNoMsgs=" ).append( getThrottleNoMsgs() );
        if ( Constants.UNSET_INT != getThrottleDisconnectLimit() && 0 != getThrottleDisconnectLimit() )             out.append( ", throttleDisconnectLimit=" ).append( getThrottleDisconnectLimit() );
        if ( Constants.UNSET_INT != getHeartBtIntMS() && 0 != getHeartBtIntMS() )             out.append( ", heartBtIntMS=" ).append( getHeartBtIntMS() );
        if ( Constants.UNSET_INT != getSessionInstanceID() && 0 != getSessionInstanceID() )             out.append( ", sessionInstanceID=" ).append( getSessionInstanceID() );
        if ( getTradSesMode() != null )             out.append( ", tradSesMode=" ).append( getTradSesMode() );
        if ( getDefaultCstmApplVerID().length() > 0 )             out.append( ", defaultCstmApplVerID=" ).append( getDefaultCstmApplVerID() );
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

    @Override public final void snapTo( ETISessionLogonResponse dest ) {
        ((ETISessionLogonResponseImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETISessionLogonResponse src ) {
        setRequestTime( src.getRequestTime() );
        setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        setThrottleNoMsgs( src.getThrottleNoMsgs() );
        setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        setHeartBtIntMS( src.getHeartBtIntMS() );
        setSessionInstanceID( src.getSessionInstanceID() );
        setTradSesMode( src.getTradSesMode() );
        getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETISessionLogonResponse src ) {
        setRequestTime( src.getRequestTime() );
        setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        setThrottleNoMsgs( src.getThrottleNoMsgs() );
        setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        setHeartBtIntMS( src.getHeartBtIntMS() );
        setSessionInstanceID( src.getSessionInstanceID() );
        setTradSesMode( src.getTradSesMode() );
        getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETISessionLogonResponse src ) {
        if ( Constants.UNSET_LONG != src.getRequestTime() ) setRequestTime( src.getRequestTime() );
        if ( Constants.UNSET_LONG != src.getThrottleTimeIntervalMS() ) setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        if ( Constants.UNSET_INT != src.getThrottleNoMsgs() ) setThrottleNoMsgs( src.getThrottleNoMsgs() );
        if ( Constants.UNSET_INT != src.getThrottleDisconnectLimit() ) setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        if ( Constants.UNSET_INT != src.getHeartBtIntMS() ) setHeartBtIntMS( src.getHeartBtIntMS() );
        if ( Constants.UNSET_INT != src.getSessionInstanceID() ) setSessionInstanceID( src.getSessionInstanceID() );
        setTradSesMode( src.getTradSesMode() );
        if ( src.getDefaultCstmApplVerID().length() > 0 ) getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
