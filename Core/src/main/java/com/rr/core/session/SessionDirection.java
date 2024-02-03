/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public enum SessionDirection {

    Upstream,       // session is a source of orders    
    Downstream,     // session is destination for orders
    DropCopy,       // drop copy session

}
