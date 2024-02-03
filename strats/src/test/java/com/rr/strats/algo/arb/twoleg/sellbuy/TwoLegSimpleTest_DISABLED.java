/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg.sellbuy;

import java.util.List;

import org.junit.Ignore;

import com.rr.core.model.Message;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.TimeInForce;
import org.junit.Test;

import static org.junit.Assert.*;

// DISABLED UNTIL SELL/BUY STRAT LOGIC CONFIRMED

@Ignore
public class TwoLegSimpleTest_DISABLED extends BaseStrategyArbTwoLegTst {

    @Test public void testInitialSnapshotBuySpread() {
        
        /**
         * Sell Synthetic Spread
         *      make order for BUY  leg1   at bestAsk
         *      make order for SELL leg2   at bestBid
         * Buy spread
         *      make order for SELL spread at bestBid
         */
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 152.5, 153.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 210 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  406.0, 408.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 408.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 153.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 252.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 406.0, 408.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 153.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  0, 5, 0, 0, 0, 5 );
    }

    @Test public void testInitialSnapshotSellSpread() {
        
        /**
         * Sell synthetic spread
         *      make order for SELL  leg1 at bestBid
         *      make order for BUY   leg2 at bestAsk
         * Buy spread
         *      make order for Buy spread at bestAsk
         */
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 152.5, 155.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 210 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  409.5, 410.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 409.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 155.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 252.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 409.5, 410.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  0, 5, 0, 0, 0, 5 );
    }

    @Test public void testSingleDeltaSellSpread() {
        
        /**
         * Sell synthetic spread
         *      make order for SELL  leg1 at bestBid
         *      make order for BUY   leg2 at bestAsk
         * Buy spread
         *      make order for Buy spread at bestAsk
         */
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 152.5, 155.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 210 );
        mdSnapshotBBO( _instSpread,   10,  407.5, 410.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdDeltaTopOfBook( _instSpread, 8,  409.5, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 409.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 155.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 252.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 409.5, 410.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  0, 5, 0, 0, 0, 5 );

        // now inject fills and check state updated
    }

    @Test public void testBuyOrderByQtyL1L2Spread() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 152.5, 155.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 101 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  407.5, 408.0, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg1,   5, 152.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg2,   5, 257.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instSpread, 5, 408.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 407.5, 408.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtyL2L1Spread() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     101, 152.5, 155.0, 210 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 100 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  407.5, 408.0, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg2,   5, 257.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 152.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instSpread, 5, 408.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 407.5, 408.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtyL2SpreadL1() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     310, 152.5, 155.0, 100 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 101 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  407.5, 408.0, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg2,   5, 257.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instSpread, 5, 408.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg1,   5, 152.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 407.5, 408.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtySpreadL2L1() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     101, 152.5, 155.0, 210 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 100 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  407.5, 408.0, 10 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 408.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg2,   5, 257.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg1,   5, 152.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 407.5, 408.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  5, 0, 0, 0, 0, 5 );
    }
    
    @Test public void testNoActionWhenAlreadyWorking() {
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 152.5, 155.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 252.5, 257.5, 210 );
        mdSnapshotBBO( _instSpread,   10,  407.5, 410.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  409.5, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 409.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 155.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 252.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 409.5, 410.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   152.5, 155.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   252.5, 257.5,  0, 5, 0, 0, 0, 5 );

        clearQueues();
        
        mdDeltaTopOfBook( _instSpread, 8,  407.5, MDEntryType.Bid);  
        _strat.doWorkUnit();
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        assertEquals( 0, events.size() );       // no update as no arb

        mdDeltaTopOfBook( _instSpread, 8,  409.5, MDEntryType.Bid);  
        _strat.doWorkUnit();
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        assertEquals( 0, events.size() );       // no update as working
    }
}
