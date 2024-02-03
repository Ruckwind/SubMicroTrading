/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class SubFixEventDefinition extends FixEventDefinition {

    public SubFixEventDefinition( String id ) {
        super( id, null, null, null );
    }

    public SubFixEventDefinition( final SubFixEventDefinition fixDefn ) {
        super( fixDefn.getName(), null, null, null );
        getTags().putAll( fixDefn.getTags() );
    }

    @Override
    public String toString() {
        return "SubFixMessageDefinition [ " + super.toString() + "]";
    }
}
