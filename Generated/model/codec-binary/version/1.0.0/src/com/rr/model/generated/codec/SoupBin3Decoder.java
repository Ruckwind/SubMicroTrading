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
import com.rr.core.codec.SoupBinDecoder;

@SuppressWarnings( "unused" )

public final class SoupBin3Decoder extends AbstractBinaryDecoder implements com.rr.core.codec.SoupBinDecoder {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs
    private static final byte      MSG_SequencedDataPacket = (byte)'S';
    private static final byte      MSG_UnsequencedDataPacket = (byte)'U';
    private static final byte      MSG_DebugPacket = (byte)'+';
    private static final byte      MSG_LogInAccepted = (byte)'A';
    private static final byte      MSG_LogInRejected = (byte)'J';
    private static final byte      MSG_LogInRequest = (byte)'L';
    private static final byte      MSG_ClientHeartbeat = (byte)'R';
    private static final byte      MSG_ServerHeartbeat = (byte)'H';
    private static final byte      MSG_EndOfSession = (byte)'Z';
    private static final byte      MSG_LogoutRequest = (byte)'O';

    private boolean _debug = false;

    private BinaryDecodeBuilder _builder;

    private       byte _msgType;
    private final byte                        _protocolVersion;
    private final String                      _id;
    private       int                         _msgStatedLen;
    private final ReusableString _dump  = new ReusableString(256);
    private final ReusableString _missedMsgTypes = new ReusableString();

    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks
    private       ReusableString              _text = new ReusableString(30);
    private       ReusableString              _sessionId = new ReusableString(30);
    private       ReusableString              _soupSeqNum = new ReusableString(30);
    private       byte                        _rejectReasonCode;
    private       ReusableString              _userName = new ReusableString(30);
    private       ReusableString              _password = new ReusableString(30);
    private       ReusableString              _requestedSession = new ReusableString(30);
    private       int                         _requestedSeqNum;

   // Pools

    private final SuperPool<SoupDebugPacketImpl> _soupDebugPacketPool = SuperpoolManager.instance().getSuperPool( SoupDebugPacketImpl.class );
    private final SoupDebugPacketFactory _soupDebugPacketFactory = new SoupDebugPacketFactory( _soupDebugPacketPool );

    private final SuperPool<SoupLogInAcceptedImpl> _soupLogInAcceptedPool = SuperpoolManager.instance().getSuperPool( SoupLogInAcceptedImpl.class );
    private final SoupLogInAcceptedFactory _soupLogInAcceptedFactory = new SoupLogInAcceptedFactory( _soupLogInAcceptedPool );

    private final SuperPool<SoupLogInRejectedImpl> _soupLogInRejectedPool = SuperpoolManager.instance().getSuperPool( SoupLogInRejectedImpl.class );
    private final SoupLogInRejectedFactory _soupLogInRejectedFactory = new SoupLogInRejectedFactory( _soupLogInRejectedPool );

    private final SuperPool<SoupLogInRequestImpl> _soupLogInRequestPool = SuperpoolManager.instance().getSuperPool( SoupLogInRequestImpl.class );
    private final SoupLogInRequestFactory _soupLogInRequestFactory = new SoupLogInRequestFactory( _soupLogInRequestPool );

    private final SuperPool<HeartbeatImpl> _heartbeatPool = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
    private final HeartbeatFactory _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

    private final SuperPool<EndOfSessionImpl> _endOfSessionPool = SuperpoolManager.instance().getSuperPool( EndOfSessionImpl.class );
    private final EndOfSessionFactory _endOfSessionFactory = new EndOfSessionFactory( _endOfSessionPool );

    private final SuperPool<LogoutRequestImpl> _logoutRequestPool = SuperpoolManager.instance().getSuperPool( LogoutRequestImpl.class );
    private final LogoutRequestFactory _logoutRequestFactory = new LogoutRequestFactory( _logoutRequestPool );


   // Constructors
    public SoupBin3Decoder() { this( null ); }
    public SoupBin3Decoder( String id ) {
        super();
        setBuilder();
        _id = id;
        _protocolVersion = (byte)'3';
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
        _builder = (_debug) ? new DebugBinaryDecodeBuilder<>( _dump, new com.rr.codec.emea.exchange.soupbin.SoupBin3DecodeBuilderImpl() )
                            : new com.rr.codec.emea.exchange.soupbin.SoupBin3DecodeBuilderImpl();
    }

    @Override
    protected final Event doMessageDecode() {
        _builder.setMaxIdx( _maxIdx );

        switch( _msgType ) {
        case MSG_SequencedDataPacket:
            return decodeSequencedDataPacket();
        case MSG_UnsequencedDataPacket:
            return decodeUnsequencedDataPacket();
        case MSG_DebugPacket:
            return decodeDebugPacket();
        case MSG_LogInAccepted:
            return decodeLogInAccepted();
        case MSG_LogInRejected:
            return decodeLogInRejected();
        case MSG_LogInRequest:
            return decodeLogInRequest();
        case MSG_ClientHeartbeat:
            return decodeClientHeartbeat();
        case MSG_ServerHeartbeat:
            return decodeServerHeartbeat();
        case MSG_EndOfSession:
            return decodeEndOfSession();
        case MSG_LogoutRequest:
            return decodeLogoutRequest();
        case ',':
        case '-':
        case '.':
        case '/':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
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
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'I':
        case 'K':
        case 'M':
        case 'N':
        case 'P':
        case 'Q':
        case 'T':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
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

    private Event decodeSequencedDataPacket() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "SequencedDataPacket" ).append( " : " );
        }

