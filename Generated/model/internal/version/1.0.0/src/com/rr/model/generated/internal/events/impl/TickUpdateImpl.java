package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.Side;
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

public final class TickUpdateImpl implements TickUpdate, Reusable<TickUpdateImpl>, Copyable<TickUpdate> {

   // Attrs

    private transient          TickUpdateImpl _next = null;
    private double _mdEntryPx = Constants.UNSET_DOUBLE;
    private int _mdEntrySize = Constants.UNSET_INT;
    private long _tradeTime = Constants.UNSET_LONG;
    private int _numberOfOrders = Constants.UNSET_INT;

    private MDEntryType _mdEntryType;
    private Side _tickDirection;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final MDEntryType getMdEntryType() { return _mdEntryType; }
    @Override public final void setMdEntryType( MDEntryType val ) { _mdEntryType = val; }

    @Override public final double getMdEntryPx() { return _mdEntryPx; }
    @Override public final void setMdEntryPx( double val ) { _mdEntryPx = val; }

    @Override public final int getMdEntrySize() { return _mdEntrySize; }
    @Override public final void setMdEntrySize( int val ) { _mdEntrySize = val; }

    @Override public final long getTradeTime() { return _tradeTime; }
    @Override public final void setTradeTime( long val ) { _tradeTime = val; }

    @Override public final Side getTickDirection() { return _tickDirection; }
    @Override public final void setTickDirection( Side val ) { _tickDirection = val; }

    @Override public final int getNumberOfOrders() { return _numberOfOrders; }
    @Override public final void setNumberOfOrders( int val ) { _numberOfOrders = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _mdEntryType = null;
        _mdEntryPx = Constants.UNSET_DOUBLE;
        _mdEntrySize = Constants.UNSET_INT;
        _tradeTime = Constants.UNSET_LONG;
        _tickDirection = null;
        _numberOfOrders = Constants.UNSET_INT;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.TickUpdate;
    }

    @Override
    public final TickUpdateImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( TickUpdateImpl nxt ) {
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
        out.append( "TickUpdateImpl" ).append( ' ' );
        if ( getMdEntryType() != null )             out.append( ", mdEntryType=" ).append( getMdEntryType() );
        if ( Utils.hasVal( getMdEntryPx() ) ) out.append( ", mdEntryPx=" ).append( getMdEntryPx() );
        if ( Constants.UNSET_INT != getMdEntrySize() && 0 != getMdEntrySize() )             out.append( ", mdEntrySize=" ).append( getMdEntrySize() );
        if ( Constants.UNSET_LONG != getTradeTime() && 0 != getTradeTime() )             out.append( ", tradeTime=" ).append( getTradeTime() );
        if ( getTickDirection() != null )             out.append( ", tickDirection=" ).append( getTickDirection() );
        if ( Constants.UNSET_INT != getNumberOfOrders() && 0 != getNumberOfOrders() )             out.append( ", numberOfOrders=" ).append( getNumberOfOrders() );
    }

    @Override public final void snapTo( TickUpdate dest ) {
        ((TickUpdateImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( TickUpdate src ) {
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setTradeTime( src.getTradeTime() );
        setTickDirection( src.getTickDirection() );
        setNumberOfOrders( src.getNumberOfOrders() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( TickUpdate src ) {
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setTradeTime( src.getTradeTime() );
        setTickDirection( src.getTickDirection() );
        setNumberOfOrders( src.getNumberOfOrders() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( TickUpdate src ) {
        setMdEntryType( src.getMdEntryType() );
        if ( Utils.hasVal( src.getMdEntryPx() ) ) setMdEntryPx( src.getMdEntryPx() );
        if ( Constants.UNSET_INT != src.getMdEntrySize() ) setMdEntrySize( src.getMdEntrySize() );
        if ( Constants.UNSET_LONG != src.getTradeTime() ) setTradeTime( src.getTradeTime() );
        setTickDirection( src.getTickDirection() );
        if ( Constants.UNSET_INT != src.getNumberOfOrders() ) setNumberOfOrders( src.getNumberOfOrders() );
    }

}
