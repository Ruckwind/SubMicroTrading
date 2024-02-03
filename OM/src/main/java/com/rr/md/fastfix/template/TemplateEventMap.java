/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.template;

import com.rr.core.model.Event;
import com.rr.model.generated.internal.events.interfaces.MDIncRefresh;
import com.rr.model.generated.internal.events.interfaces.MDSnapshotFullRefresh;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinition;
import com.rr.model.generated.internal.events.interfaces.SecurityStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Due to the nature of transfer encoding we cant generate CODEC's
 *
 * @author Richard Rose
 */
public class TemplateEventMap {

    private final Map<String, Class<? extends Event>> _msgTypeToEventClass = new HashMap<>();

    public TemplateEventMap() {
        _msgTypeToEventClass.put( "X", MDIncRefresh.class );
        _msgTypeToEventClass.put( "d", SecurityDefinition.class );
        _msgTypeToEventClass.put( "f", SecurityStatus.class );
        _msgTypeToEventClass.put( "W", MDSnapshotFullRefresh.class );
    }

    public Class<? extends Event> getFastFixEvent( final String msgType ) {
        return _msgTypeToEventClass.get( msgType );
    }
}
