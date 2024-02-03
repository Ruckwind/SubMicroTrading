/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.*;
import com.rr.core.model.BaseEvent;

import java.nio.ByteBuffer;
import java.util.TimeZone;

public abstract class BaseLogEvent<T> extends BaseEvent<T> implements Reusable<T>, LogEvent {

    public static final byte[] TRUNCATED = "[TRUNCATED]".getBytes();

    protected static final int MAGIC_SPARE = 8;
    private static final   int MAX_LEN     = 16384;
    protected final ReusableString _buf;
    protected       Level          _level;
    protected       long           _time;
    protected       TimeZone       _localTZ;
    private         LoggerArgs     _customLogArgs;

    public BaseLogEvent() {
        _buf = new ReusableString( getExpectedMaxEventSize() );
    }

    public BaseLogEvent( String str ) {
        _buf   = new ReusableString( str );
        _time  = ClockFactory.get().currentTimeMillis();
        _level = Level.info;
    }

    @Override public void dump( ReusableString str )                         { str.append( _buf ); }

    @Override public void encode( ByteBuffer buf ) {
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _localTZ, _time );

        int bufCapacity = (buf.capacity() - buf.position()) - MAGIC_SPARE;

        final byte[] levelHdr = _level.getLogHdr();

        final int msgLen = levelHdr.length + _buf.length();

        if ( msgLen > bufCapacity ) {
            int copyBytes = bufCapacity - TRUNCATED.length - levelHdr.length;
            buf.put( levelHdr, 0, levelHdr.length );
            buf.put( _buf.getBytes(), 0, copyBytes );
            buf.put( TRUNCATED, 0, TRUNCATED.length );
        } else {
            buf.put( levelHdr, 0, levelHdr.length );
            buf.put( _buf.getBytes(), 0, _buf.length() );
        }
    }

    @Override public LoggerArgs getCustomLogArgs()                           { return _customLogArgs; }

    @Override public void setCustomLogArgs( final LoggerArgs customLogArgs ) { _customLogArgs = customLogArgs; }

    @Override public Level getLevel() { return _level; }

    @Override public ZString getMessage() {
        return _buf;
    }

    @Override public int length()     { return _buf.length(); }

    @Override public void set( Level lvl, byte[] bytes, int offset, int length ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = lvl;
        _buf.setValue( bytes, offset, length );
    }

    @Override public void set( Level lvl, byte[] bytes, int offset, int length, byte[] other, int offsetOther, int otherLength ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = lvl;
        _buf.setValue( bytes, offset, length );
        _buf.append( ' ' ).append( other, offsetOther, otherLength );
    }

    @Override public void set( Level lvl, ByteBuffer buf ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = lvl;

        final int offset = buf.position();
        int       len    = buf.limit() - offset;

        _buf.reset();

        if ( len > MAX_LEN ) {
            len = MAX_LEN;
            buf.limit( offset + len );
            _buf.append( buf );
            _buf.append( TRUNCATED, 0, TRUNCATED.length );
        } else {
            _buf.append( buf );
        }
    }

    @Override public void set( Level lvl, String msg ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = lvl;
        _buf.setValue( msg );
    }

    @Override public void setError( ErrorCode code, String msg ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = Level.ERROR;
        _buf.copy( code.getError() ).append( ' ' ).append( msg );
    }

    @Override public void setError( ErrorCode code, String msg, Throwable t ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = Level.ERROR;
        _buf.copy( code.getError() ).append( ' ' ).append( msg );
        ExceptionTrace.getStackTrace( _buf, t );
    }

    @Override public void setError( ErrorCode code, byte[] bytes, int offset, int length ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = Level.ERROR;
        _buf.copy( code.getError() ).append( ' ' ).append( bytes, offset, length );
    }

    @Override public void setError( ErrorCode code, byte[] bytes, int offset, int length, byte[] other, int offsetOther, int otherLength ) {
        _time  = ClockFactory.get().currentTimeMillis();
        _level = Level.ERROR;
        _buf.copy( code.getError() ).append( ' ' ).append( bytes, offset, length ).append( ' ' ).append( other, offsetOther, otherLength );
    }

    @Override public void setLocalTimeZone( final TimeZone localTZ )         { _localTZ = localTZ; }

    @Override public void reset() {
        super.reset();

        _level = Level.info;
        _time  = 0;
        _buf.reset();
        _localTZ       = null;
        _customLogArgs = null;
    }

    protected abstract int getExpectedMaxEventSize();
}
