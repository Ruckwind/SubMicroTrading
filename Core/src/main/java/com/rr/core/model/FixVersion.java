/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public enum FixVersion {

    Fix4_0( 999999, '4', '0' ),
    Fix4_1( 999999, '4', '1' ),
    Fix4_2( 0, '4', '2' ),
    Fix4_4( 0, '4', '4' ),
    Fix5_0( 0, '5', '0' ),
    MSFix4_4( 0, '4', '4' ),
    DCFix4_4( 0, '4', '4' ),
    MDFix4_4( 0, '4', '4' ),
    MDFix5_0( 0, '5', '0' );

    public final byte _major;
    public final byte _minor;

    private final int _maxSeqNum;

    FixVersion( int maxSeqNum, char bMajor, char bMinor ) {
        _maxSeqNum = maxSeqNum;
        _major     = (byte) bMajor;
        _minor     = (byte) bMinor;
    }

    public int getMaxSeqNum() {
        return _maxSeqNum;
    }
}
