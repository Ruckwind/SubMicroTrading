package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIEurexDataStream;
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

public final class ETIRetransmitOrderEventsImpl implements BaseETIRequest, ETIRetransmitOrderEventsWrite, Copyable<ETIRetransmitOrderEvents>, Reusable<ETIRetransmitOrderEventsImpl> {

   // Attrs

    private transient          ETIRetransmitOrderEventsImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _subscriptionScope = Constants.UNSET_INT;
    private short _partitionID = Constants.UNSET_SHORT;
    private final ReusableString _applBegMsgID = new ReusableString( SizeType.ETI_APP_MSG_ID_LENGTH.getSize() );
    private final ReusableString _applEndMsgID = new ReusableString( SizeType.ETI_APP_MSG_ID_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ETIEurexDataStream _refApplID;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getSubscriptionScope() { return _subscriptionScope; }
    @Override public final void setSubscriptionScope( int val ) { _subscriptionScope = val; }

    @Override public final short getPartitionID() { return _partitionID; }
    @Override public final void setPartitionID( short val ) { _partitionID = val; }

    @Override public final ETIEurexDataStream getRefApplID() { return _refApplID; }
    @Override public final void setRefApplID( ETIEurexDataStream val ) { _refApplID = val; }

    @Override public final ViewString getApplBegMsgID() { return _applBegMsgID; }

    @Override public final void setApplBegMsgID( byte[] buf, int offset, int len ) { _applBegMsgID.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplBegMsgIDForUpdate() { return _applBegMsgID; }

    @Override public final ViewString getApplEndMsgID() { return _applEndMsgID; }

    @Override public final void setApplEndMsgID( byte[] buf, int offset, int len ) { _applEndMsgID.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplEndMsgIDForUpdate() { return _applEndMsgID; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _subscriptionScope = Constants.UNSET_INT;
        _partitionID = Constants.UNSET_SHORT;
        _refApplID = null;
        _applBegMsgID.reset();
        _applEndMsgID.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETIRetransmitOrderEvents;
    }

    @Override
    public final ETIRetransmitOrderEventsImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETIRetransmitOrderEventsImpl nxt ) {
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
        out.append( "ETIRetransmitOrderEventsImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getSubscriptionScope() && 0 != getSubscriptionScope() )             out.append( ", subscriptionScope=" ).append( getSubscriptionScope() );
        if ( Constants.UNSET_SHORT != getPartitionID() && 0 != getPartitionID() )             out.append( ", partitionID=" ).append( getPartitionID() );
        if ( getRefApplID() != null )             out.append( ", refApplID=" ).append( getRefApplID() );
        if ( getApplBegMsgID().length() > 0 )             out.append( ", applBegMsgID=" ).append( getApplBegMsgID() );
        if ( getApplEndMsgID().length() > 0 )             out.append( ", applEndMsgID=" ).append( getApplEndMsgID() );
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

    @Override public final void snapTo( ETIRetransmitOrderEvents dest ) {
        ((ETIRetransmitOrderEventsImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETIRetransmitOrderEvents src ) {
        setSubscriptionScope( src.getSubscriptionScope() );
        setPartitionID( src.getPartitionID() );
        setRefApplID( src.getRefApplID() );
        getApplBegMsgIDForUpdate().copy( src.getApplBegMsgID() );
        getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETIRetransmitOrderEvents src ) {
        setSubscriptionScope( src.getSubscriptionScope() );
        setPartitionID( src.getPartitionID() );
        setRefApplID( src.getRefApplID() );
        getApplBegMsgIDForUpdate().copy( src.getApplBegMsgID() );
        getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETIRetransmitOrderEvents src ) {
        if ( Constants.UNSET_INT != src.getSubscriptionScope() ) setSubscriptionScope( src.getSubscriptionScope() );
        if ( Constants.UNSET_SHORT != src.getPartitionID() ) setPartitionID( src.getPartitionID() );
        setRefApplID( src.getRefApplID() );
        if ( src.getApplBegMsgID().length() > 0 ) getApplBegMsgIDForUpdate().copy( src.getApplBegMsgID() );
        if ( src.getApplEndMsgID().length() > 0 ) getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
