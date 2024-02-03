/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShutdownManager {

    public static final String SHUTDOWN_THREAD_NAME = "ShutdownManager";
    private static final ShutdownManager _instance = new ShutdownManager();

    public interface Callback {

        void shuttingDown();
    }

    /**
     * priority .... higher priority will have higher ordinal, highest priority called first
     */
    public enum Priority {Low, Medium, High}
    final Map<Priority, List<CallbackEntry>> _callbackMap = new ConcurrentHashMap<>( Priority.values().length );
    Callback _loggerCallback = null;
    private AtomicBoolean _isShuttingDown = new AtomicBoolean( false );

    public static ShutdownManager instance() {
        return _instance;
    }

    private ShutdownManager() {

        for ( Priority p : Priority.values() ) {
            _callbackMap.put( p, Collections.synchronizedList( new LinkedList<>() ) );
        }

        Runtime.getRuntime().addShutdownHook( new Thread( SHUTDOWN_THREAD_NAME ) {

            @Override
            public void run() {
                final Thread t = Thread.currentThread();

                _isShuttingDown.set( true );

                System.out.println( "ShutdownManager RUNNING SHUTDOWN HOOKS, thread=" + t.getName() + ", isDaemon=" + isDaemon() );

                Priority[] vals = Priority.values();

                for ( int i = vals.length - 1; i >= 0; --i ) {
                    Priority p = vals[ i ];

                    List<CallbackEntry> copy = new LinkedList<>( _callbackMap.get( p ) );

                    int idx  = 0;
                    int size = copy.size();

                    for ( CallbackEntry c : copy ) {
                        System.out.println( "ShutdownManager shutdown [" + (++idx) + "/" + size + "] id=" + c._id );
                        System.out.flush();

                        c._callback.shuttingDown();
                    }

                    if ( _loggerCallback != null ) {
                        _loggerCallback.shuttingDown();
                    }

                }

                System.out.println( "ShutdownManager ALL SHUTDOWN HOOKS COMPLETED" );
            }
        } );
    }

    public synchronized void deregister( Callback callback ) {

        for ( List<CallbackEntry> l : _callbackMap.values() ) {
            ListIterator<CallbackEntry> it = l.listIterator();

            while( it.hasNext() ) {
                CallbackEntry c = it.next();

                if ( c._callback == callback ) {
                    it.remove();

                    return;
                }
            }
        }
    }

    public boolean isShuttingDown() { return _isShuttingDown.get(); }

    public synchronized void register( String id, Callback callback, Priority p ) {
        _callbackMap.get( p ).add( new CallbackEntry( id, callback ) );
    }

    public synchronized void registerLogger( Callback callback ) {
        _loggerCallback = callback;
    }

    public void shutdown( int code ) {
        Utils.exit( code );
    }

    private class CallbackEntry {

        String   _id;
        Callback _callback;

        public CallbackEntry( final String id, final Callback callback ) {
            _id       = id;
            _callback = callback;
        }
    }
}
