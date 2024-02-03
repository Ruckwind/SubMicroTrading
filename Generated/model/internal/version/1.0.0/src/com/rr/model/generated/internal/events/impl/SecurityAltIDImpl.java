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

public final class SecurityAltIDImpl implements SecurityAltID, Reusable<SecurityAltIDImpl>, Copyable<SecurityAltID> {

   // Attrs

    private transient          SecurityAltIDImpl _next = null;
    private final ReusableString _securityAltID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );

    private SecurityIDSource _securityAltIDSource;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getSecurityAltID() { return _securityAltID; }

    @Override public final void setSecurityAltID( byte[] buf, int offset, int len ) { _securityAltID.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityAltIDForUpdate() { return _securityAltID; }

    @Override public final SecurityIDSource getSecurityAltIDSource() { return _securityAltIDSource; }
    @Override public final void setSecurityAltIDSource( SecurityIDSource val ) { _securityAltIDSource = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _securityAltID.reset();
        _securityAltIDSource = null;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecurityAltID;
    }

    @Override
    public final SecurityAltIDImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecurityAltIDImpl nxt ) {
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
        out.append( "SecurityAltIDImpl" ).append( ' ' );
        if ( getSecurityAltID().length() > 0 )             out.append( ", securityAltID=" ).append( getSecurityAltID() );
        if ( getSecurityAltIDSource() != null )             out.append( ", securityAltIDSource=" );
        if ( getSecurityAltIDSource() != null ) out.append( getSecurityAltIDSource().id() );
    }

    @Override public final void snapTo( SecurityAltID dest ) {
        ((SecurityAltIDImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecurityAltID src ) {
        getSecurityAltIDForUpdate().copy( src.getSecurityAltID() );
        setSecurityAltIDSource( src.getSecurityAltIDSource() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecurityAltID src ) {
        getSecurityAltIDForUpdate().copy( src.getSecurityAltID() );
        setSecurityAltIDSource( src.getSecurityAltIDSource() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecurityAltID src ) {
        if ( src.getSecurityAltID().length() > 0 ) getSecurityAltIDForUpdate().copy( src.getSecurityAltID() );
        if ( getSecurityAltIDSource() != null )  setSecurityAltIDSource( src.getSecurityAltIDSource() );
    }

}
