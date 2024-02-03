package com.rr.core.loaders;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.TickBand;
import com.rr.core.model.TickManager;
import com.rr.core.model.TickScale;
import com.rr.core.utils.Utils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXMLTickManagerLoader extends BaseTestCase {

    @SuppressWarnings( "unchecked" )
    @Test public void testBandLoaded() {
        XMLTickManagerLoader l = new XMLTickManagerLoader();

        final TickManager tickMgr = (TickManager) l.create( "testTickLoader" );

        final TickScale ts = (TickScale) tickMgr.getTickType( ExchangeCode.XCME, new ViewString( "1" ) );

        assertNotNull( ts );

        assertEquals( "1", ts.getId().toString() );
        assertEquals( 3, ts.getNumBands() );

        checkBand( ts, 0, -Double.MAX_VALUE, -500.001, 10 );
        checkBand( ts, 1, -500, 500, 5 );
        checkBand( ts, 2, 500.001, Double.MAX_VALUE, 10 );

        assertEquals( 0, ts.getBandIdx( -505.0 ) );
        assertEquals( 1, ts.getBandIdx( -500.0 ) );
        assertEquals( 1, ts.getBandIdx( 500.0 ) );
        assertEquals( 2, ts.getBandIdx( 500.001 ) );

        assertTrue( ts.isValid( -510 ) );
        assertTrue( ts.isValid( -500 ) );
        assertTrue( ts.isValid( -495 ) );
        assertTrue( ts.isValid( 495 ) );
        assertTrue( ts.isValid( 500 ) );

        assertFalse( ts.isValid( -505 ) );
        assertFalse( ts.isValid( -501 ) );
        assertFalse( ts.isValid( 493 ) );
        assertFalse( ts.isValid( 501 ) );
        assertFalse( ts.isValid( 505 ) );
    }

    @Test public void testUnsetDouble() {

        double d1 = Constants.UNSET_DOUBLE;
        double d2 = 123.456;
        double d3 = Constants.UNSET_DOUBLE;

        assertTrue( d2 != d1 );

        boolean b1 = (d3 != d1);
        boolean b2 = (d3 == d1);

        assertTrue( d3 != d1 );     // NOTE : UNSET_DOUBLE != UNSET_DOUBLE  IS TRUE
        assertFalse( d1 == d3 );    // NOTE : UNSET_DOUBE  == UNSET_DOUBLE  IS FALSE

        assertTrue( Utils.isNull( d1 ) );
        assertTrue( Utils.isNull( d3 ) );
        assertFalse( Utils.hasVal( d1 ) );
        assertFalse( Utils.hasVal( d3 ) );
    }

    private void checkBand( final TickScale ts, final int idx, final double lower, final double upper, final double tickSize ) {
        TickBand b = ts.getBand( idx );

        assertNotNull( b );

        assertEquals( lower, b.getLower(), Constants.TICK_WEIGHT );
        assertEquals( upper, b.getUpper(), Constants.TICK_WEIGHT );
        assertEquals( tickSize, b.getTickSize(), Constants.TICK_WEIGHT );
    }
}
