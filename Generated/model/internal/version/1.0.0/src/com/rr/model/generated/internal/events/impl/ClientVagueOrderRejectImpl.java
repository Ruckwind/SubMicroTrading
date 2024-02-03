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

public final class ClientVagueOrderRejectImpl implements CommonExchangeHeader, ClientVagueOrderRejectUpdate, Reusable<ClientVagueOrderRejectImpl> {

   // Attrs

    private transient          ClientVagueOrderRejectImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _orderId = new ReusableString( SizeType.ORDERID_LENGTH.getSize() );
    private final ReusableString _clOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString _text = new ReusableString( SizeType.TEXT_LENGTH.getSize() );
    private boolean _isTerminal = false;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private OrderRequest  _srcEvent;
    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getOrderId() { return _orderId; }

    @Override public final void setOrderId( byte[] buf, int offset, int len ) { _orderId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrderIdForUpdate() { return _orderId; }

    @Override public final ViewString getClOrdId() { return _clOrdId; }

    @Override public final void setClOrdId( byte[] buf, int offset, int len ) { _clOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getClOrdIdForUpdate() { return _clOrdId; }

    @Override public final ViewString getText() { return _text; }

    @Override public final void setText( byte[] buf, int offset, int len ) { _text.setValue( buf, offset, len ); }
    @Override public final ReusableString getTextForUpdate() { return _text; }

    @Override public final boolean getIsTerminal() { return _isTerminal; }
    @Override public final void setIsTerminal( boolean val ) { _isTerminal = val; }

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
        _orderId.reset();
        _clOrdId.reset();
        _text.reset();
        _isTerminal = false;
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
        return ModelReusableTypes.ClientVagueOrderReject;
    }

    @Override
    public final ClientVagueOrderRejectImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ClientVagueOrderRejectImpl nxt ) {
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
        out.append( "ClientVagueOrderRejectImpl" ).append( ' ' );
        if ( getOrderId().length() > 0 )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getClOrdId().length() > 0 )             out.append( ", clOrdId=" ).append( getClOrdId() );
        if ( getText().length() > 0 )             out.append( ", text=" ).append( getText() );
        out.append( ", isTerminal=" ).append( getIsTerminal() );
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
