/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.base.type.ViewStringType;

import java.util.*;

public class ClassDefinition {

    public enum Type {Base, Event, SubEvent}

    private final String _superClass; // superclass for this class
    private final String _id;
    private final String _reusableType;
    private final Type   _type;

    private final Map<String, AttributeDefinition> _attributes      = new LinkedHashMap<>();
    private final List<String>                     _eventHooks      = new ArrayList<>();
    private       ClassDefinition                  _baseDefinition  = null;
    private       boolean                          _hasViewString   = false;
    private       EventStreamSrc                   _src;
    private       int                              _eventId;
    private       String                           _extraInterfaces = null;

    public ClassDefinition( String id, String base, Type type, String reusableType, EventStreamSrc msrc ) {
        _id           = id;
        _superClass   = base;
        _type         = type;
        _reusableType = reusableType;
        _src          = msrc;
    }

    @Override
    public String toString() {
        return "ClassDefinition [ id=" + _id + ", type=" + _type + ", extends=" + _superClass + ", attrs=" + _attributes + "]";
    }

    public boolean addAttribute( String attrName, String typeId, String defaultVal, boolean isMand, String fix44Tag, AttrType attrType, OutboundInstruction instruction, final String desc, final boolean forceOverride, String annotations ) {

        String key = attrName.toLowerCase();

        if ( _attributes.containsKey( key ) ) {
            return false;
        }

        _attributes.put( key, new AttributeDefinition( attrName, typeId, defaultVal, isMand, fix44Tag, attrType, instruction, desc, forceOverride, annotations ) );

        if ( attrType != null && (attrType.getClass() == ViewStringType.class) ) {
            _hasViewString = true;
        }

        return true;
    }

    public void addEventHook( String s ) { _eventHooks.add( s ); }

    public boolean addSubEvent( String attrName,
                                String typeId,
                                String defaultVal,
                                boolean isMand,
                                String fix44Tag,
                                AttrType attrType,
                                OutboundInstruction instruction,
                                int min,
                                int max,
                                String counterAttr,
                                boolean forceOverride,
                                String annotations ) {

        String key = attrName.toLowerCase();

        if ( _attributes.containsKey( key ) ) {
            return false;
        }

        _attributes.put( key, new SubEventAttributeDefinition( attrName, typeId, defaultVal, isMand, fix44Tag, attrType, instruction, min, max, counterAttr, forceOverride, annotations ) );

        if ( attrType != null && (attrType.getClass() == ViewStringType.class) ) {
            _hasViewString = true;
        }

        return true;
    }

    public AttributeDefinition getAttribute( String attr ) {

        String key = attr.toLowerCase();

        AttributeDefinition def = _attributes.get( key );

        if ( def == null && _baseDefinition != null ) def = _baseDefinition.getAttribute( key );

        return def;
    }

    public Collection<AttributeDefinition> getAttributes( boolean addBase ) {

        LinkedHashSet<AttributeDefinition> attrs = new LinkedHashSet<>( _attributes.values() );

        if ( _baseDefinition != null && addBase ) {
            attrs.addAll( _baseDefinition.getAttributes( addBase ) );
        }

        return attrs;
    }

    public String getBase() {
        return _superClass;
    }

    public ClassDefinition getBaseDefinition() {
        return _baseDefinition;
    }

    public void setBaseDefinition( ClassDefinition baseDef ) {
        _baseDefinition = baseDef;
    }

    public List<String> getEventHooks()  { return _eventHooks; }

    /**
     * @return the generated event integer id (for switch statements)
     */
    public int getEventIntId() {
        return _eventId;
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

    public String getReusableType() {
        return _reusableType;
    }

    public EventStreamSrc getStreamSrc() {
        return _src;
    }

    public Type getType() {
        return _type;
    }

    public boolean hasUseViewString() {

        if ( _src == EventStreamSrc.both ) return false; // no base

        boolean has = _hasViewString;

        if ( has == false && _baseDefinition != null ) {
            has = _baseDefinition.hasUseViewString();
        }

        return has;
    }

    public boolean isAttrInherited( final String attrName ) {
        if ( _baseDefinition != null ) {
            return (_baseDefinition.getAttribute( attrName ) != null);
        }

        return false;
    }

    public boolean isAttrMandatory( String tag ) {
        AttributeDefinition def = getAttribute( tag );

        if ( def != null ) {
            return def.isMandatory();
        }

        return false;
    }

    public boolean isInterface() {
        return _type == Type.Base;
    }

    public boolean isSubEvent() {
        return _type != null && _type == Type.SubEvent;
    }

    public void setEventId( int eventId ) {
        _eventId = eventId;
    }
}
