/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.file;

import com.rr.core.session.SessionConfig;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.file.FileLog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class FileSessionConfig extends SessionConfig {

    private String[] _filesIn;
    private String   _pathRoots;
    private String   _patternMatch;
    private FileLog  _fileOut;

    public FileSessionConfig( String id ) {

        super( id );
    }

    public FileLog getFileOut()                              { return _fileOut; }

    public void setFileOut( FileLog fileOut )                { _fileOut = fileOut; }

    public String[] getFilesIn() {

        if ( _pathRoots != null && _pathRoots.length() > 1 && _patternMatch != null && _patternMatch.length() > 0 ) {
            String[] orig = (_filesIn == null) ? new String[ 0 ] : _filesIn;

            String[] startDirs = _pathRoots.split( "," );

            ArrayList<Path> allFiles = new ArrayList<>();

            for ( String _startDir : startDirs ) {

                Collection<Path> paths = null;
                try {
                    paths = FileUtils.getMatchedPaths( _startDir, _patternMatch );
                } catch( FileException e ) {
                    throw new SMTRuntimeException( "FileSessionConfig.getFilesIn .. error getting matched paths : " + e.getMessage(), e );
                }

                allFiles.addAll( paths );
            }

            if ( allFiles.size() == 0 ) throw new SMTRuntimeException( "getFilesIn() failed to match any files for pathRoots=" + _pathRoots + ", ptn=" + _patternMatch );

            _filesIn = new String[ allFiles.size() + orig.length ];

            int idx = 0;

            for ( String s : orig ) {
                _filesIn[ idx++ ] = s;
            }

            for ( Path p : allFiles ) {
                _filesIn[ idx++ ] = p.toFile().getPath();
            }
        }

        return _filesIn;
    }

    public void setFilesIn( String[] filesIn )               { _filesIn = filesIn; }

    public void setPathRoots( final String pathRoots )       { _pathRoots = pathRoots; }

    public void setPatternMatch( final String patternMatch ) { _patternMatch = patternMatch; }
}
