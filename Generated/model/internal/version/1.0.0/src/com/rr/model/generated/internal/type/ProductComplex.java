package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Indicates the type of product the security is associated with
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ProductComplex implements TwoByteLookup {

    Agency( TypeIds.PRODUCTCOMPLEX_AGENCY, "1" ),
    Commodity( TypeIds.PRODUCTCOMPLEX_COMMODITY, "2" ),
    Corporate( TypeIds.PRODUCTCOMPLEX_CORPORATE, "3" ),
    Currency( TypeIds.PRODUCTCOMPLEX_CURRENCY, "4" ),
    Equity( TypeIds.PRODUCTCOMPLEX_EQUITY, "5" ),
    Government( TypeIds.PRODUCTCOMPLEX_GOVERNMENT, "6" ),
    Index( TypeIds.PRODUCTCOMPLEX_INDEX, "7" ),
    Loan( TypeIds.PRODUCTCOMPLEX_LOAN, "8" ),
    MoneyMarket( TypeIds.PRODUCTCOMPLEX_MONEYMARKET, "9" ),
    Mortgage( TypeIds.PRODUCTCOMPLEX_MORTGAGE, "10" ),
    Municipal( TypeIds.PRODUCTCOMPLEX_MUNICIPAL, "11" ),
    Other( TypeIds.PRODUCTCOMPLEX_OTHER, "12" ),
    Financing( TypeIds.PRODUCTCOMPLEX_FINANCING, "13" ),
    InterestRate( TypeIds.PRODUCTCOMPLEX_INTERESTRATE, "14" ),
    FXCash( TypeIds.PRODUCTCOMPLEX_FXCASH, "15" ),
    Energy( TypeIds.PRODUCTCOMPLEX_ENERGY, "16" ),
    Metals( TypeIds.PRODUCTCOMPLEX_METALS, "17" ),
    Unknown( TypeIds.PRODUCTCOMPLEX_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 2; }

    private final byte[] _val;
    private final int _id;

    ProductComplex( int id, String val ) {
        _val = val.getBytes();
        _id = id;
    }
    private static ProductComplex[] _entries = new ProductComplex[256*256];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) 
            { _entries[i] = Unknown; }

        for ( ProductComplex en : ProductComplex.values() ) {
             if ( en == Unknown ) continue;
            byte[] val = en.getVal();
            int key = val[0] << 8;
            if ( val.length == 2 ) key += val[1];
            _entries[ key ] = en;
        }
    }

    public static ProductComplex getVal( byte[] val, int offset, int len ) {
        int key = val[offset++] << 8;
        if ( len == 2 ) key += val[offset];
        ProductComplex eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key>0xFF) ? ("" + (char)(key>>8) + (char)(key&0xFF)) : ("" + (char)(key&0xFF))) + " for ProductComplex" );
        return eval;
    }

    public static ProductComplex getVal( byte[] val ) {
        int offset = 0;
        int key = val[offset++] << 8;
        if ( val.length == 2 ) key += val[offset];
        ProductComplex eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key>0xFF) ? ("" + (char)(key>>8) + (char)(key&0xFF)) : ("" + (char)(key&0xFF))) + " for ProductComplex" );
        return eval;
    }

    @Override
    public final byte[] getVal() {
        return _val;
    }

    public final int getID() {
        return _id;
    }

}
