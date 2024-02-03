/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;

import java.nio.ByteBuffer;
import java.util.TimeZone;

public interface LogEvent extends Event {

    @Override ReusableType getReusableType();

    /**
     * encodes the logger event to the destination buffer
     * <p>
     * LogEventLarge uses static vars for HEX encoding which MUST ONLY be invoked from same thread ... ie the AsyncLogger background thread
     */
    void encode( ByteBuffer dest );

    LoggerArgs getCustomLogArgs();

    void setCustomLogArgs( LoggerArgs customLogArgs );

    Level getLevel();

    ZString getMessage();

    int length();

    void reset();

    void set( Level lvl, byte[] bytes, int i, int length );

    void set( Level lvl, byte[] bytes, int offset, int length, byte[] other, int offsetOther, int otherLength );

    void set( Level info, ByteBuffer buf );

    void set( Level info, String msg );

    void setError( ErrorCode code, String msg );

    void setError( ErrorCode code, String msg, Throwable t );

    void setError( ErrorCode code, byte[] bytes, int offset, int length );

    void setError( ErrorCode code, byte[] bytes, int offset, int length, byte[] other, int offsetOther, int otherLength );

    void setLocalTimeZone( TimeZone localTZ );
}
