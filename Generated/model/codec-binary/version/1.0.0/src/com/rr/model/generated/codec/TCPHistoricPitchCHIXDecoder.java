package com.rr.model.generated.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.book.*;
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

public final class TCPHistoricPitchCHIXDecoder extends AbstractBinaryDecoder {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs
    private static final byte      MSG_PitchBookAddOrder = (byte)'A';
    private static final byte      MSG_PitchBookAddOrderMid = (byte)'a';
    private static final byte      MSG_PitchBookAddOrderLong = (byte)'c';
    private static final byte      MSG_PitchBookAddOrderExpanded = (byte)'t';
    private static final byte      MSG_PitchBookOrderExecuted = (byte)'E';
    private static final byte      MSG_PitchBookOrderExecutedLong = (byte)'e';
    private static final byte      MSG_PitchBookCancelOrder = (byte)'X';
    private static final byte      MSG_PitchBookCancelOrderLong = (byte)'x';
    private static final byte      MSG_PitchSymbolClear = (byte)'s';
    private static final byte      MSG_PitchSymbolClearMicros = (byte)'h';
    private static final byte      MSG_PitchBookAddOrderMicros = (byte)'K';
    private static final byte      MSG_PitchBookAddOrderLongMicros = (byte)'M';
    private static final byte      MSG_PitchBookOrderExecutedMicros = (byte)'N';
    private static final byte      MSG_PitchBookOrderExecutedLongMicros = (byte)'g';
    private static final byte      MSG_PitchBookCancelOrderMicros = (byte)'F';
    private static final byte      MSG_PitchBookCancelOrderLongMicros = (byte)'G';

    private boolean _debug = false;

    private BinaryDecodeBuilder _builder;

    private       byte _msgType;
    private final byte                        _protocolVersion;
    private final String                      _id;
    private       int                         _msgStatedLen;
    private final ReusableString _dump  = new ReusableString(256);
    private final ReusableString _missedMsgTypes = new ReusableString();

    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks
    private       long                        _orderId;
    private       byte                        _side;
    private       int                         _qty6;
    private       ReusableString              _symbol6 = new ReusableString(30);
    private       double                      _price10;
    private       int                         _qty10;
    private       double                      _price19;
    private       ReusableString              _symbol8 = new ReusableString(30);
    private       byte                        _typeIndic;
    private       ReusableString              _participant = new ReusableString(30);
    private       int                         _lastQty6;
    private       long                        _execId;
    private       int                         _lastQty10;
    private       int                         _cancelQty6;
    private       int                         _cancelQty10;
    private       byte                        _mktMech;
    private       byte                        _tradingMode;
    private       byte                        _dividend;
    private       byte                        _algoTrade;

   // Pools

    private final SuperPool<PitchBookAddOrderImpl> _pitchBookAddOrderPool = SuperpoolManager.instance().getSuperPool( PitchBookAddOrderImpl.class );
    private final PitchBookAddOrderFactory _pitchBookAddOrderFactory = new PitchBookAddOrderFactory( _pitchBookAddOrderPool );

    private final SuperPool<PitchBookOrderExecutedImpl> _pitchBookOrderExecutedPool = SuperpoolManager.instance().getSuperPool( PitchBookOrderExecutedImpl.class );
    private final PitchBookOrderExecutedFactory _pitchBookOrderExecutedFactory = new PitchBookOrderExecutedFactory( _pitchBookOrderExecutedPool );

    private final SuperPool<PitchBookCancelOrderImpl> _pitchBookCancelOrderPool = SuperpoolManager.instance().getSuperPool( PitchBookCancelOrderImpl.class );
    private final PitchBookCancelOrderFactory _pitchBookCancelOrderFactory = new PitchBookCancelOrderFactory( _pitchBookCancelOrderPool );

    private final SuperPool<PitchSymbolClearImpl> _pitchSymbolClearPool = SuperpoolManager.instance().getSuperPool( PitchSymbolClearImpl.class );
    private final PitchSymbolClearFactory _pitchSymbolClearFactory = new PitchSymbolClearFactory( _pitchSymbolClearPool );


   // Constructors
    public TCPHistoricPitchCHIXDecoder() { this( null ); }
    public TCPHistoricPitchCHIXDecoder( String id ) {
        super();
        setBuilder();
        _id = id;
        _protocolVersion = (byte)'1';
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
        _builder = (_debug) ? new DebugBinaryDecodeBuilder<>( _dump, new com.rr.codec.emea.exchange.pitch.TCPPitchDecodeBuilderImpl() )
                            : new com.rr.codec.emea.exchange.pitch.TCPPitchDecodeBuilderImpl();
    }

