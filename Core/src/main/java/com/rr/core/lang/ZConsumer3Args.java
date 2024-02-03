package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with three args
 */
@FunctionalInterface
public interface ZConsumer3Args<S, T, U> extends SerializableLambda {

    void accept( S arg1, T arg2, U arg3 );

    default ZConsumer3Args<S, T, U> andThen( ZConsumer3Args<S, T, U> after ) {
        Objects.requireNonNull( after );

        return ( a, b, c ) -> {
            accept( a, b, c );
            after.accept( a, b, c );
        };
    }
}
