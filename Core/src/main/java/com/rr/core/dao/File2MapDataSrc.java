package com.rr.core.dao;

import com.rr.core.admin.AdminAgent;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZFunction;
import com.rr.core.lang.ZFunction2Args;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.tasks.BaseTimerTask;
import com.rr.core.tasks.ZTimerFactory;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ReflectUtils;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.rr.core.thread.RunState.Unknown;

public abstract class File2MapDataSrc<K, V> implements MapDataSrc<K, V> {

    protected static final Logger    _log             = LoggerFactory.create( File2MapDataSrc.class );
    protected static final ErrorCode ERR_REFRESH_FILE = new ErrorCode( "FSD100", "IO error refreshing file" );

    private String                             _lineTransformerClassName;
    private String                             _srcFiles;
    private String                             _refreshTime;
    private boolean                            _refreshEnabled = true;
    private RunState                           _runState       = Unknown;
    private String                             _id;
    private Map<K, V>                          _dataSet        = new ConcurrentHashMap<>();
    private ZFunction2Args<Integer, String, V> _converter;
    private BaseTimerTask                      _refreshTask;

    public File2MapDataSrc( final String id ) {
        _id = id;

        RefreshAdmin adminBean = new RefreshAdmin( this );
        AdminAgent.register( adminBean );
    }

    @Override public String getComponentId()                         { return _id; }

    @Override public Map<K, V> getMap()                              { return _dataSet; }

    @Override public V get( final K k )                              { return _dataSet.get( k ); }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

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

    public void refresh() {
        if ( _srcFiles != null ) {
            // dont update the set as that would cause concurrent exceptions instead create new one and replace
            Map<K, V> newMap = new ConcurrentHashMap<>( _dataSet.size() );

            String[] srcFiles = getSrcFiles();

            for ( String srcFile : srcFiles ) {
                final List<String> lines = new ArrayList<>();

                try {
                    FileUtils.readFiles( lines, srcFile );
                } catch( IOException e ) {
                    _log.error( ERR_REFRESH_FILE, getComponentId() + " file=" + srcFile + " : " + e.getMessage(), e );
                }

                extracted( srcFile, newMap, lines, null );
            }

            _dataSet = newMap;
        }
    }

    public String[] getSrcFiles() { return FileUtils.getFiles( _srcFiles ); }

    public void setSrcFiles( final String srcFiles )              { _srcFiles = srcFiles; }

    public void setRefreshEnabled( final boolean refreshEnabled ) { _refreshEnabled = refreshEnabled; }

    public void setRefreshTime( final String refreshTime )        { _refreshTime = refreshTime; }

    protected V convert( final int lineNo, final String line ) {
        V converted = _converter.apply( lineNo, line );
        return converted;
    }

    protected void extracted( final String srcFile, final Map<K, V> map, final List<String> lines, Set<ExchangeInstrument> allInsts ) {
        for ( int idx = 0; idx < lines.size(); ++idx ) {
            String line = lines.get( idx );

            V converted = convert( idx, line );
            if ( converted != null ) {
                map.put( getKey( converted ), converted );
            }
        }
    }

    protected ZFunction2Args<Integer, String, V> getConverter() { return _converter; }

    protected void setConverter( final ZFunction2Args<Integer, String, V> converter ) {
        _converter = converter;
    }

    protected Map<K, V> getDataSet() { return _dataSet; }

    protected abstract K getKey( final V converted );

    protected synchronized void setDataMap( final Map<K, V> dataSet ) {
        _dataSet = dataSet;
    }

    protected void setupConverter() {
        if ( _lineTransformerClassName != null ) {
            setConverter( ReflectUtils.create( _lineTransformerClassName ) );
        }
    }
}
