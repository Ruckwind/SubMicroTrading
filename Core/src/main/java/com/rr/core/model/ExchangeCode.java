package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.utils.SMTRuntimeException;

import java.util.HashMap;
import java.util.Map;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

/**
 * ExchangeCode - Has One Entry Per Market Segment RIC
 * <p>
 * Operation MIC & Market Segement MIC & REC
 * <p>
 * Market Segment MIC is now default not REC
 * <p>
 * REC was used in FIX4.2 originally but now use Segment MIC
 * <p>
 * Market Identification Code ISO 10383 - Codes for Exchanges and Market Identification
 *
 * @NOTE DO NOT CHANGE THE MIC's id EVER ... its used in generated values
 */

@SuppressWarnings( { "unused", "override" } )

public enum ExchangeCode {

    // operatingMIC, isOperatingMIC, id, countryCode, desc

    // MIC's with Exchange
    XAMS( "XAMS", true, 1, "NL", BATSExch.a, "EURONEXT - EURONEXT AMSTERDAM" ),
    XBOM( "XBOM", true, 2, "IN", "BSE LTD" ),
    CHIX( "BCXE", false, 3, "GB", "CHIX - CHICAGO STOCK EXCHANGE" ),
    XCME( "XCME", true, 4, "US", "CHICAGO MERCANTILE EXCHANGE" ),
    GLBX( "XCME", false, 140, "US", "CME Globex" ),
    XEUR( "XEUR", true, 5, "DE", "EUREX DEUTSCHLAND" ),
    XLIF( "XLIF", true, 6, "GB", "Liffe" ),
    XLIS( "XLIS", false, 7, "PT", BATSExch.u, "EURONEXT - EURONEXT LISBON" ),
    XLON( "XLON", true, 8, "GB", BATSExch.l, "LONDON STOCK EXCHANGE" ),
    XPAR( "XPAR", true, 9, "FR", BATSExch.p, "EURONEXT - EURONEXT PARIS" ),
    XMAT( "XPAR", false, 10, "FR", "EURONEXT PARIS MATIF" ),

    XLDN( "XLDN", true, 100, "GB", "EURONEXT LONDON" ),
    XSMP( "XLDN", false, 101, "GB", "EURONEXT PARIS MATIF" ),

    TNLK( "XBRU", false, 102, "BE", "EURONEXT EURO MKT EXCLUDING UK" ),

    BMEX( "BMEX", true, 11, "ES", BATSExch.e, "BME - BOLSAS Y MERCADOS ESPANOLES" ),
    XMAD( "BMEX", false, 12, "ES", "Bolsa De Madrid" ),
    XDUB( "XDUB", true, 13, "IE", BATSExch.i, "Dublin" ),
    XATL( "XDUB", false, 14, "IE", "Atlantic Securities MTF" ),

    XWBO( "XWBO", true, 15, "AT", BATSExch.v, "Weiner Borse AG (Austria/Vienna)" ),

    XSWX( "XSWX", true, 16, "CH", BATSExch.z, "SIX SWISS EXCHANGE" ),
    XVTX( "XSWX", false, 17, "CH", "SIX SWISS EXCHANGE - BLUE CHIPS SEGMENT" ),

    XMIL( "XMIL", true, 18, "IT", BATSExch.m, "BORSA ITALIANA S.P.A." ),
    MTAA( "XMIL", false, 19, "IT", "Electronic Share Market Milan" ),

    XWAR( "XWAR", true, 20, "PL", BATSExch.w, "WARSAW/POLAND STOCK EXCHANGE/EQUITIES/MAIN MARKET" ),
    XPRA( "XPRA", true, 21, "CZ", BATSExch.k, "PRAGUE/CZECH REP. STOCK EXCHANGE" ),
    XBUD( "XBUD", true, 22, "HU", BATSExch.t, "HUNGARY, BUDAPEST STOCK EXCHANGE" ),

