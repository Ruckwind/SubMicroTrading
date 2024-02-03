package com.rr.core.collections;

public class SMTArrayList<T> extends java.util.ArrayList<T> {

    public SMTArrayList( final int initialCapacity ) {
        super( initialCapacity );
    }

    public SMTArrayList() {
        super();
    }

    @Override public void removeRange( int from, int upto ) { super.removeRange( from, upto ); }
}
