

// Decoder highly optimised specifically for OrderManager uses Client/Market event specialisations
// Not for use in recovery OR for more general use

package com.rr.model.generated.fix.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import java.util.HashMap;
import java.util.Map;
import com.rr.core.utils.*;
import com.rr.core.codec.*;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.factories.*;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.internal.type.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.utils.StringUtils;
import com.rr.model.generated.fix.model.defn.FixDictionary50;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;

@SuppressWarnings( "unused" )

public final class Standard50DecoderOMS extends AbstractFixDecoder50 {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs

    final String _id;
    OrdStatus _ordStatus = null;    // Tag 39
    ExecType _execType = null;    // Tag 150
    Side _side = null;    // Tag 54
    MultiLegReportingType _multiLegReportingType = null;    // Tag 442
    LiquidityInd _liquidityInd = null;    // Tag 851
    TradingStatus _tradingStatus = null;    // Tag 1700
    ExecRestatementReason _execRestatementReason = null;    // Tag 378

   // exec rpt only populated after all fields processed
   // only generate vars that are required

   // write String start and length vars required for ExecRpts

    int _clOrdIdStart = 0;    // tag 11
    int _clOrdIdLen = 0;    // tag 11
    int _execIdStart = 0;    // tag 17
    int _execIdLen = 0;    // tag 17
    int _orderIdStart = 0;    // tag 37
    int _orderIdLen = 0;    // tag 37
    int _textStart = 0;    // tag 58
    int _textLen = 0;    // tag 58
    int _parentClOrdIdStart = 0;    // tag 526
    int _parentClOrdIdLen = 0;    // tag 526
    int _lastMktStart = 0;    // tag 30
    int _lastMktLen = 0;    // tag 30
    int _securityDescStart = 0;    // tag 107
    int _securityDescLen = 0;    // tag 107
    int _origClOrdIdStart = 0;    // tag 41
    int _origClOrdIdLen = 0;    // tag 41
    int _execRefIDStart = 0;    // tag 19
    int _execRefIDLen = 0;    // tag 19

   // write value holders

    int _msgSeqNum = Constants.UNSET_INT;    // tag 34
    boolean _possDupFlag = false;    // tag 43
    long _eventTimestamp = Constants.UNSET_LONG;    // tag null
    long _transactTime = Constants.UNSET_LONG;    // tag 60
    double _avgPx = Constants.UNSET_DOUBLE;    // tag 6
    double _cumQty = Constants.UNSET_DOUBLE;    // tag 14
    double _leavesQty = Constants.UNSET_DOUBLE;    // tag 151
    double _lastQty = Constants.UNSET_DOUBLE;    // tag 32
    double _lastPx = Constants.UNSET_DOUBLE;    // tag 31

   // forced var holders

    int _securityIDSourceStart = 0;
    int _securityIDSourceLen = 0;
    int _securityIDStart = 0;
    int _securityIDLen = 0;
    int _securityExchangeStart = 0;
    int _securityExchangeLen = 0;

   // Pools

    private final SuperPool<ClientNewOrderSingleImpl> _newOrderSinglePool = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
    private final ClientNewOrderSingleFactory _newOrderSingleFactory = new ClientNewOrderSingleFactory( _newOrderSinglePool );

    private final SuperPool<ClientCancelReplaceRequestImpl> _cancelReplaceRequestPool = SuperpoolManager.instance().getSuperPool( ClientCancelReplaceRequestImpl.class );
    private final ClientCancelReplaceRequestFactory _cancelReplaceRequestFactory = new ClientCancelReplaceRequestFactory( _cancelReplaceRequestPool );

    private final SuperPool<ClientCancelRequestImpl> _cancelRequestPool = SuperpoolManager.instance().getSuperPool( ClientCancelRequestImpl.class );
    private final ClientCancelRequestFactory _cancelRequestFactory = new ClientCancelRequestFactory( _cancelRequestPool );

    private final SuperPool<MarketCancelRejectImpl> _cancelRejectPool = SuperpoolManager.instance().getSuperPool( MarketCancelRejectImpl.class );
    private final MarketCancelRejectFactory _cancelRejectFactory = new MarketCancelRejectFactory( _cancelRejectPool );

    private final SuperPool<MarketNewOrderAckImpl> _newOrderAckPool = SuperpoolManager.instance().getSuperPool( MarketNewOrderAckImpl.class );
    private final MarketNewOrderAckFactory _newOrderAckFactory = new MarketNewOrderAckFactory( _newOrderAckPool );

    private final SuperPool<MarketTradeNewImpl> _tradeNewPool = SuperpoolManager.instance().getSuperPool( MarketTradeNewImpl.class );
    private final MarketTradeNewFactory _tradeNewFactory = new MarketTradeNewFactory( _tradeNewPool );

    private final SuperPool<MarketRejectedImpl> _rejectedPool = SuperpoolManager.instance().getSuperPool( MarketRejectedImpl.class );
    private final MarketRejectedFactory _rejectedFactory = new MarketRejectedFactory( _rejectedPool );

    private final SuperPool<MarketCancelledImpl> _cancelledPool = SuperpoolManager.instance().getSuperPool( MarketCancelledImpl.class );
    private final MarketCancelledFactory _cancelledFactory = new MarketCancelledFactory( _cancelledPool );

    private final SuperPool<MarketReplacedImpl> _replacedPool = SuperpoolManager.instance().getSuperPool( MarketReplacedImpl.class );
    private final MarketReplacedFactory _replacedFactory = new MarketReplacedFactory( _replacedPool );

    private final SuperPool<MarketDoneForDayImpl> _doneForDayPool = SuperpoolManager.instance().getSuperPool( MarketDoneForDayImpl.class );
    private final MarketDoneForDayFactory _doneForDayFactory = new MarketDoneForDayFactory( _doneForDayPool );

    private final SuperPool<MarketStoppedImpl> _stoppedPool = SuperpoolManager.instance().getSuperPool( MarketStoppedImpl.class );
    private final MarketStoppedFactory _stoppedFactory = new MarketStoppedFactory( _stoppedPool );

    private final SuperPool<MarketExpiredImpl> _expiredPool = SuperpoolManager.instance().getSuperPool( MarketExpiredImpl.class );
    private final MarketExpiredFactory _expiredFactory = new MarketExpiredFactory( _expiredPool );

