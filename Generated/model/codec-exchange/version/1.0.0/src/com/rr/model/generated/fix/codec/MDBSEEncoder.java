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
import com.rr.core.codec.ReadableMDFastFixEncoder;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.model.internal.type.*;
import com.rr.model.generated.fix.model.defn.FixDictionaryMD50;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.core.FullEventIds;

@SuppressWarnings( "unused" )

public final class MDBSEEncoder implements FixEncoder {

   // Member Vars
    private static final byte      MSG_Heartbeat = (byte)'0';
    private static final byte      MSG_Logon = (byte)'A';
    private static final byte      MSG_Logout = (byte)'5';
    private static final byte      MSG_SessionReject = (byte)'3';
    private static final byte      MSG_ResendRequest = (byte)'2';
    private static final byte      MSG_SequenceReset = (byte)'4';
    private static final byte      MSG_TestRequest = (byte)'1';
    private static final byte      MSG_TradingSessionStatus = (byte)'h';
    private static final byte      MSG_MDRequest = (byte)'V';
    private static final byte[]    MSG_MassInstrumentStateChange = "CO".getBytes();
    private static final byte      MSG_SecurityStatus = (byte)'f';
    private static final byte      MSG_SecurityDefinition = (byte)'d';
    private static final byte      MSG_MDIncRefresh = (byte)'X';
    private static final byte      MSG_MDSnapshotFullRefresh = (byte)'W';
    private static final byte[]    MSG_SecurityDefinitionUpdate = "BP".getBytes();
    private static final byte[]    MSG_ProductSnapshot = "BU".getBytes();

    private final byte[]               _buf;
    private final String               _id;
    private final byte                 _majorVersion;
    private final byte                 _minorVersion;
    private final com.rr.core.codec.ReadableMDFastFixEncoder _builder;

    private final ZString              _fixVersion;
    private       TimeUtils            _tzCalculator = TimeUtilsFactory.createTimeUtils();
    private       SingleByteLookup     _sv;
    private       TwoByteLookup        _tv;
    private       MultiByteLookup      _mv;

   // Constructors
    public MDBSEEncoder( String id, byte[] buf, int offset ) {
        this( id, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor, buf, offset );
    }

    public MDBSEEncoder( byte[] buf, int offset ) {
        this( null, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor, buf, offset );
    }

    public MDBSEEncoder( byte major, byte minor, byte[] buf, int offset ) {
        this( null, major, minor, buf, offset );
    }

    public MDBSEEncoder( String id, byte major, byte minor, byte[] buf, int offset ) {
        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {
            throw new RuntimeException( "Encode buffer too small only " + buf.length + ", min=" + SizeType.MIN_ENCODE_BUFFER.getSize() );
        }
        _buf = buf;
        _id = id;
        _majorVersion = major;
        _minorVersion = minor;
        _builder = new com.rr.core.codec.ReadableMDFastFixEncoder( buf, offset, major, minor );
        _fixVersion   = new ViewString( "FIX." + (char)major + "." + (char)minor );
    }

    public MDBSEEncoder( String id, byte major, byte minor, byte[] buf ) {
        this( id, major, minor, buf, 0 );
    }

    public MDBSEEncoder( byte major, byte minor, byte[] buf ) {
        this( null, major, minor, buf, 0 );
    }


   // encode methods

