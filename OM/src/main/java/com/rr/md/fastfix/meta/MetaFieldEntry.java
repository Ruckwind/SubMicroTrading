/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

import com.rr.core.codec.binary.fastfix.common.FieldDataType;
import com.rr.core.codec.binary.fastfix.common.FieldOperator;

public class MetaFieldEntry extends MetaBaseEntry {

    private FieldOperator _operator  = FieldOperator.NOOP;
    private String        _initValue = null;

    public MetaFieldEntry( String name, int id, boolean optional, FieldDataType t ) {
        super( name, id, optional, t );

        setInitValue( null );
    }

    @Override
    public String toString() {
        return super.toString() + ", op=" + getOperator();
    }

    @Override
    public boolean requiresPresenceBit() {
        boolean requiresPresentBit = true;

        if ( isOptional() ) {
            switch( _operator ) {
            case NOOP:
            case DELTA:
                requiresPresentBit = false;
                break;
            case CONSTANT:
            case COPY:
            case DEFAULT:
            case INCREMENT:
                break;
            default:
                break;
            }
        } else {
            switch( _operator ) {
            case NOOP:
            case CONSTANT:
            case DELTA:
                requiresPresentBit = false;
                break;
            case COPY:
            case DEFAULT:
            case INCREMENT:
                break;
            default:
                break;
            }
        }

        return requiresPresentBit;
    }

    public String getInitValue() {
        return _initValue;
    }

    public void setInitValue( String initValue ) {
        _initValue = initValue;

        if ( initValue == null ) {
            if ( _operator == FieldOperator.DELTA ) { // Override null with zero for numeric delta fields
                switch( getType() ) {
                case int32:
                case int64:
                case uInt32:
                case uInt64:
                case length:
                    _initValue = "0";
                    break;
                case decimal:
                    break;
                case string:
                    break;
                case byteVector:
                    break;
                case sequence:
                case group:
                case template:
                default:
                    break;
                }
            }
        }

        if ( _operator == FieldOperator.INCREMENT ) { // Override null with zero for numeric delta fields
            switch( getType() ) {
            case int32:
            case int64:
            case uInt32:
            case uInt64:
            case length:
                if ( _initValue != null ) {
                    long overrideInitVal = Long.parseLong( initValue ) - 1;

                    _initValue = "" + overrideInitVal;
                }
                break;
            default:
                break;
            }
        }
    }

    public FieldOperator getOperator() {
        return _operator;
    }

    public void setOperator( FieldOperator operator ) {
        _operator = operator;
    }
}

