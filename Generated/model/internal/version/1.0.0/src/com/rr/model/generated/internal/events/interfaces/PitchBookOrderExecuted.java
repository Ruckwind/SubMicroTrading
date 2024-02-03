package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MMTMarketMechanism;
import com.rr.model.generated.internal.type.MMTTradingMode;
import com.rr.model.generated.internal.type.MMTDividend;
import com.rr.model.generated.internal.type.MMTAlgorithmicTrade;
import com.rr.model.generated.internal.type.MMTTransactionCategory;
import com.rr.model.generated.internal.type.MMTReferencePriceIndicator;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface PitchBookOrderExecuted extends BaseCboePitchWrite, Event {

   // Getters and Setters
    long getOrderId();

    int getLastQty();

    long getExecId();

    MMTMarketMechanism getMktMech();

    MMTTradingMode getTradingMode();

    MMTDividend getDividend();

    MMTAlgorithmicTrade getAlgoTrade();

    MMTTransactionCategory getTranCat();

    MMTReferencePriceIndicator getRefPriceInd();

    @Override void dump( ReusableString out );

}
