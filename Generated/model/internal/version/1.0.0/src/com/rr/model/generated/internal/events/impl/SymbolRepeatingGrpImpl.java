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

public final class SymbolRepeatingGrpImpl implements SymbolRepeatingGrp, Reusable<SymbolRepeatingGrpImpl>, Copyable<SymbolRepeatingGrp> {

   // Attrs

    private transient          SymbolRepeatingGrpImpl _next = null;
    private final ReusableString _symbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSymbol() { return _symbol; }

    @Override public final void setSymbol( byte[] buf, int offset, int len ) { _symbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getSymbolForUpdate() { return _symbol; }


   // Reusable Contract

    @Override
    public final void reset() {
        _symbol.reset();
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SymbolRepeatingGrp;
    }

    @Override
    public final SymbolRepeatingGrpImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SymbolRepeatingGrpImpl nxt ) {
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
        out.append( "SymbolRepeatingGrpImpl" ).append( ' ' );
        if ( getSymbol().length() > 0 )             out.append( ", symbol=" ).append( getSymbol() );
    }

    @Override public final void snapTo( SymbolRepeatingGrp dest ) {
        ((SymbolRepeatingGrpImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SymbolRepeatingGrp src ) {
        getSymbolForUpdate().copy( src.getSymbol() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SymbolRepeatingGrp src ) {
        getSymbolForUpdate().copy( src.getSymbol() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SymbolRepeatingGrp src ) {
        if ( src.getSymbol().length() > 0 ) getSymbolForUpdate().copy( src.getSymbol() );
    }

}
