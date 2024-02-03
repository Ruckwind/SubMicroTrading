/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

/**
 * Debug proxy wrapper of the decode builder
 *
 * @NOTE unsigned numbers will appear signed
 */
public final class DebugBinaryDecodeBuilder<T extends BinaryDecodeBuilder> implements BinaryDecodeBuilder {

    private static final Logger _log = LoggerFactory.create( DebugBinaryDecodeBuilder.class );

    private final ReusableString _dump;

    private final T _builder;

    private int _startIdx;

    public DebugBinaryDecodeBuilder( ReusableString dumpStr, T builder ) {
        super();
        _dump    = dumpStr;
        _builder = builder;
    }

    @Override
    public void clear() {
        _builder.clear();
    }

    @Override public long decodeBase36Number( final int len ) {
        startTrace( "decodeBase36Number" );
        long val = _builder.decodeBase36Number( len );
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public boolean decodeBool() {
        startTrace( "bool" );
        boolean val = _builder.decodeBool();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public byte decodeByte() {
        startTrace( "byte" );
        byte val = _builder.decodeByte();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public byte decodeChar() {
        startTrace( "char" );
        byte val = _builder.decodeChar();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public final void decodeData( final ReusableString dest, final int len ) {
        startTrace( "lva" );
        _builder.decodeData( dest, len );
        endTrace();
    }

    @Override
    public int decodeDate() {
        startTrace( "date" );
        int val = _builder.decodeDate();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public double decodeDecimal() {
        startTrace( "decimal" );
        double val = _builder.decodeDecimal();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public int decodeInt() {
        startTrace( "int" );
        int val = _builder.decodeInt();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public void decodeIntToString( ReusableString dest ) {
        startTrace( "intToString" );
        _builder.decodeIntToString( dest );
        _dump.append( dest );
        endTrace();
    }

    @Override
    public long decodeLong() {
        startTrace( "long" );
        long val = _builder.decodeLong();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public void decodeLongToString( ReusableString dest ) {
        startTrace( "longToString" );
        _builder.decodeLongToString( dest );
        _dump.append( dest );
        endTrace();
    }

    @Override
    public double decodePrice() {
        startTrace( "price" );
        double val = _builder.decodePrice();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override public double decodePrice( final int wholeDigits, final int decimalPlaces ) {
        startTrace( "decodePrice" );
        double val = _builder.decodePrice( wholeDigits, decimalPlaces );
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public int decodeQty() {
        startTrace( "qty" );
        int val = _builder.decodeQty();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override public int decodeQty( final int len ) {
        startTrace( "decodeQty" );
        int val = _builder.decodeQty( len );
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public short decodeShort() {
        startTrace( "short" );
        short val = _builder.decodeShort();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public void decodeString( final ReusableString dest ) {
        startTrace( "string" );
        _builder.decodeString( dest );
        _dump.append( dest );
        endTrace();
    }

    /**
     * encodes a fixed width string, string must be null padded and null terminated
     */
    @Override
    public final void decodeStringFixedWidth( final ReusableString dest, final int len ) {
        startTrace( "stringFixedWidth" );
        _builder.decodeStringFixedWidth( dest, len );
        _dump.append( dest );
        endTrace();
    }

    @Override
    public long decodeTimeLocal() {
        startTrace( "timeLocal" );
        long val = _builder.decodeTimeLocal();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public long decodeTimeUTC() {
        startTrace( "timeUTC" );
        long val = _builder.decodeTimeUTC();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public long decodeTimestampLocal() {
        startTrace( "timestampLocal" );
        long val = _builder.decodeTimestampLocal();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public long decodeTimestampUTC() {
        startTrace( "timestampUTC" );
        long val = _builder.decodeTimestampUTC();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public byte decodeUByte() {
        startTrace( "ubyte" );
        byte val = _builder.decodeUByte();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public int decodeUInt() {
        startTrace( "uint" );
        int val = _builder.decodeUInt();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public long decodeULong() {
        startTrace( "ulong" );
        long val = _builder.decodeULong();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public short decodeUShort() {
        startTrace( "ushort" );
        short val = _builder.decodeUShort();
        _dump.append( val );
        endTrace();
        return val;
    }

    @Override
    public void decodeZStringFixedWidth( ReusableString dest, int len ) {
        startTrace( "zStringFixedWidth" );
        _builder.decodeZStringFixedWidth( dest, len );
        _dump.append( dest );
        endTrace();
    }

    @Override
    public void end() {
        _builder.end();
        _dump.append( "\n}\n" );
        _log.infoLarge( _dump );
        _dump.reset();
    }

    @Override
    public byte[] getBuffer() {
        return _builder.getBuffer();
    }

    @Override
    public int getCurrentIndex() {
        return _builder.getCurrentIndex();
    }

    @Override
    public int getLength() {
        return _builder.getLength();
    }

    @Override
    public final int getMaxIdx() {
        return _builder.getMaxIdx();
    }

    @Override
    public void setMaxIdx( int maxIdx ) {
        _builder.setMaxIdx( maxIdx );
    }

    @Override
    public int getNextFreeIdx() {
        return _builder.getNextFreeIdx();
    }

    @Override
    public int getOffset() {
        return _builder.getOffset();
    }

    @Override
    public void setTimeUtils( TimeUtils calc ) {
        _builder.setTimeUtils( calc );
    }

    @Override
    public void skip( int size ) {
        startTrace( "skip" );
        _builder.skip( size );
        endTrace();
    }

    @Override
    public void start( final byte[] msg, final int offset, final int maxIdx ) {
        _builder.start( msg, offset, maxIdx );
        _dump.reset();
        _dump.append( "\n\n{    DECODE " );
    }

    private void endTrace() {
        int endIdx = _builder.getCurrentIndex();
        int bytes  = endIdx - _startIdx;
        _dump.append( ",  bytes=" ).append( bytes ).append( ", offset=" ).append( _startIdx - _builder.getOffset() ).append( ", raw=[" );
        _dump.appendHEX( _builder.getBuffer(), _startIdx, bytes );
        _dump.append( " ] " );
    }

    private void startTrace( String type ) {
        _dump.append( type ).append( ' ' );
        _startIdx = _builder.getCurrentIndex();
    }
}
