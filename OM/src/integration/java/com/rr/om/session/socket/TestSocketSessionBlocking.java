/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.socket;

import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestSocketSessionBlocking extends TestSocketSession {

    private static final Logger _log = LoggerFactory.create( TestSocketSessionBlocking.class );

    private final byte[] _buf1 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
    private final byte[] _buf2 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];

    private AtomicBoolean _clientConnected = new AtomicBoolean();
    private AtomicBoolean _serverConnected = new AtomicBoolean();

    @Test public void testSendBlocking() throws PersisterException {
        for ( int i = 1; i < 10; i++ ) {
            _log.info( "TestSocketSession testSendBlocking idx=" + i );

            doSend( false, 14238, 0 );
        }
    }
}
