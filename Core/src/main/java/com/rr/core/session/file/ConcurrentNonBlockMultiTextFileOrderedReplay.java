package com.rr.core.session.file;

import com.rr.core.codec.DecoderFactory;
import com.rr.core.collections.TimeOrderedBoundEventList;
import com.rr.core.collections.TimeOrderedEventList;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionReceiver;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.thread.ControlThread;
import com.rr.core.thread.SingleElementControlThread;
import com.rr.core.thread.SlowSingleElementControlThread;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * performance wise in backtest the single threaded version works best, the cost of all the cross thread mutex'ing on 50 / 100 plus file sessions is larger than the time spent decoding the heavy liq delta bars
 * <p>
 * THIS IS WORK IN PROGRESS - DONT USE
 */
public class ConcurrentNonBlockMultiTextFileOrderedReplay extends MultiTextFileOrderedReplay {

    private static final Logger _log = LoggerFactory.create( ConcurrentNonBlockMultiTextFileOrderedReplay.class );

    private int                    _maxQSize         = 32768;
    private int                    _maxWaitOnQFullMS = 1;
    private int                    _numReaderThreads = 8;
    private MultiSessionReceiver[] _recievers;
    private boolean                _forceSlowMode    = false;
    private int                    _nextReceiverIdx  = 0;
    private int                    _mainSpinDelay    = 0;

    public ConcurrentNonBlockMultiTextFileOrderedReplay( String name,
                                                         EventRouter inboundRouter,
                                                         FileSessionConfig config,
                                                         DecoderFactory decoderFactory ) {

        super( name, inboundRouter, config, decoderFactory );
    }

    @Override protected void connectChildren() {
        /* dont connect thats done by the MultiSessionThreadedReceiver */
    }

    @Override protected void waitAllConnected() {
        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession         sess = _inFileSessions[ idx ];
            PerSessionInboundRouter in   = (PerSessionInboundRouter) sess.getInboundRouter();

            while( !sess.isConnected() ) {
                ThreadUtilsFactory.getLive().sleep( 5 );
            }
        }

        _log.info( "All file sessions async connected" );

        super.waitAllConnected();
    }

    @Override public void init() throws PersisterException {

        _recievers = new MultiSessionReceiver[ _numReaderThreads ];

        for ( int i = 0; i < _numReaderThreads; i++ ) {
            final String  tid = "FileReplay" + i;
            ControlThread ctl = (_forceSlowMode) ? new SlowSingleElementControlThread( tid, ThreadPriority.Other ) : new SingleElementControlThread( tid, ThreadPriority.Other );

            _recievers[ i ] = new MultiSessionThreadedReceiver( "ReplayMultiSess" + i, ctl );
        }

        super.init();

        for ( int i = 0; i < _numReaderThreads; i++ ) {
            _recievers[ i ].start();
        }

//
//        for( int i=0 ; i < _numReaderThreads ; i++ ) {
//            _recievers[i].
//        }
    }

    @Override protected TimeOrderedEventList getInQForFileSess( final String baseName ) { return new TimeOrderedBoundEventList( baseName + "InboundQ", _maxQSize, _maxWaitOnQFullMS ); }

    @Override protected void setFileSessionReceiver( final String fileName, final TextFileSession fileSession ) {
        final MultiSessionReceiver receiver = _recievers[ (_nextReceiverIdx++) % _recievers.length ];
        fileSession.attachReceiver( receiver );
        receiver.addSession( fileSession );
    }

    @Override protected boolean waitForEventFromEachSession( boolean consolidateSessionList ) {
        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession         sess = _inFileSessions[ idx ];
            PerSessionInboundRouter in   = (PerSessionInboundRouter) sess.getInboundRouter();

            if ( _curEvents[ idx ] == null ) {
                while( in.isEmpty() && sess.isConnected() ) {
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
