package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import java.io.BufferedOutputStream;

public class TestBufferedGZipFile extends BaseTestCase {

    @Test public void testGzipWhenDontExceedBuffer() throws Exception {
        String testFile = "./persist/testFile.gz";

        FileUtils.rmIgnoreError( testFile );
        BufferedOutputStream writer = FileUtils.bufFileOutStream( testFile, 8 * 1024 * 1024 );

        final int numLines = 100;

        for ( int i = 0; i < numLines; i++ ) {
            String line = makeLine( i );

            writer.write( line.getBytes(), 0, line.length() );
        }

        writer.flush();

    }

    private String makeLine( final int i ) {
        return "L" + i + ":ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";
    }
}
