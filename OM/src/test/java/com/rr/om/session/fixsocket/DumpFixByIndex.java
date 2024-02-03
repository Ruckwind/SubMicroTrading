/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.codec.FixDecoder;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.FixVersion;
import com.rr.core.persister.memmap.IndexMMPersister;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.ThreadPriority;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.warmup.sim.FixFactory;

public class DumpFixByIndex {

    static final         Logger    _log   = ConsoleFactory.console( DumpFixByIndex.class, Level.info );
    private static final ErrorCode FAILED = new ErrorCode( "DFI100", "Exception in main" );

    public static void main( String[] args ) {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        DumpFixByIndex tcs = new DumpFixByIndex();

        try {

            tcs.dump( args[ 0 ], Integer.parseInt( args[ 1 ] ), Integer.parseInt( args[ 2 ] ) );

        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private void dump( String fileName, int startIdx, int endIdx ) throws Exception {
        MemMapPersister persister = new MemMapPersister( new ViewString( "Dump" ),
                                                         new ViewString( fileName + ".dat" ),
                                                         20000000,
                                                         10000000,
                                                         ThreadPriority.Other );

        IndexMMPersister indexPersister = new IndexMMPersister( persister,
                                                                new ViewString( "DumpIdxPersist" ),
                                                                new ViewString( fileName + ".idx" ),
                                                                2097152,
                                                                ThreadPriority.Other );
        persister.open();
        indexPersister.setLogTimes( false );
        indexPersister.open();

        recoverFromFile( indexPersister, startIdx, endIdx );
    }

    private void recoverFromFile( IndexMMPersister indexPersister, int startIdx, int endIdx ) throws Exception {

        ReusableString msgLog      = new ReusableString();
        byte[]         readBuf     = new byte[ Constants.MAX_BUF_LEN ];
        FixDecoder     fullDecoder = FixFactory.createFixFullDecoder( FixVersion.Fix4_4 );
        int            offset      = 18;

        fullDecoder.setInstrumentLocator( new DummyInstrumentLocator() );

        for ( int idx = startIdx; idx <= endIdx; ++idx ) {
            int bytes = indexPersister.readFromIndex( idx, readBuf, offset );

            if ( bytes > 0 ) {
                msgLog.reset();
                msgLog.append( "[RAW " ).append( idx ).append( "] :: " ).append( readBuf, offset, bytes );
                _log.info( msgLog );

                int expLen = fullDecoder.parseHeader( readBuf, offset, bytes );
                if ( expLen != bytes ) {
                    msgLog.reset();
                    msgLog.append( "LENGTH MISMATCH seqNo=" ).append( idx ).append( ", persistedLen=" ).append( bytes )
                          .append( ", decodeLen=" ).append( expLen );
                    _log.info( msgLog );
                }

                Event msg = fullDecoder.postHeaderDecode();

                if ( msg != null ) {
                    msgLog.reset();
                    msgLog.append( "[DECODED " ).append( idx ).append( "] :: " );
                    ReflectUtils.dump( msgLog, msg );
                    _log.info( msgLog );
                }
            }
        }
    }
}
