/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;

/**
 * A common base implementation for Reusable Message
 *
 * @param <T>
 * @author Richard Rose
 * @WARNING Extending methods MUST remember to invoke super in reset and dump
 */
public abstract class BaseReusableEvent<T> extends BaseEvent<T> {

    @Override public void dump( ReusableString out ) {
        out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", flags=" ).append( (int) getFlags() );
        out.append( ", eventTimestamp=" ).append( getEventTimestamp() );
    }
}
