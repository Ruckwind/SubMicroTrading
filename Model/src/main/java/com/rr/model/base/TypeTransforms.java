/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.TypeTransform;

import java.util.Set;

public interface TypeTransforms {

    void addTypeTransform( String typeId, TypeTransform t );

    String getId();

    TypeTransform getTypeTransform( String typeId );

    Set<String> getTypeTransformIds();
}
