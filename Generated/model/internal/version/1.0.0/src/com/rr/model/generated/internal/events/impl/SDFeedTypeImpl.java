package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class SDFeedTypeImpl implements SDFeedType, Reusable<SDFeedTypeImpl>, Copyable<SDFeedType> {

   // Attrs

    private transient          SDFeedTypeImpl _next = null;
    private final ReusableString _feedType = new ReusableString( SizeType.INST_FEED_TYPE_LENGTH.getSize() );
    private int _marketDepth = Constants.UNSET_INT;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getFeedType() { return _feedType; }

    @Override public final void setFeedType( byte[] buf, int offset, int len ) { _feedType.setValue( buf, offset, len ); }
    @Override public final ReusableString getFeedTypeForUpdate() { return _feedType; }

    @Override public final int getMarketDepth() { return _marketDepth; }
    @Override public final void setMarketDepth( int val ) { _marketDepth = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _feedType.reset();
        _marketDepth = Constants.UNSET_INT;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SDFeedType;
    }

    @Override
    public final SDFeedTypeImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SDFeedTypeImpl nxt ) {
        _next = nxt;
    }


   // Helper methods
    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "SDFeedTypeImpl" ).append( ' ' );
        if ( getFeedType().length() > 0 )             out.append( ", feedType=" ).append( getFeedType() );
        if ( Constants.UNSET_INT != getMarketDepth() && 0 != getMarketDepth() )             out.append( ", marketDepth=" ).append( getMarketDepth() );
    }

    @Override public final void snapTo( SDFeedType dest ) {
        ((SDFeedTypeImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SDFeedType src ) {
        getFeedTypeForUpdate().copy( src.getFeedType() );
        setMarketDepth( src.getMarketDepth() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SDFeedType src ) {
        getFeedTypeForUpdate().copy( src.getFeedType() );
        setMarketDepth( src.getMarketDepth() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SDFeedType src ) {
        if ( src.getFeedType().length() > 0 ) getFeedTypeForUpdate().copy( src.getFeedType() );
        if ( Constants.UNSET_INT != src.getMarketDepth() ) setMarketDepth( src.getMarketDepth() );
    }

}
