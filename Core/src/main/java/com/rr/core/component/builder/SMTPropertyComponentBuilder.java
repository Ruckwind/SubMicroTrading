/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.annotations.OptionalReference;
import com.rr.core.component.*;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.ConfigParam;
import com.rr.core.recovery.json.IgnoreDependency;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTException;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.rr.core.utils.ReflectUtils.get2DArrayVals;
import static com.rr.core.utils.ReflectUtils.getArrayVals;

/**
 * Component Builder : Instantiate components from property file
 * <p>
 * Must be called after initiating logger
 * <p>
 * components can be either directly instantiated or instantiated by a custom component loader
 * <p>
 * components must implement SMTComponent interface
 * <p>
 * component identifier is the second part of the component chain, eg componentId1, componentId2 are identifiers that can be used in type "ref" for references
 * <p>
 * Component loaders should have property member vars setup for all required config so loaders are abstracted from source of wiring
 * The reflective loader will check type of member and if not simple type will assume its a reference so will assume value is a componentId
 *
 * @NOTE the loader will autowire missing property references of SMTComponent/SMTLoader where the member var name matches the componentId
 * @WARNING can only autowire references to components already defined (ie already added to the components map)
 * <p>
 * To avoid typing errors, each propertyTag must be valid member of AppProps
 * (propertyTag is the last element in property ie from a.b.c only c is a propertyTag)
 * <p>
 * component.componentId1.loader=com.rr.core.loaders.FixSessionLoader
 * #avoid duplicating config by loading up some default properties using recursion
 * component.componentId1.properties.defaultProperties=session.default.up.properties
 * component.componentId1.properties.file=./data/cme/secdef.t1.dat
 * component.componentId1.properties.codecId=CME
 * component.componentId1.properties.port=1234
 * component.componentId1.properties.host=127.0.0.1
 * component.componentId1.properties.instMgr=instMgrComponentID
 * <p>
 * component.componentId2.className=com.rr.core.SomeClass
 * # example of arguments for constructor, default type is String
 * # type can be   ref|long|int|string|zstring|double|{className}|{arrayClassName}
 * # for type "ref" the value is a componentID
 * # arguments index must start at 1 and match constructor arg .. arg0 is reserved for the componentId
 * component.componentId2.arg.1.value=./data/cme/secdef.t1.dat
 * component.componentId2.arg.2.type=int
 * component.componentId2.arg.2.value=99
 * # example of property set via reflection after instantiation
 * component.componentId2.properties.someProperty=someValue
 * <p>
 * format for COMPONENT property identifiers is
 * component.{componentId}.[className|loader]
 * component.{componentId}.properties.{propertyName}
 * where componentId and propertyName must be a single string with no periods
 * ie  c1.properties.p1.p2=XX is invalid
 * <p>
 * Note for a constructor arg using an array of components you should set the type to the java name for the array class
 * eg component.cid4.arg.1.type=[Lcom.rr.core.component.TestPropertyComponentBuilder$IntermediateInterface;
 * The [L  must be before the class name and must be terminated with semi-colon
 * <p>
 * When using properties within config remember SMT is using reflective setting of the member var and NOT invoking the setter
 * IF specific behaviour required in setter then pass in reference to constructor
 * @NOTE should use contructor args in pref to properties for best perf as var can then be immutable
 */

public class SMTPropertyComponentBuilder {

    private static final String DEFAULT_PROPERTIES = "defaultProperties";

    private static final int MAX_RECURSE_CONFIG = 5;

    private static final ErrorCode ERR_COMP = new ErrorCode( "PCB100", "Error building component" );

    private static final int MAX_DEPENDENY_DEPTH = 20;

    private final Logger   _log = ConsoleFactory.console( SMTPropertyComponentBuilder.class, Level.info );
    private final AppProps _props;

    private final Map<String, Map<?, ?>> _maps = new LinkedHashMap<>();
    private final SMTComponentManager    _componentMgr;
    private final Map<SMTComponent, Set<SMTComponent>> _dependants = new LinkedHashMap<>();
    /**
     * ordered set of components, with all a components dependencies appearing before it
     */
    private final Map<String, SMTComponent>   _components        = new LinkedHashMap<>( 128 );
    private final LinkedHashSet<SMTComponent> _orderedComponents = new LinkedHashSet<>( 128 );
    private final ReusableString              _logMsg            = new ReusableString();
    private boolean _initialised = false;
    private int _nextIdx = 0;

    public SMTPropertyComponentBuilder( final AppProps props, final SMTComponentManager componentManager ) {
        super();
        _props        = props;
        _componentMgr = componentManager;
    }

    @Override
    public String toString() {
        ReusableString s = new ReusableString( 8192 );

        for ( SMTComponent c : _orderedComponents ) {
            String lc = c.getComponentId();

            s.append( lc ).append( " : " ).append( c.toString() ).append( "\n" );
        }

        return s.toString();
    }

    @SuppressWarnings( { "unchecked" } )
    public <T extends SMTComponent> T getComponent( String id ) {

        if ( id == null || id.equalsIgnoreCase( "null" ) ) return null;

        SMTComponent c = _components.get( id.toLowerCase() );

        if ( c == null ) {
            throw new SMTRuntimeException( "Attempt to reference unregistered component, id=" + id + ", check define referenced components first" );
        }

        return ((T) c);
    }

