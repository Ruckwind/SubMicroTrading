/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.lang.ZString;

import java.util.Arrays;

public class CMEConnection {

    public enum Protocol {
        TCP,
        UDP
    }

    private final ZString   _id;       // eg :  7H3A
    private final FeedType  _feedType; // eg : H Historical Replay
    private final Protocol  _protocol; // eg : TCP
    private final ZString   _ip;       // eg : 205.209.222.43 (multicast address)
    private final ZString[] _hostIPs;  // eg : 
    private final int       _port;     // eg : 10000
    private final Feed      _feed;     // eg : A

    public CMEConnection( ZString id, FeedType feedType, Protocol protocol, ZString ip, ZString[] hostIPs, int port, Feed feed ) {
        super();
        _id       = id;
        _feedType = feedType;
        _protocol = protocol;
        _ip       = ip;
        _port     = port;
        _feed     = feed;
        _hostIPs  = hostIPs;
    }

    @Override
    public String toString() {
        return "CMEConnection [_id=" + _id + ", _feedType=" + _feedType + ", _protocol=" + _protocol + ", _ip=" + _ip + ", _hostIPs="
               + Arrays.toString( _hostIPs ) + ", _port=" + _port + ", _feed=" + _feed + "]";
    }

    public Feed getFeed() {
        return _feed;
    }

    public FeedType getFeedType() {
        return _feedType;
    }

    public ZString[] getHostIPs() {
        return _hostIPs;
    }

    public ZString getIP() {
        return _ip;
    }

    public ZString getId() {
        return _id;
    }

    public int getPort() {
        return _port;
    }

    public Protocol getProtocol() {
        return _protocol;
    }
}
