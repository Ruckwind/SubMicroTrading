package com.rr.core.recovery.json;

import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.TLC;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.pubsub.codecs.JSONCodec;

import java.io.IOException;

import static com.rr.core.lang.Constants.Z_INFINITY;

public final class JSONPrettyDump {

    private static final Logger _log = LoggerFactory.create( JSONPrettyDump.class );

    private static final String         ONE_INDENT_SPACES = "\t";
    private static JSONCodec _codec;
    private final        ReusableString _depthSpaces      = new ReusableString();
    private final        ReusableString _str              = new ReusableString();
    private              int            _depth;
    private              boolean        _endOfStream;

    public static void setCustomCodec( SMTStartContext ctx ) {
        _codec = new JSONCodec( ctx );
        _codec.setExcludeNullFields( true );
    }

    /**
     * convert any object into a readable JSON string
     */
    public static synchronized void objToJSON( Object obj, ReusableString out ) {
        if ( obj == null ) return;

        if ( _codec != null ) {
            try {
                _codec.encode( obj, out );
            } catch( Exception e ) {
                _log.warn( e.getMessage() + " failed to encode " + obj.toString() );
            }
        }
    }

    public static synchronized String objToJSON( Object obj ) {
        if ( obj == null ) return null;

        if ( _codec != null ) {
            final ReusableString out = TLC.strPop();
            try {
                _codec.encode( obj, out );

                return out.toString();
            } catch( Exception e ) {
                _log.warn( e.getMessage() + " failed to encode " + obj.toString() );
            } finally {
                TLC.strPush( out );
            }
        }

        return null;
    }

    public void prettyDump( final ReusableString jsonStr, final ReusableString prettified ) throws Exception {

        ReusableStringInputStream rs = new ReusableStringInputStream();

        rs.set( jsonStr );

        JSONInputTokeniser tokeniser = new JSONInputTokeniser( rs );

        try {
            doPrettyDump( prettified, tokeniser );
        } catch( Exception e ) {
            _log.info( "Error processing JSON : " + e.getMessage() + " : current encoding :- <" + prettified.toString() + ">" );
            throw e;
        }
    }

    private void checkMatch( final JSONInputTokeniser tokeniser, final String charsToMatch ) throws Exception {
        int len = charsToMatch.length();
        for ( int i = 0; i < len; i++ ) {
            char nextChar = charsToMatch.charAt( i );
            byte nextByte = getNextByte( tokeniser );

            if ( nextByte != (byte) nextChar ) {
                throw new JSONException( "Expected " + nextChar + " got " + (char) nextByte + ", idx=" + tokeniser.getIndex() );
            }
        }
    }

    private void doPrettyDump( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        JSONInputTokeniser.Token token;

        do {
            token = tokeniser.nextToken();

            switch( token ) {
            case StartObject:
                procObject( prettified, tokeniser );
                break;
            case EndObject:
                endObject( prettified );
                break;
            case StartArray:
                procArray( prettified, tokeniser );
                break;
            case EndArray:
                endArray( prettified );
                break;
            case CommaSeperator:
                prettified.append( "," ).append( "\n" ).append( _depthSpaces );
                procValue( prettified, tokeniser );
                break;
            case Colon:
                prettified.append( " : " );
                procValue( prettified, tokeniser );
                break;
            case EndStream:
                _endOfStream = true;
                break;
            }

        } while( !_endOfStream );
    }

    private void endArray( final ReusableString prettified ) {
        setSpaces( --_depth );
        prettified.append( "\n" ).append( _depthSpaces ).append( "]" ).append( "\n" );
    }

    private void endObject( final ReusableString prettified ) {
        setSpaces( --_depth );
        prettified.append( "\n" ).append( _depthSpaces ).append( "}" );
    }

    private byte getNextByte( final JSONInputTokeniser tokeniser ) throws IOException {
        byte ret = tokeniser.nextByte();
        if ( ret == -1 ) _endOfStream = true;
        return ret;
    }

    private void procArray( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        prettified.append( "[" ).append( "\n" );
        setSpaces( ++_depth );
        prettified.append( _depthSpaces );

        byte nextByte = tokeniser.nextNonSpaceChar();

        if ( tokeniser.getToken( nextByte ) != JSONInputTokeniser.Token.EndArray ) {

            tokeniser.pushbackLastChar();

            do {
                procValue( prettified, tokeniser );

                nextByte = tokeniser.nextNonSpaceChar();

                if ( nextByte == ',' ) {
                    prettified.append( ",\n" ).append( _depthSpaces );
                } else {
                    break;
                }

            } while( true );

            tokeniser.pushbackLastChar();
            tokeniser.nextToken( JSONInputTokeniser.Token.EndArray );
        }

        setSpaces( --_depth );
        prettified.append( "\n" ).append( _depthSpaces ).append( "]" );
    }

    private void procFalse( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        checkMatch( tokeniser, "alse" );
        prettified.append( "false" );
    }

