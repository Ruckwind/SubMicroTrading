/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.algo;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Book;
import com.rr.core.model.Event;

/**
 * if the changed and handleMarketEvent can be invoked on seperate threads then the Algo implementation should use the appropriate dispatcher
 *
 * @author Richard Rose
 */
public interface SimpleAlgo extends SMTComponent {

    void changed( Book book );

    void handleMarketEvent( Event mktMsg );
}
