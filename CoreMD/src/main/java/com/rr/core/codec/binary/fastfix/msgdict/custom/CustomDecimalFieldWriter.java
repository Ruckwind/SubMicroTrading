/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.msgdict.custom;

import com.rr.core.codec.binary.fastfix.common.FieldWriter;

/**
 * all combinations of operator for exp/mantissa in use at the moment should be coded
 * otherrs should use soft implementation
 *
 * @author Richard Rose
 */
public abstract class CustomDecimalFieldWriter extends FieldWriter {

    public CustomDecimalFieldWriter( String name, int id, boolean isOptional ) {
        super( name, id, isOptional );
    }
}
