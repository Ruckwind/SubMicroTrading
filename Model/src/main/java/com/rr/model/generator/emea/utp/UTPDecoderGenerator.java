/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.utp;

import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

public class UTPDecoderGenerator extends BaseBinaryDecoderGenerator {

    public UTPDecoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.utp.UTPDecodeBuilderImpl";
    }
}
