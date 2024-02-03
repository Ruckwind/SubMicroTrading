/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.FixVersion;
import com.rr.core.model.InstrumentLocator;

public abstract class AbstractFixDecoder implements FixDecoder {

    protected static final int CHECKSUM_LEN = 7;     // 10=nnn<SOH>
    protected static final ZString RECEIVED              = new ViewString( ", received=" );
    protected static final ZString INVALID_SENDER_ID     = new ViewString( "Invalid senderCompId, expected=" );
    protected static final ZString INVALID_SENDER_SUB_ID = new ViewString( "Invalid senderSubId, expected=" );
    protected static final ZString INVALID_TARGET_ID     = new ViewString( "Invalid targetCompId, expected=" );
    protected static final ZString INVALID_TARGET_SUB_ID = new ViewString( "Invalid targetSubId, expected=" );
    private static final Logger _log = LoggerFactory.create( AbstractFixDecoder.class );
    private static final int SKIP_MSG_SHOW_BYTES                   = 512;
    private static final int MIN_FIX_HEADER_BYTES_FOR_LENGTH_FIELD = 18;
    private static final int MAX_BYTES_COPY_FIXMSG                 = 512;
    private static final ErrorCode DECODE_EXCEPTION = new ErrorCode( "AFD100", "Failed to decode message" );
    private static final ErrorCode INDEX_EXCEPTION  = new ErrorCode( "AFD200", "Index exception" );
    private static final ErrorCode OTHER_EXCEPTION  = new ErrorCode( "AFD300", "Unexpected exception" );
    protected final ReusableString _errMsg       = new ReusableString( 100 );
    protected final byte _majorVersion;
    protected final byte _minorVersion;
    protected final ReusableString _senderCompId  = new ReusableString();
    protected final ReusableString _senderSubId   = new ReusableString();
    protected final ReusableString _targetCompId  = new ReusableString();
    protected final ReusableString _targetSubId   = new ReusableString();
    private final TimeUtils.DateParseResults _dateResult = new TimeUtils.DateParseResults();
    protected       TimeUtils      _tzCalculator = TimeUtilsFactory.createTimeUtils();
    protected int     _tag       = 0;
    protected int     _idx       = 0;
    protected byte[]  _fixMsg;
    protected int     _maxIdx;
    protected int     _offset;

    // execRpt fields
    protected boolean _nanoStats = true;
    protected int     _msgStatedLen;
    protected ClientProfile _clientProfile = null;
    protected InstrumentLocator _instrumentLocator;
    protected       long           _received;   // to be used in hooks in generated code
    private   int     _skipCount;            // used in resync denotes start index of next fix header
    private         boolean        _verifyHdrVals = true;
    private boolean _validateChecksum = true;

    public AbstractFixDecoder( FixVersion ver ) {
        this( ver._major, ver._minor );
    }

    public AbstractFixDecoder( byte major, byte minor ) {
        _majorVersion = major;
        _minorVersion = minor;
        _log.info( "Initialising decoder to UTC date " + _tzCalculator.toString() );
    }

    @Override public boolean isVerifyHdrVals()                            { return _verifyHdrVals; }

    @Override public void setVerifyHdrVals( final boolean verifyHdrVals ) { _verifyHdrVals = verifyHdrVals; }

    @Override
    public void setSenderCompId( ZString senderCompId ) {
        _senderCompId.copy( senderCompId );
    }    @Override
    public int getLength() {
        return _idx - _offset;
    }

    @Override
    public void setSenderSubId( ZString senderSubId ) {
        _senderSubId.copy( senderSubId );
    }

    @Override
    public void setTargetCompId( ZString targetCompId ) {
        _targetCompId.copy( targetCompId );
    }

    @Override
    public void setTargetSubId( ZString targetSubId ) {
        _targetSubId.copy( targetSubId );
    }    @Override
    public void setNanoStats( boolean nanoTiming ) {
        _nanoStats = nanoTiming;
    }

    @Override
    public void setValidateChecksum( boolean doValidate ) {
        _validateChecksum = doValidate;
    }

    public final ClientProfile getClientProfile() {
        return _clientProfile;
    }

    @Override
    public void setClientProfile( ClientProfile clientProfile ) {
        _clientProfile = clientProfile;
    }

    public final TimeUtils getTimeZoneCalculator() {
        return _tzCalculator;
    }

    public void setCompIds( ZString senderCompId, ZString senderSubId, ZString targetCompId, ZString targetSubId ) {
        setSenderCompId( senderCompId );
        setSenderSubId( senderSubId );
        setTargetCompId( targetCompId );
        setTargetSubId( targetSubId );
    }

