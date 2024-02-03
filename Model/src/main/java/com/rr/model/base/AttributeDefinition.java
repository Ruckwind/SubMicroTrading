/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class AttributeDefinition {

    private final String  _attrName;
    private final String  _typeId;
    private final String  _defaultValue;
    private final String  _desc;
    private final boolean _isMand;
    private final boolean _forceOverride;
    private final String  _annotations;

    private final String _fix44Tag;

    private final OutboundInstruction _instruction;

    private AttrType _attrType;

    public AttributeDefinition( String attrName, String typeId, String defaultVal, boolean isMand, String fix44Tag, AttrType attrType, OutboundInstruction inst,
                                String desc, boolean forceOverride, final String annotations ) {
        _attrName      = attrName;
        _typeId        = typeId;
        _isMand        = isMand;
        _fix44Tag      = fix44Tag;
        _attrType      = attrType;
        _defaultValue  = defaultVal;
        _instruction   = inst;
        _desc          = desc;
        _forceOverride = forceOverride;
        _annotations   = annotations;
    }

    public AttributeDefinition( String attrName, String typeId, String defaultValue, AttrType attrType, final String annotations ) {
        _attrName      = attrName;
        _typeId        = typeId;
        _defaultValue  = defaultValue;
        _attrType      = attrType;
        _isMand        = false;
        _fix44Tag      = null;
        _instruction   = OutboundInstruction.none;
        _desc          = null;
        _forceOverride = false;
        _annotations   = annotations;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((_attrName == null) ? 0 : _attrName.hashCode());
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
        AttributeDefinition other = (AttributeDefinition) obj;
        if ( _attrName == null ) {
            return other._attrName == null;
        } else return _attrName.equals( other._attrName );
    }

    @Override
    public String toString() {
        return "Attribute [name=" + _attrName + ", tag=" + _fix44Tag + ", outbound=" + _instruction +
               ", mand=" + _isMand + ", type=" + _typeId +
               ", attrType=" + _attrType + ", defaultVal=" + _defaultValue + "]";
    }

    public String getAnnotations()                          { return _annotations; }

    public String getAttrName() {
        return _attrName;
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    public String getDesc()                                 { return _desc; }

    public String getHandcraftedPackage() {
        if ( isPrimitive() || _attrType == null ) return null;

        TypeDefinition type = (TypeDefinition) _attrType;

        return (type.getPackage());
    }

    public OutboundInstruction getInstruction() {
        return _instruction;
    }

    public AttrType getType() { return _attrType; }

    public void setType( AttrType type ) {
        _attrType = type;
    }

    public String getTypeId() {
        return _typeId;
    }

    public boolean hasAnnotation( final String annotation ) { return _annotations != null && _annotations.contains( annotation ); }

    public boolean isForceOverride()                        { return _forceOverride; }

    public boolean isHandcrafted() {
        if ( isPrimitive() || _attrType == null ) return false;

        TypeDefinition type = (TypeDefinition) _attrType;

        return (type.isHandCrafted());
    }

    public boolean isMandatory() {
        return _isMand;
    }

    public boolean isPrimitive() {
        return (_attrType instanceof PrimitiveType);
    }
}
