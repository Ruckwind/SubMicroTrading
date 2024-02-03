package com.rr.core.utils;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

public class ThroughPutMeter {

    private static final Logger _defaultLogger = LoggerFactory.create( ThroughPutMeter.class );

    private final Logger _log;
    private final String _id;
    private final int    _statsBlockSize;

    private ReusableString _logMsg = new ReusableString();

    private long _totalSleepMS;
    private int  _totalSleepCnt;
    private int  _blocks;

    private long   _blockStartMS;
    private long   _hits;
    private long   _totalHits;
    private double _totalDurationSecs;

    public ThroughPutMeter( String id, int statsBlockSize ) {
        this( id, statsBlockSize, _defaultLogger );
    }

    public ThroughPutMeter( String id, int statsBlockSize, Logger logger ) {
        _id             = id;
        _statsBlockSize = (statsBlockSize == 0) ? 10000 : statsBlockSize;
        _log            = logger;
    }

    public void addSleepMS( final int ms ) {
        _totalSleepMS += ms;
        ++_totalSleepCnt;
    }

    public void finish() {
        logStats();
    }

    public void hit() {
        if ( (++_hits % _statsBlockSize) == 0 ) {
            logStats();
        }
    }

    public void reset() {
        _totalSleepCnt = 0;
        _totalSleepMS  = 0;
        _hits          = 0;
        _blockStartMS  = System.currentTimeMillis(); // REAL TIME
    }

    private void logStats() {
        long   endMS        = System.currentTimeMillis();  // REAL TIME
        double durationSecs = Math.abs( endMS - _blockStartMS ) / 1000.0;

        ++_blocks;
        _totalDurationSecs += durationSecs;
        _totalHits += _hits;

        int rate        = (int) (_hits / durationSecs);
        int overallRate = (int) (_totalHits / _totalDurationSecs);

        _logMsg.copy( "Meter " ).append( _id ).append( " block #" ).append( _blocks );
        _logMsg.append( ", hits=" ).append( _hits )
               .append( ", blockRate=" ).append( rate )
               .append( ", blockSecs=" ).append( durationSecs )
               .append( ", sleeps=" ).append( _totalSleepCnt )
               .append( ", sleepMS=" ).append( _totalSleepMS )
               .append( ", totalDurSecs=" ).append( _totalDurationSecs )
               .append( ", overallRate=" ).append( overallRate );

        _log.info( _logMsg );

        _blockStartMS = endMS;
        _hits         = 0;
    }
}
