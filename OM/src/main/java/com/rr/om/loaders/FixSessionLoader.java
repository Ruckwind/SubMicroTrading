/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.component.SMTComponent;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.AbstractSession;
import com.rr.core.session.EventRouter;
import com.rr.core.session.Session;
import com.rr.core.session.SessionException;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.hub.AsyncLogSession;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.FixSocketSession;

public class FixSessionLoader extends BaseSessionLoader {

    public static final int THREAD_THROTTLE_MS = 1;
    public static final int THROTTLE_BATCH     = 1000;
    private static final Logger _log = LoggerFactory.create( FixSessionLoader.class );
    private FixSocketConfig _sessionConfig;
    private ClientProfile   _clientProfile;
    private EventRouter     _inboundRouter;
    private EventDispatcher _outboundDispatcher;
    private boolean         _defaultToFullDecoder = true;
    private boolean         _useDummySession      = false;
    private boolean         _forceSlowMode        = true;
    private Session         _hubSession;
    private ThreadPriority  _receiverPriority     = ThreadPriority.Other;

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

            FixSocketSession sess;

            if ( _defaultToFullDecoder ) {
                _log.info( "FixSessionLoader default to FULL decoder for " + id );
                omsDecoder = fullDecoder;
            }

            sess = createFixSession( _sessionConfig, _inboundRouter, encoder, omsDecoder, fullDecoder, sessName, _outboundDispatcher, _receiverPriority );

            sess.setChainSession( _hubSession );
            _outboundDispatcher.setHandler( sess );

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
            throw new SMTRuntimeException( "Unable to create FixSession id=" + id, e );
        }
    }

    protected FixSocketSession createFixSession( FixSocketConfig socketConfig,
                                                 EventRouter inboundRouter,
                                                 FixEncoder encoder,
                                                 FixDecoder decoder,
                                                 FixDecoder fullDecoder,
                                                 String name,
                                                 EventDispatcher dispatcher,
                                                 ThreadPriority receivePriority ) throws SessionException, PersisterException {

        FixSocketSession sess = new FixSocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receivePriority );

        return sess;
    }
}
