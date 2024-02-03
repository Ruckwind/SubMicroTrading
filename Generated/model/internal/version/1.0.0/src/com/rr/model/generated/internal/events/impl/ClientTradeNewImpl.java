package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LiquidityInd;
import com.rr.model.generated.internal.type.MultiLegReportingType;
import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrderCapacity;
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

public final class ClientTradeNewImpl implements TradeBase, ClientTradeNewUpdate, Reusable<ClientTradeNewImpl> {

   // Attrs

    private transient          ClientTradeNewImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private double _lastQty = Constants.UNSET_DOUBLE;
    private double _lastPx = Constants.UNSET_DOUBLE;
    private final ReusableString _lastMkt = new ReusableString( SizeType.LASTMKT_LENGTH.getSize() );
    private final ReusableString _securityDesc = new ReusableString( SizeType.INST_SEC_DESC_LENGTH.getSize() );
    private final ReusableString _execId = new ReusableString( SizeType.EXECID_LENGTH.getSize() );
    private final ReusableString _orderId = new ReusableString( SizeType.ORDERID_LENGTH.getSize() );
    @TimestampMS private long _transactTime = Constants.UNSET_LONG;
    private double _leavesQty = Constants.UNSET_DOUBLE;
    private double _cumQty = Constants.UNSET_DOUBLE;
    private double _avgPx = Constants.UNSET_DOUBLE;
    private final ReusableString _text = new ReusableString( SizeType.TRADE_TEXT_LENGTH.getSize() );
    private final ReusableString _parentClOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString _stratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString _origStratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private LiquidityInd _liquidityInd;
    private MultiLegReportingType _multiLegReportingType;
    private ExecType _execType;
    private OrdStatus _ordStatus;
    private OrderCapacity _mktCapacity;

    private OrderRequest  _srcEvent;
    private int           _flags          = 0;

   // Getters and Setters
    @Override public final double getLastQty() { return _lastQty; }
    @Override public final void setLastQty( double val ) { _lastQty = val; }

    @Override public final double getLastPx() { return _lastPx; }
    @Override public final void setLastPx( double val ) { _lastPx = val; }

    @Override public final LiquidityInd getLiquidityInd() { return _liquidityInd; }
    @Override public final void setLiquidityInd( LiquidityInd val ) { _liquidityInd = val; }

    @Override public final MultiLegReportingType getMultiLegReportingType() { return _multiLegReportingType; }
    @Override public final void setMultiLegReportingType( MultiLegReportingType val ) { _multiLegReportingType = val; }

    @Override public final ViewString getLastMkt() { return _lastMkt; }

    @Override public final void setLastMkt( byte[] buf, int offset, int len ) { _lastMkt.setValue( buf, offset, len ); }
    @Override public final ReusableString getLastMktForUpdate() { return _lastMkt; }

    @Override public final ViewString getSecurityDesc() { return _securityDesc; }

