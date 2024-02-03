package com.rr.core.model;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum DataSrc implements MultiByteLookup {
    CME( "CME direct" ),
    BXE( "CBOE BATS" ),
    CXE( "CBOE CHIX" ),
    DXE( "CBOE DXE" ),
    SAP( "S and P" ), // deprecated
    UNS( "Unspecified" );

    private static final Map<ZString, DataSrc> _map = new HashMap<>();

    static {
        for ( DataSrc en : DataSrc.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
    }

    private final String _fullCode;
    private final byte[] _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 2; }

    public static DataSrc getVal( ZString key ) {
        DataSrc val = _map.get( key );
        if ( val == null ) return DataSrc.UNS;
        return val;
    }

    DataSrc( String fullCode ) {
        _fullCode = fullCode;
        _val      = name().getBytes();
    }

    @Override public int getID()     { return ordinal(); }

    @Override public byte[] getVal() { return _val; }

    public String getFullCode()        { return _fullCode; }

    public String id()               { return name(); }
}
