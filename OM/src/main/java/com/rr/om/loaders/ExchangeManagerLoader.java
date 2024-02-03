/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.exchange.loader.XMLExchangeLoader;
import com.rr.core.idgen.DailyLongIDGenerator;

public class ExchangeManagerLoader implements SMTSingleComponentLoader {

    private int    _genNumIdPrefix = 10;
    private String _fileName       = "./common/exchange.xml";

    @Override
    public SMTComponent create( String id ) {

        // @TODO move off singleton when all code base moved to component loaders
        ExchangeManager mgr = ExchangeManager.instance();

        mgr.setId( id );

        DailyLongIDGenerator numIdGen = new DailyLongIDGenerator( _genNumIdPrefix, 19 ); // to fit ENX numeric ID format
        mgr.register( numIdGen );
        XMLExchangeLoader loader = new XMLExchangeLoader( _fileName );
        loader.load();

        return mgr;
    }
}
