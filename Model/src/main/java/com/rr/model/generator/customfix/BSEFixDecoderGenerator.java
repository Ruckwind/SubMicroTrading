/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.customfix;

import com.rr.model.generator.BaseFixDecoderGenerator;

public class BSEFixDecoderGenerator extends BaseFixDecoderGenerator {

    public BSEFixDecoderGenerator() {
        super();
    }

    @Override
    protected String getInternalTime() {
        return "getInternalTimeFromBSEFastFix()";
    }
}
