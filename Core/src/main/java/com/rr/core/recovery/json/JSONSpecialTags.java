package com.rr.core.recovery.json;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum JSONSpecialTags {

    arrayType( "@arrayOf" ),
    className( "@class" ),
    parentRef( "@this$0" ),
    containerEnum( "@enum" ), // an enum value in a container like a map
    jsonId( "@jsonId" ),
    smtId( "@smtId" ),
    smtComponentRegistered( "@smtReg" ),
    persistMode( "@pMode" ),
    ref( "@ref" ),
    entrySet( "@entrySet" ),
    jsonType( "@jsonType" ),
    overrideCustomCodec( "@overrideCustomCodec" ),
    value( "@val" ),
    key( "@key" ),
    ;

    public static final byte LEADING_CHAR = '@';
    private static final Map<ZString, JSONSpecialTags> _map = new HashMap<>();

    static {
        for ( JSONSpecialTags en : JSONSpecialTags.values() ) {
            byte[]  val  = en.getVal();
            ZString zVal = new ViewString( val );
            _map.put( zVal, en );
        }
    }

    private final byte[] _val;

    public static JSONSpecialTags getVal( ZString key ) {
        JSONSpecialTags val = _map.get( key );
        if ( val == null ) {
            throw new RuntimeDecodingException( "Unsupported value of " + key + " for JSONSpecialTags" );
        }
        return val;
    }

    JSONSpecialTags( String val ) {
        _val = val.getBytes();
    }

    public byte[] getVal() {
        return _val;
    }

}
