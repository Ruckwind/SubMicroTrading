package com.rr.core.utils;

import com.rr.core.lang.Constants;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void doubleComparison() {
        assertEquals( 1, Utils.compare( Constants.UNSET_DOUBLE, 100, Constants.TICK_WEIGHT ) );
        assertEquals( -1, Utils.compare( 100, Constants.UNSET_DOUBLE, Constants.TICK_WEIGHT ) );
        assertEquals( 0, Utils.compare( 100, 100, Constants.TICK_WEIGHT ) );
        assertEquals( 0, Utils.compare( 100, 100.01, 0.1 ) );
    }
}
