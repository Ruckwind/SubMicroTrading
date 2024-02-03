/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import java.io.IOException;

public interface SimClient {

    void dispatchEvents( int numOrders, int batchSize, int delayMicros ) throws IOException;

    int getExpectedReplies();

    int getSent();

    void reset();

}
