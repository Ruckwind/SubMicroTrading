package com.rr.core.recovery.json.custom;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.recovery.json.*;

import java.util.Map;
import java.util.Set;

import static com.rr.core.recovery.json.JSONCommon.JSON_NULL;
import static com.rr.core.recovery.json.custom.CustomJSONCodecs.startObjectPrepForNextField;

@SuppressWarnings( "unchecked" )

public class MapJSONCodec implements JSONClassCodec {

    private static final Logger _log = LoggerFactory.create( MapJSONCodec.class );

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
        Map<?, ?> map = (Map<?, ?>) val;

        startObjectPrepForNextField( writer, val, objectId, true, PersistMode.AllFields, null );

        writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.entrySet.getVal() ).append( "\" : " ) );
        writer.nextLine();

        Set<? extends Map.Entry<?, ?>> entries = map.entrySet();

        writer.startArray( entries.size(), false, null, 0 );

        boolean first = true;
        for ( Map.Entry<?, ?> e : entries ) {
            if ( first ) {
                first = false;
            } else {
                writer.writeEndLineDelim();
            }

            Object keyVal = e.getKey();
            if ( keyVal == null ) continue;

            writer.write( '{' );

            writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.key.getVal() ).append( "\" : " ) );

            CustomJSONCodecs.childObjectToJson( writer, keyVal );

            writer.write( " , " );
            writer.write( writer.getWorkStr().copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );

            Object fieldVal = e.getValue();

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

        Map<Object, Object> map = createMap( postClass );

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

                if ( key != null ) {
                    reader.setFieldHandleMissingRef( key, value, ( resolvedKey, resolvedValue ) -> { if ( resolvedValue != null ) map.put( resolvedKey, resolvedValue ); } );
                } else {
                    _log.info( "skip null key " );
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

    private Map<Object, Object> createMap( final Class<?> postClass ) throws Exception {
        if ( postClass.getName().contains( "SingletonMap" ) ) return new SingletonMap<>();

        return (Map<Object, Object>) postClass.newInstance();
    }
}
