/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister.memmap;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.persister.PersistentReplayListener;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ThreadPriority;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

public abstract class BaseTestMemMapPersister extends BaseTestCase {

    private static int _idx = 0;
    protected final ReusableString _base1  = new ReusableString( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
    protected final ReusableString _base2  = new ReusableString( "abcdefghijklmnopqrstuvwzyz" );
    protected final byte[]         _readBuf  = new byte[ Constants.MAX_BUF_LEN ];
    protected final ReusableString _readWrap = new ReusableString();
    final           ReusableString _genBuf = new ReusableString();
    protected MemMapPersister _persister = null;
    protected boolean _startedRecovery   = false;
    protected boolean _completedRecovery = false;
    protected int     _count             = 0;

    private synchronized static int nextIdx() {
        return ++_idx;
    }

    @Before
    public void setUp() {
        try {
            ReusableString fileName = new ReusableString( getFileNameBase() );
            fileName.append( nextIdx() ).append( ".dat" );
            FileUtils.rmIgnoreError( fileName.toString() );
            _persister = new MemMapPersister( new ViewString( "TestPersist" ), fileName, getFilePreSize(), getPageSize(), ThreadPriority.Other );
            _persister.setLogTimes( false );
            _persister.open();

            _readWrap.setBuffer( _readBuf, 0 );

        } catch( Exception e ) {
            e.printStackTrace();
            assertFalse( "FAIL: " + e.getClass().getName() + " : " + e.getMessage(), true );
        }
    }

    @After
    public void tearDown() {
        _persister.shutdown();
    }

    protected void checkRecovery( int expRecs ) throws PersisterException {
        _count             = 0;
        _startedRecovery   = false;
        _completedRecovery = false;
        getPersister().close();
        getPersister().open();
        getPersister().replay( new PersistentReplayListener() {

            @Override
            public void started() {
                _startedRecovery = true;
                assertEquals( 0, _count );
            }

            @Override
            public void completed() { _completedRecovery = true; }

            @Override
            public void failed() { fail(); }

            @Override
            public void message( Persister p, long key, byte[] buf, int offset, int len, short flags ) {
                int seqNum = _count++;

                formLine( seqNum );

                assertEquals( 0, offset );
                _readWrap.setBuffer( buf, len );

                assertEquals( _genBuf, _readWrap );

                assertEquals( 0, flags );   // this test didnt set any flags

                verify( p, key, seqNum );
            }

            @Override
            public void message( Persister p, long key, byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, short flags ) {
                fail();
            }
        } );

        assertTrue( _startedRecovery );
        assertTrue( _completedRecovery );
        assertEquals( expRecs, _count );
    }

    protected abstract ViewString getFileNameBase();

    protected int getFilePreSize()  { return 10000; }

    protected int getPageSize()     { return 512; }

    protected Persister getPersister() {
        return _persister;
    }

    protected int getUnmapNumRecs() { return 100; }

    protected void readAndVerifyLine( int idx, long key ) {

        try {
            int bytes = getPersister().read( key, _readBuf, 0 );

            _readWrap.setLength( bytes );

            formLine( idx );

            assertEquals( _genBuf, _readWrap );
        } catch( Exception e ) {
            assertTrue( "Failed to read row " + idx, false );
        }
    }

    protected void verify( int startIdx, int total, long[] pkeys ) {
        _readWrap.setBuffer( _readBuf, 0 );

        for ( int i = startIdx; i < total; ++i ) {
            readAndVerifyLine( i, pkeys[ i ] );
        }
    }

    protected void verify( Persister p, long key, int index ) {
        // nothing
    }

    protected long writeLine( int i ) {
        try {
            formLine( i );
            return getPersister().persist( _genBuf.getBytes(), 0, _genBuf.length() );
        } catch( PersisterException e ) {
            assertTrue( "Failed to persist row " + i, false );
        }

        return -1;
    }

    protected void writeRecs( int startIdx, int total, long[] pkeys ) {
        long pkey;
        for ( int i = startIdx; i < total; ++i ) {
            pkey = writeLine( i );

            if ( pkeys != null && i < pkeys.length ) pkeys[ i ] = pkey;
        }
    }

    void formLine( int i ) {
        _genBuf.copy( _base1 );
        _genBuf.append( i );
        _genBuf.append( _base2 );
    }
}
