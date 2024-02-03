package com.rr.core.lang;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the tri-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <S> the type of the first argument to the function
 * @param <T> the type of the second argument to the function
 * @param <U> the type of the third argument to the function
 * @param <V> the type of the fourth argument to the function
 * @param <R> the type of the result of the function
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface ZFunction4Args<S, T, U, V, R> extends SerializableLambda {

    default <W> ZFunction4Args<S, T, U, V, W> andThen( Function<? super R, ? extends W> after ) {
        Objects.requireNonNull( after );
        return ( S s, T t, U u, V v ) -> after.apply( apply( s, t, u, v ) );
    }

    /**
     * Applies this function to the given arguments.
     *
     * @param s the first function argument
     * @param t the second function argument
     * @param u the third function argument
     * @return the function result
     */
    R apply( S s, T t, U u, V v );
}