    @SuppressWarnings( { "unchecked" } )
    public <T extends SMTComponent> List<T> getComponents( String[] ids ) {

        if ( ids == null ) return null;

        List<T> components = new ArrayList<>();

        for ( String id : ids ) {

            id = id.trim();

            T c = (T) _components.get( id.toLowerCase() );

            if ( c == null ) {
                throw new SMTRuntimeException( "Attempt to reference unregistered component, id=" + id + ", check define referenced components first" );
            }

            components.add( c );
        }

        return (components);
    }

    public Collection<SMTComponent> getComponents() {
        return _orderedComponents;
    }

    public synchronized void init() {
        if ( !_initialised ) {
            _log.info( "SMTPropertyComponentBuilder.init()" );

            loadMaps();
            instantiateComponents();
            findComponentDependencies();
            orderComponentsBasedOnDependencies();
            verify();

            for ( SMTComponent c : _orderedComponents ) {
                _componentMgr.add( c );
            }
        }

        _initialised = true;
    }

    private void addComponent( SMTComponent c ) {

        if ( c != null ) {

            String id = c.getComponentId().toLowerCase();

            if ( _components.get( id ) != null ) {
                throw new SMTRuntimeException( "Duplicate component in config, id=" + id );
            }

            _components.put( id, c );
        }
    }

    private void addComponentAfterDependencies( LinkedHashSet<SMTComponent> initialiseOrder, LinkedHashSet<SMTComponent> stacked, SMTComponent c, int level ) {

        if ( !_components.values().contains( c ) ) {
            return; // not all SMTComponents are configured some are owned by enclosing components, these sub components must be ignored
        }

        if ( initialiseOrder.contains( c ) ) {
            return; // already done this component
        }

        Set<SMTComponent> dependencies = _dependants.get( c );

        StringBuilder dStr = new StringBuilder();

        if ( dependencies != null ) {
            for ( SMTComponent d : dependencies ) {
                if ( dStr.length() > 0 ) {
                    dStr.append( ", " );
                }

                dStr.append( d.getComponentId() );
            }
        }

        if ( dStr.length() == 0 ) dStr = new StringBuilder( "NOTHING" );

        _log.info( "Component " + c.getComponentId() + " depends on " + dStr );

        if ( level > MAX_DEPENDENY_DEPTH ) {
            throw new SMTRuntimeException( "Exceeded max recurive depth trying to determine order of components, depth=" + level + " on " + c.getComponentId() );
        }

        stacked.add( c ); // protect against back references by keeping track of all components in stack

        if ( dependencies != null ) {
            for ( SMTComponent d : dependencies ) {
                if ( d == null ) {
                    throw new SMTRuntimeException( "Null Component Dependency .. shouldnt be possible" );
                }

                if ( !initialiseOrder.contains( d ) && d != c && !stacked.contains( d ) ) {
                    addComponentAfterDependencies( initialiseOrder, stacked, d, level + 1 );
                }
            }
        }

        stacked.remove( c );

        initialiseOrder.add( c );
    }

