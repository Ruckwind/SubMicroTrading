package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LoanRateType;
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

public final class BrokerLoanResponseImpl implements CommonHeader, BrokerLoanResponseWrite, Copyable<BrokerLoanResponse>, Reusable<BrokerLoanResponseImpl> {

   // Attrs

    private transient          BrokerLoanResponseImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _dataSeqNum = Constants.UNSET_LONG;
    private final ReusableString _subject = new ReusableString( SizeType.SUBJECT_LEN.getSize() );
    private final ReusableString _securityId = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private final ReusableString _reference = new ReusableString( SizeType.LOAN_RESPONSE_LEN.getSize() );
    private boolean _isDisabled = false;
    private double _approveQty = Constants.UNSET_DOUBLE;
    private double _amount = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private DataSrc _dataSrc = DataSrc.UNS;
    private Instrument _instrument;
    private SecurityIDSource _idSource = SecurityIDSource.ISIN;
    private ExchangeCode _securityExchange;
    private LoanRateType _type;
    private PartyID _broker;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final DataSrc getDataSrc() { return _dataSrc; }
    @Override public final void setDataSrc( DataSrc val ) { _dataSrc = val; }

    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final long getDataSeqNum() { return _dataSeqNum; }
    @Override public final void setDataSeqNum( long val ) { _dataSeqNum = val; }

    @Override public final ViewString getSubject() { return _subject; }

    @Override public final void setSubject( byte[] buf, int offset, int len ) { _subject.setValue( buf, offset, len ); }
    @Override public final ReusableString getSubjectForUpdate() { return _subject; }

    @Override public final SecurityIDSource getIdSource() { return _idSource; }
    @Override public final void setIdSource( SecurityIDSource val ) { _idSource = val; }

    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final ViewString getReference() { return _reference; }

    @Override public final void setReference( byte[] buf, int offset, int len ) { _reference.setValue( buf, offset, len ); }
    @Override public final ReusableString getReferenceForUpdate() { return _reference; }

    @Override public final boolean getIsDisabled() { return _isDisabled; }
    @Override public final void setIsDisabled( boolean val ) { _isDisabled = val; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final double getApproveQty() { return _approveQty; }
    @Override public final void setApproveQty( double val ) { _approveQty = val; }

    @Override public final double getAmount() { return _amount; }
    @Override public final void setAmount( double val ) { _amount = val; }

    @Override public final LoanRateType getType() { return _type; }
    @Override public final void setType( LoanRateType val ) { _type = val; }

    @Override public final PartyID getBroker() { return _broker; }
    @Override public final void setBroker( PartyID val ) { _broker = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _dataSrc = DataSrc.UNS;
        _instrument = null;
        _dataSeqNum = Constants.UNSET_LONG;
        _subject.reset();
        _idSource = SecurityIDSource.ISIN;
        _securityId.reset();
        _reference.reset();
        _isDisabled = false;
        _securityExchange = null;
        _approveQty = Constants.UNSET_DOUBLE;
        _amount = Constants.UNSET_DOUBLE;
        _type = null;
        _broker = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.BrokerLoanResponse;
    }

    @Override
    public final BrokerLoanResponseImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( BrokerLoanResponseImpl nxt ) {
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
        out.append( "BrokerLoanResponseImpl" ).append( ' ' );
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( getSubject().length() > 0 )             out.append( ", subject=" ).append( getSubject() );
        if ( getIdSource() != null )             out.append( ", idSource=" );
        if ( getIdSource() != null ) out.append( getIdSource().id() );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getReference().length() > 0 )             out.append( ", reference=" ).append( getReference() );
        out.append( ", isDisabled=" ).append( getIsDisabled() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( Utils.hasVal( getApproveQty() ) ) out.append( ", approveQty=" ).append( getApproveQty() );
        if ( Utils.hasVal( getAmount() ) ) out.append( ", amount=" ).append( getAmount() );
        if ( getType() != null )             out.append( ", type=" ).append( getType() );
        if ( getBroker() != null )             out.append( ", broker=" );
        if ( getBroker() != null ) out.append( getBroker().id() );
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

    @Override public final void snapTo( BrokerLoanResponse dest ) {
        ((BrokerLoanResponseImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( BrokerLoanResponse src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        setIdSource( src.getIdSource() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getReferenceForUpdate().copy( src.getReference() );
        setIsDisabled( src.getIsDisabled() );
        setSecurityExchange( src.getSecurityExchange() );
        setApproveQty( src.getApproveQty() );
        setAmount( src.getAmount() );
        setType( src.getType() );
        setBroker( src.getBroker() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( BrokerLoanResponse src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        setIdSource( src.getIdSource() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getReferenceForUpdate().copy( src.getReference() );
        setIsDisabled( src.getIsDisabled() );
        setSecurityExchange( src.getSecurityExchange() );
        setApproveQty( src.getApproveQty() );
        setAmount( src.getAmount() );
        setType( src.getType() );
        setBroker( src.getBroker() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( BrokerLoanResponse src ) {
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( src.getSubject().length() > 0 ) getSubjectForUpdate().copy( src.getSubject() );
        if ( getIdSource() != null )  setIdSource( src.getIdSource() );
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( src.getReference().length() > 0 ) getReferenceForUpdate().copy( src.getReference() );
        setIsDisabled( src.getIsDisabled() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Utils.hasVal( src.getApproveQty() ) ) setApproveQty( src.getApproveQty() );
        if ( Utils.hasVal( src.getAmount() ) ) setAmount( src.getAmount() );
        setType( src.getType() );
        if ( getBroker() != null )  setBroker( src.getBroker() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
