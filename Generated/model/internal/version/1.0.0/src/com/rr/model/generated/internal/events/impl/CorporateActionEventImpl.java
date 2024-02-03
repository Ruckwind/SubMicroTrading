package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.CorporateActionClassification;
import com.rr.model.generated.internal.type.AdjustmentType;
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

public final class CorporateActionEventImpl implements CommonHeader, CorporateActionEventWrite, Copyable<CorporateActionEvent>, Reusable<CorporateActionEventImpl> {

   // Attrs

    private transient          CorporateActionEventImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _dataSeqNum = Constants.UNSET_LONG;
    private final ReusableString _subject = new ReusableString( SizeType.SUBJECT_LEN.getSize() );
    private final ReusableString _securityId = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    @TimestampMS private long _announceTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _qualifyTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _recordTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _actionTimestamp = Constants.UNSET_LONG;
    private double _priceAdjustVal = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private DataSrc _dataSrc = DataSrc.UNS;
    private Instrument _instrument;
    private SecurityIDSource _idSource = SecurityIDSource.ExchangeSymbol;
    private ExchangeCode _securityExchange;
    private CorporateActionClassification _type;
    private Currency _ccy;
    private AdjustmentType _adjustType = AdjustmentType.Factor;

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

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final CorporateActionClassification getType() { return _type; }
    @Override public final void setType( CorporateActionClassification val ) { _type = val; }

    @Override public final long getAnnounceTimestamp() { return _announceTimestamp; }
    @Override public final void setAnnounceTimestamp( long val ) { _announceTimestamp = val; }

    @Override public final long getQualifyTimestamp() { return _qualifyTimestamp; }
    @Override public final void setQualifyTimestamp( long val ) { _qualifyTimestamp = val; }

    @Override public final long getRecordTimestamp() { return _recordTimestamp; }
    @Override public final void setRecordTimestamp( long val ) { _recordTimestamp = val; }

    @Override public final long getActionTimestamp() { return _actionTimestamp; }
    @Override public final void setActionTimestamp( long val ) { _actionTimestamp = val; }

    @Override public final Currency getCcy() { return _ccy; }
    @Override public final void setCcy( Currency val ) { _ccy = val; }

    @Override public final AdjustmentType getAdjustType() { return _adjustType; }
    @Override public final void setAdjustType( AdjustmentType val ) { _adjustType = val; }

    @Override public final double getPriceAdjustVal() { return _priceAdjustVal; }
    @Override public final void setPriceAdjustVal( double val ) { _priceAdjustVal = val; }

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
        _idSource = SecurityIDSource.ExchangeSymbol;
        _securityId.reset();
        _securityExchange = null;
        _type = null;
        _announceTimestamp = Constants.UNSET_LONG;
        _qualifyTimestamp = Constants.UNSET_LONG;
        _recordTimestamp = Constants.UNSET_LONG;
        _actionTimestamp = Constants.UNSET_LONG;
        _ccy = null;
        _adjustType = AdjustmentType.Factor;
        _priceAdjustVal = Constants.UNSET_DOUBLE;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.CorporateActionEvent;
    }

    @Override
    public final CorporateActionEventImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( CorporateActionEventImpl nxt ) {
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
        out.append( "CorporateActionEventImpl" ).append( ' ' );
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( getSubject().length() > 0 )             out.append( ", subject=" ).append( getSubject() );
        if ( getIdSource() != null )             out.append( ", idSource=" );
        if ( getIdSource() != null ) out.append( getIdSource().id() );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getType() != null )             out.append( ", type=" ).append( getType() );
        if ( Constants.UNSET_LONG != getAnnounceTimestamp() && 0 != getAnnounceTimestamp() ) {
            out.append( ", announceTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getAnnounceTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getAnnounceTimestamp() );
            out.append( " ( " );
            out.append( getAnnounceTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getQualifyTimestamp() && 0 != getQualifyTimestamp() ) {
            out.append( ", qualifyTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getQualifyTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getQualifyTimestamp() );
            out.append( " ( " );
            out.append( getQualifyTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getRecordTimestamp() && 0 != getRecordTimestamp() ) {
            out.append( ", recordTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getRecordTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getRecordTimestamp() );
            out.append( " ( " );
            out.append( getRecordTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getActionTimestamp() && 0 != getActionTimestamp() ) {
            out.append( ", actionTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getActionTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getActionTimestamp() );
            out.append( " ( " );
            out.append( getActionTimestamp() ).append( " ) " );
        }
        if ( getCcy() != null )             out.append( ", ccy=" );
        if ( getCcy() != null ) out.append( getCcy().id() );
        if ( getAdjustType() != null )             out.append( ", adjustType=" ).append( getAdjustType() );
        if ( Utils.hasVal( getPriceAdjustVal() ) ) out.append( ", priceAdjustVal=" ).append( getPriceAdjustVal() );
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

    @Override public final void snapTo( CorporateActionEvent dest ) {
        ((CorporateActionEventImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( CorporateActionEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        setIdSource( src.getIdSource() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityExchange( src.getSecurityExchange() );
        setType( src.getType() );
        setAnnounceTimestamp( src.getAnnounceTimestamp() );
        setQualifyTimestamp( src.getQualifyTimestamp() );
        setRecordTimestamp( src.getRecordTimestamp() );
        setActionTimestamp( src.getActionTimestamp() );
        setCcy( src.getCcy() );
        setAdjustType( src.getAdjustType() );
        setPriceAdjustVal( src.getPriceAdjustVal() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( CorporateActionEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        setIdSource( src.getIdSource() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityExchange( src.getSecurityExchange() );
        setType( src.getType() );
        setAnnounceTimestamp( src.getAnnounceTimestamp() );
        setQualifyTimestamp( src.getQualifyTimestamp() );
        setRecordTimestamp( src.getRecordTimestamp() );
        setActionTimestamp( src.getActionTimestamp() );
        setCcy( src.getCcy() );
        setAdjustType( src.getAdjustType() );
        setPriceAdjustVal( src.getPriceAdjustVal() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( CorporateActionEvent src ) {
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( src.getSubject().length() > 0 ) getSubjectForUpdate().copy( src.getSubject() );
        if ( getIdSource() != null )  setIdSource( src.getIdSource() );
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        setType( src.getType() );
        if ( Constants.UNSET_LONG != src.getAnnounceTimestamp() ) setAnnounceTimestamp( src.getAnnounceTimestamp() );
        if ( Constants.UNSET_LONG != src.getQualifyTimestamp() ) setQualifyTimestamp( src.getQualifyTimestamp() );
        if ( Constants.UNSET_LONG != src.getRecordTimestamp() ) setRecordTimestamp( src.getRecordTimestamp() );
        if ( Constants.UNSET_LONG != src.getActionTimestamp() ) setActionTimestamp( src.getActionTimestamp() );
        if ( getCcy() != null )  setCcy( src.getCcy() );
        setAdjustType( src.getAdjustType() );
        if ( Utils.hasVal( src.getPriceAdjustVal() ) ) setPriceAdjustVal( src.getPriceAdjustVal() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
