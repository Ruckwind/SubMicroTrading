/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

import com.rr.core.codec.binary.fastfix.common.FieldDataType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MetaTemplate extends MetaBaseEntry {

    private String              _dictionaryId;
    private List<MetaBaseEntry> _entries = new ArrayList<>();

    public MetaTemplate( String name, int id, String dictionaryId ) {
        super( name, id, false, FieldDataType.template );
        setDictionaryId( dictionaryId );
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder( "MetaTemplate : " );

        s.append( super.toString() );

        for ( int i = 0; i < _entries.size(); ++i ) {
            s.append( "\n    " ).append( _entries.get( i ) );
        }

        return s.toString();
    }

    public void addEntry( MetaBaseEntry entry ) {
        _entries.add( entry );
    }

    public String getDictionaryId() {
        return _dictionaryId;
    }

    public void setDictionaryId( String dictionaryId ) {
        _dictionaryId = dictionaryId;
    }

    public Iterator<MetaBaseEntry> getEntryIterator() {
        return _entries.iterator();
    }
}

