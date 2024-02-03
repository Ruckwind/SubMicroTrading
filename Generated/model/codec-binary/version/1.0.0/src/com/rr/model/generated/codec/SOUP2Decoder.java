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

public final class SOUP2Decoder extends AbstractBinaryDecoder {

    private final ReusableString _tmpLookupKey = new ReusableString();

   // Attrs
    private static final byte      MSG_SequencedDataPacket = (byte)'S';
    private static final byte      MSG_UnsequencedDataPacket = (byte)'U';

    private boolean _debug = false;

    private BinaryDecodeBuilder _builder;

    private       byte _msgType;
    private final byte                        _protocolVersion;
    private final String                      _id;
    private       int                         _msgStatedLen;
    private final ReusableString _dump  = new ReusableString(256);
    private final ReusableString _missedMsgTypes = new ReusableString();

    // dict var holders for conditional mappings and fields with no corresponding event entry .. useful for hooks

   // Pools


   // Constructors
    public SOUP2Decoder() { this( null ); }
    public SOUP2Decoder( String id ) {
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
        _builder = (_debug) ? new DebugBinaryDecodeBuilder<>( _dump, new com.rr.codec.emea.exchange.soup.SOUP2DecodeBuilderImpl() )
                            : new com.rr.codec.emea.exchange.soup.SOUP2DecodeBuilderImpl();
    }

    @Override
    protected final Event doMessageDecode() {
        _builder.setMaxIdx( _maxIdx );

        switch( _msgType ) {
        case MSG_SequencedDataPacket:
            return decodeSequencedDataPacket();
        case MSG_UnsequencedDataPacket:
            return decodeUnsequencedDataPacket();
        case 'T':
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

}
