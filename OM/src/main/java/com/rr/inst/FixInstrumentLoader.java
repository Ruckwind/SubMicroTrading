/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.codec.FixDecoder;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.core.model.SecDefSpecialType;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.model.generated.fix.codec.MD44Decoder;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.type.SecurityUpdateAction;

import java.io.BufferedReader;
import java.io.IOException;

public class FixInstrumentLoader {

    private static final ErrorCode ERR_BAD_INST = new ErrorCode( "FIL100", "Bad instrument" );

    private final Logger          _log = LoggerFactory.create( FixInstrumentLoader.class );
    private final FixDecoder      _softDecoder;
    private final InstrumentStore _store;

    private SecDefSpecialType _overrideSecDefSpecialType = null;

    public FixInstrumentLoader( InstrumentStore store ) {
        _store       = store;
        _softDecoder = new MD44Decoder();
        _softDecoder.setValidateChecksum( false );
    }

    public FixInstrumentLoader( InstrumentStore store, FixDecoder decoder ) {
        _store       = store;
        _softDecoder = decoder;
        _softDecoder.setValidateChecksum( false );
    }

    public boolean addSecDef( String rawFix, SecDefSpecialType copySecDesToSymbol, boolean manualFile ) {
        byte[] msg = rawFix.getBytes();

        return decode( msg, 0, msg.length, 0, copySecDesToSymbol, manualFile, false );
    }

    public SecDefSpecialType getOverrideSecDefSpecialType()                                       { return _overrideSecDefSpecialType; }

    public void setOverrideSecDefSpecialType( final SecDefSpecialType overrideSecDefSpecialType ) { _overrideSecDefSpecialType = overrideSecDefSpecialType; }

    public void loadFromFile( String fileName, boolean throwErrorOnZeroInstruments ) throws FileException {
        if ( fileName == null || fileName.length() == 0 ) return;

        String[] files = fileName.split( "," );

        for ( String file : files ) {
            doLoadFromFile( file.trim(), throwErrorOnZeroInstruments );
        }
    }

    /**
     * load instruments from file ... invoke before force GC as creates temp strings
     */
    public void loadFromFile( String fileName ) throws FileException {
        if ( fileName == null || fileName.length() == 0 ) return;

        loadFromFile( fileName, true );
    }

    private boolean decode( byte[] newFixMsg, int offset, int len, int line, SecDefSpecialType overrideType, final boolean manualFile, final boolean histFile ) {

        Event m = _softDecoder.decode( newFixMsg, 0, len );

        if ( m != null ) {
            switch( m.getReusableType().getSubId() ) {
            case EventIds.ID_SECURITYDEFINITION:
                SecurityDefinitionImpl inc = (SecurityDefinitionImpl) m;

                if ( overrideType != null ) {
                    inc.setSecDefSpecialType( overrideType );
                }

                if ( manualFile ) {
                    inc.setFlag( MsgFlag.Override, true );
                }

                if ( histFile ) {
                    inc.setFlag( MsgFlag.Historical, true );
                }

                final SecurityUpdateAction action = inc.getSecurityUpdateAction();

                if ( action == SecurityUpdateAction.Delete ) {
                    _store.remove( inc );
                    return true;
                }
                return _store.add( inc );
            case EventIds.ID_SECURITYSTATUS:
                SecurityStatusImpl status = (SecurityStatusImpl) m;
                _store.updateStatus( status );
                break;
            default:
                _log.info( "Unexpected decode at line : " + line + " : " + m.toString() );
                break;
            }
        }

        // dont recycle

        return false;
    }

    private void doLoadFromFile( String fileName, final boolean throwErrorOnZeroInstruments ) throws FileException {

        if ( !FileUtils.isFile( fileName ) ) {
            if ( throwErrorOnZeroInstruments ) {
                throw new FileException( "Unable to load instruments from unreadable file " + fileName + " (check path/permissions)" );
            } else {
                _log.info( "FixInstrumentLoader unable to find " + fileName );
            }

            return;
        }

        BufferedReader reader = null;

        int minLineLen = 20;

        int count = 0;
        int lines = 0;

        _log.info( "About to start loading instruments from " + fileName );

        long start = ClockFactory.get().currentTimeMillis();

        int errCount = 0;
        int skipCnt  = 0;

        boolean manualFile = fileName.toLowerCase().contains( "manual" );
        boolean histFile   = fileName.toLowerCase().contains( "hist" );

        try {
            reader = FileUtils.bufFileReader( fileName );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() ) {

                ++lines;

                if ( line.length() > minLineLen ) {
                    byte[] msg = line.getBytes();

                    try {
                        if ( decode( msg, 0, msg.length, lines, _overrideSecDefSpecialType, manualFile, histFile ) ) {
                            ++count;
                        } else {
                            ++skipCnt;
                        }
                    } catch( Exception e ) {
                        _log.error( ERR_BAD_INST, " from " + fileName + " : " + e.getMessage() + ", line=" + line, e );
                        ++errCount;
                    }
                }
            }

        } catch( IOException e ) {
            throw new FileException( "Unable to load instruments from " + fileName, e );
        } finally {
            FileUtils.close( reader );
        }

        long time = (ClockFactory.get().currentTimeMillis() - start) / 1000;

        if ( errCount > 0 ) {
            _log.warn( "Some instruments had errors, Loaded " + count + " instruments out of " + lines + ", from " + fileName + " in " + time + " secs, errCount=" + errCount + ", skipCnt=" + skipCnt );
        } else {
            _log.info( "Loaded " + count + " instruments out of " + lines + ", from " + fileName + " in " + time + " secs, errCount=" + errCount + ", skipCnt=" + skipCnt );
        }
    }
}
