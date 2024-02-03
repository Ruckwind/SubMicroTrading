/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.codec.BaseReject;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixField;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Currency;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.CxlRejResponseTo;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.client.OMClientProfileImpl;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.order.Order;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.utils.FixUtils;
import com.rr.om.warmup.sim.WarmupUtils;
import org.junit.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * this code was originally in test package, moved to warmup to facilitate warmup
 */
public class FixTestUtils {

    private static final Logger _log = LoggerFactory.create( FixTestUtils.class );
    private static final byte[] cnosP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=208;35=D;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" );    private static String _dateStr      = today();
    private static final byte[] cnosP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;15=EUR;60=" );    private static byte[] _dateStrBytes = _dateStr.getBytes();

    // templates for syntehsizing messages for decoding
    private static final byte[] cnosP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;1=CLNT_JSGT33;48=ICAD.XPAR;21=1;22=R;38=" );
    private static final byte[] cnosP4 = FixTestUtils.semiColonToFixDelim( ";54=1;40=2;55=ICAD.XPAR;100=XPAR;11=" );
    private static final byte[] cnosP5 = FixTestUtils.semiColonToFixDelim( ";58=SWP;59=0;44=" );
    private static final byte[] cnosP6 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] ccrrP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=209;35=G;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" );
    private static final byte[] ccrrP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;15=EUR;60=" );
    private static final byte[] ccrrP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;1=CLNT_JSGT33;48=ICAD.XPAR;21=1;22=R;38=" );
    private static final byte[] ccrrP4 = FixTestUtils.semiColonToFixDelim( ";54=1;40=2;55=ICAD.XPAR;100=XPAR;11=" );
    private static final byte[] ccrrP5 = FixTestUtils.semiColonToFixDelim( ";58=SWP;59=0;44=" );
    private static final byte[] ccrrP6 = FixTestUtils.semiColonToFixDelim( ";41=" );
    private static final byte[] ccrrP7 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mackP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=225;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mackP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mackP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mackP4 = FixTestUtils.semiColonToFixDelim( ";150=0;39=0;40=2;54=1;38=" );
    private static final byte[] mackP5 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;44=" );
    private static final byte[] mackP6 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mackP7 = FixTestUtils.semiColonToFixDelim( ";32=0;31=0.00;151=133;11=" );
    private static final byte[] mackP8 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mrepP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=225;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mrepP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mrepP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mrepP4 = FixTestUtils.semiColonToFixDelim( ";150=5;39=5;40=2;54=1;38=" );
    private static final byte[] mrepP5 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;44=" );
    private static final byte[] mrepP6 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mrepP7 = FixTestUtils.semiColonToFixDelim( ";32=0;31=0.00;151=133;11=" );
    private static final byte[] mrepP8 = FixTestUtils.semiColonToFixDelim( ";41=" );
    private static final byte[] mrepP9 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mcanP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=225;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mcanP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mcanP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mcanP4 = FixTestUtils.semiColonToFixDelim( ";150=4;39=4;40=2;54=1;38=" );
    private static final byte[] mcanP5 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;44=" );
    private static final byte[] mcanP6 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mcanP7 = FixTestUtils.semiColonToFixDelim( ";32=0;31=0.00;151=133;11=" );
    private static final byte[] mcanP8 = FixTestUtils.semiColonToFixDelim( ";41=" );
    private static final byte[] mcanP9 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] ccanP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=187;35=F;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" );
    private static final byte[] ccanP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;15=EUR;60=" );
    private static final byte[] ccanP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.618;48=ICAD.XPAR;54=1;55=ICAD.XPAR;100=XPAR;11=" );
    private static final byte[] ccanP4 = FixTestUtils.semiColonToFixDelim( ";58=SWP;59=0;41=" );
    private static final byte[] ccanP5 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mcanrejP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=186;35=9;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mcanrejP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mcanrejP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mcanrejP4 = FixTestUtils.semiColonToFixDelim( ";58=" );
    private static final byte[] mcanrejP5 = FixTestUtils.semiColonToFixDelim( ";102=" );
    private static final byte[] mcanrejP6 = FixTestUtils.semiColonToFixDelim( ";434=" );
    private static final byte[] mcanrejP7 = FixTestUtils.semiColonToFixDelim( ";11=" );
    private static final byte[] mcanrejP8 = FixTestUtils.semiColonToFixDelim( ";41=" );
    private static final byte[] mcanrejP9 = FixTestUtils.semiColonToFixDelim( ";39=" );
    private static final byte[] mcanrejPA = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mfillP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=218;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mfillP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mfillP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mfillP4 = FixTestUtils.semiColonToFixDelim( ";150=F;39=" );
    private static final byte[] mfillP5 = FixTestUtils.semiColonToFixDelim( ";40=2;54=1;32=" );
    private static final byte[] mfillP6 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;31=" );
    private static final byte[] mfillP7 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mfillP8 = FixTestUtils.semiColonToFixDelim( ";151=133;11=" );
    private static final byte[] mfillP9 = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mtrcanP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=218;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mtrcanP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mtrcanP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mtrcanP4 = FixTestUtils.semiColonToFixDelim( ";150=H;39=" );
    private static final byte[] mtrcanP5 = FixTestUtils.semiColonToFixDelim( ";40=2;54=1;32=" );
    private static final byte[] mtrcanP6 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;31=" );
    private static final byte[] mtrcanP7 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mtrcanP8 = FixTestUtils.semiColonToFixDelim( ";151=133;11=" );
    private static final byte[] mtrcanP9 = FixTestUtils.semiColonToFixDelim( ";19=" );
    private static final byte[] mtrcanPA = FixTestUtils.semiColonToFixDelim( ";10=999;" );
    private static final byte[] mtrcorP1 = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=218;35=8;49=CHIX;56=ME01;34=85795;52=" );
    private static final byte[] mtrcorP2 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;57=ST;20=0;60=" );
    private static final byte[] mtrcorP3 = FixTestUtils.semiColonToFixDelim( "-08:29:08.622;37=" );
    private static final byte[] mtrcorP4 = FixTestUtils.semiColonToFixDelim( ";150=G;39=" );
    private static final byte[] mtrcorP5 = FixTestUtils.semiColonToFixDelim( ";40=2;54=1;32=" );
    private static final byte[] mtrcorP6 = FixTestUtils.semiColonToFixDelim( ";55=ICADp;31=" );
    private static final byte[] mtrcorP7 = FixTestUtils.semiColonToFixDelim( ";47=P;59=0;109=ME01;14=0;6=0.00;17=" );
    private static final byte[] mtrcorP8 = FixTestUtils.semiColonToFixDelim( ";151=133;11=" );
    private static final byte[] mtrcorP9 = FixTestUtils.semiColonToFixDelim( ";19=" );
    private static final byte[] mtrcorPA = FixTestUtils.semiColonToFixDelim( ";10=999;" );

    public static void setTodayStr( String testDate ) {
        _dateStr      = testDate;
        _dateStrBytes = _dateStr.getBytes();
    }

    private static String today() {
        DateFormat utcFormat = new SimpleDateFormat( "yyyyMMdd" );
        String     today     = utcFormat.format( new Date() );
        FixTestUtils.setTodayStr( today );
        return today;
    }

    public static String getDateStr() {
        return _dateStr;
    }

    public static ClientProfile getTestClient() {
        ZString id = new ViewString( "TestClient" );
        return new OMClientProfileImpl( id, Double.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, Double.MAX_VALUE, 60, 80, 90 );
    }

    public static byte[] getDateBytes() {
        return _dateStrBytes;
    }

    public static Map<String, String> msgToMap( String msg ) {

        Map<String, String> vals = new LinkedHashMap<>();

        byte[] bytes = msg.getBytes();

        int startKey = 0;
        int endKey   = -1;
        int startVal = -1;
        int endVal;

        for ( int i = 0; i < bytes.length; ++i ) {

            byte b = bytes[ i ];

            if ( b == '=' ) {
                endKey   = i;
                startVal = i + 1;
            } else if ( b == FixField.FIELD_DELIMITER ) {
                endVal = i;
                if ( endKey > 0 ) {
                    String key = msg.substring( startKey, endKey );
                    String val = msg.substring( startVal, endVal );
                    vals.put( key, val );
                }
                startKey = i + 1;
                endKey   = -1;
            }
        }

        return vals;
    }

    public static String toFixDelim( String msg ) {
        StringBuilder sb = new StringBuilder();

        char[] chars = msg.toCharArray();

        for ( int i = 0; i < chars.length - 1; ++i ) {

            if ( chars[ i ] == ';' && chars[ i + 1 ] == ' ' ) {
                sb.append( (char) FixField.FIELD_DELIMITER );
                ++i;
            } else {
                sb.append( chars[ i ] );
            }
        }

        if ( chars[ chars.length - 1 ] == ';' ) {
            sb.append( FixField.FIELD_DELIMITER );
        }

        byte[] bytes = sb.toString().getBytes();

        int bodyStart = WarmupUtils.overrideTagLen( bytes, 0, bytes.length );
        if ( bytes[ bytes.length - 1 ] == FixField.FIELD_DELIMITER ) WarmupUtils.overrideTagCheckSum( bytes, 0, bytes.length, bodyStart );

        return new String( bytes );
    }

    public static byte[] semiColonToFixDelim( String msg ) {
        StringBuilder sb = new StringBuilder();

        char[] chars = msg.toCharArray();

        for ( int i = 0; i < chars.length; ++i ) {

            if ( chars[ i ] == ';' ) {
                sb.append( (char) FixField.FIELD_DELIMITER );
            } else {
                sb.append( chars[ i ] );
            }
        }

        return sb.toString().getBytes();
    }

    public static String semiColonToFixDelimStr( String msg ) {
        StringBuilder sb = new StringBuilder();

        char[] chars = msg.toCharArray();

        for ( int i = 0; i < chars.length; ++i ) {

            if ( chars[ i ] == ';' ) {
                sb.append( (char) FixField.FIELD_DELIMITER );
            } else {
                sb.append( chars[ i ] );
            }
        }

        return sb.toString();
    }

    public static String fixDelimToSemiColonStr( String msg ) {
        StringBuilder sb = new StringBuilder();

        char[] chars = msg.toCharArray();

        for ( int i = 0; i < chars.length; ++i ) {

            if ( chars[ i ] == FixField.FIELD_DELIMITER ) {
                sb.append( ';' );
            } else {
                sb.append( chars[ i ] );
            }
        }

        return sb.toString();
    }

    public static void compare( String expectedMsg, String checkMsg, boolean ignoreSendTime ) {

        Map<String, String> expected = msgToMap( expectedMsg );
        Map<String, String> check    = msgToMap( checkMsg );

        StringBuilder errors = new StringBuilder();

        for ( String key : expected.keySet() ) {

            String expVal   = expected.get( key );
            String checkVal = check.get( key );

            if ( ignoreSendTime && ("52".equals( key ) || "60".equals( key ) || "10".equals( key )) ) {
                continue;
            }

            if ( !expVal.equals( checkVal ) ) errors.append( "\ntag " ).append( key ).append( ", expVal=" ).append( expVal ).append( ", val=" ).append( checkVal );
        }

        for ( String key : check.keySet() ) {

            String checkVal = check.get( key );

            if ( ignoreSendTime && ("52".equals( key ) || "60".equals( key ) || "10".equals( key )) ) {
                continue;
            }

            if ( !expected.containsKey( key ) ) errors.append( "\nUnexpected tag " ).append( key ).append( " val=" ).append( checkVal );
        }

        Assert.assertTrue( "Expected no errors found " + errors, errors.length() == 0 );

        Assert.assertEquals( expected.size(), check.size() );
    }

    public static void compare( String expectedMsg,
                                String checkMsg,
                                Set<String> tagsToCompare,
                                boolean tagsAreIgnore,
                                boolean ignoreSendTime ) {

        Map<String, String> expected = msgToMap( expectedMsg );
        Map<String, String> check    = msgToMap( checkMsg );

        if ( tagsAreIgnore ) {
            for ( String key : expected.keySet() ) {

                if ( tagsToCompare.contains( key ) ) {
                    continue;
                }

                String expVal   = expected.get( key );
                String checkVal = check.get( key );

                if ( ignoreSendTime && ("52".equals( key ) || "60".equals( key ) || "10".equals( key )) ) {
                    continue;
                }

                if ( "44".equals( key ) || "6".equals( key ) || "31".equals( key ) ) {
                    Assert.assertEquals( "tag " + key, Double.parseDouble( expVal ), Double.parseDouble( checkVal ), Constants.TICK_WEIGHT );
                } else {
                    Assert.assertEquals( "tag " + key, expVal, checkVal );
                }
            }
        } else {
            for ( String key : tagsToCompare ) {

                String expVal   = expected.get( key );
                String checkVal = check.get( key );

                if ( ignoreSendTime && ("52".equals( key ) || "60".equals( key ) || "10".equals( key )) ) {
                    continue;
                }

                if ( "44".equals( key ) || "31".equals( key ) || "6".equals( key ) ) {
                    Assert.assertEquals( "tag " + key, Double.parseDouble( expVal ), Double.parseDouble( checkVal ), Constants.TICK_WEIGHT );
                } else {
                    Assert.assertEquals( "tag " + key, expVal, checkVal );
                }
            }
        }
    }

    public static ClientNewOrderAckImpl getClientAck( MarketNewOrderAckImpl mktAck, ClientNewOrderSingleImpl nosEvent ) {

        ClientNewOrderAckImpl clientAck = new ClientNewOrderAckImpl();

        clientAck.setSrcEvent( nosEvent );

        clientAck.getOrderIdForUpdate().setValue( mktAck.getOrderId() );
        clientAck.getExecIdForUpdate().setValue( mktAck.getExecId() );
        clientAck.setExecType( mktAck.getExecType() );
        clientAck.setOrdStatus( mktAck.getOrdStatus() );
        clientAck.setMsgSeqNum( mktAck.getMsgSeqNum() );
        clientAck.setLeavesQty( nosEvent.getOrderQty() );

        return clientAck;
    }

    public static MarketNewOrderSingleImpl getMarketNOS( ClientNewOrderSingleImpl clientNOS ) {
        MarketNewOrderSingleImpl mktNOS = new MarketNewOrderSingleImpl();

        mktNOS.setSrcEvent( clientNOS );
        mktNOS.getClOrdIdForUpdate().setValue( clientNOS.getClOrdId() );
        mktNOS.setPrice( clientNOS.getPrice() );
        mktNOS.setOrderQty( clientNOS.getOrderQty() );
        mktNOS.setCurrency( clientNOS.getInstrument().getCurrency() );
        mktNOS.setSrcEvent( clientNOS );
        mktNOS.setMsgSeqNum( clientNOS.getMsgSeqNum() );

        return mktNOS;
    }

    public static ClientNewOrderSingleImpl getClientNOS( FixDecoder decoder, String clOrdId, int qty, double price ) {

        ReusableString rs = new ReusableString( "8=FIX.4.2;9=209;35=D;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" );
        rs.append( FixTestUtils.getDateStr() ).append( "-08:29:08.618;15=EUR;60=" );
        rs.append( FixTestUtils.getDateStr() ).append( "-08:29:08.618;1=CLNT_JSGT33;48=ICAD.XPAR;21=1;22=R;38=" ).append( qty );
        rs.append( ";133;54=1;40=2;55=ICAD.XPAR;100=XPAR;11=" ).append( clOrdId ).append( ";58=SWP;59=0;44=" ).append( price ).append( ";10=999;" );

        byte[] cnos = FixTestUtils.semiColonToFixDelim( rs.toString() );

        Event m1 = WarmupUtils.doDecode( decoder, cnos, 0, cnos.length );

        if ( m1 instanceof BaseReject<?> ) {
            BaseReject<?> rej = (BaseReject<?>) m1;

            _log.info( "Failed to decode [" + new String( cnos ) + "] " + rej.getMessage() );

            return null;
        }

        return (ClientNewOrderSingleImpl) m1;
    }

    public static Standard44DecoderOMS getOMSDecoder44( ClientProfile testClient ) {
        Standard44DecoderOMS decoder = new Standard44DecoderOMS( (byte) '4', (byte) '4' );
        decoder.setClientProfile( testClient );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        decoder.setValidateChecksum( true );
        return decoder;
    }

    public static Standard42DecoderOMS getOMSDecoder42( ClientProfile testClient ) {
        Standard42DecoderOMS decoder = new Standard42DecoderOMS( (byte) '4', (byte) '2' );
        decoder.setClientProfile( testClient );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        decoder.setValidateChecksum( true );
        return decoder;
    }

    public static CMEDecoderOMS getOMSDecoderCME42( ClientProfile testClient ) {
        CMEDecoderOMS decoder = new CMEDecoderOMS( (byte) '4', (byte) '2' );
        decoder.setClientProfile( testClient );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        decoder.setValidateChecksum( true );
        return decoder;
    }

    public static Standard44DecoderFull getFullDecoder44( ClientProfile testClient ) {
        Standard44DecoderFull decoder = new Standard44DecoderFull( (byte) '4', (byte) '4' );
        decoder.setClientProfile( testClient );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        decoder.setValidateChecksum( false );
        return decoder;
    }

    public static Standard44Encoder getEncoder44( byte[] buf, int offset ) {
        Standard44Encoder encoder = new Standard44Encoder( (byte) '4', (byte) '4', buf, offset );
        TimeUtils         calc    = TimeUtilsFactory.createTimeUtils();
        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        encoder.setTimeUtils( calc );
        return encoder;
    }

    public static Standard44DecoderOMS getOMSDecoder44() {
        return getOMSDecoder44( getTestClient() );
    }

    public static Standard42DecoderOMS getOMSDecoder42() {
        return getOMSDecoder42( getTestClient() );
    }

    public static CMEDecoderOMS getOMSDecoderCME42() {
        return getOMSDecoderCME42( getTestClient() );
    }

    public static Standard44DecoderFull getFullDecoder44() {
        return getFullDecoder44( getTestClient() );
    }

    public static ClientNewOrderSingleImpl getClientNOS( FixDecoder decoder, String clOrdId, int qty, double price, EventHandler handler ) {
        ClientNewOrderSingleImpl m = getClientNOS( decoder, clOrdId, qty, price );
        m.setEventHandler( handler );
        return m;
    }

    public static <T extends Event> T getMessage( String msg, FixDecoder decoder, EventHandler msgHandler ) {

        msg = FixUtils.chkDelim( msg );

        ReusableString buffer = new ReusableString( msg );

        decoder.setReceived( Utils.nanoTime() );

        @SuppressWarnings( "unchecked" )
        T m1 = (T) WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        m1.setEventHandler( msgHandler );

        return m1;
    }

    public static ClientNewOrderSingleImpl getClientNOS( ReusableString buffer,
                                                         FixDecoder decoder,
                                                         ReusableString key,
                                                         double qty,
                                                         double price,
                                                         EventHandler msgHandler ) {

        buffer.reset();
        buffer.append( cnosP1 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( cnosP2 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( cnosP3 );
        buffer.append( qty );
        buffer.append( cnosP4 );
        buffer.append( key );
        buffer.append( cnosP5 );
        buffer.append( price );
        buffer.append( cnosP6 );

        decoder.setReceived( Utils.nanoTime() );
        ClientNewOrderSingleImpl m1 = (ClientNewOrderSingleImpl) WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        m1.setEventHandler( msgHandler );

        return m1;
    }

    public static ClientCancelRequestImpl getClientCancelRequest( ReusableString buffer,
                                                                  FixDecoder decoder,
                                                                  ViewString clOrdId,
                                                                  ViewString origClOrdId,
                                                                  EventHandler msgHandler ) {

        buffer.reset();
        buffer.append( ccanP1 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( ccanP2 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( ccanP3 );
        buffer.append( clOrdId );
        buffer.append( ccanP4 );
        buffer.append( origClOrdId );
        buffer.append( ccanP5 );

        decoder.setReceived( Utils.nanoTime() );
        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        m1.setEventHandler( msgHandler );

        return (ClientCancelRequestImpl) m1;
    }

    public static Cancelled getCancelled( ReusableString buffer,
                                          FixDecoder decoder,
                                          ViewString clOrdId,
                                          ViewString origClOrdId,
                                          double qty,
                                          double price,
                                          ViewString orderId,
                                          ViewString execId ) {

        buffer.reset();

        buffer.append( mcanP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mcanP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mcanP3 );
        buffer.append( orderId );
        buffer.append( mcanP4 );
        buffer.append( qty );
        buffer.append( mcanP5 );
        buffer.append( price );
        buffer.append( mcanP6 );
        buffer.append( execId );
        buffer.append( mcanP7 );
        buffer.append( clOrdId );
        buffer.append( mcanP8 );
        buffer.append( origClOrdId );
        buffer.append( mcanP9 );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        Cancelled ack = (Cancelled) m1;

        return ack;
    }

    public static MarketCancelRejectImpl getMarketCancelReject( ReusableString buffer,
                                                                FixDecoder decoder,
                                                                ZString clOrdId,
                                                                ZString origClOrdId,
                                                                ZString orderId,
                                                                ZString rejectReason,
                                                                CxlRejReason reason,
                                                                CxlRejResponseTo msgTypeRejected,
                                                                OrdStatus status ) {

        buffer.reset();

        buffer.append( mcanrejP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mcanrejP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mcanrejP3 );
        buffer.append( orderId );
        buffer.append( mcanrejP4 );
        buffer.append( rejectReason );
        buffer.append( mcanrejP5 );
        buffer.append( reason.getVal() );
        buffer.append( mcanrejP6 );
        buffer.append( msgTypeRejected.getVal() );
        buffer.append( mcanrejP7 );
        buffer.append( clOrdId );
        buffer.append( mcanrejP8 );
        buffer.append( origClOrdId );
        buffer.append( mcanrejP9 );
        buffer.append( status.getVal() );
        buffer.append( mcanrejPA );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        MarketCancelRejectImpl reject = (MarketCancelRejectImpl) m1;

        return reject;
    }

    public static MarketVagueOrderRejectImpl getMarketVagueReject( ZString clOrdId, ZString rejectReason, boolean isTerminal ) {

        MarketVagueOrderRejectImpl reject = new MarketVagueOrderRejectImpl();
        reject.getClOrdIdForUpdate().copy( clOrdId );
        reject.getTextForUpdate().copy( rejectReason );
        reject.setIsTerminal( isTerminal );

        return reject;
    }

    public static MarketNewOrderAckImpl getMarketACK( ReusableString buffer,
                                                      FixDecoder decoder,
                                                      ViewString clOrdId,
                                                      double qty,
                                                      double price,
                                                      ReusableString orderId,
                                                      ReusableString execId ) {

        buffer.reset();

        buffer.append( mackP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mackP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mackP3 );
        buffer.append( orderId );
        buffer.append( mackP4 );
        buffer.append( qty );
        buffer.append( mackP5 );
        buffer.append( price );
        buffer.append( mackP6 );
        buffer.append( execId );
        buffer.append( mackP7 );
        buffer.append( clOrdId );
        buffer.append( mackP8 );

        long  now = Utils.nanoTime();
        Event m1  = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        MarketNewOrderAckImpl ack = (MarketNewOrderAckImpl) m1;

        ack.setAckReceived( now );

        return ack;
    }

    public static TradeNew getMarketTradeNew( ReusableString buffer,
                                              FixDecoder decoder,
                                              ZString mktOrderId,
                                              ZString mktClOrdId,
                                              OrderRequest creq,          // client request
                                              double lastQty,
                                              double lastPx,
                                              ZString fillExecId ) {
        buffer.reset();

        buffer.append( mfillP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mfillP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mfillP3 );
        buffer.append( mktOrderId );
        buffer.append( mfillP4 );

        if ( lastQty >= creq.getOrderQty() ) {
            buffer.append( OrdStatus.Filled.getVal() );
        } else {
            buffer.append( OrdStatus.PartiallyFilled.getVal() );
        }
        buffer.append( mfillP5 );
        buffer.append( lastQty );
        buffer.append( mfillP6 );
        buffer.append( lastPx );
        buffer.append( mfillP7 );
        buffer.append( fillExecId );
        buffer.append( mfillP8 );
        buffer.append( mktClOrdId );
        buffer.append( mfillP9 );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        TradeNew fill = (TradeNew) m1;

        return fill;
    }

    public static TradeNew getMarketTradeNew( ReusableString buffer,
                                              FixDecoder decoder,
                                              ZString mktOrderId,
                                              ZString mktClOrdId,
                                              double ordQty,
                                              double lastQty,
                                              double lastPx,
                                              ZString fillExecId ) {
        buffer.reset();

        buffer.append( mfillP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mfillP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mfillP3 );
        buffer.append( mktOrderId );
        buffer.append( mfillP4 );

        if ( lastQty >= ordQty ) {
            buffer.append( OrdStatus.Filled.getVal() );
        } else {
            buffer.append( OrdStatus.PartiallyFilled.getVal() );
        }
        buffer.append( mfillP5 );
        buffer.append( lastQty );
        buffer.append( mfillP6 );
        buffer.append( lastPx );
        buffer.append( mfillP7 );
        buffer.append( fillExecId );
        buffer.append( mfillP8 );
        buffer.append( mktClOrdId );
        buffer.append( mfillP9 );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        TradeNew fill = (TradeNew) m1;

        return fill;
    }

    public static NewOrderAck getMarketACK( FixDecoder decoder, String clOrdId, int qty, double price, ZString mktOrderId, ZString execId ) {
        // TOODO refactor to get rid of temp objs
        byte[] mack = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=225;35=8;49=CHIX;56=ME01;34=85795;52=" + FixTestUtils.getDateStr() +
                                                        "-08:29:08.622;57=ST;20=0;60=" + FixTestUtils.getDateStr() + "-08:29:08.622;" +
                                                        "37=" + new String( mktOrderId.getBytes() ) + ";150=0;39=0;40=2;54=1;38=" + qty + ";55=ICADp;44=" +
                                                        price + ";47=P;" + "59=0;109=ME01;14=0;6=0.00;17=" +
                                                        new String( execId.getBytes() ) + ";32=0;31=0.00;151=133;" +
                                                        "11=" + clOrdId + ";10=999;" );

        decoder.setReceived( Utils.nanoTime() );

        Event m1 = WarmupUtils.doDecode( decoder, mack, 0, mack.length );

        return (NewOrderAck) m1;
    }

    public static ClientCancelReplaceRequestImpl getClientCancelReplaceRequest( ReusableString buffer,
                                                                                FixDecoder decoder,
                                                                                ZString clOrdId,
                                                                                ZString origClOrdId,
                                                                                double qty,
                                                                                double price,
                                                                                EventHandler msgHandler ) {

        buffer.reset();
        buffer.append( ccrrP1 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( ccrrP2 );
        buffer.append( FixTestUtils.getDateBytes() );
        buffer.append( ccrrP3 );
        buffer.append( qty );
        buffer.append( ccrrP4 );
        buffer.append( clOrdId );
        buffer.append( ccrrP5 );
        buffer.append( price );
        buffer.append( ccrrP6 );
        buffer.append( origClOrdId );
        buffer.append( ccrrP7 );

        decoder.setReceived( Utils.nanoTime() );
        ClientCancelReplaceRequestImpl m1 = (ClientCancelReplaceRequestImpl) WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        m1.setEventHandler( msgHandler );

        return m1;
    }

    public static Replaced getMarketReplaced( ReusableString buffer,
                                              FixDecoder decoder,
                                              ZString clOrdId,
                                              ZString origClOrdId,
                                              double qty,
                                              double price,
                                              ZString orderId,
                                              ZString execId ) {

        buffer.reset();

        buffer.append( mrepP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mrepP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mrepP3 );
        buffer.append( orderId );
        buffer.append( mrepP4 );
        buffer.append( qty );
        buffer.append( mrepP5 );
        buffer.append( price );
        buffer.append( mrepP6 );
        buffer.append( execId );
        buffer.append( mrepP7 );
        buffer.append( clOrdId );
        buffer.append( mrepP8 );
        buffer.append( origClOrdId );
        buffer.append( mrepP9 );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        Replaced rep = (Replaced) m1;

        return rep;
    }

    public static TradeCancel getMarketTradeCancel( ReusableString buffer,
                                                    FixDecoder decoder,
                                                    ZString mktOrderId,
                                                    ZString mktClOrdId,
                                                    OrderRequest creq,          // client request
                                                    double lastQty,
                                                    double lastPx,
                                                    ZString execId,
                                                    ZString execRefId,
                                                    OrdStatus ordStatus ) {
        buffer.reset();

        buffer.append( mtrcanP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mtrcanP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mtrcanP3 );
        buffer.append( mktOrderId );
        buffer.append( mtrcanP4 );
        buffer.append( ordStatus.getVal() );
        buffer.append( mtrcanP5 );
        buffer.append( lastQty );
        buffer.append( mtrcanP6 );
        buffer.append( lastPx );
        buffer.append( mtrcanP7 );
        buffer.append( execId );
        buffer.append( mtrcanP8 );
        buffer.append( mktClOrdId );
        buffer.append( mtrcanP9 );
        buffer.append( execRefId );
        buffer.append( mtrcanPA );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        TradeCancel tradeCancell = (TradeCancel) m1;

        return tradeCancell;
    }

    public static TradeCorrect getMarketTradeCorrect( ReusableString buffer,
                                                      FixDecoder decoder,
                                                      ZString mktOrderId,
                                                      ZString mktClOrdId,
                                                      OrderRequest creq,          // client request
                                                      double lastQty,
                                                      double lastPx,
                                                      ZString execId,
                                                      ZString execRefId,
                                                      OrdStatus ordStatus ) {
        buffer.reset();

        buffer.append( mtrcorP1 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mtrcorP2 );
        buffer.append( FixTestUtils.getDateStr() );
        buffer.append( mtrcorP3 );
        buffer.append( mktOrderId );
        buffer.append( mtrcorP4 );
        buffer.append( ordStatus.getVal() );
        buffer.append( mtrcorP5 );
        buffer.append( lastQty );
        buffer.append( mtrcorP6 );
        buffer.append( lastPx );
        buffer.append( mtrcorP7 );
        buffer.append( execId );
        buffer.append( mtrcorP8 );
        buffer.append( mktClOrdId );
        buffer.append( mtrcorP9 );
        buffer.append( execRefId );
        buffer.append( mtrcorPA );

        Event m1 = WarmupUtils.doDecode( decoder, buffer.getBytes(), 0, buffer.length() );

        TradeCorrect tradeCorrect = (TradeCorrect) m1;

        return tradeCorrect;
    }

    public static Order createOrder( OrderRequest src ) {
        OrderImpl    order = new OrderImpl();
        OrderVersion ver   = new OrderVersion();

        ver.setBaseOrderRequest( src );

        double price = src.getPrice();

        final Currency clientCurrency  = src.getCurrency();
        final Currency tradingCurrency = src.getInstrument().getCurrency();

        if ( clientCurrency != tradingCurrency && clientCurrency.getMajorCurrency() == tradingCurrency.getMajorCurrency() ) {
            price = clientCurrency.majorMinorConvert( tradingCurrency, price );
        }

        ver.setMarketPrice( price );

        order.setLastAckedVerion( ver );
        order.setPendingVersion( ver );

        return order;
    }

    public static Order createOrder( ClientNewOrderSingleImpl nos, ClientCancelReplaceRequestImpl rep ) {
        Order order = createOrder( nos );

        OrderVersion pending = new OrderVersion();
        pending.setBaseOrderRequest( rep );

        double price = rep.getPrice();

        final Currency clientCurrency  = rep.getCurrency();
        final Currency tradingCurrency = rep.getInstrument().getCurrency();

        if ( clientCurrency != tradingCurrency && clientCurrency.getMajorCurrency() == tradingCurrency.getMajorCurrency() ) {
            price = clientCurrency.majorMinorConvert( tradingCurrency, price );
        }

        pending.setMarketPrice( price );

        order.setPendingVersion( pending );

        pending.setOrdStatus( OrdStatus.PendingReplace );

        return order;
    }




}
