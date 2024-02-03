/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

public interface CoreCommonFix {

    int BodyLength       = 9;
    int CheckSum         = 10;
    int ClOrdId          = 11;
    int ExecID           = 17;
    int MsgSeqNum        = 34;
    int MsgType          = 35;
    int OrderId          = 37;
    int OrderQty         = 38;
    int OrdStatus        = 39;
    int OrdType          = 40;
    int OrigClOrdId      = 41;
    int SenderCompID     = 49;
    int PossDupFlag      = 43;
    int Price            = 44;
    int SecurityID       = 48;
    int SenderSubID      = 50;
    int SendingTime      = 52;
    int Side             = 54;
    int Symbol           = 55;
    int TargetCompID     = 56;
    int TargetSubID      = 57;
    int Text             = 58;
    int TimeInForce      = 59;
    int TransactTime     = 60;
    int SenderLocationID = 142;
}