    @Override
    protected final Event doMessageDecode() {
        _builder.setMaxIdx( _maxIdx );

        switch( _msgType ) {
        case MSG_PitchBookAddOrder:
            return decodePitchBookAddOrder();
        case MSG_PitchBookAddOrderMid:
            return decodePitchBookAddOrderMid();
        case MSG_PitchBookAddOrderLong:
            return decodePitchBookAddOrderLong();
        case MSG_PitchBookAddOrderExpanded:
            return decodePitchBookAddOrderExpanded();
        case MSG_PitchBookOrderExecuted:
            return decodePitchBookOrderExecuted();
        case MSG_PitchBookOrderExecutedLong:
            return decodePitchBookOrderExecutedLong();
        case MSG_PitchBookCancelOrder:
            return decodePitchBookCancelOrder();
        case MSG_PitchBookCancelOrderLong:
            return decodePitchBookCancelOrderLong();
        case MSG_PitchSymbolClear:
            return decodePitchSymbolClear();
        case MSG_PitchSymbolClearMicros:
            return decodePitchSymbolClearMicros();
        case MSG_PitchBookAddOrderMicros:
            return decodePitchBookAddOrderMicros();
        case MSG_PitchBookAddOrderLongMicros:
            return decodePitchBookAddOrderLongMicros();
        case MSG_PitchBookOrderExecutedMicros:
            return decodePitchBookOrderExecutedMicros();
        case MSG_PitchBookOrderExecutedLongMicros:
            return decodePitchBookOrderExecutedLongMicros();
        case MSG_PitchBookCancelOrderMicros:
            return decodePitchBookCancelOrderMicros();
        case MSG_PitchBookCancelOrderLongMicros:
            return decodePitchBookCancelOrderLongMicros();
        case 'B':
        case 'C':
        case 'D':
        case 'H':
        case 'I':
        case 'J':
        case 'L':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'Y':
        case 'Z':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '_':
        case '`':
        case 'b':
        case 'd':
        case 'f':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 'u':
        case 'v':
        case 'w':
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

    private Event decodePitchBookAddOrder() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrder" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty6" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 6 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol6" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 6 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price10" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 6, 4 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookAddOrderMid() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrderMid" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty10" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 10 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol6" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 6 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price19" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 12, 7 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookAddOrderLong() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrderLong" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty10" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 10 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price19" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 12, 7 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookAddOrderExpanded() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrderExpanded" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty10" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 10 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price19" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 12, 7 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "typeIndic" ).append( " : " );
        msg.setTypeIndic( PitchOrderTypeIndicator.getVal( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "participant" ).append( " : " );
        _builder.decodeZStringFixedWidth( _participant, 4 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookOrderExecuted() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookOrderExecuted" ).append( " : " );
        }

        final PitchBookOrderExecutedImpl msg = _pitchBookOrderExecutedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty6" ).append( " : " );
        msg.setLastQty( _builder.decodeQty( 6 ) );
        procExecId( msg );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookOrderExecutedLong() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookOrderExecutedLong" ).append( " : " );
        }

        final PitchBookOrderExecutedImpl msg = _pitchBookOrderExecutedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty10" ).append( " : " );
        msg.setLastQty( _builder.decodeQty( 10 ) );
        procExecId( msg );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookCancelOrder() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookCancelOrder" ).append( " : " );
        }

        final PitchBookCancelOrderImpl msg = _pitchBookCancelOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty6" ).append( " : " );
        msg.setCancelQty( _builder.decodeQty( 6 ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookCancelOrderLong() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookCancelOrderLong" ).append( " : " );
        }

        final PitchBookCancelOrderImpl msg = _pitchBookCancelOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty10" ).append( " : " );
        msg.setCancelQty( _builder.decodeQty( 10 ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchSymbolClear() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchSymbolClear" ).append( " : " );
        }

        final PitchSymbolClearImpl msg = _pitchSymbolClearFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 8 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchSymbolClearMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchSymbolClearMicros" ).append( " : " );
        }

        final PitchSymbolClearImpl msg = _pitchSymbolClearFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 8 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookAddOrderMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrderMicros" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty6" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 6 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol6" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 6 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price10" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 6, 4 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookAddOrderLongMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookAddOrderLongMicros" ).append( " : " );
        }

