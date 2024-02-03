package com.rr.core.model;

import com.rr.core.component.SMTControllableComponent;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZString;
import com.rr.core.thread.NonBlockingWorker;

public interface Strategy extends SMTControllableComponent, NonBlockingWorker, PointInTime {

    boolean isDead();

    default void killStrat() { }

    default double performance() { return Constants.UNSET_DOUBLE; }

    ;

    ZString zid();

    ;
}
