package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETISessionMode;
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

public final class ETIConnectionGatewayResponseImpl implements BaseETIResponse, ETIConnectionGatewayResponseWrite, Copyable<ETIConnectionGatewayResponse>, Reusable<ETIConnectionGatewayResponseImpl> {

   // Attrs

    private transient          ETIConnectionGatewayResponseImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    @TimestampMS private long _requestTime = Constants.UNSET_LONG;
    private int _msgSeqNum = Constants.UNSET_INT;
    private int _gatewayID = Constants.UNSET_INT;
    private int _gatewaySubID = Constants.UNSET_INT;
    private int _secGatewayID = Constants.UNSET_INT;
    private int _secGatewaySubID = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ETISessionMode _sessionMode;
    private ETIEnv _tradSesMode;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getRequestTime() { return _requestTime; }
    @Override public final void setRequestTime( long val ) { _requestTime = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final int getGatewayID() { return _gatewayID; }
    @Override public final void setGatewayID( int val ) { _gatewayID = val; }

    @Override public final int getGatewaySubID() { return _gatewaySubID; }
    @Override public final void setGatewaySubID( int val ) { _gatewaySubID = val; }

    @Override public final int getSecGatewayID() { return _secGatewayID; }
    @Override public final void setSecGatewayID( int val ) { _secGatewayID = val; }

    @Override public final int getSecGatewaySubID() { return _secGatewaySubID; }
    @Override public final void setSecGatewaySubID( int val ) { _secGatewaySubID = val; }

    @Override public final ETISessionMode getSessionMode() { return _sessionMode; }
    @Override public final void setSessionMode( ETISessionMode val ) { _sessionMode = val; }

    @Override public final ETIEnv getTradSesMode() { return _tradSesMode; }
    @Override public final void setTradSesMode( ETIEnv val ) { _tradSesMode = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _requestTime = Constants.UNSET_LONG;
        _msgSeqNum = Constants.UNSET_INT;
        _gatewayID = Constants.UNSET_INT;
        _gatewaySubID = Constants.UNSET_INT;
        _secGatewayID = Constants.UNSET_INT;
        _secGatewaySubID = Constants.UNSET_INT;
        _sessionMode = null;
        _tradSesMode = null;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETIConnectionGatewayResponse;
    }

    @Override
    public final ETIConnectionGatewayResponseImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETIConnectionGatewayResponseImpl nxt ) {
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
        out.append( "ETIConnectionGatewayResponseImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getRequestTime() && 0 != getRequestTime() ) {
            out.append( ", requestTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getRequestTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getRequestTime() );
            out.append( " ( " );
            out.append( getRequestTime() ).append( " ) " );
        }
        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        if ( Constants.UNSET_INT != getGatewayID() && 0 != getGatewayID() )             out.append( ", gatewayID=" ).append( getGatewayID() );
        if ( Constants.UNSET_INT != getGatewaySubID() && 0 != getGatewaySubID() )             out.append( ", gatewaySubID=" ).append( getGatewaySubID() );
        if ( Constants.UNSET_INT != getSecGatewayID() && 0 != getSecGatewayID() )             out.append( ", secGatewayID=" ).append( getSecGatewayID() );
        if ( Constants.UNSET_INT != getSecGatewaySubID() && 0 != getSecGatewaySubID() )             out.append( ", secGatewaySubID=" ).append( getSecGatewaySubID() );
        if ( getSessionMode() != null )             out.append( ", sessionMode=" ).append( getSessionMode() );
        if ( getTradSesMode() != null )             out.append( ", tradSesMode=" ).append( getTradSesMode() );
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

    @Override public final void snapTo( ETIConnectionGatewayResponse dest ) {
        ((ETIConnectionGatewayResponseImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETIConnectionGatewayResponse src ) {
        setRequestTime( src.getRequestTime() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setGatewayID( src.getGatewayID() );
        setGatewaySubID( src.getGatewaySubID() );
        setSecGatewayID( src.getSecGatewayID() );
        setSecGatewaySubID( src.getSecGatewaySubID() );
        setSessionMode( src.getSessionMode() );
        setTradSesMode( src.getTradSesMode() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETIConnectionGatewayResponse src ) {
        setRequestTime( src.getRequestTime() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setGatewayID( src.getGatewayID() );
        setGatewaySubID( src.getGatewaySubID() );
        setSecGatewayID( src.getSecGatewayID() );
        setSecGatewaySubID( src.getSecGatewaySubID() );
        setSessionMode( src.getSessionMode() );
        setTradSesMode( src.getTradSesMode() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETIConnectionGatewayResponse src ) {
        if ( Constants.UNSET_LONG != src.getRequestTime() ) setRequestTime( src.getRequestTime() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        if ( Constants.UNSET_INT != src.getGatewayID() ) setGatewayID( src.getGatewayID() );
        if ( Constants.UNSET_INT != src.getGatewaySubID() ) setGatewaySubID( src.getGatewaySubID() );
        if ( Constants.UNSET_INT != src.getSecGatewayID() ) setSecGatewayID( src.getSecGatewayID() );
        if ( Constants.UNSET_INT != src.getSecGatewaySubID() ) setSecGatewaySubID( src.getSecGatewaySubID() );
        setSessionMode( src.getSessionMode() );
        setTradSesMode( src.getTradSesMode() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
