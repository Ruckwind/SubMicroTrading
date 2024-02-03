/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.recycler.EventRecycler;

public class TradingSessionConfig extends SessionConfig {

    private boolean _isCancelOnDisconnect;
    private boolean _isGapFillAlllowed;

    public TradingSessionConfig() {
        super();
    }

    public TradingSessionConfig( String id ) {
        super( id );
    }

    public TradingSessionConfig( Class<? extends EventRecycler> recycler ) {
        super( recycler );
    }

    public boolean isCancelOnDisconnect() {
        return _isCancelOnDisconnect;
    }

    public void setCancelOnDisconnect( boolean isCancelOnDisconnect ) {
        _isCancelOnDisconnect = isCancelOnDisconnect;
    }

    public boolean isGapFillAllowed() {
        return _isGapFillAlllowed;
    }

    public void setGapFillAllowed( boolean canGapFill ) {
        _isGapFillAlllowed = canGapFill;
    }
}
