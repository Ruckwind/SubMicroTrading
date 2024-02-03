/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseBinaryTagSet implements BinaryTagSet {

    private final String           _name;
    private       BaseBinaryTagSet _parent;

    private Map<String, Boolean> _tags           = new LinkedHashMap<>();
    private Map<String, String>  _repeatingGroup = new LinkedHashMap<>();
    private Map<String, Integer> _groupBlockLen  = new LinkedHashMap<>();

    private Map<String, Integer> _tagLen     = new LinkedHashMap<>();
    private Map<String, String>  _fillerType = new LinkedHashMap<>();
    private Map<String, String>  _comments   = new HashMap<>();

    public BaseBinaryTagSet( String name, BaseBinaryTagSet parent ) {
        _name   = name;
        _parent = parent;
    }

    @Override
    public boolean addFiller( String tag, boolean isMand, int len, String type, String comment ) {

        boolean existed = _tags.containsKey( tag );

        _tags.put( tag, isMand );
        _tagLen.put( tag, len );
        _fillerType.put( tag, type );
        _comments.put( tag, comment );

        return existed;
    }

    @Override
    public boolean addRepeatingGroup( String tag, boolean isMand, String counter, int blockLen ) {
        boolean existed = _tags.containsKey( tag );

        _tags.put( tag, isMand );
        _repeatingGroup.put( tag, counter );
        _groupBlockLen.put( tag, blockLen );

        return existed;
    }

    @Override
    public boolean addTag( String tag, boolean isMand, int len ) {

        boolean existed = _tags.containsKey( tag );

        _tags.put( tag, isMand );
        _tagLen.put( tag, len );

        return existed;
    }

    @Override
    public String getComment( String tag ) {
        return _comments.get( tag );
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        for ( String tag : _tags.keySet() ) {
            b.append( tag ).append( " " );
        }

        return "BaseBinaryTagSet[ _parentId=" + ((_parent == null) ? "null" : _parent.getName()) + " - " + b + "]";
    }

    public String getFillerType( String tag ) {

        String val = _fillerType.get( tag );

        if ( val == null ) {
            return (_parent == null) ? null : _parent.getFillerType( tag );
        }

        return val;
    }

    public int getLen( String tag ) {

        Integer val = _tagLen.get( tag );

        if ( val == null ) {
            return (_parent == null) ? 0 : _parent.getLen( tag );
        }

        return val;
    }

    public BaseBinaryTagSet getParent() {
        return _parent;
    }

    public void setParent( BinaryEventDefinition parent ) {
        _parent = parent;
    }

    public int getRepeatingGroupBlockLen( String tag ) {

        Integer blockLen = _groupBlockLen.get( tag );

        if ( blockLen == null ) {
            if ( _parent == null ) return 0;

            return _parent.getRepeatingGroupBlockLen( tag );
        }

        return blockLen;
    }

    public String getRepeatingGroupCounter( String tag ) {

        String counter = _repeatingGroup.get( tag );

        if ( counter == null ) {
            if ( _parent == null ) return null;

            return _parent.getRepeatingGroupCounter( tag );
        }

        return counter;
    }

    public Map<String, Boolean> getTagMap( boolean addParent ) {
        Map<String, Boolean> tags;

        if ( addParent && _parent != null ) {
            tags = _parent.getTagMap( true );
            tags.putAll( _tags );
        } else {
            tags = new LinkedHashMap<>( _tags );
        }

        return tags;
    }

    public boolean isMandatory( String tag ) {

        Boolean mand = _tags.get( tag );

        if ( mand == null ) {
            if ( _parent == null ) return false;

            return _parent.isMandatory( tag );
        }

        return mand;
    }

    public boolean isRepeatingGroup( String tag ) {

        String counter = _repeatingGroup.get( tag );

        if ( counter == null ) {
            if ( _parent == null ) return false;

            return _parent.isRepeatingGroup( tag );
        }

        return true;
    }

    protected Map<String, Boolean> getTags() {
        return _tags;
    }
}
