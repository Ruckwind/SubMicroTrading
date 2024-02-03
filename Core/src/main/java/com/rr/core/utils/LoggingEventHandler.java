/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;

/**
 * message handler that simply logs the message
 *
 * @author Richard Rose
 * @NOTE ONLY FOR USE BY SINGLE THREAD
 */

public class LoggingEventHandler implements EventHandler {

    private static final Logger _log = LoggerFactory.create( LoggingEventHandler.class );

    private ReusableString _msg = new ReusableString();

    private String _id;

    public LoggingEventHandler( String id ) {
        _id = id;
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void handle( Event msg ) {
        handleNow( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _msg.copy( _id ).append( " received : " );
        msg.dump( _msg );
        _log.info( _msg );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public void threadedInit() {
        //
    }
}
