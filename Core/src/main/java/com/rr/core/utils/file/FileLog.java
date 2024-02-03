/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

public interface FileLog {

    void close();

    void log( byte[] buf, int offset, int len );

    void open();

}
