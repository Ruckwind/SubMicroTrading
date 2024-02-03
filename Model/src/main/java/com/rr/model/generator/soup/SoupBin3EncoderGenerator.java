/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.soup;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class SoupBin3EncoderGenerator extends BaseBinaryEncoderGenerator {

    public SoupBin3EncoderGenerator() {
        super();
    }

    @Override protected void addEncoderImports( final StringBuilder b, final BinaryCodecDefinition def ) {
        super.addEncoderImports( b, def );
        b.append( "import com.rr.core.codec.SoupBinEncoder;\n" );
    }

    @Override protected String getBuilder() {
        return "com.rr.codec.emea.exchange.soupbin.SoupBin3EncodeBuilderImpl";
    }

    @Override protected String getInterfaceToImplement() { return "com.rr.core.codec.SoupBinEncoder"; }
}

