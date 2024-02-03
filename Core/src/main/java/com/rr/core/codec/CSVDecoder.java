package com.rr.core.codec;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * Simple CSV decoder to T
 * <p>
 * Note header must contain securityExchange for instrument resolution IF idSrc is not unique
 *
 * @WARNING generates GC - optimise later if necessary
 */
public class CSVDecoder<T extends Event & Reusable<T>> implements Decoder, SMTComponent {

    private static final Logger _log = LoggerFactory.create( CSVDecoder.class );

    private static final String    SECURITY_EXCHANGE = "securityExchange";
    private static final ErrorCode ERR_DECODE        = new ErrorCode( "CVD100", "CSVDecoder exception : " );
    private static final TimeZone _utcTZ = TimeZone.getTimeZone( "UTC" );
    private final Map<Field, String>                    _defaultFieldMappings;
    private final Map<String, String>                   _nonStandardMappings;
    private final Set<String>                           _ignoreColNames;
    private final String                                _id;
    private final Class<T>                              _class;
    private final Map<String, Function<Object, Object>> _columnFunctionsByName;
    private final PoolFactory<T> _factory;
    private final StringBuilder _tmpBuf = new StringBuilder();
    private final ReusableString _instKey          = new ReusableString();
    private InstrumentLocator          _instLocator;
    private String[]                   _columns;
    private Function<Object, Object>[] _columnFunctionsByIdx;
    private int       _idx;
    private TimeUtils _timeUtils;
    private Field[]   _fields;
    private boolean   _logErrors = false;
    private int _dateFieldIdx        = Constants.UNSET_INT;
    private int _timeFieldIdx        = Constants.UNSET_INT;
    private int _eventTimestampIdx   = Constants.UNSET_INT;
    private int _instFieldIdx        = Constants.UNSET_INT;
    private int _securityExchangeIdx = Constants.UNSET_INT;
    private int _securityIdSrcIdx    = Constants.UNSET_INT;
    private       boolean        _ignoreMissingMIC = false;

    @SuppressWarnings( "unchecked" )
    public CSVDecoder( String id,
                       Map<String, String> defaults,
                       Map<String, String> nonStdColMappings,
                       Set<String> ignoreCols,
                       String className ) {
        this( id, defaults, nonStdColMappings, Collections.emptyMap(), ignoreCols, className );
    }

    @SuppressWarnings( "unchecked" )
    public CSVDecoder( String id,
                       Map<String, String> defaults,
                       Map<String, String> nonStdColMappings,
                       Map<String, Function<Object, Object>> columnFunctions,
                       Set<String> ignoreCols,
                       String className ) {

        _defaultFieldMappings  = new HashMap<>( defaults.size() );
        _nonStandardMappings   = nonStdColMappings;
        _ignoreColNames        = ignoreCols;
        _columnFunctionsByName = columnFunctions;

        _id = id;

        try {
            _class = (Class<T>) Class.forName( className );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Exception getting class for className " + className + " : " + e.getMessage(), e );
        }

        _factory = SuperpoolManager.instance().getPoolFactory( _class );

        for ( Map.Entry<String, String> entry : defaults.entrySet() ) {
            String defField = entry.getKey();

            boolean found = false;

            for ( Field field : _class.getDeclaredFields() ) {
                String fieldName = field.getName();
                if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

                if ( fieldName.equalsIgnoreCase( defField ) ) {
                    _defaultFieldMappings.put( field, entry.getValue() );
                    found = true;
                    break;
                }
            }

            if ( !found ) {
                throw new RuntimeDecodingException( "Default value for field " + defField + " is not part of T" );
            }
        }
    }

    @Override public String getComponentId()                             { return _id; }    @Override public void setTimeUtils( final TimeUtils calc ) {
        _timeUtils = calc;
    }

    @Override public void setClientProfile( final ClientProfile client ) { /* unused */ }    @Override public void setInstrumentLocator( final InstrumentLocator instrumentLocator ) {
        _instLocator = instrumentLocator;
    }

    public boolean isIgnoreMissingMIC()                               { return _ignoreMissingMIC; }    @Override public InstrumentLocator getInstrumentLocator() {
        return _instLocator;
    }

