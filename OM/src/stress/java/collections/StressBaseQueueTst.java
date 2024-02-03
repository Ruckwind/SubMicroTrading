/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.om.order.collections.BaseQueueTst;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

public abstract class StressBaseQueueTst extends BaseQueueTst {

    @Test public void testMixedThreadedB() {
        doTestMixedThreaded( 1000, 1 );
    }

    @Test public void testMixedThreadedC() {
        doTestMixedThreaded( 1000, 2 );
    }

    @Test public void testMixedThreadedD() {
        doTestMixedThreaded( 1000, 8 );
    }

    @Test public void testMixedThreadedE() {
        doTestMixedThreaded( 2500, 32 );
    }

    @Test public void testThreaded1000_1() {
        doTestThreaded( 1000, 1 );
    }

    @Test public void testThreaded1000_8() {
        doTestThreaded( 1000, 8 );
    }

    @Test public void testThreaded2000_2() {
        doTestThreaded( 2000, 2 );
    }

    @Test public void testThreaded2500_32() {
        doTestThreaded( 2500, 32 );
    }

}
