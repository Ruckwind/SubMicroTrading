/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.HasReusableType;
import com.rr.core.lang.ReusableString;

public interface Event extends HasReusableType, PointInTime {

    /**
     * a message can belong to at most one queue ... tho this is NOT checked
     *
     * @param nextNode - the next node in the queue
     */
    void attachQueue( Event nextNode );

    /**
     * sets internal nextMessage queue ref to null
     */
    void detachQueue();

    /**
     * dump the message to the supplied reusable string
     *
     * @return the supplied out reusable string for chained calls
     */
    void dump( ReusableString out );

    /**
     * @return message handler associated with Message
     */
    EventHandler getEventHandler();

    /**
     * @param handler to associate with this message
     */
    void setEventHandler( EventHandler handler );

    int getFlags();

    int getMsgSeqNum();

    void setMsgSeqNum( int seqNum );

    /**
     * @return the next message value from attachQueue
     */
    Event getNextQueueEntry();

    boolean isFlagSet( MsgFlag flag );

    /**
     * Sample usage
     * <p>
     * setEventTimestamp( ClockFactory.get().currentInternalTime() );
     *
     * @param internalTime
     */
    void setEventTimestamp( long internalTime );

    void setFlag( MsgFlag flag, boolean isOn );
}