    private final SuperPool<MarketSuspendedImpl> _suspendedPool = SuperpoolManager.instance().getSuperPool( MarketSuspendedImpl.class );
    private final MarketSuspendedFactory _suspendedFactory = new MarketSuspendedFactory( _suspendedPool );

    private final SuperPool<MarketRestatedImpl> _restatedPool = SuperpoolManager.instance().getSuperPool( MarketRestatedImpl.class );
    private final MarketRestatedFactory _restatedFactory = new MarketRestatedFactory( _restatedPool );

    private final SuperPool<MarketTradeCorrectImpl> _tradeCorrectPool = SuperpoolManager.instance().getSuperPool( MarketTradeCorrectImpl.class );
    private final MarketTradeCorrectFactory _tradeCorrectFactory = new MarketTradeCorrectFactory( _tradeCorrectPool );

    private final SuperPool<MarketTradeCancelImpl> _tradeCancelPool = SuperpoolManager.instance().getSuperPool( MarketTradeCancelImpl.class );
    private final MarketTradeCancelFactory _tradeCancelFactory = new MarketTradeCancelFactory( _tradeCancelPool );

    private final SuperPool<MarketOrderStatusImpl> _orderStatusPool = SuperpoolManager.instance().getSuperPool( MarketOrderStatusImpl.class );
    private final MarketOrderStatusFactory _orderStatusFactory = new MarketOrderStatusFactory( _orderStatusPool );

    private final SuperPool<MarketPendingNewImpl> _pendingNewPool = SuperpoolManager.instance().getSuperPool( MarketPendingNewImpl.class );
    private final MarketPendingNewFactory _pendingNewFactory = new MarketPendingNewFactory( _pendingNewPool );

    private final SuperPool<MarketPendingCancelImpl> _pendingCancelPool = SuperpoolManager.instance().getSuperPool( MarketPendingCancelImpl.class );
    private final MarketPendingCancelFactory _pendingCancelFactory = new MarketPendingCancelFactory( _pendingCancelPool );

    private final SuperPool<MarketPendingReplaceImpl> _pendingReplacePool = SuperpoolManager.instance().getSuperPool( MarketPendingReplaceImpl.class );
    private final MarketPendingReplaceFactory _pendingReplaceFactory = new MarketPendingReplaceFactory( _pendingReplacePool );

    private final SuperPool<MarketCalculatedImpl> _calculatedPool = SuperpoolManager.instance().getSuperPool( MarketCalculatedImpl.class );
    private final MarketCalculatedFactory _calculatedFactory = new MarketCalculatedFactory( _calculatedPool );

    private final SuperPool<HeartbeatImpl> _heartbeatPool = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final SuperPool<LogonImpl> _logonPool = SuperpoolManager.instance().getSuperPool( LogonImpl.class );
    private final LogonFactory _logonFactory = new LogonFactory( _logonPool );

    private final SuperPool<LogoutImpl> _logoutPool = SuperpoolManager.instance().getSuperPool( LogoutImpl.class );
    private final LogoutFactory _logoutFactory = new LogoutFactory( _logoutPool );

    private final SuperPool<SessionRejectImpl> _sessionRejectPool = SuperpoolManager.instance().getSuperPool( SessionRejectImpl.class );
    private final SessionRejectFactory _sessionRejectFactory = new SessionRejectFactory( _sessionRejectPool );

    private final SuperPool<ResendRequestImpl> _resendRequestPool = SuperpoolManager.instance().getSuperPool( ResendRequestImpl.class );
    private final ResendRequestFactory _resendRequestFactory = new ResendRequestFactory( _resendRequestPool );

    private final SuperPool<SequenceResetImpl> _sequenceResetPool = SuperpoolManager.instance().getSuperPool( SequenceResetImpl.class );
    private final SequenceResetFactory _sequenceResetFactory = new SequenceResetFactory( _sequenceResetPool );

    private final SuperPool<TestRequestImpl> _testRequestPool = SuperpoolManager.instance().getSuperPool( TestRequestImpl.class );
    private final TestRequestFactory _testRequestFactory = new TestRequestFactory( _testRequestPool );


   // Constructors
    public Standard50DecoderOMS( String id ) {
        this( id, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor );
    }

    public Standard50DecoderOMS() {
        this( null, FixVersion.Fix5_0._major, FixVersion.Fix5_0._minor );
    }

    public Standard50DecoderOMS( byte major, byte minor ) {
        this( null, major, minor );
    }
    public Standard50DecoderOMS( String id, byte major, byte minor ) {
        super( major, minor );
        _id = id;
    }

    @Override public FixDecoder newInstance() {
        Standard50DecoderOMS dec = new Standard50DecoderOMS( getComponentId(), _majorVersion, _minorVersion );
        dec.setCompIds( _senderCompId, _senderSubId, _targetCompId, _targetSubId );
        dec.setClientProfile( _clientProfile );
        dec.setInstrumentLocator( _instrumentLocator );
        return dec;
    }


   // decode methods

    @Override public String getComponentId() { return _id; }

    @Override
    protected final Event doMessageDecode() {
        // get message type field
        if ( _fixMsg[_idx] != '3' || _fixMsg[_idx+1] != '5' || _fixMsg[_idx+2] != '=' )
            throwDecodeException( "Fix Messsage missing message type" );
        _idx += 3;

        byte msgType = _fixMsg[ _idx ];
        switch( msgType ) {
        case '8':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeExecReport();
        case 'D':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeNewOrderSingle();
        case 'G':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeCancelReplaceRequest();
        case 'F':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeCancelRequest();
        case '9':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeCancelReject();
        case '0':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeHeartbeat();
        case 'A':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeLogon();
        case '5':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeLogout();
        case '3':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeSessionReject();
        case '2':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeResendRequest();
        case '4':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeSequenceReset();
        case '1':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeTestRequest();
        case '6':
        case '7':
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'B':
        case 'C':
        case 'E':
            break;
        }
        _idx += 2;
        throwDecodeException( "Unsupported fix message type " + msgType );
        return null;
    }


