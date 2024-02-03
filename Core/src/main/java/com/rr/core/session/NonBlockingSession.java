/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.collections.EventQueue;

import java.io.IOException;

public interface NonBlockingSession extends RecoverableSession {

    EventQueue getSendQueue();

    EventQueue getSendSyncQueue();

    default boolean isChildSession() { return false; }

    boolean isMsgPendingWrite();

    void logDisconnected( Exception ex );

    void logInboundDecodingError( RuntimeDecodingException e );

    void logInboundError( Exception e );

    void logOutboundEncodingError( RuntimeEncodingException e );

    void retryCompleteWrite() throws IOException;
}
