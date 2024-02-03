package com.rr.core.recovery.json.custom;

import com.rr.core.lang.ReusableString;
import com.rr.core.recovery.json.*;
import com.rr.core.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.Collection;

import static com.rr.core.recovery.json.custom.CustomJSONCodecs.startObjectPrepForNextField;

@SuppressWarnings( "unchecked" )

public class CollectionJSONCodec implements JSONClassCodec {

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
        Collection<?> coll = (Collection<?>) val;

        startObjectPrepForNextField( writer, val, objectId, true, PersistMode.AllFields, null );

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

        Collection<Object> coll     = null;
        boolean            tempColl = false;

        try {
            coll = (Collection<Object>) postClass.newInstance();
        } catch( Exception e ) {
            tempColl = true;
            coll     = new ArrayList<>();
        }

        final Collection<Object> tcol = coll;

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
                    reader.setFieldHandleMissingRef( o, ( resolved ) -> tcol.add( resolved ) );
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

        if ( tempColl ) {
            if ( coll.size() == 0 ) {

                Object[] e    = new Object[ 0 ];
                Class[]  argc = { e.getClass() };
                Object[] args = { e };

                coll = (Collection<Object>) ReflectUtils.create( postClass, argc, args );

            } else {
                Object[] e    = coll.toArray();
                Class[]  argc = { e.getClass() };
                Object[] args = { e };

                coll = (Collection<Object>) ReflectUtils.create( postClass, argc, args );
            }
        }

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
