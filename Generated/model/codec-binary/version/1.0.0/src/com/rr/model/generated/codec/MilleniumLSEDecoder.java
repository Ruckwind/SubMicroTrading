package com.rr.model.generated.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import java.util.HashMap;
import java.util.Map;
import com.rr.core.codec.*;
import com.rr.core.utils.*;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.factories.*;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.internal.type.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.BinaryDecodeBuilder;
import com.rr.core.codec.binary.DebugBinaryDecodeBuilder;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;

@SuppressWarnings( "unused" )

public final class MilleniumLSEDecoder extends AbstractBinaryDecoder {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs
    private static final byte      MSG_Logon = (byte)'A';
    private static final byte      MSG_LogonReply = (byte)'B';
    private static final byte      MSG_Logout = (byte)'5';
    private static final byte      MSG_MissedMessageRequest = (byte)'M';
    private static final byte      MSG_MissedMsgRequestAck = (byte)'N';
    private static final byte      MSG_MissedMsgReport = (byte)'P';
    private static final byte      MSG_Heartbeat = (byte)'0';
    private static final byte      MSG_NewOrder = (byte)'D';
    private static final byte      MSG_OrderCancelRequest = (byte)'F';
    private static final byte      MSG_CancelReject = (byte)'9';
    private static final byte      MSG_Reject = (byte)'3';
    private static final byte      MSG_BusinessReject = (byte)'j';
    private static final byte      MSG_OrderReplaceRequest = (byte)'G';
    private static final byte      MSG_ExecutionReport = (byte)'8';

    private boolean _debug = false;

    private BinaryDecodeBuilder _builder;

    private       byte _msgType;
    private final byte                        _protocolVersion;
    private final String                      _id;
    private       int                         _msgStatedLen;
    private final ReusableString _dump  = new ReusableString(256);
    private final ReusableString _missedMsgTypes = new ReusableString();

    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks
    private       ReusableString              _userName = new ReusableString(30);
    private       ReusableString              _password = new ReusableString(30);
    private       ReusableString              _newPassword = new ReusableString(30);
    private       byte                        _msgVersion;
    private       int                         _rejectCode;
    private       ReusableString              _pwdExpiryDayCount = new ReusableString(30);
    private       ReusableString              _reason = new ReusableString(30);
    private       byte                        _appId;
    private       int                         _lastMsgSeqNum;
    private       byte                        _missedMsgReqAckType;
    private       byte                        _missedMsgReportType;
    private       ReusableString              _clOrdId = new ReusableString(30);
    private       ReusableString              _account = new ReusableString(30);
    private       byte                        _clearingAccount;
    private       int                         _instrumentId;
    private       byte                        _mesQualifier;
    private       byte                        _ordType;
    private       byte                        _timeInForce;
    private       int                         _expireDateTime;
    private       byte                        _side;
    private       int                         _orderQty;
    private       int                         _displayQty;
    private       double                      _price;
    private       byte                        _orderCapacity;
    private       byte                        _autoCancel;
    private       byte                        _ordSubType;
    private       byte                        _anonymity;
    private       double                      _stopPrice;
    private       ReusableString              _origClOrdId = new ReusableString(30);
    private       ReusableString              _orderId = new ReusableString(30);
    private       int                         _msgSeqNum;
    private       long                        _sendingTime;
    private       ReusableString              _rejectReason = new ReusableString(30);
    private       byte                        _rejectMsgType;
    private       ReusableString              _execId = new ReusableString(30);
    private       byte                        _execType;
    private       ReusableString              _execRefID = new ReusableString(30);
    private       byte                        _ordStatus;
    private       double                      _lastPx;
    private       int                         _lastQty;
    private       int                         _leavesQty;
    private       byte                        _container;
    private       ReusableString              _counterparty = new ReusableString(30);
    private       byte                        _liquidityInd;
    private       long                        _tradeMatchId;

   // Pools

    private final SuperPool<MilleniumLogonImpl> _milleniumLogonPool = SuperpoolManager.instance().getSuperPool( MilleniumLogonImpl.class );
    private final MilleniumLogonFactory _milleniumLogonFactory = new MilleniumLogonFactory( _milleniumLogonPool );

