/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.transforms;

import com.rr.model.base.TypeDefinition;
import com.rr.model.base.TypeTransforms;
import com.rr.model.generator.GenUtils;
import com.rr.model.generator.TypeTransform;

import java.util.Set;

public class TransformEncoderGenerator {

    public static void writeTransformAttrs( StringBuilder b, TypeTransforms def ) {
        Set<String> typeTransformIds = def.getTypeTransformIds();

        for ( String transformId : typeTransformIds ) {
            TypeTransform tr = def.getTypeTransform( transformId );
            if ( tr.getTypeDefinition().getMaxEntryValueLen() == 1 ) {
                String tVal = (tr.isExternalBinaryFormat()) ? "(byte)" + tr.getDefaultValEncode() : "(byte)'" + tr.getDefaultValEncode() + "'";

                String defVal = (tr.getDefaultValEncode() == null) ? "0x00" : tVal;
                b.append( "    private static final byte      DEFAULT_" ).append( transformId ).append( " = " ).append( defVal ).append( ";\n" );
            }
        }
    }

    public static void writeEncoderTransforms( StringBuilder b, TypeTransforms def ) {
        Set<String> typeTransformIds = def.getTypeTransformIds();

        for ( String transformId : typeTransformIds ) {
            TypeTransform tr               = def.getTypeTransform( transformId );
            boolean       isExternalBinary = tr.isExternalBinaryFormat();

            if ( tr.getTypeDefinition().getMaxEntryValueLen() == 1 ) {
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
        String         defaultVar = "_" + GenUtils.toLowerFirstChar( transformId ) + "Default";

        Set<String> internalVals = typeTransform.getEncodeInternalVals();
        int         size         = internalVals.size();
        int         mapSize      = size * 4;
        b.append( "    private static final Map<" ).append( type ).append( ", ViewString> " ).append( lookupVar ).append( " = new HashMap<>( " ).append( mapSize ).append( " );\n" );
        b.append( "    private static final ViewString " ).append( defaultVar ).append( " = new ViewString( \"" ).append( typeTransform.getDefaultValEncode() ).append( "\" );\n\n" );

        b.append( "    static {\n" );

        for ( String internalVal : internalVals ) {
            if ( !td.containsValue( internalVal ) ) {
                throw new RuntimeException( "TypeTransform has internal value of \"" + internalVal + "\" which is NOT used in " + type );
            }

            String extVal = typeTransform.getExternalVal( internalVal );
            if ( extVal != null ) {
                if ( isExternalBinary ) {
                    b.append( "         " ).append( lookupVar ).append( ".put( " ).append( type ).append( ".getVal( \"" ).append( internalVal )
                     .append( "\".getBytes() ), StringFactory.hexToViewString( \"" ).append( extVal ).append( "\" ) );\n" );
                } else {
                    b.append( "         " ).append( lookupVar ).append( ".put( " ).append( type ).append( ".getVal( new ViewString( \"" ).append( internalVal )
                     .append( "\" ) ), new ViewString( \"" ).append( extVal ).append( "\" ) );\n" );
                }
            }
        }

        b.append( "    }\n\n" );

        b.append( "    private ViewString transform" ).append( transformId ).append( "( " ).append( type ).append( " intVal ) {\n" );

        b.append( "        ViewString extVal = " ).append( lookupVar ).append( ".get( intVal );\n" );

        b.append( "        if ( extVal == null ) {\n" );
        if ( typeTransform.isRejectUnmatchedEncode() ) {
            b.append( "            throw new RuntimeEncodingException( \" unsupported encoding on " ).append( transformId )
             .append( " for value \" + intVal );\n" );
        } else {
            b.append( "            return " ).append( defaultVar ).append( ";\n" );
        }
        b.append( "        }\n" );

        b.append( "        return extVal;\n" );
        b.append( "    }\n\n" );
    }

    static void writeSingleByteTypeTransform( StringBuilder b, String transformId, TypeTransform typeTransform, boolean isExternalBinary ) {
        TypeDefinition td = typeTransform.getTypeDefinition();

        Set<String> internalVals = typeTransform.getEncodeInternalVals();

        TypeDefinition typeDef = typeTransform.getTypeDefinition();
        String         type    = typeDef.getId();
        b.append( "    private byte transform" ).append( transformId ).append( "( " ).append( type ).append( " val ) {\n" );

        b.append( "        switch( val ) {\n" );
        for ( String internalVal : internalVals ) {
            if ( !td.containsValue( internalVal ) ) {
                throw new RuntimeException( "TypeTransform has internal value of \"" + internalVal + "\" which is NOT used in " + transformId );
            }
            String typeVal = typeDef.getInstanceName( internalVal );
            String extVal  = typeTransform.getExternalVal( internalVal );
            String comment = typeTransform.getComment( internalVal );

            if ( extVal != null ) {
                b.append( "        case " ).append( typeVal ).append( ":" );
                if ( comment != null ) {
                    b.append( "  // " ).append( comment );
                }
                b.append( "\n" );
                if ( isExternalBinary ) {
                    b.append( "            return (byte)" ).append( extVal ).append( ";\n" );
                } else {
                    char extbVal = extVal.charAt( 0 );
                    b.append( "            return (byte)'" ).append( extbVal ).append( "';\n" );
                }
            }
        }
        b.append( "        default:\n" );
        b.append( "            break;\n" );
        b.append( "        }\n" );

        if ( typeTransform.isRejectUnmatchedEncode() ) {
            b.append( "        throw new RuntimeEncodingException( \" unsupported encoding on " ).append( transformId ).append( " for value \" + val );\n" );
        } else {
            String defaultVal = (typeTransform.getDefaultValEncode() != null) ? typeTransform.getDefaultValEncode() : "Constants.UNSET_BYTE";
            if ( isExternalBinary ) {
                b.append( "        return " ).append( defaultVal ).append( ";\n" );
            } else {
                b.append( "        return '" ).append( defaultVal ).append( "';\n" );
            }
        }

        b.append( "    }\n\n" );
    }
}