    public void setCompIds( String senderCompId, String senderSubId, String targetCompId, String targetSubId ) {
        setSenderCompId( new ReusableString( senderCompId ) );
        setSenderSubId( new ReusableString( senderSubId ) );
        setTargetCompId( new ReusableString( targetCompId ) );
        setTargetSubId( new ReusableString( targetSubId ) );
    }    @Override
    public void setInstrumentLocator( InstrumentLocator locator ) {
        _instrumentLocator = locator;
    }

    protected final void decodeSenderCompID() {
        if ( _verifyHdrVals && _targetCompId.length() > 0 ) {

            verifyValue( _targetCompId, INVALID_SENDER_ID );

        } else { // dont care about the value, nothing to compare against
            getValLength();
        }
    }    @Override
    public InstrumentLocator getInstrumentLocator() {
        return _instrumentLocator;
    }

    protected final void decodeSenderSubID() {
        if ( _verifyHdrVals && _targetSubId.length() > 0 ) {

            verifyValue( _targetSubId, INVALID_SENDER_SUB_ID );

        } else { // dont care about the value, nothing to compare against
            getValLength();
        }
    }

    protected final void decodeTargetCompID() {
        if ( _verifyHdrVals && _senderCompId.length() > 0 ) {

            verifyValue( _senderCompId, INVALID_TARGET_ID );

        } else { // dont care about the value, nothing to compare against
            getValLength();
        }
    }    @Override
    public final void setTimeUtils( TimeUtils calc ) {
        _tzCalculator = calc;

        _log.info( "Setting decoder " + getClass().getName() + " " + _tzCalculator );
    }

    protected final void decodeTargetSubID() {
        if ( _verifyHdrVals && _senderSubId.length() > 0 ) {

            verifyValue( _senderSubId, INVALID_TARGET_SUB_ID );

        } else { // dont care about the value, nothing to compare against
            getValLength();
        }
    }    @Override
    public final void setReceived( long nanos ) {
        _received = nanos;
    }

    protected abstract Event doMessageDecode();    @Override
    public final long getReceived() {
        return _received;
    }

    /**
     * for EXEC messages from exchange dont verify the comp ids on decoding
     */
    protected final void execCheckSenderCompID() { /* disabled */ }

    protected final void execCheckSenderSubID()  { /* disabled */ }

    protected final void execCheckTargetCompID() { /* disabled */ }    @Override
    public final Event decode( final byte[] fixMsg, final int offset, final int maxIdx ) {

        try {
            parseHeader( fixMsg, offset, maxIdx - offset );

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

    protected final void execCheckTargetSubID()  { /* disabled */ }

    protected final double getDoubleVal() {
        while( _idx < _maxIdx && _fixMsg[ _idx ] == ' ' ) {
            _idx++;
        }

        if ( _idx >= _maxIdx ) return Constants.UNSET_DOUBLE;

        boolean negative = false;

        if ( _fixMsg[ _idx ] == '+' ) {
            _idx++;
            if ( _idx >= _maxIdx ) throwDecodeException( "Cant parse double as message appears truncated" );
        } else if ( _fixMsg[ _idx ] == '-' ) {
            _idx++;
            negative = true;
            if ( _idx >= _maxIdx ) throwDecodeException( "Cant parse double as message appears truncated" );
        }

        // Find the integer part
        long    wholePart = 0;
        boolean endPart   = false;

        byte digit = 0;

        while( !endPart ) {
            digit = _fixMsg[ _idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                wholePart *= 10;
                wholePart += (digit - '0');

            } else if ( digit == '.' || digit == ' ' ) {
                endPart = true;
            } else if ( digit == FixField.FIELD_DELIMITER ) {

                _idx--;

                return negative ? -wholePart : wholePart;
            } else {
                throwDecodeException( "Double has non numeric chars" );
            }

            if ( wholePart < 0 ) throwDecodeException( "Double is too big" );
        }

        if ( digit == ' ' ) {
            while( _fixMsg[ _idx++ ] == ' ' ) {
                // consume space
            }

            if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER ) {
                _idx--;
                return negative ? -wholePart : wholePart;
            }

            throwDecodeException( "unexpect byte " + _fixMsg[ _idx - 1 ] + " in double" );
        }

        if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER )
            return negative ? -wholePart : wholePart;

        // only support prices to Constants.PRICE_DP
        int priceFractionAsInt = 0;
        endPart = false;

        int dp = 0;

        while( !endPart ) {
            digit = _fixMsg[ _idx++ ];

            if ( digit >= '0' && digit <= '9' ) {

                if ( dp++ < Constants.PRICE_DP_L ) {
                    priceFractionAsInt *= 10;
                    priceFractionAsInt += (digit - '0');
                }
            } else if ( digit == ' ' || digit == FixField.FIELD_DELIMITER ) {
                endPart = true;
            } else {
                throwDecodeException( "Double has non numeric chars" );
            }
        }

        for ( int i = dp; i < Constants.PRICE_DP_L; i++ ) {
            priceFractionAsInt *= 10;
        }

        if ( digit == ' ' ) {
            while( _fixMsg[ _idx++ ] == ' ' ) {
                // consume space
            }

            if ( _fixMsg[ _idx ] != FixField.FIELD_DELIMITER ) {
                throwDecodeException( "unexpect byte " + _fixMsg[ _idx - 1 ] + " in double" );
            }
        } else {
            _idx--; // need idx to be on the delimiter
        }

        return negative ? -(wholePart + (priceFractionAsInt / Constants.PRICE_DP_L_DFACTOR)) : (wholePart + (priceFractionAsInt / Constants.PRICE_DP_L_DFACTOR));
    }    @Override
    public Event postHeaderDecode() {
        try {
            Event e = doMessageDecode();

            if ( e == null ) {
                return ignoreEvent();
            }

            return e;

        } catch( RuntimeDecodingException e ) {
            return rejectDecodeException( e );
        } catch( IndexOutOfBoundsException e ) {
            return rejectIndexOutOfBoundsException( e );
        } catch( Throwable t ) {
            return rejectThrowable( t );
        }
    }

