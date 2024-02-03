package com.rr.core.recovery.json;

import com.rr.core.component.SMTSnapshotMember;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Identifiable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class JSONWriteSharedStateWithRefs implements JSONWriteSharedState {

    private static final Logger _log = LoggerFactory.create( JSONWriteSharedStateWithRefs.class );

    private static final ErrorCode MISSING = new ErrorCode( "JWS100", "Referenced object NOT written" );

    private static class WriteState {

        final int _jsonId;
        boolean _written = false;

        public WriteState( final int id ) { _jsonId = id; }
    }

    private final Map<Object, WriteState> _objectState = Collections.synchronizedMap( new IdentityHashMap<>( 128 ) );
    private final Set<SMTSnapshotMember>  _topLevelComponents;
    private       AtomicInteger           _nextObjId   = new AtomicInteger( 0 );

    public JSONWriteSharedStateWithRefs() { this( null ); }

    public JSONWriteSharedStateWithRefs( int startId ) {
        this( null );
        _nextObjId.set( startId );
    }

    public JSONWriteSharedStateWithRefs( final Set<SMTSnapshotMember> topLevelComponents ) {
        _topLevelComponents = topLevelComponents;
    }

    @Override public void reset() { reset( 0 ); }

    @Override public void reset( int startId ) {
        _objectState.clear();
        if ( _topLevelComponents != null ) _topLevelComponents.clear();
        _nextObjId.set( startId );
    }

    @Override public int getObjId( final Object obj, final boolean setAsWritten ) {
        WriteState w = getWriteState( obj );
        if ( setAsWritten ) {
            synchronized( w ) {
                w._written = true;
            }
        } else {
            // nothing DONT set _written to false !!!
        }
        return w._jsonId;
    }

    /**
     * @param obj
     * @return false if object already written. true id can write
     */
    @Override public boolean prepWrite( final Object obj ) {
        WriteState w = getWriteState( obj );

        synchronized( w ) {
            if ( w._written ) {
                return false;
            }

            w._written = true;
        }

        return true;
    }

    @Override public void checkMissing( Set<Object> missing, final JSONClassDefinitionCache cache ) {
        for ( Map.Entry<Object, WriteState> s : _objectState.entrySet() ) {
            final Object obj = s.getKey();

            if ( !s.getValue()._written ) {
                JSONClassCodec codec = cache.getCustomCodec( null, obj.getClass() );

                if ( codec != null ) {
                    if ( codec.checkWritten() ) {

                        missing.add( obj );

                        if ( obj instanceof Identifiable ) {
                            _log.error( MISSING, " jsonId=" + s.getValue()._jsonId + "  compId=" + ((Identifiable) obj).id() + " not written" );
                        } else {
                            _log.error( MISSING, " jsonId=" + s.getValue()._jsonId + "  class=" + obj.getClass().getSimpleName() + " not written" );
                        }
                    }

                } else {
                    String details = (obj instanceof Identifiable) ? ((Identifiable) obj).id() : JSONUtils.objectToJSON( obj );

                    _log.error( MISSING, " jsonId=" + s.getValue()._jsonId + " not written : " + obj );

                    codec = cache.getCustomCodec( null, obj.getClass() );

                    missing.add( obj );
                }
            }
        }
    }

    /**
     * @param obj
     * @return if the object is a top level component .. ie one that should not be recursed into
     */
    @Override public boolean forceReference( final Object obj ) {
        if ( obj instanceof SMTSnapshotMember ) {

            if ( _topLevelComponents != null && _topLevelComponents.contains( obj ) ) {
                SMTSnapshotMember mem = (SMTSnapshotMember) obj;

                if ( !mem.shouldEmbed() ) {
                    return true;
                }

            }
        }
        return false;
    }

//    private WriteState getWriteState( final Object key ) {
//        return _objectState.computeIfAbsent(key, k -> new WriteState( _nextObjId.incrementAndGet() ) );
//    }

    private synchronized WriteState getWriteState( final Object key ) {

        WriteState s = _objectState.get( key );

        if ( s != null ) return s;

        s = new WriteState( _nextObjId.incrementAndGet() );

//        _log.info( "WriteState jsonId=" + s._jsonId + " for " + key.getClass().getSimpleName() );

        _objectState.put( key, s );

        return s;
    }

}
