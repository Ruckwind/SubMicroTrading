/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.model.Event;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.core.utils.lock.StampedLockProxy;

import java.util.Iterator;

/**
 * blocks on an add if queue full
 * <p>
 * Iterator NOT threadsafe BUT you can make by wrapping iteration by taking out lock which is exposed
 */
public class ConcBlockingSyncLinkedQueue implements EventQueue {

    /**
     * Pointer to header node, initialized to a dummy node.  The first actual node is at head.getNext().
     * The header node itself never changes
     */
    private final SingleLinkedNode _head = new SingleLinkedNode();
    private final String           _id;
    private final SingleLinkedNode.SingleLinkedNodeFactory _entryFactory;
    private final Recycler<SingleLinkedNode>               _entryRecycler;
    private final StampedLockProxy _lock = new StampedLockProxy();
    private       SingleLinkedNode _tail = _head;

    public ConcBlockingSyncLinkedQueue() {
        this( null );
    }

    public ConcBlockingSyncLinkedQueue( String id ) {
        _id = id;
        Recycler<SingleLinkedNode> entryRecycler;
        _entryFactory  = SuperpoolManager.instance().getFactory( SingleLinkedNode.SingleLinkedNodeFactory.class, SingleLinkedNode.class );
        entryRecycler  = SuperpoolManager.instance().getRecycler( SingleLinkedNode.class );
        _entryRecycler = entryRecycler;
    }

    @Override public String getComponentId() {
        return _id;
    }

    /**
     * @return
     * @WARNING non thread safe iterator
     * <p>
     * lock = q.getLock();
     * stamp = lock.readLock();
     * <p>
     * try {
     * get iterator AND iterate .... blocking writing to queue during iteration
     * } finally {
     * lock.unlockRead( stamp );
     * }
     */
    @Override public Iterator<Event> iterator() { return new QueueIterator( _head.getNext() ); }

    @Override public EventQueue newInstance() {
        return new ConcBlockingSyncLinkedQueue( _id );
    }

    @Override public boolean add( Event e ) {

        final OptimisticReadWriteLock lock = getLock();

        boolean signal = false;

        long stamp = lock.writeLock();
        try {
            signal = (_head == _tail);

            final SingleLinkedNode newNode = _entryFactory.get();
            newNode.setValue( e );
            _tail.setNext( newNode );
            _tail = newNode;

        } finally {
            lock.unlockWrite( stamp );
        }

        if ( signal ) {
            synchronized( _head ) {
                _head.notifyAll();
            }
        }

        return true;
    }

    @Override public boolean canMessageAttachMultipleQueues() { return false; }

    @Override public void clear() {
        while( poll() != null ) ;
    }

    @Override public boolean isEmpty() {

        boolean isEmpty = false;

        final OptimisticReadWriteLock lock = getLock();

        long stamp = lock.tryOptimisticRead();

        isEmpty = (_head.getNext() == null);

        if ( !lock.validate( stamp ) ) {

            stamp = lock.readLock();

            try {
                isEmpty = (_head.getNext() == null);
            } finally {
                lock.unlockRead( stamp );
            }
        }

        return isEmpty;
    }

    @Override public Event next() {
        Event t = null;

        do {
            final OptimisticReadWriteLock lock = getLock();

            boolean wait  = false;
            long    stamp = lock.writeLock();
            try {
                if ( _tail != _head ) {

                    final SingleLinkedNode node = _head.getNext();
                    t = node.getValue();

                    if ( _tail != node ) {
                        _head.setNext( node.getNext() );
                    } else {
                        _head.setNext( null );
                        _tail = _head;
                    }
                    _entryRecycler.recycle( node );

                } else {
                    wait = true;
                }

            } finally {
                lock.unlockWrite( stamp );
            }

            if ( wait ) {
                synchronized( _head ) {
                    try {
                        _head.wait();
                    } catch( InterruptedException e ) {
                        // dont care
                    }
                }
            }

        } while( t == null );

        return t;
    }

    @Override
    public Event poll() {
        Event t;

        final OptimisticReadWriteLock lock = getLock();

        long stamp = lock.writeLock();
        try {
            if ( _tail == _head ) return null;

            final SingleLinkedNode node = _head.getNext();

            t = node.getValue();

            if ( _tail != node ) {
                _head.setNext( node.getNext() );
            } else {
                _head.setNext( null );
                _tail = _head;
            }
            _entryRecycler.recycle( node );

        } finally {
            lock.unlockWrite( stamp );
        }

        return t;
    }

    @Override public int size() {
        int              cnt = 0;
        SingleLinkedNode m;

        final OptimisticReadWriteLock lock = getLock();

        long stamp = lock.tryOptimisticRead();

        m = _head.getNext();
        while( m != null ) {
            ++cnt;
            m = m.getNext();
        }

        if ( !lock.validate( stamp ) ) {

            stamp = lock.readLock();

            try {
                cnt = 0;
                m   = _head.getNext();
                while( m != null ) {
                    ++cnt;
                    m = m.getNext();
                }
            } finally {
                lock.unlockRead( stamp );
            }
        }

        return cnt;
    }

    public OptimisticReadWriteLock getLock() {
        return _lock;
    }

    public class QueueIterator implements Iterator<Event> {

        private SingleLinkedNode _next;

        public QueueIterator( SingleLinkedNode root ) {
            _next = root;
        }

        @Override public boolean hasNext() {
            return _next != null;
        }

        @Override public Event next() {
            SingleLinkedNode curr = _next;
            if ( curr == null ) return null;
            _next = _next.getNext();
            return curr.getValue();
        }
    }
}
