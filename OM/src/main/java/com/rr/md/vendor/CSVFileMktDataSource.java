package com.rr.md.vendor;

import com.rr.core.codec.CSVDecoder;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.Reusable;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CSVFileMktDataSource<T extends InstRefData & Reusable<T> & Event> implements SMTInitialisableComponent, MktDataSrc<T> {

    private static final Logger _log = LoggerFactory.create( CSVFileMktDataSource.class );

    private final     String              _fileName;
    private final     String              _className;
    private final     String              _id;
    private final     Map<String, String> _defaultMappings;
    private final     Map<String, String> _nonStdMappings;
    private final     Set<String>         _ignoreCols;
    private final     String              _securityIdFieldName;
    private final     Map<Instrument, T>  _instMapToVal = new ConcurrentHashMap<>(); // map only mutates during init
    private final     InstrumentLocator   _instLocator;
    private transient RunState            _runState     = RunState.Unknown;

    public CSVFileMktDataSource( String id,
                                 String fileName,
                                 Map<String, String> defaults,
                                 Map<String, String> nonStdColMappings,
                                 Set<String> ignoreCols,
                                 String className,
                                 String securityIdFieldName,
                                 InstrumentLocator instrumentLocator ) {
        _id                  = id;
        _fileName            = fileName;
        _className           = className;
        _defaultMappings     = (defaults == null) ? Collections.emptyMap() : defaults;
        _nonStdMappings      = (nonStdColMappings == null) ? Collections.emptyMap() : nonStdColMappings;
        _ignoreCols          = (ignoreCols == null) ? Collections.emptySet() : ignoreCols;
        _securityIdFieldName = securityIdFieldName;
        _instLocator         = instrumentLocator;
    }

    @Override public boolean canHandle() {
        return false;
    }

    @Override public void handle( final Event msg )    { /* nothing */ }

    @Override public void handleNow( final Event msg ) { /* nothing */ }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public T getItem( final Instrument key ) {
        return _instMapToVal.get( key );
    }

    @Override public boolean hasPipeLineId( final String pipeLineId ) {
        return false;
    }

    @Override public boolean supports( final Instrument inst ) { return false; }

    @Override public List<String> getPipeLineIds() {
        return null;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @SuppressWarnings( "unchecked" ) @Override public void init( final SMTStartContext ctx, CreationPhase phase ) {

        CSVDecoder<T> decoder = new CSVDecoder<>( _id + "Src", _defaultMappings, _nonStdMappings, _ignoreCols, _className );
        decoder.setInstrumentLocator( _instLocator );

        final List<String> lines = new ArrayList<>();

        try {
            FileUtils.read( lines, _fileName, true, true );
        } catch( IOException e ) {
            throw new SMTRuntimeException( "CSVFileSource error reading file " + _fileName + " : " + e.getMessage(), e );
        }

        if ( lines.size() < 2 ) {
            _log.warn( "Only found " + lines.size() + " lines in CSVFile " + _fileName );
            return;
        }

        String hdr = lines.get( 0 );

        if ( !hdr.contains( _securityIdFieldName ) ) {
            throw new SMTRuntimeException( "Error CSVFileSource from file " + _fileName + ", doesnt have a header field of " + _securityIdFieldName );
        }

        decoder.parseHeader( hdr.getBytes(), 0, hdr.getBytes().length );

        Class<T> clazz;
        try {
            clazz = (Class<T>) Class.forName( _className );

            int errors = 0;
            int skips  = 0;

            for ( int lineNum = 1; lineNum < lines.size(); ++lineNum ) {
                String line = lines.get( lineNum );

                byte[] bytes = line.getBytes();

                try {
                    Event m = decoder.decode( bytes, 0, bytes.length );

                    if ( m == null ) {
                        ++skips;
                        _log.info( "Failed to decode entry for [" + _fileName + "/" + lineNum + "] entry : " + line );
                        continue;
                    }

                    T entry = (T) m;

                    Instrument inst = ((T) m).getInstrument();

                    if ( inst == null ) {
                        _log.warn( "No valid instrument found for [" + _fileName + "/" + lineNum + "] entry : " + line );
                        continue;
                    }

//                    if ( _log.isEnabledFor( Level.trace ) ) {
//                        _log.logger( Level.trace, "Loaded mapping for inst " + inst.getKey( _idSrc ) + " for " + entry.toString() );
//                    }

                    _instMapToVal.put( inst, entry );

                } catch( Exception e ) {
                    _log.info( "Bad line " + (lineNum + 1) + " : " + e.getMessage() + " ... ignoring" );
                    ++errors;
                }
            }

            _log.info( "CSVFileMktDataSource loaded " + _instMapToVal.size() + " entries from " + _fileName + ", errors=" + errors + ", skips=" + skips );

            if ( skips > 0 ) {
                _log.info( "CSVFileMktDataSource skipped=" + skips + " entries check for missing instruments" );
            }

        } catch( Exception e ) {
            throw new SMTRuntimeException( "Exception getting class for className " + _className + " : " + e.getMessage(), e );
        }
    }

    @Override public void prepare()                            { /* nothing */ }

    @Override public void startWork()                  { /* nothing */ }

    @Override public void stopWork()                   { /* nothing */ }

    @Override public void threadedInit()               { /* nothing */ }
}
