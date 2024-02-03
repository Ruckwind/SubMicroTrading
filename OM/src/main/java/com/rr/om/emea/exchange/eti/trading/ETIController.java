/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.codec.emea.exchange.eti.ETIDecodeContext;
import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.model.generated.internal.events.impl.ETIRetransmitOrderEventsResponseImpl;
import com.rr.model.generated.internal.events.interfaces.ETIRetransmitOrderEvents;
import com.rr.model.generated.internal.events.interfaces.ETIRetransmitOrderEventsResponse;
import com.rr.model.generated.internal.type.ETIEurexDataStream;
import com.rr.om.session.state.SessionSeqNumController;
import com.rr.om.session.state.SessionStateFactory;
import com.rr.om.session.state.StatefulSessionFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * based on native trading gateway issue 7.0 Dec 2010
 */

public class ETIController extends SessionSeqNumController {

    protected static final int   MAX_SEQ_NUM      = 256;
    private static final Logger _log = LoggerFactory.create( ETIController.class );
    protected final        int[] _lastAppIdSeqNum = new int[ MAX_SEQ_NUM ];

    private int _sessionInstanceId = 0;

    private IntMap<ApplMsgID> _partitionLastApplMsgId = new IntHashMap<>( 256, 0.75f );

    // recovery fields
    private AtomicInteger _pendingResponses = new AtomicInteger( 0 );
    private List<Integer> _recoveryKeyList;
    private int           _nextRecoveryKeyIndex;

    private short _curPartitionRecovery;

    public ETIController( SeqNumSession session, ETISocketConfig config ) {
        super( session, new ETIStateFactory( config ), new ETISessionFactory( config ) );
    }

    protected ETIController( SeqNumSession session, SessionStateFactory stateFactory, StatefulSessionFactory msgFactory ) {
        super( session, stateFactory, msgFactory ); // only invoked from the recovery controller
    }

    @Override
    public final void reset() {
        super.reset();

        _recoveryKeyList      = null;
        _nextRecoveryKeyIndex = 0;
        _partitionLastApplMsgId.clear();
    }

    @Override
    public final void recoverContext( Event msg, boolean inBound ) {
        // not used, the context appId requires special override
    }

    @Override
    public final void outboundError() {
        // nothing
    }