    protected final int getIntVal() {

        if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER )
            return Constants.UNSET_INT;

        boolean isNeg = false;
        int     value = 0;
        while( _idx <= _maxIdx ) {
            byte bVal = _fixMsg[ _idx ];

            if ( bVal >= '0' && bVal <= '9' ) {

                value = (value << 3) + (value << 1) + (bVal - '0');

            } else if ( bVal == FixField.FIELD_DELIMITER ) {

                return (isNeg) ? -value : value;

            } else if ( bVal == '-' ) {

                isNeg = true;

            } else if ( bVal == 0 ) {

                return (isNeg) ? -value : value;

            } else {
                throwDecodeException( "Non numeric char in integer value idx=" + _idx );
            }

            ++_idx;
        }

        throwDecodeException( "Integer value missing field terminator" );

        return 0; // wont get here but keeps compiler happy
    }

    /**
     * Converts a byte UTC datetime into the number of milliseconds since the start of day.
     * "YYYYMMDD-HH:mm:ss" or "YYYYMMDD-HH:mm:ss.SSS"
     * <p>
     * 20131002-23:16:18.147
     * 20130922161002479 (17)
     * <p>
     * DOESNT THROW EXCEPTION ON BAD DATE ANY LONGER
     * WHEN RUNNING 24*7 RECOVERY WILL HAVE DATES OLDER THAN TODAY
     * SO JUST WARN
     */
    protected final long getInternalTime() {

        if ( _maxIdx < (_idx + 21) ) {
            throwDecodeException( "Missing part of dateTime field" );
        }

        _tzCalculator.parseUTCStringToInternalTime( _fixMsg, _idx, _dateResult );

        final long ms = _dateResult._internalTime;

        if ( ms == 0 ) {
            getValLength();
            return 0;
        }

        _idx = _dateResult._nextIdx;

        if ( _fixMsg[ _idx ] != FixField.FIELD_DELIMITER ) {
            throwDecodeException( "Unexpected extra fields or missing delimited from datetime field" );
        }

        return ms;
    }

    protected final long getLongVal() {

        if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER )
            return Constants.UNSET_LONG;

        boolean isNeg = false;
        long    value = 0;
        while( _idx <= _maxIdx ) {
            byte bVal = _fixMsg[ _idx ];

            if ( bVal >= '0' && bVal <= '9' ) {

                value = (value << 3) + (value << 1) + (bVal - '0');

            } else if ( bVal == FixField.FIELD_DELIMITER ) {

                return (isNeg) ? -value : value;

            } else if ( bVal == '-' ) {

                isNeg = true;

            } else {
                throwDecodeException( "Non numeric char in integer value idx=" + _idx );
            }

            ++_idx;
        }

        throwDecodeException( "Long value missing field terminator" );

        return 0; // wont get here but keeps compiler happy
    }

    protected final int getTag() {
        int value = 0;

        while( _idx < _maxIdx ) { // note not <= as expect at least one byte for the value
            byte bVal = _fixMsg[ _idx ];

            if ( bVal >= '0' && bVal <= '9' ) {

                value = (value << 3) + (value << 1) + (bVal - '0');

            } else if ( bVal == '=' ) {

                ++_idx;

                if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER ) {
                    // empty tag keep going

                    value = 0;

                } else {
                    return value;
                }
            } else if ( bVal == FixField.FIELD_DELIMITER ) {

                return value;

            } else if ( bVal == 0x00 && value == 0 ) {
                // skip null in middle of message
            } else {

                throwDecodeException( "Non numeric char in tag idx=" + _idx + ", partialTagVal=" + value + ", badChar=" + (char) bVal );
            }

            ++_idx;
        }

        return 0;
    }    @Override
    public int parseHeader( final byte[] fixMsg, final int offset, final int bytesRead ) {

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

        // 8=FIX.4.4;9=152;35=D;10=123;

        if ( fixMsg[ _idx ] != '8' || fixMsg[ _idx + 1 ] != '=' ) {
            return -1;  // require resync processing
        }

        // fix version
        _idx += 2;
        int valStart = _idx;

        if ( fixMsg[ _idx + 7 ] != FixField.FIELD_DELIMITER )
            throwDecodeException( "Fix Messsage missing fix version delimiter" );

        _idx += 8;

        // GENERATOR SHOULD CHECK FIX VERSION

        final byte majorVersion = fixMsg[ valStart + 4 ];
        final byte minorVersion = fixMsg[ valStart + 6 ];

        if ( majorVersion != _majorVersion && minorVersion != _minorVersion ) {
            throwDecodeException( "Expected major version=" + _majorVersion + " not " + majorVersion );
        }

        if ( fixMsg[ _idx ] != '9' || fixMsg[ _idx + 1 ] != '=' ) throwDecodeException( "Fix Messsage missing length field " );
        _idx += 2;
        _msgStatedLen = getIntVal();
        _idx++;

        /**
         Body length

         The Body length is the byte count starting at tag 35 (included) all the way to tag 10 (excluded). SOH separators do count in the body length.
         For Example:
         8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|
         Has a Body length of 65 with the following breakdown, length(tag#)
         5(35) + 10(49) + 10(56) + 7(34) + 21(52) + 5(98) + 7(108)

         The SOH delimiter at the end of a Tag=Value belongs to the Tag
         * @return
         */

        final int numBytesInMsg = _msgStatedLen + CHECKSUM_LEN + (_idx - _offset);

        _maxIdx = numBytesInMsg + _offset;  // correctly assign maxIdx as last bytes of current message

        if ( _maxIdx > _fixMsg.length ) _maxIdx = _fixMsg.length;

        return numBytesInMsg;
    }

    protected final int getUnsignedIntVal() {

        if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER )
            return Constants.UNSET_INT;

        boolean isNeg = false;
        int     value = 0;
        while( _idx <= _maxIdx ) {
            byte bVal = _fixMsg[ _idx ];

            if ( bVal >= '0' && bVal <= '9' ) {

                value = (value << 3) + (value << 1) + (bVal - '0');

            } else if ( bVal == FixField.FIELD_DELIMITER ) {

                return value;

            } else {
                throwDecodeException( "Non numeric char in integer value idx=" + _idx );
            }

            ++_idx;
        }

        throwDecodeException( "Unsigned Integer value missing field terminator" );

        return 0; // wont get here but keeps compiler happy
    }    @Override
    public final ResyncCode resync( final byte[] fixMsg, final int offset, final int maxIdx ) {

        _skipCount = 0;

        int idx = offset;

        boolean found   = false;
        int     skipped = 0;

        while( ++idx < maxIdx - 4 ) {
            ++skipped;
            if ( fixMsg[ idx ] == '8' && fixMsg[ idx + 1 ] == '=' && fixMsg[ idx + 2 ] == 'F' && fixMsg[ idx + 3 ] == 'I' && fixMsg[ idx + 4 ] == 'X' ) {
                found = true;
                break;
            }
        }

        if ( found == false ) throwDecodeException( "Unable to find FIX header in data read" );

        _skipCount = skipped;

        if ( maxIdx - idx < MIN_FIX_HEADER_BYTES_FOR_LENGTH_FIELD ) {
            return ResyncCode.FOUND_PARTIAL_HEADER_NEED_MORE_DATA; // need read more data to complete header
        }

        logSkippedMsg( fixMsg, offset, skipped );

        return ResyncCode.FOUND_FULL_HEADER;
    }

    protected final long getUnsignedLongVal() {

        if ( _fixMsg[ _idx ] == FixField.FIELD_DELIMITER )
            return Constants.UNSET_LONG;

        long value = 0;
        while( _idx <= _maxIdx ) {
            byte bVal = _fixMsg[ _idx ];

            if ( bVal >= '0' && bVal <= '9' ) {

                value = (value << 3) + (value << 1) + (bVal - '0');

            } else if ( bVal == FixField.FIELD_DELIMITER ) {

                return value;

            } else {
                throwDecodeException( "Non numeric char in integer value idx=" + _idx );
            }

            ++_idx;
        }

        throwDecodeException( "Unsigned Long value missing field terminator" );

        return 0; // wont get here but keeps compiler happy
    }    @Override
    public final int getSkipBytes() {
        return _skipCount;
    }

    protected final int getValLength() {
        final int start = _idx;
        while( _idx <= _maxIdx ) {
            final byte bVal = _fixMsg[ _idx ];

            if ( bVal == FixField.FIELD_DELIMITER ) {
                break;
            }

            ++_idx;
        }

        return _idx - start;
    }

    protected IgnoredEvent ignoreEvent() {
        return new IgnoredEvent( _fixMsg, _offset, _maxIdx );
    }

    protected final void throwDecodeException( String errMsg ) {
        int len     = (_maxIdx < _offset) ? 0 : _maxIdx - _offset;
        int copyLen = (len + _offset > _fixMsg.length) ? _fixMsg.length - _offset : _maxIdx - _offset;

        if ( copyLen > MAX_BYTES_COPY_FIXMSG ) {
            copyLen = MAX_BYTES_COPY_FIXMSG;
        }

        ReusableString copy = TLC.instance().getString();

        if ( _offset >= 0 && copyLen > 0 ) {
            copy.setValue( _fixMsg, _offset, copyLen );
        }

        int msgBadIdx = _idx - _offset;

        throw new RuntimeDecodingException( errMsg + ", len=" + len + ", idx=" + _idx + ", offset=" + _offset + ", tag=" + _tag + ", offsetWithinMsg=" + msgBadIdx, copy );
    }

    protected void validateChecksum( final int checkSum ) {

        if ( _validateChecksum == false ) return;

        int val = 0;

        // ;10=123
        final int max = _idx - 6;

        for ( int idx = _offset; idx < max; ) {
            val += _fixMsg[ idx++ ];
        }

        val = val & 0xFF;

        if ( val != checkSum ) {
            throwDecodeException( "invalid checksum, expected=" + val + ", received=" + checkSum );
        }
    }

    private void logSkippedMsg( final byte[] fixMsg, final int offset, final int skipped ) {
        _errMsg.reset();
        _errMsg.append( "Resync fix msg, skipped " ).append( skipped ).append( " bytes to find next fix header [" );
        int showBytes = (skipped < SKIP_MSG_SHOW_BYTES) ? skipped : SKIP_MSG_SHOW_BYTES;
        _errMsg.appendReadableHEX( fixMsg, offset, showBytes );
        if ( skipped > SKIP_MSG_SHOW_BYTES ) _errMsg.append( " ... " );
        _errMsg.append( ']' );

        _log.info( _errMsg );
    }

    private Event rejectDecodeException( RuntimeDecodingException e ) {
        return new RejectDecodeException( _fixMsg, _offset, _maxIdx, e );
    }

    private Event rejectIndexOutOfBoundsException( IndexOutOfBoundsException e ) {
        return new RejectIndexOutOfBounds( _fixMsg, _offset, _maxIdx, e );
    }

    private Event rejectThrowable( Throwable t ) {
        return new RejectThrowable( _fixMsg, _offset, _maxIdx, t );
    }

    private void verifyValue( ZString expVal, ZString errMsg ) {
        final byte[] expected = expVal.getBytes();

        final int start  = _idx;
        final int maxLen = expVal.length();

        final int expStart = expVal.getOffset();
        int       expIdx   = expStart;

        byte    bVal, expByte;
        boolean bad = false;

        while( _idx <= _maxIdx ) {
            bVal = _fixMsg[ _idx ];

            if ( bVal == FixField.FIELD_DELIMITER ) {
                break;
            }

            expByte = expected[ expIdx ];

            if ( bVal != expByte ) {
                bad = true;
            }

            ++_idx;
            ++expIdx;
        }

        if ( bad || ((expIdx - expStart) != maxLen) ) {
            final int len = _idx - start;

            _errMsg.copy( errMsg );
            _errMsg.append( expVal ).append( RECEIVED );
            _errMsg.append( _fixMsg, start, len );

            throw new RuntimeDecodingException( _errMsg );
        }
    }
























}
