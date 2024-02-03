package com.rr.core.utils.file;

import com.rr.core.lang.Env;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ShutdownManager;
import com.rr.core.utils.ThreadUtilsFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundZipper {

    private static final Logger _log     = LoggerFactory.create( BackgroundZipper.class );
    private static final Logger _console = ConsoleFactory.console( BackgroundZipper.class );

    private static BackgroundZipper _instance = new BackgroundZipper();

    private ExecutorService _worker;

    private boolean _canZip;

    public static BackgroundZipper instance() { return _instance; }

    private BackgroundZipper() {
        _canZip = !AppProps.instance().getBooleanProperty( "DISABLE_BACKGROUND_ZIPPER", false, Env.isBacktest() );

        if ( _canZip ) {
            _console.info( "BackgroundZipper enabled, creating singleton executor" );

            _worker = Executors.newSingleThreadExecutor( ThreadUtilsFactory.namedThreadFactory( "BackgroundZipper", false ) );

            ShutdownManager.instance().register( "BackgroundZipper", () -> close(), ShutdownManager.Priority.High );
        } else {
            _console.info( "BackgroundZipper disabled" );
        }
    }

    public void zip( String fileName ) {
        if ( _canZip ) {
            _console.info( "BackgroundZipper submitting zip request for " + fileName );
            _worker.submit( () -> doZIP( fileName ) );
        } else {
            _console.info( "BackgroundZipper cannot zip request DENIED for " + fileName );
        }
    }

    private void close() {
        try {
            if ( Env.isBacktest() ) {

                _worker.shutdownNow();

            } else {
                _worker.shutdown();
                _worker.submit( () -> _console.info( "wake up BackgroundZipper to close it" ) );
            }
        } catch( Exception e ) {
            _console.info( "close exception " + e.getMessage() );
        }
    }

    private void doZIP( final String fileName ) {
        if ( !ShutdownManager.instance().isShuttingDown() && !_worker.isShutdown() ) {
            FileUtils.gzip( fileName );
        }
    }
}
