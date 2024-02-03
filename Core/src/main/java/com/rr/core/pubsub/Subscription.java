package com.rr.core.pubsub;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface Subscription {

    CompletableFuture<Boolean> drain( final Duration drainTimeout ) throws InterruptedException;

    String getSubject();

    void unsubscribe() throws IOException;
}
