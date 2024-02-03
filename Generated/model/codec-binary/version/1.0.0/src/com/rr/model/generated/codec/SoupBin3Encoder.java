package com.rr.model.generated.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import java.util.HashMap;
import java.util.Map;
import com.rr.core.lang.*;
import com.rr.core.utils.*;
import com.rr.core.model.*;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.pool.SuperPool;
import com.rr.core.codec.BinaryEncoder;
import com.rr.codec.emea.exchange.soupbin.SoupBin3EncodeBuilderImpl;
import com.rr.core.codec.binary.BinaryEncodeBuilder;
import com.rr.core.codec.binary.DebugBinaryEncodeBuilder;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.core.FullEventIds;
import com.rr.core.codec.SoupBinEncoder;

@SuppressWarnings( {"unused", "cast"} )

public final class SoupBin3Encoder implements com.rr.core.codec.SoupBinEncoder {

   // Member Vars
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


    private final byte[]                  _buf;
    private final String                  _id;
    private final int                     _offset;
    private final ZString                 _binaryVersion;

    private BinaryEncodeBuilder     _builder;

    private       TimeUtils               _tzCalculator = TimeUtilsFactory.createTimeUtils();
    private       SingleByteLookup        _sv;
    private       TwoByteLookup           _tv;
    private       MultiByteLookup         _mv;
    private final ReusableString          _dump  = new ReusableString(256);

    private boolean                 _debug = false;

   // Constructors
    public SoupBin3Encoder( byte[] buf, int offset ) { this( null, buf, offset ); }

    public SoupBin3Encoder( String id, byte[] buf, int offset ) {
        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {
            throw new RuntimeException( "Encode buffer too small only " + buf.length + ", min=" + SizeType.MIN_ENCODE_BUFFER.getSize() );
        }
        _id = id;
        _buf = buf;
        _offset = offset;
        _binaryVersion   = new ViewString( "3");
        setBuilder();
    }


   // encode methods

    @Override
    public final void encode( final Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_SOUPDEBUGPACKET:
            encodeSoupDebugPacket( (SoupDebugPacket) msg );
            break;
        case EventIds.ID_SOUPLOGINACCEPTED:
            encodeSoupLogInAccepted( (SoupLogInAccepted) msg );
            break;
        case EventIds.ID_SOUPLOGINREJECTED:
            encodeSoupLogInRejected( (SoupLogInRejected) msg );
            break;
        case EventIds.ID_SOUPLOGINREQUEST:
            encodeSoupLogInRequest( (SoupLogInRequest) msg );
            break;
        case EventIds.ID_HEARTBEAT:
            encodeClientHeartbeat( (Heartbeat) msg );
            break;
        case EventIds.ID_ENDOFSESSION:
            encodeEndOfSession( (EndOfSession) msg );
            break;
        case EventIds.ID_LOGOUTREQUEST:
            encodeLogoutRequest( (LogoutRequest) msg );
            break;
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
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
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
            _builder.start();
            break;
        default:
            _builder.start();
            break;
        }
    }

    @Override public final int getLength() { return _builder.getLength(); }
    @Override public final int getOffset() { return _builder.getOffset(); }

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
        _builder = (_debug) ? new DebugBinaryEncodeBuilder<>( _dump, new com.rr.codec.emea.exchange.soupbin.SoupBin3EncodeBuilderImpl( _buf, _offset, _binaryVersion ) )
                            : new com.rr.codec.emea.exchange.soupbin.SoupBin3EncodeBuilderImpl( _buf, _offset, _binaryVersion );
    }


    public final void encodeSoupDebugPacket( final SoupDebugPacket msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_DebugPacket );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "DebugPacket" ).append( "  eventType=" ).append( "SoupDebugPacket" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "text" ).append( " : " );
        _builder.encodeString( msg.getText(), 4 );
        _builder.end();
    }

    public final void encodeSoupLogInAccepted( final SoupLogInAccepted msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_LogInAccepted );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "LogInAccepted" ).append( "  eventType=" ).append( "SoupLogInAccepted" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "sessionId" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getSessionId(), 10 );
        if ( _debug ) _dump.append( "\nField: " ).append( "soupSeqNum" ).append( " : " );
        _builder.encodeFiller( 20 );    // soupSeqNum
        _builder.end();
    }

    public final void encodeSoupLogInRejected( final SoupLogInRejected msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_LogInRejected );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "LogInRejected" ).append( "  eventType=" ).append( "SoupLogInRejected" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "rejectReasonCode" ).append( " : " );
        final SoupRejectCode tRejectReasonCode = msg.getRejectReasonCode();
        final byte tRejectReasonCodeBytes = ( tRejectReasonCode != null ) ? tRejectReasonCode.getVal() : 0x00;
        _builder.encodeByte( tRejectReasonCodeBytes );
        _builder.end();
    }

    public final void encodeSoupLogInRequest( final SoupLogInRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_LogInRequest );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "LogInRequest" ).append( "  eventType=" ).append( "SoupLogInRequest" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "userName" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getUserName(), 6 );
        if ( _debug ) _dump.append( "\nField: " ).append( "password" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getPassword(), 10 );
        if ( _debug ) _dump.append( "\nField: " ).append( "requestedSession" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getRequestedSession(), 10 );
        if ( _debug ) _dump.append( "\nField: " ).append( "requestedSeqNum" ).append( " : " );
        _builder.encodeUInt( (int)msg.getRequestedSeqNum() );
        _builder.end();
    }

    public final void encodeClientHeartbeat( final Heartbeat msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_ClientHeartbeat );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "ClientHeartbeat" ).append( "  eventType=" ).append( "Heartbeat" ).append( " : " );
        }

        _builder.end();
    }

    public final void encodeServerHeartbeat( final Heartbeat msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_ServerHeartbeat );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "ServerHeartbeat" ).append( "  eventType=" ).append( "Heartbeat" ).append( " : " );
        }

        _builder.end();
    }

    public final void encodeEndOfSession( final EndOfSession msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_EndOfSession );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "EndOfSession" ).append( "  eventType=" ).append( "EndOfSession" ).append( " : " );
        }

        _builder.end();
    }

    public final void encodeLogoutRequest( final LogoutRequest msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_LogoutRequest );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "LogoutRequest" ).append( "  eventType=" ).append( "LogoutRequest" ).append( " : " );
        }

        _builder.end();
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



    @Override public void addStats( final ReusableString outBuf, final Event msg, final long time ) { /* nothing */ }

    @Override public SoupBinEncoder newInstance() {
        final byte[] buf = new byte[ _builder.getBuffer().length ];
        final int offset = _builder.getOffset();
        SoupBinEncoder e = new SoupBin3Encoder( getComponentId(), buf, offset );
        return e;
    }

}
