/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;

public interface Encoder extends SMTComponent {

    /**
     * append statistics for message to buffer
     *
     * @param msg
     * @param time
     */
    void addStats( ReusableString outBuf, Event msg, long time );

    /**
     * encode the message to the registered buffer
     *
     * @param msg
     * @NOTE always use getOffset() and getLength() for EACH message when extracting the data from the buffer
     * the offset will not necessarily be 0 due to optimisations for header processing which can adjust the offset per message
     */
    void encode( Event msg );

    /**
     * @return get the underlying byte array, used  to avoid extra memcpy in logging
     */
    byte[] getBytes();

    int getLength();

    int getOffset();

    /**
     * @param nanoTiming if true nano stat collection is enabled
     */
    void setNanoStats( boolean nanoTiming );

    /**
     * @param calc - time zone calculator to use
     */
    void setTimeUtils( TimeUtils calc );

    /**
     * invoked when a message cannot be sent downstream
     *
     * @param msg    the outbound message that cannot be sent
     * @param errMsg an error messsage to be encorporated into action message
     * @return null     if no action required, OR a reject  message that should be sent upstream
     */
    Event unableToSend( Event msg, ZString errMsg );
}
