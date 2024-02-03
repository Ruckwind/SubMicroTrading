package com.rr.core.recovery;

import com.rr.core.component.SMTSnapshotMember;

import java.util.LinkedHashSet;

public class SnapshotDefinitionImpl implements SnapshotDefinition {

    private final LinkedHashSet<SMTSnapshotMember> _components;
    private final SnapshotType                     _type;

    public SnapshotDefinitionImpl( final SnapshotType type, final LinkedHashSet<SMTSnapshotMember> components ) {
        _type       = type;
        _components = components;
    }

    @Override public SnapshotType getType()                           { return _type; }

    @Override public LinkedHashSet<SMTSnapshotMember> getComponents() { return _components; }

    @Override public String id()                                      { return _type.name(); }
}
