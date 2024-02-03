package com.rr.core.datarec;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.Utils;

public class JSONDataRecord {

    private static final Logger         _log = LoggerFactory.create( JSONDataRecord.class );
    private final        ReusableString _buf = new ReusableString();

    private boolean _atStartOfLevel = true;
    private int     _lvl            = 0;

    @Override public int hashCode() {
        return _buf != null ? _buf.hashCode() : 0;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final JSONDataRecord that = (JSONDataRecord) o;

        if ( _buf != null ? !_buf.equals( that._buf ) : that._buf != null ) return false;

        return true;
    }

    @Override public String toString() {
        return "JSONDataRecord{" +
               "_buf=" + _buf +
               '}';
    }

    public void add( long key, double val ) {
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( ZString key, double val ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( ZString key, long val ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( ZString key, int val ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( ZString key, double val, int dp ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, dp, true );
    }

    public void add( ZString key, String val ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( val ).append( "\"" );
    }

    public void add( ZString key, Object val ) {
        if ( isNullKey( key ) ) return;
        checkForDelim();
        if ( val != null ) {
            if ( val instanceof Double ) {
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Double) val ).append( "" );
            } else if ( val instanceof Integer ) {
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Integer) val ).append( "" );
            } else if ( val instanceof Long ) {
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Long) val ).append( "" );
            } else if ( val instanceof ZString ) {
                _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( (ZString) val ).append( "\"" );
            } else {
                _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( val.toString() ).append( "\"" );
            }
        } else {
            _buf.append( "\"" ).append( key ).append( "\" : \"\"" );
        }
    }

    public void add( String key, double val ) {
        if ( isNullKey( key ) ) return;
        if ( Utils.isNull( val ) ) return;

        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( String key, long val ) {
        if ( isNullKey( key ) ) return;
        if ( Utils.isNull( val ) ) return;

        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( String key, int val ) {
        if ( isNullKey( key ) ) return;
        if ( Utils.isNull( val ) ) return;

        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, true );
    }

    public void add( String key, double val, int dp ) {
        if ( isNullKey( key ) ) return;
        if ( Utils.isNull( val ) ) return;

        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).appendZ( val, dp, true );
    }

    public void add( String key, String val ) {
        if ( isNullKey( key ) ) return;
        if ( val == null ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( val ).append( "\"" );
    }

    public void add( String key, Object val ) {
        if ( isNullKey( key ) ) return;
        if ( val != null ) {
            if ( val instanceof Double ) {
                if ( Utils.isNull( (Double) val ) ) return;

                checkForDelim();
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Double) val ).append( "" );
            } else if ( val instanceof Integer ) {
                if ( Utils.isNull( (Integer) val ) ) return;

                checkForDelim();
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Integer) val ).append( "" );
            } else if ( val instanceof Long ) {
                if ( Utils.isNull( (Long) val ) ) return;

                checkForDelim();
                _buf.append( "\"" ).append( key ).append( "\" : " ).append( (Long) val ).append( "" );
            } else if ( val instanceof ZString ) {
                checkForDelim();
                _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( (ZString) val ).append( "\"" );
            } else {
                checkForDelim();
                _buf.append( "\"" ).append( key ).append( "\" : \"" ).append( val.toString() ).append( "\"" );
            }
        } else {
            checkForDelim();
            _buf.append( "\"" ).append( key ).append( "\" : \"\"" );
        }
    }

    public void addNestedRecord( ZString key ) {
        checkForDelim();
        if ( key == null || key.length() == 0 ) {
            _buf.append( "\"" ).append( "nested" ).append( "\" : { " );
        } else {
            _buf.append( "\"" ).append( key ).append( "\" : { " );
        }
        ++_lvl;
        _atStartOfLevel = true;
    }

    public void addNestedRecord( String key ) {
        checkForDelim();
        if ( key == null || key.length() == 0 ) {
            key = "nested";
        }
        _buf.append( "\"" ).append( key ).append( "\" : { " );
        ++_lvl;
        _atStartOfLevel = true;
    }

    public void addNestedRecord( long key ) {
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : { " );
        ++_lvl;
        _atStartOfLevel = true;
    }

    public void addRaw( String key, String val ) {
        if ( isNullKey( key ) ) return;
        if ( val == null ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).append( val );
    }

    public void addRaw( String key, ZString val ) {
        if ( isNullKey( key ) ) return;
        if ( val == null ) return;
        checkForDelim();
        _buf.append( "\"" ).append( key ).append( "\" : " ).append( val );
    }

    public void endNestedRecord() {
        _atStartOfLevel = false;
        _buf.append( " }" );
        --_lvl;
    }

    public int getLvl() { return _lvl; }

    public ZString getVals() { return _buf; }

    public void reset() {
        _atStartOfLevel = true;
        _lvl            = 0;
        _buf.reset();
    }

    private void checkForDelim() {
        if ( _atStartOfLevel ) {
            _atStartOfLevel = false;
        } else {
            _buf.append( ", " );
        }
    }

    private boolean isNullKey( final String key ) {
        if ( key == null || key.length() == 0 ) {
            logNullKey();
            return true;
        }
        return false;
    }

    private boolean isNullKey( final ZString key ) {
        if ( key == null || key.length() == 0 ) {
            logNullKey();
            return true;
        }
        return false;
    }

    private void logNullKey() {
        ReusableString errStr = TLC.strPop();
        errStr.copy( "JSONDataRecord missing key entry, dropping value from : " );
        Utils.getStackTrace( errStr );
        _log.warn( errStr );
        TLC.strPush( errStr );
    }
}
