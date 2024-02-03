package com.rr.core.recovery;

import com.rr.core.component.SMTInitialisableComponent;

public interface SnapshotWriter extends SMTInitialisableComponent {

    void takeSnapshot( SnapshotDefinition snapshot ) throws Exception;
}
