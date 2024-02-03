/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.om.order.Order;
import com.rr.om.order.OrderReusableType;

public class TradeWrapperImpl implements TradeWrapper, Reusable<TradeWrapperImpl> {

    private final ReusableString _execId       = new ReusableString( SizeConstants.DEFAULT_EXECID_LENGTH );
    private final ReusableString _clientExecId = new ReusableString( SizeConstants.DEFAULT_EXECID_LENGTH );
    private       Order          _order;
    private       int            _hash;
    private       double         _qty;
    private       double         _px;
    private       TradeWrapper   _next;

    public static int hashCode( Order order, ZString execId ) {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((execId == null) ? 0 : execId.hashCode());
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        return result;
    }

    public static boolean equals( TradeWrapper twA, TradeWrapper other ) {
        if ( twA == other )
            return true;
        if ( other == null )
            return false;
        if ( twA.getExecId() == null ) {
            if ( other.getExecId() != null )
                return false;
        } else if ( !twA.getExecId().equals( other.getExecId() ) )
            return false;
        final Order ordA = twA.getOrder();
        if ( ordA == null ) {
            return other.getOrder() == null;
        } else return ordA == other.getOrder();
    }

    @Override
    public boolean equals( TradeWrapper other ) {
        return equals( this, other );
    }

    @Override
    public ZString getClientExecId() {
        return _clientExecId;
    }

    @Override
    public void setClientExecId( ZString execId ) {
        _clientExecId.setValue( execId );
    }

    @Override
    public final ZString getExecId() {
        return _execId;
    }

    @Override
    public TradeWrapper getNextWrapper() {
        return _next;
    }

    /**
     * in TradeWrapperSet next could be a cancel !
     */
    @Override
    public void setNextWrapper( TradeWrapper next ) {
        _next = next;
    }

    @Override
    public Order getOrder() {
        return _order;
    }

    @Override
    public double getPrice() {
        return _px;
    }

    @Override
    public double getQty() {
        return _qty;
    }

    @Override
    public ReusableType getReusableType() {
        return OrderReusableType.TradeWrapper;
    }

    /**
     * in superpool next will always be a tradewrapper
     */
    @Override
    public TradeWrapperImpl getNext() {
        return (TradeWrapperImpl) _next;
    }

    @Override
    public void setNext( TradeWrapperImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final int hashCode() {
        int h = _hash;
        if ( h == 0 ) {
            h     = hashCode( _order, _execId );
            _hash = h;
        }

        return h;
    }

    @Override
    public void reset() {
        _hash = 0;
        _execId.reset();
        _clientExecId.reset();
        _order = null;
        _qty   = 0;
        _px    = 0.0;
    }

    public void set( Order order, ZString execId, double qty, double px ) {
        _px  = px;
        _qty = qty;
        _execId.setValue( execId );
        _order = order;
    }
}
