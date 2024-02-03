package com.rr.core.component;

/**
 * interfce given to components to be included in snapshot
 * <p>
 * identifies class where on recovery ALL non static non transient fields recovered from snapshot .. overwriting any config changes that may of been made
 */
public interface SMTSnapshotMemberAllFields extends SMTSnapshotMember {

}
