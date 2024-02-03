package com.rr.core.logger;

import com.rr.core.lang.Env;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.Utils;

public class ErrorController {

    private static final Logger _console = ConsoleFactory.console( ErrorController.class );

    private static Boolean _exitOnError;

    public static void error( String msg )  { error( msg, null ); }

    public static void error( ZString msg ) { error( msg, null ); }

    public static void error( String msg, Throwable t ) {

        if ( _exitOnError == null ) {
            try {
                _exitOnError = AppProps.instance().getBooleanProperty( CoreProps.EXIT_ON_ERROR, false, Env.isBacktest() );
            } catch( Exception e ) {
                _exitOnError = true;
            }
        }

        if ( _exitOnError ) {
            _console.warn( "EXIT ON ERROR : " + msg + " : " + ((t != null) ? t.getMessage() : "??") );

            if ( t == null ) {
                Utils.exit( 100 );
            } else {
                Utils.exit( 100, t );
            }
        } else {
            _console.warn( "IGNORING ERROR : " + msg + " : " + ((t != null) ? t.getMessage() : "??") );

            ReusableString r = TLC.strPop();

            ExceptionTrace.getStackTrace( r, t );

            _console.warn( r );

            r.copy( "CAUSE------------------" );

            if ( t != null ) {
                ExceptionTrace.getStackTrace( r, t.getCause() );

                _console.warn( r );
            }

            TLC.strPush( r );
        }
    }

    public static void error( ZString msg, Throwable t ) {

        if ( _exitOnError == null ) {
            try {
                _exitOnError = AppProps.instance().getBooleanProperty( CoreProps.EXIT_ON_ERROR, false, Env.isBacktest() );
            } catch( Exception e ) {
                _exitOnError = true;
            }
        }

        if ( _exitOnError ) {
            _console.warn( "EXIT ON ERROR : " + msg.toString() );

            if ( t == null ) {
                Utils.exit( 100 );
            } else {
                Utils.exit( 100, t );
            }
        }
    }

    public static void setExitOnError( final Boolean exitOnError ) {
        ErrorController._exitOnError = exitOnError;
    }
}
