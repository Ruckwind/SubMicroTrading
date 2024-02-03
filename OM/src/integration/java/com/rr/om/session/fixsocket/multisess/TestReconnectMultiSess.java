/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.warmup.WarmupMultiFixSocketSession;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TestReconnectMultiSess extends BaseTestCase {

    static final Logger _log = LoggerFactory.create( TestReconnectMultiSess.class );
    private static final int PORT_OFFSET_REC_A = PortOffset.getNext();
    private static final int PORT_OFFSET_REC_B = PortOffset.getNext();
    private static final int PORT_OFFSET_REC_C = PortOffset.getNext();
    private static final int PORT_OFFSET_REC_D = PortOffset.getNext();

    static {
        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TestReconnect.log", 1000000, Level.info );
    }

    private WarmupMultiFixSocketSession _sess;
    private RecoverableSession          _clientSess;
    private RecoverableSession          _omToExSess;
    private RecoverableSession          _omToClientSess;
    private RecoverableSession          _exSess;

    @Test
    public void testDropExchange() throws Exception {
        try {
            configureSessions( "A", false, PORT_OFFSET_REC_A );

            _sess.getDispatcher().start();
            _sess.getReciever().start();  // THIS WILL RUN CONNECT ON REGISTERED SESSIONS

            waitLogonExchange();

            assertTrue( _exSess.isLoggedIn() );

            _log.info( "Force disconnect on exSess" );
            _exSess.disconnect( true );

            waitLogonExchange();
            assertTrue( _exSess.isLoggedIn() );

        } finally {

            try {
                _sess.close();
            } catch( Exception e2 ) {
                _log.warn( "Exception cleaning up " + name.getMethodName() + " : " + e2.getMessage() + " (" + e2.getClass().getSimpleName() + ")" );
            }

            LoggerFactory.flush();
        }
    }

    @Test
    public void testDropOMtoExchange() throws Exception {
        try {
            configureSessions( "B", false, PORT_OFFSET_REC_B );

            _sess.getDispatcher().start();
            _sess.getReciever().start();  // THIS WILL RUN CONNECT ON REGISTERED SESSIONS

            waitLogonExchange();

            assertTrue( _exSess.isLoggedIn() );

            _log.info( "Force disconnect on exSess" );
            _omToExSess.disconnect( true );

            waitLogonExchange();
            assertTrue( _omToExSess.isLoggedIn() );

        } finally {

            try {
                _sess.close();
            } catch( Exception e2 ) {
                _log.warn( "Exception cleaning up " + name.getMethodName() + " : " + e2.getMessage() + " (" + e2.getClass().getSimpleName() + ")" );
            }

            LoggerFactory.flush();
        }
    }

    @Test
    public void testReconnectAll() throws Exception {
        try {
            configureSessions( "C", true, PORT_OFFSET_REC_C );

            _sess.getDispatcher().start();
            _sess.getReciever().start();  // THIS WILL RUN CONNECT ON REGISTERED SESSIONS

            waitLogonExchange();
            waitLogonClients();

            assertTrue( _omToExSess.isLoggedIn() );
            assertTrue( _exSess.isLoggedIn() );
            assertTrue( _clientSess.isLoggedIn() );
            assertTrue( _omToClientSess.isLoggedIn() );

            _exSess.disconnect( true );
            waitLogonExchange();
            assertTrue( _exSess.isLoggedIn() );
            assertTrue( _omToExSess.isLoggedIn() );

            _clientSess.disconnect( true );
            waitLogonClients();
            assertTrue( _clientSess.isLoggedIn() );
            assertTrue( _omToClientSess.isLoggedIn() );

            _clientSess.disconnect( true );
            waitLogonClients();
            assertTrue( _clientSess.isLoggedIn() );
            assertTrue( _omToClientSess.isLoggedIn() );

        } finally {

            try {
                _sess.close();
            } catch( Exception e2 ) {
                _log.warn( "Exception cleaning up " + name.getMethodName() + " : " + e2.getMessage() + " (" + e2.getClass().getSimpleName() + ")" );
            }

            LoggerFactory.flush();
            ThreadUtilsFactory.get().sleep( 1000 );
        }
    }

    public void waitLogonClients() {
        for ( int i = 0; i < 10; i++ ) {
            ThreadUtilsFactory.get().sleep( 1000 );
            synchronized( this ) {
                if ( _omToClientSess.isLoggedIn() && _clientSess.isLoggedIn() ) {
                    break;
                }
            }
        }
    }

    public void waitLogonExchange() {
        for ( int i = 0; i < 10; i++ ) {
            ThreadUtilsFactory.get().sleep( 1000 );
            synchronized( this ) {
                if ( _omToExSess.isLoggedIn() && _exSess.isLoggedIn() ) {
                    break;
                }
            }
        }
    }

    private void configureSessions( String idPostfix, boolean initClient, int portOffset ) throws SessionException, FileException, PersisterException, IOException {
        _sess = WarmupMultiFixSocketSession.create( "testReconnect" + idPostfix, portOffset, false, 1000 );

        _sess.setMaxRunTime( 20000 );
        _sess.setEventLogging( true );
        _sess.init( initClient );
        _sess.recover();

        _clientSess     = _sess.getClientSession();
        _omToExSess     = _sess.getOMtoExSession();
        _omToClientSess = _sess.getOMtoClientSession();
        _exSess         = _sess.getExchangeSession();
    }
}
