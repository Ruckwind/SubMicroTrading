package com.rr.core.utils;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;

import java.util.concurrent.TimeUnit;

public class PerfMeter {

    private final long[]      _values;
    private final Percentiles _percentiles;
    private final boolean     _metricsEnabled;
    private final String      _id;
    private final Logger      _log;

    private long           _startNanos;
    private int            _idx    = 0;
    private ReusableString _logMsg = new ReusableString();

    public PerfMeter( final String id, final int blockSize, final boolean metricsEnabled, final Logger log ) {
        _id             = id;
        _values         = new long[ blockSize ];
        _percentiles    = new Percentiles( _values, TimeUnit.NANOSECONDS );
        _metricsEnabled = metricsEnabled;
        _log            = log;

        ShutdownManager.instance().register( "PerfMeterLog" + id, () -> logMetrics(), ShutdownManager.Priority.Medium );
    }

    public void done() {
        if ( _metricsEnabled ) {
            logMetrics();
        }
    }

    public void in() {
        if ( _metricsEnabled ) _startNanos = System.nanoTime();
    }

    public void out() {
        if ( _metricsEnabled ) {
            long endNanos = System.nanoTime();

            long duration = Math.abs( endNanos - _startNanos );

            _values[ _idx ] = duration;

            if ( ++_idx >= _values.length ) {
                logMetrics();

                _idx = 0;
            }
        }
    }

    private void logMetrics() {
        if ( _metricsEnabled ) {
            _percentiles.recalc( _values, _values.length );

            _logMsg.copy( _id ).append( " " );
            _percentiles.logStats( _logMsg );

            _log.info( _logMsg );
        }
    }
}
