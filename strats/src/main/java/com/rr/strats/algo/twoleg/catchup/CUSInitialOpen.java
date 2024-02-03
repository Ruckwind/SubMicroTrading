/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.twoleg.catchup;

import com.rr.core.log.Logger;
import com.rr.core.log.LoggerFactory;
import com.rr.strats.algo.arb.twoleg.BaseStrategyExchTwoLegArb;

/**
 * stateless handlers for catching up strategy when alll legs didnt fill
 * 
 * StateInitialOpen - IOC orders active in market
 * 
 * invoked means, terminal order event occured  
 *
 * if all legs filled return
 * 
 * if still legs open return and wait for remaining terminal events
 * 
 * if all closed then AND arb op still exists, resubmit catchup IOCs stay in this state
 * 
 * if all closed AND arb doesnt exist send limit orders and change state to limitOrderCatchup
 */

public class CUSInitialOpen extends BaseMultiLegArbCatchupHandler {

    private static final Logger       _log = LoggerFactory.create( CUSInitialOpen.class );
    
    public CUSInitialOpen( CatchUpStates catchupStates ) {
        super( "CUSInitialOpen", catchupStates );
    }

    @Override
    public void checkForAction( BaseStrategyExchTwoLegArb strat ) {
        final int legACatchup   = strat.getLegAInst().getSliceUnfilledQty();
        final int legBCatchup   = strat.getLegBInst().getSliceUnfilledQty();
        final int spreadCatchup = strat.getSpreadInst().getSliceUnfilledQty();

        // check did we fill all legs, if so set state to None

        if ( legACatchup == 0 && legBCatchup == 0 && spreadCatchup == 0 ) {
            // SUCCESS ! filled all legs
            if ( strat.isTrace() ) {
                _logMsg.copy( "CUSInitialOpen : " ).append( strat.id() ).append( " all legs filled" );
                _log.info( _logMsg );
            }
            
            strat.setCatchUpState( _states.getNone() );
            
            return;
        }
        
        final boolean legAOpen   = strat.getLegAInst().isActiveOnMarket();
        final boolean legBOpen   = strat.getLegBInst().isActiveOnMarket();
        final boolean spreadOpen = strat.getSpreadInst().isActiveOnMarket();
        
        if ( legAOpen || legBOpen || spreadOpen ) {
            // still waiting for cancelled on 1 or more legs
            if ( strat.isTrace() ) {
                _logMsg.copy( "CUSInitialOpen : " ).append( strat.id() ).append( " legStillOpen wait for expected terminal events" )
                       .append( ", legAOpen=" ).append( legAOpen )
                       .append( ", legBOpen=" ).append( legBOpen )
                       .append( ", spreadOpen=" ).append( spreadOpen );
                
                _log.info( _logMsg );
            }

            return; // need wait for more exchange events
        }

        // all legs closed
        
        if ( strat.tryCatchUpIfArbStillExists() ) {
            // orders sent to market to try catch arb
            if ( strat.isTrace() ) {
                _logMsg.copy( "CUSInitialOpen : " ).append( strat.id() ).append( " orders sent to market to try catch arb which is still open" );
                _log.info( _logMsg );
            }

            return;
        }
        
        if ( strat.isTrace() ) {
            _logMsg.copy( "CUSInitialOpen : " ).append( strat.id() ).append( " arb doesnt exist atm, send passive orders" );
            _log.info( _logMsg );
        }
        
        strat.sendPassiveLimitOrdersToCatchup();
        
        strat.setCatchUpState( _states.getLimitOrderCatchup() );
    }
}
