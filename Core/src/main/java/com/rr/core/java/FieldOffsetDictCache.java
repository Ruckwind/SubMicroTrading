package com.rr.core.java;

import com.rr.core.lang.HasReusableType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldOffsetDictCache {

    private static final Map<Key, FieldOffsetDict> _cache = new ConcurrentHashMap<>();

    private static final class Key {

        private final Class<? extends HasReusableType> _superType;
        private final String                           _fieldName;

        public Key( final Class<? extends HasReusableType> superType, final String fieldName ) {
            _superType = superType;
            _fieldName = fieldName;
        }

        @Override public int hashCode() {
            int result = _superType != null ? _superType.hashCode() : 0;
            result = 31 * result + (_fieldName != null ? _fieldName.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final Key key = (Key) o;

            if ( _superType != key._superType ) return false;

            return _fieldName != null ? _fieldName.equals( key._fieldName ) : key._fieldName == null;
        }
    }

    public static synchronized FieldOffsetDict getFieldOffsetDict( Class<? extends HasReusableType> superType, String fieldName ) {
        Key k = new Key( superType, fieldName );

        FieldOffsetDict d = _cache.get( k );

        if ( d == null ) {
            d = new FieldOffsetDict( superType, fieldName );

            _cache.put( k, d );
        }

        return d;
    }
}

