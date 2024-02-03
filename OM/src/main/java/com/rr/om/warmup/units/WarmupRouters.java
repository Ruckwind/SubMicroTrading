/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.units;

import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.warmup.JITWarmup;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.router.RoundRobinRouter;

public class WarmupRouters implements JITWarmup {

    private int _warmupCount;

    public WarmupRouters( int warmupCount ) {
        _warmupCount = warmupCount;
    }

    @Override
    public String getName() {
        return "Routers";
    }

    @Override
    public void warmup() {
        warmRoundRobinRouter();
    }

    @SuppressWarnings( "unused" )
    private void warmRoundRobinRouter() {
        EventHandler m = new EventHandler() {

            public int _last;

            @Override public boolean canHandle() { return true; }

            @Override public void handle( Event msg ) { /* nothing */ }

            @Override public void handleNow( Event msg ) {
                _last = msg.getMsgSeqNum();
            }

            @Override public String getComponentId() { return null; }

            @Override public void threadedInit() { /* nothing */ }
        };
        EventHandler[]           handlers = { m };
        RoundRobinRouter         r        = new RoundRobinRouter( handlers );
        ClientNewOrderSingleImpl t        = new ClientNewOrderSingleImpl();
        EventHandler             routed;
        for ( int i = 0; i < _warmupCount; ++i ) {
            routed = r.getRoute( t, null );
            if ( routed == null ) System.out.println( "unexpected null route" );
        }
    }
}
