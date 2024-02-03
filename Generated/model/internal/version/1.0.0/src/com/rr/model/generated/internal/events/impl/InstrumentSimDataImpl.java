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

public final class InstrumentSimDataImpl implements CommonHeader, InstrumentSimDataWrite, Copyable<InstrumentSimData>, Reusable<InstrumentSimDataImpl> {

   // Attrs

    private transient          InstrumentSimDataImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _dataSeqNum = Constants.UNSET_LONG;
    private final ReusableString _contract = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private double _bidSpreadEstimate = Constants.UNSET_DOUBLE;
    private double _limitStratImproveEst = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private Instrument _instrument;
    private SecurityIDSource _idSource = SecurityIDSource.ExchangeSymbol;
    private ExchangeCode _securityExchange;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final long getDataSeqNum() { return _dataSeqNum; }
    @Override public final void setDataSeqNum( long val ) { _dataSeqNum = val; }

    @Override public final SecurityIDSource getIdSource() { return _idSource; }
    @Override public final void setIdSource( SecurityIDSource val ) { _idSource = val; }

    @Override public final ViewString getContract() { return _contract; }

    @Override public final void setContract( byte[] buf, int offset, int len ) { _contract.setValue( buf, offset, len ); }
    @Override public final ReusableString getContractForUpdate() { return _contract; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final double getBidSpreadEstimate() { return _bidSpreadEstimate; }
    @Override public final void setBidSpreadEstimate( double val ) { _bidSpreadEstimate = val; }

    @Override public final double getLimitStratImproveEst() { return _limitStratImproveEst; }
    @Override public final void setLimitStratImproveEst( double val ) { _limitStratImproveEst = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _instrument = null;
        _dataSeqNum = Constants.UNSET_LONG;
        _idSource = SecurityIDSource.ExchangeSymbol;
        _contract.reset();
        _securityExchange = null;
        _bidSpreadEstimate = Constants.UNSET_DOUBLE;
        _limitStratImproveEst = Constants.UNSET_DOUBLE;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.InstrumentSimData;
    }

    @Override
    public final InstrumentSimDataImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( InstrumentSimDataImpl nxt ) {
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
        out.append( "InstrumentSimDataImpl" ).append( ' ' );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( getIdSource() != null )             out.append( ", idSource=" );
        if ( getIdSource() != null ) out.append( getIdSource().id() );
        if ( getContract().length() > 0 )             out.append( ", contract=" ).append( getContract() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( Utils.hasVal( getBidSpreadEstimate() ) ) out.append( ", bidSpreadEstimate=" ).append( getBidSpreadEstimate() );
        if ( Utils.hasVal( getLimitStratImproveEst() ) ) out.append( ", limitStratImproveEst=" ).append( getLimitStratImproveEst() );
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

    @Override public final void snapTo( InstrumentSimData dest ) {
        ((InstrumentSimDataImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( InstrumentSimData src ) {
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        setIdSource( src.getIdSource() );
        getContractForUpdate().copy( src.getContract() );
        setSecurityExchange( src.getSecurityExchange() );
        setBidSpreadEstimate( src.getBidSpreadEstimate() );
        setLimitStratImproveEst( src.getLimitStratImproveEst() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( InstrumentSimData src ) {
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        setIdSource( src.getIdSource() );
        getContractForUpdate().copy( src.getContract() );
        setSecurityExchange( src.getSecurityExchange() );
        setBidSpreadEstimate( src.getBidSpreadEstimate() );
        setLimitStratImproveEst( src.getLimitStratImproveEst() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( InstrumentSimData src ) {
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( getIdSource() != null )  setIdSource( src.getIdSource() );
        if ( src.getContract().length() > 0 ) getContractForUpdate().copy( src.getContract() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Utils.hasVal( src.getBidSpreadEstimate() ) ) setBidSpreadEstimate( src.getBidSpreadEstimate() );
        if ( Utils.hasVal( src.getLimitStratImproveEst() ) ) setLimitStratImproveEst( src.getLimitStratImproveEst() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
