/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTControllableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.RecoverableSession;
import com.rr.core.thread.RunState;
import com.rr.om.session.SessionManager;

/**
 * coordinates the startup / running of the algos
 */
public class RecoveryRunner implements SMTControllableComponent {

    private static final Logger _log = LoggerFactory.create( RecoveryRunner.class );
    private final     String   _id;
    private DMARecoveryController _reconciler;
    private SessionManager        _sessionManager;
    private transient RunState _runState = RunState.Unknown;

    public RecoveryRunner( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        RecoverableSession[] downStream = _sessionManager.getDownStreamSessions();
        RecoverableSession[] upStream   = _sessionManager.getUpStreamSessions();

        _log.info( "Starting reconciliation in prepare phase (before GC)" );

        _reconciler.start();

        for ( RecoverableSession upSess : upStream ) {
            upSess.recover( _reconciler );
        }

        for ( RecoverableSession downSess : downStream ) {
            downSess.recover( _reconciler );
        }

        for ( RecoverableSession upSess : upStream ) {
            upSess.waitForRecoveryToComplete();
        }

        for ( RecoverableSession downSess : downStream ) {
            downSess.waitForRecoveryToComplete();
        }

        _reconciler.reconcile();
        _reconciler.commit();

        _log.info( "Ending reconciliation" );
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void startWork() {
        // nothing
    }

    @Override
    public void stopWork() {
        // nothing
    }
}
