/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.model.base.*;
import com.rr.model.base.type.ReusableStringType;
import com.rr.model.base.type.ViewStringType;

import java.io.*;

public class GenUtils {

    public static final  String FILLER            = "filler";
    private static final Logger _logger = ConsoleFactory.console( GenUtils.class, Level.WARN );
    private static final String INCLUDE           = "@INCLUDE ";
    private static final String SOURCE            = "source ";
    private static       String _includeDirectory = "./include/";

    private static boolean _enabled = true;

    public static void makeVersionDirectory( BaseModel model ) throws FileException {

        String rootDir = model.getDir();

        FileUtils.checkDirExists( rootDir, false );

        // now check the version directory isnt already present ... dont override or autodelete for safety

        String versionDir = model.getVersionDir();

        if ( _enabled ) {
            FileUtils.mkDir( versionDir );

            FileUtils.checkDirExists( versionDir, true );
        }
    }

    public static void createRootPackageDir( BaseModel model ) throws FileException {

        if ( _enabled ) {
            String packageDir = model.getPackageDir();
            FileUtils.mkDir( packageDir );
        }
    }

    public static File getJavaFile( BaseModel model, String intermediatePackage, String fileName ) {
        String fname = model.getPackageDir() + File.separator + intermediatePackage + File.separator + fileName + ".java";
        return new File( fname );
    }

    public static void addPackageDef( StringBuilder b, BaseModel model, String intermediatePackage, String fileName ) {
        String pk = model.getRootPackage() + "." + intermediatePackage;
        b.append( "package " ).append( pk ).append( ";\n\n" );
        addCopyright( b );
    }

    private static void addCopyright( final StringBuilder b ) {
        b.append( "/*\n" );
        b.append( "Copyright 2015 Low Latency Trading Limited\n" );
        b.append( "Author Richard Rose\n" );
        b.append( "*/\n\n" );
    }

    public static File getJavaFile( BaseModel model, String intermediatePackage, String subPackage, String fileName ) {
        String fname = model.getPackageDir() + File.separator + intermediatePackage + File.separator +
                       subPackage + File.separator + fileName + ".java";

        return new File( fname );
    }

    public static void addPackageDef( StringBuilder b, BaseModel model, String intermediatePackage, String subPackage, String fileName ) {
        String pk = model.getRootPackage() + "." + intermediatePackage + "." + subPackage;
        b.append( "package " ).append( pk ).append( ";\n\n" );
        addCopyright( b );
    }

    public static void writeFile( File file, StringBuilder b ) throws FileException, IOException {

        if ( _enabled ) {

            BufferedWriter bufferedWriter = null;

            try {
                FileUtils.mkDir( FileUtils.getDirName( file.getCanonicalPath() ) );

                //Construct the BufferedWriter object
                bufferedWriter = new BufferedWriter( new FileWriter( file ) );

                //Start writing to the output stream
                bufferedWriter.write( b.toString() );

            } catch( Exception e ) {
                throw new FileException( "writeFile() failed to write " + file.getCanonicalFile() + " err=" + e.getMessage(), e );
            } finally {
                //Close the BufferedWriter
                FileUtils.close( bufferedWriter );
            }
        } else {
            _logger.info( "\n=============================================================\n" +
                          "\nFILE : " + file.getCanonicalFile() + "\n\n" + b.toString() + "\n\n" );
        }
    }

    public static void createPackage( String rootDir, String subdir ) throws FileException {
        if ( _enabled ) {
            String dir = rootDir + File.separator + subdir;

            FileUtils.mkDir( dir );
        }
    }

    public static void createPackage( String rootDir, String subdir, String subSubDir ) throws FileException {
        if ( _enabled ) {
            String dir = rootDir + File.separator + subdir + File.separator + subSubDir;

            FileUtils.mkDir( dir );
        }
    }

    public static void addGenerated( StringBuilder b, BaseModel model, String intermediatePackage, String fileName ) {
        String pk = model.getRootPackage() + "." + intermediatePackage;
        b.append( "\nimport javax.annotation.Generated;\n" );
        b.append( "\n@Generated( \"" ).append( pk ).append( "." ).append( fileName ).append( "\" )\n\n" );
    }

    public static boolean isStringAttr( AttributeDefinition attr ) {
        return attr.getType().getClass() == ReusableStringType.class ||
               attr.getType().getClass() == ViewStringType.class;
    }

    public static String toUpperFirstChar( String base ) {
        if ( base.length() == 1 ) return base.substring( 0, 1 ).toUpperCase();

        return base.substring( 0, 1 ).toUpperCase() + base.substring( 1 );
    }

    public static String toLowerFirstChar( String base ) {
        return base.substring( 0, 1 ).toLowerCase() + base.substring( 1 );
    }

    public static boolean isFixVersionBefore( String compareToVersion, String otherVersion ) {
        return compareToVersion.compareTo( otherVersion ) > 0;
    }

    public static DelegateType getDelegateType( EventStreamSrc src, boolean isSrcSide, boolean isSrcClient, OutboundInstruction inst ) {
        DelegateType delegate = DelegateType.None;

        if ( src == EventStreamSrc.recovery ) return DelegateType.None;

        if ( isSrcClient ) {
            if ( isSrcSide ) {
                // client side will have setters for all
            } else {
                // downstream will generally delegate to order request
                if ( inst == OutboundInstruction.delegate ) delegate = DelegateType.Delegate;
                if ( inst == OutboundInstruction.delegateGetAndSet ) delegate = DelegateType.DelegateGetAndSet;
            }
        } else { // its a message from exchange
            if ( isSrcSide ) {
                // market side will have exception setters & getters for delegate fields

                if ( inst == OutboundInstruction.delegate || inst == OutboundInstruction.clientDelegateIgnoreMktSide )
                    delegate = DelegateType.EmptyDelegateThrowException;
                else if ( inst == OutboundInstruction.clientDelegateMktSeperate )
                    delegate = DelegateType.None;

            } else { // message to client
                // delegate is to the order request base NOT the message from exchange
                if ( inst == OutboundInstruction.delegate || inst == OutboundInstruction.clientDelegateIgnoreMktSide )
                    delegate = DelegateType.Delegate;
                else if ( inst == OutboundInstruction.clientDelegateMktSeperate )
                    delegate = DelegateType.Delegate;
            }
        }

        return delegate;
    }

