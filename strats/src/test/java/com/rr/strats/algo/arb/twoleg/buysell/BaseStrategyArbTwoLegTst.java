/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg.buysell;

import com.rr.core.algo.base.StrategyDefinition;
import com.rr.core.algo.strats.Algo;
import com.rr.core.algo.strats.StratInstrumentStateWrapper;
import com.rr.core.lang.ViewString;
import com.rr.core.model.Book;
import com.rr.core.model.Instrument;
import com.rr.om.router.OrderRouter;
import com.rr.strats.algo.arb.BaseStrategyArbTst;
import com.rr.strats.algo.arb.twoleg.MultiLegArbCorrectiveHandler;
import com.rr.strats.algo.arb.twoleg.buysell.AlgoExchangeTwoLegBuySellArb;
import com.rr.strats.algo.arb.twoleg.buysell.StratExchSpreadBuyLeg1SellLeg2Arb;
import com.rr.strats.algo.twoleg.catchup.CatchUpStates;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public abstract class BaseStrategyArbTwoLegTst extends BaseStrategyArbTst<StratExchSpreadBuyLeg1SellLeg2Arb, AlgoExchangeTwoLegBuySellArb> {

    protected StratInstrumentStateWrapper<?> _leg1;
    protected StratInstrumentStateWrapper<?> _leg2;
    protected StratInstrumentStateWrapper<?> _spread;

    protected CatchUpStates _catchupStates;
    
    protected Instrument _instLeg1;
    protected Instrument _instLeg2;
    protected Instrument _instSpread;

    @Override
    protected AlgoExchangeTwoLegBuySellArb createAlgoInstance( String algoId ) {
        return new AlgoExchangeTwoLegBuySellArb( algoId );
    }

    protected void checkStratCatchupState( MultiLegArbCorrectiveHandler expState ) {
        assertSame( expState, _strat.getCatchUpState() ); 
    }
    
    @Override
    protected Class<? extends Algo<? extends Book>> getAlgoClass() {
        return AlgoExchangeTwoLegBuySellArb.class;
    }

    @Override
    protected void localStratSetup() {
        StratInstrumentStateWrapper<?>[] instStates = _strat.getInstState();
        
        for( int i=0 ; i < instStates.length ; i++ ) {
            StratInstrumentStateWrapper<?> cur = instStates[i];
            if ( cur.getInstrument().getNumLegs() > 0 ) {
                _spread = instStates[i];
                break;
            }
        }

        _instSpread = _spread.getInstrument();

        _instLeg1 = _spread.getInstrument().getLeg( 0 ).getInstrument();
        _instLeg2 = _spread.getInstrument().getLeg( 1 ).getInstrument();

        for( int k=0 ; k < instStates.length ; k++ ) {
            StratInstrumentStateWrapper<?> cur = instStates[k];
            
            if ( cur.getInstrument() == _instLeg1 ) {
                _leg1 = cur;
            }

            if ( cur.getInstrument() == _instLeg2 ) {
                _leg2 = cur;
            }
        }
        
        assertNotNull( _spread );
        assertNotNull( _leg1 );
        assertNotNull( _leg2 );
        
        _catchupStates = _strat.getCatchUpStates();
    }

    @Override
    public void createStrategyInstance( OrderRouter router, StrategyDefinition def, int bookLevels ) {
        _strat = new StratExchSpreadBuyLeg1SellLeg2Arb( "A1S1" );
        _strat.setAlgo( _algo );
        _strat.setHubSession( _hubHandler );
        _strat.setStrategyDefinition( def );
        _strat.setOrderRouter( router );
        _strat.setAccount( new ViewString("LSMA") );
        _strat.setBookLevels( bookLevels );
        _strat.setMaxTimeCatchUpMS( Integer.MAX_VALUE ); // for debugging
        
        _strat.setTrace( true );
    }

    @Override
    protected StratInstrumentStateWrapper<? extends Book> getWrapper( Instrument instrument ) {
        if ( instrument == _instLeg1 )      return _leg1;
        if ( instrument == _instLeg2 )      return _leg2;
        if ( instrument == _instSpread )    return _spread;
        return null;
    }
}
