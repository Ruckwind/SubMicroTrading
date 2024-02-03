package com.rr.core.recovery.json.custom;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.recovery.json.*;
import com.rr.core.utils.ReflectUtils;

import java.lang.invoke.SerializedLambda;

import static com.rr.core.recovery.json.custom.CustomJSONCodecs.readString;
import static com.rr.core.recovery.json.custom.CustomJSONCodecs.startObjectPrepForNextField;

public final class SerializedLambdaJSONCodec implements JSONClassCodec {

    @Override public boolean useReferences() { return true; }

    @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {

        final Class<?> aClass = val.getClass();

        SerializedLambda sl = null;

        if ( ReflectUtils.isSerializableLambda( aClass ) ) {
            sl = ReflectUtils.serializeLambda( val );
        }

        if ( sl == null ) {
            throw new JSONException( "SerializedLambdaJSONCodec object is not a serializable lambda " + aClass.getName() + ", cant use with concrete class" );
        }

        JSONClassDefinition jcd = writer.getCache().getDefinition( val.getClass() );

        startObjectPrepForNextField( writer, sl, objectId, true, PersistMode.AllFields, jcd );

        writer.write( writer.getWorkStr().copy( "\"" ).append( "capturingClass" ).append( "\" : \"" ).append( sl.getCapturingClass() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "functionalInterfaceClass" ).append( "\" : \"" ).append( sl.getFunctionalInterfaceClass() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "functionalInterfaceMethodName" ).append( "\" : \"" ).append( sl.getFunctionalInterfaceMethodName() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "functionalInterfaceMethodSignature" ).append( "\" : \"" ).append( sl.getFunctionalInterfaceMethodSignature() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "implMethodKind" ).append( "\" : " ).append( sl.getImplMethodKind() ).append( "," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "implClass" ).append( "\" : \"" ).append( sl.getImplClass() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "implMethodName" ).append( "\" : \"" ).append( sl.getImplMethodName() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "implMethodSignature" ).append( "\" : \"" ).append( sl.getImplMethodSignature() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "instantiatedMethodType" ).append( "\" : \"" ).append( sl.getInstantiatedMethodType() ).append( "\"," ) );
        writer.nextLine();
        writer.write( writer.getWorkStr().copy( "\"" ).append( "capturedArgs" ).append( "\" : " ) );

        int argCnt = sl.getCapturedArgCount();

        Object[] args = new Object[ argCnt ];
        for ( int i = 0; i < argCnt; i++ ) {
            args[ i ] = sl.getCapturedArg( i );
        }

        CustomJSONCodecs.childObjectToJson( writer, args );

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

        String className = readKeyValString( reader, tmpStr, "capturingClass" );
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
        className = className.replace( '/', '.' );
        Class<?> capturingClass = Class.forName( className );

        String functionalInterfaceClass = readKeyValString( reader, tmpStr, "functionalInterfaceClass" );
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String functionalInterfaceMethodName = readKeyValString( reader, tmpStr, "functionalInterfaceMethodName" );
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String functionalInterfaceMethodSignature = readKeyValString( reader, tmpStr, "functionalInterfaceMethodSignature" );
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        readString( reader, tmpStr );
        if ( !tmpStr.equals( "implMethodKind" ) ) throw new JSONException( "SerializedLambdaJSONCodec.decode .. expected 'implMethodKind' not " + tmpStr );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        int implMethodKind = tokeniser.getInteger();
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String implClass = readKeyValString( reader, tmpStr, "implClass" );
        ;
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String implMethodName = readKeyValString( reader, tmpStr, "implMethodName" );
        ;
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String implMethodSignature = readKeyValString( reader, tmpStr, "implMethodSignature" );
        ;
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        String instantiatedMethodType = readKeyValString( reader, tmpStr, "instantiatedMethodType" );
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

        readString( reader, tmpStr );
        if ( !tmpStr.equals( "capturedArgs" ) ) throw new JSONException( "SerializedLambdaJSONCodec.decode .. expected 'capturedArgs' not " + tmpStr );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

        Object[] capturedArgs = (Object[]) reader.procValue( null );

        SerializedLambda sl = new SerializedLambda( capturingClass,
                                                    functionalInterfaceClass,
                                                    functionalInterfaceMethodName,
                                                    functionalInterfaceMethodSignature,
                                                    implMethodKind,
                                                    implClass,
                                                    implMethodName,
                                                    implMethodSignature,
                                                    instantiatedMethodType,
                                                    capturedArgs );

        tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

        Object val = null;
        try {
            val = ReflectUtils.deserialiseLambda( sl );
        } catch( Throwable e ) {
            ReusableString rs = TLC.strPop();
            ReflectUtils.dump( rs, sl );
            throw new JSONException( "SerializedLambdaJSONCodec exception deserialising lamda " + sl + " : " + e.getMessage(), e );
        }

        if ( jsonId > 0 ) {
            Resolver resolver = reader.getResolver();
            if ( resolver != null ) {
                resolver.store( jsonId, val, false );
            }
        }

        return val;
    }

    @Override public boolean checkWritten()  { return true; }

    private String readKeyValString( final JSONReader reader, final ReusableString tmpStr, final String expectedKey ) throws Exception {
        JSONInputTokeniser tokeniser = reader.getTokeniser();
        readString( reader, tmpStr );
        if ( !tmpStr.equals( expectedKey ) ) throw new JSONException( "SerializedLambdaJSONCodec.decode .. expected '" + expectedKey + "' not " + tmpStr );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        readString( reader, tmpStr );
        return tmpStr.length() == 0 ? null : tmpStr.toString();
    }
}

