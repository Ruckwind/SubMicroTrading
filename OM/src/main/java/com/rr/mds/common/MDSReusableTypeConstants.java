/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.ReusableCategoryEnum;

public interface MDSReusableTypeConstants {

    int FULL_ID_SUBSCRIBE                  = ReusableCategoryEnum.MDS.getBaseId() + 1;
    int FULL_ID_TRADING_BAND_UPDATE        = ReusableCategoryEnum.MDS.getBaseId() + 2;
    int FULL_ID_FX_SNAPSHOT                = ReusableCategoryEnum.MDS.getBaseId() + 3;
    int FULL_ID_MARKET_DATA_ACTIVE_BBO     = ReusableCategoryEnum.MDS.getBaseId() + 4;
    int FULL_ID_MARKET_DATA_ACTIVE_DEPTH   = ReusableCategoryEnum.MDS.getBaseId() + 5;
    int FULL_ID_MARKET_DATA_SNAPSHOT_BBO   = ReusableCategoryEnum.MDS.getBaseId() + 6;
    int FULL_ID_MARKET_DATA_SNAPSHOT_DEPTH = ReusableCategoryEnum.MDS.getBaseId() + 7;

    int SUB_ID_SUBSCRIBE                  = 1;
    int SUB_ID_TRADING_BAND_UPDATE        = 2;
    int SUB_ID_FX_SNAPSHOT                = 3;
    int SUB_ID_MARKET_DATA_ACTIVE_BBO     = 4;
    int SUB_ID_MARKET_DATA_ACTIVE_DEPTH   = 5;
    int SUB_ID_MARKET_DATA_SNAPSHOT_BBO   = 6;
    int SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH = 7;
}
