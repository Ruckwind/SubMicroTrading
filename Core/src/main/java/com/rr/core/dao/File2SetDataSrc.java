package com.rr.core.dao;

import com.rr.core.admin.AdminAgent;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.tasks.BaseTimerTask;
import com.rr.core.tasks.ZTimerFactory;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ReflectUtils;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;

import static com.rr.core.thread.RunState.Unknown;

public class File2SetDataSrc<T> implements SetDataSrc<T> {

    protected static final Logger    _log             = LoggerFactory.create( File2SetDataSrc.class );
    protected static final ErrorCode ERR_REFRESH_FILE = new ErrorCode( "FSD100", "IO error refreshing file" );

    private String                   _lineTransformerClassName;
    private String[]                 _srcFiles;
    private String                   _refreshTime;
    private boolean                  _refreshEnabled = true;
    private RunState                 _runState       = Unknown;
    private String                   _id;
    private Set<?>                   _dataSet;
    private Function<String, Object> _converter;
    private BaseTimerTask            _refreshTask;

    public File2SetDataSrc( final String id ) {
        _id = id;

        _dataSet = new LinkedHashSet<>();

        RefreshAdmin adminBean = new RefreshAdmin( this );
        AdminAgent.register( adminBean );
    }

    @Override public boolean contains( T v )                         { return _dataSet.contains( v ); }

    @Override public Set<T> getSet()                                 { return (Set<T>) _dataSet; }

    @Override public String getComponentId()                         { return _id; }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {

        if ( _srcFiles == null ) {
            _log.warn( getComponentId() + " MISSING srcFiles" );
        }

        setupConverter();

        refresh();

        if ( _refreshEnabled ) {

            _refreshTask = new BaseTimerTask( _id + "Refresh", TimeZone.getDefault() ) {

                @Override public void fire() {
                    refresh();
                }
            };

            ZTimerFactory.get().scheduleDaily( _refreshTask, _refreshTime, EnumSet.range( DayOfWeek.MONDAY, DayOfWeek.FRIDAY ) );
        }
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    public void refresh() {
        if ( _srcFiles != null ) {
            // dont update the set as that would cause concurrent exceptions instead create new one and replace
            Set<Object> newSet = new LinkedHashSet<>( _dataSet.size() );

            for ( String srcFile : _srcFiles ) {
                final List<String> lines = new ArrayList<>();

                try {
                    FileUtils.readFiles( lines, srcFile );
                } catch( IOException e ) {
                    _log.error( ERR_REFRESH_FILE, getComponentId() + " file=" + srcFile + " : " + e.getMessage(), e );
                }

                extracted( srcFile, newSet, lines );
            }

            _dataSet = newSet;
        }
    }

    public String[] getSrcFiles()                     { return _srcFiles; }

    protected void extracted( final String srcFile, final Set<Object> newSet, final List<String> lines ) {
        for ( String line : lines ) {
            Object converted = getConverted( line );
            if ( converted != null ) {
                newSet.add( converted );
            }
        }
    }

    protected Object getConverted( final String line ) {
        Object converted = (_converter == null) ? line : _converter.apply( line );
        return converted;
    }

    protected Function<String, Object> getConverter() {
        return _converter;
    }

    protected void setConverter( final Function<String, Object> converter ) {
        _converter = converter;
    }

    protected Set<?> getDataSet()                     { return _dataSet; }

    protected void setDataSet( final Set<?> dataSet ) { _dataSet = dataSet; }

    protected void setupConverter() {
        if ( _lineTransformerClassName != null ) {
            setConverter( ReflectUtils.create( _lineTransformerClassName ) );
        }
    }
}
