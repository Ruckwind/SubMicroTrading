package com.rr.core.model;

import java.util.HashMap;
import java.util.Map;

import static com.rr.core.model.FutureExchangeSymbol.*;

public enum FutureName {
    CarbonEmissionsECXEUA_ICE( IFEU_C ),
    VolatilityIndexVIX( XCBF_VX ),
    CrudeOil( XNYM_CL ),
    CrudeOilWTI( IFEU_T ),
    CrudeBrent( IFEU_B ),
    Gasoil( IFEU_G ),
    HeatingOil( XNYM_HO ),
    NaturalGas( XNYM_NG ),
    NationalBalancePoint( IFEU_NBP ),
    Palladium( XNYM_PA ),
    Platinum( XNYM_PL ),
    RBOBGasoline( XNYM_RB ),
    SoybeanOil( XCBT_ZL ),

    DEFAULTS( SYM_DEFAULTS ),
    UNKNOWN( FutureExchangeSymbol.UNKNOWN ),
    ;

    private static Map<FutureExchangeSymbol, FutureName> _codeMap = new HashMap<>();

    static {
        for ( FutureName en : FutureName.values() ) {
            _codeMap.put( en.getCode(), en );
        }
    }

    private final FutureExchangeSymbol _code;

    public static FutureName getVal( final FutureExchangeSymbol exCode ) { return getFromFutureCode( exCode ); }

    public static FutureName getFromFutureCode( FutureExchangeSymbol code ) {
        FutureName val = _codeMap.get( code );
        return (val == null) ? UNKNOWN : val;
    }

    FutureName( final FutureExchangeSymbol code ) { _code = code; }

    public FutureExchangeSymbol getCode()         { return _code; }

    public final String id()                      { return name(); }
}
