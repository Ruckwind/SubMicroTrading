/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.TimeSeries;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;

import java.util.List;
import java.util.Set;

/**
 * Historical Exchange Instrument Sec Def Locator
 * <p>
 * securityExchange is expected to be a MIC
 *
 * @NOTE : all instrument getters from InstrumentLocator should return the Instrument timeseries container not the timestamped version wrapper
 * <p>
 * this means the instrument can be consistently used in maps ... you cannot mix the timestamped version with the timeseries container
 */
public interface HistExchInstSecDefLocator extends InstrumentLocator {

    /**
     * will generate temp objects avoid usage during normal running
     *
     * @param instruments
     */
    void getAllInstruments( Set<ExchInstSecDefWrapperTSEntry> instruments, long atTimestamp );

    /**
     * @param commonInstrumentId
     * @param ccy
     * @return the CommonInstrument for the commonInstrumentId and currency pairing
     */
    CommonInstrument getCommonInstrument( long commonInstrumentId, Currency ccy, long atTimestamp );

    TimeSeries<CommonInstrument> getCommonInstrumentSeries( long commonInstrumentId, Currency ccy );

    /**
     * @param commonInstrumentId
     * @param dest               - copy all of the commonInstruments for the commonInstrumentId into the dest list
     */
    void getCommonInstruments( long commonInstrumentId, long atTimestamp, List<CommonInstrument> dest );

    /**
     * @param securityId
     * @param securityIDSource
     * @param atTimestamp
     * @return
     */
    ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, long atTimestamp );

    /**
     * @param securityId
     * @param securityIDSource
     * @param exchangeCode     can be left null for QH idsrc
     * @return
     */
    ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, long atTimestamp );

    ExchInstSecDefWrapperTSEntry getExchInstAt( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, Currency currency, long atTimestamp );

    TimeSeries<ExchInstSecDefWrapperTSEntry> getExchInstSeriesByUniqueInstId( long uniqueInstId );

    /**
     * locate the exchange instrument time series using the unchanging unique long / int key (depending on idSrc)
     * <p>
     * the HistExchInstSecDefWrapperTS acts as a proxy ontop of the last version of the exchange instrument
     * <p>
     * exchange instrument new versions only created for key changes and a few key related fields like maturityDate
     * <p>
     * can be used where you are sure the key is unique ... for example if only a subset of unique ISIN's loaded into store you can search by ISIN
     *
     * @param longKey          - uniqueInstId / quanthouseIntCode
     * @param securityIDSource - uniqueInstId / quanthouseIntCode
     * @return the exchange instrument time series wrapper or NULL if key is unknown
     */
    HistExchInstSecDefWrapperTS getExchInstTS( long longKey, SecurityIDSource securityIDSource );

    /**
     * locate the exchange instrument time series using the unchanging unique long / int key (depending on idSrc)
     * <p>
     * the HistExchInstSecDefWrapperTS acts as a proxy ontop of the last version of the exchange instrument
     * <p>
     * exchange instrument new versions only created for key changes and a few key related fields like maturityDate
     * <p>
     * can be used where you are sure the key is unique ... for example if only a subset of unique ISIN's loaded into store you can search by ISIN
     *
     * @param longKey      - uniqueInstId / quanthouseIntCode
     * @param exchangeCode - exchange code
     * @return the exchange instrument time series wrapper or NULL if key is unknown
     */
    HistExchInstSecDefWrapperTS getExchInstTS( long longKey, ExchangeCode exchangeCode );

    /**
     * locate the exchange instrument time series using just an id source and key
     * <p>
     * the HistExchInstSecDefWrapperTS acts as a proxy ontop of the last version of the exchange instrument
     * <p>
     * exchange instrument new versions only created for key changes and a few key related fields like maturityDate
     * <p>
     * can be used where you are sure the key is unique ... for example if only a subset of unique ISIN's loaded into store you can search by ISIN
     *
     * @param securityId
     * @param securityIDSource
     * @return the exchange instrument time series wrapper or NULL if key is unknown
     * @throws AmbiguousKeyRuntimeException - if key was seen to be non unique in locator
     */
    HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource securityIDSource );

    /**
     * locate the exchange instrument time series using just an id source and exchangeCode and securityId
     * <p>
     * can be used where you are sure the key is unique within exchange for example if only a subset of unique ISIN's within an exchange is loaded into store you can search by ISIN
     *
     * @param securityId
     * @param securityIDSource
     * @param exchangeCode
     * @return the exchange instrument time series wrapper or NULL if key is unknown
     * @throws AmbiguousKeyRuntimeException - if key was seen to be non unique in locator
     */
    HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode );

    /**
     * locate the exchange instrument time series using id source, exchangeCode, currency and securityId
     *
     * @param securityId
     * @param securityIDSource
     * @param exchangeCode
     * @param currency
     * @return the exchange instrument time series wrapper or NULL if key is unknown
     * @throws AmbiguousKeyRuntimeException - if key was seen to be non unique in locator
     */
    HistExchInstSecDefWrapperTS getExchInstTS( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode, Currency currency );

    /**
     * will generate temp objects avoid usage during normal running
     *
     * @param instruments
     */
    void getInstruments( Set<ExchInstSecDefWrapperTSEntry> instruments, Exchange ex, long atTimestamp );

    /**
     * @param parentCompanyId
     * @return the requested ParentCompany or null if not found
     */
    ParentCompany getParentCompany( long parentCompanyId, long atTimestamp );

    TimeSeries<ParentCompany> getParentCompanySeries( long parentCompanyId );
}
