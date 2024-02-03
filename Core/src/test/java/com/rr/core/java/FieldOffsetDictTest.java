package com.rr.core.java;

import com.rr.core.collections.CollectionTypes;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.logger.LogEventHuge;
import com.rr.core.logger.LogEventLarge;
import com.rr.core.logger.LogEventSmall;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.Event;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FieldOffsetDictTest extends BaseTestCase {

    static final class MessageHead extends BaseEvent {

        @Override public void dump( ReusableString out ) { /* nothing */ }

        @Override public ReusableType getReusableType() {
            return CollectionTypes.ConcurrentLinkedQueueHead;
        }
    }
    private FieldOffsetDict _nextMessageOffset;

    @Test
    public void messageHead() {
        MessageHead   m1 = new MessageHead();
        LogEventSmall m2 = new LogEventSmall();
        LogEventLarge m3 = new LogEventLarge();

        m1.attachQueue( m2 );

        assertTrue( casSetNextEntry( m1, m2, m3 ) );

        assertSame( m3, m1.getNextQueueEntry() );
    }

    @Test
    public void nextMessageTst() {
        LogEventHuge  m1 = new LogEventHuge();
        LogEventSmall m2 = new LogEventSmall();
        LogEventLarge m3 = new LogEventLarge();

        m1.attachQueue( m2 );

        assertTrue( casSetNextEntry( m1, m2, m3 ) );

        assertSame( m3, m1.getNextQueueEntry() );
    }

    @Before public void setUp() {
        _nextMessageOffset = new FieldOffsetDict( Event.class, "_nextMessage" );
    }

    private boolean casSetNextEntry( Event entry, Event cmp, Event val ) {
        long offset = _nextMessageOffset.getOffset( entry, true );
        return JavaSpecific.instance().compareAndSwapObject( entry, offset, cmp, val );
    }

}
