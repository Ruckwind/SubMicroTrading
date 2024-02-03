/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.lang.ReusableCategory;
import com.rr.core.lang.ReusableCategoryEnum;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ReusableTypeIDFactory;

public enum OrderCollectionTypes implements ReusableType {

    OrderMapHashEntry( ReusableCategoryEnum.Collection );

    private final int              _id;
    private final ReusableCategory _cat;

    OrderCollectionTypes( ReusableCategory cat ) {
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

