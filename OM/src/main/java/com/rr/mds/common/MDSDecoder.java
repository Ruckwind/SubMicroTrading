/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.binary.BinaryBigEndianDecoderUtils;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.TimeUtils;
import com.rr.core.model.*;

public final class MDSDecoder implements Decoder {

    private final BinaryBigEndianDecoderUtils _builder;
    private final ReusableString              _tmpRIC = new ReusableString();

    InstrumentLocator _instLocator;

    public MDSDecoder() {

        _builder = new BinaryBigEndianDecoderUtils();
    }

    /**
     * @NOTE ignore the params, buffer already setup in builder
     */
    @Override
    public Event decode( byte[] msg, int offset, int maxIdx ) {

        _builder.start( msg, offset, maxIdx );

        byte operation = _builder.decodeByte();

        switch( operation ) {
        case MDSReusableTypeConstants.SUB_ID_SUBSCRIBE:
            return decodeSubscribe();
        case MDSReusableTypeConstants.SUB_ID_TRADING_BAND_UPDATE:
            return decodeTradingRangeUpdate();
        case MDSReusableTypeConstants.SUB_ID_FX_SNAPSHOT:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_DEPTH:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH:
            break;
        }

        return null;
    }

    @Override public InstrumentLocator getInstrumentLocator() {
        return _instLocator;
    }

    @Override public void setInstrumentLocator( InstrumentLocator instrumentLocator ) { /* dont care */ }

    @Override
    public int getLength() {
        return _builder.getLength();
    }

    @Override public long getReceived()                                               { return 0; }

    @Override public void setReceived( long nanos )                                   { /*dont care */ }

    @Override public int getSkipBytes()                                               { return 0; }

    @Override public int parseHeader( byte[] inBuffer, int inHdrLen, int bytesRead )  { return 0; }

    @Override public Event postHeaderDecode()                                         { return null; }

    @Override public ResyncCode resync( byte[] fixMsg, int offset, int maxIdx )       { return null; }

    @Override public void setClientProfile( ClientProfile client )                    { /* dont care */ }

    @Override public void setNanoStats( boolean nanoTiming )                          { /* dont care */ }

    @Override public void setTimeUtils( TimeUtils calc )                              { /* dont care */ }

    @Override public String getComponentId()                                          { return null; }

    public void init( InstrumentLocator il ) {
        _instLocator = il;
    }

    private Event decodeSubscribe() {

        byte subOp = _builder.decodeByte();

        int count = 0xFF & _builder.decodeByte();

        ReusableString chain = null;
        ReusableString tmp;

        for ( int i = 0; i < count; i++ ) {

            tmp = TLC.instance().getString();

            _builder.decodeString( tmp );

            tmp.setNext( chain );
            chain = tmp;
        }

        _builder.end();

        switch( subOp ) {
        case MDSReusableTypeConstants.SUB_ID_SUBSCRIBE:
            break;
        case MDSReusableTypeConstants.SUB_ID_TRADING_BAND_UPDATE:
            return subscribeTradingRangeUpdate( chain );
        case MDSReusableTypeConstants.SUB_ID_FX_SNAPSHOT:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_DEPTH:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH:
            break;
        }

        return null;
    }

    private Event decodeTradingRangeUpdate() {

        int count = 0xFF & _builder.decodeByte();

        double lower;
        double upper;

        long lowerId;
        long upperId;

        int lowerFlags;
        int upperFlags;

        for ( int i = 0; i < count; ++i ) {

            _builder.decodeString( _tmpRIC );

            lower      = _builder.decodePrice();
            upper      = _builder.decodePrice();
            lowerId    = _builder.decodeLong();
            upperId    = _builder.decodeLong();
            lowerFlags = _builder.decodeInt();
            upperFlags = _builder.decodeInt();

            ExchangeInstrument inst = _instLocator.getExchInst( _tmpRIC, SecurityIDSource.ExchangeSymbol, null );

            if ( inst != null ) {
                if ( upper > 0 ) inst.getValidTradingRange().setMaxBuy( upperId, upper, upperFlags );
                inst.getValidTradingRange().setMinSell( lowerId, lower, lowerFlags );
            }
        }

        return null;
    }

    private Event subscribeTradingRangeUpdate( ReusableString chain ) {

        TLC.instance().recycleChain( chain );

        return null;
    }
}
