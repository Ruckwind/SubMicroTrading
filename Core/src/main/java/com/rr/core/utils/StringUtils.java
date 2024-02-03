/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ZString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final byte   WILDCARD = '*';
    private static final String NOT_A_NUMBER = "nan";
    private static       Logger _console = ConsoleFactory.console( StringUtils.class );

    /**
     * return index of start of first occurance of ptn in lva
     *
     * @param data
     * @param offset
     * @param length
     * @param ptn
     * @return
     */
    public static int findMatch( byte[] data, int offset, int length, byte[] ptn ) {
        int dataIdx = offset;
        int ptnIdx  = 0;
        int maxIdx  = length + offset;

        while( dataIdx < maxIdx ) {

            byte b = data[ dataIdx ];
            byte p = ptn[ ptnIdx ];

            if ( b == p ) {

                if ( (dataIdx + ptn.length) < maxIdx ) {
                    while( ++ptnIdx < ptn.length && p == b ) {
                        p = ptn[ ptnIdx ];
                        b = data[ dataIdx + ptnIdx ];
                    }

                    if ( p == b ) {
                        return dataIdx;
                    }

                    ptnIdx = 0;

                } else {
                    break;
                }
            }

            ++dataIdx;
        }

        return -1;
    }

    public static int count( byte[] input, byte[] pattern ) {

        if ( pattern.length == 0 ) return 0;

        int n = input.length;
        int m = pattern.length;

        if ( m == 0 ) return 0;
        if ( m > n ) return 0;

        int result = 0;

        byte ast = '*';

        for ( int i = 0; i <= n - m; i++ ) {
            int offset = 0;

            boolean matched = true;
            int     lastAst = -1;

            for ( int pi = 0; pi < m; pi++ ) {
                if ( offset + i + pi == n ) {
                    matched = false;
                    break;
                }

                if ( pattern[ pi ] == ast ) {
                    lastAst = pi;
                    if ( pi == m - 1 ) {
                        // match against entire remainder of string
                        return ++result;
                    }
                    continue;
                } else if ( pattern[ pi ] == input[ i + pi + offset ] ) {
                    continue;
                } else if ( pi == 0 ) {
                    matched = false;
                    break;
                } else {
                    pi = lastAst;
                    if ( pi == -1 ) {
                        matched = false;
                        break;
                    } else {
                        offset++;
                    }
                }
            }

            if ( matched ) {
                result++;
                i = offset + i + m;
            }
        }

        return result;
    }

    public static int lastIndexOf( byte[] data, int offset, int length, byte val ) {
        int i = offset + length - 1;

        for ( ; i >= offset; i-- ) {
            if ( data[ i ] == val ) {
                return i - offset;
            }
        }

        return -1;
    }

    public static int nextIndexOf( byte[] data, int offset, int length, byte val ) {
        int i = offset;

        for ( ; i < length; ++i ) {
            if ( data[ i ] == val ) {
                return i - offset;
            }
        }

        return -1;
    }

    public static String className( Class<?> aClass ) {
        if ( aClass == null ) return null;

        final String cn  = aClass.getName();
        int          idx = cn.lastIndexOf( '.' );

        return (idx > 0) ? cn.substring( idx + 1 ) : cn;
    }

    public static int parseInt( final ZString strInt ) {

        if ( strInt == null ) return 0;

        int val = 0;

        byte[] b = strInt.getBytes();

        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = b[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                throw new RuntimeEncodingException( "Invalid number, idx=" + idx + ", byte=" + (int) x + ", numStr=" + strInt );
            }
        }

        return val;
    }

    public static boolean parseBoolean( final ZString s ) {
        return ((s != null) && (s.equalsIgnoreCase( "true" ) || s.equalsIgnoreCase( "y" ) || s.equalsIgnoreCase( "1" )));
    }

    public static boolean parseBoolean( final String s ) {
        return ((s != null) && (s.equalsIgnoreCase( "true" ) || s.equalsIgnoreCase( "yes" ) || s.equalsIgnoreCase( "y" ) || s.equalsIgnoreCase( "1" )));
    }

    public static int parseInt( final ZString strInt, int defaultVal ) {
        int val = 0;

        byte[] b = strInt.getBytes();

        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = b[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                return defaultVal;
            }
        }

        return val;
    }

    public static boolean isNumber( final String strInt ) {
        int val = 0;

        int       idx    = 0;
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = (byte) strInt.charAt( idx );

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean isNumber( final ZString strInt ) {
        int val = 0;

        byte[]    bytes  = strInt.getBytes();
        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = bytes[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                return false;
            }
        }

        return true;
    }

    public static long parseLong( final ZString strInt ) {
        long val = 0;

        byte[] b = strInt.getBytes();

        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = b[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                throw new RuntimeEncodingException( "Invalid number, idx=" + idx + ", byte=" + (int) x + ", numStr=" + strInt );
            }
        }

        return val;
    }

    public static long parseLongNoException( final ZString strInt ) {
        long val = 0;

        byte[] b = strInt.getBytes();

        int       idx    = strInt.getOffset();
        final int maxLen = idx + strInt.length();
        for ( ; idx < maxLen; ++idx ) {
            byte x = b[ idx ];

            if ( x >= '0' && x <= '9' ) {
                val = (val * 10) + (x - '0');
            } else if ( x == 0x00 || x == ' ' ) {
                break;
            } else {
                return Constants.UNSET_LONG;
            }
        }

        return val;
    }

    public static <T extends Enum<T>> T getEnum( final String val, final Class<T> envClass ) {

        EnumSet<T> set = EnumSet.allOf( envClass );
        for ( T v : set ) {
            if ( v.name().toLowerCase().equalsIgnoreCase( val.toLowerCase() ) ) {
                return v;
            }
        }

        throw new SMTRuntimeException( "StringUtils " + val + " is invalid enum value for " + envClass.getSimpleName() );
    }

    public static String[] matchPatterns( final String ptnList, final Collection<String> valsToTest ) {

        if ( ptnList == null ) return new String[ 0 ];

        String[] list1 = ptnList.split( "," );

        List<String> matchedStratIds = new ArrayList<>();

        for ( String ptnStr : list1 ) {
            Pattern ptn = Pattern.compile( ptnStr );

            for ( String valToTest : valsToTest ) {

                Matcher m = ptn.matcher( valToTest );

                if ( m.matches() ) {
                    matchedStratIds.add( valToTest );
                }
            }
        }

        return matchedStratIds.toArray( new String[ matchedStratIds.size() ] );
    }

    public static Pattern[] createPatterns( final String ptnList ) {
        String[] list1 = ptnList.split( "," );

        Pattern[] ptns = new Pattern[ list1.length ];

        for ( int i = 0; i < list1.length; i++ ) {
            String  ptnStr = list1[ i ];
            Pattern ptn    = Pattern.compile( ptnStr );

            ptns[ i ] = ptn;
        }

        return ptns;
    }

    public static boolean matchPatterns( final Pattern[] ptns, final String valToTest ) {
        for ( Pattern p : ptns ) {
            Matcher m = p.matcher( valToTest );

            if ( m.matches() ) {
                return true;
            }

        }

        return false;
    }

    public static String[] split( final String line, final char ch ) {
        return split( line, ch, true );
    }

    public static String[] split( final String line, final char ch, boolean trim ) {
        ArrayList<String> bits = new ArrayList<>();

        split( line, ch, bits, trim );

        String[] res = new String[ bits.size() ];

        return bits.toArray( res );
    }

    public static void split( final String line, final char ch, final ArrayList<String> bits ) {
        split( line, ch, bits, true );
    }

    public static void split( final String line, final char ch, final ArrayList<String> bits, boolean trim ) {
        bits.clear();

        final int length = line.length();

        if ( length == 0 ) return;

        int idx = 0;
        int next;

        while( idx < length && line.charAt( idx ) == ch ) {
            bits.add( "" );
            ++idx;
        }

        while( idx < length && ((next = line.indexOf( ch, idx )) > 0) ) {

            int len = next - idx;

            if ( len > 0 ) {
                String var = line.substring( idx, next );
                bits.add( (trim) ? var.trim() : var );
            } else {
                bits.add( "" );
            }

            idx = next + 1;
        }

        if ( idx < length ) {
            String var = line.substring( idx );
            bits.add( (trim) ? var.trim() : var );
        } else {
            bits.add( "" );
        }
    }

    public static String byteBufToString( final ByteBuffer buf ) {
        Charset    latin1     = StandardCharsets.ISO_8859_1;
        CharBuffer utf8Buffer = latin1.decode( buf );
        return new String( utf8Buffer.array() );
    }

    /**
     * inefficient parsing routing !
     *
     * @TODO optimise
     */
    public static double parseDouble( final String value ) {
        if ( value == null || value.length() == 0 || value.equals( NOT_A_NUMBER ) ) {
            return Double.NaN;
        }

        if ( value.equals( Constants.Z_NEG_INFINITY ) ) return Double.NEGATIVE_INFINITY;
        if ( value.equals( Constants.Z_POS_INFINITY ) ) return Double.POSITIVE_INFINITY;

        return Double.parseDouble( value );
    }

    public static void copyMinusChar( final ZString src, final char filterOut, final ReusableString dest ) {

        dest.reset();

        for ( int idx = 0; idx < src.length(); idx++ ) {
            byte b = src.getByte( idx );

            if ( b != filterOut ) {
                dest.append( b );
            }
        }
    }

    public static String stripChar( final String src, final char filterOut ) {
        StringBuilder sb = new StringBuilder( src.length() );

        for ( int idx = 0; idx < src.length(); idx++ ) {
            char b = src.charAt( idx );

            if ( b != filterOut ) {
                sb.append( b );
            }
        }

        return sb.toString();
    }

    public static Map<String, String> stringToMap( final String args, char delim ) {
        Map<String, String> map = new HashMap<>();

        String[] argList = split( args, delim );

        for ( int i = 0; i < argList.length; ++i ) {
            String[] pair = split( argList[ i ], '=' );
            if ( pair.length > 2 ) throw new SMTRuntimeException( "bad arg list expected key=val, got " + pair.length + " for [" + argList[ i ] + "] in [" + args + "]" );

            String key = pair[ 0 ];
            String val = (pair.length == 2) ? pair[ 1 ] : "";

            map.put( key, val );
        }

        return map;
    }

    public static int parseDate( final byte[] v, final int offset, final int length ) {
        if ( v.length != 10 ) {
            throw new RuntimeDecodingException( "Not supported format for YYYY-MM-DD" );
        }

        byte dm1 = v[ 4 ];
        byte dm2 = v[ 7 ];

        if ( dm1 != dm2 ) {
            throw new RuntimeDecodingException( "Not supported format for YYYY-MM-DD, delims different " + new String( v, offset, length ) );
        }

        if ( dm1 != '-' && dm1 != '.' && dm1 != '/' ) {
            throw new RuntimeDecodingException( "Not supported format for YYYY-MM-DD, execpected delim " + new String( v, offset, length ) );
        }

        int y1, y2, y3, y4, m1, m2, d1, d2;

        y1 = v[ 0 ] - '0';
        y2 = v[ 1 ] - '0';
        y3 = v[ 2 ] - '0';
        y4 = v[ 3 ] - '0';
        m1 = v[ 5 ] - '0';
        m2 = v[ 6 ] - '0';
        d1 = v[ 8 ] - '0';
        d2 = v[ 9 ] - '0';

        int yyyy = y1 * 1000 + y2 * 100 + y3 * 10 + y4;
        int mm   = m1 * 10 + m2;
        int dd   = d1 * 10 + d2;

        int date = yyyy * 10000 + mm * 100 + dd;

        if ( date < 19000101 || date > 20990101 ) {
            throw new RuntimeDecodingException( "Date out of range " + new String( v, offset, length ) + " to " + date );
        }

        return date;
    }

    public static String UTC( long timestampMS ) {
        return TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( timestampMS );
    }

    /**
     * horribly inefficient use with care !!
     */
    public static String remove( String in, String[] strs ) {
        String ret = in;

        for ( String p : strs ) {
            ret = ret.replace( p, "" );
        }

        return ret;
    }
}
