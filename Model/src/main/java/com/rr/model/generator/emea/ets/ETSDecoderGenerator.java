/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.emea.ets;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.base.BinaryEventMap;
import com.rr.model.base.ClassDefinition;
import com.rr.model.base.HookType;
import com.rr.model.generator.GenUtils;
import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

import java.util.Collection;
import java.util.Map;

public class ETSDecoderGenerator extends BaseBinaryDecoderGenerator {

    public ETSDecoderGenerator() {
        super();
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.ets.ETSDecodeBuilderImpl";
    }

    @Override
    protected void addDecoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        super.addDecoderImports( b, def );

        b.append( "import com.rr.model.generated.model.defn.ETSEurexCodes;\n" );
        b.append( "import com.rr.codec.emea.exchange.ets.EncodingID;\n" );
    }

    @Override
    protected void declareBuilder( StringBuilder b ) {
        b.append( "\n    private com.rr.codec.emea.exchange.ets.ETSDecodeBuilderImpl _builder;\n\n" );
    }

    @Override
    protected void writeSetBuilder( StringBuilder b ) {
        // no debug version for ETS as has non standard decoder
        // would need to create ETSDecodeBuilder and extend the debug proxy
        b.append( "    private void setBuilder() {\n" );
        b.append( "        _builder = new " ).append( getBuilder() ).append( "();\n" );
        b.append( "    }\n\n" );
    }

    @Override
    protected void doWriteDecodeMessage( StringBuilder b, Collection<BinaryEventMap> binaryMaps ) {
        // do nothing as hand coded
    }

    @Override
    public void decodeKnownMessageType( BinaryEventMap map ) {
        Map<String, DecodeEntry> entries = getEntriesByTag( map );

        ClassDefinition event = map.getClassDefinition();

        String preBinary = getPrefix( event );

        _b.append( "    private final void decode" ).append( map.getBinaryMsgId() ).append( "() {\n" );

        if ( event != null ) {
            String baseVar = "_" + GenUtils.toLowerFirstChar( event.getId() );
            String base    = preBinary + event.getId();
            _b.append( "        final " ).append( base ).append( "Impl msg = " ).append( baseVar ).append( "Factory.get();\n" );
        } else {
            _b.append( "        final Message msg = null;\n" ); // external event is not to be transformed into internal event, simply skipped
        }

        _b.append( "        int maxIdx = _builder.getCurrentIndex() + _builder.getCurFieldLen();\n\n" );

        //populate fields from header\n" );
        _b.append( "        if ( _reqSeqNum > 0 ) {\n" );
        _b.append( "            msg.setMsgSeqNum( _reqSeqNum );\n" );
        _b.append( "        }\n\n" );

        addHook( map, "        ", HookType.predecode );

        String spaces = "                ";

        _b.append( "        while( _builder.getCurrentIndex() < maxIdx ) {\n" );
        _b.append( "            final short code = _builder.decodeFieldHeader();\n\n" );

        _b.append( "            switch( code ) {\n" );

        String constFile = GenUtils.getBinaryConstantsFile( _binaryModel );

        for ( Map.Entry<String, DecodeEntry> entry : entries.entrySet() ) {

            String      tag         = entry.getKey();
            DecodeEntry decodeEntry = entry.getValue();

            _b.append( "            case " ).append( constFile ).append( "." ).append( GenUtils.toUpperFirstChar( tag ) ).append( "Code:\n" );

            if ( addHook( HookType.decode, spaces, decodeEntry ) ) {
                continue;
            }

            addHook( HookType.predecode, spaces, decodeEntry );

            decode( "msg", map, spaces, tag, decodeEntry, map.getEventDefinition() );

            addHook( HookType.postdecode, spaces, decodeEntry );

            _b.append( "                break;\n" );
        }

        _b.append( "            default:\n" );
        _b.append( "                _builder.skip( _builder.getCurFieldLen() );         // dont care skip field\n" );
        _b.append( "                break;\n" );
        _b.append( "            }\n" );
        _b.append( "        }\n\n" );

        addHook( map, "        ", HookType.postdecode );

        _b.append( "        addMessage( msg );\n" );

        _b.append( "    }\n\n" );
    }
}
