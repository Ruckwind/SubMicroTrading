/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recovery.dma;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Event;
import com.rr.core.session.RecoverableSession;

/**
 * recovery controller, if recovery fails should have config switch to allow force override if problem
 * <p>
 * Note that recovery is highly concurrent with each session potentially having two threads one for inbound and one for outbound messages
 * <p>
 * Each session has two RecoverySessionContext's one for inbound, one for outbound messages
 */
public interface DMARecoveryController extends SMTComponent {

    /**
     * send all the enqueued messages from the reconcile
     */
    void commit();

    void completedInbound( DMARecoverySessionContext ctx );

    void completedOutbound( DMARecoverySessionContext ctx );

    void failedInbound( DMARecoverySessionContext ctx );

    void failedOutbound( DMARecoverySessionContext ctx );

    /**
     * @param ctx        the session sub context
     * @param persistKey a key which can be used later to reread the message from session persistence .. required for two phase recovery
     * @param msg        the recovered message
     */
    void processInbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags );

    /**
     * @param ctx        the session sub context
     * @param persistKey a key which can be used later to reread the message from session persistence .. required for two phase recovery
     * @param msg        the recovered message
     */
    void processOutbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags );

    /**
     * reconcile should only be invoked once after all sessions finished replaying records into the controller
     * <p>
     * note it is expected that there should not be many queued events as even in systems trading millions of orders actual live orders
     * will be in thousands and actions from reconcile will only apply to live orders
     * <p>
     * reconcile generates events to be sent upstream and downstrea,
     *
     * @NOTE YOU MUST INVOKE commit TO SUBMIT / QUEUE THE EVENTS WITHIN THE APPROPRIATE SESSION
     */
    void reconcile();

    /**
     * start must be invoked once before sessions played into the controller
     */
    void start();

    /**
     * inform recovery controller starting to process incoming messages
     *
     * @return RecoverySessionContext a subcontext for the sessions inbound messages
     */
    DMARecoverySessionContext startedInbound( RecoverableSession sess );

    /**
     * inform recovery controller starting to process outgoing messages
     *
     * @return RecoverySessionContext a subcontext for the sessions outbound messages
     */
    DMARecoverySessionContext startedOutbound( RecoverableSession sess );
}
