/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ViewString;
import com.rr.core.model.*;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.ListenerMktDataContextWrapper;
import com.rr.core.model.book.UnsafeL2Book;
import com.rr.core.thread.RunState;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class TestSimpleSubscriptionManager extends BaseTestCase {

    private static final class DummyBookSrc implements MktDataSrc<CMEBookAdapter> {

        Map<Instrument, CMEBookAdapter> _books = new HashMap<>();
        private transient RunState _runState = RunState.Unknown;

        public DummyBookSrc() {
            // for test
        }

        @Override public boolean canHandle()                                           { return true; }

        @Override public void handle( Event msg )                                      { /* */ }

        @Override public void handleNow( Event msg )                                   { /* */ }

        @Override public String getComponentId()                                       { return null; }

        @Override public CMEBookAdapter getItem( Instrument inst ) {
            CMEBookAdapter book = _books.get( inst );
            if ( book == null ) {
                book = new CMEBookAdapter( new UnsafeL2Book( inst, 0 ) );

                _books.put( inst, book );
            }

            return book;
        }

        @Override public boolean hasPipeLineId( String pipeLineId )                    { return false; }

        @Override public boolean supports( Instrument inst )                           { return true; }

        @Override public List<String> getPipeLineIds()                                 { return null; }

        @Override public RunState getRunState()                                        { return _runState; }

        @Override public RunState setRunState( final RunState newState )               { return _runState = newState; }

        @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) { /* nothing */ }

        @Override public void prepare()                                                { /* nothing */ }

        @Override public void startWork()                                              { /* nothing */ }

        @Override public void stopWork()                                               { /* nothing */ }

        @Override public void threadedInit()                                           { /* */ }
    }

    private static class Listener implements MktDataListener<CMEBookAdapter> {

        public List<CMEBookAdapter> _changes = new ArrayList<>();

        public Listener() {
            // for test
        }

        @Override
        public String id() {
            return null;
        }

        @Override
        public void marketDataChanged( CMEBookAdapter book ) {
            _changes.add( book );
        }

        @Override
        public void clearMktData() {
            _changes.clear();
        }
    }
    protected DummyInstrumentLocator _loc = new DummyInstrumentLocator();
    private @Mock   InstrumentSubscriptionListener  _myListener;
    private @Captor ArgumentCaptor<Set<Instrument>> _instSubscribeSet;
    private SingleMDSrcSubsMgr<CMEBookAdapter, BookContext> _mgr;
    private DummyBookSrc                                    _src;

    @Before
    public void setUp() throws Exception {
        _src = new DummyBookSrc();
        _mgr = new SingleMDSrcSubsMgr<>( "test", _src, () -> new BookContextImpl() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void testMultiSub() {
        ExchangeInstrument secDef1 = getInst( "SECA" );
        ExchangeInstrument secDef2 = getInst( "SECB" );
        ExchangeInstrument secDef3 = getInst( "SECC" );

        Listener l1 = new Listener();
        Listener l2 = new Listener();
        Listener l3 = new Listener();

        _mgr.subscribe( l1, secDef1, 0 );
        _mgr.subscribe( l1, secDef3, 0 );

        _mgr.subscribe( l2, secDef2, 0 );
        _mgr.subscribe( l2, secDef3, 0 );

        _mgr.subscribe( l3, secDef1, 0 );
        _mgr.subscribe( l3, secDef2, 0 );
        _mgr.subscribe( l3, secDef3, 0 );

        CMEBookAdapter[] l1Books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 2, l1Books.length );
        assertSame( _src._books.get( secDef1 ), l1Books[ 0 ] );
        assertSame( _src._books.get( secDef3 ), l1Books[ 1 ] );

        CMEBookAdapter[] l2Books = _mgr.getBookSet( l2 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 2, l2Books.length );
        assertSame( _src._books.get( secDef2 ), l2Books[ 0 ] );
        assertSame( _src._books.get( secDef3 ), l2Books[ 1 ] );

        CMEBookAdapter[] l3Books = _mgr.getBookSet( l3 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 3, l3Books.length );
        assertSame( _src._books.get( secDef1 ), l3Books[ 0 ] );
        assertSame( _src._books.get( secDef2 ), l3Books[ 1 ] );
        assertSame( _src._books.get( secDef3 ), l3Books[ 2 ] );

        _mgr.marketDataChanged( l3Books[ 0 ] );
        _mgr.marketDataChanged( l3Books[ 1 ] );
        _mgr.marketDataChanged( l3Books[ 2 ] );

        assertEquals( 2, l1._changes.size() );
        assertEquals( 2, l2._changes.size() );
        assertEquals( 3, l3._changes.size() );
        assertSame( l1Books[ 0 ], l1._changes.get( 0 ) );
        assertSame( l1Books[ 1 ], l1._changes.get( 1 ) );
        assertSame( l2Books[ 0 ], l2._changes.get( 0 ) );
        assertSame( l2Books[ 1 ], l2._changes.get( 1 ) );
        assertSame( l3Books[ 0 ], l3._changes.get( 0 ) );
        assertSame( l3Books[ 1 ], l3._changes.get( 1 ) );
        assertSame( l3Books[ 2 ], l3._changes.get( 2 ) );

        ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx;
        MktDataListener<ApiMutatableBook>[]                          foundListeners;

        ctx            = l3._changes.get( 0 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 2, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );
        assertSame( l3, foundListeners[ 1 ] );

        ctx            = l3._changes.get( 1 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 2, foundListeners.length );
        assertSame( l2, foundListeners[ 0 ] );
        assertSame( l3, foundListeners[ 1 ] );

        ctx            = l3._changes.get( 2 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 3, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );
        assertSame( l2, foundListeners[ 1 ] );
        assertSame( l3, foundListeners[ 2 ] );

        assertSame( l1._changes.get( 0 ).getContextWrapper(), l3._changes.get( 0 ).getContextWrapper() );
        assertSame( l2._changes.get( 0 ).getContextWrapper(), l3._changes.get( 1 ).getContextWrapper() );

        assertNotSame( l1._changes.get( 0 ).getContextWrapper(), l2._changes.get( 0 ).getContextWrapper() );
        assertNotSame( l2._changes.get( 0 ).getContextWrapper(), l3._changes.get( 0 ).getContextWrapper() );
    }

    @Test
    public void testOneBookOneSub() {
        ExchangeInstrument secDef1 = getInst( "SECA" );

        Listener l1 = new Listener();
        _mgr.subscribe( l1, secDef1, 0 );

        CMEBookAdapter[] books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 1, books.length );
        assertSame( _src._books.get( secDef1 ), books[ 0 ] );

        _mgr.marketDataChanged( books[ 0 ] );

        assertEquals( 1, l1._changes.size() );
        assertSame( books[ 0 ], l1._changes.get( 0 ) );

        final ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx = l1._changes.get( 0 ).getContextWrapper();

        MktDataListener<ApiMutatableBook>[] foundListeners = ctx.getListeners();

        assertEquals( 1, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );
    }

    @Test
    public void testSubscriptionNotification() {
        ExchangeInstrument secDef1 = getInst( "SECA" );

        _mgr.addSubscriptionListener( _myListener );

        Listener l1 = new Listener();
        _mgr.subscribe( l1, secDef1, 0 );

        Mockito.verify( _myListener ).changed( _instSubscribeSet.capture(), Mockito.eq( true ), Mockito.eq( 0L ) );
        assertTrue( _instSubscribeSet.getValue().contains( secDef1 ) );

        CMEBookAdapter[] books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 1, books.length );
        assertSame( _src._books.get( secDef1 ), books[ 0 ] );

        _mgr.marketDataChanged( books[ 0 ] );

        assertEquals( 1, l1._changes.size() );
        assertSame( books[ 0 ], l1._changes.get( 0 ) );

        final ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx = l1._changes.get( 0 ).getContextWrapper();

        MktDataListener<ApiMutatableBook>[] foundListeners = ctx.getListeners();

        assertEquals( 1, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void testThreeBookOneSub() {
        ExchangeInstrument secDef1 = getInst( "SECA" );
        ExchangeInstrument secDef2 = getInst( "SECB" );
        ExchangeInstrument secDef3 = getInst( "SECC" );

        Listener l1 = new Listener();
        _mgr.subscribe( l1, secDef1, 0 );
        _mgr.subscribe( l1, secDef2, 0 );
        _mgr.subscribe( l1, secDef3, 0 );

        CMEBookAdapter[] books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 3, books.length );
        assertSame( _src._books.get( secDef1 ), books[ 0 ] );
        assertSame( _src._books.get( secDef2 ), books[ 1 ] );
        assertSame( _src._books.get( secDef3 ), books[ 2 ] );

        _mgr.marketDataChanged( books[ 0 ] );
        _mgr.marketDataChanged( books[ 2 ] );

        assertEquals( 2, l1._changes.size() );
        assertSame( books[ 0 ], l1._changes.get( 0 ) );
        assertSame( books[ 2 ], l1._changes.get( 1 ) );

        ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx;
        MktDataListener<ApiMutatableBook>[]                          foundListeners;

        ctx            = l1._changes.get( 0 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 1, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );

        ctx            = l1._changes.get( 1 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 1, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );

        assertNotSame( l1._changes.get( 0 ).getContextWrapper(), l1._changes.get( 1 ).getContextWrapper() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void testThreeBookThreeSub() {
        ExchangeInstrument secDef1 = getInst( "SECA" );
        ExchangeInstrument secDef2 = getInst( "SECB" );
        ExchangeInstrument secDef3 = getInst( "SECC" );

        Listener l1 = new Listener();
        Listener l2 = new Listener();
        Listener l3 = new Listener();

        _mgr.subscribe( l1, secDef1, 0 );
        _mgr.subscribe( l2, secDef2, 0 );
        _mgr.subscribe( l3, secDef3, 0 );

        CMEBookAdapter[] l1Books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 1, l1Books.length );
        assertSame( _src._books.get( secDef1 ), l1Books[ 0 ] );

        CMEBookAdapter[] l2Books = _mgr.getBookSet( l2 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 1, l2Books.length );
        assertSame( _src._books.get( secDef2 ), l2Books[ 0 ] );

        CMEBookAdapter[] l3Books = _mgr.getBookSet( l3 ).toArray( new CMEBookAdapter[ 0 ] );
        assertEquals( 1, l3Books.length );
        assertSame( _src._books.get( secDef3 ), l3Books[ 0 ] );

        _mgr.marketDataChanged( l1Books[ 0 ] );
        _mgr.marketDataChanged( l3Books[ 0 ] );

        assertEquals( 1, l1._changes.size() );
        assertEquals( 0, l2._changes.size() );
        assertEquals( 1, l3._changes.size() );
        assertSame( l1Books[ 0 ], l1._changes.get( 0 ) );
        assertSame( l3Books[ 0 ], l3._changes.get( 0 ) );

        ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx;
        MktDataListener<ApiMutatableBook>[]                          foundListeners;

        ctx            = l1._changes.get( 0 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 1, foundListeners.length );
        assertSame( l1, foundListeners[ 0 ] );

        ctx            = l3._changes.get( 0 ).getContextWrapper();
        foundListeners = ctx.getListeners();
        assertEquals( 1, foundListeners.length );
        assertSame( l3, foundListeners[ 0 ] );

        assertNotSame( l1._changes.get( 0 ).getContextWrapper(), l3._changes.get( 0 ).getContextWrapper() );
    }

    @Test
    public void testUnSubscriptionNotification() {
        ExchangeInstrument secDef1 = getInst( "SECA" );

        _mgr.addSubscriptionListener( _myListener );

        Listener l1 = new Listener();
        _mgr.subscribe( l1, secDef1, 0 );

        Mockito.verify( _myListener ).changed( _instSubscribeSet.capture(), Mockito.eq( true ), Mockito.eq( 0L ) );

        CMEBookAdapter[] books = _mgr.getBookSet( l1 ).toArray( new CMEBookAdapter[ 0 ] );

        _mgr.marketDataChanged( books[ 0 ] );

        _mgr.unsubscribe( l1, secDef1 );

        Mockito.verify( _myListener ).changed( _instSubscribeSet.capture(), Mockito.eq( false ), Mockito.eq( 0L ) );
        assertTrue( _instSubscribeSet.getValue().contains( secDef1 ) );

        final ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> ctx = l1._changes.get( 0 ).getContextWrapper();

        MktDataListener<ApiMutatableBook>[] foundListeners = ctx.getListeners();

        assertEquals( 0, foundListeners.length );
    }

    protected ExchangeInstrument getInst( String securityDescription ) {
        return _loc.getExchInst( new ViewString( securityDescription.trim() ), SecurityIDSource.SecurityDesc, ExchangeCode.XCME );
    }
}
