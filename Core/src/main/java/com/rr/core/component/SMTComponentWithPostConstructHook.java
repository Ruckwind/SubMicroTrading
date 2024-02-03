/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

/**
 * Represents a component as defined in config
 * <p>
 * requires a post construction invocation (after properties and constructor called)
 * <p>
 * used for recalculations
 */

public interface SMTComponentWithPostConstructHook extends SMTComponent {

    void postConstruction();
}
