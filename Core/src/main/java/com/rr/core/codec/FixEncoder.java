/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.CopyConstructor;
import com.rr.core.lang.ZString;

public interface FixEncoder extends Encoder, CopyConstructor<FixEncoder> {

    /**
     * @return a new EMPTY instance of same class, with own buffer etc
     */
    @Override FixEncoder newInstance();

    void setSenderCompId( ZString senderCompId );

    void setSenderLocationId( ZString senderLocationId );

    void setSenderSubId( ZString senderSubId );

    void setTargetCompId( ZString targetId );

    void setTargetSubId( ZString targetSubId );
}
