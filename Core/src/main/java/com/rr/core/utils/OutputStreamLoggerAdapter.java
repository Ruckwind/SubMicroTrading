package com.rr.core.utils;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;

import java.io.IOException;
import java.io.Writer;

public class OutputStreamLoggerAdapter extends Writer {

    private final Logger _log;

    private final ReusableString _buf = new ReusableString( 128 );

    public OutputStreamLoggerAdapter( final Logger log ) {
        _log = log;
    }

    @Override public void write( final char[] cbuf, final int off, final int len ) throws IOException {
        final int max = off + len;
        for ( int i = off; i < max; i++ ) {
            _buf.append( cbuf[ i ] );
        }
    }

    @Override public void flush() throws IOException {
        _log.info( _buf );
        _buf.reset();
    }

    @Override public void close() throws IOException {
        if ( _buf.length() > 0 ) {
            flush();
        }
    }
}
