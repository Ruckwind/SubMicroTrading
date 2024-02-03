package com.rr.core.pubsub;

import com.rr.core.model.Identifiable;

import java.util.Set;

public interface SubsManager<T extends Identifiable> {

    void getAllSubscribed( Set<T> dest );

    boolean isSubscribed( T inst );

    void subscribe( T inst );

    void subscribe( T[] inst );
}
