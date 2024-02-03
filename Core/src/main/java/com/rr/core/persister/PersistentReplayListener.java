/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister;

public interface PersistentReplayListener {

    void completed();

    void failed();

    /**
     * @param p      persister that is replaying the record
     * @param key    long key that can be used to reread the record later if needed
     * @param buf    buffer that holds the recovered message
     * @param offset
     * @param len
     * @param flags  bit flags persisted with the record
     */
    void message( Persister p, long key, byte[] buf, int offset, int len, short flags );

    /**
     * call back for a record persisted with an optional context buffer
     */
    void message( Persister p, long key, byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, short flags );

    void started();

}
