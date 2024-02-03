/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;


/**
 * Marker Interface for a component that needs initialisation
 */

public interface SMTInitialisableComponent extends SMTComponent {

    default boolean dropComponentWhenDead() { return true; }

    /**
     * initialise component, no work should occur that assumes other components have "initialised"
     *
     * @WARNING init method is invoked AFTER snapshot recovery, so init method need to cater for
     * (a) first time run properties not set in config or constructor will be null
     * (b) component has been recovered and properties with @Persist will have been restored ... so check for NULL before recreating / overwriting and loosing recovered data
     */
    void init( SMTStartContext ctx, CreationPhase creationPhase );

    CompRunState getCompRunState();

    default void preDeadRemoval( SMTStartContext ctx ) { /* nothing by default */ }

    /**
     * prepare for start, invoked after all components have been initialised
     */
    default void prepare() { /* nothing */ }
}
