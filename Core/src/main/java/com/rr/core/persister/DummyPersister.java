/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister;

import com.rr.core.lang.ReusableString;

import java.nio.ByteBuffer;

public class DummyPersister implements Persister {

    private volatile boolean _isOpen;

    @Override
    public ReusableString appendState( ReusableString logMsg ) {
        return logMsg;
    }

    @Override
    public void close() {
        _isOpen = false;
    }

    @Override public void flush()                                 { /* nothing */ }

    @Override
    public boolean isOpen() {
        return _isOpen;
    }

    @Override
    public void open() {
        _isOpen = true;
    }

    @Override
    public long persist( byte[] inBuffer, int offset, int length ) {
        return 0;
    }

    @Override
    public long persist( byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) {
        return 0;
    }

    @Override
    public int read( long key, byte[] outBuffer, int offset ) {
        return 0;
    }

    @Override
    public int read( long key, byte[] outBuffer, int offset, ByteBuffer optionalContext ) {
        return 0;
    }

    @Override
    public void replay( PersistentReplayListener listener ) {
        listener.completed();
    }

    @Override
    public void rollPersistence() {
        // nothing
    }

    @Override public void setFlushPerCall( final boolean enable ) { /* nothing */ }

    @Override
    public void setLowerFlags( long persistedKey, byte flags ) {
        // nothing
    }

    @Override
    public void setUpperFlags( long persistedKey, byte flags ) {
        // nothing
    }
}
