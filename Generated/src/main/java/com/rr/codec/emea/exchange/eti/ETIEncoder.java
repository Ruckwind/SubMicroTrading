/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.eti;

import com.rr.core.codec.BinaryEncoder;
import com.rr.core.lang.ZString;

public interface ETIEncoder extends BinaryEncoder {

    void setExchangeEmulationOn();

    void setLocationId( long locationId );

    void setSenderSubID( int newId );

    void setUniqueClientCode( ZString uniqueClientCode );
}
