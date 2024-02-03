/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

/**
 * Identifies currency used for price. Absence of this field is interpreted as the default for the security. It is recommended that systems provide the currency value whenever possible.
 */

public enum UnitOfMeasure implements MultiByteLookup {

    Allowances( "ALLOW" ),
    AustralianDollar( "AUD" ),
    BlueBarrel( "BBL" ), // 42-gallon oil barrel
    BoardFeet( "BDFT" ),
    BrazilianReal( "BRL" ),
    Bushel( "BU" ),
    CanadianDollar( "CAD" ),
    CubicMeters( "CBM" ),
    SwissFranc( "CHF" ),
    ChileanPeso( "CLP" ),
    ClimateReserveTonnes( "CRT" ),
    Contract( "CTRCT" ),
    HundredWeight( "CWT" ),
    CzechKoruna( "CZK" ),
    DryMetricTons( "DT" ),
    EnvironmentalCredit( "ENVCRD" ),
    Euro( "EUR" ),
    Gallons( "GAL" ),
    PoundsSterling( "GBP" ),
    Grams( "GRAMS" ),
    GrossTons( "GT" ),
    HungarianFlorint( "HUF" ),
    IsraeliShekel( "ILS" ),
    IndianRupee( "INR" ),
    IndexPoints( "IPNT" ),
    JapaneseYen( "JPY" ),
    Kilolitres( "KL" ),
    KoreanWon( "KRW" ),
    Pounds( "LBS" ),
    MetricTons( "MTONS" ),
    OneMillionBritishThermalUnits( "MMBTU" ),
    MegawattsPerHour( "MWH" ),
    MexicanPeso( "MXN" ),
    MalaysianRinggit( "MYR" ),
    NorwegianKroner( "NOK" ),
    NewZealandDollar( "NZD" ),
    PolishZloty( "PLN" ),
    CertifiedEmissionReductionCredit( "RCER" ),
    RenewableIdentificationNumber( "RIN" ),
    ChineseRenminbi( "RMB" ),
    RussianRuble( "RUB" ),
    SwedishKronor( "SEK" ),
    Therm( "THM" ),
    TON( "TON" ),
    Tons( "TONS" ),
    TurkishLira( "TRY" ),
    TroyOunce( "TRYOZ" ),
    USDollar( "USD" ),
    SouthAfricanRand( "ZAR" ),
    Unknown( "" );                        // null

    private static Map<ZString, UnitOfMeasure> _map = new HashMap<>();

    static {
        for ( UnitOfMeasure en : UnitOfMeasure.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
    }

    private final byte[] _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 6; }

    public static UnitOfMeasure getVal( ZString key ) {
        UnitOfMeasure val = _map.get( key );
        if ( val == null ) throw new RuntimeDecodingException( "Unsupported value of " + key + " for UnitOfMeasure" );
        return val;
    }

    UnitOfMeasure( String val ) {

        _val = val.getBytes();
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte[] getVal() {
        return _val;
    }

    public String id()           { return name(); }
}
