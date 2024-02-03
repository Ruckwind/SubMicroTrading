/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public enum OutboundInstruction {

    delegate,   // field taken from order request
    seperate,   // distinct values in both up and downstream
    clientDelegateIgnoreMktSide, // mkt side message, field not required from mkt as when send to client it will be taken from the client base request
    clientDelegateMktSeperate, // client side delegates to order request, mkt side is seperate
    delegateGetAndSet, // delegate getter and setter to order request, used to allow mkt side change to propogate to parent
    none
}
