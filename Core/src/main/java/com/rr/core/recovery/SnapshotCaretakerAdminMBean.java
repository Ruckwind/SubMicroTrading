/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recovery;

import com.rr.core.admin.AdminCommand;

public interface SnapshotCaretakerAdminMBean extends AdminCommand {

    String takeSnapshot();
}