        Event msg = null;
        if ( _debug ) _dump.append( "\nHook : " ).append( "decode" ).append( " : " );
        msg = doDecodeSequencedDataPacket();
        _builder.end();
        return msg;
    }

    private Event decodeUnsequencedDataPacket() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "UnsequencedDataPacket" ).append( " : " );
        }

        Event msg = null;
        if ( _debug ) _dump.append( "\nHook : " ).append( "decode" ).append( " : " );
        msg = doDecodeUnsequencedDataPacket();
        _builder.end();
        return msg;
    }

    private Event decodeDebugPacket() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "DebugPacket" ).append( " : " );
        }

        final SoupDebugPacketImpl msg = _soupDebugPacketFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "text" ).append( " : " );
        _builder.decodeString( msg.getTextForUpdate() );
        _builder.end();
        return msg;
    }

    private Event decodeLogInAccepted() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogInAccepted" ).append( " : " );
        }

        final SoupLogInAcceptedImpl msg = _soupLogInAcceptedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "sessionId" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSessionIdForUpdate(), 10 );
        if ( _debug ) _dump.append( "\nField: " ).append( "soupSeqNum" ).append( " : " );
        _builder.decodeZStringFixedWidth( _soupSeqNum, 20 );
        _builder.end();
        return msg;
    }

    private Event decodeLogInRejected() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogInRejected" ).append( " : " );
        }

        final SoupLogInRejectedImpl msg = _soupLogInRejectedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectReasonCode" ).append( " : " );
        msg.setRejectReasonCode( SoupRejectCode.getVal( _builder.decodeByte() ) );
        _builder.end();
        return msg;
    }

    private Event decodeLogInRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogInRequest" ).append( " : " );
        }

        final SoupLogInRequestImpl msg = _soupLogInRequestFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "userName" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getUserNameForUpdate(), 6 );

        if ( _debug ) _dump.append( "\nField: " ).append( "password" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getPasswordForUpdate(), 10 );

        if ( _debug ) _dump.append( "\nField: " ).append( "requestedSession" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getRequestedSessionForUpdate(), 10 );

        if ( _debug ) _dump.append( "\nField: " ).append( "requestedSeqNum" ).append( " : " );
        msg.setRequestedSeqNum( _builder.decodeUInt() );
        _builder.end();
        return msg;
    }

    private Event decodeClientHeartbeat() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "ClientHeartbeat" ).append( " : " );
        }

        final HeartbeatImpl msg = _heartbeatFactory.get();
        _builder.end();
        return msg;
    }

    private Event decodeServerHeartbeat() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "ServerHeartbeat" ).append( " : " );
        }

        final HeartbeatImpl msg = _heartbeatFactory.get();
        _builder.end();
        return msg;
    }

    private Event decodeEndOfSession() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "EndOfSession" ).append( " : " );
        }

        final EndOfSessionImpl msg = _endOfSessionFactory.get();
        _builder.end();
        return msg;
    }

    private Event decodeLogoutRequest() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "LogoutRequest" ).append( " : " );
        }

        final LogoutRequestImpl msg = _logoutRequestFactory.get();
        _builder.end();
        return msg;
    }


    @Override public String getComponentId() { return _id; }

   // transform methods

    private Decoder _dataDecoder;

    @Override
    public final int parseHeader( final byte[] msg, final int offset, final int bytesRead ) {

        _binaryMsg = msg;
        _maxIdx = bytesRead + offset; // temp assign maxIdx to last data bytes in bufferMap
        _offset = offset;
        _builder.start( msg, offset, _maxIdx );

        if ( bytesRead < 2 ) {
            ReusableString copy = TLC.instance().getString();
            if ( bytesRead == 0 )  {
                copy.setValue( "{empty}" );
            } else{
                copy.setValue( msg, offset, bytesRead );
            }
            throw new RuntimeDecodingException( "SOAP Messsage too small, len=" + bytesRead, copy );
        }

        _msgStatedLen = _builder.decodeUShort() + 2;

        _maxIdx = _msgStatedLen + _offset;  // correctly assign maxIdx as last bytes of current message

        if ( _maxIdx > _binaryMsg.length )  _maxIdx  = _binaryMsg.length;

        _msgType = _builder.decodeByte();

        return bytesRead;
    }

    private Event doDecodeSequencedDataPacket() {

        int offset = _builder.getCurrentIndex() + 1;
        int bytesRead = _maxIdx - offset;
        int maxIdx = _maxIdx - 1;

        _dataDecoder.parseHeader( _binaryMsg, offset, bytesRead - 1 );

        return _dataDecoder.postHeaderDecode();
    }

    private Event doDecodeUnsequencedDataPacket() {

        int offset = _builder.getCurrentIndex() + 1;
        int bytesRead = _maxIdx - offset;
        int maxIdx = _maxIdx - 1;

        _dataDecoder.parseHeader( _binaryMsg, offset, bytesRead - 1 );

        return _dataDecoder.postHeaderDecode();
    }

    @Override public SoupBinDecoder newInstance() {
        Decoder clonedDecoder = ReflectUtils.shallowClone( _dataDecoder );

        SoupBin3Decoder e = new SoupBin3Decoder( getComponentId() );
        e.setDataDecoder( clonedDecoder );

        return e;
    }

    private void setDataDecoder( final Decoder decoder ) { _dataDecoder = decoder; }
}
