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

public interface PitchBookOrderExecutedWrite extends BaseCboePitchWrite, PitchBookOrderExecuted {

   // Getters and Setters
    void setOrderId( long val );

    void setLastQty( int val );

    void setExecId( long val );

    void setMktMech( MMTMarketMechanism val );

    void setTradingMode( MMTTradingMode val );

    void setDividend( MMTDividend val );

    void setAlgoTrade( MMTAlgorithmicTrade val );

    void setTranCat( MMTTransactionCategory val );

    void setRefPriceInd( MMTReferencePriceIndicator val );

}
