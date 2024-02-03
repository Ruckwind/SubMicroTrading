package com.rr.core.utils;

import com.rr.core.lang.Env;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clock c = ClockFactory.get()
 * <p>
 * Note the Clock could be a threadsafe shared clock ... eg the StandardClock  or
 * the BTClock which has a single instance per Thread
 */
public class ThreadUtilsFactory {

    private static final String DEFAULT_THREAD_STANDARD_UTILS_CLASS = "com.rr.core.utils.StandardThreadUtils";

    private static ThreadUtils _threadUtilsInstance = createThreadUtils();
    private static ThreadUtils _liveThreadUtils     = new StandardThreadUtils();

    private static class NamedThreadFactory implements ThreadFactory {

        private static final AtomicInteger _poolNumber   = new AtomicInteger( 1 );
        private final        ThreadGroup   _group;
        private final        AtomicInteger _threadNumber = new AtomicInteger( 1 );
        private final        String        _namePrefix;
        private final        boolean       _useDaemonThreads;

        NamedThreadFactory( String id, boolean useDaemonThreads ) {
            SecurityManager s = System.getSecurityManager();
            _useDaemonThreads = useDaemonThreads;
            _group            = (s != null) ? s.getThreadGroup() :
                                Thread.currentThread().getThreadGroup();
            _namePrefix       = "pool-" + id + "-" +
                                _poolNumber.getAndIncrement() +
                                "-thread-";
        }

        public Thread newThread( Runnable r ) {
            Thread t = new Thread( _group, r, _namePrefix + _threadNumber.getAndIncrement(), 0 );
            t.setDaemon( _useDaemonThreads );
            if ( t.getPriority() != Thread.NORM_PRIORITY )
                t.setPriority( Thread.NORM_PRIORITY );
            return t;
        }
    }

    public static ThreadFactory namedThreadFactory( String name, boolean useDaemonThreads ) {
        return new NamedThreadFactory( name, useDaemonThreads );
    }

    private static ThreadUtils createThreadUtils() {
        if ( Env.isBacktest() ) {
            return new BackTestThreadUtils();
        }

        String dClass           = DEFAULT_THREAD_STANDARD_UTILS_CLASS;
        String threadUtilsClass = AppProps.instance().getProperty( CoreProps.SCHEDULER_CLASS, false, dClass );

        Class<ThreadUtils> cl = ReflectUtils.getClass( threadUtilsClass );

        try {
            return cl.newInstance();
        } catch( Exception e ) {
            throw new RuntimeException( "Unable to instantiate scheduler " + cl.getName() + " : " + e.getMessage(), e );
        }
    }

    public static ThreadUtils get()     { return _threadUtilsInstance; }

    public static ThreadUtils getLive() { return _liveThreadUtils; }

    public static void reset() {
        _threadUtilsInstance = createThreadUtils();
    }
}
