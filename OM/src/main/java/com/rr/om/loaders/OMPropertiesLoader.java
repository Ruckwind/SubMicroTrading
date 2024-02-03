/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.properties.AppProps;

public class OMPropertiesLoader implements SMTSingleComponentLoader {

    @Override
    public SMTComponent create( String id ) {
        AppProps appProps = AppProps.instance();

        return appProps;
    }
}
