/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

public final class EventProcConfigImpl implements EventProcConfig {

    public static final EventProcConfigImpl DEFAULT = new EventProcConfigImpl( false );

    private final boolean _forceCancelUnknownExexId;

    public EventProcConfigImpl( boolean forceCancelUnknownExexId ) {
        super();
        _forceCancelUnknownExexId = forceCancelUnknownExexId;
    }

    @Override
    public boolean isForceCancelUnknownExexId() {
        return _forceCancelUnknownExexId;
    }
}
