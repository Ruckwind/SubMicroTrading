/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import com.rr.model.base.type.*;
import com.rr.model.generator.transforms.TransformEncoderGenerator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BaseFixEncoderGenerator implements FixEncoderGenerator {

    @SuppressWarnings( "unused" )
    private final static Logger _logger = Logger.getLogger( "BaseEncoderGenerator" );

    private static final int SEND_TIME_TAG  = 52;
    private static final int TRANS_TIME_TAG = 60;

    private static class EncodeEntry {

        final Tag                 _tag;
        final AttributeDefinition _attrDef;
        final FixEventDefinition  _msgDef;
        final FixTagEventMapping  _fixTagEventMapping;
        boolean _repeatingGroup;

        EncodeEntry( Tag tag, AttributeDefinition attrDef, FixTagEventMapping fixTagEventMapping, FixEventDefinition messageDefinition ) {
            _tag                = tag;
            _attrDef            = attrDef;
            _fixTagEventMapping = fixTagEventMapping;
            _msgDef             = messageDefinition;
        }

        @Override public int hashCode() {
            final int prime  = 31;
            int       result = 1;
            result = prime * result + ((_tag == null) ? 0 : _tag.hashCode());
            return result;
        }

        @Override public boolean equals( Object obj ) {
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

        @Override public String toString() {
            return "EncodeEntry{" + "_tag=" + _tag + ", _repeatingGroup=" + _repeatingGroup + '}';
        }

        boolean isRepeatingGroup() { return _repeatingGroup; }

        public void setRepeatingGroup( boolean repeatingGrp ) {
            _repeatingGroup = repeatingGrp;
        }
    }
    private final Set<Integer> _ignoreClientVars = new LinkedHashSet<>();
    private final Set<Integer> _ignoreMktVars    = new LinkedHashSet<>();
    private FixModels       _fix;
    private CodecModel      _base;
    private InternalModel   _internal;
    private FixModel        _fixModel;
    private CodecDefinition _def;
    private StringBuilder _b;

    @Override
    public void generate( FixModels fix, InternalModel internal, CodecModel codecModel, CodecDefinition def, FixModel fixModel ) throws FileException, IOException {
        _base     = codecModel;
        _fixModel = fixModel;
        _internal = internal;
        _fix      = fix;
        _def      = def;

        _ignoreClientVars.add( 8 );
        _ignoreClientVars.add( 9 );
        _ignoreClientVars.add( 10 );
        _ignoreClientVars.add( 35 );

        _ignoreMktVars.add( 8 );
        _ignoreMktVars.add( 9 );
        _ignoreMktVars.add( 10 );
        _ignoreMktVars.add( 35 );

        writeEncoder( def );
    }

    protected String getBuilderClass( CodecDefinition def ) {
        String ov = def.getEncodeBuilder();
        if ( ov != null ) return ov;
        return "com.rr.core.codec.FixEncodeBuilderImpl";
    }

    private void addEncoderImports( StringBuilder b, CodecDefinition def ) {
        String builder = getBuilderClass( def );

        b.append( "import java.util.HashMap;\n" );
        b.append( "import java.util.Map;\n" );

        b.append( "import com.rr.core.utils.*;\n" );
        b.append( "import com.rr.core.lang.*;\n" );

        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.core.pool.SuperPool;\n" );
        b.append( "import com.rr.core.codec.FixEncoder;\n" );
        b.append( "import " ).append( builder ).append( ";\n" );
        b.append( "import com.rr.core.codec.RuntimeEncodingException;\n" );

        b.append( "import com.rr.model.internal.type.*;\n" );

        FixGenerator.addFixDictionaryImport( b, _fix, _fixModel );
        InternalModelGenerator.addInternalEventsFactoryWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsImplWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsInterfacesWildImport( b, _internal );
        InternalModelGenerator.addInternalTypeWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreSizeTypeImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreEventIdsImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreFullEventIdsImport( b, _internal );
    }

    private void addGapFill( int minId, int maxId, StringBuilder b, Set<Integer> eventIntSet ) {
        for ( int id = minId + 1; id < maxId; id++ ) {
            if ( !eventIntSet.contains( id ) ) {
                b.append( "        case " ).append( id ).append( ":\n" );
            }
        }
    }

    private boolean addHook( HookType hookType, String spaces, EncodeEntry value ) {
        FixTagEventMapping    explicitMapping = value._fixTagEventMapping;
        Map<HookType, String> hooks;

        boolean added = false;

        if ( explicitMapping != null ) {
            hooks = explicitMapping.getHooks();

            if ( hooks != null ) {
                String decode = hooks.get( hookType );

                if ( decode != null && decode.length() > 0 ) {
                    _b.append( spaces ).append( "    " ).append( decode ).append( ";\n" );

                    added = true;
                }
            }
        }

        return added;
    }

    private boolean addHook( HookType hookType, String spaces, Map<HookType, String> hooks ) {

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

    private void addTags( final Map<Tag, EncodeEntry> entries, final FixModel fixModel, final FixEventMap map, final ClassDefinition event, final Map<Tag, Boolean> tags ) {
        for ( Map.Entry<Tag, Boolean> entry : tags.entrySet() ) {

            Tag key = entry.getKey();

            if ( key instanceof GroupPlaceholderTag ) {
                GroupPlaceholderTag gtag = (GroupPlaceholderTag) key;

                final FixTagSet repGrp = gtag.getRepeatingGroup();

                if ( repGrp instanceof SubFixEventDefinition ) {
                    SubFixEventDefinition msgDef = (SubFixEventDefinition) repGrp;

                    FixTagEventMapping fixTagEventMapping = map.getFixTagEventMapping( null, key.getTag() );

                    EncodeEntry e = new EncodeEntry( key, null, fixTagEventMapping, msgDef );
                    entries.put( key, e );
                    e.setRepeatingGroup( true );
                    continue;
                }
            }

            if ( !(key instanceof FixTag) ) {
                continue;
            }

            FixTag  fixKey = (FixTag) key;
            Integer tag    = fixKey.getTag();

            // go thru each fix message adding to set of required fix tags
            // then iterate thru the set getting the entry from fix dictionary
            // for each fix tag get the event attr from the classDef, check type eg ViewString require start and end idx

            Boolean isMand = entry.getValue();

            if ( canIgnore( event, tag ) ) continue;

            FixDictionaryTag dictTag = fixModel.getDictionaryTag( tag );

            if ( dictTag == null ) {

                throw new RuntimeException( "Tag " + tag + " doesnt exist in fix dictionary " + fixModel.getId() );
            }

            String             name               = dictTag.getName();
            FixTagEventMapping fixTagEventMapping = map.getFixTagEventMapping( name, tag );

            String fixTagMapAttr = (fixTagEventMapping != null) ? fixTagEventMapping.getEventAttr() : null;
            String attrName      = (fixTagMapAttr == null || fixTagMapAttr.length() == 0) ? name : fixTagMapAttr;

            AttributeDefinition attrDef = event.getAttribute( attrName );

            if ( attrDef == null ) {
                if ( fixTagEventMapping != null && fixTagEventMapping.hasEncodeRelatedHook() ) {
                    EncodeEntry e = new EncodeEntry( fixKey, null, fixTagEventMapping, map.getEventDefinition() );

                    entries.put( key, e );
                } else if ( isMand )
                    throw new RuntimeException( "Tag " + tag + " has fix dict entry of " + name + " which is missing from event " + event.getId() );
            } else {

                EncodeEntry e = new EncodeEntry( fixKey, attrDef, fixTagEventMapping, map.getEventDefinition() );

                entries.put( key, e );
            }
        }
    }

    private boolean canIgnore( ClassDefinition event, Integer tag ) {

        if ( event.getStreamSrc() != EventStreamSrc.exchange ) {
            return _ignoreClientVars.contains( tag );
        }

        return _ignoreMktVars.contains( tag );
    }

    private void encode( FixEventMap map ) {

        if ( map.isHandWrittenEncode() ) {
            return;
        }

        ClassDefinition event = map.getClassDefinition();

        if ( event.isSubEvent() ) return;

        String type   = event.getId();
        String method = "encode" + type;

        _b.append( "\n    public final void " ).append( method ).append( "( final " ).append( type ).append( " msg ) {\n" );

        Map<Tag, EncodeEntry> entries = getEntriesByTag( map );

        Map<HookType, String> hooks      = map.getHooks();
        String                encodeHook = hooks.get( HookType.encode );

        if ( writeHook( encodeHook ) == false ) {
            String preHook     = hooks.get( HookType.preencode );
            String postHook    = hooks.get( HookType.postencode );
            String headerHook  = hooks.get( HookType.encodeheader );
            String trailerHook = hooks.get( HookType.encodetrailer );

            _b.append( "        final long now = _tzCalculator.getNowAsInternalTime();\n" );

            if ( headerHook != null && headerHook.length() > 0 ) {
                _b.append( "        " + headerHook + "\n" );
            } else {
                _b.append( "        _builder.start();\n" );
            }

            String msgType = map.getEventDefinition().getMsgType();

            if ( msgType.length() == 1 ) {
                _b.append( "        _builder.encodeByte( 35, MSG_" ).append( map.getFixMsgId() ).append( " );\n" );
            } else if ( msgType.length() == 2 ) {
                _b.append( "        _builder.encodeTwoByte( 35, MSG_" ).append( map.getFixMsgId() ).append( " );\n" );
            } else {
                _b.append( "        _builder.encodeBytes( 35, MSG_" ).append( map.getFixMsgId() ).append( " );\n" );
            }

            writeHook( preHook );

            for ( Map.Entry<Tag, EncodeEntry> entry : entries.entrySet() ) {

                Tag key = entry.getKey();

                FixTag  fixKey = (FixTag) key;
                Integer tag    = fixKey.getTag();
                int     iTag   = tag;

                EncodeEntry encodeEntry = entry.getValue();

                if ( addHook( HookType.encode, "        ", encodeEntry ) ) {
                    continue;
                }

                addHook( HookType.preencode, "        ", encodeEntry );

                if ( iTag == SEND_TIME_TAG || iTag == TRANS_TIME_TAG ) {
                    encodeSendTime( tag );
                } else {
                    encode( "msg", map, "        ", tag, encodeEntry );
                }

                addHook( HookType.postencode, "        ", encodeEntry );
            }

            writeHook( postHook );

            if ( trailerHook != null && trailerHook.length() > 0 ) {
                _b.append( "        " + trailerHook + "\n" );
            } else {
                _b.append( "        _builder.encodeEnvelope();\n" );
            }
        }

        _b.append( "    }\n" );
    }

    private void encode( String var, FixEventMap map, String spaces, Integer iTag, EncodeEntry value ) {
        AttributeDefinition attr = value._attrDef;

        String tag        = getFixTag( iTag );
        String commentEnd = "        // tag" + iTag + "\n";

        if ( value.isRepeatingGroup() ) {

            repeatingGroup( var, map, spaces + "", iTag, value, value._msgDef );

        } else if ( attr != null && attr.isPrimitive() ) {
            String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

            if ( GenUtils.isStringAttr( attr ) ) {
                _b.append( spaces ).append( "_builder.encodeString( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == ShortType.class ) {
                _b.append( spaces ).append( "_builder.encodeInt( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == SendTimeUTCType.class ) {
                _b.append( spaces ).append( "_builder.encodeUTCTimestamp( " ).append( tag ).append( ", now );" ).append( commentEnd );
            } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                _b.append( spaces ).append( "_builder.encodeUTCTimestamp( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == LongType.class ) {
                _b.append( spaces ).append( "_builder.encodeLong( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == DateType.class ) {
                _b.append( spaces ).append( "_builder.encodeDate( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == IntType.class ) {
                _b.append( spaces ).append( "_builder.encodeInt( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == FloatType.class ) {
                _b.append( spaces ).append( "_builder.encodePrice( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else if ( attr.getType().getClass() == DoubleType.class ) {
                FixDictionaryTag dictTag = _fixModel.getDictionaryTag( iTag );

                if ( dictTag.getFixType() == FixType.number || dictTag.getFixType() == FixType.quantity ) {
                    _b.append( spaces ).append( "_builder.encodeLong( " ).append( tag ).append( ", (long)" ).append( var ).append( ".get" ).append( field ).append( "() );" );
                } else {
                    _b.append( spaces ).append( "_builder.encodePrice( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
                }

            } else if ( attr.getType().getClass() == BooleanType.class ) {
                _b.append( spaces ).append( "_builder.encodeBool( " ).append( tag ).append( ", " ).append( var ).append( ".get" ).append( field ).append( "() );" );
            } else {
                throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                            attr.getAttrName() );
            }

        } else {
            if ( attr != null ) {
                String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

                TypeDefinition defn      = _internal.getTypeDefinition( attr.getTypeId() );
                TypeTransform  transform = _def.getTypeTransform( attr.getTypeId() );

                int maxSize = defn.getMaxEntryValueLen();

                if ( transform == null ) {
                    encodeType( map, spaces, iTag, tag, field, defn, maxSize, var );
                } else {
                    encodeTypeTransform( map, spaces, iTag, tag, field, defn, transform, maxSize, var );
                }
            }
        }

        _b.append( commentEnd );
    }

    private void encodeSendTime( Integer tag ) {
        String commentEnd = "        // tag" + tag + "\n";
        _b.append( "        _builder.encodeUTCTimestamp( " ).append( getFixTag( tag ) ).append( ", now );" ).append( commentEnd );
    }

    private void encodeSubGrpField( final String subVar, final FixEventMap map, final String spaces, final Integer tag, final EncodeEntry encodeEntry ) {
        Map<HookType, String> hooks      = map.getHooks();
        String                encodeHook = hooks.get( HookType.encode );

        if ( writeHook( encodeHook ) == false ) {
            Tag key = encodeEntry._tag;

            FixTag fixKey = (FixTag) key;
            int    iTag   = tag;

            if ( addHook( HookType.encode, spaces, encodeEntry ) ) {
                return;
            }

            addHook( HookType.preencode, spaces, encodeEntry );

            encode( subVar, map, spaces, tag, encodeEntry );

            addHook( HookType.postencode, spaces, encodeEntry );
        }
    }

    private void encodeType( FixEventMap map, String spaces, Integer iTag, String tag, String field, TypeDefinition defn, int maxSize, String mvar ) {
        if ( map.getEventDefinition().isMandatory( iTag ) ) {
            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "_builder.encodeByte( " ).append( tag ).append( ", " ).append( mvar ).append( ".get" ).append( field ).append( "().getVal() );" );
            } else if ( maxSize == 2 ) {
                _b.append( spaces ).append( "_builder.encodeTwoByte( " ).append( tag ).append( ", " ).append( mvar ).append( ".get" ).append( field ).append( "().getVal() );" );
            } else {
                _b.append( spaces ).append( "_builder.encodeBytes( " ).append( tag ).append( ", " ).append( mvar ).append( ".get" ).append( field ).append( "().getVal() );" );
            }

        } else {
            String var = "t" + field;

            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = " ).append( mvar ).append( ".get" ).append( field )
                  .append( "();\n" );
                _b.append( spaces ).append( "if ( " ).append( var ).append( " != null ) _builder.encodeByte( " ).append( tag ).append( ", " ).append( var )
                  .append( ".getVal() );" );
            } else if ( maxSize == 2 ) {
                _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = " ).append( mvar ).append( ".get" ).append( field )
                  .append( "();\n" );
                _b.append( spaces ).append( "if ( " ).append( var ).append( " != null ) _builder.encodeTwoByte( " ).append( tag ).append( ", " ).append( var )
                  .append( ".getVal() );" );
            } else {
                _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = " ).append( mvar ).append( ".get" ).append( field )
                  .append( "();\n" );
                _b.append( spaces ).append( "if ( " ).append( var ).append( " != null ) _builder.encodeBytes( " ).append( tag ).append( ", " ).append( var )
                  .append( ".getVal() );" );
            }
        }
    }

    private void encodeTypeTransform( FixEventMap map, String spaces, Integer iTag, String tag, String field, TypeDefinition defn, TypeTransform transform, int maxSize, String mvar ) {
        if ( addHook( HookType.encode, spaces, transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.preencode, spaces, transform.getHooks() );

        if ( map.getEventDefinition().isMandatory( iTag ) ) {
            if ( maxSize == 1 ) {
                _b.append( spaces ).append( "_builder.encodeByte( " ).append( tag ).append( ", transform" ).append( field ).append( "( " ).append( mvar ).append( ".get" ).append( field )
                  .append( "() ) );" );
            } else {
                _b.append( spaces ).append( "_builder.encodeString( " ).append( tag ).append( ", transform" ).append( field ).append( "( " ).append( mvar ).append( ".get" ).append( field )
                  .append( "() ) );" );
            }
        } else {
            String var = "t" + field;

            _b.append( spaces ).append( "final " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( "Base = " ).append( mvar ).append( ".get" ).append( field )
              .append( "();\n" );
            if ( maxSize == 1 ) {
                String defVal = transform.getDefaultValEncode() == null ? "Constants.UNSET_BYTE" : " DEFAULT_" + transform.getId();
                _b.append( spaces ).append( "final byte " ).append( var ).append( " = ( " ).append( var ).append( "Base == null ) ? " ).append( defVal )
                  .append( " : transform" ).append( field ).append( "( " ).append( var ).append( "Base );\n" );
                _b.append( spaces ).append( "if ( " ).append( var ).append( " != Constants.UNSET_BYTE ) _builder.encodeByte( " ).append( tag ).append( ", " )
                  .append( var ).append( " );" );
            } else {
                _b.append( spaces ).append( "final ViewString " ).append( var ).append( " = transform" ).append( field ).append( "( " ).append( mvar ).append( ".get" ).append( field )
                  .append( "() );\n" );
                _b.append( spaces ).append( "if ( " ).append( var ).append( " != null ) _builder.encodeString( " ).append( tag ).append( ", " ).append( var )
                  .append( " );" );
            }
        }

        addHook( HookType.postencode, spaces, transform.getHooks() );
    }

    private Map<Tag, EncodeEntry> getEntriesByTag( FixEventMap map ) {
        Map<Tag, EncodeEntry> entries = new LinkedHashMap<>();

        FixModel fixModel = _fix.getFixModel( _def.getFixId() );

        if ( map == null ) {
            throw new RuntimeException( "Missing fix message entries for " + map.getId() );
        }

        ClassDefinition event = map.getClassDefinition();

        boolean addParent = !event.isSubEvent();
        boolean subEvent  = event.isSubEvent();

        Map<Tag, Boolean> tags = fixModel.getTagMap( map, addParent, true, false, subEvent );

        addTags( entries, fixModel, map, event, tags );

        return entries;
    }

    private String getFixTag( Integer tag ) {
        FixDictionaryTag dtag = _fixModel.getDictionaryTag( tag );

        if ( dtag == null ) return "" + tag;

        return GenUtils.getFixDictionaryFile( _fixModel ) + "." + dtag.getName();
    }

    @SuppressWarnings( "unchecked" )
    private void repeatingGroup( String var, FixEventMap parentMap, String spaces, int iTag, EncodeEntry value, FixEventDefinition parentMsgDef ) {
        FixTagEventMapping  FixTagEventMapping = value._fixTagEventMapping;
        GroupPlaceholderTag gtag               = (GroupPlaceholderTag) value._tag;
        String              field              = gtag.getModelAttr();

        if ( FixTagEventMapping != null && FixTagEventMapping.getEventAttr() != null && FixTagEventMapping.getEventAttr().length() > 0 ) field = GenUtils.toUpperFirstChar( FixTagEventMapping.getEventAttr() );
        final String subGrp = value._msgDef.getName();
        if ( field == null ) field = GenUtils.toUpperFirstChar( subGrp );

        FixEventMap map = _def.getFixEventMap( subGrp );

        if ( map != null ) {
            _b.append( "\n" ).append( spaces ).append( "{\n" );

            String oldSpaces = spaces;

            spaces = spaces + "    ";

            addHook( HookType.predecode, spaces, value );

            Map<Tag, EncodeEntry> entries = getEntriesByTag( map );

            Map.Entry<Tag, EncodeEntry> entry;

            Iterator<Map.Entry<Tag, EncodeEntry>> entryIterator = entries.entrySet().iterator();

            String subVar     = "tmp" + GenUtils.toUpperFirstChar( subGrp );
            String counterVar = "counter" + GenUtils.toUpperFirstChar( subGrp );

            ClassDefinition event        = map.getClassDefinition();
            String          parentGetter = "get" + GenUtils.toUpperFirstChar( field );
            String          base         = event.getId();
            String          implCast     = "(" + base + "Impl)";

            _b.append( spaces ).append( base ).append( "Impl " ).append( subVar ).append( " = " ).append( implCast ).append( var ).append( "." ).append( parentGetter ).append( "();\n" );

            int                counterTag   = parentMap.getEventDefinition().getRepeatingGroupCounter( subGrp );
            FixTagEventMapping eventCounter = parentMap.getFixTagEventMapping( null, counterTag );
            String             eventGrpCountAttr;

            if ( eventCounter != null ) {
                eventGrpCountAttr = eventCounter.getEventAttr();
            } else {
                FixDictionaryTag dtag = _fixModel.getDictionaryTag( iTag );
                eventGrpCountAttr = dtag.getName();
            }

            _b.append( spaces ).append( "int " ).append( counterVar ).append( " = " ).append( var ).append( ".get" ).append( GenUtils.toUpperFirstChar( eventGrpCountAttr ) ).append( "();\n" );

            String counterFixTag = getFixTag( iTag );

            _b.append( spaces ).append( "_builder.encodeInt( " ).append( counterFixTag ).append( ", " ).append( counterVar ).append( " );\n" );

            _b.append( spaces ).append( "int i=0;\n\n" );

            _b.append( spaces ).append( "while ( " ).append( subVar ).append( " != null ) { \n" );

            String spaces2 = spaces + "    ";

            _b.append( spaces2 ).append( "++i;\n" );

            while( entryIterator.hasNext() ) {
                entry = entryIterator.next();

                final Tag   tag         = entry.getKey();
                EncodeEntry encodeEntry = entry.getValue();

                encodeSubGrpField( subVar, map, spaces2, tag.getTag(), encodeEntry );
            }

            _b.append( "\n" ).append( spaces2 ).append( subVar ).append( " = " ).append( subVar ).append( ".getNext();\n" );

            _b.append( spaces ).append( "}\n\n" );

            _b.append( spaces ).append( "if ( i != " ).append( counterVar ).append( " && ! (i==0 && Utils.isNull( " ).append( counterVar ).append( " ) ) ) {\n" );
            _b.append( spaces ).append( "    throw new RuntimeEncodingException( \"Mismatch in counters in subGroup " ).append( field )
              .append( ", found \"+ i + \" entries but expected \" + " ).append( counterVar ).append( " + \" entries" )
              .append( " : \" + msg.toString() );\n" );
            _b.append( spaces ).append( "}\n" );

            _b.append( oldSpaces ).append( "}\n\n" );

        } else {
            // the group is not in the model, the counter field is assumed to be ZERO so nothing to skip
        }
    }

    private void writeAttrs( StringBuilder b, CodecDefinition def ) {

        Collection<FixEventMap> fixMaps = def.getFixEventMaps();

        for ( FixEventMap map : fixMaps ) {
            FixEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String msgType = fmd.getMsgType();

                if ( msgType != null && msgType.length() == 1 ) {
                    b.append( "    private static final byte      MSG_" ).append( map.getFixMsgId() ).append( " = (byte)\'" ).append( msgType )
                     .append( "\';\n" );
                }
                if ( msgType != null && msgType.length() > 1 ) {
                    b.append( "    private static final byte[]    MSG_" ).append( map.getFixMsgId() ).append( " = \"" ).append( msgType )
                     .append( "\".getBytes();\n" );
                }
            }
        }

        b.append( "\n" );

        TransformEncoderGenerator.writeTransformAttrs( b, def );

        String builderClass = getBuilderClass( def );

        b.append( "    private final byte[]               _buf;\n" );
        b.append( "    private final String               _id;\n" );
        b.append( "    private final byte                 _majorVersion;\n" );
        b.append( "    private final byte                 _minorVersion;\n" );
        b.append( "    private final " ).append( builderClass ).append( " _builder;\n\n" );
        b.append( "    private final ZString              _fixVersion;\n" );
        b.append( "    private       TimeUtils            _tzCalculator = TimeUtilsFactory.createTimeUtils();\n" );
        b.append( "    private       SingleByteLookup     _sv;\n" );
        b.append( "    private       TwoByteLookup        _tv;\n" );
        b.append( "    private       MultiByteLookup      _mv;\n" );

    }

    private void writeEncodeMethod( StringBuilder b, String className, CodecDefinition def ) {
        Set<String>  eventSet    = new HashSet<>();
        Set<Integer> eventIntSet = new HashSet<>();
        b.append( "\n" );
        b.append( "    @Override\n" );
        b.append( "    public final void encode( final Event msg ) {\n" );

        Collection<FixEventMap> fixMaps = def.getFixEventMaps();
        b.append( "        switch( msg.getReusableType().getSubId() ) {\n" );

        int minId = Integer.MAX_VALUE;
        int maxId = 0;

        for ( FixEventMap map : fixMaps ) {
            String eventName = map.getEventId();

            if ( eventName == null ) continue;
            if ( eventSet.contains( eventName ) ) continue;

            eventSet.add( eventName );

            ClassDefinition event = map.getClassDefinition();

            if ( event.isSubEvent() ) continue;

            int eventIntId = event.getEventIntId();
            if ( eventIntId < minId ) minId = eventIntId;
            if ( eventIntId > maxId ) maxId = eventIntId;
            eventIntSet.add( eventIntId );

            String type   = event.getId();
            String method = "encode" + type;

            b.append( "        case " ).append( GenUtils.getEventId( event ) ).append( ":\n" );
            b.append( "            " ).append( method ).append( "( (" ).append( type ).append( ") msg );\n" );
            b.append( "            break;\n" );
        }

        addGapFill( minId, maxId, b, eventIntSet );

        b.append( "        default:\n" );
        b.append( "            _builder.start();\n" );
        b.append( "            break;\n" );

        b.append( "        }\n" );
        b.append( "    }\n" );
        b.append( "\n" );
    }

    private void writeEncoder( CodecDefinition def ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        _fixModel = _fix.getFixModel( def.getFixId() );

        String className = def.getId() + "Encoder";

        File file = GenUtils.getJavaFile( _base, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _base, ModelConstants.CODEC_PACKAGE, className );

        addEncoderImports( b, def );

        b.append( "\n@SuppressWarnings( \"unused\" )\n" );

        b.append( "\npublic final class " ).append( className );

        b.append( " implements FixEncoder {\n" );

        _b   = b;
        _def = def;

        b.append( "\n   // Member Vars\n" );
        writeAttrs( b, def );

        b.append( "\n   // Constructors\n" );
        writeEncoderConstructors( className, b, def );

        b.append( "\n   // encode methods\n" );
        writeEncoderMethods( b, className, def );

        writeOtherMethods( b, def, className );

        GenUtils.append( b, def.getEncodeInclude() );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeEncoderConstructors( String className, StringBuilder b, CodecDefinition def ) {

        switch( def.getFixId() ) {
        case "4.2":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        case "MD5.0":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        case "MD4.4":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.MDFix4_4._major, FixVersion.MDFix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.MDFix4_4._major, FixVersion.MDFix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        case "DC4.4":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        case "4.4":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        case "5.0":
            b.append( "    public " ).append( className ).append( "( String id, byte[] buf, int offset ) {\n" );
            b.append( "        this( id, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "( byte[] buf, int offset ) {\n" );
            b.append( "        this( null, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor, buf, offset );\n" );
            b.append( "    }\n\n" );
            break;
        default:
            throw new RuntimeException( "Only fix 4.2, 4.4 and 5.0 supported at present not " + def.getFixId() );
        }

        b.append( "    public " ).append( className ).append( "( byte major, byte minor, byte[] buf, int offset ) {\n" );
        b.append( "        this( null, major, minor, buf, offset );\n" );
        b.append( "    }\n\n" );

        String builder = getBuilderClass( def );

        b.append( "    public " ).append( className ).append( "( String id, byte major, byte minor, byte[] buf, int offset ) {\n" );

        b.append( "        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {\n" );
        b.append( "            throw new RuntimeException( \"Encode buffer too small only \" + buf.length + \", min=\" + SizeType.MIN_ENCODE_BUFFER.getSize() );\n" );
        b.append( "        }\n" );

        b.append( "        _buf = buf;\n" );
        b.append( "        _id = id;\n" );
        b.append( "        _majorVersion = major;\n" );
        b.append( "        _minorVersion = minor;\n" );
        b.append( "        _builder = new " ).append( builder ).append( "( buf, offset, major, minor );\n" );
        b.append( "        _fixVersion   = new ViewString( \"FIX.\" + (char)major + \".\" + (char)minor );\n" );
        b.append( "    }\n\n" );

        b.append( "    public " ).append( className ).append( "( String id, byte major, byte minor, byte[] buf ) {\n" );
        b.append( "        this( id, major, minor, buf, 0 );\n" );
        b.append( "    }\n\n" );

        b.append( "    public " ).append( className ).append( "( byte major, byte minor, byte[] buf ) {\n" );
        b.append( "        this( null, major, minor, buf, 0 );\n" );
        b.append( "    }\n\n" );
    }

    private void writeEncoderMethods( StringBuilder b, String className, CodecDefinition def ) {

        writeEncodeMethod( b, className, def );

        b.append( "    @Override public final int getLength() { return _builder.getLength(); }\n" );
        b.append( "    @Override public final int getOffset() { return _builder.getOffset(); }\n" );

        b.append( "\n" );

        Set<String>             eventSet = new HashSet<>();
        Collection<FixEventMap> fixMaps  = def.getFixEventMaps();

        for ( FixEventMap map : fixMaps ) {
            String eventId = map.getEventId();

            if ( eventId == null ) continue;
            if ( eventSet.contains( eventId ) ) continue;

            eventSet.add( eventId );

            // each event can only be encoded to a single  message map
            // note decoding can have multiple message maps to single event

            encode( map );
        }
    }

    private boolean writeHook( String hook ) {

        if ( hook == null || hook.length() == 0 ) return false;

        _b.append( "        " ).append( hook ).append( ";        // HOOK\n" );

        return true;
    }

    private void writeOtherMethods( StringBuilder b, CodecDefinition def, final String className ) {
        b.append( "    @Override\n" );
        b.append( "    public final byte[] getBytes() {\n" );
        b.append( "        return _buf;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void setTimeUtils( final TimeUtils calc ) {\n" );
        b.append( "        _tzCalculator = calc;\n" );
        b.append( "        _builder.setTimeUtils( calc );\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override public FixEncoder newInstance() {\n" );
        b.append( "        final byte[] buf = new byte[ _builder.getBuffer().length ];\n" );
        b.append( "        final int offset = _builder.getStartOffset();\n" );
        b.append( "        " ).append( className ).append( " e = new " ).append( className ).append( "( getComponentId(), _majorVersion, _minorVersion, buf, offset );\n" );
        b.append( "        e.setSenderCompId( _senderCompId );\n" );
        b.append( "        e.setSenderSubId( _senderSubId );\n" );
        b.append( "        e.setTargetCompId( _targetCompId );\n" );
        b.append( "        e.setTargetSubId( _targetSubId );\n" );
        b.append( "        e.setSenderLocationId( _senderLocationId );\n" );
        b.append( "        return e;\n" );
        b.append( "    }\n" );

        b.append( "\n    @Override public String getComponentId() { return _id; }\n" );

        TransformEncoderGenerator.writeEncoderTransforms( b, def );
    }
}
