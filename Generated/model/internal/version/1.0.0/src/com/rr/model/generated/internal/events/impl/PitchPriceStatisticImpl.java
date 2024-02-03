package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.StatsPriceType;
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

public final class PitchPriceStatisticImpl implements BaseCboePitch, PitchPriceStatisticWrite, Copyable<PitchPriceStatistic>, Reusable<PitchPriceStatisticImpl> {

   // Attrs

    private transient          PitchPriceStatisticImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString  _securityId = new ReusableString( SizeType.CBOE_EUTP_SYMBOL.getSize() );
    private double _price = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecurityIDSource _securityIdSrc;
    private ExchangeCode _securityExchange;
    private StatsPriceType _statType;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final SecurityIDSource getSecurityIdSrc() { return _securityIdSrc; }
    @Override public final void setSecurityIdSrc( SecurityIDSource val ) { _securityIdSrc = val; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final StatsPriceType getStatType() { return _statType; }
    @Override public final void setStatType( StatsPriceType val ) { _statType = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _securityId.reset();
        _securityIdSrc = null;
        _securityExchange = null;
        _price = Constants.UNSET_DOUBLE;
        _statType = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.PitchPriceStatistic;
    }

    @Override
    public final PitchPriceStatisticImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( PitchPriceStatisticImpl nxt ) {
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
        out.append( "PitchPriceStatisticImpl" ).append( ' ' );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSecurityIdSrc() != null )             out.append( ", securityIdSrc=" );
        if ( getSecurityIdSrc() != null ) out.append( getSecurityIdSrc().id() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( Utils.hasVal( getPrice() ) ) out.append( ", price=" ).append( getPrice() );
        if ( getStatType() != null )             out.append( ", statType=" ).append( getStatType() );
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

    @Override public final void snapTo( PitchPriceStatistic dest ) {
        ((PitchPriceStatisticImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( PitchPriceStatistic src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setPrice( src.getPrice() );
        setStatType( src.getStatType() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( PitchPriceStatistic src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setPrice( src.getPrice() );
        setStatType( src.getStatType() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( PitchPriceStatistic src ) {
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( getSecurityIdSrc() != null )  setSecurityIdSrc( src.getSecurityIdSrc() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Utils.hasVal( src.getPrice() ) ) setPrice( src.getPrice() );
        setStatType( src.getStatType() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
