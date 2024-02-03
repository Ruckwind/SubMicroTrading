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

public class FixCodecDecoderGenerator {

    private final static Logger _logger = Logger.getLogger( "BinaryDecoderGenerator" );

    private final Model         _model;
    private final CodecModel    _codecs;
    private final InternalModel _internal;
    private final FixModels     _fix;

    public FixCodecDecoderGenerator( Model model, CodecModel codecModel ) {
        _model    = model;
        _codecs   = codecModel;
        _fix      = _model.getFix();
        _internal = _model.getInternal();
    }

    public void generate() throws FileException, IOException {

        GenUtils.makeVersionDirectory( _codecs );
        GenUtils.createRootPackageDir( _codecs );

        GenUtils.createPackage( _codecs.getVersionDir(), ModelConstants.CODEC_PACKAGE );

        Collection<CodecDefinition> codecs = _codecs.getCodecs();

        for ( CodecDefinition def : codecs ) {
            writeDecoder( def );
        }
    }

    private void writeDecoder( CodecDefinition def ) throws FileException, IOException {

        FixModel binModel = _fix.getFixModel( def.getFixId() );

        Class<? extends FixDecoderGenerator> decoderGeneratorClass = def.getDecoderGenerator();
        FixDecoderGenerator                  generator             = ReflectUtils.create( decoderGeneratorClass );

        generator.generate( _fix, _internal, _codecs, def, binModel );
    }
}
