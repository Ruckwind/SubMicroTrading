/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dummy.warmup;

import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;

public class DummyEventHandler implements EventHandler {

    @Override
    public String getComponentId() {
        return null;
    }

    @Override
    public void handle( Event msg ) {
        // do nothing
    }

    @Override
    public void handleNow( Event msg ) {
        // do nothing
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void threadedInit() {
        // do nothing
    }
}
