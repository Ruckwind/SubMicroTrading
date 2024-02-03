package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleIntCalcTest extends BaseTestCase {

    @Test public void failUseCase1() {
        String in = " 15000 + 770 ";

        int out = SimpleIntCalc.evaluate( in );

        assertEquals( 15770, out );
    }
}
