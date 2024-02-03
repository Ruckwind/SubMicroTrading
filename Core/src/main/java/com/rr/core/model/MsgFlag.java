/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * MessageFlags - misc flags should be relatively agnostic
 * <p>
 * currently message flags stored in int, if use more than 32 flags increase size of type in events
 * <p>
 * DO NOT MOVE FLAGS ABOUT .... THE bit fields are now persisted eg in SecDef table in core ref
 */

public enum MsgFlag {
    PossDupFlag,     // if a message with this sequence number has been previously received, ignore message, if not, process normally.
    PossResend,      // forward message to application and determine if previously received (i.e. verify order id and parameters).
    Reconciliation,  // DUMMY message used to keep CPU warm
    GapFill,         // event is a timed filler
    Admin,           // event generated via JMX or admin command
    TopLevelPublish, // event have been published at top level within process eg via GlobalEventManager ... so pipelines should NOT redistribute
    Internal,
    Override,        // manual flag that data should be considered manual and override others
    Historical,      // event is historical in nature eg in backtest
    DeprecatedData,  // event is for deprecated data
    Catchup,         // event is a catchup for previous event that didnt complete
    Flatten,         // event is to flatten a position
    FAKE             // ignore event which is synthetic ... eg end of month synthetic trades for swap performance reset .... event only for processing by Trade to database bridge
    ;

    private final int _bitMaskOn;
    private final int _bitMaskOff;

    public static int setFlag( int curFlags, MsgFlag flag, boolean setOn ) {
        return (setOn) ? (curFlags | flag._bitMaskOn)
                       : (curFlags & flag._bitMaskOff);
    }

    public static boolean isOn( int flags, MsgFlag flag ) {
        return (flags & flag._bitMaskOn) > 0;
    }

    MsgFlag() {
        _bitMaskOn  = 1 << ordinal();
        _bitMaskOff = ~_bitMaskOn;
    }

    public int getBitMaskOn() { return _bitMaskOn; }
}

