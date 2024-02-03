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

public final class LeanHogIndexEventImpl implements CommonHeader, LeanHogIndexEventWrite, Copyable<LeanHogIndexEvent>, Reusable<LeanHogIndexEventImpl> {

   // Attrs

    private transient          LeanHogIndexEventImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _subject = new ReusableString( SizeType.SUBJECT_LEN.getSize() );
    private long _dataSeqNum = Constants.UNSET_LONG;
    private int _indexDate = Constants.UNSET_INT;
    private double _negotHeadCount = Constants.UNSET_DOUBLE;
    private double _negotAverageNetPrice = Constants.UNSET_DOUBLE;
    private double _negotAverageCarcWt = Constants.UNSET_DOUBLE;
    private double _spmfHeadCount = Constants.UNSET_DOUBLE;
    private double _spmfAverageNetPrice = Constants.UNSET_DOUBLE;
    private double _spmfAverageCarcWt = Constants.UNSET_DOUBLE;
    private double _negotSpmfHeadCount = Constants.UNSET_DOUBLE;
    private double _negotSpmfAverageNetPrice = Constants.UNSET_DOUBLE;
    private double _negotSpmfAverageCarcWt = Constants.UNSET_DOUBLE;
    private double _dailyWeightedPrice = Constants.UNSET_DOUBLE;
    private double _indexValue = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private DataSrc _dataSrc = DataSrc.UNS;
    private Instrument _instrument;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final DataSrc getDataSrc() { return _dataSrc; }
    @Override public final void setDataSrc( DataSrc val ) { _dataSrc = val; }

    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final ViewString getSubject() { return _subject; }

    @Override public final void setSubject( byte[] buf, int offset, int len ) { _subject.setValue( buf, offset, len ); }
    @Override public final ReusableString getSubjectForUpdate() { return _subject; }

    @Override public final long getDataSeqNum() { return _dataSeqNum; }
    @Override public final void setDataSeqNum( long val ) { _dataSeqNum = val; }

    @Override public final int getIndexDate() { return _indexDate; }
    @Override public final void setIndexDate( int val ) { _indexDate = val; }

    @Override public final double getNegotHeadCount() { return _negotHeadCount; }
    @Override public final void setNegotHeadCount( double val ) { _negotHeadCount = val; }

    @Override public final double getNegotAverageNetPrice() { return _negotAverageNetPrice; }
    @Override public final void setNegotAverageNetPrice( double val ) { _negotAverageNetPrice = val; }

    @Override public final double getNegotAverageCarcWt() { return _negotAverageCarcWt; }
    @Override public final void setNegotAverageCarcWt( double val ) { _negotAverageCarcWt = val; }

    @Override public final double getSpmfHeadCount() { return _spmfHeadCount; }
    @Override public final void setSpmfHeadCount( double val ) { _spmfHeadCount = val; }

    @Override public final double getSpmfAverageNetPrice() { return _spmfAverageNetPrice; }
    @Override public final void setSpmfAverageNetPrice( double val ) { _spmfAverageNetPrice = val; }

    @Override public final double getSpmfAverageCarcWt() { return _spmfAverageCarcWt; }
    @Override public final void setSpmfAverageCarcWt( double val ) { _spmfAverageCarcWt = val; }

    @Override public final double getNegotSpmfHeadCount() { return _negotSpmfHeadCount; }
    @Override public final void setNegotSpmfHeadCount( double val ) { _negotSpmfHeadCount = val; }

    @Override public final double getNegotSpmfAverageNetPrice() { return _negotSpmfAverageNetPrice; }
    @Override public final void setNegotSpmfAverageNetPrice( double val ) { _negotSpmfAverageNetPrice = val; }

    @Override public final double getNegotSpmfAverageCarcWt() { return _negotSpmfAverageCarcWt; }
    @Override public final void setNegotSpmfAverageCarcWt( double val ) { _negotSpmfAverageCarcWt = val; }

