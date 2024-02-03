package com.rr.core.session.file;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.DecoderFactory;
import com.rr.core.codec.EndDateFilterException;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.collections.DoubleLinkedEventQueueImpl;
import com.rr.core.collections.TimeOrderedEventList;
import com.rr.core.collections.TimeOrderedEventListImpl;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.thread.AllDisconnectedException;
import com.rr.core.utils.*;

import java.io.IOException;

public class MultiTextFileOrderedReplay extends AbstractSession {

    private static final Logger _log            = LoggerFactory.create( MultiTextFileOrderedReplay.class );
    private static final int    MAX_FILE_GROUPS = 1024;

    protected static class PerSessionInboundRouter extends AbstractEventRouter {

        private boolean                    _onReadCacheQ;
        private TimeOrderedEventList       _inbound;
        private DoubleLinkedEventQueueImpl _cached;

        public PerSessionInboundRouter( final String id, TimeOrderedEventList q, boolean onReadCacheQ ) {
            super( id );
            _inbound      = q;
            _onReadCacheQ = onReadCacheQ;
            _cached       = new DoubleLinkedEventQueueImpl( id + "Cache" );
        }

        @Override public boolean canHandle()               { return _inbound.size() < _inbound.maxCapacity(); }

        @Override public void handle( final Event msg )    { handleNow( msg ); }

        @Override public void handleNow( final Event msg ) { _inbound.add( msg ); } // should be only one message in queue

        @Override public void threadedInit()               { /* nothing */ }

        public Event getOldestEvent() {
            if ( _onReadCacheQ ) {
                if ( _cached.isEmpty() ) {
                    if ( _inbound.isEmpty() ) {
                        return null;
                    }

                    _inbound.moveTo( _cached );
                }
                return _cached.removeLast();
            }
            return isEmpty() ? null : _inbound.removeLast();
        }

        public boolean isEmpty() { return _cached.isEmpty() && _inbound.isEmpty(); }
    }
    private final    String[]            _filesIn;
    protected        TextFileSession[]   _inFileSessions;
    protected        Event[]             _curEvents;
    private          Receiver            _receiver;
    private          DecoderFactory      _decoderFactory;
    private          int                 _nextSeqNum          = 0;
    private volatile boolean             _finished;       // ALL FILES FINISHED
    private          StandardThreadUtils _standardThreadUtils = new StandardThreadUtils();
    private          SMTStartContext     _ctx;
    private          boolean             _useTwoTierCaching   = false;

    public MultiTextFileOrderedReplay( String name,
                                       EventRouter inboundRouter,
                                       FileSessionConfig config,
                                       DecoderFactory decoderFactory ) {

        super( name, inboundRouter, config, new DummyMultiSessionDispatcher(), new DummyEncoder(), decoderFactory.getDefault(), decoderFactory.getDefault() );

        _decoderFactory = decoderFactory;

        _filesIn = FileNameDateGrouper.groupFilesByDate( config.getFilesIn() );

        if ( _filesIn.length > MAX_FILE_GROUPS ) throw new SMTRuntimeException( getComponentId() + " found " + _filesIn.length + " groups, max is " + MAX_FILE_GROUPS );

        for ( int i = 0; i < _filesIn.length; i++ ) {
            _log.log( Level.high, "Found STREAM " + i + " " + _filesIn[ i ] );
        }

        _inFileSessions = new TextFileSession[ _filesIn.length ];
        _curEvents      = new Event[ _filesIn.length ];
    }

    @Override
    public void attachReceiver( Receiver receiver ) {
        _receiver = receiver;
    }

    @Override public synchronized void connect() {

        if ( !isConnected() ) {
            connectChildren();

            if ( _receiver == null ) {
                _receiver = new ThreadedReceiver( this, ThreadPriority.Other, false );
            }

            _receiver.start();

            setSessionState( SessionState.Connected );
        }
    }

    @Override public void internalConnect()                                                                                                        { /* nothing */ }

    @Override public void processNextInbound() throws Exception {

        boolean consolidateSessionList = false;

        // ensure have one record for each session which is open
        consolidateSessionList = waitForEventFromEachSession( consolidateSessionList );

        if ( consolidateSessionList ) {
            consolidateSessionList();
        }

        Event oldestEvent = null;
        int   oldestIdx   = -1;

        // status events should have time of UNSET_LONG

        // iterate over the messages to find the oldest event
        for ( int idx = 0; idx < _curEvents.length; idx++ ) {
            Event t = _curEvents[ idx ];
            if ( t != null ) {
                if ( oldestEvent == null ) {
                    oldestEvent = t;
                    oldestIdx   = idx;
                } else {
                    long newTime = t.getEventTimestamp();
                    long oldTime = oldestEvent.getEventTimestamp();
                    if ( oldTime != Constants.UNSET_LONG && newTime < oldTime ) {
                        oldestEvent = t;
                        oldestIdx   = idx;
                    }
                }
            }
        }

        if ( oldestIdx >= 0 ) {
            _curEvents[ oldestIdx ] = null;
        }

        if ( oldestEvent != null ) {

            Event tmp;
            while( oldestEvent != null ) {
                tmp = oldestEvent.getNextQueueEntry();
                if ( tmp != null ) {
                    oldestEvent.detachQueue();
                }
                oldestEvent.setMsgSeqNum( nextSeqNum() );

// _log.info( "TEMP : RECEIVED [" + _inFileSessions[oldestIdx].getCurFileName() + "] " + oldestEvent );

                _inboundRouter.handle( oldestEvent );
                oldestEvent = tmp;
            }

            incrementInboundCount();
        }
    }

