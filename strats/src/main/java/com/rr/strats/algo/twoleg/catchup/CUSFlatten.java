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

public class CUSFlatten extends BaseMultiLegArbCatchupHandler {

    private static final Logger _log = LoggerFactory.create( CUSFlatten.class );

    public CUSFlatten( CatchUpStates catchupStates ) {
        super( "CUSFlatten", catchupStates );
    }

    @Override
    public void checkForAction( BaseStrategyExchTwoLegArb strat ) {
        final boolean legAopen   = strat.getLegAInst().isActiveOnMarket();
        final boolean legBopen   = strat.getLegBInst().isActiveOnMarket();
        final boolean spreadOpen = strat.getSpreadInst().isActiveOnMarket();

        // check did we fill all legs, if so set state to None

        if ( !legAopen && !legBopen && !spreadOpen ) {
            if ( strat.getLegAInst().isFlattened() && strat.getLegBInst().isFlattened() && strat.getSpreadInst().isFlattened() ) {
                // FLATTENED
                if ( strat.isTrace() ) {
                    _logMsg.copy( "CUSFlatten : " ).append( strat.id() ).append( " all legs flattened" );
                    _log.info( _logMsg );
                }
                
                strat.setCatchUpState( _states.getNone() );
            } else {
                // at least one leg unflattend, try flatten again
                _logMsg.copy( "CUSFlatten : " ).append( strat.id() ).append( " at least one leg not flattened, try again" );
                _log.info( _logMsg );

                strat.reflattenSlice();
            }
        }
    }
}
