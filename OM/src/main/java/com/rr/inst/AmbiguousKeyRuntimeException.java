package com.rr.inst;

import com.rr.core.model.SecurityIDSource;
import com.rr.core.utils.SMTRuntimeException;

public class AmbiguousKeyRuntimeException extends SMTRuntimeException {

    String           _ambiguousId;
    SecurityIDSource _ambiguousIdSrc;

    public AmbiguousKeyRuntimeException( final String msg ) {
        this( msg, SecurityIDSource.Unknown, (String) null );
    }

    public AmbiguousKeyRuntimeException( final String msg, SecurityIDSource ambiguousIdSrc, String ambiguousId ) {
        super( msg );
        _ambiguousIdSrc = ambiguousIdSrc;
        _ambiguousId    = ambiguousId;
    }

    public AmbiguousKeyRuntimeException( final String msg, SecurityIDSource ambiguousIdSrc, String ambiguousId, Throwable e ) {
        super( msg, e );
        _ambiguousIdSrc = ambiguousIdSrc;
        _ambiguousId    = ambiguousId;
    }

    public String getAmbiguousId()              { return _ambiguousId; }

    public SecurityIDSource getAmbiguousIdSrc() { return _ambiguousIdSrc; }
}
