/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import com.rr.core.codec.FixDecoder;
import com.rr.core.factories.ReusableStringFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.impl.ClientCancelReplaceRequestImpl;
import com.rr.model.generated.internal.events.impl.ClientCancelRequestImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.dummy.warmup.ClientStatsManager;
import com.rr.core.idgen.DailySimpleIDGenerator;
import com.rr.core.idgen.IDGenerator;
import com.rr.om.session.fixsocket.FixConfig;
import com.rr.sim.client.ClientSimNonBlockingFixSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSimSender implements SimClient {

    private static final Logger _log = LoggerFactory.create( ClientSimSender.class );

    private final List<byte[]>                        _templateRequests;
    private final FixDecoder                          _decoder;
    private final SeqNumSession[]                     _clientSessions;
    private final ClientStatsManager                  _statsMgr;
    private final Map<ReusableString, ReusableString> _keys       = new HashMap<>(); // 1 per template
    private final boolean                             _asyncDispatch;
    private final IDGenerator                         _idGen;
    private final ReusableStringFactory               _reusableStringFactory;
    private final boolean                             _throttleSender;
    private       int                                 _nextClient = 0;
    private volatile AtomicInteger  _sent             = new AtomicInteger( 0 );
    private          CountDownLatch _cdl              = null;
    private          AtomicBoolean  _throttlerCanSend = new AtomicBoolean( true );
    private          int            _expectedReplies;

    public ClientSimSender( List<byte[]> templateRequests, SeqNumSession[] clientSessions, ClientStatsManager statsMgr, String idPrefix ) {
        this( templateRequests, clientSessions, statsMgr, idPrefix, false );
    }

    /**
     * @param templateRequests
     * @param clientSessions
     * @param statsMgr
     * @param idPrefix
     * @param isThrottleSender if true throttles sender so only one message can be queued to send to client
     */
    public ClientSimSender( List<byte[]> templateRequests,
                            SeqNumSession[] clientSessions,
                            ClientStatsManager statsMgr,
                            String idPrefix,
                            boolean isThrottleSender ) {
        super();

        boolean asyncDispatch = clientSessions[ 0 ].getClass() == ClientSimNonBlockingFixSession.class;

        if ( asyncDispatch ) {
            for ( int i = 0; i < clientSessions.length; ++i ) {
                ClientSimNonBlockingFixSession simSess = (ClientSimNonBlockingFixSession) clientSessions[ i ];

                simSess.setListener( ( msg, sent ) -> {
                    switch( msg.getReusableType().getSubId() ) {
                    case EventIds.ID_NEWORDERSINGLE:
                        ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) msg;
                        recordSent( nos.getClOrdId(), sent );
                        break;
                    case EventIds.ID_CANCELREPLACEREQUEST:
                        ClientCancelReplaceRequestImpl rep = (ClientCancelReplaceRequestImpl) msg;
                        recordSent( rep.getClOrdId(), sent );
                        break;
                    case EventIds.ID_CANCELREQUEST:
                        ClientCancelRequestImpl can = (ClientCancelRequestImpl) msg;
                        recordSent( can.getClOrdId(), sent );
                        break;
                    }

                    throttleSender();
                } );
            }
        }

        _asyncDispatch    = asyncDispatch;
        _templateRequests = templateRequests;
        FixConfig config = (FixConfig) clientSessions[ 0 ].getStateConfig();
        _decoder = WarmupUtils.getFixOMSDecoder( config.getFixVersion() );

        _clientSessions = clientSessions;
        _statsMgr       = statsMgr;
        _idGen          = new DailySimpleIDGenerator( new ViewString( idPrefix ) );

        _throttleSender        = isThrottleSender;
        _reusableStringFactory = SuperpoolManager.instance().getFactory( ReusableStringFactory.class, ReusableString.class );
    }

    @Override
    public void dispatchEvents( int numOrders, int batchSize, int delayMicros ) {

        _expectedReplies = numOrders;

        int waitIdx = 0;
        for ( int i = 0; i < _clientSessions.length; i++ ) {
            RecoverableSession client = _clientSessions[ i ];
            while( !client.isLoggedIn() && ++waitIdx < 10 ) {
                ThreadUtilsFactory.get().sleep( 100 );
            }
        }

        _log.info( "ClientSimSender about to start sending " + numOrders + ", asyncMode=" + ((_asyncDispatch) ? "TRUE" : "FALSE") +
                   ", isThrottle=" + _throttleSender );

        for ( int idx = 0; idx < numOrders; ++idx ) {
            throttle();
            sendNext( idx );

            if ( delayMicros > 0 && (batchSize <= 1 || idx % batchSize == 0) ) {
                ThreadUtilsFactory.get().sleepMicros( delayMicros );
            }

            if ( idx % 10000 == 0 ) {
                _log.info( "Sent " + idx );
            }
        }

        ThreadUtilsFactory.get().sleep( 100 );

        _log.info( "ClientSimSender Sent " + _sent );
    }

    @Override public int getExpectedReplies() {
        return _expectedReplies;
    }

    @Override
    public int getSent() {
        return _sent.get();
    }

    public void setSent( int sent ) {
        _sent.set( sent );
    }

    @Override
    public void reset() {
        _sent.set( 0 );
    }

    public final void recordSent( ViewString clOrdId, long lastSent ) {
        //noinspection NonAtomicOperationOnVolatileField
        _sent.incrementAndGet();
        _statsMgr.sent( clOrdId, lastSent );
        if ( _cdl != null ) _cdl.countDown();
    }

    public void sendNext( int idx ) {
        byte[] template = _templateRequests.get( idx % _templateRequests.size() );

        Event msg = WarmupUtils.doDecode( _decoder, template, 0, template.length );

        msg.setMsgSeqNum( 0 );

        SeqNumSession client = _clientSessions[ _nextClient ];
        if ( ++_nextClient >= _clientSessions.length ) {
            _nextClient = 0;
        }

        msg.setEventHandler( client );

        ReusableString keyCopy = _reusableStringFactory.get();

        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) msg;
            regen( nos.getClOrdId(), idx );
            keyCopy.copy( nos.getClOrdId() );
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            ClientCancelReplaceRequestImpl rep = (ClientCancelReplaceRequestImpl) msg;
            updateOrigId( rep.getOrigClOrdIdForUpdate() );
            regen( rep.getClOrdIdForUpdate(), idx );
            keyCopy.copy( rep.getClOrdId() );
            break;
        case EventIds.ID_CANCELREQUEST:
            ClientCancelRequestImpl can = (ClientCancelRequestImpl) msg;
            updateOrigId( can.getOrigClOrdIdForUpdate() );
            regen( can.getClOrdIdForUpdate(), idx );
            keyCopy.copy( can.getClOrdId() );
            break;
        }

        _statsMgr.register( keyCopy );

        client.handle( msg );

        if ( _asyncDispatch == false ) {
            recordSent( keyCopy, client.getLastSent() );
        } else {
            // ASYNC callback
        }
    }

    public void setCountDownLatch( CountDownLatch cdl ) {
        _cdl = cdl;
    }

    void throttleSender() {
        if ( _throttleSender ) {
            _throttlerCanSend.set( true );
        }
    }

    private void copy( ReusableString curKey, ViewString base ) {
        int len = curKey.length();

        if ( len > base.length() ) throw new SMTRuntimeException( "Base key too short, increase key size in template : " + base + ", needLen=" + len );

        System.arraycopy( curKey.getBytes(), 0, base.getBytes(), base.getOffset(), len );

        base.setLength( len );
    }

    private void regen( ViewString base, int idx ) {
        ReusableString curKey = _keys.get( base );

        if ( curKey == null ) {
            curKey = _reusableStringFactory.get();

            _keys.put( _reusableStringFactory.get().copy( base ), curKey );
        }

        _idGen.genID( curKey );

        copy( curKey, base );
    }

    private void throttle() {
        // TODO Auto-generated method stub

        if ( _throttleSender ) {
            while( _throttlerCanSend.compareAndSet( true, false ) == false ) {
                // spin
            }
        }
    }

    private void updateOrigId( ReusableString origKey ) {
        ReusableString orig = _keys.get( origKey );

        origKey.copy( orig );
    }
}