    private final SuperPool<MilleniumLogonReplyImpl> _milleniumLogonReplyPool = SuperpoolManager.instance().getSuperPool( MilleniumLogonReplyImpl.class );
    private final MilleniumLogonReplyFactory _milleniumLogonReplyFactory = new MilleniumLogonReplyFactory( _milleniumLogonReplyPool );

    private final SuperPool<MilleniumLogoutImpl> _milleniumLogoutPool = SuperpoolManager.instance().getSuperPool( MilleniumLogoutImpl.class );
    private final MilleniumLogoutFactory _milleniumLogoutFactory = new MilleniumLogoutFactory( _milleniumLogoutPool );

    private final SuperPool<MilleniumMissedMessageRequestImpl> _milleniumMissedMessageRequestPool = SuperpoolManager.instance().getSuperPool( MilleniumMissedMessageRequestImpl.class );
    private final MilleniumMissedMessageRequestFactory _milleniumMissedMessageRequestFactory = new MilleniumMissedMessageRequestFactory( _milleniumMissedMessageRequestPool );

    private final SuperPool<MilleniumMissedMsgRequestAckImpl> _milleniumMissedMsgRequestAckPool = SuperpoolManager.instance().getSuperPool( MilleniumMissedMsgRequestAckImpl.class );
    private final MilleniumMissedMsgRequestAckFactory _milleniumMissedMsgRequestAckFactory = new MilleniumMissedMsgRequestAckFactory( _milleniumMissedMsgRequestAckPool );

    private final SuperPool<MilleniumMissedMsgReportImpl> _milleniumMissedMsgReportPool = SuperpoolManager.instance().getSuperPool( MilleniumMissedMsgReportImpl.class );
    private final MilleniumMissedMsgReportFactory _milleniumMissedMsgReportFactory = new MilleniumMissedMsgReportFactory( _milleniumMissedMsgReportPool );

    private final SuperPool<HeartbeatImpl> _heartbeatPool = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final SuperPool<NewOrderSingleImpl> _newOrderSinglePool = SuperpoolManager.instance().getSuperPool( NewOrderSingleImpl.class );
    private final NewOrderSingleFactory _newOrderSingleFactory = new NewOrderSingleFactory( _newOrderSinglePool );

    private final SuperPool<CancelRequestImpl> _cancelRequestPool = SuperpoolManager.instance().getSuperPool( CancelRequestImpl.class );
    private final CancelRequestFactory _cancelRequestFactory = new CancelRequestFactory( _cancelRequestPool );

    private final SuperPool<CancelRejectImpl> _cancelRejectPool = SuperpoolManager.instance().getSuperPool( CancelRejectImpl.class );
    private final CancelRejectFactory _cancelRejectFactory = new CancelRejectFactory( _cancelRejectPool );

    private final SuperPool<SessionRejectImpl> _sessionRejectPool = SuperpoolManager.instance().getSuperPool( SessionRejectImpl.class );
    private final SessionRejectFactory _sessionRejectFactory = new SessionRejectFactory( _sessionRejectPool );

    private final SuperPool<VagueOrderRejectImpl> _vagueOrderRejectPool = SuperpoolManager.instance().getSuperPool( VagueOrderRejectImpl.class );
    private final VagueOrderRejectFactory _vagueOrderRejectFactory = new VagueOrderRejectFactory( _vagueOrderRejectPool );

    private final SuperPool<CancelReplaceRequestImpl> _cancelReplaceRequestPool = SuperpoolManager.instance().getSuperPool( CancelReplaceRequestImpl.class );
    private final CancelReplaceRequestFactory _cancelReplaceRequestFactory = new CancelReplaceRequestFactory( _cancelReplaceRequestPool );

    private final SuperPool<NewOrderAckImpl> _newOrderAckPool = SuperpoolManager.instance().getSuperPool( NewOrderAckImpl.class );
    private final NewOrderAckFactory _newOrderAckFactory = new NewOrderAckFactory( _newOrderAckPool );

