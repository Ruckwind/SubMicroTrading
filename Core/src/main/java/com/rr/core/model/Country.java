package com.rr.core.model;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum Country {
    UNITED_ARAB_EMIRATES( CountryCode.AE ),
    ALBANIA( CountryCode.AL ),
    ARMENIA( CountryCode.AM ),
    ANGOLA( CountryCode.AO ),
    ARGENTINA( CountryCode.AR ),
    AUSTRIA( CountryCode.AT ),
    AUSTRALIA( CountryCode.AU ),
    AZERBAIJAN( CountryCode.AZ ),
    BOSNIA_AND_HERZEGOVINA( CountryCode.BA ),
    BARBADOS( CountryCode.BB ),
    BANGLADESH( CountryCode.BD ),
    BELGIUM( CountryCode.BE ),
    BULGARIA( CountryCode.BG ),
    BAHRAIN( CountryCode.BH ),
    BERMUDA( CountryCode.BM ),
    BOLIVIA( CountryCode.BO ),
    BRAZIL( CountryCode.BR ),
    BAHAMAS( CountryCode.BS ),
    BOTSWANA( CountryCode.BW ),
    BELARUS( CountryCode.BY ),
    CANADA( CountryCode.CA ),
    SWITZERLAND( CountryCode.CH ),
    IVORY_COAST( CountryCode.CI ),
    CHILE( CountryCode.CL ),
    CAMEROON( CountryCode.CM ),
    CHINA( CountryCode.CN ),
    COLOMBIA( CountryCode.CO ),
    COSTA_RICA( CountryCode.CR ),
    CAPE_VERDE( CountryCode.CV ),
    CURACAO( CountryCode.CW ),
    CYPRUS( CountryCode.CY ),
    CZECH_REPUBLIC( CountryCode.CZ ),
    GERMANY( CountryCode.DE ),
    DENMARK( CountryCode.DK ),
    DOMINICAN_REPUBLIC( CountryCode.DO ),
    ALGERIA( CountryCode.DZ ),
    ECUADOR( CountryCode.EC ),
    ESTONIA( CountryCode.EE ),
    EGYPT( CountryCode.EG ),
    SPAIN( CountryCode.ES ),
    FINLAND( CountryCode.FI ),
    FIJI( CountryCode.FJ ),
    FAROE_ISLANDS( CountryCode.FO ),
    FRANCE( CountryCode.FR ),
    UK( CountryCode.GB ),
    GEORGIA( CountryCode.GE ),
    GHANA( CountryCode.GH ),
    GIBRALTAR( CountryCode.GI ),
    GREECE( CountryCode.GR ),
    GUATEMALA( CountryCode.GT ),
    GUYANA( CountryCode.GY ),
    HONG_KONG( CountryCode.HK ),
    HONDURAS( CountryCode.HN ),
    CROATIA( CountryCode.HR ),
    HUNGARY( CountryCode.HU ),
    INDONESIA( CountryCode.ID ),
    IRELAND( CountryCode.IE ),
    ISRAEL( CountryCode.IL ),
    INDIA( CountryCode.IN ),
    IRAQ( CountryCode.IQ ),
    IRAN( CountryCode.IR ),
    ICELAND( CountryCode.IS ),
    ITALY( CountryCode.IT ),
    JAMAICA( CountryCode.JM ),
    JORDAN( CountryCode.JO ),
    JAPAN( CountryCode.JP ),
    KENYA( CountryCode.KE ),
    KYRGYZSTAN( CountryCode.KG ),
    CAMBODIA( CountryCode.KH ),
    SAINT_KITTS_AND_NEVIS( CountryCode.KN ),
    REPUBLIC_OF_KOREA( CountryCode.KR ),
    KUWAIT( CountryCode.KW ),
    CAYMAN_ISLANDS( CountryCode.KY ),
    KAZAKHSTAN( CountryCode.KZ ),
    LAOS( CountryCode.LA ),
    LEBANON( CountryCode.LB ),
    LIECHTENSTEIN( CountryCode.LI ),
    SRI_LANKA( CountryCode.LK ),
    LITHUANIA( CountryCode.LT ),
    LUXEMBOURG( CountryCode.LU ),
    LATVIA( CountryCode.LV ),
    LIBYAN_ARAB_JAMAHIRIYA( CountryCode.LY ),
    MOROCCO( CountryCode.MA ),
    MADAGASCAR( CountryCode.MG ),
    MACEDONIA( CountryCode.MK ),
    MONGOLIA( CountryCode.MN ),
    MALTA( CountryCode.MT ),
    MAURITIUS( CountryCode.MU ),
    MALDIVES( CountryCode.MV ),
    MALAWI( CountryCode.MW ),
    MEXICO( CountryCode.MX ),
    MALAYSIA( CountryCode.MY ),
    MOZAMBIQUE( CountryCode.MZ ),
    NAMIBIA( CountryCode.NA ),
    NIGERIA( CountryCode.NG ),
    NICARAGUA( CountryCode.NI ),
    THE_NETHERLANDS( CountryCode.NL ),
    NORWAY( CountryCode.NO ),
    NEPAL( CountryCode.NP ),
    NEW_ZEALAND( CountryCode.NZ ),
    OMAN( CountryCode.OM ),
    PANAMA( CountryCode.PA ),
    PERU( CountryCode.PE ),
    PAPUA_NEW_GUINEA( CountryCode.PG ),
    PHILIPPINES( CountryCode.PH ),
    PAKISTAN( CountryCode.PK ),
    POLAND( CountryCode.PL ),
    PORTUGAL( CountryCode.PT ),
    PARAGUAY( CountryCode.PY ),
    QATAR( CountryCode.QA ),
    ROMANIA( CountryCode.RO ),
    RUSSIA( CountryCode.RU ),
    RWANDA( CountryCode.RW ),
    SAUDI_ARABIA( CountryCode.SA ),
    REPUBLIC_OF_SEYCHELLES( CountryCode.SC ),
    SUDAN( CountryCode.SD ),
    SWEDEN( CountryCode.SE ),
    SINGAPORE( CountryCode.SG ),
    SLOVENIA( CountryCode.SI ),
    SLOVAKIA( CountryCode.SK ),
    EL_SALVADOR( CountryCode.SV ),
    SYRIAN_ARAB_REPUBLIC( CountryCode.SY ),
    SWAZILAND( CountryCode.SZ ),
    THAILAND( CountryCode.TH ),
    TUNISIA( CountryCode.TN ),
    TURKEY( CountryCode.TR ),
    TRINIDAD_AND_TOBAGO( CountryCode.TT ),
    TAIWAN( CountryCode.TW ),
    TANZANIA( CountryCode.TZ ),
    UKRAINE( CountryCode.UA ),
    UGANDA( CountryCode.UG ),
    US( CountryCode.US ),
    URUGUAY( CountryCode.UY ),
    UZBEKISTAN( CountryCode.UZ ),
    VENEZUELA( CountryCode.VE ),
    VIET_NAM( CountryCode.VN ),
    VANUATU( CountryCode.VU ),
    SOUTH_AFRICA( CountryCode.ZA ),
    ZAMBIA( CountryCode.ZM ),
    ZIMBABWE( CountryCode.ZW ),
    ZZ( CountryCode.ZZ ),
    ;

    private static Map<ZString, Country> _map = new HashMap<>();

    static {
        for ( Country en : Country.values() ) {
            ZString zVal = new ViewString( en.toString() );
            _map.put( zVal, en );
        }
    }

    private final CountryCode _code;

    public static Country getVal( final ZString val ) {
        return _map.get( val );
    }

    Country( CountryCode code ) {
        _code = code;
        code.setCountry( this );
    }

    public CountryCode getCode() { return _code; }

    public ZString getDesc()     { return _code.getDesc(); }
}
