/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg.buysell;

import com.rr.model.generated.internal.type.Side;
import com.rr.strats.algo.arb.twoleg.BaseStrategyExchTwoLegArb;

/**
 * 
    Synthetic spread bid = leg1Bid - leg2Ask;

    a) synth spread SELL should represent the price you have to pay if you want to cross spread and sell now
              leg1Bid price to sell leg1 now
              leg2Ask price to buy leg2 now
    
              crosses spread to SELL the synthetic spread (sell leg1, buy leg2), BUY exchange spread

    Synthetic spread ask = leg1Ask - leg2Bid;

    b) synth spread BUY should represent the price you have to pay if you want to cross spread and buy now
              leg1Ask price to buy leg1 now
              leg2Bid price to sell leg2 now
    
              crosses spread to BUY the synthetic spread (buy leg1, sell leg2), SELL exchange spread
 */

public final class StratExchSpreadBuyLeg1SellLeg2Arb extends BaseStrategyExchTwoLegArb {

    public StratExchSpreadBuyLeg1SellLeg2Arb( String id ) {
        super( id );
    }

    @Override
    protected final double getSynthSellPx() {
        final double legABid = getLegAEntry().getBidPx();           // cross spread to sell
        final double legBAsk = getLegBEntry().getAskPx();           // cross spread to buy
        final double synthSell = legABid - legBAsk;
        return synthSell;
    }

    @Override
    protected final double getSynthBuyPx() {
        final double legAAsk = getLegAEntry().getAskPx();           // cross spread to buy
        final double legBBid = getLegBEntry().getBidPx();           // cross spread to sell
        final double synthBuy        = legAAsk - legBBid;
        return synthBuy;
    }
        
    @Override
    protected final Side   getBuySynthSideLegA() {
        return Side.Buy;
    }
    
    @Override
    protected final Side   getBuySynthSideLegB() {
        return Side.Sell;
    }

    @Override
    protected final Side   getSellSynthSideLegA() {
        return Side.Sell;
    }
    
    @Override
    protected final Side   getSellSynthSideLegB() {
        return Side.Buy;
    }

    @Override
    protected final double getSynthSpreadVal( double legASliceVal, double legBSliceVal ) {
        return legASliceVal - legBSliceVal;
    }
}
