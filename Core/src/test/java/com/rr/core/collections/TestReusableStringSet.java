/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestReusableStringSet extends BaseTestCase {

    @Test
    public void testSet() {
        ReusableStringSet set = new ReusableStringSet( 2 );

        assertTrue( set.put( new ReusableString( "AAAA00001" ) ) );
        assertFalse( set.put( new ReusableString( "AAAA00001" ) ) );
        assertEquals( 1, set.size() );
        assertTrue( set.contains( new ReusableString( "AAAA00001" ) ) );

        int max = 16384;

        for ( int i = 2; i < max; ++i ) {
            assertTrue( set.put( new ReusableString( "AAAA00001" + i ) ) );
            assertEquals( i, set.size() );
        }

        for ( int i = 2; i < max; ++i ) {
            assertTrue( set.contains( new ReusableString( "AAAA00001" + i ) ) );
        }

        assertFalse( set.contains( new ReusableString( "AAAA00001" + (max + 2) ) ) );

        set.clear();
        assertEquals( 0, set.size() );
    }
}
