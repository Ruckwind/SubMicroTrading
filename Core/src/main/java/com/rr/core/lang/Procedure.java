package com.rr.core.lang;

/**
 * procedure a function with no args and no return value
 */
@FunctionalInterface
public interface Procedure extends SerializableLambda {

    void invoke();
}
