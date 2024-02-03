/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix;

public final class PresenceMapWriter {

    private FastFixBuilder _encoder;       // start at most significant data bit down to LSB
    private byte[]         _buf;
    private int            _offset;
    private int            _initPMapSize;
    private int            _curPMapIndex;
    private int            _lastPMapIndex;
    private int            _nextBit;

    /**
     * correct presizing map is KEY for encode efficiency and avoid array shifting
     *
     * @param encoder
     * @param mapOffset
     * @param initialPMapSize
     */
    public PresenceMapWriter( FastFixBuilder encoder, int mapOffset, int initialPMapSize ) {
        set( encoder, mapOffset, initialPMapSize );
    }

    public PresenceMapWriter() {
        // constructor for sequence pMaps
    }

    public void clearCurrentField() {
        if ( _nextBit == 0 ) {
            checkGrow();
        }
        // bit already zero
        _nextBit >>= 1;
    }

    public void end() {
        _buf[ _curPMapIndex ] |= FastFixUtils.STOP_BIT;
    }

    public void reset() {
        _lastPMapIndex = _offset + _initPMapSize - 1;

        _encoder.insertBytes( _offset, _initPMapSize );

        _curPMapIndex = _offset;
        _nextBit      = FastFixUtils.MSB_DATA_BIT;
    }

    public void set( FastFixBuilder encoder, int mapOffset, int initialPMapSize ) {
        _encoder      = encoder;
        _buf          = encoder.getBuffer();
        _offset       = mapOffset;
        _initPMapSize = initialPMapSize;

        reset();
    }

    public void setCurrentField() {
        if ( _nextBit == 0 ) {
            checkGrow();
        }
        _buf[ _curPMapIndex ] |= (byte) _nextBit;
        _nextBit >>= 1;
    }

    private void checkGrow() {
        if ( _curPMapIndex == _lastPMapIndex ) {
            // need shift entire buffer down one byte

            ++_lastPMapIndex; // shift every byte AFTER current last pmap byte down the array one space

            _encoder.insertByte( _lastPMapIndex );
        }

        ++_curPMapIndex;
        _nextBit = FastFixUtils.MSB_DATA_BIT;
    }
}
