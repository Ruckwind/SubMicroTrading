/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.annotations.DateYYYYMMDD;
import com.rr.core.annotations.TimestampMS;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.Identifiable;
import com.rr.core.model.ShallowCopy;

import java.lang.annotation.Annotation;
import java.lang.invoke.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.ref.Reference;
import java.lang.reflect.*;
import java.util.*;

import static sun.misc.ThreadGroupUtils.getRootThreadGroup;

/**
 * Note the Field is an instance returned by getMemberField ... when its modified u dont modify the actual class so no need to reset it
 */
public class ReflectUtils {

    public static final  Object[]   NULL_ARGS       = {};
    private static final Logger _log = ConsoleFactory.console( ReflectUtils.class, Level.info );
    private static final Class<?>[] NULL_CLASS_ARGS = {};

    private static Constructor<MethodHandles.Lookup> _lookupConstructor;
    private static boolean                           _ignoreMissingEnum = false;

    static {
        try {
            _lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor( Class.class, Integer.TYPE );
            _lookupConstructor.setAccessible( true );
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static <T> Class<T> getClass( String className ) {
        try {

            @SuppressWarnings( "unchecked" )
            Class<T> theClass = (Class<T>) Class.forName( className );

            return theClass;
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to get class for " + className, e );
        }
    }

    public static <T> Class<T> getClassCheckForPrimitive( String className ) {
        try {

            if ( "double".equals( className ) ) return (Class<T>) double.class;
            if ( "int".equals( className ) ) return (Class<T>) int.class;
            if ( "long".equals( className ) ) return (Class<T>) long.class;
            if ( "short".equals( className ) ) return (Class<T>) short.class;
            if ( "byte".equals( className ) ) return (Class<T>) byte.class;
            if ( "char".equals( className ) ) return (Class<T>) char.class;

            @SuppressWarnings( "unchecked" )
            Class<T> theClass = (Class<T>) Class.forName( className );

            return theClass;
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to get class for " + className, e );
        }
    }

    public static <T> T create( String className ) {
        T instance;
        try {
            @SuppressWarnings( "unchecked" )
            Class<T> theClass = (Class<T>) Class.forName( className );

            instance = theClass.newInstance();
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to instantiate class " + className, e );
        }

        return instance;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T create( String className, Class<?>[] pClass, Object[] pArgs ) {

        if ( className == null ) return null;

        className = className.trim();

        if ( className.length() == 0 ) return null;

        T instance = null;
        try {
            @SuppressWarnings( "unchecked" )
            Class<T> tClass = (Class<T>) Class.forName( className );

            if ( pClass.length == 0 ) {
                return create( tClass );
            }

            try {
                Constructor<? extends T> c = tClass.getConstructor( pClass );
                c.setAccessible( true );
                instance = c.newInstance( pArgs );
            } catch( Exception e ) {
                // maybe types are wrong

                final Constructor<?>[] ctrs = tClass.getConstructors();

                for ( Constructor<?> c : ctrs ) {
                    try {
                        final Class<?>[] argClasses = c.getParameterTypes();

                        if ( argClasses.length == pArgs.length ) {

                            Object[] newArgs = new Object[ argClasses.length ];

                            for ( int idx = 0; idx < argClasses.length; idx++ ) {
                                newArgs[ idx ] = convertArg( argClasses[ idx ], pArgs[ idx ] );
                            }

                            instance = ((Constructor<T>) c).newInstance( newArgs );

                            break;
                        }
                    } catch( SMTRuntimeException smtEx ) {
                        throw smtEx;

                    } catch( InvocationTargetException ite ) {
                        final Throwable target = ite.getTargetException();

                        if ( target instanceof SMTRuntimeException ) {
                            throw (SMTRuntimeException) target;
                        }

                        _log.log( Level.trace, "Constructor returned error for " + className + " : " + e.getMessage() );

                    } catch( Exception e2 ) {
                        _log.log( Level.trace, "Constructor returned error for " + className + " : " + e.getMessage() );
                    }
                }
            }

        } catch( Exception e ) {

            throw new SMTRuntimeException( "Unable to instantiate class " + className + " : " + e.getMessage(), e );
        }

        if ( instance == null ) {
            throw new SMTRuntimeException( "Unable to instantiate class from any available constructors " + className );
        }

        return instance;
    }

    public static boolean hasNoArgConstructor( Class<?> clazz ) {
        for ( Constructor c : clazz.getDeclaredConstructors() ) {
            if ( c.getParameterTypes().length == 0 ) return true;
        }
        return false;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public static Object convertArg( final Class<?> type, final Object pArg ) {

        if ( pArg == null ) return null;

        if ( ZString.class.isAssignableFrom( type ) ) {
            if ( !(pArg instanceof ZString) ) return TLC.safeCopy( pArg.toString() );
        } else if ( type == Double.class || type == double.class ) {
            return StringUtils.parseDouble( pArg.toString() );
        } else if ( type == Long.class || type == long.class ) {
            return Long.parseLong( pArg.toString() );
        } else if ( type == Boolean.class || type == boolean.class ) {
            return StringUtils.parseBoolean( pArg.toString() );
        } else if ( type == Integer.class || type == int.class ) {
            return Integer.parseInt( pArg.toString() );
        } else if ( Enum.class.isAssignableFrom( type ) ) {
            Class<? extends Enum> etype = (Class<? extends Enum>) type;
            return getEnumVal( etype, pArg.toString() );
        }

        return pArg;
    }

    public static <T> T create( Class<? extends T> className ) {
        T instance = null;

        try {
            instance = className.newInstance();

        } catch( IllegalAccessException e ) {

            final Constructor<?>[] cs = className.getDeclaredConstructors();

            for ( Constructor c : cs ) {
                if ( c.getParameterCount() == 0 ) {
                    boolean wasAcc = c.isAccessible();
                    c.setAccessible( true );
                    try {
                        instance = (T) c.newInstance();

                        return instance;

                    } catch( Exception e2 ) {
                        // dont care
                    }
                }
            }

        } catch( Exception e ) {
            throw new RuntimeException( "Unable to instantiate class " + className.getName(), e );
        }

        return instance;
    }

    public static <T> T create( String className, String smtId ) {
        Class<?>[] pClass = { String.class };
        Object[]   pArgs  = { smtId };

        return create( className, pClass, pArgs );
    }

    /**
     * create new instance of clazz .... cater for inner class which required parent to be non null
     *
     * @param clazz
     * @param parent
     * @param <T>
     * @return
     */
    public static <T> T instantiate( Class<? extends T> clazz, Object parent ) {

        if ( parent == null ) {
            try {
                return create( clazz );
            } catch( RuntimeException e ) {
                throw e;
            }
        }

        T instance = null;

        try {
            instance = clazz.newInstance();

        } catch( IllegalAccessException e ) {

            final Constructor<?>[] cs = clazz.getDeclaredConstructors();

            for ( Constructor c : cs ) {
                if ( c.getParameterCount() == 0 ) {
                    c.setAccessible( true );
                    try {
                        instance = (T) c.newInstance();

                        return instance;

                    } catch( Exception e2 ) {
                        // dont care
                    }
                }
            }

        } catch( Exception e ) {

            if ( clazz.getName().contains( "$" ) ) {
                try {

                    final Constructor<?>[] cs = clazz.getDeclaredConstructors();

                    // should check if this is static
                    for ( Constructor c : cs ) {
                        if ( c.getParameterCount() == 0 ) {
                            c.setAccessible( true );
                            instance = (T) c.newInstance();

                            return instance;
                        }
                    }

                    Object tmpParent = parent;

                    while( tmpParent != null ) {

                        // this is non static
                        try {
                            for ( Constructor c : cs ) {
                                if ( c.getParameterCount() == 1 ) {
                                    c.setAccessible( true );
                                    instance = (T) c.newInstance( new Object[] { tmpParent } );

                                    return instance;
                                }
                            }
                        } catch( Exception ex ) {
                            // ignore
                        }

                        Field outerWrapper = tmpParent.getClass().getDeclaredField( "this$0" );
                        outerWrapper.setAccessible( true );
                        tmpParent = outerWrapper.get( tmpParent );
                    }

                } catch( Exception e2 ) {
                    // ignore
                }

                try {
                    Constructor<?> c = clazz.getDeclaredConstructor( new Class[] { parent.getClass() } );

                    c.setAccessible( true );
                    instance = (T) c.newInstance( new Object[] { parent } );

                    return instance;

                } catch( Exception ex ) {
                    // ignore
                }

                throw new RuntimeException( "Unable to instantiate class " + clazz.getName(), e );

            } else {

                throw new RuntimeException( "Unable to instantiate class " + clazz.getName(), e );
            }
        }

        return instance;
    }

    public static <T> T forceCreate( Class<? extends T> className, Object parent ) {

        if ( parent == null ) {
            try {
                return create( className );
            } catch( RuntimeException e ) {

                T instance = forceConstructorUsingNulls( className, null );

                if ( instance != null ) {
                    return instance;
                }

                throw e;
            }
        }

        T instance = null;

        try {
            instance = className.newInstance();

        } catch( IllegalAccessException e ) {

            final Constructor<?>[] cs = className.getDeclaredConstructors();

            for ( Constructor c : cs ) {
                if ( c.getParameterCount() == 0 ) {
                    c.setAccessible( true );
                    try {
                        instance = (T) c.newInstance();

                        return instance;

                    } catch( Exception e2 ) {
                        // dont care
                    }
                }
            }

        } catch( Exception e ) {

            if ( className.getName().contains( "$" ) ) {
                try {

                    final Constructor<?>[] cs = className.getDeclaredConstructors();

                    // should check if this is static
                    for ( Constructor c : cs ) {
                        if ( c.getParameterCount() == 0 ) {
                            c.setAccessible( true );
                            instance = (T) c.newInstance();

                            return instance;
                        }
                    }

                    Object tmpParent = parent;

                    while( tmpParent != null ) {

                        // this is non static
                        try {
                            for ( Constructor c : cs ) {
                                if ( c.getParameterCount() == 1 ) {
                                    c.setAccessible( true );
                                    instance = (T) c.newInstance( new Object[] { tmpParent } );

                                    return instance;
                                }
                            }
                        } catch( Exception ex ) {
                            // ignore
                        }

                        Field outerWrapper = tmpParent.getClass().getDeclaredField( "this$0" );
                        outerWrapper.setAccessible( true );
                        tmpParent = outerWrapper.get( tmpParent );
                    }

                } catch( Exception e2 ) {
                    // ignore
                }

                try {
                    Constructor<?> c = className.getDeclaredConstructor( new Class[] { parent.getClass() } );

                    c.setAccessible( true );
                    instance = (T) c.newInstance( new Object[] { parent } );

                    return instance;

                } catch( Exception ex ) {

                    instance = forceConstructorUsingNulls( className, parent );

                    if ( instance != null ) {
                        return instance;
                    }
                }

                throw new RuntimeException( "Unable to instantiate class " + className.getName(), e );

            } else {

                instance = forceConstructorUsingNulls( className, null );

                if ( instance != null ) {
                    return instance;
                }

                throw new RuntimeException( "Unable to instantiate class " + className.getName(), e );
            }
        }

        return instance;
    }

    private static <T> T forceConstructorUsingNulls( final Class<? extends T> className, Object parent ) {
        T instance = null;

        if ( className.getName().startsWith( "hal.smt" ) ) {
            throw new SMTRuntimeException( "SMT class missing null arg constructor " + className.getName() );
        }

        final Constructor<?>[] cs = className.getDeclaredConstructors();

        for ( Constructor c : cs ) {
            c.setAccessible( true );
            try {
                int cnt = c.getParameterCount();

                if ( cnt == 1 ) {

                    final Class[] types = c.getParameterTypes();

                    Class ac = types[ 0 ];

                    if ( ac.isPrimitive() ) {
                        if ( ac == int.class ) {
                            instance = (T) c.newInstance( (int) 0 );
                        } else if ( ac == long.class ) {
                            instance = (T) c.newInstance( (long) 0 );
                        } else if ( ac == double.class ) {
                            instance = (T) c.newInstance( (double) 0 );
                        }
                    } else {
                        Object[] na = { null };

                        instance = (T) c.newInstance( na );
                    }

                    return instance;
                }

            } catch( Throwable t ) {
                // dont care
            }
        }

        for ( Constructor c : cs ) {
            try {

                int cnt = c.getParameterCount();

                if ( cnt > 1 ) {

                    final Class[] types = c.getParameterTypes();
                    Object[]      args  = new Object[ cnt ];

                    for ( int j = 0; j < cnt; j++ ) {
                        Class ac = types[ j ];

                        if ( ac.isPrimitive() ) {
                            if ( ac == int.class ) {
                                args[ j ] = (int) 0;
                            } else if ( ac == long.class ) {
                                args[ j ] = (long) 0;
                            } else if ( ac == double.class ) {
                                args[ j ] = (double) 0;
                            }
                        } else {
                            if ( parent != null && parent.getClass() == ac ) {

                                args[ j ] = parent;

                            } else {

                                args[ j ] = null;
                            }
                        }
                    }

                    instance = (T) c.newInstance( args );

                    return instance;
                }

            } catch( Throwable t2 ) {
                // dont care
            }
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T getPublicStaticMember( Class<? extends T> tClass, String memberName ) {

        T val;

        try {
            Field f = tClass.getField( memberName );

            if ( f == null ) {
                throw new SMTRuntimeException( "getPublicStaticMember()  memberName " + memberName + " doesnt exist in " + tClass.getSimpleName() );
            }

            val = (T) f.get( null );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "getPublicStaticMember()  unable to access memberName " + memberName + " doesnt exist in " + tClass.getSimpleName(), e );
        }

        if ( val == null ) {
            throw new SMTRuntimeException( "getPublicStaticMember()  memberName " + memberName + " is NULL in " + tClass.getSimpleName() );
        }

        return val;
    }

    public static <T> T create( Class<? extends T> tClass, Class<?>[] pClass, Object[] pArgs ) {
        T instance;
        try {
            Constructor<? extends T> c;

            if ( pClass.length == 0 ) {
                return create( tClass );
            }

            c = tClass.getDeclaredConstructor( pClass );
            c.setAccessible( true );
            instance = c.newInstance( pArgs );

        } catch( Exception e ) {
            throw new RuntimeException( "Unable to instantiate class " + tClass.getName() + " " + e.getMessage(), e );
        }

        return instance;
    }

    public static Set<Field> getMembers( Object obj ) {
        return (getMembers( obj.getClass() ));
    }

    public static Set<Field> getMembers( Class clazz ) {
        Set<Field> fields = new LinkedHashSet<>();

        while( clazz != null ) {
            Field[] mFields = clazz.getDeclaredFields();

            fields.addAll( Arrays.asList( mFields ) );

            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    /**
     * use reflection to set the member field to the supplied value
     * <p>
     * convert to bool/double/int/long as appropriate
     * <p>
     * for fields that have an array, the value is treated as comma delimited list of values
     *
     * @param obj   - instance to set field in
     * @param f
     * @param value (in string form)
     * @NOTE not efficient ..only for use in non time sensitive code
     */
    public static void setMemberFromString( Object obj, Field f, String value ) {
        Class<?> type = f.getType();

        boolean wasFinal = Modifier.isFinal( f.getModifiers() );

        try {
            f.setAccessible( true );

            if ( wasFinal ) setFinal( f, false );
            if ( type.isArray() ) {
                setArrayMemberFromString( obj, f, value, type );
            } else if ( !setPrimitive( obj, f, value, type ) ) {
                throw new SMTRuntimeException( "ReflectUtils.setMember() unable to set field " + f.getName() + " in " + obj.getClass().getSimpleName() + ", as type " + type.getSimpleName() +
                                               " not supported : value=" + value );
            }
        } catch( ClassNotFoundException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setMember() unable to set field " + f.getName() + ", to unfound class" + value, e );
        } catch( NumberFormatException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setMember() unable to set field " + f.getName() + ", to invalid value " + value, e );
        } catch( IllegalArgumentException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setMember() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " not supported ... check if valid enum :- " + e.getMessage(), e );
        } catch( IllegalAccessException e ) {

            throw new SMTRuntimeException( "ReflectUtils.setMember() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " access not allowed", e );
        } finally {
            if ( wasFinal ) setFinal( f, true );
        }
    }

    public static void setArrayMemberFromString( Object obj, Field f, String arrValue, Class<?> type ) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

        boolean wasFinal = Modifier.isFinal( f.getModifiers() );

        try {
            f.setAccessible( true );

            if ( wasFinal ) setFinal( f, false );

            if ( type.getName().startsWith( "[[" ) ) { // 2D arr
                String vals[][] = get2DArrayVals( arrValue );

                int   rows = vals.length;
                int   cols = vals[ 0 ].length;
                int[] size = { rows, cols };

                Class<?> baseType = type.getComponentType().getComponentType();
                Object   arr      = Array.newInstance( baseType, size );

                for ( int row = 0; row < rows; row++ ) {
                    String[] rowVals = vals[ row ];

                    Object rowArr = Array.newInstance( baseType, rowVals.length );

                    for ( int col = 0; col < rowVals.length; col++ ) {
                        String value = rowVals[ col ].trim();

                        setArrayFieldEntryFromString( baseType, rowArr, col, value );
                    }

                    Array.set( arr, row, rowArr );
                }

                f.set( obj, arr );

            } else { // 1D arr
                String vals[] = getArrayVals( arrValue );

                Object arr = Array.newInstance( type.getComponentType(), vals.length );
                for ( int i = 0; i < vals.length; i++ ) {
                    String value = vals[ i ].trim();

                    setArrayFieldEntryFromString( type.getComponentType(), arr, i, value );
                }

                f.set( obj, arr );
            }
        } catch( ClassNotFoundException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setArrayMemberFromString() unable to set field " + f.getName() + ", to unfound class" + arrValue, e );
        } catch( NumberFormatException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setArrayMemberFromString() unable to set field " + f.getName() + ", to invalid value " + arrValue, e );
        } catch( IllegalArgumentException e ) {
            throw new SMTRuntimeException( "ReflectUtils.setArrayMemberFromString() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " not supported ... check if valid enum :- " + e.getMessage(), e );
        } catch( IllegalAccessException e ) {

            throw new SMTRuntimeException( "ReflectUtils.setArrayMemberFromString() unable to set field " + f.getName() + ", as type " + type.getSimpleName() +
                                           " access not allowed", e );
        } finally {
            if ( wasFinal ) setFinal( f, true );
        }
    }

    public static void setArrayFieldEntryFromString( final Class<?> type, final Object arr, final int i, final String value ) throws ClassNotFoundException {
        if ( type == null ) {
            Array.set( arr, i, value );
        } else if ( ZString.class.isAssignableFrom( type ) ) {
            ZString r = (ZString) Array.get( arr, i );
            if ( r != null && r.getClass() == ReusableString.class ) {
                ((ReusableString) r).copy( value );
            } else {
                Array.set( arr, i, new ReusableString( value ) );
            }
        } else if ( type == String.class ) {
            Array.set( arr, i, value );
        } else if ( type == double.class ) {
            double d = StringUtils.parseDouble( value );
            Array.setDouble( arr, i, d );
        } else if ( type == Double.class ) {
            Double d = StringUtils.parseDouble( value );
            Array.set( arr, i, d );
        } else if ( type == long.class ) {
            long l = Long.parseLong( value );
            Array.setLong( arr, i, l );
        } else if ( type == Long.class ) {
            Long l = Long.parseLong( value );
            Array.set( arr, i, l );
        } else if ( type == boolean.class ) {
            boolean b = StringUtils.parseBoolean( value );
            Array.setBoolean( arr, i, b );
        } else if ( type == Boolean.class ) {
            Boolean b = StringUtils.parseBoolean( value );
            Array.set( arr, i, b );
        } else if ( type == int.class ) {
            int v = Integer.parseInt( value );
            Array.setInt( arr, i, v );
        } else if ( type == Integer.class ) {
            Integer v = Integer.parseInt( value );
            Array.set( arr, i, v );
        } else if ( type == Class.class ) {
            Class<?> c = Class.forName( value );
            Array.set( arr, i, c );
        } else if ( Enum.class.isAssignableFrom( type ) ) {
            @SuppressWarnings( { "rawtypes", "unchecked" } )
            Class<? extends Enum> etype = (Class<? extends Enum>) type;
            @SuppressWarnings( "unchecked" )
            Object val = getEnumVal( etype, value );
            Array.set( arr, i, val );
        } else if ( type == Byte.class || type == byte.class ) {
            byte l = Byte.parseByte( value );
            Array.set( arr, i, l );
        } else if ( type == float.class ) {
            float d = Float.parseFloat( value );
            Array.setFloat( arr, i, d );
        } else if ( type == Float.class ) {
            Float d = Float.parseFloat( value );
            Array.set( arr, i, d );
        }
    }

    public static void setArrayFieldEntryNull( final Class<?> type, final Object arr, final int i ) throws ClassNotFoundException {
        if ( type == double.class ) {
            Array.setDouble( arr, i, Constants.UNSET_DOUBLE );
        } else if ( type == long.class ) {
            Array.setLong( arr, i, Constants.UNSET_LONG );
        } else if ( type == boolean.class ) {
            Array.setBoolean( arr, i, false );
        } else if ( type == int.class ) {
            Array.setInt( arr, i, Constants.UNSET_INT );
        } else if ( type == byte.class ) {
            Array.set( arr, i, Constants.UNSET_BYTE );
        } else if ( type == Float.class || type == float.class ) {
            Array.setFloat( arr, i, Constants.UNSET_FLOAT );
        } else {
            Array.set( arr, i, null );
        }
    }

    public static void dump( ReusableString dumpBuf, Object obj ) {

        Class<?> clazz = obj.getClass();

        dumpBuf.append( ' ' ).append( clazz.getSimpleName() );

        while( clazz != null ) {
            Field[] mFields = clazz.getDeclaredFields();

            for ( Field f : mFields ) {
                Class<?> type = f.getType();

                boolean wasAccessable = f.isAccessible();
                try {
                    f.setAccessible( true );
                    if ( ZString.class.isAssignableFrom( type ) ) {
                        ZString val = (ZString) f.get( obj );
                        if ( val != null && val.length() > 0 ) dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( val );
                    } else if ( type == String.class ) {
                        String val = (String) f.get( obj );
                        if ( val != null && val.length() > 0 ) dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( val );
                    } else if ( type == Double.class || type == double.class ) {
                        dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( f.getDouble( obj ) );
                    } else if ( type == Long.class || type == long.class ) {
                        dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( f.getLong( obj ) );
                    } else if ( type == Boolean.class || type == boolean.class ) {
                        dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( f.getBoolean( obj ) );
                    } else if ( type == Integer.class || type == int.class ) {
                        dumpBuf.append( ", " ).append( f.getName() ).append( '=' ).append( f.getInt( obj ) );
                    }
                } catch( Exception e ) {
                    // ignore
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * silent reflect invocation of method name if it exists
     *
     * @param methodName
     * @param obj
     * @return true if invoked false if not
     */
    public static boolean invoke( String methodName, Object obj ) {
        Class<?> c = obj.getClass();

        while( c != null ) {
            try {
                Method m = c.getDeclaredMethod( methodName, NULL_CLASS_ARGS );

                boolean wasAccessible = m.isAccessible();

                if ( !wasAccessible ) m.setAccessible( true );

                m.invoke( obj, NULL_ARGS );

                return true;

            } catch( NoSuchMethodException e ) {
                // ignore

            } catch( Exception e ) {
                throw new SMTRuntimeException( e.getMessage(), e.getCause() );
            }

            c = c.getSuperclass();
        }

        return false;
    }

    public static <T> T invokeFunc( String methodName, Object obj ) {
        if ( obj == null ) return null;

        Class<?> c = obj.getClass();

        while( c != null ) {
            try {
                Method m = c.getDeclaredMethod( methodName, NULL_CLASS_ARGS );

                boolean wasAccessible = m.isAccessible();

                if ( !wasAccessible ) m.setAccessible( true );

                T v = (T) m.invoke( obj, NULL_ARGS );

                return v;

            } catch( NoSuchMethodException e ) {
                // ignore

            } catch( Exception e ) {
                throw new SMTRuntimeException( e.getMessage(), e.getCause() );
            }

            c = c.getSuperclass();
        }

        return null;
    }

    public static <T> T invokeFunc( String methodName, Object obj, final Class<?>[] classArgs, final Object[] args ) {
        if ( obj == null ) return null;

        Class<?> c = obj.getClass();

        while( c != null ) {
            try {
                Method m = c.getDeclaredMethod( methodName, classArgs );

                boolean wasAccessible = m.isAccessible();

                if ( !wasAccessible ) m.setAccessible( true );

                T v = (T) m.invoke( obj, args );

                return v;

            } catch( NoSuchMethodException e ) {
                // ignore

            } catch( Exception e ) {
                throw new SMTRuntimeException( e.getMessage(), e.getCause() );
            }

            c = c.getSuperclass();
        }

        return null;
    }

    public static void invoke( Method m, Object obj ) {
        Class<?> c = obj.getClass();

        try {

            boolean wasAccessible = m.isAccessible();

            if ( !wasAccessible ) m.setAccessible( true );

            m.invoke( obj, NULL_ARGS );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "ReflectUtils unable to invoke " + m.getName() + " on " + c.getName() + " : " + e.getMessage(), e );
        }
    }

    public static boolean invoke( String methodName, Object obj, final Class<?>[] classArgs, final Object[] args ) {
        if ( obj == null ) return false;

        Class<?> c = obj.getClass();

        while( c != null ) {
            try {
                Method m = c.getDeclaredMethod( methodName, classArgs );

                boolean wasAccessible = m.isAccessible();

                if ( !wasAccessible ) m.setAccessible( true );

                m.invoke( obj, args );

                return true;

            } catch( NoSuchMethodException e ) {
                // ignore

            } catch( Exception e ) {
                throw new SMTRuntimeException( e.getMessage(), e.getCause() );
            }

            c = c.getSuperclass();
        }

        return false;
    }

    public static void invoke( Method m, Object obj, final Object[] args ) {
        Class<?> c = obj.getClass();

        try {

            boolean wasAccessible = m.isAccessible();

            if ( !wasAccessible ) m.setAccessible( true );

            m.invoke( obj, args );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "ReflectUtils unable to invoke " + m.getName() + " on " + c.getName() + " : " + e.getMessage(), e );
        }
    }

    /**
     * if class has a static instance() return that, else create new instance
     *
     * @param className
     * @return instance of className
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T findInstance( String className ) {
        try {
            Class<T> c = (Class<T>) Class.forName( className );

            Method m = c.getMethod( "instance", NULL_CLASS_ARGS );

            if ( m != null ) {
                return (T) m.invoke( null, NULL_ARGS );
            }

            return c.newInstance();

        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to findInstance for class " + className );
        }
    }

    public static void setProperties( Object dest, Map<String, String> props ) {
        setProperties( dest, props, false );
    }

    public static void setProperties( Object dest, Map<String, String> props, boolean ignoreMissingFields ) {
        Set<Field> fields = ReflectUtils.getMembers( dest );

        for ( Map.Entry<String, String> entry : props.entrySet() ) {
            String propertyEntry = entry.getKey();
            String value         = entry.getValue();

            if ( ignoreMissingFields && !hasProperty( dest, fields, propertyEntry ) ) {
                continue;
            }

            setProperty( dest, fields, propertyEntry, value );
        }
    }

    public static String[] getArrayVals( String value ) {
        if ( value == null ) return null;

        value = value.trim();

        String[] commas = value.split( "," );

        if ( commas.length == 1 ) {
            String[] semiColons = value.split( ";" );

            if ( semiColons.length > 1 ) {
                return semiColons;
            }
        }

        return commas;
    }

    public static String[][] get2DArrayVals( String value ) {
        if ( value == null ) return null;

        value = value.trim();

        if ( value.charAt( 0 ) != '{' || value.charAt( value.length() - 1 ) != '}' ) {
            throw new SMTRuntimeException( "Error in config, field expected two D array using curly brackets got " + value );
        }

        value = value.substring( 1, value.length() - 1 );

        int cnt = 0;

        ArrayList<String[]> rows = new ArrayList<>();

        while( true ) {
            value = value.trim();

            if ( value.charAt( 0 ) != '{' ) {
                throw new SMTRuntimeException( "Error in config, field expected two D array missing curly start bracket on row " + cnt + " src=" + value );
            }

            int endIdx = value.indexOf( '}' );

            if ( endIdx == -1 ) {
                throw new SMTRuntimeException( "Error in config, field expected two D array missing curly end bracket on row " + cnt + " src=" + value );
            }

            String curVal = value.substring( 1, endIdx );

            String[] rowVals = getArrayVals( curVal );

            int rowSize = rowVals.length;

            rows.add( rowVals );

            int delimIdx = value.indexOf( ',', endIdx + 1 );

            if ( delimIdx == -1 ) {
                delimIdx = value.indexOf( ';', endIdx + 1 );
                if ( delimIdx == -1 ) {
                    break;
                }
            }

            int nextStartIdx = value.indexOf( '{', endIdx + 1 );

            if ( nextStartIdx != -1 && nextStartIdx < delimIdx ) {
                throw new SMTRuntimeException( "Error in config, field expected two D array missing comma sep between rows on row " + cnt + " src=" + value );
            }

            value = value.substring( delimIdx + 1 );
        }

        String[][] arr = new String[ rows.size() ][];

        for ( int row = 0; row < rows.size(); ++row ) {
            arr[ row ] = rows.get( row );
        }

        return arr;
    }

    /**
     * reflectively set properties in dest that exist in src (exclude static)
     *
     * @param dest
     * @param src
     */
    public static void setProperties( Object dest, Object src ) {
        Set<Field> destFields = ReflectUtils.getMembers( dest );
        Class<?>   srcClass   = src.getClass();

        for ( Field destField : destFields ) {
            int destModifiers = destField.getModifiers();

            if ( Modifier.isStatic( destModifiers ) ) continue;

            String propertyEntry = destField.getName();

            Field srcField = null;

            boolean wasSrcAcc  = true; // default so dont invoke setAccessible in finally clause if not needed
            boolean wasDestAcc = destField.isAccessible();

            try {
                srcField = srcClass.getDeclaredField( propertyEntry );

                if ( srcField != null ) {

                    int srcFieldModifiers = srcField.getModifiers();

                    if ( !Modifier.isStatic( srcFieldModifiers ) ) {
                        wasSrcAcc = srcField.isAccessible();

                        if ( !wasSrcAcc ) srcField.setAccessible( true );
                        if ( !wasDestAcc ) destField.setAccessible( true );

                        Object srcFieldVal = srcField.get( src );

                        destField.set( dest, srcFieldVal );
                    }
                }

            } catch( Exception e ) {
                // dont care
            }
        }
    }

    public static boolean invokeSetterWithString( Object obj, Method m, String value, Class<?> type ) {
        try {
            if ( type == ReusableString.class ) {
                m.invoke( obj, TLC.safeCopy( value ) );
            } else if ( ZString.class.isAssignableFrom( type ) ) {
                m.invoke( obj, TLC.safeCopy( value ) );
            } else if ( type == String.class || type == Object.class ) {
                m.invoke( obj, value );
            } else {
                if ( value == null || value.length() == 0 ) {
                    invoke1PrimArgMethodWithNull( obj, m, type );
                    return true;
                } else if ( type == Double.class || type == double.class ) {
                    double d = StringUtils.parseDouble( value );
                    m.invoke( obj, d );
                    return true;
                } else if ( type == Long.class || type == long.class ) {
                    long l = Long.parseLong( value );
                    m.invoke( obj, l );
                    return true;
                } else if ( type == Boolean.class || type == boolean.class ) {
                    boolean b = StringUtils.parseBoolean( value );
                    m.invoke( obj, b );
                    return true;
                } else if ( type == Integer.class || type == int.class ) {
                    int i = Integer.parseInt( value );
                    m.invoke( obj, i );
                    return true;
                } else if ( type == Class.class ) {
                    Class<?> c = Class.forName( value );
                    m.invoke( obj, c );
                    return true;
                } else if ( Enum.class.isAssignableFrom( type ) ) {
                    @SuppressWarnings( { "rawtypes", "unchecked" } )
                    Class<? extends Enum> etype = (Class<? extends Enum>) type;
                    @SuppressWarnings( "unchecked" ) Object val = getEnumVal( etype, value );
                    m.invoke( obj, val );
                    return true;
                } else if ( type == Short.class || type == short.class ) {
                    short i = Short.parseShort( value );
                    m.invoke( obj, i );
                    return true;
                } else if ( type == Byte.class || type == byte.class ) {
                    byte i = Byte.parseByte( value );
                    m.invoke( obj, i );
                    return true;
                } else if ( type == Float.class || type == float.class ) {
                    float fv = Float.parseFloat( value );
                    m.invoke( obj, fv );
                    return true;
                }
            }
        } catch( Exception e ) {
            return false;
        }

        return true;
    }

    private static Object getEnumVal( final Class<? extends Enum> etype, final String value ) {
        if ( _ignoreMissingEnum ) {
            try {
                return Enum.valueOf( etype, value );
            } catch( Exception e ) {
                // dont use WARN level as may get LOTS of these if someone removes an enum value or changes it
                _log.info( "WARN cant set enum " + etype.getName() + " to " + value + " as its not a valid entry, setting to null" );
            }
            return null;
        }
        return Enum.valueOf( etype, value );
    }

    public static void setPropertySilent( Object dest, String fieldName, String value ) {
        Set<Field> fields = ReflectUtils.getMembers( dest );

        try {
            setProperty( dest, fields, fieldName, value );
        } catch( Exception e ) {
            // ignore
        }
    }

    public static void setProperty( Object dest, String fieldName, String value ) {
        Set<Field> fields = ReflectUtils.getMembers( dest );

        setProperty( dest, fields, fieldName, value );
    }

    private static boolean hasProperty( Object dest, Set<Field> fields, String propertyEntry ) {
        boolean set = false;

        if ( propertyEntry.charAt( 0 ) == '_' ) propertyEntry = propertyEntry.substring( 1 );

        for ( Field f : fields ) {               //   iterate thru all the fields in the propObject looking for match
            String fieldName = f.getName();

            if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

            if ( fieldName.equalsIgnoreCase( propertyEntry ) ) {
                return true;
            }
        }

        return false;
    }

    private static void setProperty( Object dest, Set<Field> fields, String propertyEntry, String value ) {
        boolean set = false;

        if ( propertyEntry.charAt( 0 ) == '_' ) propertyEntry = propertyEntry.substring( 1 );

        for ( Field f : fields ) {               //   iterate thru all the fields in the propObject looking for match
            String fieldName = f.getName();

            if ( fieldName.charAt( 0 ) == '_' ) fieldName = fieldName.substring( 1 );

            if ( fieldName.equalsIgnoreCase( propertyEntry ) ) {

                if ( value != null && value.length() > 0 ) {
                    setMemberFromString( dest, f, value );
                }

                set = true;
                break; // DONE - NEXT PROPERTY ENTRY
            }
        }

        if ( !set ) {
            throw new SMTRuntimeException( "Unable to reflect set property [" + propertyEntry + "] as thats not valid member of " + dest.getClass().getSimpleName() );
        }
    }

    private static void invoke1PrimArgMethodWithNull( final Object obj, final Method m, final Class<?> type ) throws IllegalAccessException, InvocationTargetException {
        if ( type == double.class ) {
            m.invoke( obj, Constants.UNSET_DOUBLE );
        } else if ( type == long.class ) {
            m.invoke( obj, Constants.UNSET_LONG );
        } else if ( type == boolean.class ) {
            m.invoke( obj, false );
        } else if ( type == int.class ) {
            m.invoke( obj, Constants.UNSET_INT );
        } else if ( type == short.class ) {
            m.invoke( obj, Constants.UNSET_SHORT );
        } else if ( type == byte.class ) {
            m.invoke( obj, Constants.UNSET_BYTE );
        } else if ( type == float.class ) {
            m.invoke( obj, Constants.UNSET_FLOAT );
        } else {
            m.invoke( obj, (Object) null );
        }
    }

    private static void setNull( final Object obj, final Field f, final Class<?> type ) throws IllegalAccessException {
        if ( type == double.class ) {
            f.setDouble( obj, Constants.UNSET_DOUBLE );
        } else if ( type == long.class ) {
            f.setLong( obj, Constants.UNSET_LONG );
        } else if ( type == boolean.class ) {
            f.setBoolean( obj, false );
        } else if ( type == int.class ) {
            f.setInt( obj, Constants.UNSET_INT );
        } else if ( type == short.class ) {
            f.setShort( obj, Constants.UNSET_SHORT );
        } else if ( type == byte.class ) {
            f.setByte( obj, Constants.UNSET_BYTE );
        } else if ( type == float.class ) {
            f.setFloat( obj, Constants.UNSET_FLOAT );
        } else {
            f.set( obj, null );
        }
    }

    private static boolean setPrimitive( Object obj, Field f, String value, Class<?> type ) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        if ( type == ReusableString.class ) {
            setReusableString( obj, f, value );
            return true;
        } else if ( ZString.class.isAssignableFrom( type ) ) {
            ZString c = (ZString) f.get( obj );
            if ( c != null && c.getClass() == ReusableString.class ) {
                setReusableString( obj, f, value );
            } else {
                f.set( obj, TLC.safeCopy( value ) );
            }
            return true;
        } else if ( type == String.class || type == Object.class ) {
            f.set( obj, value );
            return true;
        } else if ( type == StringBuilder.class ) {
            StringBuilder objSB = (StringBuilder) f.get( obj );
            if ( objSB != null ) {
                objSB.setLength( 0 );
                objSB.append( value );
                return true;
            }
        } else {
            if ( value == null || value.length() == 0 ) {
                setNull( obj, f, type );
                return true;
            } else if ( type == double.class ) {
                double d = StringUtils.parseDouble( value );
                f.setDouble( obj, d );
                return true;
            } else if ( type == long.class ) {
                long l;

                try {
                    l = Long.parseLong( value );
                } catch( NumberFormatException e ) {

                    if ( f.isAnnotationPresent( TimestampMS.class ) ) {

                        byte[] bytes = value.getBytes();

                        l = TimeUtilsFactory.safeTimeUtils().parseUTCStringToInternalTime( bytes, 0, bytes.length );
                    } else {
                        throw e;
                    }
                }

                f.setLong( obj, l );
                return true;
            } else if ( type == boolean.class ) {
                boolean b = StringUtils.parseBoolean( value );
                f.setBoolean( obj, b );
                return true;
            } else if ( type == int.class ) {
                int i;

                try {
                    i = Integer.parseInt( value );
                } catch( NumberFormatException e ) {

                    if ( f.isAnnotationPresent( DateYYYYMMDD.class ) ) {

                        byte[] bytes = value.getBytes();

                        try {
                            i = StringUtils.parseDate( bytes, 0, bytes.length );
                        } catch( Exception ex ) {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }

                f.setInt( obj, i );
                return true;
            } else if ( type == Class.class ) {
                Class<?> c = Class.forName( value );
                f.set( obj, c );
                return true;
            } else if ( Enum.class.isAssignableFrom( type ) ) {
                @SuppressWarnings( { "rawtypes", "unchecked" } )
                Class<? extends Enum> etype = (Class<? extends Enum>) f.getType();
                @SuppressWarnings( "unchecked" )
                Object val = getEnumVal( etype, value );
                f.set( obj, val );
                return true;
            } else if ( type == short.class ) {
                short i = Short.parseShort( value );
                f.setShort( obj, i );
                return true;
            } else if ( type == byte.class ) {
                byte i = Byte.parseByte( value );
                f.setByte( obj, i );
                return true;
            } else if ( type == float.class ) {
                float fv = Float.parseFloat( value );
                f.setFloat( obj, fv );
                return true;
            } else if ( type == Double.class ) {
                Double d = StringUtils.parseDouble( value );
                f.set( obj, d );
                return true;
            } else if ( type == Long.class ) {
                long l = Long.parseLong( value );
                f.set( obj, l );
                return true;
            } else if ( type == Boolean.class ) {
                Boolean b = Boolean.valueOf( StringUtils.parseBoolean( value ) );
                f.set( obj, b );
                return true;
            } else if ( type == Integer.class ) {
                Integer i = Integer.parseInt( value );
                f.set( obj, i );
                return true;
            } else if ( type == Short.class ) {
                Short i = Short.parseShort( value );
                f.set( obj, i );
                return true;
            } else if ( type == Byte.class ) {
                Byte i = Byte.parseByte( value );
                f.set( obj, i );
                return true;
            } else if ( type == Float.class ) {
                Float fv = Float.parseFloat( value );
                f.set( obj, fv );
                return true;
            }
        }
        return false;
    }

    private static void setReusableString( final Object obj, final Field f, final String value ) throws IllegalAccessException {
        ReusableString r = (ReusableString) f.get( obj );
        if ( r == null ) {
            r = TLC.safeCopy( value );
            f.set( obj, r );
        } else {
            r.copy( value );
        }
    }

    public static void shallowCopy( Object dest, Object src, Set<Field> fields ) {
        shallowUpdate( dest, src, true, fields );
    }

    /**
     * copy primitive values which are not empty or null from source object to dest object
     * objects must be of the same class
     *
     * @param dest
     * @param src
     */
    public static void shallowUpdate( Object dest, Object src, Set<Field> fields ) {
        shallowUpdate( dest, src, false, fields );
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private static void shallowUpdate( final Object dest, final Object src, final boolean forceOverrideIfNewValUnset, Set<Field> fields ) {

        if ( dest == null || src == null ) return;

        if ( dest.getClass() != src.getClass() )
            throw new SMTRuntimeException( "shallowUpdate expected both classes to be same, dest=" + dest.getClass().getName() + ", src=" + src.getClass().getName() );

        if ( dest instanceof ShallowCopy ) {

            ShallowCopy destSC = (ShallowCopy) dest;

            if ( forceOverrideIfNewValUnset ) {
                destSC.shallowCopyFrom( src );
            } else {
                destSC.shallowMergeFrom( src );
            }

            return;
        }

        for ( Field field : fields ) {

            Object srcVal  = null;
            Object destVal = null;

            boolean wasAcc = field.isAccessible();

            try {
                if ( !wasAcc ) field.setAccessible( true );

                int destModifiers = field.getModifiers();
                if ( Modifier.isStatic( destModifiers ) ) continue;

                srcVal  = field.get( src );
                destVal = field.get( dest );

                if ( srcVal == null && destVal == null ) continue;

                if ( forceOverrideIfNewValUnset ) {
                    Class<?> type = (srcVal != null) ? srcVal.getClass() : destVal.getClass();

                    if ( ZString.class.isAssignableFrom( type ) ) {
                        if ( destVal != null && destVal.getClass() == ReusableString.class ) {
                            ((ReusableString) destVal).copy( (ZString) srcVal );
                        } else {
                            destVal = TLC.safeCopy( (ZString) srcVal );
                            field.set( dest, destVal );
                        }
                    } else if ( type == String.class ) {
                        field.set( dest, srcVal );
                    } else if ( type == Double.class || type == double.class ) {
                        field.setDouble( dest, (Double) srcVal );
                    } else if ( type == Long.class || type == long.class ) {
                        field.setLong( dest, (Long) srcVal );
                    } else if ( type == Integer.class || type == int.class ) {
                        field.set( dest, srcVal );
                    } else if ( type == Boolean.class || type == boolean.class ) {
                        field.set( dest, srcVal );
                    } else if ( Enum.class.isAssignableFrom( type ) ) {
                        field.set( dest, srcVal );
                    }
                } else {
                    if ( srcVal == null ) continue;

                    Class<?> type = srcVal.getClass();

                    if ( ZString.class.isAssignableFrom( type ) ) {
                        if ( ((ZString) srcVal).length() > 0 ) {
                            if ( destVal != null && destVal.getClass() == ReusableString.class ) {
                                ((ReusableString) destVal).copy( (ZString) srcVal );
                            } else {
                                destVal = TLC.safeCopy( (ZString) srcVal );
                                field.set( dest, destVal );
                            }
                        }
                    } else if ( type == String.class ) {
                        if ( ((String) srcVal).length() > 0 )
                            field.set( dest, srcVal );
                    } else if ( type == Float.class || type == float.class ) {
                        if ( Utils.hasVal( (float) srcVal ) )
                            field.setDouble( dest, (Double) srcVal );
                    } else if ( type == Double.class || type == double.class ) {
                        if ( Utils.hasVal( (Double) srcVal ) )
                            field.setDouble( dest, (Double) srcVal );
                    } else if ( type == Long.class || type == long.class ) {
                        if ( ((Long) srcVal) != Constants.UNSET_LONG )
                            field.setLong( dest, (Long) srcVal );
                    } else if ( type == Integer.class || type == int.class ) {
                        if ( ((Integer) srcVal) != Constants.UNSET_INT )
                            field.setInt( dest, (Integer) srcVal );
                    } else if ( type == Boolean.class || type == boolean.class ) {
                        field.setBoolean( dest, (Boolean) srcVal );
                    } else if ( Enum.class.isAssignableFrom( type ) ) {
                        field.set( dest, srcVal );
                    }
                }
            } catch( Exception e ) {
                throw new SMTRuntimeException( "shallowUpdate error setting object " + dest.getClass().getName() + ", field=" + field.getName() + " : " + e.getMessage(), e );
            }
        }
    }

    /**
     * set the field of an object to specified value
     *
     * @param obj
     * @param fieldName
     * @param val
     * @return true if member set, false if not
     */
    public static boolean setMember( final Object obj, final String fieldName, final Object val ) {

        if ( obj == null || fieldName == null ) return false;

        Class<?> objClass = obj.getClass();

        Field field = getMember( objClass, fieldName );

        if ( field == null ) return false;

        boolean wasAcc   = field.isAccessible();
        boolean wasFinal = Modifier.isFinal( field.getModifiers() );

        boolean done = false;

        try {
            if ( !wasAcc ) field.setAccessible( true );
            if ( wasFinal ) setFinal( field, false );

            Class<?> fieldType = field.getType();

            if ( fieldType.isPrimitive() && val == null ) {

                setNull( obj, field, fieldType );
                done = true;

            } else {
                field.set( obj, val );
                done = true;
            }

        } catch( Exception e ) {

            Class<?> superclass = objClass.getSuperclass();

            while( !done && superclass != null ) {
                field = doSilentGetMember( superclass, fieldName );

                if ( field != null ) {
                    Class<?> fieldType = field.getType();
                    wasAcc   = field.isAccessible();
                    wasFinal = Modifier.isFinal( field.getModifiers() );

                    if ( !wasAcc ) field.setAccessible( true );
                    if ( wasFinal ) setFinal( field, false );

                    try {
                        field.set( obj, val );
                        done = true;
                    } catch( Throwable t ) {
                        // swallow
                    }
                }

                superclass = superclass.getSuperclass();
            }

            if ( !done ) {
                throw new SMTRuntimeException( "Unable to setMember " + fieldName + " of " + objClass.getSimpleName() + " to " + val + " : " + e.getMessage(), e );
            }
        }

        if ( !done ) {
            throw new SMTRuntimeException( "Unable to setMember " + fieldName + " of " + objClass.getSimpleName() + " to " + val );
        }

        return true;
    }

    public static void setMember( final Object obj, final Field field, final Object val ) {

        if ( obj == null || field == null ) return;

        if ( val != null ) {
            Class<?> valClass = val.getClass();
            if ( valClass == String.class ) {
                setMemberFromString( obj, field, (String) val );
                return;
            }
        }

        boolean wasAcc   = field.isAccessible();
        boolean wasFinal = Modifier.isFinal( field.getModifiers() );

        try {
            if ( !wasAcc ) field.setAccessible( true );
            if ( wasFinal ) setFinal( field, false );

            String name = field.getName();

            if ( name.charAt( 0 ) == '_' ) name = name.substring( 1 );

            if ( val != null ) {
                field.set( obj, val );
            } else {
                Class<? extends Field> fieldClass = field.getClass();
                if ( fieldClass.isPrimitive() ) {
                    setNull( obj, field, fieldClass );
                } else {
                    field.set( obj, val );
                }
            }

        } catch( Exception e ) {
            /* dont care */
        } finally {
            if ( wasFinal ) setFinal( field, true );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T newInstanceOf( final T item ) {
        Class<?> cl = item.getClass();
        try {
            return (T) cl.newInstance();
        } catch( Exception e ) {

            if ( item instanceof Identifiable ) {
                Identifiable i = (Identifiable) item;

                try {
                    final Class<?>[] args = { String.class };
                    final Object[]   vals = { i.id() };

                    return (T) create( cl, args, vals );

                } catch( Exception ex ) {
                    /* fall thru to throw below */
                }
            }

            throw new SMTRuntimeException( "ReflectUtils : unable to create instance of class " + cl.getName() + " : " + e.getMessage(), e );
        }
    }

    public static <T> T newInstanceOfClass( final Class<T> cl ) {
        try {
            return cl.newInstance();
        } catch( Exception e ) {
            throw new SMTRuntimeException( "ReflectUtils : unable to create instance of class " + cl.getName() + " : " + e.getMessage(), e );
        }
    }

    public static Field getMember( final Class clazz, final String field ) {

        return doGetMember( clazz, field );
    }

    /**
     * @param obj
     * @param <T>
     * @return
     * @WARNING USE CLONE WITH EXTREME CARE ... ANY REUSABLESTRING'S WILL NOT BE COPIED INSTEAD THEY ARE SHARED !!!
     * <p>
     * BASICALLY AVOID, USE  CopyConstructor interface and newInstance instead
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T shallowClone( final T obj ) {

        try {
            if ( obj instanceof ShallowCopy ) {

                ShallowCopy newObj = (ShallowCopy) newInstanceOf( obj );

                newObj.shallowCopyFrom( obj );

                return (T) newObj;
            }

            Method clone = obj.getClass().getMethod( "clone" );
            return (T) clone.invoke( obj );

        } catch( Exception e ) {

            try {
                T newObj = (T) newInstanceOf( obj );

                shallowCopy( newObj, obj, getMembers( obj ) );

                return newObj;

            } catch( Exception ex ) {

                throw new SMTRuntimeException( "Unable to shallowClone " + ex.getMessage(), ex );
            }
        }
    }

    public static void clearAllThreadLocals() {
        Field   threadLocalsField              = null;
        boolean wasThreadLocalsFieldAccessible = false;

        Field   tableField              = null;
        boolean wasTableFieldAccessible = false;

        Field   referentField              = null;
        boolean wasReferentFieldAccessible = false;

        try {
            // Get a reference to the thread locals table of the current thread
            threadLocalsField = Thread.class.getDeclaredField( "threadLocals" );

            if ( threadLocalsField == null ) return;

            wasThreadLocalsFieldAccessible = threadLocalsField.isAccessible();
            threadLocalsField.setAccessible( true );

            Thread[] threads = getAllThreads();
            for ( Thread thread : threads ) {
                Object threadLocalTable = threadLocalsField.get( thread );

                if ( threadLocalTable != null ) {
                    // Get a reference to the array holding the thread local variables inside the
                    // ThreadLocalMap of the current thread
                    Class threadLocalMapClass = Class.forName( "java.lang.ThreadLocal$ThreadLocalMap" );

                    if ( threadLocalMapClass == null ) continue;

                    tableField = threadLocalMapClass.getDeclaredField( "table" );

                    if ( tableField == null ) continue;

                    wasTableFieldAccessible = tableField.isAccessible();
                    tableField.setAccessible( true );
                    Object table = tableField.get( threadLocalTable );

                    if ( table == null ) continue;

                    // The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
                    // is a reference to the actual ThreadLocal variable
                    referentField = Reference.class.getDeclaredField( "referent" );

                    if ( referentField == null ) continue;

                    wasReferentFieldAccessible = referentField.isAccessible();
                    referentField.setAccessible( true );

                    for ( int i = 0; i < Array.getLength( table ); i++ ) {
                        // Each entry in the table array of ThreadLocalMap is an Entry object
                        // representing the thread local reference and its value
                        Object entry = Array.get( table, i );
                        if ( entry != null ) {
                            // Get a reference to the thread local object and remove it from the table
                            ThreadLocal threadLocal = (ThreadLocal) referentField.get( entry );
                            if ( threadLocal != null ) threadLocal.remove();
                        }
                    }
                }
            }
        } catch( Exception e ) {
            // ignore
        }
    }

    public static Thread[] getAllThreads() {
        final ThreadGroup  root   = getRootThreadGroup();
        final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
        int                nAlloc = thbean.getThreadCount();
        int                n      = 0;
        Thread[]           threads;
        do {
            nAlloc *= 2;
            threads = new Thread[ nAlloc ];
            n       = root.enumerate( threads, true );
        } while( n == nAlloc );
        return java.util.Arrays.copyOf( threads, n );
    }

    public static boolean equals( Object a, Object b ) {
        if ( a == b ) return true;
        if ( a.getClass() != b.getClass() ) return false;

        final Set<Field> fields = getMembers( a.getClass() );

        for ( Field field : fields ) {
            boolean wasAcc = field.isAccessible();

            try {
                if ( !wasAcc ) field.setAccessible( true );

                Object va = field.get( a );
                Object vb = field.get( b );

                if ( !Objects.equals( va, vb ) ) {
                    return false;
                }

            } catch( Exception e ) {
                return false;
            }
        }

        return true;
    }

    public static String toStringOrNull( final Class<?> clazz, final String fieldName, final Object obj ) {
        if ( obj == null ) return null;

        Field   field  = ReflectUtils.getMember( clazz, fieldName );
        Object  t      = null;
        boolean wasAcc = field.isAccessible();

        try {
            field.setAccessible( true );

            if ( obj != null ) {
                t = field.get( obj );

                if ( t != null ) return t.toString();
            }
        } catch( Exception e ) {
            // ignore
        }
        return null;
    }

    public static <T> T getOrNull( final Class<?> clazz, final String fieldName, final Object obj ) {

        if ( obj == null ) return null;

        Field   field  = ReflectUtils.getMember( clazz, fieldName );
        T       t      = null;
        boolean wasAcc = field.isAccessible();

        try {
            field.setAccessible( true );

            if ( obj != null ) {
                t = (T) field.get( obj );

                if ( t != null ) return t;
            }
        } catch( Exception e ) {
            // ignore
        }
        return null;
    }

    public static <T> T get( final Class<?> clazz, final String fieldName, final Object obj ) {
        Field   field  = ReflectUtils.getMember( clazz, fieldName );
        T       t      = null;
        boolean wasAcc = field.isAccessible();

        try {
            field.setAccessible( true );

            if ( obj != null ) {
                t = (T) field.get( obj );
            }
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to reflect get item " + fieldName + " from " + clazz.getSimpleName() );
        }
        return t;
    }

    public static <T> T get( final Field field, final Object obj ) {
        T       t      = null;
        boolean wasAcc = field.isAccessible();

        try {
            field.setAccessible( true );

            t = (T) field.get( obj );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to reflect get item " + field.getName() + " from " + field.getDeclaringClass().getSimpleName() );
        }
        return t;
    }

    public static Class<?> baseType( Type type ) {
        if ( type instanceof Class<?> ) {
            return ((Class<?>) type);
        } else if ( type instanceof GenericArrayType ) {
            return Array.newInstance( baseType( ((GenericArrayType) type).getGenericComponentType() ), 0 ).getClass();
        } else if ( type instanceof ParameterizedType ) {
            return baseType( ((ParameterizedType) type).getRawType() );
        } else {
            return Object.class;
        }
    }

    public static boolean isLambda( Type type ) {
        Class<?> baseType = baseType( type );
        if ( baseType.getName().contains( "$$Lambda$" ) ) { // YES AN APPROXIMATION BUT GOOD ENOUGH
            return true;
        }
        return false;
    }

    public static boolean isSerializableLambda( Type type ) {
        Class<?> baseType = baseType( type );
        if ( baseType.getName().contains( "$$Lambda$" ) ) { // YES AN APPROXIMATION BUT GOOD ENOUGH
            try {
                Method writeReplace = baseType.getDeclaredMethod( "writeReplace" );

                return writeReplace != null;

            } catch( NoSuchMethodException e ) {
                return false;
            }

        }
        return false;
    }

    public static SerializedLambda serializeLambda( Object object ) {
        try {
            Method writeReplace = object.getClass().getDeclaredMethod( "writeReplace" );

            boolean wasAcc = writeReplace.isAccessible();

            try {
                writeReplace.setAccessible( true );

                return (SerializedLambda) writeReplace.invoke( object );

            } catch( Exception e ) {

                throw new SMTRuntimeException( "Unable to get SerializedLambda from " + object.getClass().getName() );

            }

        } catch( ReflectiveOperationException e ) {
            return null;
        }
    }

    public static Method findMethod( Class<?> clazz, String methodName ) {
        for ( Method method : clazz.getMethods() ) {
            if ( method.getName().equals( methodName ) ) {
                return method;
            }
        }
        return null;
    }

    public static Object getParent( final Object obj ) {
        Object parent = null;

        if ( obj != null ) {
            Class<?> aClass = obj.getClass();

            if ( aClass.getName().contains( "$" ) ) { // object is nested class

                try {
                    Field field = aClass.getDeclaredField( "this$0" );
                    field.setAccessible( true );

                    parent = field.get( obj );

                } catch( NoSuchFieldException e ) {
                    /* ignore */
                } catch( IllegalAccessException e ) {
                    throw new SMTRuntimeException( e.getMessage(), e ); // wont happen
                }
            }
        }

        return parent;
    }

    public static Object deserialiseLambda( SerializedLambda lambda ) throws Throwable {
        Class<?>             capturingClass = getClass( lambda.getCapturingClass().replace( '/', '.' ) );
        Class<?>             implClass      = getClass( lambda.getImplClass().replace( '/', '.' ) );
        Class<?>             interfaceType  = getClass( lambda.getFunctionalInterfaceClass().replace( '/', '.' ) );
        MethodHandles.Lookup lookup         = getLookup( implClass );
        MethodType           implType       = MethodType.fromMethodDescriptorString( lambda.getImplMethodSignature(), null );
        MethodType           samType        = MethodType.fromMethodDescriptorString( lambda.getFunctionalInterfaceMethodSignature(), null );

        MethodHandle implMethod;
        boolean      implIsInstanceMethod = true;
        switch( lambda.getImplMethodKind() ) {
        case MethodHandleInfo.REF_invokeInterface:
        case MethodHandleInfo.REF_invokeVirtual:
            implMethod = lookup.findVirtual( implClass, lambda.getImplMethodName(), implType );
            break;
        case MethodHandleInfo.REF_invokeSpecial:
            implMethod = lookup.findSpecial( implClass, lambda.getImplMethodName(), implType, implClass );
            break;
        case MethodHandleInfo.REF_invokeStatic:
            implMethod = lookup.findStatic( implClass, lambda.getImplMethodName(), implType );
            implIsInstanceMethod = false;
            break;
        default:
            throw new RuntimeException( "Unsupported impl method kind " + lambda.getImplMethodKind() );
        }

        // determine type of factory
        final Class<?>[] ptypes      = Arrays.copyOf( implType.parameterArray(), implType.parameterCount() - samType.parameterCount() );
        MethodType       factoryType = MethodType.methodType( interfaceType, ptypes );
        if ( implIsInstanceMethod ) {
            factoryType = factoryType.insertParameterTypes( 0, implClass );
        }

        // call factory
        CallSite callSite = LambdaMetafactory.altMetafactory( lookup,
                                                              lambda.getFunctionalInterfaceMethodName(), factoryType, samType, implMethod, implType,
                                                              1 );

        // invoke callsite
        Object[] capturedArgs = new Object[ lambda.getCapturedArgCount() ];
        for ( int i = 0; i < lambda.getCapturedArgCount(); i++ ) {
            capturedArgs[ i ] = lambda.getCapturedArg( i );
        }
        return callSite.dynamicInvoker().invokeWithArguments( capturedArgs );
    }

    private static MethodHandles.Lookup getLookup( Class<?> owner )
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return _lookupConstructor.newInstance( owner, 15 );
    }

    private static void setFinal( final Field field, final boolean setFinal ) {
        try {
            if ( setFinal ) {
                int   modifiers     = field.getModifiers();
                Field modifierField = field.getClass().getDeclaredField( "modifiers" );
                modifiers = modifiers | Modifier.FINAL;
                modifierField.setAccessible( true );
                modifierField.setInt( field, modifiers );
            } else {
                int   modifiers     = field.getModifiers();
                Field modifierField = field.getClass().getDeclaredField( "modifiers" );
                modifiers = modifiers & ~Modifier.FINAL;
                modifierField.setAccessible( true );
                modifierField.setInt( field, modifiers );
            }
        } catch( Exception e ) {
            throw new SMTRuntimeException( "ReflectUtils unable to setFinal modifier on field " + field.getName() );
        }
    }

    private static Field doGetMember( final Class clazz, String field ) {
        if ( field == null || field.length() == 0 ) return null;

        try {
            final Field f = clazz.getDeclaredField( field );

            f.setAccessible( true );

            return f;

        } catch( NoSuchFieldException e ) {

            if ( field.charAt( 0 ) != '_' ) {
                try {
                    return doGetMember( clazz, "_" + field );
                } catch( SMTRuntimeException e2 ) {
                    // swallow
                }
            }

            if ( clazz.getSuperclass() != null ) {

                Field f = doSilentGetMember( clazz.getSuperclass(), field );

                if ( f != null ) return f;
            }

            throw new SMTRuntimeException( "getMember()  className " + clazz.getSimpleName() + " member " + field + " not in class" );
        }
    }

    private static Field doSilentGetMember( final Class clazz, String field ) {
        try {
            return clazz.getDeclaredField( field );

        } catch( NoSuchFieldException e ) {
            if ( clazz.getSuperclass() != null ) {

                Field f = null;
                try {
                    f = doSilentGetMember( clazz.getSuperclass(), field );
                } catch( Exception ex ) {
                    // ignore will throw below
                }

                if ( f != null ) return f;
            }

            return null;
        }
    }

    public static Object[] makeArgs( final String[] types, final Object[] params ) {

        Object[] retArgs = new Object[ params.length ];

        if ( types.length != params.length ) throw new RuntimeException( "Mismatch on supplied params need " + types.length + ", got " + params.length );

        for ( int i = 0; i < params.length; i++ ) {

            try {
                final Class<?> type = ReflectUtils.getClassCheckForPrimitive( types[ i ] );

                retArgs[ i ] = convertArg( type, params[ i ] );

            } catch( Exception e ) {
                throw new RuntimeException( "Unable to convert " + types[ i ] + " " + params[ i ].toString() + " required in arg list " + Arrays.toString( types ) );
            }
        }

        return retArgs;
    }

    public static void changeRefs( final Object objToCheck, final Object oldRef, final Object newRef ) {
        Set<Field> fields   = ReflectUtils.getMembers( objToCheck );
        Class<?>   srcClass = oldRef.getClass();

        if ( oldRef == null ) {
            throw new SMTRuntimeException( "changeRefs requires a real ref to change not null" );
        }

        if ( newRef != null && !oldRef.getClass().isAssignableFrom( newRef.getClass() ) ) {
            throw new SMTRuntimeException( "changeRefs cannot change instance of " + oldRef.getClass().getName() + " to " + newRef.getClass().getName() );
        }

        for ( Field field : fields ) {
            int modifiers = field.getModifiers();

            if ( Modifier.isStatic( modifiers ) ) continue;

            try {

                field.setAccessible( true );

                Object srcFieldVal = field.get( objToCheck );

                if ( srcFieldVal == oldRef ) {

                    field.set( objToCheck, newRef );
                }

            } catch( Exception e ) {
                // dont care
            }
        }
    }

    public static void invokeAnnotatedMethod( Object obj, Class<? extends Annotation> annotatedClass ) {

        if ( obj == null || annotatedClass == null ) return;

        Class clazz = obj.getClass();

        while( clazz != null ) {

            for ( Method method : clazz.getDeclaredMethods() ) {
                if ( method.isAnnotationPresent( annotatedClass ) ) {

                    final String idStr = (obj instanceof Identifiable) ? ((Identifiable) obj).id() : "";

                    _log.info( "invokeAnnotatedMethod: " + annotatedClass.getName() + " on " + clazz.getName() + " " + idStr );

                    ReflectUtils.invoke( method, obj );
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void invokeAnnotatedMethod( Object obj, Class<? extends Annotation> annotatedClass, Class[] argClasses, Object[] args ) {

        if ( obj == null || annotatedClass == null ) return;

        Class clazz = obj.getClass();

        while( clazz != null ) {

            for ( Method method : clazz.getDeclaredMethods() ) {
                if ( method.isAnnotationPresent( annotatedClass ) ) {

                    if ( matchArgs( argClasses, method.getParameterTypes() ) ) {

                        final String idStr = (obj instanceof Identifiable) ? ((Identifiable) obj).id() : "";

                        _log.info( "invokeAnnotatedMethod: " + annotatedClass.getName() + " on " + clazz.getName() + " " + idStr );

                        ReflectUtils.invoke( method, obj, args );
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * @param argClasses
     * @param parameterTypes
     * @return true if the arguments can be used given the method params
     */
    public static boolean matchArgs( final Class[] argClasses, final Class<?>[] parameterTypes ) {
        if ( argClasses.length != parameterTypes.length ) return false;

        for ( int idx = 0; idx < argClasses.length; ++idx ) {
            Class<?> argC = argClasses[ idx ];
            Class<?> pC   = parameterTypes[ idx ];

            if ( !pC.isAssignableFrom( argC ) ) {
                return false;
            }
        }

        return true;
    }

    public static void setValDblResetSrc( final String field, final Object from, final Object to, ZFunction2Args<Double, Double, Boolean> cmpFunc, double replaceVal ) {
        if ( from == null || to == null || from.getClass() != to.getClass() ) throw new SMTRuntimeException( "setDblValIfNull " + field + " not possible as objects not same or null" );

        Class<?> fromClass = from.getClass();

        final Field fld = getMember( fromClass, field );

        if ( fld == null ) throw new SMTRuntimeException( "setDblValIfNull field " + field + " not in class " + fromClass.getName() );

        try {

            double fromVal = fld.getDouble( from );
            double toVal   = fld.getDouble( to );

            if ( Utils.isNull( fromVal ) || Utils.isNull( toVal ) ) {

                // nothing to do

            } else if ( cmpFunc.apply( fromVal, toVal ) ) {
                fld.setDouble( to, fromVal );
            }

            fld.setDouble( from, replaceVal );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "setDblValIfNull field " + field + " class " + fromClass.getName() + " exception : " + e.getMessage(), e );
        }
    }

    public static void setDblValIfNullResetSrc( final String field, final Object from, final Object to ) {
        if ( from == null || to == null || from.getClass() != to.getClass() ) throw new SMTRuntimeException( "setDblValIfNull " + field + " not possible as objects not same or null" );

        Class<?> fromClass = from.getClass();

        final Field fld = getMember( fromClass, field );

        if ( fld == null ) throw new SMTRuntimeException( "setDblValIfNull field " + field + " not in class " + fromClass.getName() );

        try {

            double fromVal = fld.getDouble( from );
            double toVal   = fld.getDouble( to );

            if ( Utils.isNull( fromVal ) ) {
                // nothing to do
            } else if ( Utils.isZero( fromVal ) ) { // if was zero and is null ... set to zero
                if ( Utils.isNull( toVal ) ) {
                    fld.setDouble( to, 0.0 );
                }
            } else { // fromVal has value

                if ( Utils.isNull( toVal ) ) {
                    fld.setDouble( to, fromVal );
                }
            }

            fld.setDouble( from, Constants.UNSET_DOUBLE );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "setDblValIfNull field " + field + " class " + fromClass.getName() + " exception : " + e.getMessage(), e );
        }
    }

    public static void moveAppendValDbl( final String field, final Object from, final Object to ) {
        if ( from == null || to == null || from.getClass() != to.getClass() ) throw new SMTRuntimeException( "moveAppendValDbl " + field + " not possible as objects not same or null" );

        Class<?> fromClass = from.getClass();

        final Field fld = getMember( fromClass, field );

        if ( fld == null ) throw new SMTRuntimeException( "moveAppendValDbl field " + field + " not in class " + fromClass.getName() );

        try {

            double fromVal = fld.getDouble( from );
            double toVal   = fld.getDouble( to );

            if ( Utils.isNull( fromVal ) ) {
                // nothing to do
            } else if ( Utils.isZero( fromVal ) ) { // if was zero and is null ... set to zero
                if ( Utils.isNull( toVal ) ) {
                    fld.setDouble( to, 0.0 );
                }
            } else { // fromVal has value

                if ( Utils.isNullOrZero( toVal ) ) {
                    fld.setDouble( to, fromVal );
                } else {
                    double newVal = fromVal + toVal;

                    fld.setDouble( to, newVal );
                }
            }

            fld.setDouble( from, 0 );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "moveAppendValDbl field " + field + " class " + fromClass.getName() + " exception : " + e.getMessage(), e );
        }
    }

    public static void moveAppendValInt( final String field, final Object from, final Object to ) {
        if ( from == null || to == null || from.getClass() != to.getClass() ) throw new SMTRuntimeException( "moveAppendValInt " + field + " not possible as objects not same or null" );

        Class<?> fromClass = from.getClass();

        final Field fld = getMember( fromClass, field );

        if ( fld == null ) throw new SMTRuntimeException( "moveAppendValDbl field " + field + " not in class " + fromClass.getName() );

        try {

            int fromVal = fld.getInt( from );
            int toVal   = fld.getInt( to );

            if ( Utils.isNull( fromVal ) ) {
                // nothing to do
            } else if ( Utils.isZero( fromVal ) ) { // if was zero and is null ... set to zero
                if ( Utils.isNull( toVal ) ) {
                    fld.setInt( to, 0 );
                }
            } else { // fromVal has value

                if ( Utils.isNullOrZero( toVal ) ) {
                    fld.setInt( to, fromVal );
                } else {
                    int newVal = fromVal + toVal;

                    fld.setInt( to, newVal );
                }
            }

            fld.setInt( from, 0 );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "moveAppendValInt field " + field + " class " + fromClass.getName() + " exception : " + e.getMessage(), e );
        }
    }

    public static void moveAppendValLong( final String field, final Object from, final Object to ) {
        if ( from == null || to == null || from.getClass() != to.getClass() ) throw new SMTRuntimeException( "moveAppendValLong " + field + " not possible as objects not same or null" );

        Class<?> fromClass = from.getClass();

        final Field fld = getMember( fromClass, field );

        if ( fld == null ) throw new SMTRuntimeException( "moveAppendValDbl field " + field + " not in class " + fromClass.getName() );

        try {

            long fromVal = fld.getLong( from );
            long toVal   = fld.getLong( to );

            if ( Utils.isNull( fromVal ) ) {
                // nothing to do
            } else if ( Utils.isZero( fromVal ) ) { // if was zero and is null ... set to zero
                if ( Utils.isNull( toVal ) ) {
                    fld.setLong( to, 0 );
                }
            } else { // fromVal has value

                if ( Utils.isNullOrZero( toVal ) ) {
                    fld.setLong( to, fromVal );
                } else {
                    long newVal = fromVal + toVal;

                    fld.setLong( to, newVal );
                }
            }

            fld.setLong( from, Constants.UNSET_LONG );

        } catch( Exception e ) {
            throw new SMTRuntimeException( "moveAppendValLong field " + field + " class " + fromClass.getName() + " exception : " + e.getMessage(), e );
        }
    }
}