    @Override public final void setSecurityDesc( byte[] buf, int offset, int len ) { _securityDesc.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityDescForUpdate() { return _securityDesc; }

    @Override public final ViewString getExecId() { return _execId; }

    @Override public final void setExecId( byte[] buf, int offset, int len ) { _execId.setValue( buf, offset, len ); }
    @Override public final ReusableString getExecIdForUpdate() { return _execId; }

    @Override public final ViewString getClOrdId() { return _srcEvent.getClOrdId(); }


    @Override public final ViewString getSecurityId() { return _srcEvent.getSecurityId(); }


    @Override public final ViewString getSymbol() { return _srcEvent.getSymbol(); }


    @Override public final Currency getCurrency() { return _srcEvent.getCurrency(); }

    @Override public final SecurityIDSource getSecurityIDSource() { return _srcEvent.getSecurityIDSource(); }

    @Override public final ViewString getOrderId() { return _orderId; }

    @Override public final void setOrderId( byte[] buf, int offset, int len ) { _orderId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrderIdForUpdate() { return _orderId; }

    @Override public final ExecType getExecType() { return _execType; }
    @Override public final void setExecType( ExecType val ) { _execType = val; }

    @Override public final OrdStatus getOrdStatus() { return _ordStatus; }
    @Override public final void setOrdStatus( OrdStatus val ) { _ordStatus = val; }

    @Override public final long getTransactTime() { return _transactTime; }
    @Override public final void setTransactTime( long val ) { _transactTime = val; }

    @Override public final double getLeavesQty() { return _leavesQty; }
    @Override public final void setLeavesQty( double val ) { _leavesQty = val; }

    @Override public final double getCumQty() { return _cumQty; }
    @Override public final void setCumQty( double val ) { _cumQty = val; }

    @Override public final double getAvgPx() { return _avgPx; }
    @Override public final void setAvgPx( double val ) { _avgPx = val; }

    @Override public final double getOrderQty() { return _srcEvent.getOrderQty(); }

    @Override public final double getPrice() { return _srcEvent.getPrice(); }

    @Override public final Side getSide() { return _srcEvent.getSide(); }

    @Override public final ViewString getText() { return _text; }

    @Override public final void setText( byte[] buf, int offset, int len ) { _text.setValue( buf, offset, len ); }
    @Override public final ReusableString getTextForUpdate() { return _text; }

    @Override public final OrderCapacity getMktCapacity() { return _mktCapacity; }
    @Override public final void setMktCapacity( OrderCapacity val ) { _mktCapacity = val; }

    @Override public final ViewString getParentClOrdId() { return _parentClOrdId; }

    @Override public final void setParentClOrdId( byte[] buf, int offset, int len ) { _parentClOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getParentClOrdIdForUpdate() { return _parentClOrdId; }

    @Override public final ViewString getStratId() { return _stratId; }

    @Override public final void setStratId( byte[] buf, int offset, int len ) { _stratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratIdForUpdate() { return _stratId; }

    @Override public final ViewString getOrigStratId() { return _origStratId; }

    @Override public final void setOrigStratId( byte[] buf, int offset, int len ) { _origStratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrigStratIdForUpdate() { return _origStratId; }

    @Override public final ViewString getOnBehalfOfId() { return _srcEvent.getOnBehalfOfId(); }


    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

    @Override public final void setSrcEvent( OrderRequest srcEvent ) { _srcEvent = srcEvent; }
    @Override public final OrderRequest getSrcEvent() { return _srcEvent; }


   // Reusable Contract

    @Override
    public final void reset() {
        _lastQty = Constants.UNSET_DOUBLE;
        _lastPx = Constants.UNSET_DOUBLE;
        _liquidityInd = null;
        _multiLegReportingType = null;
        _lastMkt.reset();
        _securityDesc.reset();
        _execId.reset();
        _orderId.reset();
        _execType = null;
        _ordStatus = null;
        _transactTime = Constants.UNSET_LONG;
        _leavesQty = Constants.UNSET_DOUBLE;
        _cumQty = Constants.UNSET_DOUBLE;
        _avgPx = Constants.UNSET_DOUBLE;
        _text.reset();
        _mktCapacity = null;
        _parentClOrdId.reset();
        _stratId.reset();
        _origStratId.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _srcEvent = null;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ClientTradeNew;
    }

    @Override
    public final ClientTradeNewImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ClientTradeNewImpl nxt ) {
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
        out.append( "ClientTradeNewImpl" ).append( ' ' );
        if ( Utils.hasVal( getLastQty() ) ) out.append( ", lastQty=" ).append( getLastQty() );
        if ( Utils.hasVal( getLastPx() ) ) out.append( ", lastPx=" ).append( getLastPx() );
        if ( getLiquidityInd() != null )             out.append( ", liquidityInd=" ).append( getLiquidityInd() );
        if ( getMultiLegReportingType() != null )             out.append( ", multiLegReportingType=" ).append( getMultiLegReportingType() );
        if ( getLastMkt().length() > 0 )             out.append( ", lastMkt=" ).append( getLastMkt() );
        if ( getSecurityDesc().length() > 0 )             out.append( ", securityDesc=" ).append( getSecurityDesc() );
        if ( getExecId().length() > 0 )             out.append( ", execId=" ).append( getExecId() );
        if ( getClOrdId().length() > 0 )             out.append( ", clOrdId=" ).append( getClOrdId() );
            out.append( ", securityId=" ).append( getSecurityId() );
            out.append( ", symbol=" ).append( getSymbol() );
        if ( getCurrency() != null )             out.append( ", currency=" );
        if ( getCurrency() != null ) out.append( getCurrency().id() );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getOrderId().length() > 0 )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getExecType() != null )             out.append( ", execType=" );
        if ( getExecType() != null ) out.append( getExecType().id() );
        if ( getOrdStatus() != null )             out.append( ", ordStatus=" ).append( getOrdStatus() );
        if ( Constants.UNSET_LONG != getTransactTime() && 0 != getTransactTime() ) {
            out.append( ", transactTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getTransactTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getTransactTime() );
            out.append( " ( " );
            out.append( getTransactTime() ).append( " ) " );
        }
        if ( Utils.hasVal( getLeavesQty() ) ) out.append( ", leavesQty=" ).append( getLeavesQty() );
        if ( Utils.hasVal( getCumQty() ) ) out.append( ", cumQty=" ).append( getCumQty() );
        if ( Utils.hasVal( getAvgPx() ) ) out.append( ", avgPx=" ).append( getAvgPx() );
        if ( Utils.hasVal( getOrderQty() ) ) out.append( ", orderQty=" ).append( getOrderQty() );
        if ( Utils.hasVal( getPrice() ) ) out.append( ", price=" ).append( getPrice() );
        if ( getSide() != null )             out.append( ", side=" ).append( getSide() );
        if ( getText().length() > 0 )             out.append( ", text=" ).append( getText() );
        if ( getMktCapacity() != null )             out.append( ", mktCapacity=" ).append( getMktCapacity() );
        if ( getParentClOrdId().length() > 0 )             out.append( ", parentClOrdId=" ).append( getParentClOrdId() );
        if ( getStratId().length() > 0 )             out.append( ", stratId=" ).append( getStratId() );
        if ( getOrigStratId().length() > 0 )             out.append( ", origStratId=" ).append( getOrigStratId() );
            out.append( ", onBehalfOfId=" ).append( getOnBehalfOfId() );
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
