/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.RTStartupException;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatsCfgFileTest extends BaseTestCase {

    @Test
    public void testPersist() throws FileException {

        String tmpFile = "./tmp/testCfgFile.cfg";

        try {
            StatsCfgFile stats = new StatsCfgFile();

            stats.initialise();

            FileUtils.mkDirIfNeeded( tmpFile );

            stats.setFile( tmpFile );
            stats.set( SizeType.DEFAULT_STRING_LENGTH, 20 );

            stats.store();
            stats.set( SizeType.DEFAULT_STRING_LENGTH, 30 );
            stats.reload();

            int stringDefault = stats.find( SizeType.DEFAULT_STRING_LENGTH );

            assertEquals( 20, stringDefault );
        } finally {
            FileUtils.rmIgnoreError( tmpFile );
        }
    }

    @Test
    public void testStatsCfgFile() {

        Stats stats = new StatsCfgFile();

        stats.initialise();

        int stringDefault = stats.find( SizeType.DEFAULT_STRING_LENGTH );

        assertEquals( 10, stringDefault );
    }

    @Test
    public void testUnknownStat() {

        StatsCfgFile stats = new StatsCfgFile();

        try {
            int res = stats.find( SizeType.DEFAULT_STRING_LENGTH );

            assertEquals( SizeType.DEFAULT_STRING_LENGTH.getSize(), res );

        } catch( RTStartupException e ) {
            fail( "Expected RTStartupException to be thrown for unknown type" );
        }
    }
}
