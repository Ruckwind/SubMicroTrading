package com.rr.core.collections;

import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.Event;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class DoubleLinkedNode implements Reusable<DoubleLinkedNode> {

    public static final class DoubleLinkedNodeFactory implements PoolFactory<DoubleLinkedNode> {

        private SuperPool<DoubleLinkedNode> _superPool;
        private DoubleLinkedNode            _root;

        public DoubleLinkedNodeFactory( SuperPool<DoubleLinkedNode> superPool ) {
            _superPool = superPool;
            _root      = _superPool.getChain();
        }

        @Override public DoubleLinkedNode get() {
            if ( _root == null ) {
                _root = _superPool.getChain();
            }
            DoubleLinkedNode obj = _root;
            _root = _root.getNext();
            obj.setNext( null );
            return obj;
        }

    }

    private Event            _value;
    private DoubleLinkedNode _prev;
    private DoubleLinkedNode _next;

    @Override public DoubleLinkedNode getNext()           { return _next; }

    @Override public void setNext( DoubleLinkedNode nxt ) { _next = nxt; }

    @Override public ReusableType getReusableType() {
        return CollectionTypes.DoubleLinkedMessageQueueNode;
    }

    @Override public void reset() {
        _prev  = null;
        _value = null;
        _next  = null;
    }

    public DoubleLinkedNode getPrev()                     { return _prev; }

    public void setPrev( final DoubleLinkedNode newPrev ) { _prev = newPrev; }

    public Event getValue()                               { return _value; }

    public void setVal( final Event val )                 { _value = val; }

    void set( DoubleLinkedNode prev, DoubleLinkedNode next, Event value ) {
        this._prev  = prev;
        this._next  = next;
        this._value = value;
    }
}
