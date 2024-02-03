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
import com.rr.codec.emea.exchange.pitch.TCPPitchEncodeBuilderImpl;
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

@SuppressWarnings( {"unused", "cast"} )

public final class TCPPitchCHIXEncoder implements BinaryEncoder {

   // Member Vars
    private static final byte      MSG_PitchBookAddOrderMicros = (byte)'K';
    private static final byte      MSG_PitchBookAddOrderLongMicros = (byte)'M';
    private static final byte      MSG_PitchBookOrderExecutedMicros = (byte)'N';
    private static final byte      MSG_PitchBookOrderExecutedLongMicros = (byte)'g';
    private static final byte      MSG_PitchBookCancelOrderMicros = (byte)'F';
    private static final byte      MSG_PitchBookCancelOrderLongMicros = (byte)'G';
    private static final byte      MSG_PitchSymbolClear = (byte)'h';

    private static final byte      DEFAULT_Side = 0x00;

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
    public TCPPitchCHIXEncoder( byte[] buf, int offset ) { this( null, buf, offset ); }

    public TCPPitchCHIXEncoder( String id, byte[] buf, int offset ) {
        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {
            throw new RuntimeException( "Encode buffer too small only " + buf.length + ", min=" + SizeType.MIN_ENCODE_BUFFER.getSize() );
        }
        _id = id;
        _buf = buf;
        _offset = offset;
        _binaryVersion   = new ViewString( "4.16");
        setBuilder();
    }


   // encode methods

    @Override
    public final void encode( final Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_PITCHBOOKADDORDER:
            encodeBookAddOrderMicros( (PitchBookAddOrder) msg );
            break;
        case EventIds.ID_PITCHBOOKORDEREXECUTED:
            encodeBookOrderExecutedMicros( (PitchBookOrderExecuted) msg );
            break;
        case EventIds.ID_PITCHBOOKCANCELORDER:
            encodeBookOrderCancelMicros( (PitchBookCancelOrder) msg );
            break;
        case EventIds.ID_PITCHSYMBOLCLEAR:
            encodeBookClear( (PitchSymbolClear) msg );
            break;
        case 111:
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
        _builder = (_debug) ? new DebugBinaryEncodeBuilder<>( _dump, new com.rr.codec.emea.exchange.pitch.TCPPitchEncodeBuilderImpl( _buf, _offset, _binaryVersion ) )
                            : new com.rr.codec.emea.exchange.pitch.TCPPitchEncodeBuilderImpl( _buf, _offset, _binaryVersion );
    }


    public final void encodeBookAddOrderMicros( final PitchBookAddOrder msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookAddOrderMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookAddOrderMicros" ).append( "  eventType=" ).append( "PitchBookAddOrder" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        _builder.encodeByte( transformSide( msg.getSide() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "qty6" ).append( " : " );
        _builder.encodeQty( msg.getOrderQty() );
        if ( _debug ) _dump.append( "\nField: " ).append( "symbol6" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getSecurityId(), 6 );
        if ( _debug ) _dump.append( "\nField: " ).append( "price10" ).append( " : " );
        _builder.encodeDecimal( msg.getPrice() );
        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.encodeFiller( 1 );
        _builder.end();
    }

