/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.lang.ZString;

import java.util.List;
import java.util.Set;

/**
 * securityExchange is expected to be a MIC
 */
public interface InstrumentLocator extends SMTInitialisableComponent, PersistAsRef {

    void getAllCommonInsts( Set<CommonInstrument> instruments );

    /**
     * will generate temp objects avoid usage during normal running
     *
     * @param instruments
     */
    void getAllExchInsts( Set<ExchangeInstrument> instruments );

    void getAllFXInsts( Set<FXInstrument> instruments );

    void getAllStratInsts( Set<StrategyInstrument> instruments );

    /**
     * @param id
     * @return instrument matching supplied identifier
     */
    Instrument getByInstId( ZString id );

    /**
     * @param commonInstrumentId
     * @param ccy
     * @return the CommonInstrument for the commonInstrumentId and currency pairing
     */
    CommonInstrument getCommonInstrument( long commonInstrumentId, Currency ccy );

    /**
     * @param commonInstrumentId
     * @param dest               - copy all of the commonInstruments for the commonInstrumentId into the dest list
     */
    void getCommonInstruments( long commonInstrumentId, List<CommonInstrument> dest );

    ExchangeInstrument getDummyExchInst(); // get the dummy instrument singleton

    /**
     * @param securityId
     * @param securityIDSource
     * @param exchangeCode     can be left null for QH idsrc
     * @return an exchange instrument
     */
    ExchangeInstrument getExchInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode );

    ExchangeInstrument getExchInstByExchangeLong( ExchangeCode exchangeCode, long instrumentId );

    ExchangeInstrument getExchInstByIsin( ZString isin, ExchangeCode primarySecurityExchange, Currency currency );

    /**
     * lookup instrument by universally unique long identifier, currently only QuanthouseUniqueLong supported but UniqueInstId should be added when avail
     *
     * @param src          - currently only {@link  }
     * @param instrumentId
     * @return
     */
    ExchangeInstrument getExchInstByUniqueCode( SecurityIDSource src, long instrumentId );

    ExchangeInstrument getExchInstByUniqueInstId( long uniqueInstId );

    /**
     * get the list of instruments matching the supplied key irrespective of exchange / ccy
     *
     * @param insts            - set to copy instruments too (clears first)
     * @param securityId
     * @param securityIDSource
     */
    void getExchInsts( Set<ExchangeInstrument> insts, ZString securityId, SecurityIDSource securityIDSource );

    /**
     * will generate temp objects avoid usage during normal running
     *
     * @param instruments
     */
    void getExchInsts( Set<ExchangeInstrument> instruments, Exchange ex );

    /**
     * get list of the exchange instruments in same security group
     *
     * @param securityGrp
     * @param dest        - container to place the instruments in ... unsorted
     * @param mic         - optional MIC code, only match instruments on supplied MIC
     */
    void getExchInstsBySecurityGrp( ZString securityGrp, ExchangeCode mic, List<ExchangeInstrument> dest );

    /**
     * @param fxPair {baseCcy}{riskccy}
     * @param code
     * @return the FX instrument for the specified venue
     */
    FXInstrument getFXInstrument( FXPair fxPair, ExchangeCode code );

    /**
     * the symbol for a future is not unique and lookup requires scoping by the maturityDate
     *
     * @param symbol
     * @param maturityDateYYYYMM
     * @param securityExchange
     * @return
     */
    ExchDerivInstrument getFutureInstrumentBySym( FutureExchangeSymbol symbol, int maturityDateYYYYMM, ExchangeCode securityExchange );

    void getFuturesBySecurityGrp( FutureExchangeSymbol symbol, List<ExchangeInstrument> dest );

    /**
     * @param securityId
     * @param securityIDSource
     * @param exchangeCode
     * @return Instrument ... could be StrategyInstrument or ExchangeInstrument
     */
    Instrument getInst( ZString securityId, SecurityIDSource securityIDSource, ExchangeCode exchangeCode );

    /**
     * the symbol for a future is not unique and lookup requires scoping by the maturityDate
     *
     * @param symbol
     * @param maturityDateYYYYMM
     * @param securityExchange
     * @return
     */
    ExchDerivInstrument getOptionInstrumentBySym( ZString symbol, int maturityDateYYYYMM, double strikePrice, OptionType type, ExchangeCode securityExchange );

    /**
     * @param parentCompanyId
     * @return the requested ParentCompany or null if not found
     */
    ParentCompany getParentCompany( long parentCompanyId );

    /**
     * @param useUniversalTickScales if true then use the universal tick id lookup instead of the MIC scoped one
     */
    void setUseUniversalTickScales( boolean useUniversalTickScales );
}
