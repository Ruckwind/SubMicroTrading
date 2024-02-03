package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
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

public final class AppRunImpl implements SessionHeader, AppRunWrite, Copyable<AppRun>, Reusable<AppRunImpl> {

   // Attrs

    private transient          AppRunImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _userName = new ReusableString( SizeType.USERNAME.getSize() );
    @TimestampMS private long _liveStartTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _liveEndTimestamp = Constants.UNSET_LONG;
    private double _unrealisedTotalPnL = 0;
    private double _realisedTotalPnL = 0;
    private int _numTrades = 0;
    private long _id = 0;
    private int _numStrategies = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private RunStatus _status;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getUserName() { return _userName; }

    @Override public final void setUserName( byte[] buf, int offset, int len ) { _userName.setValue( buf, offset, len ); }
    @Override public final ReusableString getUserNameForUpdate() { return _userName; }

    @Override public final long getLiveStartTimestamp() { return _liveStartTimestamp; }
    @Override public final void setLiveStartTimestamp( long val ) { _liveStartTimestamp = val; }

    @Override public final long getLiveEndTimestamp() { return _liveEndTimestamp; }
    @Override public final void setLiveEndTimestamp( long val ) { _liveEndTimestamp = val; }

    @Override public final RunStatus getStatus() { return _status; }
    @Override public final void setStatus( RunStatus val ) { _status = val; }

    @Override public final double getUnrealisedTotalPnL() { return _unrealisedTotalPnL; }
    @Override public final void setUnrealisedTotalPnL( double val ) { _unrealisedTotalPnL = val; }

    @Override public final double getRealisedTotalPnL() { return _realisedTotalPnL; }
    @Override public final void setRealisedTotalPnL( double val ) { _realisedTotalPnL = val; }

    @Override public final int getNumTrades() { return _numTrades; }
    @Override public final void setNumTrades( int val ) { _numTrades = val; }

    @Override public final long getId() { return _id; }
    @Override public final void setId( long val ) { _id = val; }

    @Override public final int getNumStrategies() { return _numStrategies; }
    @Override public final void setNumStrategies( int val ) { _numStrategies = val; }

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
        _liveStartTimestamp = Constants.UNSET_LONG;
        _liveEndTimestamp = Constants.UNSET_LONG;
        _status = null;
        _unrealisedTotalPnL = 0;
        _realisedTotalPnL = 0;
        _numTrades = 0;
        _id = 0;
        _numStrategies = Constants.UNSET_INT;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.AppRun;
    }

    @Override
    public final AppRunImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( AppRunImpl nxt ) {
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
        out.append( "AppRunImpl" ).append( ' ' );
        if ( getUserName().length() > 0 )             out.append( ", userName=" ).append( getUserName() );
        if ( Constants.UNSET_LONG != getLiveStartTimestamp() && 0 != getLiveStartTimestamp() ) {
            out.append( ", liveStartTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getLiveStartTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getLiveStartTimestamp() );
            out.append( " ( " );
            out.append( getLiveStartTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getLiveEndTimestamp() && 0 != getLiveEndTimestamp() ) {
            out.append( ", liveEndTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getLiveEndTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getLiveEndTimestamp() );
            out.append( " ( " );
            out.append( getLiveEndTimestamp() ).append( " ) " );
        }
        if ( getStatus() != null )             out.append( ", status=" ).append( getStatus() );
        if ( Utils.hasVal( getUnrealisedTotalPnL() ) ) out.append( ", unrealisedTotalPnL=" ).append( getUnrealisedTotalPnL() );
        if ( Utils.hasVal( getRealisedTotalPnL() ) ) out.append( ", realisedTotalPnL=" ).append( getRealisedTotalPnL() );
        if ( Constants.UNSET_INT != getNumTrades() && 0 != getNumTrades() )             out.append( ", numTrades=" ).append( getNumTrades() );
        if ( Constants.UNSET_LONG != getId() && 0 != getId() )             out.append( ", id=" ).append( getId() );
        if ( Constants.UNSET_INT != getNumStrategies() && 0 != getNumStrategies() )             out.append( ", numStrategies=" ).append( getNumStrategies() );
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

    @Override public final void snapTo( AppRun dest ) {
        ((AppRunImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( AppRun src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        setLiveEndTimestamp( src.getLiveEndTimestamp() );
        setStatus( src.getStatus() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setNumTrades( src.getNumTrades() );
        setId( src.getId() );
        setNumStrategies( src.getNumStrategies() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( AppRun src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        setLiveEndTimestamp( src.getLiveEndTimestamp() );
        setStatus( src.getStatus() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setNumTrades( src.getNumTrades() );
        setId( src.getId() );
        setNumStrategies( src.getNumStrategies() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( AppRun src ) {
        if ( src.getUserName().length() > 0 ) getUserNameForUpdate().copy( src.getUserName() );
        if ( Constants.UNSET_LONG != src.getLiveStartTimestamp() ) setLiveStartTimestamp( src.getLiveStartTimestamp() );
        if ( Constants.UNSET_LONG != src.getLiveEndTimestamp() ) setLiveEndTimestamp( src.getLiveEndTimestamp() );
        setStatus( src.getStatus() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnL() ) ) setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        if ( Utils.hasVal( src.getRealisedTotalPnL() ) ) setRealisedTotalPnL( src.getRealisedTotalPnL() );
        if ( Constants.UNSET_INT != src.getNumTrades() ) setNumTrades( src.getNumTrades() );
        if ( Constants.UNSET_LONG != src.getId() ) setId( src.getId() );
        if ( Constants.UNSET_INT != src.getNumStrategies() ) setNumStrategies( src.getNumStrategies() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
