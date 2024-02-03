package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
import com.rr.model.generated.internal.events.interfaces.StratInstrumentState;
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

public final class StrategyStateImpl implements SessionHeader, StrategyStateWrite, Copyable<StrategyState>, Reusable<StrategyStateImpl> {

   // Attrs

    private transient          StrategyStateImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _userName = new ReusableString( SizeType.USERNAME.getSize() );
    @TimestampMS private long _liveStartTimestamp = Constants.UNSET_LONG;
    private final ReusableString _idOfExportComponent = new ReusableString( SizeType.ALGO_ID_LEN.getSize() );
    @TimestampMS private long _stratTimestamp = Constants.UNSET_LONG;
    private double _unrealisedTotalPnL = 0;
    private double _realisedTotalPnL = 0;
    private long _id = 0;
    private boolean _isDeltaMode = false;
    private int _stratStateMsgsInGrp = Constants.UNSET_INT;
    private int _curStratStateMsgInGrp = Constants.UNSET_INT;
    private int _noInstEntries = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private RunStatus _status;
    private StratInstrumentState _instState;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getUserName() { return _userName; }

    @Override public final void setUserName( byte[] buf, int offset, int len ) { _userName.setValue( buf, offset, len ); }
    @Override public final ReusableString getUserNameForUpdate() { return _userName; }

    @Override public final long getLiveStartTimestamp() { return _liveStartTimestamp; }
    @Override public final void setLiveStartTimestamp( long val ) { _liveStartTimestamp = val; }

    @Override public final ViewString getIdOfExportComponent() { return _idOfExportComponent; }

    @Override public final void setIdOfExportComponent( byte[] buf, int offset, int len ) { _idOfExportComponent.setValue( buf, offset, len ); }
    @Override public final ReusableString getIdOfExportComponentForUpdate() { return _idOfExportComponent; }

    @Override public final RunStatus getStatus() { return _status; }
    @Override public final void setStatus( RunStatus val ) { _status = val; }

    @Override public final long getStratTimestamp() { return _stratTimestamp; }
    @Override public final void setStratTimestamp( long val ) { _stratTimestamp = val; }

    @Override public final double getUnrealisedTotalPnL() { return _unrealisedTotalPnL; }
    @Override public final void setUnrealisedTotalPnL( double val ) { _unrealisedTotalPnL = val; }

    @Override public final double getRealisedTotalPnL() { return _realisedTotalPnL; }
    @Override public final void setRealisedTotalPnL( double val ) { _realisedTotalPnL = val; }

    @Override public final long getId() { return _id; }
    @Override public final void setId( long val ) { _id = val; }

    @Override public final boolean getIsDeltaMode() { return _isDeltaMode; }
    @Override public final void setIsDeltaMode( boolean val ) { _isDeltaMode = val; }

    @Override public final int getStratStateMsgsInGrp() { return _stratStateMsgsInGrp; }
    @Override public final void setStratStateMsgsInGrp( int val ) { _stratStateMsgsInGrp = val; }

    @Override public final int getCurStratStateMsgInGrp() { return _curStratStateMsgInGrp; }
    @Override public final void setCurStratStateMsgInGrp( int val ) { _curStratStateMsgInGrp = val; }

    @Override public final int getNoInstEntries() { return _noInstEntries; }
    @Override public final void setNoInstEntries( int val ) { _noInstEntries = val; }

