/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ZString;

public interface MktDataPublisher<T extends SnapableMktData> extends Identifiable {

    /**
     * receive an inst related data event relevant to the listener
     * <p>
     * use subject to understand what is being sent
     * use instanceof and cast on data to decode
     * use timestamp and seqnum for any needed sequencing
     */
    void publish( ZString subject, Instrument inst, Object data, long timestamp, long seqNum );
}
