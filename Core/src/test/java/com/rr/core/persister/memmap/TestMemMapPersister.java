/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister.memmap;

import com.rr.core.lang.ViewString;
import com.rr.core.persister.PersisterException;
import org.junit.Test;

public class TestMemMapPersister extends BaseTestMemMapPersister {

    @Override protected ViewString getFileNameBase() {
        return new ViewString( "./tmp/TestMemMapPersister" );
    }

    @Test
    public void testInterleavedFlagMark() throws PersisterException {

        int    numRecs = getNumRecs();
        long   pkey;
        long[] pkeys   = new long[ numRecs ];

        for ( int i = 0; i < numRecs; ++i ) {
            pkey = writeLine( i );

            pkeys[ i ] = pkey;

            if ( i > 0 ) {
                _persister.setUpperFlags( pkeys[ i - 1 ], (byte) 16 );
            }
        }

        for ( int i = 0; i < numRecs; ++i ) {
            readAndVerifyLine( i, pkeys[ i ] );
        }
    }

    @Test
    public void testReplayAndPostReplayAppending() throws PersisterException {
        int    numRecs       = getNumRecs();
        int    runTwoTotal   = numRecs * 2;
        int    runThreeTotal = numRecs * 3;
        long[] pkeys         = new long[ runThreeTotal ];

        writeRecs( 0, numRecs, pkeys );

        checkRecovery( numRecs );

        writeRecs( numRecs, runTwoTotal, pkeys );

        verify( 0, runTwoTotal, pkeys );

        checkRecovery( runTwoTotal );

        writeRecs( runTwoTotal, runThreeTotal, pkeys );

        checkRecovery( runThreeTotal );

        verify( 0, runThreeTotal, pkeys );
    }

    @Test
    public void testSimple() {

        int    numRecs = getNumRecs();
        long   pkey;
        long[] pkeys   = new long[ numRecs ];

        for ( int i = 0; i < numRecs; ++i ) {
            pkey = writeLine( i );

            pkeys[ i ] = pkey;
        }

        for ( int i = 0; i < numRecs; ++i ) {
            readAndVerifyLine( i, pkeys[ i ] );
        }
    }

    // frow file to 4 GB file .. shouldnt run out of memory 
    @Test
    public void testUnMap() {
        int numRecs = getUnmapNumRecs();

        writeRecs( 0, numRecs, null );
    }

    protected int getNumRecs() { return 1000; }
}
