/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Map;

public interface FixTagSet {

    boolean addRepeatingGroup( FixTagSet subSet, String id, int iCounterField, boolean isMand, String modelAttr );

    boolean addTag( int tag, boolean isMand );

    void dump( StringBuilder b );

    String getName();

    void getTagMap( Map<Tag, Boolean> dest, boolean addParent, boolean expandGroups );

    boolean hasTag( int tag );
}
