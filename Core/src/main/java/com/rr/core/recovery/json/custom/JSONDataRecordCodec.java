package com.rr.core.recovery.json.custom;

import com.rr.core.datarec.JSONDataRecord;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.recovery.json.*;

import static com.rr.core.lang.Constants.Z_INFINITY;

@SuppressWarnings( "unchecked" )

public class JSONDataRecordCodec implements JSONClassCodec {

    private static final Logger _log = LoggerFactory.create( JSONDataRecordCodec.class );

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
        boolean started = writer.startObject( val, objectId, true, PersistMode.AllFields, null );

        JSONDataRecord jdr = (JSONDataRecord) val;

        ZString vals = jdr.getVals();

        if ( vals != null && vals.length() > 0 ) {
            if ( started ) {
                writer.writeEndLineDelim();
            } else {
                writer.writeSpaces();
            }

            writer.write( vals );
        } else {
            writer.writeSpaces();
        }

        writer.endObject();
    }

    @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int jsonId ) throws Exception {

        JSONInputTokeniser tokeniser = reader.getTokeniser();

        if ( postClass == null ) {
            tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );
            jsonId    = CustomJSONCodecs.decodeJsonId( tokeniser, tmpStr );
            postClass = CustomJSONCodecs.decodeClassName( reader, tokeniser, tmpStr );
        }

        JSONDataRecord record = new JSONDataRecord();

        JSONInputTokeniser.Token t = tokeniser.nextToken();

        if ( t == JSONInputTokeniser.Token.CommaSeperator ) {

            while( true ) {
                reader.getString( tmpStr );

                if ( tmpStr.length() == 0 ) {
                    break;
                }

                String key = tmpStr.toString();

                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

                byte nextByte = tokeniser.nextNonSpaceChar();

                Object val = null;

                if ( nextByte == '"' ) { // string
                    tokeniser.pushbackLastChar();
                    tokeniser.getString( tmpStr );
                    val = tmpStr;
                    record.add( key, val );
                } else if ( Character.isDigit( (char) nextByte ) ) { // number
                    String num = reader.procNumber( nextByte );
                    val = Double.parseDouble( num );
                    record.add( key, val );
                } else if ( nextByte == Z_INFINITY[ 0 ] ) { // number
                    tokeniser.pushbackLastChar();
                    String num = reader.procNumber( nextByte );
                    record.addRaw( key, num );
                } else if ( nextByte == '-' || nextByte == '+' || nextByte == '+' ) { // number
                    String num = reader.procNumber( nextByte );

                    if ( (byte) num.charAt( 0 ) == Z_INFINITY[ 0 ] || (byte) num.charAt( 1 ) == Z_INFINITY[ 0 ] ) {
                        record.addRaw( key, num );
                    } else {
                        val = Double.parseDouble( num );
                        record.add( key, val );
                    }

                } else if ( nextByte == '{' ) {

                    ReusableString ts = TLC.strPop();

                    ts.append( nextByte );

                    int lvl = 1;

                    while( (nextByte = tokeniser.nextByte()) != '}' || lvl > 1 ) {
                        if ( nextByte == '{' ) ++lvl;
                        else if ( nextByte == '}' ) --lvl;
                        ts.append( nextByte );
                    }

                    ts.append( nextByte );

                    record.addRaw( key, ts );

                    TLC.strPush( ts );

                } else {
                    tokeniser.pushbackLastChar();

                    val = reader.procValue( null );

                    record.add( key, val );
                }

                t = tokeniser.nextToken();

                if ( t != JSONInputTokeniser.Token.CommaSeperator ) {
                    tokeniser.pushbackLastChar();
                    break;
                }
            }
        } else {
            tokeniser.pushbackLastChar();
        }

        tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

        if ( jsonId > 0 ) {
            Resolver resolver = reader.getResolver();
            if ( resolver != null ) {
                resolver.store( jsonId, record, false );
            }
        }

        return record;
    }

    @Override public boolean checkWritten()  { return true; }
}
