package com.rr.core.recovery;

import com.rr.core.component.SMTInitialisableComponent;

import java.util.Collection;

/**
 * Reader to read data into prod that was exported from BackTest
 */
public interface ComponentImportReader extends SMTInitialisableComponent {

    long getTimeLastSnapshot( SnapshotDefinition def, int maxAgeDays ) throws Exception;

    /**
     * restore last snapshot provided its within the maxAgeDays
     *
     * @param def
     * @param restoredComponents
     * @param maxAgeDays         - max days to look backward to find snapshot to restore
     * @param minTimestamp       - ignore any timestamps OLDER than this minTimestamo
     * @return
     * @throws Exception
     */
    long importLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, int maxAgeDays, long minTimestamp ) throws Exception;

    /**
     * @param def
     * @param restoredComponents
     * @return time of last snapshot or UNSET_LONG if none
     * @throws Exception
     */
    long importLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, long minTimestamp ) throws Exception;
}
