package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.MMTMarketMechanism;
import com.rr.model.generated.internal.type.MMTTradingMode;
import com.rr.model.generated.internal.type.MMTTransactionCategory;
import com.rr.model.generated.internal.type.MMTReferencePriceIndicator;
import com.rr.model.generated.internal.type.MMTAlgorithmicTrade;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface PitchOffBookTradeWrite extends BaseCboePitchWrite, PitchOffBookTrade {

   // Getters and Setters
    void setOrderId( long val );

    void setSide( Side val );

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityIdSrc( SecurityIDSource val );

    void setSecurityExchange( ExchangeCode val );

    void setLastQty( int val );

    void setLastPx( double val );

    void setExecId( long val );

    void setMktMech( MMTMarketMechanism val );

    void setTradingMode( MMTTradingMode val );

    void setTranCat( MMTTransactionCategory val );

    void setRefPriceInd( MMTReferencePriceIndicator val );

    void setAlgoTrade( MMTAlgorithmicTrade val );

}
