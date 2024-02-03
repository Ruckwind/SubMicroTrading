/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinaryModel {

    private final String _id;
    private final String _modelVersionNumber; // version for change management purposes

    private final Map<String, BinaryDictionaryTag>   _dictTags      = new LinkedHashMap<>();
    private final Map<String, BinaryEventDefinition> _binaryMsgs    = new LinkedHashMap<>();
    private final Map<String, BinaryEventDefinition> _binarySubMsgs = new LinkedHashMap<>();

    private BinaryStandardHeader  _header;
    private BinaryStandardTrailer _trailer;

    @SuppressWarnings( "unused" )
    private String  _BinaryHelperClass;
    private String  _binaryVersion;
    private boolean _messageTypeAscii = true; // message type is binary int or ascii

    public BinaryModel( String id, String ver ) {
        _id                 = id;
        _modelVersionNumber = ver;
    }

    public void addBinaryMessage( BinaryEventDefinition msg ) {
        if ( msg.isSubMessage() ) {
            _binarySubMsgs.put( msg.getBinaryMsgType(), msg );
        } else {
            _binaryMsgs.put( msg.getBinaryMsgType(), msg );
        }
    }

    public void addDictionaryTag( BinaryDictionaryTag dicTag ) {

        _dictTags.put( dicTag.getId().toLowerCase(), dicTag );
    }

    public void copy( BinaryModel base ) {

        for ( Map.Entry<String, BinaryDictionaryTag> entry : base._dictTags.entrySet() ) {
            _dictTags.put( entry.getKey(), new BinaryDictionaryTag( entry.getValue() ) );
        }

        copyModel( base._binaryMsgs, _binaryMsgs );
        copyModel( base._binarySubMsgs, _binarySubMsgs );

        _header  = new BinaryStandardHeader( base._header );
        _trailer = new BinaryStandardTrailer( base._trailer );
    }

    public BinaryEventDefinition getBinaryEvent( String name ) {
        if ( _binaryMsgs.containsKey( name ) ) {
            return _binaryMsgs.get( name );
        }
        return _binarySubMsgs.get( name );
    }

    public String getBinaryVersion() {
        return _binaryVersion;
    }

    public void setBinaryVersion( String BinaryVersion ) {
        _binaryVersion = BinaryVersion;
    }

    public Collection<BinaryDictionaryTag> getDictionaryEntries() {
        return _dictTags.values();
    }

    public BinaryDictionaryTag getDictionaryTag( String id ) {

        if ( id == null ) return null;

        return _dictTags.get( id.toLowerCase() );
    }

    public BinaryStandardHeader getHeader() {
        return _header;
    }

    public String getId() {
        return _id;
    }

    public Collection<BinaryEventDefinition> getMessages() {
        return _binaryMsgs.values();
    }

    public String getModelVersion() {
        return _modelVersionNumber;
    }

    public Map<String, Boolean> getSubTagMap( String subId, boolean addParent, boolean isEncoding ) {
        Map<String, Boolean> tags = new LinkedHashMap<>();

        BinaryEventDefinition subMapDef = _binarySubMsgs.get( subId );

        if ( subMapDef != null ) {
            tags.putAll( subMapDef.getTagMap( addParent ) );
        }

        return tags;
    }

    /**
     * get all the tags for msg
     *
     * @param map        Binary message map to get all tags for
     * @param addParent  add tags from parent/base maps
     * @param isEncoding if encoding we need to ensure tag 52 is present
     * @return map of tag to mandatory presence
     */
    public Map<String, Boolean> getTagMap( BinaryEventMap map, boolean addParent, boolean isEncoding ) {
        BinaryEventDefinition BinaryDefn = map.getEventDefinition();
        Map<String, Boolean>  tags       = new LinkedHashMap<>();

        tags.putAll( _header.getTagMap( addParent ) );
        tags.putAll( BinaryDefn.getTagMap( addParent ) );
        tags.putAll( _trailer.getTagMap( addParent ) );

        return tags;
    }

    public BinaryStandardTrailer getTrailer() {
        return _trailer;
    }

    public boolean hasMessageTypeAscii() {
        return _messageTypeAscii;
    }

    public void setBinaryHelper( String className ) {
        _BinaryHelperClass = className;
    }

    public void setMessageTypeAscii( boolean intMessageType ) {  // message type constants should be int not byte[]
        _messageTypeAscii = intMessageType;
    }

    public void setStandardHeader( BinaryStandardHeader header ) {
        _header = header;
    }

    public void setStandardTrailer( BinaryStandardTrailer trailer ) {
        _trailer = trailer;
    }

    private void copyModel( Map<String, BinaryEventDefinition> src, Map<String, BinaryEventDefinition> dest ) {
        for ( Map.Entry<String, BinaryEventDefinition> entry : src.entrySet() ) {
            BinaryEventDefinition def = entry.getValue();
            dest.put( entry.getKey(), new BinaryEventDefinition( def ) );
        }

        // update parent in copied BinaryMessageDefinitions to point to the new instances owned by this model
        for ( BinaryEventDefinition def : dest.values() ) {
            BinaryEventDefinition parent = (BinaryEventDefinition) def.getParent();

            if ( parent != null ) {
                BinaryEventDefinition newParent = dest.get( parent.getBinaryMsgType() );
                if ( newParent != null ) {
                    def.setParent( newParent );
                }
            }
        }
    }
}
