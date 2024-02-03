/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.CodecFactory;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.model.generated.fix.codec.CodecFactoryPopulator;
import com.rr.om.exchange.CodecLoader;
import com.rr.om.warmup.sim.WarmupUtils;

public class CodecFactoryLoader implements SMTSingleComponentLoader {

    @Override
    public SMTComponent create( String id ) {

        CodecFactory          codecFactory = new CodecFactory( id );
        CodecFactoryPopulator pop          = new CodecLoader();
        pop.register( codecFactory );

        WarmupUtils.setCodecFactory( codecFactory );

        return codecFactory;
    }
}
