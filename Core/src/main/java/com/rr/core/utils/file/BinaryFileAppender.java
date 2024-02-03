/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

public final class BinaryFileAppender extends BaseFileAppender {

    public BinaryFileAppender( String fileName, long maxFileSize, boolean enforceMinFileSize ) {
        super( fileName, maxFileSize, enforceMinFileSize );
    }

    public BinaryFileAppender( String fileName, long maxFileSize ) {
        super( fileName, maxFileSize );
    }

    @Override
    public void doLog( byte[] buf, int offset, int len ) {

        if ( (len + _directBlockBuf.position() + 4) >= _directBlockBuf.capacity() ) {
            blockWriteToDisk();
        }

        _directBlockBuf.putInt( len );
        _directBlockBuf.put( buf, offset, len );
    }

}