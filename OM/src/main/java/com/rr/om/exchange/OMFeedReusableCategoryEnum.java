/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.lang.ReusableCategory;

/**
 * defines the starting id and range of category ids
 *
 * @NOTE the event ids should start from 1 as they are
 * key to performance in switch statements
 */
public enum OMFeedReusableCategoryEnum implements ReusableCategory {

    // @NOTE keep UNDER 32,768

    UTP( 8192, 128 );

    private final int _baseId;
    private final int _size;

    OMFeedReusableCategoryEnum( int baseId, int size ) {
        _baseId = baseId;
        _size   = size;
    }

    @Override
    public int getBaseId() {
        return _baseId;
    }

    @Override
    public int getSize() {
        return _size;
    }
}
