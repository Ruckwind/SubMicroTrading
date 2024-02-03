package com.rr.inst;

import com.rr.core.collections.TimeSeries;
import com.rr.core.collections.TimeSeriesFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZConsumer;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.exchange.ExchangeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HistoricMutatingInstMap
 * <p>
 * An array of maps is used, indexed by the ordinal of the idSrc
 * <p>
 * the nodes are an instance of MapEntry ... initially it will use UnambigousEntry where no exchange code or ccy is required to lookup the instrument series
 * <p>
 * when another series needs to be added to the same key, the node will be upgraded initially to MicSubscopeEntry and if any dups detected on same exchange then
 * <p>
 * will be upgraded to final level requiring exchange + ccy for scoping (basically only will be used for small subset of ISIN)
 * <p>
 * each TimeSeries in node is not timeseries for a single instrument ... its the timeSeries for instrument used by the code at that time
 * <p>
 * for more an exchange instrument to reuse a key, first the previous instrument has to be deactivated or the key removed from it with an inst modify
 * <p>
 * IF duplicates detected for a key then the node will be marked as DEAD and all entries removed so no ambiguity can occur on lookups
 *
 * @NOTE tradingItemId will not change nor will the uniqueInstId or the QuanthouseUnique int code
 */
public class HistoricMutatingInstMap {

    public static final  DisabledEntry DEAD_NODE                  = new DisabledEntry();
    private static final Logger        _log                       = LoggerFactory.create( HistoricMutatingInstMap.class );
    private static final int           DEFAULT_LEAVE_NODE_ENTRIES = 4;
    private static final int           DEFAULT_CCY_ENTRIES        = 1;

    public interface MapEntry {

        /**
         * add new instrument version to the series
         *
         * @param v
         * @return true if added, false if unable to add because version is ambiguous (ie multiple uniqueInstId's now match the key in map)
         */
        boolean add( ExchInstSecDefWrapperTSEntry v );

        void addSeries( TimeSeries<ExchInstSecDefWrapperTSEntry> series );

        boolean canAdd( final ExchInstSecDefWrapperTSEntry newVal, final ExchInstSecDefWrapperTSEntry latestInNode );

        default TimeSeries<ExchInstSecDefWrapperTSEntry> createTimeSeries() { return TimeSeriesFactory.createUnboundedSmallSeries( DEFAULT_LEAVE_NODE_ENTRIES ); }

        void forEach( ZConsumer<TimeSeries<ExchInstSecDefWrapperTSEntry>> f );

        void getAll( List<TimeSeries<ExchInstSecDefWrapperTSEntry>> dest );

        ExchInstSecDefWrapperTSEntry getAt( long timeStamp );

        ExchInstSecDefWrapperTSEntry getAt( ExchangeCode code, long timeStamp );

        ExchInstSecDefWrapperTSEntry getAt( ExchangeCode code, Currency ccy, long timeStamp );

        default void setIdSrc( SecurityIDSource idSrc )                     { }

        ;
    }

    private static class DisabledEntry implements MapEntry {

        @Override public boolean add( final ExchInstSecDefWrapperTSEntry v )                                                     { return false; }

        @Override public void addSeries( final TimeSeries<ExchInstSecDefWrapperTSEntry> series )                                 { /* nothing */ }

        @Override public boolean canAdd( final ExchInstSecDefWrapperTSEntry newVal, final ExchInstSecDefWrapperTSEntry latest )  { return false; }

        @Override public void forEach( final ZConsumer<TimeSeries<ExchInstSecDefWrapperTSEntry>> f )                             { /* nothing */ }

