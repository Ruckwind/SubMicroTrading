/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.model.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.channel.MktDataChannelBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Market Data Source Manager that supports market data partitioned by pipeline ... CME for example can have many channels of data so this allows those channels to be spread across pipelines
 * <p>
 * Currently only really intended for CME style L3 market data providers
 *
 * @param <T>
 * @param <C>
 */
public class PipelineableMDSrcManager<T extends MktDataWithContext, C extends Context> implements MktDataSrcMgr<T> {

    private transient final Set<MktDataSrc<T>>                           _srcs                = new LinkedHashSet<>();
    private transient final String                                       _id;
    private transient final Map<String, MktDataSrc<T>>                   _pipeLineIdToBookSrc = new ConcurrentHashMap<>();
    private transient final Map<MktDataSrc<T>, SingleMDSrcSubsMgr<T, C>> _subMgr              = new ConcurrentHashMap<>();
    private transient final NoArgsFactory<C>                             _contextFactory;
    private transient final List<String>                   _pipeLines        = new ArrayList<>();
    private transient       MktDataChannelBuilder<Integer> _mdSessionBuilder = null;

    public PipelineableMDSrcManager( String id, NoArgsFactory<C> contextFactory ) {
        this( id, contextFactory, null );
    }

    public PipelineableMDSrcManager( String id, NoArgsFactory<C> contextFactory, MktDataSrc<T>[] srcs ) {
        _id             = id;
        _contextFactory = contextFactory;

        if ( srcs != null ) {
            for ( MktDataSrc<T> src : srcs ) {
                add( src );
            }
        }
    }

    @Override public MktDataSubsMgr<T> findSubsMgr( Instrument inst, String pipeLineId ) {

        if ( pipeLineId == null || pipeLineId.length() == 0 ) {
            throw new SMTRuntimeException( "Missing pipelineId " + inst.getSecurityDesc() + " with pipeLineId=" + pipeLineId );
        }

        MktDataSrc<T> src = _pipeLineIdToBookSrc.get( pipeLineId );

        if ( src == null ) {
            throw new SMTRuntimeException( "Unable to find book source for " + inst.getSecurityDesc() + " with pipeLineId=" + pipeLineId );
        }

        MktDataSubsMgr<T> mgr = _subMgr.get( src );

        if ( mgr == null ) {
            throw new SMTRuntimeException( "Unable to find subscription manager for " + inst.getSecurityDesc() + " with pipeLineId=" + pipeLineId );
        }

        if ( inst instanceof ExchangeInstrument ) {
            ExchangeInstrument ei = (ExchangeInstrument) inst;

            int channel = ei.getIntSegment();

            if ( channel == 0 ) {
                throw new SMTRuntimeException( "Instrument missing instrument channel/intSegment for " + inst.getSecurityDesc() + " with pipeLineId=" + pipeLineId );
            }

            ensureBookSourceHasMarketDataSession( src, channel, pipeLineId );
        }

        return mgr;
    }

    @Override public String getComponentId() {
        return _id;
    }

    public synchronized void add( MktDataSrc<T> src ) {
        _srcs.add( src );

        List<String> pipeLineIds = src.getPipeLineIds();

        for ( String pipeId : pipeLineIds ) {
            if ( _pipeLineIdToBookSrc.containsKey( pipeId ) ) {
                throw new SMTRuntimeException( "Error duplicate pipeLineId " + pipeId + " used in bookSrc=" + src.getComponentId() +
                                               " and " + _pipeLineIdToBookSrc.get( pipeId ).getComponentId() );
            }

            _pipeLineIdToBookSrc.put( pipeId, src );

            if ( !_pipeLines.contains( pipeId ) ) {
                _pipeLines.add( pipeId );
            }
        }

        SingleMDSrcSubsMgr<T, C> subMgr = new SingleMDSrcSubsMgr<>( "SubMgr" + src.getComponentId(), src, _contextFactory );

        _subMgr.put( src, subMgr );
    }

    public synchronized MktDataSrc<T> findSource( Instrument inst ) {
        MktDataSrc<T> foundSrc = null;

        for ( MktDataSrc<T> src : _srcs ) {
            if ( src.supports( inst ) ) {
                foundSrc = src;
                break;
            }
        }

        return foundSrc;
    }

    public List<String> getPipeLines() {
        return _pipeLines;
    }

    public void setMdSessionBuilder( MktDataChannelBuilder<Integer> mdSessionBuilder ) {
        _mdSessionBuilder = mdSessionBuilder;
    }

    @SuppressWarnings( "boxing" )
    private void ensureBookSourceHasMarketDataSession( MktDataSrc<T> src, int channel, String pipeLineId ) {
        if ( _mdSessionBuilder != null ) {
            _mdSessionBuilder.register( channel, pipeLineId, src );
        }
    }
}
