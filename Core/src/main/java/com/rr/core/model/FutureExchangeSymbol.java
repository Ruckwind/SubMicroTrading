package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;

import java.util.*;

public enum FutureExchangeSymbol {

    IFEU_B( "B", "IFEU,ICEU", "QO", "QOA", "QO1C", "B1S", "Crude-Brent (Combined)", "CO" ),

    IFEU_C( "C", "IFEU,ICEU", "UX", "UX", "UX1C", "C1S", "Carbon Emissions ECX EUA - ICE", "MZB" ),
    IFEU_G( "G", "IFEU,ICEU", "QP", "QPA", "QP1C", "G1S", "Low Sulphur Gasoil (Combined)", "QS" ),
    IFEU_NBP( "NBP", "IFEU,ICEU", "QU", "QU", "QU1C", "NBP1S", "Natural Gas (National Balancing Point)", "FN" ),
    IFEU_T( "T", "IFEU,ICEU", "ET", "ET", "ET1C", "T1S", "Crude Oil-WTI", "EN" ),

    XCBF_VX( "VX", "XCBF", "VX", "VX", "VX1C", "VX1S", "CBOE Volatility Index VIX", "UX" ),
    XCBT_ZL( "ZL", "XCBT", "BO", "BOA", "BO1C", "ZL1S", "Soybean Oil (Combined)", "BO" ),
    XCME_XAE( "XAE", "XCME", "XAE", "XAE", "XAE1C", "XAE1S", "E-MINI Energy" ),

    XNYM_CL( "CL", "XNYM", "CL", "CLA", "CL1C", "CL1S", "Crude Oil (Combined)", "CL" ),
    XNYM_HO( "HO", "XNYM", "HO", "HOA", "HO1C", "HO1S", "Heating Oil (Combined)", "HO" ),
    XNYM_NG( "NG", "XNYM", "NG", "NGA", "NG1C", "NG1S", "Natural Gas (Combined)", "NG" ),
    XNYM_PA( "PA", "XNYM", "PA", "PAA", "PA1C", "PD1S", "Palladium (Combined)", "PA" ),
    XNYM_PL( "PL", "XNYM", "PL", "PLA", "PL1C", "PL1S", "Platinum (Nymex) Combined", "PL" ),
    XNYM_RB( "RB", "XNYM", "RB", "RBA", "RB1C", "RB1S", "RBOB Gasoline (Combined)", "XB" ),

    SYM_DEFAULTS( "", "", "DEFAULTS", "DEFAULTS", "DEFAULTS", "DEFAULTS", "DEFAULTS" ),
    UNKNOWN( "", "", "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN" ),
    ;

    private static Map<ZString, FutureExchangeSymbol> _smtSymMap      = new HashMap<>();
    private static Map<ZString, FutureExchangeSymbol> _physicalSymMap = new HashMap<>();
    private static Map<ZString, FutureExchangeSymbol> _portaraSymMap  = new HashMap<>();
    private static Map<ZString, FutureExchangeSymbol> _cqgSymMap      = new HashMap<>();
    private static Map<ZString, FutureExchangeSymbol> _bbRootMap      = new HashMap<>(); // map of bbRoot.MIC to FutSym

    static {
        for ( FutureExchangeSymbol en : FutureExchangeSymbol.values() ) {
            _smtSymMap.put( en.getSmtSym(), en );

            final Set<ExchangeCode> mics = en.getValidMICs();

            for ( ExchangeCode mic : mics ) {

                final ReusableString key = new ReusableString();
                makeMapKeyWithExchange( key, en.getPhysicalSym(), mic );

                FutureExchangeSymbol existing = _physicalSymMap.putIfAbsent( key, en );

                if ( existing != null && existing != en ) {
                    throw new SMTRuntimeException( "FutureExchangeSymbol clash with physMapKey=" + key + ", exchange="
                                                   + mic.name() + " already has "
                                                   + existing.id() + " with "
                                                   + key.toString() + " so cant add "
                                                   + en.id() );
                }

                makeMapKeyWithExchange( key, en.getPhysicalSym(), mic );

                existing = _bbRootMap.putIfAbsent( key, en );

                if ( existing != null && existing != en ) {
                    throw new SMTRuntimeException( "FutureExchangeSymbol clash with bbRootKey=" + key + ", exchange="
                                                   + mic.name() + " already has "
                                                   + existing.id() + " with "
                                                   + key.toString() + " so cant add "
                                                   + en.id() );
                }
            }

            _portaraSymMap.put( en.getPortaraHybrid(), en );

            final FutureExchangeSymbol prev = _cqgSymMap.put( en.getCqgSymbol(), en );

            if ( prev != null && prev != en ) {
                throw new SMTRuntimeException( "FutureExchangeSymbol clash with cqgSym=" + en.getCqgSymbol() + ", prev="
                                               + prev.toString() + " new=" + en.toString() );
            }
        }
    }

