package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with four args
 */
@FunctionalInterface
public interface ZConsumer4Args<S, T, U, V> extends SerializableLambda {

    void accept( S arg1, T arg2, U arg3, V arg4 );

    default ZConsumer4Args<S, T, U, V> andThen( ZConsumer4Args<S, T, U, V> after ) {
        Objects.requireNonNull( after );

        return ( a, b, c, d ) -> {
            accept( a, b, c, d );
            after.accept( a, b, c, d );
        };
    }
}
