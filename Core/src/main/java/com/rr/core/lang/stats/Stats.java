/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

public interface Stats {

    /**
     * @param id
     * @return the defined size for the id supplied
     * @throws RuntimeException if type is unknown
     */
    int find( SizeType id );

    /**
     * initialise the stats container
     */

    void initialise();

    /**
     * reread the stats from persistent store if available
     */
    void reload();

    /**
     * @param id
     * @param val
     */
    void set( SizeType id, int val );

    /**
     * persist stats if implementation supports persistence
     */
    void store();
}
