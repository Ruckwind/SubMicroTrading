/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.msgdict.TemplateFieldReader;

public interface FixFieldReader<T extends FieldValWrapper> extends TemplateFieldReader {

    void read( FastFixDecodeBuilder decoder, PresenceMapReader mapReader, T dest );
}
