package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SessionRejectReason;
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

public final class SessionRejectImpl implements SessionHeader, SessionRejectWrite, Copyable<SessionReject>, Reusable<SessionRejectImpl> {

   // Attrs

    private transient          SessionRejectImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _refSeqNum = Constants.UNSET_INT;
    private int _refTagID = Constants.UNSET_INT;
    private final ReusableString _refMsgType = new ReusableString( SizeType.TAG_LEN.getSize() );
    private final ReusableString _text = new ReusableString( SizeType.TEXT_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SessionRejectReason _sessionRejectReason;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getRefSeqNum() { return _refSeqNum; }
    @Override public final void setRefSeqNum( int val ) { _refSeqNum = val; }

    @Override public final int getRefTagID() { return _refTagID; }
    @Override public final void setRefTagID( int val ) { _refTagID = val; }

    @Override public final ViewString getRefMsgType() { return _refMsgType; }

    @Override public final void setRefMsgType( byte[] buf, int offset, int len ) { _refMsgType.setValue( buf, offset, len ); }
    @Override public final ReusableString getRefMsgTypeForUpdate() { return _refMsgType; }

    @Override public final SessionRejectReason getSessionRejectReason() { return _sessionRejectReason; }
    @Override public final void setSessionRejectReason( SessionRejectReason val ) { _sessionRejectReason = val; }

    @Override public final ViewString getText() { return _text; }

    @Override public final void setText( byte[] buf, int offset, int len ) { _text.setValue( buf, offset, len ); }
    @Override public final ReusableString getTextForUpdate() { return _text; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _refSeqNum = Constants.UNSET_INT;
        _refTagID = Constants.UNSET_INT;
        _refMsgType.reset();
        _sessionRejectReason = null;
        _text.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SessionReject;
    }

    @Override
    public final SessionRejectImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SessionRejectImpl nxt ) {
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
        out.append( "SessionRejectImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getRefSeqNum() && 0 != getRefSeqNum() )             out.append( ", refSeqNum=" ).append( getRefSeqNum() );
        if ( Constants.UNSET_INT != getRefTagID() && 0 != getRefTagID() )             out.append( ", refTagID=" ).append( getRefTagID() );
        if ( getRefMsgType().length() > 0 )             out.append( ", refMsgType=" ).append( getRefMsgType() );
        if ( getSessionRejectReason() != null )             out.append( ", sessionRejectReason=" ).append( getSessionRejectReason() );
        if ( getText().length() > 0 )             out.append( ", text=" ).append( getText() );
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

    @Override public final void snapTo( SessionReject dest ) {
        ((SessionRejectImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SessionReject src ) {
        setRefSeqNum( src.getRefSeqNum() );
        setRefTagID( src.getRefTagID() );
        getRefMsgTypeForUpdate().copy( src.getRefMsgType() );
        setSessionRejectReason( src.getSessionRejectReason() );
        getTextForUpdate().copy( src.getText() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SessionReject src ) {
        setRefSeqNum( src.getRefSeqNum() );
        setRefTagID( src.getRefTagID() );
        getRefMsgTypeForUpdate().copy( src.getRefMsgType() );
        setSessionRejectReason( src.getSessionRejectReason() );
        getTextForUpdate().copy( src.getText() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SessionReject src ) {
        if ( Constants.UNSET_INT != src.getRefSeqNum() ) setRefSeqNum( src.getRefSeqNum() );
        if ( Constants.UNSET_INT != src.getRefTagID() ) setRefTagID( src.getRefTagID() );
        if ( src.getRefMsgType().length() > 0 ) getRefMsgTypeForUpdate().copy( src.getRefMsgType() );
        setSessionRejectReason( src.getSessionRejectReason() );
        if ( src.getText().length() > 0 ) getTextForUpdate().copy( src.getText() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
