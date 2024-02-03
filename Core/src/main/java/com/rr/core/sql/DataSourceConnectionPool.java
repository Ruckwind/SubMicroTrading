package com.rr.core.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HikariCP Connection Pool ... maintains a map of DBKey to DataSource/Pool
 * <p>
 * Inside its pool HikariCP will handle stale connections, but it cant do anything with handles its given out so you must handle !
 * <p>
 * try( Connection c = DataSourceConnectionPool.getConnection( myDBKey ) ) {
 * <p>
 * ... use connection
 * <p>
 * } // will auto close and free all resources and return connection to the pool
 * <p>
 * Note you have to setup properties, if myDBKey is sandp
 * <p>
 * sandp.jdbc.host
 * sandp.jdbc.port
 * sandp.jdbc.user.name
 * sandp.jdbc.database
 * <p>
 * If you need other properties then use the alternative getter which takes a seperate sma;; properties file using hikari properties eg :-
 * <p>
 * dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
 * dataSource.user=test
 * dataSource.password=test
 * dataSource.databaseName=mydb
 * dataSource.portNumber=5432
 * dataSource.serverName=localhost
 * dataSource.poolName=sandp
 *
 * @NOTE uses lazy construction for pool, if you initiallt invoke getConnection without a properties file then subsequent calls with properties file will be IGNORED !
 * @WARNING Be aware of stale connections, if connection goes stale you need to throw away and get a new one
 */
public class DataSourceConnectionPool {

    private static final Logger _log = LoggerFactory.create( DataSourceConnectionPool.class );

    private static final String _defaultDriver = "org.postgresql.Driver";

    public static Map<String, HikariDataSource> _entries = new ConcurrentHashMap<>();

    private static String _driverClass = getDriver();

    private static String getDriver() {
        String driverClass = AppProps.instance().getProperty( "jdbc.driver", false, _defaultDriver );

        _log.info( "DataSourceConnectionPool.init() loading driver [" + driverClass + "]" );

        try {
            Class.forName( driverClass );
        } catch( ClassNotFoundException e ) {
            throw new SMTRuntimeException( "DataSourceConnectionPool unable to load " + driverClass + " : " + e.getMessage(), e );
        }

        return driverClass;
    }

    public static Connection getConnection( String jdbcKey ) throws SQLException {

        HikariDataSource ds = _entries.get( jdbcKey );

        if ( ds == null ) {

            String userName          = AppProps.instance().getProperty( jdbcKey + ".jdbcpool.user.name" );
            String pwdfile           = AppProps.instance().getProperty( jdbcKey + ".jdbcpool.user.pwdfile", false, null );
            String password          = AppProps.instance().getProperty( jdbcKey + ".jdbcpool.user.password", false, "" );
            String jdbcURL           = AppProps.instance().getProperty( jdbcKey + ".jdbcpool.url" );
            int    maxPoolSize       = AppProps.instance().getIntProperty( jdbcKey + ".jdbcpool.maximumPoolSize", false, 10 );
            int    prepCacheStmtSize = AppProps.instance().getIntProperty( jdbcKey + ".jdbcpool.prepCacheStmtSize", false, 250 );
            int    prepCacheSqlLimit = AppProps.instance().getIntProperty( jdbcKey + ".jdbcpool.prepCacheSqlLimit", false, 2048 );

            if ( password == null || password.trim().length() == 0 ) {
                try {
                    password = FileUtils.fileToString( pwdfile ).trim();
                } catch( IOException e ) {
                    throw new SMTRuntimeException( "getConnection unable to read password file " + pwdfile );
                }
            }

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl( jdbcURL );
            config.setUsername( userName );
            config.setPassword( password );
            config.setPoolName( "HIKARAI_" + jdbcKey );

            config.setMaximumPoolSize( maxPoolSize );

            config.setAutoCommit( false );

            config.addDataSourceProperty( "cachePrepStmts", "true" );
            config.addDataSourceProperty( "prepStmtCacheSize", "" + prepCacheStmtSize );
            config.addDataSourceProperty( "prepStmtCacheSqlLimit", "" + prepCacheSqlLimit );
            config.setDriverClassName( _driverClass );

            _log.info( "id() getConnection initialise " + jdbcKey + " with " + jdbcURL );

            ds = new HikariDataSource( config );

            _log.info( "id() getConnection " + jdbcKey + " initialise " + ((ds != null) ? " ok " : " fail") );

            HikariDataSource existing = _entries.putIfAbsent( jdbcKey, ds );

            if ( existing != null ) {
                ds.close();
                ds = existing;
            }
        }

        return ds.getConnection();
    }

    public static Connection getConnection( String jdbcKey, String propertiesFile ) throws SQLException {

        HikariDataSource ds = _entries.get( jdbcKey );

        if ( ds == null ) {
            HikariConfig config = new HikariConfig( propertiesFile );
            ds = new HikariDataSource( config );
            DriverManager.setLogWriter( new PrintWriter( new OutputStreamLoggerAdapter( _log ) ) );

            HikariDataSource existing = _entries.putIfAbsent( jdbcKey, ds );

            if ( existing != null ) {
                ds.close();
                ds = existing;
            }
        }

        return ds.getConnection();
    }
}
