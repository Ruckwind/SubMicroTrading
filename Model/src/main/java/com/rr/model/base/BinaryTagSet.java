/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public interface BinaryTagSet {

    boolean addFiller( String sTag, boolean isMand, int len, String type, String comment );

    boolean addRepeatingGroup( String sTag, boolean isMand, String counter, int blockLen );

    boolean addTag( String tag, boolean isMand, int len );

    String getComment( String tag );

    String getName();
}
