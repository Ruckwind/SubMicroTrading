/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix;

import com.rr.core.lang.ReusableString;

public final class PresenceMapReader {

    private byte[] _buf;
    private int    _offset;
    private int    _curPMapIndex;
    private int    _nextBit;
    private int    _lastPMapIndex;

    /**
     * correct presizing map is KEY for encode efficiency and avoid array shifting
     */
    public PresenceMapReader() {
    }

    public boolean isNextFieldPresent() {
        if ( _nextBit == 0 ) {
            if ( _curPMapIndex == _lastPMapIndex ) {
                // attempt to go beyond presence map
                return false;
            }
            ++_curPMapIndex;
            _nextBit = FastFixUtils.MSB_DATA_BIT;
        }

        boolean present = (_buf[ _curPMapIndex ] & (byte) _nextBit) != 0;

        _nextBit >>= 1;

        return present;
    }

    /**
     * set this pMap to current decoder position then move current index to end of pMap
     *
     * @return next index after pMap
     */
    public void readMap( FastFixDecodeBuilder decoder ) {
        _buf     = decoder.getBuffer();
        _offset  = decoder.getCurrentIndex();
        _nextBit = FastFixUtils.MSB_DATA_BIT;

        _curPMapIndex = _offset;
        _nextBit      = FastFixUtils.MSB_DATA_BIT;

        _lastPMapIndex = decoder.skipPMap();
    }

    public void trace( ReusableString log ) {
        log.append( "pMap nxtBit=" ).append( _nextBit ).append( ", _mapIdx=" ).append( _curPMapIndex );
    }
}
