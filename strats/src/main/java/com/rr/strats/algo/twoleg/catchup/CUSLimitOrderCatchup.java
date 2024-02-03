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
 */

public class CUSLimitOrderCatchup extends BaseMultiLegArbCatchupHandler {
    private static final Logger       _log = LoggerFactory.create( CUSLimitOrderCatchup.class );

    public CUSLimitOrderCatchup( CatchUpStates catchupStates ) {
        super( "CUSLimitOrderCatchup", catchupStates );
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
                _logMsg.copy( "CUSLimitOrderCatchup : " ).append( strat.id() ).append( " all legs filled" );
                _log.info( _logMsg );
            }
            
            strat.setCatchUpState( _states.getNone() );
            
            return;
        }

        // passive state exit is time based from the strategy doWorkMethod (post phase)
    }

}
