/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * represents a reusable type of object with a unique code identifier
 * <p>
 * enum not used as its not extensible
 *
 * @author Richard Rose
 */
public enum CoreReusableType implements ReusableType {

    NotReusable( ReusableCategoryEnum.Core ),      /* used by objects that cannot be reused like timer tasks */
    ReusableString( ReusableCategoryEnum.Core ),
    LogEventSmall( ReusableCategoryEnum.Core ),
    LogEventLarge( ReusableCategoryEnum.Core ),
    LogEventHuge( ReusableCategoryEnum.Core ),
    RejectThrowable( ReusableCategoryEnum.Core ),
    RejectIndexOutOfBounds( ReusableCategoryEnum.Core ),
    RejectDecodeException( ReusableCategoryEnum.Core ),
    HashMapEntry( ReusableCategoryEnum.Core ),
    PackedEvent( ReusableCategoryEnum.Core ),
    NullEvent( ReusableCategoryEnum.Core ),
    SessionStatusEvent( ReusableCategoryEnum.Core ),
    JSONDataPoint( ReusableCategoryEnum.Core ),
    LoanAvailability( ReusableCategoryEnum.Core ),
    TradeRestriction( ReusableCategoryEnum.Core ),
    LiqBookNoLock( ReusableCategoryEnum.MDS ),
    LiqBookWithLock( ReusableCategoryEnum.MDS ),
    LongMapHashEntry( ReusableCategoryEnum.Collection );

    private final int              _id;
    private final ReusableCategory _cat;

    CoreReusableType( ReusableCategory cat ) {
        _cat = cat;
        _id  = ReusableTypeIDFactory.nextId( cat );
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public ReusableCategory getReusableCategory() {
        return _cat;
    }

    @Override
    public int getSubId() {
        return _id;
    }
}
