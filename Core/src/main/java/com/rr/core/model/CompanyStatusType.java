package com.rr.core.model;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

/**
 * taken from S&P  ciqcompanystatustype .... they are the source for this, required mainly to check when status changes
 */

import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings( { "unused", "override" } )

public enum CompanyStatusType implements MultiByteLookup {

    Operating( 1, "Operating" ),
    OperatingSubsidiary( 2, "Operating Subsidiary" ),
    Reorganizing( 4, "Reorganizing" ),
    OutOfBusiness( 5, "Out of Business" ),
    Acquired( 6, "Acquired" ),
    NoLongerInvesting( 7, "No Longer Investing" ),
    Launched( 8, "Launched" ),
    FirstClose( 9, "First Close" ),
    SecondaryClose( 10, "Secondary Close" ),
    FinalClose( 11, "Final Close" ),
    FullyInvested( 12, "Fully Invested" ),
    FullyLiquidated( 13, "Fully Liquidated" ),
    Withdrawn( 14, "Withdrawn" ),
    PreEventProfile( 16, "Pre-Event Profile" ),
    NonOperatingShellCompany( 17, "Non-Operating Shell Company" ),
    Inactive( 18, "Inactive Index, Exchange Rate, or Interest Rate" ),
    Liquidating( 19, "Liquidating" ),
    Active( 20, "Active" ),
    Unknown( 0, "Unknown" );

    private static final Map<ZString, CompanyStatusType> _map  = new HashMap<>();
    private static final IntMap<CompanyStatusType>       _imap = new IntHashMap<>();

    static {
        for ( CompanyStatusType en : CompanyStatusType.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
            _imap.put( en.getCode(), en );
        }
    }

    private final String _fullCode;
    private final String _desc;
    private final byte[] _val;
    private final int    _code;

    public static CompanyStatusType getVal( ZString key ) {
        CompanyStatusType val = _map.get( key );
        if ( val == null ) return CompanyStatusType.Unknown;
        return val;
    }

    public static CompanyStatusType getVal( int key ) {
        CompanyStatusType val = _imap.get( key );
        if ( val == null ) return CompanyStatusType.Unknown;
        return val;
    }

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 2; }

    CompanyStatusType( int val, String desc ) {
        _fullCode = "" + val;
        _desc     = desc;
        _val      = name().getBytes();
        _code     = val;
    }

    @Override public int getID()       { return ordinal(); }

    @Override public byte[] getVal()   { return _val; }

    public int getCode()               { return _code; }

    public String getFullCode()        { return _fullCode; }

    public String id()                 { return name(); }
}