    ICEU( "ICEU", true, 134, "GB", "ICE FUTURES EUROPE - via QH" ),
    IFEU( "IFEU", true, 71, "GB", "ICE FUTURES EUROPE" ),
    IFEN( "IFEU", false, 23, "GB", "ICE FUTURES EUROPE - OIL AND REFINED PRODUCTS DIVISION" ),
    IFLL( "IFEU", false, 25, "GB", "ICE FUTURES EUROPE - FINANCIAL PRODUCTS DIVISION" ),
    IFLX( "IFEU", false, 26, "GB", "ICE FUTURES EUROPE - AGRICULTURAL PRODUCTS DIVISION" ),
    IFUS( "IFUS", true, 27, "US", "ICE FUTURES U.S." ),
    ICUS( "IFUE", false, 139, "US", "ICE FUTURES U.S." ),
    IFED( "IFUS", false, 24, "US", "ICE FUTURES U.S. ENERGY DIVISION" ),
    IFCA( "IFCA", true, 28, "CA", "ICE FUTURES CANADA" ),
    NZFX( "NZFX", true, 29, "NZ", "NEW ZEALAND FUTURES & OPTIONS" ),
    XASX( "XASX", true, 30, "AU", "ASX - ALL MARKETS" ),
    XCBF( "XCBO", false, 40, "US", "CBOE FUTURES EXCHANGE" ),
    XCBT( "XCBT", true, 41, "US", "CHICAGO BOARD OF TRADE" ),
    XOSE( "XJPX", false, 42, "JP", "OSAKA EXCHANGE" ),
    XCEC( "XNYM", false, 43, "US", "COMMODITIES EXCHANGE CENTER" ),
    XMOD( "XMOD", true, 44, "CA", "THE MONTREAL EXCHANGE / BOURSE DE MONTREAL" ),
    XNYM( "XNYM", true, 45, "US", "NEW YORK MERCANTILE EXCHANGE" ),
    XTKT( "XTKT", true, 46, "JP", "TOKYO COMMODITY EXCHANGE" ),
    XEUE( "XAMS", false, 47, "NL", "EURONEXT EQF, EQUITIES AND INDICES DERIVATIVES" ),
    XEUC( "XAMS", false, 48, "NL", "EURONEXT COM, COMMODITIES FUTURES AND OPTIONS" ),

    BCXE( "BCXE", true, 89, "GB", "CBOE EQUITIES EUROPE" ),
    BATE( "BCXE", false, 90, "GB", "CBOE EUROPE -BXE ORDER BOOKS" ),
    BATP( "BCXE", false, 135, "GB", "CBOE EUROPE -BXE Periodic Auctions" ),

    CCXE( "CCXE", true, 50, "NL", "CBOE EQUITIES EUROPE (NL)" ),
    CEUX( "CCXE", false, 51, "NL", "CBOE Europe - DXE Order Book (NL)" ),
    BEUP( "CCXE", false, 136, "NL", "CBOE Europe - DXE Periodic Auction (NL)" ),

    XEEE( "XEEE", true, 52, "DE", "European Energy Exchange" ),
    XNAS( "XNAS", true, 53, "US", "NASDAQ - All Markets" ),
    STOX( "STOX", true, 54, "CH", "Stoxx Limited" ),
    XSTX( "STOX", false, 55, "CH", "Stoxx Limited - Indices" ),
    XVIE( "XWBO", false, 56, "AT", "Wiener Boerse AG, Wertpapierboerse (Securities Exchange)" ),

    DUMX( "DUMX", true, 70, "AE", "DUBAI MERCANTILE EXCHANGE" ),
    KBCB( "KBCB", true, 72, "BE", "KBC BANK NV  GROUP MARKETS - SYSTEMATIC INTERNALISER" ),
    XBRU( "XBRU", true, 73, "BE", BATSExch.b, "EURONEXT - EURONEXT BRUSSELS" ),
    XCBO( "XCBO", true, 74, "US", "CBOE GLOBAL MARKETS INC." ),
    XCSE( "XCSE", true, 75, "DK", BATSExch.c, "NASDAQ COPENHAGEN A/S" ),
    XEMD( "XEMD", true, 76, "MX", "MERCADO MEXICANO DE DERIVADOS" ),
    XETR( "XETR", true, 77, "DE", BATSExch.d, "XETRA" ),
    XFRA( "XFRA", true, 78, "DE", "DEUTSCHE BOERSE AG" ),
    XHEL( "XHEL", true, 79, "FI", BATSExch.h, "NASDAQ HELSINKI LTD" ),
    XKBT( "XCBT", false, 80, "US", "KANSAS CITY BOARD OF TRADE" ),
    XKLS( "XKLS", true, 81, "MY", "BURSA MALAYSIA" ),
    XMGE( "XMGE", true, 82, "US", "MINNEAPOLIS GRAIN EXCHANGE" ),
    XNYE( "XNYM", false, 83, "US", "NEW YORK MERCANTILE EXCHANGE - OTC MARKETS" ),
    XNYL( "XNYM", false, 84, "US", "NEW YORK MERCANTILE EXCHANGE - ENERGY MARKETS" ),
    XNYS( "XNYS", true, 85, "US", "NEW YORK STOCK EXCHANGE" ),
    XOSL( "XOSL", true, 86, "NO", BATSExch.o, "OSLO BORS ASA" ),
    XSTO( "XSTO", true, 87, "SE", BATSExch.s, "NASDAQ STOCKHOLM AB" ),


