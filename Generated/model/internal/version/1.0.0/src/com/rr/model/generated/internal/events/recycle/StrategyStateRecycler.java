package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.StrategyStateImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.StratInstrumentStateImpl;
import com.rr.core.pool.SuperpoolManager;

public class StrategyStateRecycler implements Recycler<StrategyStateImpl> {

    private SuperPool<StrategyStateImpl> _superPool;


    private StratInstrumentStateRecycler _instStateRecycler = SuperpoolManager.instance().getRecycler( StratInstrumentStateRecycler.class, StratInstrumentStateImpl.class );

    private StrategyStateImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public StrategyStateRecycler( int recycleSize, SuperPool<StrategyStateImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = StrategyStateImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for StrategyStateImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( StrategyStateImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            StratInstrumentStateImpl instState = (StratInstrumentStateImpl) obj.getInstState();
            while ( instState != null ) {
                StratInstrumentStateImpl t = instState;
                instState = instState.getNext();
                t.setNext( null );
                _instStateRecycler.recycle( t );
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
