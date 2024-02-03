/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.ViewString;
import com.rr.core.model.*;
import com.rr.core.utils.FileException;
import com.rr.om.BaseOMTestCase;
import com.rr.om.exchange.ExchangeManager;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestBSEFixInstrumentLoader extends BaseOMTestCase {

    static {
        loadExchanges();
    }

    @Ignore
    @Test
    public void testLoadOne() throws FileException {
        Exchange                      e         = ExchangeManager.instance().getByMIC( new ViewString( "XCME" ) );
        SingleExchangeInstrumentStore instStore = new SingleExchangeInstrumentStore( e, 1000 );
        instStore.setTickManager( new TickManager( "tstTickMgr" ) );

        FixInstrumentLoader loader = new FixInstrumentLoader( instStore );
        loader.loadFromFile( "../data/bse/common/cdx/sim/bseInst.cdx.sim.20141027.dat" );

        ExchangeInstrument inst = instStore.getExchInst( new ViewString( "1001388" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCME );
        assertNotNull( inst );
        assertEquals( new ViewString( "1001388" ), inst.getExchangeSymbol() );
        assertEquals( 5, inst.getSecurityGroupId() );

        inst = instStore.getExchInst( new ViewString( "72057606922829885" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCME );
        assertNotNull( inst );
        assertEquals( new ViewString( "72057606922829885" ), inst.getExchangeSymbol() );
        assertEquals( 3, inst.getSecurityGroupId() );

    }
}
