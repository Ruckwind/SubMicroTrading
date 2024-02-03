package com.rr.model.generated.fix.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


import com.rr.core.codec.CodecName;

import com.rr.core.model.FixVersion;

public enum CodecId implements CodecName {
    MD44( FixVersion.MDFix4_4 ),
    MD50( FixVersion.MDFix5_0 ),
    Standard44( FixVersion.Fix4_4 ),
    Standard50( FixVersion.Fix5_0 ),
    Standard42( FixVersion.Fix4_2 ),
    SampleBroker1Fix44( FixVersion.Fix4_4 ),
    StratInternalFix44( FixVersion.Fix4_4 ),
    DropCopy44( FixVersion.DCFix4_4 ),
    ClientX_44( FixVersion.Fix4_4 ),
    CHIX( FixVersion.Fix4_2 ),
    MDBSE( FixVersion.MDFix5_0 ),
    CMEMD( FixVersion.MDFix4_4 ),
    CME( FixVersion.Fix4_2 ),
    UTPEuronextCash( null ),
    MilleniumLSE( null ),
    ItchLSE( null ),
    BaseETI( null ),
    ETIEurexHFT( null ),
    ETIEurexLFT( null ),
    ETIBSE( null ),
    CMESimpleBinary( null ),
    TCPPitchCHIX( null ),
    TCPHistoricPitchCHIX( null ),
    SOUP2( null ),
    SoupBin3( null );

    private FixVersion _ver;

    CodecId( FixVersion ver )  {
        _ver = ver;
    }

    public FixVersion getFixVersion() { return _ver; }
}
