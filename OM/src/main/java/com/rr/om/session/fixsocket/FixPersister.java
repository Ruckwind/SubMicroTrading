/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.persister.Persister;

public interface FixPersister extends Persister {

    boolean gapFillInBound( int fromSeqNum, int uptoSeqNum );

}
