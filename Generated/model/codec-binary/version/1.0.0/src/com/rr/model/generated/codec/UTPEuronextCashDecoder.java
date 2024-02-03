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

public final class UTPEuronextCashDecoder extends AbstractBinaryDecoder {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs
    private static final byte      MSG_Logon = (byte)'A';
    private static final byte      MSG_LogonReject = (byte)'l';
    private static final byte      MSG_TradingSessionStatus = (byte)'h';
    private static final byte      MSG_TestRequest = (byte)'1';
    private static final byte      MSG_Heartbeat = (byte)'0';
    private static final byte      MSG_NewOrder = (byte)'D';
    private static final byte      MSG_OrderAck = (byte)'a';
    private static final byte      MSG_OrderCancelRequest = (byte)'F';
    private static final byte      MSG_CancelReqAck = (byte)'6';
    private static final byte      MSG_OrderKilled = (byte)'4';
    private static final byte      MSG_OrderReplaceRequest = (byte)'G';
    private static final byte      MSG_ReplaceReqAck = (byte)'E';
    private static final byte      MSG_OrderReplaced = (byte)'5';
    private static final byte      MSG_CancelReplaceReject = (byte)'8';
    private static final byte      MSG_OrderFill = (byte)'2';
    private static final byte      MSG_BustCorrect = (byte)'C';

    private boolean _debug = false;

    private BinaryDecodeBuilder _builder;

    private       byte _msgType;
    private final byte                        _protocolVersion;
    private final String                      _id;
    private       int                         _msgStatedLen;
    private final ReusableString _dump  = new ReusableString(256);
    private final ReusableString _missedMsgTypes = new ReusableString();

    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks
    private       int                         _lastMsgSeqNum;
    private       ReusableString              _userName = new ReusableString(30);
    private       int                         _lastMsgSeqNumRcvd;
    private       int                         _lastMsgSeqNumSent;
    private       byte                        _rejectCode;
    private       ReusableString              _rejectText = new ReusableString(30);
    private       int                         _msgSeqNum;
    private       long                        _mktPhaseChgTime;
    private       ReusableString              _instClassId = new ReusableString(30);
    private       ReusableString              _instClassStatus = new ReusableString(30);
    private       boolean                     _orderEntryAllowed;
    private       ReusableString              _tradingSessionId = new ReusableString(30);
    private       long                        _clOrdId;
    private       int                         _orderQty;
    private       double                      _price;
    private       byte                        _priceScale;
    private       ReusableString              _symbol = new ReusableString(30);
    private       ReusableString              _onBehalfOfCompId = new ReusableString(30);
    private       byte                        _side;
    private       byte                        _ordType;
    private       byte                        _timeInForce;
    private       byte                        _orderCapacity;
    private       ReusableString              _account = new ReusableString(30);
    private       byte                        _confirmFlag;
    private       ReusableString              _mic = new ReusableString(30);
    private       ReusableString              _currency = new ReusableString(30);
    private       ReusableString              _giveUpFirm = new ReusableString(30);
    private       ReusableString              _parentClOrdId = new ReusableString(30);
    private       long                        _orderId;
    private       long                        _transactTime;
    private       long                        _origClOrdId;
    private       double                      _collarRejPx;
    private       int                         _errorCode;
    private       byte                        _collarRejType;
    private       byte                        _collarRejPxScale;
    private       long                        _execId;
    private       int                         _lastQty;
    private       double                      _lastPx;
    private       byte                        _liquidityInd;
    private       byte                        _tradeChangeType;

   // Pools

    private final SuperPool<UTPLogonImpl> _uTPLogonPool = SuperpoolManager.instance().getSuperPool( UTPLogonImpl.class );
    private final UTPLogonFactory _uTPLogonFactory = new UTPLogonFactory( _uTPLogonPool );

