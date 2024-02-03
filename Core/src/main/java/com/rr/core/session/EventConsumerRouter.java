/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.lang.ZConsumer;
import com.rr.core.model.Event;

public class EventConsumerRouter extends AbstractEventRouter {

    private final ZConsumer<Event> _consumer;

    public EventConsumerRouter( String id ) {
        super( id );
        _consumer = null; // reflectively set after constructor
    }

    public EventConsumerRouter( String id, ZConsumer<Event> consumer ) {
        super( id );
        _consumer = consumer;
    }

    @Override public boolean canHandle() {
        return true;
    }

    @Override public void handle( Event msg )    { handleNow( msg ); }

    @Override public void handleNow( Event msg ) { _consumer.accept( msg ); }

    @Override
    public void threadedInit() {
        // nothing
    }
}
