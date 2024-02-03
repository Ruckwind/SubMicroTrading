/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

public final class AsciiFileAppender extends BaseFileAppender {

    public AsciiFileAppender( String fileName, long maxFileSize, boolean enforceMinFileSize ) {
        super( fileName, maxFileSize, enforceMinFileSize );
    }

    public AsciiFileAppender( String fileName, long maxFileSize ) {
        super( fileName, maxFileSize );
    }

    @Override
    public void doLog( byte[] buf, int offset, int len ) {

        if ( (len + _directBlockBuf.position() + 2) >= _directBlockBuf.capacity() ) {
            blockWriteToDisk();
        }

        _directBlockBuf.put( buf, offset, len );
        _directBlockBuf.put( (byte) 0x0A );
    }
}