    @Override
    public final void encode( final Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
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
        case EventIds.ID_TRADINGSESSIONSTATUS:
            encodeTradingSessionStatus( (TradingSessionStatus) msg );
            break;
        case EventIds.ID_MDREQUEST:
            encodeMDRequest( (MDRequest) msg );
            break;
        case EventIds.ID_MASSINSTRUMENTSTATECHANGE:
            encodeMassInstrumentStateChange( (MassInstrumentStateChange) msg );
            break;
        case EventIds.ID_SECURITYSTATUS:
            encodeSecurityStatus( (SecurityStatus) msg );
            break;
        case EventIds.ID_SECURITYDEFINITION:
            encodeSecurityDefinition( (SecurityDefinition) msg );
            break;
        case EventIds.ID_MDINCREFRESH:
            encodeMDIncRefresh( (MDIncRefresh) msg );
            break;
        case EventIds.ID_MDSNAPSHOTFULLREFRESH:
            encodeMDSnapshotFullRefresh( (MDSnapshotFullRefresh) msg );
            break;
        case EventIds.ID_SECURITYDEFINITIONUPDATE:
            encodeSecurityDefinitionUpdate( (SecurityDefinitionUpdate) msg );
            break;
        case EventIds.ID_PRODUCTSNAPSHOT:
            encodeProductSnapshot( (ProductSnapshot) msg );
            break;
        case 2:
        case 3:
        case 9:
        case 12:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        default:
            _builder.start();
            break;
        }
    }

    @Override public final int getLength() { return _builder.getLength(); }
    @Override public final int getOffset() { return _builder.getOffset(); }


    public final void encodeHeartbeat( final Heartbeat msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Heartbeat );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionaryMD50.testReqID, msg.getTestReqID() );        // tag112
        _builder.encodeEnvelope();
    }

    public final void encodeLogon( final Logon msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Logon );
        _builder.encodeString( FixDictionaryMD50.SenderCompID, msg.getSenderCompId() );        // tag49
        _builder.encodeString( FixDictionaryMD50.TargetCompID, msg.getTargetCompId() );        // tag56
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        final EncryptMethod tEncryptMethod = msg.getEncryptMethod();
        if ( tEncryptMethod != null ) _builder.encodeByte( FixDictionaryMD50.EncryptMethod, tEncryptMethod.getVal() );        // tag98
        _builder.encodeInt( FixDictionaryMD50.heartBtInt, msg.getHeartBtInt() );        // tag108
        _builder.encodeInt( FixDictionaryMD50.RawDataLen, msg.getRawDataLen() );        // tag95
        _builder.encodeString( FixDictionaryMD50.RawData, msg.getRawData() );        // tag96
        _builder.encodeBool( FixDictionaryMD50.ResetSeqNumFlag, msg.getResetSeqNumFlag() );        // tag141
        _builder.encodeInt( FixDictionaryMD50.NextExpectedMsgSeqNum, msg.getNextExpectedMsgSeqNum() );        // tag789
        _builder.encodeEnvelope();
    }

    public final void encodeLogout( final Logout msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_Logout );
        _builder.encodeString( FixDictionaryMD50.SenderCompID, msg.getSenderCompId() );        // tag49
        _builder.encodeString( FixDictionaryMD50.TargetCompID, msg.getTargetCompId() );        // tag56
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeString( FixDictionaryMD50.Text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeSessionReject( final SessionReject msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SessionReject );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionaryMD50.RefSeqNum, msg.getRefSeqNum() );        // tag45
        _builder.encodeInt( FixDictionaryMD50.RefTagID, msg.getRefTagID() );        // tag371
        _builder.encodeString( FixDictionaryMD50.RefMsgType, msg.getRefMsgType() );        // tag372
        final SessionRejectReason tSessionRejectReason = msg.getSessionRejectReason();
        if ( tSessionRejectReason != null ) _builder.encodeTwoByte( FixDictionaryMD50.SessionRejectReason, tSessionRejectReason.getVal() );        // tag373
        _builder.encodeString( FixDictionaryMD50.Text, msg.getText() );        // tag58
        _builder.encodeEnvelope();
    }

    public final void encodeResendRequest( final ResendRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_ResendRequest );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionaryMD50.BeginSeqNo, msg.getBeginSeqNo() );        // tag7
        _builder.encodeInt( FixDictionaryMD50.EndSeqNo, msg.getEndSeqNo() );        // tag16
        _builder.encodeEnvelope();
    }

    public final void encodeSequenceReset( final SequenceReset msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SequenceReset );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeBool( FixDictionaryMD50.GapFillFlag, msg.getGapFillFlag() );        // tag123
        _builder.encodeInt( FixDictionaryMD50.NewSeqNo, msg.getNewSeqNo() );        // tag36
        _builder.encodeEnvelope();
    }

    public final void encodeTestRequest( final TestRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_TestRequest );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionaryMD50.testReqID, msg.getTestReqID() );        // tag112
        _builder.encodeEnvelope();
    }

    public final void encodeTradingSessionStatus( final TradingSessionStatus msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_TradingSessionStatus );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionaryMD50.marketSegmentId, msg.getMarketSegmentID() );        // tag1300
        final TradingSessionID tTradingSessionID = msg.getTradingSessionID();
        if ( tTradingSessionID != null ) _builder.encodeByte( FixDictionaryMD50.TradingSessionID, tTradingSessionID.getVal() );        // tag336
        final TradingSessionSubID tTradingSessionSubID = msg.getTradingSessionSubID();
        if ( tTradingSessionSubID != null ) _builder.encodeByte( FixDictionaryMD50.TradingSessionSubID, tTradingSessionSubID.getVal() );        // tag625
        final TradSesStatus tTradSesStatus = msg.getTradSesStatus();
        if ( tTradSesStatus != null ) _builder.encodeByte( FixDictionaryMD50.TradSesStatus, tTradSesStatus.getVal() );        // tag340
        _builder.encodeUTCTimestamp( FixDictionaryMD50.TransactTime, now );        // tag60
        _builder.encodeEnvelope();
    }

    public final void encodeMDRequest( final MDRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_MDRequest );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeString( FixDictionaryMD50.mdReqId, msg.getMdReqId() );        // tag262
        final SubsReqType tSubsReqType = msg.getSubsReqType();
        if ( tSubsReqType != null ) _builder.encodeByte( FixDictionaryMD50.subsReqType, tSubsReqType.getVal() );        // tag263
        _builder.encodeInt( FixDictionaryMD50.marketDepth, msg.getMarketDepth() );        // tag264
        // tag146
        _builder.encodeEnvelope();
    }

    public final void encodeMassInstrumentStateChange( final MassInstrumentStateChange msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeTwoByte( 35, MSG_MassInstrumentStateChange );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionaryMD50.marketSegmentId, msg.getMarketSegmentID() );        // tag1300
        _builder.encodeInt( FixDictionaryMD50.instrumentScopeProductComplex, msg.getInstrumentScopeProductComplex() );        // tag1544
        _builder.encodeInt( FixDictionaryMD50.securityMassTradingStatus, msg.getSecurityMassTradingStatus() );        // tag1679
        _builder.encodeUTCTimestamp( FixDictionaryMD50.TransactTime, now );        // tag60
        // tag146
        _builder.encodeEnvelope();
    }

    public final void encodeSecurityStatus( final SecurityStatus msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SecurityStatus );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionaryMD50.securityID, msg.getSecurityID() );        // tag48
        _builder.encodeInt( FixDictionaryMD50.TradeDate, msg.getTradeDate() );        // tag75
        _builder.encodePrice( FixDictionaryMD50.highPx, msg.getHighPx() );        // tag332
        _builder.encodePrice( FixDictionaryMD50.lowPx, msg.getLowPx() );        // tag333
        final SecurityTradingStatus tSecurityTradingStatus = msg.getSecurityTradingStatus();
        if ( tSecurityTradingStatus != null ) _builder.encodeTwoByte( FixDictionaryMD50.securityTradingStatus, tSecurityTradingStatus.getVal() );        // tag326
        _builder.encodeInt( FixDictionaryMD50.haltReason, msg.getHaltReason() );        // tag327
        _builder.encodeInt( FixDictionaryMD50.SecurityTradingEvent, msg.getSecurityTradingEvent() );        // tag1174
        _builder.encodeString( FixDictionaryMD50.Symbol, msg.getSymbol() );        // tag55
        _builder.encodeEnvelope();
    }

    public final void encodeSecurityDefinition( final SecurityDefinition msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_SecurityDefinition );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, msg.getEventTimestamp() ); // tag52;
        _builder.encodeLong( FixDictionaryMD50.uniqueInstId, msg.getUniqueInstId() );        // tag9814
        _builder.encodeInt( FixDictionaryMD50.totNumReports, msg.getTotNumReports() );        // tag911
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionaryMD50.securityID, msg.getSecurityID() );        // tag48
        _builder.encodeString( FixDictionaryMD50.Symbol, msg.getSymbol() );        // tag55
        final SecurityType tSecurityType = msg.getSecurityType();
        if ( tSecurityType != null ) _builder.encodeBytes( FixDictionaryMD50.securityType, tSecurityType.getVal() );        // tag167
        _builder.encodeString( FixDictionaryMD50.securityLongDesc, msg.getSecurityLongDesc() );        // tag9800

        {
            SecDefEventImpl tmpSecDefEvent = (SecDefEventImpl)msg.getEvents();
            int counterSecDefEvent = msg.getNoEvents();
            _builder.encodeInt( FixDictionaryMD50.noEvents, counterSecDefEvent );
            int i=0;

            while ( tmpSecDefEvent != null ) { 
                ++i;
                final SecDefEventType tEventType = tmpSecDefEvent.getEventType();
                if ( tEventType != null ) _builder.encodeByte( FixDictionaryMD50.eventType, tEventType.getVal() );        // tag865
                _builder.encodeLong( FixDictionaryMD50.eventDate, tmpSecDefEvent.getEventDate() );        // tag866
                _builder.encodeLong( FixDictionaryMD50.eventTime, tmpSecDefEvent.getEventTime() );        // tag1145

                tmpSecDefEvent = tmpSecDefEvent.getNext();
            }

            if ( i != counterSecDefEvent && ! (i==0 && Utils.isNull( counterSecDefEvent ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup events, found "+ i + " entries but expected " + counterSecDefEvent + " entries : " + msg.toString() );
            }
        }

        // tag864
        final SecurityUpdateAction tSecurityUpdateAction = msg.getSecurityUpdateAction();
        if ( tSecurityUpdateAction != null ) _builder.encodeByte( FixDictionaryMD50.SecurityUpdateAction, tSecurityUpdateAction.getVal() );        // tag980

        {
            SecDefLegImpl tmpSecDefLeg = (SecDefLegImpl)msg.getLegs();
            int counterSecDefLeg = msg.getNoLegs();
            _builder.encodeInt( FixDictionaryMD50.noLegs, counterSecDefLeg );
            int i=0;

            while ( tmpSecDefLeg != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.legSymbol, tmpSecDefLeg.getLegSymbol() );        // tag600
                _builder.encodeString( FixDictionaryMD50.legSecurityDesc, tmpSecDefLeg.getLegSecurityDesc() );        // tag620
                _builder.encodeInt( FixDictionaryMD50.legRatioQty, tmpSecDefLeg.getLegRatioQty() );        // tag623
                final Side tLegSide = tmpSecDefLeg.getLegSide();
                if ( tLegSide != null ) _builder.encodeByte( FixDictionaryMD50.legSide, tLegSide.getVal() );        // tag624
                _builder.encodeString( FixDictionaryMD50.legSecurityID, tmpSecDefLeg.getLegSecurityID() );        // tag602
                final SecurityIDSource tLegSecurityIDSource = tmpSecDefLeg.getLegSecurityIDSource();
                if ( tLegSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.legSecurityIDSource, tLegSecurityIDSource.getVal() );        // tag603

                tmpSecDefLeg = tmpSecDefLeg.getNext();
            }

            if ( i != counterSecDefLeg && ! (i==0 && Utils.isNull( counterSecDefLeg ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup legs, found "+ i + " entries but expected " + counterSecDefLeg + " entries : " + msg.toString() );
            }
        }

        // tag555
        _builder.encodePrice( FixDictionaryMD50.tradingReferencePrice, msg.getTradingReferencePrice() );        // tag1150
        _builder.encodePrice( FixDictionaryMD50.highLimitPx, msg.getHighLimitPx() );        // tag1149
        _builder.encodePrice( FixDictionaryMD50.lowLimitPx, msg.getLowLimitPx() );        // tag1148
        _builder.encodeInt( FixDictionaryMD50.TickRule, msg.getTickRule() );        // tag6350
        _builder.encodePrice( FixDictionaryMD50.futPointValue, msg.getFutPointValue() );        // tag1700
        _builder.encodePrice( FixDictionaryMD50.minPriceIncrement, msg.getMinPriceIncrement() );        // tag969
        final UnitOfMeasure tUnitOfMeasure = msg.getUnitOfMeasure();
        if ( tUnitOfMeasure != null ) _builder.encodeBytes( FixDictionaryMD50.unitOfMeasure, tUnitOfMeasure.getVal() );        // tag996
        _builder.encodePrice( FixDictionaryMD50.minPriceIncrementAmount, msg.getMinPriceIncrementAmount() );        // tag1146
        _builder.encodePrice( FixDictionaryMD50.unitOfMeasureQty, msg.getUnitOfMeasureQty() );        // tag1147
        _builder.encodeString( FixDictionaryMD50.securityGroup, msg.getSecurityGroup() );        // tag1151
        _builder.encodeString( FixDictionaryMD50.securityDesc, msg.getSecurityDesc() );        // tag107
        _builder.encodeString( FixDictionaryMD50.CFICode, msg.getCFICode() );        // tag461
        final ProductComplex tUnderlyingProduct = msg.getUnderlyingProduct();
        if ( tUnderlyingProduct != null ) _builder.encodeTwoByte( FixDictionaryMD50.underlyingProduct, tUnderlyingProduct.getVal() );        // tag462
        final ExchangeCode tSecurityExchange = msg.getSecurityExchange();
        if ( tSecurityExchange != null ) _builder.encodeBytes( FixDictionaryMD50.securityExchange, tSecurityExchange.getVal() );        // tag207
        final SecurityIDSource tUnderlyingSecurityIDSource = msg.getUnderlyingSecurityIDSource();
        if ( tUnderlyingSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.underlyingSecurityIDSource, tUnderlyingSecurityIDSource.getVal() );        // tag9801
        _builder.encodeString( FixDictionaryMD50.underlyingSecurityID, msg.getUnderlyingSecurityID() );        // tag9802
        final ExchangeCode tUnderlyingScurityExchange = msg.getUnderlyingScurityExchange();
        if ( tUnderlyingScurityExchange != null ) _builder.encodeBytes( FixDictionaryMD50.underlyingScurityExchange, tUnderlyingScurityExchange.getVal() );        // tag9803
        final ExchangeCode tPrimarySecurityExchange = msg.getPrimarySecurityExchange();
        if ( tPrimarySecurityExchange != null ) _builder.encodeBytes( FixDictionaryMD50.primarySecurityExchange, tPrimarySecurityExchange.getVal() );        // tag9804

        {
            SecurityAltIDImpl tmpSecurityAltID = (SecurityAltIDImpl)msg.getSecurityAltIDs();
            int counterSecurityAltID = msg.getNoSecurityAltID();
            _builder.encodeInt( FixDictionaryMD50.noSecurityAltID, counterSecurityAltID );
            int i=0;

            while ( tmpSecurityAltID != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.securityAltID, tmpSecurityAltID.getSecurityAltID() );        // tag455
                final SecurityIDSource tSecurityAltIDSource = tmpSecurityAltID.getSecurityAltIDSource();
                if ( tSecurityAltIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityAltIDSource, tSecurityAltIDSource.getVal() );        // tag456

                tmpSecurityAltID = tmpSecurityAltID.getNext();
            }

            if ( i != counterSecurityAltID && ! (i==0 && Utils.isNull( counterSecurityAltID ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup securityAltIDs, found "+ i + " entries but expected " + counterSecurityAltID + " entries : " + msg.toString() );
            }
        }

        // tag454
        _builder.encodePrice( FixDictionaryMD50.strikePrice, msg.getStrikePrice() );        // tag202
        final Currency tStrikeCurrency = msg.getStrikeCurrency();
        if ( tStrikeCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.strikeCurrency, tStrikeCurrency.getVal() );        // tag947
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.Currency, tCurrency.getVal() );        // tag15
        final Currency tSettlCurrency = msg.getSettlCurrency();
        if ( tSettlCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.settlCurrency, tSettlCurrency.getVal() );        // tag120
        _builder.encodeLong( FixDictionaryMD50.minTradeVol, msg.getMinTradeVol() );        // tag562
        _builder.encodeLong( FixDictionaryMD50.maxTradeVol, msg.getMaxTradeVol() );        // tag1140

        {
            SDFeedTypeImpl tmpSDFeedType = (SDFeedTypeImpl)msg.getSDFeedTypes();
            int counterSDFeedType = msg.getNoSDFeedTypes();
            _builder.encodeInt( FixDictionaryMD50.noSDFeedTypes, counterSDFeedType );
            int i=0;

            while ( tmpSDFeedType != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.feedType, tmpSDFeedType.getFeedType() );        // tag1022
                _builder.encodeInt( FixDictionaryMD50.marketDepth, tmpSDFeedType.getMarketDepth() );        // tag264

                tmpSDFeedType = tmpSDFeedType.getNext();
            }

            if ( i != counterSDFeedType && ! (i==0 && Utils.isNull( counterSDFeedType ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup SDFeedTypes, found "+ i + " entries but expected " + counterSDFeedType + " entries : " + msg.toString() );
            }
        }

        // tag1141
        _builder.encodeInt( FixDictionaryMD50.maturityMonthYear, msg.getMaturityMonthYear() );        // tag200
        _builder.encodeString( FixDictionaryMD50.applID, msg.getApplID() );        // tag1180
        _builder.encodePrice( FixDictionaryMD50.displayFactor, msg.getDisplayFactor() );        // tag9787
        _builder.encodePrice( FixDictionaryMD50.priceRatio, msg.getPriceRatio() );        // tag5770
        _builder.encodeInt( FixDictionaryMD50.contractMultiplierType, msg.getContractMultiplierType() );        // tag1435
        _builder.encodePrice( FixDictionaryMD50.contractMultiplier, msg.getContractMultiplier() );        // tag231
        _builder.encodeInt( FixDictionaryMD50.openInterestQty, msg.getOpenInterestQty() );        // tag5792
        _builder.encodeInt( FixDictionaryMD50.tradingReferenceDate, msg.getTradingReferenceDate() );        // tag5796
        _builder.encodeInt( FixDictionaryMD50.minQty, msg.getMinQty() );        // tag110
        _builder.encodePrice( FixDictionaryMD50.pricePrecision, msg.getPricePrecision() );        // tag1200
        _builder.encodeLong( FixDictionaryMD50.commonSecurityId, msg.getCommonSecurityId() );        // tag9805
        _builder.encodeLong( FixDictionaryMD50.parentCompanyId, msg.getParentCompanyId() );        // tag9806
        _builder.encodeString( FixDictionaryMD50.companyName, msg.getCompanyName() );        // tag9811
        _builder.encodeLong( FixDictionaryMD50.gicsCode, msg.getGicsCode() );        // tag9812
        _builder.encodeUTCTimestamp( FixDictionaryMD50.getOutDate, msg.getGetOutDate() );        // tag9813
        _builder.encodeUTCTimestamp( FixDictionaryMD50.deadTimestamp, msg.getDeadTimestamp() );        // tag9815
        _builder.encodeUTCTimestamp( FixDictionaryMD50.startTimestamp, msg.getStartTimestamp() );        // tag9817
        _builder.encodeUTCTimestamp( FixDictionaryMD50.endTimestamp, msg.getEndTimestamp() );        // tag9816
        final DataSrc tDataSrc = msg.getDataSrc();
        if ( tDataSrc != null ) _builder.encodeBytes( FixDictionaryMD50.dataSrc, tDataSrc.getVal() );        // tag9818
        final SecDefSpecialType tSecDefSpecialType = msg.getSecDefSpecialType();
        if ( tSecDefSpecialType != null ) _builder.encodeByte( FixDictionaryMD50.SecDefSpecialType, tSecDefSpecialType.getVal() );        // tag9819
        final CompanyStatusType tCompanyStatusType = msg.getCompanyStatusType();
        if ( tCompanyStatusType != null ) _builder.encodeBytes( FixDictionaryMD50.CompanyStatusType, tCompanyStatusType.getVal() );        // tag9820
        _builder.encodeEnvelope();
    }

    public final void encodeMDIncRefresh( final MDIncRefresh msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_MDIncRefresh );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52

        {
            MDEntryImpl tmpMDEntry = (MDEntryImpl)msg.getMDEntries();
            int counterMDEntry = msg.getNoMDEntries();
            _builder.encodeInt( FixDictionaryMD50.noMDEntries, counterMDEntry );
            int i=0;

            while ( tmpMDEntry != null ) { 
                ++i;
                final MDUpdateAction tMdUpdateAction = tmpMDEntry.getMdUpdateAction();
                if ( tMdUpdateAction != null ) _builder.encodeByte( FixDictionaryMD50.mdUpdateAction, tMdUpdateAction.getVal() );        // tag279
                _builder.encodeInt( FixDictionaryMD50.mdPriceLevel, tmpMDEntry.getMdPriceLevel() );        // tag1023
                final MDEntryType tMdEntryType = tmpMDEntry.getMdEntryType();
                if ( tMdEntryType != null ) _builder.encodeByte( FixDictionaryMD50.mdEntryType, tMdEntryType.getVal() );        // tag269
                _builder.encodeInt( FixDictionaryMD50.mdEntryTime, tmpMDEntry.getMdEntryTime() );        // tag273
                final SecurityIDSource tSecurityIDSource = tmpMDEntry.getSecurityIDSource();
                if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
                _builder.encodeString( FixDictionaryMD50.securityID, tmpMDEntry.getSecurityID() );        // tag48
                _builder.encodeInt( FixDictionaryMD50.repeatSeq, tmpMDEntry.getRepeatSeq() );        // tag83
                _builder.encodePrice( FixDictionaryMD50.mdEntryPx, tmpMDEntry.getMdEntryPx() );        // tag270
                _builder.encodeInt( FixDictionaryMD50.mdEntrySize, tmpMDEntry.getMdEntrySize() );        // tag271
                _builder.encodeInt( FixDictionaryMD50.numberOfOrders, tmpMDEntry.getNumberOfOrders() );        // tag346
                final TradingSessionID tTradingSessionID = tmpMDEntry.getTradingSessionID();
                if ( tTradingSessionID != null ) _builder.encodeByte( FixDictionaryMD50.TradingSessionID, tTradingSessionID.getVal() );        // tag336

                tmpMDEntry = tmpMDEntry.getNext();
            }

            if ( i != counterMDEntry && ! (i==0 && Utils.isNull( counterMDEntry ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup MDEntries, found "+ i + " entries but expected " + counterMDEntry + " entries : " + msg.toString() );
            }
        }

        // tag268
        _builder.encodeEnvelope();
    }

    public final void encodeMDSnapshotFullRefresh( final MDSnapshotFullRefresh msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeByte( 35, MSG_MDSnapshotFullRefresh );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        _builder.encodeInt( FixDictionaryMD50.lastMsgSeqNumProcessed, msg.getLastMsgSeqNumProcessed() );        // tag369
        _builder.encodeInt( FixDictionaryMD50.totNumReports, msg.getTotNumReports() );        // tag911
        _builder.encodeInt( FixDictionaryMD50.mdBookType, msg.getMdBookType() );        // tag1021
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionaryMD50.securityID, msg.getSecurityID() );        // tag48
        _builder.encodeInt( FixDictionaryMD50.mdSecurityTradingStatus, msg.getMdSecurityTradingStatus() );        // tag1682

        {
            MDSnapEntryImpl tmpMDSnapEntry = (MDSnapEntryImpl)msg.getMDEntries();
            int counterMDSnapEntry = msg.getNoMDEntries();
            _builder.encodeInt( FixDictionaryMD50.noMDEntries, counterMDSnapEntry );
            int i=0;

            while ( tmpMDSnapEntry != null ) { 
                ++i;
                final MDEntryType tMdEntryType = tmpMDSnapEntry.getMdEntryType();
                if ( tMdEntryType != null ) _builder.encodeByte( FixDictionaryMD50.mdEntryType, tMdEntryType.getVal() );        // tag269
                _builder.encodePrice( FixDictionaryMD50.mdEntryPx, tmpMDSnapEntry.getMdEntryPx() );        // tag270
                _builder.encodeInt( FixDictionaryMD50.mdEntrySize, tmpMDSnapEntry.getMdEntrySize() );        // tag271
                _builder.encodeInt( FixDictionaryMD50.mdEntryTime, tmpMDSnapEntry.getMdEntryTime() );        // tag273
                final TickDirection tTickDirection = tmpMDSnapEntry.getTickDirection();
                if ( tTickDirection != null ) _builder.encodeByte( FixDictionaryMD50.tickDirection, tTickDirection.getVal() );        // tag274
                _builder.encodeInt( FixDictionaryMD50.tradeVolume, tmpMDSnapEntry.getTradeVolume() );        // tag1020
                _builder.encodeInt( FixDictionaryMD50.mdPriceLevel, tmpMDSnapEntry.getMdPriceLevel() );        // tag1023

                tmpMDSnapEntry = tmpMDSnapEntry.getNext();
            }

            if ( i != counterMDSnapEntry && ! (i==0 && Utils.isNull( counterMDSnapEntry ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup MDEntries, found "+ i + " entries but expected " + counterMDSnapEntry + " entries : " + msg.toString() );
            }
        }

        // tag268
        _builder.encodeEnvelope();
    }

    public final void encodeSecurityDefinitionUpdate( final SecurityDefinitionUpdate msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeTwoByte( 35, MSG_SecurityDefinitionUpdate );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
            _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, msg.getEventTimestamp() ); // tag52;
        _builder.encodeInt( FixDictionaryMD50.totNumReports, msg.getTotNumReports() );        // tag911
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionaryMD50.securityID, msg.getSecurityID() );        // tag48
        _builder.encodeString( FixDictionaryMD50.Symbol, msg.getSymbol() );        // tag55
        final SecurityType tSecurityType = msg.getSecurityType();
        if ( tSecurityType != null ) _builder.encodeBytes( FixDictionaryMD50.securityType, tSecurityType.getVal() );        // tag167

        {
            SecDefEventImpl tmpSecDefEvent = (SecDefEventImpl)msg.getEvents();
            int counterSecDefEvent = msg.getNoEvents();
            _builder.encodeInt( FixDictionaryMD50.noEvents, counterSecDefEvent );
            int i=0;

            while ( tmpSecDefEvent != null ) { 
                ++i;
                final SecDefEventType tEventType = tmpSecDefEvent.getEventType();
                if ( tEventType != null ) _builder.encodeByte( FixDictionaryMD50.eventType, tEventType.getVal() );        // tag865
                _builder.encodeLong( FixDictionaryMD50.eventDate, tmpSecDefEvent.getEventDate() );        // tag866
                _builder.encodeLong( FixDictionaryMD50.eventTime, tmpSecDefEvent.getEventTime() );        // tag1145

                tmpSecDefEvent = tmpSecDefEvent.getNext();
            }

            if ( i != counterSecDefEvent && ! (i==0 && Utils.isNull( counterSecDefEvent ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup events, found "+ i + " entries but expected " + counterSecDefEvent + " entries : " + msg.toString() );
            }
        }

        // tag864
        final SecurityUpdateAction tSecurityUpdateAction = msg.getSecurityUpdateAction();
        if ( tSecurityUpdateAction != null ) _builder.encodeByte( FixDictionaryMD50.SecurityUpdateAction, tSecurityUpdateAction.getVal() );        // tag980

        {
            SecDefLegImpl tmpSecDefLeg = (SecDefLegImpl)msg.getLegs();
            int counterSecDefLeg = msg.getNoLegs();
            _builder.encodeInt( FixDictionaryMD50.noLegs, counterSecDefLeg );
            int i=0;

            while ( tmpSecDefLeg != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.legSymbol, tmpSecDefLeg.getLegSymbol() );        // tag600
                _builder.encodeString( FixDictionaryMD50.legSecurityDesc, tmpSecDefLeg.getLegSecurityDesc() );        // tag620
                _builder.encodeInt( FixDictionaryMD50.legRatioQty, tmpSecDefLeg.getLegRatioQty() );        // tag623
                final Side tLegSide = tmpSecDefLeg.getLegSide();
                if ( tLegSide != null ) _builder.encodeByte( FixDictionaryMD50.legSide, tLegSide.getVal() );        // tag624
                _builder.encodeString( FixDictionaryMD50.legSecurityID, tmpSecDefLeg.getLegSecurityID() );        // tag602
                final SecurityIDSource tLegSecurityIDSource = tmpSecDefLeg.getLegSecurityIDSource();
                if ( tLegSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.legSecurityIDSource, tLegSecurityIDSource.getVal() );        // tag603

                tmpSecDefLeg = tmpSecDefLeg.getNext();
            }

            if ( i != counterSecDefLeg && ! (i==0 && Utils.isNull( counterSecDefLeg ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup legs, found "+ i + " entries but expected " + counterSecDefLeg + " entries : " + msg.toString() );
            }
        }

        // tag555
        _builder.encodePrice( FixDictionaryMD50.tradingReferencePrice, msg.getTradingReferencePrice() );        // tag1150
        _builder.encodePrice( FixDictionaryMD50.highLimitPx, msg.getHighLimitPx() );        // tag1149
        _builder.encodePrice( FixDictionaryMD50.lowLimitPx, msg.getLowLimitPx() );        // tag1148
        _builder.encodePrice( FixDictionaryMD50.minPriceIncrement, msg.getMinPriceIncrement() );        // tag969
        final UnitOfMeasure tUnitOfMeasure = msg.getUnitOfMeasure();
        if ( tUnitOfMeasure != null ) _builder.encodeBytes( FixDictionaryMD50.unitOfMeasure, tUnitOfMeasure.getVal() );        // tag996
        _builder.encodePrice( FixDictionaryMD50.minPriceIncrementAmount, msg.getMinPriceIncrementAmount() );        // tag1146
        _builder.encodePrice( FixDictionaryMD50.unitOfMeasureQty, msg.getUnitOfMeasureQty() );        // tag1147
        _builder.encodeString( FixDictionaryMD50.securityGroup, msg.getSecurityGroup() );        // tag1151
        _builder.encodeString( FixDictionaryMD50.securityDesc, msg.getSecurityDesc() );        // tag107
        _builder.encodeString( FixDictionaryMD50.CFICode, msg.getCFICode() );        // tag461
        final ProductComplex tUnderlyingProduct = msg.getUnderlyingProduct();
        if ( tUnderlyingProduct != null ) _builder.encodeTwoByte( FixDictionaryMD50.underlyingProduct, tUnderlyingProduct.getVal() );        // tag462
        final ExchangeCode tSecurityExchange = msg.getSecurityExchange();
        if ( tSecurityExchange != null ) _builder.encodeBytes( FixDictionaryMD50.securityExchange, tSecurityExchange.getVal() );        // tag207

        {
            SecurityAltIDImpl tmpSecurityAltID = (SecurityAltIDImpl)msg.getSecurityAltIDs();
            int counterSecurityAltID = msg.getNoSecurityAltID();
            _builder.encodeInt( FixDictionaryMD50.noSecurityAltID, counterSecurityAltID );
            int i=0;

            while ( tmpSecurityAltID != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.securityAltID, tmpSecurityAltID.getSecurityAltID() );        // tag455
                final SecurityIDSource tSecurityAltIDSource = tmpSecurityAltID.getSecurityAltIDSource();
                if ( tSecurityAltIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityAltIDSource, tSecurityAltIDSource.getVal() );        // tag456

                tmpSecurityAltID = tmpSecurityAltID.getNext();
            }

            if ( i != counterSecurityAltID && ! (i==0 && Utils.isNull( counterSecurityAltID ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup securityAltIDs, found "+ i + " entries but expected " + counterSecurityAltID + " entries : " + msg.toString() );
            }
        }

        // tag454
        _builder.encodePrice( FixDictionaryMD50.strikePrice, msg.getStrikePrice() );        // tag202
        final Currency tStrikeCurrency = msg.getStrikeCurrency();
        if ( tStrikeCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.strikeCurrency, tStrikeCurrency.getVal() );        // tag947
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.Currency, tCurrency.getVal() );        // tag15
        final Currency tSettlCurrency = msg.getSettlCurrency();
        if ( tSettlCurrency != null ) _builder.encodeBytes( FixDictionaryMD50.settlCurrency, tSettlCurrency.getVal() );        // tag120
        _builder.encodeLong( FixDictionaryMD50.minTradeVol, msg.getMinTradeVol() );        // tag562
        _builder.encodeLong( FixDictionaryMD50.maxTradeVol, msg.getMaxTradeVol() );        // tag1140

        {
            SDFeedTypeImpl tmpSDFeedType = (SDFeedTypeImpl)msg.getSDFeedTypes();
            int counterSDFeedType = msg.getNoSDFeedTypes();
            _builder.encodeInt( FixDictionaryMD50.noSDFeedTypes, counterSDFeedType );
            int i=0;

            while ( tmpSDFeedType != null ) { 
                ++i;
                _builder.encodeString( FixDictionaryMD50.feedType, tmpSDFeedType.getFeedType() );        // tag1022
                _builder.encodeInt( FixDictionaryMD50.marketDepth, tmpSDFeedType.getMarketDepth() );        // tag264

                tmpSDFeedType = tmpSDFeedType.getNext();
            }

            if ( i != counterSDFeedType && ! (i==0 && Utils.isNull( counterSDFeedType ) ) ) {
                throw new RuntimeEncodingException( "Mismatch in counters in subGroup SDFeedTypes, found "+ i + " entries but expected " + counterSDFeedType + " entries : " + msg.toString() );
            }
        }

        // tag1141
        _builder.encodeInt( FixDictionaryMD50.maturityMonthYear, msg.getMaturityMonthYear() );        // tag200
        _builder.encodeString( FixDictionaryMD50.applID, msg.getApplID() );        // tag1180
        _builder.encodePrice( FixDictionaryMD50.displayFactor, msg.getDisplayFactor() );        // tag9787
        _builder.encodePrice( FixDictionaryMD50.priceRatio, msg.getPriceRatio() );        // tag5770
        _builder.encodeInt( FixDictionaryMD50.contractMultiplierType, msg.getContractMultiplierType() );        // tag1435
        _builder.encodePrice( FixDictionaryMD50.contractMultiplier, msg.getContractMultiplier() );        // tag231
        _builder.encodeInt( FixDictionaryMD50.openInterestQty, msg.getOpenInterestQty() );        // tag5792
        _builder.encodeInt( FixDictionaryMD50.tradingReferenceDate, msg.getTradingReferenceDate() );        // tag5796
        _builder.encodeInt( FixDictionaryMD50.minQty, msg.getMinQty() );        // tag110
        _builder.encodePrice( FixDictionaryMD50.pricePrecision, msg.getPricePrecision() );        // tag1200
        _builder.encodeUTCTimestamp( FixDictionaryMD50.deadTimestamp, msg.getDeadTimestamp() );        // tag9815
        _builder.encodeUTCTimestamp( FixDictionaryMD50.startTimestamp, msg.getStartTimestamp() );        // tag9817
        _builder.encodeUTCTimestamp( FixDictionaryMD50.endTimestamp, msg.getEndTimestamp() );        // tag9816
        final DataSrc tDataSrc = msg.getDataSrc();
        if ( tDataSrc != null ) _builder.encodeBytes( FixDictionaryMD50.dataSrc, tDataSrc.getVal() );        // tag9818
        final SecDefSpecialType tSecDefSpecialType = msg.getSecDefSpecialType();
        if ( tSecDefSpecialType != null ) _builder.encodeByte( FixDictionaryMD50.SecDefSpecialType, tSecDefSpecialType.getVal() );        // tag9819
        final CompanyStatusType tCompanyStatusType = msg.getCompanyStatusType();
        if ( tCompanyStatusType != null ) _builder.encodeBytes( FixDictionaryMD50.CompanyStatusType, tCompanyStatusType.getVal() );        // tag9820
        _builder.encodeEnvelope();
    }

    public final void encodeProductSnapshot( final ProductSnapshot msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start();
        _builder.encodeTwoByte( 35, MSG_ProductSnapshot );
            _builder.encodeString( FixDictionaryMD50.SenderCompID, _senderCompId ); // tag49;
            _builder.encodeString( FixDictionaryMD50.TargetCompID, _targetCompId ); // tag56;
        _builder.encodeInt( FixDictionaryMD50.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        _builder.encodeUTCTimestamp( FixDictionaryMD50.SendingTime, now );        // tag52
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) _builder.encodeByte( FixDictionaryMD50.securityIDSource, tSecurityIDSource.getVal() );        // tag22
        _builder.encodeString( FixDictionaryMD50.securityID, msg.getSecurityID() );        // tag48
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
        MDBSEEncoder e = new MDBSEEncoder( getComponentId(), _majorVersion, _minorVersion, buf, offset );
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
        
        if ( msg.getReusableType().getSubId() == EventIds.ID_NEWORDERSINGLE ) {
            final NewOrderSingle nos = (NewOrderSingle) msg;

            final long tickIn   = nos.getOrderReceived();

            final long microTickToTrade = (msgSent - tickIn) / 1000;
            
            outBuf.append( STATS ).append( microTickToTrade ).append( STAT_END );
        }
    }


/*
 * HANDCODED ENCODER METHDOS
 */

 

}
