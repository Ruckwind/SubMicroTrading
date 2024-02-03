/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.mds.common.events.Subscribe;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSubscribe extends BaseTestCase {

    @Ignore
    @Test
    public void testSub() {
        Subscribe sub = new Subscribe();

        ReusableString ricChain = new ReusableString( "bt.l" );
        ricChain.setNext( new ReusableString( "vod.l" ) );

        sub.setType( MDSReusableType.TradingBandUpdate );
        sub.addRICChain( ricChain );

        ReusableString copiedChain = sub.getExchangeSymbolChain();
        assertEquals( "vod.l", copiedChain.toString() );
        assertNotNull( copiedChain.getNext() );
        assertEquals( "bt.l", copiedChain.getNext().toString() );
    }
}
