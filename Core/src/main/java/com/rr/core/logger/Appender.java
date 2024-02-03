/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

public interface Appender {

    /**
     * attach a chain appender to this one where events are passed when finished with or recycled if none
     *
     * @param dest
     */
    void chain( Appender dest );

    void close();

    void flush();

    void handle( LogEvent e );

    /**
     * once only init
     */
    void init( Level level );

    boolean isEnabledFor( Level level );

    void open();
}
