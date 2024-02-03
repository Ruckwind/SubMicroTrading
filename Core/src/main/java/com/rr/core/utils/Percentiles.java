/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.ReusableString;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Percentiles {

    private TimeUnit _units;

    private long[] _sortedValues;
    private double _median;
    private double _average;
    private double _size;

    public Percentiles( long[] values ) {
        this( values, values.length );
    }

    public Percentiles( final long[] values, final TimeUnit units ) {
        this( values, values.length );
        _units = units;
    }

    public Percentiles( long[] values, int size ) {
        _sortedValues = new long[ size ];
        _size         = size;

        recalc( values, size );
    }

    public double calc( final double percentile ) {

        if ( (percentile <= 0) || (percentile > 100) ) {
            throw new IllegalArgumentException( "Invalid percentile " + percentile );
        }

        int numEntries = _sortedValues.length;

        if ( numEntries == 0 ) return 0;
        if ( numEntries == 1 ) return _sortedValues[ 0 ];

        double percentilePosition = percentile * (numEntries + 1) / 100;
        int    intPos             = (int) Math.floor( percentilePosition );

        if ( intPos >= numEntries ) return _sortedValues[ _sortedValues.length - 1 ];

        double fractionFactor = percentilePosition - intPos;

        long lower = _sortedValues[ intPos - 1 ];
        long upper = _sortedValues[ intPos ];

        return (int) (lower + fractionFactor * (upper - lower));
    }

    public double getAverage() {
        return _average;
    }

    public double getEntry( int idx ) {
        if ( _sortedValues.length == 0 ) return 0;
        return _sortedValues[ idx ];
    }

    public double getMaximum() {
        if ( _sortedValues.length == 0 ) return 0;
        return _sortedValues[ _sortedValues.length - 1 ];
    }

    public double getMinimum() {
        if ( _sortedValues.length == 0 ) return 0;
        return _sortedValues[ 0 ];
    }

    public void logStats( ReusableString out ) {
        out.append( "logStats() entries=" + _sortedValues.length );

        if ( _units != null ) out.append( ", units=" ).append( _units );

        out.append( " : " );

        out.append( ", med=" + median() + ", ave=" + DP2( getAverage() ) +
                    ", min=" + getMinimum() + ", max=" + getMaximum() +
                    ", p99.9=" + DP2( calc( 99.9 ) ) +
                    ", p99=" + DP2( calc( 99 ) ) +
                    ", p95=" + DP2( calc( 95 ) ) +
                    ", p90=" + DP2( calc( 90 ) ) +
                    ", p80=" + DP2( calc( 80 ) ) +
                    ", p70=" + DP2( calc( 70 ) ) +
                    ", p50=" + DP2( calc( 50 ) ) + "\n" );
    }

    public double median() {
        return _median;
    }

    public void recalc( final long[] values, final int size ) {
        if ( size != _sortedValues.length ) {
            _sortedValues = new long[ size ];
        }

        _size = size;

        if ( values.length == 0 ) {
            _average = 0;
            _median  = 0;
            return;
        }

        System.arraycopy( values, 0, _sortedValues, 0, size );
        recalc();
    }

    private String DP2( double n ) {
        return String.format( "%,.2f", n );
    }

    private void recalc() {
        Arrays.sort( _sortedValues );

        long total = 0;

        for ( int i = 0; i < _size; i++ ) {
            total += _sortedValues[ i ];
        }

        _average = (total / _size);

        int idx = _sortedValues.length / 2;

        _median = _sortedValues[ idx ];
    }

}
