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

public final class SecDefEventImpl implements SecDefEvent, Reusable<SecDefEventImpl>, Copyable<SecDefEvent> {

   // Attrs

    private transient          SecDefEventImpl _next = null;
    private long _eventDate = Constants.UNSET_LONG;
    private long _eventTime = Constants.UNSET_LONG;

    private SecDefEventType _eventType;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final SecDefEventType getEventType() { return _eventType; }
    @Override public final void setEventType( SecDefEventType val ) { _eventType = val; }

    @Override public final long getEventDate() { return _eventDate; }
    @Override public final void setEventDate( long val ) { _eventDate = val; }

    @Override public final long getEventTime() { return _eventTime; }
    @Override public final void setEventTime( long val ) { _eventTime = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _eventType = null;
        _eventDate = Constants.UNSET_LONG;
        _eventTime = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecDefEvent;
    }

    @Override
    public final SecDefEventImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecDefEventImpl nxt ) {
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
        out.append( "SecDefEventImpl" ).append( ' ' );
        if ( getEventType() != null )             out.append( ", eventType=" );
        if ( getEventType() != null ) out.append( getEventType().id() );
        if ( Constants.UNSET_LONG != getEventDate() && 0 != getEventDate() )             out.append( ", eventDate=" ).append( getEventDate() );
        if ( Constants.UNSET_LONG != getEventTime() && 0 != getEventTime() )             out.append( ", eventTime=" ).append( getEventTime() );
    }

    @Override public final void snapTo( SecDefEvent dest ) {
        ((SecDefEventImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecDefEvent src ) {
        setEventType( src.getEventType() );
        setEventDate( src.getEventDate() );
        setEventTime( src.getEventTime() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecDefEvent src ) {
        setEventType( src.getEventType() );
        setEventDate( src.getEventDate() );
        setEventTime( src.getEventTime() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecDefEvent src ) {
        if ( getEventType() != null )  setEventType( src.getEventType() );
        if ( Constants.UNSET_LONG != src.getEventDate() ) setEventDate( src.getEventDate() );
        if ( Constants.UNSET_LONG != src.getEventTime() ) setEventTime( src.getEventTime() );
    }

}
