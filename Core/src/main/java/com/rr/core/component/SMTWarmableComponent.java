/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

/**
 * Represents a component as defined in config that can be "warmed"
 */

public interface SMTWarmableComponent extends SMTComponent {

    /**
     * @return unique identifier of component as used in references to components in component wiring
     */
    void warmup();
}
