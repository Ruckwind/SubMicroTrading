package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDUpdateAction;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.TradingSessionID;
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

public final class MDEntryImpl implements MDEntry, Reusable<MDEntryImpl>, Copyable<MDEntry> {

   // Attrs

    private transient          MDEntryImpl _next = null;
    private final ReusableString _securityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _repeatSeq = Constants.UNSET_INT;
    private int _numberOfOrders = Constants.UNSET_INT;
    private int _mdPriceLevel = Constants.UNSET_INT;
    private double _mdEntryPx = Constants.UNSET_DOUBLE;
    private int _mdEntrySize = Constants.UNSET_INT;
    private int _mdEntryTime = Constants.UNSET_INT;

    private SecurityIDSource _securityIDSource = SecurityIDSource.ExchangeSymbol;
    private ExchangeCode _securityExchange;
    private MDUpdateAction _mdUpdateAction;
    private MDEntryType _mdEntryType;
    private TradingSessionID _tradingSessionID;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final ViewString getSecurityID() { return _securityID; }

    @Override public final void setSecurityID( byte[] buf, int offset, int len ) { _securityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIDForUpdate() { return _securityID; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final MDUpdateAction getMdUpdateAction() { return _mdUpdateAction; }
    @Override public final void setMdUpdateAction( MDUpdateAction val ) { _mdUpdateAction = val; }

    @Override public final int getRepeatSeq() { return _repeatSeq; }
    @Override public final void setRepeatSeq( int val ) { _repeatSeq = val; }

    @Override public final int getNumberOfOrders() { return _numberOfOrders; }
    @Override public final void setNumberOfOrders( int val ) { _numberOfOrders = val; }

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

    @Override public final TradingSessionID getTradingSessionID() { return _tradingSessionID; }
    @Override public final void setTradingSessionID( TradingSessionID val ) { _tradingSessionID = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _securityIDSource = SecurityIDSource.ExchangeSymbol;
        _securityID.reset();
        _securityExchange = null;
        _mdUpdateAction = null;
        _repeatSeq = Constants.UNSET_INT;
        _numberOfOrders = Constants.UNSET_INT;
        _mdPriceLevel = Constants.UNSET_INT;
        _mdEntryType = null;
        _mdEntryPx = Constants.UNSET_DOUBLE;
        _mdEntrySize = Constants.UNSET_INT;
        _mdEntryTime = Constants.UNSET_INT;
        _tradingSessionID = null;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDEntry;
    }

    @Override
    public final MDEntryImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDEntryImpl nxt ) {
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
        out.append( "MDEntryImpl" ).append( ' ' );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getSecurityID().length() > 0 )             out.append( ", securityID=" ).append( getSecurityID() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getMdUpdateAction() != null )             out.append( ", mdUpdateAction=" ).append( getMdUpdateAction() );
        if ( Constants.UNSET_INT != getRepeatSeq() && 0 != getRepeatSeq() )             out.append( ", repeatSeq=" ).append( getRepeatSeq() );
        if ( Constants.UNSET_INT != getNumberOfOrders() && 0 != getNumberOfOrders() )             out.append( ", numberOfOrders=" ).append( getNumberOfOrders() );
        if ( Constants.UNSET_INT != getMdPriceLevel() && 0 != getMdPriceLevel() )             out.append( ", mdPriceLevel=" ).append( getMdPriceLevel() );
        if ( getMdEntryType() != null )             out.append( ", mdEntryType=" ).append( getMdEntryType() );
        if ( Utils.hasVal( getMdEntryPx() ) ) out.append( ", mdEntryPx=" ).append( getMdEntryPx() );
        if ( Constants.UNSET_INT != getMdEntrySize() && 0 != getMdEntrySize() )             out.append( ", mdEntrySize=" ).append( getMdEntrySize() );
        if ( Constants.UNSET_INT != getMdEntryTime() && 0 != getMdEntryTime() )             out.append( ", mdEntryTime=" ).append( getMdEntryTime() );
        if ( getTradingSessionID() != null )             out.append( ", tradingSessionID=" ).append( getTradingSessionID() );
    }

    @Override public final void snapTo( MDEntry dest ) {
        ((MDEntryImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDEntry src ) {
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setSecurityExchange( src.getSecurityExchange() );
        setMdUpdateAction( src.getMdUpdateAction() );
        setRepeatSeq( src.getRepeatSeq() );
        setNumberOfOrders( src.getNumberOfOrders() );
        setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setMdEntryTime( src.getMdEntryTime() );
        setTradingSessionID( src.getTradingSessionID() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDEntry src ) {
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setSecurityExchange( src.getSecurityExchange() );
        setMdUpdateAction( src.getMdUpdateAction() );
        setRepeatSeq( src.getRepeatSeq() );
        setNumberOfOrders( src.getNumberOfOrders() );
        setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        setMdEntryPx( src.getMdEntryPx() );
        setMdEntrySize( src.getMdEntrySize() );
        setMdEntryTime( src.getMdEntryTime() );
        setTradingSessionID( src.getTradingSessionID() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDEntry src ) {
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( src.getSecurityID().length() > 0 ) getSecurityIDForUpdate().copy( src.getSecurityID() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        setMdUpdateAction( src.getMdUpdateAction() );
        if ( Constants.UNSET_INT != src.getRepeatSeq() ) setRepeatSeq( src.getRepeatSeq() );
        if ( Constants.UNSET_INT != src.getNumberOfOrders() ) setNumberOfOrders( src.getNumberOfOrders() );
        if ( Constants.UNSET_INT != src.getMdPriceLevel() ) setMdPriceLevel( src.getMdPriceLevel() );
        setMdEntryType( src.getMdEntryType() );
        if ( Utils.hasVal( src.getMdEntryPx() ) ) setMdEntryPx( src.getMdEntryPx() );
        if ( Constants.UNSET_INT != src.getMdEntrySize() ) setMdEntrySize( src.getMdEntrySize() );
        if ( Constants.UNSET_INT != src.getMdEntryTime() ) setMdEntryTime( src.getMdEntryTime() );
        setTradingSessionID( src.getTradingSessionID() );
    }

}
