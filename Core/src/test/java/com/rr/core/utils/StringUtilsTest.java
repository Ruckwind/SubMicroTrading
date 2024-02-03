package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest extends BaseTestCase {

    @Test public void countMatch() {
        doCount( "AAAEBAEBC", "A*EBC", 1 );

        doCount( "abcdefgabcd", "a*d", 2 );
        doCount( "abcdefgabcd", "x", 0 );
        doCount( "abcdefgabcd", "", 0 );
        doCount( "abcdefgabcd", "bc", 2 );
        doCount( "abcdefgabcd", "a*de", 1 );

        doCount( "abcdefgabcd", "a*cd", 2 );
        doCount( "abcdefgabcd", "*", 1 );
        doCount( "abcdefgabcd", "abcd", 2 );

        doCount( "abcdefgabcd", "a*cd", 2 );
        doCount( "abcdefgabcd", "ab", 2 );
        doCount( "abcdefgabcd", "b*a", 1 );
        doCount( "abcdefgabcd", "**d", 2 );
        doCount( "abcdefgabcd", "**b", 1 );
        doCount( "abcdefgabcd", "c*e*c", 1 );
        doCount( "abcdefgabcd", "*b*d", 2 );
        doCount( "abcdefgabcd", "abcd", 2 );
        doCount( "abcdefgabcd", "abcd*", 1 );
        doCount( "abcdefgabcd", "cd", 2 );
        doCount( "abcdefgabcd", "*", 1 );
        doCount( "abcdefgabcd", "a*e", 1 );
        doCount( "abcdefgabcd", "a*", 1 );
        doCount( "abcdefgabcd", "*a", 1 );
        doCount( "abcdefgabcd", "*e", 1 );
        doCount( "abcdefgabcd", "*d", 2 );
        doCount( "abcdefgabcd", "d*", 1 );
        doCount( "abcdefgabcd", "abcd", 2 );
        doCount( "abcdefgabcd", "a*a", 1 );
        doCount( "", "d*", 0 );
        doCount( "", "*", 0 );
        doCount( "a", "*", 1 );
    }

    @Test public void split() {
        doSplit( "a,b,c", 1, '.' );
        doSplit( "a,b,c", 3, ',' );
        doSplit( ",a,b,c", 4, ',' );
        doSplit( ",a,b,c,", 5, ',' );
        doSplit( "abc", 1, ',' );
        doSplit( "", 0, ',' );
    }

    private void doCount( final String str, final String ptn, final int expCnt ) {
        final byte[] strBytes = str.getBytes();
        int          cnt      = StringUtils.count( strBytes, ptn.getBytes() );
        assertEquals( expCnt, cnt );
    }

    private void doSplit( final String line, final int expCnt, char delim ) {
        final ArrayList<String> bits = new ArrayList<>();

        StringUtils.split( line, delim, bits );

        assertEquals( expCnt, bits.size() );
    }
}
