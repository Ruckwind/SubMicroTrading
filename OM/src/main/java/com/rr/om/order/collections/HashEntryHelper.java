/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.lang.ViewString;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

/**
 * non thread safe helper to create/recycle HashEntries
 */
public final class HashEntryHelper {

    private HashEntryFactory    _entryFactory  = SuperpoolManager.instance().getFactory( HashEntryFactory.class, HashEntry.class );
    private Recycler<HashEntry> _entryRecycler = SuperpoolManager.instance().getRecycler( HashEntry.class );

    HashEntryHelper() {
        // dont use static factory/recycler
    }

    HashEntry[] newArray( int size ) {
        return new HashEntry[ size ];
    }

    HashEntry newEntry( ViewString key, int hash, HashEntry next, Order value ) {
        HashEntry entry = _entryFactory.get();
        entry.set( key, hash, next, value );
        return entry;
    }

    void recycleEntry( HashEntry entry, EventProcessor proc ) {
        if ( entry != null ) {

            HashEntry tmp;

            while( entry != null ) {

                // DONT RECYCLE the KEY !!

                tmp   = entry;
                entry = entry.getNext();
                tmp.setNext( null );

                if ( proc != null ) {
                    proc.freeOrder( tmp._value ); // note order may have multiple refs, may already have been recycled
                }

                _entryRecycler.recycle( tmp );
            }
        }
    }

}
