package com.rr.core.recovery.json;

import java.util.Set;

public class JSONWriteSharedStateNoRefs implements JSONWriteSharedState {

    @Override public void reset()                                                                         { /* nothing */ }

    @Override public void reset( final int startId )                                                      { /* nothing */ }

    @Override public int getObjId( final Object obj, final boolean b )                                    { return -1; }

    @Override public boolean prepWrite( final Object obj )                                                { return true; }

    @Override public void checkMissing( final Set<Object> missing, final JSONClassDefinitionCache cache ) { /* nothing */ }

    @Override public boolean forceReference( final Object obj )                                           { return false; }
}
