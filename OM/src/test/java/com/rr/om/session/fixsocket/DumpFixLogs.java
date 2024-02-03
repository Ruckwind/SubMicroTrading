/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersistentReplayListener;
import com.rr.core.persister.Persister;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.utils.ThreadPriority;

public class DumpFixLogs {

    static final         Logger    _log   = ConsoleFactory.console( DumpFixLogs.class, Level.info );
    private static final ErrorCode FAILED = new ErrorCode( "DFL100", "Exception in main" );

    public static void main( String[] args ) {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        DumpFixLogs tcs = new DumpFixLogs();

        try {

            tcs.dump( args[ 0 ], Long.parseLong( args[ 1 ] ), Integer.parseInt( args[ 2 ] ) );

        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private void dump( String fileName, long persistDatPreSize, int persistDatPageSize ) throws Exception {
        MemMapPersister persister = new MemMapPersister( new ViewString( "Dump" ),
                                                         new ViewString( fileName ),
                                                         persistDatPreSize,
                                                         persistDatPageSize,
                                                         ThreadPriority.Other );

        persister.open();

        recoverFromFile( persister );
    }

    private void recoverFromFile( MemMapPersister persister ) throws Exception {
        persister.replay( new PersistentReplayListener() {

            int msgNum = 0;
            ReusableString msg = new ReusableString();

            @Override public void started() { _log.info( "Dump started" ); }

            @Override public void completed() { _log.info( "Dump complete" ); }

            @Override public void failed() { _log.info( "Dump FAILED" ); }

            @Override
            public void message( Persister p, long key, byte[] buf, int offset, int len, short flags ) {
                msg.reset();
                msg.append( "[" ).append( ++msgNum ).append( "] key=[ " + key + "] flags=" ).append( flags ).append( " :: " ).append( buf, offset, len );
                _log.info( msg );
            }

            @Override
            public void message( Persister p, long key, byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, short flags ) {
                message( p, key, buf, offset, len, flags );
            }
        } );
    }
}
