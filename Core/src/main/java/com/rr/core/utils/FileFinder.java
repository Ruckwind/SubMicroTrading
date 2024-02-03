package com.rr.core.utils;

import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileFinder extends SimpleFileVisitor<Path> {

    private static final Logger    _console       = ConsoleFactory.console( FileFinder.class, Level.info );
    private static final ErrorCode ERR_VISIT_FAIL = new ErrorCode( "FFR100", "Error in FileFinder visitFile" );

    private final PathMatcher matcher;
    private       List<Path>  matchedPaths = new ArrayList<Path>();

    FileFinder( String pattern ) {
        pattern = pattern.trim();

        matcher = FileSystems.getDefault().getPathMatcher( "glob:" + pattern );
    }

    @Override public FileVisitResult preVisitDirectory( Path dir,
                                                        BasicFileAttributes attrs ) {
        match( dir );
        return CONTINUE;
    }

    @Override public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) {
        match( file );
        return CONTINUE;
    }

    @Override public FileVisitResult visitFileFailed( Path file, IOException exc ) {
        _console.error( ERR_VISIT_FAIL, " on path " + file.toString() + " : " + exc.getMessage(), exc );
        System.err.println( exc );
        return CONTINUE;
    }

    public Collection<Path> getMatchedPaths() {
        return matchedPaths;
    }

    public int getTotalMatches() {
        return matchedPaths.size();
    }

    void match( Path file ) {
        Path name = file.getFileName();

        if ( name != null && matcher.matches( name ) ) {
            matchedPaths.add( file );
        }
    }
}