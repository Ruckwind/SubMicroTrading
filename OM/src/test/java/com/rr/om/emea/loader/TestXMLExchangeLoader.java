/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.loader;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeSession;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.exchange.loader.XMLExchangeLoader;
import com.rr.core.idgen.DailyLongIDGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXMLExchangeLoader extends BaseTestCase {

    @Test
    public void testXMLExchangeLoader() {
        int idNumPrefix = 90;
        // ENX = 1100000000000000000
        DailyLongIDGenerator numIdGen = new DailyLongIDGenerator( idNumPrefix, 19 ); // to fit ENX numeric ID format
        ExchangeManager.instance().register( numIdGen );

        ZString           zLon   = new ViewString( "XLON" );
        XMLExchangeLoader loader = new XMLExchangeLoader( "./common/testExchange.xml" );
        ExchangeManager.instance().clear();
        assertNull( ExchangeManager.instance().getByMIC( zLon ) );
        loader.load();

        Exchange lon = ExchangeManager.instance().getByMIC( zLon );
        assertNotNull( lon );

        ExchangeSession iobSess = lon.getExchangeSession( new ViewString( "IOB" ) );
        assertNotNull( iobSess );

        ExchangeSession defaultSess = lon.getSession();
        assertNotNull( defaultSess );

        assertNotSame( iobSess, defaultSess );

    }
}
