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
import com.rr.model.generated.fix.model.defn.FixDictionaryMD50;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;

@SuppressWarnings( "unused" )

public final class MD50Decoder extends AbstractFixDecoderMD50 {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs

    final String _id;

   // exec rpt only populated after all fields processed
   // only generate vars that are required

   // write String start and length vars required for ExecRpts


   // write value holders


   // forced var holders

    int _securityIDSourceStart = 0;
    int _securityIDSourceLen = 0;
    int _securityIDStart = 0;
    int _securityIDLen = 0;
    int _securityExchangeStart = 0;
    int _securityExchangeLen = 0;

   // Pools

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

    private final SuperPool<TradingSessionStatusImpl> _tradingSessionStatusPool = SuperpoolManager.instance().getSuperPool( TradingSessionStatusImpl.class );
    private final TradingSessionStatusFactory _tradingSessionStatusFactory = new TradingSessionStatusFactory( _tradingSessionStatusPool );

    private final SuperPool<MDRequestImpl> _mDRequestPool = SuperpoolManager.instance().getSuperPool( MDRequestImpl.class );
    private final MDRequestFactory _mDRequestFactory = new MDRequestFactory( _mDRequestPool );

    private final SuperPool<MassInstrumentStateChangeImpl> _massInstrumentStateChangePool = SuperpoolManager.instance().getSuperPool( MassInstrumentStateChangeImpl.class );
    private final MassInstrumentStateChangeFactory _massInstrumentStateChangeFactory = new MassInstrumentStateChangeFactory( _massInstrumentStateChangePool );

    private final SuperPool<SecurityStatusImpl> _securityStatusPool = SuperpoolManager.instance().getSuperPool( SecurityStatusImpl.class );
    private final SecurityStatusFactory _securityStatusFactory = new SecurityStatusFactory( _securityStatusPool );

    private final SuperPool<SecurityDefinitionImpl> _securityDefinitionPool = SuperpoolManager.instance().getSuperPool( SecurityDefinitionImpl.class );
    private final SecurityDefinitionFactory _securityDefinitionFactory = new SecurityDefinitionFactory( _securityDefinitionPool );

    private final SuperPool<SecDefEventImpl> _secDefEventPool = SuperpoolManager.instance().getSuperPool( SecDefEventImpl.class );
    private final SecDefEventFactory _secDefEventFactory = new SecDefEventFactory( _secDefEventPool );

    private final SuperPool<SecDefLegImpl> _secDefLegPool = SuperpoolManager.instance().getSuperPool( SecDefLegImpl.class );
    private final SecDefLegFactory _secDefLegFactory = new SecDefLegFactory( _secDefLegPool );

    private final SuperPool<SecurityAltIDImpl> _securityAltIDPool = SuperpoolManager.instance().getSuperPool( SecurityAltIDImpl.class );
    private final SecurityAltIDFactory _securityAltIDFactory = new SecurityAltIDFactory( _securityAltIDPool );

    private final SuperPool<SDFeedTypeImpl> _sDFeedTypePool = SuperpoolManager.instance().getSuperPool( SDFeedTypeImpl.class );
    private final SDFeedTypeFactory _sDFeedTypeFactory = new SDFeedTypeFactory( _sDFeedTypePool );

    private final SuperPool<MDIncRefreshImpl> _mDIncRefreshPool = SuperpoolManager.instance().getSuperPool( MDIncRefreshImpl.class );
    private final MDIncRefreshFactory _mDIncRefreshFactory = new MDIncRefreshFactory( _mDIncRefreshPool );

    private final SuperPool<MDEntryImpl> _mDEntryPool = SuperpoolManager.instance().getSuperPool( MDEntryImpl.class );
    private final MDEntryFactory _mDEntryFactory = new MDEntryFactory( _mDEntryPool );

    private final SuperPool<MDSnapshotFullRefreshImpl> _mDSnapshotFullRefreshPool = SuperpoolManager.instance().getSuperPool( MDSnapshotFullRefreshImpl.class );
    private final MDSnapshotFullRefreshFactory _mDSnapshotFullRefreshFactory = new MDSnapshotFullRefreshFactory( _mDSnapshotFullRefreshPool );

    private final SuperPool<MDSnapEntryImpl> _mDSnapEntryPool = SuperpoolManager.instance().getSuperPool( MDSnapEntryImpl.class );
    private final MDSnapEntryFactory _mDSnapEntryFactory = new MDSnapEntryFactory( _mDSnapEntryPool );

    private final SuperPool<SecurityDefinitionUpdateImpl> _securityDefinitionUpdatePool = SuperpoolManager.instance().getSuperPool( SecurityDefinitionUpdateImpl.class );
    private final SecurityDefinitionUpdateFactory _securityDefinitionUpdateFactory = new SecurityDefinitionUpdateFactory( _securityDefinitionUpdatePool );

    private final SuperPool<ProductSnapshotImpl> _productSnapshotPool = SuperpoolManager.instance().getSuperPool( ProductSnapshotImpl.class );
    private final ProductSnapshotFactory _productSnapshotFactory = new ProductSnapshotFactory( _productSnapshotPool );


   // Constructors
    public MD50Decoder( String id ) {
        this( id, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor );
    }

    public MD50Decoder() {
        this( null, FixVersion.MDFix5_0._major, FixVersion.MDFix5_0._minor );
    }

    public MD50Decoder( byte major, byte minor ) {
        this( null, major, minor );
    }
    public MD50Decoder( String id, byte major, byte minor ) {
        super( major, minor );
        _id = id;
    }

