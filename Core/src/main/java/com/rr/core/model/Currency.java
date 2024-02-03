/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.utils.SMTRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Identifies currency used for price. Absence of this field is interpreted as the default for the security. It is recommended that systems provide the currency value whenever possible.
 */

public enum Currency implements MultiByteLookup {

    ARS( "ARS" ),
    ATS( "ATS" ),
    AUD( "AUD" ),
    BGN( "BGN" ),                               // Bulgaria Lev
    BRL( "BRL" ),
    BEF( "BEF" ),
    CAD( "CAD" ),
    CAX( "CAX", Currency.CAD, false ),
    CHF( "CHF" ),
    COP( "COP" ),
    CLP( "CLP" ),
    CNH( "CNH" ),
    CNY( "CNY" ),
    CZK( "CZK" ),
    DEM( "DEM" ),
    DKK( "DKK" ),
    EUR( "EUR" ),
    FRF( "FRF" ),
    GBP( "GBP" ),
    GIP( "GIP" ),
    GBp( "GBp", Currency.GBP, false ),
    GBX( "GBX", Currency.GBP, false ),
    GBx( "GBx", Currency.GBP, false ),
    HKD( "HKD" ), // Hong Kong Dollar
    HUF( "HUF" ), // Hungarian Forint
    IDR( "IDR" ),
    INR( "INR" ),
    ILS( "ILS" ), // Israeli New Shekel
    ITL( "ITL" ),
    JPY( "JPY" ),
    MAD( "MAD" ),
    KRW( "KRW" ),
    MXN( "MXN" ),
    MYR( "MYR" ),
    NLG( "NLG" ),
    NOK( "NOK" ),
    NZD( "NZD" ),
    PEN( "PEN" ),
    PHP( "PHP" ),
    PLN( "PLN" ), // Poland z?oty
    PTE( "PTE" ),
    RON( "RON" ),
    RUB( "RUB" ), // Russian Ruble
    SAR( "SAR" ),
    SEK( "SEK" ),
    SGD( "SGD" ), // Singapore Dollar
    THB( "THB" ),
    TRY( "TRY" ),
    TWD( "TWD" ),
    USD( "USD" ),
    USX( "USX", Currency.USD, false ),
    ZAR( "ZAR" ),
    ZAr( "ZAr", Currency.ZAR, false ),
    ZAC( "ZAC", Currency.ZAR, false ),
    ZAc( "ZAc", Currency.ZAR, false ),

    Other( "999" ),                         // legal fix
    Unknown( "998" );                        // null

    private static final Map<ZString, Currency> _map = new HashMap<>();

    static {
        for ( Currency en : Currency.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
    }

    private byte[]   _val;
    private double   _toUSDFactor;
    private Currency _majorCurrency;
    private boolean  _isMajor;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 3; }

    public static Currency getVal( ZString key ) {
        Currency val = _map.get( key );
        if ( val == null ) throw new RuntimeDecodingException( "Unsupported value of " + key + " for Currency" );
        return val;
    }

    Currency( String val, Currency majorCurrency, boolean isMajor ) {
        _val           = val.getBytes();
        _majorCurrency = majorCurrency;
        _isMajor       = isMajor;

        _toUSDFactor = (isMajor) ? 1.0 : 0.01;
    }

    Currency( String val ) {
        _val           = val.getBytes();
        _majorCurrency = this;
        _isMajor       = true;
        _toUSDFactor   = 1.0;
    }

    @Override public int getID() { return ordinal(); }

    @Override
    public byte[] getVal() {
        return _val;
    }

    public double asMajor( double price ) {
        if ( _isMajor ) {
            return price;
        } else { // convert from minor to major
            return ((price + Constants.WEIGHT) / 100.0);
        }
    }

    public boolean getIsMajor()        { return _isMajor; }

    public Currency getMajorCurrency() { return _majorCurrency; }

    public String id()           { return name(); }

    public double majorMinorConvert( Currency fromCcy, double price ) {

        if ( fromCcy == null || fromCcy == this ) return price;

        if ( _majorCurrency != fromCcy._majorCurrency ) throw new SMTRuntimeException( "invalid majorMinorConvert mixing currencies " + name() + " with " + fromCcy.name() );

        if ( _isMajor ) {
            if ( fromCcy._isMajor == false ) { // convert from Minor to Major
                return ((price + Constants.WEIGHT) / 100.0);
            }
        } else if ( fromCcy._isMajor ) { // convert from Major to Minor
            return ((price + Constants.WEIGHT) * 100.0);
        }

        return price;
    }

    public double rawMajorMinorConvert( Currency fromCcy, double price ) {
        if ( _isMajor ) {
            if ( fromCcy._isMajor == false ) { // convert from Minor to Major
                return (price / 100.0);
            }
        } else if ( fromCcy._isMajor ) { // convert from Major to Minor
            return (price * 100.0);
        }

        return price;
    }

    public void setUSDFactor( double factor ) {
        _toUSDFactor = factor;
    }

    public double toUSDFactor() {
        return _toUSDFactor;
    }
}
