package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with two args
 */
@FunctionalInterface
public interface ZConsumer2Args<T, U> extends SerializableLambda {

    void accept( T arg1, U arg2 );

    default ZConsumer2Args<T, U> andThen( ZConsumer2Args<? super T, ? super U> after ) {
        Objects.requireNonNull( after );

        return ( l, r ) -> {
            accept( l, r );
            after.accept( l, r );
        };
    }
}
