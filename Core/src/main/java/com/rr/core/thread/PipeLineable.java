/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import java.util.List;

public interface PipeLineable {

    /**
     * @return list of the pipeLineIds or null if not assigned
     */
    List<String> getPipeLineIds();

    /**
     * @param pipeLineId
     * @return true if the the supplied pipeLineId is associated
     */
    boolean hasPipeLineId( String pipeLineId );
}
