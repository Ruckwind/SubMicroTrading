/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.internal.type;

public interface ManualTypeIds {

    int EXECTYPE_NEW            = 0;
    int EXECTYPE_PARTIALFILL    = 1;
    int EXECTYPE_FILL           = 2;
    int EXECTYPE_DONEFORDAY     = 3;
    int EXECTYPE_CANCELED       = 4;
    int EXECTYPE_REPLACED       = 5;
    int EXECTYPE_PENDINGCANCEL  = 6;
    int EXECTYPE_STOPPED        = 7;
    int EXECTYPE_REJECTED       = 8;
    int EXECTYPE_SUSPENDED      = 9;
    int EXECTYPE_PENDINGNEW     = 10;
    int EXECTYPE_CALCULATED     = 11;
    int EXECTYPE_EXPIRED        = 12;
    int EXECTYPE_RESTATED       = 13;
    int EXECTYPE_PENDINGREPLACE = 14;
    int EXECTYPE_TRADE          = 15;
    int EXECTYPE_TRADECORRECT   = 16;
    int EXECTYPE_TRADECANCEL    = 17;
    int EXECTYPE_ORDERSTATUS    = 18;
    int EXECTYPE_UNKNOWN        = 19;
}
