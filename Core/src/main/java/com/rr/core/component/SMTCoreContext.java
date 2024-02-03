/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.properties.AppProps;
import com.rr.core.recovery.SnapshotCaretaker;
import com.rr.core.recovery.json.IgnoreDependency;
import com.rr.core.recovery.json.JSONUtils;
import com.rr.core.recovery.json.custom.CustomJSONCodecs;

import java.nio.charset.Charset;

/**
 * Context block passed by Bootstrap to initialise method
 */
public class SMTCoreContext implements SMTStartContext, SMTSnapshotMemberAllFields {

    private final                       String              _id;
    private transient                   AppProps            _appProps;
    private transient @IgnoreDependency SMTComponentManager _componentManager;
    private transient @IgnoreDependency CustomJSONCodecs    _jsonCustomCodecs = new CustomJSONCodecs( "defaultCustomCodecs" );
    private transient @IgnoreDependency SnapshotCaretaker   _snapshotCaretaker;
    private transient                   long                _snapshotCreationTimestamp;

    public SMTCoreContext( String id ) {
        this( id, null );
    }

    public SMTCoreContext( String id, SMTComponentManager mgr ) {
        _id               = id;
        _componentManager = mgr;
    }

    public SMTCoreContext( String id, SMTCoreContext srcCtx, SMTComponentManager mgr ) {
        this( id, mgr );
        setAppProps( srcCtx.getAppProps() );
        setSnapshotCaretaker( srcCtx.getSnapshotCaretaker() );
    }

    @Override public String getComponentId()                                                { return _id; }

    @Override public SMTComponentManager getComponentManager()                              { return _componentManager; }

    @Override public void setComponentManager( final SMTComponentManager componentManager ) { _componentManager = componentManager; }

    @Override public CustomJSONCodecs getJSONCustomCodecs()                       { return _jsonCustomCodecs; }

    @Override public void setJSONCustomCodecs( final CustomJSONCodecs customCodecs ) {
        _jsonCustomCodecs = customCodecs;
        patchJSONUtils();
    }

    @Override public <T extends SMTStartContext> T clone( final String id, final T srcCtx, final SMTComponentManager newMgr ) {
        return (T) new SMTCoreContext( id, (SMTCoreContext) srcCtx, newMgr );
    }

    @Override public AppProps getAppProps()                                       { return _appProps; }

    public void setAppProps( AppProps appProps )                                  { _appProps = appProps; }

    @Override public SnapshotCaretaker getSnapshotCaretaker()                     { return _snapshotCaretaker; }

    @Override public long getRestoredTimestamp()                                            { return _snapshotCreationTimestamp; }

    @Override public void setRestoredTimestamp( final long snapshotCreationTimestamp )      { _snapshotCreationTimestamp = snapshotCreationTimestamp; }

    public void setSnapshotCaretaker( final SnapshotCaretaker snapshotCaretaker ) { _snapshotCaretaker = snapshotCaretaker; }

    @Override public void postRestore( final long snapshotTime, final SMTStartContext context ) {
        setComponentManager( context.getComponentManager() );
        patchJSONUtils();
    }

    @Override public String toString() {
        return "SMTCoreContext{" + _id + " }";
    }

    protected void patchJSONUtils()                                               { JSONUtils.setContext( this ); }
}