    public static String getEventId( ClassDefinition def ) {
        return ModelConstants.EVENT_ID_FILE_NAME + ".ID_" + def.getReusableType().toUpperCase();
    }

    public static String getFullEventId( ClassDefinition def, EventStreamSrc src ) {

        String full = "";

        if ( src == EventStreamSrc.client ) full = "CLIENT_";
        if ( src == EventStreamSrc.exchange ) full = "MARKET_";
        if ( src == EventStreamSrc.recovery ) full = "";

        return ModelConstants.FULL_EVENT_ID_FILE_NAME + ".ID_" + full + def.getReusableType().toUpperCase();
    }

    public static String getFixConstantsFile( FixModel model ) {
        return "FixMsgTypes" + model.getShortFixVersion();
    }

    public static String getBinaryConstantsFile( BinaryModel model ) {
        return model.getId() + "Codes";
    }

    public static String getFixDictionaryFile( FixModel model ) {
        return "FixDictionary" + model.getShortFixVersion();
    }

    public static void append( StringBuilder b, String includeFile ) {
        if ( includeFile != null ) {
            String contents = readFile( _includeDirectory + includeFile );

            b.append( contents );
        }
    }

    private static String readFile( String fname ) {

        File aFile = new File( fname );

        StringBuilder contents = new StringBuilder();

        try {

            try( BufferedReader input = new BufferedReader( new FileReader( aFile ) ) ) {
                String line;
                while( (line = input.readLine()) != null ) {

                    if ( line.startsWith( SOURCE ) ) {
                        String includeFile = line.substring( SOURCE.length() ).trim();

                        if ( includeFile.length() > 0 ) {
                            includeFile = _includeDirectory + includeFile;

                            if ( FileUtils.isFile( includeFile ) ) {
                                _logger.info( "ReadFile: recurse include " + includeFile );

                                contents.append( readFile( includeFile ) );
                            }
                        }
                    } else if ( line.startsWith( INCLUDE ) ) {
                        String includeFile = line.substring( INCLUDE.length() ).trim();

                        if ( includeFile.length() > 0 ) {
                            includeFile = _includeDirectory + includeFile;

                            if ( FileUtils.isFile( includeFile ) ) {
                                _logger.info( "ReadFile: recurse include " + includeFile );

                                contents.append( readFile( includeFile ) );
                            }
                        }
                    } else {
                        contents.append( line );
                        contents.append( "\n" );
                    }
                }
            }
        } catch( IOException e ) {
            _logger.warn( "Unable to read file " + fname + " : " + e.getMessage() );
            throw new RuntimeException( e );
        }

        return contents.toString();
    }

    public static String makeTypeId( TypeDefinition type, TypeEntry typeEntry ) {
        String typeName = type.getId().toUpperCase();

        return typeName + "_" + typeEntry.getInstanceName().toUpperCase();
    }

    public static int getFixedLenOverride( InternalModel internal,
                                           BinaryDictionaryTag dicEntry,
                                           AttributeDefinition attr,
                                           String tag,
                                           BinaryTagEventMapping binaryTagEventMapping,
                                           BinaryEventDefinition msgDef ) {
        int len = 0;

        if ( binaryTagEventMapping != null ) {
            int bLen = binaryTagEventMapping.getFixedWidth();
            if ( bLen > 0 ) len = bLen;
        }

        if ( len == 0 && dicEntry != null ) {
            len = dicEntry.getFixedLen();
        }

        if ( len == 0 && msgDef != null ) len = msgDef.getLen( tag );

        if ( len == 0 ) len = msgDef.getLen( tag );

        if ( len == 0 ) {
            String type = null;
            if ( dicEntry != null ) {
                type = dicEntry.getBinaryType().toString();
            }
            if ( type == null ) type = msgDef.getFillerType( tag );

            if ( "uByte".equalsIgnoreCase( type ) || "sByte".equalsIgnoreCase( type ) ) {
                len = 1;
            } else if ( "uShort".equalsIgnoreCase( type ) || "sShort".equalsIgnoreCase( type ) ) {
                len = 2;
            } else if ( "sInt".equalsIgnoreCase( type ) || "uInt".equalsIgnoreCase( type ) ) {
                len = 4;
            } else if ( "sLong".equalsIgnoreCase( type ) || "uLong".equalsIgnoreCase( type ) ) {
                len = 8;
            }
        }

        if ( attr != null && len == 0 ) {
            int aLen = internal.getDefaultSize( attr.getType().getSize() );
            if ( aLen > 0 ) len = aLen;
        }

        return len;
    }

    public static int getFixedLenOverride( BinaryDictionaryTag dicEntry, BinaryTagEventMapping binaryTagEventMapping ) {
        int len = 0;

        if ( binaryTagEventMapping != null ) {
            int bLen = binaryTagEventMapping.getFixedWidth();
            if ( bLen > 0 ) len = bLen;
        }

        if ( len == 0 && dicEntry != null ) {
            len = dicEntry.getFixedLen();
        }

        return len;
    }
}
