/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.codec.emea.exchange.eti.ETIDecodeContext;
import com.rr.core.codec.RejectDecodeException;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.core.session.socket.SessionStateException;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.ETIRetransmitOrderEvents;
import com.rr.model.generated.internal.events.interfaces.ETIRetransmitOrderEventsResponse;
import com.rr.model.generated.internal.events.interfaces.TestRequest;
import com.rr.om.session.state.SessionState;

public class ETISyncState implements SessionState {

    private static final Logger _log = LoggerFactory.create( ETISyncState.class );

    private static final ErrorCode ERR_NO_SEQ_NUM = new ErrorCode( "ETI200", "Non decodable message, cant find seqNum, assume its nextExpected" );

    private final ETISession    _session;
    private final ETIController _controller;

    private final ReusableString _logMsg = new ReusableString( 100 );
    private final ReusableString _logMsgBase;

    private final ApplMsgID _tmpApplMsgId = new ApplMsgID();

    public ETISyncState( ETISession session, ETIController sessionController ) {
        _session    = session;
        _controller = sessionController;
        _logMsgBase = new ReusableString( "[ETILoggedIn-" + _session.getComponentId() + "] " );
    }

    @Override
    public void connected() {
        _log.warn( "Unexpected connected event when already loggedIn" );
    }

    @Override
    public final void handle( Event msg ) throws SessionStateException {

        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERACK:
        case EventIds.ID_TRADENEW:
        case EventIds.ID_CANCELREJECT:
        case EventIds.ID_REJECTED:
        case EventIds.ID_CANCELLED:
        case EventIds.ID_REPLACED:
        case EventIds.ID_DONEFORDAY:
        case EventIds.ID_STOPPED:
        case EventIds.ID_EXPIRED:
        case EventIds.ID_SUSPENDED:
        case EventIds.ID_RESTATED:
        case EventIds.ID_TRADECORRECT:
        case EventIds.ID_TRADECANCEL:
        case EventIds.ID_ORDERSTATUS:
        case EventIds.ID_SESSIONREJECT:
            handleExec( msg );
            return;                          // dont recycle
        case EventIds.ID_NEWORDERSINGLE:
        case EventIds.ID_CANCELREPLACEREQUEST:
        case EventIds.ID_CANCELREQUEST:
            _controller.acceptMessage( msg );
            _session.dispatchInbound( msg );
            return;                          // dont recycle
        case EventIds.ID_HEARTBEAT:
            _controller.setHeartbeatReceived();
            break;
        case EventIds.ID_TESTREQUEST:
            ZString reqId = ((TestRequest) msg).getTestReqID();

            if ( reqId.length() == 0 ) {
                logWarn( "No valid TestReqID on TestRequest message" );
            }

            _controller.enqueueHeartbeat( reqId );
            break;
        case EventIds.ID_SEQUENCERESET:
        case EventIds.ID_RESENDREQUEST:
        case EventIds.ID_LOGON:
        case EventIds.ID_LOGOUT:
            throw new SessionStateException( "Unsupported event type " + msg.getClass().getSimpleName() );
        default:
            switch( msg.getReusableType().getSubId() ) {
            case EventIds.ID_ETISESSIONLOGONRESPONSE:
                throw new SessionStateException( "Already Logged in" );
            case EventIds.ID_ETISESSIONLOGOUTRESPONSE:
            case EventIds.ID_ETISESSIONLOGOUTNOTIFICATION:
                logWarn( "Logout requested" );
                _session.disconnect( false );
                break;
            case EventIds.ID_ETIUSERLOGONREQUEST:
                throw new SessionStateException( "Already Logged in" );
            case EventIds.ID_ETIRETRANSMITORDEREVENTS:
                if ( _controller.isServer() ) {
                    _controller.synthRetransmitResponse( (ETIRetransmitOrderEvents) msg );
                }
                break;
            case EventIds.ID_ETIRETRANSMITORDEREVENTSRESPONSE:
                _controller.checkRetransmitOrderEventsResponse( (ETIRetransmitOrderEventsResponse) msg );
                break;
            default:
                if ( msg.getReusableType() == CoreReusableType.RejectDecodeException ) {
                    if ( msg.getMsgSeqNum() == 0 ) {
                        // unable to decode message avoid perm loop by assuming seqNum is next expected !
                        // CAN CAUSE SYNC ISSUES SO LOG ERROR

                        RejectDecodeException e = (RejectDecodeException) msg;

                        _log.error( ERR_NO_SEQ_NUM, e.getMessage() );
                    }
                }
                handleExec( msg );
                return;
            }
        }
        _session.inboundRecycle( msg );
    }

    private void handleExec( Event msg ) {
        final ETIDecodeContext ctx             = _session.getDecodeContext();
        final int              partitionID     = ctx.getLastPartitionID();
        final ZString          newApplMsgIdNum = ctx.getLastApplMsgID();
        final ApplMsgID        lastAppMsgId    = _controller.getLastApplMsgID( partitionID );

        _tmpApplMsgId.set( newApplMsgIdNum );

        boolean sameId = _tmpApplMsgId.equals( lastAppMsgId );

        lastAppMsgId.set( _tmpApplMsgId );

        msg.setFlag( MsgFlag.PossDupFlag, true );
        msg.setFlag( MsgFlag.PossResend, true );

        _controller.acceptMessage( msg );
        _session.dispatchInbound( msg );

        _controller.processedRecoveredOrderEventMsg( sameId );
    }

    private void logWarn( String msg ) {
        _logMsg.copy( _logMsgBase ).append( msg );
        _log.warn( msg );
    }
}
