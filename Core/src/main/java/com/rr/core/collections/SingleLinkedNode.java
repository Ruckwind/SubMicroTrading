package com.rr.core.collections;

import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.Event;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.SuperPool;

public class SingleLinkedNode implements Reusable<SingleLinkedNode> {

    public static final class SingleLinkedNodeFactory implements PoolFactory<SingleLinkedNode> {

        private SuperPool<SingleLinkedNode> _superPool;
        private SingleLinkedNode            _root;

        public SingleLinkedNodeFactory( SuperPool<SingleLinkedNode> superPool ) {
            _superPool = superPool;
            _root      = _superPool.getChain();
        }

        @Override public SingleLinkedNode get() {
            if ( _root == null ) {
                _root = _superPool.getChain();
            }
            SingleLinkedNode obj = _root;
            _root = _root.getNext();
            obj.setNext( null );
            return obj;
        }

    }

    private Event            _value;
    private SingleLinkedNode _next;

    @Override public SingleLinkedNode getNext()           { return _next; }

    @Override public void setNext( SingleLinkedNode nxt ) { _next = nxt; }

    @Override public ReusableType getReusableType() {
        return CollectionTypes.SingleNode;
    }

    @Override public void reset() {
        _value = null;
        _next  = null;
    }

    public Event getValue()                               { return _value; }

    public void setValue( final Event value )             { _value = value; }

    void set( SingleLinkedNode next, Event value ) {
        this._next  = next;
        this._value = value;
    }
}
