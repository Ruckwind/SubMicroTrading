/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.utils.StringUtils;

public class JavaSystemProperties {
    // @NOTE DONT USE LOGGING HERE MUST USE SYSTEM.OUT

    private static String  _socketFactoryClass;
    private static boolean _isUseLinuxNative;
    private static boolean _isUseWindowsNative;
    private static boolean _isUseTSCForNanoTime;
    private static boolean _allowOnloadCalls;

    static {
        String socketFactoryClass  = System.getProperty( "SOCKET_FACTORY_CLASS" );
        String isUseLinuxNative    = System.getProperty( "USE_NATIVE_LINUX", "false" );
        String isUseWindowsNative  = System.getProperty( "USE_NATIVE_WINDOWS", "false" );
        String isUseTSCForNanoTime = System.getProperty( "USE_TSC_FOR_NANO_TIME", "false" );
        String allowOnloadCalls    = System.getProperty( "ALLOW_ONLOAD_CALLS", "true" );

        System.out.println( "ENV : SOCKET_FACTORY_CLASS " + socketFactoryClass );
        System.out.println( "ENV : USE_NATIVE_LINUX " + isUseLinuxNative );
        System.out.println( "ENV : USE_NATIVE_WINDOWS " + isUseWindowsNative );
        System.out.println( "ENV : USE_TSC_FOR_NANO_TIME " + isUseTSCForNanoTime );
        System.out.println( "ENV : ALLOW_ONLOAD_CALLS " + allowOnloadCalls );

        _socketFactoryClass  = socketFactoryClass;
        _isUseLinuxNative    = StringUtils.parseBoolean( isUseLinuxNative );
        _isUseWindowsNative  = StringUtils.parseBoolean( isUseWindowsNative );
        _isUseTSCForNanoTime = StringUtils.parseBoolean( isUseTSCForNanoTime );
        _allowOnloadCalls    = StringUtils.parseBoolean( allowOnloadCalls );
    }

    public static String getSocketFactoryClass() {
        return _socketFactoryClass;
    }

    public static boolean isUseTSCForNanoTime() {
        return _isUseTSCForNanoTime;
    }

    public static boolean isUseLinuxNative() {
        return _isUseLinuxNative;
    }

    public static boolean isUseWindowsNative() {
        return _isUseWindowsNative;
    }

    public static boolean isAllowOnloadCalls() {
        return _allowOnloadCalls;
    }

    public static int getIntProperty( String property, int defVal ) {
        String val = System.getProperty( property, "" + defVal );
        return Integer.parseInt( val );
    }

    public static long getLongProperty( String property, long defVal ) {
        String val = System.getProperty( property, "" + defVal );
        return Long.parseLong( val );
    }
}
