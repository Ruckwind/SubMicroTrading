/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.*;

public class TypeDefinition implements AttrType {

    private final String _id;
    private final String _desc;

    private final boolean _isHandcrafted;
    /**
     * _attributes contains all the attributes that this type has
     */
    private final Map<String, AttributeDefinition> _attributes = new LinkedHashMap<>();
    private final Set<String> _values = new LinkedHashSet<>();
    /**
     * _entries contains all the valid instances of this type
     */
    private final Map<String, TypeEntry> _entries = new LinkedHashMap<>();
    private String _package; // handcrafted can have an optional packaage specified
    private int    _maxMultiValue;
    private int    _maxValueLen = 1;
    private String _valType;
    private String _extraInterfaces;

    public TypeDefinition( String id, String desc, int multiValue, boolean isHandcrafted ) {
        _id            = id;
        _desc          = desc;
        _maxMultiValue = multiValue;
        _isHandcrafted = isHandcrafted;
    }

    @Override
    public String getSize() {
        return "" + _maxMultiValue;
    }

    @Override
    public void setSize( String constantStr ) {
        throw new RuntimeException( "TypeDefinition.setSize not appropriate : id=" + _id + ", sizeStr=" + constantStr );
    }

    @Override
    public String getTypeDeclaration() {
        return _id;
    }

    @Override
    public String getTypeDefinition() {
        return _id;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return "TypeDefinition [" + _id + ", attrs=" + _attributes + ", desc=" + _desc +
               ", multiValOcc=" + _maxMultiValue + ", entries=" + _entries + "]";
    }

    public void addAttribute( String name, String typeId, String defaultValue, AttrType attrType, String annotations ) {

        _attributes.put( name, new AttributeDefinition( name, typeId, defaultValue, attrType, annotations ) );
    }

    public void addInstance( TypeEntry entry ) {

        _entries.put( entry.getInstanceName(), entry );
        _values.add( entry.getInstanceValue() );

        int entryLen = entry.getInstanceValue().length();

        if ( entryLen > _maxValueLen ) {
            _maxValueLen = entryLen;
        }
    }

    public void addUnknown() {
        if ( isByteValType() ) {
            addInstance( new TypeEntry( "Unknown", "-1" ) );
        } else {
            addInstance( new TypeEntry( "Unknown", "?" ) );
        }
    }

    public boolean containsEntry( String key ) {
        return _entries.containsKey( key );
    }

    public boolean containsValue( String internalVal ) {
        if ( _isHandcrafted ) {
            return true; //  cant validate without adding reflection
        }
        return _values.contains( internalVal );
    }

    public AttributeDefinition getAttributeDefinition( String attrName ) {
        return _attributes.get( attrName );
    }

    public Collection<AttributeDefinition> getAttributes() {
        return _attributes.values();
    }

    public String getDesc() {
        return _desc;
    }

    public Collection<TypeEntry> getEntries() {
        return _entries.values();
    }

    public String getExtraInterfaces() {
        return _extraInterfaces;
    }

    public void setExtraInterfaces( String extraInterfaces ) {
        _extraInterfaces = extraInterfaces;
    }

    public String getId() {
        return _id;
    }

    public String getInstanceName( String externalVal ) {
        if ( externalVal == null ) return null;

        for ( TypeEntry entry : _entries.values() ) {
            if ( externalVal.equals( entry.getInstanceValue() ) ) {
                return entry.getInstanceName();
            }
        }
        return null;
    }

    public int getMaxEntryValueLen() {
        return (isByteValType()) ? 1 : _maxValueLen;
    }

    public void setMaxEntryValueLen( int maxValueLen ) {
        _maxValueLen = maxValueLen;
    }

    public int getMaxMultiValue() {
        return _maxMultiValue;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage( String packge ) {
        _package = packge;
    }

    public String getValType() {
        return _valType;
    }

    public void setValType( String valType ) {
        _valType = valType;
    }

    public Set<String> getValues() {
        return _values;
    }

    public boolean isByteValType() {
        return "byte".equalsIgnoreCase( _valType );
    }

    public boolean isHandCrafted() {
        return _isHandcrafted;
    }
}
