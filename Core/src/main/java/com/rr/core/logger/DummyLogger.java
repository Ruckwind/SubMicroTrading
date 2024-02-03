/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZString;
import com.rr.core.utils.StringUtils;
import org.apache.log4j.BasicConfigurator;

import java.nio.ByteBuffer;
import java.util.TimeZone;

public class DummyLogger implements Logger {

    @Override public void error( ErrorCode code, ZString msg ) {
    }

    @Override public void error( ErrorCode code, ZString msg, Throwable t ) {
    }

    @Override public void error( ErrorCode code, String msg ) {
    }

    @Override public void error( ErrorCode code, String msg, Throwable t ) {
    }

    @Override public void errorHuge( ErrorCode code, String msg ) {
    }

    @Override public void errorLarge( ErrorCode code, String msg ) {
    }

    @Override public void errorLarge( ErrorCode code, ZString msg ) {
    }

    @Override public Level getLowestLevel() {
        return Level.high;
    }

    @Override public Class<?> getTagClass() { return null; }

    @Override public void info( String msg ) {
    }

    @Override public void info( ZString msg ) {
    }

    @Override public void infoHuge( ZString msg ) {
    }

    @Override public void infoHuge( byte[] buf, int offset, int len ) {
    }

    @Override public void infoHuge( ByteBuffer buf ) {
    }

    @Override public void infoLarge( ZString msg ) {
    }

    @Override public void infoLarge( byte[] buf, int offset, int len ) {
    }

    @Override public void infoLargeAsHex( ZString msg, int hexStartOffset ) {
    }

    @Override public boolean isEnabledFor( final Level level ) {
        return false;
    }

    @Override public void log( final Level level, final String msg ) {
    }

    @Override public void log( final Level level, final ZString msg ) {
    }

    @Override public void log( final Level level, final String msg, final LoggerArgs customLogArgs ) {
    }

    @Override public void log( final Level level, final ZString msg, final LoggerArgs customLogArgs ) {
    }

    @Override public void logHuge( final Level level, final ZString msg ) {
    }

    @Override public void logLarge( final Level level, final ZString msg ) {
    }

    @Override public void setLevel( final Level lvl ) {
    }

    @Override public void setTimeZone( final TimeZone timeZone ) { /* not supported */ }

    @Override public void warn( ZString msg ) {
    }

    @Override public void warn( String msg ) {
    }

    @Override public void warnHuge( String msg ) {
    }

    @Override public void warnLarge( String msg ) {
    }

    @Override public String toString() {
        return "";
    }
}
