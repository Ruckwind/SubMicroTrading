/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.codec.CodecFactory;
import com.rr.model.generated.fix.codec.CodecFactoryPopulator;

public class CodecLoader extends CodecFactoryPopulator {

    @Override
    public void register( CodecFactory factory ) {
        super.register( factory );

        // add EMEA manually written codecs
    }
}
