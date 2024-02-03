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

public final class RefPriceEventImpl implements CommonHeader, RefPriceEventWrite, Copyable<RefPriceEvent>, Reusable<RefPriceEventImpl> {

   // Attrs

    private transient          RefPriceEventImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _dataSeqNum = Constants.UNSET_LONG;
    private final ReusableString _subject = new ReusableString( SizeType.SUBJECT_LEN.getSize() );
    private final ReusableString _code = new ReusableString( SizeType.CODE_LEN.getSize() );
    private double _val = Constants.UNSET_DOUBLE;
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

    @Override public final long getDataSeqNum() { return _dataSeqNum; }
    @Override public final void setDataSeqNum( long val ) { _dataSeqNum = val; }

    @Override public final ViewString getSubject() { return _subject; }

    @Override public final void setSubject( byte[] buf, int offset, int len ) { _subject.setValue( buf, offset, len ); }
    @Override public final ReusableString getSubjectForUpdate() { return _subject; }

    @Override public final ViewString getCode() { return _code; }

    @Override public final void setCode( byte[] buf, int offset, int len ) { _code.setValue( buf, offset, len ); }
    @Override public final ReusableString getCodeForUpdate() { return _code; }

    @Override public final double getVal() { return _val; }
    @Override public final void setVal( double val ) { _val = val; }

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
        _code.reset();
        _val = Constants.UNSET_DOUBLE;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.RefPriceEvent;
    }

    @Override
    public final RefPriceEventImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( RefPriceEventImpl nxt ) {
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
        out.append( "RefPriceEventImpl" ).append( ' ' );
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( getSubject().length() > 0 )             out.append( ", subject=" ).append( getSubject() );
        if ( getCode().length() > 0 )             out.append( ", code=" ).append( getCode() );
        if ( Utils.hasVal( getVal() ) ) out.append( ", val=" ).append( getVal() );
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

    @Override public final void snapTo( RefPriceEvent dest ) {
        ((RefPriceEventImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( RefPriceEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        getCodeForUpdate().copy( src.getCode() );
        setVal( src.getVal() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( RefPriceEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        setDataSeqNum( src.getDataSeqNum() );
        getSubjectForUpdate().copy( src.getSubject() );
        getCodeForUpdate().copy( src.getCode() );
        setVal( src.getVal() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( RefPriceEvent src ) {
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( src.getSubject().length() > 0 ) getSubjectForUpdate().copy( src.getSubject() );
        if ( src.getCode().length() > 0 ) getCodeForUpdate().copy( src.getCode() );
        if ( Utils.hasVal( src.getVal() ) ) setVal( src.getVal() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
