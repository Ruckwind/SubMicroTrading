package com.rr.core.lang;

/**
 * procedure a function with no args and no return value
 */
@FunctionalInterface
public interface ProcedureWithException extends SerializableLambda {

    void invoke() throws Exception;
}
