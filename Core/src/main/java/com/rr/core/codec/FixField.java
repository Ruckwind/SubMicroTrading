/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

public class FixField {

    public static final byte FIELD_DELIMITER = 0x01;

    private static final int TEN = 10;

    public enum State {Empty, Ready, Invalid, Accepted}

    public State _state;

    public int _tag;
    public int _valLen;
    public int _valStartIdx;

    public FixField() {
        //
    }

    public int parse( byte[] buf, int idx, int len ) {

        _state = State.Empty;

        _tag = 0;

        byte ch;

        while( idx < len ) {
            ch = buf[ idx++ ];

            if ( ch == '=' )
                break;

            if ( ch < '0' || ch > '9' ) {
                _tag   = 0;
                _state = State.Invalid;

                while( idx < len ) {
                    if ( buf[ idx++ ] == FIELD_DELIMITER ) return idx;
                }

                return idx;
            }

            // TODO checkperf of shift vs * ..  run PerfTestNums on linux
            _tag = (_tag * TEN) + (ch - '0');
        }

        _valStartIdx = idx++;

        while( idx < len ) {
            if ( buf[ idx++ ] == FIELD_DELIMITER ) break;
        }

        _valLen = (idx - _valStartIdx) - 1;
        _state  = State.Ready;

        return idx;
    }
}
