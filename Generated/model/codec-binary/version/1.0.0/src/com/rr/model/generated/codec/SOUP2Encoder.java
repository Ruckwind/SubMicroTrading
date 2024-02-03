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
import com.rr.codec.emea.exchange.soup.SOUP2EncodeBuilderImpl;
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

public final class SOUP2Encoder implements BinaryEncoder {

   // Member Vars
    private static final byte      MSG_SequencedDataPacket = (byte)'S';
    private static final byte      MSG_UnsequencedDataPacket = (byte)'U';


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
    public SOUP2Encoder( byte[] buf, int offset ) { this( null, buf, offset ); }

    public SOUP2Encoder( String id, byte[] buf, int offset ) {
        if ( buf.length < SizeType.MIN_ENCODE_BUFFER.getSize() ) {
            throw new RuntimeException( "Encode buffer too small only " + buf.length + ", min=" + SizeType.MIN_ENCODE_BUFFER.getSize() );
        }
        _id = id;
        _buf = buf;
        _offset = offset;
        _binaryVersion   = new ViewString( "1");
        setBuilder();
    }


   // encode methods

    @Override
    public final void encode( final Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
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
        _builder = (_debug) ? new DebugBinaryEncodeBuilder<>( _dump, new com.rr.codec.emea.exchange.soup.SOUP2EncodeBuilderImpl( _buf, _offset, _binaryVersion ) )
                            : new com.rr.codec.emea.exchange.soup.SOUP2EncodeBuilderImpl( _buf, _offset, _binaryVersion );
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


}
