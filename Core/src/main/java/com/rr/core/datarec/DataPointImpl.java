package com.rr.core.datarec;

import com.rr.core.lang.*;
import com.rr.core.model.BaseReusableEvent;
import com.rr.core.properties.AppProps;

import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/**
 * non thread safe data point
 */

public class DataPointImpl extends BaseReusableEvent<DataPointImpl> implements DataPoint {

    private static final AtomicLong _nextId = new AtomicLong( 0 );

    private static final int DEFAULT_SIZE_COMP_ID = 20;
    private static final int DEFAULT_SIZE_GRP_KEY = 20;

    private Env            _env;
    private String         _srcAppName;
    private long           _liveTS      = Constants.UNSET_LONG;
    private String         _eventType;
    private ReusableString _compOwnerId = new ReusableString( DEFAULT_SIZE_COMP_ID );
    private ReusableString _grpKey      = new ReusableString( DEFAULT_SIZE_GRP_KEY );
    private TimeZone       _tz;
    private Object         _datum;

    public DataPointImpl() {
        setEnv( AppProps.instance().getEnv() );
        setSrcAppName( AppProps.instance().getAppName() );
        setMsgSeqNum( 0 );
        _tz = TimeZone.getTimeZone( "UTC" );
    }

    @Override public void dump( ReusableString dest ) {
        dest.append( "DataPointImpl{" )
            .append( "_env=" ).append( _env )
            .append( ", _srcAppName='" ).append( _srcAppName )
            .append( ", _liveTS=" ).append( _liveTS )
            .append( ", _seqNum=" ).append( getMsgSeqNum() )
            .append( ", _eventType=" ).append( _eventType )
            .append( ", _compOwnerId=" ).append( _compOwnerId )
            .append( ", _grpKey=" ).append( _grpKey )
            .append( '}' );
    }

    @Override public ReusableString getCompOwnerId() { return _compOwnerId; }

    @Override public Object getDatum()               { return _datum; }

    @Override public Env getEnv()                    { return _env; }

    @Override public String getEventType()           { return _eventType; }

    @Override public ReusableString getGroupKey()    { return _grpKey; }

    @Override public long getLiveTS()                { return _liveTS; }

    @Override public String getSrcAppName()          { return _srcAppName; }

    @Override public TimeZone getTz()                { return _tz; }

    @Override public void nextSeqNum()                           { setMsgSeqNum( (int) _nextId.incrementAndGet() ); }

    @Override public void setTimeZone( final TimeZone timeZone ) { _tz = timeZone; }

    @Override public void stamp() {
        long clockTime = ClockFactory.get().currentTimeMillis();
        setEventTimestamp( clockTime );

        if ( ClockFactory.get() != ClockFactory.getLiveClock() ) {
            setLiveTS( ClockFactory.getLiveClock().currentTimeMillis() );
        }

        nextSeqNum();
    }

    public void setSrcAppName( final String srcAppName )         { _srcAppName = srcAppName; }

    public void setLiveTS( final long liveTS )                   { _liveTS = liveTS; }

    public void setEventType( final String eventType )           { _eventType = eventType; }

    public void setEnv( final Env env )                          { _env = env; }

    public void setDatum( final Object datum )                   { _datum = datum; }

    public void setCompOwnerId( final ZString compOwnerId )      { _compOwnerId.copy( compOwnerId ); }

    @Override public ReusableType getReusableType()  { return CoreReusableType.JSONDataPoint; }

    @Override public int hashCode() {
        int result = (int) (_liveTS ^ (_liveTS >>> 32));
        result = 31 * result + _eventType.hashCode();
        result = 31 * result + _compOwnerId.hashCode();
        return result;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final DataPointImpl dataPoint = (DataPointImpl) o;

        if ( getMsgSeqNum() != dataPoint.getMsgSeqNum() ) return false;
        if ( _liveTS != dataPoint._liveTS ) return false;
        if ( _env != dataPoint._env ) return false;
        if ( !_srcAppName.equals( dataPoint._srcAppName ) ) return false;
        if ( !_eventType.equals( dataPoint._eventType ) ) return false;
        if ( !_compOwnerId.equals( dataPoint._compOwnerId ) ) return false;
        if ( _grpKey != null ? !_grpKey.equals( dataPoint._grpKey ) : dataPoint._grpKey != null ) return false;
        if ( _tz != null ? !_tz.equals( dataPoint._tz ) : dataPoint._tz != null ) return false;
        if ( _datum != null ? !_datum.equals( dataPoint._datum ) : dataPoint._datum != null ) return false;

        return true;
    }

    @Override public void reset() {
        super.reset();

        _datum = null;
        _compOwnerId.reset();
        _grpKey.reset();
        _eventType = null;
        _liveTS    = Constants.UNSET_LONG;
        setEnv( AppProps.instance().getEnv() );
        setSrcAppName( AppProps.instance().getAppName() );
        _tz = TimeZone.getTimeZone( "UTC" );
    }

    public void setGrpKey( final ZString grpKey )                { _grpKey.copy( grpKey ); }
}
