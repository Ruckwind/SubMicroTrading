package com.rr.core.model;

import com.rr.core.collections.TimeSeries;
import com.rr.core.collections.TimeSeriesFactory;
import com.rr.core.lang.ZString;

import java.util.Iterator;
import java.util.List;

/**
 * historically represents a parent company
 * <p>
 * acts as a proxy to the current latest version of the ParentCompany
 */
public class HistoricalParentCompanyImpl implements HistoricalParentCompany {

    private static final int DEFAULT_ENTRIES = 4;

    /**
     * array of ParentCompany from newest to oldest in time order
     */
    private TimeSeries<ParentCompany> _versions = TimeSeriesFactory.createUnboundedSmallSeries( DEFAULT_ENTRIES );

    /**
     * have latest as seperate variable so can ensure latest version changes after fully added to version
     * required for the thread safe wrapper
     */
    private ParentCompany _latest = null;

    @Override public ZString getCompanyName()                                               { return _latest.getCompanyName(); }

    @Override public long getParentCompanyId()                                              { return _latest.getParentCompanyId(); }

    @Override public CommonInstrument getCommonInstrument( final long commmonInstrumentId ) { return _latest.getCommonInstrument( commmonInstrumentId ); }

    @Override public void getCommonInstruments( final List<CommonInstrument> dest )         { _latest.getCommonInstruments( dest ); }

    @Override public void attach( final CommonInstrument ci )                               { _latest.attach( ci ); }

    @Override public int getNumCommonInsts()                                                { return _latest.getNumCommonInsts(); }

    @Override public long getEventTimestamp()                                               { return _latest.getEventTimestamp(); }

    @Override public Iterator<ParentCompany> iterator()                                     { return _versions.iterator(); }

    @Override public ParentCompany latest()                                                 { return _latest; }

    @Override public ParentCompany getAt( final long timeMS )                               { return _versions.getAt( timeMS ); }

    @Override public int size()                                                             { return _versions.size(); }

    @Override public boolean add( final ParentCompany entry ) {
        boolean added = _versions.add( entry );

        _latest = _versions.latest();

        return added;
    }

    @Override public Iterator<ParentCompany> oldestToNewestIterator()                       { return _versions.oldestToNewestIterator(); }

    ;
}

