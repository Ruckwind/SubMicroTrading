/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

public class DummyAdminCommand implements DummyAdminCommandMBean {

    private String message;
    private String name;

    public DummyAdminCommand( String pname, String pmessage ) {
        name    = pname;
        message = pmessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage( String msg ) {
        message = msg;
    }

    @Override
    public String getName() {
        return name;
    }
}
