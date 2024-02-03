/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.hub;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTControllableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.Stopable;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.thread.RunState;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;

public class HubProcessor implements EventHandler, SMTControllableComponent, Stopable {

    protected static final Logger _log = LoggerFactory.create( HubProcessor.class );
    protected final ReusableString   _logMsg = new ReusableString();
    private final     String          _componentId;
    private final     EventDispatcher _inboundDispatcher;
    protected       AllEventRecycler _eventRecycler;
    private           boolean         _running  = false;
    private transient RunState        _runState = RunState.Unknown;

    public HubProcessor( String id, EventDispatcher inboundDispatcher ) {
        _componentId       = id;
        _inboundDispatcher = inboundDispatcher;

        _inboundDispatcher.setHandler( this );
    }

    @Override
    public String getComponentId() {
        return _componentId;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public void handle( Event msg ) {
        if ( msg != null ) {
            _inboundDispatcher.dispatch( msg );
        }
    }

    @Override
    public void handleNow( Event msg ) {
        _logMsg.copy( "HUB RECEIVED : " );

        msg.dump( _logMsg );

        _log.info( _logMsg );

        _eventRecycler.recycle( msg );
    }

    @Override
    public final boolean canHandle() {
        return _running;
    }

    @Override
    public void startWork() {
        // nothing
    }

    @Override
    public void stopWork() {
        _inboundDispatcher.setStopping();
    }

    @Override
    public final void stop() {
        stopWork();
    }

    @Override
    public void threadedInit() {
        _eventRecycler = new AllEventRecycler();
        _running       = true;
    }
}
