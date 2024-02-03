/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.FixEventType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FixModel {

    private static final int SEND_TIME_TAG = 52;

    private final String _id;
    private final String _modelVersionNumber; // version for change management purposes

    private final Map<Integer, FixDictionaryTag>        _dictTags   = new LinkedHashMap<>();
    private final Map<String, Integer>                  _nameToTag  = new LinkedHashMap<>();
    private final Map<FixEventType, FixEventDefinition> _fixMsgs    = new LinkedHashMap<>();
    private final Map<String, SubFixEventDefinition>    _subFixMsgs = new LinkedHashMap<>();

    private FixStandardHeader  _header;
    private FixStandardTrailer _trailer;

    @SuppressWarnings( "unused" )
    private String _fixHelperClass;
    private String _fixVersion;
    private String _shortFixVersion;

    public FixModel( String id, String ver ) {
        _id                 = id;
        _modelVersionNumber = ver;
    }

    public void addDictionaryTag( FixDictionaryTag dicTag ) {

        _dictTags.put( dicTag.getId(), dicTag );
        _nameToTag.put( dicTag.getName().toLowerCase(), dicTag.getId() );
    }

    public void addFixMessage( FixEventDefinition msg ) {
        _fixMsgs.put( msg.getFixMsgType(), msg );
    }

    public void addSubFixMessage( SubFixEventDefinition msg ) {
        _subFixMsgs.put( msg.getName(), msg );
    }

    public void copy( FixModel base ) {

        for ( Map.Entry<Integer, FixDictionaryTag> entry : base._dictTags.entrySet() ) {
            _dictTags.put( entry.getKey(), new FixDictionaryTag( entry.getValue() ) );
        }

        _nameToTag.putAll( base._nameToTag );

        for ( Map.Entry<FixEventType, FixEventDefinition> entry : base._fixMsgs.entrySet() ) {
            FixEventDefinition def = entry.getValue();
            _fixMsgs.put( entry.getKey(), new FixEventDefinition( def ) );
        }

        for ( Entry<String, SubFixEventDefinition> entry : base._subFixMsgs.entrySet() ) {
            SubFixEventDefinition def = entry.getValue();
            _subFixMsgs.put( entry.getKey(), new SubFixEventDefinition( def ) );
        }

        // update parent in copied FixMessageDefinitions to point to the new instances owned by this model
        for ( FixEventDefinition def : _fixMsgs.values() ) {
            FixEventDefinition parent = (FixEventDefinition) def.getParent();

            if ( parent != null ) {
                FixEventDefinition newParent = _fixMsgs.get( parent.getFixMsgType() );
                if ( newParent != null ) {
                    def.setParent( newParent );
                }
            }
        }

        _header  = new FixStandardHeader( base._header );
        _trailer = new FixStandardTrailer( base._trailer );
    }

    public Collection<FixDictionaryTag> getDictionaryEntries() {
        return _dictTags.values();
    }

    public FixDictionaryTag getDictionaryTag( Integer tag ) {

        return _dictTags.get( tag );
    }

    public Integer getDictionaryTag( String attrName ) {

        return _nameToTag.get( attrName.toLowerCase() );
    }

    public FixEventDefinition getFixMessage( FixEventType name ) {
        return _fixMsgs.get( name );
    }

    public String getFixVersion() {
        return _fixVersion;
    }

    public void setFixVersion( String fixVersion ) {
        _fixVersion = fixVersion;

        _shortFixVersion = fixVersion.replaceAll( "\\.", "" );
    }

    public FixStandardHeader getHeader() {
        return _header;
    }

    public String getId() {
        return _id;
    }

    public Collection<FixEventDefinition> getMessages() {
        return _fixMsgs.values();
    }

    public String getModelVersion() {
        return _modelVersionNumber;
    }

    public String getShortFixVersion() {
        return _shortFixVersion;
    }

    public SubFixEventDefinition getSubFixMessage( String id ) {
        return _subFixMsgs.get( id );
    }

    public Collection<SubFixEventDefinition> getSubFixMessages() {
        return _subFixMsgs.values();
    }

    /**
     * get all the tags for msg
     *
     * @param map        fix message map to get all tags for
     * @param addParent  add tags from parent/base maps
     * @param isEncoding if encoding we need to ensure tag 52 is present
     * @return map of tag to mandatory presence
     */
    public Map<Tag, Boolean> getTagMap( FixEventMap map, boolean addParent, boolean isEncoding, boolean expandGroups, boolean subEvent ) {
        FixEventDefinition fixDefn = map.getEventDefinition();
        Map<Tag, Boolean>  tags    = new LinkedHashMap<>();

        if ( !subEvent ) {
            _header.getTagMap( tags, addParent, expandGroups );
        }

        if ( isEncoding && !subEvent ) {
            Boolean isMand   = Boolean.TRUE;
            Tag     sendTime = new FixTag( SEND_TIME_TAG, true );

            // TODO refactor model so a field can be present for encoding but not in decoding ... tags like 52 want to ignore on decode exec rpts / cancel requests
            if ( !tags.containsKey( sendTime ) ) {
                tags.put( sendTime, isMand );
            }
        }

        fixDefn.getTagMap( tags, addParent, expandGroups );

        if ( !subEvent ) {
            _trailer.getTagMap( tags, addParent, expandGroups );
        }

        return tags;
    }

    public FixStandardTrailer getTrailer() {
        return _trailer;
    }

    public void removeDictionaryTag( int id ) {
        Integer iId = id;

        _dictTags.remove( iId );

        _nameToTag.entrySet().removeIf( entry -> entry.getValue().equals( iId ) );
    }

    public void setFixHelper( String className ) {
        _fixHelperClass = className;
    }

    public void setStandardHeader( FixStandardHeader header ) {
        _header = header;
    }

    public void setStandardTrailer( FixStandardTrailer trailer ) {
        _trailer = trailer;
    }
}
