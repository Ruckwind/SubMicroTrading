/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.ZString;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.PersisterException;
import com.rr.core.persister.memmap.MemMapPersister;
import com.rr.core.utils.ThreadPriority;

import java.nio.ByteBuffer;

/**
 * millenium doesnt provide a unique message sequence number and doesnt take one
 * <p>
 * millenium persist logs simply need to act as sequential transaction logs
 * <p>
 * adapts index writes into serial writes
 */

public class SequentialPersister extends MemMapPersister implements IndexPersister {

    public SequentialPersister( ZString name, ZString fname, long filePreSize, int pageSize, ThreadPriority priority ) {
        super( name, fname, filePreSize, pageSize, priority );
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length ) throws PersisterException {
        return persist( inBuffer, offset, length );
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) throws PersisterException {
        return persist( inBuffer, offset, length, optional, optOffset, optLen );
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset ) throws PersisterException {
        throw new PersisterException( "Indexing not supported by MilleniumPersister" );
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset, ByteBuffer optionalContext ) throws PersisterException {
        throw new PersisterException( "Indexing not supported by MilleniumPersister" );
    }

    @Override
    public boolean addIndexEntries( int fromSeqNum, int toSeqNum ) {
        return false;
    }

    @Override
    public boolean removeIndexEntries( int fromSeqNum, int toSeqNum ) {
        return false;
    }

    @Override
    public boolean verifyIndex( long key, int appSeqNum ) {
        return true;
    }
}
