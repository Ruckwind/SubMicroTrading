/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.codec.FixDecoder;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.ZString;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.InstrumentLocator;
import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import com.rr.model.base.type.*;
import com.rr.model.generator.transforms.TransformDecoderGenerator;
import com.rr.model.internal.type.ExecType;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings( "unused" )

public class BaseFixDecoderGenerator implements FixDecoder, FixDecoderGenerator {

    private final static Logger _logger = Logger.getLogger( "BaseDecodecGenerator" );

    private static final String MSG_TYPE_EXEC_RPT = "8";

    private static final int CHECKSUM_TAG = 10;

    private static class SubGrpEntry {

        final String              _msgImpl;
        final GroupPlaceholderTag _grpTag;
        final DecodeEntry         _value;

        public SubGrpEntry( String msgImpl, GroupPlaceholderTag grpTag, DecodeEntry value ) {
            _msgImpl = msgImpl;
            _grpTag  = grpTag;
            _value   = value;
        }

        @Override
        public int hashCode() {
            final int prime  = 31;
            int       result = 1;
            result = prime * result + ((_grpTag == null) ? 0 : _grpTag.hashCode());
            result = prime * result + ((_msgImpl == null) ? 0 : _msgImpl.hashCode());
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
            SubGrpEntry other = (SubGrpEntry) obj;
            if ( _grpTag == null ) {
                if ( other._grpTag != null )
                    return false;
            } else if ( !_grpTag.equals( other._grpTag ) )
                return false;
            if ( _msgImpl == null ) {
                return other._msgImpl == null;
            } else return _msgImpl.equals( other._msgImpl );
        }
    }

    private static class DecodeEntry {

        final Integer             _tag;
        final Boolean             _isMand;
        final FixDictionaryTag    _dictTag;
        final AttributeDefinition _attrDef;
        final String              _eventId;
        final FixEventType        _fixMsgId;
        final FixTagEventMapping  _fixTagEventMapping;

        DecodeEntry( Integer tag,
                     Boolean isMand,
                     FixDictionaryTag dictTag,
                     FixTagEventMapping fixTagEventMapping,
                     AttributeDefinition attrDef,
                     String eventId,
                     FixEventType fixMsgId ) {

            _tag                = tag;
            _isMand             = isMand;
            _dictTag            = dictTag;
            _fixTagEventMapping = fixTagEventMapping;
            _attrDef            = attrDef;
            _eventId            = eventId;
            _fixMsgId           = fixMsgId;
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

        @Override
        public String toString() {
            return "tag=" + _tag + ", attr=" + ((_attrDef == null) ? "null" : _attrDef.getAttrName()) + ", mapping=" + ((_fixTagEventMapping == null) ? "null" : _fixTagEventMapping.toString());
        }
    }
    private final Set<Integer> _ignoreClientVars = new LinkedHashSet<>();
    private final Set<Integer> _ignoreMktVars    = new LinkedHashSet<>();
    private final Set<Integer> _forceVars        = new LinkedHashSet<>();
    private final Set<String>  _poolsWritten     = new LinkedHashSet<>();
    private final Set<SubGrpEntry> _subGrps = new LinkedHashSet<>();
    private final boolean _enableUltraLowLatencyHacks = false;
    private FixModels       _fix;
    private CodecModel      _base;
    private InternalModel   _internal;
    private FixModel        _fixModel;
    private CodecDefinition _def;
    private StringBuilder _b;
    private String        _className;
    private boolean       _isRecovery;

    public BaseFixDecoderGenerator() {
    }

    @Override public Event decode( byte[] fixMsg, int offset, int maxIdx )            { return null; }

    @Override public InstrumentLocator getInstrumentLocator()                         { return null; }

    @Override public void setInstrumentLocator( InstrumentLocator instrumentLocator ) { /* not required */ }

    @Override public int getLength()                                                  { return 0; }

    @Override public long getReceived()                                               { return 0; }

    @Override public void setReceived( long nanos )                                   { /* not required */ }

    @Override public int getSkipBytes()                                               { return 0; }

    @Override public int parseHeader( byte[] inBuffer, int inHdrLen, int bytesRead )  { return 0; }

    // strictly speaking should cleanly seperate the fix decoder methods into simple interface the base decoder generator can implements
    @Override public Event postHeaderDecode() { return null; }

    @Override public ResyncCode resync( byte[] fixMsg, int offset, int maxIdx )       { return null; }

    @Override public void setClientProfile( ClientProfile client )                    { /* not required */ }

    @Override public void setNanoStats( boolean nanoTiming )                          { /* not required */ }

    @Override public void setTimeUtils( TimeUtils calc )                              { /* not required */ }

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

        if ( _enableUltraLowLatencyHacks ) {
            _ignoreMktVars.add( 52 );
            _ignoreMktVars.add( 60 );
        } else {
            _forceVars.add( 22 );
            _forceVars.add( 48 );
            _forceVars.add( 207 );
        }

        /**
         * @NOTE For Ultra Low Latency consider disabling tag 52 and 60
         */

        _subGrps.clear();

        if ( def.hasOMSEvents() ) {
            _isRecovery = false;
            writeDecoder( def, true );

            _isRecovery = true;
            writeDecoder( def, true );

        } else {
            _isRecovery = true;
            writeDecoder( def, false );
        }
    }

    @Override public String getComponentId()                                          { return null; }

    @Override public boolean isVerifyHdrVals()                                        { return false; }

    @Override public void setVerifyHdrVals( final boolean verifyHdrVals )             { /* not required */ }

    @Override public void setSenderCompId( ZString senderCompId )                     { /* not required */ }

    @Override public void setSenderSubId( ZString senderSubId )                       { /* not required */ }

    @Override public void setTargetCompId( ZString targetCompId )                     { /* not required */ }

    @Override public void setTargetSubId( ZString targetSubId )                       { /* not required */ }

    @Override public void setValidateChecksum( boolean doValidate )                   { /* not required */ }

    @Override public FixDecoder newInstance()                                         { return null; }

    /**
     * use the FixDecoder contract to ensure all decode methods are considered
     */

    public Event decodeExecReport() {

        _b.append( "    public final Event decodeExecReport() {\n" );

        // reset vars

        execRptResetVars();

        execRptSwitchForFields( FixEventMap.MAX_FORCE_TABLESWITCH );

        addExecReportVersionSpecifics();

        execRptGetExecMsg();

        _b.append( "    }\n\n" );

        populateExecRpts();

        return null;
    }

    public void decodeKnownMessageType( FixEventType fixMsgType, int maxTableSwitch ) {
        String                spaces  = "            ";
        Map<Tag, DecodeEntry> entries = new LinkedHashMap<>();
        getEntriesByTag( fixMsgType, entries );
        FixEventMap map = _def.getFixEventMap( fixMsgType );

        FixEventDefinition fixDefn = map.getEventDefinition();
        ClassDefinition    event   = map.getClassDefinition();

        String preFix  = getPrefix( event );
        String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
        String base    = preFix + event.getId();

        String msgImpl = base + "Impl";

        _b.append( "    public final Event decode" ).append( fixMsgType ).append( "() {\n" );

        resetForceSwitch( spaces, entries );

        _b.append( "        final " ).append( msgImpl ).append( " msg = " ).append( baseVar ).append( "Factory.get();\n" );

        addHook( map, HookType.predecode );

        _b.append( "        _tag = getTag();\n\n" );

        _b.append( "        int start;\n" );
        _b.append( "        int valLen;\n\n" );

        _b.append( "        while( _tag != 0 ) {\n" );

        List<FieldGrouper.Band> bands = getBands( entries, map );

        String tSpaces = spaces;

        int idx = 0;

        for ( FieldGrouper.Band band : bands ) {
            addSwitchBlock( tSpaces, entries, band, msgImpl, map.getRejectTags(), fixMsgType, map );
            _b.append( tSpaces + "default:\n" );
            tSpaces += "    ";
            ++idx;
        }

        _b.append( tSpaces ).append( "getValLength();\n" );
        _b.append( tSpaces ).append( "break;\n" );

        for ( int i = bands.size() - 1; i >= 0; --i ) {

            tSpaces = spaces;
            for ( int k = 0; k < i; k++ ) {
                tSpaces += "    ";
            }

            _b.append( tSpaces + "}\n" );
        }

        _b.append( "            _idx++; /* past delimiter */ \n" );
        _b.append( "            _tag = getTag();\n" );

        _b.append( "        }\n\n" );

        if ( !_isRecovery && event.hasUseViewString() && event.getStreamSrc() == EventStreamSrc.client ) {
            _b.append( "        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {\n" );
            _b.append( "            throw new RuntimeDecodingException( \"" ).append( fixMsgType )
              .append( "Message too big \" + _idx + \", max=\" + SizeType.VIEW_NOS_BUFFER.getSize() );\n" );
            _b.append( "        }\n" );

            _b.append( "        msg.setViewBuf( _fixMsg, _offset, _idx );\n\n" );
        }

        addHook( map, HookType.postdecode );

        _b.append( "        return msg;\n" );

        _b.append( "    }\n\n" );
    }

    public void getSubGrpDecodeEntries( final FixEventMap map, ClassDefinition subGroupClassDefn, Map<Tag, Boolean> tags, Map<Tag, DecodeEntry> decodeEntries ) {
        for ( Map.Entry<Tag, Boolean> entry : tags.entrySet() ) {

            Tag key = entry.getKey();

            FixTag  fixKey = (FixTag) key;
            Integer tag    = fixKey.getTag();

            Boolean isMand = entry.getValue();

            FixDictionaryTag dictTag = _fixModel.getDictionaryTag( tag );
            String           eventId = subGroupClassDefn.getId();

            if ( dictTag == null ) {
                throw new RuntimeException( "Tag " + tag + " doesnt exist in fix dictionary " + _fixModel.getId() );
            }

            String dictName      = dictTag.getName();
            String eventAttrName = dictName;

            String             name               = dictTag.getName();
            FixTagEventMapping fixTagEventMapping = (map != null) ? map.getFixTagEventMapping( name, tag ) : null;

            addDecodeEntry( decodeEntries, fixKey, tag, isMand, dictTag, eventId, null, subGroupClassDefn, dictName, fixTagEventMapping );
        }
    }

