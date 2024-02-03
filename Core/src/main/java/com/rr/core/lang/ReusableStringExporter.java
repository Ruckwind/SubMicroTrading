package com.rr.core.lang;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.model.Event;
import com.rr.core.pubsub.codecs.SMTExporter;
import com.rr.core.recovery.json.JSONUtils;

import java.io.IOException;

public class ReusableStringExporter implements SMTExporter {

    private ReusableString _str;

    public ReusableStringExporter( ReusableString dest ) {
        _str = dest;
    }

    public ReusableStringExporter( int preSize ) {
        _str = new ReusableString( preSize );
    }

    @Override public void close() throws IOException        { /* nothing */ }

    @Override public String getExportId()                   { return "ReusableStringExporter"; }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) { /* nothing */ }

    @Override public void setBlockSize( final int bufSize ) { /* nothing */ }

    @Override public void write( final Event obj ) throws Exception {
        _str.append( JSONUtils.objectToJSON( obj ) ).append( "\n" );
    }

    @Override public String toString()     { return "ReusableStringStream [" + _str + "\n]"; }

    public void copy( ReusableString out ) { out.copy( _str ); }

    /**
     * @return the underlying buffer .... copy it if needed async or beyond duration of func
     */
    public ReusableString getBuf() { return _str; }

    public void reset() {
        _str.reset();
    }

    public void set( ReusableString str )                   { _str = str; }
}
