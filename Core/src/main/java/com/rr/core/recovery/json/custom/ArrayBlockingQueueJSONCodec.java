package com.rr.core.recovery.json.custom;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.recovery.json.*;

import java.util.concurrent.ArrayBlockingQueue;

import static com.rr.core.recovery.json.custom.CustomJSONCodecs.*;

public class ArrayBlockingQueueJSONCodec implements JSONClassCodec {

    private static final String CAPACITY = "capacity";

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {

        ArrayBlockingQueue coll = (ArrayBlockingQueue) val;

        startObjectPrepForNextField( writer, val, objectId, true, PersistMode.AllFields, null );

        writer.write( writer.getWorkStr().copy( "\"" ).append( CAPACITY ).append( "\" : " ).append( coll.size() + coll.remainingCapacity() ) );

        writer.writeEndLineDelim();

        writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );

        writer.startArray( coll.size(), false, null, 0 );

        boolean first = true;
        for ( Object v : coll ) {
            if ( first ) {
                first = false;
            } else {
                writer.writeEndLineDelim();
            }
            CustomJSONCodecs.childObjectToJson( writer, v );
        }

        writer.endArray( false, 0 );

        writer.endObject();
    }

    @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int jsonId ) throws Exception {

        JSONInputTokeniser tokeniser = reader.getTokeniser();

        if ( postClass == null ) {
            tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );
            jsonId    = CustomJSONCodecs.decodeJsonId( tokeniser, tmpStr );
            postClass = CustomJSONCodecs.decodeClassName( reader, tokeniser, tmpStr );
        }

        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        readString( reader, tmpStr );

        skipSmtIdAndSmtReg( reader, tmpStr );

        if ( !tmpStr.equals( CAPACITY ) ) throw new JSONException( "ArrayBlockingQueue.decode .. expected '" + CAPACITY + "' not " + tmpStr );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        int size = reader.getInt();

        ArrayBlockingQueue coll = new ArrayBlockingQueue( size, false );

        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
        tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.value );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

        tokeniser.nextToken( JSONInputTokeniser.Token.StartArray );

        byte nextByte = tokeniser.nextNonSpaceChar();

        if ( tokeniser.getToken( nextByte ) != JSONInputTokeniser.Token.EndArray ) {

            tokeniser.pushbackLastChar();

            Object o = null;

            do {
                o = reader.procValue( null );

                if ( o != null ) {
                    reader.setFieldHandleMissingRef( o, ( resolved ) -> coll.add( (Event) resolved ) );
                }

                JSONInputTokeniser.Token t = tokeniser.nextToken();

                if ( t != JSONInputTokeniser.Token.CommaSeperator ) {
                    tokeniser.pushbackLastChar();
                    break;
                }

            } while( true );

            tokeniser.nextToken( JSONInputTokeniser.Token.EndArray );
        }

        tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

        if ( jsonId > 0 ) {
            Resolver resolver = reader.getResolver();
            if ( resolver != null ) {
                resolver.store( jsonId, coll, false );
            }
        }

        return coll;
    }

    @Override public boolean checkWritten()  { return true; }

}
