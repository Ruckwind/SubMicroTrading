/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;

public class RejectThrowable extends BaseReject<RejectThrowable> {

    public RejectThrowable( byte[] fixMsg, int offset, int maxIdx, Throwable t ) {
        super( fixMsg, offset, maxIdx, t );
    }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.RejectThrowable;
    }
}