    @Override public final double getDailyWeightedPrice() { return _dailyWeightedPrice; }
    @Override public final void setDailyWeightedPrice( double val ) { _dailyWeightedPrice = val; }

    @Override public final double getIndexValue() { return _indexValue; }
    @Override public final void setIndexValue( double val ) { _indexValue = val; }

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
        _subject.reset();
        _dataSeqNum = Constants.UNSET_LONG;
        _indexDate = Constants.UNSET_INT;
        _negotHeadCount = Constants.UNSET_DOUBLE;
        _negotAverageNetPrice = Constants.UNSET_DOUBLE;
        _negotAverageCarcWt = Constants.UNSET_DOUBLE;
        _spmfHeadCount = Constants.UNSET_DOUBLE;
        _spmfAverageNetPrice = Constants.UNSET_DOUBLE;
        _spmfAverageCarcWt = Constants.UNSET_DOUBLE;
        _negotSpmfHeadCount = Constants.UNSET_DOUBLE;
        _negotSpmfAverageNetPrice = Constants.UNSET_DOUBLE;
        _negotSpmfAverageCarcWt = Constants.UNSET_DOUBLE;
        _dailyWeightedPrice = Constants.UNSET_DOUBLE;
        _indexValue = Constants.UNSET_DOUBLE;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.LeanHogIndexEvent;
    }

    @Override
    public final LeanHogIndexEventImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( LeanHogIndexEventImpl nxt ) {
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
        out.append( "LeanHogIndexEventImpl" ).append( ' ' );
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( getSubject().length() > 0 )             out.append( ", subject=" ).append( getSubject() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( Constants.UNSET_INT != getIndexDate() && 0 != getIndexDate() )             out.append( ", indexDate=" ).append( getIndexDate() );
        if ( Utils.hasVal( getNegotHeadCount() ) ) out.append( ", negotHeadCount=" ).append( getNegotHeadCount() );
        if ( Utils.hasVal( getNegotAverageNetPrice() ) ) out.append( ", negotAverageNetPrice=" ).append( getNegotAverageNetPrice() );
        if ( Utils.hasVal( getNegotAverageCarcWt() ) ) out.append( ", negotAverageCarcWt=" ).append( getNegotAverageCarcWt() );
        if ( Utils.hasVal( getSpmfHeadCount() ) ) out.append( ", spmfHeadCount=" ).append( getSpmfHeadCount() );
        if ( Utils.hasVal( getSpmfAverageNetPrice() ) ) out.append( ", spmfAverageNetPrice=" ).append( getSpmfAverageNetPrice() );
        if ( Utils.hasVal( getSpmfAverageCarcWt() ) ) out.append( ", spmfAverageCarcWt=" ).append( getSpmfAverageCarcWt() );
        if ( Utils.hasVal( getNegotSpmfHeadCount() ) ) out.append( ", negotSpmfHeadCount=" ).append( getNegotSpmfHeadCount() );
        if ( Utils.hasVal( getNegotSpmfAverageNetPrice() ) ) out.append( ", negotSpmfAverageNetPrice=" ).append( getNegotSpmfAverageNetPrice() );
        if ( Utils.hasVal( getNegotSpmfAverageCarcWt() ) ) out.append( ", negotSpmfAverageCarcWt=" ).append( getNegotSpmfAverageCarcWt() );
        if ( Utils.hasVal( getDailyWeightedPrice() ) ) out.append( ", dailyWeightedPrice=" ).append( getDailyWeightedPrice() );
        if ( Utils.hasVal( getIndexValue() ) ) out.append( ", indexValue=" ).append( getIndexValue() );
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

    @Override public final void snapTo( LeanHogIndexEvent dest ) {
        ((LeanHogIndexEventImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( LeanHogIndexEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        getSubjectForUpdate().copy( src.getSubject() );
        setDataSeqNum( src.getDataSeqNum() );
        setIndexDate( src.getIndexDate() );
        setNegotHeadCount( src.getNegotHeadCount() );
        setNegotAverageNetPrice( src.getNegotAverageNetPrice() );
        setNegotAverageCarcWt( src.getNegotAverageCarcWt() );
        setSpmfHeadCount( src.getSpmfHeadCount() );
        setSpmfAverageNetPrice( src.getSpmfAverageNetPrice() );
        setSpmfAverageCarcWt( src.getSpmfAverageCarcWt() );
        setNegotSpmfHeadCount( src.getNegotSpmfHeadCount() );
        setNegotSpmfAverageNetPrice( src.getNegotSpmfAverageNetPrice() );
        setNegotSpmfAverageCarcWt( src.getNegotSpmfAverageCarcWt() );
        setDailyWeightedPrice( src.getDailyWeightedPrice() );
        setIndexValue( src.getIndexValue() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( LeanHogIndexEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        getSubjectForUpdate().copy( src.getSubject() );
        setDataSeqNum( src.getDataSeqNum() );
        setIndexDate( src.getIndexDate() );
        setNegotHeadCount( src.getNegotHeadCount() );
        setNegotAverageNetPrice( src.getNegotAverageNetPrice() );
        setNegotAverageCarcWt( src.getNegotAverageCarcWt() );
        setSpmfHeadCount( src.getSpmfHeadCount() );
        setSpmfAverageNetPrice( src.getSpmfAverageNetPrice() );
        setSpmfAverageCarcWt( src.getSpmfAverageCarcWt() );
        setNegotSpmfHeadCount( src.getNegotSpmfHeadCount() );
        setNegotSpmfAverageNetPrice( src.getNegotSpmfAverageNetPrice() );
        setNegotSpmfAverageCarcWt( src.getNegotSpmfAverageCarcWt() );
        setDailyWeightedPrice( src.getDailyWeightedPrice() );
        setIndexValue( src.getIndexValue() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( LeanHogIndexEvent src ) {
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( src.getSubject().length() > 0 ) getSubjectForUpdate().copy( src.getSubject() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( Constants.UNSET_INT != src.getIndexDate() ) setIndexDate( src.getIndexDate() );
        if ( Utils.hasVal( src.getNegotHeadCount() ) ) setNegotHeadCount( src.getNegotHeadCount() );
        if ( Utils.hasVal( src.getNegotAverageNetPrice() ) ) setNegotAverageNetPrice( src.getNegotAverageNetPrice() );
        if ( Utils.hasVal( src.getNegotAverageCarcWt() ) ) setNegotAverageCarcWt( src.getNegotAverageCarcWt() );
        if ( Utils.hasVal( src.getSpmfHeadCount() ) ) setSpmfHeadCount( src.getSpmfHeadCount() );
        if ( Utils.hasVal( src.getSpmfAverageNetPrice() ) ) setSpmfAverageNetPrice( src.getSpmfAverageNetPrice() );
        if ( Utils.hasVal( src.getSpmfAverageCarcWt() ) ) setSpmfAverageCarcWt( src.getSpmfAverageCarcWt() );
        if ( Utils.hasVal( src.getNegotSpmfHeadCount() ) ) setNegotSpmfHeadCount( src.getNegotSpmfHeadCount() );
        if ( Utils.hasVal( src.getNegotSpmfAverageNetPrice() ) ) setNegotSpmfAverageNetPrice( src.getNegotSpmfAverageNetPrice() );
        if ( Utils.hasVal( src.getNegotSpmfAverageCarcWt() ) ) setNegotSpmfAverageCarcWt( src.getNegotSpmfAverageCarcWt() );
        if ( Utils.hasVal( src.getDailyWeightedPrice() ) ) setDailyWeightedPrice( src.getDailyWeightedPrice() );
        if ( Utils.hasVal( src.getIndexValue() ) ) setIndexValue( src.getIndexValue() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
