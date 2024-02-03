/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CMEConnections {

    private Map<ZString, CMEConnection> _connections = new HashMap<>();

    public void add( CMEConnection conn ) {
        _connections.put( conn.getId(), conn );
    }

    public CMEConnection get( FeedType type, Feed feed ) {
        for ( CMEConnection conn : _connections.values() ) {
            if ( conn.getFeedType() == type && feed == conn.getFeed() ) {
                return conn;
            }
        }

        return null;
    }

    public CMEConnection get( FeedType type ) {
        for ( CMEConnection conn : _connections.values() ) {
            if ( conn.getFeedType() == type ) {
                return conn;
            }
        }

        return null;
    }

    public Iterator<CMEConnection> getConnectionIterator() {
        return _connections.values().iterator();
    }
}