    @SuppressWarnings( { "boxing", "unchecked", "null" } )
    private void addConstructorArg( boolean forceSMTRefs, Object[] argVals, Class<?>[] argClasses, int argIdx, String baseProps ) {
        // type can be   ref|long|int|string|zstring|double
        // for type "ref" the value is a componentID

        String typeLC = _props.getProperty( baseProps + "type", false, "string" ).toLowerCase();
        String type   = _props.getProperty( baseProps + "type", false, null );
        String val    = _props.getProperty( baseProps + "value", false, null );
        String ref    = _props.getProperty( baseProps + "ref", false, null );

        boolean isValNull = ("null".equalsIgnoreCase( val ));

        Object   pVal   = null;
        Class<?> pClass = null;

        if ( type != null && type.equalsIgnoreCase( "ref" ) ) {
            pVal   = getComponent( val.toLowerCase() );
            pClass = (forceSMTRefs) ? SMTComponent.class : pVal.getClass();
        } else if ( ref != null ) {
            if ( isArrayType( baseProps, type ) ) {
                pClass = findClass( baseProps, type );
                pVal   = populateArrayArg( ref, pClass );
            } else {
                pVal = _maps.get( ref.toLowerCase() );

                if ( pVal == null ) {
                    pVal = _maps.get( "map." + ref.toLowerCase() );
                }

                if ( pVal != null ) {
                    pClass = Map.class;

                } else { // NOT A MAP MUST BE A COMPONENT

                    pVal = getComponent( ref.toLowerCase() );

                    if ( pVal != null ) {
                        if ( type != null && type.length() > 0 ) {
                            pClass = findClass( baseProps, type );
                        } else if ( type == null || type.length() == 0 ) {
                            pClass = (forceSMTRefs) ? SMTComponent.class : pVal.getClass();
                        }
                    }
                }
            }
        } else if ( "string".equals( typeLC ) ) {
            pVal   = val;
            pClass = String.class;
        } else if ( "zstring".equals( typeLC ) ) {
            pVal   = new ViewString( val );
            pClass = ZString.class;
        } else if ( "long".equals( typeLC ) ) {
            pVal   = Long.parseLong( val );
            pClass = long.class;
        } else if ( "int".equals( typeLC ) ) {
            pVal   = Integer.parseInt( val );
            pClass = int.class;
        } else if ( "double".equals( typeLC ) ) {
            pVal   = StringUtils.parseDouble( val );
            pClass = double.class;
        } else {
            if ( type != null && type.length() > 0 ) {
                try {
                    pClass = findClass( baseProps, type );
                } catch( Exception e1 ) {
                    throw new SMTRuntimeException( "PropertyComponentBuilder : unable to load class " + type + ", baseProps=" + baseProps + ", argIdx=" + argIdx );
                }
                if ( Enum.class.isAssignableFrom( pClass ) ) {
                    if ( !isValNull ) {
                        @SuppressWarnings( { "rawtypes" } )
                        Class<Enum> enumClass = (Class<Enum>) pClass;

                        try {
                            pVal = Enum.valueOf( enumClass, val );
                        } catch( Exception e ) {
                            throw new SMTRuntimeException( "PropertyComponentBuilder : invalid ENUM , " + val + " is not valid entry for " + pClass.getCanonicalName() +
                                                           ", baseProps=" + baseProps + ", argIdx=" + argIdx );
                        }
                    }
                }
            }

            if ( !isValNull && pVal == null ) {

                // didnt find enum, assume its a component reference
                if ( pClass != null && pClass.isArray() ) {
                    pClass = findClass( baseProps, type );
                    pVal   = populateArrayArg( val, pClass );
                } else {
                    try {
                        String[] compIds = getArrayVals( val );

                        if ( compIds.length > 1 ) { // ARRAY

                            for ( int i = 0; i < compIds.length; i++ ) {
                                String aVal = compIds[ i ].trim();

                                pVal = getComponent( aVal );

                                if ( i == 0 ) {
                                    pClass = (forceSMTRefs) ? SMTComponent.class : pVal.getClass();

                                    try {
                                        pClass = Class.forName( "[L" + pClass.getName() + ";" );
                                    } catch( ClassNotFoundException e ) {
                                        throw new SMTRuntimeException( "PropertyComponentBuilder : property " + baseProps + ", argIdx=" + argIdx + " unable to derive array class " + e.getMessage(), e );
                                    }
                                }
                            }

                        } else {
                            pVal = getComponent( val );

                            if ( pClass != null ) {
                                if ( forceSMTRefs ) pClass = SMTComponent.class;
                            } else {
                                pClass = (forceSMTRefs) ? SMTComponent.class : pVal.getClass();
                            }
                        }

                        if ( !pClass.isAssignableFrom( pVal.getClass() ) ) {
                            throw new SMTRuntimeException( "PropertyComponentBuilder : property " + baseProps + ", argIdx=" + argIdx + " has type " + typeLC +
                                                           " but that doesnt match type of component " + val + " which is " + val.getClass().getSimpleName() );
                        }
                    } catch( SMTRuntimeException e ) {
                        throw new SMTRuntimeException( "PropertyComponentBuilder : property " + baseProps + ", argIdx=" + argIdx + " has type " + typeLC +
                                                       " but cant find component matching id " + val + ", check its defined before referenced" );
                    }
                }
            }
        }

        if ( isValNull ) {
            pVal = null;
        }

        argVals[ argIdx ]    = pVal;
        argClasses[ argIdx ] = pClass;
    }

    private void addToDependencies( SMTComponent c ) {

        getDependenciesForComponent( c );

        Set<Field> fields = ReflectUtils.getMembers( c );

        for ( Field f : fields ) {

            if ( f.isAnnotationPresent( IgnoreDependency.class ) ) {
                continue; // skip
            }

            Class<?> type = f.getType();

            boolean wasAccessable = f.isAccessible();
            try {
                f.setAccessible( true );
                Object val = f.get( c );

                if ( val != null && SMTComponent.class.isAssignableFrom( type ) ) {
                    SMTComponent dependantOn = (SMTComponent) val;

                    link( c, dependantOn );

                } else if ( val != null && type.isArray() && !type.getComponentType().isPrimitive() ) {
                    Object[] vals = (Object[]) f.get( c );

                    for ( Object o : vals ) {
                        if ( o instanceof SMTComponent ) {
                            link( c, (SMTComponent) o );
                        }
                    }
                }

            } catch( Exception e ) {
                // swallow
            } finally {
                f.setAccessible( wasAccessable );
            }
        }
    }

    private void autoWireMissingProperties( String baseProps, Object propHolder ) {

        baseProps = ensureLastCharIsPeriod( baseProps );

        Set<Field> fields = ReflectUtils.getMembers( propHolder );

        for ( Field f : fields ) {               //   iterate thru all the fields in the propObject looking for match
            String fieldName = f.getName();

            if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

            if ( !hasProperty( baseProps, 1, fieldName ) ) {
                setMissingRefUsingReflectionIfPossible( propHolder, fieldName, f );
            }
        }
    }

    private void autoWireMissingProps( Object c, String baseProps ) {

        baseProps = baseProps.toLowerCase();

        autoWireMissingProperties( baseProps, c );
    }