    private final SuperPool<CancelledImpl> _cancelledPool = SuperpoolManager.instance().getSuperPool( CancelledImpl.class );
    private final CancelledFactory _cancelledFactory = new CancelledFactory( _cancelledPool );

    private final SuperPool<ReplacedImpl> _replacedPool = SuperpoolManager.instance().getSuperPool( ReplacedImpl.class );
    private final ReplacedFactory _replacedFactory = new ReplacedFactory( _replacedPool );

    private final SuperPool<RejectedImpl> _rejectedPool = SuperpoolManager.instance().getSuperPool( RejectedImpl.class );
    private final RejectedFactory _rejectedFactory = new RejectedFactory( _rejectedPool );

    private final SuperPool<ExpiredImpl> _expiredPool = SuperpoolManager.instance().getSuperPool( ExpiredImpl.class );
    private final ExpiredFactory _expiredFactory = new ExpiredFactory( _expiredPool );

    private final SuperPool<RestatedImpl> _restatedPool = SuperpoolManager.instance().getSuperPool( RestatedImpl.class );
    private final RestatedFactory _restatedFactory = new RestatedFactory( _restatedPool );

    private final SuperPool<TradeNewImpl> _tradeNewPool = SuperpoolManager.instance().getSuperPool( TradeNewImpl.class );
    private final TradeNewFactory _tradeNewFactory = new TradeNewFactory( _tradeNewPool );

    private final SuperPool<TradeCorrectImpl> _tradeCorrectPool = SuperpoolManager.instance().getSuperPool( TradeCorrectImpl.class );
    private final TradeCorrectFactory _tradeCorrectFactory = new TradeCorrectFactory( _tradeCorrectPool );

    private final SuperPool<TradeCancelImpl> _tradeCancelPool = SuperpoolManager.instance().getSuperPool( TradeCancelImpl.class );
    private final TradeCancelFactory _tradeCancelFactory = new TradeCancelFactory( _tradeCancelPool );

    private final SuperPool<SuspendedImpl> _suspendedPool = SuperpoolManager.instance().getSuperPool( SuspendedImpl.class );
    private final SuspendedFactory _suspendedFactory = new SuspendedFactory( _suspendedPool );


   // Constructors
    public MilleniumLSEDecoder() { this( null ); }
    public MilleniumLSEDecoder( String id ) {
        super();
        setBuilder();
        _id = id;
        _protocolVersion = (byte)'2';
    }

   // decode methods
    @Override
    protected final int getCurrentIndex() {
        return _builder.getCurrentIndex();
    }

    @Override
    protected BinaryDecodeBuilder getBuilder() {
        return _builder;
    }

    @Override
    public boolean isDebug() {
        return _debug;
    }

    @Override
    public void setDebug( boolean isDebugOn ) {
        _debug = isDebugOn;
        setBuilder();
    }

    private void setBuilder() {
        _builder = (_debug) ? new DebugBinaryDecodeBuilder<>( _dump, new com.rr.codec.emea.exchange.millenium.MilleniumDecodeBuilderImpl() )
                            : new com.rr.codec.emea.exchange.millenium.MilleniumDecodeBuilderImpl();
    }

    @Override
    protected final Event doMessageDecode() {
        _builder.setMaxIdx( _maxIdx );

        switch( _msgType ) {
        case MSG_Logon:
            return decodeLogon();
        case MSG_LogonReply:
            return decodeLogonReply();
        case MSG_Logout:
            return decodeLogout();
        case MSG_MissedMessageRequest:
            return decodeMissedMessageRequest();
        case MSG_MissedMsgRequestAck:
            return decodeMissedMsgRequestAck();
        case MSG_MissedMsgReport:
            return decodeMissedMsgReport();
        case MSG_Heartbeat:
            return decodeHeartbeat();
        case MSG_NewOrder:
            return decodeNewOrder();
        case MSG_OrderCancelRequest:
            return decodeOrderCancelRequest();
        case MSG_CancelReject:
            return decodeCancelReject();
        case MSG_Reject:
            return decodeReject();
        case MSG_BusinessReject:
            return decodeBusinessReject();
        case MSG_OrderReplaceRequest:
            return decodeOrderReplaceRequest();
        case MSG_ExecutionReport:
            return decodeExecutionReport();
        case '1':
        case '2':
        case '4':
        case '6':
        case '7':
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'C':
        case 'E':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'O':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '_':
        case '`':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
            break;
        }
        if ( _debug ) {
            _tmpLookupKey.copy( '|' ).append( _msgType ).append( '|' );
            if ( ! _missedMsgTypes.contains( _tmpLookupKey ) ) {
                _dump.append( "Skipped Unsupported Message : " ).append( _msgType );
                _log.info( _dump );
                _dump.reset();
                _missedMsgTypes.append( _tmpLookupKey );
            }
        }
        return null;
    }

