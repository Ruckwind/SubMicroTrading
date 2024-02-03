/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

import com.rr.core.utils.SMTRuntimeException;

public interface DynamicConfig {

    /**
     * @return string of key properties in the config
     */
    String info();

    /**
     * invoked post configuration to ensure component has all required properties
     *
     * @throws SMTRuntimeException
     */
    void validate() throws SMTRuntimeException;
}
