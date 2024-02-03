/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.properties.AppProps;
import com.rr.core.recovery.SnapshotCaretaker;
import com.rr.core.recovery.json.custom.CustomJSONCodecs;

/**
 * Context block used by components in initialisation
 * <p>
 * Allows simplification of glueing be passing common components
 */
public interface SMTStartContext extends SMTComponent {

    <T extends SMTStartContext> T clone( String id, T srcCtx, final SMTComponentManager newMgr );

    AppProps getAppProps();

    SMTComponentManager getComponentManager();

    void setComponentManager( SMTComponentManager componentManager );

    CustomJSONCodecs getJSONCustomCodecs();

    void setJSONCustomCodecs( CustomJSONCodecs customCodecss );

    long getRestoredTimestamp();

    void setRestoredTimestamp( long snapshotCreationTimestamp );

    SnapshotCaretaker getSnapshotCaretaker();

}
