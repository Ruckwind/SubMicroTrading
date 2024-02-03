/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.*;
import com.rr.core.logger.*;
import com.rr.core.os.NativeHooksImpl;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Utils {

    private static final Logger _console = ConsoleFactory.console( Utils.class, Level.WARN );
    private static       Logger _log     = ConsoleFactory.console( Utils.class, Level.info );

    private static AtomicInteger _exitCode             = new AtomicInteger( 0 );
    private static AtomicBoolean _useClockForLatestVer = new AtomicBoolean( false );

    public static void setUseClockForLatestVer( final boolean useClockForLatestVer ) {
        Utils._useClockForLatestVer.set( useClockForLatestVer );
    }

    public static void logInit() {
        _log = LoggerFactory.create( Utils.class );
    }

    public static void invokeGC() {
        _console.info( "Starting forced GC" );
        _log.info( "Starting forced GC" );
        long start = ClockFactory.get().currentTimeMillis();
        System.gc();
        long end = ClockFactory.get().currentTimeMillis();
        _console.info( "Forced GC over duration=" + (end - start) + " msecs" );
        _log.info( "Forced GC over duration=" + (end - start) + " msecs" );
    }

    public static ZString getClassName( Object o ) {
        return new ViewString( o.getClass().getSimpleName() );
    }

    public static long nanoTimeMonotonicRaw() {
        return NativeHooksImpl.instance().nanoTimeMonotonicRaw();
    }

    public static long nanoTime() {
        if ( JavaSystemProperties.isUseTSCForNanoTime() ) {
            return NativeHooksImpl.instance().nanoTimeRDTSC();
        }
        return System.nanoTime();
    }

    /**
     * @NOTE arrays not recycled used with care
     */
    public static <T> T[] arrayCopyAndAddEntry( T[] base, T extra ) {
        if ( extra == null ) return base;

        int last = base.length;

        for ( int i = 0; i < last; i++ ) {
            if ( base[ i ] == extra ) return base;
        }

        T[] tmp = Arrays.copyOf( base, last + 1 );
        tmp[ last ] = extra;

        return tmp;
    }

    public static <T> T[] arrayCopyAndAddEntry( T[] base, T extra, int newSize ) {
        if ( extra == null ) return base;

        int last = base.length;

        if ( newSize < last ) newSize = last + 1;

        for ( int i = 0; i < last; i++ ) {
            if ( base[ i ] == extra ) return base;
        }

        T[] tmp = Arrays.copyOf( base, newSize );
        tmp[ last ] = extra;

        return tmp;
    }

    public static <T> void arrayShiftAndInsertEntry( T[] arr, T extra, int idxToInsert ) {
        if ( extra == null ) return;

        if ( idxToInsert < 0 ) throw new SMTRuntimeException( "arrayShiftAndInsertEntry invalid entry of " + idxToInsert );

        int last = arr.length;

        int idx = last - 1;

        while( idx > idxToInsert ) {
            arr[ idx ] = arr[ --idx ];
        }

        arr[ idxToInsert ] = extra;
    }

    /**
     * removes all instances of item from list
     *
     * @return if item doesnt exist in array returns same array, otherwise return new list with all entries removed
     * @NOTE arrays not recycled used with care .. assumes element is in array and will create new array
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T[] arrayCopyAndRemoveEntry( T[] base, T remove ) {

        if ( remove == null ) return base;

        int newSize = base.length;

        for ( int i = 0; i < base.length; i++ ) {
            if ( base[ i ] == remove ) {
                --newSize;
            }
        }

        if ( newSize >= base.length ) return base;

        T[] copy = (T[]) Array.newInstance( base.getClass().getComponentType(), newSize );

        int newIdx = 0;

        for ( int i = 0; i < base.length; i++ ) {
            if ( base[ i ] != remove ) {
                copy[ newIdx++ ] = base[ i ];
            }
        }

        return copy;
    }

    public static <T> T[] arrayCopyAndInsertEntry( T[] base, T newEntry, int idx ) {

        int newSize = base.length + 1;

        @SuppressWarnings( "unchecked" )
        T[] copy = (T[]) Array.newInstance( base.getClass().getComponentType(), newSize );

        int newIdx = 0;

        for ( int i = 0; i < idx; i++ ) {
            copy[ newIdx++ ] = base[ i ];
        }

        copy[ newIdx++ ] = newEntry;

        for ( int i = idx; i < idx; i++ ) {
            copy[ newIdx++ ] = base[ i ];
        }

        return copy;
    }

    /**
     * removes all instances of item from list
     *
     * @return if item doesnt exist in array returns same array, otherwise return new list with all entries removed
     * @NOTE arrays not recycled used with care .. assumes element is in array and will create new array
     */
    public static synchronized <T> T[] arrayCopyAndRemoveIndexEntry( T[] base, int removeIdx ) {

        if ( removeIdx < 0 || removeIdx >= base.length ) return base;

        @SuppressWarnings( "unchecked" )
        T[] copy = (T[]) Array.newInstance( base.getClass().getComponentType(), base.length - 1 );

        int newIdx = 0;

        for ( int i = 0; i < base.length; i++ ) {
            if ( i != removeIdx ) {
                copy[ newIdx++ ] = base[ i ];
            }
        }

        return copy;
    }

    public static String getLoopbackMultiCastGroup() {
        String os = System.getProperty( "os.name" );

        return (os != null && os.toLowerCase().contains( "win" ) ? "224.000.026.001" : "224.000.000.001");
    }

    public static boolean isZero( final double val )        { return Math.abs( val ) < Constants.TICK_WEIGHT; }

    public static boolean isZero( final long val )          { return val == 0; }

    public static boolean isZero( final int val )           { return val == 0; }

    public static boolean hasVal( final double val )        { return !Double.isNaN( val ); }

    public static boolean hasVal( final float val )         { return !Float.isNaN( val ); }

    public static boolean hasVal( final int val )           { return !isNull( val ); }

    public static boolean hasVal( final long val )          { return !isNull( val ); }

    public static boolean hasVal( final short val )         { return !isNull( val ); }

    public static boolean hasVal( final char val )          { return !isNull( val ); }

    public static boolean hasVal( final byte val )          { return !isNull( val ); }

    public static boolean hasNonZeroVal( final double val ) { return !Double.isNaN( val ) && !isZero( val ); }

    public static boolean hasNonZeroVal( final long val )   { return !isNull( val ) && !isZero( val ); }

    public static boolean hasNonZeroVal( final int val )    { return !isNull( val ) && !isZero( val ); }

    public static boolean isNullOrZero( final int val )     { return isNull( val ) || isZero( val ); }

    public static boolean isNullOrZero( final long val )    { return isNull( val ) || isZero( val ); }

    public static boolean isNullOrZero( final double val )  { return Double.isNaN( val ) || isZero( val ); }

    public static boolean isNull( final double val )        { return Double.isNaN( val ); }

    public static boolean isNull( final float val )         { return Float.isNaN( val ); }

    public static boolean isNull( final byte val )          { return val == Constants.UNSET_BYTE; }

    public static boolean isNull( final char val )          { return val == Constants.UNSET_BYTE; }

    public static boolean isNull( final short val )         { return val == Constants.UNSET_SHORT; }

    public static boolean isNull( final int val )           { return val == Constants.UNSET_INT; }

    public static boolean isNull( final long val )          { return val == Constants.UNSET_LONG; }

    public static double nullToZero( final double val )     { return (isNull( val )) ? 0 : val; }

    public static float nullToZero( final float val ) {
        return isNull( val ) ? 0 : val;
    }

    public static char nullToZero( final char val ) {
        return (val == Constants.UNSET_BYTE) ? (char) 0 : val;
    }

    public static byte nullToZero( final byte val ) {
        return (val == Constants.UNSET_BYTE) ? (byte) 0 : val;
    }

    public static short nullToZero( final short val ) {
        return (val == Constants.UNSET_SHORT) ? 0 : val;
    }

    public static int nullToZero( final int val ) { return (val == Constants.UNSET_INT) ? 0 : val; }

    public static long nullToZero( final long val ) {
        return (val == Constants.UNSET_LONG) ? 0 : val;
    }

    public static void close( final AutoCloseable obj ) {
        if ( obj != null ) {
            try {
                obj.close();
            } catch( Exception e ) {
                _console.log( Level.debug, e.getMessage() );
                final ReusableString trace = TLC.strPop();
                ExceptionTrace.getStackTrace( trace, e );
                _console.log( Level.debug, trace );
            }
        }
    }

    public static int getExitCode() { return _exitCode.get(); }

    public static void exit( final int code ) {
        _exitCode.set( code );
        AppState.setState( AppState.State.Exiting );

        ReusableString rs = new ReusableString();
        Utils.getStackTrace( rs );

        _console.info( "Utils.exit() forced exit code " + code + " from " + Thread.currentThread().getName() );
        _log.info( "Utils.exit() forced exit code " + code + " from " + Thread.currentThread().getName() );
        _log.info( rs );

        System.out.println( "Utils.exit() forced exit code " + code );
        System.out.println( rs.toString() );

        if ( _log instanceof LogDelegator ) {
            LoggerFactory.flush();
            LoggerFactory.shutdown();
        }

        System.exit( code );
    }

    public static void exit( final int code, Throwable e ) {
        _exitCode.set( code );
        _console.info( "Utils.exit() forced exit code " + code + " from " + Thread.currentThread().getName() );
        _log.info( "Utils.exit() forced exit code " + code + " from " + Thread.currentThread().getName() );
        System.out.println( "Forced exit due to : " + e.getMessage() );
        e.printStackTrace();
        LoggerFactory.flush();
        LoggerFactory.shutdown();
        System.exit( code );
    }

    public static int getMaxCores() {
        double processors = Runtime.getRuntime().availableProcessors();

        int maxCores = AppProps.instance().getIntProperty( CoreProps.MAX_CORES, false, (int) processors );

        if ( maxCores <= 0 || maxCores > processors ) maxCores = (int) processors;

        return maxCores;
    }

    public static long getRequestedStartTime() {
        SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd-HH:mm:ss.SSS" );

        final String startTimestamp = AppProps.instance().getProperty( CoreProps.RUN_START_TIME, false, null );

        if ( startTimestamp == null ) return 0;

        long unixTime = 0;

        if ( startTimestamp != null ) {
            try {
                unixTime = df.parse( startTimestamp ).getTime();

            } catch( ParseException e ) {
                Utils.exit( -1, e );
            }
        }

        return unixTime;
    }

    public static TimeZone getTimeZone() {
        String tzStr = AppProps.instance().getProperty( CoreProps.APP_TIMEZONE, false, null );

        return (tzStr != null) ? TimeZone.getTimeZone( tzStr ) : TimeZone.getDefault();
    }

    public static boolean isSamePx( final double p1, final double p2 ) {
        return (Utils.isNull( p1 ) && Utils.isNull( p2 )) || (Math.abs( p1 - p2 ) < Constants.TICK_WEIGHT);
    }

    public static int compare( final double px1, final double px2 ) {
        return Double.compare( px1, px2 );
    }

    public static int compare( final double px1, final double px2, final double weight ) {

        if ( isNull( px1 ) || isNull( px2 ) ) return compare( px1, px2 );

        double diff = px1 - px2;

        if ( Math.abs( diff ) < weight ) return 0;

        return diff > 0 ? 1 : -1;
    }

    public static void getStackTrace( final ReusableString t ) {
        try {
            throw new SMTRuntimeException( "" );
        } catch( SMTRuntimeException e ) {
            StackTraceElement[] stack = e.getStackTrace();

            for ( StackTraceElement entry : stack ) {
                ExceptionTrace.addStackFrame( t, entry );
            }
        }
    }

    public static double diff( final double d1, final double d2 ) { return (d1 > d2) ? d1 - d2 : d2 - d1; }

    public static void logStackTrace( final Logger log, final ReusableString logMsg, final Exception e ) {
        getStackTrace( logMsg, e );
        log.info( logMsg );
    }

    public static void getStackTrace( final ReusableString logMsg, final Exception e ) {
        ExceptionTrace.getStackTrace( logMsg, e );
    }

    public static boolean useClockForLatest() {

        // really depends on thread, if thread is pipeline thread then should be true

        return false;

//        return _useClockForLatestVer.get();
    }
}
