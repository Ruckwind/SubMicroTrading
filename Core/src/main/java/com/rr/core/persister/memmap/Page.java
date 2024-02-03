/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister.memmap;

import java.nio.MappedByteBuffer;

public final class Page {

    private          int              _pageNo = -1;
    private volatile MappedByteBuffer _buf    = null;

    public Page() {
        super();
    }

    public Page( int pageNo, MappedByteBuffer buf ) {
        super();
        _pageNo = pageNo;
        _buf    = buf;
    }

    public MappedByteBuffer getMappedByteBuf() {
        return _buf;
    }

    public void setMappedByteBuf( MappedByteBuffer buf ) {
        _buf = buf;
    }

    public int getPageNo() {
        return _pageNo;
    }

    public void setPageNo( int pageNo ) {
        _pageNo = pageNo;
    }

    public void reset() {
        _pageNo = -1;
        _buf    = null;
    }
}
