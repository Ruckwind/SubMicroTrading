/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;

public class RejectDecodeException extends BaseReject<RejectDecodeException> {

    private final ReusableString _trace = new ReusableString();

    public RejectDecodeException( byte[] fixMsg, int offset, int maxIdx, RuntimeDecodingException t ) {
        this( fixMsg, offset, maxIdx, t, null );
    }

    public RejectDecodeException( byte[] fixMsg, int offset, int maxIdx, RuntimeDecodingException t, ReusableString dump ) {
        super( fixMsg, offset, maxIdx, t );

        _trace.copy( dump );
    }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.RejectDecodeException;
    }

    @Override public void reset() {
        super.reset();
        _trace.reset();
    }

    @Override public void dump( ReusableString out ) {
        if ( _trace.length() > 0 ) {
            out.append( ", TRACE - " );
            out.append( _trace );
            out.append( " :: " );

            _trace.reset(); // stop multiple copies of this appearing in logger
        }

        super.dump( out );
    }
}
