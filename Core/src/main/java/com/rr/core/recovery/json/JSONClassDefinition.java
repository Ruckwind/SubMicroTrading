package com.rr.core.recovery.json;

import com.rr.core.annotations.Persist;
import com.rr.core.annotations.PostRestoreObject;
import com.rr.core.annotations.TimestampMS;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class JSONClassDefinition {

    public static final char SHADOW_DELIM = '.';

    public static final Logger _log = LoggerFactory.create( JSONClassDefinition.class );

    public static final class FieldEntry {

        @Persist private final Field               _field;                 // reflective field entry
        @Persist private final JSONFieldType       _fieldType;             // primitive type or Object for an object
        @Persist private final Class<?>            _fieldClass;
        @Persist private final boolean             _isArray;
        @Persist private final JSONClassDefinition _objectDef;             // null if field is primitive type
        @Persist private final boolean             _hasPersistAnnotation;
        @Persist private final boolean             _hadPermission;
        @Persist private final JSONClassCodec      _customCodec;
        @Persist private final boolean             _isForceSMTRef;
        @Persist private final boolean             _isEncodeAsStringTimestamp;
        @Persist private final int                 _depth;
        @Persist private final boolean             _shadowed;
        @Persist private final boolean             _isExportable;

        public FieldEntry( JSONFieldType fieldType, JSONClassDefinition objectDef, Field field, boolean hadPermission, JSONClassCodec customCodec, Class<?> fieldClass, int depth, boolean shadowed ) {

            final Persist     persistAnnotation   = field.getAnnotation( Persist.class );
            final TimestampMS timestampAnnotation = field.getAnnotation( TimestampMS.class );

            _fieldType                 = fieldType;
            _objectDef                 = objectDef;
            _field                     = field;
            _hasPersistAnnotation      = persistAnnotation != null;
            _hadPermission             = hadPermission;
            _customCodec               = customCodec;
            _fieldClass                = fieldClass;
            _isArray                   = fieldClass.isArray();
            _isForceSMTRef             = (persistAnnotation != null) ? persistAnnotation.forceSMTRef() : false;
            _isEncodeAsStringTimestamp = (timestampAnnotation != null) ? timestampAnnotation.encodeAsString() : false;
            _depth                     = depth;
            _shadowed                  = shadowed;
            _isExportable              = (persistAnnotation != null) ? !persistAnnotation.nonExportable() : true;
        }

        public String toString()                  { return _fieldClass.getSimpleName() + ":" + _field.getName(); }

        public boolean encodeAsStringTimestamp()  { return _isEncodeAsStringTimestamp; }

        public JSONClassCodec getCustomCodec()    { return _customCodec; }

        public int getDepth()                     { return _depth; }

        public Field getField()                   { return _field; }

        public Class<?> getFieldClass()           { return _fieldClass; }

        public String getFieldName() {
            return (isShadowed() && getDepth() > 0) ? getField().getDeclaringClass().getName() + SHADOW_DELIM + getField().getName() : getField().getName();
        }

        public JSONFieldType getFieldType()       { return _fieldType; }

        public JSONClassDefinition getObjectDef() { return _objectDef; }

        public boolean isArray()                  { return _isArray; }

        public boolean isExportable()             { return _isExportable; }

        public boolean isForceSMTRef()            { return _isForceSMTRef; }

        public boolean isHadPermission()          { return _hadPermission; }

        public boolean isPersistAnnotation()      { return _hasPersistAnnotation; }

        public boolean isShadowed()               { return _shadowed; }
    }

    private transient Map<ZString, FieldEntry> _fieldLookup = null; // lazy init

    private Class<?>            _targetClass;
    /**
     * array of field entries for the targetClass, or if targetClass is an array the field entries for the array componentType
     */
    private FieldEntry[]        _entries;
    private FieldEntry          _innerClassesParent; // this$0 field
    private boolean             _isArray;
    private JSONClassDefinition _componentJCD;
    private int                 _classDefId;
    private boolean             _isInnerClass = false;
    private boolean             _embeddedClass;
    private List<Method>        _postRestoreMethods;

    private transient JSONClassCodec _topLevelCustomCodec;

    JSONClassDefinition( Class<?> aTargetClass ) {
        _targetClass = aTargetClass;
    }

    @Override public String toString() {
        return "JSONClassDefinition{" +
               " _classDefId=" + _classDefId +
               ", _targetClass=" + _targetClass +
               ", _entries=" + Arrays.toString( _entries ) +
               '}';
    }

    public int getClassDefId() {
        return _classDefId;
    }

    public JSONClassDefinition getComponentJCD() {
        return _componentJCD;
    }

    public FieldEntry[] getEntries() {
        return _entries;
    }

    public FieldEntry getField( final ReusableString fieldName ) {

        if ( _fieldLookup == null ) _fieldLookup = createFieldLookup();

        return _fieldLookup.get( fieldName );
    }

    public FieldEntry getInnerClassParent()     { return _innerClassesParent; }

    public List<Method> getPostRestoreMethods() { return _postRestoreMethods; }

    public Class<?> getTargetClass() {
        return _targetClass;
    }

    public JSONClassCodec getTopLevelCustomCodec() { return _topLevelCustomCodec; }

    public boolean hasRestoreMethods()          { return _postRestoreMethods != null; }

    public void init( JSONClassDefinitionCache cache, final int classDefId ) {

        Class clazz = _targetClass;

        _classDefId = classDefId;

        _isArray = clazz.isArray();

        if ( _isArray ) {
            Class elementClass = clazz.getComponentType();
            _componentJCD = cache.getDefinition( elementClass );
            _entries      = new FieldEntry[ 0 ];
            return;
        }

        if ( clazz.getName().contains( "$" ) ) {
            _embeddedClass = true;
        }

        _topLevelCustomCodec = cache.getCustomCodec( null, clazz );

        if ( _topLevelCustomCodec == null || !_topLevelCustomCodec.useCodec( null, null, true ) ) {
            ArrayList<FieldEntry> jsonFieldEntries = new ArrayList<>();

            int depth = 0;

            while( clazz != null ) {
                Field[] fields = clazz.getDeclaredFields();

                for ( Field f : fields ) {
                    Class<?> fieldClass = f.getType();

                    if ( _embeddedClass && f.getName().equals( "this$0" ) ) {

                        JSONFieldType ft = JSONFieldType.getVal( fieldClass );

                        JSONClassDefinition objectDef = getObjectDef( cache, fieldClass, ft, null );

                        FieldEntry e = new FieldEntry( ft, objectDef, f, f.isAccessible(), null, fieldClass, depth, false );

                        f.setAccessible( true );

                        _innerClassesParent = e;

                        _isInnerClass = true;

                    } else if ( shouldAddField( f, fieldClass, cache ) ) {

                        JSONFieldType ft = JSONFieldType.getVal( fieldClass );

                        JSONClassCodec customCodec = cache.getCustomCodec( ft, fieldClass );

                        JSONClassDefinition objectDef = getObjectDef( cache, fieldClass, ft, customCodec );

                        boolean nameDuplicated = false;

                        for ( FieldEntry fe : jsonFieldEntries ) {
                            if ( fe.getField().getName().equals( f.getName() ) ) {
                                nameDuplicated = true;
                                break;
                            }
                        }

                        FieldEntry e = new FieldEntry( ft, objectDef, f, f.isAccessible(), customCodec, fieldClass, depth, nameDuplicated );

                        f.setAccessible( true ); // MUST BE RESET AFTER ENCODING COMPLETED

                        jsonFieldEntries.add( e );
                    }
                }

                for ( Method method : clazz.getDeclaredMethods() ) {
                    if ( method.isAnnotationPresent( PostRestoreObject.class ) ) {
                        method.setAccessible( true );

                        if ( _postRestoreMethods == null ) _postRestoreMethods = new ArrayList<>();

                        _postRestoreMethods.add( method );
                    }
                }

                clazz = clazz.getSuperclass();
                ++depth;
            }
            _entries = new FieldEntry[ jsonFieldEntries.size() ];
            jsonFieldEntries.toArray( _entries );
        } else {
            _entries = new FieldEntry[ 0 ];
        }
    }

    public boolean isArray() {
        return _isArray;
    }

    public boolean isInnerClass()       { return _embeddedClass && _isInnerClass; }

    public boolean isStaticInnerClass() { return _embeddedClass && !_isInnerClass; }

    private Map<ZString, FieldEntry> createFieldLookup() {
        Map<ZString, FieldEntry> map = new HashMap<>( _entries.length );
        for ( FieldEntry f : _entries ) {
            map.put( new ViewString( f.getFieldName() ), f );
        }
        return map;
    }

    private JSONClassDefinition getObjectDef( final JSONClassDefinitionCache cache, final Class<?> fieldClass, final JSONFieldType ft, final JSONClassCodec customCodec ) {

        if ( customCodec != null ) return null; // dont need

        if ( fieldClass.isPrimitive() ) return null; // dont need

        if ( fieldClass.isInterface() ) return null; // dont need

        if ( fieldClass.isEnum() ) return null; // dont need

        if ( ft == JSONFieldType.Object ) {
            return cache.getDefinition( fieldClass );
        }

        return null;
    }

    private boolean shouldAddField( final Field f, final Class<?> fieldClass, final JSONClassDefinitionCache cache ) {

        boolean hasPersistAnnotation = (f.getAnnotation( Persist.class ) != null);

        if ( hasPersistAnnotation ) return true;

        boolean isTransient = Modifier.isTransient( f.getModifiers() );
        boolean isStatic    = Modifier.isStatic( f.getModifiers() );

        if ( isTransient ) return false;

        if ( isStatic ) return false;

        if ( cache.filterClass( fieldClass ) ) return false;

        return true;
    }
}
