/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.millenium;

import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

public class MilleniumDecoderGenerator extends BaseBinaryDecoderGenerator {

    public MilleniumDecoderGenerator() {
        super();
        _ignoreMktVars.add( "msgStart" );
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.millenium.MilleniumDecodeBuilderImpl";
    }
}
