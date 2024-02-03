/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

import com.rr.core.lang.RTStartupException;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Repository for statistics, to be populated on startup as soon as possible and before any use of pooling or ZString etc
 *
 * @author Richard Rose
 */
public class StatsCfgFile implements Stats {

    private static final Logger _log = ConsoleFactory.console( StatsCfgFile.class, Level.info );

    private static final String DEFAULT_STATS_CFG_FILE = "./core/stats.cfg";

    private final Map<SizeType, Integer> _stats = new HashMap<>( 128 );

    private String _file = DEFAULT_STATS_CFG_FILE;

    public StatsCfgFile() {
        this( DEFAULT_STATS_CFG_FILE );
    }

    public StatsCfgFile( String cfgFile ) {
        _file = (cfgFile != null) ? cfgFile : DEFAULT_STATS_CFG_FILE;
    }

    // TODO pass a Config element into initialise
    @Override
    public void initialise() {

        Properties p = new Properties();

        try {
            BufferedReader rdr = FileUtils.bufFileReader( _file );

            p.load( rdr );

            rdr.close();

        } catch( IOException e ) {
            throw new RTStartupException( "StatsCfgFile : Unable to load stats properties from " + _file + ", error=" + e.getMessage() );
        }

        for ( Map.Entry<Object, Object> entry : p.entrySet() ) {
            String key   = (String) entry.getKey();
            String value = (String) entry.getValue();

            SizeType s = Enum.valueOf( SizeType.class, key );

            if ( s == null ) {
                throw new RTStartupException( "StatsCfgFile : " + key + " is not a valid entry, entries must be members of " + SizeType.class.getSimpleName() );
            }

            Integer iVal;

            try {
                iVal = Integer.valueOf( value );
            } catch( NumberFormatException e ) {
                throw new RTStartupException( "StatsCfgFile : " + key + " entry of " + value + " is not a valid number" );
            }

            _stats.put( s, iVal );
        }
    }

    /**
     * @param id
     * @return
     * @throws RTStartupException if id doesnt have entry
     */
    @Override
    public int find( SizeType id ) {

        Integer val = _stats.get( id );

        if ( val == null ) {
            _log.info( "StatsCfgFile : no config entry for " + id + " use default " + id.getSize() );

            return id.getSize();
        }

        return val;
    }

    @Override
    public void set( SizeType id, int val ) {
        _stats.put( id, val );
    }

    /**
     * persist stats
     */
    @Override
    public void store() {

        Properties p = new Properties();
        for ( Map.Entry<SizeType, Integer> entry : _stats.entrySet() ) {
            SizeType key   = entry.getKey();
            Integer  value = entry.getValue();
            if ( value != null ) {
                p.put( key.toString(), value.toString() );
            }
        }
        try( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( _file ) ) ) ) {

            p.store( writer, null );

        } catch( IOException e ) {
            throw new RTStartupException( "StatsCfgFile : Unable to load stats properties from " + _file + ", error=" + e.getMessage() );
        }
        // TODO console logger error closing file
    }

    @Override
    public void reload() {
        initialise();
    }

    void setFile( String fileName ) {
        _file = fileName;
    }
}
