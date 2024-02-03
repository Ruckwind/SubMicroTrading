package com.rr.core.lang;

import java.util.Objects;

/**
 * procedure a function with two args
 */
@FunctionalInterface
public interface ZConsumer<T> extends SerializableLambda {

    void accept( T arg1 );

    default ZConsumer<T> andThen( ZConsumer<? super T> after ) {
        Objects.requireNonNull( after );

        return ( l ) -> {
            accept( l );
            after.accept( l );
        };
    }
}
