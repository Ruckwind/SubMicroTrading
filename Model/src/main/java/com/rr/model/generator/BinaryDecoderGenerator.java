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

public class BinaryDecoderGenerator {

    private final static Logger _logger = Logger.getLogger( "BinaryDecoderGenerator" );

    private final Model            _model;
    private final BinaryCodecModel _codecs;
    private final InternalModel    _internal;
    private final BinaryModels     _binary;

    public BinaryDecoderGenerator( Model model, BinaryCodecModel codecModel ) {
        _model    = model;
        _codecs   = codecModel;
        _binary   = _model.getBinary();
        _internal = _model.getInternal();
    }

    public void generate() throws FileException, IOException {

        GenUtils.makeVersionDirectory( _codecs );
        GenUtils.createRootPackageDir( _codecs );

        GenUtils.createPackage( _codecs.getVersionDir(), ModelConstants.CODEC_PACKAGE );

        Collection<BinaryCodecDefinition> codecs = _codecs.getCodecs();

        for ( BinaryCodecDefinition def : codecs ) {

            writeDecoder( def );
        }
    }

    private void writeDecoder( BinaryCodecDefinition def ) throws FileException, IOException {

        if ( def.isAbstract() ) return;

        BinaryModel binModel = _binary.getBinaryModel( def.getBinaryModelId() );

        Class<DecoderGenerator> decoderGeneratorClass = def.getDecoderGenerator();
        DecoderGenerator        generator             = ReflectUtils.create( decoderGeneratorClass );

        generator.generate( _internal, _codecs, def, binModel );
    }
}
