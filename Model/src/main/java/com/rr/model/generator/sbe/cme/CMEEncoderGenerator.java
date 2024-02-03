/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.sbe.cme;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.base.BinaryEventMap;
import com.rr.model.generator.binary.BaseBinaryEncoderGenerator;

public class CMEEncoderGenerator extends BaseBinaryEncoderGenerator {

    public CMEEncoderGenerator() {
        super();
    }

    @Override
    protected void addEncoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        super.addEncoderImports( b, def );

        b.append( "import com.rr.core.codec.binary.sbe.SBEPacketHeader;\n" );
    }

    @Override
    protected void encoderStart( BinaryEventMap map, String type ) {
        int blockLen = map.getEventDefinition().getBlockLen();

        _b.append( "        if ( _debug ) {\n" );
        _b.append( "            _dump.append( \"  encodeMap=\" ).append( \"" ).append( map.getBinaryMsgId() ).append( "\" )." )
          .append( "append( \"  rootBlockLen=\" ).append( \"" ).append( blockLen ).append( "\" )." ).append( "append( \"  eventType=\" ).append( \"" )
          .append( type ).append( "\" ).append( \" : \" );\n" );

        _b.append( "        ((SBEEncodeBuilderImpl)_builder).setNextBlockLen( (short)" ).append( blockLen ).append( " );\n" );
        _b.append( "        _builder.start( MSG_" ).append( map.getBinaryMsgId() ).append( " );\n" );

        _b.append( "        }\n\n" );

    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.cme.sbe.SBEEncodeBuilderImpl";
    }

    @Override
    protected String getInterfaceToImplement() {
        return "com.rr.core.codec.binary.sbe.SBEEncoder";
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
