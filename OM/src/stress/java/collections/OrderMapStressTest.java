/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package collections;

import com.rr.om.order.collections.OrderMapTest;

public class OrderMapStressTest extends OrderMapTest {

    private static final int ORDER_SIZE = 900000;

    @Override protected int getOrderSize() { return ORDER_SIZE; }
}
