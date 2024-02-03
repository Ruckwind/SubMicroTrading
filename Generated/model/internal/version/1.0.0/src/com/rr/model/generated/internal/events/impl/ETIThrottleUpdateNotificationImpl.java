package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class ETIThrottleUpdateNotificationImpl implements BaseETIResponse, ETIThrottleUpdateNotificationWrite, Copyable<ETIThrottleUpdateNotification>, Reusable<ETIThrottleUpdateNotificationImpl> {

   // Attrs

    private transient          ETIThrottleUpdateNotificationImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _throttleTimeIntervalMS = Constants.UNSET_LONG;
    private int _throttleNoMsgs = Constants.UNSET_INT;
    private int _throttleDisconnectLimit = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getThrottleTimeIntervalMS() { return _throttleTimeIntervalMS; }
    @Override public final void setThrottleTimeIntervalMS( long val ) { _throttleTimeIntervalMS = val; }

    @Override public final int getThrottleNoMsgs() { return _throttleNoMsgs; }
    @Override public final void setThrottleNoMsgs( int val ) { _throttleNoMsgs = val; }

    @Override public final int getThrottleDisconnectLimit() { return _throttleDisconnectLimit; }
    @Override public final void setThrottleDisconnectLimit( int val ) { _throttleDisconnectLimit = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _throttleTimeIntervalMS = Constants.UNSET_LONG;
        _throttleNoMsgs = Constants.UNSET_INT;
        _throttleDisconnectLimit = Constants.UNSET_INT;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETIThrottleUpdateNotification;
    }

    @Override
    public final ETIThrottleUpdateNotificationImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETIThrottleUpdateNotificationImpl nxt ) {
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
        out.append( "ETIThrottleUpdateNotificationImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getThrottleTimeIntervalMS() && 0 != getThrottleTimeIntervalMS() )             out.append( ", throttleTimeIntervalMS=" ).append( getThrottleTimeIntervalMS() );
        if ( Constants.UNSET_INT != getThrottleNoMsgs() && 0 != getThrottleNoMsgs() )             out.append( ", throttleNoMsgs=" ).append( getThrottleNoMsgs() );
        if ( Constants.UNSET_INT != getThrottleDisconnectLimit() && 0 != getThrottleDisconnectLimit() )             out.append( ", throttleDisconnectLimit=" ).append( getThrottleDisconnectLimit() );
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

    @Override public final void snapTo( ETIThrottleUpdateNotification dest ) {
        ((ETIThrottleUpdateNotificationImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETIThrottleUpdateNotification src ) {
        setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        setThrottleNoMsgs( src.getThrottleNoMsgs() );
        setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETIThrottleUpdateNotification src ) {
        setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        setThrottleNoMsgs( src.getThrottleNoMsgs() );
        setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETIThrottleUpdateNotification src ) {
        if ( Constants.UNSET_LONG != src.getThrottleTimeIntervalMS() ) setThrottleTimeIntervalMS( src.getThrottleTimeIntervalMS() );
        if ( Constants.UNSET_INT != src.getThrottleNoMsgs() ) setThrottleNoMsgs( src.getThrottleNoMsgs() );
        if ( Constants.UNSET_INT != src.getThrottleDisconnectLimit() ) setThrottleDisconnectLimit( src.getThrottleDisconnectLimit() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
