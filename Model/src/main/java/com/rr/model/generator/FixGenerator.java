/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;

import java.io.File;
import java.io.IOException;

public class FixGenerator {

    private final FixModels _base;

    public static void addFixDictionaryImport( StringBuilder b, FixModels base, FixModel model ) {
        String className = GenUtils.getFixDictionaryFile( model );
        b.append( "import " ).append( base.getRootPackage() ).append( "." ).append( ModelConstants.MODEL_PACKAGE ).append( "." )
         .append( ModelConstants.DEFN_PACKAGE ).append( "." ).append( className ).append( ";\n" );
    }

    public FixGenerator( Model model ) {
        _base = model.getFix();
    }

    public void generate() throws FileException, IOException {
        GenUtils.makeVersionDirectory( _base );
        GenUtils.createRootPackageDir( _base );

        GenUtils.createPackage( _base.getVersionDir(), ModelConstants.MODEL_PACKAGE );
        GenUtils.createPackage( _base.getVersionDir(), ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE );

        for ( FixModel model : _base.getFixModels() ) {
            writeFixModel( model );
        }
    }

    private void writeFixDictionary( FixModel model ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        String className = GenUtils.getFixDictionaryFile( model );

        GenUtils.addPackageDef( b, _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.addGenerated( b, _base, ModelConstants.MODEL_PACKAGE, className );

        b.append( "public interface " ).append( className ).append( " {\n" );

        for ( FixDictionaryTag tag : model.getDictionaryEntries() ) {
            b.append( "    public int " ).append( tag.getName() ).append( " = " ).append( tag.getId() ).append( ";\n" );
        }

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.writeFile( file, b );
    }

    private void writeFixModel( FixModel model ) throws FileException, IOException {
        writeFixMsgTypes( model );
        writeFixDictionary( model );
    }

    private void writeFixMsgTypes( FixModel model ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        String className = GenUtils.getFixConstantsFile( model );

        GenUtils.addPackageDef( b, _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.addGenerated( b, _base, ModelConstants.MODEL_PACKAGE, className );

        b.append( "public interface " ).append( className ).append( " {\n" );

        for ( FixEventDefinition msg : model.getMessages() ) {
            b.append( "    public byte[] " ).append( msg.getFixMsgType() ).append( " = \"" ).append( msg.getMsgType() ).append( "\".getBytes();\n" );
        }

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _base, ModelConstants.MODEL_PACKAGE, ModelConstants.DEFN_PACKAGE, className );

        GenUtils.writeFile( file, b );
    }
}
