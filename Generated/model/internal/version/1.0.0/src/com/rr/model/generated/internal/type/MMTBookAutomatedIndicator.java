package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Off Book Automated Indicator
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTBookAutomatedIndicator implements SingleByteLookup {

    Automated( TypeIds.MMTBOOKAUTOMATEDINDICATOR_AUTOMATED, "Q" ),
    Manual( TypeIds.MMTBOOKAUTOMATEDINDICATOR_MANUAL, "M" ),
    NotSpecified( TypeIds.MMTBOOKAUTOMATEDINDICATOR_NOTSPECIFIED, "-" ),
    Unknown( TypeIds.MMTBOOKAUTOMATEDINDICATOR_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTBookAutomatedIndicator( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final MMTBookAutomatedIndicator[] _entries = new MMTBookAutomatedIndicator[37];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTBookAutomatedIndicator en : MMTBookAutomatedIndicator.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTBookAutomatedIndicator getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTBookAutomatedIndicator" );
        }
        MMTBookAutomatedIndicator eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTBookAutomatedIndicator" );
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
