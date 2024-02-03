/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.ExceptionTrace;
import com.rr.core.properties.AppProps;
import com.rr.core.tasks.BasicSchedulerCallback;
import com.rr.core.tasks.CoreScheduledEvent;
import com.rr.core.tasks.SchedulerFactory;

import java.util.Map;

public class SystemStatus {

    private static SystemStatus _instance = new SystemStatus();

    private String _emailTo;

    private SMTComponentManager _componentManager;

    public static SystemStatus instance() {
        return _instance;
    }

    public void initialised( SMTComponentManager componentManager ) {

        _componentManager = componentManager;

        if ( isUnitTst() )
            return; // unit test

        if ( _emailTo != null ) {
            ShutdownManager.instance().register( "SystemStatusLog", () -> logStatus( "STOP" ), ShutdownManager.Priority.Medium );

            SchedulerFactory.get().registerForGroupEvent( CoreScheduledEvent.EndOfDay,
                                                          new BasicSchedulerCallback( "SystemStatus.EndOfDay", ( e ) -> logStatus( "DATE ROLL" ) ) );

            String subject = mkSubject() + " START";
            String msg     = mkHdr();

            String[] to = { _emailTo };

            EmailProxy.instance().sendMail( to, subject, msg );
        }
    }

    public void logStatus( String cmt ) {
        String subject = mkSubject() + " " + cmt;
        String msg     = mkHdr() + "\n\n";

        if ( _componentManager != null ) {
            msg += _componentManager.toString();
        }

        String[] to = { _emailTo };

        EmailProxy.instance().sendMail( to, subject, msg );
    }

    private boolean isUnitTst() {
        if ( AppProps.instance().getFile() == null )
            return true;

        Map<Thread, StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
        for ( Thread t : stackTraceMap.keySet() ) {
            if ( "main".equals( t.getName() ) ) {
                StackTraceElement[] mainStackTrace = stackTraceMap.get( t );
                for ( StackTraceElement element : mainStackTrace ) {
                    if ( element.toString().contains( "Test" ) ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String mkHdr() {
        ReusableString h = new ReusableString( "System initialised\n" );

        h.append( AppProps.instance().toString() );

        h.append( "\n\nSTACK TRACE\n\n" );

        ExceptionTrace.dumpStackTrace( h );

        h.append( "\n\n" );

        return h.toString();
    }

    private String mkSubject() {
        String app = AppProps.instance().getFile();

        return app + ":SystemStatus";
    }
}
