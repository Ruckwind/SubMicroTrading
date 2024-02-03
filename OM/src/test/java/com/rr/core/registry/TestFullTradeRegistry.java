/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.registry;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.model.generated.internal.events.impl.MarketTradeNewImpl;
import com.rr.model.generated.internal.events.interfaces.MarketTradeNewWrite;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.om.registry.FullTradeRegistry;
import com.rr.om.registry.TradeWrapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFullTradeRegistry extends BaseTestCase {

    @Test
    public void testRegistryTrade() {

        FullTradeRegistry set = new FullTradeRegistry( 2 );

        MarketTradeNewWrite trade = new MarketTradeNewImpl();

        assertTrue( set.register( null, setTrade( trade, "AAAA00001", 10, 121.2 ) ) );
        assertFalse( set.register( null, setTrade( trade, "AAAA00001", 10, 121.2 ) ) );
        assertFalse( set.register( null, setTrade( trade, "AAAA00001", 20, 121.2 ) ) );
        assertEquals( 1, set.size() );
        assertTrue( set.contains( null, new ReusableString( "AAAA00001" ) ) );

        int max = 16384;

        for ( int i = 2; i < max; ++i ) {
            assertTrue( set.register( null, setTrade( trade, "AAAA00001" + i, i, 121.2 ) ) );
            assertEquals( i, set.size() );
        }

        for ( int i = 2; i < max; ++i ) {
            ReusableString execId = new ReusableString( "AAAA00001" + i );
            assertTrue( set.contains( null, execId ) );

            TradeWrapper wrapper = set.get( null, execId );

            assertNotNull( wrapper );

            assertEquals( i, wrapper.getQty(), Constants.TICK_WEIGHT );
        }

        assertFalse( set.contains( null, new ReusableString( "AAAA00001" + (max + 2) ) ) );

        set.clear();
        assertEquals( 0, set.size() );
    }

    private TradeNew setTrade( MarketTradeNewWrite trade, String execId, int qty, double px ) {
        trade.getExecIdForUpdate().setValue( execId.getBytes() );
        trade.setLastQty( qty );
        trade.setLastPx( px );
        return trade;
    }
}
