/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.Env;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;

/**
 * load application properties files
 * <p>
 * when use include can now supply local properties to be directly replaced in the file read in
 * idea is to avoid repition of same component and allow it to be reused by supplying local properties that can be used in key and value before resolve happens
 * <p>
 * ${var}      is a variable resolved after all property files read in
 * %{localVar} is a local variable which was passed as part of the include command, substitution is immediate
 * <p>
 * local variables always converted to uppercase
 *
 * @author Richard Rose
 * @INCLUDE fastfixsession.properties ID=cme05 port=11223 host=138.42.121.18
 * @INCLUDE fastfixsession.properties ID=cme06 PORT=11224 HOST=138.42.121.18
 * <p>
 * within fastfixsession.properties
 * <p>
 * sess.up.fastfix.%{ID}.port=%{PORT}
 */
public class AppProps implements SMTComponent {

    private static final Logger   _log      = ConsoleFactory.console( AppProps.class, Level.info );
    private static final AppProps _instance = new AppProps();

    private static final int       MAX_DEPTH               = 10;
    private static final String    INCLUDE_DIRECTIVE       = "@INCLUDE ";
    private static final String    FORCE_INCLUDE_DIRECTIVE = "@REREAD ";
    private static final ErrorCode FAIL_LOAD               = new ErrorCode( "APP100", "Failed to load property file" );
    private static final String    EXPR_STR                = "eval(";
    private final Map<String, String> _props       = new LinkedHashMap<>();
    private       boolean             _init        = false;
    private       PropertyTags        _propSet     = CoreProps.instance();
    private       String              _id          = "AppProps";
    private       Set<String>         _filesLoaded = new HashSet<>();

    private Map<String, String> _caseSavedProps = new LinkedHashMap<>();

    private String _errs = "";
    private int    _err  = 0;

    private Exception _firstException = null;
    private String    _topFile        = null;
    private String    _appName;
    private Env       _env;

    public static AppProps instance() { return _instance; }

    protected AppProps() {
        System.setProperty( "line.separator", "\n" );
        checkTagOverride( CoreProps.APP_TAGS, System.getProperty( CoreProps.APP_TAGS ) );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public String toString() {
        ReusableString m = new ReusableString();

        for ( Map.Entry<String, String> entry : _caseSavedProps.entrySet() ) {
            m.append( entry.getKey() ).append( "=" ).append( entry.getValue() ).append( System.lineSeparator() );
        }

        return m.toString();
    }

    public synchronized void apply( BiFunction<String, String, String> valFunc ) {
        for ( Map.Entry<String, String> e : _caseSavedProps.entrySet() ) {
            String newVal = valFunc.apply( e.getKey(), e.getValue() );
            if ( newVal != e.getValue() ) {
                e.setValue( newVal );
                _props.put( e.getKey().toLowerCase(), newVal );
            }
        }
    }

    public String getAppName() { return _appName; }

    protected void setAppName( final String appName ) { _appName = appName; }

    public boolean getBooleanProperty( String property, boolean isMand, boolean defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val != null ) {
            boolean bVal;

            try {
                bVal = StringUtils.parseBoolean( val ) || "1".equals( val );
            } catch( NumberFormatException e ) {
                throw new SMTRuntimeException( "AppProperties property " + property + " has invalid boolean (" + val + ")" );
            }

            return bVal;
        }

        _log.log( Level.trace, "AppProperties : defaulting " + property + " to " + defaultVal );

        return defaultVal;
    }

    public double getDoubleProperty( String property, boolean isMand, double defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val != null ) {
            double dval;

            try {
                dval = Double.parseDouble( val );
            } catch( NumberFormatException e ) {
                throw new SMTRuntimeException( "AppProperties property " + property + " has invalid double (" + val + ")" );
            }

            return dval;
        }

        _log.log( Level.trace, "AppProperties : defaulting " + property + " to " + defaultVal );

