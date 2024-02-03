/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

/**
 * Identifies currency used for price. Absence of this field is interpreted as the default for the security. It is recommended that systems provide the currency value whenever possible.
 * <p>
 * In an exchange rate like USD/JPY it is quote as number of JPY for 1 USD. The base currency is the USD and the risk currency is the JPY.
 * In an exchange rate like GBP/USD it is quote as number of USD for 1 GBP. The base currency is the GBP and the risk currency is the USD.
 *
 * @WARNING DONT ADD A CURRENCY WITHOUT ADDING ENTRY TO getVal
 */

public enum FXPair implements MultiByteLookup {

    AUDCAD( Currency.AUD, Currency.CAD, 1e-5 ),
    AUDCHF( Currency.AUD, Currency.CHF, 1e-5 ),
    AUDJPY( Currency.AUD, Currency.JPY, 1e-3 ),
    AUDNZD( Currency.AUD, Currency.NZD, 1e-5 ),
    AUDUSD( Currency.AUD, Currency.USD, 1e-5 ),
    CADCHF( Currency.CAD, Currency.CHF, 1e-5 ),
    CADJPY( Currency.CAD, Currency.JPY, 1e-3 ),
    CHFJPY( Currency.CHF, Currency.JPY, 1e-3 ),
    EURAUD( Currency.EUR, Currency.AUD, 1e-5 ),
    EURCAD( Currency.EUR, Currency.CAD, 1e-5 ),
    EURCHF( Currency.EUR, Currency.CHF, 1e-5 ),
    EURCNH( Currency.EUR, Currency.CNH, 1e-5 ),
    EURCNY( Currency.EUR, Currency.CNY, 1e-5 ),
    EURCZK( Currency.EUR, Currency.CZK, 1e-4 ),
    EURDKK( Currency.EUR, Currency.DKK, 1e-5 ),
    EURGBP( Currency.EUR, Currency.GBP, 1e-5 ),
    EURHUF( Currency.EUR, Currency.HUF, 1e-3 ),
    EURJPY( Currency.EUR, Currency.JPY, 1e-3 ),
    EURNOK( Currency.EUR, Currency.NOK, 1e-5 ),
    EURNZD( Currency.EUR, Currency.NZD, 1e-5 ),
    EURPLN( Currency.EUR, Currency.PLN, 1e-5 ),
    EURSEK( Currency.EUR, Currency.SEK, 1e-5 ),
    EURTRY( Currency.EUR, Currency.TRY, 1e-5 ),
    EURUSD( Currency.EUR, Currency.USD, 1e-5 ),
    GBPAUD( Currency.GBP, Currency.AUD, 1e-5 ),
    GBPCAD( Currency.GBP, Currency.CAD, 1e-5 ),
    GBPCHF( Currency.GBP, Currency.CHF, 1e-5 ),
    GBPJPY( Currency.GBP, Currency.JPY, 1e-3 ),
    GBPNZD( Currency.GBP, Currency.NZD, 1e-5 ),
    GBPUSD( Currency.GBP, Currency.USD, 1e-5 ),
    JPYBRL( Currency.JPY, Currency.BRL, 1e-5 ),
    JPYTRY( Currency.JPY, Currency.TRY, 1e-5 ),
    JPYZAR( Currency.JPY, Currency.ZAR, 1e-5 ),
    NZDCAD( Currency.NZD, Currency.CAD, 1e-5 ),
    NZDCHF( Currency.NZD, Currency.CHF, 1e-5 ),
    NZDJPY( Currency.NZD, Currency.JPY, 1e-3 ),
    NZDUSD( Currency.NZD, Currency.USD, 1e-5 ),
    USDBRL( Currency.USD, Currency.BRL, 1e-5 ),
    USDCAD( Currency.USD, Currency.CAD, 1e-5 ),
    USDCHF( Currency.USD, Currency.CHF, 1e-5 ),
    USDCLP( Currency.USD, Currency.CLP, 1e-2 ),
    USDCNH( Currency.USD, Currency.CNH, 1e-5 ),
    USDCNY( Currency.USD, Currency.CNY, 1e-5 ),
    USDCOP( Currency.USD, Currency.COP, 1e-2 ),
    USDCZK( Currency.USD, Currency.CZK, 1e-4 ),
    USDDKK( Currency.USD, Currency.DKK, 1e-5 ),
    USDHKD( Currency.USD, Currency.HKD, 1e-5 ),
    USDHUF( Currency.USD, Currency.HUF, 1e-3 ),
    USDIDR( Currency.USD, Currency.IDR, 1e-2 ),
    USDINR( Currency.USD, Currency.INR, 1e-4 ),
    USDJPY( Currency.USD, Currency.JPY, 1e-3 ),
    USDKRW( Currency.USD, Currency.KRW, 1e-2 ),
    USDMXN( Currency.USD, Currency.MXN, 1e-5 ),
    USDNOK( Currency.USD, Currency.NOK, 1e-5 ),
    USDPLN( Currency.USD, Currency.PLN, 1e-5 ),
    USDRUB( Currency.USD, Currency.RUB, 1e-4 ),
    USDSEK( Currency.USD, Currency.SEK, 1e-5 ),
    USDSGD( Currency.USD, Currency.SGD, 1e-5 ),
    USDTHB( Currency.USD, Currency.THB, 1e-5 ),
    USDTRY( Currency.USD, Currency.TRY, 1e-5 ),
    USDTWD( Currency.USD, Currency.TWD, 1e-4 ),
    USDZAR( Currency.USD, Currency.ZAR, 1e-5 ),

    OTHER( Currency.Other, Currency.Other, 1e-5 ),
    Unknown( Currency.Unknown, Currency.Unknown, Constants.UNSET_DOUBLE );

    private static final Map<ZString, FXPair> _map = new HashMap<>();

    static {
        for ( FXPair en : FXPair.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
        _map.put( new ViewString( "OTHER." ), OTHER );
    }

    private final Currency _baseCcy;
    private final Currency _riskCcy;
    private final double   _tickSize;
    private final ViewString _code;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 3; }

    public static FXPair getVal( ZString key ) {
        FXPair val = _map.get( key );
        if ( val == null ) throw new RuntimeDecodingException( "Unsupported value of " + key + " for FXPair" );
        return val;
    }

    public static FXPair get( ZString key ) {
        FXPair val = _map.get( key );
        return val;
    }

    FXPair( Currency baseCcy, Currency riskCcy, double tickSize ) {
        _code     = new ViewString( name().getBytes() );
        _baseCcy  = baseCcy;
        _riskCcy  = riskCcy;
        _tickSize = tickSize;
    }

    @Override public int getID() { return ordinal(); }

    @Override
    public byte[] getVal() {
        return _code.getBytes();
    }

    public Currency getBaseCcy() { return _baseCcy; }

    public ZString getFXCode()   { return _code; }

    public Currency getRiskCcy() { return _riskCcy; }

    public double getTickSize()  { return _tickSize; }

    public String id()           { return name(); }
}
