/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * defines the starting id and range of category ids
 *
 * @NOTE the event ids should start from 1 as they are
 * key to performance in switch statements
 */
public enum ReusableCategoryEnum implements ReusableCategory {

    // @NOTE keep UNDER 32,768

    Event( 1, 1023 ),
    Collection( 1024, 128 ),
    OM( 1280, 128 ),
    Core( 1536, 128 ),
    MDS( 1792, 128 ),
    Strats( 2048, 1000 ),
    Test( 3072, 128 ),
    OMFeedType( 8192, 2048 );

    private final int _baseId;
    private final int _size;

    ReusableCategoryEnum( int baseId, int size ) {
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
