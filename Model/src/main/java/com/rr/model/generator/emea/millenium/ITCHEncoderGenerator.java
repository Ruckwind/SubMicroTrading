/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.millenium;

import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class ITCHEncoderGenerator extends BaseBinaryEncoderGenerator {

    public ITCHEncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.millenium.ITCHEncodeBuilderImpl";
    }

    @Override
    protected int getPriceSize() {
        return 8;
    }

    @Override
    protected int getTimestampSize() {
        return 8;
    }
}
