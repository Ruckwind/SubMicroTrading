/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.component.SMTComponent;
import com.rr.core.utils.SMTRuntimeException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CodecFactory implements SMTComponent {

    private final Map<CodecName, Constructor<? extends Encoder>> _encoderMap     = new HashMap<>();
    private final Map<CodecName, Constructor<? extends Decoder>> _omsDecoderMap  = new HashMap<>();
    private final Map<CodecName, Constructor<? extends Decoder>> _fullDecoderMap = new HashMap<>();
    private final String                                         _id;

    public CodecFactory() {
        this( "CodecFactory" );
    }

    public CodecFactory( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public Encoder getEncoder( CodecName id, byte[] buf, int offset ) {

        Constructor<? extends Encoder> constructor = _encoderMap.get( id );

        Encoder encoder = null;

        if ( constructor != null ) {
            try {
                encoder = constructor.newInstance( buf, offset );
            } catch( Exception e ) {
                throw new SMTRuntimeException( "Error in instantiating encoder id " + id.toString() + " " + e.getMessage(), e );
            }
        }

        if ( encoder == null ) {
            throw new SMTRuntimeException( "Unsupported codec id " + id.toString() );
        }

        return encoder;
    }

    public Decoder getFullDecoder( CodecName id ) {
        Constructor<? extends Decoder> constructor = _fullDecoderMap.get( id );

        Decoder decoder = null;

        if ( constructor != null ) {
            try {
                decoder = constructor.newInstance();
            } catch( Exception e ) {
                throw new SMTRuntimeException( "Error in instantiating full decoder id " + id.toString() + " " + e.getMessage(), e );
            }
        }

        if ( decoder == null ) {
            throw new SMTRuntimeException( "Unsupported codec id " + id.toString() );
        }

        return decoder;
    }

    public Decoder getOMSDecoder( CodecName id ) {
        Constructor<? extends Decoder> constructor = _omsDecoderMap.get( id );

        Decoder decoder = null;

        if ( constructor != null ) {
            try {
                decoder = constructor.newInstance();
            } catch( Exception e ) {
                throw new SMTRuntimeException( "Error in instantiating decoder id " + id.toString() + " " + e.getMessage(), e );
            }
        }

        if ( decoder == null ) {
            throw new SMTRuntimeException( "Unsupported codec id " + id.toString() );
        }

        return decoder;
    }

    public void register( CodecName id, Class<? extends Encoder> encoder, Class<? extends Decoder> omsDecoder, Class<? extends Decoder> fullDecoder ) {

        try {
            _encoderMap.put( id, encoder.getConstructor( byte[].class, int.class ) );
            _omsDecoderMap.put( id, omsDecoder.getConstructor() );
            _fullDecoderMap.put( id, fullDecoder.getConstructor() );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "Error in registering codec " + id.toString() + " " + e.getMessage(), e );
        }
    }
}

