package com.rr.core.tasks;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

public class BasicSchedulerCallback implements Scheduler.Callback {

    private final ZString                          _id;
    private final EventTaskHandler<ScheduledEvent> _eventHandler;

    public BasicSchedulerCallback( final String id, final EventTaskHandler<ScheduledEvent> eventHandler ) {
        _id           = new ViewString( id );
        _eventHandler = eventHandler;
    }

    @Override public void event( final ScheduledEvent event ) {
        _eventHandler.notify( event );
    }

    @Override public ZString getName() {
        return _id;
    }
}