    public final void encodeBookAddOrderLongMicros( final PitchBookAddOrder msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookAddOrderLongMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookAddOrderLongMicros" ).append( "  eventType=" ).append( "PitchBookAddOrder" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "side" ).append( " : " );
        _builder.encodeByte( transformSide( msg.getSide() ) );
        if ( _debug ) _dump.append( "\nField: " ).append( "qty10" ).append( " : " );
        _builder.encodeQty( msg.getOrderQty() );
        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getSecurityId(), 8 );
        if ( _debug ) _dump.append( "\nField: " ).append( "price19" ).append( " : " );
        _builder.encodeDecimal( msg.getPrice() );
        if ( _debug ) _dump.append( "\nField: " ).append( "filler" ).append( " : " );
        _builder.encodeFiller( 1 );
        _builder.end();
    }

    public final void encodeBookOrderExecutedMicros( final PitchBookOrderExecuted msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookOrderExecutedMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookOrderExecutedMicros" ).append( "  eventType=" ).append( "PitchBookOrderExecuted" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty6" ).append( " : " );
        _builder.encodeQty( msg.getLastQty() );
        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        _builder.encodeBase36Number( msg.getExecId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "mktMech" ).append( " : " );
        final MMTMarketMechanism tMktMech = msg.getMktMech();
        final byte tMktMechBytes = ( tMktMech != null ) ? tMktMech.getVal() : 0x00;
        _builder.encodeByte( tMktMechBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "tradingMode" ).append( " : " );
        final MMTTradingMode tTradingMode = msg.getTradingMode();
        final byte tTradingModeBytes = ( tTradingMode != null ) ? tTradingMode.getVal() : 0x00;
        _builder.encodeByte( tTradingModeBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "dividend" ).append( " : " );
        final MMTDividend tDividend = msg.getDividend();
        final byte tDividendBytes = ( tDividend != null ) ? tDividend.getVal() : 0x00;
        _builder.encodeByte( tDividendBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "algoTrade" ).append( " : " );
        final MMTAlgorithmicTrade tAlgoTrade = msg.getAlgoTrade();
        final byte tAlgoTradeBytes = ( tAlgoTrade != null ) ? tAlgoTrade.getVal() : 0x00;
        _builder.encodeByte( tAlgoTradeBytes );
        _builder.end();
    }

    public final void encodeBookOrderExecutedLongMicros( final PitchBookOrderExecuted msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookOrderExecutedLongMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookOrderExecutedLongMicros" ).append( "  eventType=" ).append( "PitchBookOrderExecuted" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "lastQty10" ).append( " : " );
        _builder.encodeQty( msg.getLastQty() );
        if ( _debug ) _dump.append( "\nField: " ).append( "execId" ).append( " : " );
        _builder.encodeBase36Number( msg.getExecId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "mktMech" ).append( " : " );
        final MMTMarketMechanism tMktMech = msg.getMktMech();
        final byte tMktMechBytes = ( tMktMech != null ) ? tMktMech.getVal() : 0x00;
        _builder.encodeByte( tMktMechBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "tradingMode" ).append( " : " );
        final MMTTradingMode tTradingMode = msg.getTradingMode();
        final byte tTradingModeBytes = ( tTradingMode != null ) ? tTradingMode.getVal() : 0x00;
        _builder.encodeByte( tTradingModeBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "dividend" ).append( " : " );
        final MMTDividend tDividend = msg.getDividend();
        final byte tDividendBytes = ( tDividend != null ) ? tDividend.getVal() : 0x00;
        _builder.encodeByte( tDividendBytes );
        if ( _debug ) _dump.append( "\nField: " ).append( "algoTrade" ).append( " : " );
        final MMTAlgorithmicTrade tAlgoTrade = msg.getAlgoTrade();
        final byte tAlgoTradeBytes = ( tAlgoTrade != null ) ? tAlgoTrade.getVal() : 0x00;
        _builder.encodeByte( tAlgoTradeBytes );
        _builder.end();
    }

    public final void encodeBookOrderCancelMicros( final PitchBookCancelOrder msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookCancelOrderMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookCancelOrderMicros" ).append( "  eventType=" ).append( "PitchBookCancelOrder" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty6" ).append( " : " );
        _builder.encodeQty( msg.getCancelQty() );
        _builder.end();
    }

    public final void encodeBookOrderCancelLongMicros( final PitchBookCancelOrder msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchBookCancelOrderLongMicros );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchBookCancelOrderLongMicros" ).append( "  eventType=" ).append( "PitchBookCancelOrder" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "orderId" ).append( " : " );
        _builder.encodeBase36Number( msg.getOrderId(), 12 );
        if ( _debug ) _dump.append( "\nField: " ).append( "cancelQty10" ).append( " : " );
        _builder.encodeQty( msg.getCancelQty() );
        _builder.end();
    }

    public final void encodeBookClear( final PitchSymbolClear msg ) {
        final long now = _tzCalculator.getNowAsInternalTime();
        _builder.start( MSG_PitchSymbolClear );
        if ( _debug ) {
            _dump.append( "  encodeMap=" ).append( "PitchSymbolClear" ).append( "  eventType=" ).append( "PitchSymbolClear" ).append( " : " );
        }

        if ( _debug ) _dump.append( "\nField: " ).append( "symbol8" ).append( " : " );
        _builder.encodeZStringFixedWidth( msg.getSecurityId(), 8 );
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
    private byte transformSide( Side val ) {
        switch( val ) {
        case Buy:  // Buy
            return (byte)'B';
        case Sell:  // Sell
            return (byte)'S';
        default:
            break;
        }
        throw new RuntimeEncodingException( " unsupported encoding on Side for value " + val );
    }

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


}
