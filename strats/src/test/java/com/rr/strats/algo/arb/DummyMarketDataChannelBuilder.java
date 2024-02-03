/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb;

import com.rr.core.model.EventHandler;
import com.rr.md.channel.MarketDataChannelBuilder;
import static org.junit.Assert.*;


public class DummyMarketDataChannelBuilder implements MarketDataChannelBuilder<Integer> {

    @Override
    public String id() {
        return "DummyMDChannelBuilder";
    }

    @Override
    public void register( Integer channelKey, String pipeLineId, EventHandler consumer ) {
        // nothing
    }
}
