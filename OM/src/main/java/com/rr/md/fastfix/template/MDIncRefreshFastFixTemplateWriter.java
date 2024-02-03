/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.template;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;

public interface MDIncRefreshFastFixTemplateWriter extends FastFixTemplateWriter {

    void write( FastFixBuilder encoder, PresenceMapWriter pMapOut, MDIncRefreshImpl inc );

}
