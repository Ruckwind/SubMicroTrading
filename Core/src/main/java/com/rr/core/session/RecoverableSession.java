/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.SMTActiveWorker;
import com.rr.core.component.SMTControllableComponent;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;

import java.nio.ByteBuffer;

public interface RecoverableSession extends Session, SMTControllableComponent, SMTActiveWorker {

    byte PERSIST_FLAG_CONFIRM_SENT = 1 << 3;    // in upper byte of short flags

    enum SessionState {Listening, Connected, Disconnected}

    /**
     * send the message on the current thread of control (invoked from MessageDispatcher)
     *
     * @param msg
     */
    @Override void handleNow( Event msg );

    @Override boolean isConnected();

    /**
     * stop the session
     *
     * @NOTE a stopped session cannot be restarted
     */
    @Override void stop();

    /**
     * pass in explicit receiver for the session
     *
     * @param receiver
     */
    void attachReceiver( Receiver receiver );

    /**
     * request to connect, actual connection may occur off receiver thread from init
     */
    void connect();

    /**
     * @param msg
     * @return true IF the message should be discarded after a disconnect eg a session message like heartbeat
     */
    boolean discardOnDisconnect( Event msg );

    /**
     * drop current connection, releases appropriate resources
     *
     * @param tryReconnect - after a disconnection try to reconnect
     * @NOTE dispatch threads must cater for disconnected session and not spinlock
     * <p>
     * connect can be reinvoked after disconnect
     */
    void disconnect( boolean tryReconnect );

    /**
     * invoke the router to dispatch inbound message (1 at a time if msg chained)
     *
     * @param msg decoded message which is already logged and persisted
     */
    void dispatchInbound( Event msg );

    /**
     * @return next session in chain
     */
    Session getChainSession();

    /**
     * @param sess session to dispatch all outbound messages after they have been sent over wire
     */
    void setChainSession( Session sess );

    SessionConfig getConfig();

    /**
     * session direction, mainly to identify upstream vs downstream sessions purposes
     */
    SessionDirection getDirection();

    /**
     * @return if nano logging enabled then return the time of the last message sent
     */
    long getLastSent();

    boolean getRejectOnDisconnect();

    /**
     * @param reject if true then reject messages if disconnected
     */
    void setRejectOnDisconnect( boolean reject );

    SessionState getSessionState();

    /**
     * if able handle the message now even tho disconnected
     * <p>
     * only possible for sessions that support gap fill on reconnect
     *
     * @param msg
     * @return true if message persisted locally and send to hub / recycled
     */
    default boolean handleDisconnectedNow( Event msg ) { return false; }

    /**
     * dispatch a message for async sending as part of the sync process
     */
    void handleForSync( Event msg );

    /**
     * @param msg use the sessions INBOUND recycler to recycle the message
     */
    void inboundRecycle( Event msg );

    /**
     * @return a string with description of the session
     */
    String info();

    void init() throws PersisterException;

    /**
     * connect within the current thread context, invoked by receiver thread
     */
    void internalConnect();

    boolean isLogEvents();

    /**
     * @param on logger in and out events
     */
    void setLogEvents( boolean on );

    /**
     * @return true is session is fully logged in
     * @TODO refactor out replace by adding LoggedIn to State
     */
    boolean isLoggedIn();

    boolean isPaused();

    /**
     *
     */
    void setPaused( boolean paused );

    boolean isRejectOnDisconnect();


    /**
     * @param msg use the sessions OUTBOUND recycler to recycle the message
     */
    void outboundRecycle( Event msg );

    /**
     * persist the last message received
     */
    void persistLastInboundMesssage();

    /**
     * process inbound messages until finished set or connection problem
     *
     * @NOTE causes disconnect on SessionException, IOException
     */
    void processIncoming();

    /**
     * call to process the next message, blocking call
     *
     * @throws Exception, SessionException
     *                    IOException
     */
    void processNextInbound() throws Exception;

    void recover( DMARecoveryController ctl );

    /**
     * read message from persistence using persistence key
     *
     * @param isInbound  if true retrieve message from inbound persister, otherwise use outbound persister
     * @param persistKey key previously returned from a write to persister ... usually start address in persister
     * @param tmpBigBuf  tmp buffer big enough to read message (usually 8k buff which is reused)
     * @param tmpCtxBuf  tmp buffer for reading the optional context (if any)
     * @return the requested event regenerated OR null if unable to regenerate
     * @NOTE this method is not on Session interface and is only for use in reconciliation/recovery
     * @WARNING threading will be a problem if invoked outside of reconciliation
     */
    Event recoverEvent( boolean isInbound, long persistKey, ReusableString tmpBigBuf, ByteBuffer tmpCtxBuf );

    void registerConnectionListener( ConnectionListener listener );

    /**
     * if disconnected then this routine is used to synth a reject and send upstream
     * <p>
     * session should not reject posDup messages or reconciliation messages
     *
     * @param msg
     * @param errMsg
     * @return true IF the message was reject was generated and sent upstream
     */
    boolean rejectMessageUpstream( Event msg, ZString errMsg );

    void setConnectionId( ZString connId );

    /**
     * @param logStats if true timestamp message in/out
     */
    void setLogStats( boolean logStats );

    /**
     * set throttler for sending of messages, messages exceeding this rate will be rejected
     *
     * @param throttleNoMsgs         - restrict new messages to this many messages per period (throttler may allow cancels and reject NOS/REP)
     * @param disconnectLimit        - total message limit in period (all messages rejected)
     * @param throttleTimeIntervalMS - throttle period in ms
     */
    void setThrottle( int throttleNoMsgs, int disconnectLimit, long throttleTimeIntervalMS );

    /**
     * BLOCKING call waiting for recovery to finish replay
     *
     * @NOTE ensure invoke recover first
     */
    void waitForRecoveryToComplete();
}
