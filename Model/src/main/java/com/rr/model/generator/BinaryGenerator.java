/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;

import java.io.File;
import java.io.IOException;

public class BinaryGenerator {

    private final BinaryModels _base;

    public BinaryGenerator( Model model ) {
        _base = model.getBinary();
    }

    public void generate() throws FileException, IOException {
        GenUtils.makeVersionDirectory( _base );
        GenUtils.createRootPackageDir( _base );

        GenUtils.createPackage( _base.getVersionDir(), ModelConstants.MODEL_PACKAGE );
        GenUtils.createPackage( _base.getVersionDir(), ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE );

        for ( BinaryModel model : _base.getBinaryModels() ) {
            writeBinaryModel( model );
        }
    }

    private void writeBinaryModel( BinaryModel model ) throws FileException, IOException {
        writeBinaryMsgTypes( model );
    }

    private void writeBinaryMsgTypes( BinaryModel model ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        String className = GenUtils.getBinaryConstantsFile( model );

        GenUtils.addPackageDef( b, _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.addGenerated( b, _base, ModelConstants.MODEL_PACKAGE, className );

        b.append( "public interface " ).append( className ).append( " {\n" );

        for ( BinaryEventDefinition msg : model.getMessages() ) {
            if ( !model.hasMessageTypeAscii() ) {
                b.append( "    public int " ).append( msg.getBinaryMsgType() ).append( " = " ).append( msg.getMsgType() ).append( ";\n" );
            } else {
                b.append( "    public byte[] " ).append( msg.getBinaryMsgType() ).append( " = \"" ).append( msg.getMsgType() ).append( "\".getBytes();\n" );
            }
        }

        b.append( "\n" );

        for ( BinaryDictionaryTag entry : model.getDictionaryEntries() ) {
            int    code   = entry.getCode();
            String baseId = entry.getId();
            String id     = GenUtils.toUpperFirstChar( baseId );

            if ( code > 0 ) {
                b.append( "    public int " ).append( id ).append( "Code = " ).append( code ).append( ";\n" );
            }
        }

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.writeFile( file, b );
    }
}