        final PitchBookAddOrderImpl msg = _pitchBookAddOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        msg.setSide( transformSide( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "qty10" ).append( " : " );
        msg.setOrderQty( _builder.decodeQty( 10 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.decodeZStringFixedWidth( msg.getSecurityIdForUpdate(), 8 );

        if ( _debug ) _dump.append( "\nField: " ).append( "price19" ).append( " : " );
        msg.setPrice( _builder.decodePrice( 12, 7 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.skip( 1 );
        msg.setSecurityIdSrc( _securityIdSrc );
        msg.setSecurityExchange( _securityExchange );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookOrderExecutedMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookOrderExecutedMicros" ).append( " : " );
        }

        final PitchBookOrderExecutedImpl msg = _pitchBookOrderExecutedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty6" ).append( " : " );
        msg.setLastQty( _builder.decodeQty( 6 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        msg.setExecId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "mktMech" ).append( " : " );
        msg.setMktMech( MMTMarketMechanism.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "tradingMode" ).append( " : " );
        msg.setTradingMode( MMTTradingMode.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "dividend" ).append( " : " );
        msg.setDividend( MMTDividend.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "algoTrade" ).append( " : " );
        msg.setAlgoTrade( MMTAlgorithmicTrade.getVal( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookOrderExecutedLongMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookOrderExecutedLongMicros" ).append( " : " );
        }

        final PitchBookOrderExecutedImpl msg = _pitchBookOrderExecutedFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty10" ).append( " : " );
        msg.setLastQty( _builder.decodeQty( 10 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        msg.setExecId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "mktMech" ).append( " : " );
        msg.setMktMech( MMTMarketMechanism.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "tradingMode" ).append( " : " );
        msg.setTradingMode( MMTTradingMode.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "dividend" ).append( " : " );
        msg.setDividend( MMTDividend.getVal( _builder.decodeByte() ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "algoTrade" ).append( " : " );
        msg.setAlgoTrade( MMTAlgorithmicTrade.getVal( _builder.decodeByte() ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookCancelOrderMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookCancelOrderMicros" ).append( " : " );
        }

        final PitchBookCancelOrderImpl msg = _pitchBookCancelOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty6" ).append( " : " );
        msg.setCancelQty( _builder.decodeQty( 6 ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }

    private Event decodePitchBookCancelOrderLongMicros() {
        if ( _debug ) {
            _dump.append( "\nKnown Message : " ).append( "PitchBookCancelOrderLongMicros" ).append( " : " );
        }

        final PitchBookCancelOrderImpl msg = _pitchBookCancelOrderFactory.get();

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        msg.setOrderId( _builder.decodeBase36Number( 12 ) );

        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty10" ).append( " : " );
        msg.setCancelQty( _builder.decodeQty( 10 ) );
        if ( _debug ) _dump.append( "\nHook : " ).append( "postdecode" ).append( " : " );
        msg.setEventTimestamp( genTimestamp( _timestampMS ) );
        _builder.end();
        return msg;
    }


    @Override public String getComponentId() { return _id; }

   // transform methods
    private static final Side[] _sideMap = new Side[19];
    private static final int    _sideIndexOffset = 'B';
    static {
        for ( int i=0 ; i < _sideMap.length ; i++ ) {
             _sideMap[i] = null;
        }
         _sideMap[ (byte)'B' - _sideIndexOffset ] = Side.Buy;
         _sideMap[ (byte)'S' - _sideIndexOffset ] = Side.Sell;
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

    private long _timestampMS;
    private ExchangeCode _securityExchange = ExchangeCode.UNKNOWN;
    private SecurityIDSource _securityIdSrc = SecurityIDSource.ExchangeSymbol;

    public void setSecurityIdSrc( final SecurityIDSource securityIdSrc ) {
        _securityIdSrc = securityIdSrc;
    }

    public void setSecurityExchange( final ExchangeCode securityExchange ) {
        _securityExchange = securityExchange;
    }

    @Override
    public final int parseHeader( final byte[] msg, final int offset, final int bytesRead ) {

        if ( _debug ) _dump.reset();

        _binaryMsg = msg;
        _maxIdx = bytesRead + offset; // temp assign maxIdx to last data bytes in bufferMap
        _offset = offset;
        _builder.start( msg, offset, _maxIdx );

        if ( bytesRead < 10 ) {
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

        if ( Character.isDigit( msg[offset+8] ) ) {
            _timestampMS = _builder.decodeTimestampLocal() / 1000;

            _msgType = _builder.decodeByte();

            if ( _msgType == 'a' ) _msgType = 'H';

        } else{
            _timestampMS = _builder.decodeTimeLocal();

            _msgType = _builder.decodeByte();
        }

        return bytesRead;
    }

    private long genTimestamp( final long microseconds ) {
        return _tzCalculator.localMSFromMidnightToInternalTimeToday( _timestampMS );
    }

    private void procExecId( PitchBookOrderExecutedImpl msg ) {
        byte execIdTye = _builder.getBuffer()[ _builder.getCurrentIndex() + 3 ];

        msg.setExecId( _builder.decodeBase36Number( 12 ) );

        if ( _builder.getCurrentIndex() < _builder.getMaxIdx() ) {
            if ( _debug ) _dump.append( "\nField: " ).append( "mktMech" ).append( " : " );
            msg.setMktMech( MMTMarketMechanism.getVal( _builder.decodeByte() ) );

            if ( _builder.getCurrentIndex() < _builder.getMaxIdx() ) {
                if ( _debug ) _dump.append( "\nField: " ).append( "tradingMode" ).append( " : " );
                msg.setTradingMode( MMTTradingMode.getVal( _builder.decodeByte() ) );
            } else {
                msg.setTradingMode( MMTTradingMode.Unknown );
            }
        } else {
            if ( execIdTye == '0' || execIdTye == '1' ) {
                msg.setMktMech( MMTMarketMechanism.CentralLimitOrderBook );
            } else {
                msg.setMktMech( MMTMarketMechanism.DarkOrderBook );
            }
            msg.setTradingMode( MMTTradingMode.Unknown );
        }
    }

}