    @Override public final StratInstrumentState getInstState() { return _instState; }
    @Override public final void setInstState( StratInstrumentState val ) { _instState = val; }

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
        _idOfExportComponent.reset();
        _status = null;
        _stratTimestamp = Constants.UNSET_LONG;
        _unrealisedTotalPnL = 0;
        _realisedTotalPnL = 0;
        _id = 0;
        _isDeltaMode = false;
        _stratStateMsgsInGrp = Constants.UNSET_INT;
        _curStratStateMsgInGrp = Constants.UNSET_INT;
        _noInstEntries = Constants.UNSET_INT;
        _instState = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.StrategyState;
    }

    @Override
    public final StrategyStateImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( StrategyStateImpl nxt ) {
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
        out.append( "StrategyStateImpl" ).append( ' ' );
        if ( getUserName().length() > 0 )             out.append( ", userName=" ).append( getUserName() );
        if ( Constants.UNSET_LONG != getLiveStartTimestamp() && 0 != getLiveStartTimestamp() ) {
            out.append( ", liveStartTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getLiveStartTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getLiveStartTimestamp() );
            out.append( " ( " );
            out.append( getLiveStartTimestamp() ).append( " ) " );
        }
        if ( getIdOfExportComponent().length() > 0 )             out.append( ", idOfExportComponent=" ).append( getIdOfExportComponent() );
        if ( getStatus() != null )             out.append( ", status=" ).append( getStatus() );
        if ( Constants.UNSET_LONG != getStratTimestamp() && 0 != getStratTimestamp() ) {
            out.append( ", stratTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getStratTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getStratTimestamp() );
            out.append( " ( " );
            out.append( getStratTimestamp() ).append( " ) " );
        }
        if ( Utils.hasVal( getUnrealisedTotalPnL() ) ) out.append( ", unrealisedTotalPnL=" ).append( getUnrealisedTotalPnL() );
        if ( Utils.hasVal( getRealisedTotalPnL() ) ) out.append( ", realisedTotalPnL=" ).append( getRealisedTotalPnL() );
        if ( Constants.UNSET_LONG != getId() && 0 != getId() )             out.append( ", id=" ).append( getId() );
        out.append( ", isDeltaMode=" ).append( getIsDeltaMode() );
        if ( Constants.UNSET_INT != getStratStateMsgsInGrp() && 0 != getStratStateMsgsInGrp() )             out.append( ", stratStateMsgsInGrp=" ).append( getStratStateMsgsInGrp() );
        if ( Constants.UNSET_INT != getCurStratStateMsgInGrp() && 0 != getCurStratStateMsgInGrp() )             out.append( ", curStratStateMsgInGrp=" ).append( getCurStratStateMsgInGrp() );
        if ( Constants.UNSET_INT != getNoInstEntries() && 0 != getNoInstEntries() )             out.append( ", noInstEntries=" ).append( getNoInstEntries() );

        StratInstrumentStateImpl tPtrinstState = (StratInstrumentStateImpl) getInstState();
        int tIdxinstState=0;

        while( tPtrinstState != null ) {
            out.append( " {#" ).append( ++tIdxinstState ).append( "} " );
            tPtrinstState.dump( out );
            tPtrinstState = tPtrinstState.getNext();
        }

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

    @Override public final void snapTo( StrategyState dest ) {
        ((StrategyStateImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( StrategyState src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        setStratTimestamp( src.getStratTimestamp() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setId( src.getId() );
        setIsDeltaMode( src.getIsDeltaMode() );
        setStratStateMsgsInGrp( src.getStratStateMsgsInGrp() );
        setCurStratStateMsgInGrp( src.getCurStratStateMsgInGrp() );
        setNoInstEntries( src.getNoInstEntries() );
        StratInstrumentStateImpl tSrcPtrInstState = (StratInstrumentStateImpl) src.getInstState();
        StratInstrumentStateImpl tNewPtrInstState = null;
        while( tSrcPtrInstState != null ) {
            if ( tNewPtrInstState == null ) {
                tNewPtrInstState = new StratInstrumentStateImpl();
                setInstState( tNewPtrInstState );
            } else {
                tNewPtrInstState.setNext( new StratInstrumentStateImpl() );
                tNewPtrInstState = tNewPtrInstState.getNext();
            }
            tNewPtrInstState.deepCopyFrom( tSrcPtrInstState );
            tSrcPtrInstState = tSrcPtrInstState.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( StrategyState src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        setStratTimestamp( src.getStratTimestamp() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setId( src.getId() );
        setIsDeltaMode( src.getIsDeltaMode() );
        setStratStateMsgsInGrp( src.getStratStateMsgsInGrp() );
        setCurStratStateMsgInGrp( src.getCurStratStateMsgInGrp() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( StrategyState src ) {
        if ( src.getUserName().length() > 0 ) getUserNameForUpdate().copy( src.getUserName() );
        if ( Constants.UNSET_LONG != src.getLiveStartTimestamp() ) setLiveStartTimestamp( src.getLiveStartTimestamp() );
        if ( src.getIdOfExportComponent().length() > 0 ) getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        if ( Constants.UNSET_LONG != src.getStratTimestamp() ) setStratTimestamp( src.getStratTimestamp() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnL() ) ) setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        if ( Utils.hasVal( src.getRealisedTotalPnL() ) ) setRealisedTotalPnL( src.getRealisedTotalPnL() );
        if ( Constants.UNSET_LONG != src.getId() ) setId( src.getId() );
        setIsDeltaMode( src.getIsDeltaMode() );
        if ( Constants.UNSET_INT != src.getStratStateMsgsInGrp() ) setStratStateMsgsInGrp( src.getStratStateMsgsInGrp() );
        if ( Constants.UNSET_INT != src.getCurStratStateMsgInGrp() ) setCurStratStateMsgInGrp( src.getCurStratStateMsgInGrp() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
