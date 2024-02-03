package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface SecDefLeg extends SubEvent, com.rr.core.model.LegInstrument {

   // Getters and Setters
    ViewString getLegSymbol();

    ViewString getLegSecurityID();

    SecurityIDSource getLegSecurityIDSource();

    int getLegRatioQty();

    ViewString getLegSecurityDesc();

    Side getLegSide();

    ExchangeInstrument getInstrument();

    @Override void dump( ReusableString out );

    void setLegSymbol( byte[] buf, int offset, int len );
    ReusableString getLegSymbolForUpdate();

    void setLegSecurityID( byte[] buf, int offset, int len );
    ReusableString getLegSecurityIDForUpdate();

    void setLegSecurityIDSource( SecurityIDSource val );

    void setLegRatioQty( int val );

    void setLegSecurityDesc( byte[] buf, int offset, int len );
    ReusableString getLegSecurityDescForUpdate();

    void setLegSide( Side val );

    void setInstrument( ExchangeInstrument val );

}
