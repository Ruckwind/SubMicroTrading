/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.model.Event;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.om.processor.BaseProcessorTestCase;
import com.rr.om.processor.EventProcessorImpl;
import com.rr.om.warmup.FixTestUtils;

import static org.junit.Assert.fail;

public abstract class BaseRecoveryTst extends BaseProcessorTestCase {

    protected static class TestHighFreqSimpleRecoveryController extends HighFreqSimpleRecoveryController {

        public TestHighFreqSimpleRecoveryController( int expOrders, int totalSessions, EventProcessorImpl proc ) {
            super( expOrders, totalSessions, proc );
        }

        @Override
        public Event getDownChain() {
            return super.getDownChain();
        }

        @Override
        public Event getUpstreamChain() {
            return super.getUpstreamChain();
        }
    }

    private byte[]            _deBuf   = new byte[ 8192 ];
    private byte[]            _enBuf   = new byte[ 8192 ];
    private Standard44Encoder _encoder = FixTestUtils.getEncoder44( _enBuf, 0 );

    protected long persist( DummyRecoverySession sess, Event msg, boolean isInbound ) {
        long key = 0;

        try {
            _encoder.encode( msg );

            Persister p = (isInbound) ? sess.getInboundPersister() : sess.getOutboundPersister();
            key = p.persist( _enBuf, _encoder.getOffset(), _encoder.getLength() );
        } catch( PersisterException e ) {
            fail();
        }

        return key;
    }

    protected Event regen( DummyRecoverySession client, boolean isInbound, long key ) {
        Persister p = (isInbound) ? client.getInboundPersister() : client.getOutboundPersister();

        int len = 0;

        try {
            len = p.read( key, _deBuf, 0 );
        } catch( PersisterException e ) {
            fail();
        }

        return _decoder.decode( _deBuf, 0, len );
    }
}
