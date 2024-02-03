package com.rr.core.recovery;

import com.rr.core.component.SMTComponent;

import java.math.BigDecimal;

public class DummyPrimitivePersistableItemA implements SMTComponent {

    private String     _id;
    private boolean    _myBool;
    private int        _myInt;
    private double     _myDouble;
    private float      _myFloat;
    private BigDecimal _myBigDecimal;
    private short      _myShort;
    private long       _myLong;
    private String     _myString;

    public DummyPrimitivePersistableItemA( String smtId ) { _id = smtId; }

    public DummyPrimitivePersistableItemA( final String id, final boolean myBool, final int myInt, final double myDouble, final float myFloat,
                                           final BigDecimal myBigDecimal, final short myShort, final long myLong, final String myString ) {
        _id           = id;
        _myBool       = myBool;
        _myInt        = myInt;
        _myDouble     = myDouble;
        _myFloat      = myFloat;
        _myBigDecimal = myBigDecimal;
        _myShort      = myShort;
        _myLong       = myLong;
        _myString     = myString;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public int hashCode() {
        int  result;
        long temp;
        result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_myBool ? 1 : 0);
        result = 31 * result + _myInt;
        temp   = Double.doubleToLongBits( _myDouble );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (_myFloat != +0.0f ? Float.floatToIntBits( _myFloat ) : 0);
        result = 31 * result + (_myBigDecimal != null ? _myBigDecimal.hashCode() : 0);
        result = 31 * result + (int) _myShort;
        result = 31 * result + (int) (_myLong ^ (_myLong >>> 32));
        result = 31 * result + (_myString != null ? _myString.hashCode() : 0);
        return result;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final DummyPrimitivePersistableItemA that = (DummyPrimitivePersistableItemA) o;

        if ( _myBool != that._myBool ) return false;
        if ( _myInt != that._myInt ) return false;
        if ( Double.compare( that._myDouble, _myDouble ) != 0 ) return false;
        if ( Float.compare( that._myFloat, _myFloat ) != 0 ) return false;
        if ( _myShort != that._myShort ) return false;
        if ( _myLong != that._myLong ) return false;
        if ( _id != null ? !_id.equals( that._id ) : that._id != null ) return false;
        if ( _myBigDecimal != null ? !_myBigDecimal.equals( that._myBigDecimal ) : that._myBigDecimal != null ) return false;
        return _myString != null ? _myString.equals( that._myString ) : that._myString == null;
    }
}
