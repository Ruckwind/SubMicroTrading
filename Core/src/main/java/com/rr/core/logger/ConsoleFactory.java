/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @WARNING NOT FOR NORMAL USE - ONLY COMPONENTS THAT ARE TIED TO LoggerFactory CAN USE DIRECTLY
 */
public final class ConsoleFactory {

    private static List<Logger> _allLoggers = Collections.synchronizedList( new ArrayList<>() );

    public static synchronized Logger console( Class<?> aClass ) {
        return console( aClass, Level.info );
    }

    /**
     * @param aClass
     * @return a console logger .. suitable for use before pooling / app is initialised
     */
    public static synchronized Logger console( Class<?> aClass, Level lvl ) {
        Logger l = ApacheLogger.getApacheLogger( aClass, lvl );
        _allLoggers.add( l );
        return l;
    }
}
