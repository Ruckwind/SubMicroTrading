/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.algo.t1;

import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Book;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.inst.InstrumentStore;
import com.rr.md.book.l2.BaseL2BookTst;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2BookFactory;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEMktDataController;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;
import com.rr.model.generated.internal.type.Side;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class T1AlgoTest extends BaseL2BookTst {

    protected CMEMktDataController _ctlr;
    T1Algo _t1;
    private DummyExchangeSession _downstreamHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        _downstreamHandler = new DummyExchangeSession();

        OrderRouter router = new SingleDestRouter( _downstreamHandler );

        _t1 = new T1Algo( 2, router );

        // inboundDispatcher used to pass src market data events to controller
        EventDispatcher               inboundDispatcher = new DirectDispatcherNonThreadSafe();
        InstrumentStore               instStore         = new DummyInstrumentLocator();
        L2BookFactory<CMEBookAdapter> bookFactory       = new L2BookFactory<>( CMEBookAdapter.class, false, instStore, 10 );

        // book dispatcher allows async handoff of book change notification
        EventDispatcher bookEventDispatcher = new DirectDispatcherNonThreadSafe();

        bookEventDispatcher.setHandler( new EventHandler() {

            @Override public boolean canHandle() { return true; }

            @Override
            public void handle( Event event ) {
                handleNow( event );
            }

            @Override public void handleNow( Event event ) {
                _t1.changed( (Book) event );
            }

            @Override public String getComponentId() { return null; }

            @Override public void threadedInit() { /* nothing */ }
        } );

        L2BookDispatchAdapter<CMEBookAdapter> bookListener = new L2BookDispatchAdapter<>( bookEventDispatcher );

        // market data controller, applies market data events to books generating change events to registered listener
        _ctlr = new CMEMktDataController( "TestController", "2", inboundDispatcher, bookFactory, bookListener, instStore, false );
        _ctlr.threadedInit();
        _ctlr.setOverrideSubscribeSet( true );
    }

    @Test
    public void testSingleUpdate() {
        ZString secId = new ReusableString().copy( 12345 );

        List<Event> events = _downstreamHandler.getEvents();

        MDSnapshotFullRefreshImpl snapEvent = getSnapEvent( 10000, 999, secId );
        addMDEntry( snapEvent, 0, 100, 15.10, MDEntryType.Bid );
        addMDEntry( snapEvent, 1, 200, 15.07, MDEntryType.Bid );
        addMDEntry( snapEvent, 2, 300, 15.05, MDEntryType.Bid );
        addMDEntry( snapEvent, 0, 110, 15.2, MDEntryType.Offer );
        addMDEntry( snapEvent, 1, 210, 15.25, MDEntryType.Offer );
        addMDEntry( snapEvent, 2, 310, 14.3, MDEntryType.Offer );

        _ctlr.handle( snapEvent );              // dispatch market data event to MarketDataController

        assertEquals( 0, events.size() );       // mod is 2 so first event wont trigger order

        MDIncRefreshImpl incEvent = getBaseEvent( 10001 );
        addMDEntry( secId, incEvent, 0, MDUpdateAction.Change, 90, 15.175, MDEntryType.Offer, 1000 );

        _ctlr.handle( incEvent );
        assertEquals( 1, events.size() );       // order triggered by update

        NewOrderSingleImpl nos = (NewOrderSingleImpl) events.get( 0 );
        assertEquals( 90, nos.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 15.175, nos.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( Side.Buy, nos.getSide() );
    }
}
