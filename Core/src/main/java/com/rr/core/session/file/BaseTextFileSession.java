/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.codec.Decoder;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.recovery.json.JSONDecoder;
import com.rr.core.session.DummyMultiSessionDispatcher;
import com.rr.core.session.EventRouter;
import com.rr.core.session.SessionStatus;
import com.rr.core.session.SessionStatusEvent;

import java.io.IOException;

/**
 * for backtesting you must use seperate session PER file as processing orders msgs by timestamp so all must be replayed at same time
 */
public class BaseTextFileSession extends BaseFileSession {

    protected static final Logger _log = LoggerFactory.create( BaseTextFileSession.class );
    private final ReusableString _logInMsg   = new ReusableString();
    private       long           _openTime;
    private       long           _line;
    private       long           _totalRead;
    private       int            _lineLen;
    private       boolean        _procHeader = true;

    public BaseTextFileSession( String name, EventRouter inboundRouter, FileSessionConfig config, Decoder decoder ) {

        super( name, inboundRouter, config, new DummyMultiSessionDispatcher(), new DummyEncoder(), decoder, decoder );

        if ( decoder instanceof JSONDecoder ) {
            _procHeader = false;
        }
    }

    @Override public void processNextInbound() throws Exception {
        if ( isPaused() ) return;

        int b1 = _fileIn.read();

        while( b1 == -1 && !_finished.get() ) {
            openNext();

            if ( !_finished.get() ) {
                b1 = _fileIn.read();
            }
        }

        if ( _finished.get() ) {
            dispatchState( new SessionStatusEvent().set( getIntId(), SessionStatus.END_OF_FIXED_STREAM, getConnectionId() ) );
            disconnect( false );
            return;
        }

        ++_line;
        ++_totalRead;

        // have at least one byte

        _buf.reset();
        _lineLen = 0;

        while( b1 == '\n' ) {
            b1 = _fileIn.read();
        }

        while( b1 != '\n' && b1 != -1 ) {
            if ( _lineLen >= _buf.length() ) {
                _buf.ensureCapacity( _lineLen + Constants.MAX_BUF_LEN );
            }

            if ( b1 != '\r' ) {
                ++_lineLen;
                _buf.append( (byte) b1 );
            }

            b1 = _fileIn.read();
        }

        if ( _lineLen > 0 ) {
            if ( _line == 1 && _procHeader ) {

                _decoder.parseHeader( _buf.getBytes(), 0, _lineLen );

                processNextInbound(); // RECURSE will only do for first line

            } else {
                Event msg = _decoder.decode( _buf.getBytes(), 0, _lineLen );

                dispatchInwards( msg );
            }
        }
    }

    @Override
    public void logInboundError( Exception e ) {
        if ( e instanceof IOException ) {
            _log.info( "IO Error in " + getComponentId() + ", file=" + _curFileName + ", switch to next if avail" );
            openNext();
        } else {
            _log.error( ERR_IN_MSG, "Error in " + getComponentId() + ", file=" + _curFileName + ",  lineNo=" + _line + " : " + e.getMessage() + " on line\n" + _buf.toString(), e );
        }
    }

    @Override protected void openNext() {
        long now = ClockFactory.get().currentTimeMillis();
        if ( _openTime == 0 ) {
            dispatchState( new SessionStatusEvent().set( getIntId(), SessionStatus.START_OF_FIXED_STREAM, getConnectionId() ) );
        } else {
            double durationSEC = Math.max( 1, now - _openTime ) / 1000.0;
            double rate        = (long) (_line / durationSEC);
            _log.info( "BaseTextFileSession " + _curFileName + " processed " + _line + " lines in " + durationSEC + " seconds, giving a rate of " + rate + " (events/second)" );
        }
        super.openNext();
        _openTime = ClockFactory.get().currentTimeMillis();
        _line     = 0;
    }

    public long getTotalRead() { return _totalRead; }

    private void dispatchInwards( final Event msg ) {
        logInEvent( null );

        if ( msg != null ) {
            logInEventPojo( msg );

            invokeController( msg );
        }
    }

    private void dispatchState( final SessionStatusEvent event ) {
        _logInMsg.copy( "Event status change for " ).append( getComponentId() );
        event.dump( _logInMsg );
        logInEvent( _logInMsg );
        dispatchInwards( event );
    }
}
