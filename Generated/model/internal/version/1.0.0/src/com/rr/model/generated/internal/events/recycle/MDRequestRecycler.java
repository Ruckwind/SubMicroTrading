package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.MDRequestImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.SymbolRepeatingGrpImpl;
import com.rr.core.pool.SuperpoolManager;

public class MDRequestRecycler implements Recycler<MDRequestImpl> {

    private SuperPool<MDRequestImpl> _superPool;


    private SymbolRepeatingGrpRecycler _symbolGrpRecycler = SuperpoolManager.instance().getRecycler( SymbolRepeatingGrpRecycler.class, SymbolRepeatingGrpImpl.class );

    private MDRequestImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public MDRequestRecycler( int recycleSize, SuperPool<MDRequestImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = MDRequestImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for MDRequestImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( MDRequestImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            SymbolRepeatingGrpImpl symbolGrp = (SymbolRepeatingGrpImpl) obj.getSymbolGrp();
            while ( symbolGrp != null ) {
                SymbolRepeatingGrpImpl t = symbolGrp;
                symbolGrp = symbolGrp.getNext();
                t.setNext( null );
                _symbolGrpRecycler.recycle( t );
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
