package com.rr.core.recovery;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Identifiable;
import com.rr.core.properties.AppProps;
import com.rr.core.recovery.json.JSONException;
import com.rr.core.recovery.json.MissingRef;
import com.rr.core.recovery.json.MissingRefImpl;
import com.rr.core.recovery.json.Resolver;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * currently SMTComponentManager only manages statically created components
 */
public class SMTComponentResolver implements Resolver {

    private static final ErrorCode ERR_NOT_SMT_COMPONENT = new ErrorCode( "SCR100", "Object does not implement SMTComponent" );
    private static final ErrorCode MISSED_REFS           = new ErrorCode( "SCR200", "FATAL : On restore, no component found for refernce id ! " );
    private static Logger _log = LoggerFactory.create( SMTComponentResolver.class );
    private final SMTComponentManager _componentManager;
    private Map<Integer, Object>      _objectMap    = new ConcurrentHashMap<>( 128 );
    private Map<String, Identifiable> _componentMap = new ConcurrentHashMap<>( 128 );
    private Set<MissingRef>           _missingRefs  = Collections.synchronizedSet( new HashSet<>() );
    public SMTComponentResolver( final SMTComponentManager componentManager ) {
        _componentManager = componentManager;
    }

    @Override public void addMissingRef( final Object src, final String fieldName, final String missingComponentId ) {
        MissingRef ref = new MissingRefImpl( src, fieldName, missingComponentId );
        _missingRefs.add( ref );
    }

    @Override public void addMissingRef( final MissingRef ref ) {
        _missingRefs.add( ref );
    }

    @Override public void clear() {
        _objectMap.clear();
        _componentMap.clear();
    }

    @Override public Object fetch( final int jsonId ) { return _objectMap.get( jsonId ); }

    @Override public Object find( final ZString refId ) {
        Object res = null;

        if ( StringUtils.isNumber( refId ) ) {
            Integer id = StringUtils.parseInt( refId );

            res = _objectMap.get( id );

        } else {
            if ( res == null ) {
                res = findBySMTComponentId( refId );
            }
        }

        if ( res == null ) {
            final MissingRefImpl missingRef = new MissingRefImpl( refId.toString() );
            _missingRefs.add( missingRef );
            return missingRef;
        }

        return res;
    }

    @Override public Object findBySMTComponentId( final ZString smtId ) {
        String id = smtId.toString();
        Object o  = _componentManager.getComponentOrNull( id );

        if ( o != null ) return o;

        return _componentMap.get( id );
    }

    @Override public synchronized boolean resolveMissing() {

        Set<MissingRef> missedRefs = new LinkedHashSet<>();
        for ( MissingRef ref : _missingRefs ) {
            if ( StringUtils.isNumber( ref.getRefComponentId() ) ) {
                int jsonId = Integer.parseInt( ref.getRefComponentId() );

                Object requiredObj = _objectMap.get( jsonId );

                if ( requiredObj != null ) {
                    ref.resolve( requiredObj );
                } else {
                    missedRefs.add( ref );
                }

            } else {
                String compId       = ref.getRefComponentId();
                Object requiredComp = _componentManager.getComponentOrNull( compId );

                if ( requiredComp != null ) {
                    ref.resolve( requiredComp );
                } else {
                    missedRefs.add( ref );
                }
            }
        }

        for ( MissingRef ref : missedRefs ) {
            _log.error( MISSED_REFS, " " + ref.toString() );
        }

        if ( missedRefs.size() > 0 ) {
            if ( AppProps.instance().getBooleanProperty( "FORCE_SKIP_UNRESOLVED_PERSISTENT_FIELDS", false, false ) ) {

                _log.warn( "FORCE_SKIP_UNRESOLVED_PERSISTENT_FIELDS make sure you check all " + missedRefs.size() + " were ok to skip" );

            } else {
                Utils.exit( 101 );
            }
        }

        return missedRefs.size() == 0;
    }

    @Override public void store( final int jsonId, final Object obj, final boolean registerWithSMTCompMgr ) throws JSONException {

        if ( jsonId == 0 ) return;

        if ( obj == null ) {
            throw new SMTRuntimeException( "SMTComponentResolver storing a null against key " + jsonId );
        }

        Object prev = _objectMap.putIfAbsent( jsonId, obj );
        if ( prev != null && prev != obj ) {
            throw new JSONException( "Duplicate reference " + jsonId + ", existing=" + prev.getClass().getName() + ", new=" + obj.getClass().getName() );
        }

        if ( Identifiable.class.isAssignableFrom( obj.getClass() ) ) {
            final Identifiable zobj = (Identifiable) obj;
            String             id   = zobj.id();

            if ( id != null && id.length() > 0 ) {
                _componentMap.putIfAbsent( id, zobj );
            }
        }

        if ( registerWithSMTCompMgr ) {
            /**
             * register all SMTComponents with ComponentManager to ensure if Initialisable that it is initialised regardless of wether it was statically registered
             */
            if ( obj instanceof SMTComponent ) {
                SMTComponent newObj = (SMTComponent) obj;
                Object       cur    = _componentManager.getComponentOrNull( newObj.getComponentId() );
                if ( cur == null ) {
                    _componentManager.add( newObj, CreationPhase.Recovery );
                    _log.info( "SMTComponentResolver restored unregistered component " + newObj.getComponentId() );
                } else if ( cur != newObj ) {
                    throw new SMTRuntimeException( "SMTComponentResolver component restored with id " + newObj.getComponentId() + " is DIFFERENT instance to that in component manager" );
                }
            } else {
                if ( _log.isEnabledFor( Level.trace ) ) {
                    _log.log( Level.trace, "SMTComponentResolver " + obj.getClass().getName() + " unable to store in SMTComponentManager as not SMTComponent" );
                }
            }
        }
    }
}
