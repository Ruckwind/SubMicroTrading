/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister;

import java.nio.ByteBuffer;

public class DummyIndexPersister extends DummyPersister implements IndexPersister {

    @Override
    public boolean addIndexEntries( int fromSeqNum, int toSeqNum ) {
        return true;
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) {
        return 0;
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length ) {
        return 0;
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset, ByteBuffer optionalContext ) {
        return 0;
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset ) {
        return 0;
    }

    @Override
    public boolean removeIndexEntries( int fromSeqNum, int toSeqNum ) {
        return true;
    }

    @Override
    public boolean verifyIndex( long key, int appSeqNum ) {
        return true;
    }
}
