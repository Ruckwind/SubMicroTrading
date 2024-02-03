package com.rr.core.pubsub;

import java.io.IOException;
import java.util.List;

public class DummyPubSubSess implements PubSubSess {

    public DummyPubSubSess( final String sessionId ) { }

    @Override public void close() throws IOException {
        /* nothing */
    }

    @Override public void stream( final MsgStream stream ) {
        /* nothing */
    }

    @Override public void syncPublish( final String topic, final Object msg ) {
        /* nothing */
    }

    @Override public void asyncPublish( final String topic, final Object msg ) {
        /* nothing */
    }

    @Override public void syncPublish( final String topic, final Object msg, final String replyTopic ) throws Exception {
        /* nothing */
    }

    @Override public <C, M> Subscription subscribe( final String subscriberId, final String topic, final C context, final Callback<C, M> callback, final SubscribeOptions opts ) {
        return null;
    }
}
