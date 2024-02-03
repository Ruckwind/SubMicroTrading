package com.rr.model.generated.internal.events.recycle;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.SecurityDefinitionUpdateImpl;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.lang.Constants;
import com.rr.core.pool.RuntimePoolingException;
import com.rr.model.generated.internal.events.impl.SecDefEventImpl;
import com.rr.model.generated.internal.events.impl.SecDefLegImpl;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SDFeedTypeImpl;
import com.rr.core.pool.SuperpoolManager;

public class SecurityDefinitionUpdateRecycler implements Recycler<SecurityDefinitionUpdateImpl> {

    private SuperPool<SecurityDefinitionUpdateImpl> _superPool;


    private SecDefEventRecycler _eventsRecycler = SuperpoolManager.instance().getRecycler( SecDefEventRecycler.class, SecDefEventImpl.class );


    private SecDefLegRecycler _legsRecycler = SuperpoolManager.instance().getRecycler( SecDefLegRecycler.class, SecDefLegImpl.class );


    private SecurityAltIDRecycler _securityAltIDsRecycler = SuperpoolManager.instance().getRecycler( SecurityAltIDRecycler.class, SecurityAltIDImpl.class );


    private SDFeedTypeRecycler _SDFeedTypesRecycler = SuperpoolManager.instance().getRecycler( SDFeedTypeRecycler.class, SDFeedTypeImpl.class );

    private SecurityDefinitionUpdateImpl _root;

    private int          _recycleSize;
    private int          _count = 0;
    public SecurityDefinitionUpdateRecycler( int recycleSize, SuperPool<SecurityDefinitionUpdateImpl> superPool ) {
        _superPool = superPool;
        _recycleSize = recycleSize;
        try {
            _root    = SecurityDefinitionUpdateImpl.class.newInstance();
        } catch( Exception e ) {
            throw new RuntimePoolingException( "Unable to create recycle root for SecurityDefinitionUpdateImpl : " + e.getMessage(), e );
        }
    }


    @Override public void recycle( SecurityDefinitionUpdateImpl obj ) {
        if ( Constants.DISABLE_RECYCLING ) return;
        if ( obj == null ) return;
        if ( obj.getNext() == null ) {
            SecDefEventImpl events = (SecDefEventImpl) obj.getEvents();
            while ( events != null ) {
                SecDefEventImpl t = events;
                events = events.getNext();
                t.setNext( null );
                _eventsRecycler.recycle( t );
            }

            SecDefLegImpl legs = (SecDefLegImpl) obj.getLegs();
            while ( legs != null ) {
                SecDefLegImpl t = legs;
                legs = legs.getNext();
                t.setNext( null );
                _legsRecycler.recycle( t );
            }

            SecurityAltIDImpl securityAltIDs = (SecurityAltIDImpl) obj.getSecurityAltIDs();
            while ( securityAltIDs != null ) {
                SecurityAltIDImpl t = securityAltIDs;
                securityAltIDs = securityAltIDs.getNext();
                t.setNext( null );
                _securityAltIDsRecycler.recycle( t );
            }

            SDFeedTypeImpl SDFeedTypes = (SDFeedTypeImpl) obj.getSDFeedTypes();
            while ( SDFeedTypes != null ) {
                SDFeedTypeImpl t = SDFeedTypes;
                SDFeedTypes = SDFeedTypes.getNext();
                t.setNext( null );
                _SDFeedTypesRecycler.recycle( t );
            }

            obj.reset();
            obj.setNext( _root.getNext() );
            _root.setNext( obj );
            if ( ++_count == _recycleSize ) {
                _superPool.returnChain( _root.getNext() );
                _root.setNext( null );
                _count = 0;
            }
        }
    }
}
