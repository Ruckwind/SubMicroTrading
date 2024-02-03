package com.rr.core.component;

/**
 * interfce given to components to be included in snapshot
 * Could of used an Annotation but interface fits more cleanly into the bootstrap and component manager
 * <p>
 * only restores fields with @Persist ... unless object created dynamically post the start phase when ALL fields persisted
 */
public interface SMTSnapshotMemberOnlyPersistFields extends SMTSnapshotMember {

}
