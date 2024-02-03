package com.rr.core.sql;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.OutputStreamLoggerAdapter;
import com.rr.core.utils.SMTRuntimeException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @WARNING Be aware of stale connections, if connection goes stale you need to throw away and get a new one
 */
public class JDBCDirectConnectionFactory {

    private static final Logger        _log           = LoggerFactory.create( DataSourceConnectionPool.class );
    private static final String        _defaultDriver = "org.postgresql.Driver";
    private static       AtomicBoolean _loaded        = new AtomicBoolean( false );

    public static void init() {
        if ( _loaded.compareAndSet( false, true ) ) {
            String className = AppProps.instance().getProperty( "jdbc.driver", false, _defaultDriver );

            try {
                Class.forName( className );
            } catch( ClassNotFoundException e ) {
                throw new SMTRuntimeException( "JDBCDriverLoader unable to load " + className + " : " + e.getMessage(), e );
            }
        }
    }

    /**
     * @return a connection that you are responsible for cleaning up after
     */
    public static Connection getConnection( String jdbcKey ) throws SQLException {

        String  hostName      = AppProps.instance().getProperty( jdbcKey + ".jdbc.host" );
        String  hostName2     = AppProps.instance().getProperty( jdbcKey + ".jdbc.host2" );
        String  userName      = AppProps.instance().getProperty( jdbcKey + ".jdbc.user.name" );
        String  database      = AppProps.instance().getProperty( jdbcKey + ".jdbc.database" );
        String  password      = AppProps.instance().getProperty( jdbcKey + ".jdbc.user.password", false, "" );
        String  pwdfile       = AppProps.instance().getProperty( jdbcKey + ".jdbc.user.pwdfile", false, null );
        int     port          = AppProps.instance().getIntProperty( jdbcKey + ".jdbc.port", true, 0 );
        int     port2         = AppProps.instance().getIntProperty( jdbcKey + ".jdbc.port2", false, 0 );
        int     socketTimeout = AppProps.instance().getIntProperty( jdbcKey + ".jdbc.socketTimeout", true, 30 );
        boolean tcpKeepAlive  = AppProps.instance().getBooleanProperty( jdbcKey + ".jdbc.tcpKeepAlive", false, true );

        if ( password == null || password.trim().length() == 0 ) {
            try {
                password = FileUtils.fileToString( pwdfile ).trim();
            } catch( IOException e ) {
                throw new SMTRuntimeException( "getConnection unable to read password file " + pwdfile );
            }
        }

        String     backup = (port2 > 0) ? "," + hostName2 + ":" + port2 : "";
        String     url    = "jdbc:postgresql://" + hostName + ":" + port + backup + "/" + database;
        Properties props  = new Properties();

        props.setProperty( "user", userName );
        props.setProperty( "password", password );
        props.setProperty( "ssl", "false" );
        props.setProperty( "socketTimeout", Integer.toString( socketTimeout ) );
        props.setProperty( "tcpKeepAlive", Boolean.toString( tcpKeepAlive ) );
        props.setProperty( "logUnclosedConnections", "true" );

        Connection conn = DriverManager.getConnection( url, props );

        DriverManager.setLogWriter( new PrintWriter( new OutputStreamLoggerAdapter( _log ) ) );

        return conn;
    }
}
