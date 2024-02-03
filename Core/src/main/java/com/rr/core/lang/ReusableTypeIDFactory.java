/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * represents a reusable type of object with a unique code identifier
 * <p>
 * enum not used as its not extensible
 *
 * @author Richard Rose
 */
public class ReusableTypeIDFactory {

    private static Map<String, AtomicInteger> _ids = new HashMap<>();

    private static Map<String, Set<Integer>> _catIdToSet = new HashMap<>();
    private static Set<Integer>              _allIds     = new LinkedHashSet<>();

    private static int _maxId = 0;

    public static int nextId( ReusableCategory cat ) {

        int id;

        synchronized( _ids ) {
            String catId = cat.toString();

            AtomicInteger baseId = _ids.get( catId );

            if ( baseId == null ) {
                baseId = new AtomicInteger( cat.getBaseId() );

                _ids.put( catId, baseId );
            }

            id = baseId.incrementAndGet();

            setID( cat, id );
        }

        return id;
    }

    public static int maxId() {
        return _maxId;
    }

    public static int setID( ReusableCategory cat, int id ) {
        synchronized( _ids ) {
            String catId = cat.toString();

            Integer idInt = id;

            if ( _allIds.contains( idInt ) ) {
                throw new RuntimeException( "ReusableTypeID duplicate/overlapping ID, category=" + catId + ", id=" + id );
            }

            Set<Integer> catIds = _catIdToSet.computeIfAbsent( catId, k -> new LinkedHashSet<>() );

            if ( catIds.contains( idInt ) ) {
                throw new RuntimeException( "ReusableTypeID duplicate ID, category=" + catId + ", id=" + id );
            }

            catIds.add( idInt );

            _allIds.add( idInt );

            AtomicInteger baseId = _ids.get( catId );

            if ( baseId == null ) {
                baseId = new AtomicInteger( cat.getBaseId() );

                _ids.put( catId, baseId );
            }

            if ( baseId.get() < id ) {
                baseId.set( id + 1 );
            }

            if ( id > _maxId ) {
                _maxId = id;
            }
        }

        return id;
    }
}
