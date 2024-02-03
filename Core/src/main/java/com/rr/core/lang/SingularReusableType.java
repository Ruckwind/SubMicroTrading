/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * SingularReusableType  for use where Type is singular and not part of a larger group
 */
public class SingularReusableType implements ReusableType {

    private final String           _name;
    private final ReusableCategory _cat;
    private final int              _id;

    public SingularReusableType( final String name, final ReusableCategory cat ) {
        _name = name;
        _cat  = cat;
        _id   = ReusableTypeIDFactory.nextId( cat );
    }

    /**
     * @return sub identifier for this type
     */
    @Override public final int getSubId() {
        return _id;
    }

    /**
     * @return the reusable category object
     */
    @Override public final ReusableCategory getReusableCategory() {
        return _cat;
    }

    /**
     * @return unique identifier for this type, ids are grouped sequentialy within category
     */
    @Override public final int getId() {
        return _id;
    }

    /**
     * @return name of the type ... expected to be an enum
     */
    @Override public final String toString() {
        return _name;
    }
}
