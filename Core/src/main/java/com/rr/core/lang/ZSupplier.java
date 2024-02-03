/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package com.rr.core.lang;

/**
 * Represents a function that produces a result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get(Object)}.
 *
 * @param <T> the type of the input to the function
 * @since 1.8
 */
@FunctionalInterface
public interface ZSupplier<T> extends SerializableLambda {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get();
}
