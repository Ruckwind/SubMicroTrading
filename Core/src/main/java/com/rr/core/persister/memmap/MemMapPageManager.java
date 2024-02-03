/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister.memmap;

import com.rr.core.java.JavaSpecific;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * uses a background thread to get the nextPage whenever it becomes null
 * <p>
 * ONLY FOR USE BY OWNING Persister is NOT THREADSAFE FOR ANY OTHER USE ie dont share instance among persisters
 * <p>
 * popPage may be used by a thread other than the owning persister thread
 * eg off the FixInbound thread to fetch messages from theoutboound persister which need to be sent as gap fill
 */
public class MemMapPageManager {

    static final ErrorCode PERSIST_ERR = new ErrorCode( "MMBA100", "MemMap map error " );
    private static final Logger  _log                   = LoggerFactory.create( MemMapPageManager.class );
    private static final ZString SYNC_LOCK_DELAY_WARN   = new ViewString( "MemMapPageMgr SYNC getPage delay us=" );
    private static final ZString PAGE_SWITCH_DELAY_WARN = new ViewString( "MemMapPageMgr PRELOADED getPage delay us=" );
    private static final boolean _logWarnings = false; // DONT CARE FOR NOW
    final ZString _threadSync;
    private final int         _pageSize;
    private final BackgroundAllocator _backAllocator;
    private final ReusableString      _msg   = new ReusableString();
    Page _nextPage = new Page();   // all access to _nextPage MUST be syncd against threadSync
    private       FileChannel _channel;
    private       boolean     _logMapOps = false;
    private       ZString     _name;
    private Page _lastPage = new Page();
    private Page _curPage  = new Page();
    private Page _tmpPage = new Page();
    private Page _floatingPage = new Page();   // only used by PopPage
    private       boolean             _trace = false;

    public MemMapPageManager( ZString name, int pageSize, ThreadPriority priority ) {
        _pageSize = pageSize;
        _name     = name;

        _threadSync = new ViewString( _name );

        _backAllocator = new BackgroundAllocator( "MemMapAllocator" + _name, priority );

        _backAllocator.setDaemon( true );

        _backAllocator.start();
    }

    public Page getPage( int newPageNum ) throws PersisterException {

        if ( newPageNum == _curPage.getPageNo() ) return _curPage;
        if ( newPageNum == _lastPage.getPageNo() ) return _lastPage;

        if ( newPageNum == _nextPage.getPageNo() ) { // moving from current to next which is already ready
            // unmap last page

            long startMove = Utils.nanoTime();

            synchronized( _threadSync ) {
                move( _tmpPage, _lastPage );
                move( _lastPage, _curPage );
                move( _curPage, _nextPage );
                move( _nextPage, _tmpPage );

                _backAllocator.unmapNextPageAndGetNewNext( newPageNum + 1 );

                _threadSync.notifyAll();
            }

            logPageSwitchDelay( startMove );

            return _curPage;
        }

        // even if floatingPage is the page thats required remap it to save double checking in unmap

        // ok need a page thats not read read

        move( _tmpPage, _lastPage );
        move( _lastPage, _curPage );
        map( _curPage, newPageNum, _logMapOps );

        long start = Utils.nanoTime();
        synchronized( _threadSync ) {
            move( _nextPage, _tmpPage );
            _backAllocator.unmapNextPageAndGetNewNext( newPageNum + 1 );

            _threadSync.notifyAll();
        }

        logSyncGetDelay( start );

        return _curPage;
    }

    /*
     * get a page but dont unmap the current page, lastPage or nextPage
     *
     * popPage only used for off main thread access eg fix resendRequest
     */
    public Page popPage( int recPage ) throws PersisterException {

        if ( recPage == _floatingPage.getPageNo() ) return _floatingPage;

        map( _floatingPage, recPage, _logMapOps );

        return _floatingPage;
    }

    /**
     * init the memmap
     *
     * @param channel
     */
    public void setChannel( FileChannel channel ) {
        _channel = channel;
    }

    public void setLogTimes( boolean logTimes ) {
        _logMapOps = logTimes;
    }

    public void shutdown() {
        synchronized( _threadSync ) {
            _backAllocator.finish();

            _threadSync.notifyAll();
        }
    }

    /**
     * blocking call dont return until all unmapped (dont unmap nextPageas thats in free list in mapper thread)
     */
    public void unmapAll() {
        synchronized( _threadSync ) {
            _backAllocator.unmapNextPageAndGetNewNext( -1 );
        }
        unmap( _lastPage, _logMapOps );
        unmap( _curPage, _logMapOps );
        unmap( _floatingPage, _logMapOps );
    }

    void map( Page page, int pageToFetch, boolean logTime ) throws PersisterException {

        if ( pageToFetch < 0 ) {
            throw new PersisterException( "MemMapPageManager cant read page " + pageToFetch );
        }

        if ( _trace ) _log.info( "MAP REQUEST page " + pageToFetch + ", logUnmap=" + logTime );

        long offset = pageToFetch * _pageSize;

        unmap( page, logTime );

        try {
            long start = 0, end;

            if ( logTime ) start = Utils.nanoTime();

            MappedByteBuffer buffer = _channel.map( FileChannel.MapMode.READ_WRITE, offset, _pageSize );

            page.setPageNo( pageToFetch );
            page.setMappedByteBuf( buffer );

            if ( logTime ) {
                end = Utils.nanoTime();

                _log.info( "Map for page " + page.getPageNo() + ", mapper=" + _name + ", size=" + _pageSize +
                           " : " + ((end - start) >>> 10) + " usec" );
            }

        } catch( IOException e ) {
            throw new PersisterException( "Failed to map page " + pageToFetch + " in persister " + _name +
                                          ", size=" + _pageSize + ", offset=" + offset +
                                          " : " + e.getMessage(), e );
        }
    }

