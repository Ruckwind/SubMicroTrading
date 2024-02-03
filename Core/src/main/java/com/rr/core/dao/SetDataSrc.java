package com.rr.core.dao;

import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.lang.Refreshable;

import java.util.Set;

public interface SetDataSrc<T> extends SMTInitialisableComponent, Refreshable {

    boolean contains( T v );

    Set<T> getSet();
}
