/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.eti;

import com.rr.model.base.BinaryEventMap;
import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class ETIEncoderGenerator extends BaseBinaryEncoderGenerator {

    public ETIEncoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.eti.ETIEncodeBuilderImpl";
    }

    @Override
    protected String getInterfaceToImplement() {
        return "com.rr.codec.emea.exchange.eti.ETIEncoder";
    }

    @Override
    protected int getPriceSize() {
        return 8;
    }

    @Override
    protected int getTimestampSize() {
        return 8;
    }

    @Override
    protected boolean useMsgTypesForStartEncode( BinaryEventMap map ) {
        return true;
    }

    @Override
    protected void writeMessageConstant( StringBuilder b, BinaryEventMap map, String msgType ) {
        b.append( "    private static final int      MSG_" ).append( map.getBinaryMsgId() ).append( " = " ).append( msgType ).append( ";\n" );
    }
}
