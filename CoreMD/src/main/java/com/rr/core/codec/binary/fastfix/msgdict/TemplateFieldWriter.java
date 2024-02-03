/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict;

/**
 * Template field writer ... doesnt use generics to avoid autoboxing
 *
 * @author Richard Rose
 */
public interface TemplateFieldWriter {

    int getId();

    String getName();

    boolean isOptional();

    boolean requiresPMap();
}
