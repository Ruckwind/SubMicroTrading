package com.rr.core.recovery.json;

import com.rr.core.collections.EventQueue;
import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTStartContext;
import com.rr.core.recovery.json.custom.CustomJSONCodecs;
import com.rr.core.tasks.ZTimerTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JSONClassDefinitionCache {

    private static Set<String>   _ignoreClassesStartingWith = buildExclusions();
    private static Set<Class<?>> _ignoreClasses             = buildExclusionClasses();

    private final SMTStartContext _ctx;
    private Map<Class<?>, JSONClassDefinition> _jsonClassDefs = new ConcurrentHashMap<>();
    private int _nextClassDefId = 0;

    private static Set<String> buildExclusions() {
        Set<String> classExclusions = new HashSet<>();

        classExclusions.add( "java.net" );
        classExclusions.add( "java.security" );
        classExclusions.add( "java.lang.reflect" );

        return classExclusions;
    }

    private static Set<Class<?>> buildExclusionClasses() {
        Set<Class<?>> classExclusions = new HashSet<>();

        classExclusions.add( ZTimerTask.class );
        classExclusions.add( TimerTask.class );

        return classExclusions;
    }

    public static boolean filterClass( final Class<?> fieldClass ) {
        String name = fieldClass.getName();

        for ( String s : _ignoreClassesStartingWith ) {
            if ( name.startsWith( s ) ) return true;
        }

        for ( Class<?> c : _ignoreClasses ) {
            if ( c.isAssignableFrom( fieldClass ) ) {
                return true;
            }
        }

        return false;
    }

    public JSONClassDefinitionCache( final SMTStartContext ctx ) {
        _ctx = ctx;

        CustomJSONCodecs    jsonCustomCodecs = _ctx.getJSONCustomCodecs();
        SMTComponentManager componentManager = ctx.getComponentManager();

        if ( jsonCustomCodecs != null && componentManager != null ) {

            jsonCustomCodecs.checkInit( ctx, componentManager.getCreationPhase() ); // ENSURE JSON custom codecs are initialised

            getDefinition( JSONClassDefinition.class );
            getDefinition( JSONClassDefinitionCache.class );
        }
    }

    public SMTStartContext getContext() { return _ctx; }

    @SuppressWarnings( "unchecked" )
    public JSONClassCodec getCustomCodec( final JSONFieldType ft, final Class<?> fieldClass ) {

        CustomJSONCodecs jsonCustomCodecs = _ctx.getJSONCustomCodecs();

        JSONClassCodec customCodec = jsonCustomCodecs.get( fieldClass );

        if ( customCodec != null ) return customCodec;

        if ( ft != null ) {
            switch( ft ) {
            case Map:
                return jsonCustomCodecs.get( Map.class );
            case Collection:
                return jsonCustomCodecs.get( Collection.class );
            case MessageQueue:
                return jsonCustomCodecs.get( EventQueue.class );
            case Enum:
                synchronized( jsonCustomCodecs ) {
                    customCodec = jsonCustomCodecs.get( fieldClass );
                    if ( customCodec != null ) return customCodec;
                    customCodec = new CustomJSONCodecs.SpecificEnumJSONCodec( fieldClass );
                    jsonCustomCodecs.register( fieldClass, customCodec, false );
                    return customCodec;
                }
            }
        } else {
            if ( Enum.class.isAssignableFrom( fieldClass ) ) {
                synchronized( jsonCustomCodecs ) {
                    customCodec = jsonCustomCodecs.get( fieldClass );
                    if ( customCodec != null ) return customCodec;
                    customCodec = new CustomJSONCodecs.SpecificEnumJSONCodec( fieldClass );
                    jsonCustomCodecs.register( fieldClass, customCodec, false );
                    return customCodec;
                }
            }
        }

        return null;
    }

    public JSONClassDefinition getDefinition( Class<?> aClass ) {
        JSONClassDefinition def = _jsonClassDefs.get( aClass );

        if ( def != null ) return def;

        def = new JSONClassDefinition( aClass );

        final JSONClassDefinition old = _jsonClassDefs.putIfAbsent( aClass, def );

        if ( old == null ) {
            def.init( this, nextId() );
        }

        return (old == null) ? def : old;
    }

    public void resetPermissions( boolean forceAccess ) {
        for ( JSONClassDefinition c : _jsonClassDefs.values() ) {
            for ( JSONClassDefinition.FieldEntry f : c.getEntries() ) {
                if ( !f.isHadPermission() ) {
                    f.getField().setAccessible( forceAccess );
                }
            }
        }
    }

    public void setNextClassDefId( int nextId ) {
        _nextClassDefId = nextId;
    }

    private synchronized int nextId() {
        return ++_nextClassDefId;
    }
}
