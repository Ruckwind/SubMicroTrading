/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.lang.ZConsumer2Args;
import com.rr.core.utils.SMTRuntimeException;

/**
 * Represents a component as defined in config
 * <p>
 * Initially these components will be created using custom loaders or refelection
 * Later may move to Spring ... reason for not doing so now is simplly time.
 * Spring is great when it works and terrible when it doesnt and is a general time sink
 * <p>
 * SMTComponent must have a constructor with first element a string for the componentId
 * <p>
 * AntiSpringBootStrap apps will not terminate until ALL active workers are stopping
 * so dont make a component active if it does not have a life cycle with an end
 */

public interface SMTActiveWorker extends SMTComponent {

    default void addCompStateListener( ZConsumer2Args<CompRunState, CompRunState> callback ) { throw new SMTRuntimeException( "SMTActiveComponent implementation doesnt support addCompStateListener" ); }

    CompRunState getCompRunState();

    default boolean isActive() { return CompRunState.canProcess( getCompRunState() ); }
    default boolean isStopping() { return CompRunState.isStopping( getCompRunState() ); }

    default boolean isCompleted() { return CompRunState.isComplete( getCompRunState() ); }
}
