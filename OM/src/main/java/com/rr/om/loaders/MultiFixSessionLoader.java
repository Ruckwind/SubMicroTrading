/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.component.SMTComponent;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.hub.AsyncLogSession;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.NonBlockingFixSocketSession;

public class MultiFixSessionLoader extends BaseSessionLoader {

    public static final int THREAD_THROTTLE_MS = 1;
    public static final int THROTTLE_BATCH     = 1000;
    private static final Logger _log = LoggerFactory.create( MultiFixSessionLoader.class );
    private FixSocketConfig                _sessionConfig;
    private ClientProfile                  _clientProfile;
    private EventRouter                    _inboundRouter;
    private MultiSessionThreadedDispatcher _outboundDispatcher;
    private MultiSessionThreadedReceiver   _inboundDispatcher;
    private EventQueue                     _queue;
    private boolean                        _defaultToFullDecoder = true;
    private boolean                        _useDummySession      = false;
    private boolean                        _forceSlowMode        = true;
    private Session                        _hubSession;

    @Override
    public SMTComponent create( String id ) {

        try {
            String sessName = id;

            if ( _useDummySession ) {
                _log.info( "Forcing session " + id + " to be a dummy async log session, slowQ=" + _forceSlowMode );
                return new AsyncLogSession( id, _forceSlowMode );
            }

            _sessionConfig.setFixVersion( _codecId.getFixVersion() );
            _sessionConfig.setCodecId( _codecId );

            _sessionConfig.validate();

            int        logHdrOut   = AbstractSession.getDataOffset( id, false );
            byte[]     outBuf      = createCodecBuffer( _sessionConfig.getCodecBufferSize() );
            FixEncoder encoder     = (FixEncoder) getEncoder( _codecId, outBuf, logHdrOut, _trace );
            FixDecoder omsDecoder  = (FixDecoder) getOMSDecoder( _codecId, _clientProfile, _trace );
            FixDecoder fullDecoder = (FixDecoder) getFullDecoder( _codecId, _clientProfile, _trace );

            omsDecoder.setValidateChecksum( true );
            fullDecoder.setValidateChecksum( true );

            _sessionConfig.setInboundPersister( createInboundPersister( sessName + "_" + _sessionDirection + "_IN" ) );
            _sessionConfig.setOutboundPersister( createOutboundPersister( sessName + "_" + _sessionDirection + "_OUT" ) );

            NonBlockingFixSocketSession sess;

            if ( _defaultToFullDecoder ) {
                _log.info( "MultiFixSessionLoader default to FULL decoder for " + id );
                omsDecoder = fullDecoder;
            }

            sess = createNonBlockingFixSession( _sessionConfig, _inboundRouter, encoder, omsDecoder, fullDecoder, sessName, _outboundDispatcher, _inboundDispatcher, _queue );

            sess.setChainSession( _hubSession );
            _outboundDispatcher.addSession( sess );

            sess.setLogStats( _logStats );
            sess.setLogEvents( _logEvents );
            sess.setLogPojos( _logPojoEvents );

            if ( _disableNanoStats ) {
                encoder.setNanoStats( false );
                omsDecoder.setNanoStats( false );
                sess.setLogStats( false );
            } else {
                encoder.setNanoStats( true );
                omsDecoder.setNanoStats( true );
            }

            postSessionCreate( encoder, sess );

            return sess;
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to create MultiFixSession id=" + id, e );
        }
    }

    protected NonBlockingFixSocketSession createNonBlockingFixSession( FixSocketConfig socketConfig,
                                                                       EventRouter inboundRouter,
                                                                       FixEncoder encoder,
                                                                       FixDecoder decoder,
                                                                       FixDecoder fullDecoder,
                                                                       String name,
                                                                       MultiSessionThreadedDispatcher dispatcher,
                                                                       MultiSessionThreadedReceiver receiver,
                                                                       EventQueue dispatchQueue ) throws SessionException, PersisterException {

        NonBlockingFixSocketSession sess = new NonBlockingFixSocketSession( name,
                                                                            inboundRouter,
                                                                            socketConfig,
                                                                            dispatcher,
                                                                            receiver,
                                                                            encoder,
                                                                            decoder,
                                                                            fullDecoder,
                                                                            dispatchQueue );
        return sess;
    }
}
