/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.millenium;

import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class MilleniumEncoderGenerator extends BaseBinaryEncoderGenerator {

    public MilleniumEncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.millenium.MilleniumEncodeBuilderImpl";
    }

    @Override
    protected int getTimestampSize() {
        return 8;
    }

    @Override
    protected int getPriceSize() {
        return 8;
    }
}
