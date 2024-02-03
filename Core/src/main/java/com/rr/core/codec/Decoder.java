/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.TimeUtils;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.InstrumentLocator;

public interface Decoder extends SMTComponent {

    enum ResyncCode {FOUND_FULL_HEADER, FOUND_PARTIAL_HEADER_NEED_MORE_DATA}

    /**
     * decode message of unknown length, must decode message to determine length
     * <p>
     * decode can create multiple different messages using message chain
     *
     * @param msg
     * @param offset offset data starts at in buffer
     * @param maxIdx maxIdx in buffer
     * @return
     */
    Event decode( final byte[] msg, final int offset, final int maxIdx );

    default String getEndTimeFilter() { return null; }

    InstrumentLocator getInstrumentLocator();

    void setInstrumentLocator( InstrumentLocator instrumentLocator );

    /**
     * @return last index parsed minus offset .. ie bytes consumes in decode
     */
    int getLength();

    long getReceived();

    void setReceived( long nanos );

    /**
     * only to be used after invoking resync
     *
     * @return the number of bytes skipped to reach start of next header ... should be used to shift left buffer before reinvoking parseHeader
     */
    int getSkipBytes();

    /**
     * @return optional start date filter YYYY-MM-DD
     */
    default String getStartTimeFilter() { return null; }

    /**
     * verify the header is as expected
     *
     * @param inBuffer
     * @param inHdrLen  offset to start of lva
     * @param bytesRead
     * @return total length of message from start of the header to end of the trailer (not the same as tag 9 in fix)
     * -1 if inBuffer doesnt have expected header information
     */
    int parseHeader( byte[] inBuffer, int inHdrLen, int bytesRead );

    /**
     * message now fully read continue processing from parseHeader call
     *
     * @return
     */
    Event postHeaderDecode();

    /**
     * when parse header fails, the resync can be called to try and rescyn the data stream
     * <p>
     * if find full header ... invoke getSkipped() to find how many bytes lost, user must shiftLeft then invoke parseHeader to parse hdr
     * if find partial header at end of buffer shift left ... invoke getSkipped() to find how many bytes lost, user must read more lva
     * if WASTE throw away buffer as unusable
     *
     * @param fixMsg
     * @param offset
     * @param maxIdx
     * @return FOUND_FULL_HEADER - found complete header,
     * FOUND_PARTIAL_HEADER_NEED_MORE_DATA
     * @throws RuntimeDecodingException if buffer doesnt contain a header
     */
    ResyncCode resync( final byte[] fixMsg, final int offset, final int maxIdx );

    // utility methods
    void setClientProfile( ClientProfile client );

    /**
     * @param nanoTiming if true enable nano stat collection
     */
    void setNanoStats( boolean nanoTiming );

    /**
     * @param calc - time zone calculator to use
     */
    void setTimeUtils( TimeUtils calc );

}