    protected String getInternalTime() {
        return "getInternalTime()";
    }

    private void addCaseDecoding( String msgImpl, String spaces, Tag keyTag, DecodeEntry value ) {
        Integer tag = keyTag.getTag();

        addHook( HookType.predecode, spaces, value );

        if ( keyTag instanceof GroupPlaceholderTag ) {
            GroupPlaceholderTag grpTag = (GroupPlaceholderTag) keyTag;

            AttributeDefinition attr = value._attrDef;

            String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

            String grpProcessor = grpProcessFuncName( grpTag );
            String numVar       = GenUtils.toLowerFirstChar( grpTag.getId() ) + "Num";

            _b.append( spaces ).append( "    int " ).append( numVar ).append( " = getIntVal(); // past delimiter\n" );
            _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( " ).append( numVar ).append( " );\n" );
            _b.append( spaces ).append( "    if ( " ).append( numVar ).append( " > 0 ) {\n" );
            _b.append( spaces ).append( "        _idx++; // past delimiter IF subgroups exist\n" );
            _b.append( spaces ).append( "        " ).append( grpProcessor ).append( "( msg, " ).append( numVar ).append( " );\n" );
            _b.append( spaces ).append( "        continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE\n" );
            _b.append( spaces ).append( "    }\n" );

            addHook( HookType.postdecode, spaces, value );

            _b.append( spaces ).append( "    break;\n" );

            _subGrps.add( new SubGrpEntry( msgImpl, grpTag, value ) );

            return;
        }

        if ( tag == CHECKSUM_TAG ) {
            _b.append( spaces ).append( "    validateChecksum( getIntVal() );\n" );

        } else {
            AttributeDefinition attr = value._attrDef;

            if ( attr == null ) {
                if ( _forceVars.contains( tag ) ) {
                    final FixDictionaryTag fixTag  = _fixModel.getDictionaryTag( tag );
                    String                 baseVar = "_" + GenUtils.toLowerFirstChar( fixTag.getName() );
                    _b.append( spaces ).append( "    " ).append( baseVar ).append( "Start = _idx;\n" );
                    _b.append( spaces ).append( "    " ).append( baseVar ).append( "Len = getValLength();\n" );
                } else {
                    _b.append( spaces ).append( "    getValLength(); // no model attribute, SKIP\n" );
                }
            } else if ( attr.isPrimitive() ) {
                String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

                if ( attr.getType().getClass() == ReusableStringType.class ) {
                    _b.append( spaces ).append( "    start = _idx;\n" );
                    _b.append( spaces ).append( "    valLen = getValLength();\n" );
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( _fixMsg, start, valLen );\n" );
                } else if ( attr.getType().getClass() == ViewStringType.class ) {
                    if ( _isRecovery ) { // recovery classes override use of viewstring
                        _b.append( spaces ).append( "    start = _idx;\n" );
                        _b.append( spaces ).append( "    valLen = getValLength();\n" );
                        _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( _fixMsg, start, valLen );\n" );
                    } else {
                        _b.append( spaces ).append( "    start = _idx - _offset;\n" );
                        _b.append( spaces ).append( "    valLen = getValLength();\n" );
                        _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( start, valLen );\n" );
                    }
                } else if ( attr.getType().getClass() == ShortType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getIntVal() );\n" );
                } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( " ).append( getInternalTime() ).append( " );\n" );
                } else if ( attr.getType().getClass() == LongType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getLongVal() );\n" );
                } else if ( attr.getType().getClass() == DateType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getIntVal() );\n" );
                } else if ( attr.getType().getClass() == IntType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getIntVal() );\n" );
                } else if ( attr.getType().getClass() == FloatType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getFloatVal() );\n" );
                } else if ( attr.getType().getClass() == DoubleType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( getDoubleVal() );\n" );
                } else if ( attr.getType().getClass() == BooleanType.class ) {
                    _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( _fixMsg[_idx++]=='Y' );\n" );
                } else {
                    throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                                attr.getAttrName() );
                }
            } else {
                String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

                TypeTransform transform = _def.getTypeTransform( attr.getTypeId() );

                TypeDefinition defn    = _internal.getTypeDefinition( attr.getTypeId() );
                int            maxSize = defn.getMaxEntryValueLen();

                if ( transform == null ) {
                    decodeType( field, defn, maxSize, spaces );
                } else {
                    decodeTypeTransform( field, defn, maxSize, transform, spaces );
                }
            }
        }

        addHook( HookType.postdecode, spaces, value );

        _b.append( spaces ).append( "    break;\n" );
    }

    private boolean addCaseEntry( String spaces, Tag keyTag, DecodeEntry value ) {
        Integer tag = keyTag.getTag();

        String fixTag = getFixTag( tag );

        _b.append( spaces ).append( "case " ).append( fixTag ).append( ":         // tag" ).append( tag ).append( "\n" );

        if ( addHook( HookType.decode, spaces, value ) ) {
            _b.append( spaces ).append( "    break;\n" );
            return false;
        }

        return true;
    }

    private void addDecodeEntry( Map<Tag, DecodeEntry> entries,
                                 FixTag fixKey,
                                 Integer tag,
                                 Boolean isMand,
                                 FixDictionaryTag dictTag,
                                 String eventId,
                                 FixEventType fixMsgId,
                                 ClassDefinition event,
                                 String dictName,
                                 FixTagEventMapping fixTagEventMapping ) {

        String eventAttrName;

        if ( canIgnore( event, tag ) ) return;

        AttributeDefinition attrDef = event.getAttribute( dictName );

        if ( attrDef == null && fixTagEventMapping != null && fixTagEventMapping.getEventAttr() != null ) {
            eventAttrName = fixTagEventMapping.getEventAttr();
            attrDef       = event.getAttribute( eventAttrName );
        }

        if ( attrDef == null ) {
            if ( fixTagEventMapping != null && fixTagEventMapping.hasDecodeRelatedHook() ) {
                DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, attrDef, eventId, fixMsgId );

                entries.put( fixKey, e );
            } else {
                if ( isMand ) {
                    throw new RuntimeException( "Tag " + tag + " has fix dict entry of " + dictName +
                                                " which is missing from event " + event.getId() + ", fix=" + _def.getFixId() );
                }

                if ( event.isSubEvent() ) {
                    DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, null, eventId, fixMsgId );

                    entries.put( fixKey, e );
                }
            }
        } else {
            OutboundInstruction inst        = attrDef.getInstruction();
            boolean             isSrcSide   = true; // decoding means = src side
            boolean             isSrcClient = (event.getStreamSrc() != EventStreamSrc.exchange);

            EventStreamSrc streamSrc    = _isRecovery ? EventStreamSrc.recovery : event.getStreamSrc();
            DelegateType   delegateType = GenUtils.getDelegateType( streamSrc, isSrcSide, isSrcClient, inst );

            if ( delegateType == DelegateType.None ) {

                DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, attrDef, eventId, fixMsgId );

                // System.out.println( "addDecodeEntry() event " + event.getId() + ", fixKey=" + fixKey + ", decodeEntry " + e.toString() );

                entries.put( fixKey, e );

            } else {
                // attr is to be taken from the src event not the market
            }
        }
    }

    private void addDecoderImports( StringBuilder b, CodecDefinition def ) {
        b.append( "import java.util.HashMap;\n" );
        b.append( "import java.util.Map;\n" );

        b.append( "import com.rr.core.utils.*;\n" );
        b.append( "import com.rr.core.codec.*;\n" );
        b.append( "import com.rr.core.lang.*;\n" );
        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.factories.*;\n" );

        b.append( "import com.rr.core.pool.SuperPool;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.model.internal.type.*;\n" );
        b.append( "import com.rr.core.codec.RuntimeDecodingException;\n" );
        b.append( "import com.rr.core.utils.StringUtils;\n" );

        FixGenerator.addFixDictionaryImport( b, _fix, _fixModel );
        InternalModelGenerator.addInternalEventsFactoryWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsImplWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsInterfacesWildImport( b, _internal );
        InternalModelGenerator.addInternalTypeWildImport( b, _internal );
        InternalModelGenerator.addInternalEventsCoreSizeTypeImport( b, _internal );
    }

    private void addDirect( String msgImpl, String spaces, Tag keyTag, DecodeEntry value ) {

        if ( !addCaseEntry( spaces, keyTag, value ) ) {
            return; // manual decode
        }

        addCaseDecoding( msgImpl, spaces, keyTag, value );
    }

    private void addExecReport( FixEventType fix, FixEventMap map ) {

        Map<Tag, DecodeEntry> entries = new LinkedHashMap<>();

        getEntriesByTag( fix, entries );

        FixEventDefinition fixDefn = map.getEventDefinition();
        ClassDefinition    event   = map.getClassDefinition();

        String preFix  = getPrefix( event );
        String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
        String base    = preFix + event.getId();

        _b.append( "    public final Event populateExecRpt" ).append( fixDefn.getExecType() ).append( "() {\n" );

        _b.append( "        " ).append( base ).append( "Impl msg = " ).append( baseVar ).append( "Factory.get();\n" );

        addHook( map, HookType.predecode );

        if ( haveComplexTypes( entries ) ) {
            _b.append( "        int start;\n" );
            _b.append( "        int valLen;\n" );
        }

        String spaces = "        ";

        for ( DecodeEntry entry : entries.values() ) {

            if ( addHook( HookType.decode, "    ", entry ) ) {
                continue;
            }

            AttributeDefinition attr = entry._attrDef;

            addHook( HookType.predecode, spaces, entry );

            if ( attr != null ) {
                String field = GenUtils.toUpperFirstChar( attr.getAttrName() );

                if ( attr.isPrimitive() ) {
                    if ( GenUtils.isStringAttr( attr ) ) {
                        String startVar = mkStartVar( attr );
                        String lenVar   = mkLenVar( attr );

                        _b.append( spaces ).append( "if ( " ).append( lenVar ).append( " > 0 ) msg.set" ).append( field ).append( "( _fixMsg, " )
                          .append( startVar ).append( ", " ).append( lenVar ).append( " );\n" );
                    } else {

                        String var = "_" + attr.getAttrName();

                        _b.append( spaces ).append( "msg.set" ).append( field ).append( "( " ).append( var ).append( " );\n" );
                    }

                } else {
                    TypeDefinition defn = _internal.getTypeDefinition( attr.getTypeId() );
                    String         var  = "_" + GenUtils.toLowerFirstChar( defn.getId() );

                    _b.append( spaces ).append( "msg.set" ).append( field ).append( "( " ).append( var ).append( " );\n" );
                }
            }

            addHook( HookType.postdecode, "    ", entry );
        }

        addHook( map, HookType.postdecode );

        _b.append( "        return msg;\n" );
        _b.append( "    }\n\n" );
    }

    private void addExecReportVersionSpecifics() {
        if ( GenUtils.isFixVersionBefore( "4.3", _def.getFixId() ) ) {
            _b.append( "        preExecMessageDetermination();\n\n" );
        }
    }

    private void addExecRptCaseEntry( String spaces, Integer tag, DecodeEntry value ) {
        String fixTag = getFixTag( tag );

        _b.append( spaces ).append( "case " ).append( fixTag ).append( ":         // tag" ).append( tag ).append( "\n" );

        AttributeDefinition attr = value._attrDef;

        if ( attr == null ) { // must be a complex type requiring hooks to handle
            Map<Integer, String> complexTypes = _def.getComplexTypes();
            String               complexType  = complexTypes.get( tag );
            TypeDefinition       defn         = _internal.getTypeDefinition( complexType );

            addExecRptCaseEntryComplex( spaces, tag, value, defn, defn.getId() );

        } else if ( attr.isPrimitive() ) {

            String attrName = "_" + attr.getAttrName();

            if ( GenUtils.isStringAttr( attr ) ) {
                String startVar = mkStartVar( attr );
                String lenVar   = mkLenVar( attr );
                _b.append( spaces ).append( "    " ).append( startVar ).append( " = _idx;\n" );
                _b.append( spaces ).append( "    " ).append( lenVar ).append( " = getValLength();\n" );
            } else if ( attr.getType().getClass() == ShortType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getShortVal();\n" );
            } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = " ).append( getInternalTime() ).append( ";\n" );
            } else if ( attr.getType().getClass() == LongType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getLongVal();\n" );
            } else if ( attr.getType().getClass() == DateType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getIntVal();\n" );
            } else if ( attr.getType().getClass() == IntType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getIntVal();\n" );
            } else if ( attr.getType().getClass() == FloatType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getFloatVal();\n" );
            } else if ( attr.getType().getClass() == DoubleType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = getDoubleVal();\n" );
            } else if ( attr.getType().getClass() == BooleanType.class ) {
                _b.append( spaces ).append( "    " ).append( attrName ).append( " = (_fixMsg[_idx++] == 'Y');\n" );
            } else {
                throw new RuntimeException( "Expected prmitive type not " + attr.getType().getClass().getSimpleName() + " attr=" +
                                            attr.getAttrName() );
            }

        } else {
            // msg.setHandlInst( HandlInst.getVal( _fixMsg[_idx++] ) );

            TypeTransform transform = _def.getTypeTransform( attr.getTypeId() );

            TypeDefinition defn    = _internal.getTypeDefinition( attr.getTypeId() );
            int            maxSize = defn.getMaxEntryValueLen();

            if ( transform == null ) {
                addExecRptCaseEntryComplex( spaces, tag, value, defn, attr.getAttrName() );
            } else {
                addExecTypeTransform( spaces, tag, value, defn, attr.getAttrName(), transform );
            }
        }

        _b.append( spaces ).append( "    break;\n" );
    }

    private void addExecRptCaseEntryComplex( String spaces, Integer tag, DecodeEntry value, TypeDefinition defn, String attrName ) {
        String var     = " _" + GenUtils.toLowerFirstChar( defn.getId() );
        String field   = GenUtils.toUpperFirstChar( attrName );
        int    maxSize = defn.getMaxEntryValueLen();

        if ( maxSize == 1 ) {
            _b.append( spaces ).append( "    " ).append( var ).append( " = " ).append( field ).append( ".getVal( _fixMsg[_idx++] );\n" );
        } else if ( maxSize == 2 ) {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    " ).append( var ).append( " = " ).append( field ).append( ".getVal( _fixMsg, start, valLen );\n" );
        } else {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    " ).append( var ).append( " = " ).append( field ).append( ".getVal( _tmpLookupKey.copy(_fixMsg, start, valLen) );\n" );
        }
    }

    private void addExecTypeTransform( String spaces, Integer tag, DecodeEntry value, TypeDefinition defn, String attrName, TypeTransform transform ) {
        String var     = " _" + GenUtils.toLowerFirstChar( defn.getId() );
        String field   = GenUtils.toUpperFirstChar( attrName );
        int    maxSize = defn.getMaxEntryValueLen();

        if ( addHook( HookType.decode, spaces, transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.predecode, spaces, transform.getHooks() );

        if ( maxSize == 1 ) {
            _b.append( spaces ).append( "    " ).append( var ).append( " = transform" ).append( field ).append( "( _fixMsg[_idx++] );\n" );
        } else {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    " ).append( var ).append( " = transform" ).append( field ).append( "( _fixMsg, start, valLen );\n" );
        }

        addHook( HookType.postdecode, spaces, transform.getHooks() );
    }

    private boolean addHook( FixEventMap map, HookType hookType ) {

        if ( map == null ) return false;

        Map<HookType, String> hooks = map.getHooks();

        boolean added = false;

        if ( hooks != null ) {
            String decode = hooks.get( hookType );

            if ( decode != null && decode.length() > 0 ) {
                _b.append( "        " ).append( decode ).append( ";\n" );

                added = true;
            }
        }

        return added;
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

    /**
     * @return TRUE if hook added
     */
    private boolean addHook( HookType hookType, String spaces, DecodeEntry value ) {
        int                   tag             = value._tag;
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

    private void addNYI( String msgType ) {

        _b.append( "    @Override\n\n" );
        _b.append( "    public final Event decode" ).append( msgType ).append( "() {\n" );
        _b.append( "        throwDecodeException( \"Unsupported message type " ).append( msgType ).append( "\" );\n" );
        _b.append( "        return null;\n" );
        _b.append( "    }\n\n" );
    }

    private void addPool( StringBuilder b, String grpId ) {
        String preFix  = "";
        String baseVar = "_" + GenUtils.toLowerFirstChar( grpId );
        String base    = preFix + grpId;

        if ( _poolsWritten.contains( base ) ) return;

        _poolsWritten.add( base );

        b.append( "\n    private final SuperPool<" ).append( base ).append( "Impl> " ).append( baseVar )
         .append( "Pool = SuperpoolManager.instance().getSuperPool( " ).append( base ).append( "Impl.class );\n" );
        b.append( "    private final " ).append( base ).append( "Factory " ).append( baseVar ).append( "Factory = new " ).append( base ).append( "Factory( " )
         .append( baseVar ).append( "Pool );\n" );
        b.append( "\n" );
    }

    @SuppressWarnings( "unchecked" )
    private void addSubGrpProcessor( StringBuilder b, CodecDefinition def, SubGrpEntry subGrp ) {

        String              msgImpl = subGrp._msgImpl;
        GroupPlaceholderTag grpTag  = subGrp._grpTag;
        DecodeEntry         value   = subGrp._value;

        // fix subgroup must have same name as model sub group
        ClassDefinition subGroupClassDefn = _internal.getClassDefinition( grpTag.getId() );

        if ( subGroupClassDefn == null ) throw new RuntimeException( "Unable to find model sub group entry to match fix sub group id=" + grpTag.getId() );

        String funcName      = grpProcessFuncName( grpTag );
        String subGrpAttr    = GenUtils.toUpperFirstChar( grpTag.getModelAttr() );
        String subGrpType    = grpTag.getId();
        String subGrpImpl    = subGrpType + "Impl";
        String subGrpFactory = "_" + GenUtils.toLowerFirstChar( subGrpType ) + "Factory";

        SubFixEventDefinition subFixMessageDefinition = (SubFixEventDefinition) grpTag.getRepeatingGroup();
        Map<Tag, Boolean>     tags                    = new LinkedHashMap<>();
        subFixMessageDefinition.getTagMap( tags, false, false );
        Map<Tag, DecodeEntry> decodeEntries = new LinkedHashMap<>();

        FixEventMap map = _def.getFixEventMap( subGrpType );

        getSubGrpDecodeEntries( map, subGroupClassDefn, tags, decodeEntries );

        if ( decodeEntries.size() > 1 ) {
            _b.append( "\n    @SuppressWarnings( \"null\" )\n" );
        }

        _b.append( "    private void " ).append( funcName ).append( "( " ).append( msgImpl ).append( " parent, int numEntries ) {\n\n" );

        _b.append( "       " ).append( subGrpImpl ).append( " msg = null;\n" );
        _b.append( "       " ).append( subGrpImpl ).append( " lastUpdate = null;\n" );
        _b.append( "       _tag = getTag();\n\n" );

        _b.append( "       int start;\n" );
        _b.append( "       int valLen;\n\n" );

        String spaces = "           ";

        addHook( map, HookType.predecode );

        Tag startMsgTag = decodeEntries.entrySet().iterator().next().getKey();

        List<FieldGrouper.Band> bands = getBands( decodeEntries, map );

        _b.append( "       while( _tag != 0 ) {\n" );

        String tSpaces = spaces;

        int idx = 0;

        for ( FieldGrouper.Band band : bands ) {
            addSwitchBlock( idx, tSpaces, decodeEntries, band, value, subGrpFactory, subGrpAttr, msgImpl, startMsgTag, map );
            _b.append( tSpaces + "default:\n" );
            tSpaces += "    ";
            ++idx;
        }

        _b.append( tSpaces ).append( "return;\n" );

        for ( int i = bands.size() - 1; i >= 0; --i ) {

            tSpaces = spaces;
            for ( int k = 0; k < i; k++ ) {
                tSpaces += "    ";
            }

            _b.append( tSpaces + "}\n" );
        }

        _b.append( "           _idx++; // past delimiter\n" );
        _b.append( "           _tag = getTag();\n" );
        _b.append( "       }\n\n" );

        addHook( map, HookType.postdecode );

        _b.append( "    }\n" );

    }

    private void addSubSwitch( StringBuilder b, Collection<FixEventMap> fixMaps, char firstChar, Set<String> eventSet ) {
        b.append( "          {\n" );
        b.append( "            byte msgType2 = _fixMsg[ _idx+1 ];\n" );

        b.append( "            switch( msgType2 ) {\n" );

        for ( FixEventMap map : fixMaps ) {

            if ( map.isIgnore() ) continue;

            String eventId = map.getEventId();
            String msgType = map.getEventDefinition().getMsgType();

            if ( eventId == null || msgType == null ) continue;
            if ( eventSet.contains( msgType ) ) continue;

            FixEventType fixMsgType = map.getFixMsgId();

            if ( fixMsgType.isConcreteExecRpt() ) {
                continue;
            }

            if ( msgType.charAt( 0 ) == firstChar ) {
                eventSet.add( msgType );

                ClassDefinition event = map.getClassDefinition();

                String method = "decode" + fixMsgType.toString();

                b.append( "            case '" ).append( msgType.charAt( 1 ) ).append( "':\n" );

                addSwitchEntryForMultiTwoByteType( method, b, msgType, "    " );
            }
        }

        b.append( "            }\n" );

        b.append( "            _idx += 3;\n" );
        b.append( "            throwDecodeException( \"Unsupported fix message type \" + msgType );\n" );

        b.append( "            return null;\n" );
        b.append( "          }\n" );
    }

    private void addSwitchBlock( int idx,
                                 String spaces,
                                 Map<Tag, DecodeEntry> decodeEntries,
                                 FieldGrouper.Band band,
                                 DecodeEntry value,
                                 String subGrpFactory,
                                 String subGrpAttr,
                                 String msgImpl,
                                 Tag startMsgTag,
                                 FixEventMap map ) {

        _b.append( spaces ).append( "switch( _tag ) {\n" );

        SortedSet<Tag> tags = new TreeSet<>( decodeEntries.keySet() );

        for ( Tag tag : tags ) {
            DecodeEntry subGrpVal = decodeEntries.get( tag );

            if ( band.has( tag.getTag() ) ) {

                addHook( HookType.predecode, spaces, value );

                if ( tag.getTag().equals( startMsgTag.getTag() ) ) {
                    addCaseEntry( spaces, tag, value );
                    _b.append( spaces ).append( "    msg = " ).append( subGrpFactory ).append( ".get();\n" );
                    _b.append( spaces ).append( "    if ( lastUpdate == null ) {\n" );
                    _b.append( spaces ).append( "        parent.set" ).append( subGrpAttr ).append( "( msg );\n" );
                    _b.append( spaces ).append( "    } else {\n" );
                    _b.append( spaces ).append( "        lastUpdate.setNext( msg );\n" );
                    _b.append( spaces ).append( "    }\n" );
                    _b.append( spaces ).append( "    lastUpdate = msg;\n" );

                    resetForceSwitch( spaces, decodeEntries );

                    addCaseDecoding( msgImpl, spaces, tag, subGrpVal );
                } else {
                    addDirect( msgImpl, spaces, tag, subGrpVal );
                }
            }
        }

        // add fillers

        if ( !band.isSparse( (map == null) ? FixEventMap.MAX_SUB_BAND : map.getMaxSubBand() )
             && band.size() > ((map == null) ? FixEventMap.MIN_SUB_SWITCH_PACK_SIZE : map.getMinSubSwitchPackSize()) ) {

            Set<Integer> vals = band.getVals();

            int cnt = 0;
            _b.append( spaces );
            for ( int i = band.getLow(); i <= band.getHigh(); i++ ) {
                if ( !vals.contains( i ) ) {
                    _b.append( "case " ).append( i ).append( ": " );

//                    addHook( HookType.predecode,  spaces, transform.getHooks() );
//                    addHook( HookType.decode,     spaces, transform.getHooks() );
//                    addHook( HookType.postdecode, spaces, transform.getHooks() );

                    if ( ++cnt % 8 == 0 ) {
                        _b.append( " /* SKIP */\n" ).append( spaces );
                    }
                }
            }

            if ( cnt > 0 ) {
                _b.append( spaces ).append( "    return;\n" );
            }
        }
    }

    private void addSwitchBlock( String spaces, Map<Tag, DecodeEntry> entries, FieldGrouper.Band band, String msgImpl, Set<Integer> rejectTags, FixEventType fixMsgType, FixEventMap map ) {

        _b.append( spaces ).append( "switch( _tag ) {\n" );

        boolean first = true;

        SortedSet<Tag> tags = new TreeSet<>( entries.keySet() );

        for ( Tag tag : tags ) {
            DecodeEntry entry = entries.get( tag );

            if ( band.has( tag.getTag() ) ) {

                if ( entry != null ) {
                    addDirect( msgImpl, spaces, tag, entry );
                }
            }
        }

        // add fillers

        if ( !band.isSparse( map.getMaxSubBand() ) && band.size() > map.getMinSubSwitchPackSize() ) {
            Set<Integer> vals = band.getVals();

            for ( int i = band.getLow(); i <= band.getHigh(); i++ ) {
                if ( !vals.contains( i ) ) {
                    FixTag tag = new FixTag( i );

                    if ( rejectTags.contains( tag ) ) {
                        _b.append( spaces ).append( "case " ).append( getFixTag( i ) ).append( ":         // tag" ).append( tag ).append( "\n" );
                        _b.append( spaces ).append( "    getValLength();\n" );
                        _b.append( spaces ).append( "    throwDecodeException( \"Tag " ).append( tag ).append( " not supported in " ).append( fixMsgType ).append( "\" );\n" );
                        _b.append( spaces ).append( "    break;\n" );
                    }
                }
            }

            int cnt = 0;
            _b.append( spaces );
            for ( int i = band.getLow(); i <= band.getHigh(); i++ ) {
                if ( !vals.contains( i ) ) {
                    FixTag tag = new FixTag( i );
                    if ( !rejectTags.contains( tag ) ) {
                        _b.append( "case " ).append( i ).append( ": " );

                        if ( ++cnt % 8 == 0 ) {
                            _b.append( " /* SKIP */\n" ).append( spaces );
                        }
                    }
                }
            }

            if ( cnt > 0 ) {
                _b.append( "\n" ).append( spaces ).append( "    getValLength();\n" );
                _b.append( spaces ).append( "    break;\n" );
            }
        }
    }

    private void addSwitchEntryForMultiTwoByteType( String method, StringBuilder b, String msgType, String extraSpaces ) {
        b.append( extraSpaces ).append( "            if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {\n" );
        b.append( extraSpaces )
         .append( "                throwDecodeException( \"Unsupported fix message type \" + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );\n" );
        b.append( extraSpaces ).append( "            }\n" );
        b.append( extraSpaces ).append( "            _idx += 3;\n" );
        b.append( extraSpaces ).append( "            return " ).append( method ).append( "();\n" );
    }

    private void addSwitchEntryForSingleByteMsgType( String method, StringBuilder b ) {
        b.append( "            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type\n" );
        b.append( "                throwDecodeException( \"Unsupported fix message type \" + _fixMsg[_idx] + _fixMsg[_idx+1] );\n" );
        b.append( "            }\n" );
        b.append( "            _idx += 2;\n" );
        b.append( "            return " ).append( method ).append( "();\n" );
    }

    private void addSwitchEntryForSingleTwoByteType( String method, StringBuilder b, String msgType, String extraSpaces ) {
        b.append( extraSpaces ).append( "            {\n" );
        b.append( extraSpaces ).append( "                byte msgType2 = _fixMsg[ _idx+1 ];\n" );
        b.append( extraSpaces ).append( "                if ( msgType2 != '" ).append( msgType.charAt( 1 ) ).append( "' ) {\n" );
        b.append( extraSpaces ).append( "                    throwDecodeException( \"Unsupported fix message type \" + (char)msgType + (char)msgType2 );\n" );
        b.append( extraSpaces ).append( "                }\n" );
        b.append( extraSpaces ).append( "                _idx += 3;\n" );
        b.append( extraSpaces ).append( "                return " ).append( method ).append( "();\n" );
        b.append( extraSpaces ).append( "            }\n" );
    }

    private boolean canIgnore( ClassDefinition event, Integer tag ) {

        if ( event.getStreamSrc() == EventStreamSrc.exchange ) {
            return _ignoreMktVars.contains( tag );
        }

        return _ignoreClientVars.contains( tag );
    }

    private boolean checkHasExecs( Collection<FixEventMap> fixMaps ) {
        for ( FixEventMap map : fixMaps ) {
            if ( map.getFixMsgId().isConcreteExecRpt() ) {
                return true;
            }
        }
        return false;
    }

    private boolean checkWroteSwitch( StringBuilder b, boolean first ) {
        if ( first ) {
            _b.append( "                switch( _tag ) {\n" );
        }

        return false; // switch is written
    }

    private void decodeSessionMessage( FixEventType fixMsgType ) {
        Map<Tag, DecodeEntry> entries = new LinkedHashMap<>();
        getEntriesByTag( fixMsgType, entries );
        FixEventMap map = _def.getFixEventMap( fixMsgType );

        FixEventDefinition fixDefn = map.getEventDefinition();
        ClassDefinition    event   = map.getClassDefinition();

        String preFix  = getPrefix( event );
        String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
        String base    = preFix + event.getId();

        String msgImpl = base + "Impl";

        _b.append( "    @Override\n" );
        _b.append( "    public final Event decode" ).append( fixMsgType ).append( "() {\n" );

        _b.append( "        final " ).append( msgImpl ).append( " msg = " ).append( baseVar ).append( "Factory.get();\n" );

        addHook( map, HookType.predecode );

        _b.append( "        _tag = getTag();\n\n" );

        _b.append( "        int start;\n" );
        _b.append( "        int valLen;\n\n" );

        _b.append( "        while( _tag != 0 ) {\n" );
        _b.append( "            switch( _tag ) {\n" );

        boolean first = true;

        for ( Map.Entry<Tag, DecodeEntry> entry : entries.entrySet() ) {
            Tag tag = entry.getKey();
            addDirect( msgImpl, "            ", tag, entry.getValue() );
        }

        Set<Integer> rejectTags = map.getRejectTags();
        for ( Integer tag : rejectTags ) {
            Tag tmpTag = new FixTag( tag );

            if ( !entries.containsKey( tmpTag ) ) {
                _b.append( "            case " ).append( getFixTag( tag ) ).append( ":         // tag" ).append( tag ).append( "\n" );
                _b.append( "                getValLength();\n" );
                _b.append( "                throwDecodeException( \"Tag " ).append( tag ).append( " not supported in " ).append( fixMsgType )
                  .append( "\" );\n" );
                _b.append( "                break;\n" );
            }
        }

        // skip  unwanted tags over 60
        _b.append( "            default:\n" );
        _b.append( "                getValLength();\n" );
        _b.append( "                break;\n" );

        _b.append( "            }\n" );

        _b.append( "            _idx++; /* past delimiter */ \n" );
        _b.append( "            _tag = getTag();\n" );

        _b.append( "        }\n\n" );

        addHook( map, HookType.postdecode );

        _b.append( "        return msg;\n" );

        _b.append( "    }\n\n" );
    }

    private void decodeType( String field, TypeDefinition defn, int maxSize, String spaces ) {
        if ( maxSize == 1 ) {
            _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( " ).append( defn.getId() ).append( ".getVal( _fixMsg[_idx++] ) );\n" );
        } else if ( maxSize == 2 ) {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( " ).append( defn.getId() ).append( ".getVal( _fixMsg, start, valLen ) );\n" );

        } else {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    _tmpLookupKey.setValue( _fixMsg, start, valLen );\n" );
            _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( " ).append( defn.getId() ).append( ".getVal( _tmpLookupKey ) );\n" );
        }
    }

    private void decodeTypeTransform( String field, TypeDefinition defn, int maxSize, TypeTransform transform, String spaces ) {
        if ( addHook( HookType.decode, spaces, transform.getHooks() ) ) {
            return;
        }

        addHook( HookType.predecode, spaces, transform.getHooks() );

        if ( maxSize == 1 ) {
            _b.append( spaces ).append( "msg.set" ).append( field ).append( "( transform" ).append( field ).append( "( _fixMsg[_idx++] ) );\n" );
        } else {
            _b.append( spaces ).append( "    start = _idx;\n" );
            _b.append( spaces ).append( "    valLen = getValLength();\n" );
            _b.append( spaces ).append( "    msg.set" ).append( field ).append( "( transform" ).append( field ).append( "( _fixMsg, start, valLen ) );\n" );
        }

        addHook( HookType.postdecode, spaces, transform.getHooks() );
    }

    private void execRptGetExecMsg() {
        _b.append( "        if ( _ordStatus == null || _execType == null ){\n" );
        _b.append( "            throwDecodeException( \"Execution report missing order or exec status \" );\n" );
        _b.append( "        }\n" );

        _b.append( "        switch( _execType.getID() ){\n" );

        Collection<FixEventMap> fixMaps = _def.getFixEventMaps();

        for ( ExecType type : ExecType.values() ) {
            String typeID = "ManualTypeIds.EXECTYPE_" + type.toString().toUpperCase();
            _b.append( "        case " ).append( typeID ).append( ":\n" );

            boolean found = false;

            for ( FixEventMap map : fixMaps ) {
                FixEventDefinition fixDefn = map.getEventDefinition();

                if ( fixDefn != null ) {
                    ExecType execType = fixDefn.getExecType();

                    if ( execType == type ) {

                        found = true;

                        _b.append( "            return populateExecRpt" ).append( type ).append( "();\n" );

                        break; // NEXT TYPE
                    }
                }
            }

            if ( !found ) {
                _b.append( "            return populateExecRpt" ).append( type ).append( "();\n" );
            }
        }

        _b.append( "        }\n" );

        _b.append( "        throwDecodeException( \"ExecRpt type \" + _execType + \" not supported\" );\n" );
        _b.append( "        return null;\n" );

    }

    private void execRptResetVars() {
        Map<Integer, String> complexTypes = _def.getComplexTypes();

        Map<Integer, DecodeEntry> vars        = getVarsForExecReports( _className, _def );
        Collection<DecodeEntry>   execRptVars = vars.values();

        boolean complexTypePresent = false;

        for ( DecodeEntry entry : execRptVars ) {
            AttributeDefinition attr = entry._attrDef;

            if ( attr == null ) {
                Integer        tag         = entry._tag;
                String         complexType = complexTypes.get( tag );
                TypeDefinition defn        = _internal.getTypeDefinition( complexType );
                String         var         = "_" + GenUtils.toLowerFirstChar( defn.getId() );

                _b.append( "        " ).append( var ).append( " = null;    // Tag " ).append( tag ).append( "\n" );
            } else if ( !attr.isPrimitive() ) {
                complexTypePresent = true;

                Integer tag = findComplexType( complexTypes, attr.getAttrName() );

                if ( tag == null ) {
                    tag = _fixModel.getDictionaryTag( attr.getAttrName() );
                }

                if ( tag != null ) {
                    TypeDefinition defn = _internal.getTypeDefinition( attr.getTypeId() );
                    String         var  = "_" + GenUtils.toLowerFirstChar( defn.getId() );
                    _b.append( "        " ).append( var ).append( " = null;    // Tag " ).append( tag ).append( "\n" );
                }
            }
        }

        for ( DecodeEntry entry : execRptVars ) {
            AttributeDefinition attr = entry._attrDef;

            if ( attr != null ) {
                if ( GenUtils.isStringAttr( attr ) ) {
                    Integer tag = _fixModel.getDictionaryTag( attr.getAttrName() );

                    _b.append( "        " ).append( mkStartVar( attr ) ).append( " = 0;    // tag " ).append( tag ).append( "\n" );
                    _b.append( "        " ).append( mkLenVar( attr ) ).append( " = 0;    // tag " ).append( tag ).append( "\n" );
                }
            }
        }

        for ( DecodeEntry entry : execRptVars ) {
            AttributeDefinition attr = entry._attrDef;

            if ( attr != null && attr.isPrimitive() ) {
                if ( !GenUtils.isStringAttr( attr ) ) {
                    Integer tag        = _fixModel.getDictionaryTag( attr.getAttrName() );
                    String  defaultVal = getDefaultVal( attr );

                    _b.append( "        _" ).append( attr.getAttrName() ).append( " = " ).append( defaultVal ).append( ";    // tag " ).append( tag )
                      .append( "\n" );
                }
            }
        }

        if ( complexTypePresent ) {
            _b.append( "        int start;\n" );
            _b.append( "        int valLen;\n" );
        }
    }

    private void execRptSwitchForFields( int maxTableSwitch ) {

        Map<Integer, String> complexTypes = _def.getComplexTypes();

        Map<Integer, DecodeEntry> entries = getVarsForExecReports( _className, _def );

        _b.append( "        \n" );
        _b.append( "        _tag = getTag();\n" );

        _b.append( "        while( _tag != 0 ) {\n" );
        _b.append( "            switch( _tag ) {\n" );

        // force tableswitch bytecode by using a switch statement with all cases from 1 to 60

        for ( int i = 1; i <= maxTableSwitch; i++ ) { // tags to PROCESS
            Integer     tag   = i;
            DecodeEntry entry = entries.get( tag );

            if ( entry != null ) {
                addExecRptCaseEntry( "            ", tag, entry );
            }
        }

        int cnt = 0;

        for ( int i = 1; i <= maxTableSwitch; i++ ) {    // tags to SKIP
            Integer     tag   = i;
            DecodeEntry entry = entries.get( tag );

            if ( entry == null ) {
                _b.append( "            case " ).append( getFixTag( tag ) ).append( ":         // tag" ).append( tag ).append( "\n" );
                ++cnt;
            }
        }

        if ( cnt > 0 ) {
            _b.append( "                getValLength();\n" );
            _b.append( "                break;\n" );
        }

        boolean first = true;

        // now put all other possible tags in lookupswitch under the default for above
        _b.append( "            default:\n" );

        for ( Map.Entry<Integer, DecodeEntry> entry : entries.entrySet() ) {
            Integer tag = entry.getKey();

            if ( tag > maxTableSwitch ) {
                first = checkWroteSwitch( _b, first );
                addExecRptCaseEntry( "                ", tag, entry.getValue() );
            }
        }

        // skip  unwanted tags over 60
        if ( !first ) {
            _b.append( "                default:\n" );
            _b.append( "                    getValLength();\n" );
            _b.append( "                    break;\n" );
            _b.append( "                }\n" );
        } else {
            _b.append( "                getValLength();\n" );
        }

        _b.append( "                break;\n" );

        _b.append( "            }\n\n" );
        _b.append( "            _idx++; // past delimiter\n" );
        _b.append( "            _tag = getTag();\n" );
        _b.append( "        }\n" );

    }

    private Integer findComplexType( Map<Integer, String> complexTypes, String attrName ) {
        for ( Map.Entry<Integer, String> entry : complexTypes.entrySet() ) {
            if ( entry.getValue().equalsIgnoreCase( attrName ) ) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void forceByteTableSwitch( Set<String> keys, StringBuilder b, String spaces ) {
        byte min = Byte.MAX_VALUE;
        byte max = 0;

        for ( String msgType : keys ) {

            byte entry = (byte) msgType.charAt( 0 );
            if ( entry > max ) max = entry;
            if ( entry < min ) min = entry;
        }

        if ( min < 0 ) min = 0; // force tableswitch
        if ( max > min ) {
            int cnt = 0;
            for ( byte entry = min; entry < max; ++entry ) {
                String msgType = "" + (char) entry;
                if ( !keys.contains( msgType ) ) {
                    char firstChar = msgType.charAt( 0 );

                    ++cnt;
                    if ( entry == '\\' ) {
                        b.append( spaces ).append( "case '\\\\':\n" );
                    } else {
                        b.append( spaces ).append( "case '" ).append( firstChar ).append( "':\n" );
                    }
                }
            }
            if ( cnt > 0 ) {
                b.append( spaces ).append( "    break;\n" );
            }
        }
    }

    private List<FieldGrouper.Band> getBands( final Map<Tag, DecodeEntry> decodeEntries, FixEventMap map ) {
        FieldGrouper gp = new FieldGrouper();
        for ( Tag tag : decodeEntries.keySet() ) {
            gp.addValue( tag.getTag() );
        }
        return (map == null) ? gp.getBands( FixEventMap.MAX_SUB_BAND, FixEventMap.MIN_SUB_SWITCH_PACK_SIZE ) : gp.getBands( map.getMaxSubBand(), map.getMinSubSwitchPackSize() );
    }

    private String getDefaultVal( AttributeDefinition attr ) {
        Class<?> attrClass = attr.getType().getClass();

        if ( attrClass == UTCTimestampType.class ) {
            return "Constants.UNSET_LONG";
        } else if ( attrClass == LongType.class ) {
            return "Constants.UNSET_LONG";
        } else if ( attrClass == DateType.class ) {
            return "Constants.UNSET_INT";
        } else if ( attrClass == IntType.class ) {
            return "Constants.UNSET_INT";
        } else if ( attrClass == ShortType.class ) {
            return "Constants.UNSET_SHORT";
        } else if ( attrClass == FloatType.class ) {
            return "Constants.UNSET_FLOAT";
        } else if ( attrClass == CharType.class ) {
            return "Constants.UNSET_CHAR";
        } else if ( attrClass == DoubleType.class ) {
            return "Constants.UNSET_DOUBLE";
        } else if ( attrClass == BooleanType.class ) {
            return "false";
        }

        return "null";
    }

    private void getEntriesByTag( FixEventType fixMsgType, Map<Tag, DecodeEntry> entries ) {
        Collection<FixEventMap> fixMaps = _def.getFixEventMaps();

        FixEventMap map = _def.getFixEventMap( fixMsgType );

        if ( map == null ) {
            throw new RuntimeException( "Missing fix message entries for " + fixMsgType );
        }

        String       eventId  = map.getEventId();
        FixEventType fixMsgId = map.getFixMsgId();

        if ( !fixMsgId.equals( fixMsgType ) ) {
            throw new RuntimeException( "Mismatch on fixMsgType " + fixMsgId + ", expected " + fixMsgType );
        }

        FixEventDefinition fixDefn = map.getEventDefinition();
        ClassDefinition    event   = map.getClassDefinition();

        // only need vars for exec reports
        boolean           subEvent = event.isSubEvent();
        Map<Tag, Boolean> tags     = _fixModel.getTagMap( map, !subEvent, false, false, subEvent );

        for ( Map.Entry<Tag, Boolean> entry : tags.entrySet() ) {

            Tag key = entry.getKey();

            FixTag  fixKey = (FixTag) key;
            Integer tag    = fixKey.getTag();

            // go thru each fix message adding to set of required fix tags
            // then iterate thru the set getting the entry from fix dictionary
            // for each fix tag get the event attr from the classDef, check type eg ViewString require start and end idx

            Boolean isMand = entry.getValue();

            procEntry( map, entries, fixKey, tag, isMand );
        }
    }

    private FixEventMap getFixMap( FixEventType fixMsgId ) {

        Collection<FixEventMap> fixMaps = _def.getFixEventMaps();

        for ( FixEventMap map : fixMaps ) {
            FixEventDefinition fixDefn = map.getEventDefinition();

            if ( map.getFixMsgId() == fixMsgId ) {
                return map;
            }
        }

        return null;
    }

    private String getFixTag( Integer tag ) {
        FixDictionaryTag dtag = _fixModel.getDictionaryTag( tag );

        if ( dtag == null ) return "" + tag;

        return GenUtils.getFixDictionaryFile( _fixModel ) + "." + dtag.getName();
    }

    private Set<String> getMessageTypesStartingWith( Collection<FixEventMap> fixMaps, byte startByte ) {
        int cnt = 0;

        Set<String> matchFirstChar = new HashSet<>();

        for ( FixEventMap map : fixMaps ) {

            if ( map.isIgnore() ) continue;

            String eventId = map.getEventId();

            if ( eventId == null ) continue;

            String msgType = map.getEventDefinition().getMsgType();

            if ( msgType != null && msgType.charAt( 0 ) == startByte ) {
                matchFirstChar.add( msgType );
            }
        }

        return matchFirstChar;
    }

    private String getPrefix( ClassDefinition event ) {
        EventStreamSrc src = event.getStreamSrc();

        if ( _isRecovery ) {
            if ( src == EventStreamSrc.client || src == EventStreamSrc.exchange ) return ModelConstants.FULL_EVENT_PRENAME;

            return "";
        }

        if ( src == EventStreamSrc.client ) return "Client";
        if ( src == EventStreamSrc.exchange ) return "Market";
        return "";
    }

    private Map<Integer, DecodeEntry> getVarsForExecReports( String className, CodecDefinition def ) {

        Map<Integer, DecodeEntry> entries      = new LinkedHashMap<>();
        Collection<FixEventMap>   fixMaps      = def.getFixEventMaps();
        Map<Integer, String>      complexTypes = def.getComplexTypes();

        for ( FixEventMap map : fixMaps ) {

            if ( map.isIgnore() ) {
                continue; // SKIP messages flagged for ignore
            }

            String             eventId  = map.getEventId();
            FixEventType       fixMsgId = map.getFixMsgId();
            FixEventDefinition fixDefn  = map.getEventDefinition();
            ClassDefinition    event    = map.getClassDefinition();

            // only need vars for exec reports

            if ( MSG_TYPE_EXEC_RPT.equals( fixDefn.getMsgType() ) ) {
                Map<Tag, Boolean> tags = _fixModel.getTagMap( map, true, false, false, false );

                for ( Map.Entry<Tag, Boolean> entry : tags.entrySet() ) {

                    // go thru each fix message adding to set of required fix tags
                    // then iterate thru the set getting the entry from fix dictionary
                    // for each fix tag get the event attr from the classDef, check type eg ViewString require start and end idx

                    Tag key = entry.getKey();

                    if ( !(key instanceof FixTag) ) {
                        continue;
                    }

                    FixTag  fixKey = (FixTag) key;
                    Integer tag    = fixKey.getTag();
                    Boolean isMand = entry.getValue();

                    if ( canIgnore( event, tag ) ) continue;

                    FixDictionaryTag dictTag = _fixModel.getDictionaryTag( tag );

                    if ( dictTag == null ) {
                        throw new RuntimeException( "Tag " + tag + " doesnt exist in fix dictionary " + _fixModel.getId() );
                    }

                    String dictName = dictTag.getName();
                    String eventAttrName;

                    if ( event == null )
                        throw new RuntimeException( "No matching event for exec report " + fixDefn.getExecType() );

                    boolean isComplexType = complexTypes.containsKey( tag );

                    FixTagEventMapping  fixTagEventMapping = map.getFixTagEventMapping( dictName, dictTag.getId() );
                    AttributeDefinition attrDef            = event.getAttribute( dictName );

                    if ( attrDef == null && fixTagEventMapping != null && fixTagEventMapping.getEventAttr() != null ) {
                        eventAttrName = fixTagEventMapping.getEventAttr();
                        attrDef       = event.getAttribute( eventAttrName );
                    }

                    if ( attrDef == null ) {
                        if ( isMand && fixTagEventMapping != null &&
                             !fixTagEventMapping.hasEncodeHook() && !fixTagEventMapping.hasDecodeHook() ) {

                            throw new RuntimeException( "Tag " + tag + " has fix dict entry of " + dictName +
                                                        " which is missing from event " + event.getId() );
                        }

                        // for complex types added to the fixMessageMap but not events still need the complex type to have a var declared
                        // for encode/decode funcs to use

                        if ( fixTagEventMapping != null && isComplexType &&
                             (fixTagEventMapping.hasEncodeHook() || fixTagEventMapping.hasDecodeHook()) ) {
                            DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, attrDef, eventId, fixMsgId );

                            entries.put( tag, e );
                        }
                    } else {
                        OutboundInstruction inst         = attrDef.getInstruction();
                        boolean             isSrcSide    = true; // decoding means = src side
                        boolean             isSrcClient  = (event.getStreamSrc() != EventStreamSrc.exchange);
                        EventStreamSrc      streamSrc    = _isRecovery ? EventStreamSrc.recovery : event.getStreamSrc();
                        DelegateType        delegateType = GenUtils.getDelegateType( streamSrc, isSrcSide, isSrcClient, inst );

                        if ( delegateType == DelegateType.None ) {

                            DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, attrDef, eventId, fixMsgId );

                            entries.put( tag, e );
                        } else {
                            // attr is to be taken from the src event not the market
                        }
                    }
                }
            }
        }

        return entries;
    }

    private String grpProcessFuncName( GroupPlaceholderTag keyTag ) {
        String func = "process" + GenUtils.toUpperFirstChar( keyTag.getId() );

        if ( func.charAt( func.length() - 1 ) != 's' ) {
            func = func + 's';
        }

        return func;
    }

    private boolean haveComplexTypes( Map<Tag, DecodeEntry> entries ) {
        for ( DecodeEntry entry : entries.values() ) {
            AttributeDefinition attr = entry._attrDef;

            if ( attr != null && !attr.isPrimitive() ) {
                return true;
            }
        }
        return false;
    }

    private String mkLenVar( AttributeDefinition attr ) {
        return "_" + attr.getAttrName() + "Len";
    }

    private String mkStartVar( AttributeDefinition attr ) {
        return "_" + attr.getAttrName() + "Start";
    }

    private void populateExecRpts() {

        EnumSet<ExecType> execTypes = EnumSet.allOf( ExecType.class );

        for ( FixEventType fix : FixEventType.values() ) {
            if ( fix.isConcreteExecRpt() ) {
                FixEventMap map = getFixMap( fix );

                if ( map != null ) {
                    if ( map.isIgnore() ) {

                        _b.append( "    public final Event populateExecRpt" ).append( map.getFixMsgId() ).append( "() {\n" );
                        _b.append( "        return null; // flagged in model with IGNORE\n" );
                        _b.append( "    }\n\n" );

                    } else {
                        addExecReport( fix, map );
                    }

                    execTypes.remove( map.getEventDefinition().getExecType() );
                }
            }
        }

        for ( ExecType type : execTypes ) {
            _b.append( "    public final Event populateExecRpt" ).append( type ).append( "() {\n" );
            _b.append( "        throwDecodeException( \"ExecRpt type " ).append( type ).append( " not supported\" );\n" );
            _b.append( "        return null;\n" );
            _b.append( "    }\n\n" );
        }
    }

    private void procEntry( FixEventMap map, Map<Tag, DecodeEntry> entries, FixTag fixKey, Integer tag, Boolean isMand ) {
        FixDictionaryTag dictTag  = _fixModel.getDictionaryTag( tag );
        String           eventId  = map.getEventId();
        FixEventType     fixMsgId = map.getFixMsgId();

        ClassDefinition event = map.getClassDefinition();

        if ( dictTag == null ) {
            throw new RuntimeException( "Tag " + tag + " doesnt exist in fix dictionary " + _fixModel.getId() );
        }

        String             dictName           = dictTag.getName();
        FixTagEventMapping fixTagEventMapping = map.getFixTagEventMapping( dictName, tag );

        String fixTagMapAttr = (fixTagEventMapping != null) ? fixTagEventMapping.getEventAttr() : null;
        String attrName      = (fixTagMapAttr == null || fixTagMapAttr.length() == 0) ? dictName : fixTagMapAttr;

        if ( _enableUltraLowLatencyHacks ) {
            if ( event.getStreamSrc() != EventStreamSrc.exchange && tag == CHECKSUM_TAG ) {
                DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, null, eventId, fixMsgId );
                entries.put( fixKey, e );
                return;
            }
        } else {
            if ( tag == CHECKSUM_TAG ) {
                DecodeEntry e = new DecodeEntry( tag, isMand, dictTag, fixTagEventMapping, null, eventId, fixMsgId );
                entries.put( fixKey, e );
                return;
            }
        }

        addDecodeEntry( entries, fixKey, tag, isMand, dictTag, eventId, fixMsgId, event, attrName, fixTagEventMapping );
    }

    private void resetForceSwitch( String spaces, Map<Tag, DecodeEntry> entries ) {
        spaces += "    ";
        for ( int i : _forceVars ) {
            final FixDictionaryTag fixTag = _fixModel.getDictionaryTag( i );

            Tag t = new FixTag( i );

            DecodeEntry e = entries.get( t );

            if ( e != null ) {
                AttributeDefinition attr = e._attrDef;
                if ( attr == null ) {
                    String baseVar = "_" + GenUtils.toLowerFirstChar( fixTag.getName() );
                    _b.append( spaces ).append( baseVar ).append( "Start = 0;\n" );
                    _b.append( spaces ).append( baseVar ).append( "Len = 0;\n" );
                }
            }
        }
    }

    private void writeDecodeMethod( StringBuilder b, String className, CodecDefinition def ) {
        Set<String> eventSet = new LinkedHashSet<>();
        b.append( "\n" );
        b.append( "    @Override\n" );
        b.append( "    protected final Event doMessageDecode() {\n" );

        b.append( "        // get message type field\n" );
        b.append( "        if ( _fixMsg[_idx] != '3' || _fixMsg[_idx+1] != '5' || _fixMsg[_idx+2] != '=' )\n" );
        b.append( "            throwDecodeException( \"Fix Messsage missing message type\" );\n" );

        b.append( "        _idx += 3;\n\n" );

        b.append( "        byte msgType = _fixMsg[ _idx ];\n" );

        Collection<FixEventMap> fixMaps = def.getFixEventMaps();
        b.append( "        switch( msgType ) {\n" );

        if ( checkHasExecs( fixMaps ) ) {
            b.append( "        case '8':\n" );
            b.append( "            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type\n" );
            b.append( "                throwDecodeException( \"Unsupported fix message type \" + _fixMsg[_idx] + _fixMsg[_idx+1] );\n" );
            b.append( "            }\n" );
            b.append( "            _idx += 2;\n" );
            b.append( "            return decodeExecReport();\n" );

            eventSet.add( "8" );
        }

        for ( FixEventMap map : fixMaps ) {

            if ( map.isIgnore() ) continue;

            String eventId = map.getEventId();
            String msgType = map.getEventDefinition().getMsgType();

            if ( eventId == null || msgType == null ) continue;
            if ( eventSet.contains( msgType ) ) continue;

            FixEventType fixMsgType = map.getFixMsgId();

            if ( fixMsgType.isConcreteExecRpt() ) {
                continue;
            }

            Set<String> msgTypesWithSameStartChar = getMessageTypesStartingWith( fixMaps, (byte) msgType.charAt( 0 ) );

            String method = "decode" + fixMsgType.toString();

            String msgTypeFirstChar = "" + msgType.charAt( 0 );

            b.append( "        case '" ).append( msgTypeFirstChar ).append( "':\n" );

            if ( msgTypesWithSameStartChar.size() == 1 ) {
                if ( msgType.length() == 1 ) {
                    addSwitchEntryForSingleByteMsgType( method, b );
                } else {
                    addSwitchEntryForSingleTwoByteType( method, b, msgType, "" );
                }
            } else { // to be more than one entry must be two bytes

                addSubSwitch( b, fixMaps, msgType.charAt( 0 ), eventSet );
            }

            eventSet.add( msgType );
            eventSet.add( msgTypeFirstChar );
        }

        forceByteTableSwitch( eventSet, b, "        " );

        b.append( "        }\n" );

        b.append( "        _idx += 2;\n" );
        b.append( "        throwDecodeException( \"Unsupported fix message type \" + msgType );\n" );

        b.append( "        return null;\n" );

        b.append( "    }\n" );
        b.append( "\n\n" );
    }

    private void writeDecoder( CodecDefinition def, boolean hasOMSEvents ) throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String className = def.getId() + "Decoder";

        if ( hasOMSEvents ) {
            if ( _isRecovery ) {
                b.append( "\n\n// General Decoder uses the full standard events eg NewOrderSingleImpl\n\n" );
                className = className + ModelConstants.FULL_DECODER_POSTNAME;
            } else {
                b.append( "\n\n// Decoder highly optimised specifically for OrderManager uses Client/Market event specialisations\n" );
                b.append( "// Not for use in recovery OR for more general use\n\n" );
                className = className + ModelConstants.OMS_DECODER_POSTNAME;
            }
        }

        File file = GenUtils.getJavaFile( _base, ModelConstants.CODEC_PACKAGE, className );
        GenUtils.addPackageDef( b, _base, ModelConstants.CODEC_PACKAGE, className );

        addDecoderImports( b, def );

        b.append( "\n@SuppressWarnings( \"unused\" )\n" );

        b.append( "\npublic final class " ).append( className );

        switch( def.getFixId() ) {
        case "4.2":
            b.append( " extends AbstractFixDecoder42 {\n" );
            break;
        case "4.4":
            b.append( " extends AbstractFixDecoder44 {\n" );
            break;
        case "5.0":
            b.append( " extends AbstractFixDecoder50 {\n" );
            break;
        case "DC4.4":
            b.append( " extends AbstractFixDecoderDC44 {\n" );
            break;
        case "MS4.4":
            b.append( " extends AbstractFixDecoder44 {\n" );
            break;
        case "MD4.4":
            b.append( " extends AbstractFixDecoderMD44 {\n" );
            break;
        case "MD5.0":
            b.append( " extends AbstractFixDecoderMD50 {\n" );
            break;
        default:
            throw new RuntimeException( "Only fix 4.2, 4.4, DC4.4, MD4.4, MD5.0 and 5.0 supported at present not " + def.getFixId() );
        }

        Map<Integer, DecodeEntry> execRptVars = getVarsForExecReports( className, def );

        _b         = b;
        _className = className;
        _def       = def;

        b.append( "\n    private final ReusableString _tmpLookupKey = new ReusableString();\n" );

        b.append( "\n   // Attrs\n" );
        writeDecoderAttrs( className, b, def, execRptVars );

        b.append( "\n   // Pools\n" );
        writeDecoderPools( className, b, def );

        b.append( "\n   // Constructors\n" );
        writeDecoderConstructors( className, b, def );

        b.append( "\n   // decode methods\n" );
        writeDecoderMethods( b, className, def );

        b.append( "\n   // SubGrps\n" );
        writeSubGrps( b, def );

        b.append( "\n   // transform methods\n" );
        TransformDecoderGenerator.writeDecoderTransforms( b, className, def );

        GenUtils.append( b, def.getDecodeInclude() );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    /**
     * fix decoder based on TestFixDecoder
     * <p>
     * optimisation for decoding of exec rpt into 1 of many event types need maintain values of fields as they are decoded
     *
     * @param className
     * @param b
     * @param def
     */
    private void writeDecoderAttrs( String className, StringBuilder b, CodecDefinition def, Map<Integer, DecodeEntry> vars ) {

        b.append( "\n    final String _id;\n" );

        Collection<DecodeEntry> execRptVars  = vars.values();
        Map<Integer, String>    complexTypes = def.getComplexTypes();

        for ( DecodeEntry entry : execRptVars ) {

            AttributeDefinition attr = entry._attrDef;

            if ( attr == null ) {
                Integer        tag         = entry._tag;
                String         complexType = complexTypes.get( tag );
                TypeDefinition defn        = _internal.getTypeDefinition( complexType );
                String         var         = "_" + GenUtils.toLowerFirstChar( defn.getId() );

                b.append( "    " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = null;    // Tag " ).append( tag ).append( "\n" );

            } else if ( !attr.isPrimitive() ) {

                Integer tag = findComplexType( complexTypes, attr.getAttrName() );

                if ( tag == null ) {
                    tag = _fixModel.getDictionaryTag( attr.getAttrName() );
                }

                if ( tag != null ) {
                    TypeDefinition defn = _internal.getTypeDefinition( attr.getTypeId() );
                    String         var  = "_" + GenUtils.toLowerFirstChar( defn.getId() );
                    b.append( "    " ).append( defn.getTypeDeclaration() ).append( " " ).append( var ).append( " = null;    // Tag " ).append( tag )
                     .append( "\n" );
                }
            }
        }

        b.append( "\n   // exec rpt only populated after all fields processed\n" );
        b.append( "   // only generate vars that are required\n" );

        b.append( "\n   // write String start and length vars required for ExecRpts\n\n" );

        for ( DecodeEntry entry : execRptVars ) {

            AttributeDefinition attr = entry._attrDef;

            if ( attr != null ) {
                if ( GenUtils.isStringAttr( attr ) ) {
                    Integer tag = _fixModel.getDictionaryTag( attr.getAttrName() );

                    b.append( "    int " ).append( mkStartVar( attr ) ).append( " = 0;    // tag " ).append( tag ).append( "\n" );
                    b.append( "    int " ).append( mkLenVar( attr ) ).append( " = 0;    // tag " ).append( tag ).append( "\n" );
                }
            }
        }

        b.append( "\n   // write value holders\n\n" );

        for ( DecodeEntry entry : execRptVars ) {

            AttributeDefinition attr = entry._attrDef;

            if ( attr != null && attr.isPrimitive() ) {
                if ( !GenUtils.isStringAttr( attr ) ) {
                    Integer tag = _fixModel.getDictionaryTag( attr.getAttrName() );

                    String defaultVal = getDefaultVal( attr );

                    b.append( "    " ).append( attr.getType().getTypeDeclaration() ).append( " _" ).append( attr.getAttrName() ).append( " = " )
                     .append( defaultVal ).append( ";    // tag " ).append( tag ).append( "\n" );
                }
            }
        }

        b.append( "\n   // forced var holders\n\n" );

        for ( int i : _forceVars ) {
            final FixDictionaryTag entry   = _fixModel.getDictionaryTag( i );
            String                 baseVar = "_" + GenUtils.toLowerFirstChar( entry.getName() );
            b.append( "    int " ).append( baseVar ).append( "Start = 0;\n" );
            b.append( "    int " ).append( baseVar ).append( "Len = 0;\n" );
        }
    }

    private void writeDecoderConstructors( String className, StringBuilder b, CodecDefinition def ) {
        switch( def.getFixId() ) {
        case "4.2":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor );\n" );
            b.append( "    }\n\n" );
            break;
        case "MD5.0":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor );\n" );
            b.append( "    }\n\n" );
            break;
        case "MD4.4":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.MDFix4_4._major, FixVersion.MDFix4_4._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.MDFix4_4._major, FixVersion.MDFix4_4._minor );\n" );
            b.append( "    }\n\n" );
            break;
        case "DC4.4":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );\n" );
            b.append( "    }\n\n" );
            break;
        case "4.4":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );\n" );
            b.append( "    }\n\n" );
            break;
        case "5.0":
            b.append( "    public " ).append( className ).append( "( String id ) {\n" );
            b.append( "        this( id, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor );\n" );
            b.append( "    }\n\n" );
            b.append( "    public " ).append( className ).append( "() {\n" );
            b.append( "        this( null, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor );\n" );
            b.append( "    }\n\n" );
            break;
        default:
            throw new RuntimeException( "Only fix 4.2, 4.4 and 5.0 supported at present not " + def.getFixId() );
        }

        b.append( "    public " ).append( className ).append( "( byte major, byte minor ) {\n" );
        b.append( "        this( null, major, minor );\n" );
        b.append( "    }\n" );

        b.append( "    public " ).append( className ).append( "( String id, byte major, byte minor ) {\n" );
        b.append( "        super( major, minor );\n" );
        b.append( "        _id = id;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override public FixDecoder newInstance() {\n" );
        b.append( "        " ).append( className ).append( " dec = new " ).append( className ).append( "( getComponentId(), _majorVersion, _minorVersion );\n" );
        b.append( "        dec.setCompIds( _senderCompId, _senderSubId, _targetCompId, _targetSubId );\n" );
        b.append( "        dec.setClientProfile( _clientProfile );\n" );
        b.append( "        dec.setInstrumentLocator( _instrumentLocator );\n" );
        b.append( "        return dec;\n" );

        b.append( "    }\n\n" );

    }

    private void writeDecoderMethods( StringBuilder b, String className, CodecDefinition def ) {

        b.append( "\n    @Override public String getComponentId() { return _id; }\n" );

        writeDecodeMethod( b, className, def );

        Set<String>             eventSet = new HashSet<>();
        Collection<FixEventMap> fixMaps  = def.getFixEventMaps();

        boolean hasExecs = checkHasExecs( fixMaps );

        if ( hasExecs ) {
            decodeExecReport();
        }

        for ( FixEventMap map : fixMaps ) {

            if ( map.isHandWrittenDecode() ) {
                continue;
            }

            if ( map.getEventDefinition() instanceof SubFixEventDefinition ) {
                continue;
            }

            if ( map.getFixMsgId().isConcreteExecRpt() ) {
                continue;
            }

            String eventId = map.getEventId();

            if ( eventId == null ) continue;
            if ( eventSet.contains( eventId ) ) continue;

            eventSet.add( eventId );

            // each event can only be encoded to a single  message map
            // note decoding can have multiple message maps to single event

            decodeKnownMessageType( map.getFixMsgId(), map.getMaxForceTableSwitch() );
        }
    }

    private void writeDecoderPools( String className, StringBuilder b, CodecDefinition def ) {

        Collection<FixEventMap> fixMaps = def.getFixEventMaps();

        Set<String> eventSet = new HashSet<>();

        b.append( "\n" );

        for ( FixEventMap map : fixMaps ) {
            String eventId = map.getEventId();

            if ( eventId == null ) continue;
            if ( eventSet.contains( eventId ) ) continue;

            eventSet.add( eventId );

            FixEventType fixMsgId = map.getFixMsgId();

            FixEventDefinition fixDefn = map.getEventDefinition();
            ClassDefinition    event   = map.getClassDefinition();

            String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
            String preFix  = getPrefix( event );
            String base    = preFix + event.getId();

            _poolsWritten.add( base );

            eventSet.add( base );

            b.append( "    private final SuperPool<" ).append( base ).append( "Impl> " ).append( baseVar )
             .append( "Pool = SuperpoolManager.instance().getSuperPool( " ).append( base ).append( "Impl.class );\n" );
            b.append( "    private final " ).append( base ).append( "Factory " ).append( baseVar ).append( "Factory = new " ).append( base )
             .append( "Factory( " ).append( baseVar ).append( "Pool );\n" );
            b.append( "\n" );
        }
    }

    private void writeSubGrps( StringBuilder b, CodecDefinition def ) {

        Set<SubGrpEntry> seen = new HashSet<>();

        int added;

        // adding a subgroup can cause another level of subgrp ... so loop until nothing added

        do {
            added = 0;

            Set<SubGrpEntry> subGrps = new LinkedHashSet<>( _subGrps );

            for ( SubGrpEntry entry : subGrps ) {
                if ( seen.contains( entry ) ) {
                    continue;
                }
                seen.add( entry );
                ++added;

                String grpId = entry._grpTag.getId();

                addPool( b, grpId );

                addSubGrpProcessor( b, def, entry );
            }

        } while( added > 0 );
    }
}
