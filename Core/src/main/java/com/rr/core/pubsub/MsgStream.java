package com.rr.core.pubsub;

/**
 * MsgStreams .... do NOT change any without checking property files are also updated
 * <p>
 * changing stream name WILL BREAK existing prod ..... only for weekly rollout and even then consider potential persistence mismatching !
 */
public enum MsgStream {
    orderExec,
    stratPub,
    instRef,
    dataPoint,
    mktDataFast,
    mktDataSlow,
    subscriptions,
    other,
    testStream;

    public static MsgStream getMktDataMsgStreamName( final int durationSecs ) {
        MsgStream stream = MsgStream.mktDataSlow;

        if ( durationSecs == 1 ) {
            stream = MsgStream.mktDataFast;
        }

        return stream;
    }
}
