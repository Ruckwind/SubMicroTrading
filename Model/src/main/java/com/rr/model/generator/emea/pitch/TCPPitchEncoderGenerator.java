/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.pitch;

import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class TCPPitchEncoderGenerator extends BaseBinaryEncoderGenerator {

    public TCPPitchEncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.pitch.TCPPitchEncodeBuilderImpl";
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
