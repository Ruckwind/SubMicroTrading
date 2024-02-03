package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MassInstrumentStateChangeImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.SecMassStatGrpImpl;
import com.rr.core.pool.SuperpoolManager;

public class MassInstrumentStateChangeRecycler implements Recycler<MassInstrumentStateChangeImpl> {

    private SuperPool<MassInstrumentStateChangeImpl> _superPool;


    private SecMassStatGrpRecycler _instStateRecycler = SuperpoolManager.instance().getRecycler( SecMassStatGrpRecycler.class, SecMassStatGrpImpl.class );

    private MassInstrumentStateChangeImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MassInstrumentStateChangeRecycler( int recycleSize, SuperPool<MassInstrumentStateChangeImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MassInstrumentStateChangeImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MassInstrumentStateChangeImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MassInstrumentStateChangeImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            SecMassStatGrpImpl instState = (SecMassStatGrpImpl) obj.getInstState();
            while ( instState != null ) {
                SecMassStatGrpImpl t = instState;
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