    // ANY is only for use in HOLIDAY calendar !!
    ANY( "ANY", true, 97, "GB", "ANY" ),

    // LEGACY and TEST
    TST( "TST", true, 99, "GB", "TEST" ),

    XDJI( "XDJI", true, 110, "GB", "Future XDJI" ),
    XLS1( "XLS1", true, 111, "GB", "Future XLS1" ),
    XLSE( "XLSE", true, 112, "GB", "Future XLSE" ),
    XREU( "XREU", true, 113, "GB", "Future XREU" ),
    XMAL( "XMAL", true, 31, "MT", "Malta" ),

    ASEX( "ASEX", true, 115, "GR", "ATHENS STOCK EXCHANGE" ),
    XATH( "ASEX", false, 116, "GR", "ATHENS EXCHANGE S.A. CASH MARKET" ),
    XLJU( "XLJU", true, 117, "SI", "Slovenia LJUBLJANA STOCK EXCHANGE (OFFICIAL MARKET)" ),
    XICE( "XICE", true, 118, "IS", "NASDAQ ICELAND HF." ),
    XRIS( "XRIS", true, 119, "LV", "Latvia NASDAQ RIGA AS" ),
    XBUL( "XBUL", true, 120, "BG", "BULGARIAN STOCK EXCHANGE" ),
    XTAL( "XTAL", true, 121, "EE", "NASDAQ TALLINN AS" ),
    XLIT( "XLIT", true, 122, "LT", "Lithuania AB NASDAQ VILNIUS" ),
    XBSE( "XBSE", true, 123, "RO", "Runamia SPOT REGULATED MARKET - BVB" ),

    XBRA( "XBRA", true, 128, "SK", "Slovakia BRATISLAVA STOCK EXCHANGE" ),
    XCYS( "XCYS", true, 129, "CY", "CYPRUS STOCK EXCHANGE" ),
    XJSE( "XJSE", true, 130, "ZA", "JOHANNESBURG STOCK EXCHANGE" ),
    XLUX( "XLUX", true, 131, "LU", "LUXEMBOURG STOCK EXCHANGE" ),
    XMCE( "XMCE", true, 132, "GB", "UNKNOWN" ),

    XIST( "XIST", true, 133, "TR", BATSExch.y, "Turkey BORSA ISTANBUL" ),

    // for matching from broker
    // operatingMIC, isOperatingMIC, id, countryCode, desc
    // actually lastMkt is stringf so dont need ALL the possible values !!!
    AQXE( "AQXE", true, 141, "GB", "Aquis Exchange PLC" ),
    AQEU( "AQEU", true, 142, "FR", "Aquis Europe" ),
    BATF( "BCXE", false, 143, "GB", "CBOE Europe - BATS offbook" ),
    CEUO( "CCXE", false, 144, "NL", "Aquis Europe" ),
    MABX( "BMEX", false, 145, "ES", "BME MTF EQUITY" ),
    GROW( "BMEX", false, 146, "ES", "BME Growth Market" ),

    DUMMY( "DUMMY", true, 126, "GB", "DUMMY" ),

    UNKNOWN( "UNKNOWN", true, 127, "GB", "UNKNOWN" );

    private static IntMap<ExchangeCode>        _idMap           = new IntHashMap<>( 256, 0.75f );
    private static Map<ZString, ExchangeCode>  _micMap          = new HashMap<>();
    private static Map<BATSExch, ExchangeCode> _batsExMap       = new HashMap<>();
    private static Map<ZString, ExchangeCode>  _operatingMicMap = new HashMap<>();

    public enum BATSExch {a, b, c, d, e, h, i, k, l, m, o, p, s, t, u, v, w, x, y, z}


