package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
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

public final class MarketAlertTradeMissingOrdersImpl implements Alert, MarketAlertTradeMissingOrdersWrite, Reusable<MarketAlertTradeMissingOrdersImpl> {

   // Attrs

    private transient          MarketAlertTradeMissingOrdersImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _orderId = new ReusableString( SizeType.ORDERID_LENGTH.getSize() );
    private double _lastQty = Constants.UNSET_DOUBLE;
    private double _lastPx = Constants.UNSET_DOUBLE;
    private final ReusableString _lastMkt = new ReusableString( SizeType.LASTMKT_LENGTH.getSize() );
    private final ReusableString _text = new ReusableString( SizeType.TEXT_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ExecType _execType;
    private OrdStatus _ordStatus;

    private OrderRequest  _srcEvent;
    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getOrderId() { return _orderId; }

    @Override public final void setOrderId( byte[] buf, int offset, int len ) { _orderId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrderIdForUpdate() { return _orderId; }

    @Override public final ExecType getExecType() { return _execType; }
    @Override public final void setExecType( ExecType val ) { _execType = val; }

    @Override public final OrdStatus getOrdStatus() { return _ordStatus; }
    @Override public final void setOrdStatus( OrdStatus val ) { _ordStatus = val; }

    @Override public final double getLastQty() { return _lastQty; }
    @Override public final void setLastQty( double val ) { _lastQty = val; }

    @Override public final double getLastPx() { return _lastPx; }
    @Override public final void setLastPx( double val ) { _lastPx = val; }

    @Override public final ViewString getLastMkt() { return _lastMkt; }

    @Override public final void setLastMkt( byte[] buf, int offset, int len ) { _lastMkt.setValue( buf, offset, len ); }
    @Override public final ReusableString getLastMktForUpdate() { return _lastMkt; }

    @Override public ViewString getClOrdId() { throw new IllegalFieldAccess( "Getter for clOrdId event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public ViewString getSecurityId() { throw new IllegalFieldAccess( "Getter for securityId event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public ViewString getSymbol() { throw new IllegalFieldAccess( "Getter for symbol event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public Currency getCurrency() { throw new IllegalFieldAccess( "Getter for currency event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public SecurityIDSource getSecurityIDSource() { throw new IllegalFieldAccess( "Getter for securityIDSource event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public final ViewString getText() { return _text; }

    @Override public final void setText( byte[] buf, int offset, int len ) { _text.setValue( buf, offset, len ); }
    @Override public final ReusableString getTextForUpdate() { return _text; }

    @Override public double getOrderQty() { throw new IllegalFieldAccess( "Getter for orderQty event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public double getPrice() { throw new IllegalFieldAccess( "Getter for price event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public Side getSide() { throw new IllegalFieldAccess( "Getter for side event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public ViewString getOnBehalfOfId() { throw new IllegalFieldAccess( "Getter for onBehalfOfId event AlertTradeMissingOrders is a delegate field from order request base" ); }


    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _orderId.reset();
        _execType = null;
        _ordStatus = null;
        _lastQty = Constants.UNSET_DOUBLE;
        _lastPx = Constants.UNSET_DOUBLE;
        _lastMkt.reset();
        _text.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MarketAlertTradeMissingOrders;
    }

    @Override
    public final MarketAlertTradeMissingOrdersImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MarketAlertTradeMissingOrdersImpl nxt ) {
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
        out.append( "MarketAlertTradeMissingOrdersImpl" ).append( ' ' );
        if ( getOrderId().length() > 0 )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getExecType() != null )             out.append( ", execType=" );
        if ( getExecType() != null ) out.append( getExecType().id() );
        if ( getOrdStatus() != null )             out.append( ", ordStatus=" ).append( getOrdStatus() );
        if ( Utils.hasVal( getLastQty() ) ) out.append( ", lastQty=" ).append( getLastQty() );
        if ( Utils.hasVal( getLastPx() ) ) out.append( ", lastPx=" ).append( getLastPx() );
        if ( getLastMkt().length() > 0 )             out.append( ", lastMkt=" ).append( getLastMkt() );
        if ( getText().length() > 0 )             out.append( ", text=" ).append( getText() );
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

}
