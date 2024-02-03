package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
import com.rr.model.generated.internal.events.interfaces.StratInstrument;
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

public final class StrategyRunImpl implements SessionHeader, StrategyRunWrite, Copyable<StrategyRun>, Reusable<StrategyRunImpl> {

   // Attrs

    private transient          StrategyRunImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _userName = new ReusableString( SizeType.USERNAME.getSize() );
    @TimestampMS private long _liveStartTimestamp = Constants.UNSET_LONG;
    private final ReusableString _idOfExportComponent = new ReusableString( SizeType.ALGO_ID_LEN.getSize() );
    private final ReusableString _algoId = new ReusableString( SizeType.ALGO_ID_LEN.getSize() );
    private final ReusableString _stratTimeZone = new ReusableString( SizeType.TIMEZONE_LEN.getSize() );
    @TimestampMS private long _btStartTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _btEndTimestamp = Constants.UNSET_LONG;
    private double _unrealisedTotalPnL = 0;
    private double _realisedTotalPnL = 0;
    private int _numTrades = 0;
    private final ReusableString _strategyDefinition = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private long _id = 0;
    private int _noInstEntries = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private RunStatus _status;
    private StratInstrument _instruments;

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

    @Override public final ViewString getAlgoId() { return _algoId; }

    @Override public final void setAlgoId( byte[] buf, int offset, int len ) { _algoId.setValue( buf, offset, len ); }
    @Override public final ReusableString getAlgoIdForUpdate() { return _algoId; }

    @Override public final ViewString getStratTimeZone() { return _stratTimeZone; }

    @Override public final void setStratTimeZone( byte[] buf, int offset, int len ) { _stratTimeZone.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratTimeZoneForUpdate() { return _stratTimeZone; }

    @Override public final long getBtStartTimestamp() { return _btStartTimestamp; }
    @Override public final void setBtStartTimestamp( long val ) { _btStartTimestamp = val; }

    @Override public final long getBtEndTimestamp() { return _btEndTimestamp; }
    @Override public final void setBtEndTimestamp( long val ) { _btEndTimestamp = val; }

    @Override public final double getUnrealisedTotalPnL() { return _unrealisedTotalPnL; }
    @Override public final void setUnrealisedTotalPnL( double val ) { _unrealisedTotalPnL = val; }

    @Override public final double getRealisedTotalPnL() { return _realisedTotalPnL; }
    @Override public final void setRealisedTotalPnL( double val ) { _realisedTotalPnL = val; }

    @Override public final int getNumTrades() { return _numTrades; }
    @Override public final void setNumTrades( int val ) { _numTrades = val; }

    @Override public final ViewString getStrategyDefinition() { return _strategyDefinition; }

    @Override public final void setStrategyDefinition( byte[] buf, int offset, int len ) { _strategyDefinition.setValue( buf, offset, len ); }
    @Override public final ReusableString getStrategyDefinitionForUpdate() { return _strategyDefinition; }

    @Override public final long getId() { return _id; }
    @Override public final void setId( long val ) { _id = val; }

    @Override public final int getNoInstEntries() { return _noInstEntries; }
    @Override public final void setNoInstEntries( int val ) { _noInstEntries = val; }

    @Override public final StratInstrument getInstruments() { return _instruments; }
    @Override public final void setInstruments( StratInstrument val ) { _instruments = val; }

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
        _algoId.reset();
        _stratTimeZone.reset();
        _btStartTimestamp = Constants.UNSET_LONG;
        _btEndTimestamp = Constants.UNSET_LONG;
        _unrealisedTotalPnL = 0;
        _realisedTotalPnL = 0;
        _numTrades = 0;
        _strategyDefinition.reset();
        _id = 0;
        _noInstEntries = Constants.UNSET_INT;
        _instruments = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.StrategyRun;
    }

