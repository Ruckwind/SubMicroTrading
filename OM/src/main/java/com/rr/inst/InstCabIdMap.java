package com.rr.inst;

import com.rr.core.collections.LongMap;
import com.rr.core.model.Instrument;

import java.util.function.Function;

/**
 * non threadsafe map
 * <p>
 * Note any version of the instrument in time will have the same uniqueInstId
 * <p>
 * isA LongMap  which extracts the uniqueInstId from the instrument to use as the key
 *
 * @param <T>
 */
public interface InstCabIdMap<T> extends LongMap<T> {

    T computeIfAbsent( Instrument key, Function<Instrument, ? extends T> mappingFunction );

    boolean containsKey( Instrument key );

    T put( Instrument key, T value );

    boolean putIfKeyAbsent( Instrument key, T value );

    T remove( Instrument key );
}
