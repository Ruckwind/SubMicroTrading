/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

public interface PropertyTags {

    interface Tag {

        @Override String toString();
    }

    /**
     * @return name of this tag set
     */
    String getSetName();

    /**
     * @param tag the final part of a property so xxx.yyy.zzz would have a tag of zzz
     * @return true if the tag is a valid property
     */
    boolean isValidTag( String tag );

    Tag lookup( String tag );
}