    private Event decodeLogon() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "Logon" ).append( " : " );
        }

        final MilleniumLogonImpl msg = _milleniumLogonFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "userName" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getUserNameForUpdate(), 25 );

        if ( _debug ) _dump.append( "\nField: " ).append( "password" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getPasswordForUpdate(), 25 );

        if ( _debug ) _dump.append( "\nField: " ).append( "newPassword" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getNewPasswordForUpdate(), 25 );
        if ( _debug ) _dump.append( "\nField: " ).append( "msgVersion" ).append( " : " );
        _msgVersion = _builder.decodeUByte();
        _builder.end();
        return msg;
    }

    private Event decodeLogonReply() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogonReply" ).append( " : " );
        }

        final MilleniumLogonReplyImpl msg = _milleniumLogonReplyFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        msg.setRejectCode( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "pwdExpiryDayCount" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getPwdExpiryDayCountForUpdate(), 30 );
        _builder.end();
        return msg;
    }

    private Event decodeLogout() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "Logout" ).append( " : " );
        }

        final MilleniumLogoutImpl msg = _milleniumLogoutFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "reason" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getReasonForUpdate(), 20 );
        _builder.end();
        return msg;
    }

    private Event decodeMissedMessageRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "MissedMessageRequest" ).append( " : " );
        }

        final MilleniumMissedMessageRequestImpl msg = _milleniumMissedMessageRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "appId" ).append( " : " );
        msg.setAppId( _builder.decodeUByte() );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastMsgSeqNum" ).append( " : " );
        msg.setLastMsgSeqNum( _builder.decodeInt() );
        _builder.end();
        return msg;
    }

    private Event decodeMissedMsgRequestAck() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "MissedMsgRequestAck" ).append( " : " );
        }

        final MilleniumMissedMsgRequestAckImpl msg = _milleniumMissedMsgRequestAckFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "missedMsgReqAckType" ).append( " : " );
        msg.setMissedMsgReqAckType( MilleniumMissedMsgReqAckType.getVal( _builder.decodeByte() ) );
        _builder.end();
        return msg;
    }

    private Event decodeMissedMsgReport() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "MissedMsgReport" ).append( " : " );
        }

        final MilleniumMissedMsgReportImpl msg = _milleniumMissedMsgReportFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "missedMsgReportType" ).append( " : " );
        msg.setMissedMsgReportType( MilleniumMissedMsgReportType.getVal( _builder.decodeByte() ) );
        _builder.end();
        return msg;
    }

    private Event decodeHeartbeat() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "Heartbeat" ).append( " : " );
        }

        final HeartbeatImpl msg = _heartbeatFactory.get();
        _builder.end();
        return msg;
    }

    private Event decodeNewOrder() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "NewOrder" ).append( " : " );
        }

        final NewOrderSingleImpl msg = _newOrderSingleFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 11 );

        if ( _debug ) _dump.append( "\nField: " ).append( "account" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getAccountForUpdate(), 10 );
        if ( _debug ) _dump.append( "\nField: " ).append( "clearingAccount" ).append( " : " );
        _clearingAccount = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "instrumentId" ).append( " : " );
        _instrumentId = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "mesQualifier" ).append( " : " );
        _mesQualifier = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 1 );

        if ( _debug ) _dump.append( "\nField: " ).append( "ordType" ).append( " : " );
        msg.setOrdType( OrdType.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "timeInForce" ).append( " : " );
        msg.setTimeInForce( TimeInForce.getVal( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "expireDateTime" ).append( " : " );
        _expireDateTime = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( Side.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderQty" ).append( " : " );
        msg.setOrderQty( _builder.decodeInt() );
        if ( _debug ) _dump.append( "\nField: " ).append( "displayQty" ).append( " : " );
        _displayQty = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "price" ).append( " : " );
        msg.setPrice( _builder.decodeDecimal() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderCapacity" ).append( " : " );
        msg.setOrderCapacity( transformOrderCapacity( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "autoCancel" ).append( " : " );
        _autoCancel = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "ordSubType" ).append( " : " );
        _ordSubType = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "anonymity" ).append( " : " );
        _anonymity = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "stopPrice" ).append( " : " );
        _stopPrice = _builder.decodePrice();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler3" ).append( " : " );
        _builder.skip( 10 );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        enrich( msg );
        _builder.end();
        return msg;
    }

    private Event decodeOrderCancelRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderCancelRequest" ).append( " : " );
        }

        final CancelRequestImpl msg = _cancelRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "origClOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrigClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrderIdForUpdate(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "instrumentId" ).append( " : " );
        _instrumentId = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 2 );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( Side.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 10 );
        _builder.end();
        return msg;
    }

    private Event decodeCancelReject() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "CancelReject" ).append( " : " );
        }

        final CancelRejectImpl msg = _cancelRejectFactory.get();
        if ( _debug ) _dump.append( "\nField: " ).append( "appId" ).append( " : " );
        _appId = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrderIdForUpdate(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        _rejectCode = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "sendingTime" ).append( " : " );
        msg.setEventTimestamp( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 8 );
        _builder.end();
        return msg;
    }

    private Event decodeReject() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "Reject" ).append( " : " );
        }

        final SessionRejectImpl msg = _sessionRejectFactory.get();
        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        _rejectCode = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectReason" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getTextForUpdate(), 30 );

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectMsgType" ).append( " : " );
        _builder.decodeString( msg.getRefMsgTypeForUpdate() );
        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _clOrdId, 20 );
        _builder.end();
        return msg;
    }

    private Event decodeBusinessReject() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "BusinessReject" ).append( " : " );
        }

        final VagueOrderRejectImpl msg = _vagueOrderRejectFactory.get();
        if ( _debug ) _dump.append( "\nField: " ).append( "appId" ).append( " : " );
        _appId = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        _builder.decodeIntToString( msg.getTextForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrderIdForUpdate(), 12 );

        if ( _debug ) _dump.append( "\nField: " ).append( "sendingTime" ).append( " : " );
        msg.setEventTimestamp( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 10 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderReplaceRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderReplaceRequest" ).append( " : " );
        }

        final CancelReplaceRequestImpl msg = _cancelReplaceRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "origClOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrigClOrdIdForUpdate(), 20 );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getOrderIdForUpdate(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "instrumentId" ).append( " : " );
        _instrumentId = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 2 );
        if ( _debug ) _dump.append( "\nField: " ).append( "expireDateTime" ).append( " : " );
        _expireDateTime = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderQty" ).append( " : " );
        msg.setOrderQty( _builder.decodeInt() );
        if ( _debug ) _dump.append( "\nField: " ).append( "displayQty" ).append( " : " );
        _displayQty = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "price" ).append( " : " );
        msg.setPrice( _builder.decodeDecimal() );

        if ( _debug ) _dump.append( "\nField: " ).append( "account" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getAccountForUpdate(), 10 );

        if ( _debug ) _dump.append( "\nField: " ).append( "timeInForce" ).append( " : " );
        msg.setTimeInForce( TimeInForce.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( Side.getVal( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "stopPrice" ).append( " : " );
        _stopPrice = _builder.decodePrice();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 10 );
        _builder.end();
        return msg;
    }

    private Event conditionalDecoder1( Event prevMsg ) {
        switch( _execType ) {
        case 'G': {
                final TradeCorrectImpl msg = _tradeCorrectFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.getExecRefIDForUpdate().copy( _execRefID );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLastPx( _lastPx );
                msg.setLastQty( _lastQty );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setLiquidityInd( transformLiquidityInd( _liquidityInd ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case '8': {
                final RejectedImpl msg = _rejectedFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 'H': {
                final TradeCancelImpl msg = _tradeCancelFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.getExecRefIDForUpdate().copy( _execRefID );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLastPx( _lastPx );
                msg.setLastQty( _lastQty );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setLiquidityInd( transformLiquidityInd( _liquidityInd ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case '4': {
                final CancelledImpl msg = _cancelledFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 'C': {
                final ExpiredImpl msg = _expiredFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 'F': {
                final TradeNewImpl msg = _tradeNewFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLastPx( _lastPx );
                msg.setLastQty( _lastQty );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setLiquidityInd( transformLiquidityInd( _liquidityInd ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case '5': {
                final ReplacedImpl msg = _replacedFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 'D': {
                final RestatedImpl msg = _restatedFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case '9': {
                final SuspendedImpl msg = _suspendedFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case '0': {
                final NewOrderAckImpl msg = _newOrderAckFactory.get();
                if ( _debug ) _dump.append( "\nHook : " ).append( "predecode" ).append( " : " );
                if ( _nanoStats ) msg.setAckReceived( _received );
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getExecIdForUpdate().copy( _execId );
                msg.getClOrdIdForUpdate().copy( _clOrdId );
                msg.getOrderIdForUpdate().copy( _orderId );
                msg.setExecType( ExecType.getVal( _execType ) );
                msg.setOrdStatus( OrdStatus.getVal( _ordStatus ) );
                msg.setLeavesQty( _leavesQty );
                msg.setSide( Side.getVal( _side ) );
                msg.setEventTimestamp( _sendingTime );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 49: case 50: case 51: case 54: case 55: case 58: case 59: case 60:         
case 61: case 62: case 63: case 64: case 65: case 66: case 69: 
            break;
        }
        throw new RuntimeDecodingException( "No matching condition for conditional message type" );
    }
    private Event decodeExecutionReport() {
        Event msg;
        if ( _debug ) _dump.append( "\nField: " ).append( "appId" ).append( " : " );
        _appId = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        _msgSeqNum = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _execId, 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _clOrdId, 20 );
        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _orderId, 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "execType" ).append( " : " );
        _execType = _builder.decodeChar();
        if ( _debug ) _dump.append( "\nField: " ).append( "execRefID" ).append( " : " );
        _builder.decodeZStringFixedWidth( _execRefID, 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "ordStatus" ).append( " : " );
        _ordStatus = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        _rejectCode = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "lastPx" ).append( " : " );
        _lastPx = _builder.decodePrice();
        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty" ).append( " : " );
        _lastQty = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "leavesQty" ).append( " : " );
        _leavesQty = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "container" ).append( " : " );
        _container = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "displayQty" ).append( " : " );
        _displayQty = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "instrumentId" ).append( " : " );
        _instrumentId = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 2 );
        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        _side = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 8 );
        if ( _debug ) _dump.append( "\nField: " ).append( "counterparty" ).append( " : " );
        _builder.decodeZStringFixedWidth( _counterparty, 11 );
        if ( _debug ) _dump.append( "\nField: " ).append( "liquidityInd" ).append( " : " );
        _liquidityInd = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "tradeMatchId" ).append( " : " );
        _tradeMatchId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "sendingTime" ).append( " : " );
        _sendingTime = _builder.decodeTimestampUTC();
        if ( _debug ) _dump.append( "\nField: " ).append( "filler3" ).append( " : " );
        _builder.skip( 10 );
        msg = conditionalDecoder1( null );
        _builder.end();
        return msg;
    }


    @Override public String getComponentId() { return _id; }

   // transform methods
    private static final LiquidityInd[] _liquidityIndMap = new LiquidityInd[19];
    private static final int    _liquidityIndIndexOffset = 'A';
    static {
        for ( int i=0 ; i < _liquidityIndMap.length ; i++ ) {
             _liquidityIndMap[i] = null;
        }
         _liquidityIndMap[ (byte)'A' - _liquidityIndIndexOffset ] = LiquidityInd.AddedLiquidity;
         _liquidityIndMap[ (byte)'R' - _liquidityIndIndexOffset ] = LiquidityInd.RemovedLiquidity;
         _liquidityIndMap[ (byte)'C' - _liquidityIndIndexOffset ] = LiquidityInd.Auction;
    }

    private LiquidityInd transformLiquidityInd( byte extVal ) {
        final int arrIdx = extVal - _liquidityIndIndexOffset;
        if ( arrIdx < 0 || arrIdx >= _liquidityIndMap.length ) {
            throw new RuntimeDecodingException( " unsupported decoding on LiquidityInd for value " + (char)extVal );
        }
        LiquidityInd intVal = _liquidityIndMap[ arrIdx ];
        if ( intVal == null ) {
            throw new RuntimeDecodingException( " unsupported decoding on LiquidityInd for value " + (char)extVal );
        }
        return intVal;
    }

    private static final OrderCapacity[] _orderCapacityMap = new OrderCapacity[4];
    private static final int    _orderCapacityIndexOffset = '1';
    static {
        for ( int i=0 ; i < _orderCapacityMap.length ; i++ ) {
             _orderCapacityMap[i] = OrderCapacity.Principal;
        }
         _orderCapacityMap[ (byte)'1' - _orderCapacityIndexOffset ] = OrderCapacity.RisklessPrincipal;
         _orderCapacityMap[ (byte)'2' - _orderCapacityIndexOffset ] = OrderCapacity.Principal;
         _orderCapacityMap[ (byte)'3' - _orderCapacityIndexOffset ] = OrderCapacity.AgentForOtherMember;
    }

    private OrderCapacity transformOrderCapacity( byte extVal ) {
        final int arrIdx = extVal - _orderCapacityIndexOffset;
        if ( arrIdx < 0 || arrIdx >= _orderCapacityMap.length ) {
            return OrderCapacity.Principal;
        }
        OrderCapacity intVal = _orderCapacityMap[ arrIdx ];
        return intVal;
    }


    @Override
    public final int parseHeader( final byte[] msg, final int offset, final int bytesRead ) {

        _appId = -1;       
        _binaryMsg = msg;
        _maxIdx = bytesRead + offset; // temp assign maxIdx to last data bytes in bufferMap
        _offset = offset;
        _builder.start( msg, offset, _maxIdx );
        
        if ( bytesRead < 4 ) {
            ReusableString copy = TLC.instance().getString();
            if ( bytesRead == 0 )  {
                copy.setValue( "{empty}" );
            } else{
                copy.setValue( msg, offset, bytesRead );
            }
            throw new RuntimeDecodingException( "Millenium Messsage too small, len=" + bytesRead, copy );
        } else if ( msg.length < _maxIdx ){
            throwDecodeException( "Buffer too small for specified bytesRead=" + bytesRead + ",offset=" + offset + ", bufLen=" + msg.length );
        }
        
        final byte version = _builder.decodeByte();
        
        if ( version != _protocolVersion ) {
            throwDecodeException( "Expected version="  + _protocolVersion + " not " + version );
        }

        _msgStatedLen = _builder.decodeShort() + 3; // add 3 to pass protoVer and length

        _msgType = _builder.decodeByte();
        
        _maxIdx = _msgStatedLen + _offset;  // correctly assign maxIdx as last bytes of current message 

        if ( _maxIdx > _binaryMsg.length )  _maxIdx  = _binaryMsg.length;
        
        return _msgStatedLen;
    }
    
    public final byte getAppId() {
        return _appId;
    }
    
    // enrich of NOS only used by exchange emulator
    
    private static final ViewString _lseREC = new ViewString( "L" );

    private void enrich( NewOrderSingleImpl nos ) {
        
        ExchangeInstrument instr = null;
        
        if ( _instrumentId > 0 ) {
            instr = _instrumentLocator.getExchInstByExchangeLong( ExchangeCode.XLON, _instrumentId );
        }

        if ( instr != null ) {
            nos.setCurrency( instr.getCurrency() );
            nos.getSymbolForUpdate().setValue( ((ExchangeInstrument)instr).getExchangeSymbol() );
        }
        
        nos.setInstrument( instr );
    }
    
}
