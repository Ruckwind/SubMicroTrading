/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.base.type.*;

public abstract class PrimitiveType implements AttrType {

    public static class BadSizeException extends Exception {

        private static final long serialVersionUID = 1L;

        public BadSizeException( String msg ) {
            super( msg );
        }
    }
    protected int    _arraySize = 0;
    private   String _size      = "1";
    @SuppressWarnings( "unused" )
    private   String _typeDef;

    /**
     * @param typeId
     * @return PrimitiveType to represent the type OR null if not a primitive
     * @throws BadSizeException
     */
    public static PrimitiveType get( String typeId, InternalModel internalModel ) throws BadSizeException {

        PrimitiveType type = null;

        int    arrayIdx  = typeId.indexOf( '[' );
        int    arraySize = 0;
        String sizeStr   = null;

        if ( arrayIdx != -1 ) {
            int endArray = typeId.indexOf( ']', arrayIdx );

            sizeStr = typeId.substring( arrayIdx + 1, endArray ).trim();

            arraySize = internalModel.getDefaultSize( sizeStr );

            if ( arraySize < 0 ) {
                throw new BadSizeException( "Type is an array but the size is not valid " + typeId );
            }

            typeId = typeId.substring( 0, arrayIdx ).trim();
        }

        // type : boolean char int | short | double | float | date | viewstring | reusablestring\[nnn\] | UTCTimestamp | InternalTypeId

        switch( typeId ) {
        case "string":
            type = new ReusableStringType();
            break;
        case "viewstring":
            type = new ViewStringType();
            break;
        case "boolean":
            type = new BooleanType();
            break;
        case "char":
            type = new CharType();
            break;
        case "int":
            type = new IntType();
            break;
        case "long":
            type = new LongType();
            break;
        case "short":
            type = new ShortType();
            break;
        case "double":
            type = new DoubleType();
            break;
        case "float":
            type = new FloatType();
            break;
        case "date":
            type = new DateType();
            break;
        case "UTCTimestamp":
            type = new UTCTimestampType();
            break;
        case "SendTimeUTCType":
            type = new SendTimeUTCType();
            break;
        }

        if ( type != null && arraySize > 1 ) {
            type.setArrayLength( arraySize );
            type.setSize( sizeStr );
        }

        return type;
    }

    @Override
    public String getSize() {
        return _size;
    }

    @Override
    public void setSize( String sizeStr ) {
        _size = sizeStr;
    }

    @Override
    public String getTypeDeclaration() {
        String typeStr = getTypeStr();

        if ( _arraySize > 1 ) {
            typeStr = typeStr + "[ " + _size + " ]";
        }

        return typeStr;
    }

    @Override
    public String getTypeDefinition() {
        String typeStr = getTypeStr();

        if ( _arraySize > 1 ) {
            typeStr = typeStr + "[]";
        }

        return typeStr;
    }

    @Override
    public abstract boolean isValid();

    public int getArraySize() {
        return _arraySize;
    }

    protected abstract String getTypeStr();

    protected void setArrayLength( int arraySize ) {
        _arraySize = arraySize;
    }
}