    private final SuperPool<UTPLogonRejectImpl> _uTPLogonRejectPool = SuperpoolManager.instance().getSuperPool( UTPLogonRejectImpl.class );
    private final UTPLogonRejectFactory _uTPLogonRejectFactory = new UTPLogonRejectFactory( _uTPLogonRejectPool );

    private final SuperPool<UTPTradingSessionStatusImpl> _uTPTradingSessionStatusPool = SuperpoolManager.instance().getSuperPool( UTPTradingSessionStatusImpl.class );
    private final UTPTradingSessionStatusFactory _uTPTradingSessionStatusFactory = new UTPTradingSessionStatusFactory( _uTPTradingSessionStatusPool );

    private final SuperPool<TestRequestImpl> _testRequestPool = SuperpoolManager.instance().getSuperPool( TestRequestImpl.class );
    private final TestRequestFactory _testRequestFactory = new TestRequestFactory( _testRequestPool );

    private final SuperPool<HeartbeatImpl> _heartbeatPool = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final SuperPool<NewOrderSingleImpl> _newOrderSinglePool = SuperpoolManager.instance().getSuperPool( NewOrderSingleImpl.class );
    private final NewOrderSingleFactory _newOrderSingleFactory = new NewOrderSingleFactory( _newOrderSinglePool );

    private final SuperPool<NewOrderAckImpl> _newOrderAckPool = SuperpoolManager.instance().getSuperPool( NewOrderAckImpl.class );
    private final NewOrderAckFactory _newOrderAckFactory = new NewOrderAckFactory( _newOrderAckPool );

    private final SuperPool<CancelRequestImpl> _cancelRequestPool = SuperpoolManager.instance().getSuperPool( CancelRequestImpl.class );
    private final CancelRequestFactory _cancelRequestFactory = new CancelRequestFactory( _cancelRequestPool );

    private final SuperPool<CancelledImpl> _cancelledPool = SuperpoolManager.instance().getSuperPool( CancelledImpl.class );
    private final CancelledFactory _cancelledFactory = new CancelledFactory( _cancelledPool );

    private final SuperPool<CancelReplaceRequestImpl> _cancelReplaceRequestPool = SuperpoolManager.instance().getSuperPool( CancelReplaceRequestImpl.class );
    private final CancelReplaceRequestFactory _cancelReplaceRequestFactory = new CancelReplaceRequestFactory( _cancelReplaceRequestPool );

    private final SuperPool<ReplacedImpl> _replacedPool = SuperpoolManager.instance().getSuperPool( ReplacedImpl.class );
    private final ReplacedFactory _replacedFactory = new ReplacedFactory( _replacedPool );

    private final SuperPool<RejectedImpl> _rejectedPool = SuperpoolManager.instance().getSuperPool( RejectedImpl.class );
    private final RejectedFactory _rejectedFactory = new RejectedFactory( _rejectedPool );

    private final SuperPool<TradeNewImpl> _tradeNewPool = SuperpoolManager.instance().getSuperPool( TradeNewImpl.class );
    private final TradeNewFactory _tradeNewFactory = new TradeNewFactory( _tradeNewPool );

    private final SuperPool<TradeCancelImpl> _tradeCancelPool = SuperpoolManager.instance().getSuperPool( TradeCancelImpl.class );
    private final TradeCancelFactory _tradeCancelFactory = new TradeCancelFactory( _tradeCancelPool );

    private final SuperPool<TradeCorrectImpl> _tradeCorrectPool = SuperpoolManager.instance().getSuperPool( TradeCorrectImpl.class );
    private final TradeCorrectFactory _tradeCorrectFactory = new TradeCorrectFactory( _tradeCorrectPool );


   // Constructors
    public UTPEuronextCashDecoder() { this( null ); }
    public UTPEuronextCashDecoder( String id ) {
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
        _builder = (_debug) ? new DebugBinaryDecodeBuilder<>( _dump, new com.rr.codec.emea.exchange.utp.UTPDecodeBuilderImpl() )
                            : new com.rr.codec.emea.exchange.utp.UTPDecodeBuilderImpl();
    }

