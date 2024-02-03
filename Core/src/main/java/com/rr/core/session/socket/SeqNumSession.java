/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

import com.rr.core.model.Event;
import com.rr.core.session.RecoverableSession;

public interface SeqNumSession extends RecoverableSession {

    /**
     * extend the persistence index from nextOutSeqNo to newSeqNum
     *
     * @param nextOutSeqNo
     * @param newSeqNum
     */
    void gapExtendOutbound( int nextOutSeqNo, int newSeqNum );

    /**
     * create gap fill messsages UPTO but not including the gapFillToNewSeqNum
     *
     * @param origExpInSeqNum
     * @param gapFillToNewSeqNum
     * @throws SessionStateException
     */
    void gapFillInbound( int origExpInSeqNum, int gapFillToNewSeqNum ) throws SessionStateException;

    /**
     * @return seqNum of last message processed
     */
    int getLastSeqNumProcessed();

    /**
     * @return config with state details
     */
    SessionControllerConfig getStateConfig();

    /**
     * @param recMsg
     * @return return true if recMsg is a session message
     */
    boolean isSessionMessage( Event recMsg );

    /**
     * retrieve previously sent requested message from persistence store and decode it
     *
     * @param curMsgSeqNo
     * @return decoded message or NULL if unable to obtain
     */
    Event retrieve( int curMsgSeqNo );

    /**
     * if client looses messages they have sent us, allow index to be truncated down
     *
     * @param fromSeqNum
     * @param toSeqNum
     * @throws SessionStateException if unable to comply
     */
    void truncateInboundIndexDown( int fromSeqNum, int toSeqNum ) throws SessionStateException;

    /**
     * allow admin override to set index to be truncated down
     *
     * @param fromSeqNum
     * @param toSeqNum
     * @throws SessionStateException if unable to comply
     * @NOTE ONLY FOR USE IN ADMINS
     */
    void truncateOutboundIndexDown( int fromSeqNum, int toSeqNum ) throws SessionStateException;
}

