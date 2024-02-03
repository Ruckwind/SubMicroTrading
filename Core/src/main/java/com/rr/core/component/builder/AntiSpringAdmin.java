/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.admin.AdminAgent;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.Procedure;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class AntiSpringAdmin implements AntiSpringAdminMBean {

    private static final ErrorCode FAIL_START = new ErrorCode( "ASA100", "Error in saveAndExit" );
    private static Logger _log = LoggerFactory.create( AntiSpringAdmin.class );
    private final Procedure _saveAndExit;

    public AntiSpringAdmin( final Procedure saveAndExit ) {
        _saveAndExit = saveAndExit;
    }

    @Override public String getName() {
        return "AntiSpringAdmin";
    }

    @Override public String saveAndExit() {
        Thread  thread   = Thread.currentThread();
        boolean isDaemon = thread.isDaemon();

        final AtomicBoolean doneFlag = new AtomicBoolean( false );

        if ( isDaemon ) {
            _log.info( "AntiSpringAdmin about to invoke saveAndExit on DAEMON thread " + thread.getName() + ", creating non daemon thread for adminCMD" );

            Thread t = new Thread( () -> invokeSave( doneFlag ), "AdminSaveAndEXIT" );
            t.setDaemon( false );
            t.start();

            while( !doneFlag.get() ) {
                _log.info( "AntiSpringAdmin waiting for saveAndExit to complete" );
                synchronized( doneFlag ) {
                    try { doneFlag.wait( 1000 ); } catch( InterruptedException e ) { /* ignore */ }
                }
            }

        } else {
            _log.info( "AntiSpringAdmin about to invoke saveAndExit on NON daemon thread " + thread.getName() );

            invokeSave( doneFlag );
        }

        return "AntiSpringAdmin saveAndExit completed";
    }

    @Override public String restartJMXWeb() {

        String ret = "restartJMXWeb invoked : ";
        try {
            AdminAgent.restartJMXWeb();

            ret += " OK";

        } catch( Throwable e ) {

            ret += " EXCEPTION " + e.getMessage();
        }

        return ret;
    }

    private void invokeSave( final AtomicBoolean doneFlag ) {
        try {
            _log.info( "AntiSpringAdmin : invoking saveAndExit " );

            _saveAndExit.invoke();

        } catch( Exception e ) {
            _log.error( FAIL_START, "", e );
        } finally {
            doneFlag.set( true );
            synchronized( doneFlag ) {
                doneFlag.notifyAll();
            }
        }
    }
}
