package com.rr.core.model;

import com.rr.core.lang.*;

public abstract class ImmutableBaseEvent implements Event {

    private volatile Event _nextMessage;             // used for concurrent Q

    private int  _msgSeqNum      = Constants.UNSET_INT;
    private long _eventTimestamp = Constants.UNSET_LONG;

    private int          _flags = 0;
    private EventHandler _messageHandler;

    public ImmutableBaseEvent() {
        /* nothing */
    }

    @Override public final void attachQueue( Event nxt )                { _nextMessage = nxt; }

    @Override public final void detachQueue()                           { _nextMessage = null; }

    @Override public final EventHandler getEventHandler()               { return _messageHandler; }

    @Override public final void setEventHandler( EventHandler handler ) { _messageHandler = handler; }

    @Override public int getFlags() { return _flags; }

    @Override public final int getMsgSeqNum()                           { return _msgSeqNum; }

    @Override public final void setMsgSeqNum( int val )                 { _msgSeqNum = val; }

    @Override public final Event getNextQueueEntry()                    { return _nextMessage; }

    @Override public final boolean isFlagSet( MsgFlag flag ) { return MsgFlag.isOn( _flags, flag ); }

    @Override public final long getEventTimestamp()                     { return _eventTimestamp; }

    @Override public final void setEventTimestamp( long val )           { _eventTimestamp = val; }

    // Helper methods
    @Override public final void setFlag( MsgFlag flag, boolean isOn ) { _flags = MsgFlag.setFlag( _flags, flag, isOn ); }

    @Override public ReusableType getReusableType()          { return CoreReusableType.NotReusable; }

    @Override public final String toString() {
        ReusableString s = TLC.instance().pop();
        dump( s );
        String out = s.toString();
        TLC.instance().pushback( s );
        return out;
    }
}

