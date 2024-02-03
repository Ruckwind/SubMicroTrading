/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableCategory;
import com.rr.core.lang.ReusableCategoryEnum;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ReusableTypeIDFactory;

public enum CollectionTypes implements ReusableType {

    LinkedMapEntry( ReusableCategoryEnum.Collection ),
    MapEntry( ReusableCategoryEnum.Collection ),
    SingleNode( ReusableCategoryEnum.Collection ),
    ConcurrentMapEntry( ReusableCategoryEnum.Collection ),
    ConcurrentLinkedQueueHead( ReusableCategoryEnum.Collection ),
    NonBlockSyncQueueHead( ReusableCategoryEnum.Collection ),
    DoubleLinkedMessageQueueNode( ReusableCategoryEnum.Collection ),
    DoubleLinkedMessageQueue( ReusableCategoryEnum.Collection ),
    SimpleMessageQueue( ReusableCategoryEnum.Collection ),
    BlockSyncQueueHead( ReusableCategoryEnum.Collection ),
    EventHead( ReusableCategoryEnum.Collection );

    private final int              _id;
    private final ReusableCategory _cat;

    CollectionTypes( ReusableCategory cat ) {
        _cat = cat;
        _id  = ReusableTypeIDFactory.nextId( cat );
    }

    @Override
    public int getSubId() {
        return _id;
    }

    @Override
    public ReusableCategory getReusableCategory() {
        return _cat;
    }

    @Override
    public int getId() {
        return _id;
    }
}