    public void setIgnoreMissingMIC( final boolean ignoreMissingMIC ) { _ignoreMissingMIC = ignoreMissingMIC; }    @SuppressWarnings( "unchecked" )
    @Override public int parseHeader( final byte[] inBuffer, final int offset, final int bytesRead ) {
        String header = new String( inBuffer, offset, bytesRead );

        _columns              = header.split( "," );
        _fields               = new Field[ _columns.length ];
        _columnFunctionsByIdx = new Function[ _columns.length ];

        for ( int idx = 0; idx < _columns.length; idx++ ) {
            String col = _columns[ idx ];

            col = col.trim();

            col = overrideColumnFromFile( col );

            if ( col.equalsIgnoreCase( "idSource" ) || col.equalsIgnoreCase( "securityIdSrc" ) || col.equalsIgnoreCase( "securityIdSource" ) ) {
                _securityIdSrcIdx = idx;
                continue;
            }

            if ( col.equalsIgnoreCase( "eventTimestamp" ) ) { _eventTimestampIdx = idx; }

            if ( col.equalsIgnoreCase( "date" ) ) { _dateFieldIdx = idx; }

            if ( col.equalsIgnoreCase( "time" ) ) { _timeFieldIdx = idx; }

            if ( col.equalsIgnoreCase( "securityExchange" ) || col.equalsIgnoreCase( "MIC" ) ) { _securityExchangeIdx = idx; }

            if ( col.equalsIgnoreCase( "contract" ) || col.equalsIgnoreCase( "securityId" ) || col.equalsIgnoreCase( "instrumentId" ) || col.equalsIgnoreCase( "securityDesc" ) ) {
                _instFieldIdx = idx;

                for ( Field field : _class.getDeclaredFields() ) {
                    String fieldName = field.getName();
                    if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

                    if ( fieldName.equalsIgnoreCase( "inst" ) || fieldName.equalsIgnoreCase( "instrument" ) ) {
                        _fields[ idx ] = field;
                        break;
                    }
                }
                continue;
            }

            if ( !_ignoreColNames.contains( col ) ) {

                Function<Object, Object> mapFunc = _columnFunctionsByName.get( col );

                _columnFunctionsByIdx[ idx ] = mapFunc;

                for ( Field field : _class.getDeclaredFields() ) {
                    String fieldName = field.getName();
                    if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

                    if ( fieldName.equalsIgnoreCase( col ) ) {
                        _fields[ idx ] = field;
                        break;
                    }
                }

                if ( _fields[ idx ] == null && idx != _instFieldIdx && idx != _dateFieldIdx && idx != _timeFieldIdx && idx != _securityExchangeIdx && idx != _securityIdSrcIdx ) {
                    throw new RuntimeDecodingException( "Header has field " + col + " which is not part of " + _class.getName() );
                }

                if ( _fields[ idx ] == null ) {
                    _log.log( Level.debug, id() + " skip hdr field " + col + " as not in class" );
                }
            }
        }

        if ( _securityExchangeIdx == Constants.UNSET_INT && !_ignoreMissingMIC ) {
            throw new RuntimeDecodingException( "Header " + header + " missing " + SECURITY_EXCHANGE );
        }

        return 0;
    }

    private int c( final char ch ) {
        if ( Character.isDigit( ch ) ) return ch - '0';

        if ( ch == 0 ) return 0;

        throw new SMTRuntimeException( "Unexpected character, expected a digit (check date/time fields) not " + ch );
    }

