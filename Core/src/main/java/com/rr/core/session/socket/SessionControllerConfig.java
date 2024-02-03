/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

public interface SessionControllerConfig {

    int getHeartBeatIntSecs();

    void setHeartBeatIntSecs( int heartBeatIntSecs );

    /**
     * @return maximum sequence number in resync, eg in fix 0/999999 means upto last sent msg depending on FIX version
     */
    int getMaxSeqNum();

    boolean isDisconnectOnMissedHB();

    boolean isGapFillAllowed();

    void setGapFillAllowed( boolean canGapFill );

    /**
     * when in loggedInState and receive logOut message should auto reconnect
     *
     * @return
     */
    default boolean isReconnectOnLogout() { return false; }

    /**
     * if other side send an nextSeqNum less than expected in logger on message then can optionally truncate down automatically
     * <p>
     * THIS IS NOT USUALLY ADVISABLE WITH EXCHANGE BUT MAYBE SO FOR CLIENTS
     *
     * @return true if should truncate down expected seq num from other side
     */
    boolean isRecoverFromLoginSeqNumTooLow();

    void setRecoverFromLoginSeqNumTooLow( boolean isRecoverFromLoginSeqNumTooLow );

    boolean isServer();
}
