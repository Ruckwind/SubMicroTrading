package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Code to identify status of resend requests
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MilleniumMissedMsgReportType implements SingleByteLookup {

    DownloadComplete( TypeIds.MILLENIUMMISSEDMSGREPORTTYPE_DOWNLOADCOMPLETE, "0" ),
    Incomplete( TypeIds.MILLENIUMMISSEDMSGREPORTTYPE_INCOMPLETE, "1" ),
    Unknown( TypeIds.MILLENIUMMISSEDMSGREPORTTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MilleniumMissedMsgReportType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final MilleniumMissedMsgReportType[] _entries = new MilleniumMissedMsgReportType[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MilleniumMissedMsgReportType en : MilleniumMissedMsgReportType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MilleniumMissedMsgReportType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MilleniumMissedMsgReportType" );
        }
        MilleniumMissedMsgReportType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MilleniumMissedMsgReportType" );
        return eval;
    }

    @Override
    public final byte getVal() {
        return _val;
    }

    public final int getID() {
        return _id;
    }

}
