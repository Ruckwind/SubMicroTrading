package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class SecDefLegImpl implements SecDefLeg, Reusable<SecDefLegImpl>, Copyable<SecDefLeg> {

   // Attrs

    private transient          SecDefLegImpl _next = null;
    private final ReusableString _legSymbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private final ReusableString _legSecurityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _legRatioQty = Constants.UNSET_INT;
    private final ReusableString _legSecurityDesc = new ReusableString( SizeType.INST_SEC_DESC_LENGTH.getSize() );

    private SecurityIDSource _legSecurityIDSource = SecurityIDSource.ExchangeSymbol;
    private Side _legSide;
    private ExchangeInstrument _instrument;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getLegSymbol() { return _legSymbol; }

    @Override public final void setLegSymbol( byte[] buf, int offset, int len ) { _legSymbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getLegSymbolForUpdate() { return _legSymbol; }

    @Override public final ViewString getLegSecurityID() { return _legSecurityID; }

    @Override public final void setLegSecurityID( byte[] buf, int offset, int len ) { _legSecurityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getLegSecurityIDForUpdate() { return _legSecurityID; }

    @Override public final SecurityIDSource getLegSecurityIDSource() { return _legSecurityIDSource; }
    @Override public final void setLegSecurityIDSource( SecurityIDSource val ) { _legSecurityIDSource = val; }

    @Override public final int getLegRatioQty() { return _legRatioQty; }
    @Override public final void setLegRatioQty( int val ) { _legRatioQty = val; }

    @Override public final ViewString getLegSecurityDesc() { return _legSecurityDesc; }

    @Override public final void setLegSecurityDesc( byte[] buf, int offset, int len ) { _legSecurityDesc.setValue( buf, offset, len ); }
    @Override public final ReusableString getLegSecurityDescForUpdate() { return _legSecurityDesc; }

    @Override public final Side getLegSide() { return _legSide; }
    @Override public final void setLegSide( Side val ) { _legSide = val; }

    @Override public final ExchangeInstrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( ExchangeInstrument val ) { _instrument = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _legSymbol.reset();
        _legSecurityID.reset();
        _legSecurityIDSource = SecurityIDSource.ExchangeSymbol;
        _legRatioQty = Constants.UNSET_INT;
        _legSecurityDesc.reset();
        _legSide = null;
        _instrument = null;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecDefLeg;
    }

    @Override
    public final SecDefLegImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecDefLegImpl nxt ) {
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
        out.append( "SecDefLegImpl" ).append( ' ' );
        if ( getLegSymbol().length() > 0 )             out.append( ", legSymbol=" ).append( getLegSymbol() );
        if ( getLegSecurityID().length() > 0 )             out.append( ", legSecurityID=" ).append( getLegSecurityID() );
        if ( getLegSecurityIDSource() != null )             out.append( ", legSecurityIDSource=" );
        if ( getLegSecurityIDSource() != null ) out.append( getLegSecurityIDSource().id() );
        if ( Constants.UNSET_INT != getLegRatioQty() && 0 != getLegRatioQty() )             out.append( ", legRatioQty=" ).append( getLegRatioQty() );
        if ( getLegSecurityDesc().length() > 0 )             out.append( ", legSecurityDesc=" ).append( getLegSecurityDesc() );
        if ( getLegSide() != null )             out.append( ", legSide=" ).append( getLegSide() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
    }

    @Override public final void snapTo( SecDefLeg dest ) {
        ((SecDefLegImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecDefLeg src ) {
        getLegSymbolForUpdate().copy( src.getLegSymbol() );
        getLegSecurityIDForUpdate().copy( src.getLegSecurityID() );
        setLegSecurityIDSource( src.getLegSecurityIDSource() );
        setLegRatioQty( src.getLegRatioQty() );
        getLegSecurityDescForUpdate().copy( src.getLegSecurityDesc() );
        setLegSide( src.getLegSide() );
        setInstrument( src.getInstrument() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecDefLeg src ) {
        getLegSymbolForUpdate().copy( src.getLegSymbol() );
        getLegSecurityIDForUpdate().copy( src.getLegSecurityID() );
        setLegSecurityIDSource( src.getLegSecurityIDSource() );
        setLegRatioQty( src.getLegRatioQty() );
        getLegSecurityDescForUpdate().copy( src.getLegSecurityDesc() );
        setLegSide( src.getLegSide() );
        setInstrument( src.getInstrument() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecDefLeg src ) {
        if ( src.getLegSymbol().length() > 0 ) getLegSymbolForUpdate().copy( src.getLegSymbol() );
        if ( src.getLegSecurityID().length() > 0 ) getLegSecurityIDForUpdate().copy( src.getLegSecurityID() );
        if ( getLegSecurityIDSource() != null )  setLegSecurityIDSource( src.getLegSecurityIDSource() );
        if ( Constants.UNSET_INT != src.getLegRatioQty() ) setLegRatioQty( src.getLegRatioQty() );
        if ( src.getLegSecurityDesc().length() > 0 ) getLegSecurityDescForUpdate().copy( src.getLegSecurityDesc() );
        setLegSide( src.getLegSide() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
    }

}
