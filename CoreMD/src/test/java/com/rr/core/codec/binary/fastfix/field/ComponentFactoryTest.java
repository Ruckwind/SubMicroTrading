/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.field;

import com.rr.core.codec.binary.fastfix.common.def.int32.IntMandWriterDefault;
import com.rr.core.codec.binary.fastfix.msgdict.DictComponentFactory;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int64.LongMandWriterDelta;
import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ComponentFactoryTest extends BaseTestCase {

    @SuppressWarnings( "boxing" )
    @Test
    public void testCompFactory() {
        DictComponentFactory cf = new DictComponentFactory();

        IntMandWriterDefault exp  = cf.getWriter( IntMandWriterDefault.class, "F1Exp", 100, 2 );
        LongMandWriterDelta  mant = cf.getWriter( LongMandWriterDelta.class, "F1Mant", 100, 10000L );

        assertEquals( 2, exp.getInitValue() );
        assertEquals( 10000, mant.getInitValue() );

        IntMandWriterDefault exp2  = cf.getWriter( IntMandWriterDefault.class, "F1Exp", 100, 2 );
        LongMandWriterDelta  mant2 = cf.getWriter( LongMandWriterDelta.class, "F1Mant", 100, 10000L );

        assertSame( exp, exp2 );
        assertSame( mant, mant2 );
    }
}
