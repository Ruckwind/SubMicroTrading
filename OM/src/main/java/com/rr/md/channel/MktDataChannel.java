/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.channel;

/**
 * represents an individual channel stream that can be used as source for market data events
 * <p>
 * Channel may also be analogous to instrument segment
 * <p>
 * A session may have many channels
 * A channel may have may keys
 */

public interface MktDataChannel<T> {

    void addChannelKey( T channelKey );

    T[] getChannelKeys();

    /**
     * @param channelKey
     * @return true if the MarketDataChannel supports the supplied channelKey
     */
    boolean hasChannelKey( T channelKey );
}
