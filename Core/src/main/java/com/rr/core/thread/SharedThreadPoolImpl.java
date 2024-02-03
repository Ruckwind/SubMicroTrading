package com.rr.core.thread;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.ShutdownManager;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;

import java.util.concurrent.*;

import static com.rr.core.utils.Utils.getMaxCores;

public class SharedThreadPoolImpl implements SMTThreadPool, SMTComponent {

    private static final ErrorCode ERR_TASK        = new ErrorCode( "STP100", "ExecutionException in ExecutorService" );
    private static final long      LOG_DURATION_MS = 5 * 1000;

    private static final Logger        _log      = LoggerFactory.create( SharedThreadPoolImpl.class );
    private static       SMTThreadPool _instance = new SharedThreadPoolImpl( "SharedThreadPool" );
    private RunState        _runState = RunState.Unknown;
    private String          _id;
    private ExecutorService _pool;
    private int             _workers;

    public static SMTThreadPool instance() { return _instance; }

    private static int calcDefaultThreads() {
        double processors = getMaxCores();

        int poolThreadRatio = AppProps.instance().getIntProperty( CoreProps.SHARED_THREAD_POOL_CORE_RATIO, false, 50 );

        int threads = (int) (processors * poolThreadRatio / 100.0) + 1;

        if ( threads >= processors ) threads = (int) processors - 1;

        return threads;
    }

    public SharedThreadPoolImpl( final String id ) {
        this( id, calcDefaultThreads() );
    }

    public SharedThreadPoolImpl( String id, int numThreads ) {
        int processors = Utils.getMaxCores();

        if ( numThreads > processors ) numThreads = (processors * 7) / 8;

        if ( numThreads <= 0 ) numThreads = 1;

        _workers = numThreads;
        _pool    = Executors.newFixedThreadPool( numThreads, ThreadUtilsFactory.namedThreadFactory( id, true ) );
        _id      = id;

        _log.info( "SharedThreadPoolImpl " + id + " threadPoolSize=" + numThreads );

        ShutdownManager.instance().register( "Stop" + getComponentId(), () -> stopWork(), ShutdownManager.Priority.Medium );
    }

    @Override public <T> Future<T> execute( Callable<T> func ) {
        return _pool.submit( func );
    }

    @Override public int size() {
        return _workers;
    }

    @Override public <T> T waitForResult( Future<T> task ) {
        long start = System.currentTimeMillis();

        boolean logged = false;

        while( true ) {
            try {
                T res = task.get( LOG_DURATION_MS, TimeUnit.MILLISECONDS );

                long end        = System.currentTimeMillis();
                long durationMS = (end - start);

                if ( durationMS > LOG_DURATION_MS ) {
                    _log.info( _id + " pool task took " + durationMS / 1000 + " secs" );
                }

                return res;
            } catch( TimeoutException e ) {

                if ( !logged ) {
                    long end        = System.currentTimeMillis();
                    long durationMS = (end - start);

                    _log.info( _id + " pool task still running after " + durationMS / 1000 + " secs" );

                    logged = true;
                }

            } catch( InterruptedException e ) {

                if ( !logged ) {
                    long end        = System.currentTimeMillis();
                    long durationMS = (end - start);

                    _log.info( _id + " pool task still running after " + durationMS / 1000 + " secs" );

                    logged = true;
                }

            } catch( ExecutionException e ) {
                _log.error( ERR_TASK, " " + _id + " : " + e.getMessage(), e );
                return null;
            }
        }
    }

    @Override public String getComponentId() {
        return _id;
    }

    public void stopWork() {
        _pool.shutdownNow();
    }
}
