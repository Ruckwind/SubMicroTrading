package com.rr.core.component;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZConsumer;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.SMTRuntimeException;

import java.util.*;

public class SMTComponentManager implements SMTComponent {

    public static EnumSet<CreationPhase> CREATION_TIME_PRE_RUNTIME = EnumSet.of( CreationPhase.Config, CreationPhase.Initialisation, CreationPhase.Recovery );
    private final Logger _log = ConsoleFactory.console( SMTComponentManager.class, Level.info );
    /**
     * component map, map keyed by LOWERCASE componentId
     */
    private final Map<String, SMTComponent> _components = new LinkedHashMap<>( 128 );
    /**
     * ordered map of components, with all a components dependencies appearing before it
     * map keyed by component, value is the startup phase the component was created in
     */
    private final LinkedHashMap<SMTComponent, CreationPhase> _orderedComponents = new LinkedHashMap<>( 128 );
    private final LinkedHashSet<SMTActiveWorker> _activeComponents = new LinkedHashSet<>( 128 );
    private final ReusableString _logMsg = new ReusableString();
    private final String _id;
    private CreationPhase _createPhase = CreationPhase.Config;

    public SMTComponentManager()            { this( "componentManager" ); }

    public SMTComponentManager( String id ) { _id = id; }

    public SMTComponentManager( final SMTComponentManager componentManager ) {
        _id = (componentManager != null) ? componentManager._id : "componentManager";
        if ( componentManager != null ) {
            for ( SMTComponent c : componentManager._orderedComponents.keySet() ) {
                add( c );
            }
        }
    }

    @Override public String getComponentId() { return _id; }

    @Override
    public String toString() {
        ReusableString s = new ReusableString( 8192 );

        s.copy( "SMTComponentManager : " );

        synchronized( _orderedComponents ) {
            for ( SMTComponent c : _orderedComponents.keySet() ) {
                if ( c != this ) {
                    String lc = c.getComponentId();

                    s.append( lc ).append( " : " ).append( c.toString() ).append( "\n" );
                }
            }
        }

        return s.toString();
    }

    public void add( final SMTComponent c ) {
        add( c, _createPhase );
    }

    public void add( final SMTComponent c, CreationPhase phase ) {

        if ( c == null ) return;

        final SMTComponent prev = _components.putIfAbsent( c.getComponentId().toLowerCase(), c );

        if ( prev != null ) {
            if ( prev == c ) {
                return; // nothing to do
            }
            throw new SMTRuntimeException( "SMTComponentManager duplicate component id=" + c.getComponentId() );
        }

        _orderedComponents.put( c, phase );

        if ( c instanceof SMTActiveWorker ) {
            _activeComponents.add( (SMTActiveWorker) c );
        }
    }

    public boolean anyComponentsActive() {
        synchronized( _activeComponents ) {
            for ( SMTActiveWorker c : _activeComponents ) {
                if ( c.hasOutstandingWork() ) {
                    if ( _log.isEnabledFor( Level.trace ) ) {
                        _logMsg.copy( "Active component " ).append( c.getComponentId() );
                        _log.log( Level.trace, _logMsg );
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public void forEach( ZConsumer<SMTComponent> consumer ) {
        _orderedComponents.forEach( ( c, a ) -> consumer.accept( c ) );
    }

    @SuppressWarnings( { "unchecked" } )
    public <T extends SMTComponent> T getComponent( String id ) {

        if ( id == null || id.equalsIgnoreCase( "null" ) || id.length() == 0 ) return null;

        SMTComponent c;
        synchronized( _components ) {
            c = _components.get( id.toLowerCase() );
        }

        if ( c == null ) {
            throw new SMTRuntimeException( "Attempt to reference unregistered component, id=" + id + ", check define referenced components first" );
        }

        return ((T) c);
    }

    public CreationPhase getComponentCreationPhase( final SMTComponent component ) {

        return _orderedComponents.get( component );
    }

    /**
     * @return the components map ... sync on the map before use
     */
    public void getComponentMap( EnumSet<CreationPhase> timeSet, Map<String, SMTComponent> dest ) {
        for ( Map.Entry<SMTComponent, CreationPhase> e : _orderedComponents.entrySet() ) {
            CreationPhase eTime = e.getValue();

            if ( timeSet.contains( eTime ) ) {
                dest.put( e.getKey().getComponentId(), e.getKey() );
            }
        }
    }

    public <T extends SMTComponent> T getComponentOrNull( String id ) {

        if ( id == null || id.equalsIgnoreCase( "null" ) ) return null;

        SMTComponent c;
        synchronized( _components ) {
            c = _components.get( id.toLowerCase() );
        }

        if ( c == null ) {
            return null;
        }

        return ((T) c);
    }

    /**
     * @return the ordered components set ... sync on the set before use
     */
    public void getComponentSet( EnumSet<CreationPhase> timeSet, Set<SMTComponent> dest ) {
        for ( Map.Entry<SMTComponent, CreationPhase> e : _orderedComponents.entrySet() ) {
            CreationPhase eTime = e.getValue();

            if ( timeSet.contains( eTime ) ) {
                dest.add( e.getKey() );
            }
        }
    }

    public void getComponentSet( Set<SMTComponent> dest ) {
        dest.addAll( _orderedComponents.keySet() );
    }

    public CreationPhase getCreationPhase() {
        return _createPhase;
    }

    public void setCreationPhase( CreationPhase createTime ) {
        _createPhase = createTime;
    }

    public boolean hasComponent( final String id ) {
        synchronized( _components ) {
            return _components.containsKey( id.toLowerCase() );
        }
    }

    public void logComponentsActive() {
        synchronized( _activeComponents ) {
            for ( SMTActiveWorker c : _activeComponents ) {
                if ( c.hasOutstandingWork() ) {
                    _logMsg.copy( "Active component " ).append( c.getComponentId() );
                    _log.info( _logMsg );
                }
            }
        }
    }

    public void remove( final SMTComponent c ) {
        if ( c == null ) return;

        _components.remove( c.getComponentId().toLowerCase() );

        _orderedComponents.remove( c );

        if ( c instanceof SMTActiveWorker ) {
            _activeComponents.remove( c );
        }
    }

    public void replace( final SMTComponent oldComp, final SMTComponent newComp ) {
        remove( oldComp );
        add( newComp );
    }
}
