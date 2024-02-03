/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class SubEventAttributeDefinition extends AttributeDefinition {

    private final int _min;
    private final int _max;

    private final String _counterAttr;

    private ClassDefinition _cd;

    public SubEventAttributeDefinition( String attrName,
                                        String typeId,
                                        String defaultVal,
                                        boolean isMand,
                                        String fix44Tag,
                                        AttrType attrType,
                                        OutboundInstruction inst,
                                        int min,
                                        int max,
                                        String counterAttr,
                                        boolean forceOverride,
                                        String annotations ) {

        super( attrName, typeId, defaultVal, isMand, fix44Tag, attrType, inst, null, forceOverride, annotations );

        _min         = min;
        _max         = max;
        _counterAttr = counterAttr;
    }

    @Override
    public boolean isHandcrafted() {
        return false;
    }

    @Override
    public String getHandcraftedPackage() {
        return null;
    }

    public String getCounterAttr() {
        return _counterAttr;
    }

    public ClassDefinition getDefinition() {
        return _cd;
    }

    public void setDefinition( ClassDefinition cd ) {
        _cd = cd;
    }

    public int getMax() {
        return _max;
    }

    public int getMin() {
        return _min;
    }
}
