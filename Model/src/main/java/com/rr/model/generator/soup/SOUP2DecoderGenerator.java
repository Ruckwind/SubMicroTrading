/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.soup;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

public class SOUP2DecoderGenerator extends BaseBinaryDecoderGenerator {

    public SOUP2DecoderGenerator() {
        super();
    }

    @Override
    protected void addDecoderImports( StringBuilder b, BinaryCodecDefinition def ) {

        b.append( "import com.rr.core.book.*;\n" );

        super.addDecoderImports( b, def );
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.soup.SOUP2DecodeBuilderImpl";
    }
}
