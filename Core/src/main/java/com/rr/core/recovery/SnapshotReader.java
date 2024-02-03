package com.rr.core.recovery;

import com.rr.core.component.SMTInitialisableComponent;

import java.util.Collection;

public interface SnapshotReader extends SMTInitialisableComponent {

    long getTimeLastSnapshot( SnapshotDefinition def, int maxAgeDays ) throws Exception;

    /**
     * restore last snapshot provided its within the maxAgeDays
     *
     * @param def
     * @param restoredComponents
     * @param maxAgeDays
     * @return
     * @throws Exception
     */
    long restoreLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, int maxAgeDays ) throws Exception;

    /**
     * @param def
     * @param restoredComponents
     * @return time of last snapshot or UNSET_LONG if none
     * @throws Exception
     */
    long restoreLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents ) throws Exception;
}
