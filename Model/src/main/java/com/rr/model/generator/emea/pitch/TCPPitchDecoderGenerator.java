/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.pitch;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

public class TCPPitchDecoderGenerator extends BaseBinaryDecoderGenerator {

    public TCPPitchDecoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.pitch.TCPPitchDecodeBuilderImpl";
    }

    @Override
    protected void addDecoderImports( StringBuilder b, BinaryCodecDefinition def ) {

        b.append( "import com.rr.core.book.*;\n" );

        super.addDecoderImports( b, def );
    }
}
