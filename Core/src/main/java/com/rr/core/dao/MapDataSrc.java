package com.rr.core.dao;

import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.lang.Refreshable;

import java.util.Map;

public interface MapDataSrc<K, V> extends SMTInitialisableComponent, Refreshable {

    /**
     * @param k key
     * @return null if no entry or V
     */
    V get( K k );

    Map<K, V> getMap();
}
