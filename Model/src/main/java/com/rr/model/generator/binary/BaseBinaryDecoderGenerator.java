/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.binary;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import com.rr.model.base.type.*;
import com.rr.model.generator.*;
import com.rr.model.generator.transforms.TransformDecoderGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class BaseBinaryDecoderGenerator implements DecoderGenerator {

    protected static class DecodeEntry {

        public final AttributeDefinition   _attrDef;
        public final BinaryTagEventMapping _binaryTagEventMapping;
        final        String                _tag;
        private      boolean               _repeatingGroup;

        DecodeEntry( String tag,
                     BinaryTagEventMapping binaryTagEventMapping,
                     AttributeDefinition attrDef ) {

            _tag                   = tag;
            _binaryTagEventMapping = binaryTagEventMapping;
            _attrDef               = attrDef;
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
            DecodeEntry other = (DecodeEntry) obj;
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
    protected final Set<String> _ignoreMktVars = new LinkedHashSet<>();
    protected InternalModel         _internal;
    protected StringBuilder         _b;
    protected BinaryModel           _binaryModel;
    protected Set<String> _conditionalMessages = new HashSet<>();
    private   BinaryCodecModel      _codecModel;
    private   BinaryCodecDefinition _codecDef;
    private   BinaryCodecDefinition _def;
    private   boolean               _isRecovery;

    public BaseBinaryDecoderGenerator() {
        _ignoreMktVars.add( "msgType" );
        _ignoreMktVars.add( "protocolVersion" );
        _ignoreMktVars.add( "msgLen" );
        _ignoreMktVars.add( "etx" );
    }

    @Override
    public void generate( InternalModel internal,
                          BinaryCodecModel codec,
                          BinaryCodecDefinition def,
                          BinaryModel binModel ) throws FileException, IOException {

        _internal    = internal;
        _binaryModel = binModel;
        _codecModel  = codec;
        _codecDef    = def;

        StringBuilder b = new StringBuilder();

        String className = def.getId() + "Decoder";

        if ( _isRecovery ) className = ModelConstants.FULL_EVENT_PRENAME + className;

        File file = GenUtils.getJavaFile( _codecModel, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _codecModel, ModelConstants.CODEC_PACKAGE, className );

        addDecoderImports( b, def );

        b.append( "\n@SuppressWarnings( \"unused\" )\n" );

        String interfaceName = getInterfaceToImplement();

        b.append( "\npublic final class " ).append( className ).append( " extends AbstractBinaryDecoder" );
        if ( interfaceName != null && interfaceName.length() > 0 ) {
            b.append( " implements " ).append( interfaceName );
        }
        b.append( " {\n" );

        _b   = b;
        _def = def;

        b.append( "\n    private final ReusableString _tmpLookupKey = new ReusableString();\n" );

        b.append( "\n   // Attrs\n" );
        writeDecoderAttrs( className, b, def );

        b.append( "\n   // Pools\n" );
        writeDecoderPools( className, b, def );

        b.append( "\n   // Constructors\n" );
        writeDecoderConstructors( className, b, def );

        b.append( "\n   // decode methods\n" );
        writeDecoderMethods( b, className, def );

        b.append( "\n   // transform methods\n" );
        TransformDecoderGenerator.writeDecoderTransforms( b, className, def );

        GenUtils.append( b, def.getDecodeInclude() );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    public void decodeKnownMessageType( BinaryEventMap map ) {

        Map<String, DecodeEntry> entries = getEntriesByTag( map );

        ClassDefinition event = map.getClassDefinition();

        String preBinary = getPrefix( event );

        _b.append( "    private Event decode" ).append( map.getBinaryMsgId() ).append( "() {\n" );

        String spaces = "        ";

        _b.append( spaces ).append( "if ( _debug ) {\n" );
        _b.append( spaces ).append( "    _dump.append( \"\\nKnown Message : \" ).append( \"" ).append( map.getBinaryMsgId() )
          .append( "\" ).append( \" : \" );\n" );
        _b.append( spaces ).append( "}\n\n" );

        if ( event != null ) {
            String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
            String base    = preBinary + event.getId();
            _b.append( "        final " ).append( base ).append( "Impl msg = " ).append( baseVar ).append( "Factory.get();\n" );
        } else {
            _b.append( "        Event msg = null;\n" ); // external event is not to be transformed into internal event, simply skipped
        }

        int blockLen = map.getEventDefinition().getBlockLen();

        if ( blockLen > 0 ) {
            decodeRootWithBlockSkipCheck( map, entries, spaces, blockLen );
        } else {
            decodeRoot( map, entries, spaces );
        }

        _b.append( "        _builder.end();\n" );

        _b.append( "        return msg;\n" );

        _b.append( "    }\n\n" );
    }

    protected void addDecoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        b.append( "import java.util.HashMap;\n" );
        b.append( "import java.util.Map;\n" );

        b.append( "import com.rr.core.codec.*;\n" );
        b.append( "import com.rr.core.utils.*;\n" );
        b.append( "import com.rr.core.lang.*;\n" );
        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.factories.*;\n" );

        b.append( "import com.rr.core.pool.SuperPool;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.model.internal.type.*;\n" );
        b.append( "import com.rr.core.codec.RuntimeDecodingException;\n" );
        b.append( "import com.rr.core.codec.binary.BinaryDecodeBuilder;\n" );
        b.append( "import com.rr.core.codec.binary.DebugBinaryDecodeBuilder;\n" );

        InternalModelGenerator.addInternalEventsFactoryWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsImplWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsInterfacesWildImport( b, _internal );
        InternalModelGenerator.addInternalTypeWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreSizeTypeImport( b, _internal );
    }

    protected void addEventConstant( StringBuilder b, Set<String> msgTypes, BinaryEventMap map, String msgType ) {
        if ( msgType != null && msgType.length() >= 1 && !msgTypes.contains( msgType ) ) {
            msgTypes.add( msgType );
            b.append( "    private static final byte      MSG_" ).append( map.getBinaryMsgId() ).append( " = (byte)\'" ).append( msgType ).append( "\';\n" );
        }
    }

    protected boolean addHook( BinaryEventMap map, String spaces, HookType hookType ) {
        Map<HookType, String> hooks = map.getHooks();

        boolean added = false;

        if ( hooks != null ) {
            String decode = hooks.get( hookType );

            if ( decode != null && decode.length() > 0 ) {
                _b.append( spaces ).append( "if ( _debug ) _dump.append( \"\\nHook : \" ).append( \"" ).append( hookType )
                  .append( "\" ).append( \" : \" );\n" );

                _b.append( spaces ).append( decode ).append( ";\n" );

                added = true;
            }
        }

        return added;
    }

    /**
     * @return TRUE if hook added
     */
    protected boolean addHook( HookType hookType, String spaces, DecodeEntry value ) {
        BinaryTagEventMapping explicitMapping = value._binaryTagEventMapping;
        Map<HookType, String> hooks;

        boolean added = false;

        if ( explicitMapping != null ) {
            hooks = explicitMapping.getHooks();

            if ( hooks != null ) {
                String decode = hooks.get( hookType );

                if ( decode != null && decode.length() > 0 ) {
                    _b.append( spaces ).append( decode ).append( ";\n" );

                    added = true;
                }
            }
        }

        return added;
    }

    protected void declareBuilder( StringBuilder b ) {
        b.append( "\n    private BinaryDecodeBuilder _builder;\n\n" );
    }

    protected void decode( String var, BinaryEventMap map, String spaces, String tag, DecodeEntry value, BinaryEventDefinition msgDef ) {

        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        String field = null;
        if ( binaryTagEventMapping != null && binaryTagEventMapping.getEventAttr() != null && binaryTagEventMapping.getEventAttr().length() > 0 ) field = GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );
        if ( field == null ) field = GenUtils.toUpperFirstChar( tag );

        // most of dump logic is in the builder

        String unsigned = getUnsignedPrefix( extType );

        if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) ) {
            logField( spaces, tag );
            int    len     = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );
            String comment = msgDef.getComment( tag );
            _b.append( spaces ).append( "_builder.skip( " ).append( len ).append( " );" );
            if ( comment != null ) _b.append( "    // " ).append( comment );
            _b.append( "\n" );
        } else if ( value.isRepeatingGroup() ) {

            repeatingGroup( var, map, spaces, tag, value, msgDef );

        } else if ( attr == null ) {
            decodeToVar( map, spaces, tag, value, msgDef );
        } else if ( attr.isPrimitive() ) {
            logField( spaces, tag );
            if ( GenUtils.isStringAttr( attr ) ) {
                int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );
                if ( extType == BinaryType.fstr ) {
                    _b.append( spaces ).append( "_builder.decodeStringFixedWidth( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate(), " )
                      .append( len ).append( " );\n" );
                } else if ( extType == BinaryType.zstr ) {
                    _b.append( spaces ).append( "_builder.decodeZStringFixedWidth( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate(), " )
                      .append( len ).append( " );\n" );
                } else if ( extType == BinaryType.data ) {
                    _b.append( spaces ).append( "_builder.decodeData( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate(), " ).append( len )
                      .append( " );\n" );
                } else if ( extType == BinaryType.sInt || extType == BinaryType.uInt ) {
                    _b.append( spaces ).append( "_builder.decodeIntToString( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate() );\n" );
                } else if ( extType == BinaryType.sLong || extType == BinaryType.uLong ) {
                    _b.append( spaces ).append( "_builder.decodeLongToString( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate() );\n" );
                } else {
                    _b.append( spaces ).append( "_builder.decodeString( " ).append( var ).append( ".get" ).append( field ).append( "ForUpdate() );\n" );
                }
            } else if ( attr.getType().getClass() == CharType.class ) {
                if ( extType == BinaryType.ch ) {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeChar() );\n" );
                } else {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Byte() );\n" );
                }
            } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                if ( extType == BinaryType.timeLocal ) {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimeLocal() );\n" );
                } else if ( extType == BinaryType.timeUTC ) {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimeUTC() );\n" );
                } else if ( extType == BinaryType.timestampLocal ) {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimestampLocal() );\n" );
                } else {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimestampUTC() );\n" );
                }
            } else if ( attr.getType().getClass() == ShortType.class ) {
                decodeWholeNumber( var, spaces, extType, field, unsigned, dictTag, binaryTagEventMapping );
            } else if ( attr.getType().getClass() == IntType.class ) {
                decodeWholeNumber( var, spaces, extType, field, unsigned, dictTag, binaryTagEventMapping );
            } else if ( attr.getType().getClass() == LongType.class ) {
                if ( extType == BinaryType.base36 ) {
                    int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeBase36Number( " ).append( len ).append( " ) );\n" );
                } else {
                    decodeWholeNumber( var, spaces, extType, field, unsigned, dictTag, binaryTagEventMapping );
                }
            } else if ( attr.getType().getClass() == DateType.class ) {
                _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeDate() );\n" );
            } else if ( attr.getType().getClass() == FloatType.class ) {
                _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeDecimal() );\n" );
            } else if ( attr.getType().getClass() == DoubleType.class ) {
                int lenOverride = GenUtils.getFixedLenOverride( dictTag, binaryTagEventMapping );
                if ( lenOverride > 0 && extType == BinaryType.price ) {
                    _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodePrice( " + lenOverride + ", " + dictTag.getDecimalPlaces() + " ) );\n" );
                } else {
                    if ( BinaryType.isWholeNumber( extType ) ) {
                        if ( extType == BinaryType.base36 ) {
                            int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

                            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeBase36Number( " ).append( len ).append( " ) );\n" );
                        } else {
                            decodeWholeNumber( var, spaces, extType, field, unsigned, dictTag, binaryTagEventMapping );
                        }
                    } else {
                        _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeDecimal() );\n" );
                    }
                }
            } else if ( attr.getType().getClass() == BooleanType.class ) {
                _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeBool() );\n" );
            } else {
                throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                            attr.getAttrName() );
            }
        } else {
            logField( spaces, tag );
            TypeDefinition defn      = _internal.getTypeDefinition( attr.getTypeId() );
            TypeTransform  transform = _def.getTypeTransform( attr.getTypeId() );

            int maxInternalSize = defn.getMaxEntryValueLen();
            int fixedLen        = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

            if ( transform == null ) {
                decodeType( var, map, tag, field, defn, dictTag, maxInternalSize, fixedLen, spaces );
            } else {
                decodeTypeTransform( var, map, tag, field, dictTag, defn, maxInternalSize, transform, fixedLen, spaces );
            }
        }
    }

    protected void doWriteDecodeMessage( StringBuilder b, Collection<BinaryEventMap> binaryMaps ) {
        b.append( "    @Override\n" );
        b.append( "    protected final Event doMessageDecode() {\n" );
        b.append( "        _builder.setMaxIdx( _maxIdx );\n\n" );

        b.append( "        switch( _msgType ) {\n" );

        StringBuilder switchEntrySet = new StringBuilder();

        _conditionalMessages.clear();
        byte min = Byte.MAX_VALUE;
        byte max = 0;
        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                if ( msgType != null && msgType.length() >= 1 && switchEntrySet.indexOf( msgType ) == -1 ) {
                    b.append( "        case MSG_" ).append( map.getBinaryMsgId() ).append( ":\n" );
                    b.append( "            return decode" ).append( map.getBinaryMsgId() ).append( "();\n" );
                    switchEntrySet.append( msgType );
                    byte entry = (byte) msgType.charAt( 0 );
                    if ( entry > max ) max = entry;
                    if ( entry < min ) min = entry;
                    if ( map.getConditionalKey() != null ) {
                        _conditionalMessages.add( msgType );
                    }
                }
            }
        }

        if ( min < 0 ) min = 0; // force tableswitch
        if ( max > min ) {
            int cnt = 0;
            for ( byte entry = min; entry < max; ++entry ) {
                if ( switchEntrySet.toString().indexOf( entry ) == -1 ) {
                    ++cnt;
                    if ( entry == '\\' ) {
                        b.append( "        case '\\\\':\n" );
                    } else {
                        b.append( "        case '" ).append( (char) entry ).append( "':\n" );
                    }
                }
            }
            if ( cnt > 0 ) {
                b.append( "            break;\n" );
            }
        }

        b.append( "        }\n" );

        b.append( "        if ( _debug ) {\n" );
        b.append( "            _tmpLookupKey.copy( '|' ).append( _msgType ).append( '|' );\n" );

        b.append( "            if ( ! _missedMsgTypes.contains( _tmpLookupKey ) ) {\n" );

        b.append( "                _dump.append( \"Skipped Unsupported Message : \" ).append( _msgType );\n" );

        b.append( "                _log.info( _dump );\n" );
        b.append( "                _dump.reset();\n" );

        b.append( "                _missedMsgTypes.append( _tmpLookupKey );\n" );
        b.append( "            }\n" );
        b.append( "        }\n" );

        b.append( "        return null;\n" );
        b.append( "    }\n\n" );
    }

    protected boolean forceNoDelegate() {
        return false;
    }

    protected abstract String getBuilder();

    protected String getDebugBuilder() {
        return "DebugBinaryDecodeBuilder<>";
    }

    protected DelegateType getDelegateType( ClassDefinition event, OutboundInstruction inst, boolean isSrcSide, boolean isSrcClient ) {
        return GenUtils.getDelegateType( event.getStreamSrc(), isSrcSide, isSrcClient, inst );
    }

    protected Map<String, DecodeEntry> getEntriesByTag( BinaryEventMap map ) {
        Map<String, DecodeEntry> entries = new LinkedHashMap<>();

        // only need vars for exec reports

        Map<String, Boolean> tags = _binaryModel.getTagMap( map, true, false );

        for ( Map.Entry<String, Boolean> entry : tags.entrySet() ) {

            // go thru each binary message adding to set of required binary tags
            // then iterate thru the set getting the entry from binary dictionary
            // for each binary tag get the event attr from the classDef, check type eg ViewString require start and end idx

            String  tag    = entry.getKey();
            Boolean isMand = entry.getValue();

            processDecodeEntry( map, entries, tag, isMand );
        }

        // add custom fields that are generated by decode hooks
        for ( Map.Entry<String, BinaryTagEventMapping> m : map.getTagsById().entrySet() ) {
            String                key     = m.getKey();
            BinaryTagEventMapping mapping = m.getValue();
            String                mAttr   = mapping.getEventAttr();
            if ( (!entries.containsKey( key )) && (mAttr == null || !(entries.containsKey( mAttr ))) ) {
                if ( mapping.hasDecodeHook() ) {
                    boolean found = false;
                    for ( DecodeEntry e : entries.values() ) {
                        AttributeDefinition attr = e._attrDef;
                        if ( attr != null && attr.getAttrName().equals( mapping.getEventAttr() ) ) {
                            found = true;
                            break;
                        }
                    }
                    if ( !found ) {
                        DecodeEntry e = new DecodeEntry( key, mapping, null );
                        entries.put( key, e );
                    }
                }
            }
        }

        return entries;
    }

    protected String getInterfaceToImplement() {
        return null;
    }

    protected String getMsgTypeClass() {
        return "byte";
    }

    protected String getPrefix( ClassDefinition event ) {
        if ( event != null ) {
            EventStreamSrc src = event.getStreamSrc();
            if ( src == EventStreamSrc.client ) return ModelConstants.FULL_EVENT_PRENAME;  // cant use Client events for non fix
            if ( src == EventStreamSrc.exchange ) return ModelConstants.FULL_EVENT_PRENAME;
        }
        return "";
    }

    protected Map<String, DecodeEntry> getSubGroupEntriesByTag( BinaryEventMap map, String subGrpId ) {
        Map<String, DecodeEntry> entries = new LinkedHashMap<>();

        // only need vars for exec reports

        Map<String, Boolean> tags = _binaryModel.getSubTagMap( subGrpId, true, false );

        for ( Map.Entry<String, Boolean> entry : tags.entrySet() ) {

            // go thru each binary message adding to set of required binary tags
            // then iterate thru the set getting the entry from binary dictionary
            // for each binary tag get the event attr from the classDef, check type eg ViewString require start and end idx

            String  tag    = entry.getKey();
            Boolean isMand = entry.getValue();

            processDecodeEntry( map, entries, tag, isMand );
        }

        return entries;
    }

    protected void repeatingGroup( String var, BinaryEventMap parentMap, String spaces, String grpTag, DecodeEntry value, BinaryEventDefinition parentMsgDef ) {
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;

        String field = null;

        if ( binaryTagEventMapping != null && binaryTagEventMapping.getEventAttr() != null && binaryTagEventMapping.getEventAttr().length() > 0 ) field = GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );

        if ( field == null ) field = GenUtils.toUpperFirstChar( grpTag );

        BinaryEventMap map = _codecDef.getBinaryEventMapById( grpTag );

        _b.append( "\n" ).append( spaces ).append( "{\n" );

        if ( map != null ) {

            //        REPEAT GROUP tag=MDEntriesReal,
            //                attr=null
            //                binaryTagEventMapping=BinaryTagEventMapping [ eventAttr=MDEntries, BinaryTag=MDEntriesReal, maxOccurs=1]
            //                dictTag=null

            int blockLen = parentMsgDef.getRepeatingGroupBlockLen( grpTag );

            addHook( map, spaces, HookType.predecode );

            Map<String, DecodeEntry> entries = getEntriesByTag( map );

            Map.Entry<String, DecodeEntry> entry;

            Iterator<Map.Entry<String, DecodeEntry>> entryIterator = entries.entrySet().iterator();

            String subVar     = "tmp" + GenUtils.toUpperFirstChar( grpTag );
            String subLastVar = "last" + GenUtils.toUpperFirstChar( grpTag );

            String counterVar = "counter" + GenUtils.toUpperFirstChar( grpTag );

            ClassDefinition event        = map.getClassDefinition();
            String          preBinary    = getPrefix( event );
            String          parentSetter = "set" + GenUtils.toUpperFirstChar( field );
            String          base         = preBinary + event.getId();

            String subGrpFactory = "_" + GenUtils.toLowerFirstChar( base ) + "Factory";

            _b.append( spaces ).append( "     // CHECKPOINT: repeatingGroup type " ).append( base ).append( "\n" );

            _b.append( spaces ).append( "    " ).append( base ).append( "Impl " ).append( subVar ).append( ";\n" );
            _b.append( spaces ).append( "    " ).append( base ).append( "Impl " ).append( subLastVar ).append( " = null;\n" );

            String                counterTag        = parentMap.getEventDefinition().getRepeatingGroupCounter( grpTag );
            BinaryTagEventMapping eventCounter      = parentMap.getBinaryTagEventMapping( null, counterTag );
            String                eventGrpCountAttr = (eventCounter.getEventAttr() == null) ? counterTag : eventCounter.getEventAttr();

            _b.append( spaces ).append( "    int " ).append( counterVar ).append( " = " ).append( var ).append( ".get" )
              .append( GenUtils.toUpperFirstChar( eventGrpCountAttr ) ).append( "();\n" );

            _b.append( spaces ).append( "    for( int i=0 ; i < " ).append( counterVar ).append( " ; ++i ) { \n" );

            String spaces2 = spaces + "        ";

            if ( blockLen > 0 ) startBlock( spaces2, blockLen );

            _b.append( "                " ).append( subVar ).append( " = " ).append( subGrpFactory ).append( ".get();\n" );
            _b.append( "                if ( " ).append( subLastVar ).append( " == null ) {\n" );
            _b.append( "                    " ).append( var ).append( "." ).append( parentSetter ).append( "( " ).append( subVar ).append( " );\n" );
            _b.append( "                } else {\n" );
            _b.append( "                    " ).append( subLastVar ).append( ".setNext( " ).append( subVar ).append( " );\n" );
            _b.append( "                }\n" );
            _b.append( "                " ).append( subLastVar ).append( " = " ).append( subVar ).append( ";\n" );

            while( entryIterator.hasNext() ) {
                entry = entryIterator.next();

                String      tag         = entry.getKey();
                DecodeEntry decodeEntry = entry.getValue();

                decodeFieldKnownMessage( subVar, map, spaces2, tag, decodeEntry );
            }

            if ( blockLen > 0 ) endBlock( spaces2, blockLen );

            _b.append( spaces ).append( "    }\n" );

        } else {
            int blockLen = parentMsgDef.getRepeatingGroupBlockLen( grpTag );

            String counterField = parentMap.getEventDefinition().getRepeatingGroupCounter( grpTag );

            String counterVar = "_" + GenUtils.toLowerFirstChar( counterField );

            _b.append( spaces ).append( "    final int bytesToSkip = " ).append( counterVar ).append( " * " ).append( blockLen ).append( ";\n" );
            _b.append( spaces ).append( "    _builder.skip( bytesToSkip ); // SKIPPING repeating group : " ).append( grpTag ).append( "\n" );
        }

        _b.append( spaces ).append( "}\n\n" );
    }

    protected void writeDecoderConstructors( String className, StringBuilder b, BinaryCodecDefinition def ) {

        b.append( "    public " ).append( className ).append( "() { this( null ); }\n" );

        b.append( "    public " ).append( className ).append( "( String id ) {\n" );
        b.append( "        super();\n" );
        b.append( "        setBuilder();\n" );
        b.append( "        _id = id;\n" );
        b.append( "        _protocolVersion = (byte)'" ).append( _binaryModel.getBinaryVersion().charAt( 0 ) ).append( "';\n" );

        b.append( "    }\n" );
    }

    protected void writeSetBuilder( StringBuilder b ) {
        b.append( "    private void setBuilder() {\n" );
        b.append( "        _builder = (_debug) ? new " ).append( getDebugBuilder() ).append( "( _dump, new " ).append( getBuilder() ).append( "() )\n" );
        b.append( "                            : new " ).append( getBuilder() ).append( "();\n" );
        b.append( "    }\n\n" );
    }

    private boolean addHook( HookType hookType, String spaces, Map<HookType, String> hooks ) {

        boolean added = false;

        if ( hooks != null ) {
            String decode = hooks.get( hookType );

            if ( decode != null && decode.length() > 0 ) {
                _b.append( spaces ).append( decode ).append( ";\n" );

                added = true;
            }
        }

        return added;
    }

    private boolean canIgnore( ClassDefinition event, String tag ) {

        return _ignoreMktVars.contains( tag );
    }

    private void decodeCondMsgType( Map<String, DecodeEntry> entries, Set<BinaryEventMap> relatedMsgs, String funcName ) {

        _b.append( "    private Event " ).append( funcName ).append( "( Event prevMsg ) {\n" );

        String spaces = "        ";

        String  zelse         = "";
        boolean switchStarted = false;

        Set<Integer> vals = new HashSet<>();

        for ( BinaryEventMap map : relatedMsgs ) {
            String key     = map.getConditionalKey();
            String keyVar  = "_" + GenUtils.toLowerFirstChar( key );
            String condVal = map.getConditionalVal();

            BinaryDictionaryTag dictTag = _binaryModel.getDictionaryTag( key );
            BinaryType          extType = (dictTag != null) ? dictTag.getBinaryType() : null;

            if ( dictTag == null ) {
                throw new RuntimeException( "Error in model, conditional field " + key + " is NOT in binary dictionary" );
            }

            switch( extType ) {
            case ch:
                if ( !switchStarted ) {
                    _b.append( spaces ).append( "switch( " ).append( keyVar ).append( " ) {\n" );
                    switchStarted = true;
                }

                vals.add( (int) condVal.charAt( 0 ) );

                _b.append( spaces ).append( "case '" ).append( condVal ).append( "': {\n" );
                populateCondMsg( map, spaces + "        " );
                _b.append( spaces ).append( "    }\n" );
                break;
            case uByte:
            case sByte:
            case qty:
            case uInt:
            case sInt:
            case uShort:
            case sShort:
                if ( !switchStarted ) {
                    _b.append( spaces ).append( "switch( " ).append( keyVar ).append( " ) {\n" );
                    switchStarted = true;
                }
                vals.add( (int) condVal.charAt( 0 ) );
                _b.append( spaces ).append( "case " ).append( condVal ).append( ": {\n" );
                populateCondMsg( map, spaces + "        " );
                _b.append( spaces ).append( "    }\n" );
                break;
            case uLong:
            case sLong:
            case base36:
            case bool:
            case nowUTC:
            case timeLocal:
            case timeUTC:
            case timestampLocal:
            case timestampUTC:
            case decimal:
            case price:
                _b.append( spaces ).append( zelse ).append( "if ( " ).append( condVal ).append( " == " ).append( keyVar ).append( " ) {\n" );
                populateCondMsg( map, spaces + "    " );
                _b.append( spaces ).append( "}\n" );
                break;
            case data:
            case fstr:
            case str:
            case zstr:
                _b.append( spaces ).append( zelse ).append( "if ( \"" ).append( condVal ).append( "\".equals( " ).append( keyVar ).append( " ) {\n" );
                populateCondMsg( map, spaces + "    " );
                _b.append( spaces ).append( "}\n" );
                break;
            }
            zelse = "else ";
        }

        padd( spaces, vals );

        if ( switchStarted ) {
            _b.append( spaces ).append( "}\n" );
        }

        _b.append( spaces ).append( "throw new RuntimeDecodingException( \"No matching condition for conditional message type\" );\n" );
        _b.append( "    }\n" );
    }

    private void decodeCondMsgWithRepeatingGrp( BinaryEventMap map, Map<String, DecodeEntry> entries, String subGrpId, String counterTag, String spaces, Set<BinaryEventMap> relatedMsgs, String condFuncName ) {
        Map<String, DecodeEntry> grpEntries = getSubGroupEntriesByTag( map, subGrpId );

        String counterVar = "_" + GenUtils.toLowerFirstChar( counterTag );

        _b.append( spaces ).append( "Event tmpMsg = msg;\n" );

        _b.append( spaces ).append( "for( int i=0 ; i < " ).append( counterVar ).append( " ; ++i ) { \n" );

        String spaces2 = spaces + "    ";

        BinaryEventDefinition subMsgDef = _binaryModel.getBinaryEvent( subGrpId );

        for ( Map.Entry<String, DecodeEntry> entry : grpEntries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            decodeToVar( map, spaces2, tag, decodeEntry, subMsgDef );
        }

        _b.append( spaces2 ).append( "// CHECKPOINT : decodeCondMsgWithRepeatingGrp with " ).append( condFuncName ).append( "\n" );
        _b.append( spaces2 ).append( "msg = " ).append( condFuncName ).append( "( msg );\n" );

        _b.append( spaces ).append( "}\n" );

        _b.append( spaces ).append( "msg = tmpMsg;\n" );
    }

    private void decodeConditional( BinaryEventMap map, String spaces, String tag, DecodeEntry value, BinaryEventDefinition msgDef ) {

        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        String var = "_" + GenUtils.toLowerFirstChar( tag );
        String field = (binaryTagEventMapping == null) ? GenUtils.toUpperFirstChar( tag )
                                                       : GenUtils.toUpperFirstChar( binaryTagEventMapping.getEventAttr() );

        if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) ) {
            // already skipped
        } else if ( attr == null ) {
            // external value not mapped to an internal value
        } else if ( attr.isPrimitive() ) {
            if ( GenUtils.isStringAttr( attr ) ) {
                if ( extType == BinaryType.fstr || extType == BinaryType.str || extType == BinaryType.zstr || extType == BinaryType.data ) {
                    _b.append( spaces ).append( "msg.get" ).append( field ).append( "ForUpdate().copy( " ).append( var ).append( " );\n" );
                } else {
                    _b.append( spaces ).append( "msg.get" ).append( field ).append( "ForUpdate().append( " ).append( var ).append( " );\n" );
                }
            } else {
                _b.append( spaces ).append( "msg.set" ).append( field ).append( "( " ).append( var ).append( " );\n" );
            }
        } else {
            TypeDefinition defn      = _internal.getTypeDefinition( attr.getTypeId() );
            TypeTransform  transform = _def.getTypeTransform( attr.getTypeId() );

            int maxInternalSize = defn.getMaxEntryValueLen();

            int fixedLen = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

            if ( transform == null ) {
                String type = defn.getTypeDefinition();
                if ( fixedLen > 0 ) maxInternalSize = fixedLen;
                if ( maxInternalSize == 1 ) {
                    _b.append( spaces ).append( "msg.set" ).append( field ).append( "( " ).append( type ).append( ".getVal( " ).append( var )
                      .append( " ) );\n" );
                } else {
                    String params = "val.getBytes(), 0, val.length()";
                    _b.append( spaces ).append( "msg.set" ).append( field ).append( "( " ).append( type ).append( ".getVal( " ).append( params )
                      .append( " ) );\n" );
                }
            } else {
                if ( addHook( HookType.decode, spaces, transform.getHooks() ) ) {
                    return;
                }
                addHook( HookType.predecode, spaces, transform.getHooks() );
                if ( maxInternalSize == 1 ) {
                    _b.append( spaces ).append( "msg.set" ).append( field ).append( "( transform" ).append( field ).append( "( " ).append( var )
                      .append( " ) );\n" );
                } else {
                    String params = "val.getBytes(), 0, val.length()";
                    _b.append( spaces ).append( "msg.set" ).append( field ).append( "( transform" ).append( field ).append( "( " ).append( params )
                      .append( " ) );\n" );
                }
                addHook( HookType.postdecode, spaces, transform.getHooks() );
            }
        }
    }

    private void decodeConditionalMessageType( Set<BinaryEventMap> relatedMsgs, String binaryMsgType, String condFuncName ) {

        if ( relatedMsgs.size() == 0 ) return;

        BinaryEventMap           map     = relatedMsgs.iterator().next();
        Map<String, DecodeEntry> entries = getEntriesByTag( map );
        // note the binary message maps to multiple message maps in codec SO at this point for conditional messages the attr mappings
        // may NOT be correct

        decodeCondMsgType( entries, relatedMsgs, condFuncName );

        _b.append( "    private Event decode" ).append( binaryMsgType ).append( "() {\n" );

        String spaces = "        ";

        _b.append( spaces ).append( "Event msg;\n" );

        boolean repeatingGroups = false;

        Iterator<Map.Entry<String, DecodeEntry>> it    = entries.entrySet().iterator();
        Map.Entry<String, DecodeEntry>           entry = null;

        Set<String> processedTags = new HashSet<>();

        while( it.hasNext() ) {
            entry = it.next();
            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                repeatingGroups = true;
                break; // WILL PROCESS SUBGROUPS LATER
            }

            processedTags.add( tag );

            if ( addHook( HookType.decode, spaces, decodeEntry ) ) {
                continue;
            }

            decodeToVar( map, spaces, tag, decodeEntry, map.getEventDefinition() );
        }

        if ( repeatingGroups && entry != null ) {
            String splitAttr = map.getDecodeSplitAttr();

            do {
                String      tag         = entry.getKey();
                DecodeEntry decodeEntry = entry.getValue();

                if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                    String curCounter = map.getEventDefinition().getRepeatingGroupCounter( tag );

                    if ( !processedTags.contains( curCounter ) ) {
                        throw new RuntimeException( "Message " + map.getBinaryMsgId() + ", counter field " + curCounter + " not present before repeating group " + tag );
                    }

                    if ( splitAttr.equals( tag ) ) {
                        decodeCondMsgWithRepeatingGrp( map, entries, tag, curCounter, spaces, relatedMsgs, condFuncName );  // THIS POPULATES THE EVENT MESSAGES
                    } else {
                        skipGroup( map, tag, curCounter, spaces );
                    }
                } else {

                    if ( !addHook( HookType.decode, spaces, decodeEntry ) ) {
                        decodeToVar( map, spaces, tag, decodeEntry, map.getEventDefinition() );
                    }
                }
                entry = it.next();

            } while( it.hasNext() );

        } else {
            _b.append( spaces ).append( "msg = " ).append( condFuncName ).append( "( null );\n" );
        }

        _b.append( "        _builder.end();\n" );
        _b.append( "        return msg;\n" );

        _b.append( "    }\n\n" );
    }

    private void decodeFieldKnownMessage( String var, BinaryEventMap map, String spaces, String tag, DecodeEntry decodeEntry ) {
        if ( addHook( HookType.decode, spaces, decodeEntry ) ) {
            return;
        }

        addHook( HookType.predecode, spaces, decodeEntry );

        decode( var, map, spaces, tag, decodeEntry, map.getEventDefinition() );

        addHook( HookType.postdecode, spaces, decodeEntry );
    }

    private void decodeKnownSplitMessage( BinaryEventMap map ) {

        Map<String, DecodeEntry> entries = getEntriesByTag( map );

        String splitAttr = map.getDecodeSplitAttr();

        ClassDefinition event = map.getClassDefinition();

        String preBinary = getPrefix( event );

        _b.append( "    private Event decode" ).append( map.getBinaryMsgId() ).append( "() {\n" );

        String spaces = "        ";

        _b.append( spaces ).append( "if ( _debug ) {\n" );
        _b.append( spaces ).append( "    _dump.append( \"\\nKnown Message : \" ).append( \"" ).append( map.getBinaryMsgId() )
          .append( "\" ).append( \" : \" );\n" );
        _b.append( spaces ).append( "}\n\n" );

        if ( event != null ) {
            String base = preBinary + event.getId();
            _b.append( "        " ).append( base ).append( "Impl msg = null;\n" );
        } else {
            _b.append( "        final Event msg;\n" ); // external event is not to be transformed into internal event, simply skipped
        }

        boolean processedRepeatGrp = false;

        Set<String> processedTags = new HashSet<>();

        // decode all fields upto first repeating group
        for ( Map.Entry<String, DecodeEntry> entry : entries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                String curCounter = map.getEventDefinition().getRepeatingGroupCounter( tag );

                if ( !processedTags.contains( curCounter ) ) {
                    throw new RuntimeException( "Event " + map.getBinaryMsgId() + ", counter field " + curCounter + " not present before repeating group " + tag );
                }

                if ( splitAttr.equals( tag ) && event != null ) {
                    processMsgSplitByRepeatGrp( map, entries, tag, curCounter, spaces );  // THIS POPULATES THE EVENT MESSAGES
                    processedRepeatGrp = true;
                } else {
                    skipGroup( map, tag, curCounter, spaces );
                }

                continue;
            }

            if ( processedRepeatGrp ) {
                throw new RuntimeException( "Error in model, repeating group fields in binary message must be grouped after all other fields, msgId=" + map.getId() + ", tag=" + tag );
            }

            decodeToVar( map, spaces, tag, decodeEntry, map.getEventDefinition() );

            processedTags.add( tag );
        }

        _b.append( "        _builder.end();\n" );

        _b.append( "        return msg;\n" );

        _b.append( "    }\n\n" );
    }

    private void decodeRoot( BinaryEventMap map, Map<String, DecodeEntry> entries, String spaces ) {
        addHook( map, spaces, HookType.predecode );

        if ( !addHook( map, spaces, HookType.decode ) ) {

            for ( Map.Entry<String, DecodeEntry> entry : entries.entrySet() ) {

                String      tag         = entry.getKey();
                DecodeEntry decodeEntry = entry.getValue();

                decodeFieldKnownMessage( "msg", map, spaces, tag, decodeEntry );
            }
        }

        addHook( map, spaces, HookType.postdecode );
    }

    private void decodeRootWithBlockSkipCheck( BinaryEventMap map, Map<String, DecodeEntry> entries, String spaces, int blockLen ) {
        startRootBlock( spaces, blockLen );

        addHook( map, spaces, HookType.predecode );

        boolean repeatingGroups = false;

        Map.Entry<String, DecodeEntry> entry = null;

        Iterator<Map.Entry<String, DecodeEntry>> entryIterator = entries.entrySet().iterator();

        while( entryIterator.hasNext() ) {
            entry = entryIterator.next();

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                repeatingGroups = true;
                break; // WILL PROCESS SUBGROUPS LATER
            }

            decodeFieldKnownMessage( "msg", map, spaces, tag, decodeEntry );
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
                DecodeEntry decodeEntry = entry.getValue();

                if ( !map.getEventDefinition().isRepeatingGroup( tag ) ) {
                    throw new RuntimeException( "Message " + map.getBinaryMsgId() + ", BlockSize's in use, repeating groups must come together at end of message, invalidField=" + tag );
                }

                decodeFieldKnownMessage( "msg", map, spaces, tag, decodeEntry );

            } while( entryIterator.hasNext() );
        }

        addHook( map, spaces, HookType.postdecode );
    }

    private void decodeToVar( BinaryEventMap map, String spaces, String tag, DecodeEntry value, BinaryEventDefinition msgDef ) {
        AttributeDefinition   attr                  = value._attrDef;
        BinaryTagEventMapping binaryTagEventMapping = value._binaryTagEventMapping;
        BinaryDictionaryTag   dictTag               = _binaryModel.getDictionaryTag( tag );
        BinaryType            extType               = (dictTag != null) ? dictTag.getBinaryType() : null;

        int len = GenUtils.getFixedLenOverride( _internal, dictTag, attr, tag, binaryTagEventMapping, msgDef );

        _b.append( spaces ).append( "if ( _debug ) _dump.append( \"\\nField: \" ).append( \"" ).append( tag ).append( "\" ).append( \" : \" );\n" );

        if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) || extType == null ) {
            String comment = msgDef.getComment( tag );
            _b.append( spaces ).append( "_builder.skip( " ).append( len ).append( " );" );
            if ( comment != null ) _b.append( "    // " ).append( comment );
            _b.append( "\n" );
        } else {
            String var = "_" + GenUtils.toLowerFirstChar( tag );

            String unsigned = getUnsignedPrefix( extType );

            switch( extType ) {
            case ch:
                _b.append( spaces ).append( var ).append( " = _builder.decodeChar();\n" );
                break;
            case sByte:
            case uByte:
                _b.append( spaces ).append( var ).append( " = _builder.decode" ).append( unsigned ).append( "Byte();\n" );
                break;
            case data:
                _b.append( spaces ).append( "_builder.decodeData( " ).append( var ).append( ", " ).append( len ).append( " );\n" );
                break;
            case fstr:
                _b.append( spaces ).append( "_builder.decodeStringFixedWidth( " ).append( var ).append( ", " ).append( len ).append( " );\n" );
                break;
            case base36:
                _b.append( spaces ).append( var ).append( " = _builder.decodeBase36Number( " ).append( len ).append( " );\n" );
                break;
            case qty:
                _b.append( spaces ).append( var ).append( " = _builder.decodeQty();\n" );
                break;
            case uInt:
            case sInt:
                _b.append( spaces ).append( var ).append( " = _builder.decode" ).append( unsigned ).append( "Int();\n" );
                break;
            case uLong:
            case sLong:
                _b.append( spaces ).append( var ).append( " = _builder.decode" ).append( unsigned ).append( "Long();\n" );
                break;
            case uShort:
            case sShort:
                _b.append( spaces ).append( var ).append( " = _builder.decode" ).append( unsigned ).append( "Short();\n" );
                break;
            case bool:
                _b.append( spaces ).append( var ).append( " = _builder.decodeBool();\n" );
                break;
            case decimal:
                _b.append( spaces ).append( var ).append( " = _builder.decodeDecimal();\n" );
                break;
            case price:
                _b.append( spaces ).append( var ).append( " = _builder.decodePrice();\n" );
                break;
            case str:
                _b.append( spaces ).append( "_builder.decodeString( " ).append( var ).append( " );\n" );
                break;
            case timeLocal:
                _b.append( spaces ).append( var ).append( " = _builder.decodeTimeLocal();\n" );
                break;
            case timeUTC:
                _b.append( spaces ).append( var ).append( " = _builder.decodeTimeUTC();\n" );
                break;
            case timestampLocal:
                _b.append( spaces ).append( var ).append( " = _builder.decodeTimestampLocal();\n" );
                break;
            case nowUTC:
            case timestampUTC:
                _b.append( spaces ).append( var ).append( " = _builder.decodeTimestampUTC();\n" );
                break;
            case zstr:
                _b.append( spaces ).append( "_builder.decodeZStringFixedWidth( " ).append( var ).append( ", " ).append( len ).append( " );\n" );
                break;
            default:
                System.out.println( "Unexpected external type" );
            }
        }
    }

    private void decodeType( String var,
                             BinaryEventMap map,
                             String tag,
                             String field,
                             TypeDefinition defn,
                             BinaryDictionaryTag dictTag,
                             int maxSize,
                             int fixedLen,
                             String spaces ) {

        String type = defn.getTypeDefinition();

        if ( fixedLen > 0 ) maxSize = fixedLen;

        if ( maxSize == 1 ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( " ).append( type ).append( ".getVal( _builder.decodeByte() ) );\n" );
        } else if ( maxSize == 2 ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( " ).append( type )
              .append( ".getVal( _binaryMsg, _builder.getCurrentIndex(), " ).append( maxSize ).append( ") );\n" );
            _b.append( spaces ).append( "_builder.skip( " ).append( maxSize ).append( ");\n" );
        } else {
            _b.append( spaces ).append( "_tmpLookupKey.setValue( _binaryMsg, _builder.getCurrentIndex(), " ).append( maxSize ).append( " );\n" );
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( " ).append( type ).append( ".getVal( _tmpLookupKey ) );\n" );
            _b.append( spaces ).append( "_builder.skip( " ).append( maxSize ).append( ");\n" );
        }
    }

    private void decodeTypeTransform( String var,
                                      BinaryEventMap map,
                                      String tag,
                                      String field,
                                      BinaryDictionaryTag dictTag,
                                      TypeDefinition defn,
                                      int maxSize,
                                      TypeTransform transform,
                                      int fixedLen,
                                      String spaces ) {

        if ( fixedLen > 0 ) maxSize = fixedLen;

        if ( addHook( HookType.decode, spaces, transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.predecode, spaces, transform.getHooks() );

        if ( maxSize == 1 ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( transform" ).append( defn.getId() )
              .append( "( _builder.decodeByte() ) );\n" );
        } else {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( transform" ).append( defn.getId() )
              .append( "( _binaryMsg, _builder.getCurrentIndex(), " ).append( maxSize ).append( " ) );\n" );
            _b.append( spaces ).append( "_builder.skip( " ).append( maxSize ).append( ");\n" );
        }

        addHook( HookType.postdecode, spaces, transform.getHooks() );
    }

    private void decodeWholeNumber( String var, String spaces, BinaryType extType, String field, String unsigned, final BinaryDictionaryTag dictTag, final BinaryTagEventMapping binaryTagEventMapping ) {
        if ( extType == BinaryType.uByte ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Byte() & 0xFF );\n" );
        } else if ( extType == BinaryType.sByte ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Byte() );\n" );
        } else if ( extType == BinaryType.uShort || extType == BinaryType.sShort ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Short() );\n" );
        } else if ( extType == BinaryType.uInt || extType == BinaryType.sInt ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Int() );\n" );
        } else if ( extType == BinaryType.uLong || extType == BinaryType.sLong ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Long() );\n" );
        } else if ( extType == BinaryType.qty ) {
            int    lenOverride = GenUtils.getFixedLenOverride( dictTag, binaryTagEventMapping );
            String lenStr      = (lenOverride == 0) ? "" : (" " + lenOverride + " ");
            if ( lenOverride > 0 ) {
                _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeQty(" + lenStr + ") );\n" );
            } else {
                _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decode" ).append( unsigned ).append( "Qty() );\n" );
            }
        } else if ( extType == BinaryType.timestampUTC ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimestampUTC() );\n" );
        } else if ( extType == BinaryType.timeLocal ) {
            _b.append( spaces ).append( var ).append( ".set" ).append( field ).append( "( _builder.decodeTimeLocal() );\n" );
        } else {
            throw new RuntimeException( "DecodeField " + field + " doesnt have expected byte/short/int/long external type, has=" + extType.toString() );
        }
    }

    private void doWriteIndivDecodeMsgs( Collection<BinaryEventMap> binaryMaps ) {
        Set<String> writtenCondMessages = new HashSet<>();

        int funcId = 1;

        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                if ( msgType != null && msgType.length() >= 1 ) {
                    if ( !_conditionalMessages.contains( msgType ) ) {
                        if ( map.getDecodeSplitAttr() == null ) {
                            decodeKnownMessageType( map );
                        } else {
                            decodeKnownSplitMessage( map );
                        }
                    } else if ( !writtenCondMessages.contains( msgType ) ) {
                        writtenCondMessages.add( msgType );
                        Set<BinaryEventMap> relatedMsgs = new HashSet<>();
                        for ( BinaryEventMap relatedMap : binaryMaps ) {
                            BinaryEventDefinition relatedFmd = relatedMap.getEventDefinition();
                            if ( relatedFmd != null ) {
                                String relatedMsgType = relatedFmd.getMsgType();
                                if ( relatedMsgType != null && relatedMsgType.equals( msgType ) ) {
                                    relatedMsgs.add( relatedMap );
                                }
                            }
                        }
                        String fname = "conditionalDecoder" + funcId++;
                        decodeConditionalMessageType( relatedMsgs, map.getBinaryMsgId(), fname );
                    }
                }
            }
        }
    }

    private void endBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( "\n" ).append( spaces ).append( "final int endBlockIdx = _builder.getCurrentIndex();\n" );
            _b.append( spaces ).append( "final int bytesToSkip = " ).append( blockSize ).append( " - (endBlockIdx - startBlockIdx);\n" );
            _b.append( spaces ).append( "if ( bytesToSkip > 0 ) _builder.skip( bytesToSkip );\n" );
        }
    }

    private void endRootBlock( String spaces, int blockSize ) {
        if ( blockSize > 0 ) {
            _b.append( "\n" ).append( spaces ).append( "final int endRootBlockIdx = _builder.getCurrentIndex();\n" );
            _b.append( spaces ).append( "final int rootBytesToSkip = _curMsgRootBlockLen - (endRootBlockIdx - startRootBlockIdx);\n" );
            _b.append( spaces ).append( "if ( rootBytesToSkip > 0 ) _builder.skip( rootBytesToSkip );\n" );
        }
    }

    private void generateFieldVars( BinaryEventMap map, StringBuilder b, Set<String> binVars, Map<String, Boolean> tags ) {
        for ( Map.Entry<String, Boolean> entry : tags.entrySet() ) {
            String tag = entry.getKey();

            if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                Map<String, Boolean> subGrpTags = _binaryModel.getSubTagMap( tag, true, false );
                generateFieldVars( map, b, binVars, subGrpTags );
                continue;
            }

            BinaryDictionaryTag dictTag = _binaryModel.getDictionaryTag( tag );
            BinaryType          extType = (dictTag != null) ? dictTag.getBinaryType() : null;

            if ( extType != null && !binVars.contains( tag ) && !canIgnore( null, tag ) ) {
                binVars.add( tag );

                String varName = "_" + GenUtils.toLowerFirstChar( tag );
                switch( extType ) {
                case ch:
                case uByte:
                case sByte:
                    b.append( "    private       byte                        " ).append( varName ).append( ";\n" );
                    break;
                case data:
                case str:
                case fstr:
                case zstr:
                    b.append( "    private       ReusableString              " ).append( varName ).append( " = new ReusableString(30);\n" );
                    break;
                case qty:
                case uInt:
                case sInt:
                    b.append( "    private       int                         " ).append( varName ).append( ";\n" );
                    break;
                case uLong:
                case sLong:
                case nowUTC:
                case timeUTC:
                case timestampLocal:
                case timestampUTC:
                case timeLocal:
                case base36:
                    b.append( "    private       long                        " ).append( varName ).append( ";\n" );
                    break;
                case uShort:
                case sShort:
                    b.append( "    private       int                         " ).append( varName ).append( ";\n" );
                    break;
                case bool:
                    b.append( "    private       boolean                     " ).append( varName ).append( ";\n" );
                    break;
                case decimal:
                case price:
                    b.append( "    private       double                      " ).append( varName ).append( ";\n" );
                    break;
                }
            }
        }
    }

    private String getUnsignedPrefix( BinaryType extType ) {
        return BinaryType.isUnsignedNumber( extType ) ? "U" : "";
    }

    private boolean hasHook( HookType hookType, DecodeEntry value ) {
        BinaryTagEventMapping explicitMapping = value._binaryTagEventMapping;
        Map<HookType, String> hooks;

        boolean hasHook = false;

        if ( explicitMapping != null ) {
            hooks = explicitMapping.getHooks();

            if ( hooks != null ) {
                String decode = hooks.get( hookType );

                if ( decode != null && decode.length() > 0 ) {
                    hasHook = true;
                }
            }
        }

        return hasHook;
    }

    private void logField( String spaces, String tag ) {
        _b.append( "\n" );
        _b.append( spaces ).append( "if ( _debug ) _dump.append( \"\\nField: \" ).append( \"" ).append( tag ).append( "\" ).append( \" : \" );\n" );
    }

    private void padd( String spaces, Set<Integer> vals ) {

        int minVal = Integer.MAX_VALUE;
        int maxVal = 0;

        for ( Integer val : vals ) {
            int iVal = val;

            if ( iVal < minVal ) minVal = iVal;
            if ( iVal > maxVal ) maxVal = iVal;
        }

        if ( minVal == Integer.MAX_VALUE || maxVal == 0 ) return;

        int range = maxVal - minVal;

        if ( range > 255 ) return;

        int added = 0;

        for ( int i = minVal + 1; i < maxVal; i++ ) {
            Integer val = i;

            if ( !vals.contains( val ) ) {
                if ( added == 0 ) _b.append( spaces );

                _b.append( "case " ).append( i ).append( ": " );

                if ( ++added % 8 == 0 ) {
                    _b.append( spaces ).append( "\n" );
                }
            }
        }

        if ( added > 0 ) {
            _b.append( "\n" ).append( spaces ).append( "    break;\n" );
        }
    }

    private void populateCondMsg( BinaryEventMap map, String spaces ) {
        ClassDefinition          event   = map.getClassDefinition();
        Map<String, DecodeEntry> entries = getEntriesByTag( map );

        String preBinary = getPrefix( event );
        String baseVar   = "_" + GenUtils.toLowerFirstChar( event.getId() );
        String base      = preBinary + event.getId();

        _b.append( spaces ).append( "final " ).append( base ).append( "Impl msg = " ).append( baseVar ).append( "Factory.get();\n" );

        populateNormalisedEvent( map, entries, spaces, map.getEventDefinition() );

        _b.append( spaces ).append( "if ( prevMsg != null ) prevMsg.attachQueue( msg );\n" );

        _b.append( spaces ).append( "return msg;\n" );
    }

    private void populateNormalisedEvent( BinaryEventMap map, Map<String, DecodeEntry> entries, String spaces, BinaryEventDefinition msgDef ) {
        addHook( map, spaces, HookType.predecode );

        for ( Map.Entry<String, DecodeEntry> entry : entries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                Map<String, DecodeEntry> grpEntries = getSubGroupEntriesByTag( map, tag );
                // POPULATE SUB GROUP
                BinaryEventDefinition subMsgDef = _binaryModel.getBinaryEvent( tag );
                populateNormalisedEvent( map, grpEntries, spaces, subMsgDef );
                continue;
            }

            if ( hasHook( HookType.decode, decodeEntry ) ) {
                continue;
            }

            addHook( HookType.predecode, spaces, decodeEntry );

            decodeConditional( map, spaces, tag, decodeEntry, msgDef );

            addHook( HookType.postdecode, spaces, decodeEntry );
        }

        addHook( map, spaces, HookType.postdecode );
    }

    private void processDecodeEntry( BinaryEventMap map, Map<String, DecodeEntry> entries, String tag, Boolean isMand ) {

        ClassDefinition event = map.getClassDefinition();

        BinaryDictionaryTag dictTag = _binaryModel.getDictionaryTag( tag );

        String name;
        if ( dictTag == null ) {
            if ( tag.toLowerCase().startsWith( GenUtils.FILLER ) ) {
                BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( null, tag );
                DecodeEntry           e                     = new DecodeEntry( tag, binaryTagEventMapping, null );
                entries.put( tag, e );
            } else if ( map.getEventDefinition().isRepeatingGroup( tag ) ) {
                BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( null, tag );
                DecodeEntry           e                     = new DecodeEntry( tag, binaryTagEventMapping, null );
                entries.put( tag, e );
                e.setRepeatingGroup( true );
            } else {
                throw new RuntimeException( "Tag " + tag + " doesnt exist in binary dictionary " + _binaryModel.getId() );
            }

            return;
        }

        name = dictTag.getId();

        BinaryTagEventMapping binaryTagEventMapping = map.getBinaryTagEventMapping( name, tag );
        String                attrName              = (binaryTagEventMapping == null) ? name : binaryTagEventMapping.getEventAttr();

        if ( canIgnore( event, tag ) ) return;

        String              eventID = (event != null) ? event.getId() : "null";
        AttributeDefinition attrDef = (event != null) ? event.getAttribute( attrName ) : null;

        if ( attrDef == null ) {
            if ( binaryTagEventMapping != null ) {
                DecodeEntry e = new DecodeEntry( tag, binaryTagEventMapping, null );
                entries.put( tag, e );
            } else if ( !isMand ) { // not intended for internal event, but still needs to be decoded
                DecodeEntry e = new DecodeEntry( tag, null, null );
                entries.put( tag, e );
            } else {
                DecodeEntry e = new DecodeEntry( tag, null, null );
                entries.put( tag, e );
                throw new RuntimeException( "Tag " + tag + " has binary dict entry of " + name +
                                            " which is missing from event " + eventID + ", binary=" + _def.getId() +
                                            ",  map=" + map.getId() );
            }
        } else if ( event != null ) {
            OutboundInstruction inst         = attrDef.getInstruction();
            boolean             isSrcSide    = true; // decoding means = src side
            boolean             isSrcClient  = (event.getStreamSrc() != EventStreamSrc.exchange);
            DelegateType        delegateType = getDelegateType( event, inst, isSrcSide, isSrcClient );

            if ( forceNoDelegate() || delegateType == DelegateType.None ) {

                DecodeEntry e = new DecodeEntry( tag, binaryTagEventMapping, attrDef );
                entries.put( tag, e );

            } else {
                // attr is to be taken from the src event not the market

                DecodeEntry e = new DecodeEntry( tag, binaryTagEventMapping, null );
                entries.put( tag, e );
            }
        }
    }

    private void processMsgSplitByRepeatGrp( BinaryEventMap map, Map<String, DecodeEntry> entries, String subGrpId, String counterTag, String spaces ) {
        Map<String, DecodeEntry> grpEntries = getSubGroupEntriesByTag( map, subGrpId );

        String counterVar = "_" + GenUtils.toLowerFirstChar( counterTag );

        ClassDefinition event     = map.getClassDefinition();
        String          preBinary = getPrefix( event );
        String          baseVar   = "_" + GenUtils.toLowerFirstChar( event.getId() );
        String          base      = preBinary + event.getId();

        int blockLen = map.getEventDefinition().getRepeatingGroupBlockLen( subGrpId );

        _b.append( "\n" ).append( spaces ).append( "{\n" );
        _b.append( spaces ).append( "    " ).append( base ).append( "Impl firstMsg = msg;\n" );

        _b.append( spaces ).append( "    for( int i=0 ; i < " ).append( counterVar ).append( " ; ++i ) { \n" );

        String spaces2 = spaces + "        ";

        startBlock( spaces2, blockLen );

        _b.append( spaces2 ).append( "if ( msg != null ) {\n" );
        _b.append( spaces2 ).append( "    final " ).append( base ).append( "Impl nxtMsg = " ).append( baseVar ).append( "Factory.get();\n" );
        _b.append( spaces2 ).append( "    msg.setNext( nxtMsg );\n" );
        _b.append( spaces2 ).append( "    msg = nxtMsg;\n" );
        _b.append( spaces2 ).append( "} else {\n" );
        _b.append( spaces2 ).append( "    firstMsg = msg = " ).append( baseVar ).append( "Factory.get();\n" );
        _b.append( spaces2 ).append( "}\n" );

        BinaryEventDefinition subMsgDef = _binaryModel.getBinaryEvent( subGrpId );

        for ( Map.Entry<String, DecodeEntry> entry : grpEntries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            decodeToVar( map, spaces2, tag, decodeEntry, subMsgDef );
        }

        populateNormalisedEvent( map, entries, spaces + "        ", map.getEventDefinition() );

        endBlock( spaces2, blockLen );
        _b.append( spaces ).append( "    }\n" );
        _b.append( spaces ).append( "    msg = firstMsg;\n" );
        _b.append( spaces ).append( "}\n" );
    }

    private void skipGroup( BinaryEventMap map, String subGrpId, String counterTag, String spaces ) {
        Map<String, DecodeEntry> entries = getSubGroupEntriesByTag( map, subGrpId );

        String counterVar = "_" + GenUtils.toLowerFirstChar( counterTag );

        _b.append( spaces ).append( "for( int i=0 ; i < " ).append( counterVar ).append( " ; ++i ) { \n" );

        BinaryEventDefinition subMsgDef = _binaryModel.getBinaryEvent( subGrpId );

        for ( Map.Entry<String, DecodeEntry> entry : entries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            decodeToVar( map, spaces + "    ", tag, decodeEntry, subMsgDef );
        }

        _b.append( spaces ).append( "}\n" );
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

    /**
     * binary decoder based on TestBinaryDecoder
     */
    private void writeDecoderAttrs( String className, StringBuilder b, BinaryCodecDefinition def ) {

        Collection<BinaryEventMap> binaryMaps = def.getBinaryEventMaps();

        Set<String> msgTypes = new HashSet<>();
        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                addEventConstant( b, msgTypes, map, msgType );
            }
        }

        String msgTypeClass = getMsgTypeClass();

        b.append( "\n    private boolean _debug = false;\n" );

        declareBuilder( b );

        b.append( "    private       " ).append( msgTypeClass ).append( " _msgType;\n" );
        b.append( "    private final byte                        _protocolVersion;\n" );
        b.append( "    private final String                      _id;\n" );
        b.append( "    private       int                         _msgStatedLen;\n" );

        b.append( "    private final ReusableString _dump  = new ReusableString(256);\n" );
        b.append( "    private final ReusableString _missedMsgTypes = new ReusableString();\n" );

        b.append( "\n    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks\n" );

        Set<String> binVars = new HashSet<>();
        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                if ( msgType != null && msgType.length() >= 1 ) {
                    Map<String, Boolean> tags = _binaryModel.getTagMap( map, true, false );

                    generateFieldVars( map, b, binVars, tags );
                }
            }
        }
    }

    private void writeDecoderMethods( StringBuilder b, String className, BinaryCodecDefinition def ) {

        b.append( "    @Override\n" );
        b.append( "    protected final int getCurrentIndex() {\n" );
        b.append( "        return _builder.getCurrentIndex();\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    protected BinaryDecodeBuilder getBuilder() {\n" );
        b.append( "        return _builder;\n" );
        b.append( "    }\n\n" );

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

        doWriteDecodeMessage( b, binaryMaps );

        doWriteIndivDecodeMsgs( binaryMaps );

        b.append( "\n    @Override public String getComponentId() { return _id; }\n" );
    }

    private void writeDecoderPools( String className, StringBuilder b, BinaryCodecDefinition def ) {

        Collection<BinaryEventMap> binaryMaps = def.getBinaryEventMaps();

        Set<String> eventSet = new HashSet<>();

        b.append( "\n" );

        for ( BinaryEventMap map : binaryMaps ) {
            String eventId = map.getEventId();

            if ( eventId == null ) continue;
            if ( eventSet.contains( eventId ) ) continue;

            eventSet.add( eventId );

            ClassDefinition event = map.getClassDefinition();

            String preBinary = getPrefix( event );
            String baseVar   = "_" + GenUtils.toLowerFirstChar( event.getId() );
            String base      = preBinary + event.getId();

            b.append( "    private final SuperPool<" ).append( base ).append( "Impl> " ).append( baseVar )
             .append( "Pool = SuperpoolManager.instance().getSuperPool( " ).append( base ).append( "Impl.class );\n" );
            b.append( "    private final " ).append( base ).append( "Factory " ).append( baseVar ).append( "Factory = new " ).append( base )
             .append( "Factory( " ).append( baseVar ).append( "Pool );\n" );
            b.append( "\n" );
        }
    }
}
