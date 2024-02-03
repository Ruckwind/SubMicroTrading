package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public enum OptionType implements SingleByteLookup {

    Call( 'C' ),
    Put( 'P' );

    private final ReusableString _id;
    private final byte _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 1; }

    public static OptionType getVal( byte val ) {
        if ( val == 'P' ) return Put;
        if ( val == 'C' ) return Call;
        return null;
    }

    OptionType( char val ) {
        _id  = new ReusableString( toString() );
        _val = (byte) val;
    }

    @Override public int getID()            { return ordinal(); }

    @Override public final byte getVal() {
        return _val;
    }

    public ZString id( ReusableString out ) { return _id; }
}
