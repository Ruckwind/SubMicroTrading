/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.session.EventRouter;

public class TextFileSession extends BaseTextFileSession {

    public TextFileSession( String id,
                            EventRouter inboundRouter,
                            FileSessionConfig config,
                            Decoder decoder ) {

        super( id, inboundRouter, config, decoder );
    }

    @Override
    public void logOutboundEncodingError( RuntimeEncodingException e ) {
        _logOutErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() ).append( ":: " );
        _log.error( ERR_OUT_MSG, _logOutErrMsg, e );
    }
}
