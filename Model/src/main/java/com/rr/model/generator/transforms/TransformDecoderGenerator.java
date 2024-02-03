/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.transforms;

import com.rr.model.base.TypeDefinition;
import com.rr.model.base.TypeTransforms;
import com.rr.model.generator.GenUtils;
import com.rr.model.generator.TypeTransform;

import java.util.Set;

public class TransformDecoderGenerator {

    public static void writeDecoderTransforms( StringBuilder b, String className, TypeTransforms def ) {
        Set<String> typeTransformIds = def.getTypeTransformIds();

        for ( String transformId : typeTransformIds ) {
            TypeTransform tr               = def.getTypeTransform( transformId );
            boolean       isExternalBinary = tr.isExternalBinaryFormat();

            if ( tr.getMaxExternalEntryValueLen() == 1 ) {
                writeSingleByteTypeTransform( b, transformId, tr, isExternalBinary );
            } else {
                writeMultiByteTypeTransform( b, transformId, tr, isExternalBinary );
            }
        }
    }

    static void writeMultiByteTypeTransform( StringBuilder b, String transformId, TypeTransform typeTransform, boolean isExternalBinary ) {
        TypeDefinition td         = typeTransform.getTypeDefinition();
        String         type       = td.getTypeDefinition();
        String         lookupVar  = "_" + GenUtils.toLowerFirstChar( transformId ) + "Map";
        String         defaultVal = getTypeInstance( td, typeTransform.getDefaultValDecode(), transformId );

        Set<String> externalVals = typeTransform.getDecodeExternalVals();
        int         size         = externalVals.size();
        int         mapSize      = size * 4;
        b.append( "    private static final Map<ViewString," ).append( type ).append( "> " ).append( lookupVar ).append( " = new HashMap<>( " ).append( mapSize ).append( ");\n" );

        b.append( "    static {\n" );

        for ( String externalVal : externalVals ) {
            String intVal = typeTransform.getInternalVal( externalVal );
            if ( intVal != null ) {
                if ( !td.containsValue( intVal ) ) {
                    throw new RuntimeException( "TypeTransform has external value of \"" + externalVal + "\" mapping to missing internal val " + intVal );
                }

                String instance = getTypeInstance( td, intVal, transformId );

                if ( isExternalBinary ) {
                    b.append( "         " ).append( lookupVar ).append( ".put( StringFactory.hexToViewString( \"" ).append( externalVal ).append( "\" ), " )
                     .append( instance ).append( " );\n" );
                } else {
                    b.append( "         " ).append( lookupVar ).append( ".put( new ViewString( \"" ).append( externalVal ).append( "\" ), " ).append( instance )
                     .append( " );\n" );
                }
            }
        }
        b.append( "    }\n\n" );

        b.append( "    private " ).append( type ).append( " transform" ).append( transformId ).append( "( byte[] buf, int offset, int len ) {\n" );
        b.append( "        _tmpLookupKey.setValue( buf, offset, len );\n" );

        b.append( "        " ).append( type ).append( " intVal = " ).append( lookupVar ).append( ".get( _tmpLookupKey );\n" );

        b.append( "        if ( intVal == null ) {\n" );
        if ( typeTransform.isRejectUnmatchedDecode() ) {
            b.append( "            throw new RuntimeDecodingException( \" unsupported decoding on " ).append( transformId )
             .append( " for value \" + _tmpLookupKey );\n" );
        } else {
            b.append( "            return " ).append( defaultVal ).append( ";\n" );
        }
        b.append( "        }\n" );

        b.append( "        return intVal;\n" );
        b.append( "    }\n\n" );
    }

