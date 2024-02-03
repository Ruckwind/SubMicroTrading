package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.UTPRejCode;
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

public final class UTPLogonRejectImpl implements BaseUTP, UTPLogonRejectWrite, Copyable<UTPLogonReject>, Reusable<UTPLogonRejectImpl> {

   // Attrs

    private transient          UTPLogonRejectImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _lastMsgSeqNumRcvd = Constants.UNSET_INT;
    private int _lastMsgSeqNumSent = Constants.UNSET_INT;
    private final ReusableString _rejectText = new ReusableString( SizeType.UTP_REJECT_TEXT_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private UTPRejCode _rejectCode;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getLastMsgSeqNumRcvd() { return _lastMsgSeqNumRcvd; }
    @Override public final void setLastMsgSeqNumRcvd( int val ) { _lastMsgSeqNumRcvd = val; }

    @Override public final int getLastMsgSeqNumSent() { return _lastMsgSeqNumSent; }
    @Override public final void setLastMsgSeqNumSent( int val ) { _lastMsgSeqNumSent = val; }

    @Override public final UTPRejCode getRejectCode() { return _rejectCode; }
    @Override public final void setRejectCode( UTPRejCode val ) { _rejectCode = val; }

    @Override public final ViewString getRejectText() { return _rejectText; }

    @Override public final void setRejectText( byte[] buf, int offset, int len ) { _rejectText.setValue( buf, offset, len ); }
    @Override public final ReusableString getRejectTextForUpdate() { return _rejectText; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _lastMsgSeqNumRcvd = Constants.UNSET_INT;
        _lastMsgSeqNumSent = Constants.UNSET_INT;
        _rejectCode = null;
        _rejectText.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.UTPLogonReject;
    }

    @Override
    public final UTPLogonRejectImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( UTPLogonRejectImpl nxt ) {
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
        out.append( "UTPLogonRejectImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getLastMsgSeqNumRcvd() && 0 != getLastMsgSeqNumRcvd() )             out.append( ", lastMsgSeqNumRcvd=" ).append( getLastMsgSeqNumRcvd() );
        if ( Constants.UNSET_INT != getLastMsgSeqNumSent() && 0 != getLastMsgSeqNumSent() )             out.append( ", lastMsgSeqNumSent=" ).append( getLastMsgSeqNumSent() );
        if ( getRejectCode() != null )             out.append( ", rejectCode=" ).append( getRejectCode() );
        if ( getRejectText().length() > 0 )             out.append( ", rejectText=" ).append( getRejectText() );
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

    @Override public final void snapTo( UTPLogonReject dest ) {
        ((UTPLogonRejectImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( UTPLogonReject src ) {
        setLastMsgSeqNumRcvd( src.getLastMsgSeqNumRcvd() );
        setLastMsgSeqNumSent( src.getLastMsgSeqNumSent() );
        setRejectCode( src.getRejectCode() );
        getRejectTextForUpdate().copy( src.getRejectText() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( UTPLogonReject src ) {
        setLastMsgSeqNumRcvd( src.getLastMsgSeqNumRcvd() );
        setLastMsgSeqNumSent( src.getLastMsgSeqNumSent() );
        setRejectCode( src.getRejectCode() );
        getRejectTextForUpdate().copy( src.getRejectText() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( UTPLogonReject src ) {
        if ( Constants.UNSET_INT != src.getLastMsgSeqNumRcvd() ) setLastMsgSeqNumRcvd( src.getLastMsgSeqNumRcvd() );
        if ( Constants.UNSET_INT != src.getLastMsgSeqNumSent() ) setLastMsgSeqNumSent( src.getLastMsgSeqNumSent() );
        setRejectCode( src.getRejectCode() );
        if ( src.getRejectText().length() > 0 ) getRejectTextForUpdate().copy( src.getRejectText() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
