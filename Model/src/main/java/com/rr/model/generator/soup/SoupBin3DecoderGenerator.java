/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.soup;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

public class SoupBin3DecoderGenerator extends BaseBinaryDecoderGenerator {

    public SoupBin3DecoderGenerator() {
        super();
    }

    @Override protected void addDecoderImports( final StringBuilder b, final BinaryCodecDefinition def ) {
        super.addDecoderImports( b, def );
        b.append( "import com.rr.core.codec.SoupBinDecoder;\n" );
    }

    @Override protected String getBuilder() { return "com.rr.codec.emea.exchange.soupbin.SoupBin3DecodeBuilderImpl"; }

    @Override protected String getInterfaceToImplement() {
        return "com.rr.core.codec.SoupBinDecoder";
    }
}
