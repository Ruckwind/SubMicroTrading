

// General Decoder uses the full standard events eg NewOrderSingleImpl

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
import com.rr.model.generated.fix.model.defn.FixDictionaryDC44;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;

@SuppressWarnings( "unused" )

public final class DropCopy44DecoderFull extends AbstractFixDecoderDC44 {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs

    final String _id;
    OrdStatus _ordStatus = null;    // Tag 39
    ExecType _execType = null;    // Tag 150
    Side _side = null;    // Tag 54
    Currency _currency = null;    // Tag 15
    SecurityIDSource _securityIDSource = null;    // Tag 22
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
    int _symbolStart = 0;    // tag 55
    int _symbolLen = 0;    // tag 55
    int _securityIdStart = 0;    // tag 48
    int _securityIdLen = 0;    // tag 48
    int _textStart = 0;    // tag 58
    int _textLen = 0;    // tag 58
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
    double _orderQty = Constants.UNSET_DOUBLE;    // tag 38
    double _price = Constants.UNSET_DOUBLE;    // tag 44
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

    private final SuperPool<NewOrderSingleImpl> _newOrderSinglePool = SuperpoolManager.instance().getSuperPool( NewOrderSingleImpl.class );
    private final NewOrderSingleFactory _newOrderSingleFactory = new NewOrderSingleFactory( _newOrderSinglePool );

    private final SuperPool<CancelReplaceRequestImpl> _cancelReplaceRequestPool = SuperpoolManager.instance().getSuperPool( CancelReplaceRequestImpl.class );
    private final CancelReplaceRequestFactory _cancelReplaceRequestFactory = new CancelReplaceRequestFactory( _cancelReplaceRequestPool );

    private final SuperPool<CancelRequestImpl> _cancelRequestPool = SuperpoolManager.instance().getSuperPool( CancelRequestImpl.class );
    private final CancelRequestFactory _cancelRequestFactory = new CancelRequestFactory( _cancelRequestPool );

    private final SuperPool<CancelRejectImpl> _cancelRejectPool = SuperpoolManager.instance().getSuperPool( CancelRejectImpl.class );
    private final CancelRejectFactory _cancelRejectFactory = new CancelRejectFactory( _cancelRejectPool );

    private final SuperPool<NewOrderAckImpl> _newOrderAckPool = SuperpoolManager.instance().getSuperPool( NewOrderAckImpl.class );
    private final NewOrderAckFactory _newOrderAckFactory = new NewOrderAckFactory( _newOrderAckPool );

    private final SuperPool<TradeNewImpl> _tradeNewPool = SuperpoolManager.instance().getSuperPool( TradeNewImpl.class );
    private final TradeNewFactory _tradeNewFactory = new TradeNewFactory( _tradeNewPool );

    private final SuperPool<RejectedImpl> _rejectedPool = SuperpoolManager.instance().getSuperPool( RejectedImpl.class );
    private final RejectedFactory _rejectedFactory = new RejectedFactory( _rejectedPool );

    private final SuperPool<CancelledImpl> _cancelledPool = SuperpoolManager.instance().getSuperPool( CancelledImpl.class );
    private final CancelledFactory _cancelledFactory = new CancelledFactory( _cancelledPool );

    private final SuperPool<ReplacedImpl> _replacedPool = SuperpoolManager.instance().getSuperPool( ReplacedImpl.class );
    private final ReplacedFactory _replacedFactory = new ReplacedFactory( _replacedPool );

    private final SuperPool<DoneForDayImpl> _doneForDayPool = SuperpoolManager.instance().getSuperPool( DoneForDayImpl.class );
    private final DoneForDayFactory _doneForDayFactory = new DoneForDayFactory( _doneForDayPool );

    private final SuperPool<StoppedImpl> _stoppedPool = SuperpoolManager.instance().getSuperPool( StoppedImpl.class );
    private final StoppedFactory _stoppedFactory = new StoppedFactory( _stoppedPool );

    private final SuperPool<ExpiredImpl> _expiredPool = SuperpoolManager.instance().getSuperPool( ExpiredImpl.class );
    private final ExpiredFactory _expiredFactory = new ExpiredFactory( _expiredPool );

    private final SuperPool<SuspendedImpl> _suspendedPool = SuperpoolManager.instance().getSuperPool( SuspendedImpl.class );
    private final SuspendedFactory _suspendedFactory = new SuspendedFactory( _suspendedPool );

    private final SuperPool<RestatedImpl> _restatedPool = SuperpoolManager.instance().getSuperPool( RestatedImpl.class );
    private final RestatedFactory _restatedFactory = new RestatedFactory( _restatedPool );

    private final SuperPool<TradeCorrectImpl> _tradeCorrectPool = SuperpoolManager.instance().getSuperPool( TradeCorrectImpl.class );
    private final TradeCorrectFactory _tradeCorrectFactory = new TradeCorrectFactory( _tradeCorrectPool );

    private final SuperPool<TradeCancelImpl> _tradeCancelPool = SuperpoolManager.instance().getSuperPool( TradeCancelImpl.class );
    private final TradeCancelFactory _tradeCancelFactory = new TradeCancelFactory( _tradeCancelPool );

    private final SuperPool<OrderStatusImpl> _orderStatusPool = SuperpoolManager.instance().getSuperPool( OrderStatusImpl.class );
    private final OrderStatusFactory _orderStatusFactory = new OrderStatusFactory( _orderStatusPool );

