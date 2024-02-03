package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SecurityTradingStatus;
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

public final class SecurityStatusImpl implements BaseMDResponse, SecurityStatusWrite, Copyable<SecurityStatus>, Reusable<SecurityStatusImpl> {

   // Attrs

    private transient          SecurityStatusImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _securityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _TradeDate = Constants.UNSET_INT;
    private double _highPx = Constants.UNSET_DOUBLE;
    private double _lowPx = Constants.UNSET_DOUBLE;
    private int _haltReason = Constants.UNSET_INT;
    private int _SecurityTradingEvent = Constants.UNSET_INT;
    private final ReusableString _symbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecurityIDSource _securityIDSource = SecurityIDSource.ExchangeSymbol;
    private SecurityTradingStatus _securityTradingStatus;
    private ExchangeCode _securityExchange;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final ViewString getSecurityID() { return _securityID; }

    @Override public final void setSecurityID( byte[] buf, int offset, int len ) { _securityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIDForUpdate() { return _securityID; }

    @Override public final int getTradeDate() { return _TradeDate; }
    @Override public final void setTradeDate( int val ) { _TradeDate = val; }

    @Override public final double getHighPx() { return _highPx; }
    @Override public final void setHighPx( double val ) { _highPx = val; }

    @Override public final double getLowPx() { return _lowPx; }
    @Override public final void setLowPx( double val ) { _lowPx = val; }

    @Override public final SecurityTradingStatus getSecurityTradingStatus() { return _securityTradingStatus; }
    @Override public final void setSecurityTradingStatus( SecurityTradingStatus val ) { _securityTradingStatus = val; }

    @Override public final int getHaltReason() { return _haltReason; }
    @Override public final void setHaltReason( int val ) { _haltReason = val; }

    @Override public final int getSecurityTradingEvent() { return _SecurityTradingEvent; }
    @Override public final void setSecurityTradingEvent( int val ) { _SecurityTradingEvent = val; }

    @Override public final ViewString getSymbol() { return _symbol; }

    @Override public final void setSymbol( byte[] buf, int offset, int len ) { _symbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getSymbolForUpdate() { return _symbol; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _securityIDSource = SecurityIDSource.ExchangeSymbol;
        _securityID.reset();
        _TradeDate = Constants.UNSET_INT;
        _highPx = Constants.UNSET_DOUBLE;
        _lowPx = Constants.UNSET_DOUBLE;
        _securityTradingStatus = null;
        _haltReason = Constants.UNSET_INT;
        _SecurityTradingEvent = Constants.UNSET_INT;
        _symbol.reset();
        _securityExchange = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecurityStatus;
    }

    @Override
    public final SecurityStatusImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecurityStatusImpl nxt ) {
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
        out.append( "SecurityStatusImpl" ).append( ' ' );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getSecurityID().length() > 0 )             out.append( ", securityID=" ).append( getSecurityID() );
        if ( Constants.UNSET_INT != getTradeDate() && 0 != getTradeDate() )             out.append( ", TradeDate=" ).append( getTradeDate() );
        if ( Utils.hasVal( getHighPx() ) ) out.append( ", highPx=" ).append( getHighPx() );
        if ( Utils.hasVal( getLowPx() ) ) out.append( ", lowPx=" ).append( getLowPx() );
        if ( getSecurityTradingStatus() != null )             out.append( ", securityTradingStatus=" ).append( getSecurityTradingStatus() );
        if ( Constants.UNSET_INT != getHaltReason() && 0 != getHaltReason() )             out.append( ", haltReason=" ).append( getHaltReason() );
        if ( Constants.UNSET_INT != getSecurityTradingEvent() && 0 != getSecurityTradingEvent() )             out.append( ", SecurityTradingEvent=" ).append( getSecurityTradingEvent() );
        if ( getSymbol().length() > 0 )             out.append( ", symbol=" ).append( getSymbol() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
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

    @Override public final void snapTo( SecurityStatus dest ) {
        ((SecurityStatusImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecurityStatus src ) {
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setTradeDate( src.getTradeDate() );
        setHighPx( src.getHighPx() );
        setLowPx( src.getLowPx() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setHaltReason( src.getHaltReason() );
        setSecurityTradingEvent( src.getSecurityTradingEvent() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setSecurityExchange( src.getSecurityExchange() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecurityStatus src ) {
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setTradeDate( src.getTradeDate() );
        setHighPx( src.getHighPx() );
        setLowPx( src.getLowPx() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setHaltReason( src.getHaltReason() );
        setSecurityTradingEvent( src.getSecurityTradingEvent() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setSecurityExchange( src.getSecurityExchange() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecurityStatus src ) {
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( src.getSecurityID().length() > 0 ) getSecurityIDForUpdate().copy( src.getSecurityID() );
        if ( Constants.UNSET_INT != src.getTradeDate() ) setTradeDate( src.getTradeDate() );
        if ( Utils.hasVal( src.getHighPx() ) ) setHighPx( src.getHighPx() );
        if ( Utils.hasVal( src.getLowPx() ) ) setLowPx( src.getLowPx() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        if ( Constants.UNSET_INT != src.getHaltReason() ) setHaltReason( src.getHaltReason() );
        if ( Constants.UNSET_INT != src.getSecurityTradingEvent() ) setSecurityTradingEvent( src.getSecurityTradingEvent() );
        if ( src.getSymbol().length() > 0 ) getSymbolForUpdate().copy( src.getSymbol() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
