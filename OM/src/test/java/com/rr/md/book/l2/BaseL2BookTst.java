/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.lang.Constants;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.model.book.DoubleSidedBookEntryImpl;
import com.rr.core.model.book.UnsafeL2Book;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.inst.InstTestUtils;
import com.rr.inst.InstrumentStore;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.MDSnapEntryImpl;
import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;
import com.rr.om.BaseOMTestCase;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.model.instrument.InstrumentWrite;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class BaseL2BookTst extends BaseOMTestCase {

    protected static InstrumentWrite _inst = new DummyInstrumentLocator().getExchInst( new ViewString( "BT.TST" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.UNKNOWN );
    private final Map<Instrument, AtomicInteger> _secToSeqNumMap     = new HashMap<>( 16, 0.75f );
    private final Map<Instrument, AtomicInteger> _secToBookSeqNumMap = new HashMap<>( 16, 0.75f );
    protected CMEBookAdapter   _book;
    protected AllEventRecycler _entryRecycler = TLC.instance().getInstanceOf( AllEventRecycler.class );
    protected InstrumentStore  _instrumentLocator;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        _instrumentLocator = InstTestUtils.createCMEAlgoInstStore( getInstFile(), null );

        _inst.setCurrency( Currency.EUR );

        ApiMutatableBook base = new UnsafeL2Book( _inst, 5 );

        _book = new CMEBookAdapter( base );
    }

    protected void add( MDIncRefreshImpl event, int lvl, MDUpdateAction action, int qty, double px, MDEntryType type, int bookSeqNum ) {
        addMDEntry( null, event, lvl, action, qty, px, type, bookSeqNum );
    }

    protected void addMDEntry( MDSnapshotFullRefreshImpl event, int lvl, int qty, double px, MDEntryType type ) {
        MDSnapEntryImpl entry = makeEntry( event, lvl, qty, px, type );
        MDSnapEntryImpl root  = (MDSnapEntryImpl) event.getMDEntries();

        if ( root == null ) {
            event.setMDEntries( entry );
        } else {
            while( root.getNext() != null ) {
                root = root.getNext();
            }

            root.setNext( entry );
        }

        event.setNoMDEntries( event.getNoMDEntries() + 1 );
    }

    protected void addMDEntry( Instrument inst, MDIncRefreshImpl event, int lvl, MDUpdateAction action, int qty, double px, MDEntryType type ) {
        addMDEntry( inst.getExchangeSymbol(), event, lvl, action, qty, px, type, nextBookSeqNum( inst ) );
    }

    protected void addMDEntry( ZString secId, MDIncRefreshImpl event, int lvl, MDUpdateAction action, int qty, double px, MDEntryType type, int bookSeqNum ) {

        ZString localInstId = (secId == null) ? TLC.safeCopy( "0" ) : TLC.safeCopy( secId );

        MDEntryImpl entry = makeEntry( secId, event, lvl, action, qty, px, type, bookSeqNum );
        MDEntryImpl root  = (MDEntryImpl) event.getMDEntries();

        if ( root == null ) {
            event.setMDEntries( entry );
        } else {
            while( root.getNext() != null ) {
                root = root.getNext();
            }

            root.setNext( entry );
        }

        event.setNoMDEntries( event.getNoMDEntries() + 1 );
    }

    protected void applyEvent( CMEBookAdapter book, MDIncRefreshImpl event ) {

        MDEntryImpl next = (MDEntryImpl) event.getMDEntries();

        while( next != null ) {
            book.applyIncrementalEntry( event.getMsgSeqNum(), next );

            next = next.getNext();
        }
    }

    protected void applyEvent( CMEBookAdapter book, MDSnapshotFullRefreshImpl event ) {

        book.applySnapshot( event, _entryRecycler );
    }

    protected List<Instrument> createInsts( final String instList, final InstrumentLocator instrumentLocator ) {
        List<Instrument> insts = new ArrayList<>();

        String[] instSecDesc = instList.split( "," );

        for ( String secDes : instSecDesc ) {
            Instrument inst = instrumentLocator.getExchInst( new ViewString( secDes.trim() ), SecurityIDSource.SecurityDesc, ExchangeCode.XCME );

            if ( inst == null ) {
                throw new SMTRuntimeException( "Failed to locate instrument [" + secDes + "]" );
            }

            insts.add( inst );
        }

        return insts;
    }

    protected MDIncRefreshImpl getBaseEvent( int seqNum ) {
        MDIncRefreshImpl event = new MDIncRefreshImpl();
        event.setNoMDEntries( 0 );
        event.setMsgSeqNum( seqNum );
        return event;
    }

    protected MDIncRefreshImpl getBaseEvent( Instrument inst ) {
        MDIncRefreshImpl event = new MDIncRefreshImpl();
        event.setNoMDEntries( 0 );
        event.setMsgSeqNum( nextSeqNum( inst ) );
        return event;
    }

    protected String getInstFile() {
        return "./data/cme/algo_secdef.dat";
    }

    protected MDSnapshotFullRefreshImpl getSnapEvent( int seqNum, int repSet, ZString securityId ) {
        MDSnapshotFullRefreshImpl event = getSnapEvent( seqNum, repSet );
        event.getSecurityIDForUpdate().copy( securityId );
        event.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        return event;
    }

    protected MDSnapshotFullRefreshImpl getSnapEvent( Instrument inst ) {
        int seqNum = nextSeqNum( inst );
        int repSet = nextBookSeqNum( inst );

        MDSnapshotFullRefreshImpl event = getSnapEvent( seqNum, repSet );
        event.getSecurityIDForUpdate().copy( inst.getExchangeSymbol() );
        event.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        event.setNoMDEntries( 0 );
        return event;
    }

    protected MDSnapshotFullRefreshImpl getSnapEvent( int seqNum, int repSet ) {
        MDSnapshotFullRefreshImpl event = new MDSnapshotFullRefreshImpl();
        event.setNoMDEntries( 0 );
        event.setMsgSeqNum( seqNum );
        event.setRptSeq( repSet );
        return event;
    }

    protected MDSnapEntryImpl makeEntry( MDSnapshotFullRefreshImpl event, int lvl, int qty, double px, MDEntryType type ) {
        MDSnapEntryImpl entry = new MDSnapEntryImpl();
        entry.setMdEntryPx( px );
        entry.setMdEntrySize( qty );
        entry.setMdPriceLevel( lvl + 1 );
        entry.setMdEntryType( type );
        return entry;
    }

    protected MDEntryImpl makeEntry( ZString securityId, MDIncRefreshImpl event, int lvl, MDUpdateAction action, int qty, double px, MDEntryType type, int bookSeqNum ) {
        MDEntryImpl entry = makeEntry( event, lvl, action, qty, px, type, bookSeqNum );

        entry.getSecurityIDForUpdate().copy( securityId );
        entry.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );

        return entry;
    }

    protected MDEntryImpl makeEntry( MDIncRefreshImpl event, int lvl, MDUpdateAction action, int qty, double px, MDEntryType type, int bookSeqNum ) {
        MDEntryImpl entry = new MDEntryImpl();
        entry.setMdEntryPx( px );
        entry.setMdEntrySize( qty );
        entry.setMdUpdateAction( action );
        entry.setMdPriceLevel( lvl + 1 );
        entry.setMdEntryType( type );
        entry.setRepeatSeq( bookSeqNum );
        return entry;
    }

    protected int nextBookSeqNum( Instrument inst ) {
        AtomicInteger ia = _secToBookSeqNumMap.get( inst );

        if ( ia == null ) {
            ia = new AtomicInteger( 1000 );

            _secToBookSeqNumMap.put( inst, ia );
        }

        return ia.incrementAndGet();
    }

    protected int nextSeqNum( Instrument inst ) {
        AtomicInteger ia = _secToSeqNumMap.get( inst );

        if ( ia == null ) {
            ia = new AtomicInteger( 100 );

            _secToSeqNumMap.put( inst, ia );
        }

        return ia.incrementAndGet();
    }

    protected void verify( Book verBook, double[][] results ) {
        UnsafeL2Book snappedBook = new UnsafeL2Book( _inst, 10 );
        verBook.snapTo( snappedBook );

        int numLevels = results.length;

        assertTrue( verBook.getActiveLevels() >= numLevels );

        DoubleSidedBookEntry entry = new DoubleSidedBookEntryImpl();

        for ( int l = 0; l < numLevels; l++ ) {
            boolean ok = snappedBook.getLevel( l, entry );

            assertTrue( ok );
            assertTrue( results[ l ].length == 4 );

            assertEquals( results[ l ][ 0 ], entry.getBidQty(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 1 ], entry.getBidPx(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 2 ], entry.getAskQty(), Constants.TICK_WEIGHT );
            assertEquals( results[ l ][ 3 ], entry.getAskPx(), Constants.TICK_WEIGHT );
        }
    }

}
