/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;

public final class DualElementControlThread extends AbstractControlThread {

    private final Logger _log = LoggerFactory.create( DualElementControlThread.class );

    private ExecutableElement _workerA = null;
    private ExecutableElement _workerB = null;

    public DualElementControlThread( String name, ThreadPriority priority ) {

        super( name, priority );
    }

    @Override
    public void register( ExecutableElement ex ) {

        _log.info( "DualElementControlThread : registered " + ex.getComponentId() + " with " + getName() );

        if ( _workerA == null ) {
            _workerA = ex;
        } else if ( _workerB == null ) {
            _workerB = ex;
        } else {
            if ( _workerA != ex && _workerB != ex ) {
                throw new SMTRuntimeException( "DualElementControlThread cannot register 3 execElems, failed with " + ex.getComponentId() );
            }
        }
    }

    @Override
    protected void runControlLoop() {

        _log.info( "DualElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " starting" );

        if ( _workerA == null || _workerB == null ) {
            abortMissingWorker();
        }

        ExecutableElement cur = _workerB;

        try {
            _workerA.threadedInit();
            _workerB.threadedInit();
        } catch( Exception e ) {
            abortThreadedInit( e );
        }

        boolean readyA;
        boolean readyB;

        while( !isStopping() ) {
            readyA = _workerA.checkReady();
            readyB = _workerB.checkReady();

            if ( readyA || readyB ) {
                try {
                    while( !isStopping() ) {
                        if ( cur == _workerB ) { // catch case where workerA previously threw exception ie its B's turn next 
                            cur = _workerA;
                            _workerA.execute();
                        }
                        cur = _workerB;
                        _workerB.execute();
                    }
                } catch( Exception e ) {
                    cur.handleExecutionException( e );
                }
            } else {
                goPassive();
            }
        }

        _workerA.stop();
        _workerB.stop();

        _log.info( "DualElementControlThread : runControlLoop " + getComponentId() + " / " + getName() + " end" );
    }
}
