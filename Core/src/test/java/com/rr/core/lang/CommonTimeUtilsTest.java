package com.rr.core.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommonTimeUtilsTest extends BaseTestCase {

    @Test public void testCSVTime() {
        doTime( 0, "00:00:00" );
        doTime( 1, "00:00:01" );
        doTime( 10, "00:00:10" );
        doTime( 12, "00:00:12" );
        doTime( 103, "00:01:03" );
        doTime( 120, "00:01:20" );
        doTime( 123, "00:01:23" );
        doTime( 1034, "00:10:34" );
        doTime( 1234, "00:12:34" );
        doTime( 10345, "01:03:45" );
        doTime( 12345, "01:23:45" );
        doTime( 100406, "10:04:06" );
        doTime( 123456, "12:34:56" );
    }

    private void doTime( final int time, final String timeStr ) {
        ReusableString dest = new ReusableString();

        CommonTimeUtils.formatTime( time, dest );

        assertEquals( timeStr, dest.toString() );
    }
}