    private void procNull( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        checkMatch( tokeniser, "ull" );
        prettified.append( "null" );
    }

    private void procNumber( final ReusableString prettified, final JSONInputTokeniser tokeniser, byte nextByte ) throws Exception {

        if ( nextByte == '-' || nextByte == '+' ) {
            prettified.append( nextByte );

            nextByte = getNextByte( tokeniser );

            if ( nextByte == Z_INFINITY[ 0 ] ) {
                skipInfinity( prettified, tokeniser, nextByte );
                return;
            }

            tokeniser.pushbackLastChar();

        } else if ( nextByte == Z_INFINITY[ 0 ] ) {

            skipInfinity( prettified, tokeniser, nextByte );
            return;

        } else {
            prettified.append( nextByte );
        }

        while( Character.isDigit( (nextByte = getNextByte( tokeniser )) ) ) {
            prettified.append( nextByte );
        }

        if ( nextByte == '.' ) {
            nextByte = writeAndGetNextByte( prettified, tokeniser, nextByte );

            if ( nextByte == 'e' || nextByte == 'E' ) {
                nextByte = writeAndGetNextByte( prettified, tokeniser, nextByte );
            }

            if ( nextByte == '+' || nextByte == '-' ) {
                nextByte = writeAndGetNextByte( prettified, tokeniser, nextByte );
            }

            while( Character.isDigit( nextByte ) ) {
                prettified.append( nextByte );
                nextByte = getNextByte( tokeniser );
            }
        }
    }

    private void procObject( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        startObject( prettified );

        JSONInputTokeniser.Token next;
        int                      cnt = 0;

        do {
            if ( cnt++ > 0 ) {
                prettified.append( "," ).append( "\n" );
            }

            tokeniser.getString( _str );

            prettified.append( _depthSpaces ).append( '"' ).append( _str ).append( '"' );
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            prettified.append( " : " );
            procValue( prettified, tokeniser );
            next = tokeniser.nextToken();
        } while( next == JSONInputTokeniser.Token.CommaSeperator );

        tokeniser.pushbackLastChar();

        tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

        endObject( prettified );
    }

    private void procString( final ReusableString prettified, final JSONInputTokeniser tokeniser, byte nextByte ) throws Exception {
        tokeniser.pushbackLastChar();
        tokeniser.getString( _str );
        prettified.append( '"' ).append( _str ).append( '"' );
    }

    private void procTrue( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        checkMatch( tokeniser, "rue" );
        prettified.append( "true" );
    }

    private void procValue( final ReusableString prettified, final JSONInputTokeniser tokeniser ) throws Exception {
        byte nextByte = tokeniser.nextNonSpaceChar();

        if ( nextByte == '"' ) { // string
            procString( prettified, tokeniser, nextByte );
            return; // NO UNDO OF NEXT BYTE
        } else if ( Character.isDigit( (char) nextByte ) || nextByte == '-' || nextByte == '+' || nextByte == Z_INFINITY[ 0 ] ) { // number
            procNumber( prettified, tokeniser, nextByte );
        } else if ( nextByte == '[' ) {
            procArray( prettified, tokeniser );
            return;
        } else if ( nextByte == 't' ) {
            procTrue( prettified, tokeniser );
            return; // NO UNDO OF NEXT BYTE
        } else if ( nextByte == 'f' ) {
            procFalse( prettified, tokeniser );
            return; // NO UNDO OF NEXT BYTE
        } else if ( nextByte == 'n' ) {
            procNull( prettified, tokeniser );
            return; // NO UNDO OF NEXT BYTE
        } else if ( nextByte == '{' ) {
            procObject( prettified, tokeniser );
            return; // NO UNDO OF NEXT BYTE
        } else {
            throw new JSONException( "Bad Value at idx " + tokeniser.getIndex() );
        }
        tokeniser.pushbackLastChar();
    }

    private void setSpaces( final int depth ) {
        _depthSpaces.reset();
        for ( int i = 0; i < depth; i++ ) {
            _depthSpaces.append( ONE_INDENT_SPACES );
        }
    }

    private void skipInfinity( final ReusableString prettified, final JSONInputTokeniser tokeniser, byte nextByte ) throws IOException, JSONException {
        prettified.append( nextByte );

        for ( int i = 1; i < Z_INFINITY.length; i++ ) {
            nextByte = getNextByte( tokeniser );

            if ( nextByte == Z_INFINITY[ i ] ) {
                prettified.append( nextByte );
            } else {
                throw new JSONException( "Bad Value at idx " + tokeniser.getIndex() );
            }
        }

        nextByte = getNextByte( tokeniser );
    }

    private void startObject( final ReusableString prettified ) {
        prettified.append( "{" ).append( "\n" );
        setSpaces( ++_depth );
    }

    private byte writeAndGetNextByte( final ReusableString prettified, final JSONInputTokeniser tokeniser, byte nextByte ) throws IOException {
        prettified.append( nextByte );
        nextByte = getNextByte( tokeniser );
        return nextByte;
    }
}
