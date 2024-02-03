/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;

public abstract class AbstractFixDecoderMD44 extends AbstractFixDecoder {

    private int _appVer;

    public AbstractFixDecoderMD44( byte major, byte minor ) {
        super( major, minor );
    }

    @Override
    public final int parseHeader( final byte[] fixMsg, final int offset, final int bytesRead ) {

        _fixMsg = fixMsg;
        _offset = offset;
        _idx    = offset;
        _maxIdx = bytesRead + offset; // temp assign maxIdx to last data bytes in buffer

        if ( bytesRead < 20 ) {
            ReusableString copy = TLC.instance().getString();
            if ( bytesRead == 0 ) {
                copy.setValue( "{empty}" );
            } else {
                copy.setValue( fixMsg, offset, bytesRead );
            }
            throw new RuntimeDecodingException( "Fix Messsage too small, len=" + bytesRead, copy );
        } else if ( fixMsg.length < _maxIdx ) {
            throwDecodeException( "Buffer too small for specified bytesRead=" + bytesRead + ",offset=" + offset + ", bufLen=" + fixMsg.length );
        }

        // 1128=9;35=D;10=123;

        int idx = _idx;
        if ( fixMsg[ idx++ ] == '1' && fixMsg[ idx++ ] == '1' && fixMsg[ idx++ ] == '2' && fixMsg[ idx++ ] == '8' ) {
            // fix version
            _idx = ++idx;

            _appVer = getIntVal();
            _idx++;
        }

        final int numBytesInMsg;

        if ( fixMsg[ _idx ] == '9' || fixMsg[ _idx + 1 ] == '=' ) {
            _idx += 2;
            _msgStatedLen = getIntVal();
            _idx++;

            numBytesInMsg = _msgStatedLen + CHECKSUM_LEN + (_idx - _offset);
            _maxIdx       = numBytesInMsg + _offset;  // correctly assign maxIdx as last bytes of current message
        } else {
            _msgStatedLen = bytesRead;
            numBytesInMsg = _msgStatedLen;
        }

        if ( _maxIdx > _fixMsg.length ) _maxIdx = _fixMsg.length;

        return numBytesInMsg;
    }

    public int getAppVer() {
        return _appVer;
    }
}
