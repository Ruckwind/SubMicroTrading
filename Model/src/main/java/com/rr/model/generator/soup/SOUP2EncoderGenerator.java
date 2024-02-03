/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.soup;

import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class SOUP2EncoderGenerator extends BaseBinaryEncoderGenerator {

    public SOUP2EncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.soup.SOUP2EncodeBuilderImpl";
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
