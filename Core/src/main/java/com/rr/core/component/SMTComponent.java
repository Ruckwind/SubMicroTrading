/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.model.Identifiable;

/**
 * Represents a component as defined in config
 * <p>
 * Initially these components will be created using custom loaders or refelection
 * Later may move to Spring ... reason for not doing so now is simplly time.
 * Spring is great when it works and terrible when it doesnt and is a general time sink
 * <p>
 * SMTComponent must have a constructor with first element a string for the componentId
 *
 * @WARNING on recovery a constructor must exists which takes only the SMT id ... so initialisting fields in other constructors could be ERROR prone .. use init method rather than overloading constructors
 * <p>
 * The simplest component contract
 */

public interface SMTComponent extends Identifiable {

    @Override default String id() { return getComponentId(); }

    /**
     * @return unique identifier of component as used in references to components in component wiring
     */
    String getComponentId();
}