    private void directInstantiate( String id, String baseProps, String className ) {
        _log.info( "SMTPropertyComponentBuilder.directInstantiate " + nextIdx() + " : " + id );

        /*
            component.componentId2.className=com.rr.core.SomeClass
             # example of arguments for constructor, default type is String
             # type can be   ref|long|int|string|double
             # for type "ref" the value is a componentID
            component.componentId2.arg.1.value=./data/cme/secdef.t1.dat
            component.componentId2.arg.2.type=int
            component.componentId2.arg.2.value=99
             # example of property set via reflection after instantiation
            component.componentId1.properties.someProperty=someValue
         */

        String   argBase = baseProps + "arg.";
        String[] argIdxs = _props.getNodesWithCaseIntact( argBase );

        SMTComponent c;

        int numArgs = argIdxs.length + 1;

        Object[]   argVals    = new Object[ numArgs ];
        Class<?>[] argClasses = new Class<?>[ numArgs ];

        // first constructor argument is for the componentId
        argVals[ 0 ]    = id;
        argClasses[ 0 ] = String.class;

        for ( int i = 1; i < numArgs; i++ ) {
            int matchIdx = Integer.parseInt( argIdxs[ i - 1 ] );

            if ( matchIdx != i ) {
                throw new SMTRuntimeException( "Bad config in component " + id + ", next arg expected " + i + ", but got " + matchIdx );
            }

            addConstructorArg( false, argVals, argClasses, i, argBase + i + "." );
        }

        try {
            c = ReflectUtils.create( className, argClasses, argVals );

        } catch( SMTRuntimeException e ) { // failed to instantiate, retry forcing untyped references to use type SMTComponent
            for ( int i = 1; i < numArgs; i++ ) {
                addConstructorArg( true, argVals, argClasses, i, argBase + i + "." );
            }

            try {
                c = ReflectUtils.create( className, argClasses, argVals );
            } catch( SMTRuntimeException e1 ) {
                throw new SMTRuntimeException( e.getMessage() + "\nAlso failed with : " + e1.getMessage() + " for " + id, e );
            }
        }

        // first attempt to set all properties
        setComponentProperties( c, id, baseProps + "properties.", false );

        addComponent( c );
    }

    private String ensureLastCharIsPeriod( String baseProps ) {
        if ( baseProps.length() > 0 && baseProps.charAt( baseProps.length() - 1 ) != '.' ) {
            baseProps += '.';
        }
        return baseProps;
    }

    private Class<?> findClass( String baseProps, String className ) {
        Class<?> pClass;

        try {
            pClass = Class.forName( className );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "PropertyComponentBuilder : Unable to find class " + className + " specified in property " + baseProps + "className" );
        }

