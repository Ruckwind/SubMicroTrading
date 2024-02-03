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

public final class OpenInterestEventImpl implements CommonHeader, OpenInterestEventWrite, Copyable<OpenInterestEvent>, Reusable<OpenInterestEventImpl> {

   // Attrs

    private transient          OpenInterestEventImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _subject = new ReusableString( SizeType.SUBJECT_LEN.getSize() );
    private long _dataSeqNum = Constants.UNSET_LONG;
    private double _openInterest = Constants.UNSET_DOUBLE;
    private double _netOpenInterest = Constants.UNSET_DOUBLE;
    private double _prevOpenInterest = Constants.UNSET_DOUBLE;
    @TimestampMS private long _openInterestDateTime = Constants.UNSET_LONG;
    @TimestampMS private long _prevOpenInterestDateTime = Constants.UNSET_LONG;
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

    @Override public final double getOpenInterest() { return _openInterest; }
    @Override public final void setOpenInterest( double val ) { _openInterest = val; }

    @Override public final double getNetOpenInterest() { return _netOpenInterest; }
    @Override public final void setNetOpenInterest( double val ) { _netOpenInterest = val; }

    @Override public final double getPrevOpenInterest() { return _prevOpenInterest; }
    @Override public final void setPrevOpenInterest( double val ) { _prevOpenInterest = val; }

    @Override public final long getOpenInterestDateTime() { return _openInterestDateTime; }
    @Override public final void setOpenInterestDateTime( long val ) { _openInterestDateTime = val; }

    @Override public final long getPrevOpenInterestDateTime() { return _prevOpenInterestDateTime; }
    @Override public final void setPrevOpenInterestDateTime( long val ) { _prevOpenInterestDateTime = val; }

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
        _openInterest = Constants.UNSET_DOUBLE;
        _netOpenInterest = Constants.UNSET_DOUBLE;
        _prevOpenInterest = Constants.UNSET_DOUBLE;
        _openInterestDateTime = Constants.UNSET_LONG;
        _prevOpenInterestDateTime = Constants.UNSET_LONG;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.OpenInterestEvent;
    }

    @Override
    public final OpenInterestEventImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( OpenInterestEventImpl nxt ) {
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
        out.append( "OpenInterestEventImpl" ).append( ' ' );
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( getSubject().length() > 0 )             out.append( ", subject=" ).append( getSubject() );
        if ( Constants.UNSET_LONG != getDataSeqNum() && 0 != getDataSeqNum() )             out.append( ", dataSeqNum=" ).append( getDataSeqNum() );
        if ( Utils.hasVal( getOpenInterest() ) ) out.append( ", openInterest=" ).append( getOpenInterest() );
        if ( Utils.hasVal( getNetOpenInterest() ) ) out.append( ", netOpenInterest=" ).append( getNetOpenInterest() );
        if ( Utils.hasVal( getPrevOpenInterest() ) ) out.append( ", prevOpenInterest=" ).append( getPrevOpenInterest() );
        if ( Constants.UNSET_LONG != getOpenInterestDateTime() && 0 != getOpenInterestDateTime() ) {
            out.append( ", openInterestDateTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getOpenInterestDateTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getOpenInterestDateTime() );
            out.append( " ( " );
            out.append( getOpenInterestDateTime() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getPrevOpenInterestDateTime() && 0 != getPrevOpenInterestDateTime() ) {
            out.append( ", prevOpenInterestDateTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getPrevOpenInterestDateTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getPrevOpenInterestDateTime() );
            out.append( " ( " );
            out.append( getPrevOpenInterestDateTime() ).append( " ) " );
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

    @Override public final void snapTo( OpenInterestEvent dest ) {
        ((OpenInterestEventImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( OpenInterestEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        getSubjectForUpdate().copy( src.getSubject() );
        setDataSeqNum( src.getDataSeqNum() );
        setOpenInterest( src.getOpenInterest() );
        setNetOpenInterest( src.getNetOpenInterest() );
        setPrevOpenInterest( src.getPrevOpenInterest() );
        setOpenInterestDateTime( src.getOpenInterestDateTime() );
        setPrevOpenInterestDateTime( src.getPrevOpenInterestDateTime() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( OpenInterestEvent src ) {
        setDataSrc( src.getDataSrc() );
        setInstrument( src.getInstrument() );
        getSubjectForUpdate().copy( src.getSubject() );
        setDataSeqNum( src.getDataSeqNum() );
        setOpenInterest( src.getOpenInterest() );
        setNetOpenInterest( src.getNetOpenInterest() );
        setPrevOpenInterest( src.getPrevOpenInterest() );
        setOpenInterestDateTime( src.getOpenInterestDateTime() );
        setPrevOpenInterestDateTime( src.getPrevOpenInterestDateTime() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( OpenInterestEvent src ) {
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( src.getSubject().length() > 0 ) getSubjectForUpdate().copy( src.getSubject() );
        if ( Constants.UNSET_LONG != src.getDataSeqNum() ) setDataSeqNum( src.getDataSeqNum() );
        if ( Utils.hasVal( src.getOpenInterest() ) ) setOpenInterest( src.getOpenInterest() );
        if ( Utils.hasVal( src.getNetOpenInterest() ) ) setNetOpenInterest( src.getNetOpenInterest() );
        if ( Utils.hasVal( src.getPrevOpenInterest() ) ) setPrevOpenInterest( src.getPrevOpenInterest() );
        if ( Constants.UNSET_LONG != src.getOpenInterestDateTime() ) setOpenInterestDateTime( src.getOpenInterestDateTime() );
        if ( Constants.UNSET_LONG != src.getPrevOpenInterestDateTime() ) setPrevOpenInterestDateTime( src.getPrevOpenInterestDateTime() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
