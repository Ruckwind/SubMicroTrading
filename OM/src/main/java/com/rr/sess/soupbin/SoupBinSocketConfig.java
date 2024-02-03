package com.rr.sess.soupbin;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.session.socket.SessionControllerConfig;
import com.rr.core.session.socket.SocketConfig;

public class SoupBinSocketConfig extends SocketConfig implements SessionControllerConfig {

    private boolean _isAllowMultipleChildren;
    private boolean _disconnectOnMissedHB           = true;
    private int     _heartBeatIntSecs               = 30;
    private boolean _isRecoverFromLoginSeqNumTooLow = false;
    private int     _userId;
    private ZString _password                       = new ViewString( "" );

    @Override public boolean isDisconnectOnMissedHB()                                                     { return _disconnectOnMissedHB; }

    @Override public int getHeartBeatIntSecs()                                                            { return _heartBeatIntSecs; }

    @Override public void setHeartBeatIntSecs( final int heartBeatIntSecs )                               { _heartBeatIntSecs = heartBeatIntSecs; }

    @Override public boolean isRecoverFromLoginSeqNumTooLow()                                             { return _isRecoverFromLoginSeqNumTooLow; }

    @Override public void setRecoverFromLoginSeqNumTooLow( final boolean isRecoverFromLoginSeqNumTooLow ) { _isRecoverFromLoginSeqNumTooLow = isRecoverFromLoginSeqNumTooLow; }

    @Override public int getMaxSeqNum()                                                                   { return 0; }

    public boolean isAllowMultipleChildren()                                                              { return _isAllowMultipleChildren; }

    public void setAllowMultipleChildren( final boolean allowMultipleChildren )                           { _isAllowMultipleChildren = allowMultipleChildren; }
}
