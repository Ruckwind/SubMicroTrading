/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

// Indicates type of security. See also the Product (460) and CFICode (461) fields. It is recommended that CFICode (461) be used instead of SecurityType (167) for non-Fixed Income instruments

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum SecurityType implements MultiByteLookup {

    Cash( "CASH", ProductType.Other ),
    ConvBond( "CB", ProductType.Other ),
    CommonStock( "CS", ProductType.Equity ),
    CorpBond( "CORP", ProductType.Other ),
    Equity( "EQUITY", ProductType.Equity ),
    ExchangeTradedCommodity( "ETC", ProductType.Other ),
    ExchangeTradedFund( "ETF", ProductType.Other ),
    ExchangeTradedNote( "ETN", ProductType.Other ),
    EuroCertDeposit( "EUCD", ProductType.Other ),
    ForeignExchangeContract( "FOR", ProductType.Other ),
    Future( "FUT", ProductType.Future ),
    FX( "FX", ProductType.FX ),
    GovTreasuries( "GOVT", ProductType.Other ),
    MunicipalFund( "MF", ProductType.Other ),
    MunicipalBond( "MUNI", ProductType.Other ),
    Option( "OPT", ProductType.Option ),
    PreferredStock( "PS", ProductType.Equity ),
    USTreasuryBill( "USTB", ProductType.Other ),
    Warrant( "WAR", ProductType.Other ),
    None( "NONE", ProductType.Other ),
    MultiLeg( "MLEG", ProductType.Other ),
    Strategy( "STRAT", ProductType.Strat ),
    Index( "IDX", ProductType.Index ),
    Basket( "BASKET", ProductType.Basket ),
    OptionsOnFutures( "OOF", ProductType.Option ),
    Unknown( "?", ProductType.Other );

    private static Map<ZString, SecurityType> _map = new HashMap<>();

    static {
        for ( SecurityType en : SecurityType.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
    }

    private byte[]      _val;
    private ProductType _prodType;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 6; }

    public static SecurityType getVal( ZString key ) {
        SecurityType val = _map.get( key );
        if ( val == null ) throw new RuntimeDecodingException( "Unsupported value of " + key + " for SecurityType" );
        return val;
    }

    SecurityType( String val, ProductType prodType ) {
        _val      = val.getBytes();
        _prodType = prodType;
    }

    @Override public int getID()     { return ordinal(); }

    @Override
    public final byte[] getVal() {
        return _val;
    }

    public ProductType getProdType() { return _prodType; }

    public String id()               { return name(); }
}
