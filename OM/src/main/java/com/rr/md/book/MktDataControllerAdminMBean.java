/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.admin.AdminCommand;

public interface MktDataControllerAdminMBean extends AdminCommand {

    String clearAllBooks();

    String clearBook( String securityDesc );

    String listAllTopBook();

    String listBook( String securityDesc );

}