    private final ZString                     _zid;
    private final ZString                     _portaraHybrid;
    private final ZString                     _cqgSymbol;
    private final ZString                     _portaraContContract;
    private final ZString                     _gfutCode;
    private final ZString                     _marketName;
    private final ZString                     _smtSym;
    private final ZString                     _physicalSym;
    private final ZString                     _bbRootSym;
    private final LinkedHashSet<ExchangeCode> _validMICs = new LinkedHashSet<>();

    public static FutureExchangeSymbol getVal( final ZString exCode ) { return getFromSMTSymbol( exCode ); }

    public static FutureExchangeSymbol getFromSMTSymbol( ZString code ) {
        FutureExchangeSymbol val = _smtSymMap.get( code );
        return (val == null) ? UNKNOWN : val;
    }

    public static FutureExchangeSymbol getFromPhysicalSymbol( ZString code, final ExchangeCode securityExchange ) {
        final ReusableString tmpPhysKey = TLC.strPop();
        makeMapKeyWithExchange( tmpPhysKey, code, securityExchange );
        FutureExchangeSymbol val = _physicalSymMap.get( tmpPhysKey );
        TLC.strPush( tmpPhysKey );
        return (val == null) ? UNKNOWN : val;
    }

    public static FutureExchangeSymbol getFromBloombergRoot( ZString bbRoot, final ExchangeCode securityExchange ) {
        final ReusableString tmpKey = TLC.strPop();
        makeMapKeyWithExchange( tmpKey, bbRoot, securityExchange );
        FutureExchangeSymbol val = _bbRootMap.get( tmpKey );
        TLC.strPush( tmpKey );
        return (val == null) ? UNKNOWN : val;
    }

    public static FutureExchangeSymbol getFromBloombergTicker( ZString bbTicker, final ExchangeCode securityExchange ) {
        final ReusableString tmpPhysKey = TLC.strPop();

        tmpPhysKey.copy( bbTicker, 0, bbTicker.length() - 2 ).append( "." ).append( securityExchange.getMIC() );

        FutureExchangeSymbol val = _bbRootMap.get( tmpPhysKey );
        TLC.strPush( tmpPhysKey );
        return (val == null) ? UNKNOWN : val;
    }

    private static void makeMapKeyWithExchange( final ReusableString key, final ZString physicalSym, final ExchangeCode securityExchange ) {
        key.copy( physicalSym ).append( "." ).append( securityExchange.getMIC() );
    }

    FutureExchangeSymbol( String physicalSym, String micList, String portaraHybrid, String cqgSymbol, String portaraContContract, String gfutCode, String marketName ) {
        this( physicalSym, micList, portaraHybrid, cqgSymbol, portaraContContract, gfutCode, marketName, null );
    }

    FutureExchangeSymbol( String physicalSym, String micList, String portaraHybrid, String cqgSymbol, String portaraContContract, String gfutCode, String marketName, String bbRootSym ) {
        int idx = name().indexOf( '_' );

        _zid = new ViewString( name() );

        _smtSym = new ViewString( (idx > 0) ? name().substring( idx + 1 ) : name() ); // skip MIC_

        _physicalSym         = new ViewString( physicalSym );
        _portaraHybrid       = new ViewString( portaraHybrid );
        _cqgSymbol           = new ViewString( cqgSymbol );
        _portaraContContract = new ViewString( portaraContContract );
        _gfutCode            = new ViewString( gfutCode );
        _marketName          = new ViewString( marketName );
        _bbRootSym           = (bbRootSym != null) ? new ViewString( bbRootSym ) : _physicalSym;

        ArrayList<String> mics = new ArrayList<>();

        StringUtils.split( micList, ',', mics );

        for ( String mic : mics ) {
            ExchangeCode e = ExchangeCode.valueOf( mic );

            if ( e == null ) throw new SMTRuntimeException( "Unknown exchange code of " + mic );

            _validMICs.add( e );
        }
    }

    public ZString getBbRootSym()                     { return _bbRootSym; }

    public ZString getCqgSymbol()                     { return _cqgSymbol; }

    public ZString getDesc()                          { return _marketName; }

    public ZString getGfutCode()                      { return _gfutCode; }

    public ZString getPhysicalSym()                   { return _physicalSym; }

    public ZString getPortaraContContract()           { return _portaraContContract; }

    public ZString getPortaraHybrid()                 { return _portaraHybrid; }

    public ZString getSmtSym()                        { return _smtSym; }

    public final byte[] getVal()                      { return _physicalSym.getBytes(); }

    public LinkedHashSet<ExchangeCode> getValidMICs() { return _validMICs; }

    public String id()                                { return name(); }

    public boolean isValidMIC( ExchangeCode mic )     { return _validMICs.size() == 0 || _validMICs.contains( mic ); }

    public ZString zid()                              { return _zid; }
}
