/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

/**
 * logger level, note ordinal position is important, priority is from top to bottom
 */
public enum Level {

    xtrace( " [xtrace] " ),
    trace( " [trace]  " ),
    debug( " [debug]  " ),
    info( " [info]   " ),
    high( " [high]   " ),
    vhigh( " [vhigh]  " ), /* vhigh and above will be emailed as well */
    WARN( " [WARN]   " ), /* warning and above will be added to gcloud */
    ERROR( " [ERROR]  " ); /* error will be texted as well */

    private final byte[] _logHdr;

    Level( String logHdr ) {
        _logHdr = logHdr.getBytes();
    }

    public byte[] getLogHdr() { return _logHdr; }
}
