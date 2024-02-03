/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister;

import com.rr.core.lang.ReusableString;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Messages are persisted at the boundary
 * <p>
 * in general the message persisted is exactly the same as the message received or sent, possibly with an extra context block if needed
 * <p>
 * this means on replay, the session that stored the message needs to decode it from byte[]to PoJo, replay into the session
 * also allows the session to consume the context block as needed
 *
 * @NOTE only one thread can be writing and one thread reading,  if need more readers then SYNC the read
 * @NOTE BE VERY CAREFUL TO CHECKOUT THE RESOURCE COST OF A PERSISTER ESPECIALLY AS EACH SESSION WILL HAVE TWO
 */
public interface Persister {

    /**
     * append current state to logger message
     *
     * @param logMsg
     * @return the supplied buf
     */
    ReusableString appendState( ReusableString logMsg );

    /**
     * close down persistence freeing up resources
     */
    void close();

    /**
     * force flush to disk
     */
    void flush();

    boolean isOpen();

    /**
     * open required resources and get ready for persistence
     *
     * @throws PersisterException
     */
    void open() throws PersisterException;

    /**
     * persist the buffer for the messsage
     *
     * @return a long key which can be used to identify a persisted record used in some update methods
     * @throws PersisterException
     * @throws IOException
     */
    long persist( byte[] inBuffer, int offset, int length ) throws PersisterException;

    /**
     * persist the buffer for the messsage and an optional context buffer
     *
     * @return a long key which can be used to identify a persisted record used in some update methods
     * @throws PersisterException
     * @throws IOException
     */
    long persist( byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) throws PersisterException;

    /**
     * retrieve the record at the specified key location
     *
     * @return number of bytes read into the buffer
     * @throws PersisterException
     */
    int read( long key, byte[] outBuffer, int offset ) throws PersisterException;

    /**
     * retrieve the record at the specified key location, also populates the optionalContext buffer which must be big enough to accomodate it
     *
     * @return number of bytes placed into outBuffer
     * @throws PersisterException
     */
    int read( long key, byte[] outBuffer, int offset, ByteBuffer optionalContext ) throws PersisterException;

    /**
     * replay all messages in the file from start too finish
     * <p>
     * at the end of replay the persister ready to start amending new records
     *
     * @throws PersisterException
     */

    void replay( PersistentReplayListener listener ) throws PersisterException;

    /**
     * archive existing lva, then start with persistence empty/clean, acts like truncation
     *
     * @throws PersisterException if unable to reopen persistence file
     */
    void rollPersistence() throws PersisterException;

    /**
     * @param enable if enable force flush per call ... NOT RECOMMENDED !
     */
    void setFlushPerCall( boolean enable );

    /**
     * find the record identified by the key then "OR" the lower flag byte with supplied flag
     * <p>
     * as memory is being written too directly it isnt possible to force flush from CPU cache
     * so be careful using the flags
     *
     * @throws PersisterException
     */
    void setLowerFlags( long persistedKey, byte flags ) throws PersisterException;

    /**
     * find the record identified by the key then "OR" the upper (MSB) flag byte with supplied flag
     * <p>
     * as memory is being written too directly it isnt possible to force flush from CPU cache
     * so be careful using the flags
     *
     * @throws PersisterException
     */
    void setUpperFlags( long persistedKey, byte flags ) throws PersisterException;
}
