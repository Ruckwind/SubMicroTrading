/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.newmain;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.component.SMTWarmableComponent;
import com.rr.core.model.book.WarmupSafeBookReserver;
import com.rr.core.thread.RunState;
import com.rr.core.warmup.WarmupRegistry;
import com.rr.om.warmup.units.*;

public class WarmupControl implements SMTInitialisableComponent, SMTWarmableComponent {

    private final     String         _id;
    private           int            _warmupCount        = 1000;
    private           WarmupRegistry _warmUpRegistry     = new WarmupRegistry();
    @SuppressWarnings( "unused" )
    private           int            _warmUpPortOffset   = 0;
    @SuppressWarnings( "unused" )
    private           boolean        _enableSendSpinLock = false;
    private transient RunState       _runState           = RunState.Unknown;

    public WarmupControl( String id ) {
        super();
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        //
    }

    @Override
    public void prepare() {
        _warmUpRegistry.register( new WarmupCodecs( _warmupCount ) );
        _warmUpRegistry.register( new WarmupRecycling( _warmupCount ) );
        _warmUpRegistry.register( new WarmupLogger( _warmupCount ) );
        _warmUpRegistry.register( new WarmupRouters( _warmupCount ) );
        _warmUpRegistry.register( new WarmupJavaSpecific( _warmupCount ) );
        _warmUpRegistry.register( new WarmupSafeBookReserver( _warmupCount ) );
    }

    @Override
    public void warmup() {
        _warmUpRegistry.warmAll();
    }
}
