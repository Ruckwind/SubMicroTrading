/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.tools;

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
import com.rr.core.persister.memmap.IndexMMPersister;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.ThreadPriority;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.warmup.sim.FixFactory;

public class DumpFixByIndex {

    static final         Logger    _log   = ConsoleFactory.console( DumpFixByIndex.class, Level.info );
    private static final ErrorCode FAILED = new ErrorCode( "DFI100", "Exception in main" );

    public static void main( String[] args ) {
        AppProps.instance().init( "DumpFixByIndex" );
        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        DumpFixByIndex tcs = new DumpFixByIndex();

        try {

            CodecId codecId = CodecId.StratInternalFix44;

            tcs.dump( args[ 0 ], Integer.parseInt( args[ 1 ] ), Integer.parseInt( args[ 2 ] ), codecId );

        } catch( Exception e ) {

            _log.error( FAILED, "", e );
        }
    }

    private void dump( String fileName, int startIdx, int endIdx, final CodecId codecId ) throws Exception {
        MemMapPersister persister = new MemMapPersister( new ViewString( "Dump" ),
                                                         new ViewString( fileName + ".dat" ),
                                                         2148532224L,
                                                         10000000,
                                                         ThreadPriority.Other );

        IndexMMPersister indexPersister = new IndexMMPersister( persister,
                                                                new ViewString( "DumpIdxPersist" ),
                                                                new ViewString( fileName + ".idx" ),
                                                                135266304,
                                                                ThreadPriority.Other );

//        20191001-15:32:50.735 (BST) [info]  MemMapPersister strat1Server_Upstream_IN, filePreSize=2148532224, pageSize=10000000
//        20191001-15:32:50.735 (BST) [info]  Scheduler.registerForGroupEvent EndOfDay, listener strat1Server_Upstream_INDateRoll, isEventScheduled=true
//        20191001-15:32:50.735 (BST) [info]  MemMapPersister strat1Server_Upstream_IN, maxRecSize=4087, pageSize=10000000, initFileSize=2148532224
//        20191001-15:32:50.735 (BST) [info]  MemMapPersister IDX_strat1Server_Upstream_IN, filePreSize=135266304, pageSize=2097152
//        20191001-15:32:50.736 (BST) [info]  Scheduler.registerForGroupEvent EndOfDay, listener IDX_strat1Server_Upstream_INDateRoll, isEventScheduled=true
//        20191001-15:32:50.736 (BST) [info]  IndexMMPersister IDX_strat1Server_Upstream_IN, preSize=135266304, recSize=16, entriesPage=131072, pageSize=2097152
//        20191001-15:32:50.736 (BST) [info]  MemMapPersister strat1Server_Upstream_OUT, filePreSize=2148532224, pageSize=10000000
//        20191001-15:32:50.736 (BST) [info]  Scheduler.registerForGroupEvent EndOfDay, listener strat1Server_Upstream_OUTDateRoll, isEventScheduled=true
//        20191001-15:32:50.736 (BST) [info]  MemMapPersister strat1Server_Upstream_OUT, maxRecSize=4087, pageSize=10000000, initFileSize=2148532224
//        20191001-15:32:50.736 (BST) [info]  MemMapPersister IDX_strat1Server_Upstream_OUT, filePreSize=135266304, pageSize=2097152
//        20191001-15:32:50.736 (BST) [info]  Scheduler.registerForGroupEvent EndOfDay, listener IDX_strat1Server_Upstream_OUTDateRoll, isEventScheduled=true
//        20191001-15:32:50.736 (BST) [info]  IndexMMPersister IDX_strat1Server_Upstream_OUT, preSize=135266304, recSize=16, entriesPage=131072, pageSize=2097152

        persister.open();
        indexPersister.setLogTimes( false );
        indexPersister.open();

        recoverFromFile( indexPersister, startIdx, endIdx, codecId );
    }

    private void recoverFromFile( IndexMMPersister indexPersister, int startIdx, int endIdx, final CodecId codecId ) throws Exception {

        ReusableString msgLog      = new ReusableString();
        byte[]         readBuf     = new byte[ Constants.MAX_BUF_LEN ];
        FixDecoder     fullDecoder = FixFactory.createFixFullDecoder( codecId.getFixVersion() );
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
                    msg.dump( msgLog );
                    _log.info( msgLog );
                }
            }
        }
    }
}
