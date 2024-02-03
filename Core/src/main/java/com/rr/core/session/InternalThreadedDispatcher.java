/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.collections.EventQueue;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.NullEvent;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * a pausable dispatcher customised for session awareness
 */
final class InternalThreadedDispatcher extends Thread {

    static final Logger  _log         = LoggerFactory.create( InternalThreadedDispatcher.class );
    static final ZString DISCONNECTED = new ViewString( "InternalThreadedDispatcher() Unable to dispatch as destination Session Disconnected" );

    private static final ZString DROP_MSG = new ViewString( "Dropping session message as not logged in, type=" );

    private final int                _maxPause;
    private final EventQueue         _queue;
    private final EventQueue         _syncQueue;
    private final ThreadPriority     _threadPriority;
    private final ReusableString _logMsg = new ReusableString( "50" );
    private       RecoverableSession _session;
    private       boolean            _finished;
    private boolean _connected = false;

    InternalThreadedDispatcher( String threadName,
                                ThreadPriority threadPriority,
                                EventQueue queue,
                                EventQueue syncQueue,
                                int maxPause ) {
        super( threadName );

        _maxPause       = maxPause;
        _queue          = queue;
        _syncQueue      = syncQueue;
        _threadPriority = threadPriority;

        setDaemon( true );
    }

    @Override
    public void run() {

        _session.threadedInit();

        ThreadUtilsFactory.get().setPriority( this, _threadPriority );

        _log.info( _session.getComponentId() + " InternalThreadedDispatcher running, disconnectPause=" + _maxPause );

        while( !_finished ) { // finished is not volatile but will be updated by sync mem barrier in processor
            if ( _maxPause > 1000 ) {
                _log.info( _session.getComponentId() + " Session connected, about to processes events" );
            }
            connectedEventProcessing();
            if ( _maxPause > 1000 ) {
                _log.info( _session.getComponentId() + " Session cant handle events so invoke disconnected flush" );
            }
            disconnectedFlush();
        }

        _log.info( "SessionThreadedDispatcher " + getName() + " finished" );
    }

    public void connected( boolean connected ) {
        if ( connected != _connected ) {
            _log.info( "Session OutDispatcher " + getName() + " : " + ((connected) ? "CONNECTED" : "DISCONNECTED") );
            _connected = connected;

            synchronized( _syncQueue ) {
                _syncQueue.notifyAll();
            }

            synchronized( _queue ) {
                _queue.notifyAll();
            }
        }
    }

    void setFinished() {
        _finished = true;
        if ( _queue.isEmpty() ) {
            _queue.add( new NullEvent() ); // wake up queue
        }

    }

    void setSession( RecoverableSession session ) {
        _session = session;
    }

    private void connectedEventProcessing() {
        if ( _session.canHandle() ) {
            try {
                sendSyncMessages();

                doProcessConnectedEvents();
            } catch( Exception e ) {
                _log.info( "InternalThreadedDispatcher " + getName() + " exception " + e.getMessage() );
            }
        }
    }

    private void disconnectedFlush() {

        int postFlush = 0;

        if ( !_connected ) {
            flush();
            postFlush = _queue.size();
        }

        if ( _maxPause > 1000 ) {
            _log.info( "InternalThreadedDispatcher " + getName() + " disconnectedFlush START sleep pending wakeup connected=" + _connected + ", postFlush" + postFlush + ", qsize=" + _queue.size() );
        }

        // sleep until session can handle messages or more messages appear that require flushing

        while( !_finished && !_session.canHandle() && _queue.size() == postFlush ) {
            synchronized( _queue ) {
                try {
                    _queue.wait( _maxPause );
                } catch( InterruptedException e ) {
                    _log.info( e.getMessage() );
                }
            }
        }

        if ( _maxPause > 1000 ) {
            _log.info( "InternalThreadedDispatcher " + getName() + " disconnectedFlush END sleep pending wakeup connected=" + _connected + ", postFlush" + postFlush + ", qsize=" + _queue.size() );
        }
    }

    private void doProcessConnectedEvents() {
        Event msg;

        while( !_finished && _connected ) {
            msg = _queue.next();

            if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                _session.handleNow( msg );
            }
        }
    }

    private void flush() {

        // throw away anything in the syncQ
        while( !_syncQueue.isEmpty() ) {
            _syncQueue.next();
        }

        // disconnected drop any session messages
        // optionally keep any other messages or reject back  upstream

        Event head = null;
        Event tail = null;

        while( !_queue.isEmpty() ) {
            Event msg = _queue.next();
            if ( msg == null || msg.getReusableType() == CoreReusableType.NullEvent ) break;

            if ( _session.discardOnDisconnect( msg ) == false ) {

                if ( _session.rejectMessageUpstream( msg, DISCONNECTED ) ) {
                    // message recycled by successful reject processing
                } else {

                    if ( tail == null ) {
                        head = msg;
                        tail = msg;
                    } else {
                        tail.attachQueue( msg );
                        tail = msg;
                    }
                }
            } else {
                _logMsg.copy( DROP_MSG ).append( msg.getReusableType().toString() );

                _log.info( _logMsg );

                _session.outboundRecycle( msg );
            }
        }

        // Replay outstanding messages back to the transmission queue

        if ( head != null ) {

            Event tmp = head;

            while( tmp != null ) {
                Event next = tmp.getNextQueueEntry();
                tmp.detachQueue();
                _queue.add( tmp );
                tmp = next;
            }
        }

        if ( !_finished ) {
            synchronized( _queue ) {
                try {
                    _queue.wait();
                } catch( InterruptedException e ) {
                    // dont care
                }
            }
        }
    }

    private void sendSyncMessages() {
        try {
            Event msg;

            while( !_finished && !_session.isLoggedIn() && _connected ) {
                msg = _syncQueue.next();

                if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                    _session.handleNow( msg );
                }
            }

            while( !_finished && !_syncQueue.isEmpty() && _connected ) { // drain syncQ
                msg = _syncQueue.next();

                if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                    _session.handleNow( msg );
                }
            }

/*
            while( !_finished && !_syncQueue.isEmpty() && !_session.isLoggedIn() && _connected ) {
                msg = _syncQueue.next();

                if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                    _session.handleNow( msg );
                }
            }

            while( !_finished && !_syncQueue.isEmpty() && _connected ) { // drain syncQ
                msg = _syncQueue.next();

                if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                    _session.handleNow( msg );
                }
            }
*/
        } catch( Exception e ) {
            _log.info( "SessionThreadedDispatcher " + getName() + " exception " + e.getMessage() );
        }
    }
}