        @Override public void getAll( final List<TimeSeries<ExchInstSecDefWrapperTSEntry>> dest )                                { /* nothing */ }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final long timeStamp )                                              { return null; }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final long timeStamp )                     { return null; }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final Currency ccy, final long timeStamp ) { return null; }
    }

    private static class MicSubscopeEntry implements MapEntry {

        private Map<ExchangeCode, TimeSeries<ExchInstSecDefWrapperTSEntry>> _map = new HashMap<>( DEFAULT_LEAVE_NODE_ENTRIES, 0.95f );
        private SecurityIDSource                                            _idSrc;

        @Override public boolean add( final ExchInstSecDefWrapperTSEntry newVal ) {

            ExchangeCode securityExchange = InstUtils.getExchangeCode( newVal, _idSrc );

            TimeSeries<ExchInstSecDefWrapperTSEntry> versions = _map.computeIfAbsent( securityExchange, ( k ) -> createTimeSeries() );

            final ExchInstSecDefWrapperTSEntry latest = versions.latest();

            if ( newVal == latest ) return true;

            if ( latest != null ) {

                if ( !canAdd( newVal, latest ) ) {
                    return false;
                }
            }

            versions.add( newVal );

            return true;
        }

        @Override public void addSeries( final TimeSeries<ExchInstSecDefWrapperTSEntry> series ) {
            final ExchangeCode                             securityExchange = InstUtils.getExchangeCode( series.latest(), _idSrc );
            final TimeSeries<ExchInstSecDefWrapperTSEntry> old              = _map.put( securityExchange, series );

            if ( old != null ) throw new AmbiguousKeyRuntimeException( "MicSubscopeEntry duplicate entry on " + securityExchange );
        }

        /**
         * can we add the newversion to the timeseries
         * <p>
         * always yes if same uniqueInstId
         * always yes if same object
         * no if current version is still active and NOT same uniqueInstId
         *
         * @param newVal
         * @param latest
         * @return
         */
        @Override public boolean canAdd( final ExchInstSecDefWrapperTSEntry newVal, final ExchInstSecDefWrapperTSEntry latest ) {
            if ( newVal == latest || latest == null ) return true;

            if ( latest != null && latest.isActiveAt( newVal.getEventTimestamp() ) && latest.getUniqueInstId() != newVal.getUniqueInstId() ) {
                return false;
            }

            return true;
        }

        @Override public void forEach( final ZConsumer<TimeSeries<ExchInstSecDefWrapperTSEntry>> f ) {
            _map.values().forEach( ( v ) -> f.accept( v ) );
        }

        @Override public void getAll( final List<TimeSeries<ExchInstSecDefWrapperTSEntry>> dest ) { dest.addAll( _map.values() ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( long timeStamp )                     { throw new AmbiguousKeyRuntimeException( "MICSubscopeEntry is non unique requires MIC to determine entry : " + toString() ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( ExchangeCode code, long timeStamp ) {
            TimeSeries<ExchInstSecDefWrapperTSEntry> versions = _map.get( code );

            return (versions != null) ? versions.getAt( timeStamp ) : null;
        }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final Currency ccy, final long timeStamp ) { return getAt( code, timeStamp ); }

        @Override public void setIdSrc( SecurityIDSource idSrc ) {
            _idSrc = idSrc;
        }

        @Override public String toString() {
            ReusableString s = TLC.strPop();

            s.copy( "MicSubscopeEntry " ).append( _idSrc.name() );

            for ( Map.Entry<ExchangeCode, TimeSeries<ExchInstSecDefWrapperTSEntry>> e : _map.entrySet() ) {
                s.append( " : " ).append( e.getKey() ).append( " -> " );
                final TimeSeries<ExchInstSecDefWrapperTSEntry> v = e.getValue();
                v.forEach( ( t ) -> s.append( "|" ).append( t.id() ) );
            }

            String r = s.toString();

            TLC.strPush( s );

            return r;
        }
    }

    private static class MicCcySubscopeEntry implements MapEntry {

        private Map<ZString, TimeSeries<ExchInstSecDefWrapperTSEntry>> _map = new HashMap<>( DEFAULT_LEAVE_NODE_ENTRIES, 0.95f );

        private SecurityIDSource _idSrc;

        @Override public boolean add( final ExchInstSecDefWrapperTSEntry newVal ) {

            final ReusableString tmpKey = TLC.strPop();

            ExchangeCode securityExchange = InstUtils.getExchangeCode( newVal, _idSrc );

            InstUtils.formExchangeWithCcyKey( tmpKey, securityExchange, newVal.getSecDef().getCurrency() );

            TimeSeries<ExchInstSecDefWrapperTSEntry> versions = _map.get( tmpKey );

            if ( versions == null ) {
                versions = createTimeSeries();
                _map.put( tmpKey, versions );
            } else {
                TLC.instance().pushback( tmpKey );
            }

            final ExchInstSecDefWrapperTSEntry latest = versions.latest();

            if ( newVal == latest ) return true;

            if ( latest != null ) {
                if ( !canAdd( newVal, latest ) ) {
                    return false;
                }
            }

            versions.add( newVal );

            return true;
        }

        @Override public void addSeries( final TimeSeries<ExchInstSecDefWrapperTSEntry> series ) {
            final SecurityDefinitionImpl secDef           = series.latest().getSecDef();
            final ExchangeCode           securityExchange = InstUtils.getExchangeCode( series.latest(), _idSrc );

            final ReusableString tmpKey = TLC.instance().pop();

            InstUtils.formExchangeWithCcyKey( tmpKey, securityExchange, secDef.getCurrency() );

            final TimeSeries<ExchInstSecDefWrapperTSEntry> old = _map.put( tmpKey, series );

            if ( old != null ) {
                throw new AmbiguousKeyRuntimeException( "MicCcySubscopeEntry duplicate entry on " + securityExchange + " key=" + tmpKey.toString() );
            }
        }

        /**
         * can we add the newversion to the timeseries
         * <p>
         * always yes if same uniqueInstId
         * always yes if same object
         * no if current version is still active and NOT same uniqueInstId
         *
         * @param newVal
         * @param latest
         * @return
         */
        @Override public boolean canAdd( final ExchInstSecDefWrapperTSEntry newVal, final ExchInstSecDefWrapperTSEntry latest ) {
            if ( newVal == latest ) return true;

            if ( latest != null && latest.isActiveAt( newVal.getEventTimestamp() ) && latest.getUniqueInstId() != newVal.getUniqueInstId() ) {
                return false;
            }

            return true;
        }

        @Override public void forEach( final ZConsumer<TimeSeries<ExchInstSecDefWrapperTSEntry>> f ) {
            _map.values().forEach( ( v ) -> f.accept( v ) );
        }

        @Override public void getAll( final List<TimeSeries<ExchInstSecDefWrapperTSEntry>> dest ) { dest.addAll( _map.values() ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( long timeStamp ) {
            throw new AmbiguousKeyRuntimeException( "MicCcySubscopeEntry is non unique requires MIC and CCY to determine entry for " + toString() );
        }

        @Override public ExchInstSecDefWrapperTSEntry getAt( ExchangeCode code, long timeStamp ) {
            String x = (code == null) ? "null" : code.toString();

            throw new AmbiguousKeyRuntimeException( "MicCcySubscopeEntry is non unique requires MIC and CCY to determine entry, code=" + x + " for " + toString() );
        }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final Currency ccy, final long timeStamp ) {
            final ReusableString tmpKey = TLC.instance().pop();

            InstUtils.formExchangeWithCcyKey( tmpKey, code, ccy );

            TimeSeries<ExchInstSecDefWrapperTSEntry> versions = _map.get( tmpKey );

            return (versions != null) ? versions.getAt( timeStamp ) : null;
        }

        @Override public void setIdSrc( SecurityIDSource idSrc ) {
            _idSrc = idSrc;
        }

        @Override public String toString() {
            ReusableString s = TLC.strPop();

            s.copy( "MicCcySubscopeEntry " ).append( _idSrc.name() );

            for ( Map.Entry<ZString, TimeSeries<ExchInstSecDefWrapperTSEntry>> e : _map.entrySet() ) {
                s.append( " : " ).append( e.getKey() ).append( " -> " );
                final TimeSeries<ExchInstSecDefWrapperTSEntry> v = e.getValue();
                v.forEach( ( t ) -> s.append( "|" ).append( t.id() ) );
            }

            String r = s.toString();

            TLC.strPush( s );

            return r;
        }
    }

    private final Map<ZString, MapEntry>                         _idMap;
    private final SecurityIDSource                               _securityIDSource;
    private final List<TimeSeries<ExchInstSecDefWrapperTSEntry>> _tmpNodeList = new ArrayList<>();

    public HistoricMutatingInstMap( int preSize, SecurityIDSource idSrc ) {
        _idMap            = new HashMap<>( preSize, 0.75f );
        _securityIDSource = idSrc;
    }

    public void addKey( SecurityIDSource idSrc, ZString keyVal, HistoricalExchangeInstrumentImpl instTimeSeries, ExchInstSecDefWrapperTSEntry latest ) {

        if ( keyVal == null || keyVal.length() == 0 ) {
            return;
        }

        final Map<ZString, MapEntry> map = _idMap;

        MapEntry ce = map.computeIfAbsent( keyVal, ( k ) -> new UnambiguousEntry( _securityIDSource ) );

        if ( ce == DEAD_NODE ) {
            _log.info( "HistoricMutatingInstMap unable to add inst to " + idSrc + " keyMap with key " + keyVal + " because node disabled due to duplicate values def=" + latest );
            return;
        }

        if ( !ce.add( latest ) ) {
            upgradeNode( map, idSrc, keyVal, ce, latest );
        }
    }

    public MapEntry get( final ZString securityId ) {
        final MapEntry e = _idMap.get( securityId );

        if ( e == DEAD_NODE ) {
            throw new AmbiguousKeyRuntimeException( "Attempt to search for instrument on key matching multiple instruments idSrc=" + _securityIDSource + ", secId=" + securityId, _securityIDSource, securityId.toString() );
        }

        return e;
    }

    public void removeKey( final ZString keyVal, final HistoricalExchangeInstrumentImpl instTimeSeries, final ExchInstSecDefWrapperTSEntry prev ) {
        final Map<ZString, MapEntry> map = _idMap;

        MapEntry ce = map.computeIfAbsent( keyVal, ( k ) -> new UnambiguousEntry( _securityIDSource ) );

        if ( ce == DEAD_NODE ) {
            return;
        }

        // all work already completed in historic sec def store
        // when key changes a new version is created and prev one has end date set
        // never actually remove a key
    }

    private Exchange getExchange( final ExchangeCode exCode ) {
        if ( exCode == null ) return null;

        Exchange ex = ExchangeManager.instance().getByCode( exCode );

        if ( ex == null ) {
            throw new RuntimeException( "ExchangeManager doesnt have exCode=[" + exCode + "] loaded" );
        }

        return ex;
    }

    private void nodeDead( final Map<ZString, MapEntry> map, final ZString keyVal ) {
        map.put( keyVal, DEAD_NODE );
    }

    private void upgradeNode( Map<ZString, MapEntry> map, SecurityIDSource idSrc, ZString keyVal, MapEntry ce, ExchInstSecDefWrapperTSEntry latest ) {

        _tmpNodeList.clear();
        ce.getAll( _tmpNodeList );

        MapEntry newNode = null;

        if ( ce.getClass() == UnambiguousEntry.class ) {
            newNode = new MicSubscopeEntry();
        } else if ( ce.getClass() == MicSubscopeEntry.class ) {
            if ( idSrc == SecurityIDSource.ISIN ) {
                newNode = new MicCcySubscopeEntry();
            } else {
                _log.info( "HistoricMutatingInstMap upgradeNode - cannot upgrade node beyone MIC scope : unable to add inst to " + idSrc + " keyMap with key " + keyVal +
                           " because node disabled due to duplicate values def=" + latest.toString() );

                nodeDead( map, keyVal );

                return;
            }
        } else if ( ce.getClass() == MicCcySubscopeEntry.class ) {
            _log.info( "HistoricMutatingInstMap upgradeNode - cannot upgrade node beyone MIC+CCY scope : unable to add inst to " + idSrc + " keyMap with key " + keyVal +
                       " because node disabled due to duplicate values def=" + latest.toString() );

            nodeDead( map, keyVal );

            return;
        } else if ( ce == DEAD_NODE ) {
            return;
        }

        newNode.setIdSrc( idSrc );

        for ( TimeSeries<ExchInstSecDefWrapperTSEntry> series : _tmpNodeList ) {
            if ( series != null && series.size() > 0 ) {
                newNode.addSeries( series );
            }
        }

        map.put( keyVal, newNode );

        if ( !newNode.add( latest ) ) {
            upgradeNode( map, idSrc, keyVal, newNode, latest );
        }
    }

    private class UnambiguousEntry implements MapEntry {

        private SecurityIDSource                         _idSrc;
        private TimeSeries<ExchInstSecDefWrapperTSEntry> _versions = createTimeSeries();

        public UnambiguousEntry() { /* reflection */ }

        public UnambiguousEntry( final SecurityIDSource idSrc ) {
            _idSrc = idSrc;
        }

        @Override public boolean add( final ExchInstSecDefWrapperTSEntry newVal ) {
            final ExchInstSecDefWrapperTSEntry latest = _versions.latest();

            if ( newVal == latest ) return true;

            if ( latest != null ) {

                if ( !canAdd( newVal, latest ) ) {
                    return false;
                }
            }

            _versions.add( newVal );

            return true;
        }

        @Override public void addSeries( final TimeSeries<ExchInstSecDefWrapperTSEntry> series ) { throw new SMTRuntimeException( "Cannot add new series" ); }

        /**
         * can we add the newversion to the timeseries
         * <p>
         * always yes if same uniqueInstId
         * always yes if same object
         * no if current version is still active and NOT same uniqueInstId
         *
         * @param newVal
         * @param latestInNode
         * @return
         */
        @Override public boolean canAdd( final ExchInstSecDefWrapperTSEntry newVal, final ExchInstSecDefWrapperTSEntry latestInNode ) {
            if ( newVal == latestInNode ) return true;

            if ( latestInNode != null &&
                 latestInNode.isActiveAt( newVal.getEventTimestamp() ) &&
                 latestInNode.getUniqueInstId() != newVal.getUniqueInstId() ) {

                return false;
            }

            return true;
        }

        @Override public void forEach( final ZConsumer<TimeSeries<ExchInstSecDefWrapperTSEntry>> f )                             { f.accept( _versions ); }

        @Override public void getAll( final List<TimeSeries<ExchInstSecDefWrapperTSEntry>> dest )                                { dest.add( _versions ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( long timeStamp )                                                    { return _versions.getAt( timeStamp ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final long timeStamp )                     { return getAt( timeStamp ); }

        @Override public ExchInstSecDefWrapperTSEntry getAt( final ExchangeCode code, final Currency ccy, final long timeStamp ) { return getAt( timeStamp ); }

        @Override public void setIdSrc( SecurityIDSource idSrc ) {
            _idSrc = idSrc;
        }
    }
}
