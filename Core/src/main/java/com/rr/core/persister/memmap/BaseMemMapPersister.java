/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.persister.memmap;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.core.tasks.BasicSchedulerCallback;
import com.rr.core.tasks.CoreScheduledEvent;
import com.rr.core.tasks.ScheduledEvent;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * each persister operates on a single file
 * <p>
 * long key is composed of an int pageNUmber and an int offset within the page
 * this caters for a page size upto 4GB !
 * <p>
 * in general page size will be 10MB ... tho this needs some experimentation
 * need three pages mapped
 * previousPage       so when markConf called we should have page mapped
 * curPage            current page for writing new records too
 * nextFreePage       so dont have to wait, when curPage full go straight to next free page
 * <p>
 * Page management is via MemMapPageHelper
 * <p>
 * If the persistMarker is invoked on a page not in memory then the helper will retrieve a temp page
 * The helper will allow a list of 3 small temp pages of 1MB each
 * <p>
 * <2byte lenA><2byte flags><dataA><1 byte lenB><lva B><2byte Marker>
 * <p>
 * if dataB exists the context flag is set
 * <p>
 * IMPORTANT, when reading from start to finish, reading two bytes at start of record can be either the 2 byte flag of a new
 * record OR the two byte end of page marker ! This the bits used in the end of page marker cannot be used by the app
 *
 * @NOTE the memmap manager maintains FOUR pages in memory ! SO for a session thats EIGHT !
 */
public abstract class BaseMemMapPersister implements Persister {

    public static final byte  UPPER_FLAG_OPT_CONTEXT = 1 << 6;                       // the persisted record has optional context
    public static final short FLAG_OPT_CONTEXT       = UPPER_FLAG_OPT_CONTEXT << 8;  // byte flag as an short
    static final Logger _log = LoggerFactory.create( BaseMemMapPersister.class );
    static final ErrorCode DATE_ROLL_FAIL = new ErrorCode( "MMP500", "Error during date roll" );
    private static final long MIN_FILE_SIZE = 1024 * 1024;
    private static final int  MIN_PAGE_SIZE = 1024;
    protected final int               _pageSize;
    protected final MemMapPageManager _pageManager;
    protected final ReusableString _logBuf = new ReusableString( 100 );
    final           ReusableString    _name  = new ReusableString();
    private final   ReusableString    _fname = new ReusableString();
    private final   long              _filePreSize;
    private final long _bitShiftForPageMask;
    protected MappedByteBuffer _curPage;
    protected int              _curPageNo;
    protected int              _curPageOffset;     // offset to start of next record in page
    protected long             _curPageNoKeyMask;
    protected boolean          _flushPerCall = false;
    protected volatile boolean _isOpen = false;
    private         RandomAccessFile  _file;
    private         FileChannel       _channel;

    public BaseMemMapPersister( ZString name, ZString fname, long filePreSize, int pageSize, long bitShiftForPageMask, ThreadPriority priority ) {
        super();

        _name.copy( name );
        _fname.copy( fname );
        if ( filePreSize < MIN_FILE_SIZE ) filePreSize = MIN_FILE_SIZE;
        if ( pageSize < MIN_PAGE_SIZE ) pageSize = MIN_PAGE_SIZE;

        _log.info( "MemMapPersister " + name + ", filePreSize=" + filePreSize + ", pageSize=" + pageSize );

        _filePreSize = filePreSize;

        _pageSize    = pageSize;
        _pageManager = new MemMapPageManager( _name, _pageSize, priority );

        _bitShiftForPageMask = bitShiftForPageMask;

        ShutdownManager.instance().register( "BaseMemMapPersisterClose" + _name, this::shutdown, ShutdownManager.Priority.Low );

        final String cbName = _name + "DateRoll";

        final BasicSchedulerCallback callback = new BasicSchedulerCallback( cbName, ( event ) -> rollPersistenceEvent( cbName, event ) );

        SchedulerFactory.get().registerForGroupEvent( CoreScheduledEvent.EndOfDay, callback );
    }

    @Override
    public ReusableString appendState( ReusableString logMsg ) {
        logMsg.append( ", curPageNo=" ).append( _curPageNo ).append( ", curPageOffset=" ).append( _curPageOffset );

        return logMsg;
    }

