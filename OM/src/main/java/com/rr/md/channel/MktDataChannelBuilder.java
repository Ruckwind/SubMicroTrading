/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.channel;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.EventHandler;

public interface MktDataChannelBuilder<T> extends SMTComponent {

    /**
     * register the consumer with appropriate sessions for the channelKey
     * <p>
     * builder can throw exception if channel already associated with another consumer
     *
     * @param channelKey channel / instrument segment
     * @param consumer
     */
    void register( T channelKey, String pipeLineId, EventHandler consumer );
}