        return defaultVal;
    }

    public Env getEnv()        { return _env; }

    protected void setEnv( final Env env )            { _env = env; }

    public String getFile() {
        return _topFile;
    }

    public int getIntProperty( String property ) {
        String val = validateAndGet( property, true );

        int iVal;

        try {
            iVal = Integer.parseInt( val );
        } catch( NumberFormatException e ) {
            throw new SMTRuntimeException( "AppProperties property " + property + " has invalid int (" + val + ")" );
        }

        return iVal;
    }

    public int getIntProperty( String property, boolean isMand, int defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val != null ) {
            int iVal;

            try {
                iVal = Integer.parseInt( val );
            } catch( NumberFormatException e ) {
                throw new SMTRuntimeException( "AppProperties property " + property + " has invalid int (" + val + ")" );
            }

            return iVal;
        }

        _log.log( Level.trace, "AppProperties : defaulting " + property + " to " + defaultVal );

        return defaultVal;
    }

    public long getLongProperty( String property, boolean isMand, long defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val != null ) {
            long lVal;

            try {
                lVal = Long.parseLong( val );
            } catch( NumberFormatException e ) {
                throw new SMTRuntimeException( "AppProperties property " + property + " has invalid long (" + val + ")" );
            }

            return lVal;
        }

        _log.log( Level.trace, "AppProperties : defaulting " + property + " to " + defaultVal );

        return defaultVal;
    }

    public String[] getMatchedKeys( final String propertyBase ) {

        Set<String> matchedVals = new LinkedHashSet<>();

        for ( Map.Entry<String, String> entry : _props.entrySet() ) {
            String key = entry.getKey();

            if ( startsWithIgnoreCase( key, propertyBase ) ) {

                matchedVals.add( key );
            }
        }

        return matchedVals.toArray( new String[ 0 ] );
    }

    /**
     * @param propertyBase pattern to applu to all properties
     * @return list of values matching the suuplied pattern
     */
    public String[] getMatchedVals( final String propertyBase ) {

        Set<String> matchedVals = new LinkedHashSet<>();

        for ( Map.Entry<String, String> entry : _props.entrySet() ) {
            String key = entry.getKey();

            if ( startsWithIgnoreCase( key, propertyBase ) ) {

                matchedVals.add( entry.getValue() );
            }
        }

        return matchedVals.toArray( new String[ 0 ] );
    }

    public String[] getNodes( String propertyBase ) {
        String[]    parts = propertyBase.split( "\\." );
        int         depth = parts.length;
        Set<String> nodes = new LinkedHashSet<>();

        for ( String key : _props.keySet() ) {
            String[] keyParts = key.split( "\\." );
            int      keyDepth = keyParts.length;

            if ( keyDepth > depth && startsWithIgnoreCase( key, propertyBase ) ) {
                String finalTag = keyParts[ depth ]; // arrays are 0 indexed

                nodes.add( finalTag );
            }
        }

        return nodes.toArray( new String[ 0 ] );
    }

    public String[] getNodesWithCaseIntact( String propertyBase ) {
        String[]    parts = propertyBase.split( "\\." );
        int         depth = parts.length;
        Set<String> nodes = new LinkedHashSet<>();

        for ( String key : _caseSavedProps.keySet() ) {
            String[] keyParts = key.split( "\\." );
            int      keyDepth = keyParts.length;

            if ( keyDepth > depth && startsWithIgnoreCase( key, propertyBase ) ) {
                String finalTag = keyParts[ depth ]; // arrays are 0 indexed

                nodes.add( finalTag );
            }
        }

        return nodes.toArray( new String[ 0 ] );
    }

    public String getProperty( String property ) {
        return validateAndGet( property, true );
    }

    public String getProperty( String property, boolean isMand, String defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val == null && defaultVal != null ) {
            _log.log( Level.trace, "AppProperties : defaulting " + property + " to " + defaultVal );
            val = defaultVal;
        }

        return val;
    }

    public <T extends Enum<T>> T getProperty( final String property, final Class<T> envClass ) {
        String val = validateAndGet( property, true );

        T eVal;

        EnumSet<T> set = EnumSet.allOf( envClass );
        for ( T v : set ) {
            if ( v.name().toLowerCase().equalsIgnoreCase( val.toLowerCase() ) ) {
                return v;
            }
        }

        throw new SMTRuntimeException( "AppProperties property " + property + " has invalid enum value of (" + val + ") for " + envClass.getSimpleName() );
    }

    public <T extends Enum<T>> T getProperty( final String property, boolean isMand, final Class<T> envClass, T defaultVal ) {
        String val = validateAndGet( property, isMand );

        if ( val == null ) return defaultVal;

        T eVal;

        EnumSet<T> set = EnumSet.allOf( envClass );
        for ( T v : set ) {
            if ( v.name().toLowerCase().equalsIgnoreCase( val.toLowerCase() ) ) {
                return v;
            }
        }

        return defaultVal;
    }

    public <T extends Enum<T>> void getPropertySet( final String property, final Class<T> envClass, EnumSet<T> outSet ) {
        String sval = validateAndGet( property, true );

        String[] vals = StringUtils.split( sval, ',' );

        EnumSet<T> set = EnumSet.allOf( envClass );

        for ( String val : vals ) {

            val = val.trim();

            if ( val.length() > 0 ) {
                for ( T v : set ) {
                    if ( v.name().toLowerCase().equalsIgnoreCase( val.toLowerCase() ) ) {
                        outSet.add( v );
                    } else {
                        throw new SMTRuntimeException( "AppProperties property " + property + " has invalid enum value of (" + sval + ") for " + envClass.getSimpleName() );
                    }
                }
            }
        }
    }

    public void init( String envPropertyFile, String appPropertyFile ) throws Exception {
        _topFile = appPropertyFile;

        _firstException = null;
        _errs           = "";
        _err            = 0;

        SimpleDateFormat sdf   = new SimpleDateFormat( "yyyyMMdd" );
        Calendar         c     = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
        String           today = sdf.format( c.getTime() );

        CommonTimeUtils.lastWorkingDay( null, c, 0 );
        String lastBizDay = sdf.format( c.getTime() );

        put( CoreProps.BACKTEST_ROOT, System.getProperty( CoreProps.BACKTEST_ROOT ) );
        put( CoreProps.USER_HOME, System.getProperty( "user.home" ) );
        put( CoreProps.UTC_TODAY, today );
        put( CoreProps.UTC_LAST_WEEK_DAY, lastBizDay );

        CommonTimeUtils.lastWorkingDay( null, c, 1 );
        String yestBizDay = sdf.format( c.getTime() );
        put( CoreProps.UTC_YESTERDAY_WEEK_DAY, yestBizDay );

        Map<String, String> localProps = new LinkedHashMap<>();
        Map<String, String> prop2File  = new LinkedHashMap<>();
        if ( envPropertyFile != null ) {
            loadProps( envPropertyFile, localProps, prop2File );
        }
        if ( appPropertyFile != null ) {
            loadProps( appPropertyFile, localProps, prop2File );
        }
        try {
            resolveProps( prop2File );
        } catch( Exception e ) {
            _log.warn( e.getMessage() );

            if ( _firstException == null ) {
                _firstException = e;
            }
        }

        if ( _firstException != null ) {
            _log.error( FAIL_LOAD, appPropertyFile + _errs, _firstException );

            throw _firstException;
        }

        setInit();
    }

    public void init( String propertyFile, PropertyTags validNames ) throws Exception {

        setPropSet( validNames );
        Map<String, String> prop2File = new LinkedHashMap<>();
        if ( propertyFile != null ) {
            loadProps( propertyFile, new LinkedHashMap<>(), prop2File );
        }
        resolveProps( prop2File );
        setInit();
    }

    public void init( String appId ) {
        override( CoreProps.APP_NAME, appId );
        setInit();
    }

    public void init( AppProps other ) {
        if ( other != null ) {
            setPropSet( other._propSet );
            _props.putAll( other._props );
            _caseSavedProps.putAll( other._caseSavedProps );
            resolveProps( new LinkedHashMap<>() );
        }
        setInit();
    }

    public void init( Map<String, String> overrides ) {
        overrides.entrySet().stream().forEach( ( e ) -> override( e.getKey(), e.getValue() ) );
        setInit();
    }

    public boolean isProd() {
        Env env = getProperty( CoreProps.RUN_ENV, false, Env.class, Env.DEV );

        return env.isProd();
    }

    public boolean isProdOrUAT() {
        Env env = getProperty( CoreProps.RUN_ENV, false, Env.class, Env.DEV );

        return env.isProd() || env == Env.UAT;
    }

    /**
     * Really only for use in tests, any values placed in the properties will NOT be resolved
     *
     * @param key
     * @param value
     * @return prev value
     */
    public String override( String key, String value ) {
        final String old = put( key, value );

        if ( _init ) {
            setSpecialProps(); // incase any special props have changed
        }

        return old;
    }

    public String resolve( String value ) {
        return doResolve( value, 0, true, null );
    }

    public final void setPropSet( PropertyTags validNames ) {
        _propSet = validNames;
    }

    protected final void procLine( String line, String propertyFile, int lineNo, Map<String, String> localProps, Map<String, String> prop2File ) throws Exception {
        String entry = line.trim();

        if ( entry.startsWith( "#" ) )
            return;

        if ( entry.length() > 0 ) {

            String directive = INCLUDE_DIRECTIVE;

            boolean includeFile      = entry.startsWith( directive );
            boolean forceIncludeFile = false;

            if ( !includeFile ) {
                directive        = FORCE_INCLUDE_DIRECTIVE;
                forceIncludeFile = entry.startsWith( directive );
            }

            if ( includeFile || forceIncludeFile ) {
                entry = checkLocalVarSubst( entry, localProps, false, propertyFile, lineNo );

                String postInclude = entry.substring( directive.length() ).trim();

                if ( postInclude.length() > 0 ) {
                    String[] parts = postInclude.split( " +" );

                    String fileToInclude = doResolve( parts[ 0 ].trim(), 0, false, null );

                    if ( forceIncludeFile || !_filesLoaded.contains( entry ) ) {

                        _filesLoaded.add( entry );

                        // new local properties defaults to current properties and then allows override
                        Map<String, String> newLocalProps = new LinkedHashMap<>( localProps );

                        for ( int i = 1; i < parts.length; i++ ) {
                            String[] keyVal = StringUtils.split( parts[ i ], '=' );
                            if ( keyVal.length == 2 ) {
                                String key = keyVal[ 0 ].trim().toUpperCase();
                                String val = keyVal[ 1 ].trim();

                                if ( key.length() > 0 && val.length() > 0 ) {
                                    newLocalProps.put( key, val );
                                } else {
                                    newLocalProps.put( key, val );

                                    _log.log( Level.high, propertyFile + " : include line local property is empty, idx=" + i + ", entry=[" + parts[ i ] + "] line=" + line );
                                }
                            }
                        }

                        loadProps( fileToInclude, newLocalProps, prop2File );
                    }
                }

                return;
            }

            entry = checkLocalVarSubst( entry, localProps, true, propertyFile, lineNo );

            if ( entry != null ) {
                int equalIdx = entry.indexOf( "=" );

                if ( equalIdx > 0 && (equalIdx + 1) < entry.length() ) {
                    String property = entry.substring( 0, equalIdx ).trim();
                    String val      = entry.substring( equalIdx + 1 ).trim();

                    val = removeQuotes( val );

                    checkTagOverride( property, val );

                    if ( !property.startsWith( "log" ) ) {
                        // val can be a variable (ie no "." seperators or a property with "." seperators)
                        String finalTag = getFinalTag( property );

                        if ( !property.equals( finalTag ) && !property.startsWith( "map." ) && !_propSet.isValidTag( finalTag ) ) {
                            throw new InvalidPropertyException( "AppProperties property " + finalTag + " (from " + property +
                                                                ") is not in property set " + _propSet.getSetName() );
                        }
                    }

                    put( property, val );

                    prop2File.put( property, propertyFile + ":" + lineNo );

                } else if ( entry.length() > 0 && entry.charAt( entry.length() - 1 ) == '=' ) {
                    String property = entry.substring( 0, equalIdx ).trim();

                    // val can be a variable (ie no "." seperators or a property with "." seperators)
                    String finalTag = getFinalTag( property );

                    if ( !property.equals( finalTag ) && !_propSet.isValidTag( finalTag ) ) {
                        throw new SMTRuntimeException( "AppProperties property " + finalTag + " (from " + property +
                                                       ") is not in property set " + _propSet.getSetName() );
                    }

                    put( property, "" );
                }
            }
        }
    }

    protected String put( String key, String value ) {
        String old = _props.put( key.toLowerCase(), value );
        _caseSavedProps.put( key, value );
        return old;
    }

    protected final void resolveProps( final Map<String, String> prop2File ) {

        for ( Map.Entry<String, String> entry : _props.entrySet() ) {
            String value = entry.getValue();

            try {
                value = doResolve( value, 0, true, entry.getKey() );
            } catch( SMTRuntimeException e ) {
                throw new SMTRuntimeException( "Error resolving value of " + prop2File + "/" + entry.getKey() + " : " + e.getMessage(), e );
            }

            value = evalExpr( value );

            entry.setValue( value );
        }

        for ( Map.Entry<String, String> entry : _caseSavedProps.entrySet() ) {

            String property = entry.getKey();
            String value    = entry.getValue();

            value = doResolve( value, 0, true, property );

            value = evalExpr( value );

            entry.setValue( value );

            String location = prop2File.get( property );

            if ( location != null ) {
                _log.info( "AppProperties [" + location + "] set " + property + "=" + value );
            }
        }
    }

    protected final void setInit() {
        _init = true;

        setSpecialProps();
    }

    protected void setSpecialProps() {
        _appName = getProperty( CoreProps.APP_NAME );

        String spawnIteration = System.getProperty( CoreProps.SPAWN_ITERATION );

        if ( spawnIteration != null ) {
            put( CoreProps.SPAWN_ITERATION, spawnIteration );

            _appName = _appName + "/" + spawnIteration;
        }

        _env = getProperty( CoreProps.RUN_ENV, false, Env.class, Env.DEV );
        System.out.println( "Setting unix line seperator" );
        String home = System.getProperty( "user.home" );
        put( CoreProps.USER_HOME, home );
        System.out.println( "Setting user home to " + home );

    }

    private String checkLocalVarSubst( String entry, Map<String, String> localProps, boolean resolve, final String propertyFile, final int lineNo ) {
        if ( entry == null ) return null;

        if ( resolve ) {
            entry = doResolve( entry, 0, false, null );
        }

        int lastIdx     = 0;
        int varStartIdx = entry.indexOf( "%{" );

        if ( varStartIdx == -1 ) return entry;

        StringBuilder val = new StringBuilder();

        do {
            int varEndIdx = entry.indexOf( '}', varStartIdx );

            if ( varEndIdx == -1 ) {
                val.append( entry.substring( lastIdx ) );
                break;
            }

            val.append( entry, lastIdx, varStartIdx );

            int defIdx = entry.indexOf( ":-", varStartIdx );

            String var;
            String defaultVal = null;

            if ( defIdx == -1 ) {
                var = entry.substring( varStartIdx + 2, varEndIdx ).toUpperCase();
            } else {
                var        = entry.substring( varStartIdx + 2, defIdx ).toUpperCase();
                defaultVal = entry.substring( defIdx + 2, varEndIdx );
            }

            String varVal = localProps.get( var );
            if ( varVal == null ) {
                if ( defaultVal != null ) {
                    _log.info( "Configuration local property %{" + var + "} is not defined, using default val " + defaultVal + " for " + propertyFile + ":" + lineNo );

                    varVal = defaultVal;

                } else {
                    _log.info( "Configuration error local property %{" + var + "} is not defined, setting to null for " + propertyFile + ":" + lineNo );
                    return null;
                }
            }

            val.append( varVal );

            lastIdx = varEndIdx + 1;

            if ( lastIdx >= entry.length() ) break; // macro was at end of value

            varStartIdx = entry.indexOf( "%{", lastIdx );

        } while( varStartIdx > 0 );

        if ( lastIdx < entry.length() ) {
            val.append( entry.substring( lastIdx ) );
        }

        return val.toString();
    }

    private void checkTagOverride( String property, String val ) {
        if ( val != null && val.trim().length() > 0 && CoreProps.APP_TAGS.equals( property ) ) {
            PropertyTags props = ReflectUtils.findInstance( val.trim() );
            setPropSet( props );
        }
    }

    /**
     * value could be a whole key=value (whwer property is null)  or  just a value where property will be set IF part of the initial file load
     */
    private String doResolve( String entry, int depth, boolean failIfVarMissing, String property ) {
        if ( entry == null ) {
            return null;
        }

        if ( property == null ) {
            int keyValSep = entry.indexOf( "=" );

            if ( keyValSep != -1 ) {
                property = entry.substring( 0, keyValSep );
            }
        }

        ++depth;

        if ( depth > MAX_DEPTH ) {
            throw new SMTRuntimeException( "Config recursive reference error with " + entry );
        }

        int lastIdx     = 0;
        int varStartIdx = entry.indexOf( "${" );

        if ( varStartIdx == -1 ) return entry;

        StringBuilder val = new StringBuilder();

        do {
            int varEndIdx = entry.indexOf( '}', varStartIdx );

            if ( varEndIdx == -1 ) {
                val.append( entry.substring( lastIdx ) );
                break;
            }

            val.append( entry, lastIdx, varStartIdx );

            int defaultValIdx = entry.indexOf( ":-", varStartIdx );

            if ( defaultValIdx == -1 ) { // ${IDENTIFIER}

                String identifier  = entry.substring( varStartIdx + 2, varEndIdx );
                String var         = identifier.toLowerCase();
                String existingVal = _props.get( var );

                if ( identifier.equalsIgnoreCase( property ) ) { // A=${A}xxxx

                    if ( existingVal == null ) {
                        if ( failIfVarMissing ) {
                            throw new SMTRuntimeException( "Configuration error ${" + entry.substring( varStartIdx + 2, varEndIdx ) + "} is not defined" );
                        }
                        val.append( "${" ).append( identifier ).append( "}" );
                    } else {
                        if ( failIfVarMissing ) {
                            val.append( doResolve( existingVal.trim(), depth, failIfVarMissing, identifier ) );        // *** RECURSE ***
                        } else {

                            val.append( "${" ).append( identifier ).append( ":-" ).append( existingVal ).append( "}" );
                        }
                    }

                } else if ( existingVal == null ) {

                    if ( failIfVarMissing ) {
                        throw new SMTRuntimeException( "Configuration error ${" + entry.substring( varStartIdx + 2, varEndIdx ) + "} is not defined" );
                    } else {
                        val.append( "${" ).append( identifier ).append( "}" );
                    }

                } else {

                    val.append( doResolve( existingVal.trim(), depth, failIfVarMissing, identifier ) );        // *** RECURSE ***
                }

            } else {  // ${IDENTIFIER:-defaultVal}

                String identifier = entry.substring( varStartIdx + 2, defaultValIdx );

                String varOrig  = identifier;
                String varLower = varOrig.toLowerCase();
                String varVal   = _props.get( varLower );

                int closeBraceCnt = 0;
                int openBraceCnt  = 1;

                char prev = '\0';

                for ( int idx = varStartIdx + 2; idx < entry.length(); idx++ ) {
                    char ch = entry.charAt( idx );

                    if ( prev == '$' && ch == '{' ) {
                        ++openBraceCnt;
                    }
                    if ( ch == '}' ) {
                        ++closeBraceCnt;
                    }

                    if ( openBraceCnt == closeBraceCnt ) {
                        varEndIdx = idx;
                        break;
                    }
                    prev = ch;
                }

                String defaultVal = entry.substring( defaultValIdx + 2, varEndIdx );

                if ( identifier.equalsIgnoreCase( property ) ) { // A=${A:-yyy}xxxx

                    if ( varVal == null ) {
                        if ( failIfVarMissing ) {
                            String resolvedDefault = doResolve( defaultVal.trim(), depth, failIfVarMissing, varOrig );
                            val.append( resolvedDefault );
                            _log.info( getComponentId() + " resolve (A) VAR " + varOrig + " setting to resolved default value [" + resolvedDefault + "]" );
                        } else {
                            val.append( "${" ).append( identifier ).append( ":-" ).append( defaultVal ).append( "}" );
                        }
                    } else {
                        if ( failIfVarMissing ) {
                            val.append( doResolve( defaultVal.trim(), depth, failIfVarMissing, varOrig ) );
                        } else if ( varVal.equals( entry ) ) {
                            val.append( varVal );
                        } else {
                            // extract the
                            val.append( "${" ).append( identifier ).append( ":-" ).append( varVal ).append( "}" );
                        }
                    }

                } else if ( varVal == null ) {
                    varVal = defaultVal;

                    if ( failIfVarMissing ) {
                        String resolvedDefault = doResolve( varVal.trim(), depth, failIfVarMissing, varOrig );
                        val.append( resolvedDefault );        // *** RECURSE ***

                        _log.info( getComponentId() + " resolve (B) VAR " + varOrig + " setting to default value [" + resolvedDefault + "]" );
                    } else if ( varVal.equals( entry ) ) {
                        val.append( varVal );
                    } else {
                        val.append( "${" ).append( identifier ).append( ":-" ).append( varVal ).append( "}" );
                    }

                } else {

                    val.append( doResolve( varVal.trim(), depth, failIfVarMissing, varOrig ) );        // *** RECURSE ***

                }
            }

            lastIdx = varEndIdx + 1;

            if ( lastIdx >= entry.length() ) break; // macro was at end of value

            varStartIdx = entry.indexOf( "${", lastIdx );

        } while( varStartIdx > 0 );

        if ( lastIdx < entry.length() ) {
            val.append( entry.substring( lastIdx ) );
        }

        return val.toString();
    }

    private String evalExpr( String val ) {
        if ( val != null ) {
            val = val.trim();

            String lowerVal = val.toLowerCase();
            int    exprIdx  = lowerVal.indexOf( EXPR_STR );

            if ( exprIdx != -1 ) {
                String newVal = val.substring( 0, exprIdx );

                int braceDepth = 1;

                int startExprInsideIdx = exprIdx + EXPR_STR.length();
                int idx                = startExprInsideIdx;

                for ( ; idx < val.length(); idx++ ) {
                    char c = val.charAt( idx );

                    if ( c == '(' ) {
                        ++braceDepth;
                    } else if ( c == ')' ) {
                        --braceDepth;
                        if ( braceDepth == 0 ) {
                            break;
                        }
                    }
                }

                if ( braceDepth != 0 ) {
                    throw new SMTRuntimeException( "evalExpr [" + val + "] has mismatched bracing ... idx=" + idx + ", braceDepth=" + braceDepth );
                }

                String expr = val.substring( startExprInsideIdx, idx );

                int intVal = 0;
                try {
                    intVal = SimpleIntCalc.evaluate( expr );
                } catch( Exception e ) {
                    throw new RuntimeException( e );
                }

                newVal = newVal + intVal;

                if ( ++idx < val.length() ) {
                    newVal += evalExpr( val.substring( idx ) );
                }

                return newVal;
            }
        }

        return val;
    }

    private String getFinalTag( String property ) {
        if ( property == null ) return null;

        int idx = property.lastIndexOf( '.' );
        if ( idx >= 0 && idx == (property.length() - 1) ) {
            return property;
        }

        return (idx == -1) ? property : property.substring( idx + 1 );
    }

    private void loadProps( String propertyFile, Map<String, String> localProps, Map<String, String> prop2File ) throws Exception {
        File propFile = FileUtils.getFile( propertyFile );

        if ( !propFile.exists() || !propFile.canRead() ) {
            throw new SMTRuntimeException( "Unable to read file " + propertyFile );
        }

        BufferedReader reader;

        int lineNo = 0;

        ReusableString line = new ReusableString();

        synchronized( AppProps.class ) {

            _log.info( "AppProperties loading from " + propertyFile );

            reader = new BufferedReader( new FileReader( propFile ) );

            for ( String strLine = reader.readLine(); strLine != null; strLine = reader.readLine() ) {

                String trimLine = strLine.trim();

                if ( trimLine.endsWith( "\\" ) ) {

                    line.append( trimLine.substring( 0, trimLine.length() - 1 ) );

                } else {
                    line.append( strLine );

                    try {
                        procLine( line.toString(), propertyFile, ++lineNo, localProps, prop2File );
                    } catch( Exception e ) {
                        if ( _firstException == null ) {
                            _firstException = e;
                        }

                        //noinspection StringConcatenationInLoop
                        _errs += "\n\nERR #" + (++_err) + " [" + propertyFile + ":" + lineNo + "] - " + e.getMessage();
                    }

                    line.reset();
                }
            }

            FileUtils.close( reader );
        }
    }

    private String removeQuotes( final String val ) {
        if ( val.charAt( 0 ) == '"' ) {
            final int lastCharIdx = val.length() - 1;
            if ( val.charAt( lastCharIdx ) == '"' ) {
                return val.substring( 1, lastCharIdx );
            }
        }
        return val;
    }

    private boolean startsWithIgnoreCase( String fullKey, String base ) {
        return fullKey.toLowerCase().startsWith( base.toLowerCase() );
    }

    private String validateAndGet( String property, boolean isMand ) {
        if ( !_init ) {
            throw new SMTRuntimeException( "Must initialise AppProperties before use" );
        }

        boolean validateTag = property.indexOf( '.' ) > 0;

        String finalTag = getFinalTag( property );

        if ( validateTag ) {
            // if its a number dont bother validating against propSet
            if ( StringUtils.isNumber( finalTag ) ) validateTag = false;
        }

        if ( validateTag && !property.startsWith( "log" ) ) {
            if ( !_propSet.isValidTag( finalTag ) ) {
                throw new SMTRuntimeException( "AppProperties property " + finalTag + " (from " + property + ") is not in property set " +
                                               _propSet.getSetName() );
            }
        }

        String val = _props.get( property.toLowerCase() );

        if ( val != null ) val = val.trim();

        if ( val == null || val.length() == 0 ) {
            if ( isMand ) throw new SMTRuntimeException( "AppProperties missing mand property " + property );

            return null;
        }

        return val;
    }
}
