package com.rr.core.utils;

import com.rr.core.component.SMTComponent;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternToValueMatcher implements SMTComponent {

    private static final Logger _log = LoggerFactory.create( PatternToValueMatcher.class );

    private final Map<Pattern, String> _patternMatches = new HashMap<>();
    private final String               _id;
    private final String               _defaultVal;
    private final String               _patternFile;

    public PatternToValueMatcher( final String id, final List<String> lines, String defaultVal ) {
        _id          = id;
        _defaultVal  = defaultVal;
        _patternFile = "noFile";

        commonInit( _patternFile, lines );
    }

    public PatternToValueMatcher( final String id, final String patternFile ) {
        this( id, patternFile, null );
    }

    public PatternToValueMatcher( final String id, final String patternFile, String defaultVal ) {
        _id          = id;
        _defaultVal  = defaultVal;
        _patternFile = patternFile;

        List<String> lines = new ArrayList<>();

        try {
            FileUtils.read( lines, patternFile, true, true );
        } catch( IOException e ) {
            throw new SMTRuntimeException( "Unable to parse file " + patternFile + " : " + e.getMessage(), e );
        }

        commonInit( patternFile, lines );

        _log.info( "PatternToValueMatcher loaded " + _patternMatches.size() + " entries from " + patternFile );
    }

    @Override public String getComponentId() {
        return _id;
    }

    public String getPatternFile() {
        return _patternFile;
    }

    /**
     * @param key the string to evaluate in the pattern matcher
     * @return the value associated with the matching pattern
     * @throws SMTRuntimeException if more than one pattern matches with different return values
     */
    public String match( final String key ) {
        String val = _defaultVal;

        Pattern firstPtn = null;
        Matcher firstHit = null;

        for ( Map.Entry<Pattern, String> entry : _patternMatches.entrySet() ) {
            Matcher m = entry.getKey().matcher( key );

            if ( m.matches() ) {
                if ( firstHit == null ) {
                    firstPtn = entry.getKey();

                    firstHit = m;
                    val      = entry.getValue();
                } else {
                    if ( !val.equals( entry.getValue() ) ) {
                        throw new SMTRuntimeException( "PatternToValueMatcher ambiguous match of pattern1 " + firstPtn.pattern() + " with " + entry.getKey().pattern() + " for key " + key );
                    }
                }
            }
        }

        return val;
    }

    public int size() {
        return _patternMatches.size();
    }

    private void commonInit( final String patternFile, final List<String> lines ) {
        for ( String line : lines ) {
            int idx = line.lastIndexOf( "," );
            if ( idx > 0 && idx < line.length() ) {
                String regExpStr = line.substring( 0, idx );
                String val       = line.substring( idx + 1 );

                Pattern p = Pattern.compile( regExpStr );

                _patternMatches.put( p, val );

            } else {
                throw new SMTRuntimeException( "Invalid line [" + line + "] in file " + patternFile );
            }
        }

        if ( _patternMatches.size() != lines.size() ) {
            throw new SMTRuntimeException( "PatternToValueMatcher pattern mismatch, file=" + patternFile + ", lines=" + lines.size() + ", patterns=" + _patternMatches.size() );
        }
    }
}
