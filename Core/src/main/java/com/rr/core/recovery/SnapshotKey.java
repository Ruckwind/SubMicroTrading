package com.rr.core.recovery;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

/**
 * Files will be
 * <p>
 * ${DIRECTORY}/${BASENAME}_S_{shapshotId}.dat.{fileIndex 0001..nnnn}
 * <p>
 * Snapshot will be serialised into files which are split up when reaching max size
 */
public class SnapshotKey {

    private final ReusableString _directory    = new ReusableString();
    private final ReusableString _baseFileName = new ReusableString();
    private final int            _snapshotId   = Constants.UNSET_INT;
}
