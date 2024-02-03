package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPatternToValueMatcher extends BaseTestCase {

    @Test
    public void javaPtn() {
        Pattern p = Pattern.compile( "^SP.*" );

        Matcher m = p.matcher( "SP" );
        assertTrue( m.matches() );

        m = p.matcher( "SP" );
        assertTrue( m.matches() );

        m = p.matcher( "SP1C" );
        assertTrue( m.matches() );
    }

    @Test
    public void javaPtn2() {
        Pattern p = Pattern.compile( "^SP\\.(csv|001)$" );

        Matcher m = p.matcher( "SP.001" );
        assertTrue( m.matches() );
    }
}
