/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.core.utils.ReflectUtils;
import com.rr.model.base.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings( "unused" )

public class BinaryEncoderGenerator {

    private final static Logger _logger = Logger.getLogger( "BinaryEncoderGenerator" );

    private final Model            _model;
    private final BinaryCodecModel _codecs;
    private final InternalModel    _internal;
    private final BinaryModels     _binary;

    public BinaryEncoderGenerator( Model model, BinaryCodecModel codecModel ) {
        _model    = model;
        _codecs   = codecModel;
        _binary   = _model.getBinary();
        _internal = _model.getInternal();
    }

    public void generate() throws FileException, IOException {

        Collection<BinaryCodecDefinition> codecs = _codecs.getCodecs();

        for ( BinaryCodecDefinition def : codecs ) {

            writeEncoder( def );
        }
    }

    private void writeEncoder( BinaryCodecDefinition def ) throws FileException, IOException {
        if ( def.isAbstract() ) return;

        BinaryModel binModel = _binary.getBinaryModel( def.getBinaryModelId() );

        Class<EncoderGenerator> encoderGeneratorClass = def.getEncoderGenerator();
        EncoderGenerator        generator             = ReflectUtils.create( encoderGeneratorClass );

        generator.generate( _internal, _codecs, def, binModel );
    }
}
