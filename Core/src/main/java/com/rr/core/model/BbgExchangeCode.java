package com.rr.core.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BBG Exchange Code used for FIGI lookup
 */
@SuppressWarnings( { "unused" } )
public enum BbgExchangeCode {

    // some MICs could map to multiple exchanges, be careful!
    // futures only (listed as non-equity derivs exchanges online)
    ICE( ExchangeCode.IFEU, ExchangeCode.IFLX ), // ICE FUTURES EUROPE
    NYB( ExchangeCode.IFUS ), // ICE MARKETS AGRICULTURE seemed like best fit
    CBT( ExchangeCode.XCBT ),
    CMX( ExchangeCode.XCEC ),
    CME( ExchangeCode.XCME ),
    NYM( ExchangeCode.XNYM ),

    // others
    NA( ExchangeCode.XAMS ),
    PL( ExchangeCode.XLIS ),
    EB( ExchangeCode.BATE ),
    LN( ExchangeCode.XLON ),
    IM( ExchangeCode.XMIL ),
    FP( ExchangeCode.XPAR ),
    ID( ExchangeCode.XDUB ),
    AV( ExchangeCode.XWBO ),
    SE( ExchangeCode.XSWX ),
    SM( ExchangeCode.BMEX ),
    PW( ExchangeCode.XWAR ),
    CK( ExchangeCode.XPRA ),
    HB( ExchangeCode.XBUD ),

    BB( ExchangeCode.XBRU ),
    DC( ExchangeCode.XCSE ),
    GY( ExchangeCode.XETR ),
    FH( ExchangeCode.XHEL ),
    NO( ExchangeCode.XOSL ),
    SS( ExchangeCode.XSTO ),
    ;

    private static Map<ExchangeCode, BbgExchangeCode> _exchangeCodeMap = new HashMap<>();

    static {
        for ( BbgExchangeCode en : BbgExchangeCode.values() ) {
            for ( final ExchangeCode exchangeCode : en.getExchangeCode() ) {
                BbgExchangeCode prev = _exchangeCodeMap.putIfAbsent( exchangeCode, en );
                if ( prev != null ) {
                    throw new IllegalStateException( "Multiple BbgExchangeCode mappings found for MIC " + exchangeCode );
                }
            }
        }
    }

    private final List<ExchangeCode> _exchangeCode;

    public static BbgExchangeCode fromExchangeCode( ExchangeCode code ) {
        return _exchangeCodeMap.get( code );
    }

    BbgExchangeCode( ExchangeCode... exchangeCode ) {
        this._exchangeCode = Arrays.asList( exchangeCode );
    }

    public final List<ExchangeCode> getExchangeCode() { return _exchangeCode; }

}