    @Override public void handleForSync( Event msg ) {
        handleNow( msg );
    }

    @Override
    public boolean discardOnDisconnect( Event msg ) {
        return false;
    }

    @Override public void persistLastInboundMesssage()                                                                                             { /* nothing */ }

    @Override
    public boolean canHandle() {
        return !_finished;
    }

    @Override public boolean hasOutstandingWork() {
        return !_finished && Utils.getExitCode() == 0;
    }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        try {
            _ctx = ctx;

            init();
        } catch( PersisterException e ) {
            throw new SMTRuntimeException( getComponentId() + " failed to initialise : " + e.getMessage(), e );
        }
    }

    @Override public void init() throws PersisterException {
        super.init();

        for ( int i = 0; i < _filesIn.length; i++ ) {
            String fileName = _filesIn[ i ];

            String baseName = fileName;

            int idx = fileName.lastIndexOf( "/" );
            if ( idx > 0 && idx < fileName.length() ) {
                baseName = fileName.substring( idx + 1 );
            }

            Decoder clonedDecoder = getDecoder( baseName );

            _log.log( Level.debug, getComponentId() + " creating session for files " + fileName );

            String            cfgId = fileName + "Cfg";
            FileSessionConfig fsc   = new FileSessionConfig( cfgId );
            ReflectUtils.shallowCopy( fsc, getConfig(), ReflectUtils.getMembers( fsc ) );
            fsc.setComponentId( cfgId );
            fsc.setRecycler( getConfig().getRecycler() );
            String subFiles[] = fileName.split( "," );
            fsc.setPathRoots( null );
            fsc.setPatternMatch( null );
            fsc.setFilesIn( subFiles );

            final EventRouter     collectingRouter = new PerSessionInboundRouter( getComponentId() + "_" + "InRouter", getInQForFileSess( baseName ), getUseTwoTierCaching() );
            final TextFileSession fileSession      = new TextFileSession( getComponentId() + "_" + FileUtils.getBaseName( fileName ), collectingRouter, fsc, clonedDecoder );

            setFileSessionReceiver( fileName, fileSession );

            _inFileSessions[ i ] = fileSession;
        }

        for ( TextFileSession sess : _inFileSessions ) {
            sess.init();
        }
    }

    @Override public synchronized void stop() {
        super.stop();

        if ( _inFileSessions != null ) {
            for ( TextFileSession sess : _inFileSessions ) {
                if ( sess != null ) sess.stop();
            }
        }
    }

    /**
     * processIncoming is NOT used by the NON blocking multiplexors
     */
    @Override public void processIncoming() {
        try {
            waitAllConnected();

            while( !_finished ) {
                processNextInbound();
            }
        } catch( AllDisconnectedException ade ) {
            throw ade;
        } catch( Exception e ) {
            _log.error( ERR_IN_MSG, "Exception processing inbound : " + e.getMessage(), e );
        }
    }

    @Override
    public void startWork() {
        connect();
    }

    @Override
    public void stopWork() {
        stop();
    }

    @Override
    public void logInboundError( Exception e ) {
        _log.error( ERR_IN_MSG, "Error in " + getComponentId() + " : " + e.getMessage(), e );
    }

    @Override
    protected void disconnectCleanup() {
        stop();
    }

    @Override
    protected void logInEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override
    protected void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override protected void persistIntegrityCheck( boolean inbound, long key, Event msg )                                                         { /* nothing */ }

    @Override protected Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound )                                                   { return null; }

    @Override protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) { return null; }

    @Override
    protected void sendChain( Event msg, boolean canRecycle ) {
        if ( getChainSession() != null ) {
            getChainSession().handle( msg );
        }
    }

    @Override
    protected void sendNow( Event msg ) {
        throw new SMTRuntimeException( "Unable to send event as no output file confifured" );
    }

    @Override public void threadedInit()                                                                                                           { /* nothing */ }

    protected void connectChildren() {
        for ( TextFileSession sess : _inFileSessions ) {
            sess.connect();
        }
    }

    protected Decoder getDecoder( final String baseName ) { return _decoderFactory.getDecoder( _ctx, baseName ); }

    protected TimeOrderedEventList getInQForFileSess( final String baseName ) { return new TimeOrderedEventListImpl( baseName + "InboundQ" ); }

    protected void getNextFromSession( final TextFileSession sess ) {
        try {
            sess.processNextInbound();

        } catch( SessionException e ) {

            sess.logInboundError( e );
            sess.disconnect( !e.isForcedLogout() ); // pause session on forced logout

        } catch( DisconnectedException e ) {

            sess.disconnect( false );

        } catch( IOException e ) {

            sess.disconnect( false );

        } catch( EndDateFilterException e ) {

            _log.info( getComponentId() + " EndDateFilter for " + sess.getCurFileName() + " : " + e.getMessage() );

            sess.setFinished();

        } catch( RuntimeDecodingException e ) {
            sess.logInboundDecodingError( e );
        } catch( Exception e ) {
            sess.logInboundError( e );
        }
    }

    protected boolean getUseTwoTierCaching()                                  { return _useTwoTierCaching; }

    public void setUseTwoTierCaching( final boolean useTwoTierCaching )       { _useTwoTierCaching = useTwoTierCaching; }

    protected void invokeController( Event msg ) {
        dispatchInbound( msg );
    }

    protected void setFileSessionReceiver( final String fileName, final TextFileSession fileSession ) {
        fileSession.attachReceiver( new DummyMultiSessionReceiver( "DummyReceiver" + FileUtils.getBaseName( fileName ) ) );
    }

    protected void waitAllConnected() {
        boolean missing = false;

        boolean consolidateSessionList = waitForEventFromEachSession( false );

        if ( consolidateSessionList ) {
            consolidateSessionList();
        }

        int sentConnected = 0;

        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession         sess = _inFileSessions[ idx ];
            PerSessionInboundRouter in   = (PerSessionInboundRouter) sess.getInboundRouter();

            if ( _curEvents[ idx ] != null ) {
                Event m = _curEvents[ idx ];

                if ( m instanceof SessionStatusEvent ) {
                    _curEvents[ idx ] = m.getNextQueueEntry();

                    m.detachQueue();

                    m.setMsgSeqNum( nextSeqNum() );

                    _inboundRouter.handle( m );
                    incrementInboundCount();

                    ++sentConnected;
                }
            }
        }

        _log.info( getComponentId() + " sent " + sentConnected + " connected events" );
    }

    protected boolean waitForEventFromEachSession( boolean consolidateSessionList ) {
        for ( int idx = 0; idx < _inFileSessions.length; ++idx ) {
            TextFileSession         sess = _inFileSessions[ idx ];
            PerSessionInboundRouter in   = (PerSessionInboundRouter) sess.getInboundRouter();

            if ( _curEvents[ idx ] == null ) {
                while( in.isEmpty() && sess.isConnected() ) {
                    getNextFromSession( sess );
                }

                _curEvents[ idx ] = in.getOldestEvent();

                if ( !sess.isConnected() && in.isEmpty() && _curEvents[ idx ] == null ) {
                    consolidateSessionList = true;
                }
            }
        }
        return consolidateSessionList;
    }

    private void consolidateSessionList() {
        int activeSess = 0;

        for ( int oldIdx = 0; oldIdx < _inFileSessions.length; ++oldIdx ) {
            TextFileSession sess = _inFileSessions[ oldIdx ];
            if ( sess.isConnected() || !((PerSessionInboundRouter) sess.getInboundRouter()).isEmpty() || (_curEvents[ oldIdx ] != null) ) {
                ++activeSess;
            }
        }

        if ( activeSess == _inFileSessions.length ) return; // nothing to do

        if ( activeSess == 0 ) {
            _finished = true;

            _log.info( getComponentId() + " All text files have been replayed" );

            throw new AllDisconnectedException( getComponentId() + " ALL EVENTS PROCESSED CLOSING" );
        }

        final TextFileSession[] inFileSessions = new TextFileSession[ activeSess ];
        final Event[]           curEvents      = new Event[ activeSess ];

        int newIdx = 0;

        for ( int oldIdx = 0; oldIdx < _inFileSessions.length; ++oldIdx ) {
            TextFileSession sess = _inFileSessions[ oldIdx ];
            if ( sess.isConnected() || !((PerSessionInboundRouter) sess.getInboundRouter()).isEmpty() || (_curEvents[ oldIdx ] != null) ) {
                inFileSessions[ newIdx ] = sess;
                curEvents[ newIdx ]      = _curEvents[ oldIdx ];
                ++newIdx;
            } else {
                _log.info( "Dropping file session " + sess.getComponentId() + " lines=" + sess.getTotalRead() + " which is no longer connected and has no data left to process" );
            }
        }

        _inFileSessions = inFileSessions;
        _curEvents      = curEvents;
    }

    private int nextSeqNum() {
        return ++_nextSeqNum;
    }
}
