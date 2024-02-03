/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZString;

import java.nio.ByteBuffer;
import java.util.TimeZone;

/**
 * picking appropriate logger method helps reduce required memory and growing of
 * logger event byte arrays
 *
 * @author Richard Rose
 */
public interface Logger {

    int DEFAULT_FLUSH_INT_MS = 5000;

    void error( ErrorCode code, ZString msg );

    void error( ErrorCode code, ZString msg, Throwable t );

    void error( ErrorCode code, String msg );

    void error( ErrorCode code, String msg, Throwable t );

    void errorHuge( ErrorCode code, String msg );

    void errorLarge( ErrorCode code, String msg );

    void errorLarge( ErrorCode code, ZString msg );

    Level getLowestLevel();

    /**
     * @return the class passed into the LoggerFactory.create method which in effect tags/scopes the Logger
     */
    Class<?> getTagClass();

    void info( String msg ); // avoid as uses tempobjs

    void info( ZString msg );

    void infoHuge( ZString msg );

    void infoHuge( byte[] buf, int offset, int len );

    void infoHuge( ByteBuffer buf );

    void infoLarge( ZString msg );

    void infoLarge( byte[] buf, int offset, int len );

    // logging binary messages typically 300 bytes, but in hex format can be 2K, this method saves an extra memcpy
    void infoLargeAsHex( ZString event, int hexStartIdx );

    boolean isEnabledFor( Level level );

    void log( Level level, String msg );

    void log( Level level, ZString msg );

    void log( Level level, String msg, LoggerArgs customLogArgs );

    /**
     * logger with custom log controls .... args are GC so only for exceptional use
     */
    void log( Level level, ZString msg, LoggerArgs customLogArgs );

    // logging messages over 512 bytes
    void logHuge( Level level, ZString msg );

    // logging messages upto 512 bytes
    void logLarge( Level level, ZString msg );

    // set explicit level from this time forward
    void setLevel( Level lvl );

    // set timezone for local logging
    void setTimeZone( TimeZone timeZone );

    void warn( ZString msg );

    void warn( String msg );

    void warnHuge( String msg );

    void warnLarge( String msg );
}
