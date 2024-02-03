/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator.sbe.cme;

import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.base.BinaryEventDefinition;
import com.rr.model.base.BinaryEventMap;
import com.rr.model.generator.binary.BaseBinaryDecoderGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CMEDecoderGenerator extends BaseBinaryDecoderGenerator {

    public CMEDecoderGenerator() {
        super();
        _ignoreMktVars.add( "msgStart" );
    }

    @Override
    protected String getBuilder() {
        return "com.rr.codec.emea.exchange.cme.sbe.SBEDecodeBuilderImpl";
    }

    @Override
    protected void addDecoderImports( StringBuilder b, BinaryCodecDefinition def ) {
        super.addDecoderImports( b, def );

        b.append( "import com.rr.core.codec.binary.sbe.SBEPacketHeader;\n" );
        b.append( "import com.rr.core.utils.StringUtils;\n" );
    }

    @Override
    protected void addEventConstant( StringBuilder b, Set<String> msgTypes, BinaryEventMap map, String msgType ) {
        if ( msgType != null && msgType.length() >= 1 && !msgTypes.contains( msgType ) ) {
            msgTypes.add( msgType );
            b.append( "    private static final short      MSG_" ).append( map.getBinaryMsgId() ).append( " = " ).append( msgType ).append( ";\n" );
        }
    }

    //

    @Override
    protected String getMsgTypeClass() {
        return "short";
    }

    @SuppressWarnings( "boxing" )
    @Override
    protected void doWriteDecodeMessage( StringBuilder b, Collection<BinaryEventMap> binaryMaps ) {
        b.append( "    @Override\n" );
        b.append( "    protected final Event doMessageDecode() {\n" );
        b.append( "        _builder.setMaxIdx( _maxIdx );\n\n" );

        b.append( "        switch( _msgType ) {\n" );

        Set<Short> switchEntrySet = new HashSet<>();

        _conditionalMessages.clear();
        short min = Short.MAX_VALUE;
        short max = 0;

        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String sMsgType = fmd.getMsgType();

                if ( sMsgType == null ) continue;

                short msgType = Short.parseShort( sMsgType );

                if ( msgType > max ) max = msgType;
                if ( msgType < min ) min = msgType;
            }
        }

        short threshold = getSwitchThreshold( binaryMaps, min );

        int overThreshold = 0;

        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String sMsgType = fmd.getMsgType();

                if ( sMsgType == null ) continue;

                short msgType = Short.parseShort( sMsgType );

                if ( msgType <= threshold ) {
                    if ( !switchEntrySet.contains( msgType ) ) {
                        b.append( "        case MSG_" ).append( map.getBinaryMsgId() ).append( ":\n" );
                        b.append( "            return decode" ).append( map.getBinaryMsgId() ).append( "();\n" );
                        switchEntrySet.add( msgType );
                        if ( map.getConditionalKey() != null ) {
                            _conditionalMessages.add( sMsgType );
                        }
                    }
                } else {
                    ++overThreshold;
                }
            }
        }

        if ( min < 0 ) min = 0; // force tableswitch
        if ( max > min ) {
            int cnt = 0;
            for ( short entry = min; entry < threshold; ++entry ) {
                if ( !switchEntrySet.contains( entry ) ) {
                    ++cnt;
                    b.append( "        case " ).append( entry ).append( ":\n" );
                }
            }
            if ( cnt > 0 ) {
                b.append( "            break;\n" );
            }
        }

        if ( overThreshold > 0 ) {
            b.append( "        default:\n" );
            b.append( "            switch( _msgType ) {\n" );

            for ( BinaryEventMap map : binaryMaps ) {
                BinaryEventDefinition fmd = map.getEventDefinition();

                if ( fmd != null ) {
                    String sMsgType = fmd.getMsgType();

                    if ( sMsgType == null ) continue;

                    short msgType = Short.parseShort( sMsgType );

                    if ( msgType > threshold ) {
                        if ( !switchEntrySet.contains( msgType ) ) {
                            b.append( "            case MSG_" ).append( map.getBinaryMsgId() ).append( ":\n" );
                            b.append( "                return decode" ).append( map.getBinaryMsgId() ).append( "();\n" );
                            switchEntrySet.add( msgType );
                            if ( map.getConditionalKey() != null ) {
                                _conditionalMessages.add( sMsgType );
                            }
                        }
                    }
                }
            }
            b.append( "            }\n" );
            b.append( "            break;\n" );
        }

        b.append( "        }\n" );

        b.append( "        return null;\n" );
        b.append( "    }\n\n" );
    }

    @Override
    protected String getInterfaceToImplement() {
        return "com.rr.core.codec.binary.sbe.SBEDecoder";
    }

    private short getSwitchThreshold( Collection<BinaryEventMap> binaryMaps, short min ) {
        short thresholdMax = (short) (min + 256);
        short threshold    = min;

        for ( BinaryEventMap map : binaryMaps ) {
            BinaryEventDefinition fmd = map.getEventDefinition();

            if ( fmd != null ) {
                String sMsgType = fmd.getMsgType();

                if ( sMsgType == null ) continue;

                short msgType = Short.parseShort( sMsgType );

                if ( msgType >= threshold && msgType < thresholdMax ) threshold = msgType;
            }
        }

        return threshold;
    }
}
