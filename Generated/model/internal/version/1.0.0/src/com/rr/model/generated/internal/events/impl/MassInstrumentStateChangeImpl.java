package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.SecMassStatGrp;
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

public final class MassInstrumentStateChangeImpl implements SessionHeader, MassInstrumentStateChangeWrite, Copyable<MassInstrumentStateChange>, Reusable<MassInstrumentStateChangeImpl> {

   // Attrs

    private transient          MassInstrumentStateChangeImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _marketSegmentID = Constants.UNSET_INT;
    private int _instrumentScopeProductComplex = Constants.UNSET_INT;
    private int _securityMassTradingStatus = Constants.UNSET_INT;
    @TimestampMS private long _transactTime = Constants.UNSET_LONG;
    private int _numRelatedSym = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecMassStatGrp _instState;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getMarketSegmentID() { return _marketSegmentID; }
    @Override public final void setMarketSegmentID( int val ) { _marketSegmentID = val; }

    @Override public final int getInstrumentScopeProductComplex() { return _instrumentScopeProductComplex; }
    @Override public final void setInstrumentScopeProductComplex( int val ) { _instrumentScopeProductComplex = val; }

    @Override public final int getSecurityMassTradingStatus() { return _securityMassTradingStatus; }
    @Override public final void setSecurityMassTradingStatus( int val ) { _securityMassTradingStatus = val; }

    @Override public final long getTransactTime() { return _transactTime; }
    @Override public final void setTransactTime( long val ) { _transactTime = val; }

    @Override public final int getNumRelatedSym() { return _numRelatedSym; }
    @Override public final void setNumRelatedSym( int val ) { _numRelatedSym = val; }

    @Override public final SecMassStatGrp getInstState() { return _instState; }
    @Override public final void setInstState( SecMassStatGrp val ) { _instState = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _marketSegmentID = Constants.UNSET_INT;
        _instrumentScopeProductComplex = Constants.UNSET_INT;
        _securityMassTradingStatus = Constants.UNSET_INT;
        _transactTime = Constants.UNSET_LONG;
        _numRelatedSym = Constants.UNSET_INT;
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
        return ModelReusableTypes.MassInstrumentStateChange;
    }

    @Override
    public final MassInstrumentStateChangeImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MassInstrumentStateChangeImpl nxt ) {
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
        out.append( "MassInstrumentStateChangeImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getMarketSegmentID() && 0 != getMarketSegmentID() )             out.append( ", marketSegmentID=" ).append( getMarketSegmentID() );
        if ( Constants.UNSET_INT != getInstrumentScopeProductComplex() && 0 != getInstrumentScopeProductComplex() )             out.append( ", instrumentScopeProductComplex=" ).append( getInstrumentScopeProductComplex() );
        if ( Constants.UNSET_INT != getSecurityMassTradingStatus() && 0 != getSecurityMassTradingStatus() )             out.append( ", securityMassTradingStatus=" ).append( getSecurityMassTradingStatus() );
        if ( Constants.UNSET_LONG != getTransactTime() && 0 != getTransactTime() ) {
            out.append( ", transactTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getTransactTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getTransactTime() );
            out.append( " ( " );
            out.append( getTransactTime() ).append( " ) " );
        }
        if ( Constants.UNSET_INT != getNumRelatedSym() && 0 != getNumRelatedSym() )             out.append( ", numRelatedSym=" ).append( getNumRelatedSym() );

        SecMassStatGrpImpl tPtrinstState = (SecMassStatGrpImpl) getInstState();
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

    @Override public final void snapTo( MassInstrumentStateChange dest ) {
        ((MassInstrumentStateChangeImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MassInstrumentStateChange src ) {
        setMarketSegmentID( src.getMarketSegmentID() );
        setInstrumentScopeProductComplex( src.getInstrumentScopeProductComplex() );
        setSecurityMassTradingStatus( src.getSecurityMassTradingStatus() );
        setTransactTime( src.getTransactTime() );
        setNumRelatedSym( src.getNumRelatedSym() );
        SecMassStatGrpImpl tSrcPtrInstState = (SecMassStatGrpImpl) src.getInstState();
        SecMassStatGrpImpl tNewPtrInstState = null;
        while( tSrcPtrInstState != null ) {
            if ( tNewPtrInstState == null ) {
                tNewPtrInstState = new SecMassStatGrpImpl();
                setInstState( tNewPtrInstState );
            } else {
                tNewPtrInstState.setNext( new SecMassStatGrpImpl() );
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
    @Override public final void shallowCopyFrom( MassInstrumentStateChange src ) {
        setMarketSegmentID( src.getMarketSegmentID() );
        setInstrumentScopeProductComplex( src.getInstrumentScopeProductComplex() );
        setSecurityMassTradingStatus( src.getSecurityMassTradingStatus() );
        setTransactTime( src.getTransactTime() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MassInstrumentStateChange src ) {
        if ( Constants.UNSET_INT != src.getMarketSegmentID() ) setMarketSegmentID( src.getMarketSegmentID() );
        if ( Constants.UNSET_INT != src.getInstrumentScopeProductComplex() ) setInstrumentScopeProductComplex( src.getInstrumentScopeProductComplex() );
        if ( Constants.UNSET_INT != src.getSecurityMassTradingStatus() ) setSecurityMassTradingStatus( src.getSecurityMassTradingStatus() );
        if ( Constants.UNSET_LONG != src.getTransactTime() ) setTransactTime( src.getTransactTime() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