    private Event doDecode( final byte[] msg, final int offset, final int maxIdx ) {
        String line = new String( msg, offset, maxIdx );

        String vals[] = StringUtils.split( line, ',' );

        if ( vals.length != _fields.length ) {
            throw new RuntimeDecodingException( "CSVDecoder field mismatch, expected " + _fields.length + " Fields but found " + vals.length );
        }

        T event = _factory.get();

        for ( Map.Entry<Field, String> e : _defaultFieldMappings.entrySet() ) {
            ReflectUtils.setMemberFromString( event, e.getKey(), e.getValue() );
        }

        SecurityIDSource idSource = getIdSrc( vals );

        ExchangeCode code;

        if ( _ignoreMissingMIC && Utils.isNull( _securityExchangeIdx ) || _ignoreMissingMIC && vals[ _securityExchangeIdx ].length() == 0 ) {
            code = ExchangeCode.UNKNOWN;
        } else {
            code = ExchangeCode.getFromMktSegmentMIC( TLC.safeCopy( vals[ _securityExchangeIdx ] ) );
        }

        char y1 = 0, y2 = 0, y3 = 0, y4 = 0;
        char m1 = 0, m2 = 0;
        char d1 = 0, d2 = 0;

        char h1   = 0, h2 = 0;
        char min1 = 0, min2 = 0;
        char s1   = 0, s2 = 0;

        long unixTime = Constants.UNSET_LONG;

        for ( int idx = 0; idx < vals.length; idx++ ) {
            Field f = _fields[ idx ];

            String v = vals[ idx ];

            Object val = v;

            Function<Object, Object> mapFunc = _columnFunctionsByIdx[ idx ];

            if ( mapFunc != null ) {
                val = mapFunc.apply( v );
            } else if ( idx == _eventTimestampIdx ) {

                unixTime = TimeUtilsFactory.safeTimeUtils().parseUTCStringToInternalTime( v.getBytes(), 0, v.length() );

            } else if ( idx == _dateFieldIdx ) { // 2007-12-24

                if ( v.length() < 10 ) throw new RuntimeDecodingException( "Invalid date of " + v + " expected min 10 bytes" );

                y1 = v.charAt( 0 );
                y2 = v.charAt( 1 );
                y3 = v.charAt( 2 );
                y4 = v.charAt( 3 );
                m1 = v.charAt( 5 );
                m2 = v.charAt( 6 );
                d1 = v.charAt( 8 );
                d2 = v.charAt( 9 );

                _tmpBuf.setLength( 0 );

                _tmpBuf.append( y1 ).append( y2 ).append( y3 ).append( y4 );
                _tmpBuf.append( m1 ).append( m2 );
                _tmpBuf.append( d1 ).append( d2 );

                val = _tmpBuf.toString();

            } else if ( idx == _timeFieldIdx ) { // 12:15:00

                if ( v.length() < 8 ) throw new RuntimeDecodingException( "Invalid date of " + v + " expected min 8 bytes" );

                h1   = v.charAt( 0 );
                h2   = v.charAt( 1 );
                min1 = v.charAt( 3 );
                min2 = v.charAt( 4 );
                s1   = v.charAt( 6 );
                s2   = v.charAt( 7 );

                _tmpBuf.setLength( 0 );

                _tmpBuf.append( h1 ).append( h2 );
                _tmpBuf.append( min1 ).append( min2 );
                _tmpBuf.append( s1 ).append( s2 );

                val = _tmpBuf.toString();

            } else if ( idx == _instFieldIdx ) {
                _instKey.copy( v );

                Instrument inst = null;

                if ( idSource == SecurityIDSource.StrategyId ) {
                    inst = _instLocator.getInst( _instKey, idSource, code );
                } else {
                    inst = _instLocator.getExchInst( _instKey, idSource, code );
                }

                if ( inst == null ) {
                    throw new SMTRuntimeException( _id + " Invalid instrument with key " + _instKey );
                }

                val = inst;
            }

            ReflectUtils.setMember( event, f, val );
        }

        if ( Utils.isNull( unixTime ) ) {
            int yyyy = c( y1 ) * 1000 + c( y2 ) * 100 + c( y3 ) * 10 + c( y4 );
            int mm   = c( m1 ) * 10 + c( m2 );
            int dd   = c( d1 ) * 10 + c( d2 );
            int hh   = c( h1 ) * 10 + c( h2 );
            int mins = c( min1 ) * 10 + c( min2 );
            int ss   = c( s1 ) * 10 + c( s2 );
            int ms   = 0;

            if ( yyyy != 0 && (yyyy < 1900 || yyyy > 2200) ) throw new SMTRuntimeException( "Bad year of " + yyyy );

            unixTime = CommonTimeUtils.ddmmyyyyToUnixTime( _utcTZ, yyyy, mm, dd, hh, mins, ss, ms );
        }

        /**
         * set the bar event timestamp to end of the bar
         */
        long internalTime = CommonTimeUtils.unixTimeToInternalTime( unixTime );

        event.setEventTimestamp( internalTime );

        return event;
    }    @Override public Event decode( final byte[] msg, final int offset, final int maxIdx ) {
        try {
            return doDecode( msg, offset, maxIdx );
        } catch( Exception e ) {
            if ( _logErrors ) {
                _log.error( ERR_DECODE, e.getMessage(), e );
            }
            return null;
        }
    }

    private SecurityIDSource getIdSrc( final String[] vals ) {
        if ( _securityIdSrcIdx == Constants.UNSET_INT ) return SecurityIDSource.Unknown;
        String idSrcStr = vals[ _securityIdSrcIdx ];
        if ( idSrcStr == null ) return SecurityIDSource.Unknown;
        return SecurityIDSource.getVal( (byte) idSrcStr.charAt( 0 ) );
    }

    private String overrideColumnFromFile( final String col ) {
        String override = _nonStandardMappings.get( col );

        return (override != null) ? override : col;
    }



    @Override public ResyncCode resync( final byte[] msg, final int offset, final int maxIdx ) {

        _idx = 0;

        while( _idx < maxIdx ) {
            if ( msg[ _idx++ ] == '\n' ) {
                return ResyncCode.FOUND_FULL_HEADER;
            }
        }

        return ResyncCode.FOUND_PARTIAL_HEADER_NEED_MORE_DATA;
    }





    @Override public int getLength() {
        return _idx;
    }

    @Override public int getSkipBytes()                                  { return 0; }

    @Override public Event postHeaderDecode()                            { return null; }

    @Override public void setNanoStats( final boolean nanoTiming )       { /* unused */ }



    @Override public void setReceived( final long nanos )                { /* unused */ }

    @Override public long getReceived()                                  { return 0; }


}
