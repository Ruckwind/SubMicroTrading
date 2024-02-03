package com.rr.core.pubsub;

import com.rr.core.lang.Constants;
import com.rr.core.properties.AppProps;

public class SubscribeOptions {

    public enum InitialSubMode {
        fromTimestampNanos,
        fromSeqNum,
        fromStart,
        NextNew,
        LastSeen
    }
    private boolean        _isDurable          = true;
    private long           _fromTimestampNanos = Constants.UNSET_LONG;
    private long           _fromSeqNum         = Constants.UNSET_LONG;
    private InitialSubMode _subMode            = InitialSubMode.NextNew;
    private String         _dispatchName       = null; // dispatch queue name .... (may or may not be supported)
    private String         _consumerGroup      = null; // if specified use consumer groups ... which restricts a single consumer per group
    public SubscribeOptions()                            { }
    public SubscribeOptions( final InitialSubMode mode ) { _subMode = mode; }

    @Override public String toString() {
        return "SubscribeOptions{" +
               "_isDurable=" + _isDurable +
               ", _fromTimestampNanos=" + _fromTimestampNanos +
               ", _fromSeqNum=" + _fromSeqNum +
               ", _subMode=" + _subMode +
               ", _dispatchName='" + _dispatchName + '\'' +
               ", _consumerGroup='" + _consumerGroup + '\'' +
               '}';
    }

    public String getConsumerGroup()                                   { return _consumerGroup; }

    public void setConsumerGroup( final String consumerGroup )         { _consumerGroup = consumerGroup; }

    public String getDispatchName()                                    { return _dispatchName; }

    public void setDispatchName( final String dispatchName )           { _dispatchName = dispatchName; }

    public long getFromSeqNum()                                        { return _fromSeqNum; }

    public void setFromSeqNum( final long fromSeqNum )                 { _fromSeqNum = fromSeqNum; }

    public long getFromTimestampNanos()                                { return _fromTimestampNanos; }

    public void setFromTimestampNanos( final long fromTimestampNanos ) { _fromTimestampNanos = fromTimestampNanos; }

    public InitialSubMode getSubMode()                                 { return _subMode; }

    public void setSubMode( final InitialSubMode subMode )             { _subMode = subMode; }

    public boolean isDurable()                                         { return _isDurable; }

    public void setDurable( final boolean durable )                    { _isDurable = durable; }
}
