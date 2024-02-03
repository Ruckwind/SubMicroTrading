/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.tools;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.fastfix.FastFixDecoder;
import com.rr.core.codec.binary.fastfix.FastFixEncoder;
import com.rr.core.lang.ZString;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionDispatcher;
import com.rr.core.session.MultiSessionReceiver;
import com.rr.core.session.file.FileSessionConfig;
import com.rr.core.session.file.NonBlockingBinaryFileSession;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;

public class CMEFastFixFileSession extends NonBlockingBinaryFileSession {

    public CMEFastFixFileSession( String name,
                                  EventRouter inboundRouter,
                                  FileSessionConfig config,
                                  MultiSessionDispatcher dispatcher,
                                  MultiSessionReceiver receiver,
                                  Encoder encoder,
                                  Decoder decoder,
                                  Decoder fullDecoder ) {

        super( name, inboundRouter, config, dispatcher, receiver, encoder, decoder, fullDecoder );
    }

    @Override
    public void logInboundDecodingError( RuntimeDecodingException e ) {
        logInboundError( e );
    }

    @Override
    public void logInboundError( Exception e ) {
        _logInErrMsg.copy( getComponentId() ).append( " lastSeqNum=" ).append( ((CMEFastFixDecoder) _decoder).getLastSeqNum() );
        _logInErrMsg.append( ' ' ).append( e.getMessage() );
        _log.error( ERR_IN_MSG, _logInErrMsg, e );
        ((FastFixDecoder) _decoder).logLastMsg();
    }

    @Override
    protected final void logInEvent( ZString event ) {
        if ( _logEvents ) {
            ((FastFixDecoder) _decoder).logLastMsg();
        }
    }

    @Override
    protected final void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            ((FastFixEncoder) _encoder).logLastMsg();
        }
    }
}
