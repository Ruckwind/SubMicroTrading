/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.utils.NumberFormatUtils;

/**
 * used to decode readable FastFix messages which have been decoded from binary to readable ASCII
 * this is for market data messages of format  eg :-
 * <p>
 * 1128=9|35=X|49=CME|34=5457711|52=20120403194342222|75=20120403|268=1|279=1|1023=1|269=0|273=194342000|22=8|48=27069|83=7952927|270=252184990490.0|271=35|346=10|336=2|
 *
 * @author Richard Rose
 */
public final class ReadableMDFastFixEncoder extends BaseFixEncodeBuilderImpl {

    private final static int  APPL_VER_ID     = 1128;
    private static final byte APPL_VER_ID_VAL = '9';

    // 1128=9;9=0000;
    private static final int DATA_OFFSET = 14;

    private final int    _startBodyOffset;
    private final byte[] _hdr;
    private final int    _hdrLen;
    private final int    _lastIdxForBodyLen;

    private final byte _applVerId;

    public ReadableMDFastFixEncoder( byte[] buffer, int offset, byte major, byte minor ) {
        super( buffer, offset );
        _applVerId = APPL_VER_ID_VAL; // @TODO get from config

        _hdr    = ("1128=" + (char) _applVerId + (char) FixField.FIELD_DELIMITER + "9=").getBytes();
        _hdrLen = _hdr.length + 1;

        _startBodyOffset = _startOffset + DATA_OFFSET;

        _lastIdxForBodyLen = _startBodyOffset - 1;
    }

    @Override
    public void encodeEnvelope() {

        encodeFixHeader();
        encodeChecksum();

        _msgLen = _idx - _msgOffset;
    }

    @Override
    public void start() {
        _idx = _startBodyOffset;
    }

    public void encodeEnvelopeNoLenOrChecksum() {
        _msgLen = _idx - _msgOffset;
    }

    /**
     * start encoding a fast fix readable message without tags 8, 9, 10
     */
    public void startNoLenOrChecksum() {
        _idx = _startOffset;
        encodeByte( APPL_VER_ID, _applVerId );
    }

    private void encodeFixHeader() {

        //           1         2
        // 0123456789012345678901234
        // 1128=9;9=0000;35=D

        final int saved = _idx;

        final int bodyLen = _idx - _startBodyOffset;
        final int lenSize = NumberFormatUtils.getPosIntLen( bodyLen );
        _msgOffset = _startBodyOffset - (_hdrLen + lenSize);

        System.arraycopy( _hdr, 0, _buffer, _msgOffset, _hdr.length );

        _idx = _lastIdxForBodyLen - lenSize;

        writePosInt( bodyLen, lenSize );
        writeFixDelimiter();

        _idx = saved;
    }
}
