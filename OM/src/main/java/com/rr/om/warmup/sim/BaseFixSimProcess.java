/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.PersisterException;
import com.rr.core.persister.memmap.IndexMMPersister;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.session.SessionException;
import com.rr.core.session.SessionThreadedDispatcher;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ThreadPriority;
import com.rr.om.warmup.FixTestUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class BaseFixSimProcess {

    private static final Logger _log = ConsoleFactory.console( BaseFixSimProcess.class, Level.info );

    protected static int _heartbeat = 30;

    protected final ZString      _persistFileNameBase;
    protected       FixSimParams _params;

    public static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {

        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );

        sp.init( chains, chainSize, extraAlloc );
    }

    public static void setHeartbeat( int hbSecs ) {
        _heartbeat = hbSecs;
    }

    public BaseFixSimProcess( FixSimParams params ) {
        _params = params;

        AppProps props   = AppProps.instance();
        String   appName = params.getAppName();
        String   baseDir = props.getProperty( CoreProps.TMP_DIR, false, "./persist" );
        _persistFileNameBase = new ViewString( baseDir + "/warmup/" + appName );
    }

    protected IndexPersister createInboundPersister( String id, ThreadPriority priority ) {

        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/in/" ).append( id ).append( ".dat" );
        if ( _params.isRemovePersistence() ) FileUtils.rmIgnoreError( fileName.toString() );
        MemMapPersister persister = new MemMapPersister( new ViewString( id ),
                                                         fileName,
                                                         _params.getPersistDatPreSize(),
                                                         _params.getPersistDatPageSize(),
                                                         priority );

        fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/in/" ).append( id ).append( ".idx" );
        if ( _params.isRemovePersistence() ) FileUtils.rmIgnoreError( fileName.toString() );
        IndexPersister indexPersister = new IndexMMPersister( persister,
                                                              new ViewString( "IDX_" + id ),
                                                              fileName,
                                                              _params.getPersistIdxPreSize(),
                                                              priority );

        return indexPersister;
    }

    protected IndexPersister createOutboundPersister( String id, ThreadPriority priority ) {

        ReusableString fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/out/" ).append( id ).append( ".dat" );
        if ( _params.isRemovePersistence() ) FileUtils.rmIgnoreError( fileName.toString() );
        MemMapPersister persister = new MemMapPersister( new ViewString( id ),
                                                         fileName,
                                                         _params.getPersistDatPreSize(),
                                                         _params.getPersistDatPageSize(),
                                                         priority );

        fileName = new ReusableString( _persistFileNameBase );
        fileName.append( '/' ).append( id.toLowerCase() ).append( "/out/" ).append( id ).append( ".idx" );
        if ( _params.isRemovePersistence() ) FileUtils.rmIgnoreError( fileName.toString() );
        IndexPersister indexPersister = new IndexMMPersister( persister,
                                                              new ViewString( "IDX_" + id ),
                                                              fileName,
                                                              _params.getPersistIdxPreSize(),
                                                              priority );

        return indexPersister;
    }

    protected final EventQueue getQueue( String name ) {

        _log.info( "Using Queue with BlockingSyncQueue for " + name );

        return new BlockingSyncQueue();
    }

    protected EventDispatcher getSessionDispatcher( String name, ThreadPriority priority ) {

        //  SIM sessions must not use spinning queues, as that causes massive contention and latency

        EventDispatcher dispatcher;

        if ( _params.isOptimiseForLatency() ) {
            // no dispatcher faster for straight latency .. fine providing only one client
            // tho should really lock the order in processor

            dispatcher = new DirectDispatcher();

            _log.info( "Using DirectDispatcher for " + name );

        } else {
            EventQueue queue = new BlockingSyncQueue();
            dispatcher = new SessionThreadedDispatcher( name, queue, priority, 5 );

            _log.info( "Using SessionThreadedDispatcher with BlockingSyncQueue for " + name );
        }

        return dispatcher;
    }

    /**
     * @throws SessionException
     * @throws FileException
     * @throws PersisterException
     * @throws IOException
     */
    protected void init() throws SessionException, FileException, PersisterException, IOException {
        //
    }

    protected void loadTradesFromFile( String fileName, List<byte[]> templateRequests ) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader( new FileReader( fileName ) );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() ) {

                String req = line.trim();

                if ( req.length() > 0 ) {
                    byte[] msg = FixTestUtils.semiColonToFixDelim( req );

                    templateRequests.add( msg );

                    if ( _params.isDebug() ) _log.info( "Template Request '" + line + "' len=" + msg.length );
                }
            }
        } finally {
            FileUtils.close( reader );
        }
    }

    protected void setSocketPerfOptions( SocketConfig socketConfig ) {
        socketConfig.setSoDelayMS( 0 );

        if ( _params.isOptimiseForThroughPut() || _params.isOptimiseForLatency() ) {
            socketConfig.setUseNIO( true );
        } else {
            socketConfig.setUseNIO( false );
        }
    }
}
