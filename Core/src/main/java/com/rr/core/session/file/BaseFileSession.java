/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.codec.*;
import com.rr.core.collections.EventQueue;
import com.rr.core.component.CompRunState;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import com.rr.core.utils.file.FileLog;

import java.io.BufferedInputStream;
import java.io.IOException;

public abstract class BaseFileSession extends AbstractSession implements NonBlockingSession {

    protected static final Logger _log = LoggerFactory.create( BaseFileSession.class );

    private static final int BUFFER_SIZE = 131072;

    private final String[] _filesIn;
    private final FileLog  _fileOut;
    protected BufferedInputStream _fileIn;
    protected ReusableString      _buf = new ReusableString( Constants.MAX_BUF_LEN );
    protected String _curFileName;
    private Receiver _receiver;
    private int _nextFileIdx = 0;

    public BaseFileSession( String name,
                            EventRouter inboundRouter,
                            FileSessionConfig config,
                            EventDispatcher dispatcher,
                            Encoder encoder,
                            Decoder decoder,
                            Decoder fullDecoder ) {

        super( name, inboundRouter, config, dispatcher, encoder, decoder, fullDecoder );

        _filesIn = config.getFilesIn();
        _fileOut = config.getFileOut();
    }

    @Override public void attachReceiver( Receiver receiver ) {
        _receiver = receiver;
    }

    @Override
    public synchronized void connect() {

        if ( !isConnected() ) {
            openNext();

            if ( _receiver == null ) {
                _receiver = new ThreadedReceiver( this, ThreadPriority.Other );
            }

            _receiver.start();
            _outboundDispatcher.start();

            if ( _fileOut != null ) _fileOut.open();

            setSessionState( SessionState.Connected );
        }
    }

    @Override public void internalConnect() {
        if ( _receiver instanceof MultiSessionReceiver ) {
            connect();
        }
    }

    @Override
    public abstract void processNextInbound() throws Exception;

    @Override
    public void handleForSync( Event msg ) {
        handleNow( msg );
    }

    @Override public boolean discardOnDisconnect( Event msg ) {
        return false;
    }

    @Override public void persistLastInboundMesssage()                                                                                             { /* nothing */ }

    @Override public boolean canHandle() {
        return isConnected() && ! getCompRunState().isComplete();
    }

    @Override public void init() throws PersisterException                                                                                         { super.init(); }

    @Override
    public synchronized void stop() {
        super.stop();

        if ( _fileIn != null ) {
            _log.info( "BaseFileSession " + getComponentId() + " : closing file " + _curFileName );

            FileUtils.close( _fileIn );
        }

        if ( _fileOut != null ) _fileOut.close();
    }

    /**
     * processIncoming is NOT used by the NON blocking multiplexors
     */
    @Override
    public void processIncoming() {
        try {
            while( ! getCompRunState().isComplete() ) {
                processNextInbound();
            }

        } catch( SessionException e ) {

            logInboundError( e );
            disconnect( !e.isForcedLogout() ); // pause session on forced logout

        } catch( DisconnectedException e ) {

            disconnect( ! getCompRunState().isComplete() );

        } catch( EndDateFilterException e ) {

            disconnect( ! getCompRunState().isComplete() );
            setFinished();              // files in sequential order so dont process more

        } catch( IOException e ) {

            disconnect( ! getCompRunState().isComplete() );
            openNext();

        } catch( RuntimeDecodingException e ) {
            logInboundDecodingError( e );
        } catch( Exception e ) {
            // not a socket error dont drop socket
            logInboundError( e );
        }
    }

    @Override public void logInboundError( Exception e ) {
        if ( e instanceof IOException ) {
            _log.info( "IO Error in " + getComponentId() + ", file=" + _curFileName + ", switch to next if avail" );
            openNext();
        } else {
            _log.error( ERR_IN_MSG, "Error in " + getComponentId() + ", file=" + _curFileName + " : " + e.getMessage(), e );
        }
    }

    @Override protected void disconnectCleanup() {
        stop();
    }

    @Override protected Level getSessionChangeStateLogLevel() { return Level.high; }

    @Override protected void logInEvent( ZString event ) {
        if ( _logEvents && event != null ) {
            _log.infoLarge( event );
        }
    }

    @Override protected void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override protected void persistIntegrityCheck( boolean inbound, long key, Event msg )                                                         { /* nothing */ }

    @Override protected Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound )                                                   { return null; }

    @Override protected Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound ) { return null; }

    @Override protected void sendChain( Event msg, boolean canRecycle ) {
        if ( getChainSession() != null ) {
            getChainSession().handle( msg );
        }
    }

    @Override protected void sendNow( Event msg ) {

        if ( _fileOut == null ) {
            throw new SMTRuntimeException( "Unable to send event as no output file confifured" );
        }

        _encoder.encode( msg );

        if ( _logStats ) {
            lastSent( Utils.nanoTime() );
        }

        _fileOut.log( _encoder.getBytes(), _encoder.getOffset(), _encoder.getLength() );

        if ( _chainSession != null && _chainSession.isConnected() ) {
            _chainSession.handle( msg );
        } else {
            outboundRecycle( msg );
        }
    }

    @Override public boolean isMsgPendingWrite()                                                                                                   { return false; }

    @Override public void retryCompleteWrite() throws IOException                                                                                  { /* nothing */ }

    @Override public EventQueue getSendQueue()                                                                                                     { return null; }

    @Override public EventQueue getSendSyncQueue()                                                                                                 { return null; }

    @Override public void threadedInit()                                                                                                           { /* nothing */ }

    public String getCurFileName() { return _curFileName; }

    protected void invokeController( Event msg ) {
        dispatchInbound( msg );
    }

    protected void openNext() {
        if ( _fileIn != null ) {
            try {
                _log.info( "BaseFileSession " + getComponentId() + " : closing file " + _curFileName );
                _fileIn.close();
                _fileIn = null;
            } catch( IOException e ) {
                _log.warn( "Exception closing file " + e.getMessage() );
            }
        }

        if ( _nextFileIdx >= _filesIn.length ) {
            // finished replay but dont terminate process as need wait for consumer to process all events
            setFinished();
        } else {

            _curFileName = _filesIn[ _nextFileIdx ];

            try {
                _log.info( "BaseFileSession " + getComponentId() + " : opening file " + _curFileName );

                _fileIn = FileUtils.bufFileInpStream( _curFileName, BUFFER_SIZE );

                if ( _decoder instanceof StreamDependant ) {
                    ((StreamDependant) _decoder).setStreamID( _curFileName );
                }

            } catch( Exception e ) {
                throw new SMTRuntimeException( "FileNonBlockaingSession Unable to open " + _curFileName + " : " + e.getMessage(), e );
            }

            ++_nextFileIdx;
        }
    }

    protected void setFinished() {
        setCompRunState( CompRunState.Complete );
    }
}
