/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionDispatcher;
import com.rr.core.session.MultiSessionReceiver;
import com.rr.core.session.NonBlockingSession;

public class NonBlockingBinaryFileSession extends BaseBinaryFileSession implements NonBlockingSession {

    private EventQueue _queue = new ConcLinkedEventQueueSingle();

    public NonBlockingBinaryFileSession( String name,
                                         EventRouter inboundRouter,
                                         FileSessionConfig config,
                                         MultiSessionDispatcher dispatcher,
                                         MultiSessionReceiver receiver,
                                         Encoder encoder,
                                         Decoder decoder,
                                         Decoder fullDecoder ) {

        super( name, inboundRouter, config, dispatcher, encoder, decoder, fullDecoder );

        attachReceiver( receiver );
        receiver.addSession( this );
    }

    @Override public boolean isMsgPendingWrite() { return false; }

    @Override public void retryCompleteWrite()   { /* nothing */ }

    @Override
    public EventQueue getSendQueue() {
        return _queue;
    }

    @Override
    public EventQueue getSendSyncQueue() {
        return _queue;
    }

    @Override
    public void logOutboundEncodingError( RuntimeEncodingException e ) {
        _logOutErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() ).append( ":: " );
        _log.error( ERR_OUT_MSG, _logOutErrMsg, e );
    }
}
