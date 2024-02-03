/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.time;

public final class TimeTables {

    public static final int[] _hourToMS = genHourToMS();
    public static final int[] _minToMS  = genMinToMS();
    public static final int[] _secToMS  = genSecToMS();

    public static final int[] _msHundreds = genMSHundreds();
    public static final int[] _msTen      = genMsTens();

    private static int[] genHourToMS() {
        int[] hrToMs = new int[ 24 ];

        for ( int i = 0; i < 24; i++ ) {
            hrToMs[ i ] = i * 60 * 60 * 1000;
        }

        return hrToMs;
    }

    private static int[] genMinToMS() {
        int[] table = new int[ 60 ];

        for ( int i = 0; i < 60; i++ ) {
            table[ i ] = i * 60 * 1000;
        }

        return table;
    }

    private static int[] genSecToMS() {
        int[] table = new int[ 60 ];

        for ( int i = 0; i < 60; i++ ) {
            table[ i ] = i * 1000;
        }

        return table;
    }

    private static int[] genMSHundreds() {
        int[] table = new int[ 10 ];

        for ( int i = 0; i < 10; i++ ) {
            table[ i ] = i * 100;
        }

        return table;
    }

    private static int[] genMsTens() {
        int[] table = new int[ 10 ];

        for ( int i = 0; i < 10; i++ ) {
            table[ i ] = i * 10;
        }

        return table;
    }
}