    public final Event decodeExecReport() {
        _ordStatus = null;    // Tag 39
        _execType = null;    // Tag 150
        _side = null;    // Tag 54
        _multiLegReportingType = null;    // Tag 442
        _liquidityInd = null;    // Tag 851
        _tradingStatus = null;    // Tag 1700
        _execRestatementReason = null;    // Tag 378
        _clOrdIdStart = 0;    // tag 11
        _clOrdIdLen = 0;    // tag 11
        _execIdStart = 0;    // tag 17
        _execIdLen = 0;    // tag 17
        _orderIdStart = 0;    // tag 37
        _orderIdLen = 0;    // tag 37
        _textStart = 0;    // tag 58
        _textLen = 0;    // tag 58
        _parentClOrdIdStart = 0;    // tag 526
        _parentClOrdIdLen = 0;    // tag 526
        _lastMktStart = 0;    // tag 30
        _lastMktLen = 0;    // tag 30
        _securityDescStart = 0;    // tag 107
        _securityDescLen = 0;    // tag 107
        _origClOrdIdStart = 0;    // tag 41
        _origClOrdIdLen = 0;    // tag 41
        _execRefIDStart = 0;    // tag 19
        _execRefIDLen = 0;    // tag 19
        _msgSeqNum = Constants.UNSET_INT;    // tag 34
        _possDupFlag = false;    // tag 43
        _eventTimestamp = Constants.UNSET_LONG;    // tag null
        _transactTime = Constants.UNSET_LONG;    // tag 60
        _avgPx = Constants.UNSET_DOUBLE;    // tag 6
        _cumQty = Constants.UNSET_DOUBLE;    // tag 14
        _leavesQty = Constants.UNSET_DOUBLE;    // tag 151
        _lastQty = Constants.UNSET_DOUBLE;    // tag 32
        _lastPx = Constants.UNSET_DOUBLE;    // tag 31
        int start;
        int valLen;
        
        _tag = getTag();
        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.AvgPx:         // tag6
                _avgPx = getDoubleVal();
                break;
            case FixDictionary50.ClOrdId:         // tag11
                _clOrdIdStart = _idx;
                _clOrdIdLen = getValLength();
                break;
            case FixDictionary50.CumQty:         // tag14
                _cumQty = getDoubleVal();
                break;
            case FixDictionary50.ExecID:         // tag17
                _execIdStart = _idx;
                _execIdLen = getValLength();
                break;
            case FixDictionary50.ExecRefID:         // tag19
                _execRefIDStart = _idx;
                _execRefIDLen = getValLength();
                break;
            case FixDictionary50.LastMkt:         // tag30
                _lastMktStart = _idx;
                _lastMktLen = getValLength();
                break;
            case FixDictionary50.LastPx:         // tag31
                _lastPx = getDoubleVal();
                break;
            case FixDictionary50.LastQty:         // tag32
                _lastQty = getDoubleVal();
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                _msgSeqNum = getIntVal();
                break;
            case FixDictionary50.OrderId:         // tag37
                _orderIdStart = _idx;
                _orderIdLen = getValLength();
                break;
            case FixDictionary50.OrdStatus:         // tag39
                 _ordStatus = OrdStatus.getVal( _fixMsg[_idx++] );
                break;
            case FixDictionary50.OrigClOrdId:         // tag41
                _origClOrdIdStart = _idx;
                _origClOrdIdLen = getValLength();
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                _possDupFlag = (_fixMsg[_idx++] == 'Y');
                break;
            case FixDictionary50.SendingTime:         // tag52
                _eventTimestamp = getInternalTime();
                break;
            case FixDictionary50.Side:         // tag54
                 _side = Side.getVal( _fixMsg[_idx++] );
                break;
            case FixDictionary50.Text:         // tag58
                _textStart = _idx;
                _textLen = getValLength();
                break;
            case FixDictionary50.TransactTime:         // tag60
                _transactTime = getInternalTime();
                break;
            case FixDictionary50.SecurityDesc:         // tag107
                _securityDescStart = _idx;
                _securityDescLen = getValLength();
                break;
            case FixDictionary50.Account:         // tag1
            case 2:         // tag2
            case 3:         // tag3
            case 4:         // tag4
            case 5:         // tag5
            case FixDictionary50.BeginSeqNo:         // tag7
            case FixDictionary50.BeginString:         // tag8
            case FixDictionary50.BodyLength:         // tag9
            case FixDictionary50.CheckSum:         // tag10
            case 12:         // tag12
            case 13:         // tag13
            case FixDictionary50.Currency:         // tag15
            case FixDictionary50.EndSeqNo:         // tag16
            case FixDictionary50.ExecInst:         // tag18
            case 20:         // tag20
            case FixDictionary50.HandlInst:         // tag21
            case FixDictionary50.SecurityIDSource:         // tag22
            case 23:         // tag23
            case 24:         // tag24
            case 25:         // tag25
            case 26:         // tag26
            case 27:         // tag27
            case 28:         // tag28
            case 29:         // tag29
            case 33:         // tag33
            case FixDictionary50.MsgType:         // tag35
            case FixDictionary50.NewSeqNo:         // tag36
            case FixDictionary50.OrderQty:         // tag38
            case FixDictionary50.OrdType:         // tag40
            case 42:         // tag42
            case FixDictionary50.Price:         // tag44
            case FixDictionary50.RefSeqNum:         // tag45
            case 46:         // tag46
            case 47:         // tag47
            case FixDictionary50.SecurityID:         // tag48
            case FixDictionary50.SenderCompID:         // tag49
            case FixDictionary50.SenderSubID:         // tag50
            case 51:         // tag51
            case 53:         // tag53
            case FixDictionary50.Symbol:         // tag55
            case FixDictionary50.TargetCompID:         // tag56
            case FixDictionary50.TargetSubID:         // tag57
            case FixDictionary50.TimeInForce:         // tag59
            case 61:         // tag61
            case 62:         // tag62
            case 63:         // tag63
            case 64:         // tag64
            case 65:         // tag65
            case 66:         // tag66
            case 67:         // tag67
            case 68:         // tag68
            case 69:         // tag69
            case 70:         // tag70
            case 71:         // tag71
            case 72:         // tag72
            case 73:         // tag73
            case 74:         // tag74
            case 75:         // tag75
            case 76:         // tag76
            case FixDictionary50.PositionEffect:         // tag77
            case 78:         // tag78
            case 79:         // tag79
            case 80:         // tag80
            case 81:         // tag81
            case 82:         // tag82
            case 83:         // tag83
            case 84:         // tag84
            case 85:         // tag85
            case 86:         // tag86
            case 87:         // tag87
            case 88:         // tag88
            case FixDictionary50.Signature:         // tag89
            case 90:         // tag90
            case 91:         // tag91
            case 92:         // tag92
            case FixDictionary50.SignatureLength:         // tag93
            case 94:         // tag94
            case FixDictionary50.RawDataLen:         // tag95
            case FixDictionary50.RawData:         // tag96
            case FixDictionary50.PossResend:         // tag97
            case FixDictionary50.EncryptMethod:         // tag98
            case 99:         // tag99
            case FixDictionary50.ExDest:         // tag100
            case 101:         // tag101
            case FixDictionary50.CxlRejReason:         // tag102
            case FixDictionary50.OrdRejReason:         // tag103
            case 104:         // tag104
            case 105:         // tag105
            case 106:         // tag106
            case FixDictionary50.heartBtInt:         // tag108
            case FixDictionary50.clientID:         // tag109
            case 110:         // tag110
            case FixDictionary50.displayQty:         // tag111
            case FixDictionary50.testReqID:         // tag112
            case 113:         // tag113
            case 114:         // tag114
            case FixDictionary50.OnBehalfOfCompID:         // tag115
            case FixDictionary50.OnBehalfOfSubID:         // tag116
            case 117:         // tag117
            case 118:         // tag118
            case 119:         // tag119
            case FixDictionary50.settlCurrency:         // tag120
            case 121:         // tag121
            case FixDictionary50.OrigSendingTime:         // tag122
            case FixDictionary50.GapFillFlag:         // tag123
            case 124:         // tag124
            case 125:         // tag125
            case FixDictionary50.expireTime:         // tag126
            case 127:         // tag127
            case FixDictionary50.DeliverToCompID:         // tag128
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.ExecType:         // tag150
                     _execType = ExecType.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionary50.LeavesQty:         // tag151
                    _leavesQty = getDoubleVal();
                    break;
                case FixDictionary50.parentClOrdId:         // tag526
                    _parentClOrdIdStart = _idx;
                    _parentClOrdIdLen = getValLength();
                    break;
                case FixDictionary50.MultiLegReportingType:         // tag442
                     _multiLegReportingType = MultiLegReportingType.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionary50.LiquidityInd:         // tag851
                     _liquidityInd = LiquidityInd.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionary50.TradingStatus:         // tag1700
                    start = _idx;
                    valLen = getValLength();
                     _tradingStatus = TradingStatus.getVal( _fixMsg, start, valLen );
                    break;
                case FixDictionary50.ExecRestatementReason:         // tag378
                    start = _idx;
                    valLen = getValLength();
                     _execRestatementReason = ExecRestatementReason.getVal( _fixMsg, start, valLen );
                    break;
                default:
                    getValLength();
                    break;
                }
                break;
            }

            _idx++; // past delimiter
            _tag = getTag();
        }
        if ( _ordStatus == null || _execType == null ){
            throwDecodeException( "Execution report missing order or exec status " );
        }
        switch( _execType.getID() ){
        case ManualTypeIds.EXECTYPE_NEW:
            return populateExecRptNew();
        case ManualTypeIds.EXECTYPE_PARTIALFILL:
            return populateExecRptPartialFill();
        case ManualTypeIds.EXECTYPE_FILL:
            return populateExecRptFill();
        case ManualTypeIds.EXECTYPE_DONEFORDAY:
            return populateExecRptDoneForDay();
        case ManualTypeIds.EXECTYPE_CANCELED:
            return populateExecRptCanceled();
        case ManualTypeIds.EXECTYPE_REPLACED:
            return populateExecRptReplaced();
        case ManualTypeIds.EXECTYPE_PENDINGCANCEL:
            return populateExecRptPendingCancel();
        case ManualTypeIds.EXECTYPE_STOPPED:
            return populateExecRptStopped();
        case ManualTypeIds.EXECTYPE_REJECTED:
            return populateExecRptRejected();
        case ManualTypeIds.EXECTYPE_SUSPENDED:
            return populateExecRptSuspended();
        case ManualTypeIds.EXECTYPE_PENDINGNEW:
            return populateExecRptPendingNew();
        case ManualTypeIds.EXECTYPE_CALCULATED:
            return populateExecRptCalculated();
        case ManualTypeIds.EXECTYPE_EXPIRED:
            return populateExecRptExpired();
        case ManualTypeIds.EXECTYPE_RESTATED:
            return populateExecRptRestated();
        case ManualTypeIds.EXECTYPE_PENDINGREPLACE:
            return populateExecRptPendingReplace();
        case ManualTypeIds.EXECTYPE_TRADE:
            return populateExecRptTrade();
        case ManualTypeIds.EXECTYPE_TRADECORRECT:
            return populateExecRptTradeCorrect();
        case ManualTypeIds.EXECTYPE_TRADECANCEL:
            return populateExecRptTradeCancel();
        case ManualTypeIds.EXECTYPE_ORDERSTATUS:
            return populateExecRptOrderStatus();
        case ManualTypeIds.EXECTYPE_UNKNOWN:
            return populateExecRptUnknown();
        }
        throwDecodeException( "ExecRpt type " + _execType + " not supported" );
        return null;
    }

    public final Event populateExecRptNew() {
        MarketNewOrderAckImpl msg = _newOrderAckFactory.get();
        if ( _nanoStats ) msg.setAckReceived( _received );
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptTrade() {
        MarketTradeNewImpl msg = _tradeNewFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        msg.setLastQty( _lastQty );
        msg.setLastPx( _lastPx );
        if ( _lastMktLen > 0 ) msg.setLastMkt( _fixMsg, _lastMktStart, _lastMktLen );
        if ( _securityDescLen > 0 ) msg.setSecurityDesc( _fixMsg, _securityDescStart, _securityDescLen );
        msg.setMultiLegReportingType( _multiLegReportingType );
        msg.setLiquidityInd( _liquidityInd );
        return msg;
    }

    public final Event populateExecRptRejected() {
        MarketRejectedImpl msg = _rejectedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        msg.setTradingStatus( _tradingStatus );
        return msg;
    }

    public final Event populateExecRptReplaced() {
        MarketReplacedImpl msg = _replacedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptCanceled() {
        MarketCancelledImpl msg = _cancelledFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptDoneForDay() {
        MarketDoneForDayImpl msg = _doneForDayFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptPendingCancel() {
        MarketPendingCancelImpl msg = _pendingCancelFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptStopped() {
        MarketStoppedImpl msg = _stoppedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptSuspended() {
        MarketSuspendedImpl msg = _suspendedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptPendingNew() {
        MarketPendingNewImpl msg = _pendingNewFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptCalculated() {
        MarketCalculatedImpl msg = _calculatedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptExpired() {
        MarketExpiredImpl msg = _expiredFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptRestated() {
        MarketRestatedImpl msg = _restatedFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        msg.setExecRestatementReason( _execRestatementReason );
        return msg;
    }

    public final Event populateExecRptPendingReplace() {
        MarketPendingReplaceImpl msg = _pendingReplaceFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptTradeCorrect() {
        MarketTradeCorrectImpl msg = _tradeCorrectFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        msg.setLastQty( _lastQty );
        msg.setLastPx( _lastPx );
        if ( _lastMktLen > 0 ) msg.setLastMkt( _fixMsg, _lastMktStart, _lastMktLen );
        if ( _securityDescLen > 0 ) msg.setSecurityDesc( _fixMsg, _securityDescStart, _securityDescLen );
        msg.setMultiLegReportingType( _multiLegReportingType );
        msg.setLiquidityInd( _liquidityInd );
        if ( _execRefIDLen > 0 ) msg.setExecRefID( _fixMsg, _execRefIDStart, _execRefIDLen );
        return msg;
    }

    public final Event populateExecRptTradeCancel() {
        MarketTradeCancelImpl msg = _tradeCancelFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        msg.setLastQty( _lastQty );
        msg.setLastPx( _lastPx );
        if ( _lastMktLen > 0 ) msg.setLastMkt( _fixMsg, _lastMktStart, _lastMktLen );
        if ( _securityDescLen > 0 ) msg.setSecurityDesc( _fixMsg, _securityDescStart, _securityDescLen );
        msg.setMultiLegReportingType( _multiLegReportingType );
        msg.setLiquidityInd( _liquidityInd );
        if ( _execRefIDLen > 0 ) msg.setExecRefID( _fixMsg, _execRefIDStart, _execRefIDLen );
        return msg;
    }

    public final Event populateExecRptOrderStatus() {
        MarketOrderStatusImpl msg = _orderStatusFactory.get();
        int start;
        int valLen;
        execCheckSenderCompID();
        execCheckTargetCompID();
        msg.setMsgSeqNum( _msgSeqNum );
        execCheckSenderSubID();
        execCheckTargetSubID();
        msg.setPossDupFlag( _possDupFlag );
        msg.setEventTimestamp( _eventTimestamp );
        if ( _clOrdIdLen > 0 ) msg.setClOrdId( _fixMsg, _clOrdIdStart, _clOrdIdLen );
        if ( _execIdLen > 0 ) msg.setExecId( _fixMsg, _execIdStart, _execIdLen );
        if ( _orderIdLen > 0 ) msg.setOrderId( _fixMsg, _orderIdStart, _orderIdLen );
        msg.setOrdStatus( _ordStatus );
        msg.setExecType( _execType );
        msg.setSide( _side );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLeavesQty( _leavesQty );
        if ( _parentClOrdIdLen > 0 ) msg.setParentClOrdId( _fixMsg, _parentClOrdIdStart, _parentClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptPartialFill() {
        throwDecodeException( "ExecRpt type PartialFill not supported" );
        return null;
    }

    public final Event populateExecRptFill() {
        throwDecodeException( "ExecRpt type Fill not supported" );
        return null;
    }

    public final Event populateExecRptUnknown() {
        throwDecodeException( "ExecRpt type Unknown not supported" );
        return null;
    }

    public final Event decodeNewOrderSingle() {
        final ClientNewOrderSingleImpl msg = _newOrderSingleFactory.get();
        if ( _nanoStats ) msg.setOrderReceived( getReceived() );
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.Account:         // tag1
                start = _idx - _offset;
                valLen = getValLength();
                msg.setAccount( start, valLen );
                break;
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.ClOrdId:         // tag11
                start = _idx - _offset;
                valLen = getValLength();
                msg.setClOrdId( start, valLen );
                break;
            case FixDictionary50.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionary50.HandlInst:         // tag21
                msg.setHandlInst( HandlInst.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.OrderQty:         // tag38
                msg.setOrderQty( getDoubleVal() );
                break;
            case FixDictionary50.OrdType:         // tag40
                msg.setOrdType( OrdType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.Price:         // tag44
                msg.setPrice( getDoubleVal() );
                break;
            case FixDictionary50.SecurityID:         // tag48
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSecurityId( start, valLen );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.Side:         // tag54
                msg.setSide( Side.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.Symbol:         // tag55
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSymbol( start, valLen );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionary50.Text:         // tag58
                start = _idx - _offset;
                valLen = getValLength();
                msg.setText( start, valLen );
                break;
            case FixDictionary50.TimeInForce:         // tag59
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionary50.ExDest:         // tag100
                start = _idx - _offset;
                valLen = getValLength();
                msg.setExDest( start, valLen );
                break;
            case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:  /* SKIP */
            case 12: case 13: case 14: case 16: case 17: case 18: case 19: case 20:  /* SKIP */
            case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30:  /* SKIP */
            case 31: case 32: case 33: case 35: case 36: case 37: case 39: case 41:  /* SKIP */
            case 42: case 45: case 46: case 47: case 51: case 53: case 61: case 62:  /* SKIP */
            case 63: case 64: case 65: case 66: case 67: case 68: case 69: case 70:  /* SKIP */
            case 71: case 72: case 73: case 74: case 75: case 76: case 77: case 78:  /* SKIP */
            case 79: case 80: case 81: case 82: case 83: case 84: case 85: case 86:  /* SKIP */
            case 87: case 88: case 89: case 90: case 91: case 92: case 93: case 94:  /* SKIP */
            case 95: case 96: case 97: case 98: case 99: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.expireTime:         // tag126
                    msg.setExpireTime( getInternalTime() );
                    break;
                case FixDictionary50.effectiveTime:         // tag168
                    msg.setEffectiveTime( getInternalTime() );
                    break;
                case FixDictionary50.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionary50.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionary50.parentClOrdId:         // tag526
                    start = _idx - _offset;
                    valLen = getValLength();
                    msg.setParentClOrdId( start, valLen );
                    break;
                case FixDictionary50.OrderCapacity:         // tag528
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionary50.BookingType:         // tag775
                    msg.setBookingType( BookingType.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionary50.TargetStrategy:         // tag847
                    msg.setTargetStrategy( TargetStrategy.getVal( _fixMsg[_idx++] ) );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {
            throw new RuntimeDecodingException( "NewOrderSingleMessage too big " + _idx + ", max=" + SizeType.VIEW_NOS_BUFFER.getSize() );
        }
        msg.setViewBuf( _fixMsg, _offset, _idx );

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelReplaceRequest() {
        final ClientCancelReplaceRequestImpl msg = _cancelReplaceRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.Account:         // tag1
                start = _idx - _offset;
                valLen = getValLength();
                msg.setAccount( start, valLen );
                break;
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionary50.HandlInst:         // tag21
                msg.setHandlInst( HandlInst.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.OrderQty:         // tag38
                msg.setOrderQty( getDoubleVal() );
                break;
            case FixDictionary50.OrdType:         // tag40
                msg.setOrdType( OrdType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.Price:         // tag44
                msg.setPrice( getDoubleVal() );
                break;
            case FixDictionary50.SecurityID:         // tag48
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSecurityId( start, valLen );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.Side:         // tag54
                msg.setSide( Side.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.Symbol:         // tag55
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSymbol( start, valLen );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionary50.Text:         // tag58
                start = _idx - _offset;
                valLen = getValLength();
                msg.setText( start, valLen );
                break;
            case FixDictionary50.TimeInForce:         // tag59
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionary50.ExDest:         // tag100
                start = _idx - _offset;
                valLen = getValLength();
                msg.setExDest( start, valLen );
                break;
            case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:  /* SKIP */
            case 12: case 13: case 14: case 16: case 17: case 18: case 19: case 20:  /* SKIP */
            case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30:  /* SKIP */
            case 31: case 32: case 33: case 35: case 36: case 39: case 42: case 45:  /* SKIP */
            case 46: case 47: case 51: case 53: case 61: case 62: case 63: case 64:  /* SKIP */
            case 65: case 66: case 67: case 68: case 69: case 70: case 71: case 72:  /* SKIP */
            case 73: case 74: case 75: case 76: case 77: case 78: case 79: case 80:  /* SKIP */
            case 81: case 82: case 83: case 84: case 85: case 86: case 87: case 88:  /* SKIP */
            case 89: case 90: case 91: case 92: case 93: case 94: case 95: case 96:  /* SKIP */
            case 97: case 98: case 99: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.expireTime:         // tag126
                    msg.setExpireTime( getInternalTime() );
                    break;
                case FixDictionary50.effectiveTime:         // tag168
                    msg.setEffectiveTime( getInternalTime() );
                    break;
                case FixDictionary50.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionary50.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionary50.parentClOrdId:         // tag526
                    start = _idx - _offset;
                    valLen = getValLength();
                    msg.setParentClOrdId( start, valLen );
                    break;
                case FixDictionary50.OrderCapacity:         // tag528
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionary50.BookingType:         // tag775
                    msg.setBookingType( BookingType.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionary50.TargetStrategy:         // tag847
                    msg.setTargetStrategy( TargetStrategy.getVal( _fixMsg[_idx++] ) );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {
            throw new RuntimeDecodingException( "CancelReplaceRequestMessage too big " + _idx + ", max=" + SizeType.VIEW_NOS_BUFFER.getSize() );
        }
        msg.setViewBuf( _fixMsg, _offset, _idx );

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelRequest() {
        final ClientCancelRequestImpl msg = _cancelRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.Account:         // tag1
                start = _idx - _offset;
                valLen = getValLength();
                msg.setAccount( start, valLen );
                break;
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionary50.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SecurityID:         // tag48
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSecurityId( start, valLen );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.Symbol:         // tag55
                start = _idx - _offset;
                valLen = getValLength();
                msg.setSymbol( start, valLen );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionary50.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionary50.ExDest:         // tag100
                start = _idx - _offset;
                valLen = getValLength();
                msg.setExDest( start, valLen );
                break;
            case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:  /* SKIP */
            case 12: case 13: case 14: case 16: case 17: case 18: case 19: case 20:  /* SKIP */
            case 21: case 23: case 24: case 25: case 26: case 27: case 28: case 29:  /* SKIP */
            case 30: case 31: case 32: case 33: case 35: case 36: case 38: case 39:  /* SKIP */
            case 40: case 42: case 44: case 45: case 46: case 47: case 51: case 53:  /* SKIP */
            case 54: case 58: case 59: case 61: case 62: case 63: case 64: case 65:  /* SKIP */
            case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73:  /* SKIP */
            case 74: case 75: case 76: case 77: case 78: case 79: case 80: case 81:  /* SKIP */
            case 82: case 83: case 84: case 85: case 86: case 87: case 88: case 89:  /* SKIP */
            case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:  /* SKIP */
            case 98: case 99: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionary50.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionary50.parentClOrdId:         // tag526
                    start = _idx - _offset;
                    valLen = getValLength();
                    msg.setParentClOrdId( start, valLen );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {
            throw new RuntimeDecodingException( "CancelRequestMessage too big " + _idx + ", max=" + SizeType.VIEW_NOS_BUFFER.getSize() );
        }
        msg.setViewBuf( _fixMsg, _offset, _idx );

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelReject() {
        final MarketCancelRejectImpl msg = _cancelRejectFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.OrdStatus:         // tag39
                msg.setOrdStatus( OrdStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionary50.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case FixDictionary50.CxlRejReason:         // tag102
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCxlRejReason( CxlRejReason.getVal( _tmpLookupKey ) );
                break;
            case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:  /* SKIP */
            case 20: case 21: case 22: case 23: case 24: case 25: case 26: case 27:  /* SKIP */
            case 28: case 29: case 30: case 31: case 32: case 33: case 35: case 36:  /* SKIP */
            case 38: case 40: case 42: case 44: case 45: case 46: case 47: case 48:  /* SKIP */
            case 51: case 53: case 54: case 55: case 59: case 60: case 61: case 62:  /* SKIP */
            case 63: case 64: case 65: case 66: case 67: case 68: case 69: case 70:  /* SKIP */
            case 71: case 72: case 73: case 74: case 75: case 76: case 77: case 78:  /* SKIP */
            case 79: case 80: case 81: case 82: case 83: case 84: case 85: case 86:  /* SKIP */
            case 87: case 88: case 89: case 90: case 91: case 92: case 93: case 94:  /* SKIP */
            case 95: case 96: case 97: case 98: case 99: case 100: case 101: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.CxlRejResponseTo:         // tag434
                    msg.setCxlRejResponseTo( CxlRejResponseTo.getVal( _fixMsg[_idx++] ) );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeHeartbeat() {
        final HeartbeatImpl msg = _heartbeatFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionary50.testReqID:         // tag112
                start = _idx;
                valLen = getValLength();
                msg.setTestReqID( _fixMsg, start, valLen );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:  /* SKIP */
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 35:  /* SKIP */
            case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 44:  /* SKIP */
            case 45: case 46: case 47: case 48: case 51: case 53: case 54: case 55:  /* SKIP */
            case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 65:  /* SKIP */
            case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73:  /* SKIP */
            case 74: case 75: case 76: case 77: case 78: case 79: case 80: case 81:  /* SKIP */
            case 82: case 83: case 84: case 85: case 86: case 87: case 88: case 89:  /* SKIP */
            case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:  /* SKIP */
            case 98: case 99: case 100: case 101: case 102: case 103: case 104: case 105:  /* SKIP */
            case 106: case 107: case 108: case 109: case 110: case 111: 
                getValLength();
                break;
            default:
                getValLength();
                break;
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeLogon() {
        final LogonImpl msg = _logonFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.SenderSubID:         // tag50
                start = _idx;
                valLen = getValLength();
                msg.setSenderSubId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.TargetSubID:         // tag57
                start = _idx;
                valLen = getValLength();
                msg.setTargetSubId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.RawDataLen:         // tag95
                msg.setRawDataLen( getIntVal() );
                break;
            case FixDictionary50.RawData:         // tag96
                start = _idx;
                valLen = getValLength();
                msg.setRawData( _fixMsg, start, valLen );
                break;
            case FixDictionary50.EncryptMethod:         // tag98
                msg.setEncryptMethod( EncryptMethod.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionary50.heartBtInt:         // tag108
                msg.setHeartBtInt( getIntVal() );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:  /* SKIP */
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 35:  /* SKIP */
            case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 44:  /* SKIP */
            case 45: case 46: case 47: case 48: case 51: case 52: case 53: case 54:  /* SKIP */
            case 55: case 58: case 59: case 60: case 61: case 62: case 63: case 64:  /* SKIP */
            case 65: case 66: case 67: case 68: case 69: case 70: case 71: case 72:  /* SKIP */
            case 73: case 74: case 75: case 76: case 77: case 78: case 79: case 80:  /* SKIP */
            case 81: case 82: case 83: case 84: case 85: case 86: case 87: case 88:  /* SKIP */
            case 89: case 90: case 91: case 92: case 93: case 94: case 97: case 99:  /* SKIP */
            case 100: case 101: case 102: case 103: case 104: case 105: case 106: case 107:  /* SKIP */
            
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.ResetSeqNumFlag:         // tag141
                    msg.setResetSeqNumFlag( _fixMsg[_idx++]=='Y' );
                    break;
                case FixDictionary50.NextExpectedMsgSeqNum:         // tag789
                    msg.setNextExpectedMsgSeqNum( getIntVal() );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeLogout() {
        final LogoutImpl msg = _logoutFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.SenderSubID:         // tag50
                start = _idx;
                valLen = getValLength();
                msg.setSenderSubId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.TargetSubID:         // tag57
                start = _idx;
                valLen = getValLength();
                msg.setTargetSubId( _fixMsg, start, valLen );
                break;
            case FixDictionary50.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.lastMsgSeqNumProcessed:         // tag369
                    msg.setLastMsgSeqNumProcessed( getIntVal() );
                    break;
                case FixDictionary50.NextExpectedMsgSeqNum:         // tag789
                    msg.setNextExpectedMsgSeqNum( getIntVal() );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeSessionReject() {
        final SessionRejectImpl msg = _sessionRejectFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.RefSeqNum:         // tag45
                msg.setRefSeqNum( getIntVal() );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionary50.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:  /* SKIP */
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 35:  /* SKIP */
            case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 44:  /* SKIP */
            case 46: case 47: case 48: case 51: case 53: case 54: case 55: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionary50.RefTagID:         // tag371
                    msg.setRefTagID( getIntVal() );
                    break;
                case FixDictionary50.RefMsgType:         // tag372
                    start = _idx;
                    valLen = getValLength();
                    msg.setRefMsgType( _fixMsg, start, valLen );
                    break;
                case FixDictionary50.SessionRejectReason:         // tag373
                    start = _idx;
                    valLen = getValLength();
                    msg.setSessionRejectReason( SessionRejectReason.getVal( _fixMsg, start, valLen ) );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeResendRequest() {
        final ResendRequestImpl msg = _resendRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.BeginSeqNo:         // tag7
                msg.setBeginSeqNo( getIntVal() );
                break;
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.EndSeqNo:         // tag16
                msg.setEndSeqNo( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                getValLength();
                break;
            case 8: case 9: case 11: case 12: case 13: case 14: case 15: case 17:  /* SKIP */
            case 18: case 19: case 20: case 21: case 22: case 23: case 24: case 25:  /* SKIP */
            case 26: case 27: case 28: case 29: case 30: case 31: case 32: case 33:  /* SKIP */
            case 35: case 36: case 37: case 38: case 39: case 40: case 41: case 42:  /* SKIP */
            case 44: case 45: case 46: case 47: case 48: case 51: case 53: case 54:  /* SKIP */
            case 55: 
                getValLength();
                break;
            default:
                getValLength();
                break;
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeSequenceReset() {
        final SequenceResetImpl msg = _sequenceResetFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.NewSeqNo:         // tag36
                msg.setNewSeqNo( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionary50.GapFillFlag:         // tag123
                msg.setGapFillFlag( _fixMsg[_idx++]=='Y' );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:  /* SKIP */
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 35:  /* SKIP */
            case 37: case 38: case 39: case 40: case 41: case 42: case 44: case 45:  /* SKIP */
            case 46: case 47: case 48: case 51: case 53: case 54: case 55: case 58:  /* SKIP */
            case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66:  /* SKIP */
            case 67: case 68: case 69: case 70: case 71: case 72: case 73: case 74:  /* SKIP */
            case 75: case 76: case 77: case 78: case 79: case 80: case 81: case 82:  /* SKIP */
            case 83: case 84: case 85: case 86: case 87: case 88: case 89: case 90:  /* SKIP */
            case 91: case 92: case 93: case 94: case 95: case 96: case 97: case 98:  /* SKIP */
            case 99: case 100: case 101: case 102: case 103: case 104: case 105: case 106:  /* SKIP */
            case 107: case 108: case 109: case 110: case 111: case 112: case 113: case 114:  /* SKIP */
            case 115: case 116: case 117: case 118: case 119: case 120: case 121: case 122:  /* SKIP */
            
                getValLength();
                break;
            default:
                getValLength();
                break;
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeTestRequest() {
        final TestRequestImpl msg = _testRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionary50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionary50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionary50.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionary50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionary50.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionary50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionary50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionary50.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionary50.testReqID:         // tag112
                start = _idx;
                valLen = getValLength();
                msg.setTestReqID( _fixMsg, start, valLen );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:  /* SKIP */
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 35:  /* SKIP */
            case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 44:  /* SKIP */
            case 45: case 46: case 47: case 48: case 51: case 53: case 54: case 55:  /* SKIP */
            case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 65:  /* SKIP */
            case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73:  /* SKIP */
            case 74: case 75: case 76: case 77: case 78: case 79: case 80: case 81:  /* SKIP */
            case 82: case 83: case 84: case 85: case 86: case 87: case 88: case 89:  /* SKIP */
            case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:  /* SKIP */
            case 98: case 99: case 100: case 101: case 102: case 103: case 104: case 105:  /* SKIP */
            case 106: case 107: case 108: case 109: case 110: case 111: 
                getValLength();
                break;
            default:
                getValLength();
                break;
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }


   // SubGrps

   // transform methods
     
    /**
     * PostPend  Common Decoder File
     */
     
    private void enrich( ClientNewOrderSingleImpl nos ) {
        
        ExchangeInstrument instr = lookupInst( nos, true );

        final Currency         clientCcy = nos.getCurrency();
        
        if ( clientCcy == null ) {
            nos.setCurrency( instr.getCurrency() );
        }
        
        nos.setInstrument( instr );
        nos.setClient( _clientProfile );
    }

    private void enrich( ClientCancelReplaceRequestImpl rep ) {
        
        final ExchangeInstrument instr = lookupInst( rep, false );

        rep.setInstrument( instr );
        rep.setClient( _clientProfile );
    }

    private void enrich( ClientCancelRequestImpl can ) {
        
        final ExchangeInstrument instr = lookupInst( can, false );

        can.setInstrument( instr );
        can.setClient( _clientProfile );
    }

    private void enrich( NewOrderSingleImpl nos ) {

        final ExchangeInstrument instr = lookupInst( nos, true );

        final Currency         clientCcy = nos.getCurrency();

        if ( clientCcy == null ) {
            nos.setCurrency( instr.getCurrency() );
        }

        nos.setInstrument( instr );
        nos.setClient( _clientProfile );
    }

    private void enrich( CancelReplaceRequestImpl rep ) {

        final ExchangeInstrument instr = lookupInst( rep, false );

        rep.setInstrument( instr );
        rep.setClient( _clientProfile );
    }

    private void enrich( CancelRequestImpl can ) {

        final ExchangeInstrument instr = lookupInst( can, false );

        can.setInstrument( instr );
        can.setClient( _clientProfile );
    }

    private ExchangeInstrument lookupInst() {

        ExchangeInstrument instr = null;

        if ( _securityIDSourceLen > 0 && _securityExchangeLen > 0 && _securityIDStart > 0 ) {
            final SecurityIDSource idSrc = SecurityIDSource.getVal( _fixMsg[_securityIDSourceStart] );

            _tmpLookupKey.setValue( _fixMsg, _securityExchangeStart, _securityExchangeLen );

            final ExchangeCode exCode = ExchangeCode.getVal( _tmpLookupKey );

            _tmpLookupKey.setValue( _fixMsg, _securityIDStart, _securityIDLen );

            instr = _instrumentLocator.getExchInst( _tmpLookupKey, idSrc, exCode );
        }

        if ( instr == null ) {
            throwDecodeException( "Instrument not found" );
        }

        return instr;
    }

    private ExchangeInstrument lookupInst( final BaseOrderRequest nos, boolean throwOnMissing ) {
        final Currency         clientCcy = nos.getCurrency();
        final SecurityIDSource src       = nos.getSecurityIDSource();

        ExchangeInstrument instr = null;
        if ( src == null ) {
            ExchangeCode code = nos.getSecurityExchange();
            if ( code == null ) code = ExchangeCode.getVal( nos.getExDest() );
            instr = _instrumentLocator.getExchInst( nos.getSymbol(), null, nos.getSecurityExchange() );

        } else if ( src == SecurityIDSource.ISIN ) {
            instr = _instrumentLocator.getExchInstByIsin( nos.getSecurityId(), nos.getSecurityExchange(), nos.getCurrency() );
        } else {
            int maturity = nos.getMaturityMonthYear();

            if ( Utils.hasNonZeroVal( maturity ) && nos.getSecurityIDSource() == SecurityIDSource.BloombergTicker ) {
                ExchangeCode code = nos.getSecurityExchange();
                if ( code == null ) code = ExchangeCode.getVal( nos.getExDest() );

                FutureExchangeSymbol s = FutureExchangeSymbol.getFromBloombergTicker( nos.getSecurityId(), code );

                if ( s != FutureExchangeSymbol.UNKNOWN ) {
                    instr = _instrumentLocator.getFutureInstrumentBySym( s, maturity, nos.getSecurityExchange() );
                }
            }

            if ( instr == null ) {
                instr = _instrumentLocator.getExchInst( nos.getSecurityId(), src, nos.getSecurityExchange() );
            }
        }

        if ( instr == null && throwOnMissing ) {
            throwDecodeException( "Instrument not found" );
        }

        return instr;
    }

/*
 * HANDCODED DECODER METHDOS
 */

}
