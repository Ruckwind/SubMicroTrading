/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.pool;

import com.rr.core.lang.Reusable;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.ShutdownManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SuperpoolManager {

    private static final Logger _log = ConsoleFactory.console( SuperpoolManager.class, Level.WARN );

    private static final SuperpoolManager _instance = new SuperpoolManager();

    private final Map<Class<?>, SuperPool<?>> _pools = new HashMap<>();

    public static SuperpoolManager instance() { return _instance; }

    private SuperpoolManager() {

        ShutdownManager.instance().register( "SuperpoolManagerStats", this::logStats, ShutdownManager.Priority.Low );

    }

    /**
     * allows a specific factory to be used with the superpool for a class (avoiding generics in pool is faster)
     *
     * @param <F>          factory class
     * @param <T>          pooled object type
     * @param factoryClass
     * @param poolClass
     * @return
     */
    public synchronized <F, T extends Reusable<T>> F getFactory( Class<F> factoryClass, Class<T> poolClass ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( poolClass );

        try {
            Constructor<F> c = factoryClass.getConstructor( sp.getClass() );

            return c.newInstance( sp );

        } catch( Exception e ) {
            throw new RuntimeException( "Failed to reflect instantiate " + factoryClass.getSimpleName() );
        }
    }

    public synchronized <T extends Reusable<T>> PoolFactory<T> getPoolFactory( Class<T> poolClass ) {
        SuperPool<T> sp = getSuperPool( poolClass );

        return sp.getPoolFactory();
    }

    /**
     * @param poolClass
     * @param <T>
     * @return a simple recycler .... DOENST recycle sub objects ... use specific Recycler class for that
     */
    public synchronized <T extends Reusable<T>> Recycler<T> getRecycler( Class<T> poolClass ) {
        SuperPool<T> sp = getSuperPool( poolClass );

        return sp.getRecycleFactory();
    }

    /**
     * allows a specific Recycler to be used with the superpool for a class (avoiding generics in pool is faster)
     *
     * @param <F>           recycler class
     * @param <T>           pooled object type
     * @param recyclerClass
     * @param poolClass
     * @return
     */
    @SuppressWarnings( "boxing" )
    public synchronized <F, T extends Reusable<T>> F getRecycler( Class<F> recyclerClass, Class<T> poolClass ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( poolClass );

        try {
            Constructor<F> c = recyclerClass.getConstructor( int.class, sp.getClass() );

            int chainSize = sp.getChainSize();

            return c.newInstance( chainSize, sp );

        } catch( Exception e ) {
            throw new RuntimeException( "Failed to reflect instantiate " + recyclerClass.getSimpleName() );
        }
    }

    @SuppressWarnings( "unchecked" )
    public synchronized <T extends Reusable<T>> SuperPool<T> getSuperPool( Class<T> poolClass ) {
        SuperPool<T> sp = (SuperPool<T>) _instance._pools.get( poolClass );

        if ( sp == null ) {
            sp = new SuperPool<>( poolClass );

            _instance._pools.put( poolClass, sp );
        }

        return sp;
    }

    public void resetPoolStats() {
        _log.info( "\n\n" );

        for ( SuperPool<?> pool : _pools.values() ) {
            pool.resetStats();
        }
    }

    protected synchronized void logStats() {
        _log.info( "\n\nLOG POOL STATS\n" );

        for ( SuperPool<?> pool : _pools.values() ) {
            pool.logStats();
        }
    }
}