        return pClass;
    }

    private void findComponentDependencies() {
        for ( SMTComponent c : _components.values() ) {
            addToDependencies( c );
        }

        /**
         * dont recurse thru full tree as would need deal with cyclic links etc
         */
//        for( SMTComponent c : _components.values() ) {
//            Set<SMTComponent> componentDependencies = getDependenciesForComponent( c );
//
//            if ( componentDependencies != null ) {
//                for ( SMTComponent s : componentDependencies ) {
//                    Set<SMTComponent> subDependencies = getDependenciesForComponent( c );
//
//                    for( SMTComponent t : subDependencies ) {
//                        if ( t != c ) {
//                            componentDependencies.add( t );
//                        }
//                    }
//                }
//            }
//        }
    }

    private Set<SMTComponent> getDependenciesForComponent( SMTComponent c ) {
        Set<SMTComponent> componentDependencies = _dependants.computeIfAbsent( c, k -> new LinkedHashSet<>() );

        return componentDependencies;
    }

    private boolean hasProperty( String baseProps, int depth, String fieldName ) {

        if ( depth > MAX_RECURSE_CONFIG ) {
            throw new SMTRuntimeException( "Exceeded recursion depth in ProperyComponentBuilder.autoWireMissingProps baseProps=" + baseProps +
                                           " depth=" + depth + ", field=" + fieldName );
        }

        String defaultLoaderProps = _props.getProperty( baseProps + DEFAULT_PROPERTIES, false, null );

        if ( defaultLoaderProps != null ) {
            String[] sets = getArrayVals( defaultLoaderProps );
            for ( String set : sets ) {
                if ( hasProperty( set.trim() + ".", depth + 1, fieldName ) ) {
                    return true;
                }
            }
        }

        String[] props = _props.getNodesWithCaseIntact( baseProps );

        for ( String propertyEntry : props ) {      // iterate thru all the specified properties

            if ( DEFAULT_PROPERTIES.equalsIgnoreCase( propertyEntry ) ) {
                continue;
            }

            if ( fieldName.equalsIgnoreCase( propertyEntry ) ) { // ALREADY SET
                return true;
            }
        }

        return false;
    }

    private void initPt1( String id, String baseProps ) throws SMTException {
        String className = _props.getProperty( baseProps + "className", false, null );

        if ( className != null ) {
            directInstantiate( id, baseProps, className );
        } else {
            String loader = _props.getProperty( baseProps + "loader" );

            instantiateViaLoader( id, baseProps, loader );
        }
    }

    private void instantiateComponents() {
        String   base    = "component.";
        String[] compIds = _props.getNodesWithCaseIntact( base );

        String    firstId  = "";
        Exception first    = null;
        int       firstIdx = -1;

        addComponent( _componentMgr );

        for ( int i = 0; i < compIds.length; ++i ) {
            String id = compIds[ i ];

            try {
                initPt1( id, base + id + "." );
            } catch( Exception e ) {
                _log.error( ERR_COMP, "Component " + id + " FAIL " + e.getMessage(), e );

                if ( first == null ) {
                    firstIdx = i;
                    firstId  = id;
                    first    = e;
                }
            }
        }

        if ( first != null ) {
            throw new SMTRuntimeException( "FIRST component failure [" + firstIdx + "] " + firstId + " : " + first.getMessage(), first );
        }

        // try and pickup any remaining forward references
        _log.info( "SMTPropertyComponentBuilder.instantiateComponents() second attempt to set properties after all components have bben constructed" );
        for ( int i = 0; i < compIds.length; ++i ) {
            String id = compIds[ i ];

            try {
                setPropsOnDirectInstantiated( id, base + id + "." );
            } catch( Exception e ) {
                _log.error( ERR_COMP, "Component " + id + " FAIL " + e.getMessage() );

                if ( first == null ) {
                    firstId = id;
                    first   = e;
                }
            }
        }

        if ( first != null ) {
            throw new SMTRuntimeException( "FIRST component failure " + firstId + " : " + first.getMessage(), first );
        }

        _log.info( "SMTPropertyComponentBuilder.initialised " + compIds.length + " components" );
    }

    private void instantiateViaLoader( String id, String baseProps, String loaderClassName ) throws SMTException {

        Object loader = ReflectUtils.create( loaderClassName );

        setComponentProperties( loader, id, baseProps + "properties.", true );

        if ( loader instanceof SMTMultiComponentLoader ) {
            SMTComponent[] components = ((SMTMultiComponentLoader) loader).create( id );

            _log.info( "SMTPropertyComponentBuilder.instantiateViaLoader " + nextIdxRange( components.length ) + " : " + id );

            for ( SMTComponent component : components ) {
                addComponent( component );
            }
        } else if ( loader instanceof SMTSingleComponentLoader ) {
            SMTComponent component = ((SMTSingleComponentLoader) loader).create( id );

            _log.info( "SMTPropertyComponentBuilder.instantiateViaLoader " + nextIdx() + " : " + id );

            addComponent( component );
        }
    }

    private boolean isArrayType( String baseProps, String className ) {

        if ( className == null ) return false;

        Class<?> pClass;

        try {
            pClass = Class.forName( className );
        } catch( Exception e ) {
            return false;
        }

        return pClass.isArray();
    }

    private void link( SMTComponent c, SMTComponent dependantOn ) {
        if ( c == null ) {
            throw new SMTRuntimeException( "Null component " );
        }

        Set<SMTComponent> componentDependencies = getDependenciesForComponent( c );

        componentDependencies.add( dependantOn );
    }

    /*
    map.borrowLimitPerInst.keyType=com.rr.core.model.ExchangeCode
    map.borrowLimitPerInst.valType=java.lang.Double
    map.borrowLimitPerInst.entry.1=XLON|20000000.0
    map.borrowLimitPerInst.entry.2=XPAR|200000.0
     */
    private void loadMap( String id, String baseProps ) {
        String keyType = _props.getProperty( baseProps + "keyType", false, "java.lang.String" );
        String valType = _props.getProperty( baseProps + "valType", false, "java.lang.String" );

        Class<?> keyClass = ReflectUtils.getClass( keyType );
        Class<?> valClass = ReflectUtils.getClass( valType );

        Map<Object, Object> map = new LinkedHashMap<>();

        // map.{mapId}.entry.{idx}={key}|{value}

        String   argBase = baseProps + "entry.";
        String[] argIdxs = _props.getNodesWithCaseIntact( argBase );

        int numArgs = argIdxs.length + 1;

        for ( int i = 1; i < numArgs; i++ ) {
            int matchIdx = Integer.parseInt( argIdxs[ i - 1 ] );

            if ( matchIdx != i ) {
                throw new SMTRuntimeException( "Bad config in map " + id + ", next arg expected " + i + ", but got " + matchIdx );
            }

            String   entry = _props.getProperty( argBase + matchIdx );
            String[] parts = entry.split( "\\|" );
            if ( parts.length != 2 ) {
                throw new SMTRuntimeException( "Bad config in map " + id + ", idx=" + i + ", expected {key}|{val}  not [" + entry + "]" );
            }

            String keyStr = parts[ 0 ].trim();
            String valStr = parts[ 1 ].trim();

            Object key = ReflectUtils.convertArg( keyClass, keyStr );
            Object val = ReflectUtils.convertArg( valClass, valStr );

            map.put( key, val );
        }

        if ( _maps.put( "map." + id.toLowerCase(), map ) != null ) {
            throw new SMTRuntimeException( "Duplicate map in config " + id );
        }
    }

    private void loadMaps() {
        String   base    = "map.";
        String[] compIds = _props.getNodesWithCaseIntact( base );

        String    firstId = "";
        Exception first   = null;

        for ( int i = 0; i < compIds.length; ++i ) {
            String id = compIds[ i ];

            try {
                loadMap( id, base + id + "." );
            } catch( Exception e ) {
                _log.error( ERR_COMP, "Map Load " + id + " FAIL " + e.getMessage() );

                if ( first == null ) {
                    firstId = id;
                    first   = e;
                }
            }
        }

        if ( first != null ) {
            throw new SMTRuntimeException( "FIRST map load failure " + firstId + " : " + first.getMessage(), first );
        }

        _log.info( "SMTPropertyComponentBuilder.initialised " + compIds.length + " components" );
    }

    private String nextIdx() {
        return " [ " + (++_nextIdx) + " ]";
    }

    private String nextIdxRange( int n ) {
        int nextIdx = ++_nextIdx;

        return " [ " + nextIdx + " -> " + (nextIdx + n - 1) + " ]";
    }

    private void orderComponentsBasedOnDependencies() {

        int level = 1;

        LinkedHashSet<SMTComponent> stacked = new LinkedHashSet<>();

        // force all components with no dependencies to appear first
        for ( SMTComponent c : _dependants.keySet() ) {
            if ( c == null ) {
                throw new SMTRuntimeException( "Null component : should never happen" );
            }

            Set<SMTComponent> dependencies = _dependants.get( c );
            if ( dependencies == null || dependencies.size() == 0 ) {
                _orderedComponents.add( c );
            }
        }

        for ( SMTComponent c : _dependants.keySet() ) {
            addComponentAfterDependencies( _orderedComponents, stacked, c, level );
        }

        int i = 1;

        for ( SMTComponent c : _orderedComponents ) {
            _log.info( "Component Order # " + (i++) + " : " + c.getComponentId() );

            if ( c.getComponentId() == null ) {
                throw new SMTRuntimeException( "Null componentId in class " + c.getClass().getSimpleName() );
            }
        }
    }

    private Object populateArrayArg( String ref, Class<?> pClass ) {
        String  ids[]      = getArrayVals( ref );
        List<?> components = getComponents( ids );

        Object arr = Array.newInstance( pClass.getComponentType(), components.size() );
        for ( int i = 0; i < components.size(); i++ ) {
            Object v = components.get( i );
            Array.set( arr, i, v );
        }
        return arr;
    }

    private void setBasicArrayProperty( final Object obj, final Field f, final String value, final Class<?> type ) throws IllegalAccessException {
        if ( type.getName().startsWith( "[[" ) ) { // 2D arr
            String vals[][] = get2DArrayVals( value );

            int   rows = vals.length;
            int   cols = vals[ 0 ].length;
            int[] size = { rows, cols };

            Object arr = Array.newInstance( type.getComponentType().getComponentType(), size );

            for ( int row = 0; row < rows; row++ ) {
                String[] rowVals = vals[ row ];

                Object rowArr = Array.newInstance( type.getComponentType().getComponentType(), rowVals.length );

                for ( int col = 0; col < rowVals.length; col++ ) {

                    Object val = ReflectUtils.convertArg( type.getComponentType().getComponentType(), rowVals[ col ] );

                    Array.set( rowArr, col, val );
                }

                Array.set( arr, row, rowArr );
            }

            f.set( obj, arr );

        } else { // 1D arr
            String vals[] = getArrayVals( value );

            Object arr = Array.newInstance( type.getComponentType(), vals.length );
            for ( int i = 0; i < vals.length; i++ ) {
                Object val = ReflectUtils.convertArg( type.getComponentType(), vals[ i ] );

                Array.set( arr, i, val );
            }

            f.set( obj, arr );
        }
    }

    private void setBasicProperty( final Object obj, final Field f, final String value, final Class<?> type ) throws IllegalAccessException {
        SMTComponent c = _components.get( value.toLowerCase() );

        if ( c != null && type.isAssignableFrom( c.getClass() ) ) { // found a component matching value, which can be assigned to the fields type
            f.set( obj, c );
        } else {
            ReflectUtils.setMemberFromString( obj, f, value );
        }
    }

    private boolean setComponentArrayProperty( final Object obj, final Field f, final String value, final boolean failOnComponentMissing, final Class<?> type ) throws IllegalAccessException {
        try {
            String ids[] = getArrayVals( value );

            List<?> components = getComponents( ids );

            Object arr = Array.newInstance( type.getComponentType(), components.size() );
            for ( int i = 0; i < components.size(); i++ ) {
                Object val = components.get( i );
                Array.set( arr, i, val );
            }

            f.set( obj, arr );

        } catch( SMTRuntimeException e ) {
            if ( !failOnComponentMissing ) return true;

            if ( f.isAnnotationPresent( OptionalReference.class ) ) {
                _log.info( "SMTPropertyComponentBuilder.setProperty() optional reference of " + f.getName() + ", as type " + type.getSimpleName() +
                           " is not set" );
            } else {
                throw e;
            }
        }
        return false;
    }

    private void setComponentProperties( Object c, String id, String baseProps, boolean failOnComponentMissing ) {
        try {
            setComponentProperties( c, id, baseProps, 1, false, failOnComponentMissing );
        } catch( SMTRuntimeException e ) {
            throw new SMTRuntimeException( "Error setting component " + id + " : " + e.getMessage(), e );
        }

        autoWireMissingProps( c, baseProps );
    }

    private void setComponentProperties( Object c, String id, String baseProps, int depth, boolean propsMand, boolean failOnComponentMissing ) {

        baseProps = baseProps.toLowerCase();

        if ( depth > MAX_RECURSE_CONFIG ) {
            throw new SMTRuntimeException( "Exceeded recursion depth in ProperyComponentBuilder.setComponentProperties id=" + id + " depth=" + depth );
        }

        String defaultLoaderProps = _props.getProperty( baseProps + DEFAULT_PROPERTIES, false, null );

        if ( defaultLoaderProps != null ) {
            String[] sets = getArrayVals( defaultLoaderProps );
            for ( String set : sets ) {
                setComponentProperties( c, id, set.trim() + ".", depth + 1, true, failOnComponentMissing );
            }
        }

        setObjectProperties( baseProps, c, propsMand, failOnComponentMissing );
    }

    private boolean setComponentProperty( final Object obj, final Field f, final String value, final boolean failOnComponentMissing, final Class<?> type ) throws IllegalAccessException {
        SMTComponent c = null;

        try {
            c = getComponent( value );
        } catch( SMTRuntimeException e ) {
            if ( !failOnComponentMissing ) return true;

            if ( f.isAnnotationPresent( OptionalReference.class ) ) {
                _log.info( "SMTPropertyComponentBuilder.setProperty() optional reference of " + f.getName() + ", as type " + type.getSimpleName() +
                           " is not set" );
            } else {
                throw e;
            }
        }

        f.set( obj, c );
        return false;
    }

    private void setLinkedSetProperty( final Object obj, final Field f, final String value ) throws IllegalAccessException {
        LinkedHashSet<String> c = new LinkedHashSet<>();

        String vals[] = getArrayVals( value );

        c.addAll( Arrays.asList( vals ) );

        if ( c != null ) {
            f.set( obj, c );
        } else {
            throw new SMTRuntimeException( "Property " + f.getName() + " is a map, but " + value + " is not defined as map in config" );
        }
    }

    private void setListProperty( final Object obj, final Field f, final String value ) throws IllegalAccessException {
        List<String> c = new ArrayList<>();

        String vals[] = getArrayVals( value );

        c.addAll( Arrays.asList( vals ) );

        if ( c != null ) {
            f.set( obj, c );
        } else {
            throw new SMTRuntimeException( "Property " + f.getName() + " is a map, but " + value + " is not defined as map in config" );
        }
    }

    private void setMapProperty( final Object obj, final Field f, final String value ) throws IllegalAccessException {
        Map<?, ?> c;

        c = _maps.get( value.toLowerCase() );

        if ( c == null ) {
            c = _maps.get( "map." + value.toLowerCase() );
        }

        if ( c != null ) {
            f.set( obj, c );
        } else {
            throw new SMTRuntimeException( "Property " + f.getName() + " is a map, but " + value + " is not defined as map in config" );
        }
    }

    private void setMissingRefUsingReflectionIfPossible( Object obj, String fieldName, Field f ) {
        String compKey = fieldName.toLowerCase();

        Class<?> type = f.getType();

        SMTComponent c = _components.get( compKey );

        if ( c != null && SMTComponent.class.isAssignableFrom( type ) ) {
            _log.info( "AUTOWIRE " + obj.getClass().getName() + " : " + fieldName + ", compId=" + compKey );

            setProperty( obj, f, compKey, true );
        }
    }

    /**
     * all entries under baseProps must be valid members of the propertyHolder
     *
     * @param baseProps
     * @param propHolder instance to set properties
     * @param propsMand
     */
    private void setObjectProperties( String baseProps, Object propHolder, boolean propsMand, boolean failOnComponentMissing ) {

        baseProps = ensureLastCharIsPeriod( baseProps );

        Set<Field> fields = ReflectUtils.getMembers( propHolder );
        String[]   props  = _props.getNodesWithCaseIntact( baseProps );

        if ( propsMand && props.length == 0 ) {
            throw new SMTRuntimeException( "Unable to reflect set properties using [" + baseProps + "] for " + propHolder.getClass().getSimpleName() +
                                           " as no matching properties found" );
        }

        for ( String propertyEntry : props ) {      // iterate thru all the specified properties

            if ( DEFAULT_PROPERTIES.equalsIgnoreCase( propertyEntry ) ) {
                continue; // the recursive link already processed
            }

            boolean set = false;

            String value = _props.getProperty( baseProps + propertyEntry, false, null );

            if ( value != null && value.length() > 0 ) {
                for ( Field f : fields ) {               //   iterate thru all the fields in the propObject looking for match
                    String fieldName = f.getName();

                    if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

                    if ( fieldName.equalsIgnoreCase( propertyEntry ) ) {

                        setProperty( propHolder, f, value, failOnComponentMissing );

                        set = true;
                        break; // DONE - NEXT PROPERTY ENTRY
                    }
                }

                if ( !set ) {
                    String setterMethod = "set" + Character.toUpperCase( propertyEntry.charAt( 0 ) ) + propertyEntry.substring( 1 );

                    Method setter = ReflectUtils.findMethod( propHolder.getClass(), setterMethod );

                    if ( setter != null ) {

                        final Class<?>[] types = setter.getParameterTypes();

                        if ( types.length == 1 ) {

                            Class<?> argType = types[ 0 ];

                            if ( ReflectUtils.invokeSetterWithString( propHolder, setter, value, argType ) ) {

                                set = true;
                            }
                        }
                    }

                    if ( !set && propertyEntry.length() > 2 && propertyEntry.startsWith( "is" ) && Character.isUpperCase( propertyEntry.charAt( 2 ) ) ) {
                        setterMethod = "set" + propertyEntry.charAt( 2 ) + propertyEntry.substring( 3 );

                        setter = ReflectUtils.findMethod( propHolder.getClass(), setterMethod );

                        if ( setter != null ) {

                            final Class<?>[] types = setter.getParameterTypes();

                            if ( types.length == 1 ) {

                                Class<?> argType = types[ 0 ];

                                if ( ReflectUtils.invokeSetterWithString( propHolder, setter, value, argType ) ) {

                                    set = true;
                                }
                            }
                        }
                    }
                }
            } else {
                set = true;
            }

            if ( !set ) {
                throw new SMTRuntimeException( "Unable to reflect set property [" + propertyEntry + "] as thats not valid member of " + propHolder.getClass().getSimpleName() +
                                               ", baseProps=" + baseProps );
            }
        }
    }

    private void setProperty( Object obj, Field f, String value, boolean failOnComponentMissing ) {
        Class<?> type = f.getType();

        boolean wasAccessable = f.isAccessible();
        try {
            f.setAccessible( true );

            if ( LinkedHashSet.class.isAssignableFrom( type ) ) {

                setLinkedSetProperty( obj, f, value );

            } else if ( Set.class.isAssignableFrom( type ) ) {

                setSetProperty( obj, f, value );

            } else if ( Map.class.isAssignableFrom( type ) ) {

                setMapProperty( obj, f, value );

            } else if ( List.class.isAssignableFrom( type ) ) {

                setListProperty( obj, f, value );

            } else if ( SMTComponent.class.isAssignableFrom( type ) || (type.isArray() && SMTComponent.class.isAssignableFrom( type.getComponentType() )) ) {

                if ( type.isArray() ) {

                    if ( setComponentArrayProperty( obj, f, value, failOnComponentMissing, type ) ) return; // IGNORE FOR NOW

                } else {
                    if ( setComponentProperty( obj, f, value, failOnComponentMissing, type ) ) return; // IGNORE FOR NOW
                }

            } else {
                if ( type.isArray() ) {
                    setBasicArrayProperty( obj, f, value, type );
                } else {
                    setBasicProperty( obj, f, value, type );
                }
            }
        } catch( IllegalArgumentException e ) {
            throw new SMTRuntimeException( "SMTPropertyComponentBuilder.setProperty() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " not supported", e );
        } catch( IllegalAccessException e ) {
            throw new SMTRuntimeException( "SMTPropertyComponentBuilder.setProperty() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " access not allowed", e );
        } finally {
            f.setAccessible( wasAccessable );
        }
    }

    private void setPropsOnDirectInstantiated( String id, String baseProps ) throws SMTException {
        String className = _props.getProperty( baseProps + "className", false, null );

        if ( className != null ) {
            String compId = id.toLowerCase();

            SMTComponent c = _components.get( compId );

            if ( c == null ) throw new SMTRuntimeException( "Unable to set props on " + id + " as unable to find constructed object" );

            setPropsOnDirectInstantiated( c, id, baseProps );
        }
    }

    private void setPropsOnDirectInstantiated( SMTComponent c, String id, String baseProps ) {
        _log.info( "SMTPropertyComponentBuilder.setPropsOnDirectInstantiated " + nextIdx() + " : " + id );

        /*
            component.componentId2.className=com.rr.core.SomeClass
             # example of arguments for constructor, default type is String
             # type can be   ref|long|int|string|double
             # for type "ref" the value is a componentID
            component.componentId2.arg.1.value=./data/cme/secdef.t1.dat
            component.componentId2.arg.2.type=int
            component.componentId2.arg.2.value=99
             # example of property set via reflection after instantiation
            component.componentId1.properties.someProperty=someValue
         */

        setComponentProperties( c, id, baseProps + "properties.", true );

        if ( !c.getComponentId().equalsIgnoreCase( id ) ) {
            throw new SMTRuntimeException( "componentId of component doesnt match config, configId=" + id + ", instanceId=" + c.getComponentId() );
        }

        if ( c instanceof SMTComponentWithPostConstructHook ) {
            ((SMTComponentWithPostConstructHook) c).postConstruction();
        }
    }

    private void setSetProperty( final Object obj, final Field f, final String value ) throws IllegalAccessException {
        Set<String> c = new HashSet<>();

        String vals[] = getArrayVals( value );

        c.addAll( Arrays.asList( vals ) );

        if ( c != null ) {
            f.set( obj, c );
        } else {
            throw new SMTRuntimeException( "Property " + f.getName() + " is a map, but " + value + " is not defined as map in config" );
        }
    }

    private void verify() {

        // check for case mismatch on componentIds
        Map<String, SMTComponent> ids = new HashMap<>();
        for ( SMTComponent c : _orderedComponents ) {
            String lc = c.getComponentId().toLowerCase();

            SMTComponent existing = ids.get( lc );

            if ( existing != null ) {
                throw new SMTRuntimeException( "Component id " + lc + " has case mismatched entries, eg " +
                                               c.getComponentId() + " vs " + existing.getComponentId() );
            }

            ids.put( lc, c );
        }

        for ( SMTComponent c : _components.values() ) {
            if ( !_orderedComponents.contains( c ) ) {
                throw new SMTRuntimeException( "Component id " + c.getComponentId() + " is missing in ordered list" );
            }
        }

        _logMsg.reset();
        for ( SMTComponent c : _components.values() ) {
            final Set<Field> props = ReflectUtils.getMembers( c.getClass() );

            for ( Field f : props ) {
                boolean hasConfigAnnotation = (f.getAnnotation( ConfigParam.class ) != null);

                if ( hasConfigAnnotation ) {
                    Object val = ReflectUtils.get( f, c );

                    if ( val == null ) {
                        _logMsg.append( c.getComponentId() ).append( " has missing property " ).append( f.getName() ).append( ", which has ConfigParam annotation so must be set" );
                    }
                }
            }
        }

        if ( _logMsg.length() > 0 ) {
            throw new SMTRuntimeException( _logMsg.toString() );
        }
    }
}
