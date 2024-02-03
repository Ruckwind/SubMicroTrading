/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;

/**
 * A common base implementation for Reusable Message
 *
 * @param <T>
 * @author Richard Rose
 * @WARNING Extending methods MUST remember to invoke super in reset and dump
 */
public abstract class BaseNonReusableMessage<T> extends BaseEvent<T> {

    @Override public void dump( ReusableString out ) {
        out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", flags=" ).append( (int) getFlags() );
        out.append( ", eventTimestamp=" ).append( getEventTimestamp() );
    }

    @Override public ReusableType getReusableType() { return CoreReusableType.NotReusable; }
}
