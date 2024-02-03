/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public interface AssignableString extends ZString {

    void setValue( byte[] buf, int offset, int len );

    void setValue( ZString str );
}
