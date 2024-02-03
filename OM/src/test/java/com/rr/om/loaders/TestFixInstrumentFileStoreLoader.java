package com.rr.om.loaders;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.ExceptionTrace;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.utils.*;
import com.rr.inst.ConcurrentInstrumentSecDefStore;
import com.rr.inst.InstrumentSecurityDefWrapper;
import com.rr.inst.InstrumentStore;
import com.rr.inst.ThreadsafeInstrumentStore;
import com.rr.model.generated.fix.codec.CMEMDDecoder;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.BaseOMTestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.*;

public class TestFixInstrumentFileStoreLoader extends BaseOMTestCase {

    private final static Logger _log = LoggerFactory.create( TestFixInstrumentFileStoreLoader.class );

    /**
     * @throws FileException
     * @TODO fix CME multileg loading and use of sec def using security description in symbol
     */
    @Ignore
    @Test
    public void testConcurrent() throws SMTException {
        int numReaders      = 2;
        int numWriters      = 1;
        int readIterations  = 5;
        int writeIterations = 5;

        doTestConcurrent( numReaders, numWriters, readIterations, writeIterations );
    }

    protected void doTestConcurrent( int numReaders, int numWriters, int readIterations, int writeIterations ) throws SMTException {
        FixInstrumentFileStoreLoader loader = new FixInstrumentFileStoreLoader();

        loader.setOverrrideSecDefType( SecDefSpecialType.CMEFuture );

        loader.setDecoder( new CMEMDDecoder( "CMEDeocder" ) );

        ReflectUtils.setProperty( loader, "type", "concurrent" );
        ReflectUtils.setProperty( loader, "file", "./data/cme/secdef.autocert.dat" );

        final SMTComponent c = loader.create( "testId" );

        assertTrue( c instanceof InstrumentStore );

        InstrumentStore store = (InstrumentStore) c;

        ExchangeInstrument inst3 = store.getExchInst( new ViewString( "0GEU4" ), SecurityIDSource.SecurityDesc, ExchangeCode.XCBT );
        assertNotNull( inst3 );

        ExchangeInstrument inst = store.getExchInst( new ViewString( "27069" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCEC );

        assertNotNull( inst );

        CyclicBarrier cbStart = new CyclicBarrier( numReaders + numWriters );
        List<Thread>  threads = new ArrayList<>();

        for ( int i = 0; i < numReaders; i++ ) {
            final int idx = i;

            Thread t = new Thread( () -> reader( idx, cbStart, store, readIterations ), "READER" + idx );
            t.start();

            threads.add( t );
        }

        for ( int i = 0; i < numWriters; i++ ) {
            final int idx = i;

            Thread t = new Thread( () -> writer( idx, cbStart, store, writeIterations ), "WRITER" + idx );
            t.start();

            threads.add( t );
        }

        for ( int i = 0; i < threads.size(); i++ ) {
            try {
                threads.get( i ).join();
            } catch( InterruptedException e ) {
                // ignore
            }
            _log.info( "Thread " + i + " interrupted/completed" );
        }

        _log.info( "done" );

        synchronized( store ) {
            // mem barrier
        }

        if ( store instanceof ConcurrentInstrumentSecDefStore ) ((ConcurrentInstrumentSecDefStore) store).logStats();

        if ( store instanceof ConcurrentInstrumentSecDefStore ) ((ConcurrentInstrumentSecDefStore) store).assertIndicesSameSize();

        if ( store instanceof ThreadsafeInstrumentStore ) ((ConcurrentInstrumentSecDefStore) ((ThreadsafeInstrumentStore) store).getLetter()).logStats();

        if ( store instanceof ThreadsafeInstrumentStore ) ((ConcurrentInstrumentSecDefStore) ((ThreadsafeInstrumentStore) store).getLetter()).assertIndicesSameSize();
    }

    private int doWriteNewInst( final int writerIdx, final InstrumentStore store, final ReusableString tmpKey, final int idx, int instIdx, final ExchangeInstrument inst ) {
        final ExchangeCode exCode = inst.getExchange().getExchangeCode();

        if ( inst instanceof ExchDerivInstrument && ((ExchDerivInstrument) inst).getNumLegs() > 0 ) {
            return instIdx;
        }

        ++instIdx;

        tmpKey.reset();
        tmpKey.copy( inst.getExchangeSymbol() );

        InstrumentSecurityDefWrapper instByExSym = (InstrumentSecurityDefWrapper) store.getExchInst( tmpKey, SecurityIDSource.ExchangeSymbol, exCode );

        if ( instByExSym == null ) {
            fail( "Failed to read inst " + tmpKey + " on " + exCode );
        }

        SecurityDefinitionImpl copy = new SecurityDefinitionImpl();
        copy.shallowCopyFrom( instByExSym.getSecDef() );

        copy.setSecurityExchange( exCode );

        setKey( copy.getSymbolForUpdate(), writerIdx, idx, instIdx );
        setKey( copy.getSecurityIDForUpdate(), writerIdx, idx, instIdx );
        setKey( copy.getSecurityDescForUpdate(), writerIdx, idx, instIdx );

        SecurityAltIDImpl altId    = (SecurityAltIDImpl) copy.getSecurityAltIDs();
        SecurityAltIDImpl newAltId = null;

        while( altId != null ) {
            if ( newAltId == null ) {
                newAltId = new SecurityAltIDImpl();
            } else {
                newAltId.setNext( new SecurityAltIDImpl() );
                newAltId = newAltId.getNext();
            }

            newAltId.setSecurityAltIDSource( altId.getSecurityAltIDSource() );
            newAltId.getSecurityAltIDForUpdate().copy( altId.getSecurityAltID() );

            altId = altId.getNext();
        }

        copy.setSecurityAltIDs( newAltId );

        store.add( copy );

        return instIdx;
    }

    private void reader( final int readerIdx, final CyclicBarrier cbStart, final InstrumentStore store, final int iterations ) {
        try {
            cbStart.await();
        } catch( Exception e ) {
            // ignore
        }

        _log.info( "Reader " + readerIdx + " starting" );

        final Set<ExchangeInstrument> insts  = new LinkedHashSet<>();
        final ReusableString          tmpKey = new ReusableString();

        for ( int idx = 0; idx < iterations; idx++ ) {

            insts.clear();

            store.getAllExchInsts( insts );

            synchronized( this ) {
                // force mem barriers
            }

            for ( ExchangeInstrument inst : insts ) {
                final ExchangeCode exCode = inst.getExchange().getExchangeCode();
                if ( !Utils.isNull( inst.getExchangeLongId() ) ) {
                    ExchangeInstrument i2 = store.getExchInstByExchangeLong( exCode, inst.getExchangeLongId() );
                    assertSame( inst, i2 );
                }
                tmpKey.reset();
                tmpKey.copy( inst.getExchangeSymbol() );

                ExchangeInstrument instByExSym = store.getExchInst( tmpKey, SecurityIDSource.ExchangeSymbol, exCode );

                if ( instByExSym != inst ) {
                    _log.info( "Orig " + inst );
                    _log.info( "New  " + instByExSym );

                    fail( "Failed to read same inst by Exchange symbol " + tmpKey + " on " + exCode );
                }

                if ( inst.getSecurityType() == SecurityType.Future ) {
                    tmpKey.reset();
                    tmpKey.copy( inst.getSymbol() );

                    ExchDerivInstrument edi = (ExchDerivInstrument) inst;

                    ExchDerivInstrument instBySym = store.getFutureInstrumentBySym( FutureExchangeSymbol.getVal( tmpKey ), edi.getMaturityMonthYear(), exCode );

                    if ( instBySym != inst ) {
                        _log.info( "Orig " + inst );
                        _log.info( "New  " + instBySym );

                        fail( "Failed to read same inst by Symbol " + tmpKey + " on " + exCode );
                    }
                } else if ( inst.getSecurityType() == SecurityType.Option ) {
                    tmpKey.reset();
                    tmpKey.copy( inst.getSymbol() );

                    ExchDerivInstrument edi = (ExchDerivInstrument) inst;

                    ExchDerivInstrument instBySym = store.getOptionInstrumentBySym( tmpKey, edi.getMaturityMonthYear(), edi.getStrikePrice(), edi.getOptionType(), exCode );

                    if ( instBySym != inst ) {
                        _log.info( "Orig " + inst );
                        _log.info( "New  " + instBySym );

                        fail( "Failed to read same inst by Symbol " + tmpKey + " on " + exCode );
                    }
                }
            }

            Thread.yield();
        }
        _log.info( "Reader " + readerIdx + " completed" );
    }

    private void setKey( final ReusableString key, final int writerId, final int newSubId, final int instIdx ) {
        int subIdx = key.lastIndexOf( '#' );
        if ( subIdx >= 0 ) {
            key.copy( key.toString().substring( 0, subIdx ) );
        }
        key.append( "#" ).append( writerId ).append( "/" ).append( newSubId ).append( "/" ).append( instIdx );
    }

    private void writer( final int writerIdx, final CyclicBarrier cbStart, final InstrumentStore store, final int iterations ) {
        try {
            cbStart.await();
        } catch( Exception e ) {
            // ignore
        }

        _log.info( "Writer " + writerIdx + " starting" );

        final Set<ExchangeInstrument> insts  = new LinkedHashSet<>();
        final ReusableString          tmpKey = new ReusableString();

        int max = 0;

        try {
            for ( int idx = 0; idx < iterations; idx++ ) {

                insts.clear();

                store.getAllExchInsts( insts );

                if ( max == 0 ) max = insts.size();

                int instIdx = 0;

                for ( ExchangeInstrument inst : insts ) {
                    instIdx = doWriteNewInst( writerIdx, store, tmpKey, idx, instIdx, inst );

                    if ( instIdx > max ) break;
                }

                ThreadUtilsFactory.get().sleep( 1 );
            }
        } catch( Exception e ) {
            e.printStackTrace();
            final ReusableString stackTrace = new ReusableString();
            ExceptionTrace.getStackTrace( stackTrace, e );
            _log.info( stackTrace );
            fail( e.getMessage() );
        }
        _log.info( "Writer " + writerIdx + " completed" );
    }
}
