/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.stats.StatsCfgFile;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.Utils;

public class AntiSpringConfigDump {

    private static final Logger    _console = ConsoleFactory.console( AntiSpringConfigDump.class, Level.info );
    private static final ErrorCode FAILED   = new ErrorCode( "SPB100", "Exception in main" );
    private static final int       ERR_EXIT = 101;

    static Logger _log;

    private static SMTComponentManager _componentManager;

    public static void main( String[] args ) {

        try {

            basicSetup( args );

            _componentManager = new SMTComponentManager( "componentManager" );

            SMTPropertyComponentBuilder componentBuilder = new SMTPropertyComponentBuilder( AppProps.instance(), _componentManager );

            componentBuilder.init();

        } catch( Exception e ) {

            _console.error( FAILED, "", e );

            Utils.exit( ERR_EXIT );
        }
    }

    private static void basicSetup( String[] args ) throws Exception {
        if ( args.length == 0 ) {
            _console.info( "Error : missing property file arguments" );
            _console.info( "Usage: {prog} envPropertyFile appPropertyFile" );
            Utils.exit( 99 );
        }

        String envPropFile = args[ 0 ];
        String appPropFile = (args.length > 1) ? args[ 1 ] : null;

        AppProps.instance().init( envPropFile, appPropFile );

        AppProps props = AppProps.instance();

        StatsMgr.setStats( new StatsCfgFile( props.getProperty( CoreProps.STATS_CFG_FILE, false, null ) ) );
        StatsMgr.instance().initialise();

        LoggerFactory.setForceConsole( true );

        _log = LoggerFactory.create( AntiSpringConfigDump.class );

        ClockFactory.init();
    }

    AntiSpringConfigDump() {
        super();
    }
}
