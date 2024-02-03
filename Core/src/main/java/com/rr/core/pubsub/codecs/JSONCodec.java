package com.rr.core.pubsub.codecs;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.pubsub.Codec;
import com.rr.core.recovery.json.*;

public class JSONCodec implements Codec {

    private final JSONReader                 _reader;
    private final JSONWriter                 _writer;
    private final ReusableStringInputStream  _inputStream;
    private final ReusableStringOutputStream _outputStream;
    private       boolean                    _clearResolverPerDecode;

    public JSONCodec( final SMTStartContext ctx ) {
        _inputStream  = new ReusableStringInputStream();
        _outputStream = new ReusableStringOutputStream( 1 );

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( ctx );
        _reader = new JSONReaderImpl( _inputStream, cache );
        _writer = new JSONWriterImpl( _outputStream, cache, new SMTComponentManager(), false, new JSONWriteSharedStateNoRefs() );
    }

    public JSONCodec( final SMTStartContext ctx, final boolean compressOn, final boolean clearResolverPerDecode ) {

        this( ctx );

        if ( compressOn ) {
            enableCompress();
        }

        setClearResolverPerDecode( clearResolverPerDecode );
    }

    @Override public <M> M decode( final ReusableString encodedMsg ) throws Exception {

        _inputStream.set( encodedMsg );

        final M obj = _reader.jsonToObject();

        if ( _clearResolverPerDecode ) {
            _reader.getResolver().clear();
        }

        return obj;
    }

    @Override public void encode( final Object obj, final ReusableString dest ) throws Exception {
        dest.reset();

        _outputStream.set( dest );

        _writer.objectToJson( obj, PersistMode.AllFields );
    }

    @Override public void setEncodeNewLineChar( final String newLine ) {
        _writer.setEncodeNewLineChar( newLine );
    }

    @Override public void setExcludeNullFields( final boolean excludeNullFields ) { _writer.setExcludeNullFields( excludeNullFields ); }

    @Override public void setVerboseSpacing( final boolean isEnabled ) {
        _writer.setVerboseSpacing( isEnabled );
    }

    public <M> M decode( final ReusableString encodedMsg, Class<M> topClass ) throws Exception {

        _inputStream.set( encodedMsg );

        M m = (M) _reader.procValue( topClass );

        if ( _clearResolverPerDecode ) {
            _reader.getResolver().clear();
        }

        return m;
    }

    public void enableCompress() { _writer.enableCompress(); }

    public void setClearResolverPerDecode( final boolean clearResolverPerDecode ) {
        _clearResolverPerDecode = clearResolverPerDecode;
    }
}
