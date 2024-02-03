/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.idgen;

import com.rr.core.idgen.IDGenerator;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public abstract class AbstractBase32IDGenerator implements IDGenerator {

    private static final byte[] _bytes = "1234567890ABCDEFGHIJKLMNOPQRSTUVW".getBytes();

    private byte[] _prefix;
    private long   _counter;

    private int _totalLen;
    private int _prefixLen;

    public AbstractBase32IDGenerator() {
        /* for reflection */
    }

    /**
     * @param processInstanceSeed
     * @param len                 length of non seed component
     */
    public AbstractBase32IDGenerator( ZString processInstanceSeed, int len ) {
        _prefix = processInstanceSeed.getBytes();

        _totalLen  = len + _prefix.length;
        _prefixLen = _prefix.length;

        _counter = seed();
    }

    @Override
    public void genID( ReusableString id ) {

        _counter++;

        long tmpId = _counter;

        id.setLength( _totalLen );

        byte[] dest = id.getBytes();

        int destIdx = _totalLen;

        while( destIdx > _prefixLen ) {
            final int idx = (int) (tmpId & 0x1F);
            dest[ --destIdx ] = _bytes[ idx ];

            tmpId >>>= 5;                                   // base 32, ideally would be base 64, but only 62 alphaNum chars
        }

        while( --destIdx >= 0 ) {
            dest[ destIdx ] = _prefix[ destIdx ];
        }
    }

    protected abstract long seed();
}