    @Override
    protected final void onLogout() {
        super.onLogout();

        // reset the sync nums

        resetSeqNums();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void checkRetransmitOrderEventsResponse( ETIRetransmitOrderEventsResponse msg ) {

        int pendingReplies = msg.getApplTotalMessageCount();

        if ( pendingReplies <= 0 ) {
            _log.info( "Partition has no further recovery messages, partitionId=" + _curPartitionRecovery + ", check next partitionId" );
            ++_nextRecoveryKeyIndex;
            sendNextRecoveryRequest();
        } else {
            _log.info( "Partition partitionId=" + _curPartitionRecovery + " has " + pendingReplies + " recovery messages" );
            _pendingResponses.set( pendingReplies );
        }
    }

    public ApplMsgID getLastApplMsgID( int partitionID ) {
        ApplMsgID id = _partitionLastApplMsgId.get( partitionID );

        if ( id == null ) {
            id = new ApplMsgID();
            _partitionLastApplMsgId.put( partitionID, id );
        }

        return id;
    }

    /**
     * next recovery event from ETI has been processed
     * <p>
     * note multiple events with same applMsgId dont count towards the expected message count
     *
     * @param sameId
     */
    public void processedRecoveredOrderEventMsg( boolean sameId ) {
        if ( sameId ) {
            _log.info( "Current ETI event has same id as previous event" );
        } else if ( _pendingResponses.decrementAndGet() <= 0 ) {
            _log.info( "Processed all recovery replies for batch with partitionId=" + _curPartitionRecovery + ", check for more" );
            sendNextRecoveryRequest();
        }
    }

    public final void recoverContext( Event msg, boolean inBound, ETIDecodeContext ctx ) {

        if ( inBound ) {
            storeMaxSeqNum( msg, ctx );
        }
    }

    public void resetSeqNums() {
        setInSeqNumForSycn( 0 );
        setNextExpectedInSeqNum( 1 );
        setNextOutSeqNum( 1 );
    }

    public void sendLogonSyncMsgs() {

        _recoveryKeyList = new LinkedList<>( _partitionLastApplMsgId.keys() );

        _pendingResponses.set( 0 );

        _nextRecoveryKeyIndex = 0;

        sendNextRecoveryRequest();
    }

    public final void sendSessionLogonNow() {
        Event logon = _sessionFactory.getSessionLogOn( 0, 0, 0 );
        send( logon, true );
    }

    public void sendUserLogonRequest() {
        Event logon = ((ETISessionFactory) _sessionFactory).getUserLogOn();
        send( logon, true );
    }

    public void setSessionInstanceID( int sessionInstanceID ) {
        _sessionInstanceId = sessionInstanceID;

        _log.info( "Setting session " + _session.getComponentId() + " to sessionInstanceId " + _sessionInstanceId );
    }

    public void setThrottle( int throttleNoMsgs, long throttleTimeIntervalMS ) {
        _session.setThrottle( throttleNoMsgs, throttleNoMsgs, throttleTimeIntervalMS );
    }

    public void storeMaxSeqNum( Event msg, ETIDecodeContext ctx ) {
        if ( ctx.hasValue() ) {
            ApplMsgID curMax = getLastApplMsgID( ctx.getLastPartitionID() );

            curMax.setIfGreater( ctx.getLastApplMsgID() );
        }
    }

    // checkMaxSeqNums is used to integrate the trading session max seq nums
    // with the recovery session seqNums ... really belongs in the recoveryController
    protected final void checkMaxSeqNums( int[] lastAppIdSeqNum ) {
        for ( int i = 0; i < MAX_SEQ_NUM; i++ ) {
            if ( lastAppIdSeqNum[ i ] > _lastAppIdSeqNum[ i ] ) {
                _lastAppIdSeqNum[ i ] = lastAppIdSeqNum[ i ];
            }
        }
    }

    void acceptMessage( Event msg ) {
        final int newSeqNum          = msg.getMsgSeqNum();
        final int nextExpectedSeqNum = getNextExpectedInSeqNo();

        if ( newSeqNum == 0 ) return;

        if ( newSeqNum == nextExpectedSeqNum ) {
            persistInMsgAndUpdateSeqNum( newSeqNum + 1 );
        } else if ( newSeqNum < nextExpectedSeqNum ) {

            if ( msg.isFlagSet( MsgFlag.PossDupFlag ) == false )
                _log.info( "Accepting out of lower sequence number mismatch. Expecting=" + nextExpectedSeqNum + ", Received=" + newSeqNum );

            // pos dup dont DECREASE the seqnum

            persistPosDupMsg( newSeqNum );

        } else { // missing messages
            _log.info( "Accepting out of higher sequence number mismatch. Expecting=" + nextExpectedSeqNum + ", Received=" + newSeqNum );

            // dont disconnect, ETI has lots of messages and this could simply be due to a message not in the model ... for now just warn
            persistInMsgAndUpdateSeqNum( newSeqNum + 1 );
        }
    }

    /**
     * Server Emulation Methods
     */
    final void sendSessionLogonReplyNow( ZString msg, int rejectCode ) {
        Event logon = ((ETISessionFactory) _sessionFactory).getSessionLogonReply();
        send( logon, true );
    }

    void sendUserLogonReplyNow() {
        Event msg = ((ETISessionFactory) _sessionFactory).getUserLogOnReply();
        send( msg, true );
    }

    void synthRetransmitResponse( ETIRetransmitOrderEvents msg ) {
        ETIRetransmitOrderEventsResponseImpl response = new ETIRetransmitOrderEventsResponseImpl();
        response.setApplTotalMessageCount( (short) 0 );
        send( response, false );
    }

    private void requestGapFill( ETIEurexDataStream stream, short partitionId, ApplMsgID applMsgID ) {
        ETIRetransmitOrderEvents req = ((ETISessionFactory) _sessionFactory).createRetransmitOrderEventsRequest( stream, partitionId, applMsgID );

        _log.info( "Request recoveryOrderEvents partitionId=" + partitionId + ", lastApplMsgId=" + applMsgID.toString() );

        send( req, false );
    }

    private void sendNextRecoveryRequest() {

        if ( _nextRecoveryKeyIndex < _recoveryKeyList.size() ) {
            short partitionId = (short) _recoveryKeyList.get( _nextRecoveryKeyIndex ).intValue();

            _curPartitionRecovery = partitionId;

            requestGapFill( ETIEurexDataStream.SessionData, partitionId, _partitionLastApplMsgId.get( partitionId ) );

        } else {
            _log.info( "All partitionIds are fully processed, partitionCnt=" + _nextRecoveryKeyIndex + ", lastPartitionId=" + _curPartitionRecovery );
            changeState( getStateLoggedIn() );
        }
    }
}