    @Override public FixDecoder newInstance() {
        MD50Decoder dec = new MD50Decoder( getComponentId(), _majorVersion, _minorVersion );
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
        case 'h':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeTradingSessionStatus();
        case 'V':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeMDRequest();
        case 'C':
            {
                byte msgType2 = _fixMsg[ _idx+1 ];
                if ( msgType2 != 'O' ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 );
                }
                _idx += 3;
                return decodeMassInstrumentStateChange();
            }
        case 'f':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeSecurityStatus();
        case 'd':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeSecurityDefinition();
        case 'X':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeMDIncRefresh();
        case 'W':
            if ( _fixMsg[_idx+1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[_idx] + _fixMsg[_idx+1] );
            }
            _idx += 2;
            return decodeMDSnapshotFullRefresh();
        case 'B':
          {
            byte msgType2 = _fixMsg[ _idx+1 ];
            switch( msgType2 ) {
            case 'P':
                if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );
                }
                _idx += 3;
                return decodeSecurityDefinitionUpdate();
            case 'U':
                if ( _fixMsg[_idx+2 ] != FixField.FIELD_DELIMITER ) {
                    throwDecodeException( "Unsupported fix message type " + (char)msgType + (char)msgType2 + (char)_fixMsg[_idx+2 ] );
                }
                _idx += 3;
                return decodeProductSnapshot();
            }
            _idx += 3;
            throwDecodeException( "Unsupported fix message type " + msgType );
            return null;
          }
        case '6':
        case '7':
        case '8':
        case '9':
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
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
        case 'e':
        case 'g':
            break;
        }
        _idx += 2;
        throwDecodeException( "Unsupported fix message type " + msgType );
        return null;
    }


    public final Event decodeHeartbeat() {
        final HeartbeatImpl msg = _heartbeatFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.testReqID:         // tag112
                start = _idx;
                valLen = getValLength();
                msg.setTestReqID( _fixMsg, start, valLen );
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
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.RawDataLen:         // tag95
                msg.setRawDataLen( getIntVal() );
                break;
            case FixDictionaryMD50.RawData:         // tag96
                start = _idx;
                valLen = getValLength();
                msg.setRawData( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.EncryptMethod:         // tag98
                msg.setEncryptMethod( EncryptMethod.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.heartBtInt:         // tag108
                msg.setHeartBtInt( getIntVal() );
                break;
            case FixDictionaryMD50.ResetSeqNumFlag:         // tag141
                msg.setResetSeqNumFlag( _fixMsg[_idx++]=='Y' );
                break;
            default:
                switch( _tag ) {
                case FixDictionaryMD50.CheckSum:         // tag10
                    validateChecksum( getIntVal() );
                    break;
                case FixDictionaryMD50.NextExpectedMsgSeqNum:         // tag789
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
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                start = _idx;
                valLen = getValLength();
                msg.setSenderCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                start = _idx;
                valLen = getValLength();
                msg.setTargetCompId( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
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

    public final Event decodeSessionReject() {
        final SessionRejectImpl msg = _sessionRejectFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.RefSeqNum:         // tag45
                msg.setRefSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.Text:         // tag58
                start = _idx;
                valLen = getValLength();
                msg.setText( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.RefTagID:         // tag371
                msg.setRefTagID( getIntVal() );
                break;
            case FixDictionaryMD50.RefMsgType:         // tag372
                start = _idx;
                valLen = getValLength();
                msg.setRefMsgType( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.SessionRejectReason:         // tag373
                start = _idx;
                valLen = getValLength();
                msg.setSessionRejectReason( SessionRejectReason.getVal( _fixMsg, start, valLen ) );
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

    public final Event decodeResendRequest() {
        final ResendRequestImpl msg = _resendRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.BeginSeqNo:         // tag7
                msg.setBeginSeqNo( getIntVal() );
                break;
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.EndSeqNo:         // tag16
                msg.setEndSeqNo( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
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
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.NewSeqNo:         // tag36
                msg.setNewSeqNo( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.GapFillFlag:         // tag123
                msg.setGapFillFlag( _fixMsg[_idx++]=='Y' );
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
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.testReqID:         // tag112
                start = _idx;
                valLen = getValLength();
                msg.setTestReqID( _fixMsg, start, valLen );
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

    public final Event decodeTradingSessionStatus() {
        final TradingSessionStatusImpl msg = _tradingSessionStatusFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionaryMD50.TradingSessionID:         // tag336
                msg.setTradingSessionID( TradingSessionID.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.TradSesStatus:         // tag340
                msg.setTradSesStatus( TradSesStatus.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.TradingSessionSubID:         // tag625
                msg.setTradingSessionSubID( TradingSessionSubID.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.marketSegmentId:         // tag1300
                msg.setMarketSegmentID( getIntVal() );
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

    public final Event decodeMDRequest() {
        final MDRequestImpl msg = _mDRequestFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.numRelatedSym:         // tag146
                int symbolRepeatingGrpNum = getIntVal(); // past delimiter
                msg.setNumRelatedSym( symbolRepeatingGrpNum );
                if ( symbolRepeatingGrpNum > 0 ) {
                    _idx++; // past delimiter IF subgroups exist
                    processSymbolRepeatingGrps( msg, symbolRepeatingGrpNum );
                    continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                }
                break;
            case FixDictionaryMD50.mdReqId:         // tag262
                start = _idx;
                valLen = getValLength();
                msg.setMdReqId( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.subsReqType:         // tag263
                msg.setSubsReqType( SubsReqType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.marketDepth:         // tag264
                msg.setMarketDepth( getIntVal() );
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

    public final Event decodeMassInstrumentStateChange() {
        final MassInstrumentStateChangeImpl msg = _massInstrumentStateChangeFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.TransactTime:         // tag60
                msg.setTransactTime( getInternalTime() );
                break;
            case FixDictionaryMD50.numRelatedSym:         // tag146
                int secMassStatGrpNum = getIntVal(); // past delimiter
                msg.setNumRelatedSym( secMassStatGrpNum );
                if ( secMassStatGrpNum > 0 ) {
                    _idx++; // past delimiter IF subgroups exist
                    processSecMassStatGrps( msg, secMassStatGrpNum );
                    continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                }
                break;
            case FixDictionaryMD50.marketSegmentId:         // tag1300
                msg.setMarketSegmentID( getIntVal() );
                break;
            case FixDictionaryMD50.instrumentScopeProductComplex:         // tag1544
                msg.setInstrumentScopeProductComplex( getIntVal() );
                break;
            case FixDictionaryMD50.securityMassTradingStatus:         // tag1679
                msg.setSecurityMassTradingStatus( getIntVal() );
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

    public final Event decodeSecurityStatus() {
        final SecurityStatusImpl msg = _securityStatusFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.securityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.securityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityID( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.Symbol:         // tag55
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.TradeDate:         // tag75
                msg.setTradeDate( getIntVal() );
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 23: case 24: case 25: case 26: case 27:  /* SKIP */
            case 28: case 29: case 30: case 31: case 32: case 33: case 35: case 36:  /* SKIP */
            case 37: case 38: case 39: case 40: case 41: case 42: case 43: case 44:  /* SKIP */
            case 45: case 46: case 47: case 50: case 51: case 53: case 54: case 57:  /* SKIP */
            case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 65:  /* SKIP */
            case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73:  /* SKIP */
            case 74: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryMD50.securityTradingStatus:         // tag326
                    start = _idx;
                    valLen = getValLength();
                    msg.setSecurityTradingStatus( SecurityTradingStatus.getVal( _fixMsg, start, valLen ) );
                    break;
                case FixDictionaryMD50.haltReason:         // tag327
                    msg.setHaltReason( getIntVal() );
                    break;
                case FixDictionaryMD50.highPx:         // tag332
                    msg.setHighPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.lowPx:         // tag333
                    msg.setLowPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.SecurityTradingEvent:         // tag1174
                    msg.setSecurityTradingEvent( getIntVal() );
                    break;
                default:
                    getValLength();
                    break;
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        if ( msg.getSecurityExchange() == ExchangeCode.UNKNOWN ) msg.setSecurityExchange( _defaultExchange );
        return msg;
    }

    public final Event decodeSecurityDefinition() {
        final SecurityDefinitionImpl msg = _securityDefinitionFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.displayFactor:         // tag9787
                msg.setDisplayFactor( getDoubleVal() );
                break;
            case FixDictionaryMD50.securityLongDesc:         // tag9800
                start = _idx;
                valLen = getValLength();
                msg.setSecurityLongDesc( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.underlyingSecurityIDSource:         // tag9801
                msg.setUnderlyingSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.underlyingSecurityID:         // tag9802
                start = _idx;
                valLen = getValLength();
                msg.setUnderlyingSecurityID( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.underlyingScurityExchange:         // tag9803
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setUnderlyingScurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryMD50.primarySecurityExchange:         // tag9804
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setPrimarySecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryMD50.commonSecurityId:         // tag9805
                msg.setCommonSecurityId( getLongVal() );
                break;
            case FixDictionaryMD50.parentCompanyId:         // tag9806
                msg.setParentCompanyId( getLongVal() );
                break;
            case FixDictionaryMD50.companyName:         // tag9811
                start = _idx;
                valLen = getValLength();
                msg.setCompanyName( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.gicsCode:         // tag9812
                msg.setGicsCode( getLongVal() );
                break;
            case FixDictionaryMD50.getOutDate:         // tag9813
                msg.setGetOutDate( getInternalTime() );
                break;
            case FixDictionaryMD50.uniqueInstId:         // tag9814
                msg.setUniqueInstId( getLongVal() );
                break;
            case FixDictionaryMD50.deadTimestamp:         // tag9815
                msg.setDeadTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.endTimestamp:         // tag9816
                msg.setEndTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.startTimestamp:         // tag9817
                msg.setStartTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.dataSrc:         // tag9818
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setDataSrc( DataSrc.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryMD50.SecDefSpecialType:         // tag9819
                msg.setSecDefSpecialType( SecDefSpecialType.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.CompanyStatusType:         // tag9820
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCompanyStatusType( CompanyStatusType.getVal( _tmpLookupKey ) );
                break;
            case 9788: case 9789: case 9790: case 9791: case 9792: case 9793: case 9794: case 9795:  /* SKIP */
            case 9796: case 9797: case 9798: case 9799: case 9807: case 9808: case 9809: case 9810:  /* SKIP */
            
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryMD50.maxTradeVol:         // tag1140
                    msg.setMaxTradeVol( getLongVal() );
                    break;
                case FixDictionaryMD50.noSDFeedTypes:         // tag1141
                    int sDFeedTypeNum = getIntVal(); // past delimiter
                    msg.setNoSDFeedTypes( sDFeedTypeNum );
                    if ( sDFeedTypeNum > 0 ) {
                        _idx++; // past delimiter IF subgroups exist
                        processSDFeedTypes( msg, sDFeedTypeNum );
                        continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                    }
                    break;
                case FixDictionaryMD50.minPriceIncrementAmount:         // tag1146
                    msg.setMinPriceIncrementAmount( getDoubleVal() );
                    break;
                case FixDictionaryMD50.unitOfMeasureQty:         // tag1147
                    msg.setUnitOfMeasureQty( getDoubleVal() );
                    break;
                case FixDictionaryMD50.lowLimitPx:         // tag1148
                    msg.setLowLimitPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.highLimitPx:         // tag1149
                    msg.setHighLimitPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.tradingReferencePrice:         // tag1150
                    msg.setTradingReferencePrice( getDoubleVal() );
                    break;
                case FixDictionaryMD50.securityGroup:         // tag1151
                    start = _idx;
                    valLen = getValLength();
                    msg.setSecurityGroup( _fixMsg, start, valLen );
                    break;
                case FixDictionaryMD50.applID:         // tag1180
                    start = _idx;
                    valLen = getValLength();
                    msg.setApplID( _fixMsg, start, valLen );
                    break;
                case FixDictionaryMD50.pricePrecision:         // tag1200
                    msg.setPricePrecision( getDoubleVal() );
                    break;
                case 1142: case 1143: case 1144: case 1145: case 1152: case 1153: case 1154: case 1155:  /* SKIP */
                case 1156: case 1157: case 1158: case 1159: case 1160: case 1161: case 1162: case 1163:  /* SKIP */
                case 1164: case 1165: case 1166: case 1167: case 1168: case 1169: case 1170: case 1171:  /* SKIP */
                case 1172: case 1173: case 1174: case 1175: case 1176: case 1177: case 1178: case 1179:  /* SKIP */
                case 1181: case 1182: case 1183: case 1184: case 1185: case 1186: case 1187: case 1188:  /* SKIP */
                case 1189: case 1190: case 1191: case 1192: case 1193: case 1194: case 1195: case 1196:  /* SKIP */
                case 1197: case 1198: case 1199: 
                    getValLength();
                    break;
                default:
                    switch( _tag ) {
                    case FixDictionaryMD50.CheckSum:         // tag10
                        validateChecksum( getIntVal() );
                        break;
                    case FixDictionaryMD50.Currency:         // tag15
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.securityIDSource:         // tag22
                        msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                        break;
                    case FixDictionaryMD50.MsgSeqNum:         // tag34
                        msg.setMsgSeqNum( getIntVal() );
                        break;
                    case FixDictionaryMD50.securityID:         // tag48
                        start = _idx;
                        valLen = getValLength();
                        msg.setSecurityID( _fixMsg, start, valLen );
                        break;
                    case FixDictionaryMD50.SenderCompID:         // tag49
                        getValLength();
                        break;
                    case FixDictionaryMD50.SendingTime:         // tag52
                        msg.setEventTimestamp( getInternalTime() );
                        break;
                    case FixDictionaryMD50.Symbol:         // tag55
                        start = _idx;
                        valLen = getValLength();
                        msg.setSymbol( _fixMsg, start, valLen );
                        break;
                    case FixDictionaryMD50.TargetCompID:         // tag56
                        getValLength();
                        break;
                    case 11: case 12: case 13: case 14: case 16: case 17: case 18: case 19:  /* SKIP */
                    case 20: case 21: case 23: case 24: case 25: case 26: case 27: case 28:  /* SKIP */
                    case 29: case 30: case 31: case 32: case 33: case 35: case 36: case 37:  /* SKIP */
                    case 38: case 39: case 40: case 41: case 42: case 43: case 44: case 45:  /* SKIP */
                    case 46: case 47: case 50: case 51: case 53: case 54: 
                        getValLength();
                        break;
                    default:
                        switch( _tag ) {
                        case FixDictionaryMD50.securityDesc:         // tag107
                            start = _idx;
                            valLen = getValLength();
                            msg.setSecurityDesc( _fixMsg, start, valLen );
                            break;
                        case FixDictionaryMD50.minQty:         // tag110
                            msg.setMinQty( getIntVal() );
                            break;
                        case FixDictionaryMD50.settlCurrency:         // tag120
                            start = _idx;
                            valLen = getValLength();
                            _tmpLookupKey.setValue( _fixMsg, start, valLen );
                            msg.setSettlCurrency( Currency.getVal( _tmpLookupKey ) );
                            break;
                        case FixDictionaryMD50.securityType:         // tag167
                            start = _idx;
                            valLen = getValLength();
                            _tmpLookupKey.setValue( _fixMsg, start, valLen );
                            msg.setSecurityType( SecurityType.getVal( _tmpLookupKey ) );
                            break;
                        case FixDictionaryMD50.maturityMonthYear:         // tag200
                            msg.setMaturityMonthYear( getIntVal() );
                            break;
                        case FixDictionaryMD50.strikePrice:         // tag202
                            msg.setStrikePrice( getDoubleVal() );
                            break;
                        case FixDictionaryMD50.securityExchange:         // tag207
                            start = _idx;
                            valLen = getValLength();
                            _tmpLookupKey.setValue( _fixMsg, start, valLen );
                            msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                            break;
                        case FixDictionaryMD50.contractMultiplier:         // tag231
                            msg.setContractMultiplier( getDoubleVal() );
                            break;
                        case FixDictionaryMD50.noSecurityAltID:         // tag454
                            int securityAltIDNum = getIntVal(); // past delimiter
                            msg.setNoSecurityAltID( securityAltIDNum );
                            if ( securityAltIDNum > 0 ) {
                                _idx++; // past delimiter IF subgroups exist
                                processSecurityAltIDs( msg, securityAltIDNum );
                                continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                            }
                            break;
                        case FixDictionaryMD50.CFICode:         // tag461
                            start = _idx;
                            valLen = getValLength();
                            msg.setCFICode( _fixMsg, start, valLen );
                            break;
                        case FixDictionaryMD50.underlyingProduct:         // tag462
                            start = _idx;
                            valLen = getValLength();
                            msg.setUnderlyingProduct( ProductComplex.getVal( _fixMsg, start, valLen ) );
                            break;
                        case FixDictionaryMD50.noLegs:         // tag555
                            int secDefLegNum = getIntVal(); // past delimiter
                            msg.setNoLegs( secDefLegNum );
                            if ( secDefLegNum > 0 ) {
                                _idx++; // past delimiter IF subgroups exist
                                processSecDefLegs( msg, secDefLegNum );
                                continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                            }
                            break;
                        case FixDictionaryMD50.minTradeVol:         // tag562
                            msg.setMinTradeVol( getLongVal() );
                            break;
                        case FixDictionaryMD50.noEvents:         // tag864
                            int secDefEventNum = getIntVal(); // past delimiter
                            msg.setNoEvents( secDefEventNum );
                            if ( secDefEventNum > 0 ) {
                                _idx++; // past delimiter IF subgroups exist
                                processSecDefEvents( msg, secDefEventNum );
                                continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                            }
                            break;
                        case FixDictionaryMD50.totNumReports:         // tag911
                            msg.setTotNumReports( getIntVal() );
                            break;
                        case FixDictionaryMD50.strikeCurrency:         // tag947
                            start = _idx;
                            valLen = getValLength();
                            _tmpLookupKey.setValue( _fixMsg, start, valLen );
                            msg.setStrikeCurrency( Currency.getVal( _tmpLookupKey ) );
                            break;
                        case FixDictionaryMD50.minPriceIncrement:         // tag969
                            msg.setMinPriceIncrement( getDoubleVal() );
                            break;
                        case FixDictionaryMD50.SecurityUpdateAction:         // tag980
                            msg.setSecurityUpdateAction( SecurityUpdateAction.getVal( _fixMsg[_idx++] ) );
                            break;
                        case FixDictionaryMD50.unitOfMeasure:         // tag996
                            start = _idx;
                            valLen = getValLength();
                            _tmpLookupKey.setValue( _fixMsg, start, valLen );
                            msg.setUnitOfMeasure( UnitOfMeasure.getVal( _tmpLookupKey ) );
                            break;
                        case FixDictionaryMD50.contractMultiplierType:         // tag1435
                            msg.setContractMultiplierType( getIntVal() );
                            break;
                        case FixDictionaryMD50.futPointValue:         // tag1700
                            msg.setFutPointValue( getDoubleVal() );
                            break;
                        case FixDictionaryMD50.priceRatio:         // tag5770
                            msg.setPriceRatio( getDoubleVal() );
                            break;
                        case FixDictionaryMD50.openInterestQty:         // tag5792
                            msg.setOpenInterestQty( getIntVal() );
                            break;
                        case FixDictionaryMD50.tradingReferenceDate:         // tag5796
                            msg.setTradingReferenceDate( getIntVal() );
                            break;
                        case FixDictionaryMD50.TickRule:         // tag6350
                            msg.setTickRule( getIntVal() );
                            break;
                        default:
                            getValLength();
                            break;
                        }
                    }
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeMDIncRefresh() {
        final MDIncRefreshImpl msg = _mDIncRefreshFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case FixDictionaryMD50.noMDEntries:         // tag268
                int mDEntryNum = getIntVal(); // past delimiter
                msg.setNoMDEntries( mDEntryNum );
                if ( mDEntryNum > 0 ) {
                    _idx++; // past delimiter IF subgroups exist
                    processMDEntrys( msg, mDEntryNum );
                    continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                }
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

    public final Event decodeMDSnapshotFullRefresh() {
        final MDSnapshotFullRefreshImpl msg = _mDSnapshotFullRefreshFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.securityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.securityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityID( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:  /* SKIP */
            case 19: case 20: case 21: case 23: case 24: case 25: case 26: case 27:  /* SKIP */
            case 28: case 29: case 30: case 31: case 32: case 33: case 35: case 36:  /* SKIP */
            case 37: case 38: case 39: case 40: case 41: case 42: case 43: case 44:  /* SKIP */
            case 45: case 46: case 47: case 50: case 51: case 53: case 54: case 55:  /* SKIP */
            
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryMD50.noMDEntries:         // tag268
                    int mDSnapEntryNum = getIntVal(); // past delimiter
                    msg.setNoMDEntries( mDSnapEntryNum );
                    if ( mDSnapEntryNum > 0 ) {
                        _idx++; // past delimiter IF subgroups exist
                        processMDSnapEntrys( msg, mDSnapEntryNum );
                        continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                    }
                    break;
                case FixDictionaryMD50.lastMsgSeqNumProcessed:         // tag369
                    msg.setLastMsgSeqNumProcessed( getIntVal() );
                    break;
                case FixDictionaryMD50.totNumReports:         // tag911
                    msg.setTotNumReports( getIntVal() );
                    break;
                case FixDictionaryMD50.mdBookType:         // tag1021
                    msg.setMdBookType( getIntVal() );
                    break;
                case FixDictionaryMD50.mdSecurityTradingStatus:         // tag1682
                    msg.setMdSecurityTradingStatus( getIntVal() );
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

    public final Event decodeSecurityDefinitionUpdate() {
        final SecurityDefinitionUpdateImpl msg = _securityDefinitionUpdateFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.Currency:         // tag15
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case FixDictionaryMD50.securityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.securityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityID( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.Symbol:         // tag55
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
                getValLength();
                break;
            case 11: case 12: case 13: case 14: case 16: case 17: case 18: case 19:  /* SKIP */
            case 20: case 21: case 23: case 24: case 25: case 26: case 27: case 28:  /* SKIP */
            case 29: case 30: case 31: case 32: case 33: case 35: case 36: case 37:  /* SKIP */
            case 38: case 39: case 40: case 41: case 42: case 43: case 44: case 45:  /* SKIP */
            case 46: case 47: case 50: case 51: case 53: case 54: 
                getValLength();
                break;
            default:
                switch( _tag ) {
                case FixDictionaryMD50.maxTradeVol:         // tag1140
                    msg.setMaxTradeVol( getLongVal() );
                    break;
                case FixDictionaryMD50.noSDFeedTypes:         // tag1141
                    int sDFeedTypeNum = getIntVal(); // past delimiter
                    msg.setNoSDFeedTypes( sDFeedTypeNum );
                    if ( sDFeedTypeNum > 0 ) {
                        _idx++; // past delimiter IF subgroups exist
                        processSDFeedTypes( msg, sDFeedTypeNum );
                        continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                    }
                    break;
                case FixDictionaryMD50.minPriceIncrementAmount:         // tag1146
                    msg.setMinPriceIncrementAmount( getDoubleVal() );
                    break;
                case FixDictionaryMD50.unitOfMeasureQty:         // tag1147
                    msg.setUnitOfMeasureQty( getDoubleVal() );
                    break;
                case FixDictionaryMD50.lowLimitPx:         // tag1148
                    msg.setLowLimitPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.highLimitPx:         // tag1149
                    msg.setHighLimitPx( getDoubleVal() );
                    break;
                case FixDictionaryMD50.tradingReferencePrice:         // tag1150
                    msg.setTradingReferencePrice( getDoubleVal() );
                    break;
                case FixDictionaryMD50.securityGroup:         // tag1151
                    start = _idx;
                    valLen = getValLength();
                    msg.setSecurityGroup( _fixMsg, start, valLen );
                    break;
                case FixDictionaryMD50.applID:         // tag1180
                    start = _idx;
                    valLen = getValLength();
                    msg.setApplID( _fixMsg, start, valLen );
                    break;
                case FixDictionaryMD50.pricePrecision:         // tag1200
                    msg.setPricePrecision( getDoubleVal() );
                    break;
                case 1142: case 1143: case 1144: case 1145: case 1152: case 1153: case 1154: case 1155:  /* SKIP */
                case 1156: case 1157: case 1158: case 1159: case 1160: case 1161: case 1162: case 1163:  /* SKIP */
                case 1164: case 1165: case 1166: case 1167: case 1168: case 1169: case 1170: case 1171:  /* SKIP */
                case 1172: case 1173: case 1174: case 1175: case 1176: case 1177: case 1178: case 1179:  /* SKIP */
                case 1181: case 1182: case 1183: case 1184: case 1185: case 1186: case 1187: case 1188:  /* SKIP */
                case 1189: case 1190: case 1191: case 1192: case 1193: case 1194: case 1195: case 1196:  /* SKIP */
                case 1197: case 1198: case 1199: 
                    getValLength();
                    break;
                default:
                    switch( _tag ) {
                    case FixDictionaryMD50.securityDesc:         // tag107
                        start = _idx;
                        valLen = getValLength();
                        msg.setSecurityDesc( _fixMsg, start, valLen );
                        break;
                    case FixDictionaryMD50.minQty:         // tag110
                        msg.setMinQty( getIntVal() );
                        break;
                    case FixDictionaryMD50.settlCurrency:         // tag120
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setSettlCurrency( Currency.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.securityType:         // tag167
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setSecurityType( SecurityType.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.maturityMonthYear:         // tag200
                        msg.setMaturityMonthYear( getIntVal() );
                        break;
                    case FixDictionaryMD50.strikePrice:         // tag202
                        msg.setStrikePrice( getDoubleVal() );
                        break;
                    case FixDictionaryMD50.securityExchange:         // tag207
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.contractMultiplier:         // tag231
                        msg.setContractMultiplier( getDoubleVal() );
                        break;
                    case FixDictionaryMD50.noSecurityAltID:         // tag454
                        int securityAltIDNum = getIntVal(); // past delimiter
                        msg.setNoSecurityAltID( securityAltIDNum );
                        if ( securityAltIDNum > 0 ) {
                            _idx++; // past delimiter IF subgroups exist
                            processSecurityAltIDs( msg, securityAltIDNum );
                            continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                        }
                        break;
                    case FixDictionaryMD50.CFICode:         // tag461
                        start = _idx;
                        valLen = getValLength();
                        msg.setCFICode( _fixMsg, start, valLen );
                        break;
                    case FixDictionaryMD50.underlyingProduct:         // tag462
                        start = _idx;
                        valLen = getValLength();
                        msg.setUnderlyingProduct( ProductComplex.getVal( _fixMsg, start, valLen ) );
                        break;
                    case FixDictionaryMD50.noLegs:         // tag555
                        int secDefLegNum = getIntVal(); // past delimiter
                        msg.setNoLegs( secDefLegNum );
                        if ( secDefLegNum > 0 ) {
                            _idx++; // past delimiter IF subgroups exist
                            processSecDefLegs( msg, secDefLegNum );
                            continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                        }
                        break;
                    case FixDictionaryMD50.minTradeVol:         // tag562
                        msg.setMinTradeVol( getLongVal() );
                        break;
                    case FixDictionaryMD50.noEvents:         // tag864
                        int secDefEventNum = getIntVal(); // past delimiter
                        msg.setNoEvents( secDefEventNum );
                        if ( secDefEventNum > 0 ) {
                            _idx++; // past delimiter IF subgroups exist
                            processSecDefEvents( msg, secDefEventNum );
                            continue; // ALREADY HAVE NEXT TAG MUST RE-EVALUATE
                        }
                        break;
                    case FixDictionaryMD50.totNumReports:         // tag911
                        msg.setTotNumReports( getIntVal() );
                        break;
                    case FixDictionaryMD50.strikeCurrency:         // tag947
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setStrikeCurrency( Currency.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.minPriceIncrement:         // tag969
                        msg.setMinPriceIncrement( getDoubleVal() );
                        break;
                    case FixDictionaryMD50.SecurityUpdateAction:         // tag980
                        msg.setSecurityUpdateAction( SecurityUpdateAction.getVal( _fixMsg[_idx++] ) );
                        break;
                    case FixDictionaryMD50.unitOfMeasure:         // tag996
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setUnitOfMeasure( UnitOfMeasure.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.contractMultiplierType:         // tag1435
                        msg.setContractMultiplierType( getIntVal() );
                        break;
                    case FixDictionaryMD50.priceRatio:         // tag5770
                        msg.setPriceRatio( getDoubleVal() );
                        break;
                    case FixDictionaryMD50.openInterestQty:         // tag5792
                        msg.setOpenInterestQty( getIntVal() );
                        break;
                    case FixDictionaryMD50.tradingReferenceDate:         // tag5796
                        msg.setTradingReferenceDate( getIntVal() );
                        break;
                    case FixDictionaryMD50.displayFactor:         // tag9787
                        msg.setDisplayFactor( getDoubleVal() );
                        break;
                    case FixDictionaryMD50.deadTimestamp:         // tag9815
                        msg.setDeadTimestamp( getInternalTime() );
                        break;
                    case FixDictionaryMD50.endTimestamp:         // tag9816
                        msg.setEndTimestamp( getInternalTime() );
                        break;
                    case FixDictionaryMD50.startTimestamp:         // tag9817
                        msg.setStartTimestamp( getInternalTime() );
                        break;
                    case FixDictionaryMD50.dataSrc:         // tag9818
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setDataSrc( DataSrc.getVal( _tmpLookupKey ) );
                        break;
                    case FixDictionaryMD50.SecDefSpecialType:         // tag9819
                        msg.setSecDefSpecialType( SecDefSpecialType.getVal( _fixMsg[_idx++] ) );
                        break;
                    case FixDictionaryMD50.CompanyStatusType:         // tag9820
                        start = _idx;
                        valLen = getValLength();
                        _tmpLookupKey.setValue( _fixMsg, start, valLen );
                        msg.setCompanyStatusType( CompanyStatusType.getVal( _tmpLookupKey ) );
                        break;
                    default:
                        getValLength();
                        break;
                    }
                }
            }
            _idx++; /* past delimiter */ 
            _tag = getTag();
        }

        return msg;
    }

    public final Event decodeProductSnapshot() {
        final ProductSnapshotImpl msg = _productSnapshotFactory.get();
        _tag = getTag();

        int start;
        int valLen;

        while( _tag != 0 ) {
            switch( _tag ) {
            case FixDictionaryMD50.CheckSum:         // tag10
                validateChecksum( getIntVal() );
                break;
            case FixDictionaryMD50.securityIDSource:         // tag22
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                break;
            case FixDictionaryMD50.MsgSeqNum:         // tag34
                msg.setMsgSeqNum( getIntVal() );
                break;
            case FixDictionaryMD50.securityID:         // tag48
                start = _idx;
                valLen = getValLength();
                msg.setSecurityID( _fixMsg, start, valLen );
                break;
            case FixDictionaryMD50.SenderCompID:         // tag49
                getValLength();
                break;
            case FixDictionaryMD50.SendingTime:         // tag52
                msg.setEventTimestamp( getInternalTime() );
                break;
            case FixDictionaryMD50.TargetCompID:         // tag56
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

    private final SuperPool<SymbolRepeatingGrpImpl> _symbolRepeatingGrpPool = SuperpoolManager.instance().getSuperPool( SymbolRepeatingGrpImpl.class );
    private final SymbolRepeatingGrpFactory _symbolRepeatingGrpFactory = new SymbolRepeatingGrpFactory( _symbolRepeatingGrpPool );

    private void processSymbolRepeatingGrps( MDRequestImpl parent, int numEntries ) {

       SymbolRepeatingGrpImpl msg = null;
       SymbolRepeatingGrpImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.Symbol:         // tag55
               msg = _symbolRepeatingGrpFactory.get();
               if ( lastUpdate == null ) {
                   parent.setSymbolGrp( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setSymbol( _fixMsg, start, valLen );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    private final SuperPool<SecMassStatGrpImpl> _secMassStatGrpPool = SuperpoolManager.instance().getSuperPool( SecMassStatGrpImpl.class );
    private final SecMassStatGrpFactory _secMassStatGrpFactory = new SecMassStatGrpFactory( _secMassStatGrpPool );


    @SuppressWarnings( "null" )
    private void processSecMassStatGrps( MassInstrumentStateChangeImpl parent, int numEntries ) {

       SecMassStatGrpImpl msg = null;
       SecMassStatGrpImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.securityIDSource:         // tag22
               msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.securityID:         // tag48
               msg = _secMassStatGrpFactory.get();
               if ( lastUpdate == null ) {
                   parent.setInstState( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setSecurityId( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.securityTradingStatus:         // tag326
               start = _idx;
               valLen = getValLength();
               msg.setSecurityTradingStatus( SecurityTradingStatus.getVal( _fixMsg, start, valLen ) );
               break;
           case FixDictionaryMD50.securityStatus:         // tag965
               msg.setSecurityStatus( _fixMsg[_idx++]=='Y' );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSDFeedTypes( SecurityDefinitionImpl parent, int numEntries ) {

       SDFeedTypeImpl msg = null;
       SDFeedTypeImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.marketDepth:         // tag264
               msg.setMarketDepth( getIntVal() );
               break;
           case FixDictionaryMD50.feedType:         // tag1022
               msg = _sDFeedTypeFactory.get();
               if ( lastUpdate == null ) {
                   parent.setSDFeedTypes( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setFeedType( _fixMsg, start, valLen );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecurityAltIDs( SecurityDefinitionImpl parent, int numEntries ) {

       SecurityAltIDImpl msg = null;
       SecurityAltIDImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.securityAltID:         // tag455
               msg = _securityAltIDFactory.get();
               if ( lastUpdate == null ) {
                   parent.setSecurityAltIDs( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setSecurityAltID( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.securityAltIDSource:         // tag456
               msg.setSecurityAltIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecDefLegs( SecurityDefinitionImpl parent, int numEntries ) {

       SecDefLegImpl msg = null;
       SecDefLegImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.LegPrice:         // tag566
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryMD50.legSymbol:         // tag600
               msg = _secDefLegFactory.get();
               if ( lastUpdate == null ) {
                   parent.setLegs( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setLegSymbol( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legSecurityID:         // tag602
               start = _idx;
               valLen = getValLength();
               msg.setLegSecurityID( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legSecurityIDSource:         // tag603
               msg.setLegSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.legSecurityDesc:         // tag620
               start = _idx;
               valLen = getValLength();
               msg.setLegSecurityDesc( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legRatioQty:         // tag623
               msg.setLegRatioQty( getIntVal() );
               break;
           case FixDictionaryMD50.legSide:         // tag624
               msg.setLegSide( Side.getVal( _fixMsg[_idx++] ) );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecDefEvents( SecurityDefinitionImpl parent, int numEntries ) {

       SecDefEventImpl msg = null;
       SecDefEventImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.eventType:         // tag865
               msg = _secDefEventFactory.get();
               if ( lastUpdate == null ) {
                   parent.setEvents( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               msg.setEventType( SecDefEventType.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.eventDate:         // tag866
               msg.setEventDate( getLongVal() );
               break;
           case FixDictionaryMD50.eventTime:         // tag1145
               msg.setEventTime( getLongVal() );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processMDEntrys( MDIncRefreshImpl parent, int numEntries ) {

       MDEntryImpl msg = null;
       MDEntryImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.securityID:         // tag48
               start = _idx;
               valLen = getValLength();
               msg.setSecurityID( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.repeatSeq:         // tag83
               msg.setRepeatSeq( getIntVal() );
               break;
           case FixDictionaryMD50.mdEntryType:         // tag269
               msg.setMdEntryType( MDEntryType.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.mdEntryPx:         // tag270
               msg.setMdEntryPx( getDoubleVal() );
               break;
           case FixDictionaryMD50.mdEntrySize:         // tag271
               msg.setMdEntrySize( getIntVal() );
               break;
           case FixDictionaryMD50.mdEntryTime:         // tag273
               msg.setMdEntryTime( getIntVal() );
               break;
           case FixDictionaryMD50.mdUpdateAction:         // tag279
               msg = _mDEntryFactory.get();
               if ( lastUpdate == null ) {
                   parent.setMDEntries( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               msg.setMdUpdateAction( MDUpdateAction.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.TradingSessionID:         // tag336
               msg.setTradingSessionID( TradingSessionID.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.numberOfOrders:         // tag346
               msg.setNumberOfOrders( getIntVal() );
               break;
           case FixDictionaryMD50.netChangePrevDay:         // tag451
               getValLength(); // no model attribute, SKIP
               break;
           case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56:  /* SKIP */
           case 57: case 58: case 59: case 60: case 61: case 62: case 63: case 64:  /* SKIP */
           case 65: case 66: case 67: case 68: case 69: case 70: case 71: case 72:  /* SKIP */
           case 73: case 74: case 75: case 76: case 77: case 78: case 79: case 80:  /* SKIP */
           case 81: case 82: case 84: case 85: case 86: case 87: case 88: case 89:  /* SKIP */
           case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:  /* SKIP */
           case 98: case 99: case 100: case 101: case 102: case 103: case 104: case 105:  /* SKIP */
           case 106: case 107: case 108: case 109: case 110: case 111: case 112: case 113:  /* SKIP */
           case 114: case 115: case 116: case 117: case 118: case 119: case 120: case 121:  /* SKIP */
           case 122: case 123: case 124: case 125: case 126: case 127: case 128: case 129:  /* SKIP */
           case 130: case 131: case 132: case 133: case 134: case 135: case 136: case 137:  /* SKIP */
           case 138: case 139: case 140: case 141: case 142: case 143: case 144: case 145:  /* SKIP */
           case 146: case 147: case 148: case 149: case 150: case 151: case 152: case 153:  /* SKIP */
           case 154: case 155: case 156: case 157: case 158: case 159: case 160: case 161:  /* SKIP */
           case 162: case 163: case 164: case 165: case 166: case 167: case 168: case 169:  /* SKIP */
           case 170: case 171: case 172: case 173: case 174: case 175: case 176: case 177:  /* SKIP */
           case 178: case 179: case 180: case 181: case 182: case 183: case 184: case 185:  /* SKIP */
           case 186: case 187: case 188: case 189: case 190: case 191: case 192: case 193:  /* SKIP */
           case 194: case 195: case 196: case 197: case 198: case 199: case 200: case 201:  /* SKIP */
           case 202: case 203: case 204: case 205: case 206: case 207: case 208: case 209:  /* SKIP */
           case 210: case 211: case 212: case 213: case 214: case 215: case 216: case 217:  /* SKIP */
           case 218: case 219: case 220: case 221: case 222: case 223: case 224: case 225:  /* SKIP */
           case 226: case 227: case 228: case 229: case 230: case 231: case 232: case 233:  /* SKIP */
           case 234: case 235: case 236: case 237: case 238: case 239: case 240: case 241:  /* SKIP */
           case 242: case 243: case 244: case 245: case 246: case 247: case 248: case 249:  /* SKIP */
           case 250: case 251: case 252: case 253: case 254: case 255: case 256: case 257:  /* SKIP */
           case 258: case 259: case 260: case 261: case 262: case 263: case 264: case 265:  /* SKIP */
           case 266: case 267: case 268: case 272: case 274: case 275: case 276: case 277:  /* SKIP */
           case 278: case 280: case 281: case 282: case 283: case 284: case 285: case 286:  /* SKIP */
           case 287: case 288: case 289: case 290: case 291: case 292: case 293: case 294:  /* SKIP */
           case 295: case 296: case 297: case 298: case 299: case 300: case 301: case 302:  /* SKIP */
           case 303: case 304: case 305: case 306: case 307: case 308: case 309: case 310:  /* SKIP */
           case 311: case 312: case 313: case 314: case 315: case 316: case 317: case 318:  /* SKIP */
           case 319: case 320: case 321: case 322: case 323: case 324: case 325: case 326:  /* SKIP */
           case 327: case 328: case 329: case 330: case 331: case 332: case 333: case 334:  /* SKIP */
           case 335: case 337: case 338: case 339: case 340: case 341: case 342: case 343:  /* SKIP */
           case 344: case 345: case 347: case 348: case 349: case 350: case 351: case 352:  /* SKIP */
           case 353: case 354: case 355: case 356: case 357: case 358: case 359: case 360:  /* SKIP */
           case 361: case 362: case 363: case 364: case 365: case 366: case 367: case 368:  /* SKIP */
           case 369: case 370: case 371: case 372: case 373: case 374: case 375: case 376:  /* SKIP */
           case 377: case 378: case 379: case 380: case 381: case 382: case 383: case 384:  /* SKIP */
           case 385: case 386: case 387: case 388: case 389: case 390: case 391: case 392:  /* SKIP */
           case 393: case 394: case 395: case 396: case 397: case 398: case 399: case 400:  /* SKIP */
           case 401: case 402: case 403: case 404: case 405: case 406: case 407: case 408:  /* SKIP */
           case 409: case 410: case 411: case 412: case 413: case 414: case 415: case 416:  /* SKIP */
           case 417: case 418: case 419: case 420: case 421: case 422: case 423: case 424:  /* SKIP */
           case 425: case 426: case 427: case 428: case 429: case 430: case 431: case 432:  /* SKIP */
           case 433: case 434: case 435: case 436: case 437: case 438: case 439: case 440:  /* SKIP */
           case 441: case 442: case 443: case 444: case 445: case 446: case 447: case 448:  /* SKIP */
           case 449: case 450:                return;
           default:
               switch( _tag ) {
               case FixDictionaryMD50.securityIDSource:         // tag22
                   msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
                   break;
               case FixDictionaryMD50.tradeVolume:         // tag1020
                   getValLength(); // no model attribute, SKIP
                   break;
               case FixDictionaryMD50.mdPriceLevel:         // tag1023
                   msg.setMdPriceLevel( getIntVal() );
                   break;
               case FixDictionaryMD50.aggressorSide:         // tag5797
                   getValLength(); // no model attribute, SKIP
                   break;
               case FixDictionaryMD50.MatchEventIndicator:         // tag5799
                   getValLength(); // no model attribute, SKIP
                   break;
               default:
                   return;
               }
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processMDSnapEntrys( MDSnapshotFullRefreshImpl parent, int numEntries ) {

       MDSnapEntryImpl msg = null;
       MDSnapEntryImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.mdEntryType:         // tag269
               msg = _mDSnapEntryFactory.get();
               if ( lastUpdate == null ) {
                   parent.setMDEntries( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               msg.setMdEntryType( MDEntryType.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.mdEntryPx:         // tag270
               msg.setMdEntryPx( getDoubleVal() );
               break;
           case FixDictionaryMD50.mdEntrySize:         // tag271
               msg.setMdEntrySize( getIntVal() );
               break;
           case FixDictionaryMD50.mdEntryTime:         // tag273
               msg.setMdEntryTime( getIntVal() );
               break;
           case FixDictionaryMD50.tickDirection:         // tag274
               msg.setTickDirection( TickDirection.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.numberOfOrders:         // tag346
               getValLength(); // no model attribute, SKIP
               break;
           case 272: case 275: case 276: case 277: case 278: case 279: case 280: case 281:  /* SKIP */
           case 282: case 283: case 284: case 285: case 286: case 287: case 288: case 289:  /* SKIP */
           case 290: case 291: case 292: case 293: case 294: case 295: case 296: case 297:  /* SKIP */
           case 298: case 299: case 300: case 301: case 302: case 303: case 304: case 305:  /* SKIP */
           case 306: case 307: case 308: case 309: case 310: case 311: case 312: case 313:  /* SKIP */
           case 314: case 315: case 316: case 317: case 318: case 319: case 320: case 321:  /* SKIP */
           case 322: case 323: case 324: case 325: case 326: case 327: case 328: case 329:  /* SKIP */
           case 330: case 331: case 332: case 333: case 334: case 335: case 336: case 337:  /* SKIP */
           case 338: case 339: case 340: case 341: case 342: case 343: case 344: case 345:  /* SKIP */
                          return;
           default:
               switch( _tag ) {
               case FixDictionaryMD50.tradeVolume:         // tag1020
                   msg.setTradeVolume( getIntVal() );
                   break;
               case FixDictionaryMD50.mdPriceLevel:         // tag1023
                   msg.setMdPriceLevel( getIntVal() );
                   break;
               default:
                   return;
               }
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSDFeedTypes( SecurityDefinitionUpdateImpl parent, int numEntries ) {

       SDFeedTypeImpl msg = null;
       SDFeedTypeImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.marketDepth:         // tag264
               msg.setMarketDepth( getIntVal() );
               break;
           case FixDictionaryMD50.feedType:         // tag1022
               msg = _sDFeedTypeFactory.get();
               if ( lastUpdate == null ) {
                   parent.setSDFeedTypes( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setFeedType( _fixMsg, start, valLen );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecurityAltIDs( SecurityDefinitionUpdateImpl parent, int numEntries ) {

       SecurityAltIDImpl msg = null;
       SecurityAltIDImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.securityAltID:         // tag455
               msg = _securityAltIDFactory.get();
               if ( lastUpdate == null ) {
                   parent.setSecurityAltIDs( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setSecurityAltID( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.securityAltIDSource:         // tag456
               msg.setSecurityAltIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecDefLegs( SecurityDefinitionUpdateImpl parent, int numEntries ) {

       SecDefLegImpl msg = null;
       SecDefLegImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.LegPrice:         // tag566
               getValLength(); // no model attribute, SKIP
               break;
           case FixDictionaryMD50.legSymbol:         // tag600
               msg = _secDefLegFactory.get();
               if ( lastUpdate == null ) {
                   parent.setLegs( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               start = _idx;
               valLen = getValLength();
               msg.setLegSymbol( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legSecurityID:         // tag602
               start = _idx;
               valLen = getValLength();
               msg.setLegSecurityID( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legSecurityIDSource:         // tag603
               msg.setLegSecurityIDSource( SecurityIDSource.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.legSecurityDesc:         // tag620
               start = _idx;
               valLen = getValLength();
               msg.setLegSecurityDesc( _fixMsg, start, valLen );
               break;
           case FixDictionaryMD50.legRatioQty:         // tag623
               msg.setLegRatioQty( getIntVal() );
               break;
           case FixDictionaryMD50.legSide:         // tag624
               msg.setLegSide( Side.getVal( _fixMsg[_idx++] ) );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

    @SuppressWarnings( "null" )
    private void processSecDefEvents( SecurityDefinitionUpdateImpl parent, int numEntries ) {

       SecDefEventImpl msg = null;
       SecDefEventImpl lastUpdate = null;
       _tag = getTag();

       int start;
       int valLen;

       while( _tag != 0 ) {
           switch( _tag ) {
           case FixDictionaryMD50.eventType:         // tag865
               msg = _secDefEventFactory.get();
               if ( lastUpdate == null ) {
                   parent.setEvents( msg );
               } else {
                   lastUpdate.setNext( msg );
               }
               lastUpdate = msg;
               msg.setEventType( SecDefEventType.getVal( _fixMsg[_idx++] ) );
               break;
           case FixDictionaryMD50.eventDate:         // tag866
               msg.setEventDate( getLongVal() );
               break;
           case FixDictionaryMD50.eventTime:         // tag1145
               msg.setEventTime( getLongVal() );
               break;
           default:
               return;
           }
           _idx++; // past delimiter
           _tag = getTag();
       }

    }

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

    private ExchangeCode _defaultExchange = ExchangeCode.UNKNOWN;
    public void setDefaultExchange( ExchangeCode ex ) { _defaultExchange = ex ; }
    public ExchangeCode getDefaultExchange() {
        return _defaultExchange;
    }
}
