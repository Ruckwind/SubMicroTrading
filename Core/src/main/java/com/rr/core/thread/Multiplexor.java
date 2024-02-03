package com.rr.core.thread;

public interface Multiplexor<T> extends ExecutableElement {

    void addWorker( T worker );
}
