/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.idgen;

import com.rr.core.lang.ReusableString;

public interface IDGenerator {

    /**
     * @param idField buffer to be cleared then have the ID put in
     */
    void genID( ReusableString idField );

}
