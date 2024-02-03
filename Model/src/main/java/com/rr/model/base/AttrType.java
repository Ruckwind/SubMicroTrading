/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public interface AttrType {

    String getSize();

    void setSize( String constantStr );

    String getTypeDeclaration();

    String getTypeDefinition();

    boolean isValid();
}
