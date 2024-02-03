/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import java.nio.ByteBuffer;

public interface ZString extends Comparable<ZString> {

    ZString EMPTY_STRING = new ViewString( "" );

    boolean contains( String s );

    boolean contains( ZString s );

    boolean endsWith( String suffix );

    boolean endsWith( ZString suffix );

    boolean equals( ZString s );

    /**
     * @NOTE use equals in preference to equalsIgnoreCase as its slower
     */
    boolean equalsIgnoreCase( ZString s );

    boolean equalsIgnoreCase( String s );

    byte getByte( int idx );

    void getBytes( byte[] target, int targetStart );

    void getBytes( byte[] target, int srcStart, int targetStart, int len );

    /**
     * @return the underlying byte array
     * @NOTE do not use this method to modify the underlying byte array
     */
    byte[] getBytes();

    int getOffset();

    /**
     * @param c - character to search for
     * @return index that character first occurs or -1 if it doesnt
     * @WARNING although arg is an int, search only compares BYTE
     */
    int indexOf( int c );

    int indexOf( int c, int offset );

    int indexOf( String s );

    int indexOf( String s, int fromIdx );

    int indexOf( ZString s );

    int indexOf( ZString s, int fromIdx );

    int lastIndexOf( int c );

    int lastIndexOf( int c, int fromIdx );

    int length();

    boolean startsWith( String prefix );

    boolean startsWith( ZString prefix );

    boolean startsWith( String prefix, int toffset );

    boolean startsWith( ZString prefix, int toffset );

    /**
     * copy bytes of this string to dest starting at startIdx to end of string
     *
     * @param dest
     * @param startIdx
     */
    void substring( ReusableString dest, int startIdx );

    /**
     * copy bytes of this string to dest starting at startIdx and ending at endIdx
     *
     * @param dest
     * @param startIdx
     */
    void substring( ReusableString dest, int startIdx, int endIdx );

    /**
     * avoid creates temp object
     *
     * @param startIdx
     */
    String substring( int startIdx );

    /**
     * avoid creates temp object
     *
     * @param startIdx
     */
    String substring( int startIdx, int endIdx );

    void write( ByteBuffer bb );
}