/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.ReusableCategory;
import com.rr.core.lang.ReusableCategoryEnum;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ReusableTypeIDFactory;

/**
 * represents a reusable type of object with a unique code identifier
 * <p>
 * enum not used as its not extensible
 *
 * @author Richard Rose
 */
public enum MDSReusableType implements ReusableType, MDSReusableTypeConstants {

    Subscribe( ReusableCategoryEnum.MDS, FULL_ID_SUBSCRIBE, SUB_ID_SUBSCRIBE ),
    TradingBandUpdate( ReusableCategoryEnum.MDS, FULL_ID_TRADING_BAND_UPDATE, SUB_ID_TRADING_BAND_UPDATE ),
    FXSnapshot( ReusableCategoryEnum.MDS, FULL_ID_FX_SNAPSHOT, SUB_ID_FX_SNAPSHOT ),
    MarketDataActiveBBO( ReusableCategoryEnum.MDS, FULL_ID_MARKET_DATA_ACTIVE_BBO, SUB_ID_MARKET_DATA_ACTIVE_BBO ),
    MarketDataActiveDepth( ReusableCategoryEnum.MDS, FULL_ID_MARKET_DATA_ACTIVE_DEPTH, SUB_ID_MARKET_DATA_ACTIVE_DEPTH ),
    MarketDataSnapshotBBO( ReusableCategoryEnum.MDS, FULL_ID_MARKET_DATA_SNAPSHOT_BBO, SUB_ID_MARKET_DATA_SNAPSHOT_BBO ),
    MarketDataSnapshotDepth( ReusableCategoryEnum.MDS, FULL_ID_MARKET_DATA_SNAPSHOT_DEPTH, SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH );

    private final int              _eventId;
    private final int              _id;
    private final ReusableCategory _cat;

    MDSReusableType( ReusableCategory cat, int catId, int eventId ) {
        _cat     = cat;
        _id      = ReusableTypeIDFactory.setID( cat, catId );
        _eventId = eventId;
    }

    @Override
    public int getSubId() {
        return _eventId;
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
