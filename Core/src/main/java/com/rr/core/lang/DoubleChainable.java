/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public interface DoubleChainable<T> extends Chainable<T> {

    T getPrev();

    void setPrev( T prev );
}
