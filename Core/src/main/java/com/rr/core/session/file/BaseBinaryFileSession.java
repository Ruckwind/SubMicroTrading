/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.EventRouter;
import com.rr.core.utils.SMTRuntimeException;

public class BaseBinaryFileSession extends BaseFileSession {

    protected static final Logger _log = LoggerFactory.create( BaseBinaryFileSession.class );

    public BaseBinaryFileSession( String name,
                                  EventRouter inboundRouter,
                                  FileSessionConfig config,
                                  EventDispatcher dispatcher,
                                  Encoder encoder,
                                  Decoder decoder,
                                  Decoder fullDecoder ) {

        super( name, inboundRouter, config, dispatcher, encoder, decoder, fullDecoder );
    }

    @Override
    public void processNextInbound() throws Exception {
        if ( isPaused() ) return;

        int b1 = _fileIn.read();

        while( b1 == -1 && !_finished.get() ) {
            openNext();

            b1 = _fileIn.read();
        }

        if ( _finished.get() ) {
            throw new SMTRuntimeException( "No more files to read" );
        }

        int b2 = _fileIn.read();
        int b3 = _fileIn.read();
        int b4 = _fileIn.read();

        int bytes = (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;

        if ( bytes == 0 ) return;

        if ( bytes > _buf.length() ) {
            throw new SMTRuntimeException( "Cant read record as length of " + bytes + " greater than bufSize of " + _buf.length() );
        }

        int read = _fileIn.read( _buf.getBytes(), 0, bytes );

        if ( read != bytes ) {
            throw new SMTRuntimeException( "Only able to read " + read + " bytes not the expected length of " + bytes );
        }

        Event msg = _decoder.decode( _buf.getBytes(), 0, bytes );

        logInEvent( null );

        // @TODO should check actual decode left and shift left / set prebuffered for remaining
        // CME only put 1 message per packet

        if ( msg != null ) {
            logInEventPojo( msg );

            invokeController( msg );
        }
    }
}
