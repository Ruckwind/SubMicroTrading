/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * represents a reusable type of object with a unique code identifier
 * <p>
 * implementations will be enums for groups
 * <p>
 * Look at ModelReusableTypes
 * <p>
 * NewOrderSingle(       ReusableCategoryEnum.Event, FullEventIds.ID_NEWORDERSINGLE,        EventIds.ID_NEWORDERSINGLE ),
 * ClientNewOrderSingle( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_NEWORDERSINGLE, EventIds.ID_NEWORDERSINGLE ),
 * MarketNewOrderSingle( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_NEWORDERSINGLE, EventIds.ID_NEWORDERSINGLE ),
 * <p>
 * The subId can be used in a single switch case to denote NewOrderSingle ... and as long as the NewOrderSingle interface is used for processing
 * all is good. Note to use EventIds for getSubId() and FullEventIds for getId()
 * <p>
 * Both getId() and getSubId() should be unqiue across all categories as each Category has its own id range
 * Note there is an overlap of codes between EventIds and FullEventIds ... so dont mix and match in single switch
 * You can use in single switch subId for one cat and id for another cat
 *
 * @author Richard Rose
 */
public interface ReusableType {

    /**
     * @return name of the type ... expected to be an enum
     */
    @Override String toString();

    /**
     * @return unique identifier for this type, ids are grouped sequentialy within category
     */
    int getId();

    /**
     * @return the reusable category object
     */
    ReusableCategory getReusableCategory();

    /**
     * @return non unique grouping
     */
    int getSubId();
}
