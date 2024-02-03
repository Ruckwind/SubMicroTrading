package com.rr.core.recovery.json;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class JSONInputTokeniser {

    private static final Logger _log = LoggerFactory.create( JSONInputTokeniser.class );

    public enum Token {
        StartObject,
        EndObject,
        StartArray,
        EndArray,
        CommaSeperator,
        Colon,
        EndStream
    }

    private int         _index = 0;
    private int         _lineNum;
    private byte        _lastChar;
    private boolean     _undoLastCharRead;
    private boolean     _endOfStream;
    private InputStream _inStr;

    public static void setLogLevel( Level l ) {
        _log.setLevel( l );
    }

    public static Token getToken( final byte byteVal ) {
        if ( byteVal == '{' ) return Token.StartObject;
        if ( byteVal == '}' ) return Token.EndObject;
        if ( byteVal == '[' ) return Token.StartArray;
        if ( byteVal == ']' ) return Token.EndArray;
        if ( byteVal == ',' ) return Token.CommaSeperator;
        if ( byteVal == ':' ) return Token.Colon;
        if ( byteVal == -1 ) return Token.EndStream;

        return null;
    }

    public JSONInputTokeniser( final InputStream rs ) {
        _inStr = rs;
    }

    public Boolean getBoolean() throws Exception {
        byte nextByte = nextNonSpaceChar();

        if ( nextByte == 't' ) {
            match( "rue" );
            return Boolean.TRUE;
        } else if ( nextByte == 'f' ) {
            match( "alse" );
            return Boolean.FALSE;
        } else if ( nextByte == 'n' ) {
            match( "ull" );
            return null;
        }

        throw new JSONException( "Bad Value at idx " + getIndex() + ", line=" + getLineNum() + " expected true/false/null not '" + (char) nextByte + "'" );

    }

    public int getIndex() {
        return _index;
    }

    public Integer getInteger() throws Exception {
        int num = 0;

        int startLine = _lineNum;

        byte nextByte = nextNonSpaceChar();

        boolean negative = false;

        if ( !Character.isDigit( nextByte ) ) {
            if ( nextByte == 'n' ) {
                match( "ull" );
                return null;
            } else if ( nextByte == '-' ) {
                negative = true;

                nextByte = nextNonSpaceChar();

                if ( !Character.isDigit( nextByte ) ) {
                    throw new JSONException( "Line " + startLine + " Bad Integer, unexpected character " + (char) nextByte + " looking for digit after minus sign, at idx=" + _index );
                }
            } else {
                throw new JSONException( "Line " + startLine + " Bad Integer, unexpected character " + (char) nextByte + " looking for digit at idx=" + _index );
            }
        }

        num = nextByte - '0';

        while( !_endOfStream && Character.isDigit( (nextByte = nextByte()) ) ) {
            num = num * 10 + (nextByte - '0');
        }

        pushbackLastChar();

        return (negative) ? (num * -1) : num;
    }

    public int getLineNum() {
        return _lineNum;
    }

    public Long getLong() throws Exception {
        long num = 0;

        int startLine = _lineNum;

        byte nextByte = nextNonSpaceChar();

        if ( !Character.isDigit( nextByte ) ) {
            if ( nextByte == 'n' ) {
                match( "ull" );
                return null;
            } else {
                throw new JSONException( "Line " + startLine + " Bad Long, unexpected character " + (char) nextByte + " looking for digit at idx=" + _index );
            }
        }

        num = nextByte - '0';

        while( !_endOfStream && Character.isDigit( (nextByte = nextByte()) ) ) {
            num = num * 10 + (nextByte - '0');
        }

        pushbackLastChar();

        if ( _log.isEnabledFor( Level.xtrace ) ) {
            _log.log( Level.xtrace, "json [" + _lineNum + "/" + _index + "] " + num );
        }

        return num;
    }

    public Short getShort() throws Exception {
        int num = 0;

        int startLine = _lineNum;

        boolean negative = false;

        byte nextByte = nextNonSpaceChar();

        if ( !Character.isDigit( nextByte ) ) {
            if ( nextByte == 'n' ) {
                match( "ull" );
                return null;
            } else if ( nextByte == '-' ) {
                negative = true;
            } else {
                throw new JSONException( "Line " + startLine + " Bad Short, unexpected character " + (char) nextByte + " looking for digit at idx=" + _index );
            }
        }

        num = nextByte - '0';

        while( !_endOfStream && Character.isDigit( (nextByte = nextByte()) ) ) {
            num = num * 10 + (nextByte - '0');
        }

        pushbackLastChar();

        if ( num > Short.MAX_VALUE || num < Short.MIN_VALUE ) {
            throw new JSONException( "Line " + startLine + " Bad number " + num + " wont fit into short" );
        }

        return (negative) ? (short) (num * -1) : (short) num;
    }

    public boolean getString( final ReusableString str ) throws Exception {
        str.reset();

        int startLine = _lineNum;

        byte nextByte = nextNonSpaceChar();

        if ( nextByte != '"' ) {
            if ( nextByte == 'n' ) {
                match( "ull" );
                return false;
            } else {
                throw new JSONException( "Line " + startLine + " Bad String, missing start, unexpected character " + (char) nextByte + " looking for next token at idx=" + _index );
            }
        }

        while( !_endOfStream && (nextByte = nextByte()) != '"' ) {
            str.append( nextByte );
            if ( nextByte == '\\' ) {
                nextByte = nextByte();
                str.append( nextByte );
            }
        }

        if ( _log.isEnabledFor( Level.xtrace ) ) {
            _log.log( Level.xtrace, "json [" + _lineNum + "/" + _index + "] \"" + str.toString() + "\" " );
        }

        return true;
    }

    public void getStringNoQuotes( final ReusableString str ) throws IOException {
        str.reset();

        int startLine = _lineNum;

        byte nextByte = nextNonSpaceChar();

        while( !_endOfStream && !Character.isWhitespace( nextByte ) && nextByte != ',' ) {
            str.append( nextByte );
            if ( nextByte == '\\' ) {
                nextByte = nextByte();
                str.append( nextByte );
            }
            nextByte = nextByte();
        }

        if ( _log.isEnabledFor( Level.xtrace ) ) {
            _log.log( Level.xtrace, "json [" + _lineNum + "/" + _index + "] " + str.toString() );
        }
    }

    public Object jsonException( String msg ) throws JSONException {
        throw new JSONException( "JSONExcepion [line=" + _lineNum + ",idx=" + _index + "] " + msg );
    }

    public Object jsonException( String msg, Exception e ) throws JSONException {
        throw new JSONException( "JSONExcepion [line=" + _lineNum + ",idx=" + _index + "] " + msg, e );
    }

    public void match( final String str ) throws Exception {
        for ( int i = 0; i < str.length(); i++ ) {
            byte nextByte = nextNonSpaceChar();

            final byte expChar = (byte) str.charAt( i );

            if ( nextByte != expChar ) throw new JSONException( "Bad Value at idx " + getIndex() + ", line=" + getLineNum() + " expected to match chars '" + str + "'" );
        }
    }

    public byte nextByte() throws IOException {
        if ( _undoLastCharRead ) {
            _undoLastCharRead = false;
            return _lastChar;
        }

        if ( _endOfStream ) return -1;

        _lastChar = (byte) _inStr.read();
        if ( _lastChar == -1 ) _endOfStream = true;
        if ( _lastChar == '\n' ) {
            ++_lineNum;
            _index = 0;
        } else {
            ++_index;
        }
        return _lastChar;
    }

    public byte nextNonSpaceChar() throws IOException {
        byte nextByte = -1;

        while( !_endOfStream && Character.isWhitespace( (nextByte = nextByte()) ) ) {
            // nothing extra
        }

        return nextByte;
    }

    public void nextSpecialTag( final ReusableString tmpVal, final JSONSpecialTags expVal ) throws Exception {
        getString( tmpVal );

        verifySpecialTag( tmpVal, expVal );
    }

    public JSONSpecialTags nextSpecialTag( final ReusableString tmpVal ) throws Exception {
        getString( tmpVal );

        return JSONSpecialTags.getVal( tmpVal );
    }

    public Token nextToken() throws Exception {
        byte nextByte = nextNonSpaceChar();

        final Token x = getToken( nextByte );
        if ( x != null ) return x;

        throw new JSONException( "Line " + _lineNum + " Unexpected character " + (char) nextByte + " looking for next token at idx=" + _index );
    }

    public void nextToken( final Token expected ) throws Exception {
        Token next = nextToken();
        if ( next != expected ) {
            throw new JSONException( "Line " + _lineNum + " expected character " + expected.toString() + " but found " + next.toString() + " at idx=" + _index );
        }
    }

    public boolean peekIsNextNonSpaceToken() throws IOException {
        byte nextByte = nextNonSpaceChar();
        pushbackLastChar();
        return (getToken( nextByte ) != null);
    }

    public void pushbackLastChar() {
        _undoLastCharRead = true;
    }

    public void set( final InputStream inStr ) {
        _inStr            = inStr;
        _index            = 0;
        _endOfStream      = false;
        _lastChar         = 0;
        _undoLastCharRead = false;
    }

    public void verifySpecialTag( final ReusableString tmpVal, final JSONSpecialTags expVal ) throws JSONException {
        JSONSpecialTags st = JSONSpecialTags.getVal( tmpVal ); // @TODO optimise out map hit

        if ( st != expVal ) throw new JSONException( "Expected " + expVal + " but got " + st + " at line " + getLineNum() );
    }
}
