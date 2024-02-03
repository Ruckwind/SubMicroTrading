/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Instrument;

/**
 * for use when only read and write to book on same thread
 * <p>
 * bitflags were used to show which levels are set but given #writes much higher than #reads
 * overhead of maintenance not worth it ... especially with delete/adds
 *
 * @author Richard Rose
 */
public abstract class BaseFixedSizeBook extends AbstractBook implements ApiMutatableBook {

    protected int _numLevels;
    protected int _maxLevels;

    public BaseFixedSizeBook() { /* for reflective construction */
        super();
    }

    public BaseFixedSizeBook( Instrument instrument, int maxLevels ) {

        super( instrument );

        _numLevels = 0;
        _maxLevels = maxLevels;
    }

    @Override public final int getMaxLevels() { return _maxLevels; }
}