    @Override
    protected final Event doMessageDecode() {
        _builder.setMaxIdx( _maxIdx );

        switch( _msgType ) {
        case MSG_Logon:
            return decodeLogon();
        case MSG_LogonReject:
            return decodeLogonReject();
        case MSG_TradingSessionStatus:
            return decodeTradingSessionStatus();
        case MSG_TestRequest:
            return decodeTestRequest();
        case MSG_Heartbeat:
            return decodeHeartbeat();
        case MSG_NewOrder:
            return decodeNewOrder();
        case MSG_OrderAck:
            return decodeOrderAck();
        case MSG_OrderCancelRequest:
            return decodeOrderCancelRequest();
        case MSG_CancelReqAck:
            return decodeCancelReqAck();
        case MSG_OrderKilled:
            return decodeOrderKilled();
        case MSG_OrderReplaceRequest:
            return decodeOrderReplaceRequest();
        case MSG_ReplaceReqAck:
            return decodeReplaceReqAck();
        case MSG_OrderReplaced:
            return decodeOrderReplaced();
        case MSG_CancelReplaceReject:
            return decodeCancelReplaceReject();
        case MSG_OrderFill:
            return decodeOrderFill();
        case MSG_BustCorrect:
            return decodeBustCorrect();
        case '3':
        case '7':
        case '9':
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'B':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
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
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'i':
        case 'j':
        case 'k':
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

        final UTPLogonImpl msg = _uTPLogonFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "lastMsgSeqNum" ).append( " : " );
        msg.setLastMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "userName" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getUserNameForUpdate(), 11 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 4 );
        _builder.end();
        return msg;
    }

    private Event decodeLogonReject() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogonReject" ).append( " : " );
        }

        final UTPLogonRejectImpl msg = _uTPLogonRejectFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "lastMsgSeqNumRcvd" ).append( " : " );
        msg.setLastMsgSeqNumRcvd( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastMsgSeqNumSent" ).append( " : " );
        msg.setLastMsgSeqNumSent( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectCode" ).append( " : " );
        msg.setRejectCode( UTPRejCode.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectText" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getRejectTextForUpdate(), 40 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        _builder.end();
        return msg;
    }

    private Event decodeTradingSessionStatus() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "TradingSessionStatus" ).append( " : " );
        }

        final UTPTradingSessionStatusImpl msg = _uTPTradingSessionStatusFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "mktPhaseChgTime" ).append( " : " );
        msg.setMktPhaseChgTime( _builder.decodeTimeUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "instClassId" ).append( " : " );
        _builder.decodeStringFixedWidth( msg.getInstClassIdForUpdate(), 2 );

        if ( _debug ) _dump.append( "\nField: " ).append( "instClassStatus" ).append( " : " );
        _builder.decodeStringFixedWidth( msg.getInstClassStatusForUpdate(), 4 );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderEntryAllowed" ).append( " : " );
        msg.setOrderEntryAllowed( _builder.decodeBool() );

        if ( _debug ) _dump.append( "\nField: " ).append( "tradingSessionId" ).append( " : " );
        _builder.decodeStringFixedWidth( msg.getTradingSessionIdForUpdate(), 4 );
        _builder.end();
        return msg;
    }

    private Event decodeTestRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "TestRequest" ).append( " : " );
        }

        final TestRequestImpl msg = _testRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 3 );
        _builder.end();
        return msg;
    }

    private Event decodeHeartbeat() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "Heartbeat" ).append( " : " );
        }

        final HeartbeatImpl msg = _heartbeatFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 3 );
        _builder.end();
        return msg;
    }

    private Event decodeNewOrder() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "NewOrder" ).append( " : " );
        }

        final NewOrderSingleImpl msg = _newOrderSingleFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderQty" ).append( " : " );
        msg.setOrderQty( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "price" ).append( " : " );
        msg.setPrice( _builder.decodeDecimal() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 24 );
        if ( _debug ) _dump.append( "\nField: " ).append( "priceScale" ).append( " : " );
        _priceScale = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSymbolForUpdate(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "onBehalfOfCompId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _onBehalfOfCompId, 11 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 1 );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "ordType" ).append( " : " );
        msg.setOrdType( transformOrdType( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "timeInForce" ).append( " : " );
        msg.setTimeInForce( transformTimeInForce( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderCapacity" ).append( " : " );
        msg.setOrderCapacity( transformOrderCapacity( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler3" ).append( " : " );
        _builder.skip( 4 );

        if ( _debug ) _dump.append( "\nField: " ).append( "account" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getAccountForUpdate(), 12 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler4" ).append( " : " );
        _builder.skip( 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "confirmFlag" ).append( " : " );
        _confirmFlag = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "mic" ).append( " : " );
        _builder.decodeStringFixedWidth( _mic, 4 );

        if ( _debug ) _dump.append( "\nField: " ).append( "currency" ).append( " : " );
        _tmpLookupKey.setValue( _binaryMsg, _builder.getCurrentIndex(), 3 );
        msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
        _builder.skip( 3);
        if ( _debug ) _dump.append( "\nField: " ).append( "giveUpFirm" ).append( " : " );
        _builder.decodeZStringFixedWidth( _giveUpFirm, 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler5" ).append( " : " );
        _builder.skip( 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "parentClOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getParentClOrdIdForUpdate(), 18 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler6" ).append( " : " );
        _builder.skip( 52 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderAck() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderAck" ).append( " : " );
        }

        final NewOrderAckImpl msg = _newOrderAckFactory.get();
        if ( _debug ) _dump.append( "\nHook : " ).append( "predecode" ).append( " : " );
        if ( _nanoStats ) msg.setAckReceived( _received );

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        msg.setTransactTime( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 31 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderCancelRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderCancelRequest" ).append( " : " );
        }

        final CancelRequestImpl msg = _cancelRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "origClOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrigClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );
        if ( _debug ) _dump.append( "\nField: " ).append( "onBehalfOfCompId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _onBehalfOfCompId, 11 );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSymbolForUpdate(), 12 );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 23 );
        _builder.end();
        return msg;
    }

    private Event decodeCancelReqAck() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "CancelReqAck" ).append( " : " );
        }

        Event msg = null;
        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        _msgSeqNum = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _clOrdId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _orderId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        _transactTime = _builder.decodeTimestampUTC();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 31 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderKilled() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderKilled" ).append( " : " );
        }

        final CancelledImpl msg = _cancelledFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        msg.setTransactTime( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 31 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderReplaceRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderReplaceRequest" ).append( " : " );
        }

        final CancelReplaceRequestImpl msg = _cancelReplaceRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "origClOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrigClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderQty" ).append( " : " );
        msg.setOrderQty( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "price" ).append( " : " );
        msg.setPrice( _builder.decodeDecimal() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 24 );
        if ( _debug ) _dump.append( "\nField: " ).append( "priceScale" ).append( " : " );
        _priceScale = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "onBehalfOfCompId" ).append( " : " );
        _builder.decodeZStringFixedWidth( _onBehalfOfCompId, 11 );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSymbolForUpdate(), 12 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 1 );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "ordType" ).append( " : " );
        msg.setOrdType( transformOrdType( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "timeInForce" ).append( " : " );
        msg.setTimeInForce( transformTimeInForce( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderCapacity" ).append( " : " );
        msg.setOrderCapacity( transformOrderCapacity( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler3" ).append( " : " );
        _builder.skip( 4 );

        if ( _debug ) _dump.append( "\nField: " ).append( "account" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getAccountForUpdate(), 12 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler4" ).append( " : " );
        _builder.skip( 5 );
        if ( _debug ) _dump.append( "\nField: " ).append( "confirmFlag" ).append( " : " );
        _confirmFlag = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler5" ).append( " : " );
        _builder.skip( 14 );
        if ( _debug ) _dump.append( "\nField: " ).append( "giveUpFirm" ).append( " : " );
        _builder.decodeZStringFixedWidth( _giveUpFirm, 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler6" ).append( " : " );
        _builder.skip( 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "parentClOrdId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getParentClOrdIdForUpdate(), 18 );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler7" ).append( " : " );
        _builder.skip( 4 );
        _builder.end();
        return msg;
    }

    private Event decodeReplaceReqAck() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "ReplaceReqAck" ).append( " : " );
        }

        Event msg = null;
        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        _msgSeqNum = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _clOrdId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _orderId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        _transactTime = _builder.decodeTimestampUTC();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 31 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderReplaced() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderReplaced" ).append( " : " );
        }

        final ReplacedImpl msg = _replacedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        msg.setTransactTime( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 31 );
        _builder.end();
        return msg;
    }

    private Event decodeCancelReplaceReject() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "CancelReplaceReject" ).append( " : " );
        }

        final RejectedImpl msg = _rejectedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "origClOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrigClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        msg.setTransactTime( _builder.decodeTimestampUTC() );
        if ( _debug ) _dump.append( "\nField: " ).append( "collarRejPx" ).append( " : " );
        _collarRejPx = _builder.decodePrice();
        if ( _debug ) _dump.append( "\nField: " ).append( "errorCode" ).append( " : " );
        _errorCode = _builder.decodeShort();

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectText" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getTextForUpdate(), 40 );
        if ( _debug ) _dump.append( "\nField: " ).append( "collarRejType" ).append( " : " );
        _collarRejType = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "collarRejPxScale" ).append( " : " );
        _collarRejPxScale = _builder.decodeUByte();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 33 );
        _builder.end();
        return msg;
    }

    private Event decodeOrderFill() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "OrderFill" ).append( " : " );
        }

        final TradeNewImpl msg = _tradeNewFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        msg.setMsgSeqNum( _builder.decodeInt() );

        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _builder.decodeLongToString( msg.getClOrdIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.decodeLongToString( msg.getOrderIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        _builder.decodeLongToString( msg.getExecIdForUpdate() );

        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        msg.setTransactTime( _builder.decodeTimestampUTC() );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty" ).append( " : " );
        msg.setLastQty( _builder.decodeInt() );
        _lastPx = _builder.decodeInt();

        if ( _debug ) _dump.append( "\nField: " ).append( "filler1" ).append( " : " );
        _builder.skip( 11 );
        if ( _debug ) _dump.append( "\nField: " ).append( "priceScale" ).append( " : " );
        _priceScale = _builder.decodeUByte();
        msg.setLastPx( scale( _lastPx, _priceScale ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "liquidityInd" ).append( " : " );
        msg.setLiquidityInd( transformLiquidityInd( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 33 );
        _builder.end();
        return msg;
    }

    private Event conditionalDecoder1( Event prevMsg ) {
        switch( _tradeChangeType ) {
        case 1: {
                final TradeCancelImpl msg = _tradeCancelFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getClOrdIdForUpdate().append( _clOrdId );
                msg.getExecIdForUpdate().append( _execId );
                msg.setTransactTime( _transactTime );
                msg.setLastQty( _lastQty );
                msg.setLastPx( scale( _lastPx, _priceScale ) );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        case 2: {
                final TradeCorrectImpl msg = _tradeCorrectFactory.get();
                msg.setMsgSeqNum( _msgSeqNum );
                msg.getClOrdIdForUpdate().append( _clOrdId );
                msg.getExecIdForUpdate().append( _execId );
                msg.setTransactTime( _transactTime );
                msg.setLastQty( _lastQty );
                msg.setLastPx( scale( _lastPx, _priceScale ) );
                if ( prevMsg != null ) prevMsg.attachQueue( msg );
                return msg;
            }
        }
        throw new RuntimeDecodingException( "No matching condition for conditional message type" );
    }
    private Event decodeBustCorrect() {
        Event msg;
        if ( _debug ) _dump.append( "\nField: " ).append( "msgSeqNum" ).append( " : " );
        _msgSeqNum = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "clOrdId" ).append( " : " );
        _clOrdId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        _execId = _builder.decodeLong();
        if ( _debug ) _dump.append( "\nField: " ).append( "transactTime" ).append( " : " );
        _transactTime = _builder.decodeTimestampUTC();
        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty" ).append( " : " );
        _lastQty = _builder.decodeInt();
        _lastPx = _builder.decodeInt();
        if ( _debug ) _dump.append( "\nField: " ).append( "priceScale" ).append( " : " );
        _priceScale = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "tradeChangeType" ).append( " : " );
        _tradeChangeType = _builder.decodeUByte();
        if ( _debug ) _dump.append( "\nField: " ).append( "filler2" ).append( " : " );
        _builder.skip( 33 );
        msg = conditionalDecoder1( null );
        _builder.end();
        return msg;
    }


    @Override public String getComponentId() { return _id; }

   // transform methods
    private static final Side[] _sideMap = new Side[3];
    private static final int    _sideIndexOffset = '1';
    static {
        for ( int i=0 ; i < _sideMap.length ; i++ ) {
             _sideMap[i] = null;
        }
         _sideMap[ (byte)'1' - _sideIndexOffset ] = Side.Buy;
         _sideMap[ (byte)'2' - _sideIndexOffset ] = Side.Sell;
    }

    private Side transformSide( byte extVal ) {
        final int arrIdx = extVal - _sideIndexOffset;
        if ( arrIdx < 0 || arrIdx >= _sideMap.length ) {
            throw new RuntimeDecodingException( " unsupported decoding on Side for value " + (char)extVal );
        }
        Side intVal = _sideMap[ arrIdx ];
        if ( intVal == null ) {
            throw new RuntimeDecodingException( " unsupported decoding on Side for value " + (char)extVal );
        }
        return intVal;
    }

    private static final LiquidityInd[] _liquidityIndMap = new LiquidityInd[25];
    private static final int    _liquidityIndIndexOffset = 'A';
    static {
        for ( int i=0 ; i < _liquidityIndMap.length ; i++ ) {
             _liquidityIndMap[i] = null;
        }
         _liquidityIndMap[ (byte)'A' - _liquidityIndIndexOffset ] = LiquidityInd.AddedLiquidity;
         _liquidityIndMap[ (byte)'R' - _liquidityIndIndexOffset ] = LiquidityInd.RemovedLiquidity;
         _liquidityIndMap[ (byte)'X' - _liquidityIndIndexOffset ] = LiquidityInd.LiquidityRoutedOut;
         _liquidityIndMap[ (byte)'O' - _liquidityIndIndexOffset ] = LiquidityInd.AddedLiquidity;
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

    private static final OrdType[] _ordTypeMap = new OrdType[2];
    private static final int    _ordTypeIndexOffset = '2';
    static {
        for ( int i=0 ; i < _ordTypeMap.length ; i++ ) {
             _ordTypeMap[i] = null;
        }
         _ordTypeMap[ (byte)'2' - _ordTypeIndexOffset ] = OrdType.Limit;
    }

    private OrdType transformOrdType( byte extVal ) {
        final int arrIdx = extVal - _ordTypeIndexOffset;
        if ( arrIdx < 0 || arrIdx >= _ordTypeMap.length ) {
            throw new RuntimeDecodingException( " unsupported decoding on OrdType for value " + (char)extVal );
        }
        OrdType intVal = _ordTypeMap[ arrIdx ];
        if ( intVal == null ) {
            throw new RuntimeDecodingException( " unsupported decoding on OrdType for value " + (char)extVal );
        }
        return intVal;
    }

    private static final TimeInForce[] _timeInForceMap = new TimeInForce[9];
    private static final int    _timeInForceIndexOffset = '0';
    static {
        for ( int i=0 ; i < _timeInForceMap.length ; i++ ) {
             _timeInForceMap[i] = null;
        }
         _timeInForceMap[ (byte)'0' - _timeInForceIndexOffset ] = TimeInForce.Day;
         _timeInForceMap[ (byte)'3' - _timeInForceIndexOffset ] = TimeInForce.ImmediateOrCancel;
         _timeInForceMap[ (byte)'4' - _timeInForceIndexOffset ] = TimeInForce.FillOrKill;
         _timeInForceMap[ (byte)'7' - _timeInForceIndexOffset ] = TimeInForce.AtTheClose;
    }

    private TimeInForce transformTimeInForce( byte extVal ) {
        final int arrIdx = extVal - _timeInForceIndexOffset;
        if ( arrIdx < 0 || arrIdx >= _timeInForceMap.length ) {
            throw new RuntimeDecodingException( " unsupported decoding on TimeInForce for value " + (char)extVal );
        }
        TimeInForce intVal = _timeInForceMap[ arrIdx ];
        if ( intVal == null ) {
            throw new RuntimeDecodingException( " unsupported decoding on TimeInForce for value " + (char)extVal );
        }
        return intVal;
    }

    private static final OrderCapacity[] _orderCapacityMap = new OrderCapacity[9];
    private static final int    _orderCapacityIndexOffset = '1';
    static {
        for ( int i=0 ; i < _orderCapacityMap.length ; i++ ) {
             _orderCapacityMap[i] = OrderCapacity.Principal;
        }
         _orderCapacityMap[ (byte)'6' - _orderCapacityIndexOffset ] = OrderCapacity.Proprietary;
         _orderCapacityMap[ (byte)'1' - _orderCapacityIndexOffset ] = OrderCapacity.Individual;
         _orderCapacityMap[ (byte)'2' - _orderCapacityIndexOffset ] = OrderCapacity.Principal;
         _orderCapacityMap[ (byte)'8' - _orderCapacityIndexOffset ] = OrderCapacity.RisklessPrincipal;
         _orderCapacityMap[ (byte)'7' - _orderCapacityIndexOffset ] = OrderCapacity.AgentForOtherMember;
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
            throw new RuntimeDecodingException( "UTP Messsage too small, len=" + bytesRead, copy );
        } else if ( msg.length < _maxIdx ){
            throwDecodeException( "Buffer too small for specified bytesRead=" + bytesRead + ",offset=" + offset + ", bufLen=" + msg.length );
        }
        
        _msgType = _builder.decodeByte();
        final byte version = _builder.decodeByte();
        
        if ( version != _protocolVersion ) {
            throwDecodeException( "Expected version="  + _protocolVersion + " not " + version );
        }

        _msgStatedLen = _builder.decodeShort();
        
        _maxIdx = _msgStatedLen + _offset;  // correctly assign maxIdx as last bytes of current message

        if ( _maxIdx > _binaryMsg.length )  _maxIdx  = _binaryMsg.length;
        
        return _msgStatedLen;
    }

    private double scale( final double lastPx, final byte priceScale ) {
        switch( priceScale ) {
        case '0':
            return lastPx;
        case '1':
            return lastPx / 10.0;
        case '2':
            return lastPx / 100.0;
        case '3':
            return lastPx / 1000.0;
        case '4':
            return lastPx / 10000.0;
        case '5':
            return lastPx / 100000.0;
        case '6':
            return lastPx / 1000000.0;
        case '7':
            return lastPx / 10000000.0;
        case '8':
            return lastPx / 100000000.0;
        case '9':
            return lastPx / 1000000000.0;
        }
        return lastPx;
    }

}
