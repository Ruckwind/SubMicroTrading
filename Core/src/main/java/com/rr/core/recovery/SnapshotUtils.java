package com.rr.core.recovery;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SnapshotUtils {

    public static final String BASE_SNAP_FILE_NAME = "snapshot";
    public static final String SNAPSHOT_EXT        = ".json.gz";
    private static final Logger _log     = LoggerFactory.create( SnapshotUtils.class );
    private static final Logger _console = ConsoleFactory.console( SnapshotUtils.class );
    private static final int    MAX_DAYS_IN_PAST = 365;
    private static final String LOG_FILE         = "snapshot.log";
    private static final String YYYYMMDD         = "yyyyMMdd";
    private static final String TIME_PTN         = "HH:mm:ss";
    private static final String TIMESTAMP_PTN    = "yyyy-MM-dd,HH:mm:ss";
    private static final String SNAP_DIR         = "snap";
    private final        String _snapshotRootPath;

    public static String formRollableFileName( String fname, int rollNumber, String extension ) {
        return String.format( "%s_%03d%s", fname, rollNumber, extension );
    }

    public SnapshotUtils( final String snapshotRootPath ) throws FileException {

        FileUtils.mkDir( snapshotRootPath );

        _snapshotRootPath = snapshotRootPath;
    }

    public String createSnapshotFile( Env env, SnapshotType snapshotId ) throws Exception {

        String dirName = makeSnapshotDir( env, snapshotId );

        createLog( env, snapshotId, dirName );

        return dirName + "/" + BASE_SNAP_FILE_NAME;
    }

    public boolean finaliseSnapshot( Env env, SnapshotType snapshotId ) throws FileException {

        SimpleDateFormat fileDateFmt = getFileDateFormat();

        Calendar c = getCalendar( env, snapshotId );

        String tmpDirName = SNAP_DIR + ".TEMP";

        String appDir = getAppDirName( env, snapshotId );

        for ( int i = 0; i < 2; i++ ) { // cater for snapshot straddling midnight

            final String yymmdd = fileDateFmt.format( c.getTime() );

            String dailyDirName = appDir + "/" + yymmdd;

            if ( !FileUtils.isDir( dailyDirName, false ) ) {
                continue;
            }

            List<String> entries = FileUtils.getDirEntries( dailyDirName );

            if ( entries.contains( tmpDirName ) ) {
                String destName = getSnapshotDir( env, snapshotId, yymmdd );
                String tmpName  = destName + ".TEMP";

                if ( FileUtils.isDir( tmpName, true ) ) {
                    FileUtils.renameDir( tmpName, destName );

                    _console.info( "Finalised snapshot " + destName );
                    _log.info( "Finalised snapshot " + destName );

                    return true;
                }
            }

            c.add( Calendar.DATE, -1 );
        }

        return false;
    }

    public String getFinalisedSnapshotDir( Env env, SnapshotType snapshotId ) throws FileException {

        String yymmdd = getDateStr( env, snapshotId );

        return getSnapshotDir( env, snapshotId, yymmdd );
    }

    public String getLastSnapshotFile( Env env, SnapshotType snapshotId, int maxDaysInPast ) throws FileException {

        String dirName = lastSnapshotDir( env, snapshotId, maxDaysInPast );

        if ( dirName == null ) return null;

        String fname = dirName + "/" + BASE_SNAP_FILE_NAME;

        return fname;
    }

    public String getLastSnapshotFile( Env env, SnapshotType snapshotId ) throws FileException {

        String dirName = lastSnapshotDir( env, snapshotId );

        if ( dirName == null ) return null;

        String fname = dirName + "/" + BASE_SNAP_FILE_NAME;

        return fname;
    }

    public long getSnapshotStartTime( String baseSnapshotFile, final String firstSnapFile ) throws Exception {
        String base1 = FileUtils.getDirName( baseSnapshotFile );
        String base2 = FileUtils.getDirName( base1 );

        String yyymmdd = base2.substring( base2.length() - 8 );

        final List<String> lines = new ArrayList<>( 2 );

        FileUtils.read( lines, base1 + "/" + LOG_FILE, true, true );

        if ( lines.size() < 1 ) {

            _log.info( "SnapshotLogfile " + base1 + "/" + LOG_FILE + " had no entries or is missing, use modification time on file" );

            return FileUtils.getLastModifiedTS( firstSnapFile );
        }

        String   keyval = lines.get( 0 );
        String[] parts  = keyval.split( "=" );

        if ( parts.length != 2 ) throw new SMTRuntimeException( "SnapshotLogfile " + base1 + "/" + LOG_FILE + " bad format " + keyval );

        String timestamp = parts[ 1 ];

        SimpleDateFormat timeFmt = new SimpleDateFormat( TIMESTAMP_PTN );
        timeFmt.setTimeZone( getSnapshotTimezone() );

        final Date time = timeFmt.parse( timestamp );

        return time.getTime();
    }

    public String lastSnapshotDir( Env env, SnapshotType snapshotId, int maxDaysInPast ) throws FileException {

        if ( Utils.isNull( maxDaysInPast ) ) return lastSnapshotDir( env, snapshotId );

        Calendar c = Calendar.getInstance( getSnapshotTimezone() );
        c.setTimeInMillis( getTimeMS( env, snapshotId ) );

        SimpleDateFormat fileDateFmt = getFileDateFormat();

        String appDirName = getAppDirName( env, snapshotId );

        if ( !FileUtils.isDir( appDirName, false ) ) {
            return null;
        }

        for ( int i = 0; i < maxDaysInPast; i++ ) {
            final String yymmdd = fileDateFmt.format( c.getTime() );

            String dirName = getSnapshotDir( env, snapshotId, yymmdd );

            if ( FileUtils.isDir( dirName, false ) ) {
                return dirName;
            }

            c.add( Calendar.DATE, -1 );
        }

        _log.warn( "SnapshotUtils lastSnapshotDir " + snapshotId.name() + " maxAgeDays=" + maxDaysInPast + " unable to find restore to load" );

        return null;
    }

    public String lastSnapshotDir( Env env, SnapshotType snapshotId ) throws FileException {

        SimpleDateFormat fileDateFmt = getFileDateFormat();

        String appDirName = getAppDirName( env, snapshotId );

        if ( !FileUtils.isDir( appDirName, false ) ) {
            _log.info( "lastSnapshotDir : not found for " + appDirName );
            return null;
        }

        List<String> entries = FileUtils.getDirEntries( appDirName );

        if ( entries.size() == 0 ) return null;

        Collections.sort( entries, Collections.reverseOrder() );

        for ( String date : entries ) {
            String sd = appDirName + "/" + date + "/" + SNAP_DIR;

            if ( FileUtils.isDir( sd, false ) ) {
                return sd;
            }
        }

        return null;
    }

    public String makeSnapshotDir( Env env, SnapshotType snapshotId ) throws FileException {

        String yymmdd = getDateStr( env, snapshotId );

        String destName = getSnapshotDir( env, snapshotId, yymmdd );
        String tmpName  = destName + ".TEMP";

        if ( FileUtils.isDir( destName, true ) ) {
            String hhmmss = getTime( getTimeMS( env, snapshotId ) );

            if ( !FileUtils.renameDir( destName, destName + "_" + hhmmss ) ) {
                if ( FileUtils.isDir( destName, true ) ) { // cant rename previous snapshot so delete it !
                    _log.info( "SnapshotUtils unable to rename " + destName + " so removing it allowing a clean snapshot" );
                    FileUtils.rmRecurse( destName, true );
                }
            }
        }

        if ( FileUtils.isDir( tmpName, true ) ) { // remove previous failed snapshot
            FileUtils.rmRecurse( tmpName, true );
        }

        FileUtils.mkDir( tmpName );

        return tmpName;
    }

    public int removeTempSnapshots( Env env, SnapshotType snapshotId ) throws FileException {

        SimpleDateFormat fileDateFmt = getFileDateFormat();

        Calendar c = getCalendar( env, snapshotId );

        int cnt = 0;

        for ( int i = 0; i < 7; i++ ) { // just check last week

            final String yymmdd = fileDateFmt.format( c.getTime() );

            String dailyDirName = getSnapshotDir( env, snapshotId, yymmdd );

            if ( !FileUtils.isDir( dailyDirName, false ) ) {
                continue;
            }

            List<String> entries = FileUtils.getDirEntries( dailyDirName );

            if ( entries.contains( snapshotId ) ) {
                String tmpName = getSnapshotDir( env, snapshotId, yymmdd ) + ".TEMP";

                if ( FileUtils.isDir( tmpName, false ) ) {

                    FileUtils.rmRecurse( tmpName, true );

                    ++cnt;
                }
            }

            c.add( Calendar.DATE, -1 );
        }

        return cnt;
    }

    private void createLog( Env env, SnapshotType snapshotId, String dirName ) throws IOException, FileException {
        final String logFileName = dirName + "/" + LOG_FILE;

        String log = "startClockTime=" + getTimeStamp( getTimeMS( env, snapshotId ) ) + "\n";

        FileUtils.writeFile( logFileName, log );
    }

    private String getAppDirName( final Env env, final SnapshotType snapshotId ) {
        String appName = AppProps.instance().getProperty( CoreProps.APP_NAME );

        return _snapshotRootPath + "/" + appName + "/" + env + "/" + snapshotId.name();
    }

    private Calendar getCalendar( Env env, SnapshotType snapshotId ) {
        Calendar c = Calendar.getInstance( getSnapshotTimezone() );
        c.setTimeInMillis( getTimeMS( env, snapshotId ) );
        return c;
    }

    private String getDateStr( Env env, SnapshotType snapshotId ) {
        SimpleDateFormat fileDateFmt = getFileDateFormat();

        return fileDateFmt.format( new Date( getTimeMS( env, snapshotId ) ) );
    }

    private SimpleDateFormat getFileDateFormat() {
        SimpleDateFormat fileDateFmt = new SimpleDateFormat( YYYYMMDD );
        fileDateFmt.setTimeZone( getSnapshotTimezone() );
        return fileDateFmt;
    }

    private String getSnapshotDir( Env env, SnapshotType snapshotId, String yymmdd ) {
        return getAppDirName( env, snapshotId ) + "/" + yymmdd + "/" + SNAP_DIR;
    }

    private TimeZone getSnapshotTimezone() {
        return TimeZone.getTimeZone( "UTC" );
    }

    private String getTime( long time ) {
        SimpleDateFormat timeFmt = new SimpleDateFormat( TIME_PTN );
        timeFmt.setTimeZone( getSnapshotTimezone() );
        return timeFmt.format( new Date( time ) );
    }

    private long getTimeMS( Env env, SnapshotType type ) {

        return ClockFactory.get().currentTimeMillis();
    }

    private String getTimeStamp( long time ) {
        SimpleDateFormat timeFmt = new SimpleDateFormat( TIMESTAMP_PTN );
        timeFmt.setTimeZone( getSnapshotTimezone() );
        return timeFmt.format( new Date( time ) );
    }
}
