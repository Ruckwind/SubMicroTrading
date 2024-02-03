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

public final class SoupLogInRequestImpl implements BaseSoup, SoupLogInRequestWrite, Copyable<SoupLogInRequest>, Reusable<SoupLogInRequestImpl> {

   // Attrs

    private transient          SoupLogInRequestImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString  _userName = new ReusableString( SizeType.SOUP_USERNAME_LEN.getSize() );
    private final ReusableString  _password = new ReusableString( SizeType.SOUP_PASSWORD_LEN.getSize() );
    private final ReusableString  _requestedSession = new ReusableString( SizeType.SOUP_SESSION_LEN.getSize() );
    private long _requestedSeqNum = Constants.UNSET_LONG;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getUserName() { return _userName; }

    @Override public final void setUserName( byte[] buf, int offset, int len ) { _userName.setValue( buf, offset, len ); }
    @Override public final ReusableString getUserNameForUpdate() { return _userName; }

    @Override public final ViewString getPassword() { return _password; }

    @Override public final void setPassword( byte[] buf, int offset, int len ) { _password.setValue( buf, offset, len ); }
    @Override public final ReusableString getPasswordForUpdate() { return _password; }

    @Override public final ViewString getRequestedSession() { return _requestedSession; }

    @Override public final void setRequestedSession( byte[] buf, int offset, int len ) { _requestedSession.setValue( buf, offset, len ); }
    @Override public final ReusableString getRequestedSessionForUpdate() { return _requestedSession; }

    @Override public final long getRequestedSeqNum() { return _requestedSeqNum; }
    @Override public final void setRequestedSeqNum( long val ) { _requestedSeqNum = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _userName.reset();
        _password.reset();
        _requestedSession.reset();
        _requestedSeqNum = Constants.UNSET_LONG;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SoupLogInRequest;
    }

    @Override
    public final SoupLogInRequestImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SoupLogInRequestImpl nxt ) {
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
        out.append( "SoupLogInRequestImpl" ).append( ' ' );
        if ( getUserName().length() > 0 )             out.append( ", userName=" ).append( getUserName() );
        if ( getPassword().length() > 0 )             out.append( ", password=" ).append( getPassword() );
        if ( getRequestedSession().length() > 0 )             out.append( ", requestedSession=" ).append( getRequestedSession() );
        if ( Constants.UNSET_LONG != getRequestedSeqNum() && 0 != getRequestedSeqNum() )             out.append( ", requestedSeqNum=" ).append( getRequestedSeqNum() );
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

    @Override public final void snapTo( SoupLogInRequest dest ) {
        ((SoupLogInRequestImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SoupLogInRequest src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        getPasswordForUpdate().copy( src.getPassword() );
        getRequestedSessionForUpdate().copy( src.getRequestedSession() );
        setRequestedSeqNum( src.getRequestedSeqNum() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SoupLogInRequest src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        getPasswordForUpdate().copy( src.getPassword() );
        getRequestedSessionForUpdate().copy( src.getRequestedSession() );
        setRequestedSeqNum( src.getRequestedSeqNum() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SoupLogInRequest src ) {
        if ( src.getUserName().length() > 0 ) getUserNameForUpdate().copy( src.getUserName() );
        if ( src.getPassword().length() > 0 ) getPasswordForUpdate().copy( src.getPassword() );
        if ( src.getRequestedSession().length() > 0 ) getRequestedSessionForUpdate().copy( src.getRequestedSession() );
        if ( Constants.UNSET_LONG != src.getRequestedSeqNum() ) setRequestedSeqNum( src.getRequestedSeqNum() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
