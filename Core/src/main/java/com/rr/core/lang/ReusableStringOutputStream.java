package com.rr.core.lang;

import java.io.IOException;
import java.io.OutputStream;

public class ReusableStringOutputStream extends OutputStream {

    private ReusableString _str;

    public ReusableStringOutputStream( ReusableString dest ) {
        _str = dest;
    }

    public ReusableStringOutputStream( int preSize ) {
        _str = new ReusableString( preSize );
    }

    @Override public String toString() {
        return "ReusableStringStream [" + _str + "\n]";
    }

    @Override public void write( final int b ) throws IOException {
        _str.append( (char) b );
    }

    public void copy( ReusableString out ) {
        out.copy( _str );
    }

    /**
     * @return the underlying buffer .... copy it if needed async or beyond duration of func
     */
    public ReusableString getBuf() {
        return _str;
    }

    public void reset() {
        _str.reset();
    }

    public void set( ReusableString str ) {
        _str = str;
    }
}
