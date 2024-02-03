package com.rr.core.recovery;

import com.rr.core.annotations.Persist;
import com.rr.core.component.SMTNonExportable;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZFunction;
import com.rr.core.recovery.json.Export;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * export container allows for all the data for a BT component (eg strategy) to be contained in single unit for use by JSON writer
 */
public class ExportContainer {

    private           String              _idOfExportComponent;
    private           Map<String, Object> _exportVals = new LinkedHashMap<>();
    private transient Set<Class>          _filterClasses;

    public ExportContainer()                                               { /* nothing */ }

    public <T extends Annotation> void addAnnotatedFields( final Object src, Class<T> annotationClass, ZFunction<T, Boolean> filter ) {

        if ( src == null ) return;

        Class clazz = src.getClass();

        if ( src instanceof SMTNonExportable ) {
            return;
        }

        while( clazz != null ) {
            Field[] fields = clazz.getDeclaredFields();

            for ( Field f : fields ) {
                Class<?> fieldClass = f.getType();

                boolean hasAnnotation = f.isAnnotationPresent( annotationClass );

                if ( hasAnnotation ) {

                    if ( filter != null ) {
                        final T annotation = f.getAnnotation( annotationClass );

                        if ( filter.apply( annotation ) ) {

                            continue;
                        }
                    }

                    final String fieldKey = makeKey( clazz, f );

                    Object fVal = ReflectUtils.get( f, src );

                    if ( !filter( fVal ) ) {
                        Object prev = _exportVals.putIfAbsent( fieldKey, fVal );

                        if ( prev != null && fVal != prev ) {
                            throw new SMTRuntimeException( "addAnnotatedFields() multiple conflicting values for same key field " + fieldKey );
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * add to export container all fields with the Export annotation
     *
     * @param src
     */
    public void addExportableFields( final Object src ) { addAnnotatedFields( src, Export.class, null ); }

    /**
     * add to export container all fields with the Persist annotation
     *
     * @param src
     */
    public void addPersistableFields( final Object src ) {
        ZFunction<Persist, Boolean> filter = ( f ) -> f.nonExportable();
        addAnnotatedFields( src, Persist.class, filter );
    }

    public <T> T get( String key ) { return (T) _exportVals.get( key ); }

    public byte getByte( String key ) {
        Byte d = (Byte) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_BYTE : d.byteValue();
    }

    public double getDouble( String key ) {
        Double d = (Double) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_DOUBLE : d.doubleValue();
    }

    public float getFloat( String key ) {
        Float d = (Float) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_FLOAT : d.floatValue();
    }

    public String getIdOfExportComponent()                                 { return _idOfExportComponent; }

    public void setIdOfExportComponent( final String idOfExportComponent ) { _idOfExportComponent = idOfExportComponent; }

    public int getInt( String key ) {
        Integer d = (Integer) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_INT : d.intValue();
    }

    public long getLong( String key ) {
        Long d = (Long) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_LONG : d.longValue();
    }

    public short getShort( String key ) {
        Short d = (Short) _exportVals.get( key );

        return (d == null) ? Constants.UNSET_SHORT : d.shortValue();
    }

    public void put( String key, Object value ) {

        Object mapVal = _exportVals.putIfAbsent( key, value );

        if ( mapVal != value && mapVal != null ) {
            throw new SMTRuntimeException( "ExortContainer " + _idOfExportComponent + " detected duplicate key of " + key +
                                           " with val class " + mapVal.getClass().getSimpleName() + ", newVal class " + value.getClass().getSimpleName() );
        }
    }

    public void reflectivelyImportAnnotatedFields( final Object dest, Class<? extends Annotation> annotationClass ) {
        if ( dest == null ) return;

        Class clazz = dest.getClass();

        while( clazz != null ) {
            Field[] fields = clazz.getDeclaredFields();

            for ( Field f : fields ) {
                Class<?> fieldClass = f.getType();

                boolean hasAnnotation = f.isAnnotationPresent( annotationClass );

                if ( hasAnnotation ) {

                    final String fieldKey = makeKey( clazz, f );

                    if ( _exportVals.containsKey( fieldKey ) ) {
                        Object fVal = _exportVals.get( fieldKey );

                        if ( fVal != null ) {
                            ReflectUtils.setMember( dest, f, fVal );
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void reflectivelyImportExportFields( final Object dest )    { reflectivelyImportAnnotatedFields( dest, Export.class ); }

    public void reflectivelyImportPersistedFields( final Object dest ) { reflectivelyImportAnnotatedFields( dest, Persist.class ); }

    public void setFilterClasses( final Set<Class> filterClasses ) {
        _filterClasses = filterClasses;
    }

    private boolean filter( final Object obj ) {
        if ( obj == null ) return false;
        boolean filter = false;
        if ( _filterClasses != null ) {
            filter = _filterClasses.contains( obj.getClass() );
        }
        return filter;
    }

    private String makeKey( final Class clazz, final Field f ) {
        return clazz.getName() + "/" + f.getName();
    }
}
