/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.binary;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import com.rr.model.base.type.*;
import com.rr.model.generator.GenUtils;
import com.rr.model.generator.InternalModelGenerator;
import com.rr.model.generator.ModelConstants;
import com.rr.model.generator.TypeTransform;
import com.rr.model.generator.transforms.TransformEncoderGenerator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class BaseBinaryEncoderGenerator implements EncoderGenerator {

    @SuppressWarnings( "unused" )
    private static final Logger _logger = Logger.getLogger( "BaseBinaryEncoderGenerator" );

    public static class EncodeEntry {

        public final AttributeDefinition   _attrDef;
        public final BinaryTagEventMapping _binaryTagEventMapping;
        public final BinaryEventDefinition _msgDef;
        final        String                _tag;
        private      boolean               _repeatingGroup;

        EncodeEntry( String tag, AttributeDefinition attrDef, BinaryTagEventMapping binaryTagEventMapping, BinaryEventDefinition msgDef ) {
            _tag                   = tag;
            _attrDef               = attrDef;
            _binaryTagEventMapping = binaryTagEventMapping;
            _msgDef                = msgDef;
        }

        @Override
        public int hashCode() {
            final int prime  = 31;
            int       result = 1;
            result = prime * result + ((_tag == null) ? 0 : _tag.hashCode());
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            EncodeEntry other = (EncodeEntry) obj;
            if ( _tag == null ) {
                return other._tag == null;
            } else return _tag.equals( other._tag );
        }

        final boolean isRepeatingGroup() {
            return _repeatingGroup;
        }

        public void setRepeatingGroup( boolean repeatingGrp ) {
            _repeatingGroup = repeatingGrp;
        }
    }
    private final Set<String> _ignoreMktVars = new LinkedHashSet<>();
    protected InternalModel    _internal;
    protected StringBuilder         _b;
    protected BinaryCodecDefinition _def;
    protected BinaryModel           _binaryModel;
    private   BinaryCodecModel _codecModel;
    private BinaryCodecDefinition _codecDef;

    public BaseBinaryEncoderGenerator() {
        // @TODO flag these in the model dict
        _ignoreMktVars.add( "msgStart" );
        _ignoreMktVars.add( "msgType" );
        _ignoreMktVars.add( "protocolVersion" );
        _ignoreMktVars.add( "msgLen" );
        _ignoreMktVars.add( "etx" );
    }

    @Override
    public void generate( InternalModel internal,
                          BinaryCodecModel codecModel,
                          BinaryCodecDefinition def,
                          BinaryModel binModel ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        _codecModel  = codecModel;
        _internal    = internal;
        _binaryModel = binModel;
        _codecDef    = def;

        String className = def.getId() + "Encoder";

        File file = GenUtils.getJavaFile( _codecModel, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _codecModel, ModelConstants.CODEC_PACKAGE, className );

        addEncoderImports( b, def );

        b.append( "\n@SuppressWarnings( {\"unused\", \"cast\"} )\n" );

        b.append( "\npublic final class " ).append( className );

        String interfaceName = getInterfaceToImplement();

        if ( interfaceName != null && interfaceName.length() > 0 ) {
            b.append( " implements " ).append( interfaceName );
        }
        b.append( " {\n" );

        _b   = b;
        _def = def;

        b.append( "\n   // Member Vars\n" );
        writeAttrs( b, def );

        b.append( "\n   // Constructors\n" );
        writeEncoderConstructors( className, b, def );

        b.append( "\n   // encode methods\n" );
        writeEncoderMethods( b, className, def );

        writeOtherMethods( b, def );

        GenUtils.append( b, def.getEncodeInclude() );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    protected void addEncoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        b.append( "import java.util.HashMap;\n" );
        b.append( "import java.util.Map;\n" );

        b.append( "import com.rr.core.lang.*;\n" );

        b.append( "import com.rr.core.utils.*;\n" );
        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.core.pool.SuperPool;\n" );
        b.append( "import com.rr.core.codec.BinaryEncoder;\n" );
        b.append( "import " ).append( getBuilder() ).append( ";\n" );
        b.append( "import com.rr.core.codec.binary.BinaryEncodeBuilder;\n" );
        b.append( "import com.rr.core.codec.binary.DebugBinaryEncodeBuilder;\n" );

        b.append( "import com.rr.core.codec.RuntimeEncodingException;\n" );

        b.append( "import com.rr.model.internal.type.*;\n" );

        InternalModelGenerator.addInternalEventsFactoryWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsImplWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsInterfacesWildImport( b, _internal );
        InternalModelGenerator.addInternalTypeWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreSizeTypeImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreEventIdsImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreFullEventIdsImport( b, _internal );
    }

    protected boolean addHook( HookType hookType, String spaces, EncodeEntry value ) {
        BinaryTagEventMapping explicitMapping = value._binaryTagEventMapping;
        Map<HookType, String> hooks;

        boolean added = false;

        if ( explicitMapping != null ) {
            hooks = explicitMapping.getHooks();

            if ( hooks != null ) {
                String code = hooks.get( hookType );

                if ( code != null && code.length() > 0 ) {
                    _b.append( "        if ( _debug ) _dump.append( \"\\nHook : \" ).append( \"" ).append( value._tag )
                      .append( "\" ).append( \" : \" ).append( \"" ).append( hookType ).append( "\" ).append( \" : \" );\n" );

                    _b.append( spaces ).append( code ).append( ";\n" );

                    added = true;
                }
            }
        }

        return added;
    }

    protected boolean addHook( HookType hookType, String spaces, Map<HookType, String> hooks ) {

        boolean added = false;

        if ( hooks != null ) {
            String decode = hooks.get( hookType );

            if ( decode != null && decode.length() > 0 ) {
                _b.append( spaces ).append( "    " ).append( decode ).append( ";\n" );

                added = true;
            }
        }

        return added;
    }

    protected void declareBuilder( StringBuilder b ) {
        b.append( "\n    private BinaryEncodeBuilder     _builder;\n\n" );
    }

    protected void encode( BinaryEventMap map ) {
        ClassDefinition event = map.getClassDefinition();

        String type   = event.getId();
        String method = getEncodeMethodName( map );

        _b.append( "\n    public final void " ).append( method ).append( "( final " ).append( type ).append( " msg ) {\n" );

        Map<String, EncodeEntry> entries = getEntriesByTag( map );

        Map<HookType, String> hooks      = map.getHooks();
        String                encodeHook = hooks.get( HookType.encode );

        if ( writeHook( encodeHook ) == false ) {
            encodeMethodPrehook( map, type, hooks );

            for ( Map.Entry<String, EncodeEntry> entry : entries.entrySet() ) {
                encodeField( map, entry );
            }

            encodeMethodPostHook( hooks );
        }

        _b.append( "    }\n" );
    }

    protected void encode( String var, BinaryEventMap map, String spaces, String tag, EncodeEntry value ) {
        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        String                commentEnd            = "\n";
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        String field = null;
        if ( binaryTagEventMapping != null && binaryTagEventMapping.getEventAttr() != null && binaryTagEventMapping.getEventAttr().length() > 0 ) field = GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );
        if ( field == null ) field = GenUtils.toUpperFirstChar( tag );

        _b.append( spaces ).append( "if ( _debug ) _dump.append( \"\\nField: \" ).append( \"" ).append( tag ).append( "\" ).append( \" : \" );\n" );

        String unsigned = BinaryType.isUnsignedNumber( extType ) ? "U" : "";

        if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) ) {
            int    len        = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );
            String fillerType = value._msgDef.getFillerType( tag );
            if ( "uByte".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeUByte( Constants.UNSET_BYTE );" );
            } else if ( "sByte".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeByte( Constants.UNSET_BYTE );" );
            } else if ( "uShort".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeUShort( Constants.UNSET_SHORT );" );
            } else if ( "sShort".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeShort( Constants.UNSET_SHORT );" );
            } else if ( "sInt".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeInt( Constants.UNSET_INT );" );
            } else if ( "uInt".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeUInt( Constants.UNSET_INT );" );
            } else if ( "sLong".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeLong( Constants.UNSET_LONG );" );
            } else if ( "uLong".equalsIgnoreCase( fillerType ) ) {
                _b.append( spaces ).append( "_builder.encodeULong( Constants.UNSET_LONG );" );
            } else {
                _b.append( spaces ).append( "_builder.encodeFiller( " ).append( len ).append( " );" );
            }
        } else if ( value.isRepeatingGroup() ) {

            repeatingGroup( var, map, spaces + "", tag, value, value._msgDef );

        } else if ( attr == null ) {
            encodeFiller( map, spaces, tag, value );
        } else if ( attr.isPrimitive() ) {
            if ( GenUtils.isStringAttr( attr ) ) {
                int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.encodeStringFixedWidth( " ).append( var ).append( ".get" ).append( field ).append( "(), " )
                      .append( len ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.encodeZStringFixedWidth( " ).append( var ).append( ".get" ).append( field ).append( "(), " )
                      .append( len ).append( " );" );
                } else if ( extType == BinaryType.data ) {
                    _b.append( spaces ).append( "_builder.encodeData( " ).append( var ).append( ".get" ).append( field ).append( "(), " ).append( len )
                      .append( " );" );
                } else if ( extType == BinaryType.sLong || extType == BinaryType.uLong ) {
                    _b.append( spaces ).append( "_builder.encodeStringAsLong( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else if ( extType == BinaryType.sInt || extType == BinaryType.uInt ) {
                    _b.append( spaces ).append( "_builder.encodeStringAsInt( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else if ( len > 0 ) {
                    _b.append( spaces ).append( "_builder.encodeString( " ).append( var ).append( ".get" ).append( field ).append( "(), " ).append( len )
                      .append( " );" );
                } else {
                    _b.append( spaces ).append( "_builder.encodeString( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == CharType.class ) {
                if ( extType == BinaryType.ch ) {
                    _b.append( spaces ).append( "_builder.encodeChar( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else {
                    _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Byte( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == SendTimeUTCType.class ) {
                _b.append( spaces ).append( "_builder.encodeTimestampUTC( now );" );
            } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                if ( extType == BinaryType.timeLocal ) {
                    _b.append( spaces ).append( "_builder.encodeTimeLocal( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else if ( extType == BinaryType.nowUTC ) {
                    _b.append( spaces ).append( "_builder.encodeTimestampUTC( now );" );
                } else if ( extType == BinaryType.timeUTC ) {
                    _b.append( spaces ).append( "_builder.encodeTimeUTC( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else if ( extType == BinaryType.timestampLocal ) {
                    _b.append( spaces ).append( "_builder.encodeTimestampLocal( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else {
                    _b.append( spaces ).append( "_builder.encodeTimestampUTC( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == DateType.class ) {
                _b.append( spaces ).append( "_builder.encodeDate( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == ShortType.class ) {
                encodeWholeNumber( attr, var, spaces, extType, field, unsigned );
            } else if ( attr.getType().getClass() == IntType.class ) {
                encodeWholeNumber( attr, var, spaces, extType, field, unsigned );
            } else if ( attr.getType().getClass() == LongType.class ) {
                if ( extType == BinaryType.base36 ) {
                    int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );
                    _b.append( spaces ).append( "_builder.encodeBase36Number( " ).append( var ).append( ".get" ).append( field ).append( "(), " ).append( len ).append( " );" );
                } else {
                    encodeWholeNumber( attr, var, spaces, extType, field, unsigned );
                }
            } else if ( attr.getType().getClass() == FloatType.class ) {
                _b.append( spaces ).append( "_builder.encodeDecimal( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == DoubleType.class ) {

                if ( extType == BinaryType.base36 ) {
                    int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );
                    _b.append( spaces ).append( "_builder.encodeBase36Number( (long)" ).append( var ).append( ".get" ).append( field ).append( "(), " ).append( len ).append( " );" );
                } else if ( BinaryType.isWholeNumber( extType ) ) {
                    encodeWholeNumber( attr, var, spaces, extType, field, unsigned );
                } else {
                    _b.append( spaces ).append( "_builder.encodeDecimal( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                }
            } else if ( attr.getType().getClass() == BooleanType.class ) {
                _b.append( spaces ).append( "_builder.encodeBool( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else {
                throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                            attr.getAttrName() );
            }

        } else {
            TypeDefinition defn      = _internal.getTypeDefinition( attr.getTypeId() );
            TypeTransform  transform = _def.getTypeTransform( attr.getTypeId() );

            int maxInternalSize = defn.getMaxEntryValueLen();
            int fixedLen        = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );

            if ( transform == null ) {
                encodeType( var, map, spaces, tag, field, defn, dictTag, maxInternalSize, fixedLen );
            } else {
                encodeTypeTransform( var, map, spaces, tag, field, dictTag, defn, maxInternalSize, transform, fixedLen );
            }
        }

        _b.append( commentEnd );
    }

    protected void encodeRootWithBlockSkipCheck( BinaryEventMap map, int blockLen ) {
        ClassDefinition event = map.getClassDefinition();

        String type   = event.getId();
        String method = getEncodeMethodName( map );

        _b.append( "\n    public final void " ).append( method ).append( "( final " ).append( type ).append( " msg ) {\n" );

        Map<String, EncodeEntry> entries = getEntriesByTag( map );
        String                   spaces  = "        ";

        startRootBlock( spaces, blockLen );

        boolean repeatingGroups = false;

        Map<HookType, String> hooks      = map.getHooks();
        String                encodeHook = hooks.get( HookType.encode );

        if ( writeHook( encodeHook ) == false ) {
            encodeMethodPrehook( map, type, hooks );

            Map.Entry<String, EncodeEntry>           entry         = null;
            Iterator<Map.Entry<String, EncodeEntry>> entryIterator = entries.entrySet().iterator();

            while( entryIterator.hasNext() ) {
                entry = entryIterator.next();
                String tag = entry.getKey();

                if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                    repeatingGroups = true;
                    break; // WILL PROCESS SUBGROUPS LATER
                }

                encodeField( map, entry );
            }

            endRootBlock( spaces, blockLen );

            if ( repeatingGroups ) {
                boolean first = true;
                do {
                    if ( first ) {
                        first = false; // already have repeating group entry
                    } else {
                        entry = entryIterator.next();
                    }

                    @SuppressWarnings( "null" )
                    String tag = entry.getKey();

                    if ( !map.getEventDefinition().isRepeatingGroup( tag ) ) {
                        throw new RuntimeException( "Message " + map.getBinaryMsgId() + ", BlockSize's in use, repeating groups must come together at end of message, invalidField=" + tag );
                    }

                    encodeField( map, entry );

                } while( entryIterator.hasNext() );
            }

            encodeMethodPostHook( hooks );
        } else {
            endBlock( spaces, blockLen );
        }

        _b.append( "    }\n" );
    }

    protected void encodeType( String var,
                               BinaryEventMap map,
                               String spaces,
                               String tag,
                               String field,
                               TypeDefinition defn,
                               BinaryDictionaryTag dictTag,
                               int maxSize,
                               int fixedLen ) {

        if ( map.getEventDefinition().isMandatory( tag ) ) {
            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "_builder.encodeByte( " ).append( var ).append( ".get" ).append( field ).append( "().getVal() );" );
                if ( fixedLen > 1 ) _b.append( "\n        _builder.encodeFiller( " ).append( fixedLen - 1 ).append( " );" );
            } else {
                BinaryType extType = (dictTag != null) ? dictTag.getBinaryType() : null;
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.encodeStringFixedWidth( " ).append( var ).append( ".get" ).append( field )
                      .append( "().getVal(), 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.encodeZStringFixedWidth( " ).append( var ).append( ".get" ).append( field )
                      .append( "().getVal(), 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.data ) {
                    _b.append( spaces ).append( "_builder.encodeData( " ).append( var ).append( ".get" ).append( field ).append( "().getVal(), 0, " )
                      .append( fixedLen ).append( " );" );
                } else {
                    _b.append( spaces ).append( "_builder.encodeBytes( " ).append( var ).append( ".get" ).append( field ).append( "().getVal() );" );
                }
            }

        } else {
            String tvar   = "t" + field;
            String varBin = tvar + "Bytes";

            _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( tvar ).append( " = " ).append( var )
              .append( ".get" ).append( field ).append( "();\n" );

            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "final byte " ).append( varBin ).append( " = ( " ).append( tvar ).append( " != null ) ? " ).append( tvar )
                  .append( ".getVal() : 0x00;\n" );
                _b.append( spaces ).append( "_builder.encodeByte( " ).append( varBin ).append( " );" );
            } else {
                _b.append( spaces ).append( "final byte[] " ).append( varBin ).append( " = ( " ).append( tvar ).append( " != null ) ? " ).append( tvar )
                  .append( ".getVal() : null;\n" );
                BinaryType extType = (dictTag != null) ? dictTag.getBinaryType() : null;
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.encodeStringFixedWidth( " ).append( varBin ).append( ", 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.encodeZStringFixedWidth( " ).append( varBin ).append( ", 0, " ).append( fixedLen ).append( " );" );
                } else {
                    _b.append( spaces ).append( "if ( " ).append( tvar ).append( " != null ) _builder.encodeBytes( " ).append( tvar ).append( ".getVal() );" );
                }
            }
        }
    }

    protected void encodeTypeTransform( String var,
                                        BinaryEventMap map,
                                        String spaces,
                                        String tag,
                                        String field,
                                        BinaryDictionaryTag dictTag,
                                        TypeDefinition defn,
                                        int maxSize,
                                        TypeTransform transform,
                                        int fixedLen ) {

        if ( addHook( HookType.encode, spaces, transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.preencode, spaces, transform.getHooks() );

        boolean srcFieldMand = map.getClassDefinition().isAttrMandatory( tag );

        if ( map.getEventDefinition().isMandatory( tag ) && srcFieldMand ) {
            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "_builder.encodeByte( transform" ).append( defn.getId() ).append( "( " ).append( var ).append( ".get" )
                  .append( field ).append( "() ) );" );
                if ( fixedLen > 1 ) _b.append( "\n        _builder.encodeFiller( " ).append( fixedLen - 1 ).append( " );" );
            } else {
                BinaryType extType = (dictTag != null) ? dictTag.getBinaryType() : null;
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.encodeStringFixedWidth( transform" ).append( defn.getId() ).append( "( " ).append( var )
                      .append( ".get" ).append( field ).append( "() ), 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.encodeZStringFixedWidth( transform" ).append( defn.getId() ).append( "( " ).append( var )
                      .append( ".get" ).append( field ).append( "() ), 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.data ) {
                    _b.append( spaces ).append( "_builder.encodeData( transform" ).append( defn.getId() ).append( "( " ).append( var ).append( ".get" )
                      .append( field ).append( "() ), 0, " ).append( fixedLen ).append( " );" );
                } else {
                    _b.append( spaces ).append( "_builder.encodeString( transform" ).append( defn.getId() ).append( "( " ).append( var ).append( ".get" )
                      .append( field ).append( "() ) );" );
                }
            }
        } else {
            String tvar = "t" + field;

            _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( tvar ).append( "Base = " ).append( var )
              .append( ".get" ).append( field ).append( "();\n" );
            if ( maxSize == 1 ) {
                String defVal = transform.getDefaultValEncode() == null ? "Constants.UNSET_BYTE" : " DEFAULT_" + transform.getId();
                _b.append( spaces ).append( "final byte " ).append( tvar ).append( " = ( " ).append( tvar ).append( "Base == null ) ? " ).append( defVal )
                  .append( " : transform" ).append( defn.getId() ).append( "( " ).append( tvar ).append( "Base );\n" );
                _b.append( spaces ).append( "_builder.encodeByte( " ).append( tvar ).append( " );" );
                if ( fixedLen > 1 ) _b.append( "\n        _builder.encodeFiller( " ).append( fixedLen - 1 ).append( " );" );
            } else {
                _b.append( spaces ).append( "final ViewString " ).append( tvar ).append( " = transform" ).append( defn.getId() ).append( "( " ).append( var )
                  .append( ".get" ).append( field ).append( "() );\n" );
                BinaryType extType = (dictTag != null) ? dictTag.getBinaryType() : null;
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.encodeStringFixedWidth( " ).append( tvar ).append( ", 0, " ).append( fixedLen ).append( " );" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.encodeZStringFixedWidth( " ).append( tvar ).append( ", 0, " ).append( fixedLen ).append( " );" );
                } else {
                    _b.append( spaces ).append( "if ( " ).append( tvar ).append( " != null ) _builder.encodeString( " ).append( tvar ).append( " );" );
                }
            }
        }

        addHook( HookType.postencode, spaces, transform.getHooks() );
    }

    protected void encoderStart( BinaryEventMap map, String type ) {
        if ( useMsgTypesForStartEncode( map ) ) {
            _b.append( "        _builder.start( MSG_" ).append( map.getBinaryMsgId() ).append( " );\n" );
        } else {
            _b.append( "        _builder.start();\n" );
        }

        _b.append( "        if ( _debug ) {\n" );
        _b.append( "            _dump.append( \"  encodeMap=\" ).append( \"" ).append( map.getBinaryMsgId() )
          .append( "\" ).append( \"  eventType=\" ).append( \"" ).append( type ).append( "\" ).append( \" : \" );\n" );
        _b.append( "        }\n\n" );

    }

    /**
     * @return FULL package and className for encode builder
     */
    protected abstract String getBuilder();

    protected String getDebugBuilder() {
        return "DebugBinaryEncodeBuilder<>";
    }

    protected Map<String, EncodeEntry> getEntriesByTag( BinaryEventMap map ) {
        Map<String, EncodeEntry> entries = new LinkedHashMap<>();

        BinaryModel binaryModel = _binaryModel;

        if ( map == null ) {
            throw new RuntimeException( "Null binary message map" );
        }

        ClassDefinition event = map.getClassDefinition();

        Map<String, Boolean> tags = binaryModel.getTagMap( map, true, true );

        addTags( map, entries, binaryModel, event, tags, map.getEventDefinition() );

        return entries;
    }

    protected String getInterfaceToImplement() {
        return "BinaryEncoder";
    }

    protected int getPriceSize() {
        return 4;
    }

    protected int getTimestampSize() {
        return 4;
    }

    protected boolean useMsgTypesForStartEncode( BinaryEventMap map ) {
        String msgType = map.getEventDefinition().getMsgType();

        return msgType.length() == 1;
    }

    protected void writeEncodeMethod( StringBuilder b, String className, BinaryCodecDefinition def ) {
        Set<String> eventSet = new HashSet<>();
        b.append( "\n" );
        b.append( "    @Override\n" );
        b.append( "    public final void encode( final Event msg ) {\n" );

        int          min            = Byte.MAX_VALUE;
        int          max            = 0;
        Set<Integer> switchEntrySet = new HashSet<>();

        Collection<BinaryEventMap> binaryMaps = def.getBinaryEventMaps();
        b.append( "        switch( msg.getReusableType().getSubId() ) {\n" );
        for ( BinaryEventMap map : binaryMaps ) {
            if ( map.isSubEvent() ) continue;

            String eventId = map.getEventId();

            if ( eventId == null ) continue;
            if ( eventSet.contains( eventId ) ) continue;

            ClassDefinition event = map.getClassDefinition();

            if ( !map.isSubEvent() ) {
                eventSet.add( eventId );
                int intId = event.getEventIntId();
                switchEntrySet.add( intId );
                if ( intId > max ) max = intId;
                if ( intId < min ) min = intId;

                String type   = event.getId();
                String method = getEncodeMethodName( map );

                b.append( "        case " ).append( GenUtils.getEventId( event ) ).append( ":\n" );

                String conditionalMethod = map.getEncodeOverrideMethod();

                if ( conditionalMethod != null ) {
                    b.append( "            " ).append( conditionalMethod ).append( "( (" ).append( type ).append( ") msg );\n" );
                } else {
                    b.append( "            " ).append( method ).append( "( (" ).append( type ).append( ") msg );\n" );
                }

                b.append( "            break;\n" );
            }
        }

        if ( min < 0 ) min = 0; // force tableswitch
        if ( max > min ) {
            int cnt = 0;
            for ( int entry = min; entry < max; ++entry ) {
                Integer key = entry;
                if ( !switchEntrySet.contains( key ) ) {
                    ++cnt;
                    b.append( "        case " ).append( entry ).append( ":\n" );
                }
            }
            if ( cnt > 0 ) {
                b.append( "            _builder.start();\n" );
                b.append( "            break;\n" );
            }
        }

        b.append( "        default:\n" );
        b.append( "            _builder.start();\n" );
        b.append( "            break;\n" );

        b.append( "        }\n" );
        b.append( "    }\n" );
        b.append( "\n" );
    }

    protected void writeEncoderConstructors( String className, StringBuilder b, BinaryCodecDefinition def ) {

        b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) { this( null, buf, offset ); }\n\n" );

        b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );

        b.append( "        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {\n" );
        b.append( "            throw new RuntimeException( \"Encode buffer too small only \" + buf.length + \", min=\" + SizeType.MIN_ENCODE_BUFFER.getSize() );\n" );
        b.append( "        }\n" );

        b.append( "        _id = id;\n" );
        b.append( "        _buf = buf;\n" );
        b.append( "        _offset = offset;\n" );
        b.append( "        _binaryVersion   = new ViewString( \"" ).append( _binaryModel.getBinaryVersion() ).append( "\");\n" );

        b.append( "        setBuilder();\n" );

        b.append( "    }\n\n" );
    }

    protected boolean writeHook( String hook ) {

        if ( hook == null || hook.length() == 0 ) return false;

        _b.append( "        " ).append( hook ).append( ";        // HOOK\n" );

        return true;
    }

    protected void writeMessageConstant( StringBuilder b, BinaryEventMap map, String msgType ) {
        b.append( "    private static final byte      MSG_" ).append( map.getBinaryMsgId() ).append( " = (byte)\'" ).append( msgType ).append( "\';\n" );
    }

    protected void writeSetBuilder( StringBuilder b ) {
        b.append( "    private void setBuilder() {\n" );
        b.append( "        _builder = (_debug) ? new " ).append( getDebugBuilder() ).append( "( _dump, new " ).append( getBuilder() )
         .append( "( _buf, _offset, _binaryVersion ) )\n" );
        b.append( "                            : new " ).append( getBuilder() ).append( "( _buf, _offset, _binaryVersion );\n" );
        b.append( "    }\n\n" );
    }

    private void addTags( BinaryEventMap map, Map<String, EncodeEntry> entries, BinaryModel binaryModel, ClassDefinition event, Map<String, Boolean> tags, BinaryEventDefinition msgDef ) {
        for ( Map.Entry<String, Boolean> entry : tags.entrySet() ) {

            // go thru each binary message adding to set of required binary tags
            // then iterate thru the set getting the entry from binary dictionary
            // for each binary tag get the event attr from the classDef, check type eg ViewString require start and end idx

            String tag = entry.getKey();

            if ( msgDef.isRepeatingGroup( tag ) ) {
                String splitAttr = map.getDecodeSplitAttr();

                if ( splitAttr != null ) {
                    /**
                     * ETI uses a split attr to handle demultiplexing of single external event into different internal events
                     * each repeating group on decode must become seperate top level event, so it doesnt have sub binary internal to external mappings
                     */
                    Map<String, Boolean>  subGrpTags = _binaryModel.getSubTagMap( tag, true, false );
                    BinaryEventDefinition subMsgDef  = _binaryModel.getBinaryEvent( tag );
                    addTags( map, entries, binaryModel, event, subGrpTags, subMsgDef );
                    continue;
                }
                /**
                 * multiple entries in subgroup can be encoded within same external event
                 */
                BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( null, tag );
                EncodeEntry           e                     = new EncodeEntry( tag, null, binaryTagEventMapping, msgDef );
                entries.put( tag, e );
                e.setRepeatingGroup( true );
                continue;
            }

            Boolean isMand = entry.getValue();

            if ( canIgnore( event, tag ) ) continue;

            BinaryDictionaryTag dictTag = binaryModel.getDictionaryTag( tag );

            String name = tag;
            if ( dictTag == null ) {

                if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) ) {
                    BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( null, tag );
                    EncodeEntry           e                     = new EncodeEntry( tag, null, binaryTagEventMapping, msgDef );
                    entries.put( tag, e );
                } else {
                    throw new RuntimeException( "Tag " + tag + " doesnt exist in binary dictionary " + binaryModel.getId() );
                }
            } else {
                name = dictTag.getId();
            }

            BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( name, tag );
            String                attrName              = (binaryTagEventMapping == null) ? name : binaryTagEventMapping.getEventAttr();

            AttributeDefinition attrDef = event.getAttribute( attrName );

            if ( attrDef == null ) {
                if ( binaryTagEventMapping != null ) {
                    EncodeEntry e = new EncodeEntry( tag, null, binaryTagEventMapping, msgDef );
                    entries.put( tag, e );
                } else if ( isMand == false ) {
                    EncodeEntry e = new EncodeEntry( tag, null, null, msgDef );
                    entries.put( tag, e );
                } else {
                    throw new RuntimeException( "Tag " + tag + " has binary dict entry of " + name +
                                                " which is missing from event " + event.getId() + ", map=" + map.getId() );
                }
            } else {
                EncodeEntry e = new EncodeEntry( tag, attrDef, binaryTagEventMapping, msgDef );
                entries.put( tag, e );
            }
        }
    }

    private boolean canIgnore( ClassDefinition event, String tag ) {

        return _ignoreMktVars.contains( tag );
    }

    private void encodeField( BinaryEventMap map, Map.Entry<String, EncodeEntry> entry ) {
        String      tag         = entry.getKey();
        EncodeEntry encodeEntry = entry.getValue();
        if ( addHook( HookType.encode, "        ", encodeEntry ) ) {
            return;
        }
        addHook( HookType.preencode, "        ", encodeEntry );
        encode( "msg", map, "        ", tag, encodeEntry );
        addHook( HookType.postencode, "        ", encodeEntry );
    }

    private void encodeFiller( BinaryEventMap map, String spaces, String tag, EncodeEntry value ) {
        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        String comment = "    // " + tag;

        int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, value._msgDef );

        switch( extType ) {
        case uByte:
            _b.append( spaces ).append( "_builder.encodeUByte( Constants.UNSET_BYTE );" ).append( comment );
            break;
        case sByte:
        case ch:
            _b.append( spaces ).append( "_builder.encodeByte( Constants.UNSET_BYTE );" ).append( comment );
            break;
        case data:
        case base36:
        case fstr:
        case str:
        case zstr:
            _b.append( spaces ).append( "_builder.encodeFiller( " ).append( len ).append( " );" ).append( comment );
            break;
        case uInt:
            _b.append( spaces ).append( "_builder.encodeUInt( Constants.UNSET_INT );" ).append( comment );
            break;
        case qty:
        case sInt:
            _b.append( spaces ).append( "_builder.encodeInt( Constants.UNSET_INT );" ).append( comment );
            break;
        case uLong:
            _b.append( spaces ).append( "_builder.encodeULong( Constants.UNSET_LONG );" ).append( comment );
            break;
        case sLong:
            _b.append( spaces ).append( "_builder.encodeLong( Constants.UNSET_LONG );" ).append( comment );
            break;
        case uShort:
            _b.append( spaces ).append( "_builder.encodeUShort( Constants.UNSET_SHORT );" ).append( comment );
            break;
        case sShort:
            _b.append( spaces ).append( "_builder.encodeShort( Constants.UNSET_SHORT );" ).append( comment );
            break;
        case bool:
            _b.append( spaces ).append( "_builder.encodeFiller( 1 );" ).append( comment );
            break;
        case decimal:
        case price:
            _b.append( spaces ).append( "_builder.encodePrice( Constants.UNSET_DOUBLE );" ).append( comment );
            break;
        case timeLocal:
        case timeUTC:
            _b.append( spaces ).append( "_builder.encodeFiller( 6 );" ).append( comment );
            break;
        case nowUTC:
        case timestampLocal:
        case timestampUTC:
            _b.append( spaces ).append( "_builder.encodeFiller( " ).append( getTimestampSize() ).append( " );" ).append( comment );
            break;
        }
    }

    private void encodeMethodPostHook( Map<HookType, String> hooks ) {
        String postHook = hooks.get( HookType.postencode );
        writeHook( postHook );
        _b.append( "        _builder.end();\n" );
    }

    private void encodeMethodPrehook( BinaryEventMap map, String type, Map<HookType, String> hooks ) {
        String preHook = hooks.get( HookType.preencode );
        _b.append( "        final long now = _tzCalculator.getNowAsInternalTime();\n" );
        encoderStart( map, type );
        writeHook( preHook );
    }

    private void encodeWholeNumber( final AttributeDefinition attr, String var, String spaces, BinaryType extType, String field, String unsigned ) {

        boolean attrDbl = attr.getType().getClass() == DoubleType.class;

        if ( extType == BinaryType.uByte || extType == BinaryType.sByte ) {
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Byte( (byte)" ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.uShort || extType == BinaryType.sShort ) {
            String cast = "";
            if ( attr.getType().getClass() == IntType.class || attr.getType().getClass() == LongType.class || attrDbl ) {
                cast = "(short)";
            }
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Short( " ).append( cast ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.uInt || extType == BinaryType.sInt ) {
            String cast = "";
            if ( attr.getType().getClass() == LongType.class || attrDbl ) {
                cast = "(int)";
            }
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Int( " ).append( cast ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.uLong || extType == BinaryType.sLong ) {
            String cast = (attrDbl) ? "(long)" : "";
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Long( " ).append( cast ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.qty ) {
            String cast = (attrDbl) ? "(int)" : "";
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "Qty( " ).append( cast ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.timestampUTC ) {
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "TimestampUTC( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else if ( extType == BinaryType.timeLocal ) {
            _b.append( spaces ).append( "_builder.encode" ).append( unsigned ).append( "TimeLocal( " ).append( var ).append( ".get" ).append( field ).append( "() );" );
        } else {
            throw new RuntimeException( "EncodeField " + field + " doesnt have expected byte/short/int/long external type, has=" + extType.toString() );
        }
    }

    private void endBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( "\n" ).append( spaces ).append( "final int endBlockIdx = _builder.getCurrentIndex();\n" );
            _b.append( spaces ).append( "final int bytesToSkip = " ).append( blockSize ).append( " - (endBlockIdx - startBlockIdx);\n" );
            _b.append( spaces ).append( "if ( bytesToSkip > 0 ) _builder.encodeFiller( bytesToSkip );\n" );
        }
    }

    private void endRootBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( "\n" ).append( spaces ).append( "final int endRootBlockIdx = _builder.getCurrentIndex();\n" );
            _b.append( spaces ).append( "final int rootBytesToSkip = " ).append( blockSize ).append( " - (endRootBlockIdx - startRootBlockIdx);\n" );
            _b.append( spaces ).append( "if ( rootBytesToSkip > 0 ) _builder.encodeFiller( rootBytesToSkip );\n" );
        }
    }

    private String getEncodeMethodName( BinaryEventMap map ) {
        return "encode" + map.getId();
    }

    private void repeatingGroup( String var, BinaryEventMap parentMap, String spaces, String grpTag, EncodeEntry value, BinaryEventDefinition parentMsgDef ) {
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;

        String field = null;
        if ( binaryTagEventMapping != null && binaryTagEventMapping.getEventAttr() != null && binaryTagEventMapping.getEventAttr().length() > 0 ) field = GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );
        if ( field == null ) field = GenUtils.toUpperFirstChar( grpTag );

        BinaryEventMap map = _codecDef.getBinaryEventMapById( grpTag );

        if ( map != null ) {
            _b.append( "\n" ).append( spaces ).append( "{\n" );

            int blockLen = parentMsgDef.getRepeatingGroupBlockLen( grpTag );

            addHook( HookType.predecode, spaces, value );

            Map<String, EncodeEntry> entries = getEntriesByTag( map );

            Map.Entry<String, EncodeEntry> entry;

            Iterator<Map.Entry<String, EncodeEntry>> entryIterator = entries.entrySet().iterator();

            String subVar     = "tmp" + GenUtils.toUpperFirstChar( grpTag );
            String counterVar = "counter" + GenUtils.toUpperFirstChar( grpTag );

            ClassDefinition event        = map.getClassDefinition();
            String          parentGetter = "get" + GenUtils.toUpperFirstChar( field );
            String          base         = event.getId();
            String          implCast     = "(" + base + "Impl)";

            _b.append( spaces ).append( "    " ).append( base ).append( "Impl " ).append( subVar ).append( " = " ).append( implCast ).append( var )
              .append( "." ).append( parentGetter ).append( "();\n" );

            String                counterTag        = parentMap.getEventDefinition().getRepeatingGroupCounter( grpTag );
            BinaryTagEventMapping eventCounter      = parentMap.getBinaryTagEventMapping( null, counterTag );
            String                eventGrpCountAttr = (eventCounter.getEventAttr() == null) ? counterTag : eventCounter.getEventAttr();

            _b.append( spaces ).append( "    int " ).append( counterVar ).append( " = " ).append( var ).append( ".get" )
              .append( GenUtils.toUpperFirstChar( eventGrpCountAttr ) ).append( "();\n" );

            _b.append( spaces ).append( "    for( int i=0 ; i < " ).append( counterVar ).append( " ; ++i ) { \n" );

            String spaces2 = spaces + "        ";

            if ( blockLen > 0 ) startBlock( spaces2, blockLen );

            while( entryIterator.hasNext() ) {
                entry = entryIterator.next();

                String      tag         = entry.getKey();
                EncodeEntry encodeEntry = entry.getValue();

                encode( subVar, map, spaces2, tag, encodeEntry );
            }

            if ( blockLen > 0 ) endBlock( spaces2, blockLen );

            _b.append( spaces2 ).append( subVar ).append( " = " ).append( subVar ).append( ".getNext();\n" );

            _b.append( spaces ).append( "    }\n" );
            _b.append( spaces ).append( "}\n\n" );

        } else {
            // the group is not in the model, the counter field is assumed to be ZERO so nothing to skip
        }
    }

    private void startBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( spaces ).append( "final int startBlockIdx = _builder.getCurrentIndex();\n" );
        }
    }

    private void startRootBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( spaces ).append( "final int startRootBlockIdx = _builder.getCurrentIndex();\n" );
        }
    }

    private void writeAttrs( StringBuilder b, BinaryCodecDefinition def ) {

        Collection<BinaryEventMap> binaryMaps = def.getBinaryEventMaps();

        StringBuilder msgTypes = new StringBuilder();
        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                String key = ":" + msgType + "; ";

                if ( msgType != null && useMsgTypesForStartEncode( map ) && msgTypes.indexOf( key ) == -1 ) {
                    msgTypes.append( key );
                    writeMessageConstant( b, map, msgType );
                }
            }
        }

        b.append( "\n" );

        TransformEncoderGenerator.writeTransformAttrs( b, def );

        b.append( "\n    private final byte[]                  _buf;\n" );
        b.append( "    private final String                  _id;\n" );
        b.append( "    private final int                     _offset;\n" );
        b.append( "    private final ZString                 _binaryVersion;\n" );

        declareBuilder( b );

        b.append( "    private       TimeUtils               _tzCalculator = TimeUtilsFactory.createTimeUtils();\n" );
        b.append( "    private       SingleByteLookup        _sv;\n" );
        b.append( "    private       TwoByteLookup           _tv;\n" );
        b.append( "    private       MultiByteLookup         _mv;\n" );
        b.append( "    private final ReusableString          _dump  = new ReusableString(256);\n" );

        b.append( "\n    private boolean                 _debug = false;\n" );
    }

    private void writeEncoderMethods( StringBuilder b, String className, BinaryCodecDefinition def ) {

        writeEncodeMethod( b, className, def );

        b.append( "    @Override public final int getLength() { return _builder.getLength(); }\n" );
        b.append( "    @Override public final int getOffset() { return _builder.getOffset(); }\n" );

        b.append( "\n" );

        b.append( "    @Override\n" );
        b.append( "    public boolean isDebug() {\n" );
        b.append( "        return _debug;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public void setDebug( boolean isDebugOn ) {\n" );
        b.append( "        _debug = isDebugOn;\n" );
        b.append( "        setBuilder();\n" );
        b.append( "    }\n\n" );

        writeSetBuilder( b );

        Collection<BinaryEventMap> binaryMaps = def.getBinaryEventMaps();

        for ( BinaryEventMap map : binaryMaps ) {
            if ( map.isSubEvent() ) continue;

            String eventId = map.getEventId();

            if ( eventId == null ) continue;

            int blockLen = map.getEventDefinition().getBlockLen();

            if ( blockLen > 0 ) {
                encodeRootWithBlockSkipCheck( map, blockLen );
            } else {
                encode( map );
            }
        }
    }

    private void writeOtherMethods( StringBuilder b, BinaryCodecDefinition def ) {
        b.append( "    @Override\n" );
        b.append( "    public final byte[] getBytes() {\n" );
        b.append( "        return _buf;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void setTimeUtils( final TimeUtils calc ) {\n" );
        b.append( "        _tzCalculator = calc;\n" );
        b.append( "        _builder.setTimeUtils( calc );\n" );
        b.append( "    }\n\n" );

        b.append( "\n    @Override public String getComponentId() { return _id; }\n" );

        TransformEncoderGenerator.writeEncoderTransforms( b, def );
    }
}
