/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

/**
 * Represents a controllable component as defined in config
 * <p>
 * can be started/ stopped
 * has initialise method
 * <p>
 * SMTComponent must have a constructor with first element a string for the componentId
 */

public interface SMTControllableComponent extends SMTInitialisableComponent {

    /**
     * preStop is invoked on all components before stopWork as an opportunity to take some final action while all components still running
     * eg send message to HUB process
     */
    default void preStop() { /* by default no specific action */ }

    ;

    /**
     * startWork - invoked by bootstrat after all components have had following invoked
     * legacy code uses start() method in some cases that needs to be moved to prepare in others startWork
     * this method is named differently to make it simpler to identify legacy code and move it
     * DONT FORGET TO SET AS ACTIVE SO MULTIPLEXORS KNOW AVAILABLE
     * <p>
     * init - used to validate properties, makes no assumptions on state of other components
     * <p>
     * prepare - invoked after all components have been instantiated and had init invoked
     * used for second chance to get links to referenced components
     * assumes no other components have been prepared
     * <p>
     * method should return and not simply grab the main thread.
     */
    default void startWork() { /* nothing */ }

    /**
     * tell all components to stop work, at this stage no guarentee as to what other components may still be running
     */
    default void stopWork() { /* nothing */ }

    ;
}
