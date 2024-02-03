/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseFixTagSet implements FixTagSet {

    private final String        _name;
    private       BaseFixTagSet _parent;

    private Map<Tag, Boolean>                _tags           = new LinkedHashMap<>();
    private Map<String, GroupPlaceholderTag> _repeatingGroup = new LinkedHashMap<>();

    public BaseFixTagSet( String name, BaseFixTagSet parent ) {
        _name   = name;
        _parent = parent;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean addTag( int tag, boolean isMand ) {

        FixTag iTag = new FixTag( tag, isMand );

        boolean existed = _tags.containsKey( iTag );

        _tags.put( iTag, isMand );

        return existed;
    }

    @Override
    public boolean hasTag( int tag ) {

        FixTag iTag = new FixTag( tag );

        return _tags.containsKey( iTag );
    }

    @Override
    public boolean addRepeatingGroup( FixTagSet subSet, String id, int iCounterField, boolean isMand, String modelAttr ) {
        GroupPlaceholderTag tag = new GroupPlaceholderTag( subSet, id, iCounterField, isMand, modelAttr );

        boolean existed = _tags.containsKey( tag );

        _tags.put( tag, isMand );
        _repeatingGroup.put( id, tag );

        return existed;
    }

    @Override
    public void dump( StringBuilder b ) {

        for ( Tag tag : _tags.keySet() ) {
            tag.dump( b );
            b.append( " " );
        }
    }

    @Override
    public void getTagMap( Map<Tag, Boolean> dest, boolean addParent, boolean expandGroups ) {

        if ( addParent && _parent != null ) {
            _parent.getTagMap( dest, true, expandGroups );
        }

        for ( Map.Entry<Tag, Boolean> entry : _tags.entrySet() ) {
            Tag     tag    = entry.getKey();
            Boolean isMand = entry.getValue();

            if ( tag instanceof GroupPlaceholderTag && expandGroups ) {
                GroupPlaceholderTag grp = (GroupPlaceholderTag) tag;

                grp.getRepeatingGroup().getTagMap( dest, addParent, expandGroups );
            } else {
                dest.put( tag, isMand );
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        dump( b );

        return "BaseFixTagSet[ _parentId=" + ((_parent == null) ? "null" : _parent.getName()) + " - " + b + "]";
    }

    public BaseFixTagSet getParent() {
        return _parent;
    }

    public void setParent( FixEventDefinition parent ) {
        _parent = parent;
    }

    public Map<String, GroupPlaceholderTag> getRepeatingGroup() {
        return _repeatingGroup;
    }

    public int getRepeatingGroupCounter( String tag ) {

        GroupPlaceholderTag rTag = _repeatingGroup.get( tag );

        if ( rTag == null ) {
            if ( _parent == null ) {
                throw new RuntimeException( "Missing counter tag for group id " + tag );
            }

            return _parent.getRepeatingGroupCounter( tag );
        }

        return rTag.getCounterTag();
    }

    public boolean isMandatory( int tag ) {

        FixTag iTag = new FixTag( tag );

        Boolean mand = _tags.get( iTag );

        if ( mand == null ) {
            return _parent.isMandatory( tag );
        }

        return mand;
    }

    public boolean isRepeatingGroup( String tag ) {

        GroupPlaceholderTag rTag = _repeatingGroup.get( tag );

        if ( rTag == null ) {
            if ( _parent == null ) return false;

            return _parent.isRepeatingGroup( tag );
        }

        return true;
    }

    protected Map<Tag, Boolean> getTags() {
        return _tags;
    }

}
