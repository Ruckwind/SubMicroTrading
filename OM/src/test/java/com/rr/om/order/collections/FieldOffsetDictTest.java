/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.java.FieldOffsetDict;
import com.rr.core.java.FieldOffsetDictCache;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.HasReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.Event;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldOffsetDictTest extends BaseTestCase {

    @SuppressWarnings( "unused" )
    public static class Simple implements HasReusableType {

        private static ReusableType _type = CoreReusableType.LogEventSmall;
        private          long  _stuff       = 0;
        private volatile Event _nextMessage = null;

        @Override
        public ReusableType getReusableType() {
            return _type;
        }
    }

    @Test
    public void testDict() {

        FieldOffsetDict s = FieldOffsetDictCache.getFieldOffsetDict( HasReusableType.class, "_stuff" );
        FieldOffsetDict d = FieldOffsetDictCache.getFieldOffsetDict( HasReusableType.class, "_nextMessage" );

        long offset = d.getOffset( new Simple(), true );

        long stuffOffset = s.getOffset( new Simple(), false );

        assertEquals( 12, offset );
        assertEquals( 16, stuffOffset );
    }
}
