/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.lang.ReusableString;
import com.rr.core.persister.IndexPersister;
import com.rr.core.persister.PersistentReplayListener;
import com.rr.core.persister.PersisterException;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MemoryIndexedPersister implements IndexPersister {

    private          ArrayList<ReusableString> _entries = new ArrayList<>();
    private volatile boolean                   _isOpen;

    @Override
    public boolean addIndexEntries( int fromSeqNum, int toSeqNum ) {

        _entries.ensureCapacity( toSeqNum + 1 );

        return true;
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) throws PersisterException {
        throw new PersisterException( "persistIdx with optional buffer not supported" );
    }

    @Override
    public long persistIdxAndRec( int appSeqNum, byte[] inBuffer, int offset, int length ) {

        _entries.ensureCapacity( appSeqNum + 1 );

        if ( appSeqNum < _entries.size() ) {
            ReusableString rs = _entries.get( appSeqNum );
            if ( rs == null ) {
                rs = new ReusableString( inBuffer, offset, length );
                _entries.add( appSeqNum, rs );
            } else {
                rs.setValue( inBuffer, offset, length );
            }
        } else {
            ReusableString rs = new ReusableString( inBuffer, offset, length );

            for ( int idx = _entries.size(); idx <= appSeqNum; idx++ ) {
                _entries.add( null );
            }

            _entries.set( appSeqNum, rs );
        }

        return appSeqNumToKey( appSeqNum );
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset, ByteBuffer optionalContext ) throws PersisterException {
        throw new PersisterException( "readFromIdx with optional buffer not supported" );
    }

    @Override
    public int readFromIndex( int appSeqNum, byte[] outBuffer, int offset ) {
        _entries.ensureCapacity( appSeqNum + 1 );

        int bytes = 0;

        ReusableString rs = _entries.get( appSeqNum );

        if ( rs != null ) {
            bytes = rs.length();

            rs.getBytes( outBuffer, offset );
        }

        return bytes;
    }

    @Override
    public boolean removeIndexEntries( int fromSeqNum, int toSeqNum ) {
        _entries.ensureCapacity( toSeqNum + 1 );

        for ( int i = fromSeqNum; i < toSeqNum; ++i ) {
            _entries.set( i, null );
        }

        return true;
    }

    @Override
    public boolean verifyIndex( long key, int appSeqNum ) {
        return true;
    }

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
        _entries.add( new ReusableString( inBuffer, offset, length ) );

        return appSeqNumToKey( _entries.size() - 1 );
    }

    @Override
    public long persist( byte[] inBuffer, int offset, int length, byte[] optional, int optOffset, int optLen ) throws PersisterException {
        throw new PersisterException( "persist with optional buffer not supported" );
    }

    @Override
    public int read( long key, byte[] outBuffer, int offset ) {

        int appSeqNum = keyToAppSeqNum( key );

        _entries.ensureCapacity( appSeqNum + 1 );

        int bytes = 0;

        ReusableString rs = _entries.get( appSeqNum );

        if ( rs != null ) {
            bytes = rs.length();

            rs.getBytes( outBuffer, offset );
        }

        return bytes;
    }

    @Override
    public int read( long key, byte[] outBuffer, int offset, ByteBuffer optionalContext ) throws PersisterException {
        return read( key, outBuffer, offset );
    }

    @Override
    public void replay( PersistentReplayListener listener ) {

        listener.started();

        int entries = _entries.size();

        for ( int key = 0; key < entries; ++key ) {
            ReusableString rs = _entries.get( key );
            listener.message( this, key, rs.getBytes(), rs.getOffset(), rs.length(), (short) 0 );
        }

        listener.completed();
    }

    @Override
    public void rollPersistence() {
        _entries.clear();
    }

    @Override public void setFlushPerCall( final boolean enable ) { /* nothing */ }

    @Override
    public void setLowerFlags( long persistedKey, byte flags ) {
        // do nothing
    }

    @Override
    public void setUpperFlags( long persistedKey, byte flags ) {
        // do nothing
    }

    private int appSeqNumToKey( int appSeqNum ) {
        return appSeqNum + 1000000;
    }

    private int keyToAppSeqNum( long key ) {
        return (int) (key - 1000000);
    }
}
