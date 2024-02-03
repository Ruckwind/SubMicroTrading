/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.utils.SMTException;

/**
 * helper component used for more complex glueing of components
 * <p>
 * loader responsible for instantiating multiple components in one go
 */
public interface SMTMultiComponentLoader extends SMTComponentLoader {

    /**
     * create instances for 1 or more components
     *
     * @return 1 or more component instances
     * @throws SMTException if exception creating component
     */
    SMTComponent[] create( String grpId ) throws SMTException;
}
