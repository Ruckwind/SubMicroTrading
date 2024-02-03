package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTCoreContext;
import com.rr.core.lang.BaseTestCase;
import org.junit.After;
import org.junit.Before;

public abstract class BaseJSONTest extends BaseTestCase {

    protected JSONPrettyDump           _jsonUtils = new JSONPrettyDump();
    protected JSONClassDefinitionCache _cache;
    protected SMTCoreContext           _ctx       = createCtx();

    @After public void reset() {
        if ( _cache != null ) _cache.resetPermissions( false );
    }

    @Before public void setup() throws Exception {
        if ( _ctx.getComponentManager() == null ) _ctx.setComponentManager( new SMTComponentManager() );
        _cache = new JSONClassDefinitionCache( _ctx );
    }

    protected SMTCoreContext createCtx() {
        return new SMTCoreContext( "ctx", new SMTComponentManager() );
    }
}
