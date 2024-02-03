package com.rr.core.component;

/**
 * marker interfce given to components to be included in snapshot
 * Could of used an Annotation but interface fits more cleanly into the bootstrap and component manager
 * <p>
 * only restores fields with @Persist ... unless object created dynamically post the start phase when ALL fields persisted
 */
public interface PostRestorePatchup extends SMTComponent {

    /**
     * invoked after all components have been restored
     * place to for post restore checks and other processing ... calcs / timers
     *
     * @param snapshotTime - the create time of the snapshot file .... ie time from which to perform post recovery from eg MD subscriptions
     * @param context
     */
    default void postRestore( final long snapshotTime, final SMTStartContext context ) { /* nothing */ }
}
