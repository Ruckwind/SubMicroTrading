package com.rr.core.recovery.json;

import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public enum JSONFieldType {
    byteType( byte.class ),
    charType( char.class ),
    shortType( short.class ),
    intType( int.class ),
    longType( long.class ),
    floatType( float.class ),
    doubleType( double.class ),
    booleanType( boolean.class ),

    ByteType( Byte.class ),
    CharType( Character.class ),
    ShortType( Short.class ),
    IntType( Integer.class ),
    LongType( Long.class ),
    FloatType( Float.class ),
    DoubleType( Double.class ),
    BooleanType( Boolean.class ),
    StringType( String.class ),

    ZStringType( ZString.class ),
    ViewStringType( ViewString.class ),
    ReusableStringType( ReusableString.class ),

    Enum( null, false ),

    Map( null, false ),
    Collection( null, false ),
    MessageQueue( null, false ),

    Object( null, false );

    private static final Map<Class<?>, JSONFieldType> _map = new HashMap<>();

    static {
        for ( JSONFieldType en : JSONFieldType.values() ) {
            if ( en.getTypeClass() != null ) {
                _map.put( en.getTypeClass(), en );
            }
        }
    }

    private final Class<?> _class;
    private final boolean  _isPrimitive;

    public static JSONFieldType getVal( Class<?> key ) {
        JSONFieldType val = _map.get( key );
        if ( val == null ) {
            if ( key.isArray() ) {
                return getVal( key.getComponentType() );
            }
            if ( key.isEnum() ) {
                return JSONFieldType.Enum;
            }
            if ( Map.class.isAssignableFrom( key ) ) {
                return JSONFieldType.Map;
            }
            if ( EventQueue.class.isAssignableFrom( key ) ) {
                return JSONFieldType.MessageQueue;
            }
            if ( java.util.Collection.class.isAssignableFrom( key ) ) {
                return JSONFieldType.Collection;
            }
            return JSONFieldType.Object;
        }
        return val;
    }

    JSONFieldType( Class<?> c ) {
        this( c, true );
    }

    JSONFieldType( Class<?> c, boolean isPrimitive ) {
        _class       = c;
        _isPrimitive = isPrimitive;
    }

    public Class<?> getTypeClass() { return _class; }

    public boolean isPrimitive()   { return _isPrimitive; }
}
