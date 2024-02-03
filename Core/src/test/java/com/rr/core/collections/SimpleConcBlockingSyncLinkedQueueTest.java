package com.rr.core.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.Event;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SimpleConcBlockingSyncLinkedQueueTest extends BaseTestCase {

    private static final class SimpleMsg extends BaseEvent<SimpleMsg> {

        private final String _id;

        public SimpleMsg( final String id ) { _id = id; }

        @Override public void dump( final ReusableString out ) {
            out.append( _id );
        }

        @Override public ReusableType getReusableType() { return CoreReusableType.NotReusable; }
    }

    @Test public void addFour() {
        ConcBlockingSyncLinkedQueue q = new ConcBlockingSyncLinkedQueue( "myq" );

        final Event m1 = mkMsg( "a1" );
        final Event m2 = mkMsg( "a2" );
        final Event m3 = mkMsg( "a3" );
        final Event m4 = mkMsg( "a4" );

        q.add( m1 );
        q.add( m2 );
        q.add( m3 );
        q.add( m4 );

        checkNext( q, m1, 3 );
        checkNext( q, m2, 2 );
        checkNext( q, m3, 1 );
        checkNext( q, m4, 0 );
    }

    @Test public void addFourJavaQ() {
        ConcurrentLinkedQueue<Event> q = new ConcurrentLinkedQueue<Event>();

        final Event m1 = mkMsg( "a1" );
        final Event m2 = mkMsg( "a2" );
        final Event m3 = mkMsg( "a3" );
        final Event m4 = mkMsg( "a4" );

        q.add( m1 );
        q.add( m2 );
        q.add( m3 );
        q.add( m4 );
    }

    @Test public void addOne() {
        ConcBlockingSyncLinkedQueue q = new ConcBlockingSyncLinkedQueue( "myq" );

        final Event m1 = mkMsg( "a1" );

        q.add( m1 );

        checkNext( q, m1, 0 );
    }

    @Test public void addThree() {
        ConcBlockingSyncLinkedQueue q = new ConcBlockingSyncLinkedQueue( "myq" );

        final Event m1 = mkMsg( "a1" );
        final Event m2 = mkMsg( "a2" );
        final Event m3 = mkMsg( "a3" );

        q.add( m1 );
        q.add( m2 );
        q.add( m3 );

        checkNext( q, m1, 2 );
        checkNext( q, m2, 1 );
        checkNext( q, m3, 0 );
    }

    @Test public void addTwo() {
        ConcBlockingSyncLinkedQueue q = new ConcBlockingSyncLinkedQueue( "myq" );

        final Event m1 = mkMsg( "a1" );
        final Event m2 = mkMsg( "a2" );

        q.add( m1 );
        q.add( m2 );

        checkNext( q, m1, 1 );
        checkNext( q, m2, 0 );
    }

    private void checkNext( final ConcBlockingSyncLinkedQueue q, final Event expected, final int postSize ) {
        Event nxt = q.poll();

        assertSame( expected, nxt );
        assertEquals( postSize, q.size() );
    }

    private Event mkMsg( final String id ) {
        return new SimpleMsg( id );
    }
}
