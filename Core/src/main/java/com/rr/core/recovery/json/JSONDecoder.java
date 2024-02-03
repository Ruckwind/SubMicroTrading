package com.rr.core.recovery.json;

import com.rr.core.codec.Decoder;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.TimeUtils;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.InstrumentLocator;
import com.rr.core.utils.SMTRuntimeException;

@SuppressWarnings( "unchecked" )

public class JSONDecoder implements Decoder {

    private final String _id;
    private InstrumentLocator _instLocator;
    private JSONReaderImpl            _reader;
    private ReusableString            _inBuf;
    private ReusableStringInputStream _inStream;

    public JSONDecoder( String id ) {
        _id = id;
    }

    @Override public Event decode( final byte[] msg, final int offset, final int maxIdx ) {
        _inBuf.copy( msg, offset, maxIdx );
        _inStream.set( _inBuf );

        try {
            return _reader.jsonToObject();

        } catch( Exception e ) {
            throw new SMTRuntimeException( "JSONDecoder.decode " + e.getMessage(), e );
        }
    }

    @Override public InstrumentLocator getInstrumentLocator()                               { return _instLocator; }

    @Override public void setInstrumentLocator( final InstrumentLocator instrumentLocator ) { _instLocator = instrumentLocator; }

    @Override public int getLength()                                                                   { return 0; }

    @Override public long getReceived()                                                                { return 0; }

    @Override public void setReceived( final long nanos )                                              { /* nothing */ }

    @Override public int getSkipBytes()                                                                { return 0; }

    @Override public int parseHeader( final byte[] inBuffer, final int inHdrLen, final int bytesRead ) { return 0; }

    @Override public Event postHeaderDecode()                                                          { return null; }

    @Override public ResyncCode resync( final byte[] fixMsg, final int offset, final int maxIdx )      { return null; }

    @Override public void setClientProfile( final ClientProfile client )                    { /* nothing */ }

    @Override public void setNanoStats( final boolean nanoTiming )                                     { /* nothing */ }

    @Override public void setTimeUtils( final TimeUtils calc )                                         { /* nothing */ }

    @Override public String getComponentId()                                                           { return _id; }

    public void init( SMTStartContext ctx ) {
        _inBuf    = new ReusableString();
        _inStream = new ReusableStringInputStream( _inBuf );
        _reader   = new JSONReaderImpl( _inStream, new JSONClassDefinitionCache( ctx ) );
    }
}
