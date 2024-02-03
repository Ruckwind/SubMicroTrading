/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.core.utils.lock.StampedLockProxy;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;

import java.util.List;
import java.util.Set;

/**
 * thread safe instrument store
 * <p>
 * provides spin locking for thread safety to a supplied instrument store
 *
 * @WARN Locking is NON re-entrant
 * <p>
 * will BLOCK on updates ... for intraday updates of instruments use more concurrent version
 */

public final class ThreadsafeInstrumentStore implements InstrumentStore {

    private final     OptimisticReadWriteLock _stampLock = new StampedLockProxy();
    private final     InstrumentStore         _store;
    private transient RunState                _runState  = RunState.Unknown;

    public ThreadsafeInstrumentStore( InstrumentStore store ) {
        _store = store;
    }

    @Override public boolean add( SecurityDefinitionImpl def ) {
        long stamp = grabWriteLock();
        try {
            return _store.add( def );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void add( final Instrument def ) {
        long stamp = grabWriteLock();
        try {
            _store.add( def );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public boolean allowIntradayAddition() {
        return true;
    }

    @Override
    public void remove( SecurityDefinitionImpl def ) {
        long stamp = grabWriteLock();
        try {
            _store.remove( def );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public boolean updateStatus( SecurityStatusImpl status ) {
        long stamp = grabWriteLock();
        try {
            return _store.updateStatus( status );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void getAllCommonInsts( final Set<CommonInstrument> instruments ) {
        long stamp = grabReadLock();
        try {
            _store.getAllCommonInsts( instruments );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getAllExchInsts( final Set<ExchangeInstrument> instruments ) {
        long stamp = grabReadLock();
        try {
            _store.getAllExchInsts( instruments );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getAllFXInsts( final Set<FXInstrument> instruments ) {
        long stamp = grabReadLock();
        try {
            _store.getAllFXInsts( instruments );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getAllStratInsts( final Set<StrategyInstrument> instruments ) {
        long stamp = grabReadLock();
        try {
            _store.getAllStratInsts( instruments );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public Instrument getByInstId( final ZString id ) {
        long stamp = grabWriteLock();
        try {
            return _store.getByInstId( id );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public CommonInstrument getCommonInstrument( final long commonInstrumentId, final Currency ccy ) {
        long stamp = grabWriteLock();
        try {
            return _store.getCommonInstrument( commonInstrumentId, ccy );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void getCommonInstruments( final long commonInstrumentId, final List<CommonInstrument> dest ) {
        long stamp = grabWriteLock();
        try {
            _store.getCommonInstruments( commonInstrumentId, dest );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public ExchangeInstrument getDummyExchInst() {
        return _store.getDummyExchInst();
    }

    @Override public ExchangeInstrument getExchInst( final ZString securityId, final SecurityIDSource securityIDSource, final ExchangeCode securityExchange ) {
        long stamp = grabReadLock();
        try {
            return _store.getExchInst( securityId, securityIDSource, securityExchange );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchangeInstrument getExchInstByExchangeLong( final ExchangeCode exchangeCode, final long instrumentId ) {
        long stamp = grabReadLock();
        try {
            return _store.getExchInstByExchangeLong( exchangeCode, instrumentId );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchangeInstrument getExchInstByIsin( final ZString isin, final ExchangeCode securityExchange, final Currency currency ) {
        long stamp = grabReadLock();
        try {
            return _store.getExchInstByIsin( isin, securityExchange, currency );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchangeInstrument getExchInstByUniqueCode( final SecurityIDSource src, final long instrumentId ) {
        long stamp = grabReadLock();
        try {
            return _store.getExchInstByUniqueCode( src, instrumentId );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchangeInstrument getExchInstByUniqueInstId( final long uniqueInstId ) {
        long stamp = grabWriteLock();
        try {
            return _store.getExchInstByUniqueInstId( uniqueInstId );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void getExchInsts( final Set<ExchangeInstrument> insts, final ZString securityId, final SecurityIDSource securityIDSource ) {
        long stamp = grabReadLock();
        try {
            _store.getExchInsts( insts, securityId, securityIDSource );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getExchInsts( Set<ExchangeInstrument> instruments, Exchange ex ) {
        long stamp = grabReadLock();
        try {
            _store.getExchInsts( instruments, ex );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getExchInstsBySecurityGrp( final ZString securityGrp, ExchangeCode mic, final List<ExchangeInstrument> dest ) {
        long stamp = grabReadLock();
        try {
            _store.getExchInstsBySecurityGrp( securityGrp, mic, dest );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public FXInstrument getFXInstrument( final FXPair fxPair, final ExchangeCode code ) {
        long stamp = grabReadLock();
        try {
            return _store.getFXInstrument( fxPair, code );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchDerivInstrument getFutureInstrumentBySym( final FutureExchangeSymbol symbol, final int maturityDateYYYYMMDD, final ExchangeCode securityExchange ) {
        long stamp = grabReadLock();
        try {
            return _store.getFutureInstrumentBySym( symbol, maturityDateYYYYMMDD, securityExchange );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public void getFuturesBySecurityGrp( final FutureExchangeSymbol symbol, final List<ExchangeInstrument> dest ) {
        long stamp = grabReadLock();
        try {
            _store.getFuturesBySecurityGrp( symbol, dest );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public Instrument getInst( final ZString securityId, final SecurityIDSource securityIDSource, final ExchangeCode exchangeCode ) {
        long stamp = grabReadLock();
        try {
            return _store.getInst( securityId, securityIDSource, exchangeCode );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ExchDerivInstrument getOptionInstrumentBySym( final ZString symbol, final int maturityDateYYYYMMDD, final double strikePrice, final OptionType type, final ExchangeCode securityExchange ) {
        long stamp = grabReadLock();
        try {
            return _store.getOptionInstrumentBySym( symbol, maturityDateYYYYMMDD, strikePrice, type, securityExchange );
        } finally {
            releaseReadLock( stamp );
        }
    }

    @Override public ParentCompany getParentCompany( final long parentCompanyId ) {
        long stamp = grabWriteLock();
        try {
            return _store.getParentCompany( parentCompanyId );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void setUseUniversalTickScales( final boolean useUniversalTickScales ) {
        long stamp = grabWriteLock();
        try {
            _store.setUseUniversalTickScales( useUniversalTickScales );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override
    public String getComponentId() {
        return _store.getComponentId(); // as this is a wrapper return id of the wrapped component, only one of which should be in comp. registry
    }

    @Override public RunState getRunState() { return _runState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase phase ) {
        long stamp = grabWriteLock();
        try {
            _store.init( ctx, phase );
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public void prepare() {
        long stamp = grabWriteLock();
        try {
            _store.prepare();
        } finally {
            releaseWriteLock( stamp );
        }
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    public InstrumentStore getLetter()                               { return _store; }

    private long grabReadLock() {
        return _stampLock.readLock();
    }

    private long grabWriteLock() {
        return _stampLock.writeLock();
    }

    private void releaseReadLock( long stamp ) {
        _stampLock.unlockRead( stamp );
    }

    private void releaseWriteLock( long stamp ) {
        _stampLock.unlockWrite( stamp );
    }
}
