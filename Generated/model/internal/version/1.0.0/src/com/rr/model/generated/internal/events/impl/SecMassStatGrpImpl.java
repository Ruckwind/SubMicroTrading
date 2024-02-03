package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SecurityTradingStatus;
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

public final class SecMassStatGrpImpl implements SecMassStatGrp, Reusable<SecMassStatGrpImpl>, Copyable<SecMassStatGrp> {

   // Attrs

    private transient          SecMassStatGrpImpl _next = null;
    private final ReusableString _securityId = new ReusableString( SizeType.SECURITYID_LENGTH.getSize() );
    private boolean _securityStatus = false;

    private SecurityIDSource _securityIDSource;
    private SecurityTradingStatus _securityTradingStatus;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final SecurityTradingStatus getSecurityTradingStatus() { return _securityTradingStatus; }
    @Override public final void setSecurityTradingStatus( SecurityTradingStatus val ) { _securityTradingStatus = val; }

    @Override public final boolean getSecurityStatus() { return _securityStatus; }
    @Override public final void setSecurityStatus( boolean val ) { _securityStatus = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _securityId.reset();
        _securityIDSource = null;
        _securityTradingStatus = null;
        _securityStatus = false;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecMassStatGrp;
    }

    @Override
    public final SecMassStatGrpImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecMassStatGrpImpl nxt ) {
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
        out.append( "SecMassStatGrpImpl" ).append( ' ' );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getSecurityTradingStatus() != null )             out.append( ", securityTradingStatus=" ).append( getSecurityTradingStatus() );
        out.append( ", securityStatus=" ).append( getSecurityStatus() );
    }

    @Override public final void snapTo( SecMassStatGrp dest ) {
        ((SecMassStatGrpImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecMassStatGrp src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIDSource( src.getSecurityIDSource() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setSecurityStatus( src.getSecurityStatus() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecMassStatGrp src ) {
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIDSource( src.getSecurityIDSource() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setSecurityStatus( src.getSecurityStatus() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecMassStatGrp src ) {
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setSecurityStatus( src.getSecurityStatus() );
    }

}
