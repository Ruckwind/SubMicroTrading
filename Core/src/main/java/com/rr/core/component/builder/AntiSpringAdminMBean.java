/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.admin.AdminCommand;

public interface AntiSpringAdminMBean extends AdminCommand {

    String restartJMXWeb();

    String saveAndExit();
}
