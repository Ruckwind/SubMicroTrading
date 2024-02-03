/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.model.base.*;
import com.rr.model.base.ClassDefinition.Type;
import com.rr.model.internal.type.ExecType;
import com.rr.model.xml.XMLDuplicateNodeException;
import com.rr.model.xml.XMLException;
import com.rr.model.xml.XMLHelper;
import com.rr.model.xml.XMLMissingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLToModelBuilder {

    private Model     _model;
    private XMLHelper _helper;

    public XMLToModelBuilder( XMLHelper helper ) {
        _helper = helper;

        _model = new Model();
    }

    public Model create() throws XMLException {

        loadInternalModel();
        loadFixModel();
        loadBinaryModel();
        loadCodecModel();
        loadBinaryCodecModel();
        loadFactoryModel();

        verify();

        return _model;
    }

    /**
     * <BinaryCodec id="UTP_ENX" model="UTP_ENX" decodeInclude="ENXDecoder.include" encodeInclude="ENXEncoder.include">
     *
     * @param model
     * @param node
     * @throws XMLException
     */
    private void addBinaryCodec( BinaryCodecModel model, Element node ) throws XMLException {
        String  id          = _helper.getAttr( node, "id", true );
        boolean handCrafted = _helper.getAttrBool( node, "isHandCoded", false, false );

        if ( handCrafted ) {
            HandCraftedCodecDefinition codec = new HandCraftedCodecDefinition( id );
            model.addHandCraftedBinaryCodecDefinition( codec );
            return;
        }

        String modelId         = _helper.getAttr( node, "model", true );
        String extendsCodec    = _helper.getAttr( node, "extends", false );
        String decodeInclude   = _helper.getAttr( node, "decodeInclude", false );
        String encodeInclude   = _helper.getAttr( node, "encodeInclude", false );
        String decodeGenerator = _helper.getAttr( node, "decodeGenerator", false );
        String encodeGenerator = _helper.getAttr( node, "encodeGenerator", decodeGenerator != null );

        CodecType cType = CodecType.Binary;

        BinaryCodecDefinition base  = model.getCodec( extendsCodec );
        BinaryCodecDefinition codec = new BinaryCodecDefinition( id, modelId, cType, extendsCodec );

        boolean isAbstract = (decodeGenerator == null && encodeGenerator == null);

        codec.setAbstract( isAbstract );

        if ( base != null ) {
            codec.copy( base );
        }

        if ( decodeInclude != null ) codec.setDecodeInclude( decodeInclude );
        if ( encodeInclude != null ) codec.setEncodeInclude( encodeInclude );

        try {
            if ( decodeGenerator != null ) codec.setDecodeGenerator( decodeGenerator );
            if ( encodeGenerator != null ) codec.setEncodeGenerator( encodeGenerator );
        } catch( ClassNotFoundException e ) {
            throw new XMLException( "Invalid encode/decode generator class " + e.getMessage(), e );
        }

        addTransformations( codec, node );
        addMessageMaps( codec, node );

        model.addBinaryCodecDefinition( codec );
    }

    private BinaryEventDefinition addBinaryMessage( BinaryModel BinaryModel, Element node, boolean isSubMsg ) throws XMLException {
        String id       = getBinaryMsgType( node, "id", true );
        String msgType  = _helper.getAttr( node, "msgType", false );
        String superMsg = getBinaryMsgType( node, "extends", false );
        int    blockLen = _helper.getAttrInt( node, "blockLength", false, 0 );

        BinaryEventDefinition parent = BinaryModel.getBinaryEvent( superMsg );

        if ( parent == null && superMsg != null ) {
            throw new XMLException( "Parent Binary messsage doesnt exist " + superMsg, node );
        }

        BinaryEventDefinition msg = BinaryModel.getBinaryEvent( id );

        if ( msg == null ) {
            msg = new BinaryEventDefinition( id, msgType, parent, blockLen, isSubMsg );
        } else {
            msg.clearFields();

            if ( parent != null && parent != msg.getParent() ) {
                msg.setParent( parent );
            }
        }

        addBinaryTags( msg, node );

        BinaryModel.addBinaryMessage( msg );

        return msg;
    }

    /**
     * <BinaryMessage id="NewOrderSingle">
     * <Tag id="11" mand="Y"/>
     *
     * @param BinaryModel
     * @param eBinaryModel
     * @throws XMLException
     */
    private void addBinaryMessages( BinaryModel BinaryModel, Element eBinaryModel ) throws XMLException {
        List<Node> entries = _helper.getChildElements( eBinaryModel, "SubMessage", false );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addBinaryMessage( BinaryModel, (Element) node, true );
            }
        }

        entries = _helper.getChildElements( eBinaryModel, "Message", true );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addBinaryMessage( BinaryModel, (Element) node, false );
            }
        }
    }

    private void addBinaryModels( BinaryModels model, Element modelRoot ) throws XMLException {
        List<Node> BinaryModels = _helper.getChildElements( modelRoot, "Model", true );

        for ( Node BinaryModelNode : BinaryModels ) {
            if ( BinaryModelNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element eBinaryModel = (Element) BinaryModelNode;

                String id  = _helper.getAttr( eBinaryModel, "id", true );
                String ver = _helper.getAttr( eBinaryModel, "modelVersionNumber", true );

                BinaryModel binaryModel = new BinaryModel( id, ver );

                extendBinaryModel( model, binaryModel, eBinaryModel );

                List<Node> attrs = _helper.getChildElements( eBinaryModel, "Attribute", false );

                for ( Node attr : attrs ) {
                    String entryName = _helper.getAttr( attr, "name", true );
                    String value     = _helper.getAttr( attr, "value", true );

                    if ( "BinaryVersion".equals( entryName ) ) {
                        binaryModel.setBinaryVersion( value );
                    } else if ( "BinaryHelper".equals( entryName ) ) {
                        binaryModel.setBinaryHelper( value );
                    } else if ( "MessageTypeAscii".equals( entryName ) ) {
                        boolean val = _helper.getAttrBool( attr, "value", true, false );
                        binaryModel.setMessageTypeAscii( val );
                    } else {
                        throw new XMLException( "Unsupported Attribute " + entryName + " expected BinaryVersion or BinaryHelper", attr );
                    }
                }

                populateBinaryModel( binaryModel, eBinaryModel );

                addStandardBinaryHeader( binaryModel, eBinaryModel );
                addStandardTrailer( binaryModel, eBinaryModel );
                addBinaryMessages( binaryModel, eBinaryModel );

                model.addBinaryModel( binaryModel );
            }
        }
    }

    private void addBinaryTags( BinaryTagSet set, Element eBinaryMsg ) throws XMLException {

        List<Node> entries = _helper.getChildElements( eBinaryMsg, "Field", false );

        for ( Node tag : entries ) {

            if ( tag.getNodeType() == Node.ELEMENT_NODE ) {
                String  sTag    = _helper.getAttr( tag, "id", true );
                boolean isMand  = _helper.getAttrBool( tag, "mand", false, false );
                int     len     = _helper.getAttrInt( tag, "len", false, 0 );
                String  counter = _helper.getAttr( tag, "counter", false );
                String  comment = _helper.getAttr( tag, "comment", false );

                if ( sTag.startsWith( "filler" ) ) {
                    String type = _helper.getAttr( tag, "type", false );
                    if ( set.addFiller( sTag, isMand, len, type, comment ) ) {
                        throw new XMLException( "Duplicate tag " + sTag + " for " + set.getName(), tag );
                    }
                } else if ( counter != null ) {
                    int blockLen = _helper.getAttrInt( tag, "blockLength", false, 0 );
                    if ( set.addRepeatingGroup( sTag, isMand, counter, blockLen ) ) {
                        throw new XMLException( "Duplicate tag " + sTag + " for " + set.getName(), tag );
                    }
                } else {
                    if ( set.addTag( sTag, isMand, len ) ) {
                        throw new XMLException( "Duplicate tag " + sTag + " for " + set.getName(), tag );
                    }
                }
            }
        }
    }

    /**
     * <Codec id="exchangeX_44" fix="4.4" type="Exchange" extends="standard44">
     * <ComplexType typeId="BookingType" tag="775"/>
     *
     * @param model
     * @param node
     * @throws XMLException
     */
    private void addCodec( CodecModel model, Element node ) throws XMLException {
        String id              = _helper.getAttr( node, "id", true );
        String fixId           = _helper.getAttr( node, "fix", true );
        String codecType       = _helper.getAttr( node, "type", false );
        String extendsCodec    = _helper.getAttr( node, "extends", false );
        String decodeInclude   = _helper.getAttr( node, "decodeInclude", false );
        String encodeInclude   = _helper.getAttr( node, "encodeInclude", false );
        String encodeBuilder   = _helper.getAttr( node, "encodeBuilder", false );
        String decodeGenerator = _helper.getAttr( node, "decodeGenerator", false );
        String encodeGenerator = _helper.getAttr( node, "encodeGenerator", false );

        CodecType cType = CodecType.Base;

        if ( codecType != null ) {
            cType = CodecType.valueOf( codecType );

            if ( cType == null ) {
                throw new XMLException( "Invalid codec type " + codecType + " expected Client or Exchange or not specified" );
            }
        }

        CodecDefinition base  = model.getCodec( extendsCodec );
        CodecDefinition codec = new CodecDefinition( id, fixId, cType, extendsCodec );

        if ( base != null ) {
            codec.copy( base );
        }

        if ( decodeInclude != null ) codec.setDecodeInclude( decodeInclude );
        if ( encodeInclude != null ) codec.setEncodeInclude( encodeInclude );

        if ( encodeBuilder != null ) codec.setEncodeBuilder( encodeBuilder );

        try {
            if ( decodeGenerator != null ) codec.setDecodeGenerator( decodeGenerator );
            if ( encodeGenerator != null ) codec.setEncodeGenerator( encodeGenerator );
        } catch( ClassNotFoundException e ) {
            throw new XMLException( "Invalid encode/decode generator class " + e.getMessage(), e );
        }

        List<Node> entries = _helper.getChildElements( node, "ComplexType", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String typeId = _helper.getAttr( entry, "typeId", true );
                int    iTag   = _helper.getAttrInt( entry, "tag", true );

                codec.addComplexType( iTag, typeId );
            }
        }

        addTransformations( codec, node );
        addMessageMaps( codec, node );

        model.addCodecDefinition( codec );
    }

    private void addDefaultSizes( InternalModel model, Element internalModel ) throws XMLException {
        NodeList sizes = _helper.getElements( internalModel, "DefaultSize", true );

        // <DefaultSize id="ACCOUNT_LENGTH" val="5"/>

        for ( int i = 0; i < sizes.getLength(); ++i ) {

            Node node = sizes.item( i );

            String id  = _helper.getAttr( node, "id", true );
            int    len = _helper.getAttrInt( node, "val", true );

            _model.getInternal().addDefaultSize( id, len );
        }
    }

    /**
     * <Tag id="1" name="Account" type="string"/>
     * <Tag id="6" name="AvgPx" type="price"/>
     * <Tag id="8" name="BeginString" type="fixVersion"/>
     * <Tag id="9" name="BodyLength" type="length"/>
     * <Tag id="14" name="CumQty" type="quantity"/>
     * <Tag id="15" name="Currency" type="currency"/>
     * <Tag id="21" name="HandlInst" type="ch"/>
     * <Tag id="34" name="MsgSeqNum" type="seqnum"/>
     * <Tag id="35" name="MsgType" type="msgType"/>
     * <Tag id="43" name="PossDupFlag" type="bool"/>
     * <Tag id="52" name="SendingTime" type="UTCTimestamp"/>
     * <Tag id="89" name="Signature" type="lva"/>
     * <Tag id="11611" name="AckStats" type="ch" src="SMT"/>
     *
     * @param tag
     * @throws XMLException
     */

    private void addDictionaryTag( FixModel fixModel, Element tag ) throws XMLException {
        int    id   = _helper.getAttrInt( tag, "id", true );
        String name = _helper.getAttr( tag, "name", false );

        if ( name == null || name.length() == 0 ) {
            fixModel.removeDictionaryTag( id );
        } else {
            String type = _helper.getAttr( tag, "type", true );
            String src  = _helper.getAttr( tag, "src", false );

            FixType fixType = FixType.valueOf( type );
            if ( fixType == null ) {
                throw new XMLException( "DictionaryTag type " + type + " is not valid ", tag );
            }

            FixDictionaryTag dicTag = new FixDictionaryTag( id, name, fixType );

            if ( src != null ) {
                dicTag.setSrc( src );
            }

            fixModel.addDictionaryTag( dicTag );
        }
    }

    /**
     * <Field id="lastMsgSeqNum"       type="int"              comment="last message sequence number"/>
     * <Field id="userName"            type="fstr" len="UTP_TEXT_LENGTH"/>
     *
     * @param tag
     * @throws XMLException
     */

    private void addDictionaryTag( BinaryModel BinaryModel, Element tag ) throws XMLException {
        String id      = _helper.getAttr( tag, "id", true );
        String comment = _helper.getAttr( tag, "comment", false );
        String type    = _helper.getAttr( tag, "type", true );
        int    code    = _helper.getAttrInt( tag, "code", false );

        BinaryType binaryType = BinaryType.valueOf( type );
        if ( binaryType == null ) {
            throw new XMLException( "DictionaryTag type " + type + " is not valid ", tag );
        }

        BinaryDictionaryTag dicTag;

        String sWidth;

        if ( binaryType == BinaryType.fstr || binaryType == BinaryType.zstr || binaryType == BinaryType.data || binaryType == BinaryType.base36 ) {
            sWidth = _helper.getAttr( tag, "len", true );       // min and maxLen
        } else {
            sWidth = _helper.getAttr( tag, "len", false, "0" ); // maxLen
        }

        int width = _model.getInternal().getDefaultSize( sWidth );

        dicTag = new BinaryDictionaryTag( id, comment, binaryType, code, width );

        if ( binaryType == BinaryType.price ) {
            String dp = _helper.getAttr( tag, "dp", false, null ); // decimal digits

            if ( dp != null ) {
                int decPlaces = Integer.parseInt( dp );

                dicTag.setDecimalPlaces( decPlaces );
            }

        }

        BinaryModel.addDictionaryTag( dicTag );
    }

    private void addEvent( InternalModel model, Element node ) throws XMLException {

        boolean              isSubEvent = false;
        String               nodeName   = node.getNodeName();
        ClassDefinition.Type type       = Type.Event;

        if ( "Base".equals( nodeName ) ) {
            type = Type.Base;
        } else if ( "SubEvent".equalsIgnoreCase( nodeName ) ) {
            /**
             * SubEvent represents a sub structure used for repeating groups
             */
            type       = Type.SubEvent;
            isSubEvent = true;
        } else if ( !"Event".equals( nodeName ) ) {
            throw new XMLException( "Expected Base or Event node not " + nodeName, node );
        }

        String id   = _helper.getAttr( node, "id", true );
        String base = _helper.getAttr( node, "extends", false );

        String extraInterfaces = _helper.getAttr( node, "extraInterfaces", false );

        String         reusableType = _helper.getAttr( node, "reusableType", false );
        EventStreamSrc msrc;

        if ( isSubEvent ) {
            msrc = EventStreamSrc.both;
        } else {
            String src = _helper.getAttr( node, "src", true ).toLowerCase();
            msrc = EventStreamSrc.valueOf( src );
        }

        if ( reusableType == null ) {
            reusableType = id;
        }

        ClassDefinition defn = new ClassDefinition( id, base, type, reusableType, msrc );
        defn.setExtraInterfaces( extraInterfaces );

        List<Node> hooks = _helper.getChildElements( node, "Hook", false );
        for ( Node hookEntry : hooks ) {
            String code = _helper.getAttr( hookEntry, "code", true );

            defn.addEventHook( code );
        }

        List<Node> defaults = _helper.getChildElements( node, "Attribute", false );

        for ( Node entry : defaults ) {
            String  typeId      = _helper.getAttr( entry, "typeId", true );
            String  attrName    = _helper.getAttr( entry, "name", false );
            boolean isMand      = _helper.getAttrBool( entry, "mandatory", false, false );
            String  fix44Tag    = _helper.getAttr( entry, "tag", false );
            String  outbound    = _helper.getAttr( entry, "outbound", false );
            String  defaultVal  = _helper.getAttr( entry, "defaultVal", false );
            String  desc        = _helper.getAttr( entry, "desc", false );
            String  annotations = _helper.getAttr( entry, "annotations", false );

            boolean forceOverride = _helper.getAttrBool( entry, "override", false, false );

            // outbound=[delegate|exclude|only|seperate]

            OutboundInstruction instruction = OutboundInstruction.none;

            if ( outbound != null ) instruction = OutboundInstruction.valueOf( outbound );

            if ( attrName == null ) {
                attrName = genNameFromType( typeId );
            }

            AttrType attrType = checkTypeId( typeId, entry, model );

            if ( model.isSubElement( typeId ) ) {
                int    min         = _helper.getAttrInt( entry, "min", false, 0 );
                int    max         = _helper.getAttrInt( entry, "max", false, 99 );
                String counterAttr = _helper.getAttr( entry, "counter", true );

                defn.addSubEvent( attrName, typeId, defaultVal, isMand, fix44Tag, attrType, instruction, min, max, counterAttr, forceOverride, annotations );

            } else {
                defn.addAttribute( attrName, typeId, defaultVal, isMand, fix44Tag, attrType, instruction, desc, forceOverride, annotations );
            }
        }

        model.addClassDefinition( defn );

    }

    /**
     * <Events>
     * <Base id="SharedOrderFields">
     * <Attribute typeId="char[CLORDID_LENGTH]" name="clOrdId" mandatory="Y"/>
     *
     * <Attribute typeId="double" name="price" mandatory="Y"/>
     * <Attribute typeId="int" name="quantity" mandatory="Y"/>
     *
     * <Attribute typeId="BookingType" tag="775"/>
     * <Attribute typeId="EncryptMethod" tag="98"/>
     * <Attribute typeId="ExecInst" tag="18"/>
     * <Attribute typeId="ExecType" tag="150"/>
     * <Attribute typeId="HandlInst" tag="21"/>
     * <Attribute typeId="OrderCapacity" tag="528"/>
     * <Attribute typeId="OrderRestrictions" tag="529"/>
     * <Attribute typeId="OrdType" tag="40"/>
     * <Attribute typeId="PartyIDSource" tag="447"/>
     * <Attribute typeId="PositionEffect" tag="77"/>
     * <Attribute typeId="SecurityType" tag="167"/>
     * <Attribute typeId="SecurityIDSource" tag="22"/>
     * <Attribute typeId="Side" tag="54"/>
     * <Attribute typeId="TimeInForce" tag="59"/>
     * </Base>
     *
     * <Event id="NewOrderSingle" extends="SharedOrderFields">
     * </Event>
     *
     * <Event id="CancelReplaceRequest" extends="SharedOrderFields">
     * <Attribute typeId="char[CLORDID_LENGTH]" name="origClOrdId"/>
     * </Event>
     *
     * @param model
     * @param internalModelElem
     * @throws XMLMissingException
     * @throws XMLDuplicateNodeException
     */
    private void addEvents( InternalModel model, Element internalModelElem ) throws XMLException {

        Element type = _helper.getElement( internalModelElem, "Events", true );

        NodeList types = type.getChildNodes();

        for ( int i = 0; i < types.getLength(); ++i ) {

            Node node = types.item( i );

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addEvent( model, (Element) node );
            }
        }

    }

    private void addFixMessage( FixModel fixModel, Element node ) throws XMLException {
        FixEventType id       = getFixMsgType( node, "id", true );
        String       msgType  = _helper.getAttr( node, "msgType", false );
        FixEventType superMsg = getFixMsgType( node, "extends", false );

        FixEventDefinition parent = fixModel.getFixMessage( superMsg );

        if ( parent == null && superMsg != null ) {
            throw new XMLException( "Parent fix messsage doesnt exist " + superMsg, node );
        }

        FixEventDefinition msg = fixModel.getFixMessage( id );

        if ( msg == null ) {
            msg = new FixEventDefinition( id, msgType, parent );
        } else {
            msg.clearFields();

            if ( parent != null && parent != msg.getParent() ) {
                msg.setParent( parent );
            }
        }

        if ( "8".equals( msgType ) ) {
            String   execType = _helper.getAttr( node, "execType", true );
            ExecType type     = ExecType.valueOf( execType );

            if ( type == null ) throw new XMLException( "ExecReport fix message requires a valid ExeType not " + execType, node );

            msg.setExecType( type );
        }

        addFixTags( fixModel, msg, node );

        fixModel.addFixMessage( msg );
    }

    /**
     * <FixMessage id="NewOrderSingle">
     * <Tag id="11" mand="Y"/>
     *
     * @param fixModel
     * @param eFixModel
     * @throws XMLException
     */
    private void addFixMessages( FixModel fixModel, Element eFixModel ) throws XMLException {
        List<Node> entries = _helper.getChildElements( eFixModel, "SubFixMessage", false );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addSubFixMessage( fixModel, (Element) node );
            }
        }

        entries = _helper.getChildElements( eFixModel, "FixMessage", false );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addFixMessage( fixModel, (Element) node );
            }
        }
    }

    /**
     * <FixModel id="4.4" modelVersionNumber="1.0" extends="4.2">
     * <Attribute name="FixVersion" value="4.4"/>
     * <Attribute name="FixHelper" value="com.rr.fix.codec.helper.FixCodecHelper44"/>
     *
     * <Dictionary id="4.4">
     * <!-- certain tags are special and have specific optimised codec code eg fixVersion.msgType -->
     * <!--  type should be fixed facing not internal type mapping -->
     *
     * <Tag id="1" name="Account" type="string"/>
     *
     * @param model
     * @param modelRoot
     * @throws XMLException
     */
    private void addFixModels( FixModels model, Element modelRoot ) throws XMLException {
        List<Node> fixModels = _helper.getChildElements( modelRoot, "FixModel", true );

        for ( Node fixModelNode : fixModels ) {
            if ( fixModelNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element eFixModel = (Element) fixModelNode;

                String id  = _helper.getAttr( eFixModel, "id", true );
                String ver = _helper.getAttr( eFixModel, "modelVersionNumber", true );

                FixModel fixModel = new FixModel( id, ver );

                extendFixModel( model, fixModel, eFixModel );

                List<Node> attrs = _helper.getChildElements( eFixModel, "Attribute", false );

                for ( Node attr : attrs ) {
                    String entryName = _helper.getAttr( attr, "name", true );
                    String value     = _helper.getAttr( attr, "value", true );

                    if ( "FixVersion".equals( entryName ) ) {
                        fixModel.setFixVersion( value );
                    } else if ( "FixHelper".equals( entryName ) ) {
                        fixModel.setFixHelper( value );
                    } else {
                        throw new XMLException( "Unsupported Attribute " + entryName + " expected FixVersion or FixHelper", attr );
                    }
                }

                populateFixModel( fixModel, eFixModel );

                addStandardFixHeader( fixModel, eFixModel );
                addStandardTrailer( fixModel, eFixModel );
                addFixMessages( fixModel, eFixModel );

                model.addFixModel( fixModel );
            }
        }
    }

    private void addFixTags( FixModel fixModel, FixTagSet set, Element eFixMsg ) throws XMLException {

        List<Node> entries = _helper.getChildElements( eFixMsg, "Tag", false );

        for ( Node tag : entries ) {
            boolean isMand = _helper.getAttrBool( tag, "mand", false, false );

            if ( tag.getNodeType() == Node.ELEMENT_NODE ) {
                int    iTag        = _helper.getAttrInt( tag, "id", true );
                String subFixGroup = _helper.getAttr( tag, "subGrp", false );

                if ( subFixGroup != null && subFixGroup.length() > 0 ) {
                    String modelAttr = _helper.getAttr( tag, "modelAttr", true );

                    SubFixEventDefinition sub = fixModel.getSubFixMessage( subFixGroup );

                    if ( sub == null ) {
                        throw new XMLException( "FixMessage missing SubFixMessage def " + subFixGroup + " missing in " + set.getName(), tag );
                    }

                    set.addRepeatingGroup( sub, subFixGroup, iTag, isMand, modelAttr );
                } else {
                    if ( set.addTag( iTag, isMand ) ) {
                        throw new XMLException( "Duplicate tag " + iTag + " for " + set.getName(), tag );
                    }
                }
            }
        }
    }

    private void addFixVersion( FixModels model, Element node ) throws XMLMissingException {
        String name  = _helper.getAttr( node, "name", true );
        String value = _helper.getAttr( node, "value", true );

        model.addFixVersion( name, value );
    }

    /**
     * <FixVersion>
     * <Entry name="4.0" value="4.0"/>
     *
     * @param model
     * @param modelRoot
     * @throws XMLMissingException
     * @throws XMLDuplicateNodeException
     */
    private void addFixVersions( FixModels model, Element modelRoot ) throws XMLMissingException, XMLDuplicateNodeException {
        Element type = _helper.getChildElement( modelRoot, "FixVersion", true );

        NodeList entries = type.getChildNodes();

        for ( int i = 0; i < entries.getLength(); ++i ) {

            Node node = entries.item( i );

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addFixVersion( model, (Element) node );
            }
        }
    }

    /**
     * <Hook type="pre" class=""/>
     *
     * @param node
     * @param map
     * @throws XMLException
     */
    private void addHooks( Node node, HookSupport map ) throws XMLException {
        List<Node> entries = _helper.getChildElements( node, "Hook", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String sType = _helper.getAttr( entry, "type", true );
                String code  = _helper.getAttr( entry, "code", true );

                HookType type = HookType.valueOf( sType.toLowerCase() );

                if ( type == null ) {
                    throw new XMLException( "Invalid HookType " + sType, node );
                }

                if ( code != null ) {
                    code = code.trim();
                    map.addHook( type, code );
                }
            }
        }
    }

    private void addInstructions( InternalModel model, Element internalModel ) throws XMLException {

        /*
            <Instruction>UseViewString</Instruction>
            <Instruction>RestrictMultiValueFieldsToSingleValue</Instruction>
        */

        String[] instructions = _helper.getElementValues( "Instruction", false );

        for ( String inst : instructions ) {

            try {
                InternalModelInstruction i = InternalModelInstruction.valueOf( inst );

                model.addInstruction( i );

            } catch( Exception e ) {
                throw new XMLException( "InternalModel.instuction " + inst + " is not valid, must match entry in enum InternalModelInstruction" );
            }
        }
    }

    /**
     * <MessageMap id="NOS" eventId="NewOrderSingle" fixMessageId="NewOrderSingle" extends="someOtherMapId">
     * <!-- as all attributes of NewOrderSingle event match the fix dictionary no more mappings required -->
     * </MessageMap>
     *
     * @param codec
     * @param node
     * @throws XMLException
     */
    private void addMessageMaps( CodecDefinition codec, Element node ) throws XMLException {

        boolean    isMand  = codec.getFixEventMaps().size() == 0;
        List<Node> entries = _helper.getChildElements( node, "MessageMap", isMand );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String  id                   = _helper.getAttr( entry, "id", true );
                String  parentId             = _helper.getAttr( entry, "extends", false );
                boolean hasBase              = parentId != null && parentId.length() > 0;
                boolean ignore               = _helper.getAttrBool( entry, "ignore", false, false );
                boolean handEncode           = _helper.getAttrBool( entry, "handEncode", false, false );
                boolean handDecode           = _helper.getAttrBool( entry, "handDecode", false, false );
                int     maxForceTableSwitch  = _helper.getAttrInt( entry, "maxForceTableSwitch", false, FixEventMap.MAX_FORCE_TABLESWITCH );
                int     maxSubBand           = _helper.getAttrInt( entry, "maxSubBand", false, FixEventMap.MAX_SUB_BAND );
                int     minSubSwitchPackSize = _helper.getAttrInt( entry, "minSubSwitchPackSize", false, FixEventMap.MIN_SUB_SWITCH_PACK_SIZE );

                FixEventType fixMsgId = getFixMsgType( entry, "fixMessageId", ignore );

                FixEventMap map = codec.getFixEventMap( id );

                if ( map == null ) {
                    if ( ignore ) {
                        map = new FixEventMap( id, fixMsgId, true );
                    } else {
                        String eventId = _helper.getAttr( entry, "eventId", true );
                        map = new FixEventMap( id, eventId, fixMsgId );
                    }

                    map.setHandWrittenDecode( handDecode );
                    map.setHandWrittenEncode( handEncode );
                    map.setMaxForceTableSwitch( maxForceTableSwitch );
                    map.setMaxSubBand( maxSubBand );
                    map.setMinSubSwitchPackSize( minSubSwitchPackSize );

                } else {
                    map.clear();
                }

                if ( hasBase ) {
                    FixEventMap parent = codec.getFixEventMap( parentId );

                    if ( parent == null ) {
                        throw new XMLException( "Invalid MessageMap parentId " + parentId + " doesnt exist or not yet registered" );
                    }

                    map.setParent( parent );
                }

                loadMessageMap( map, entry );

                addHooks( entry, map );
                addRejectIfPresent( entry, map );

                codec.addMessageMap( map );
            }
        }
    }

    /**
     * <MessageMap id="NOS" eventId="NewOrderSingle" BinaryMessageId="NewOrderSingle" extends="someOtherMapId">
     * <MessageMap id="Logon"       eventId="UTPLogon"       messageId="Logon"/>
     *
     * @param codec
     * @param node
     * @throws XMLException
     */
    private void addMessageMaps( BinaryCodecDefinition codec, Element node ) throws XMLException {

        boolean    isMand  = codec.getBinaryEventMaps().size() == 0;
        List<Node> entries = _helper.getChildElements( node, "MessageMap", isMand );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String  id       = _helper.getAttr( entry, "id", true );
                String  parentId = _helper.getAttr( entry, "extends", false );
                boolean hasBase  = parentId != null && parentId.length() > 0;
                boolean ignore   = _helper.getAttrBool( entry, "ignore", false, false );

                System.out.println( "Processing MessageMap id=" + id );

                String encodeWrapper = _helper.getAttr( entry, "encodeFunc", false );

                String binaryMsgId = getBinaryMsgType( entry, "messageId", ignore );

                BinaryEventMap map = codec.getBinaryEventMapById( id );

                if ( map == null ) {
                    if ( ignore ) {
                        map = new BinaryEventMap( id, binaryMsgId, true );
                    } else {
                        String eventId        = _helper.getAttr( entry, "eventId", true );
                        String conditionalKey = _helper.getAttr( entry, "key1", false );
                        String conditionalVal = _helper.getAttr( entry, "val1", false );

                        if ( conditionalKey != null && conditionalVal != null ) {
                            map = new BinaryEventMap( id, eventId, binaryMsgId, conditionalKey, conditionalVal );
                        } else {
                            map = new BinaryEventMap( id, eventId, binaryMsgId );
                        }
                    }
                } else {
                    map.clear();
                }

                map.setEncodeOverrideMethod( encodeWrapper );

                if ( hasBase ) {
                    BinaryEventMap parent = codec.getBinaryEventMapById( parentId );

                    if ( parent == null ) {
                        throw new XMLException( "Invalid MessageMap parentId " + parentId + " doesnt exist or not yet registered" );
                    }

                    map.setParent( parent );
                }

                loadMessageMap( map, entry );

                addHooks( entry, map );

                System.out.println( "Storing MessageMap id=" + id );

                codec.addMessageMap( map );
            }
        }
    }

    /**
     * <RejectIfPresent fixTag="211"/>
     *
     * @param node
     * @param map
     * @throws XMLException
     */
    private void addRejectIfPresent( Node node, FixEventMap map ) throws XMLException {
        List<Node> entries = _helper.getChildElements( node, "RejectIfPresent", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                int tag = _helper.getAttrInt( entry, "fixTag", true );

                map.addRejectTag( tag );
            }
        }
    }

    /**
     * <StandardHeader>
     * <Tag id="8" mand="Y"/>
     *
     * @param BinaryModel
     * @param eBinaryModel
     * @throws XMLException
     */
    private void addStandardBinaryHeader( BinaryModel BinaryModel, Element eBinaryModel ) throws XMLException {
        BinaryStandardHeader header = BinaryModel.getHeader();
        boolean              isMand = (header == null);
        Element              dict   = _helper.getChildElement( eBinaryModel, "StandardHeader", isMand );

        if ( header == null ) header = new BinaryStandardHeader();

        addBinaryTags( header, dict );

        BinaryModel.setStandardHeader( header );
    }

    /**
     * <StandardHeader>
     * <Tag id="8" mand="Y"/>
     *
     * @param fixModel
     * @param eFixModel
     * @throws XMLException
     */
    private void addStandardFixHeader( FixModel fixModel, Element eFixModel ) throws XMLException {
        FixStandardHeader header = fixModel.getHeader();
        boolean           isMand = (header == null);
        Element           dict   = _helper.getChildElement( eFixModel, "StandardHeader", isMand );

        if ( header == null ) header = new FixStandardHeader();

        addFixTags( fixModel, header, dict );

        fixModel.setStandardHeader( header );
    }

    private void addStandardTrailer( FixModel fixModel, Element eFixModel ) throws XMLException {
        FixStandardTrailer trailer = fixModel.getTrailer();
        boolean            isMand  = (trailer == null);
        Element            dict    = _helper.getChildElement( eFixModel, "StandardTrailer", isMand );

        if ( trailer == null ) trailer = new FixStandardTrailer();

        addFixTags( fixModel, trailer, dict );

        fixModel.setStandardTrailer( trailer );
    }

    private void addStandardTrailer( BinaryModel BinaryModel, Element eBinaryModel ) throws XMLException {
        BinaryStandardTrailer trailer = BinaryModel.getTrailer();
        boolean               isMand  = (trailer == null);
        Element               dict    = _helper.getChildElement( eBinaryModel, "StandardTrailer", isMand );

        if ( trailer == null ) trailer = new BinaryStandardTrailer();

        addBinaryTags( trailer, dict );

        BinaryModel.setStandardTrailer( trailer );
    }

    private void addSubFixMessage( FixModel fixModel, Element node ) throws XMLException {
        String id = _helper.getAttr( node, "id", true );

        SubFixEventDefinition msg = fixModel.getSubFixMessage( id );

        if ( msg == null ) {
            msg = new SubFixEventDefinition( id );
        } else {
            throw new XMLException( "Duplicate sub fix messsage " + id );
        }

        addFixTags( fixModel, msg, node );

        fixModel.addSubFixMessage( msg );
    }

    /**
     * <TypeTransform typeId="OrdType" transform="map" nonMatched="reject">
     * <Map internal="1" external="X" comment="Market NAE"/>
     * <Map internal="P" external="Y" comment="Pegged NW"/>
     * <Map internal="K" external="Z" comment="Market To Limit RM NAE"/>
     * </TypeTransform>
     * <p>
     * binary external must be in hex format
     */
    private void addTransformations( TypeTransforms codec, Element node ) throws XMLException {
        List<Node> entries = _helper.getChildElements( node, "TypeTransform", false );

        InternalModel internal = _model.getInternal();

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String typeId    = _helper.getAttr( entry, "typeId", true );
                String transform = _helper.getAttr( entry, "transform", true );

                String externalFormat   = _helper.getAttr( entry, "external", false, "char" );
                String nonMatchedDecode = _helper.getAttr( entry, "nonMatchedDecode", true );
                String nonMatchedEncode = _helper.getAttr( entry, "nonMatchedEncode", true );

                TypeDefinition td = internal.getTypeDefinition( typeId );

                if ( td == null ) {
                    throw new XMLException( "Invalid TypeTransform typeId " + typeId + " doesnt exist or not yet registered" );
                }

                TypeTransform t = new TypeTransform( typeId, td );

                if ( "binary".equalsIgnoreCase( externalFormat ) ) {
                    t.setExternalBinaryFormat( true );
                }

                if ( "reject".equalsIgnoreCase( nonMatchedEncode ) ) {
                    t.setRejectUnmatchedEncode( true );
                } else {
                    t.setDefaultValEncode( nonMatchedEncode );
                }

                if ( "reject".equalsIgnoreCase( nonMatchedDecode ) ) {
                    t.setRejectUnmatchedDecode( true );
                } else {
                    t.setDefaultValDecode( nonMatchedDecode );
                }

                if ( "map".equals( transform ) ) {
                    addTypeTransformMapEntries( t, entry );
                } else if ( "hook".equals( transform ) ) {
                    addHooks( entry, t );
                } else {
                    throw new XMLException( "Invalid TypeTransform transform " + transform + " not supported" );
                }

                codec.addTypeTransform( typeId, t );
            }
        }
    }

    /**
     * <Type id="ExecInst" multiValue="1">
     *
     * @param model
     * @param node
     * @throws XMLException
     */
    private void addType( InternalModel model, Element node ) throws XMLException {

        String  name        = _helper.getAttr( node, "id", true );
        String  valType     = _helper.getAttr( node, "valType", false );
        int     multiValue  = _helper.getAttrInt( node, "multiValue", false, 1 );
        boolean handCrafted = _helper.getAttrBool( node, "isHandCoded", false, false );
        String  packge      = _helper.getAttr( node, "package", false );

        String extraInterfaces = _helper.getAttr( node, "extraInterfaces", false );

        String desc = _helper.getChildElementValue( node, "Description", true );

        TypeDefinition type = new TypeDefinition( name, desc, multiValue, handCrafted );
        type.setExtraInterfaces( extraInterfaces );

        if ( valType != null && valType.length() > 0 ) {
            type.setValType( valType );
        }

        if ( handCrafted ) {

            int maxValueLen = _helper.getAttrInt( node, "maxValueLen", false, 1 );

            if ( maxValueLen > 1 ) type.setMaxEntryValueLen( maxValueLen );

            type.setPackage( packge );

        } else {

            List<Node> typeValidEntries = _helper.getChildElements( node, "Entry", true );

            Map<String, String> attrs = new HashMap<>(); // name, type

            Node defaultNode = _helper.getChildElement( node, "Default", false );

            if ( defaultNode != null ) {
                List<Node> defaults = _helper.getChildElements( defaultNode, "Attribute", false );

                for ( Node defaultEntry : defaults ) {
                    String entryName    = _helper.getAttr( defaultEntry, "name", true );
                    String defaultValue = _helper.getAttr( defaultEntry, "value", true );
                    String typeId       = _helper.getAttr( defaultEntry, "typeId", true );
                    String annotations  = _helper.getAttr( defaultEntry, "annotations", false );

                    AttrType attrType = checkTypeId( typeId, defaultEntry, model );

                    type.addAttribute( entryName, typeId, defaultValue, attrType, annotations );

                    attrs.put( entryName, typeId );
                }
            }

            for ( Node instance : typeValidEntries ) {
                String instanceName  = _helper.getAttr( instance, "name", true );
                String instanceValue = _helper.getAttr( instance, "value", true );

                List<Node> attributeNodes = _helper.getChildElements( instance, "Attribute", false );

                TypeEntry entry = new TypeEntry( instanceName, instanceValue );

                if ( attributeNodes != null ) {
                    for ( Node attributeNode : attributeNodes ) {
                        String entryName    = _helper.getAttr( attributeNode, "name", true );
                        String defaultValue = _helper.getAttr( attributeNode, "value", true );
                        String typeId       = _helper.getAttr( attributeNode, "typeId", true );
                        String annotations  = _helper.getAttr( attributeNode, "annotations", false );

                        AttrType attrType = checkTypeId( typeId, attributeNode, model );

                        if ( attrs.containsKey( entryName ) ) {
                            entry.addAttribute( entryName, typeId, defaultValue, attrType, annotations );
                        } else {
                            throw new XMLException( "Missing entry in Default block for " + entryName + " in type " + name );
                        }
                    }
                }

                type.addInstance( entry );
            }
        }

        model.addTypeDefinition( type );
    }

    /**
     * <Map internal="1" external="X" comment="Market NAE"/>
     *
     * @throws XMLException
     */
    private void addTypeTransformMapEntries( TypeTransform t, Node node ) throws XMLException {
        List<Node> entries = _helper.getChildElements( node, "Map", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String  internalVal    = _helper.getAttr( entry, "internal", true );
                String  externalVal    = _helper.getAttr( entry, "external", true );
                String  comment        = _helper.getAttr( entry, "comment", false );
                boolean decodePriority = _helper.getAttrBool( entry, "decodePriority", false, false );

                if ( t.isExternalBinaryFormat() && !externalVal.startsWith( "0x" ) ) {
                    throw new XMLException( "Binary external transform value must be in hex, not [" + externalVal + "] for " + t.getId() );
                }

                try {
                    t.map( internalVal, externalVal, comment, decodePriority );
                } catch( Exception e ) {
                    throw new XMLException( e.getLocalizedMessage(), e );
                }
            }
        }
    }

    private void addTypes( InternalModel model, Element internalModel ) throws XMLException {

        /*
          <Types>
            <Type id="BookingType">
                <Description>Method for booking out this order. Used when notifying a broker that an order to be settled by that broker is to be booked out as an OTC derivative (e.g. CFD or similar)</Description>
                <Entry name="Regular" value="0"/>
                <Entry name="CFD" value="1"/>
                <Entry name="TotalReturnSwap" value="2"/>
            </Type>

            <!-- sample of a type with an attribute ... if type has an attribute then it must have a default entry -->

            <Type id="SecurityType">
                <Default>
                    <Attribute name="prodType" typeId="ProductType" value="Other"/>
                </Default>

                <Entry name="Option" value="FUT">
                    <Attribute name="prodType" typeId="ProductType" value="Option"/>
                </Entry>

         */

        Element types = _helper.getElement( internalModel, "Types", true );

        List<Node> typeList = _helper.getChildElements( types, "Type", true );

        for ( Node node : typeList ) {
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addType( model, (Element) node );
            }
        }
    }

    private void addVersionAndRoot( BaseModel model, Node entry ) throws XMLException {
        String dir         = _helper.getAttr( entry, "dir", true );
        String rootPackage = _helper.getAttr( entry, "rootPackage", true );
        String version     = _helper.getAttr( entry, "version", true );

        model.setDir( dir );
        model.setRootPackage( rootPackage );
        model.setModelVersionNumber( version );
    }

    private PrimitiveType checkTypeId( String typeId, Node attr, InternalModel model ) throws XMLException {

        try {
            PrimitiveType type = PrimitiveType.get( typeId, model );

            // type will be null if complex type

            if ( type != null && !type.isValid() ) {
                throw new XMLException( "Invalid typeId " + typeId, attr );
            }

            return type;

        } catch( Exception e ) {
            throw new XMLException( "Invalid typeId " + typeId, attr, e );
        }
    }

    /*
     * <BinaryModel id="4.4" modelVersionNumber="1.0" extends="4.2">
     */
    private void extendBinaryModel( BinaryModels models, BinaryModel BinaryModel, Element eBinaryModel ) throws XMLException {
        String id = _helper.getAttr( eBinaryModel, "extends", false );

        if ( id != null ) {
            BinaryModel base = models.getBinaryModel( id );

            if ( base == null ) {
                throw new XMLException( "BinaryModel extends id not recognised : " + id );
            }

            // deep copy from base

            BinaryModel.copy( base );
        }
    }

    /*
     * <FixModel id="4.4" modelVersionNumber="1.0" extends="4.2">
     */
    private void extendFixModel( FixModels models, FixModel fixModel, Element eFixModel ) throws XMLException {
        String id = _helper.getAttr( eFixModel, "extends", false );

        if ( id != null ) {
            FixModel base = models.getFixModel( id );

            if ( base == null ) {
                throw new XMLException( "FixModel extends id not recognised : " + id );
            }

            // deep copy from base

            fixModel.copy( base );
        }
    }

    private String genNameFromType( String typeId ) {
        return typeId.substring( 0, 1 ).toLowerCase() + typeId.substring( 1 );
    }

    private String getBinaryMsgType( Node node, String attr, boolean isMand ) throws XMLMissingException {
        String typeStr = _helper.getAttr( node, attr, isMand );

        if ( !isMand && typeStr == null ) return null;

        return typeStr;
    }

    private FixEventType getFixMsgType( Node node, String attr, boolean isMand ) throws XMLMissingException {
        String typeStr = _helper.getAttr( node, attr, isMand );

        if ( !isMand && typeStr == null ) return null;

        FixEventType t = FixEventType.valueOf( typeStr );

        if ( t == null ) throw new XMLMissingException( "Invalid FixMessageType " + typeStr, node );

        return t;
    }

    private void loadBinaryCodecModel() throws XMLException {

        Element codecs = _helper.getElement( "BinaryCodecs", true );

        BinaryCodecModel model = _model.getBinaryCodecs();
        loadBinaryCodecs( model, codecs );
    }

    private void loadBinaryCodecs( BinaryCodecModel model, Element codecs ) throws XMLException {

        addVersionAndRoot( model, codecs );

        List<Node> entries = _helper.getChildElements( codecs, "BinaryCodec", true );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addBinaryCodec( model, (Element) node );
            }
        }
    }

    private void loadBinaryModel() throws XMLException {

        Element modelRoot = _helper.getElement( "BinaryModels", false );

        BinaryModels model = _model.getBinary();

        addVersionAndRoot( model, modelRoot );

        addBinaryModels( model, modelRoot );
    }

    /**
     * <Codecs>
     * <Codec id="standard44" fix="4.4" decodeInclude="xyz" extends="otherCodecId">
     * <p>
     * <!-- following entries map complex types to fix tags -->
     *
     * <ComplexType typeId="BookingType" tag="775"/>
     *
     * <ExchangeCodecs>
     * <Codec id="exchangeX_44" fix="4.4" type="Exchange" extends="standard44">
     * <p>
     * <!-- for clientX the senderSubId must be set to a property from its session -->
     *
     * <Map tagId="50" fixMessageId="NewOrderSingle" decode="com.rr.fix.exchange.exchangex.SenderSubId"/>
     * </Codec>
     * </ExchangeCodecs>
     *
     * <ClientCodecs>
     * <Codec id="clientX_44" fix="4.4" type="Client" extends="standard44">
     *
     * @throws XMLException
     */
    private void loadCodecModel() throws XMLException {

        Element codecs = _helper.getElement( "Codecs", true );

        CodecModel model = _model.getCodec();
        loadCodecs( model, codecs );

        Element exchangeCodecs = _helper.getElement( "ExchangeCodecs", true );
        loadCodecs( _model.getExchangeCodecs(), exchangeCodecs );

        Element clientCodecs = _helper.getElement( "ClientCodecs", true );
        loadCodecs( _model.getClientCodecs(), clientCodecs );
    }

    private void loadCodecs( CodecModel model, Element codecs ) throws XMLException {

        addVersionAndRoot( model, codecs );

        List<Node> entries = _helper.getChildElements( codecs, "Codec", true );

        for ( Node node : entries ) {

            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                addCodec( model, (Element) node );
            }
        }
    }

    private void loadFactoryModel() throws XMLException {

        Element modelRoot = _helper.getElement( "CodecFactories", true );

        FactoryModel model = _model.getFactoryModel();

        addVersionAndRoot( model, modelRoot );
    }

    /**
     * <FixModels>
     *
     * <FixVersion>
     * <Entry name="4.0" value="4.0"/>
     * <Entry name="4.2" value="4.2"/>
     * <Entry name="4.4" value="4.4"/>
     * <Entry name="5.0" value="5.0"/>
     * </FixVersion>
     *
     * <FixModel id="4.4" modelVersionNumber="1.0">
     * <Attribute typeId="FixVersion" value="4.4"/>
     * <Attribute typeId="FixHelper" value="com.rr.fix.codec.helper.FixCodecHelper44"/>
     *
     * <Dictionary id="4.4">
     * <!-- certain tags are special and have specific optimised codec code eg fixVersion.msgType -->
     * <!--  type should be fixed facing not internal type mapping -->
     *
     * <Tag id="1" name="Account" type="string"/>
     * <Tag id="6" name="AvgPx" type="price"/>
     *
     * @throws XMLException
     */
    private void loadFixModel() throws XMLException {

        Element modelRoot = _helper.getElement( "FixModels", true );

        FixModels model = _model.getFix();

        addVersionAndRoot( model, modelRoot );
        addFixVersions( model, modelRoot );
        addFixModels( model, modelRoot );
    }

    private void loadInternalModel() throws XMLException {

        Element internalModelElem = _helper.getElement( "InternalModel", true );

        InternalModel model = _model.getInternal();

        addVersionAndRoot( model, internalModelElem );
        addDefaultSizes( model, internalModelElem );
        addInstructions( model, internalModelElem );
        addTypes( model, internalModelElem );
        addEvents( model, internalModelElem );
    }

    /**
     * <Map eventAttr="clOrdId" fixTag="11" retainSrc="false"/>
     *
     * @throws XMLException
     */
    private void loadMessageMap( FixEventMap msgMap, Node node ) throws XMLException {

        List<Node> entries = _helper.getChildElements( node, "Map", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String  eventAttr = _helper.getAttr( entry, "eventAttr", false );
                String  fixTag    = _helper.getAttr( entry, "fixTag", true );
                int     maxOccurs = _helper.getAttrInt( entry, "maxOccurs", false, 1 );
                boolean retainSrc = _helper.getAttrBool( entry, "retainSrc", false, false );

                FixTagEventMapping mapping = new FixTagEventMapping( eventAttr, fixTag, maxOccurs, retainSrc );

                addHooks( entry, mapping );

                msgMap.addMapping( mapping );
            }
        }
    }

    /**
     * <Map eventAttr="clOrdId" field="clOrdId"/>
     *
     * @throws XMLException
     */
    private void loadMessageMap( BinaryEventMap msgMap, Node node ) throws XMLException {

        List<Node> entries = _helper.getChildElements( node, "Map", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String field     = _helper.getAttr( entry, "field", true );
                String eventAttr = _helper.getAttr( entry, "eventAttr", false, field );
                int    maxOccurs = _helper.getAttrInt( entry, "maxOccurs", false, 1 );

                // the len ONLY applies to fields of type fstr/zstr, here the mapping len overrides the len specified in the dictionary entry
                int len = _helper.getAttrInt( entry, "len", false, 0 );

                BinaryTagEventMapping mapping = new BinaryTagEventMapping( eventAttr, field, maxOccurs, len );

                addHooks( entry, mapping );

                msgMap.addMapping( mapping );
            }
        }

        //                 <MapInstruction type="split" attr1="FillsGrp"/>

        entries = _helper.getChildElements( node, "MapInstruction", false );

        for ( Node entry : entries ) {

            if ( entry.getNodeType() == Node.ELEMENT_NODE ) {
                String type = _helper.getAttr( entry, "type", true );

                if ( "split".equalsIgnoreCase( type ) ) {
                    String eventAttr = _helper.getAttr( entry, "attr1", false );

                    if ( msgMap.decodeRepeatingGroupIntoSeperateMsgs( eventAttr ) == false ) {
                        throw new XMLException( "MessageMap can only have one split instruction per message, messageMap id=" + msgMap.getId() );
                    }
                }
            }
        }
    }

    /**
     * <Dictionary id="4.4">
     * <Field id="msgType"             type="byte"             comment="message type"/>
     *
     * @param BinaryModel
     * @param eBinaryModel
     * @throws XMLException
     */
    private void populateBinaryModel( BinaryModel BinaryModel, Element eBinaryModel ) throws XMLException {
        boolean isMand = BinaryModel.getDictionaryEntries().size() == 0;
        Element dict   = _helper.getChildElement( eBinaryModel, "Dictionary", isMand );

        if ( dict != null ) {
            List<Node> entries = _helper.getChildElements( dict, "Field", isMand );

            if ( entries != null ) {
                for ( Node node : entries ) {

                    if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                        addDictionaryTag( BinaryModel, (Element) node );
                    }
                }
            }
        }
    }

    /**
     * <Dictionary id="4.4">
     * <Tag id="1" name="Account" type="string"/>
     *
     * @param fixModel
     * @param eFixModel
     * @throws XMLException
     */
    private void populateFixModel( FixModel fixModel, Element eFixModel ) throws XMLException {
        boolean isMand = fixModel.getDictionaryEntries().size() == 0;
        Element dict   = _helper.getChildElement( eFixModel, "Dictionary", isMand );

        if ( dict != null ) {
            List<Node> entries = _helper.getChildElements( dict, "Tag", isMand );

            if ( entries != null ) {
                for ( Node node : entries ) {

                    if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                        addDictionaryTag( fixModel, (Element) node );
                    }
                }
            }
        }
    }

    private void verify() throws XMLException {
        if ( !_model.verify() ) {
            throw new XMLException( "Invalid Model : " + _model.getErrors() );
        }
    }
}
