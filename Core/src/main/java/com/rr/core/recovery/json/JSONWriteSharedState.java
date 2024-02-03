package com.rr.core.recovery.json;

import java.util.Set;

public interface JSONWriteSharedState {

    void checkMissing( Set<Object> missing, final JSONClassDefinitionCache cache );

    /**
     * @param obj
     * @return if the object is a top level component .. ie one that should not be recursed into
     */
    boolean forceReference( final Object obj );

    /**
     * @param obj
     * @param setAsWritten - if object is about to be written set this to true
     * @return return unique (within this shared scope) object id for object
     */
    int getObjId( final Object obj, final boolean setAsWritten );

    /**
     * @param obj
     * @return false if object already written. true id can write
     */
    boolean prepWrite( final Object obj );

    void reset();

    void reset( int startId );

}
