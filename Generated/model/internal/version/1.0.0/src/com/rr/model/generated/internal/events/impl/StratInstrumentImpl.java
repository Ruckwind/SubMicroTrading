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

public final class StratInstrumentImpl implements StratInstrument, Reusable<StratInstrumentImpl>, Copyable<StratInstrument> {

   // Attrs

    private transient          StratInstrumentImpl _next = null;
    private long _id = 0;

    private Instrument _instrument;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final long getId() { return _id; }
    @Override public final void setId( long val ) { _id = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _instrument = null;
        _id = 0;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.StratInstrument;
    }

    @Override
    public final StratInstrumentImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( StratInstrumentImpl nxt ) {
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
        out.append( "StratInstrumentImpl" ).append( ' ' );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Constants.UNSET_LONG != getId() && 0 != getId() )             out.append( ", id=" ).append( getId() );
    }

    @Override public final void snapTo( StratInstrument dest ) {
        ((StratInstrumentImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( StratInstrument src ) {
        setInstrument( src.getInstrument() );
        setId( src.getId() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( StratInstrument src ) {
        setInstrument( src.getInstrument() );
        setId( src.getId() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( StratInstrument src ) {
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Constants.UNSET_LONG != src.getId() ) setId( src.getId() );
    }

}
