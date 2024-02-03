/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.instrument;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.model.TickBand;
import com.rr.core.model.TickScale;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTickBands extends BaseTestCase {

    @Test
    public void testAddBadBands() {
        doTestFail( 6, 5, 0.005 );
        doTestFail( 1, 5, 0 );
    }

    @Test
    public void testOneBand() {

        double z = Constants.UNSET_DOUBLE;

        doTestOneBand( 0, 5, 0.0025, 3.005, 3.00251, " price 3.00251 not a multiple of 0.0025" );
        doTestOneBand( 2, 6, 2, 4, 4.00001, " price 4.00001 not a multiple of 2.0" );
        doTestOneBand( 2, z, 2, 4, 4.00001, " price 4.00001 not a multiple of 2.0" );
    }

    @Test
    public void testThreeBands() {

        double z = Constants.UNSET_DOUBLE;

        TickScale bands = new TickScale( new ViewString( "SCALE1" ) );
        bands.addBand( new TickBand( 0.0, 5.0, 0.00025 ) );
        bands.addBand( new TickBand( 5.00025, 10.0, 0.0005 ) );
        bands.addBand( new TickBand( 15.00025, z, 0.05 ) );

        doTestThreeBands( bands, 3.005, -1.0, " Price doesnt fall within any tick band, price=-1.0" );
        doTestThreeBands( bands, 3.005, 3.00003, " price 3.00003 not a multiple of 0.00025" );
        doTestThreeBands( bands, 3.005, 3.00251, " price 3.00251 not a multiple of 0.00025" );
        doTestThreeBands( bands, 5.0, 5.00255, " price 5.00255 not a multiple of 0.0005" );
        doTestThreeBands( bands, 17.35, 17.13, " price 17.13 not a multiple of 0.05" );
    }

    private void doTestFail( double lower, double upper, double tickSize ) {
        TickScale bands = new TickScale( new ViewString( "SCALE1" ) );

        try {
            bands.addBand( new TickBand( lower, upper, tickSize ) );

            assertTrue( "didnt fail as expected", false );

        } catch( RuntimeException e ) {
            assertTrue( e.getMessage().startsWith( "Invalid band" ) );
        }
    }

    private void doTestOneBand( double lower, double upper, double tickSize, double sampleGoodPrice, double sampleBadPrice, String err ) {
        TickScale bands = new TickScale( new ViewString( "SCALE1" ) );
        bands.addBand( new TickBand( lower, upper, tickSize ) );

        assertTrue( bands.canVerifyPrice() );
        assertTrue( bands.isValid( sampleGoodPrice ) );
        assertFalse( bands.isValid( sampleBadPrice ) );

        ReusableString msg = new ReusableString();
        bands.writeError( sampleBadPrice, msg );
        assertEquals( err, msg.toString() );
    }

    private void doTestThreeBands( TickScale bands,
                                   double sampleGoodPrice,
                                   double sampleBadPrice,
                                   String err ) {

        assertTrue( bands.canVerifyPrice() );
        assertTrue( bands.isValid( sampleGoodPrice ) );
        assertFalse( bands.isValid( sampleBadPrice ) );

        ReusableString msg = new ReusableString();
        bands.writeError( sampleBadPrice, msg );
        assertEquals( err, msg.toString() );
    }
}
