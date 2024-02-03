/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recovery;

import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

public class SnapshotCaretakerAdmin implements SnapshotCaretakerAdminMBean {

    private static final Logger    _log     = LoggerFactory.create( SnapshotCaretakerAdmin.class );
    private static final ErrorCode ERR_SNAP = new ErrorCode( "SCB100", "Error in snapshot" );

    private final String _id;

    private SnapshotCaretaker _caretaker;

    public SnapshotCaretakerAdmin( final SnapshotCaretaker caretaker ) {
        _caretaker = caretaker;
        _id        = _caretaker.id() + "MBean";
    }

    @Override public String getName() { return _id; }

    @Override public String takeSnapshot() {

        String status;

        try {
            _log.info( "SnapshotCaretakerMBeanImpl invoking takeSnapshot" );

            _caretaker.takeSnapshot();

            status = "Snapshot completed";

        } catch( Exception e ) {

            status = "Snapshot Error : " + e.getMessage();

            _log.error( ERR_SNAP, e.getMessage(), e );
        }

        return status;
    }
}
