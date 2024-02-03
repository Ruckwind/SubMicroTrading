/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

public interface DummyAdminCommandMBean extends AdminCommand {

    String getMessage();

    void setMessage( String msg );
}
