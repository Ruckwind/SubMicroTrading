/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.codec.binary.BinaryDecodeBuilder;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.InstrumentLocator;

public abstract class AbstractBinaryDecoder implements BinaryDecoder {

    protected static final Logger _log = LoggerFactory.create( AbstractBinaryDecoder.class );
    protected static final ZString RECEIVED = new ViewString( ", received=" );
    private static final int MAX_BYTES_COPY_FIXMSG = 512;
    private static final ErrorCode DECODE_EXCEPTION = new ErrorCode( "ABD100", "Failed to decode message" );
    private static final ErrorCode INDEX_EXCEPTION  = new ErrorCode( "ABD200", "Index exception" );
    private static final ErrorCode OTHER_EXCEPTION  = new ErrorCode( "ABD300", "Unexpected exception" );
    protected final byte[]            _today = new byte[ TimeUtils.DATE_STR_LEN ];
    protected ReusableString _errMsg       = new ReusableString( 100 );
    protected TimeUtils      _tzCalculator = TimeUtilsFactory.createTimeUtils();
    protected byte[]  _binaryMsg;
    protected int     _maxIdx;
    protected int     _offset;
    protected boolean _nanoStats = true;
    protected ClientProfile _clientProfile = null;
    protected       InstrumentLocator _instrumentLocator;
    protected       long              _received;   // to be used in hooks in generated code
    private   int     _skipCount;            // used in resync denotes start index of next fix header

    public AbstractBinaryDecoder() {
        _tzCalculator.getToday( _today );
        _log.info( "Initialising decoder to UTC date " + new String( _today ) );
    }

    @Override
    public final Event decode( final byte[] fixMsg, final int offset, final int maxIdx ) {

        try {
            parseHeader( fixMsg, offset, maxIdx - offset );

            return doMessageDecode();

        } catch( RuntimeDecodingException e ) {
            dumpPartialProcessedMsg();
            _log.error( DECODE_EXCEPTION, e.getMessage() );
            return rejectDecodeException( e );
        } catch( IndexOutOfBoundsException e ) {
            _log.error( INDEX_EXCEPTION, e.getMessage() );
            return rejectIndexOutOfBoundsException( e );
        } catch( Throwable t ) {
            _log.error( OTHER_EXCEPTION, t.getMessage() );
            return rejectThrowable( t );
        }
    }

    @Override
    public InstrumentLocator getInstrumentLocator() {
        return _instrumentLocator;
    }

    @Override
    public void setInstrumentLocator( InstrumentLocator locator ) {
        _instrumentLocator = locator;
    }

    @Override
    public int getLength() {
        return getBuilder().getLength();
    }

    @Override
    public final long getReceived() {
        return _received;
    }

    @Override
    public final void setReceived( long nanos ) {
        _received = nanos;
    }

    @Override
    public final int getSkipBytes() {
        return _skipCount;
    }

    /**
     * the caller should of already checked the fixVersion and len, so skip tags 8 and 9
     *
     * @param fixMsg
     * @param offset
     * @return
     */
    @Override
    public abstract int parseHeader( final byte[] fixMsg, final int offset, final int bytesRead );

    @Override
    public Event postHeaderDecode() {
        try {
            return doMessageDecode();
        } catch( RuntimeDecodingException e ) {
            _log.error( DECODE_EXCEPTION, e.getMessage() );
            return rejectDecodeException( e );
        } catch( IndexOutOfBoundsException e ) {
            _log.error( INDEX_EXCEPTION, e.getMessage() );
            return rejectIndexOutOfBoundsException( e );
        } catch( Throwable t ) {
            _log.error( OTHER_EXCEPTION, t.getMessage() );
            return rejectThrowable( t );
        }
    }

    @Override
    public final ResyncCode resync( final byte[] fixMsg, final int offset, final int maxIdx ) {

        throwDecodeException( "Binary protocols dont support resync after corrupt message" );

        return null;
    }

    public final ClientProfile getClientProfile() {
        return _clientProfile;
    }

    @Override
    public void setClientProfile( ClientProfile clientProfile ) {
        _clientProfile = clientProfile;
    }

    @Override
    public void setNanoStats( boolean nanoTiming ) {
        _nanoStats = nanoTiming;
    }

    @Override
    public final void setTimeUtils( TimeUtils calc ) {
        _tzCalculator = calc;
        _tzCalculator.getToday( _today );
    }

    public final TimeUtils getTimeZoneCalculator() {
        return _tzCalculator;
    }

    protected abstract Event doMessageDecode();

    protected void dumpPartialProcessedMsg() {
        getBuilder().end();
    }

    protected abstract BinaryDecodeBuilder getBuilder();

    protected abstract int getCurrentIndex();

    protected Event rejectDecodeException( RuntimeDecodingException e ) {
        return new RejectDecodeException( _binaryMsg, _offset, _maxIdx, e );
    }

    protected final void throwDecodeException( String errMsg ) {
        int len     = (_maxIdx < _offset) ? 0 : _maxIdx - _offset;
        int copyLen = (len + _offset > _binaryMsg.length) ? _binaryMsg.length - _offset : _maxIdx - _offset;

        if ( copyLen > MAX_BYTES_COPY_FIXMSG ) {
            copyLen = MAX_BYTES_COPY_FIXMSG;
        }

        ReusableString copy = TLC.instance().getString();

        if ( _offset >= 0 && copyLen > 0 ) {
            copy.setValue( _binaryMsg, _offset, copyLen );
        }

        int idx       = getCurrentIndex();
        int msgBadIdx = idx - _offset;

        throw new RuntimeDecodingException( errMsg + ", len=" + len + ", idx=" + idx + ", offset=" + _offset +
                                            ", offsetWithinMsg=" + msgBadIdx, copy );
    }

    private Event rejectIndexOutOfBoundsException( IndexOutOfBoundsException e ) {
        return new RejectIndexOutOfBounds( _binaryMsg, _offset, _maxIdx, e );
    }

    private Event rejectThrowable( Throwable t ) {
        return new RejectThrowable( _binaryMsg, _offset, _maxIdx, t );
    }
}
