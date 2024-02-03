/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.collections.ReusableStringSet;
import com.rr.core.factories.ReusableStringFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.interfaces.TradeCancel;
import com.rr.model.generated.internal.events.interfaces.TradeCorrect;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.om.order.Order;

/**
 * a non thread safe registry for execIds
 * <p>
 * suitable for all EMEA exchanges except ENX and other UTP exchanges
 */
public final class SimpleTradeRegistry implements TradeRegistry {

    private final ReusableStringSet     _set;
    private final ReusableStringFactory _reusableStringFactory;

    public SimpleTradeRegistry( int presize ) {
        _set = new ReusableStringSet( presize );

        SuperpoolManager sp = SuperpoolManager.instance();

        _reusableStringFactory = sp.getFactory( ReusableStringFactory.class, ReusableString.class );
    }

    @Override
    public void clear() {
        _set.clear();
    }

    @Override
    public boolean contains( final Order order, final ViewString execId ) {
        return _set.contains( execId );
    }

    @Override
    public TradeWrapper get( final Order order, final ViewString execId ) {
        return null;
    }

    @Override
    public boolean hasDetails( final Order order, ViewString execRefID ) {
        return false;
    }

    @Override
    public boolean hasTradeDetails() {
        return false;
    }

    @Override
    public boolean register( final Order order, TradeCorrect msg ) {
        // create a copy of the execId
        ReusableString execIdCopy = _reusableStringFactory.get();
        execIdCopy.setValue( msg.getExecId() );

        // recycle immediately if dup
        return _set.put( execIdCopy );
    }

    @Override
    public boolean register( final Order order, final TradeNew msg ) {

        // create a copy of the execId
        ReusableString execIdCopy = _reusableStringFactory.get();
        execIdCopy.setValue( msg.getExecId() );

        // recycle immediately if dup
        return _set.put( execIdCopy );
    }

    @Override
    public boolean register( final Order order, TradeCancel msg ) {
        // create a copy of the execId
        ReusableString execIdCopy = _reusableStringFactory.get();
        execIdCopy.setValue( msg.getExecId() );

        // recycle immediately if dup
        return _set.put( execIdCopy );
    }

    @Override
    public int size() {
        return _set.size();
    }
}
