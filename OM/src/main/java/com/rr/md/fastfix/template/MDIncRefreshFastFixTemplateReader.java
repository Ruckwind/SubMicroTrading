/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.template;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;

public interface MDIncRefreshFastFixTemplateReader extends FastFixTemplateReader {

    MDIncRefreshImpl read( FastFixDecodeBuilder decoder, PresenceMapReader pMapIn );

}
