package com.rr.core.recovery.json.custom;

import com.rr.core.collections.ArrayBlockingEventQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.collections.IntMap;
import com.rr.core.collections.LongMap;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.datarec.JSONDataRecord;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.SerializableLambda;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.recovery.json.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rr.core.recovery.json.JSONCommon.JSON_NULL;

@SuppressWarnings( "unchecked" )

public class CustomJSONCodecs implements SMTInitialisableComponent {

    private static final Logger _log = LoggerFactory.create( CustomJSONCodecs.class );

    /**
     * optimal custom codecs for common types
     */

    public static final class ClassJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != Class.class ) throw new JSONException( "ClassJSONCodec can only be used for Class not " + val.getClass().getSimpleName() );

            final Class v = (Class) val;

            writeString( writer, v.getName() ); // @TODO wite GC version of Class and allow StringBuilder/ReusableString to be passed in
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            return Class.forName( tmpStr.toString() );
        }
    }

    public static final class TimeZoneJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( !(val instanceof TimeZone) ) throw new JSONException( "ClassJSONCodec can only be used for Class not " + val.getClass().getSimpleName() );

            final TimeZone v = (TimeZone) val;

            writeString( writer, v.getID() ); // @TODO wite GC version of Class and allow StringBuilder/ReusableString to be passed in
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            return TimeZone.getTimeZone( tmpStr.toString() );
        }

        @Override public boolean useCodec( Object o, WriteContext ctx, boolean isTopLevel ) {
            return !isTopLevel;
        }
    }

    public static final class ReflectFieldJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != Field.class ) throw new JSONException( "ReflectFieldJSONCodec can only be used for Class not " + val.getClass().getSimpleName() );

            final Field f = (Field) val;

            startObjectPrepForNextField( writer, null, null, false, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( "fieldClass" ).append( "\" : \"" ).append( f.getDeclaringClass().getName() ).append( "\"," ) );
            writer.nextLine();
            writer.write( writer.getWorkStr().copy( "\"" ).append( "fieldName" ).append( "\" : \"" ).append( f.getName() ).append( '"' ) );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            readString( reader, tmpStr );
            if ( !tmpStr.equals( "fieldClass" ) ) throw new JSONException( "ReflectFieldJSONCodec.decode .. expected 'fieldClass' not " + tmpStr );
            readString( reader, tmpStr );
            if ( tmpStr.length() == 0 ) return null;
            Class<?> clazz = Class.forName( tmpStr.toString() );
            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

            readString( reader, tmpStr );
            if ( !tmpStr.equals( "fieldName" ) ) throw new JSONException( "ReflectFieldJSONCodec.decode .. expected 'fieldName' not " + tmpStr );
            readString( reader, tmpStr );

            Field f = ReflectUtils.getMember( clazz, tmpStr.toString() );

            return f;
        }
    }

    public static final class ReflectMethodJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            throw new SMTRuntimeException( "java.lang.reflect.Method Not Implemented - make transient and derive in postRestore" );
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            throw new SMTRuntimeException( "Not Implemented" );
        }
    }

    public static final class MessageQueueJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            throw new SMTRuntimeException( "MessageQueue Not Implemented - make transient and derive in postRestore or use collection" );
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            throw new SMTRuntimeException( "MessageQueue Not Implemented - make transient and derive in postRestore or use collectionm class=" + postClass.getSimpleName() );
        }
    }

    public static final class LoggerJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( !(val instanceof Logger) ) throw new JSONException( "LoggerJSONCodec can only be used for Logger not " + val.getClass().getSimpleName() );

            Logger l = (Logger) val;

            writeString( writer, l.getTagClass().getName() ); // @TODO wite GC version of BigDecimal and allow StringBuilder/ReusableString to be passed in
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            return LoggerFactory.create( Class.forName( tmpStr.toString() ) );
        }
    }

    public static final class BigDecimalJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != BigDecimal.class ) throw new JSONException( "BigDecimalJSONCode can only be used for BigDecimal not " + val.getClass().getSimpleName() );

            final BigDecimal bd = (BigDecimal) val;

            writeString( writer, bd.toPlainString() ); // @TODO wite GC version of BigDecimal and allow StringBuilder/ReusableString to be passed in
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            return new BigDecimal( tmpStr.toString() );
        }
    }

    public static final class BigIntegerJSONCodec implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != BigInteger.class ) throw new JSONException( "BigIntegerJSONCode can only be used for BigInteger not " + val.getClass().getSimpleName() );

            final BigInteger bd = (BigInteger) val;

            writeString( writer, bd.toString() ); // @TODO wite GC version of BigInteger and allow StringBuilder/ReusableString to be passed in
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            return new BigInteger( tmpStr.toString() );
        }
    }

    public static final class GeneralEnumJSONCodec<T extends Enum> implements JSONClassCodec {

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( !Enum.class.isAssignableFrom( val.getClass() ) ) throw new JSONException( "GeneralEnumJSONCodec can only be used for Enum not " + val.getClass().getSimpleName() );

            Enum<?> enumObj = (Enum<?>) val;

            startObjectPrepForNextField( writer, null, null, false, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( "@enum" ).append( "\" : " ) );

            writer.write( writer.getWorkStr().copy( "\"" ).append( val.getClass().getName() ).append( '.' ).append( enumObj.name() ).append( '"' ) );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            if ( tokeniser.peekIsNextNonSpaceToken() ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );

                readString( reader, tmpStr );

                JSONSpecialTags t = JSONSpecialTags.getVal( tmpStr );

                if ( t != JSONSpecialTags.containerEnum ) throw new JSONException( "GeneralEnumJSONCodec expected " + JSONSpecialTags.containerEnum.name() + " not " + tmpStr );

                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

                readString( reader, tmpStr );

                String    val     = tmpStr.toString();
                final int lastIdx = val.lastIndexOf( '.' );

                if ( lastIdx <= 0 ) {
                    int startLine = tokeniser.getLineNum();
                    throw new JSONException( "JSON container enum bad format " + tmpStr + " decoder started at line " + startLine );
                }

                String enumClassStr = val.substring( 0, lastIdx );
                String enumValStr   = val.substring( lastIdx + 1 );

                Class<Enum> enumClass = ReflectUtils.getClass( enumClassStr );

                Enum<?> eVal = Enum.valueOf( enumClass, enumValStr );

                tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

                return eVal;
            }

            // old style just one line
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            int sepIdx = tmpStr.lastIndexOf( '.' );

            if ( sepIdx <= 0 ) throw new JSONException( "EnumJSONCodec decoder expected enumClass.enumVal not  " + tmpStr );

            // already generating GC having to invoke toString, so dont worry about the extra here
            String val          = tmpStr.toString();
            String enumClassStr = val.substring( 0, sepIdx );
            String enumValStr   = val.substring( sepIdx + 1 );

            Class<Enum> enumClass = (Class<Enum>) Class.forName( enumClassStr );

            Enum<?> eVal = Enum.valueOf( enumClass, enumValStr );

            return eVal;
        }
    }

    public static final class SpecificEnumJSONCodec<T extends Enum> implements JSONClassCodec {

        private final Class<T> _enumClass;

        public SpecificEnumJSONCodec( final Class<T> fieldClass ) {
            _enumClass = fieldClass;
        }

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != _enumClass ) throw new JSONException( "Expected enum " + _enumClass.getName() + " not " + val.getClass().getSimpleName() );

            Enum<?> enumObj = (Enum<?>) val;

            startObjectPrepForNextField( writer, null, null, false, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( "@enum" ).append( "\" : " ) );

            writer.write( writer.getWorkStr().copy( "\"" ).append( val.getClass().getName() ).append( '.' ).append( enumObj.name() ).append( '"' ) );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            if ( tokeniser.peekIsNextNonSpaceToken() ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );

                readString( reader, tmpStr );

                JSONSpecialTags t = JSONSpecialTags.getVal( tmpStr );

                if ( t != JSONSpecialTags.containerEnum ) throw new JSONException( "GeneralEnumJSONCodec expected " + JSONSpecialTags.containerEnum.name() + " not " + tmpStr );

                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

                readString( reader, tmpStr );

                String    val     = tmpStr.toString();
                final int lastIdx = val.lastIndexOf( '.' );

                if ( lastIdx <= 0 ) {
                    int startLine = tokeniser.getLineNum();
                    throw new JSONException( "JSON container enum bad format " + tmpStr + " decoder started at line " + startLine );
                }

                String enumClassStr = val.substring( 0, lastIdx );
                String enumValStr   = val.substring( lastIdx + 1 );

                Class<Enum> enumClass = ReflectUtils.getClass( enumClassStr );

                Enum<?> eVal = Enum.valueOf( enumClass, enumValStr );

                tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

                return eVal;
            }

            // old style just one line
            readString( reader, tmpStr );

            if ( tmpStr.length() == 0 ) return null;

            int sepIdx = tmpStr.lastIndexOf( '.' );

            if ( sepIdx <= 0 ) throw new JSONException( "EnumJSONCodec decoder expected enumClass.enumVal not  " + tmpStr );

            // already generating GC having to invoke toString, so dont worry about the extra here
            String val          = tmpStr.toString();
            String enumClassStr = val.substring( 0, sepIdx );
            String enumValStr   = val.substring( sepIdx + 1 );

            if ( !enumClassStr.equals( _enumClass.getSimpleName() ) && !enumClassStr.equals( _enumClass.getName() ) )
                throw new JSONException( "Specific enum decode expected " + _enumClass.getName() + " not " + enumClassStr );

            final Enum eVal = Enum.valueOf( _enumClass, enumValStr );

            return eVal;
        }
    }

    private static final class Entry {

        private final Class<?>       _aClass;
        private final JSONClassCodec _codec;
        private final boolean        _canHandleSubclasses;

        public Entry( final Class<?> aClass, final JSONClassCodec codec, final boolean canHandleSubclasses ) {
            _aClass              = aClass;
            _codec               = codec;
            _canHandleSubclasses = canHandleSubclasses;
        }
    }

    public static final class DurationJSONCodec implements JSONClassCodec {

        private static final String F_SECONDS = "seconds";
        private static final String F_NANOS   = "nanos";

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != Duration.class ) throw new JSONException( "DurationJSONCodec can only be used for Duration not " + val.getClass().getSimpleName() );

            final Duration bd = (Duration) val;

            startObjectPrepForNextField( writer, val, 0, true, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( F_SECONDS ).append( "\" : " ) );
            writer.writeVal( bd.getSeconds() );

            writer.writeEndLineDelim();

            writer.write( writer.getWorkStr().copy( "\"" ).append( F_NANOS ).append( "\" : " ) );
            writer.writeVal( bd.getNano() );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            if ( postClass == null ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );
                postClass = CustomJSONCodecs.decodeClassName( reader, tokeniser, tmpStr );
            }

            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
            readString( reader, tmpStr );
            if ( !tmpStr.equals( F_SECONDS ) ) throw new JSONException( "DurationJSONCodec expected " + F_SECONDS );
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

            long secs = reader.getLong();

            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
            readString( reader, tmpStr );
            if ( !tmpStr.equals( F_NANOS ) ) throw new JSONException( "DurationJSONCodec expected " + F_NANOS );
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

            long nanos = reader.getLong();

            Duration ret = Duration.ofSeconds( secs, nanos );

            tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

            if ( objectId > 0 ) {
                Resolver resolver = reader.getResolver();
                if ( resolver != null ) {
                    resolver.store( objectId, ret, false );
                }
            }

            return ret;
        }
    }

    public static final class LocalTimeJSONCodec implements JSONClassCodec {

        private static final String F_NANO_OF_DAY = "nanoOfDay";

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != LocalTime.class ) throw new JSONException( "LocalTimeJSONCodec can only be used for LocalTime not " + val.getClass().getSimpleName() );

            final LocalTime bd = (LocalTime) val;

            long nanoOfDay = bd.toNanoOfDay();

            startObjectPrepForNextField( writer, val, 0, true, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( F_NANO_OF_DAY ).append( "\" : " ) );
            writer.writeVal( nanoOfDay );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            if ( postClass == null ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );
                postClass = CustomJSONCodecs.decodeClassName( reader, tokeniser, tmpStr );
            }

            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
            readString( reader, tmpStr );
            if ( !tmpStr.equals( F_NANO_OF_DAY ) ) throw new JSONException( "DurationJSONCodec expected " + F_NANO_OF_DAY );
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

            long nanoOfDay = reader.getLong();

            LocalTime ret = LocalTime.ofNanoOfDay( nanoOfDay );

            tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

            if ( objectId > 0 ) {
                Resolver resolver = reader.getResolver();
                if ( resolver != null ) {
                    resolver.store( objectId, ret, false );
                }
            }

            return ret;
        }
    }

    public static final class LocalDateJSONCodec implements JSONClassCodec {

        private static final String F_FROM_EPOCH_DAY = "fromEpochDay";

        @Override public boolean useReferences() { return false; }

        @Override public void encode( final JSONWriter writer, final Object val, int objectId ) throws Exception {
            if ( val.getClass() != LocalDate.class ) throw new JSONException( "LocalDateCodec can only be used for LocalDate not " + val.getClass().getSimpleName() );

            final LocalDate bd = (LocalDate) val;

            long epochDay = bd.toEpochDay();

            startObjectPrepForNextField( writer, val, 0, true, PersistMode.AllFields, null );

            writer.write( writer.getWorkStr().copy( "\"" ).append( F_FROM_EPOCH_DAY ).append( "\" : " ) );
            writer.writeVal( epochDay );

            writer.endObject();
        }

        @Override public Object decode( final JSONReader reader, final ReusableString tmpStr, Class<?> postClass, int objectId ) throws Exception {
            JSONInputTokeniser tokeniser = reader.getTokeniser();

            if ( postClass == null ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.StartObject );
                postClass = CustomJSONCodecs.decodeClassName( reader, tokeniser, tmpStr );
            }

            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
            readString( reader, tmpStr );
            if ( !tmpStr.equals( F_FROM_EPOCH_DAY ) ) throw new JSONException( "DurationJSONCodec expected " + F_FROM_EPOCH_DAY );
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );

            long epochDay = reader.getLong();

            LocalDate ret = LocalDate.ofEpochDay( epochDay );

            tokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

            if ( objectId > 0 ) {
                Resolver resolver = reader.getResolver();
                if ( resolver != null ) {
                    resolver.store( objectId, ret, false );
                }
            }

            return ret;
        }
    }
    private final String _id;
    private           Entry[]  _entries  = new Entry[ 0 ];
    private transient RunState _runState = RunState.Unknown;
    private AtomicBoolean _init = new AtomicBoolean( false );

    public static void startObjectPrepForNextField( JSONWriter writer, Object obj, Integer objectId, boolean writeType, PersistMode mode, JSONClassDefinition jcd ) throws IOException {
        if ( writer.startObject( obj, objectId, writeType, mode, jcd ) ) {
            writer.writeEndLineDelim();
        } else {
            writer.writeSpaces();
        }
    }

    public static void nextString( final JSONReader reader, final ReusableString tmpStr, final String expected ) throws Exception {
        reader.getString( tmpStr );
        if ( !tmpStr.equals( expected ) ) {
            final JSONInputTokeniser t = reader.getTokeniser();

            throw new JSONException( "expected [" + expected + "] but got [" + tmpStr + "] at line " + t.getLineNum() );
        }
    }

    /**
     * helper functions
     */
    public static void writeString( final JSONWriter writer, String str ) throws IOException {
        writer.write( '"' );
        writer.write( str );
        writer.write( '"' );
    }

    public static void readString( final JSONReader reader, final ReusableString tmpStr ) throws Exception {
        reader.getString( tmpStr );
    }

    public static void skipSmtIdAndSmtReg( final JSONReader reader, final ReusableString tmpStr ) throws Exception {
        JSONInputTokeniser tokeniser = reader.getTokeniser();

        if ( tmpStr.equals( JSONSpecialTags.smtId.getVal() ) ) {
            tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            readString( reader, tmpStr );
            tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
            readString( reader, tmpStr );

            if ( tmpStr.equals( JSONSpecialTags.smtComponentRegistered.getVal() ) ) {
                tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
                reader.getBoolean();
                tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
                readString( reader, tmpStr );
            }
        }
    }

    public static int decodeJsonId( final JSONInputTokeniser tokeniser, final ReusableString tmpStr ) throws Exception {
        tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.jsonId );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        int jsonId = tokeniser.getInteger();
        tokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
        return jsonId;
    }

    public static Class<?> decodeClassName( final JSONReader reader, final JSONInputTokeniser tokeniser, final ReusableString tmpStr ) throws Exception {
        tokeniser.nextSpecialTag( tmpStr, JSONSpecialTags.className );
        tokeniser.nextToken( JSONInputTokeniser.Token.Colon );
        readString( reader, tmpStr );
        return Class.forName( tmpStr.toString() );
    }

    public static boolean checkOverrideShowType( final Object val ) {
        if ( val != null ) {
            final Class<?> vClass = val.getClass();
            if ( vClass == String.class ) {
                return false;
            }
        }
        return true;
    }

    public static void childObjectToJson( final JSONWriter writer, final Object val ) throws Exception {
        if ( val == null ) {

            writer.write( JSON_NULL );

        } else {
            boolean showType = CustomJSONCodecs.checkOverrideShowType( val );

            if ( val instanceof Enum ) {
                Enum<?> enumObj = (Enum<?>) val;

                startObjectPrepForNextField( writer, null, null, false, PersistMode.AllFields, null );

                writer.write( writer.getWorkStr().copy( "\"" ).append( "@enum" ).append( "\" : " ) );

                writer.write( writer.getWorkStr().copy( "\"" ).append( val.getClass().getName() ).append( '.' ).append( enumObj.name() ).append( '"' ) );

                writer.endObject();
            } else {
                writer.childObjectToJson( val, null, showType, PersistMode.AllFields, true, false );
            }
        }
    }

    public static void childObjectToJson( final JSONWriter writer, final Object val, boolean showType ) throws Exception {
        if ( val == null ) {

            writer.write( JSON_NULL );

        } else {
            if ( val instanceof Enum ) {
                Enum<?> enumObj = (Enum<?>) val;

                startObjectPrepForNextField( writer, null, null, false, PersistMode.AllFields, null );

                writer.write( writer.getWorkStr().copy( "\"" ).append( "@enum" ).append( "\" : " ) );

                writer.write( writer.getWorkStr().copy( "\"" ).append( val.getClass().getName() ).append( '.' ).append( enumObj.name() ).append( '"' ) );

                writer.endObject();
            } else {
                writer.childObjectToJson( val, null, showType, PersistMode.AllFields, true, false );
            }
        }
    }

    public static boolean isNull( final JSONInputTokeniser tokeniser ) throws Exception {
        byte next = tokeniser.nextNonSpaceChar();

        boolean isNull = false;

        if ( next == 'n' ) {
            tokeniser.match( "ull" );

            isNull = true;
        } else {
            tokeniser.pushbackLastChar();
        }

        return isNull;
    }

    /**
     * MUST PREREG CUSTOM CODECS HERE
     */
    public CustomJSONCodecs( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    /**
     * @param ctx
     * @WARNING THIS METHOD CAN BE INVOKED MULTIPLE TIMES ..> ENSURE IDEMPOTENT !
     */
    @Override public void init( final SMTStartContext ctx, CreationPhase created ) {
        _init.set( true );

        register( Class.class, new ClassJSONCodec(), false );
        register( TimeZone.class, new TimeZoneJSONCodec(), true );
        register( Duration.class, new DurationJSONCodec(), true );
        register( LocalTime.class, new LocalTimeJSONCodec(), true );
        register( LocalDate.class, new LocalDateJSONCodec(), true );
        register( SerializableLambda.class, new SerializedLambdaJSONCodec(), true );
        register( SerializedLambda.class, new SerializedLambdaJSONCodec(), true );
        register( Field.class, new ReflectFieldJSONCodec(), false );
        register( Method.class, new ReflectMethodJSONCodec(), false );
        register( BigDecimal.class, new BigDecimalJSONCodec(), false );
        register( BigInteger.class, new BigIntegerJSONCodec(), false );
        register( IntMap.class, new IntMapJSONCodec(), true );
        register( LongMap.class, new LongMapJSONCodec(), true );
        register( Logger.class, new LoggerJSONCodec(), true );
        register( EnumMap.class, new EnumMapJSONCodec(), false );
        register( Map.class, new MapJSONCodec(), true );
        register( ArrayBlockingEventQueue.class, new ArrayBlockingEventQueueJSONCodec(), true );
        register( ArrayBlockingQueue.class, new ArrayBlockingQueueJSONCodec(), true );
        register( Collection.class, new CollectionJSONCodec(), true );
        register( EventQueue.class, new MessageQueueJSONCodec(), true );
        register( Enum.class, new GeneralEnumJSONCodec(), false );
        register( JSONDataRecord.class, new JSONDataRecordCodec(), false );

        JSONPrettyDump.setCustomCodec( ctx );
    }

    public void checkInit( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        if ( _init.compareAndSet( false, true ) ) {
            init( ctx, creationPhase );
        }
    }

    public JSONClassCodec get( Class<?> aClass ) {
        final Entry[] entries = _entries;
        for ( Entry e : entries ) {
            if ( e._canHandleSubclasses ) {
                if ( e._aClass.isAssignableFrom( aClass ) ) {
                    return e._codec;
                }
            } else if ( e._aClass == aClass ) {
                return e._codec;
            }
        }

        return null;
    }

    public synchronized void register( Class<?> aClass, JSONClassCodec codec, boolean canHandleSubclasses ) {
        for ( Entry e : _entries ) {
            if ( e._aClass == aClass ) {
                if ( e._codec.getClass() == codec.getClass() ) {
                    return;
                }

                throw new SMTRuntimeException( "CustomJSONCodecs duplicate codecs for class " + aClass.getSimpleName() + ", old=" + e._codec.getClass().getSimpleName() + ", new=" + codec.getClass().getSimpleName() );
            }
        }

        Entry newEntry = new Entry( aClass, codec, canHandleSubclasses );

        for ( int i = 0; i < _entries.length; i++ ) {
            Entry e = _entries[ i ];

            if ( e._canHandleSubclasses ) {
                if ( e._aClass.isAssignableFrom( aClass ) ) { // current entry would hide new entry so insert new entry before current

                    _log.info( "CustomJSONCodecs class " + e._aClass.getName() + " would hide new custom codec " + aClass.getName() + " so insert new class before it" );

                    _entries = Utils.arrayCopyAndInsertEntry( _entries, newEntry, i );

                    return;
                }
            }
        }

        _entries = Utils.arrayCopyAndAddEntry( _entries, newEntry );
    }
}
