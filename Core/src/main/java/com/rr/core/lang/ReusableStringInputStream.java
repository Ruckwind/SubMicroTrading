package com.rr.core.lang;

import java.io.IOException;
import java.io.InputStream;

public class ReusableStringInputStream extends InputStream {

    private ReusableString _buf;
    private int            _len = 0;
    private int            _idx = -1;

    public ReusableStringInputStream() {
        // nothing
    }

    public ReusableStringInputStream( ReusableString str ) {
        _buf = new ReusableString( str );
        _len = _buf.length();
    }

    @Override public int read() throws IOException {
        if ( ++_idx >= _len ) return -1;

        return _buf.getByte( _idx );
    }

    public void copy( ReusableString str ) {
        _buf.copy( str );
        _idx = -1;
        _len = _buf.length();
    }

    public void set( ReusableString str ) {
        _buf = str;
        _idx = -1;
        _len = _buf.length();
    }
}
