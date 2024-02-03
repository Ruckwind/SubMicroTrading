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
 * StateNone - normal state, no catchup, not behind at present
 */

public class CUSNone extends BaseMultiLegArbCatchupHandler {

    private static final Logger       _log = LoggerFactory.create( CUSNone.class );

    public CUSNone( CatchUpStates catchupStates ) {
        super( "CUSNone", catchupStates );
    }

    @Override
    public void checkForAction( BaseStrategyExchTwoLegArb strat ) {
        // this can happen eg when cancelling limit orders and sending flatten order
        _logMsg.copy( "Market termination event received but state is out of market : UNEXPECTED for strat " ).append( strat.id() );
        _log.warn( _logMsg );
    }
}
