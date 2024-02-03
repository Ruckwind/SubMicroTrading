package com.rr.core.recovery;

import com.rr.core.component.SMTSnapshotMember;

import java.util.LinkedHashSet;

public interface SnapshotDefinition {

    /**
     * @return list of top level SMT components that will be used to form the snapshot
     */
    LinkedHashSet<SMTSnapshotMember> getComponents();

    SnapshotType getType();

    String id();
}
