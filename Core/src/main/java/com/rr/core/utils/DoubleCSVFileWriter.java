package com.rr.core.utils;

import com.rr.core.lang.ReusableString;

import java.util.HashSet;
import java.util.Set;

/**
 * DoubleCSVFileWriter writer = new DoubleCSVFileWriter( "sample.csv", "a,b,c" );
 * double[] nums = {1,2,3};
 * writer.write( nums );
 * writer.close();
 */
public class DoubleCSVFileWriter {

    private static int              _idx;
    private static Set<String> _fileNames = new HashSet<>();
    private        SimpleFileWriter _fileWriter;
    private        ReusableString   _buf = new ReusableString( 256 );

    private static int nextIdx() { return ++_idx; }

    public DoubleCSVFileWriter( String fileName, String header ) {

        synchronized( _fileNames ) {
            String base = fileName;

            if ( _fileNames.contains( fileName ) ) {
                int idx = fileName.indexOf( ".csv" );
                if ( idx != -1 ) {
                    fileName = fileName.substring( 0, idx );
                }
                fileName = fileName + "_" + nextIdx() + ".csv";
            }

            _fileNames.add( base );
        }

        _fileWriter = new SimpleFileWriter( fileName, header );
    }

    public void close() {
        _fileWriter.close();
    }

    public void write( double[] entries ) {
        _buf.reset();
        for ( int i = 0; i < entries.length; i++ ) {
            if ( i > 0 ) _buf.append( ',' );

            if ( i == 0 && (int) (entries[ i ]) == 560703 ) {
                System.out.println( entries[ i ] );
            }

//            if(entries[i] > 0.1 ) {
//                System.out.println("tamere = " + entries[i]);
//                System.exit( 1 );
//            }
            _buf.append( entries[ i ] );
        }

        _fileWriter.writeLine( _buf );
    }
}
