package com.rr.core.model;

import java.util.List;

public interface CommonInstrument extends InstrumentWrite, PointInTime {

    void attach( ExchangeInstrument inst );

    /**
     * remove instrument from the list of exchange instruments
     *
     * @param inst
     */
    void detach( ExchangeInstrument inst );

    Currency getCcy();

    long getCommonInstrumentId();

    /**
     * @param code
     * @return the listing for specified exchange code
     */
    ExchangeInstrument getListing( ExchangeCode code );

    /**
     * @param dest list to copy all of the listed ExchangeInstruments into
     */
    void getListings( List<ExchangeInstrument> dest );

    /**
     * @return the ParentCompany or null if none (eg future)
     */
    ParentCompany getParentCompany();

    ExchangeInstrument getPrimaryListing();
}
