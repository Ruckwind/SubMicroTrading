package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum CountryCode implements TwoByteLookup {
    AE( "UNITED ARAB EMIRATES" ),
    AL( "ALBANIA" ),
    AM( "ARMENIA" ),
    AO( "ANGOLA" ),
    AR( "ARGENTINA" ),
    AT( "AUSTRIA" ),
    AU( "AUSTRALIA" ),
    AZ( "AZERBAIJAN" ),
    BA( "BOSNIA AND HERZEGOVINA" ),
    BB( "BARBADOS" ),
    BD( "BANGLADESH" ),
    BE( "BELGIUM" ),
    BG( "BULGARIA" ),
    BH( "BAHRAIN" ),
    BM( "BERMUDA" ),
    BO( "BOLIVIA" ),
    BR( "BRAZIL" ),
    BS( "BAHAMAS" ),
    BW( "BOTSWANA" ),
    BY( "BELARUS" ),
    CA( "CANADA" ),
    CH( "SWITZERLAND" ),
    CI( "IVORY COAST" ),
    CL( "CHILE" ),
    CM( "CAMEROON" ),
    CN( "CHINA" ),
    CO( "COLOMBIA" ),
    CR( "COSTA RICA" ),
    CV( "CAPE VERDE" ),
    CW( "CURACAO" ),
    CY( "CYPRUS" ),
    CZ( "CZECH REPUBLIC" ),
    DE( "GERMANY" ),
    DK( "DENMARK" ),
    DO( "DOMINICAN REPUBLIC" ),
    DZ( "ALGERIA" ),
    EC( "ECUADOR" ),
    EE( "ESTONIA" ),
    EG( "EGYPT" ),
    ES( "SPAIN" ),
    FI( "FINLAND" ),
    FJ( "FIJI" ),
    FO( "FAROE ISLANDS" ),
    FR( "FRANCE" ),
    GB( "UNITED KINGDOM" ),
    GE( "GEORGIA" ),
    GH( "GHANA" ),
    GI( "GIBRALTAR" ),
    GR( "GREECE" ),
    GT( "GUATEMALA" ),
    GY( "GUYANA" ),
    HK( "HONG KONG" ),
    HN( "HONDURAS" ),
    HR( "CROATIA" ),
    HU( "HUNGARY" ),
    ID( "INDONESIA" ),
    IE( "IRELAND" ),
    IL( "ISRAEL" ),
    IN( "INDIA" ),
    IQ( "IRAQ" ),
    IR( "IRAN" ),
    IS( "ICELAND" ),
    IT( "ITALY" ),
    JM( "JAMAICA" ),
    JO( "JORDAN" ),
    JP( "JAPAN" ),
    KE( "KENYA" ),
    KG( "KYRGYZSTAN" ),
    KH( "CAMBODIA" ),
    KN( "SAINT KITTS AND NEVIS" ),
    KR( "REPUBLIC OF KOREA" ),
    KW( "KUWAIT" ),
    KY( "CAYMAN ISLANDS" ),
    KZ( "KAZAKHSTAN" ),
    LA( "LAOS" ),
    LB( "LEBANON" ),
    LI( "LIECHTENSTEIN" ),
    LK( "SRI LANKA" ),
    LT( "LITHUANIA" ),
    LU( "LUXEMBOURG" ),
    LV( "LATVIA" ),
    LY( "LIBYAN ARAB JAMAHIRIYA" ),
    MA( "MOROCCO" ),
    MG( "MADAGASCAR" ),
    MK( "MACEDONIA" ),
    MN( "MONGOLIA" ),
    MT( "MALTA" ),
    MU( "MAURITIUS" ),
    MV( "MALDIVES" ),
    MW( "MALAWI" ),
    MX( "MEXICO" ),
    MY( "MALAYSIA" ),
    MZ( "MOZAMBIQUE" ),
    NA( "NAMIBIA" ),
    NG( "NIGERIA" ),
    NI( "NICARAGUA" ),
    NL( "THE NETHERLANDS" ),
    NO( "NORWAY" ),
    NP( "NEPAL" ),
    NZ( "NEW ZEALAND" ),
    OM( "OMAN" ),
    PA( "PANAMA" ),
    PE( "PERU" ),
    PG( "PAPUA NEW GUINEA" ),
    PH( "PHILIPPINES" ),
    PK( "PAKISTAN" ),
    PL( "POLAND" ),
    PT( "PORTUGAL" ),
    PY( "PARAGUAY" ),
    QA( "QATAR" ),
    RO( "ROMANIA" ),
    RU( "RUSSIA" ),
    RW( "RWANDA" ),
    SA( "SAUDI ARABIA" ),
    SC( "REPUBLIC OF SEYCHELLES" ),
    SD( "SUDAN" ),
    SE( "SWEDEN" ),
    SG( "SINGAPORE" ),
    SI( "SLOVENIA" ),
    SK( "SLOVAKIA" ),
    SV( "EL SALVADOR" ),
    SY( "SYRIAN ARAB REPUBLIC" ),
    SZ( "SWAZILAND" ),
    TH( "THAILAND" ),
    TN( "TUNISIA" ),
    TR( "TURKEY" ),
    TT( "TRINIDAD AND TOBAGO" ),
    TW( "TAIWAN" ),
    TZ( "TANZANIA" ),
    UA( "UKRAINE" ),
    UG( "UGANDA" ),
    US( "UNITED STATES OF AMERICA" ),
    UY( "URUGUAY" ),
    UZ( "UZBEKISTAN" ),
    VE( "VENEZUELA" ),
    VN( "VIET NAM" ),
    VU( "VANUATU" ),
    ZA( "SOUTH AFRICA" ),
    ZM( "ZAMBIA" ),
    ZW( "ZIMBABWE" ),
    ZZ( "ZZ" ),
    ;

    private static CountryCode[] _entries = new CountryCode[ 256 * 256 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) { _entries[ i ] = ZZ; }

        for ( CountryCode en : CountryCode.values() ) {
            if ( en == ZZ ) continue;
            byte[] val = en.getVal();
            int    key = val[ 0 ] << 8;
            if ( val.length == 2 ) key += val[ 1 ];
            _entries[ key ] = en;
        }
    }

    private final ZString _desc;
    private final byte[]  _code;
    private       Country _country;

    public static CountryCode getVal( byte[] val, int offset, int len ) {
        int         key = getKey( val, offset, len );
        CountryCode eval;
        eval = _entries[ key ];
        if ( eval == ZZ ) throw new RuntimeDecodingException( "Unsupported value of " + ((key > 0xFF) ? ("" + (char) (key >> 8) + (char) (key & 0xFF)) : ("" + (char) (key & 0xFF))) + " for CountryCode" );
        return eval;
    }

    private static int getKey( final byte[] val, int offset, final int len ) {
        int key = val[ offset++ ] << 8;
        if ( len == 2 ) key += val[ offset ];
        return key;
    }

    public static CountryCode getVal( final ZString val ) {
        return getVal( val.getBytes(), val.getOffset(), val.length() );
    }

    CountryCode( String desc ) {
        _desc = new ViewString( desc );
        _code = name().getBytes();
    }

    @Override public int getID()                    { return ordinal(); }

    @Override public byte[] getVal()                { return _code; }

    public Country getCountry()                     { return _country; }

    public void setCountry( final Country country ) { _country = country; }

    public ZString getDesc()                        { return _desc; }

    public String id()                              { return name(); }
}
