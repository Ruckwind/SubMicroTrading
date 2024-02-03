/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestLogLargeHex extends BaseTestCase {

    public static  String CR            = "\n";
    private static int    _skipHdrBytes = "20180730-10:36:42.493 (BST) ".length();

    private static String _expSmall = "[info]   " + CR + CR +
                                      "0000 .ABCDE...ABCDE...ABCDE...ABCDE..                  " + CR +
                                      "     1        10        20        30        40        50" + CR + CR +
                                      "0000 09 41 42 43 44 45 FF 10 09 41 42 43 44 45 FF 11 09 41 42 43 44 45 FF 12 09 41 42 43 44 45 FF 13" + CR +
                                      "     1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50" + CR +
                                      "byteCount=32" + CR;

    private static String _expLarge = "[info]   " + CR + CR +
                                      "0000 .abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLM" + CR +
                                      "0050 NOPQRSTUVWXYZ...abcdefghijklmnopqrstuvwxyz12345678" + CR +
                                      "0100 90ABCDEFGHIJKLMNOPQRSTUVWXYZ...abcdefghijklmnopqrs" + CR +
                                      "0150 tuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ...abcd" + CR +
                                      "0200 efghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQR" + CR +
                                      "0250 STUVWXYZ..                                        " + CR +
                                      "     1        10        20        30        40        50" + CR + CR +
                                      "0000 01 61 62 63 64 65 66 67 68 69 6A 6B 6C 6D 6E 6F 70 71 72 73 74 75 76 77 78 79 7A 31 32 33 34 35 36 37 38 39 30 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D" + CR +
                                      "0050 4E 4F 50 51 52 53 54 55 56 57 58 59 5A FF 00 01 61 62 63 64 65 66 67 68 69 6A 6B 6C 6D 6E 6F 70 71 72 73 74 75 76 77 78 79 7A 31 32 33 34 35 36 37 38" + CR +
                                      "0100 39 30 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A FF 01 01 61 62 63 64 65 66 67 68 69 6A 6B 6C 6D 6E 6F 70 71 72 73" + CR +
                                      "0150 74 75 76 77 78 79 7A 31 32 33 34 35 36 37 38 39 30 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A FF 02 01 61 62 63 64" + CR +
                                      "0200 65 66 67 68 69 6A 6B 6C 6D 6E 6F 70 71 72 73 74 75 76 77 78 79 7A 31 32 33 34 35 36 37 38 39 30 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50 51 52" + CR +
                                      "0250 53 54 55 56 57 58 59 5A FF 03" + CR +
                                      "     1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50" + CR +
                                      "byteCount=260" + CR;

    private static String _expTruncate = "[info]   .abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZÿ..abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZÿ..abcdefghijklm[TRUNCATED]";

    private static String makeSmall() {

        ReusableString a = new ReusableString();
        a.append( (byte) 0x09 ).append( "ABCDE" ).append( (byte) 0xFF );

        ReusableString b = new ReusableString();
        b.append( a ).append( (byte) 0x10 ).append( a ).append( (byte) 0x11 ).append( a ).append( (byte) 0x12 ).append( a ).append( (byte) 0x13 );

        return b.toString();
    }

    private static String makeLarge() {

        ReusableString a = new ReusableString();
        a.append( (byte) 0x01 ).append( "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" ).append( (byte) 0xFF );

        ReusableString b = new ReusableString();
        b.append( a ).append( (byte) 0x00 ).append( a ).append( (byte) 0x01 ).append( a ).append( (byte) 0x02 ).append( a ).append( (byte) 0x03 );

        return b.toString();
    }

    @Test
    public void testHexLarge() {
        doTest( makeLarge(), _expLarge );
    }

    @Test
    public void testHexSmall() {
        doTest( makeSmall(), _expSmall );
    }

    @Test
    public void testTruncate() {
        LogEventLarge l = new LogEventLarge();

        ReusableString r = new ReusableString( makeLarge() );

        l.set( Level.info, r, 0 );

        ByteBuffer b = ByteBuffer.allocate( 200 );

        l.encode( b );

        b.flip();

        ReusableString res = new ReusableString( b.array(), 0, b.limit() );

        System.out.println( res.toString() );

        assertTrue( res.toString().substring( _skipHdrBytes ).startsWith( _expTruncate ) );
    }

    private void doTest( String src, String exp ) {
        LogEventLarge l = new LogEventLarge();

        ReusableString r = new ReusableString( src );

        l.set( Level.info, r, 0 );

        ByteBuffer b = ByteBuffer.allocate( 4096 );

        l.encode( b );

        b.flip();

        ReusableString res = new ReusableString( b.array(), 0, b.limit() );

        String resStr = res.toString();

        resStr = resStr.substring( resStr.indexOf( '[' ) );
        System.out.println( res.toString() );

        for ( int i = 0; i < exp.length(); i++ ) {
            assertEquals( "Index " + i, exp.charAt( i ), resStr.charAt( i ) );
        }

        assertEquals( exp.length(), resStr.length() );
    }
}
