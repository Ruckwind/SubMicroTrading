/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

public class DummyStats implements Stats {

    @Override public int find( final SizeType id )                { return id.getSize(); }

    @Override public void initialise()                            { /* nothing */ }

    @Override public void reload()                                { /* nothing */ }

    @Override public void set( final SizeType id, final int val ) { /* nothing */ }

    @Override public void store()                                 { /* nothing */ }
}
