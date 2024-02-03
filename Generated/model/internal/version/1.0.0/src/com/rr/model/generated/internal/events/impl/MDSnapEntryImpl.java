package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.TickDirection;
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

public final class MDSnapEntryImpl implements MDSnapEntry, Reusable<MDSnapEntryImpl>, Copyable<MDSnapEntry> {

   // Attrs

    private transient          MDSnapEntryImpl _next = null;
    private int _mdPriceLevel = Constants.UNSET_INT;
    private double _mdEntryPx = Constants.UNSET_DOUBLE;
    private int _mdEntrySize = Constants.UNSET_INT;
    private int _mdEntryTime = Constants.UNSET_INT;
    private int _tradeVolume = Constants.UNSET_INT;

    private MDEntryType _mdEntryType;
    private TickDirection _tickDirection;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getMdPriceLevel() { return _mdPriceLevel; }
    @Override public final void setMdPriceLevel( int val ) { _mdPriceLevel = val; }

    @Override public final MDEntryType getMdEntryType() { return _mdEntryType; }
    @Override public final void setMdEntryType( MDEntryType val ) { _mdEntryType = val; }

    @Override public final double getMdEntryPx() { return _mdEntryPx; }
    @Override public final void setMdEntryPx( double val ) { _mdEntryPx = val; }

    @Override public final int getMdEntrySize() { return _mdEntrySize; }
    @Override public final void setMdEntrySize( int val ) { _mdEntrySize = val; }

    @Override public final int getMdEntryTime() { return _mdEntryTime; }
    @Override public final void setMdEntryTime( int val ) { _mdEntryTime = val; }

    @Override public final TickDirection getTickDirection() { return _tickDirection; }
    @Override public final void setTickDirection( TickDirection val ) { _tickDirection = val; }

    @Override public final int getTradeVolume() { return _tradeVolume; }
    @Override public final void setTradeVolume( int val ) { _tradeVolume = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _mdPriceLevel = Constants.UNSET_INT;
        _mdEntryType = null;
        _mdEntryPx = Constants.UNSET_DOUBLE;
        _mdEntrySize = Constants.UNSET_INT;
        _mdEntryTime = Constants.UNSET_INT;
        _tickDirection = null;
        _tradeVolume = Constants.UNSET_INT;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDSnapEntry;
    }

    @Override
    public final MDSnapEntryImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDSnapEntryImpl nxt ) {
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
        out.append( "MDSnapEntryImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getMdPriceLevel() && 0 != getMdPriceLevel() )             out.append( ", mdPriceLevel=" ).append( getMdPriceLevel() );
        if ( getMdEntryType() != null )             out.append( ", mdEntryType=" ).append( getMdEntryType() );
        if ( Utils.hasVal( getMdEntryPx() ) ) out.append( ", mdEntryPx=" ).append( getMdEntryPx() );
        if ( Constants.UNSET_INT != getMdEntrySize() && 0 != getMdEntrySize() )             out.append( ", mdEntrySize=" ).append( getMdEntrySize() );
        if ( Constants.UNSET_INT != getMdEntryTime() && 0 != getMdEntryTime() )             out.append( ", mdEntryTime=" ).append( getMdEntryTime() );
        if ( getTickDirection() != null )             out.append( ", tickDirection=" ).append( getTickDirection() );
        if ( Constants.UNSET_INT != getTradeVolume() && 0 != getTradeVolume() )             out.append( ", tradeVolume=" ).append( getTradeVolume() );
    }

    @Override public final void snapTo( MDSnapEntry dest ) {
        ((MDSnapEntryImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDSnapEntry src ) {
        setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setMdEntryTime( src.getMdEntryTime() );
        setTickDirection( src.getTickDirection() );
        setTradeVolume( src.getTradeVolume() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDSnapEntry src ) {
        setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setMdEntryTime( src.getMdEntryTime() );
        setTickDirection( src.getTickDirection() );
        setTradeVolume( src.getTradeVolume() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDSnapEntry src ) {
        if ( Constants.UNSET_INT != src.getMdPriceLevel() ) setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        if ( Utils.hasVal( src.getMdEntryPx() ) ) setMdEntryPx( src.getMdEntryPx() );
        if ( Constants.UNSET_INT != src.getMdEntrySize() ) setMdEntrySize( src.getMdEntrySize() );
        if ( Constants.UNSET_INT != src.getMdEntryTime() ) setMdEntryTime( src.getMdEntryTime() );
        setTickDirection( src.getTickDirection() );
        if ( Constants.UNSET_INT != src.getTradeVolume() ) setTradeVolume( src.getTradeVolume() );
    }

}
