/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.CopyConstructor;
import com.rr.core.lang.ZString;

public interface FixDecoder extends Decoder, CopyConstructor<FixDecoder> {

    boolean isVerifyHdrVals();

    void setVerifyHdrVals( boolean verifyHdrVals );

    // compIDs
    void setSenderCompId( ZString senderCompId );

    void setSenderSubId( ZString senderSubId );

    void setTargetCompId( ZString targetCompId );

    void setTargetSubId( ZString targetSubId );

    // validation & logging controls
    void setValidateChecksum( boolean doValidate );
}
