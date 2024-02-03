/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict;

public interface TemplateFieldReader {

    int getId();

    String getName();

    boolean isOptional();

    boolean requiresPMap();

    void reset();
}
