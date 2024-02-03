package com.rr.core.session.file;

import com.rr.core.codec.DecoderFactory;
import com.rr.core.collections.TimeOrderedBoundEventList;
import com.rr.core.collections.TimeOrderedEventList;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.EventRouter;
import com.rr.core.session.ThreadedReceiver;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * performance wise in backtest the single threaded version works best, the cost of all the cross thread mutex'ing on 50 / 100 plus file sessions is larger than the time spent decoding the heavy liq delta bars
 * <p>
 * THIS IS WORK IN PROGRESS - DONT USE
 */
public class ConcurrentMultiTextFileOrderedReplay extends MultiTextFileOrderedReplay {

    private static final Logger _log = LoggerFactory.create( ConcurrentMultiTextFileOrderedReplay.class );

    private int _maxQSize         = 1024;
    private int _maxWaitOnQFullMS = 1;
    private int _mainSpinDelay    = 0;

    public ConcurrentMultiTextFileOrderedReplay( String name,
                                                 EventRouter inboundRouter,
                                                 FileSessionConfig config,
                                                 DecoderFactory decoderFactory ) {

        super( name, inboundRouter, config, decoderFactory );

        setUseTwoTierCaching( true );
    }

    @Override protected void waitAllConnected() {
        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession         sess = _inFileSessions[ idx ];
            PerSessionInboundRouter in   = (PerSessionInboundRouter) sess.getInboundRouter();

            while( !sess.isConnected() && !sess.isStopping() ) {
                ThreadUtilsFactory.getLive().sleep( 5 );
            }
        }

        _log.info( "All file sessions async connected" );

        super.waitAllConnected();
    }

    @Override protected TimeOrderedEventList getInQForFileSess( final String baseName ) { return new TimeOrderedBoundEventList( baseName + "InboundQ", _maxQSize, _maxWaitOnQFullMS ); }

    @Override protected void setFileSessionReceiver( final String fileName, final TextFileSession fileSession ) {
        String                 baseName = FileUtils.getBaseName( fileName );
        final ThreadedReceiver receiver = new ThreadedReceiver( "Receiver:" + baseName, fileSession, ThreadPriority.Other );
        fileSession.attachReceiver( receiver );
    }

    @Override protected boolean waitForEventFromEachSession( boolean consolidateSessionList ) {
        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession                                    sess = _inFileSessions[ idx ];
            MultiTextFileOrderedReplay.PerSessionInboundRouter in   = (MultiTextFileOrderedReplay.PerSessionInboundRouter) sess.getInboundRouter();

            if ( _curEvents[ idx ] == null ) {
                while( in.isEmpty() && sess.isConnected() && !sess.isStopping() ) {
                    ThreadUtilsFactory.getLive().sleep( _mainSpinDelay );
                }

                _curEvents[ idx ] = in.getOldestEvent();

                if ( !sess.isConnected() && in.isEmpty() && _curEvents[ idx ] == null ) {
                    consolidateSessionList = true;
                }
            }
        }
        return consolidateSessionList;
    }

    @Override protected void getNextFromSession( final TextFileSession sess ) {
        ThreadUtilsFactory.getLive().sleep( _mainSpinDelay );
    }
}
