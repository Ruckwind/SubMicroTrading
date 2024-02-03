package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class BookAddOrderImpl implements BaseITCH, BookAddOrderWrite, Copyable<BookAddOrder>, Reusable<BookAddOrderImpl> {

   // Attrs

    private transient          BookAddOrderImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _nanosecond = Constants.UNSET_INT;
    private long _orderId = Constants.UNSET_LONG;
    private int _orderQty = Constants.UNSET_INT;
    private double _price = Constants.UNSET_DOUBLE;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private Side _side;
    private Book _book;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getNanosecond() { return _nanosecond; }
    @Override public final void setNanosecond( int val ) { _nanosecond = val; }

    @Override public final long getOrderId() { return _orderId; }
    @Override public final void setOrderId( long val ) { _orderId = val; }

    @Override public final Side getSide() { return _side; }
    @Override public final void setSide( Side val ) { _side = val; }

    @Override public final int getOrderQty() { return _orderQty; }
    @Override public final void setOrderQty( int val ) { _orderQty = val; }

    @Override public final Book getBook() { return _book; }
    @Override public final void setBook( Book val ) { _book = val; }

    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _nanosecond = Constants.UNSET_INT;
        _orderId = Constants.UNSET_LONG;
        _side = null;
        _orderQty = Constants.UNSET_INT;
        _book = null;
        _price = Constants.UNSET_DOUBLE;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.BookAddOrder;
    }

    @Override
    public final BookAddOrderImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( BookAddOrderImpl nxt ) {
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
        out.append( "BookAddOrderImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getNanosecond() && 0 != getNanosecond() )             out.append( ", nanosecond=" ).append( getNanosecond() );
        if ( Constants.UNSET_LONG != getOrderId() && 0 != getOrderId() )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getSide() != null )             out.append( ", side=" ).append( getSide() );
        if ( Constants.UNSET_INT != getOrderQty() && 0 != getOrderQty() )             out.append( ", orderQty=" ).append( getOrderQty() );
        if ( getBook() != null )             out.append( ", book=" );
        if ( getBook() != null ) out.append( getBook().id() );
        if ( Utils.hasVal( getPrice() ) ) out.append( ", price=" ).append( getPrice() );
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

    @Override public final void snapTo( BookAddOrder dest ) {
        ((BookAddOrderImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( BookAddOrder src ) {
        setNanosecond( src.getNanosecond() );
        setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        setOrderQty( src.getOrderQty() );
        setBook( src.getBook() );
        setPrice( src.getPrice() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( BookAddOrder src ) {
        setNanosecond( src.getNanosecond() );
        setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        setOrderQty( src.getOrderQty() );
        setBook( src.getBook() );
        setPrice( src.getPrice() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( BookAddOrder src ) {
        if ( Constants.UNSET_INT != src.getNanosecond() ) setNanosecond( src.getNanosecond() );
        if ( Constants.UNSET_LONG != src.getOrderId() ) setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        if ( Constants.UNSET_INT != src.getOrderQty() ) setOrderQty( src.getOrderQty() );
        if ( getBook() != null )  setBook( src.getBook() );
        if ( Utils.hasVal( src.getPrice() ) ) setPrice( src.getPrice() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
