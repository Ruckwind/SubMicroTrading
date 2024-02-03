package com.rr.model.generated.fix.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import java.util.HashMap;
import java.util.Map;
import com.rr.core.utils.*;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.pool.SuperPool;
import com.rr.core.codec.FixEncoder;
import com.rr.core.codec.FixEncodeBuilderImpl;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.model.internal.type.*;
import com.rr.model.generated.fix.model.defn.FixDictionary42;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.core.FullEventIds;

@SuppressWarnings( "unused" )

public final class CHIXEncoder implements FixEncoder {

   // Member Vars
    private static final byte      MSG_NewOrderSingle = (byte)'D';
    private static final byte      MSG_CancelReplaceRequest = (byte)'G';
    private static final byte      MSG_CancelRequest = (byte)'F';
    private static final byte      MSG_CancelReject = (byte)'9';
    private static final byte      MSG_NewOrderAck = (byte)'8';
    private static final byte      MSG_Trade = (byte)'8';
    private static final byte      MSG_Rejected = (byte)'8';
    private static final byte      MSG_Cancelled = (byte)'8';
    private static final byte      MSG_Replaced = (byte)'8';
    private static final byte      MSG_DoneForDay = (byte)'8';
    private static final byte      MSG_Stopped = (byte)'8';
    private static final byte      MSG_Expired = (byte)'8';
    private static final byte      MSG_Suspended = (byte)'8';
    private static final byte      MSG_Restated = (byte)'8';
    private static final byte      MSG_TradeCorrect = (byte)'8';
    private static final byte      MSG_TradeCancel = (byte)'8';
    private static final byte      MSG_OrderStatus = (byte)'8';
    private static final byte      MSG_PendingNew = (byte)'8';
    private static final byte      MSG_PendingCancel = (byte)'8';
    private static final byte      MSG_PendingReplace = (byte)'8';
    private static final byte      MSG_Calculated = (byte)'8';
    private static final byte      MSG_Heartbeat = (byte)'0';
    private static final byte      MSG_Logon = (byte)'A';
    private static final byte      MSG_Logout = (byte)'5';
    private static final byte      MSG_SessionReject = (byte)'3';
    private static final byte      MSG_ResendRequest = (byte)'2';
    private static final byte      MSG_SequenceReset = (byte)'4';
    private static final byte      MSG_TestRequest = (byte)'1';
    private static final byte      MSG_PartialFill = (byte)'8';
    private static final byte      MSG_Fill = (byte)'8';

    private final byte[]               _buf;
    private final String               _id;
    private final byte                 _majorVersion;
    private final byte                 _minorVersion;
    private final com.rr.core.codec.FixEncodeBuilderImpl _builder;

    private final ZString              _fixVersion;
    private       TimeUtils            _tzCalculator = TimeUtilsFactory.createTimeUtils();
    private       SingleByteLookup     _sv;
    private       TwoByteLookup        _tv;
    private       MultiByteLookup      _mv;

   // Constructors
    public CHIXEncoder( String id, byte[] buf, int offset ) {
        this( id, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor, buf, offset );
    }

    public CHIXEncoder( byte[] buf, int offset ) {
        this( null, FixVersion.Fix4_2._major, FixVersion.Fix4_2._minor, buf, offset );
    }

    public CHIXEncoder( byte major, byte minor, byte[] buf, int offset ) {
        this( null, major, minor, buf, offset );
    }

    public CHIXEncoder( String id, byte major, byte minor, byte[] buf, int offset ) {
        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {
            throw new RuntimeException( "Encode buffer too small only " + buf.length + ", min=" + SizeType.MIN_ENCODE_BUFFER.getSize() );
        }
        _buf = buf;
        _id = id;
        _majorVersion = major;
        _minorVersion = minor;
        _builder = new com.rr.core.codec.FixEncodeBuilderImpl( buf, offset, major, minor );
        _fixVersion   = new ViewString( "FIX." + (char)major + "." + (char)minor );
    }

