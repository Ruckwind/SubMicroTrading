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

import com.rr.core.algo.strats.StratInstrumentStateWrapper;
import com.rr.core.model.Message;
import com.rr.md.book.MutableFixBook;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.TimeInForce;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * BUY leg1 SELL leg2
 * 
    Synthetic spread bid = leg1Bid - leg2Ask;

    a) synth spread BID should represent the best bid price, which is what you have to pay if you want to cross spread and sell now
              leg1Bid price to sell leg1 now
              leg2Ask price to buy leg2 now
    
              crosses spread to SELL the synthetic spread (sell leg1, buy leg2), BUY exchange spread

    Synthetic spread ask = leg1Ask - leg2Bid;

    b) synth spread ASK should represent the lowest offer price, which is what you have to pay if you want to cross spread and buy now
              leg1Ask price to buy leg1 now
              leg2Bid price to sell leg2 now
    
              crosses spread to BUY the synthetic spread (buy leg1, sell leg2), SELL exchange spread
 */

public class TwoLegSimpleTest extends BaseStrategyArbTwoLegTst {

    @Override
    public void setUp() {
        _arbThresh = "0.5";
        
        super.setUp();
    }
    
    @Test public void testInitialSnapshotSellSpread() {
        
        List<Message> events = _downstreamHandler.getEvents();

        long baseTime = System.nanoTime();
        
        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        setBookNanoTime( _leg1, baseTime );
        setBookNanoTime( _leg2, baseTime+100 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  3.0, 4.0, 11 );
        setBookNanoTime( _spread, baseTime+500 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 3.0,    Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,    3.0,    4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );
    }

    private void setBookNanoTime( StratInstrumentStateWrapper<?> instw, long timeNanos ) {
        MutableFixBook book = (MutableFixBook) instw.getBook();
        
        book.setLastTickInNanos( timeNanos );
    }

    /**
     * Synthetic spread bid = leg1Bid - leg2Ask;
    
         synth spread BID should represent the best bid price, which is what you have to pay if you want to cross spread and sell now
         
                  leg1Bid price to sell leg1 now
                  leg2Ask price to buy leg2 now
        
                  crosses spread to SELL the synthetic spread (sell leg1, buy leg2), BUY exchange spread
     */
    @Test public void testInitialSnapshotBuySpread() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  1.75, 0.5, 11 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5,    0.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 9975.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,   1.75,    0.5,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testSingleDeltaSellSpread() {
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.5, 4.0, 11 );

        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 3.0,    Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 3.0, 4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );

        // now inject fills and check state updated
    }

    @Test public void testBuyOrderByQtyL1L2Spread() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 101 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  1.75, 0.5, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg1,   5, 9975.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instSpread, 5,    0.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,   1.75,    0.5,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtyL2L1Spread() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     101, 9975.5, 9976.0, 210 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 100 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  1.75, 0.5, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 9975.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instSpread, 5,    0.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,   1.75,    0.5,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtyL2SpreadL1() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     310, 9975.5, 9976.0, 100 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 101 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  1.75, 0.5, 300 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instSpread, 5,    0.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg1,   5, 9975.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,   1.75,    0.5,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  5, 0, 0, 0, 0, 5 );
    }

    @Test public void testBuyOrderByQtySpreadL2L1() {
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     101, 9975.5, 9976.0, 210 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 100 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update

        mdSnapshotBBO( _instSpread,   10,  1.75, 0.5, 10 );
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // order triggered by update
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5,    0.5, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg2,   5, 9974.0, Side.Buy,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg1,   5, 9975.5, Side.Sell, TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread,   1.75,    0.5,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  5, 0, 0, 0, 0, 5 );
    }
    
    @Test public void testNoActionWhenMinQtyNotAvailOnSpread() {
        
        List<Message> events = _downstreamHandler.getEvents();

        _strat.setMinOrderQty( 12 );
        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.0, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
    }

    @Test public void testNoActionWhenMinQtyNotAvailOnLeg1() {
        
        List<Message> events = _downstreamHandler.getEvents();

        _strat.setMinOrderQty( 5 );
        mdSnapshotBBO( _instLeg1,     3, 9975.5, 9976.0, 4 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.0, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
    }

    @Test public void testNoActionWhenMinQtyNotAvailOnLeg2() {
        
        List<Message> events = _downstreamHandler.getEvents();

        _strat.setMinOrderQty( 5 );
        mdSnapshotBBO( _instLeg1,     13, 9975.5, 9976.0, 14 );
        mdSnapshotBBO( _instLeg2,     3, 9973.5, 9974.0, 4 );
        mdSnapshotBBO( _instSpread,   10,  1.0, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       
    }

    @Test public void testNoActionWhenAlreadyWorking() {
        
        List<Message> events = _downstreamHandler.getEvents();

        mdSnapshotBBO( _instLeg1,     100, 9975.5, 9976.0, 110 );
        mdSnapshotBBO( _instLeg2,     200, 9973.5, 9974.0, 210 );
        mdSnapshotBBO( _instSpread,   10,  1.0, 4.0, 11 );
        _strat.doWorkUnit();
        assertEquals( 0, events.size() );       // order triggered by update
        checkStratCatchupState( _catchupStates.getNone() );

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  // new spread bid will trigger order
        _strat.doWorkUnit();
        assertEquals( 3, events.size() );       // orders triggered by update
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        
        // buy spread, sell synth
        verifyNOS( 0, _instSpread, 5, 3.0, Side.Sell,  TimeInForce.ImmediateOrCancel );
        verifyNOS( 1, _instLeg1,   5, 9976.0, Side.Buy,   TimeInForce.ImmediateOrCancel );
        verifyNOS( 2, _instLeg2,   5, 9973.5, Side.Sell,  TimeInForce.ImmediateOrCancel );
        
        // verifyInststate :  inst,  bidPx, askPx, openLong, openShort, executedLong, executedShort
        verifyInstState( _spread, 3.0, 4.0,  0, 5, 0, 0, 0, 5 );
        verifyInstState( _leg1,   9975.5, 9976.0,  5, 0, 0, 0, 0, 5 );
        verifyInstState( _leg2,   9973.5, 9974.0,  0, 5, 0, 0, 0, 5 );

        clearQueues();
        
        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  
        _strat.doWorkUnit();
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        assertEquals( 0, events.size() );       // no update as no arb

        mdDeltaTopOfBook( _instSpread, 8,  3.0, MDEntryType.Bid);  
        _strat.doWorkUnit();
        checkStratCatchupState( _catchupStates.getInitialOpen() );
        assertEquals( 0, events.size() );       // no update as working
    }
}
