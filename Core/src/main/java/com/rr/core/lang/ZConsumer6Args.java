package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with five args
 */
@FunctionalInterface
public interface ZConsumer6Args<S, T, U, V, X, Y> extends SerializableLambda {

    void accept( S arg1, T arg2, U arg3, V arg4, X arg5, Y arg6 );

    default ZConsumer6Args<S, T, U, V, X, Y> andThen( ZConsumer6Args<S, T, U, V, X, Y> after ) {
        Objects.requireNonNull( after );

        return ( a, b, c, d, e, f ) -> {
            accept( a, b, c, d, e, f );
            after.accept( a, b, c, d, e, f );
        };
    }
}