    static void writeSingleByteTypeTransform( StringBuilder b, String transformId, TypeTransform typeTransform, boolean isExternalBinary ) {
        TypeDefinition td        = typeTransform.getTypeDefinition();
        String         lookupVar = "_" + GenUtils.toLowerFirstChar( transformId ) + "Map";
        String         offsetVar = "_" + GenUtils.toLowerFirstChar( transformId ) + "IndexOffset";
        String         type      = td.getTypeDefinition();

        Set<String> externalVals = typeTransform.getDecodeExternalVals();
        int         max          = 0;
        int         min          = 255;
        for ( String externalVal : externalVals ) {
            int val = (isExternalBinary) ? externalByteVal( externalVal ) : (byte) externalVal.charAt( 0 );
            if ( val > max ) max = val;
            if ( val < min ) min = val;
        }
        if ( min < 0 ) min = 0;
        int size = (max - min) + 1;

        b.append( "    private static final " ).append( type ).append( "[] " ).append( lookupVar ).append( " = new " ).append( type ).append( "[" )
         .append( size + 1 ).append( "];\n" );

        if ( isExternalBinary ) {
            b.append( "    private static final int    " ).append( offsetVar ).append( " = " ).append( min ).append( ";\n" );
        } else {
            b.append( "    private static final int    " ).append( offsetVar ).append( " = '" ).append( (char) min ).append( "';\n" );
        }

        String defaultVal = getTypeInstance( td, typeTransform.getDefaultValDecode(), transformId );

        b.append( "    static {\n" );
        b.append( "        for ( int i=0 ; i < " ).append( lookupVar ).append( ".length ; i++ ) {\n" );
        if ( typeTransform.getDefaultValDecode() == null || typeTransform.isRejectUnmatchedDecode() ) {
            b.append( "             " ).append( lookupVar ).append( "[i] = null;\n" );
        } else {
            b.append( "             " ).append( lookupVar ).append( "[i] = " ).append( defaultVal ).append( ";\n" );
        }
        b.append( "        }\n" );

        for ( String externalVal : externalVals ) {
            String intVal = typeTransform.getInternalVal( externalVal );
            if ( intVal != null ) {
                String instance = getTypeInstance( td, intVal, transformId );
                if ( isExternalBinary ) {
                    b.append( "         " ).append( lookupVar ).append( "[ (byte)" ).append( externalVal ).append( " - " ).append( offsetVar ).append( " ] = " )
                     .append( instance ).append( ";\n" );
                } else {
                    char extbVal = externalVal.charAt( 0 );
                    b.append( "         " ).append( lookupVar ).append( "[ (byte)'" ).append( extbVal ).append( "' - " ).append( offsetVar ).append( " ] = " )
                     .append( instance ).append( ";\n" );
                }
            }
        }
        b.append( "    }\n\n" );

        b.append( "    private " ).append( type ).append( " transform" ).append( transformId ).append( "( byte extVal ) {\n" );

        b.append( "        final int arrIdx = extVal - " ).append( offsetVar ).append( ";\n" );
        b.append( "        if ( arrIdx < 0 || arrIdx >= " ).append( lookupVar ).append( ".length ) {\n" );
        if ( typeTransform.isRejectUnmatchedDecode() ) {
            b.append( "            throw new RuntimeDecodingException( \" unsupported decoding on " ).append( transformId )
             .append( " for value \" + (char)extVal );\n" );
        } else {
            b.append( "            return " ).append( defaultVal ).append( ";\n" );
        }
        b.append( "        }\n" );

        b.append( "        " ).append( type ).append( " intVal = " ).append( lookupVar ).append( "[ arrIdx ];\n" );

        if ( typeTransform.isRejectUnmatchedDecode() ) {
            b.append( "        if ( intVal == null ) {\n" );
            b.append( "            throw new RuntimeDecodingException( \" unsupported decoding on " ).append( transformId )
             .append( " for value \" + (char)extVal );\n" );
            b.append( "        }\n" );
        }
        b.append( "        return intVal;\n" );
        b.append( "    }\n\n" );
    }

    private static int externalByteVal( String externalVal ) {
        return Integer.parseInt( externalVal.substring( 2 ), 16 );
    }

    static String getTypeInstance( TypeDefinition td, String intVal, String transformId ) {
        String type = td.getTypeDefinition();

        if ( td.isHandCrafted() ) {
            return intVal == null ? null : type + "." + intVal;
        }

        if ( intVal == null ) return null;

        if ( !td.containsValue( intVal ) ) {
            if ( intVal.endsWith( "Unknown" ) ) {
                return intVal;
            }

            throw new RuntimeException( "TypeTransform has internal value of \"" + intVal + "\" which is NOT valid in " + transformId );
        }

        String val = td.getInstanceName( intVal );

        return ((val == null) ? null : type + "." + val);
    }
}
