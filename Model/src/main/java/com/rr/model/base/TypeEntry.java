/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeEntry {

    private final String _instanceValue;
    private final String _instanceName;

    private final Map<String, AttributeDefinition> _attributes = new LinkedHashMap<>();

    public TypeEntry( String instanceName, String instanceValue ) {
        _instanceName  = instanceName;
        _instanceValue = instanceValue;
    }

    @Override
    public String toString() {
        return "TypeEntry [" + _instanceName + ", val=" + _instanceValue + ", attr=" + _attributes + "]";
    }

    public void addAttribute( String name, String typeId, String defaultValue, AttrType type, String annotations ) {

        _attributes.put( name, new AttributeDefinition( name, typeId, defaultValue, type, annotations ) );
    }

    public AttributeDefinition getAttributeDefinition( String attrName ) {
        return _attributes.get( attrName );
    }

    public Collection<AttributeDefinition> getAttributes() {
        return _attributes.values();
    }

    public String getInstanceName() {
        return _instanceName;
    }

    public String getInstanceValue() {
        return _instanceValue;
    }
}
