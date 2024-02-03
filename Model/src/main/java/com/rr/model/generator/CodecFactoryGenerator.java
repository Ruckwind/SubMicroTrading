/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings( "unused" )

public class CodecFactoryGenerator {

    private final static Logger _logger = Logger.getLogger( "CodecFactoryGenerator" );

    private final Model         _model;
    private final FactoryModel  _base;
    private final InternalModel _internal;
    private final FixModels     _fix;

    private final Collection<CodecDefinition>            _codecs            = new LinkedHashSet<>();
    private final Collection<BinaryCodecDefinition>      _binCodecs         = new LinkedHashSet<>();
    private final Collection<HandCraftedCodecDefinition> _handCraftedCodecs = new LinkedHashSet<>();

    private StringBuilder   _b;
    private String          _className;
    private CodecDefinition _def;
    private FixModel        _fixModel;
    private boolean         _isRecovery;

    public CodecFactoryGenerator( Model model ) {
        _model    = model;
        _base     = _model.getFactoryModel();
        _fix      = _model.getFix();
        _internal = _model.getInternal();
    }

    public void generate() throws FileException, IOException {

        GenUtils.makeVersionDirectory( _base );
        GenUtils.createRootPackageDir( _base );

        GenUtils.createPackage( _base.getVersionDir(), ModelConstants.CODEC_PACKAGE );

        addCodecs( _model.getCodec() );
        addCodecs( _model.getClientCodecs() );
        addCodecs( _model.getExchangeCodecs() );

        _binCodecs.addAll( _model.getBinaryCodecs().getCodecs() );
        _handCraftedCodecs.addAll( _model.getBinaryCodecs().getHandCodecs() );

        writeCodecIds();
        writeFactory();
    }

    public void writeCodecIds() throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String className = "CodecId";

        File file = GenUtils.getJavaFile( _base, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _base, ModelConstants.CODEC_PACKAGE, className );

        b.append( "\nimport com.rr.core.codec.CodecName;\n" );
        b.append( "\nimport com.rr.core.model.FixVersion;\n" );

        b.append( "\npublic enum " ).append( className ).append( " implements CodecName {\n" );

        int count = 0;

        for ( CodecDefinition def : _codecs ) {

            if ( count++ > 0 ) b.append( ",\n" );

            switch( def.getFixId() ) {
            case "4.2":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.Fix4_2 )" );
                break;
            case "4.4":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.Fix4_4 )" );
                break;
            case "DC4.4":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.DCFix4_4 )" );
                break;
            case "MD4.4":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.MDFix4_4 )" );
                break;
            case "MD5.0":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.MDFix5_0 )" );
                break;
            case "5.0":
                b.append( "    " ).append( def.getId() ).append( "( FixVersion.Fix5_0 )" );
                break;
            default:
                throw new RuntimeException( "Only fix 4.2, 4.4 and 5.0 supported at present not " + def.getFixId() );
            }
        }

        for ( BinaryCodecDefinition def : _binCodecs ) {
            if ( count++ > 0 ) b.append( ",\n" );
            b.append( "    " ).append( def.getId() ).append( "( null )" );
        }

        for ( HandCraftedCodecDefinition def : _handCraftedCodecs ) {
            if ( count++ > 0 ) b.append( ",\n" );
            b.append( "    " ).append( def.getId() ).append( "( null )" );
        }

        b.append( ";\n\n" );

        b.append( "    private FixVersion _ver;\n\n" );

        b.append( "    CodecId( FixVersion ver )  {\n" );
        b.append( "        _ver = ver;\n" );
        b.append( "    }\n\n" );

        b.append( "    public FixVersion getFixVersion() { return _ver; }\n" );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    public void writeFactory() throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String className = "CodecFactoryPopulator";

        File file = GenUtils.getJavaFile( _base, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _base, ModelConstants.CODEC_PACKAGE, className );

        b.append( "import com.rr.core.codec.CodecFactory;\n" );
        b.append( "import com.rr.model.generated.codec.*;\n" );

        b.append( "\npublic class " ).append( className ).append( " {\n\n" );

        b.append( "    public void register( CodecFactory factory ) {\n" );
        for ( CodecDefinition def : _codecs ) {
            b.append( "        factory.register( CodecId." ).append( def.getId() ).append( ", \n" );
            b.append( "                          " ).append( def.getId() ).append( "Encoder.class, \n" );

            if ( def.hasOMSEvents() ) {
                b.append( "                          " ).append( def.getId() ).append( "Decoder" ).append( ModelConstants.OMS_DECODER_POSTNAME )
                 .append( ".class, \n" );
                b.append( "                          " ).append( def.getId() ).append( "Decoder" ).append( ModelConstants.FULL_DECODER_POSTNAME )
                 .append( ".class ); \n" );
            } else {
                b.append( "                          " ).append( def.getId() ).append( "Decoder.class, \n" );
                b.append( "                          " ).append( def.getId() ).append( "Decoder.class ); \n" );
            }
        }

        for ( BinaryCodecDefinition def : _binCodecs ) {
            if ( def.isAbstract() ) continue;

            b.append( "        factory.register( CodecId." ).append( def.getId() ).append( ", \n" );
            b.append( "                          " ).append( def.getId() ).append( "Encoder.class, \n" );
            b.append( "                          " ).append( def.getId() ).append( "Decoder.class, \n" );
            b.append( "                          " ).append( def.getId() ).append( "Decoder.class ); \n" );
        }

        b.append( "    }\n" );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void addCodecs( CodecModel codec ) {

        _codecs.addAll( codec.getCodecs() );
    }
}
