/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dao;

import com.rr.core.admin.AdminCommand;
import com.rr.core.lang.Refreshable;

public interface RefreshAdminMBean extends AdminCommand {

    String refresh();
}
