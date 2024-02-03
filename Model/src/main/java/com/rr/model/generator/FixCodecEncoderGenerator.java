/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.base.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings( "unused" )

public class FixCodecEncoderGenerator {

    private final static Logger _logger = Logger.getLogger( "BinaryEncoderGenerator" );

    private final Model         _model;
    private final CodecModel    _codecs;
    private final InternalModel _internal;
    private final FixModels     _fix;

    public FixCodecEncoderGenerator( Model model, CodecModel codecModel ) {
        _model    = model;
        _codecs   = codecModel;
        _fix      = _model.getFix();
        _internal = _model.getInternal();
    }

    public void generate() throws FileException, IOException {

        Collection<CodecDefinition> codecs = _codecs.getCodecs();

        Exception err = null;

        for ( CodecDefinition def : codecs ) {
            try {
                writeEncoder( def );
            } catch( Exception e ) {
                _logger.info( "Codec failed encode for " + def.getId() );
                err = e;
            }
        }

        if ( err != null ) throw new SMTRuntimeException( "Encode generate failed " + err.getMessage(), err );
    }

    private void writeEncoder( CodecDefinition def ) throws FileException, IOException {

        FixModel binModel = _fix.getFixModel( def.getFixId() );

        Class<? extends FixEncoderGenerator> encoderGeneratorClass = def.getEncoderGenerator();
        FixEncoderGenerator                  generator             = ReflectUtils.create( encoderGeneratorClass );

        generator.generate( _fix, _internal, _codecs, def, binModel );
    }
}
