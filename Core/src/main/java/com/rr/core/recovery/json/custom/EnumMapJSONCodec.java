package com.rr.core.recovery.json.custom;

import com.rr.core.lang.ReusableString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.recovery.json.*;
import com.rr.core.utils.ReflectUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.rr.core.recovery.json.JSONCommon.JSON_NULL;
import static com.rr.core.recovery.json.custom.CustomJSONCodecs.startObjectPrepForNextField;

@SuppressWarnings( "unchecked" )

public class EnumMapJSONCodec implements JSONClassCodec {

    private static final Logger _log       = LoggerFactory.create( EnumMapJSONCodec.class );
    private static final String ENUM_CLASS = "enumClass";

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
        EnumMap<?, ?> map = (EnumMap<?, ?>) val;

        startObjectPrepForNextField( writer, val, objectId, true, PersistMode.AllFields, null );

        ReusableString workStr = writer.getWorkStr();

        final Class<?> kt = ReflectUtils.get( val.getClass(), "keyType", val );

        writer.write( workStr.copy( "\"" ).append( ENUM_CLASS ).append( "\" : \"" ).append( kt.getName() ).append( "\"," ) );
        writer.nextLine();

        writer.write( workStr.copy( "\"" ).append( JSONSpecialTags.entrySet.getVal() ).append( "\" : " ) );
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

            writer.write( workStr.copy( "\"" ).append( JSONSpecialTags.key.getVal() ).append( "\" : " ) );

            CustomJSONCodecs.childObjectToJson( writer, keyVal );

            writer.write( " , " );
            writer.write( workStr.copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );

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

        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
        reader.getString( tmpStr );
        if ( !tmpStr.equals( ENUM_CLASS ) ) throw new JSONException( "EnumMapJSONCodec.decode .. expected '" + ENUM_CLASS + "' not " + tmpStr );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        reader.getString( tmpStr );

        Class<?> enumType = ReflectUtils.getClass( tmpStr.toString() );

        EnumMap map = new EnumMap( enumType );

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
                    reader.setFieldHandleMissingRef( key, value, ( resolvedKey, resolvedValue ) -> { if ( resolvedValue != null ) map.put( (Enum) resolvedKey, resolvedValue ); } );
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
}
