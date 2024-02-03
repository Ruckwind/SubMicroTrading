/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

/**
 * admin commands must extend AdminCommand and be named XXXMBean where concrete class is XXX
 *
 * @author Richard Rose
 */
public interface AdminCommand {

    char WILDCARD = '#';

    String getName();

}
