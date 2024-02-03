package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class ExpiredImpl implements CommonExecRpt, ExpiredWrite, Copyable<Expired>, MarketExpiredWrite, Reusable<ExpiredImpl> {

   // Attrs

    private transient          ExpiredImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _execId = new ReusableString( SizeType.EXECID_LENGTH.getSize() );
    private final ReusableString _clOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString  _securityId = new ReusableString( SizeType.SECURITYID_LENGTH.getSize() );
    private final ReusableString  _symbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private final ReusableString _orderId = new ReusableString( SizeType.ORDERID_LENGTH.getSize() );
    @TimestampMS private long _transactTime = Constants.UNSET_LONG;
    private double _leavesQty = Constants.UNSET_DOUBLE;
    private double _cumQty = Constants.UNSET_DOUBLE;
    private double _avgPx = Constants.UNSET_DOUBLE;
    private double _orderQty = Constants.UNSET_DOUBLE;
    private double _price = Constants.UNSET_DOUBLE;
    private final ReusableString _text = new ReusableString( SizeType.TRADE_TEXT_LENGTH.getSize() );
    private final ReusableString _parentClOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString _stratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString _origStratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString  _onBehalfOfId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private Currency _currency;
    private SecurityIDSource _securityIDSource;
    private ExecType _execType;
    private OrdStatus _ordStatus;
    private Side _side;
    private OrderCapacity _mktCapacity;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getExecId() { return _execId; }

    @Override public final void setExecId( byte[] buf, int offset, int len ) { _execId.setValue( buf, offset, len ); }
    @Override public final ReusableString getExecIdForUpdate() { return _execId; }

    @Override public final ViewString getClOrdId() { return _clOrdId; }

    @Override public final void setClOrdId( byte[] buf, int offset, int len ) { _clOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getClOrdIdForUpdate() { return _clOrdId; }

    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final ViewString getSymbol() { return _symbol; }

    @Override public final void setSymbol( byte[] buf, int offset, int len ) { _symbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getSymbolForUpdate() { return _symbol; }

    @Override public final Currency getCurrency() { return _currency; }
    @Override public final void setCurrency( Currency val ) { _currency = val; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

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

    @Override public final double getOrderQty() { return _orderQty; }
    @Override public final void setOrderQty( double val ) { _orderQty = val; }

    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final Side getSide() { return _side; }
    @Override public final void setSide( Side val ) { _side = val; }

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

    @Override public final ViewString getOnBehalfOfId() { return _onBehalfOfId; }

    @Override public final void setOnBehalfOfId( byte[] buf, int offset, int len ) { _onBehalfOfId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOnBehalfOfIdForUpdate() { return _onBehalfOfId; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _execId.reset();
        _clOrdId.reset();
        _securityId.reset();
        _symbol.reset();
        _currency = null;
        _securityIDSource = null;
        _orderId.reset();
        _execType = null;
        _ordStatus = null;
        _transactTime = Constants.UNSET_LONG;
        _leavesQty = Constants.UNSET_DOUBLE;
        _cumQty = Constants.UNSET_DOUBLE;
        _avgPx = Constants.UNSET_DOUBLE;
        _orderQty = Constants.UNSET_DOUBLE;
        _price = Constants.UNSET_DOUBLE;
        _side = null;
        _text.reset();
        _mktCapacity = null;
        _parentClOrdId.reset();
        _stratId.reset();
        _origStratId.reset();
        _onBehalfOfId.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.Expired;
    }

    @Override
    public final ExpiredImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ExpiredImpl nxt ) {
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
        out.append( "ExpiredImpl" ).append( ' ' );
        if ( getExecId().length() > 0 )             out.append( ", execId=" ).append( getExecId() );
        if ( getClOrdId().length() > 0 )             out.append( ", clOrdId=" ).append( getClOrdId() );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSymbol().length() > 0 )             out.append( ", symbol=" ).append( getSymbol() );
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
        if ( getOnBehalfOfId().length() > 0 )             out.append( ", onBehalfOfId=" ).append( getOnBehalfOfId() );
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

    @Override public final void snapTo( Expired dest ) {
        ((ExpiredImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( Expired src ) {
        getExecIdForUpdate().copy( src.getExecId() );
        getClOrdIdForUpdate().copy( src.getClOrdId() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setCurrency( src.getCurrency() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getOrderIdForUpdate().copy( src.getOrderId() );
        setExecType( src.getExecType() );
        setOrdStatus( src.getOrdStatus() );
        setTransactTime( src.getTransactTime() );
        setLeavesQty( src.getLeavesQty() );
        setCumQty( src.getCumQty() );
        setAvgPx( src.getAvgPx() );
        setOrderQty( src.getOrderQty() );
        setPrice( src.getPrice() );
        setSide( src.getSide() );
        getTextForUpdate().copy( src.getText() );
        setMktCapacity( src.getMktCapacity() );
        getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        getStratIdForUpdate().copy( src.getStratId() );
        getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( Expired src ) {
        getExecIdForUpdate().copy( src.getExecId() );
        getClOrdIdForUpdate().copy( src.getClOrdId() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setCurrency( src.getCurrency() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getOrderIdForUpdate().copy( src.getOrderId() );
        setExecType( src.getExecType() );
        setOrdStatus( src.getOrdStatus() );
        setTransactTime( src.getTransactTime() );
        setLeavesQty( src.getLeavesQty() );
        setCumQty( src.getCumQty() );
        setAvgPx( src.getAvgPx() );
        setOrderQty( src.getOrderQty() );
        setPrice( src.getPrice() );
        setSide( src.getSide() );
        getTextForUpdate().copy( src.getText() );
        setMktCapacity( src.getMktCapacity() );
        getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        getStratIdForUpdate().copy( src.getStratId() );
        getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( Expired src ) {
        if ( src.getExecId().length() > 0 ) getExecIdForUpdate().copy( src.getExecId() );
        if ( src.getClOrdId().length() > 0 ) getClOrdIdForUpdate().copy( src.getClOrdId() );
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( src.getSymbol().length() > 0 ) getSymbolForUpdate().copy( src.getSymbol() );
        if ( getCurrency() != null )  setCurrency( src.getCurrency() );
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( src.getOrderId().length() > 0 ) getOrderIdForUpdate().copy( src.getOrderId() );
        if ( getExecType() != null )  setExecType( src.getExecType() );
        setOrdStatus( src.getOrdStatus() );
        if ( Constants.UNSET_LONG != src.getTransactTime() ) setTransactTime( src.getTransactTime() );
        if ( Utils.hasVal( src.getLeavesQty() ) ) setLeavesQty( src.getLeavesQty() );
        if ( Utils.hasVal( src.getCumQty() ) ) setCumQty( src.getCumQty() );
        if ( Utils.hasVal( src.getAvgPx() ) ) setAvgPx( src.getAvgPx() );
        if ( Utils.hasVal( src.getOrderQty() ) ) setOrderQty( src.getOrderQty() );
        if ( Utils.hasVal( src.getPrice() ) ) setPrice( src.getPrice() );
        setSide( src.getSide() );
        if ( src.getText().length() > 0 ) getTextForUpdate().copy( src.getText() );
        setMktCapacity( src.getMktCapacity() );
        if ( src.getParentClOrdId().length() > 0 ) getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        if ( src.getStratId().length() > 0 ) getStratIdForUpdate().copy( src.getStratId() );
        if ( src.getOrigStratId().length() > 0 ) getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        if ( src.getOnBehalfOfId().length() > 0 ) getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