    private final SuperPool<PendingNewImpl> _pendingNewPool = SuperpoolManager.instance().getSuperPool( PendingNewImpl.class );
    private final PendingNewFactory _pendingNewFactory = new PendingNewFactory( _pendingNewPool );

    private final SuperPool<PendingCancelImpl> _pendingCancelPool = SuperpoolManager.instance().getSuperPool( PendingCancelImpl.class );
    private final PendingCancelFactory _pendingCancelFactory = new PendingCancelFactory( _pendingCancelPool );

    private final SuperPool<PendingReplaceImpl> _pendingReplacePool = SuperpoolManager.instance().getSuperPool( PendingReplaceImpl.class );
    private final PendingReplaceFactory _pendingReplaceFactory = new PendingReplaceFactory( _pendingReplacePool );

    private final SuperPool<CalculatedImpl> _calculatedPool = SuperpoolManager.instance().getSuperPool( CalculatedImpl.class );
    private final CalculatedFactory _calculatedFactory = new CalculatedFactory( _calculatedPool );

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

    private final SuperPool<StratInstrumentImpl> _stratInstrumentPool = SuperpoolManager.instance().getSuperPool( StratInstrumentImpl.class );
    private final StratInstrumentFactory _stratInstrumentFactory = new StratInstrumentFactory( _stratInstrumentPool );

    private final SuperPool<StratInstrumentStateImpl> _stratInstrumentStatePool = SuperpoolManager.instance().getSuperPool( StratInstrumentStateImpl.class );
    private final StratInstrumentStateFactory _stratInstrumentStateFactory = new StratInstrumentStateFactory( _stratInstrumentStatePool );

    private final SuperPool<AppRunImpl> _appRunPool = SuperpoolManager.instance().getSuperPool( AppRunImpl.class );
    private final AppRunFactory _appRunFactory = new AppRunFactory( _appRunPool );

    private final SuperPool<StrategyRunImpl> _strategyRunPool = SuperpoolManager.instance().getSuperPool( StrategyRunImpl.class );
    private final StrategyRunFactory _strategyRunFactory = new StrategyRunFactory( _strategyRunPool );

    private final SuperPool<StrategyStateImpl> _strategyStatePool = SuperpoolManager.instance().getSuperPool( StrategyStateImpl.class );
    private final StrategyStateFactory _strategyStateFactory = new StrategyStateFactory( _strategyStatePool );


   // Constructors
    public DropCopy44DecoderFull( String id ) {
        this( id, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );
    }

    public DropCopy44DecoderFull() {
        this( null, FixVersion.Fix4_4._major, FixVersion.Fix4_4._minor );
    }

    public DropCopy44DecoderFull( byte major, byte minor ) {
        this( null, major, minor );
    }
    public DropCopy44DecoderFull( String id, byte major, byte minor ) {
        super( major, minor );
        _id = id;
    }