    void move( Page toPage, Page fromPage ) {

        toPage.setMappedByteBuf( fromPage.getMappedByteBuf() );
        toPage.setPageNo( fromPage.getPageNo() );

        fromPage.reset();
    }

    void unmap( Page page, boolean logUnmap ) {

        MappedByteBuffer buf = page.getMappedByteBuf();

        if ( buf != null ) {
            final int  pageNum = page.getPageNo();
            final long start   = Utils.nanoTime();

            if ( _trace ) _log.info( "Unmap REQUEST page (-1 = background unmap) " + pageNum + ", logUnmap=" + logUnmap );

            page.reset();
            JavaSpecific.instance().unmap( _name, buf );

            if ( logUnmap ) {
                final long end = Utils.nanoTime();

                _log.info( "Unmap for page " + pageNum + ", mapper=" + _name + " : " + ((end - start) >> 10) + " usec" );
            }
        }
    }

    private void logPageSwitchDelay( long start ) {
        if ( !_logWarnings ) {
            return;
        }

        long durationMicros = Math.abs( Utils.nanoTime() - start ) >> 10;

        if ( durationMicros > 4 ) {
            _msg.copy( PAGE_SWITCH_DELAY_WARN );
            _msg.append( durationMicros );
            _msg.append( " page=" );
            _msg.append( _curPage.getPageNo() );
            _msg.append( " name=" );
            _msg.append( _name );
            _log.warn( _msg );
        } else {
            if ( _logMapOps && durationMicros > 2 ) {
                _msg.copy( "MemMapPageMgr PRELOADED getPage delay us=" );
                _msg.append( durationMicros );
                _msg.append( " page=" );
                _msg.append( _curPage.getPageNo() );
                _msg.append( " name=" );
                _msg.append( _name );
                _log.info( _msg );
            }
        }
    }

    private void logSyncGetDelay( long start ) {
        if ( !_logWarnings ) {
            return;
        }

        long durationMicros = Math.abs( Utils.nanoTime() - start ) >> 10;

        if ( durationMicros > 10 ) {
            _msg.copy( SYNC_LOCK_DELAY_WARN );
            _msg.append( durationMicros );
            _msg.append( " page=" );
            _msg.append( _curPage.getPageNo() );
            _msg.append( " name=" );
            _msg.append( _name );
            _log.warn( _msg );
        } else {
            if ( _logMapOps && durationMicros > 3 ) {
                _msg.copy( "MemMapPageMgr SYNC getPage delay us=" );
                _msg.append( durationMicros );
                _msg.append( " page=" );
                _msg.append( _curPage.getPageNo() );
                _msg.append( " name=" );
                _msg.append( _name );
                _log.info( _msg );
            }
        }
    }

    private class BackgroundAllocator extends Thread {

        private final ThreadPriority _priority;
        private List<Page> _freeList = new ArrayList<>();
        private volatile int     _nextPageToFetch = -1;
        private          boolean _stopped         = false;

        public BackgroundAllocator( String name, ThreadPriority priority ) {
            super( name );
            _priority = priority;
        }

        @Override
        public void run() {

            Logger log = LoggerFactory.create( this.getClass() );

            ThreadUtilsFactory.get().setPriority( this, _priority );

            boolean forceSlow = AppProps.instance().getBooleanProperty( "FORCE_SLOW_MODE", false, false );

            int lowPriLoopWaitMs = AppProps.instance().getIntProperty( "MM_FETCHER_WAIT_MS", false, (forceSlow) ? 500 : Constants.LOW_PRI_LOOP_WAIT_MS );

            while( !_stopped ) {

                synchronized( _threadSync ) {
                    try {
                        _threadSync.wait( lowPriLoopWaitMs );
                    } catch( InterruptedException e ) { /* */}
                }

                int count;

                synchronized( _threadSync ) {
                    if ( _nextPageToFetch >= 0 && _nextPageToFetch != _nextPage.getPageNo() && !_stopped ) {
                        try {
                            _nextPage.reset();
                            map( _nextPage, _nextPageToFetch, false );  // dont logger time mapping in background thread
                        } catch( PersisterException e ) {
                            log.error( PERSIST_ERR, getName(), e );
                            _nextPageToFetch = -1;
                        }
                    }
                    count = _freeList.size();
                }

                for ( int idx = count - 1; idx >= 0; idx-- ) {
                    synchronized( _threadSync ) {
                        Page page = _freeList.remove( idx );

                        unmap( page, false );
                    }
                }
            }
        }

        public void finish() {
            _stopped = true;
        }

        public void unmapNextPageAndGetNewNext( int nextPageToFetch ) {

            if ( _nextPage.getPageNo() != nextPageToFetch && _nextPage.getPageNo() >= 0 ) {
                if ( _nextPage.getMappedByteBuf() != null ) {
                    Page page = new Page();
                    move( page, _nextPage );
                    _freeList.add( page );
                }
            }

            _nextPageToFetch = nextPageToFetch;
        }
    }
}
