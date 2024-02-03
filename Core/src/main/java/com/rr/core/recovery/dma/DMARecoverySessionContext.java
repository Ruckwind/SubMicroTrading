/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recovery.dma;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.persister.Persister;
import com.rr.core.session.RecoverableSession;

/**
 * RecoverSessionContext - each session will have two, inbound/outbound processed on different threads
 */
public interface DMARecoverySessionContext {

    /**
     * @return the persister associated with this context
     */
    Persister getPersister();

    /**
     * @param persister used to reread events from persistence file during recovery
     */
    void setPersister( Persister persister );

    /**
     * @return the session associated with this recovery context
     */
    RecoverableSession getSession();

    /**
     * @return buffer to use for generating a warning message
     */
    ReusableString getWarnMessage();

    /**
     * @return true if the session is configured with a chain session, used for detecting messages that didnt propogate to chain
     */
    boolean hasChainSession();

    /**
     * recovery context is either inbound (for received messages) or outbound (for sent messages)
     */
    boolean isInbound();

    /**
     * @return true if the session will mark a message as sent in persistent flags
     */
    boolean persistFlagConfirmSentEnabled();

    /**
     * regenerate the requested event from the registered persister
     *
     * @param persistKey
     * @return
     * @NOTE MUST ONLY USE WHEN 100% SURE NO CONFLICTING CROSS THREAD USE ie DURING RECONCILIATION
     */
    Event regenerate( long persistKey );
}
