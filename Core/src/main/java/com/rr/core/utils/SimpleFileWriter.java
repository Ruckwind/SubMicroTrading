package com.rr.core.utils;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class SimpleFileWriter {

    private final static Logger _console = ConsoleFactory.console( SimpleFileWriter.class );

    private static final char MAIN_SEP = ',';

    private static final int       BUF_SIZE       = 128 * 1024;
    private static final ErrorCode ERR_FAIL_WRITE = new ErrorCode( "SFW100", "Failed to write to file " );

    private String               _fileName;
    private BufferedOutputStream _fileOS;
    private ReusableString       _buf = new ReusableString( 128 );

    public SimpleFileWriter( String fileName ) {
        this( fileName, null, BUF_SIZE );
    }

    public SimpleFileWriter( String fileName, String header ) {
        this( fileName, header, BUF_SIZE );
    }

    public SimpleFileWriter( String fileName, String header, int bufSize ) {

        _fileName = fileName;

        try {
            _fileOS = FileUtils.bufFileOutStream( _fileName, BUF_SIZE );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "SimpleFileWriter error creating strat csv file " + _fileName + " : " + e.getMessage(), e );
        }

        writeHeader( header );

        ShutdownManager.instance().register( "SimpleFileWriterClose" + fileName, () -> close(), ShutdownManager.Priority.Low );
    }

    public void close() {
        FileUtils.flush( _fileOS );
        FileUtils.close( _fileOS );
    }

    public void writeHeader( String header ) {
        if ( header != null && header.length() > 0 ) {
            _buf.copy( header );
            writeLine( _fileOS );
        }
    }

    public void writeLine( ZString line ) {
        _buf.copy( line );
        writeLine( _fileOS );
    }

    private void writeLine( BufferedOutputStream file ) {
        _buf.append( "\n" );
        try {
            file.write( _buf.getBytes(), 0, _buf.length() );
        } catch( IOException e ) {
            _console.error( ERR_FAIL_WRITE, e.getMessage() + " : " + _buf );
        }
    }
}
