package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StrategyRunImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.StratInstrumentImpl;
import com.rr.core.pool.SuperpoolManager;

public class StrategyRunRecycler implements Recycler<StrategyRunImpl> {

    private SuperPool<StrategyRunImpl> _superPool;


    private StratInstrumentRecycler _instrumentsRecycler = SuperpoolManager.instance().getRecycler( StratInstrumentRecycler.class, StratInstrumentImpl.class );

    private StrategyRunImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public StrategyRunRecycler( int recycleSize, SuperPool<StrategyRunImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = StrategyRunImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for StrategyRunImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( StrategyRunImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            StratInstrumentImpl instruments = (StratInstrumentImpl) obj.getInstruments();
            while ( instruments != null ) {
                StratInstrumentImpl t = instruments;
                instruments = instruments.getNext();
                t.setNext( null );
                _instrumentsRecycler.recycle( t );
            }

            obj.reset();
            obj.setNext( _root.getNext() );
            _root.setNext( obj );
            if ( ++_count == _recycleSize ) {
                _superPool.returnChain( _root.getNext() );
                _root.setNext( null );
                _count = 0;
            }
        }
    }
}