    @Override public FixDecoder newInstance() {
        DropCopy44DecoderFull dec = new DropCopy44DecoderFull( getComponentId(), _majorVersion, _minorVersion );
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
        case 'U':
          {
            byte msgType2 = _fixMsg[ _idx+1 ];
            switch( msgType2 ) {
            case '1':
                if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );
                }
                _idx += 3;
                return decodeAppRun();
            case '2':
                if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );
                }
                _idx += 3;
                return decodeStrategyRun();
            case '3':
                if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );
                }
                _idx += 3;
                return decodeStrategyState();
            }
            _idx += 3;
            throwDecodeException( "Unsupported fix message type " + msgType );
            return null;
          }
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
        _currency = null;    // Tag 15
        _securityIDSource = null;    // Tag 22
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
        _symbolStart = 0;    // tag 55
        _symbolLen = 0;    // tag 55
        _securityIdStart = 0;    // tag 48
        _securityIdLen = 0;    // tag 48
        _textStart = 0;    // tag 58
        _textLen = 0;    // tag 58
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
        _orderQty = Constants.UNSET_DOUBLE;    // tag 38
        _price = Constants.UNSET_DOUBLE;    // tag 44
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
            case FixDictionaryDC44.AvgPx:         // tag6
                _avgPx = getDoubleVal();
                break;
            case FixDictionaryDC44.ClOrdId:         // tag11
                _clOrdIdStart = _idx;
                _clOrdIdLen = getValLength();
                break;
            case FixDictionaryDC44.CumQty:         // tag14
                _cumQty = getDoubleVal();
                break;
            case FixDictionaryDC44.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                 _currency = Currency.getVal( _tmpLookupKey.copy(_fixMsg, start, valLen) );
                break;
            case FixDictionaryDC44.ExecID:         // tag17
                _execIdStart = _idx;
                _execIdLen = getValLength();
                break;
            case FixDictionaryDC44.ExecRefID:         // tag19
                _execRefIDStart = _idx;
                _execRefIDLen = getValLength();
                break;
            case FixDictionaryDC44.SecurityIDSource:         // tag22
                 _securityIDSource = SecurityIDSource.getVal( _fixMsg[_idx++] );
                break;
            case FixDictionaryDC44.LastMkt:         // tag30
                _lastMktStart = _idx;
                _lastMktLen = getValLength();
                break;
            case FixDictionaryDC44.LastPx:         // tag31
                _lastPx = getDoubleVal();
                break;
            case FixDictionaryDC44.LastQty:         // tag32
                _lastQty = getDoubleVal();
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                _msgSeqNum = getIntVal();
                break;
            case FixDictionaryDC44.OrderId:         // tag37
                _orderIdStart = _idx;
                _orderIdLen = getValLength();
                break;
            case FixDictionaryDC44.OrderQty:         // tag38
                _orderQty = getDoubleVal();
                break;
            case FixDictionaryDC44.OrdStatus:         // tag39
                 _ordStatus = OrdStatus.getVal( _fixMsg[_idx++] );
                break;
            case FixDictionaryDC44.OrigClOrdId:         // tag41
                _origClOrdIdStart = _idx;
                _origClOrdIdLen = getValLength();
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                _possDupFlag = (_fixMsg[_idx++] == 'Y');
                break;
            case FixDictionaryDC44.Price:         // tag44
                _price = getDoubleVal();
                break;
            case FixDictionaryDC44.SecurityID:         // tag48
                _securityIdStart = _idx;
                _securityIdLen = getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                _eventTimestamp = getInternalTime();
                break;
            case FixDictionaryDC44.Side:         // tag54
                 _side = Side.getVal( _fixMsg[_idx++] );
                break;
            case FixDictionaryDC44.Symbol:         // tag55
                _symbolStart = _idx;
                _symbolLen = getValLength();
                break;
            case FixDictionaryDC44.Text:         // tag58
                _textStart = _idx;
                _textLen = getValLength();
                break;
            case FixDictionaryDC44.TransactTime:         // tag60
                _transactTime = getInternalTime();
                break;
            case FixDictionaryDC44.SecurityDesc:         // tag107
                _securityDescStart = _idx;
                _securityDescLen = getValLength();
                break;
            case FixDictionaryDC44.Account:         // tag1
            case 2:         // tag2
            case 3:         // tag3
            case 4:         // tag4
            case 5:         // tag5
            case FixDictionaryDC44.BeginSeqNo:         // tag7
            case FixDictionaryDC44.BeginString:         // tag8
            case FixDictionaryDC44.BodyLength:         // tag9
            case FixDictionaryDC44.CheckSum:         // tag10
            case 12:         // tag12
            case 13:         // tag13
            case FixDictionaryDC44.EndSeqNo:         // tag16
            case FixDictionaryDC44.ExecInst:         // tag18
            case 20:         // tag20
            case FixDictionaryDC44.HandlInst:         // tag21
            case 23:         // tag23
            case 24:         // tag24
            case 25:         // tag25
            case 26:         // tag26
            case 27:         // tag27
            case 28:         // tag28
            case 29:         // tag29
            case 33:         // tag33
            case FixDictionaryDC44.MsgType:         // tag35
            case FixDictionaryDC44.NewSeqNo:         // tag36
            case FixDictionaryDC44.OrdType:         // tag40
            case 42:         // tag42
            case FixDictionaryDC44.RefSeqNum:         // tag45
            case 46:         // tag46
            case 47:         // tag47
            case FixDictionaryDC44.SenderCompID:         // tag49
            case FixDictionaryDC44.SenderSubID:         // tag50
            case 51:         // tag51
            case 53:         // tag53
            case FixDictionaryDC44.TargetCompID:         // tag56
            case FixDictionaryDC44.TargetSubID:         // tag57
            case FixDictionaryDC44.TimeInForce:         // tag59
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
            case FixDictionaryDC44.PositionEffect:         // tag77
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
            case FixDictionaryDC44.Signature:         // tag89
            case 90:         // tag90
            case 91:         // tag91
            case 92:         // tag92
            case FixDictionaryDC44.SignatureLength:         // tag93
            case 94:         // tag94
            case FixDictionaryDC44.RawDataLen:         // tag95
            case FixDictionaryDC44.RawData:         // tag96
            case FixDictionaryDC44.PossResend:         // tag97
            case FixDictionaryDC44.EncryptMethod:         // tag98
            case 99:         // tag99
            case FixDictionaryDC44.ExDest:         // tag100
            case 101:         // tag101
            case FixDictionaryDC44.CxlRejReason:         // tag102
            case FixDictionaryDC44.OrdRejReason:         // tag103
            case 104:         // tag104
            case 105:         // tag105
            case 106:         // tag106
            case FixDictionaryDC44.heartBtInt:         // tag108
            case FixDictionaryDC44.clientID:         // tag109
            case 110:         // tag110
            case FixDictionaryDC44.displayQty:         // tag111
            case FixDictionaryDC44.testReqID:         // tag112
            case 113:         // tag113
            case 114:         // tag114
            case FixDictionaryDC44.OnBehalfOfCompID:         // tag115
            case FixDictionaryDC44.OnBehalfOfSubID:         // tag116
            case 117:         // tag117
            case 118:         // tag118
            case 119:         // tag119
            case FixDictionaryDC44.settlCurrency:         // tag120
            case 121:         // tag121
            case FixDictionaryDC44.OrigSendingTime:         // tag122
            case FixDictionaryDC44.GapFillFlag:         // tag123
            case 124:         // tag124
            case 125:         // tag125
            case FixDictionaryDC44.expireTime:         // tag126
            case 127:         // tag127
            case FixDictionaryDC44.DeliverToCompID:         // tag128
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryDC44.ExecType:         // tag150
                     _execType = ExecType.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionaryDC44.LeavesQty:         // tag151
                    _leavesQty = getDoubleVal();
                    break;
                case FixDictionaryDC44.MultiLegReportingType:         // tag442
                     _multiLegReportingType = MultiLegReportingType.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionaryDC44.LiquidityInd:         // tag851
                     _liquidityInd = LiquidityInd.getVal( _fixMsg[_idx++] );
                    break;
                case FixDictionaryDC44.TradingStatus:         // tag1700
                    start = _idx;
                    valLen = getValLength();
                     _tradingStatus = TradingStatus.getVal( _fixMsg, start, valLen );
                    break;
                case FixDictionaryDC44.ExecRestatementReason:         // tag378
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
        NewOrderAckImpl msg = _newOrderAckFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptTrade() {
        TradeNewImpl msg = _tradeNewFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setLastQty( _lastQty );
        msg.setLastPx( _lastPx );
        if ( _lastMktLen > 0 ) msg.setLastMkt( _fixMsg, _lastMktStart, _lastMktLen );
        if ( _securityDescLen > 0 ) msg.setSecurityDesc( _fixMsg, _securityDescStart, _securityDescLen );
        msg.setMultiLegReportingType( _multiLegReportingType );
        msg.setLiquidityInd( _liquidityInd );
        return msg;
    }

    public final Event populateExecRptRejected() {
        RejectedImpl msg = _rejectedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setTradingStatus( _tradingStatus );
        return msg;
    }

    public final Event populateExecRptReplaced() {
        ReplacedImpl msg = _replacedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptCanceled() {
        CancelledImpl msg = _cancelledFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptDoneForDay() {
        DoneForDayImpl msg = _doneForDayFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptPendingCancel() {
        PendingCancelImpl msg = _pendingCancelFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptStopped() {
        StoppedImpl msg = _stoppedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptSuspended() {
        SuspendedImpl msg = _suspendedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptPendingNew() {
        PendingNewImpl msg = _pendingNewFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptCalculated() {
        CalculatedImpl msg = _calculatedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptExpired() {
        ExpiredImpl msg = _expiredFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        return msg;
    }

    public final Event populateExecRptRestated() {
        RestatedImpl msg = _restatedFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        msg.setExecRestatementReason( _execRestatementReason );
        return msg;
    }

    public final Event populateExecRptPendingReplace() {
        PendingReplaceImpl msg = _pendingReplaceFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
        if ( _origClOrdIdLen > 0 ) msg.setOrigClOrdId( _fixMsg, _origClOrdIdStart, _origClOrdIdLen );
        return msg;
    }

    public final Event populateExecRptTradeCorrect() {
        TradeCorrectImpl msg = _tradeCorrectFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
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
        TradeCancelImpl msg = _tradeCancelFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
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
        OrderStatusImpl msg = _orderStatusFactory.get();
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
        msg.setOrderQty( _orderQty );
        msg.setPrice( _price );
        msg.setSide( _side );
        if ( _symbolLen > 0 ) msg.setSymbol( _fixMsg, _symbolStart, _symbolLen );
        msg.setCurrency( _currency );
        msg.setSecurityIDSource( _securityIDSource );
        if ( _securityIdLen > 0 ) msg.setSecurityId( _fixMsg, _securityIdStart, _securityIdLen );
        msg.setTransactTime( _transactTime );
        msg.setAvgPx( _avgPx );
        msg.setCumQty( _cumQty );
        msg.setLeavesQty( _leavesQty );
        if ( _textLen > 0 ) msg.setText( _fixMsg, _textStart, _textLen );
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
        final NewOrderSingleImpl msg = _newOrderSingleFactory.get();
        if ( _nanoStats ) msg.setOrderReceived( getReceived() );
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.Account:         // tag1
                start = _idx;
                valLen = getValLength();
                msg.setAccount( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryDC44.HandlInst:         // tag21
                msg.setHandlInst( HandlInst.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.OrderQty:         // tag38
                msg.setOrderQty( getDoubleVal() );
                break;
            case FixDictionaryDC44.OrdType:         // tag40
                msg.setOrdType( OrdType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.Price:         // tag44
                msg.setPrice( getDoubleVal() );
                break;
            case FixDictionaryDC44.SecurityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.Side:         // tag54
                msg.setSide( Side.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.Symbol:         // tag55
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionaryDC44.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TimeInForce:         // tag59
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionaryDC44.ExDest:         // tag100
                start = _idx;
                valLen = getValLength();
                msg.setExDest( _fixMsg, start, valLen );
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
                case FixDictionaryDC44.expireTime:         // tag126
                    msg.setExpireTime( getInternalTime() );
                    break;
                case FixDictionaryDC44.effectiveTime:         // tag168
                    msg.setEffectiveTime( getInternalTime() );
                    break;
                case FixDictionaryDC44.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionaryDC44.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionaryDC44.parentClOrdId:         // tag526
                    start = _idx;
                    valLen = getValLength();
                    msg.setParentClOrdId( _fixMsg, start, valLen );
                    break;
                case FixDictionaryDC44.OrderCapacity:         // tag528
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionaryDC44.BookingType:         // tag775
                    msg.setBookingType( BookingType.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionaryDC44.TargetStrategy:         // tag847
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

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelReplaceRequest() {
        final CancelReplaceRequestImpl msg = _cancelReplaceRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.Account:         // tag1
                start = _idx;
                valLen = getValLength();
                msg.setAccount( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryDC44.HandlInst:         // tag21
                msg.setHandlInst( HandlInst.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.OrderQty:         // tag38
                msg.setOrderQty( getDoubleVal() );
                break;
            case FixDictionaryDC44.OrdType:         // tag40
                msg.setOrdType( OrdType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.Price:         // tag44
                msg.setPrice( getDoubleVal() );
                break;
            case FixDictionaryDC44.SecurityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.Side:         // tag54
                msg.setSide( Side.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.Symbol:         // tag55
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionaryDC44.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TimeInForce:         // tag59
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionaryDC44.ExDest:         // tag100
                start = _idx;
                valLen = getValLength();
                msg.setExDest( _fixMsg, start, valLen );
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
                case FixDictionaryDC44.expireTime:         // tag126
                    msg.setExpireTime( getInternalTime() );
                    break;
                case FixDictionaryDC44.effectiveTime:         // tag168
                    msg.setEffectiveTime( getInternalTime() );
                    break;
                case FixDictionaryDC44.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionaryDC44.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionaryDC44.parentClOrdId:         // tag526
                    start = _idx;
                    valLen = getValLength();
                    msg.setParentClOrdId( _fixMsg, start, valLen );
                    break;
                case FixDictionaryDC44.OrderCapacity:         // tag528
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionaryDC44.BookingType:         // tag775
                    msg.setBookingType( BookingType.getVal( _fixMsg[_idx++] ) );
                    break;
                case FixDictionaryDC44.TargetStrategy:         // tag847
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

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelRequest() {
        final CancelRequestImpl msg = _cancelRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.Account:         // tag1
                start = _idx;
                valLen = getValLength();
                msg.setAccount( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryDC44.SecurityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SecurityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.Symbol:         // tag55
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionaryDC44.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionaryDC44.ExDest:         // tag100
                start = _idx;
                valLen = getValLength();
                msg.setExDest( _fixMsg, start, valLen );
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
                case FixDictionaryDC44.maturityMonthYear:         // tag200
                    msg.setMaturityMonthYear( getIntVal() );
                    break;
                case FixDictionaryDC44.SecurityExchange:         // tag207
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case FixDictionaryDC44.parentClOrdId:         // tag526
                    start = _idx;
                    valLen = getValLength();
                    msg.setParentClOrdId( _fixMsg, start, valLen );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        enrich( msg );
        return msg;
    }

    public final Event decodeCancelReject() {
        final CancelRejectImpl msg = _cancelRejectFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.ClOrdId:         // tag11
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.OrderId:         // tag37
                start = _idx;
                valLen = getValLength();
                msg.setOrderId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.OrdStatus:         // tag39
                msg.setOrdStatus( OrdStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.OrigClOrdId:         // tag41
                start = _idx;
                valLen = getValLength();
                msg.setOrigClOrdId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                decodeSenderCompID();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                decodeSenderSubID();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                decodeTargetCompID();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                decodeTargetSubID();
                break;
            case FixDictionaryDC44.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.CxlRejReason:         // tag102
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
                case FixDictionaryDC44.CxlRejResponseTo:         // tag434
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionaryDC44.testReqID:         // tag112
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                start = _idx;
                valLen = getValLength();
                msg.setSenderSubId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                start = _idx;
                valLen = getValLength();
                msg.setTargetSubId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.RawDataLen:         // tag95
                msg.setRawDataLen( getIntVal() );
                break;
            case FixDictionaryDC44.RawData:         // tag96
                start = _idx;
                valLen = getValLength();
                msg.setRawData( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.EncryptMethod:         // tag98
                msg.setEncryptMethod( EncryptMethod.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.heartBtInt:         // tag108
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
                case FixDictionaryDC44.ResetSeqNumFlag:         // tag141
                    msg.setResetSeqNumFlag( _fixMsg[_idx++]=='Y' );
                    break;
                case FixDictionaryDC44.NextExpectedMsgSeqNum:         // tag789
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                start = _idx;
                valLen = getValLength();
                msg.setSenderSubId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                start = _idx;
                valLen = getValLength();
                msg.setTargetSubId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            default:
                switch( _tag ) {
                case FixDictionaryDC44.lastMsgSeqNumProcessed:         // tag369
                    msg.setLastMsgSeqNumProcessed( getIntVal() );
                    break;
                case FixDictionaryDC44.NextExpectedMsgSeqNum:         // tag789
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.RefSeqNum:         // tag45
                msg.setRefSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionaryDC44.Text:         // tag58
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
                case FixDictionaryDC44.RefTagID:         // tag371
                    msg.setRefTagID( getIntVal() );
                    break;
                case FixDictionaryDC44.RefMsgType:         // tag372
                    start = _idx;
                    valLen = getValLength();
                    msg.setRefMsgType( _fixMsg, start, valLen );
                    break;
                case FixDictionaryDC44.SessionRejectReason:         // tag373
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
            case FixDictionaryDC44.BeginSeqNo:         // tag7
                msg.setBeginSeqNo( getIntVal() );
                break;
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.EndSeqNo:         // tag16
                msg.setEndSeqNo( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.NewSeqNo:         // tag36
                msg.setNewSeqNo( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionaryDC44.GapFillFlag:         // tag123
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
            case FixDictionaryDC44.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryDC44.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryDC44.PossDupFlag:         // tag43
                msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                break;
            case FixDictionaryDC44.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryDC44.SenderSubID:         // tag50
                getValLength();
                break;
            case FixDictionaryDC44.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryDC44.TargetSubID:         // tag57
                getValLength();
                break;
            case FixDictionaryDC44.testReqID:         // tag112
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

    public final Event decodeAppRun() {
        final AppRunImpl msg = _appRunFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.unrealisedTotalPnL:         // tag9022
                msg.setUnrealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.realisedTotalPnL:         // tag9039
                msg.setRealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.liveStartTimestamp:         // tag9040
                msg.setLiveStartTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.status:         // tag9041
                msg.setStatus( RunStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.userName:         // tag9046
                start = _idx;
                valLen = getValLength();
                msg.setUserName( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.numTrades:         // tag9047
                msg.setNumTrades( getIntVal() );
                break;
            case FixDictionaryDC44.numStrategies:         // tag9048
                msg.setNumStrategies( getIntVal() );
                break;
            case FixDictionaryDC44.liveEndTimestamp:         // tag9051
                msg.setLiveEndTimestamp( getInternalTime() );
                break;
            default:
                switch( _tag ) {
                case FixDictionaryDC44.CheckSum:         // tag10
                    validateChecksum( getIntVal() );
                    break;
                case FixDictionaryDC44.MsgSeqNum:         // tag34
                    msg.setMsgSeqNum( getIntVal() );
                    break;
                case FixDictionaryDC44.PossDupFlag:         // tag43
                    msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                    break;
                case FixDictionaryDC44.SenderCompID:         // tag49
                    decodeSenderCompID();
                    break;
                case FixDictionaryDC44.SenderSubID:         // tag50
                    decodeSenderSubID();
                    break;
                case FixDictionaryDC44.SendingTime:         // tag52
                    msg.setEventTimestamp( getInternalTime() );
                    break;
                case FixDictionaryDC44.TargetCompID:         // tag56
                    decodeTargetCompID();
                    break;
                case FixDictionaryDC44.TargetSubID:         // tag57
                    decodeTargetSubID();
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

    public final Event decodeStrategyRun() {
        final StrategyRunImpl msg = _strategyRunFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.idOfExportComponent:         // tag9001
                start = _idx;
                valLen = getValLength();
                msg.setIdOfExportComponent( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.noInstEntries:         // tag9006
                int stratInstrumentNum = getIntVal(); // past delimiter
                msg.setNoInstEntries( stratInstrumentNum );
                if ( stratInstrumentNum > 0 ) {
                    _idx++; // past delimiter IF subgroups exist
                    processStratInstruments( msg, stratInstrumentNum );
                    continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                }
                break;
            case FixDictionaryDC44.unrealisedTotalPnL:         // tag9022
                msg.setUnrealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.realisedTotalPnL:         // tag9039
                msg.setRealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.liveStartTimestamp:         // tag9040
                msg.setLiveStartTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.status:         // tag9041
                msg.setStatus( RunStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.algoId:         // tag9042
                start = _idx;
                valLen = getValLength();
                msg.setAlgoId( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.btStartTimestamp:         // tag9043
                msg.setBtStartTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.btEndTimestamp:         // tag9044
                msg.setBtEndTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.strategyDefinition:         // tag9045
                start = _idx;
                valLen = getValLength();
                msg.setStrategyDefinition( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.userName:         // tag9046
                start = _idx;
                valLen = getValLength();
                msg.setUserName( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.numTrades:         // tag9047
                msg.setNumTrades( getIntVal() );
                break;
            case FixDictionaryDC44.stratTimeZone:         // tag9053
                start = _idx;
                valLen = getValLength();
                msg.setStratTimeZone( _fixMsg, start, valLen );
                break;
            case 9002: case 9003: case 9004: case 9005: case 9007: case 9008: case 9009: case 9010:  /* SKIP */
            case 9011: case 9012: case 9013: case 9014: case 9015: case 9016: case 9017: case 9018:  /* SKIP */
            case 9019: case 9020: case 9021: case 9023: case 9024: case 9025: case 9026: case 9027:  /* SKIP */
            case 9028: case 9029: case 9030: case 9031: case 9032: case 9033: case 9034: case 9035:  /* SKIP */
            case 9036: case 9037: case 9038: case 9048: case 9049: case 9050: case 9051: case 9052:  /* SKIP */
            
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryDC44.CheckSum:         // tag10
                    validateChecksum( getIntVal() );
                    break;
                case FixDictionaryDC44.MsgSeqNum:         // tag34
                    msg.setMsgSeqNum( getIntVal() );
                    break;
                case FixDictionaryDC44.PossDupFlag:         // tag43
                    msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                    break;
                case FixDictionaryDC44.SenderCompID:         // tag49
                    decodeSenderCompID();
                    break;
                case FixDictionaryDC44.SenderSubID:         // tag50
                    decodeSenderSubID();
                    break;
                case FixDictionaryDC44.SendingTime:         // tag52
                    msg.setEventTimestamp( getInternalTime() );
                    break;
                case FixDictionaryDC44.TargetCompID:         // tag56
                    decodeTargetCompID();
                    break;
                case FixDictionaryDC44.TargetSubID:         // tag57
                    decodeTargetSubID();
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

    public final Event decodeStrategyState() {
        final StrategyStateImpl msg = _strategyStateFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryDC44.idOfExportComponent:         // tag9001
                start = _idx;
                valLen = getValLength();
                msg.setIdOfExportComponent( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.noInstEntries:         // tag9006
                int stratInstrumentStateNum = getIntVal(); // past delimiter
                msg.setNoInstEntries( stratInstrumentStateNum );
                if ( stratInstrumentStateNum > 0 ) {
                    _idx++; // past delimiter IF subgroups exist
                    processStratInstrumentStates( msg, stratInstrumentStateNum );
                    continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                }
                break;
            case FixDictionaryDC44.unrealisedTotalPnL:         // tag9022
                msg.setUnrealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.realisedTotalPnL:         // tag9039
                msg.setRealisedTotalPnL( getDoubleVal() );
                break;
            case FixDictionaryDC44.liveStartTimestamp:         // tag9040
                msg.setLiveStartTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.status:         // tag9041
                msg.setStatus( RunStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryDC44.userName:         // tag9046
                start = _idx;
                valLen = getValLength();
                msg.setUserName( _fixMsg, start, valLen );
                break;
            case FixDictionaryDC44.stratTimestamp:         // tag9049
                msg.setStratTimestamp( getInternalTime() );
                break;
            case FixDictionaryDC44.stratStateMsgsInGrp:         // tag9054
                msg.setStratStateMsgsInGrp( getIntVal() );
                break;
            case FixDictionaryDC44.curStratStateMsgInGrp:         // tag9055
                msg.setCurStratStateMsgInGrp( getIntVal() );
                break;
            case FixDictionaryDC44.isDeltaMode:         // tag9056
                msg.setIsDeltaMode( _fixMsg[_idx++]=='Y' );
                break;
            case 9002: case 9003: case 9004: case 9005: case 9007: case 9008: case 9009: case 9010:  /* SKIP */
            case 9011: case 9012: case 9013: case 9014: case 9015: case 9016: case 9017: case 9018:  /* SKIP */
            case 9019: case 9020: case 9021: case 9023: case 9024: case 9025: case 9026: case 9027:  /* SKIP */
            case 9028: case 9029: case 9030: case 9031: case 9032: case 9033: case 9034: case 9035:  /* SKIP */
            case 9036: case 9037: case 9038: case 9042: case 9043: case 9044: case 9045: case 9047:  /* SKIP */
            case 9048: case 9050: case 9051: case 9052: case 9053: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryDC44.CheckSum:         // tag10
                    validateChecksum( getIntVal() );
                    break;
                case FixDictionaryDC44.MsgSeqNum:         // tag34
                    msg.setMsgSeqNum( getIntVal() );
                    break;
                case FixDictionaryDC44.PossDupFlag:         // tag43
                    msg.setPossDupFlag( _fixMsg[_idx++]=='Y' );
                    break;
                case FixDictionaryDC44.SenderCompID:         // tag49
                    decodeSenderCompID();
                    break;
                case FixDictionaryDC44.SenderSubID:         // tag50
                    decodeSenderSubID();
                    break;
                case FixDictionaryDC44.SendingTime:         // tag52
                    msg.setEventTimestamp( getInternalTime() );
                    break;
                case FixDictionaryDC44.TargetCompID:         // tag56
                    decodeTargetCompID();
                    break;
                case FixDictionaryDC44.TargetSubID:         // tag57
                    decodeTargetSubID();
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


   // SubGrps

    @SuppressWarnings( "null" )
    private void processStratInstruments( StrategyRunImpl parent, int numEntries ) {

       StratInstrumentImpl msg = null;
       StratInstrumentImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryDC44.SecurityIDSource:         // tag22
               msg = _stratInstrumentFactory.get();
               if ( lastUpdate == null ) {
                   parent.setInstruments( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               _securityIDSourceStart = 0;
               _securityIDSourceLen = 0;
               _securityIDStart = 0;
               _securityIDLen = 0;
               _securityExchangeStart = 0;
               _securityExchangeLen = 0;
               _securityIDSourceStart = _idx;
               _securityIDSourceLen = getValLength();
               break;
           case FixDictionaryDC44.SecurityID:         // tag48
               _securityIDStart = _idx;
               _securityIDLen = getValLength();
               break;
           case FixDictionaryDC44.SecurityExchange:         // tag207
               _securityExchangeStart = _idx;
               _securityExchangeLen = getValLength();
               msg.setInstrument( lookupInst() );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processStratInstrumentStates( StrategyStateImpl parent, int numEntries ) {

       StratInstrumentStateImpl msg = null;
       StratInstrumentStateImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryDC44.lastTickId:         // tag9004
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromLongRealisedTotalQty:         // tag9008
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromLongRealisedTotalLongValue:         // tag9009
               msg.setFromLongRealisedTotalLongValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromLongRealisedTotalShortValue:         // tag9010
               msg.setFromLongRealisedTotalShortValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromLongRealisedTotalLongAvePrice:         // tag9011
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromLongRealisedTotalShortAvePrice:         // tag9012
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromLongRealisedTotalPnL:         // tag9013
               msg.setFromLongRealisedTotalPnL( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromShortRealisedTotalQty:         // tag9014
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromShortRealisedTotalLongValue:         // tag9015
               msg.setFromShortRealisedTotalLongValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromShortRealisedTotalShortValue:         // tag9016
               msg.setFromShortRealisedTotalShortValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromShortRealisedTotalLongAvePrice:         // tag9017
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromShortRealisedTotalShortAvePrice:         // tag9018
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.fromShortRealisedTotalPnL:         // tag9019
               msg.setFromShortRealisedTotalPnL( getDoubleVal() );
               break;
           case FixDictionaryDC44.unrealisedTotalValue:         // tag9020
               msg.setUnrealisedTotalValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.unrealisedTotalAvePrice:         // tag9021
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.unrealisedTotalPnL:         // tag9022
               msg.setUnrealisedTotalPnL( getDoubleVal() );
               break;
           case FixDictionaryDC44.lastPrice:         // tag9023
               msg.setLastPrice( getDoubleVal() );
               break;
           case FixDictionaryDC44.position:         // tag9024
               msg.setPosition( getDoubleVal() );
               break;
           case FixDictionaryDC44.totalTradeQty:         // tag9025
               msg.setTotalTradeQty( getDoubleVal() );
               break;
           case FixDictionaryDC44.totalTradeVal:         // tag9026
               msg.setTotalTradeVal( getDoubleVal() );
               break;
           case FixDictionaryDC44.averagePrice:         // tag9027
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.pointValue:         // tag9028
               msg.setPointValue( getDoubleVal() );
               break;
           case FixDictionaryDC44.totalLongOrders:         // tag9029
               msg.setTotalLongOrders( getIntVal() );
               break;
           case FixDictionaryDC44.totalShortOrders:         // tag9030
               msg.setTotalShortOrders( getIntVal() );
               break;
           case FixDictionaryDC44.totLongContractsUnwound:         // tag9031
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.totShortContractsUnwound:         // tag9032
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.bidPx:         // tag9033
               msg.setBidPx( getDoubleVal() );
               break;
           case FixDictionaryDC44.askPx:         // tag9034
               msg.setAskPx( getDoubleVal() );
               break;
           case FixDictionaryDC44.lastDecidedPosition:         // tag9035
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.unwindPnl:         // tag9036
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.totLongOpenQty:         // tag9037
               msg.setTotLongOpenQty( getDoubleVal() );
               break;
           case FixDictionaryDC44.totShortOpenQty:         // tag9038
               msg.setTotShortOpenQty( getDoubleVal() );
               break;
           case FixDictionaryDC44.stratTimestamp:         // tag9049
               msg.setStratTimestamp( getInternalTime() );
               break;
           case FixDictionaryDC44.publishSeqNum:         // tag9052
               msg.setPublishSeqNum( getIntVal() );
               break;
           case FixDictionaryDC44.isActiveTracker:         // tag9057
               msg.setIsActiveTracker( _fixMsg[_idx++]=='Y' );
               break;
           case FixDictionaryDC44.unrealisedTotalPnLMin:         // tag9058
               msg.setUnrealisedTotalPnLMin( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromLongRealisedTotalPnLMin:         // tag9059
               msg.setFromLongRealisedTotalPnLMin( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromShortRealisedTotalPnLMin:         // tag9060
               msg.setFromShortRealisedTotalPnLMin( getDoubleVal() );
               break;
           case FixDictionaryDC44.unrealisedTotalPnLMax:         // tag9061
               msg.setUnrealisedTotalPnLMax( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromLongRealisedTotalPnLMax:         // tag9062
               msg.setFromLongRealisedTotalPnLMax( getDoubleVal() );
               break;
           case FixDictionaryDC44.fromShortRealisedTotalPnLMax:         // tag9063
               msg.setFromShortRealisedTotalPnLMax( getDoubleVal() );
               break;
           case FixDictionaryDC44.cashAllocation:         // tag9064
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryDC44.leverage:         // tag9065
               getValLength(); // no model attribute, SKIP
               break;
           case 9005: case 9006: case 9007: case 9039: case 9040: case 9041: case 9042: case 9043:  /* SKIP */
           case 9044: case 9045: case 9046: case 9047: case 9048: case 9050: case 9051: case 9053:  /* SKIP */
           case 9054: case 9055: case 9056:                return;
           default:
               switch( _tag ) {
               case FixDictionaryDC44.SecurityIDSource:         // tag22
                   msg = _stratInstrumentStateFactory.get();
                   if ( lastUpdate == null ) {
                       parent.setInstState( msg );
                   } else {
                       lastUpdate.setNext( msg );
                   }
                   lastUpdate = msg;
                   _securityIDSourceStart = 0;
                   _securityIDSourceLen = 0;
                   _securityIDStart = 0;
                   _securityIDLen = 0;
                   _securityExchangeStart = 0;
                   _securityExchangeLen = 0;
                   _securityIDSourceStart = _idx;
                   _securityIDSourceLen = getValLength();
                   break;
               case FixDictionaryDC44.SecurityID:         // tag48
                   _securityIDStart = _idx;
                   _securityIDLen = getValLength();
                   break;
               case FixDictionaryDC44.SecurityExchange:         // tag207
                   _securityExchangeStart = _idx;
                   _securityExchangeLen = getValLength();
                   msg.setInstrument( lookupInst() );
                   break;
               default:
                   return;
               }
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

   // transform methods

/**
 * HAND GENERATED TRANSFORMS
 */

     
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
