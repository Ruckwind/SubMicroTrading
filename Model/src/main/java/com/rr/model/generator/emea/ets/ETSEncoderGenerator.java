/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.ets;

import com.rr.model.base.*;
import com.rr.model.base.type.*;
import com.rr.model.generator.GenUtils;
import com.rr.model.generator.TypeTransform;
import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

import java.util.Map;

public class ETSEncoderGenerator extends BaseBinaryEncoderGenerator {

    public ETSEncoderGenerator() {
        super();
    }

    @Override
    protected void addEncoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        super.addEncoderImports( b, def );

        b.append( "import com.rr.model.generated.model.defn.ETSEurexCodes;\n" );
        b.append( "import com.rr.codec.emea.exchange.ets.EncodingID;\n" );
    }

    @Override
    protected void declareBuilder( StringBuilder b ) {
        b.append( "\n    private final " ).append( getBuilder() ).append( " _builder;\n\n" );
    }

    @Override
    protected void encode( BinaryEventMap map ) {
        ClassDefinition event = map.getClassDefinition();

        String type   = event.getId();
        String method = "encode" + type;

        _b.append( "\n    public final void " ).append( method ).append( "( final " ).append( type ).append( " msg ) {\n" );

        Map<String, EncodeEntry> entries = getEntriesByTag( map );

        Map<HookType, String> hooks      = map.getHooks();
        String                encodeHook = hooks.get( HookType.encode );

        if ( writeHook( encodeHook ) == false ) {
            String preHook  = hooks.get( HookType.preencode );
            String postHook = hooks.get( HookType.postencode );

            _b.append( "        final long now = _tzCalculator.getNowAsInternalTime();\n" );

            _b.append( "        int endDataIdx = _builder.getCurrentIndex();\n" );

            writeHook( preHook );

            for ( Map.Entry<String, EncodeEntry> entry : entries.entrySet() ) {

                String      tag         = entry.getKey();
                EncodeEntry encodeEntry = entry.getValue();

                if ( addHook( HookType.encode, "        ", encodeEntry ) ) {
                    continue;
                }

                addHook( HookType.preencode, "        ", encodeEntry );

                encode( "msg", map, "        ", tag, encodeEntry );

                addHook( HookType.postencode, "        ", encodeEntry );
            }

            writeHook( postHook );

            String msgEncodingId = "ETSEurexCodes." + map.getBinaryMsgId();

            _b.append( "        encodeRecordHeader( " ).append( msgEncodingId ).append( ", endDataIdx );\n" );
        }

        _b.append( "    }\n" );
    }

    @Override
    protected void encode( String var, BinaryEventMap map, String spaces, String tag, EncodeEntry value ) {
        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        String                commentEnd            = "\n";
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryEventDefinition msgDef                = map.getEventDefinition();
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        if ( attr == null ) {
            return;
        }

        String field = (binaryTagEventMapping == null) ? GenUtils.toUpperFirstChar( tag )
                                                       : GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );

        String constId = getFieldConst( dictTag );

        if ( attr.isPrimitive() ) {
            if ( GenUtils.isStringAttr( attr ) ) {
                int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );
                if ( extType == BinaryType.fstr ) {
                    _b.append( "        _builder.encodeStringFixedWidth( " ).append( constId ).append( ", msg.get" ).append( field ).append( "(), " )
                      .append( len ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( "        _builder.encodeZStringFixedWidth( " ).append( constId ).append( ", msg.get" ).append( field ).append( "(), " )
                      .append( len ).append( " );" );
                } else if ( extType == BinaryType.data ) {
                    _b.append( "        _builder.encodeData( " ).append( constId ).append( ", msg.get" ).append( field ).append( "(), " ).append( len )
                      .append( " );" );
                } else if ( extType == BinaryType.sLong ) {
                    _b.append( "        _builder.encodeStringAsLong( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                } else if ( len > 0 ) {
                    _b.append( "        _builder.encodeString( " ).append( constId ).append( ", msg.get" ).append( field ).append( "(), " ).append( len )
                      .append( " );" );
                } else {
                    _b.append( "        _builder.encodeString( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == CharType.class ) {
                _b.append( "        _builder.encodeByte( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == ShortType.class ) {
                _b.append( "        _builder.encodeInt( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == SendTimeUTCType.class ) {
                _b.append( "        _builder.encodeTimestampUTC( " ).append( constId ).append( ", now );" );
            } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                if ( extType == BinaryType.timeLocal ) {
                    _b.append( "        _builder.encodeTimeLocal( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                } else if ( extType == BinaryType.nowUTC ) {
                    _b.append( "        _builder.encodeTimestampUTC( " ).append( constId ).append( ", now );" );
                } else if ( extType == BinaryType.timeUTC ) {
                    _b.append( "        _builder.encodeTimeUTC( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                } else if ( extType == BinaryType.timestampLocal ) {
                    _b.append( "        _builder.encodeTimestampLocal( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                } else {
                    _b.append( "        _builder.encodeTimestampUTC( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == DateType.class ) {
                _b.append( "        _builder.encodeDate( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == LongType.class ) {
                _b.append( "        _builder.encodeLong( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == IntType.class ) {
                _b.append( "        _builder.encodeInt( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == FloatType.class ) {
                _b.append( "        _builder.encodePrice( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == DoubleType.class ) {
                _b.append( "        _builder.encodePrice( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == BooleanType.class ) {
                _b.append( "        _builder.encodeBool( " ).append( constId ).append( ", msg.get" ).append( field ).append( "() );" );
            } else {
                throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                            attr.getAttrName() );
            }

        } else {
            TypeDefinition defn      = _internal.getTypeDefinition( attr.getTypeId() );
            TypeTransform  transform = _def.getTypeTransform( attr.getTypeId() );

            int maxInternalSize = defn.getMaxEntryValueLen();
            int fixedLen        = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

            if ( transform == null ) {
                encodeType( var, map, spaces, tag, field, defn, dictTag, maxInternalSize, fixedLen );
            } else {
                encodeTypeTransform( var, map, spaces, tag, field, dictTag, defn, maxInternalSize, transform, fixedLen );
            }
        }

        _b.append( commentEnd );
    }

    @Override
    protected void encodeType( String msgVar,
                               BinaryEventMap map,
                               String spaces,
                               String tag,
                               String field,
                               TypeDefinition defn,
                               BinaryDictionaryTag dictTag,
                               int maxSize,
                               int fixedLen ) {

        String constId = getFieldConst( dictTag );

        if ( map.getEventDefinition().isMandatory( tag ) ) {
            if ( maxSize == 1 ) {
                _b.append( "        _builder.encodeByte( " ).append( constId ).append( " msg.get" ).append( field ).append( "().getVal() );" );
            } else {
                _b.append( "        _builder.encodeBytes( " ).append( constId ).append( " msg.get" ).append( field ).append( "().getVal() );" );
            }

        } else {
            String var    = "t" + field;
            String varBin = var + "Bytes";

            _b.append( "        final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = msg.get" ).append( field )
              .append( "();\n" );

            if ( maxSize == 1 ) {
                _b.append( "        final byte " ).append( varBin ).append( " = ( " ).append( var ).append( " != null ) ? " ).append( var )
                  .append( ".getVal() : 0x00;\n" );
                _b.append( "        _builder.encodeByte( " ).append( constId ).append( " " ).append( varBin ).append( " );" );
            } else {
                _b.append( "        final byte[] " ).append( varBin ).append( " = ( " ).append( var ).append( " != null ) ? " ).append( var )
                  .append( ".getVal() : null;\n" );
                _b.append( "        if ( " ).append( var ).append( " != null ) _builder.encodeBytes( " ).append( constId ).append( " " ).append( var )
                  .append( ".getVal() );" );
            }
        }
    }

    @Override
    protected void encodeTypeTransform( String msgVar, BinaryEventMap map, String spaces, String tag, String field, BinaryDictionaryTag dictTag, TypeDefinition defn, int maxSize, TypeTransform transform, int fixedLen ) {
        String constId = getFieldConst( dictTag );

        if ( addHook( HookType.encode, "    ", transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.preencode, "    ", transform.getHooks() );

        if ( map.getEventDefinition().isMandatory( tag ) ) {
            if ( maxSize == 1 ) {
                _b.append( "        _builder.encodeByte( " ).append( constId ).append( " transform" ).append( field ).append( "( msg.get" ).append( field )
                  .append( "().getVal() ) );" );
            } else {
                _b.append( "        _builder.encodeString( " ).append( constId ).append( " transform" ).append( field ).append( "( msg.get" ).append( field )
                  .append( "() ) );" );
            }
        } else {
            String var = "t" + field;

            _b.append( "        final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( "Base = msg.get" ).append( field )
              .append( "();\n" );
            if ( maxSize == 1 ) {
                String defVal = transform.getDefaultValEncode() == null ? "0x00" : " DEFAULT_" + transform.getId();
                _b.append( "        final byte " ).append( var ).append( " = ( " ).append( var ).append( "Base == null ) ? " ).append( defVal )
                  .append( " : transform" ).append( field ).append( "( " ).append( var ).append( "Base.getVal() );\n" );
                _b.append( "        _builder.encodeByte( " ).append( constId ).append( " " ).append( var ).append( " );" );
            } else {
                _b.append( "        final ViewString " ).append( var ).append( " = transform" ).append( field ).append( "( msg.get" ).append( field )
                  .append( "() );\n" );
                _b.append( "        if ( " ).append( var ).append( " != null ) _builder.encodeString( " ).append( constId ).append( " " ).append( var )
                  .append( " );" );
            }
        }

        addHook( HookType.postencode, "    ", transform.getHooks() );
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.ets.ETSEncodeBuilderImpl";
    }

    @Override
    protected void writeEncodeMethod( StringBuilder b, String className, BinaryCodecDefinition def ) {
        // nothing as hand coded
    }

    @Override
    protected void writeEncoderConstructors( String className, StringBuilder b, BinaryCodecDefinition def ) {

        b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );

        b.append( "        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {\n" );
        b.append( "            throw new RuntimeException( \"Encode buffer too small only \" + buf.length + \", min=\" + SizeType.MIN_ENCODE_BUFFER.getSize() );\n" );
        b.append( "        }\n" );

        b.append( "        _offset = offset;\n" );
        b.append( "        _buf = buf;\n" );
        b.append( "        _binaryVersion   = new ViewString( \"" ).append( _binaryModel.getBinaryVersion() ).append( "\");\n" );
        b.append( "        _builder = new " ).append( getBuilder() ).append( "( buf, offset, _binaryVersion );\n" );
        b.append( "    }\n\n" );
    }

    @Override
    protected void writeSetBuilder( StringBuilder b ) {
        // no debug version for ETS as has non standard decoder
        // would need to create ETSDecodeBuilder and extend the debug proxy
        b.append( "    private void setBuilder() {\n" );
        b.append( "        // disable builder cannot be changed for ETS\n" );
        b.append( "    }\n\n" );
    }

    private String getFieldConst( BinaryDictionaryTag dictTag ) {
        String constFile = GenUtils.getBinaryConstantsFile( _binaryModel );
        String constId   = constFile + "." + GenUtils.toUpperFirstChar( dictTag.getId() ) + "Code";
        return constId;
    }
}
