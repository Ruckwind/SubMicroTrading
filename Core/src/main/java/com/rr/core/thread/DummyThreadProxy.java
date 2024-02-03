package com.rr.core.thread;

import com.rr.core.utils.ThreadPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * a dummy control thread used by tests which dont use Mocks
 */
public class DummyThreadProxy implements ControlThread {

    private final String                  _id;
    private final Thread                  _worker;
    private       List<ExecutableElement> _elements = new ArrayList<>();

    public DummyThreadProxy( final String id, final Thread worker ) {
        _id     = id;
        _worker = worker;
    }

    @Override public String getComponentId() { return _id; }

    @Override public Thread getThread()                                { return _worker; }

    @Override public boolean isStarted()                               { return true; }

    @Override public boolean isStopping()                              { return false; }

    @Override public void setStopping( final boolean stopping )        { /* nothing */ }

    @Override public void register( final ExecutableElement ex ) {
        _elements.add( ex );
    }

    @Override public void setPriority( final ThreadPriority priority ) { /* nothing */ }

    @Override public void start()                                      { /* nothing */ }

    @Override public void statusChange()                               { /* nothing */ }

    @Override public boolean hasOutstandingWork()                      { return false; }
}
