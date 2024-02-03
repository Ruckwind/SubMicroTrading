package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with five args
 */
@FunctionalInterface
public interface ZConsumer5Args<S, T, U, V, X> extends SerializableLambda {

    void accept( S arg1, T arg2, U arg3, V arg4, X arg5 );

    default ZConsumer5Args<S, T, U, V, X> andThen( ZConsumer5Args<S, T, U, V, X> after ) {
        Objects.requireNonNull( after );

        return ( a, b, c, d, e ) -> {
            accept( a, b, c, d, e );
            after.accept( a, b, c, d, e );
        };
    }
}
