/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.jmx;

import com.rr.core.admin.AdminCommand;

/**
 * @WARNING injected fix messages MUST have 3 digit checksum and 3 digit length
 */

public interface JMXSessionAdminMBean extends AdminCommand {

    String injectMessage( String rawMessage );

    /**
     * inject a message directly to specified session bypassing configured processor
     */
    String injectMessage( String sessionName, String rawMessage );

}
