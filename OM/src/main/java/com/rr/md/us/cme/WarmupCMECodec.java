/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.codec.Decoder;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Utils;
import com.rr.core.warmup.JITWarmup;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.md.us.cme.writer.CMEFastFixEncoder;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.factory.MDEntryFactory;
import com.rr.model.generated.internal.events.factory.MDIncRefreshFactory;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;

public class WarmupCMECodec implements JITWarmup {

    private static final Logger _log = LoggerFactory.create( WarmupCMECodec.class );

    private final Decoder           _decoder = new CMEFastFixDecoder( "CMETstReader", "data/cme/templates.xml", -1, false );
    private final CMEFastFixEncoder _encoder = new CMEFastFixEncoder( "CMETstWriter", "data/cme/templates.xml", false );

    private final byte[] _buf = _encoder.getBytes();

    private final String    _dateStr = "20120403";
    private final TimeUtils _calc;

    private final int              _warmupCount;
    private final AllEventRecycler _recycler;

    private final SuperPool<MDIncRefreshImpl> _mdIncPool    = SuperpoolManager.instance().getSuperPool( MDIncRefreshImpl.class );
    private final MDIncRefreshFactory         _mdIncFactory = new MDIncRefreshFactory( _mdIncPool );

    private final SuperPool<MDEntryImpl> _entryPool    = SuperpoolManager.instance().getSuperPool( MDEntryImpl.class );
    private final MDEntryFactory         _entryFactory = new MDEntryFactory( _entryPool );
    private       int                    _bookSeqNum   = 0;
    private       int                    _seqNum       = 0;

    public WarmupCMECodec( int warmupCount ) {
        _warmupCount = warmupCount;
        _calc        = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _decoder.setTimeUtils( _calc );
        _recycler = new AllEventRecycler();
    }

    @Override
    public String getName() {
        return "Codecs";
    }

    @Override
    public void warmup() {
        _log.info( "WarmupCMECodec start msgs=" + _seqNum );
        _seqNum = 0;
        for ( int i = 0; i < _warmupCount; i++ ) {
            T81();
            T83();
            T84();
            T103();
            T109();
        }
        _log.info( "WarmupCMECodec end msgs=" + _seqNum );
    }

    public void T103() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( ++_seqNum, 5 );
        _encoder.encode( inc, 103, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        _recycler.recycle( dec );
    }

    public void T109() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( ++_seqNum, 5 );
        _encoder.encode( inc, 109, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        _recycler.recycle( dec );
    }

    public void T81() {
        MDIncRefreshImpl inc = makeMDIncRefresh( 10 );
        _encoder.encode( inc, 81, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        _recycler.recycle( dec );
    }

    public void T83() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( ++_seqNum, 5 );
        _encoder.encode( inc, 83, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        _recycler.recycle( dec );
    }

    public void T84() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( ++_seqNum, 5 );
        _encoder.encode( inc, 84, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        _recycler.recycle( dec );
    }

    @SuppressWarnings( "null" )
    public MDIncRefreshImpl makeMDIncRefresh( int lvls ) {

        MDIncRefreshImpl inc = _mdIncFactory.get();

        inc.setEventTimestamp( Utils.nanoTime() );
        inc.setMsgSeqNum( ++_seqNum );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( lvls * 2 );

        MDEntryImpl first = null;
        MDEntryImpl tmp   = null;

        for ( int i = 0; i < lvls; i++ ) {

            if ( first == null ) {
                tmp = first = _entryFactory.get();
            } else {
                tmp.setNext( _entryFactory.get() );
                tmp = tmp.getNext();
            }

            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().setValue( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.Change );
            tmp.setRepeatSeq( ++_bookSeqNum );
            tmp.setNumberOfOrders( i * 10 );
            tmp.setMdPriceLevel( i + 1 );
            tmp.setMdEntryType( MDEntryType.Bid );
            tmp.setMdEntryPx( 1000.12345 - i );
            tmp.setMdEntrySize( 100 - i * 10 );
            tmp.setMdEntryTime( 800000 + i );

            tmp.setNext( _entryFactory.get() );
            tmp = tmp.getNext();

            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().setValue( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.New );
            tmp.setRepeatSeq( ++_bookSeqNum );
            tmp.setNumberOfOrders( i * 10 );
            tmp.setMdPriceLevel( i );
            tmp.setMdEntryType( MDEntryType.Offer );
            tmp.setMdEntryPx( 1000.12345 + i );
            tmp.setMdEntrySize( 100 + i * 10 );
            tmp.setMdEntryTime( 800000 + i );
        }

        inc.setMDEntries( first );

        return inc;
    }

    @SuppressWarnings( "null" )
    public MDIncRefreshImpl makeMDIncRefreshTrade( int numMDEntries ) {

        MDIncRefreshImpl inc = _mdIncFactory.get();

        inc.setEventTimestamp( Utils.nanoTime() );
        inc.setMsgSeqNum( ++_seqNum );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( numMDEntries );

        MDEntryImpl first = null;
        MDEntryImpl tmp   = null;

        for ( int i = 0; i < numMDEntries; i++ ) {

            if ( first == null ) {
                tmp = first = _entryFactory.get();
            } else {
                tmp.setNext( _entryFactory.get() );
                tmp = tmp.getNext();
            }

            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().setValue( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.New );
            tmp.setRepeatSeq( i + 1 );
            tmp.setNumberOfOrders( i * 10 );
            tmp.setMdPriceLevel( i + 1 );
            tmp.setMdEntryType( MDEntryType.Trade );
            tmp.setMdEntryPx( 1000.12345 + i );
            tmp.setMdEntrySize( 100 + i * 10 );
            tmp.setMdEntryTime( 800000 + i );
        }

        inc.setMDEntries( first );

        return inc;
    }
}