    static {
        for ( ExchangeCode en : ExchangeCode.values() ) {
            ZString zVal = en.getMIC();
            _micMap.put( zVal, en );

            BATSExch be = en.getBatsCode();
            if ( be != null ) {
                ExchangeCode prev = _batsExMap.put( be, en );
                if ( prev != null ) {
                    throw new SMTRuntimeException( "Duplicate BATS exchange code of " + be + " in " + prev + " and " + en );
                }
            }

            ExchangeCode prev = _idMap.put( en.getIntId(), en );

            if ( prev != null ) {
                throw new SMTRuntimeException( "Duplicate exchange code id of " + en.getIntId() + " in " + prev + " and " + en );
            }

            if ( en.getIntId() > 255 ) throw new SMTRuntimeException( "ExchangeCode of " + en.getVal() + " above max of 127 ... change code in UniqueInstIdHelper" );
        }

        for ( ExchangeCode en : ExchangeCode.values() ) {
            ZString operatingMicCode = en.getOperatingMICCode();

            ExchangeCode operatingMic = _micMap.get( operatingMicCode );

            en.setOperatingMic( operatingMic );
        }
    }

    private final int          _intId;
    private final ZString      _segmentMIC;
    private final ZString      _desc;
    private final ZString      _operatingMICCode;
    private final BATSExch     _batsCode;
    private final CountryCode  _countryCode;
    private final boolean      _isOperatingMIC;
    private       ExchangeCode _operatingMIC;

    public static boolean isCBOE( final ExchangeCode code ) {
        return code == CHIX || code == BATE || code == CEUX;
    }

    /**
     * @param code
     * @return the sub-segment or trading MIC
     */
    public static ExchangeCode getFromMktSegmentMIC( ZString code ) {
        ExchangeCode val = _micMap.get( code );
        if ( val == null ) {
            throw new RuntimeDecodingException( "Unsupported value of " + code + " for Market Segment MIC" );
        }
        return val;
    }

    public static ExchangeCode byMIC( ZString code ) {
        ExchangeCode val = _micMap.get( code );
        return val;
    }

    public static ExchangeCode getValFromBatsEx( BATSExch code ) {
        ExchangeCode val = _batsExMap.get( code );
        if ( val == null ) {
            throw new RuntimeDecodingException( "Unsupported value of " + code + " for BATS exchange" );
        }
        return val;
    }

    public static ExchangeCode getValFromBatsEx( String batsExCode ) {
        BATSExch code = null;
        try {
            code = BATSExch.valueOf( batsExCode );
        } catch( IllegalArgumentException e ) {
            throw new RuntimeDecodingException( "Unsupported value of " + batsExCode + " for BATS exchange" );
        }

        ExchangeCode val = _batsExMap.get( code );
        if ( val == null ) {
            throw new RuntimeDecodingException( "Unsupported value of " + code + " for BATS exchange" );
        }
        return val;
    }

    public static ExchangeCode getValFromId( final int exId ) { return _idMap.get( exId ); }

    public static ExchangeCode getVal( final ZString mic )    { return getFromMktSegmentMIC( mic ); }

    ExchangeCode( String operatingMIC, boolean isOperatingMIC, int id, String countryCode, String desc ) {
        this( operatingMIC, isOperatingMIC, id, countryCode, null, desc );
    }

    ExchangeCode( String operatingMIC, boolean isOperatingMIC, int id, String countryCode, BATSExch batsCode, String desc ) {
        _operatingMICCode = new ViewString( operatingMIC );
        _segmentMIC       = new ViewString( name() );
        _desc             = new ViewString( desc );
        _countryCode      = CountryCode.getVal( new ViewString( countryCode ) );
        _isOperatingMIC   = isOperatingMIC;
        _batsCode         = batsCode;
        _intId            = id;
    }

    public BATSExch getBatsCode()               { return _batsCode; }

    public CountryCode getCountryCode()         { return _countryCode; }

    public int getIntId()                       { return _intId; }

    public final ZString getMIC()               { return _segmentMIC; }

    public final ExchangeCode getOperatingMIC() { return _operatingMIC; }

    public final ZString getOperatingMICCode()  { return _operatingMICCode; }

    public final byte[] getVal()                { return getMIC().getBytes(); }

    public final String id()                    { return name(); }

    public final boolean isOperatingMICCode()   { return _isOperatingMIC; }

    private void setOperatingMic( ExchangeCode operatingMic ) {
        _operatingMIC = operatingMic;
    }
}
