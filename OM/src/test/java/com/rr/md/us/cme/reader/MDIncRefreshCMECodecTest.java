/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.binary.fastfix.FastFixDecoder;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.md.us.cme.FastFixTstUtils;
import com.rr.md.us.cme.writer.CMEFastFixEncoder;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import org.junit.Before;
import org.junit.Test;

public class MDIncRefreshCMECodecTest extends BaseTestCase {

    private static final Logger _log = LoggerFactory.create( MDIncRefreshCMECodecTest.class );

    private Decoder           _decoder = new CMEFastFixDecoder( "CMETstReader", "data/cme/templates.xml", -1, true );
    private CMEFastFixEncoder _encoder = new CMEFastFixEncoder( "CMETstWriter", "data/cme/templates.xml", true );

    private final byte[] _buf = _encoder.getBytes();

    private String    _dateStr = "20120403";
    private TimeUtils _calc;

    public MDIncRefreshCMECodecTest() {
        // nothing
    }

    @Before
    public void setUp() {
        _calc = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _decoder.setTimeUtils( _calc );
    }

    @Test
    public void testFiveMDEntry() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );

        _encoder.encode( inc );

        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );

        FastFixTstUtils.checkEqualsA( inc, dec );
    }

    @Test
    public void testFiveMDEntryT103() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );
        _encoder.encode( inc, 103, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        FastFixTstUtils.checkEqualsA( inc, dec );
    }

    @Test
    public void testFiveMDEntryT109() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );
        _encoder.encode( inc, 109, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        FastFixTstUtils.checkEqualsB( inc, dec );
    }

    @Test
    public void testFiveMDEntryT81() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );
        _encoder.encode( inc, 81, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        FastFixTstUtils.checkEqualsA( inc, dec );
    }

    @Test
    public void testFiveMDEntryT83() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );
        _encoder.encode( inc, 83, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        FastFixTstUtils.checkEqualsA( inc, dec );
    }

    @Test
    public void testFiveMDEntryT84() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 5 );
        _encoder.encode( inc, 84, (byte) 3 );
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );
        FastFixTstUtils.checkEqualsB( inc, dec );
    }

    @Test
    public void testOneMDEntry() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( 1 );

        // encode to String
        _encoder.encode( inc );

        //  encode String to fastfix

        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );

        FastFixTstUtils.checkEqualsA( inc, dec );
    }

    @Test
    public void testTopMDEntry() {
        MDIncRefreshImpl inc = FastFixTstUtils.makeTOPBookIncRefresh();

        // encode to String
        _encoder.encode( inc, 83, (byte) 0 );

        ReusableString destFixMsg = new ReusableString();
        destFixMsg.reset();
        destFixMsg.append( "MSG T83  " );
        destFixMsg.appendHEX( _encoder.getBytes(), _encoder.getOffset(), _encoder.getLength() );

        _log.info( destFixMsg );

        //  encode String to fastfix

        ((FastFixDecoder) _decoder).setNextDummy();
        _decoder.parseHeader( _buf, 0, _encoder.getLength() );
        MDIncRefreshImpl dec = (MDIncRefreshImpl) _decoder.decode( _buf, 0, _encoder.getLength() );

        FastFixTstUtils.checkEqualsA( inc, dec );
    }
}
