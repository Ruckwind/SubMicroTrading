/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;

public final class SingleElementControlThread extends AbstractControlThread {

    private final Logger _log = LoggerFactory.create( SingleElementControlThread.class );

    private ExecutableElement _worker;

    public SingleElementControlThread( String name ) {

        super( name, ThreadPriority.Other );
    }

    public SingleElementControlThread( String name, ThreadPriority priority ) {

        super( name, priority );
    }

    @Override
    public void register( ExecutableElement ex ) {
        _log.info( "SingleElementControlThread : registered " + ex.getComponentId() + " with " + getName() );

        if ( _worker != null && _worker != ex ) {
            throw new SMTRuntimeException( getComponentId() + " already registered with " + _worker.getComponentId() + " cannot register " + ex.getComponentId() );
        }

        _worker = ex;
    }

    @Override
    protected void runControlLoop() {
        if ( _worker == null ) {
            abortMissingWorker();
        }

        try {
            _worker.threadedInit();
        } catch( Exception e ) {
            abortThreadedInit( e );
        }

        while( !isStopping() ) {

            if ( _worker.checkReady() ) {
                try {

                    while( !isStopping() ) {
                        _worker.execute();
                    }

                } catch( AllDisconnectedException e ) { // worker is complete

                    setStopping( true );

                } catch( Exception e ) {

                    _worker.handleExecutionException( e );
                }
            } else {
                _worker.notReady();

                goPassive();
            }
        }

        _worker.stop();
    }
}
