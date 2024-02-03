/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.newmain;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTCoreContext;
import com.rr.core.component.SMTStartContext;
import com.rr.core.model.TickManager;
import com.rr.core.recovery.json.IgnoreDependency;
import com.rr.core.session.Session;
import com.rr.hub.AsyncLogSession;
import com.rr.inst.InstrumentStore;
import com.rr.om.client.ClientProfileManager;
import com.rr.om.exchange.ExchangeManager;

/**
 * Context block passed by Bootstrap to initialise method
 */
public class SMTContext extends SMTCoreContext {

    private transient @IgnoreDependency ExchangeManager      _exchangeManager;
    private transient @IgnoreDependency InstrumentStore      _instrumentLocator;
    private transient @IgnoreDependency ClientProfileManager _clientProfileManager;
    private transient @IgnoreDependency WarmupControl        _warmupControl;
    private transient @IgnoreDependency TickManager          _tickManager;
    private transient @IgnoreDependency Session              _hubSession = new AsyncLogSession( "DefaultLogOnlyHubSession", true );

    public SMTContext( String id ) {
        this( id, null );
    }

    public SMTContext( String id, SMTComponentManager mgr ) {
        super( id, mgr );
    }

    public SMTContext( String id, SMTContext srcCtx, SMTComponentManager mgr ) {
        super( id, srcCtx, mgr );
        setInstrumentLocator( srcCtx.getInstrumentLocator() );
        setClientProfileManager( srcCtx.getClientProfileManager() );
        setAppProps( srcCtx.getAppProps() );
        setTickManager( srcCtx.getTickManager() );
        setExchangeManager( srcCtx.getExchangeManager() );
        setHubSession( srcCtx.getHubSession() );
    }

    @Override public <T extends SMTStartContext> T clone( final String id, final T srcCtx, final SMTComponentManager newMgr ) {
        return (T) new SMTContext( id, (SMTContext) srcCtx, newMgr );
    }

    public ClientProfileManager getClientProfileManager() {
        return _clientProfileManager;
    }

    public void setClientProfileManager( final ClientProfileManager clientProfileManager ) { _clientProfileManager = clientProfileManager; }

    public ExchangeManager getExchangeManager() {
        return _exchangeManager;
    }

    public void setExchangeManager( final ExchangeManager exchangeManager )                { _exchangeManager = exchangeManager; }

    public Session getHubSession()                                                         { return _hubSession; }

    public void setHubSession( final Session hubSession )                                  { _hubSession = hubSession; }

    public InstrumentStore getInstrumentLocator() {
        return _instrumentLocator;
    }

    public void setInstrumentLocator( InstrumentStore instrumentLocator ) {
        this._instrumentLocator = instrumentLocator;
        patchJSONUtils();
    }

    public TickManager getTickManager()                                                    { return _tickManager; }

    public void setTickManager( final TickManager tickManager )                            { _tickManager = tickManager; }

    public WarmupControl getWarmupControl() {
        return _warmupControl;
    }

    public void setWarmupControl( final WarmupControl warmupControl )                      { _warmupControl = warmupControl; }
}