    public CHIXEncoder( String id, byte major, byte minor, byte[] buf ) {
        this( id, major, minor, buf, 0 );
    }

    public CHIXEncoder( byte major, byte minor, byte[] buf ) {
        this( null, major, minor, buf, 0 );
    }


   // encode methods

    @Override
    public final void encode( final Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            encodeNewOrderSingle( (NewOrderSingle) msg );
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            encodeCancelReplaceRequest( (CancelReplaceRequest) msg );
            break;
        case EventIds.ID_CANCELREQUEST:
            encodeCancelRequest( (CancelRequest) msg );
            break;
        case EventIds.ID_CANCELREJECT:
            encodeCancelReject( (CancelReject) msg );
            break;
        case EventIds.ID_NEWORDERACK:
            encodeNewOrderAck( (NewOrderAck) msg );
            break;
        case EventIds.ID_TRADENEW:
            encodeTradeNew( (TradeNew) msg );
            break;
        case EventIds.ID_REJECTED:
            encodeRejected( (Rejected) msg );
            break;
        case EventIds.ID_CANCELLED:
            encodeCancelled( (Cancelled) msg );
            break;
        case EventIds.ID_REPLACED:
            encodeReplaced( (Replaced) msg );
            break;
        case EventIds.ID_DONEFORDAY:
            encodeDoneForDay( (DoneForDay) msg );
            break;
        case EventIds.ID_STOPPED:
            encodeStopped( (Stopped) msg );
            break;
        case EventIds.ID_EXPIRED:
            encodeExpired( (Expired) msg );
            break;
        case EventIds.ID_SUSPENDED:
            encodeSuspended( (Suspended) msg );
            break;
        case EventIds.ID_RESTATED:
            encodeRestated( (Restated) msg );
            break;
        case EventIds.ID_TRADECORRECT:
            encodeTradeCorrect( (TradeCorrect) msg );
            break;
        case EventIds.ID_TRADECANCEL:
            encodeTradeCancel( (TradeCancel) msg );
            break;
        case EventIds.ID_ORDERSTATUS:
            encodeOrderStatus( (OrderStatus) msg );
            break;
        case EventIds.ID_PENDINGNEW:
            encodePendingNew( (PendingNew) msg );
            break;
        case EventIds.ID_PENDINGCANCEL:
            encodePendingCancel( (PendingCancel) msg );
            break;
        case EventIds.ID_PENDINGREPLACE:
            encodePendingReplace( (PendingReplace) msg );
            break;
        case EventIds.ID_CALCULATED:
            encodeCalculated( (Calculated) msg );
            break;
        case EventIds.ID_HEARTBEAT:
            encodeHeartbeat( (Heartbeat) msg );
            break;
        case EventIds.ID_LOGON:
            encodeLogon( (Logon) msg );
            break;
        case EventIds.ID_LOGOUT:
            encodeLogout( (Logout) msg );
            break;
        case EventIds.ID_SESSIONREJECT:
            encodeSessionReject( (SessionReject) msg );
            break;
        case EventIds.ID_RESENDREQUEST:
            encodeResendRequest( (ResendRequest) msg );
            break;
        case EventIds.ID_SEQUENCERESET:
            encodeSequenceReset( (SequenceReset) msg );
            break;
        case EventIds.ID_TESTREQUEST:
            encodeTestRequest( (TestRequest) msg );
            break;
        case 2:
        case 3:
        case 9:
        case 11:
        case 12:
        case 13:
        case 17:
        case 18:
        case 36:
        default:
            _builder.start();
            break;
        }
    }

    @Override public final int getLength() { return _builder.getLength(); }
    @Override public final int getOffset() { return _builder.getOffset(); }


    public final void encodeNewOrderSingle( final NewOrderSingle msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_NewOrderSingle );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        final TimeInForce tTimeInForce = msg.getTimeInForce();
        if ( tTimeInForce != null ) _builder.encodeByte( FixDictionary42.TimeInForce, tTimeInForce.getVal() );        // tag59
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        _builder.encodeByte( FixDictionary42.OrdType, msg.getOrdType().getVal() );        // tag40
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final HandlInst tHandlInst = msg.getHandlInst();
        if ( tHandlInst != null ) _builder.encodeByte( FixDictionary42.HandlInst, tHandlInst.getVal() );        // tag21
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodeString( FixDictionary42.ExDest, msg.getExDest() );        // tag100
        _builder.encodeString( FixDictionary42.Account, msg.getAccount() );        // tag1
            _builder.encodeByte( FixDictionary42.Rule80A, encodeRule47A( msg.getOrderCapacity() ) );
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        final ExchangeCode tSecurityExchange = msg.getSecurityExchange();
        if ( tSecurityExchange != null ) _builder.encodeBytes( FixDictionary42.SecurityExchange, tSecurityExchange.getVal() );        // tag207
        final BookingType tBookingType = msg.getBookingType();
        if ( tBookingType != null ) _builder.encodeByte( FixDictionary42.BookingType, tBookingType.getVal() );        // tag775
        final TargetStrategy tTargetStrategy = msg.getTargetStrategy();
        if ( tTargetStrategy != null ) _builder.encodeByte( FixDictionary42.TargetStrategy, tTargetStrategy.getVal() );        // tag847
        _builder.encodeUTCTimestamp( FixDictionary42.effectiveTime, msg.getEffectiveTime() );        // tag168
        _builder.encodeUTCTimestamp( FixDictionary42.expireTime, msg.getExpireTime() );        // tag126
        _builder.encodeEnvelope();
    }

    public final void encodeCancelReplaceRequest( final CancelReplaceRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_CancelReplaceRequest );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        final TimeInForce tTimeInForce = msg.getTimeInForce();
        if ( tTimeInForce != null ) _builder.encodeByte( FixDictionary42.TimeInForce, tTimeInForce.getVal() );        // tag59
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
        _builder.encodeBytes( FixDictionary42.Currency, msg.getCurrency().getVal() );        // tag15
        _builder.encodeByte( FixDictionary42.OrdType, msg.getOrdType().getVal() );        // tag40
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final HandlInst tHandlInst = msg.getHandlInst();
        if ( tHandlInst != null ) _builder.encodeByte( FixDictionary42.HandlInst, tHandlInst.getVal() );        // tag21
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodeString( FixDictionary42.ExDest, msg.getExDest() );        // tag100
        _builder.encodeString( FixDictionary42.Account, msg.getAccount() );        // tag1
            _builder.encodeByte( FixDictionary42.Rule80A, encodeRule47A( msg.getOrderCapacity() ) );
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        final ExchangeCode tSecurityExchange = msg.getSecurityExchange();
        if ( tSecurityExchange != null ) _builder.encodeBytes( FixDictionary42.SecurityExchange, tSecurityExchange.getVal() );        // tag207
        final BookingType tBookingType = msg.getBookingType();
        if ( tBookingType != null ) _builder.encodeByte( FixDictionary42.BookingType, tBookingType.getVal() );        // tag775
        final TargetStrategy tTargetStrategy = msg.getTargetStrategy();
        if ( tTargetStrategy != null ) _builder.encodeByte( FixDictionary42.TargetStrategy, tTargetStrategy.getVal() );        // tag847
        _builder.encodeUTCTimestamp( FixDictionary42.effectiveTime, msg.getEffectiveTime() );        // tag168
        _builder.encodeUTCTimestamp( FixDictionary42.expireTime, msg.getExpireTime() );        // tag126
        _builder.encodeEnvelope();
    }

    public final void encodeCancelRequest( final CancelRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_CancelRequest );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodeString( FixDictionary42.ExDest, msg.getExDest() );        // tag100
        _builder.encodeString( FixDictionary42.Account, msg.getAccount() );        // tag1
        final ExchangeCode tSecurityExchange = msg.getSecurityExchange();
        if ( tSecurityExchange != null ) _builder.encodeBytes( FixDictionary42.SecurityExchange, tSecurityExchange.getVal() );        // tag207
        _builder.encodeEnvelope();
    }

    public final void encodeCancelReject( final CancelReject msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_CancelReject );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        final CxlRejReason tCxlRejReason = msg.getCxlRejReason();
        if ( tCxlRejReason != null ) _builder.encodeBytes( FixDictionary42.CxlRejReason, tCxlRejReason.getVal() );        // tag102
        _builder.encodeByte( FixDictionary42.CxlRejResponseTo, msg.getCxlRejResponseTo().getVal() );        // tag434
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeNewOrderAck( final NewOrderAck msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_NewOrderAck );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeTradeNew( final TradeNew msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Trade );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
            _builder.encodeByte( FixDictionary42.ExecType, msg.getOrderQty() - msg.getCumQty() > 0 ? (byte)'1' : (byte)'2' );
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
        _builder.encodeLong( FixDictionary42.LastQty, (long)msg.getLastQty() );        // tag32
        _builder.encodePrice( FixDictionary42.LastPx, msg.getLastPx() );        // tag31
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.LastMkt, msg.getLastMkt() );        // tag30
        _builder.encodeString( FixDictionary42.SecurityDesc, msg.getSecurityDesc() );        // tag107
        final MultiLegReportingType tMultiLegReportingType = msg.getMultiLegReportingType();
        if ( tMultiLegReportingType != null ) _builder.encodeByte( FixDictionary42.MultiLegReportingType, tMultiLegReportingType.getVal() );        // tag442
        final LiquidityInd tLiquidityInd = msg.getLiquidityInd();
        if ( tLiquidityInd != null ) _builder.encodeByte( FixDictionary42.LiquidityInd, tLiquidityInd.getVal() );        // tag851
        _builder.encodeEnvelope();
    }

    public final void encodeRejected( final Rejected msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Rejected );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        final TradingStatus tTradingStatus = msg.getTradingStatus();
        if ( tTradingStatus != null ) _builder.encodeTwoByte( FixDictionary42.TradingStatus, tTradingStatus.getVal() );        // tag1700
        _builder.encodeEnvelope();
    }

    public final void encodeCancelled( final Cancelled msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Cancelled );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeEnvelope();
    }

    public final void encodeReplaced( final Replaced msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Replaced );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
            encodeReplacedOrdStatus( msg );
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeEnvelope();
    }

    public final void encodeDoneForDay( final DoneForDay msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_DoneForDay );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeStopped( final Stopped msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Stopped );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeExpired( final Expired msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Expired );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeSuspended( final Suspended msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Suspended );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeRestated( final Restated msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Restated );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        final ExecRestatementReason tExecRestatementReason = msg.getExecRestatementReason();
        if ( tExecRestatementReason != null ) _builder.encodeTwoByte( FixDictionary42.ExecRestatementReason, tExecRestatementReason.getVal() );        // tag378
        _builder.encodeEnvelope();
    }

    public final void encodeTradeCorrect( final TradeCorrect msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_TradeCorrect );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'2' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
            _builder.encodeByte( FixDictionary42.ExecType, msg.getOrderQty() - msg.getCumQty() > 0 ? (byte)'1' : (byte)'2' );
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
        _builder.encodeLong( FixDictionary42.LastQty, (long)msg.getLastQty() );        // tag32
        _builder.encodePrice( FixDictionary42.LastPx, msg.getLastPx() );        // tag31
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.LastMkt, msg.getLastMkt() );        // tag30
        _builder.encodeString( FixDictionary42.SecurityDesc, msg.getSecurityDesc() );        // tag107
        final MultiLegReportingType tMultiLegReportingType = msg.getMultiLegReportingType();
        if ( tMultiLegReportingType != null ) _builder.encodeByte( FixDictionary42.MultiLegReportingType, tMultiLegReportingType.getVal() );        // tag442
        final LiquidityInd tLiquidityInd = msg.getLiquidityInd();
        if ( tLiquidityInd != null ) _builder.encodeByte( FixDictionary42.LiquidityInd, tLiquidityInd.getVal() );        // tag851
        _builder.encodeString( FixDictionary42.ExecRefID, msg.getExecRefID() );        // tag19
        _builder.encodeEnvelope();
    }

    public final void encodeTradeCancel( final TradeCancel msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_TradeCancel );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'1' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
            _builder.encodeByte( FixDictionary42.ExecType, msg.getOrderQty() - msg.getCumQty() > 0 ? (byte)'1' : (byte)'2' );
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.LastMkt, msg.getLastMkt() );        // tag30
        _builder.encodeString( FixDictionary42.SecurityDesc, msg.getSecurityDesc() );        // tag107
        final MultiLegReportingType tMultiLegReportingType = msg.getMultiLegReportingType();
        if ( tMultiLegReportingType != null ) _builder.encodeByte( FixDictionary42.MultiLegReportingType, tMultiLegReportingType.getVal() );        // tag442
        final LiquidityInd tLiquidityInd = msg.getLiquidityInd();
        if ( tLiquidityInd != null ) _builder.encodeByte( FixDictionary42.LiquidityInd, tLiquidityInd.getVal() );        // tag851
        _builder.encodeString( FixDictionary42.ExecRefID, msg.getExecRefID() );        // tag19
        _builder.encodeEnvelope();
    }

    public final void encodeOrderStatus( final OrderStatus msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_OrderStatus );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'3' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodePendingNew( final PendingNew msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_PendingNew );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodePendingCancel( final PendingCancel msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_PendingCancel );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeEnvelope();
    }

    public final void encodePendingReplace( final PendingReplace msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_PendingReplace );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeString( FixDictionary42.OrigClOrdId, msg.getOrigClOrdId() );        // tag41
        _builder.encodeEnvelope();
    }

    public final void encodeCalculated( final Calculated msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Calculated );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.ClOrdId, msg.getClOrdId() );        // tag11
        _builder.encodeString( FixDictionary42.ExecID, msg.getExecId() );        // tag17
            _builder.encodeByte( FixDictionary42.ExecTransType, (byte)'0' );
        _builder.encodeString( FixDictionary42.OrderId, msg.getOrderId() );        // tag37
        _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        _builder.encodeByte( FixDictionary42.ExecType, msg.getExecType().getVal() );        // tag150
        _builder.encodeLong( FixDictionary42.OrderQty, (long)msg.getOrderQty() );        // tag38
        _builder.encodePrice( FixDictionary42.Price, msg.getPrice() );        // tag44
            _builder.encodeByte( FixDictionary42.LastQty, (byte)'0' );
            _builder.encodeByte( FixDictionary42.LastPx, (byte)'0' );
        _builder.encodeByte( FixDictionary42.Side, msg.getSide().getVal() );        // tag54
        _builder.encodeString( FixDictionary42.Symbol, msg.getSymbol() );        // tag55
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionary42.Currency, tCurrency.getVal() );        // tag15
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionary42.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionary42.SecurityID, msg.getSecurityId() );        // tag48
        _builder.encodeUTCTimestamp( FixDictionary42.TransactTime, now );        // tag60
        _builder.encodePrice( FixDictionary42.AvgPx, msg.getAvgPx() );        // tag6
        _builder.encodeLong( FixDictionary42.CumQty, (long)msg.getCumQty() );        // tag14
        _builder.encodeLong( FixDictionary42.LeavesQty, (long)msg.getLeavesQty() );        // tag151
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeHeartbeat( final Heartbeat msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Heartbeat );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.testReqID, msg.getTestReqID() );        // tag112
        _builder.encodeEnvelope();
    }

    public final void encodeLogon( final Logon msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Logon );
        _builder.encodeString( FixDictionary42.SenderCompID, msg.getSenderCompId() );        // tag49
        _builder.encodeString( FixDictionary42.TargetCompID, msg.getTargetCompId() );        // tag56
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeString( FixDictionary42.SenderSubID, msg.getSenderSubId() );        // tag50
        _builder.encodeString( FixDictionary42.TargetSubID, msg.getTargetSubId() );        // tag57
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        final EncryptMethod tEncryptMethod = msg.getEncryptMethod();
        if ( tEncryptMethod != null ) _builder.encodeByte( FixDictionary42.EncryptMethod, tEncryptMethod.getVal() );        // tag98
        _builder.encodeInt( FixDictionary42.heartBtInt, msg.getHeartBtInt() );        // tag108
        _builder.encodeInt( FixDictionary42.RawDataLen, msg.getRawDataLen() );        // tag95
        _builder.encodeString( FixDictionary42.RawData, msg.getRawData() );        // tag96
        _builder.encodeBool( FixDictionary42.ResetSeqNumFlag, msg.getResetSeqNumFlag() );        // tag141
        _builder.encodeInt( FixDictionary42.NextExpectedMsgSeqNum, msg.getNextExpectedMsgSeqNum() );        // tag789
        _builder.encodeEnvelope();
    }

    public final void encodeLogout( final Logout msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Logout );
        _builder.encodeString( FixDictionary42.SenderCompID, msg.getSenderCompId() );        // tag49
        _builder.encodeString( FixDictionary42.TargetCompID, msg.getTargetCompId() );        // tag56
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeString( FixDictionary42.SenderSubID, msg.getSenderSubId() );        // tag50
        _builder.encodeString( FixDictionary42.TargetSubID, msg.getTargetSubId() );        // tag57
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeInt( FixDictionary42.lastMsgSeqNumProcessed, msg.getLastMsgSeqNumProcessed() );        // tag369
        _builder.encodeInt( FixDictionary42.NextExpectedMsgSeqNum, msg.getNextExpectedMsgSeqNum() );        // tag789
        _builder.encodeEnvelope();
    }

    public final void encodeSessionReject( final SessionReject msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SessionReject );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionary42.RefSeqNum, msg.getRefSeqNum() );        // tag45
        _builder.encodeInt( FixDictionary42.RefTagID, msg.getRefTagID() );        // tag371
        _builder.encodeString( FixDictionary42.RefMsgType, msg.getRefMsgType() );        // tag372
        final SessionRejectReason tSessionRejectReason = msg.getSessionRejectReason();
        if ( tSessionRejectReason != null ) _builder.encodeTwoByte( FixDictionary42.SessionRejectReason, tSessionRejectReason.getVal() );        // tag373
        _builder.encodeString( FixDictionary42.text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeResendRequest( final ResendRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_ResendRequest );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionary42.BeginSeqNo, msg.getBeginSeqNo() );        // tag7
        _builder.encodeInt( FixDictionary42.EndSeqNo, msg.getEndSeqNo() );        // tag16
        _builder.encodeEnvelope();
    }

    public final void encodeSequenceReset( final SequenceReset msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SequenceReset );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeBool( FixDictionary42.GapFillFlag, msg.getGapFillFlag() );        // tag123
        _builder.encodeInt( FixDictionary42.NewSeqNo, msg.getNewSeqNo() );        // tag36
        _builder.encodeEnvelope();
    }

    public final void encodeTestRequest( final TestRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_TestRequest );
            _builder.encodeString( FixDictionary42.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionary42.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionary42.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeString( FixDictionary42.SenderSubID, _senderSubId ); // tag50;
            _builder.encodeString( FixDictionary42.TargetSubID, _targetSubId ); // tag57;
        _builder.encodeBool( FixDictionary42.PossDupFlag, msg.getPossDupFlag() );        // tag43
        _builder.encodeUTCTimestamp( FixDictionary42.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionary42.testReqID, msg.getTestReqID() );        // tag112
        _builder.encodeEnvelope();
    }
    @Override
    public final byte[] getBytes() {
        return _buf;
    }

    @Override
    public final void setTimeUtils( final TimeUtils calc ) {
        _tzCalculator = calc;
        _builder.setTimeUtils( calc );
    }

    @Override public FixEncoder newInstance() {
        final byte[] buf = new byte[ _builder.getBuffer().length ];
        final int offset = _builder.getStartOffset();
        CHIXEncoder e = new CHIXEncoder( getComponentId(), _majorVersion, _minorVersion, buf, offset );
        e.setSenderCompId( _senderCompId );
        e.setSenderSubId( _senderSubId );
        e.setTargetCompId( _targetCompId );
        e.setTargetSubId( _targetSubId );
        e.setSenderLocationId( _senderLocationId );
        return e;
    }

    @Override public String getComponentId() { return _id; }
    
    /**
     * PostPend  Common Encoder File
     *
     * expected to contain methods used in hooks from model
     */
     
    @Override
    public void setNanoStats( boolean nanoTiming ) {
        _nanoStats = nanoTiming;
    }

    private       boolean         _nanoStats    =  true;
         
    private       int             _idx          = 1;
    
    private final ClientCancelRejectFactory _canRejFactory   = SuperpoolManager.instance().getFactory( ClientCancelRejectFactory.class, ClientCancelRejectImpl.class );
    private final ClientRejectedFactory     _rejectedFactory = SuperpoolManager.instance().getFactory( ClientRejectedFactory.class,     ClientRejectedImpl.class ); 

    public static final ZString ENCODE_REJ              = new ViewString( "ERJ" );
    public static final ZString NONE                    = new ViewString( "NON" );

    @Override
    public Event unableToSend( Event msg, ZString errMsg ) {
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            return rejectNewOrderSingle( (NewOrderSingle) msg, errMsg );
        case EventIds.ID_NEWORDERACK:
            break;
        case EventIds.ID_TRADENEW:
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            return rejectCancelReplaceRequest( (CancelReplaceRequest) msg, errMsg );
        case EventIds.ID_CANCELREQUEST:
            return rejectCancelRequest( (CancelRequest) msg, errMsg );
        }
        
        return null;
    }

    private Event rejectNewOrderSingle( NewOrderSingle nos, ZString errMsg ) {
        final ClientRejectedImpl reject = _rejectedFactory.get();

        reject.setSrcEvent( nos );
        reject.getExecIdForUpdate().copy( ENCODE_REJ ).append( nos.getClOrdId() ).append( ++_idx );
        reject.getOrderIdForUpdate().setValue( NONE );
        reject.setOrdRejReason( OrdRejReason.Other );
        reject.getTextForUpdate().setValue( errMsg );
        reject.setOrdStatus( OrdStatus.Rejected );
        reject.setExecType( ExecType.Rejected );

        reject.setCumQty( 0 );
        reject.setAvgPx( 0.0 );

        reject.setEventHandler( nos.getEventHandler() );
        return reject;
    }

    private Event rejectCancelReplaceRequest( CancelReplaceRequest msg, ZString errMsg ) {
        final ClientCancelRejectImpl reject = _canRejFactory.get();
        
        reject.getClOrdIdForUpdate().    setValue( msg.getClOrdId() );
        reject.getOrigClOrdIdForUpdate().setValue( msg.getOrigClOrdId() );
        reject.getOrderIdForUpdate().    setValue( NONE );
        reject.getTextForUpdate().       setValue( errMsg );

        reject.setCxlRejResponseTo( CxlRejResponseTo.CancelReplace );
        reject.setCxlRejReason(     CxlRejReason.Other );
        reject.setOrdStatus(        OrdStatus.Unknown );

        return reject;
    }

    private Event rejectCancelRequest( CancelRequest msg, ZString errMsg ) {
        final ClientCancelRejectImpl reject = _canRejFactory.get();
        
        reject.getClOrdIdForUpdate().    setValue( msg.getClOrdId() );
        reject.getOrigClOrdIdForUpdate().setValue( msg.getOrigClOrdId() );
        reject.getOrderIdForUpdate().    setValue( NONE );
        reject.getTextForUpdate().       setValue( errMsg );

        reject.setCxlRejResponseTo( CxlRejResponseTo.CancelRequest );
        reject.setCxlRejReason(     CxlRejReason.Other );
        reject.setOrdStatus(        OrdStatus.Unknown );

        return reject;
    }

    private static final byte[] STATS       = "     [".getBytes();
    private static final byte   STAT_DELIM  = ',';
    private static final byte   STAT_END    = ']';


    
    private       ReusableString  _senderCompId      = new ReusableString();
    private       ReusableString  _senderSubId       = new ReusableString();
    private       ReusableString  _senderLocationId  = new ReusableString();
    private       ReusableString  _targetCompId      = new ReusableString();
    private       ReusableString  _targetSubId       = new ReusableString();
    
    @Override
    public void setSenderCompId( ZString senderCompId ) {
        _senderCompId.copy( senderCompId );
    }

    @Override
    public void setSenderSubId( ZString senderSubId ) {
        _senderSubId.copy( senderSubId );
    }

    @Override
    public void setSenderLocationId( ZString senderLocationId ) {
        _senderLocationId.copy( senderLocationId );
    }

    @Override
    public void setTargetCompId( ZString targetId ) {
        _targetCompId.copy( targetId );
    }

    @Override
    public void setTargetSubId( ZString targetSubId ) {
        _targetSubId.copy( targetSubId );
    }

    public void setCompIds( String senderCompId, String senderSubId,  String targetCompId, String targetSubId ) {
        setSenderCompId(     new ReusableString( senderCompId ) );
        setSenderSubId(      new ReusableString( senderSubId  ) );
        setTargetCompId(     new ReusableString( targetCompId ) );
        setTargetSubId(      new ReusableString( targetSubId  ) );
    }






    @Override
    public void addStats( final ReusableString outBuf, final Event msg, final long msgSent ) {
        
        if ( msg.getReusableType().getId() == FullEventIds.ID_MARKET_NEWORDERSINGLE ) {
            final MarketNewOrderSingleImpl nos = (MarketNewOrderSingleImpl) msg;
            nos.setOrderSent( msgSent );        
        } else if ( msg.getReusableType().getId() == FullEventIds.ID_CLIENT_NEWORDERACK ) {
            final ClientNewOrderAckImpl ack = (ClientNewOrderAckImpl) msg;

            final long orderIn  = ack.getOrderReceived();
            final long orderOut = ack.getOrderSent();
            final long ackIn    = ack.getAckReceived();
            final long ackOut   = msgSent;

            final long microNOSToMKt    = (orderOut - orderIn)  >> 10;
            final long microInMkt       = (ackIn    - orderOut) >> 10;
            final long microAckToClient = (ackOut   - ackIn)    >> 10;
            
            outBuf.append( STATS      ).append( microNOSToMKt )
                  .append( STAT_DELIM ).append( microInMkt )
                  .append( STAT_DELIM ).append( microAckToClient ).append( STAT_END );
        }
    }



    private byte encodeRule47A( OrderCapacity orderCapacity ) {
        if ( orderCapacity == OrderCapacity.Principal ) return 'P'; 
        if ( orderCapacity == OrderCapacity.AgentForOtherMember ) return 'A'; 
        return 'P';
    }

    private void encodeReplacedOrdStatus( Replaced msg ) {
        if ( msg.getCumQty() == 0 ) {
            _builder.encodeByte( FixDictionary42.OrdStatus, (byte) '5' );        // tag39 = REPLACED
        } else{
            _builder.encodeByte( FixDictionary42.OrdStatus, msg.getOrdStatus().getVal() );        // tag39
        }
    }


/*
 * HANDCODED ENCODER METHDOS
 */

}
