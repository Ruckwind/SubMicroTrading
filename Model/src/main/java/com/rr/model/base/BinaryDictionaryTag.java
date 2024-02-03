/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class BinaryDictionaryTag {

    private final String     _id;
    private final int        _code;
    private final String     _comment;
    private final BinaryType _binaryType;

    /**
     * _fixedLen if > 0 then specifies fixed width
     */
    private final int _fixedLen;

    private int _decimalPlaces = 0;

    public BinaryDictionaryTag( String id, String comment, BinaryType fixType, int code ) {
        this( id, comment, fixType, code, 0 );
    }

    /**
     * @param id
     * @param fixType
     * @param fixedSize only required for BinaryType fstr (fixed width string) | zstr (fixed width terminated string)
     */
    public BinaryDictionaryTag( String id, String comment, BinaryType fixType, int code, int fixedSize ) {
        _id         = id;
        _binaryType = fixType;
        _fixedLen   = fixedSize;
        _comment    = comment;
        _code       = code;
    }

    public BinaryDictionaryTag( BinaryDictionaryTag value ) {
        _id            = value._id;
        _binaryType    = value._binaryType;
        _fixedLen      = value._fixedLen;
        _comment       = value._comment;
        _code          = value._code;
        _decimalPlaces = value._decimalPlaces;
    }

    public BinaryType getBinaryType() {
        return _binaryType;
    }

    public int getCode() {
        return _code;
    }

    public String getComment() {
        return _comment;
    }

    public int getDecimalPlaces()                       { return _decimalPlaces; }

    public void setDecimalPlaces( final int decPlaces ) { _decimalPlaces = decPlaces; }

    public int getFixedLen() {
        return _fixedLen;
    }

    public String getId() {
        return _id;
    }
}
