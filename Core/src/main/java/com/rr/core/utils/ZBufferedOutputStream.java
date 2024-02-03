package com.rr.core.utils;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * wrapper to allow if necessary access to underlying output stream
 * <p>
 * eg flush doesnt finish a gzip buffer
 */
public class ZBufferedOutputStream extends BufferedOutputStream {

    public ZBufferedOutputStream( final OutputStream outputStream, final int bufSize ) {
        super( outputStream, bufSize );
    }
}
