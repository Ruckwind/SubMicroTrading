package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.AuctionType;
import com.rr.model.generated.internal.type.PriceCollarTolerance;
import com.rr.model.generated.internal.type.AuctionCollarIncludesPrimaryQuotes;
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

public final class AuctionSummaryImpl implements BaseCboePitch, AuctionSummaryWrite, Copyable<AuctionSummary>, Reusable<AuctionSummaryImpl> {

   // Attrs

    private transient          AuctionSummaryImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString  _securityId = new ReusableString( SizeType.CBOE_EUTP_SYMBOL.getSize() );
    private double _price = Constants.UNSET_DOUBLE;
    private int _qty = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecurityIDSource _securityIdSrc;
    private ExchangeCode _securityExchange;
    private AuctionType _auctionType;
    private PriceCollarTolerance _priceCollarTol;
    private AuctionCollarIncludesPrimaryQuotes _incPrimary;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final SecurityIDSource getSecurityIdSrc() { return _securityIdSrc; }
    @Override public final void setSecurityIdSrc( SecurityIDSource val ) { _securityIdSrc = val; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final AuctionType getAuctionType() { return _auctionType; }
    @Override public final void setAuctionType( AuctionType val ) { _auctionType = val; }

    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final int getQty() { return _qty; }
    @Override public final void setQty( int val ) { _qty = val; }

    @Override public final PriceCollarTolerance getPriceCollarTol() { return _priceCollarTol; }
    @Override public final void setPriceCollarTol( PriceCollarTolerance val ) { _priceCollarTol = val; }

    @Override public final AuctionCollarIncludesPrimaryQuotes getIncPrimary() { return _incPrimary; }
    @Override public final void setIncPrimary( AuctionCollarIncludesPrimaryQuotes val ) { _incPrimary = val; }

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
        _auctionType = null;
        _price = Constants.UNSET_DOUBLE;
        _qty = Constants.UNSET_INT;
        _priceCollarTol = null;
        _incPrimary = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.AuctionSummary;
    }

    @Override
    public final AuctionSummaryImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( AuctionSummaryImpl nxt ) {
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
        out.append( "AuctionSummaryImpl" ).append( ' ' );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSecurityIdSrc() != null )             out.append( ", securityIdSrc=" );
        if ( getSecurityIdSrc() != null ) out.append( getSecurityIdSrc().id() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getAuctionType() != null )             out.append( ", auctionType=" ).append( getAuctionType() );
        if ( Utils.hasVal( getPrice() ) ) out.append( ", price=" ).append( getPrice() );
        if ( Constants.UNSET_INT != getQty() && 0 != getQty() )             out.append( ", qty=" ).append( getQty() );
        if ( getPriceCollarTol() != null )             out.append( ", priceCollarTol=" ).append( getPriceCollarTol() );
        if ( getIncPrimary() != null )             out.append( ", incPrimary=" ).append( getIncPrimary() );
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

    @Override public final void snapTo( AuctionSummary dest ) {
        ((AuctionSummaryImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( AuctionSummary src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setAuctionType( src.getAuctionType() );
        setPrice( src.getPrice() );
        setQty( src.getQty() );
        setPriceCollarTol( src.getPriceCollarTol() );
        setIncPrimary( src.getIncPrimary() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( AuctionSummary src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setAuctionType( src.getAuctionType() );
        setPrice( src.getPrice() );
        setQty( src.getQty() );
        setPriceCollarTol( src.getPriceCollarTol() );
        setIncPrimary( src.getIncPrimary() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( AuctionSummary src ) {
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( getSecurityIdSrc() != null )  setSecurityIdSrc( src.getSecurityIdSrc() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        setAuctionType( src.getAuctionType() );
        if ( Utils.hasVal( src.getPrice() ) ) setPrice( src.getPrice() );
        if ( Constants.UNSET_INT != src.getQty() ) setQty( src.getQty() );
        setPriceCollarTol( src.getPriceCollarTol() );
        setIncPrimary( src.getIncPrimary() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
