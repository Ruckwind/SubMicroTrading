/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.*;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;

public interface InstrumentStore extends InstrumentLocator {

    /**
     * add/replace the security in the store
     * <p>
     * if the update action is : SecurityUpdateAction.Modify  then the SecurityDefinition is treated as a delta and merged to the existing secdef
     * <p>
     * def should be a FULL version not a partial DELTA ... upstream feed should create new version and use deepCopy for partial updates
     * <p>
     * method takes ownership of the SecurityDefination,so should store/recycle as appropriate
     *
     * @return false if not added eg out of sequence
     * @WARNING doesnt throw AmbiguousKeyRuntimeException .. if key becomes ambiguous logs a warning and removed the ambigous entries from map
     * becuase otherwise would never run .. lots of ambiguous data we dont care about
     */
    boolean add( SecurityDefinitionImpl def );

    /**
     * add an FXInstrument or StrategyInstrument to the Store
     *
     * @param def
     */
    void add( Instrument def );

    /**
     * @return if store can handle intraday addition in threadsafe manner
     */
    boolean allowIntradayAddition();

    /**
     * remove the security in the store
     * <p>
     * note the instrument still exists until its garbage collected, so any orders for sample will not be broken
     * <p>
     * method takes ownership of the SecurityDefination,so should store/recycle as appropriate
     */
    void remove( SecurityDefinitionImpl def );

    /**
     * method takes ownership of the SecurityStatusImpl,so should store/recycle as appropriate
     */
    boolean updateStatus( SecurityStatusImpl status );
}
