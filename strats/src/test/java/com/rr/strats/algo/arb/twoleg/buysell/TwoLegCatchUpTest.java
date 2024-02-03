/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg.buysell;

import java.util.List;

import com.rr.core.model.Message;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.TimeInForce;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CalArb catchup test
 * 
 * Buy Synthetic Spread
 *      make order for BUY  leg1   at bestAsk
 *      make order for SELL leg2   at bestBid
 *      
 * Sell spread
 *      make order for SELL spread at bestBid
 */
public class TwoLegCatchUpTest extends BaseStrategyArbTwoLegTst {

    @Override
    public void setUp() {
        _arbThresh = "0.5";
        
        super.setUp();
    }
    
    /**
     * test
     */
    @Test public void testWhenFullyFilledStateBackToNone() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.5, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        checkStratCatchupState( _catchupStates.getInitialOpen() );

        // first three hub events are the orders sent to exchange 
        //                      idx,    expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 3,      0,       1,          1002,       0 );

        // buy spread, sell synth
        NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5, 3.0,    Side.Sell,  TimeInForce.ImmediateOrCancel );
        NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );

        // synth price to buy = leg1NOSPx - leg2NOSPx
        double spread1SynthPriceBuy = getSynthSpreadPriceFromLegOrders( legOneNOS, legTwoNOS );
        double expProfit = (spreadNOS.getPrice() - spread1SynthPriceBuy) * spreadNOS.getOrderQty();
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort, pnl, catchUpQty 
        verifyInstState( _spread,    3.0,    4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );
        verifyStratState( 0 );

        clearQueues();
        
        sendFill( _spread, spreadNOS, 5, 3.0 );
        sendFill( _leg1,   legOneNOS, 5, 9976.0 );
        sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
        _strat.doWorkUnit();

        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );
        
        //                inst,   bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,    3.0,    4.0,  0,         0,          0,          5, 0, 0 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          5,          0, 0, 0 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
        verifyStratState( expProfit );

        // HUB will have the 3 fills as well
        //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 3,   expProfit,       1,         1002,       2 );
    }

    public double getSynthSpreadPriceFromLegOrders( NewOrderSingle legOneNOS, NewOrderSingle legTwoNOS ) {
        double spread1SynthPriceBuy = legOneNOS.getPrice() - legTwoNOS.getPrice();
        return spread1SynthPriceBuy;
    }

    @Test public void testCancelledWithOneIOCOpenStaysInOpenState() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.5, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        checkStratCatchupState( _catchupStates.getInitialOpen() );

        // first three hub events are the orders sent to exchange 
        //                      idx,    expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 3,      0,       1,          1002,       0 );

        // buy spread, sell synth
        NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5,    3.0, Side.Sell,  TimeInForce.ImmediateOrCancel );
        NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );

        double spread1SynthPriceBuy = getSynthSpreadPriceFromLegOrders( legOneNOS, legTwoNOS );
        double expProfit = (spreadNOS.getPrice() - spread1SynthPriceBuy) * spreadNOS.getOrderQty();
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort, 
        verifyInstState( _spread,    3.0,    4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );
        verifyStratState( 0 );

        clearQueues();
        
        sendFill( _spread, spreadNOS, 5, 3.0 );
        sendCancelled( _spread, spreadNOS, 5 );
        _strat.doWorkUnit();
        
        // no action as strat waits for replies to open orders
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        clearQueues();

        sendFill( _leg1,   legOneNOS, 5, 9976.0 );
        sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
        _strat.doWorkUnit();

        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );
        
        //                inst,   bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,    3.0,    4.0,  0,         0,          0,          5, 0, 0 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          5,          0, 0, 0 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
        verifyStratState( expProfit );

        // HUB will have the 3 fills as well
        //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 2,   expProfit,       1,         1002,       2 );
    }

    @Test public void testResubmitIOCWhenArbStillExists() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.5, 4.0, 11 );
        _strat.doWorkUnit();

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update

        // buy spread, sell synth
        NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5, 3.0, Side.Sell,  TimeInForce.ImmediateOrCancel );
        NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );

        double spread1SynthPriceBuy = getSynthSpreadPriceFromLegOrders( legOneNOS, legTwoNOS );
        double expProfit = (spreadNOS.getPrice() - spread1SynthPriceBuy) * spreadNOS.getOrderQty();
        
        clearQueues();
        
        sendFill( _spread, spreadNOS, 5, 3.0 );
        sendCancelled( _spread, spreadNOS, 5 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() ); // no action as strat waits for replies to open orders
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        clearQueues();

        sendCancelled( _leg1,   legOneNOS, 0 );
        sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
        _strat.doWorkUnit();

        // all open orders terminal .. should generate new IOC as market data still shows arb op
        // stay in initialOpen waiting new IOC order
        
        assertEquals( 1, events.size() );       
        legOneNOS = verifyNOS( 0, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        
        //                inst,   bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,    3.0,    4.0,  0,         0,          0,          5, 0, 0 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5,         0,          0,          0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
        verifyStratState( 0 );

        // HUB will have the fill + cancelled + 1 IOC as well
        //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 3,     0,       1,         1002,       2 );

        clearQueues();
        sendFill( _leg1,   legOneNOS, 5, 9976.0 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );

        verifyInstState( _spread,    3.0,    4.0,  0,         0,          0,          5, 0, 0 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          5,          0, 0, 0 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
        verifyStratState( expProfit );

        // HUB will have the tradeNew + status
        //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 1,     expProfit,       1,         1002,       1 );
    }

    @Test public void testCancelledAfterAllFilledDoesNothing() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.5, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        checkStratCatchupState( _catchupStates.getInitialOpen() );

        // first three hub events are the orders sent to exchange 
        //                      idx,    expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 3,      0,       1,          1002,       0 );

        // buy spread, sell synth
        NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5,    3.0, Side.Sell,  TimeInForce.ImmediateOrCancel );
        NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );

        double spread1SynthPriceBuy = getSynthSpreadPriceFromLegOrders( legOneNOS, legTwoNOS );
        double expProfit = (spreadNOS.getPrice() - spread1SynthPriceBuy) * spreadNOS.getOrderQty();
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort, 
        verifyInstState( _spread,    3.0,    4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );
        verifyStratState( 0 );

        clearQueues();
        
        sendFill( _spread, spreadNOS, 5, 3.0 );
        sendCancelled( _spread, spreadNOS, 5 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        clearQueues();

        sendFill( _leg1,   legOneNOS, 5, 9976.0 );
        sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
        _strat.doWorkUnit();

        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );
        
        //                inst,   bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,    3.0,    4.0,  0,         0,          0,          5, 0, 0 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          5,          0, 0, 0 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
        verifyStratState( expProfit );

        // HUB will have the 3 fills as well
        //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
        checkStratStateHubEvent( 2,   expProfit,       1,         1002,       2 );
        clearQueues();
        
        sendCancelled( _leg1, legOneNOS, 5 );
        sendCancelled( _leg2, legTwoNOS, 5 );
        _strat.doWorkUnit();

        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );
        assertEquals( 0, _hubHandler.getEvents().size() );
    }

    @Test public void testSubmitLimitWhenArbDoesntExist() {
        List<Message> events = _downstreamHandler.getEvents();

            mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
            mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
            mdSnapshotBBO( _instSpread,    10,    1.5,    4.0, 11 );
            _strat.doWorkUnit();
    
            mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
            _strat.doWorkUnit();
            assertEquals( 3, events.size() );       // orders triggered by update
    
            // buy spread, sell synth
            NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5, 3.0,    Side.Sell,  TimeInForce.ImmediateOrCancel );
            NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
            NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
    
            double spread1SynthPriceBuy = getSynthSpreadPriceFromLegOrders( legOneNOS, legTwoNOS );
            double expProfit = (spreadNOS.getPrice() - spread1SynthPriceBuy) * spreadNOS.getOrderQty();
        
        clearQueues();
        
            sendFill( _spread, spreadNOS, 5, 3.0 );
            sendCancelled( _spread, spreadNOS, 5 );
            _strat.doWorkUnit();
            assertEquals( 0, events.size() ); // no action as strat waits for replies to open orders
            checkStratCatchupState( _catchupStates.getInitialOpen() );
            clearQueues();
    
            sendCancelled( _leg1,   legOneNOS, 0 );
            sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
            _strat.doWorkUnit();
    
            // all open orders terminal .. should generate new IOC as market data still shows arb op
            // stay in initialOpen waiting new IOC order
            
            assertEquals( 1, events.size() );       
            legOneNOS = verifyNOS( 0, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
            checkStratCatchupState( _catchupStates.getInitialOpen() );

        clearQueues();

            mdDeltaTopOfBook( _instSpread, 8,  1.5, MDEntryType.Bid);  // no arb op
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            // note the change in book didnt get updated to the stratInstState struct as strat active no recalc performed
            verifyInstState( _spread, 3.0, 4.0,  0,         0,          0,          5, 0, 0 );
        
        clearQueues();

            sendCancelled( _leg1,   legOneNOS, 0 );
            _strat.doWorkUnit();
            assertEquals( 1, events.size() );       
            legOneNOS = verifyNOS( 0, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.Day);  // DAY LIMIT ORDER
            checkStratCatchupState( _catchupStates.getLimitOrderCatchup() );
            verifyInstState( _spread, 1.5, 4.0,  0,         0,          0,          5, 0, 0 );

        clearQueues();
            
            sendFill( _leg1,   legOneNOS, 5, 9976.0 );
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            checkStratCatchupState( _catchupStates.getNone() );
    
            verifyInstState( _spread,    1.5,    4.0,  0,         0,          0,          5, 0, 0 );
            verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          5,          0, 0, 0 );
            verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          0,          5, 0, 0 );
            verifyStratState( expProfit );
    
            // HUB will have the tradeNew + status
            //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
            checkStratStateHubEvent( 1,     expProfit,       1,         1003,       1 );
    }

    @Test public void testFlatten() {
        List<Message> events = _downstreamHandler.getEvents();

            mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
            mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
            mdSnapshotBBO( _instSpread,    10,    1.5,    4.0,  11 );
            _strat.doWorkUnit();
    
            mdDeltaTopOfBook( _instSpread, 8, 3.0, MDEntryType.Bid );  // new spread bid will trigger order
            _strat.doWorkUnit();
            assertEquals( 3, events.size() );       // orders triggered by update
    
            // buy spread, sell synth
            NewOrderSingle spreadNOS = verifyNOS( 0, _instSpread, 5,    3.0, Side.Sell,  TimeInForce.ImmediateOrCancel );
            NewOrderSingle legOneNOS = verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
            NewOrderSingle legTwoNOS = verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
    
        clearQueues();
        
            sendFill( _spread, spreadNOS, 5, 3.0 );
            sendCancelled( _spread, spreadNOS, 5 );
            _strat.doWorkUnit();
            assertEquals( 0, events.size() ); // no action as strat waits for replies to open orders
            checkStratCatchupState( _catchupStates.getInitialOpen() );
            clearQueues();
    
            sendCancelled( _leg1,   legOneNOS, 0 );
            sendFill( _leg2,   legTwoNOS, 5, 9973.5 );
            _strat.doWorkUnit();
    
            // all open orders terminal .. should generate new IOC as market data still shows arb op
            // stay in initialOpen waiting new IOC order
            
            assertEquals( 1, events.size() );       
            legOneNOS = verifyNOS( 0, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
            checkStratCatchupState( _catchupStates.getInitialOpen() );

        clearQueues();

            mdDeltaTopOfBook( _instSpread, 8,  1.5, MDEntryType.Bid);  // no arb op
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            // note the change in book didnt get updated to the stratInstState struct as strat active no recalc performed
            verifyInstState( _spread, 3.0, 4.0,  0,         0,          0,          5, 0, 0 );
        
        clearQueues();

            sendCancelled( _leg1,   legOneNOS, 0 );
            _strat.doWorkUnit();
            assertEquals( 1, events.size() );       
            legOneNOS = verifyNOS( 0, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.Day );  // DAY LIMIT ORDER
            checkStratCatchupState( _catchupStates.getLimitOrderCatchup() );
            verifyInstState( _spread, 1.5, 4.0,  0,         0,          0,          5, 0, 0 );

        clearQueues();

            mdDeltaTopOfBook( _instSpread, 8,  5.0, MDEntryType.Offer );  // no arb op
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            // note the change in book didnt get updated to the stratInstState struct as strat active no recalc performed
            verifyInstState( _spread, 1.5, 4.0,  0,         0,          0,          5, 0, 0 );
        
        clearQueues();

            _strat.setMaxTimeCatchUpMS( 100 ); 
            pause( 1000 );
            _strat.doWorkUnit();
            checkStratCatchupState( _catchupStates.getCancellingOpen() );
            assertEquals( 1, events.size() );
            verifyCanReq( 0, _instLeg1, Side.Buy,  legOneNOS.getClOrdId() ); 

        clearQueues();
        
            sendCancelled( _leg1,   legOneNOS, 0 );
            _strat.doWorkUnit();
            checkStratCatchupState( _catchupStates.getFlatten() );

            assertEquals( 2, events.size() );       
            legTwoNOS = verifyNOS( 0, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel ); // BUY to get out position
            spreadNOS = verifyNOS( 1, _instSpread, 5, 5.0,   Side.Buy,  TimeInForce.ImmediateOrCancel ); // BUY to get out position

            //                inst,    bidPx, askPx, openLong, openShort, executedLong, executedShort
            verifyInstState( _spread,    1.5,   5.0,  5,         0,          0,          5, 0, 5 );
            verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          0,          0, 0, 0 );
            verifyInstState( _leg2,   9973.5, 9974.0,  5,         0,          0,          5, 0, 5 );
        
        clearQueues();
        
            sendFill( _spread, spreadNOS, 5, 5.0 );
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            checkStratCatchupState( _catchupStates.getFlatten() );

        clearQueues();
            sendFill( _leg2,   legTwoNOS, 5, 9974.0 );
            _strat.doWorkUnit();
            assertEquals( 0, events.size() );       
            checkStratCatchupState( _catchupStates.getNone() );
    
            //                inst,   bidPx, askPx, openLong, openShort, executedLong, executedShort, unwindPnl, unfilled
            verifyInstState( _spread,    1.5,  5.0,    0,         0,          5,          5,             -10,         0 );
            verifyInstState( _leg1,   9975.5, 9976.0,  0,         0,          0,          0,               0,         0 );
            verifyInstState( _leg2,   9973.5, 9974.0,  0,         0,          5,          5,              -2.5,       0 );

            // profit = cost to backout leg2 + cost to backout spread
            // leg2    (9973.5 * 5) - (9974.0 * 5) = -2.5
            // spread  (3.0 * 5) - (5.0 * 5) = -10
            // total profit = -12.5

            double expProfit = -12.5;
            
            verifyStratState( expProfit );
    
            // HUB will have the tradeNew + status
            //                       idx, expPnl, algoSeqNum, tickId, lastEventInst
            checkStratStateHubEvent( 1,     expProfit,       1,         1004,       2 );
    }

    private void pause( int ms ) {
        try {
            Thread.sleep( ms ); // sleep past the 100ms threshold for the limit order
        } catch( InterruptedException e ) {
            // nothing
        }
    }
}
