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

public final class UTPTradingSessionStatusImpl implements BaseUTP, UTPTradingSessionStatusWrite, Copyable<UTPTradingSessionStatus>, Reusable<UTPTradingSessionStatusImpl> {

   // Attrs

    private transient          UTPTradingSessionStatusImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    @TimestampMS private long _mktPhaseChgTime = Constants.UNSET_LONG;
    private final ReusableString _instClassId = new ReusableString( SizeType.UTP_INST_CLASS_LEN.getSize() );
    private final ReusableString _instClassStatus = new ReusableString( SizeType.UTP_INST_CLASS_STATUS_LEN.getSize() );
    private boolean _orderEntryAllowed = false;
    private final ReusableString _tradingSessionId = new ReusableString( SizeType.UTP_TRADING_SESSION_ID_LEN.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getMktPhaseChgTime() { return _mktPhaseChgTime; }
    @Override public final void setMktPhaseChgTime( long val ) { _mktPhaseChgTime = val; }

    @Override public final ViewString getInstClassId() { return _instClassId; }

    @Override public final void setInstClassId( byte[] buf, int offset, int len ) { _instClassId.setValue( buf, offset, len ); }
    @Override public final ReusableString getInstClassIdForUpdate() { return _instClassId; }

    @Override public final ViewString getInstClassStatus() { return _instClassStatus; }

    @Override public final void setInstClassStatus( byte[] buf, int offset, int len ) { _instClassStatus.setValue( buf, offset, len ); }
    @Override public final ReusableString getInstClassStatusForUpdate() { return _instClassStatus; }

    @Override public final boolean getOrderEntryAllowed() { return _orderEntryAllowed; }
    @Override public final void setOrderEntryAllowed( boolean val ) { _orderEntryAllowed = val; }

    @Override public final ViewString getTradingSessionId() { return _tradingSessionId; }

    @Override public final void setTradingSessionId( byte[] buf, int offset, int len ) { _tradingSessionId.setValue( buf, offset, len ); }
    @Override public final ReusableString getTradingSessionIdForUpdate() { return _tradingSessionId; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _mktPhaseChgTime = Constants.UNSET_LONG;
        _instClassId.reset();
        _instClassStatus.reset();
        _orderEntryAllowed = false;
        _tradingSessionId.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.UTPTradingSessionStatus;
    }

    @Override
    public final UTPTradingSessionStatusImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( UTPTradingSessionStatusImpl nxt ) {
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
        out.append( "UTPTradingSessionStatusImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getMktPhaseChgTime() && 0 != getMktPhaseChgTime() ) {
            out.append( ", mktPhaseChgTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getMktPhaseChgTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getMktPhaseChgTime() );
            out.append( " ( " );
            out.append( getMktPhaseChgTime() ).append( " ) " );
        }
        if ( getInstClassId().length() > 0 )             out.append( ", instClassId=" ).append( getInstClassId() );
        if ( getInstClassStatus().length() > 0 )             out.append( ", instClassStatus=" ).append( getInstClassStatus() );
        out.append( ", orderEntryAllowed=" ).append( getOrderEntryAllowed() );
        if ( getTradingSessionId().length() > 0 )             out.append( ", tradingSessionId=" ).append( getTradingSessionId() );
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

    @Override public final void snapTo( UTPTradingSessionStatus dest ) {
        ((UTPTradingSessionStatusImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( UTPTradingSessionStatus src ) {
        setMktPhaseChgTime( src.getMktPhaseChgTime() );
        getInstClassIdForUpdate().copy( src.getInstClassId() );
        getInstClassStatusForUpdate().copy( src.getInstClassStatus() );
        setOrderEntryAllowed( src.getOrderEntryAllowed() );
        getTradingSessionIdForUpdate().copy( src.getTradingSessionId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( UTPTradingSessionStatus src ) {
        setMktPhaseChgTime( src.getMktPhaseChgTime() );
        getInstClassIdForUpdate().copy( src.getInstClassId() );
        getInstClassStatusForUpdate().copy( src.getInstClassStatus() );
        setOrderEntryAllowed( src.getOrderEntryAllowed() );
        getTradingSessionIdForUpdate().copy( src.getTradingSessionId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( UTPTradingSessionStatus src ) {
        if ( Constants.UNSET_LONG != src.getMktPhaseChgTime() ) setMktPhaseChgTime( src.getMktPhaseChgTime() );
        if ( src.getInstClassId().length() > 0 ) getInstClassIdForUpdate().copy( src.getInstClassId() );
        if ( src.getInstClassStatus().length() > 0 ) getInstClassStatusForUpdate().copy( src.getInstClassStatus() );
        setOrderEntryAllowed( src.getOrderEntryAllowed() );
        if ( src.getTradingSessionId().length() > 0 ) getTradingSessionIdForUpdate().copy( src.getTradingSessionId() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
