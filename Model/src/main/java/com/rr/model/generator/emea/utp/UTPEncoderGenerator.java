/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.utp;

import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class UTPEncoderGenerator extends BaseBinaryEncoderGenerator {

    public UTPEncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.utp.UTPEncodeBuilderImpl";
    }
}
