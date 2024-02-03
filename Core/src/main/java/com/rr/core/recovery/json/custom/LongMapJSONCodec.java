package com.rr.core.recovery.json.custom;

import com.rr.core.collections.LongMap;
import com.rr.core.lang.ReusableString;
import com.rr.core.recovery.json.*;

import java.util.Collection;

import static com.rr.core.recovery.json.JSONCommon.JSON_NULL;
import static com.rr.core.recovery.json.custom.CustomJSONCodecs.startObjectPrepForNextField;

@SuppressWarnings( "unchecked" )

public class LongMapJSONCodec implements JSONClassCodec {

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
        LongMap<?> map = (LongMap<?>) val;

        startObjectPrepForNextField( writer, val, objectId, true, PersistMode.AllFields, null );

        writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.entrySet.getVal() ).append( "\" : " ) );
        writer.nextLine();

        Collection<Long> keys = map.keys();

        writer.startArray( keys.size(), false, null, 0 );

        boolean first = true;
        for ( Long keyVal : keys ) {
            if ( first ) {
                first = false;
            } else {
                writer.writeEndLineDelim();
            }

            if ( keyVal == null ) continue;

            writer.write( '{' );

            writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.key.getVal() ).append( "\" : " ) );
            CustomJSONCodecs.childObjectToJson( writer, keyVal );

            writer.write( " , " );
            writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );

            Object fieldVal = map.get( keyVal );

            if ( fieldVal == null ) {
                writer.write( JSON_NULL );
            } else {
                CustomJSONCodecs.childObjectToJson( writer, fieldVal );
            }

            writer.write( '}' );
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

        LongMap<Object> map = (LongMap<Object>) postClass.newInstance();

        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
        tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.entrySet );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

        tokeniser.nextToken( JSONInputTokeniser.Token.StartArray );

        byte nxt = tokeniser.nextNonSpaceChar();

        if ( tokeniser.getToken( nxt ) != JSONInputTokeniser.Token.EndArray ) {

            tokeniser.pushbackLastChar();

            do {

                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );

                tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.key );
                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
                Object key = reader.procValue( null );

                tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

                tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.value );
                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
                Object value = reader.procValue( null );

                if ( key != null && value != null && key instanceof Number ) {
                    reader.setFieldHandleMissingRef( value, ( resolved ) -> { if ( resolved != null ) map.put( ((Number) key).longValue(), resolved ); } );
                }

                tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

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
                resolver.store( jsonId, map, false );
            }
        }

        return map;
    }

    @Override public boolean checkWritten()  { return true; }
}