    @Override
    public final StrategyRunImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( StrategyRunImpl nxt ) {
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
        out.append( "StrategyRunImpl" ).append( ' ' );
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
        if ( getAlgoId().length() > 0 )             out.append( ", algoId=" ).append( getAlgoId() );
        if ( getStratTimeZone().length() > 0 )             out.append( ", stratTimeZone=" ).append( getStratTimeZone() );
        if ( Constants.UNSET_LONG != getBtStartTimestamp() && 0 != getBtStartTimestamp() ) {
            out.append( ", btStartTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getBtStartTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getBtStartTimestamp() );
            out.append( " ( " );
            out.append( getBtStartTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getBtEndTimestamp() && 0 != getBtEndTimestamp() ) {
            out.append( ", btEndTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getBtEndTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getBtEndTimestamp() );
            out.append( " ( " );
            out.append( getBtEndTimestamp() ).append( " ) " );
        }
        if ( Utils.hasVal( getUnrealisedTotalPnL() ) ) out.append( ", unrealisedTotalPnL=" ).append( getUnrealisedTotalPnL() );
        if ( Utils.hasVal( getRealisedTotalPnL() ) ) out.append( ", realisedTotalPnL=" ).append( getRealisedTotalPnL() );
        if ( Constants.UNSET_INT != getNumTrades() && 0 != getNumTrades() )             out.append( ", numTrades=" ).append( getNumTrades() );
        if ( getStrategyDefinition().length() > 0 )             out.append( ", strategyDefinition=" ).append( getStrategyDefinition() );
        if ( Constants.UNSET_LONG != getId() && 0 != getId() )             out.append( ", id=" ).append( getId() );
        if ( Constants.UNSET_INT != getNoInstEntries() && 0 != getNoInstEntries() )             out.append( ", noInstEntries=" ).append( getNoInstEntries() );

        StratInstrumentImpl tPtrinstruments = (StratInstrumentImpl) getInstruments();
        int tIdxinstruments=0;

        while( tPtrinstruments != null ) {
            out.append( " {#" ).append( ++tIdxinstruments ).append( "} " );
            tPtrinstruments.dump( out );
            tPtrinstruments = tPtrinstruments.getNext();
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

    @Override public final void snapTo( StrategyRun dest ) {
        ((StrategyRunImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( StrategyRun src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        getAlgoIdForUpdate().copy( src.getAlgoId() );
        getStratTimeZoneForUpdate().copy( src.getStratTimeZone() );
        setBtStartTimestamp( src.getBtStartTimestamp() );
        setBtEndTimestamp( src.getBtEndTimestamp() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setNumTrades( src.getNumTrades() );
        getStrategyDefinitionForUpdate().copy( src.getStrategyDefinition() );
        setId( src.getId() );
        setNoInstEntries( src.getNoInstEntries() );
        StratInstrumentImpl tSrcPtrInstruments = (StratInstrumentImpl) src.getInstruments();
        StratInstrumentImpl tNewPtrInstruments = null;
        while( tSrcPtrInstruments != null ) {
            if ( tNewPtrInstruments == null ) {
                tNewPtrInstruments = new StratInstrumentImpl();
                setInstruments( tNewPtrInstruments );
            } else {
                tNewPtrInstruments.setNext( new StratInstrumentImpl() );
                tNewPtrInstruments = tNewPtrInstruments.getNext();
            }
            tNewPtrInstruments.deepCopyFrom( tSrcPtrInstruments );
            tSrcPtrInstruments = tSrcPtrInstruments.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( StrategyRun src ) {
        getUserNameForUpdate().copy( src.getUserName() );
        setLiveStartTimestamp( src.getLiveStartTimestamp() );
        getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        getAlgoIdForUpdate().copy( src.getAlgoId() );
        getStratTimeZoneForUpdate().copy( src.getStratTimeZone() );
        setBtStartTimestamp( src.getBtStartTimestamp() );
        setBtEndTimestamp( src.getBtEndTimestamp() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setRealisedTotalPnL( src.getRealisedTotalPnL() );
        setNumTrades( src.getNumTrades() );
        getStrategyDefinitionForUpdate().copy( src.getStrategyDefinition() );
        setId( src.getId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( StrategyRun src ) {
        if ( src.getUserName().length() > 0 ) getUserNameForUpdate().copy( src.getUserName() );
        if ( Constants.UNSET_LONG != src.getLiveStartTimestamp() ) setLiveStartTimestamp( src.getLiveStartTimestamp() );
        if ( src.getIdOfExportComponent().length() > 0 ) getIdOfExportComponentForUpdate().copy( src.getIdOfExportComponent() );
        setStatus( src.getStatus() );
        if ( src.getAlgoId().length() > 0 ) getAlgoIdForUpdate().copy( src.getAlgoId() );
        if ( src.getStratTimeZone().length() > 0 ) getStratTimeZoneForUpdate().copy( src.getStratTimeZone() );
        if ( Constants.UNSET_LONG != src.getBtStartTimestamp() ) setBtStartTimestamp( src.getBtStartTimestamp() );
        if ( Constants.UNSET_LONG != src.getBtEndTimestamp() ) setBtEndTimestamp( src.getBtEndTimestamp() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnL() ) ) setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        if ( Utils.hasVal( src.getRealisedTotalPnL() ) ) setRealisedTotalPnL( src.getRealisedTotalPnL() );
        if ( Constants.UNSET_INT != src.getNumTrades() ) setNumTrades( src.getNumTrades() );
        if ( src.getStrategyDefinition().length() > 0 ) getStrategyDefinitionForUpdate().copy( src.getStrategyDefinition() );
        if ( Constants.UNSET_LONG != src.getId() ) setId( src.getId() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