    @Override
    public synchronized void close() {
        if ( isOpen() ) {
            setOpen( false );

            _log.info( "Closed MemMapPersister " + _fname + ", page=" + _curPageNo );

            _pageManager.unmapAll();
            _pageManager.shutdown();

            _curPage          = null;
            _curPageNo        = 0;
            _curPageNoKeyMask = 0;
            _curPageOffset    = 0;

            close( _channel );
            _channel = null;
            close( _file );
            _file = null;
        }
    }

    @Override public void flush() {
        if ( _curPage != null ) {
            _curPage.force();
        }
    }

    @Override
    public boolean isOpen() {
        return _isOpen;
    }

    @Override
    public void open() throws PersisterException {
        String fname = _fname.toString();

        log( _logBuf.copy( _name ).append( " " ).append( " Opening MemMapPersister " ).append( _fname ) );

        try {
            FileUtils.mkDirIfNeeded( fname );

            _file = new RandomAccessFile( fname, "rw" );

            if ( _file.length() == 0 ) {
                _file.setLength( _filePreSize );
            }

            _channel = _file.getChannel();

            _pageManager.setChannel( _channel );

            setOpen( true );

            getPage( 0 );

            log( _logBuf.copy( _name ).append( " " ).append( " Opened MemMapPersister " ).append( _fname )
                        .append( ", page=" ).append( _curPageNo )
                        .append( ", isOpen=" ).append( isOpen() ) );

        } catch( FileException e ) {
            throw new PersisterException( "MemMapPersister " + _name + " failed to mkdir for file " + fname, e );
        } catch( IOException e ) {
            throw new PersisterException( "MemMapPersister " + _name + " failed to memmap file " + fname, e );
        }
    }

    @Override
    public void rollPersistence() throws PersisterException {
        log( _logBuf.copy( _name ).append( " " ).append( " Base MemMap rollPersistence started " ).append( _fname ) );
        close();
        FileUtils.backup( _fname.toString() );
        open();
        log( _logBuf.copy( _name ).append( " " ).append( " Base MemMap rollPersistence ended " ).append( _fname ) );
    }

    @Override public void setFlushPerCall( final boolean enable ) { _flushPerCall = enable; }

    protected void setOpen( boolean isOpen ) {
        _isOpen = isOpen;
    }

    public final void setLogTimes( boolean logTimes ) {
        _pageManager.setLogTimes( logTimes );
    }

    public void shutdown() {
        close();
        _pageManager.shutdown();
    }

    protected final void getPage( int newPageNum ) throws PersisterException {
        if ( _isOpen ) {
            Page page = _pageManager.getPage( newPageNum );

            _curPage   = page.getMappedByteBuf();
            _curPageNo = page.getPageNo();

            if ( _curPage == null ) {
                throw new PersisterException( "Failed to obtain page " + newPageNum + ", pageMgr returnd null buffer for page " + _curPageNo );
            }

            if ( _curPageNo != newPageNum ) {
                throw new PersisterException( "Failed to obtain page " + newPageNum + ", pageMgr returnd " + _curPageNo );
            }

            _curPageNoKeyMask = (long) _curPageNo << _bitShiftForPageMask;
            _curPageOffset    = 0;
        } else {
            throw new SMTRuntimeException( "BaseMemMapPersister() cannot getPage as " + _name + " is not open" );
        }
    }

    protected void log( final ReusableString buf ) {
        appendState( buf );

        _log.info( buf );
    }

    private void close( Closeable obj ) {
        if ( obj == null ) return;
        try { obj.close(); } catch( Exception e ) { /* dont care */ }
    }

    private void rollPersistenceEvent( final String cbName, final ScheduledEvent event ) {
        try {
            log( _logBuf.copy( _name ).append( " " ).append( " start rollPersistenceEvent " ).append( cbName ).append( " " ).append( _fname ) );

            rollPersistence();

            log( _logBuf.copy( _name ).append( " " ).append( " end rollPersistenceEvent " ).append( cbName ).append( " " ).append( _fname ) );
        } catch( PersisterException e ) {
            _log.error( DATE_ROLL_FAIL, cbName, e );

            ShutdownManager.instance().shutdown( -1 );
        }
    }
}
