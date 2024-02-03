/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb;

import com.rr.algo.t1.DummyExchangeSession;
import com.rr.core.algo.base.StrategyDefinition;
import com.rr.core.algo.base.StrategyDefinitionImpl;
import com.rr.core.algo.mgr.StrategyManager;
import com.rr.core.algo.mgr.StrategyManagerImpl;
import com.rr.core.algo.strats.*;
import com.rr.core.algo.strats.Strategy.StratBookAdapter;
import com.rr.core.dispatch.DirectDispatcherNonThreadSafe;
import com.rr.core.dispatch.MessageDispatcher;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;
import com.rr.core.model.Book;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Instrument;
import com.rr.core.model.Message;
import com.rr.core.utils.Utils;
import com.rr.md.book.BookSourceManager;
import com.rr.md.book.l2.BaseL2BookTst;
import com.rr.md.book.l2.L2BookDispatchAdapter;
import com.rr.md.book.l2.L2LongIdBookFactory;
import com.rr.md.us.cme.CMEBookAdapter;
import com.rr.md.us.cme.CMEMarketDataController;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.*;
import com.rr.model.internal.type.ExecType;
import com.rr.om.router.OrderRouter;
import com.rr.om.router.SingleDestRouter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public abstract class BaseStrategyArbTst<T extends BaseL2BookStrategy, V extends BaseL2BookAlgo> extends BaseL2BookTst {

    protected DummyExchangeSession _downstreamHandler;
    protected DummyExchangeSession _hubHandler;
    protected T                    _strat;
    protected V                    _algo;
              StrategyManager      _mgr;
            
    protected CMEMarketDataController   _ctlr;
    
    private   int                       _nextExecId;
    protected String                    _arbThresh = "1";

    @Override
    public void setUp() {
        super.setUp();
        
        _downstreamHandler = new DummyExchangeSession();
        _hubHandler        = new DummyExchangeSession();
        
        _downstreamHandler.setChainSession( _hubHandler );
        
        String algoId   = "A1";
        String stratId  = "A1S1";
        String pipeLine = "P1";
        
        _algo = createAlgoInstance( algoId );
        _mgr = new StrategyManagerImpl( stratId );
        
        OrderRouter router = new SingleDestRouter( _downstreamHandler );

        List<Instrument> insts = createInsts( "ESH4-ESM4,ESH4,ESM4" ); // leg1=Sell, leg2=Buy
        Map<String, String> props = makeStratProps();
        
        StrategyDefinition def = new StrategyDefinitionImpl( stratId, algoId, pipeLine, getAlgoClass(), insts, props );
        int bookLevels = 3;
        
        createStrategyInstance( router, def, bookLevels );
        
        _algo.registerStrategy( _strat );
        _mgr.registerStrategy( _strat );

        // inboundDispatcher used to pass src market data events to controller
        MessageDispatcher inboundDispatcher = new DirectDispatcherNonThreadSafe();
        L2LongIdBookFactory<CMEBookAdapter> bookFactory = new L2LongIdBookFactory<CMEBookAdapter>( CMEBookAdapter.class, true, _instrumentLocator, bookLevels );

        // book dispatcher allows async handoff of book change notification
        MessageDispatcher bookEventDispatcher = new DirectDispatcherNonThreadSafe();

        bookEventDispatcher.setHandler( new EventHandler() {

            @Override public String id() {
                return "bookHandler";
            }

            @Override
            public void handle( Message event ) {
                handleNow( event );
            }

            @Override public void handleNow( Message event ) {
                Book book = (Book) event;
                
                StratInstrumentStateWrapper<? extends Book> instWrapper = getWrapper(book.getInstrument());
                
                assertNotNull( instWrapper ); 
                
                if ( instWrapper != null ) {
                    @SuppressWarnings( "unchecked" )
                    StratBookAdapter<Book> bh = (StratBookAdapter<Book>) instWrapper.getBookHandler();
                    bh.changed( book );
                }
            }

            @Override public void       threadedInit()  { /* nothing */ }
            @Override public boolean    canHandle()     { return true; }
        });
        
        L2BookDispatchAdapter<CMEBookAdapter> bookListener = new L2BookDispatchAdapter<CMEBookAdapter>( bookEventDispatcher );

        // market data controller, applies market data events to books generating change events to registered listener
        _ctlr = new CMEMarketDataController( "TestController", "2", inboundDispatcher, bookFactory, bookListener, _instrumentLocator, false );
        _ctlr.setPipeIdList( "P1" );

        StratContext ctx = new StratContext( "StratTestContext" );
        BookSourceManager<CMEBookAdapter> bsm = new BookSourceManager<CMEBookAdapter>( "BSM" );
        bsm.setMdSessionBuilder( new DummyMarketDataChannelBuilder() );
        bsm.add( _ctlr );
        ctx.setBookSrcMgr( bsm );

        _algo.init( ctx );

        _ctlr.threadedInit();
        _strat.threadedInit();
        
        localStratSetup();
        
        _strat.startWork();
    }

    protected abstract Class<? extends Algo<? extends Book>> getAlgoClass();

    protected abstract V createAlgoInstance( String algoId );

    protected abstract void localStratSetup();

    public abstract void createStrategyInstance( OrderRouter router, StrategyDefinition def, int bookLevels );

    protected abstract StratInstrumentStateWrapper<? extends Book> getWrapper( Instrument instrument );

    private Map<String, String> makeStratProps() {
        Map<String, String> props = new LinkedHashMap<String, String>();
        
        props.put( "arbThresh",         _arbThresh );
        props.put( "trace",             "true" );
        props.put( "maxOrderQty",       "5" );
        props.put( "tradingAllowed",    "true" );
        
        return props;
    }

    protected NewOrderSingleImpl verifyNOS( int eventIdx, Instrument inst, int qty, double px, Side side, TimeInForce tif ) {
        List<Message> events = _downstreamHandler.getEvents();

        NewOrderSingleImpl nos = (NewOrderSingleImpl) events.get( eventIdx );
        assertSame( inst, nos.getInstrument() );
        assertEquals( side, nos.getSide() );
        assertEquals( qty,  nos.getOrderQty() );
        assertEquals( px,   nos.getPrice(), Constants.TICK_WEIGHT );

        assertSame( tif, nos.getTimeInForce() );
        
        return nos;
    }

    protected RecoveryCancelRequestImpl verifyCanReq( int eventIdx, Instrument inst, Side side, ZString nosClOrdId ) {
        List<Message> events = _downstreamHandler.getEvents();

        RecoveryCancelRequestImpl cxlReq = (RecoveryCancelRequestImpl) events.get( eventIdx );
        assertEquals( side, cxlReq.getSide() );
        assertEquals( nosClOrdId, cxlReq.getOrigClOrdId() );

        assertSame( inst, cxlReq.getInstrument() );
        
        return cxlReq;
    }

    protected void verifyInstState( StratInstrumentStateWrapper<?> istate, double bidPx, double askPx, int openLongQty, int openShortQty ) {
        StratInstrumentStateImpl i = istate.getStratInstState();
        
        assertEquals( bidPx, i.getBidPx(), Constants.TICK_WEIGHT );
        assertEquals( askPx, i.getAskPx(), Constants.TICK_WEIGHT );
        
        assertEquals( openLongQty, i.getTotLongContractsOpen() );
        assertEquals( openShortQty, i.getTotShortContractsOpen() );

        assertEquals( 0, i.getTotLongContractsExecuted() );
        assertEquals( 0, i.getTotShortContractsExecuted() );
    }

    protected void verifyInstState( StratInstrumentStateWrapper<?>  istate, 
                                    double                          bidPx, 
                                    double                          askPx, 
                                    int                             openLongQty, 
                                    int                             openShortQty, 
                                    int                             longExecuted, 
                                    int                             shortExecuted, 
                                    double                          legPnl,
                                    int                             catchupQty ) {
        StratInstrumentStateImpl i = istate.getStratInstState();

        assertEquals( catchupQty, istate.getSliceUnfilledQty() );
        assertEquals( bidPx, i.getBidPx(), Constants.TICK_WEIGHT );
        assertEquals( askPx, i.getAskPx(), Constants.TICK_WEIGHT );
        
        assertEquals( openLongQty, i.getTotLongContractsOpen() );
        assertEquals( openShortQty, i.getTotShortContractsOpen() );

        assertEquals( longExecuted, i.getTotLongContractsExecuted() );
        assertEquals( shortExecuted, i.getTotShortContractsExecuted() );
        
        if ( Utils.hasValue( legPnl ) ) {
            assertEquals( legPnl, i.getUnwindPnl(), Constants.TICK_WEIGHT );
        }
    }

    protected void mdSnapshotBBO( Instrument inst, int bidQty, double bidPx, double askPx, int askQty ) {
        MDSnapshotFullRefreshImpl snapEventL1 = getSnapEvent( inst );
        addMDEntry( snapEventL1, 0, bidQty, bidPx, MDEntryType.Bid );
        addMDEntry( snapEventL1, 0, askQty, askPx, MDEntryType.Offer );

        _ctlr.handle( snapEventL1 );              // dispatch market data event to MarketDataController
    }

    protected void mdDeltaTopOfBook( Instrument inst, int qty, double px, MDEntryType entryType ) {
        MDIncRefreshImpl incEvent = getBaseEvent( inst );
        addMDEntry( inst, incEvent, 0, MDUpdateAction.Overlay, qty,  px, entryType );
        _ctlr.handle( incEvent );
    }

    protected void deltaBBO( Instrument inst, int bidQty, double bidPx, double askPx, int askQty ) {
        MDIncRefreshImpl incEvent = getBaseEvent( inst );
        addMDEntry( inst, incEvent, 0, MDUpdateAction.Overlay, bidQty, bidPx, MDEntryType.Bid );
        addMDEntry( inst, incEvent, 0, MDUpdateAction.Overlay, askQty, askPx, MDEntryType.Offer );
        _ctlr.handle( incEvent );
    }

    @Override
    protected String getInstFile() {
        return "./data/cme/algo_secdef.dat";
    }

    protected void clearQueues() {
        _downstreamHandler.getEvents().clear();
        _hubHandler.getEvents().clear();
    }

    protected void sendFill( StratInstrumentStateWrapper<?> wrapper, NewOrderSingle nos, int qty, double px ) {
        sendFill( wrapper, nos, qty, px, qty, px );
    }
    
    protected void sendFill( StratInstrumentStateWrapper<?> wrapper, NewOrderSingle nos, int qty, double px, int cumQty, double avePx ) {
        TradeNewImpl trade = new TradeNewImpl();
        
        trade.setCumQty( cumQty );
        trade.setAvgPx( avePx );
        trade.setLastPx( px );
        trade.setLastQty( qty );
        trade.setSide( nos.getSide() );
        
        trade.getExecIdForUpdate().copy( "EX-" + nos.getClOrdId() + "-" + (++_nextExecId) );
        trade.getClOrdIdForUpdate().copy( nos.getClOrdId() );
        trade.getSymbolForUpdate().copy( nos.getInstrument().getExchangeSymbol() );
        trade.getSecurityDescForUpdate().copy( nos.getInstrument().getSecurityDesc() );
        
        int leavesQty = nos.getOrderQty() - qty;
        
        trade.setExecType( ExecType.Trade );        
        
        if ( nos.getTimeInForce() == TimeInForce.ImmediateOrCancel || nos.getTimeInForce() == TimeInForce.FillOrKill ) {
            trade.setOrdStatus( leavesQty > 0 ? OrdStatus.Canceled : OrdStatus.Filled );
        } else {
            trade.setOrdStatus( leavesQty > 0 ? OrdStatus.PartiallyFilled : OrdStatus.Filled );
        }
        
        wrapper.getExchangeHandler().handle( trade );
    }

    protected void sendCancelled( StratInstrumentStateWrapper<?> wrapper, NewOrderSingle nos, int cumQty ) {
        RecoveryCancelledImpl trade = new RecoveryCancelledImpl();
        
        trade.setLeavesQty( 0 );
        trade.setCumQty( cumQty );
        trade.setSide( nos.getSide() );
        
        trade.getExecIdForUpdate().copy( "EX-" + nos.getClOrdId() + "-" + (++_nextExecId) );
        trade.getClOrdIdForUpdate().copy( nos.getClOrdId() );
        trade.getSymbolForUpdate().copy( nos.getInstrument().getExchangeSymbol() );
        
        trade.setExecType( ExecType.Canceled );        
        
        trade.setOrdStatus( OrdStatus.Canceled );
        
        wrapper.getExchangeHandler().handle( trade );
    }
    
    protected void verifyStratState( double expProfit ) {
        assertEquals( expProfit, _strat.getStrategyState().getPnl(), Constants.TICK_WEIGHT );
    }

    protected void checkStratStateHubEvent( int idx, double expPnl, int algoSeqNum, long tickId, int lastEventInst ) {
        List<Message> hubEvents = _hubHandler.getEvents();
        assertTrue( hubEvents.size() > idx );       
        
        StrategyStateImpl si = (StrategyStateImpl) hubEvents.get( idx );
        
        assertEquals( expPnl, si.getPnl(), Constants.TICK_WEIGHT );
        assertEquals( algoSeqNum, si.getAlgoEventSeqNum() );
        assertEquals( tickId, si.getLastTickId() );
        assertEquals( lastEventInst, si.getLastEventInst() );
    }
}
