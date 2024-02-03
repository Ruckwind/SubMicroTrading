package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Identifiable;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.CustomJSONCodecs;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static com.rr.core.lang.Constants.*;

@SuppressWarnings( "unchecked" )

/**
 * @TODO invoke postJSONDecode ... it should be added to the JSONClassDefinition 
 */
public class JSONReaderImpl implements JSONReader {

    private static Logger _log = LoggerFactory.create( JSONReaderImpl.class );

    private InputStream              _inStream;
    private Resolver                 _resolver;
    private JSONInputTokeniser       _inputTokeniser;
    private JSONClassDefinitionCache _cache;
    private Map<String, Class<?>>    _classes = new HashMap<>( 128 );

    private DecimalFormat _df = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );

    public JSONReaderImpl( InputStream inputStream, JSONClassDefinitionCache cache ) {
        this( inputStream, new SMTComponentResolver( new SMTComponentManager() ), cache );
    }

    public JSONReaderImpl( final String jsonFileName, final SMTStartContext ctx ) throws Exception {
        this( FileUtils.bufFileInpStream( jsonFileName ), new SMTComponentResolver( new SMTComponentManager() ), new JSONClassDefinitionCache( ctx ) );
    }

    public JSONReaderImpl( InputStream inputStream, Resolver resolver, JSONClassDefinitionCache cache ) {
        _inStream       = inputStream;
        _resolver       = resolver;
        _cache          = cache;
        _inputTokeniser = new JSONInputTokeniser( _inStream );

        _classes.put( "double", double.class );
        _classes.put( "short", short.class );
        _classes.put( "int", int.class );
        _classes.put( "byte", byte.class );
        _classes.put( "char", char.class );
        _classes.put( "long", long.class );
    }

    @Override public void checkMatch( String charsToMatch ) throws Exception {
        int len = charsToMatch.length();
        for ( int i = 0; i < len; i++ ) {
            char nextChar = charsToMatch.charAt( i );
            byte nextByte = _inputTokeniser.nextByte();

            if ( nextByte != (byte) nextChar ) {
                throw new JSONException( "Expected " + nextChar + " got " + (char) nextByte + ", idx=" + _inputTokeniser.getIndex() + ", line=" + _inputTokeniser.getLineNum() );
            }
        }
    }

    @Override public boolean getBoolean() throws Exception {
        boolean val = _inputTokeniser.getBoolean();
        return val;
    }

    @Override public JSONClassDefinitionCache getCache() { return _cache; }

    @Override public double getDouble( final ReusableString tmpVal ) throws Exception {
        if ( _inputTokeniser.peekIsNextNonSpaceToken() ) {
            return Constants.UNSET_DOUBLE;
        }

        _inputTokeniser.getStringNoQuotes( tmpVal );
        _inputTokeniser.pushbackLastChar();
        if ( tmpVal.equals( "null" ) ) {
            return UNSET_DOUBLE;
        }

        double val;

        if ( tmpVal.equals( Z_INFINITY ) || tmpVal.equals( Z_POS_INFINITY ) ) {
            val = Double.POSITIVE_INFINITY;
        } else if ( tmpVal.equals( Z_NEG_INFINITY ) ) {
            val = Double.NEGATIVE_INFINITY;
        } else if ( !tmpVal.equals( "null" ) ) {
            val = (double) _df.parse( tmpVal.toString() ).doubleValue();
        } else {
            val = Constants.UNSET_DOUBLE;
        }

        return val;
    }

    @Override public int getInt() throws Exception {
        if ( _inputTokeniser.peekIsNextNonSpaceToken() ) {
            return Constants.UNSET_INT;
        }

        Integer val = _inputTokeniser.getInteger();
        return (val == null) ? Constants.UNSET_INT : val;
    }

    @Override public long getLong() throws Exception {
        if ( _inputTokeniser.peekIsNextNonSpaceToken() ) {
            return Constants.UNSET_LONG;
        }

        Long val = _inputTokeniser.getLong();
        return (val == null) ? Constants.UNSET_LONG : val;
    }

    @Override public Resolver getResolver() {
        return _resolver;
    }

    @Override public void getString( ReusableString outStr ) throws Exception {
        if ( _inputTokeniser.peekIsNextNonSpaceToken() ) {
            outStr.reset();
            return;
        }
        _inputTokeniser.getString( outStr );
    }

    @Override public JSONInputTokeniser getTokeniser() {
        return _inputTokeniser;
    }

    @Override public boolean isNullNext() throws Exception {
        byte nxt = _inputTokeniser.nextNonSpaceChar();

        if ( nxt == 'n' ) {
            _inputTokeniser.match( "ull" );
            return true;
        }

        _inputTokeniser.pushbackLastChar();
        return false;
    }

    @Override public <T> T jsonToObject() throws Exception {

        try {
            if ( isNullNext() ) return null;

            JSONInputTokeniser.Token token = _inputTokeniser.nextToken();

            switch( token ) {
            case StartObject:
                return (T) procObject( null, null, true );
            case StartArray:
                return (T) procArray( null );
            case EndStream:
                return null;
            default:
                _inputTokeniser.pushbackLastChar();
                return (T) procValue( null );
            }
        } catch( Exception e ) {
            if ( e instanceof JSONException ) throw e;

            _inputTokeniser.jsonException( "Exception " + e.getMessage(), e ); // THROWS JSON EXCEPTION
        }

        return null;
    }

    @Override public String procNumber( byte nextByte ) throws Exception {

        ReusableString s = TLC.instance().pop();

        boolean isNeg = false;

        if ( nextByte == '-' ) {
            s.append( '-' );
            if ( isInfinity() ) {
                return Z_NEG_INFINITY;
            }
        } else if ( Character.isDigit( nextByte ) ) {
            s.append( nextByte );
        } else if ( nextByte == '+' ) {
            if ( isInfinity() ) {
                return Z_POS_INFINITY;
            }
        } else if ( nextByte == Z_INFINITY[ 0 ] ) {
            _inputTokeniser.pushbackLastChar();
            if ( isInfinity() ) {
                return Z_POS_INFINITY;
            }
        }

        while( nextByte != -1 && Character.isDigit( (nextByte = _inputTokeniser.nextByte()) ) ) {
            s.append( nextByte );
        }

        if ( nextByte == '.' ) {
            nextByte = writeAndGetNextByte( s, nextByte );

            if ( nextByte == 'e' || nextByte == 'E' ) {
                nextByte = writeAndGetNextByte( s, nextByte );
            }

            if ( nextByte == '+' || nextByte == '-' ) {
                nextByte = writeAndGetNextByte( s, nextByte );
            }

            while( nextByte != -1 && Character.isDigit( nextByte ) ) {
                s.append( nextByte );
                nextByte = _inputTokeniser.nextByte();
            }
        }

        _inputTokeniser.pushbackLastChar();

        String v = s.toString();

        TLC.instance().pushback( s );

        return v;
    }

    @Override public Object procRawKnownChildObject( final JSONClassDefinition jcd, final Object dest ) throws Exception {
        byte nextByte = _inputTokeniser.nextNonSpaceChar();

        if ( nextByte == 'n' ) {
            return procNull();
        } else if ( nextByte == '{' ) {
            return doProcRawKnownChildObject( jcd, dest );
        }

        return _inputTokeniser.jsonException( "Bad Char Value [" + (char) nextByte + " / (int)" + nextByte + "]" );
    }

    /**
     * @param clazz
     * @return
     * @throws Exception
     * @NOTE currentlky short cut taken so that numbers are read back and returned as String so the reflective setFromString can be used
     * this is sub optimal and should be refactored in future when time allows ... to reduce map lookup and extended if/then/else statements
     */
    @Override public Object procValue( Class<?> clazz ) throws Exception {
        byte nextByte = _inputTokeniser.nextNonSpaceChar();

        if ( nextByte == '"' ) { // string
            _inputTokeniser.pushbackLastChar();
            ReusableString s = TLC.instance().pop();
            _inputTokeniser.getString( s );
            String v = s.toString();
            TLC.instance().pushback( s );
            return v;
        } else if ( Character.isDigit( (char) nextByte ) || nextByte == '-' || nextByte == '+' ) { // number
            return procNumber( nextByte );
        } else if ( nextByte == '[' ) {
            return procArray( clazz );
        } else if ( nextByte == 't' ) {
            return procTrue();
        } else if ( nextByte == 'f' ) {
            return procFalse();
        } else if ( nextByte == 'n' ) {
            return procNull();
        } else if ( nextByte == '{' ) {
            return procObject( clazz, null, false );
        }

        return _inputTokeniser.jsonException( "Bad Char Value [" + (char) nextByte + " / (int)" + nextByte + "]" );
    }

    @Override public void setFieldHandleMissingRef( Object val, ZConsumer<Object> setter ) {
        if ( val instanceof MissingRef ) {
            MissingRef ref = (MissingRef) val;

            ref.addResolver( setter );

            _resolver.addMissingRef( ref );
        } else {
            setter.accept( val );
        }
    }

    @Override public void setFieldHandleMissingRef( final Object val1, final Object val2, final ZConsumer2Args<Object, Object> setter ) {
        boolean val1Missing = val1 instanceof MissingRef;
        boolean val2Missing = val2 instanceof MissingRef;

        if ( val1Missing && val2Missing ) {

            /**
             * register two missing refs which when both resolved will invoke the supplied setter
             */
            new DualMissingRefResolver( (MissingRef) val1, (MissingRef) val2, _resolver, setter );

            return;
        }

        if ( val1Missing ) {
            MissingRef ref = (MissingRef) val1;

            ref.addResolver( ( v1 ) -> setter.accept( v1, val2 ) );

            _resolver.addMissingRef( ref );

            return;

        } else if ( val2Missing ) {
            MissingRef ref = (MissingRef) val2;

            ref.addResolver( ( v2 ) -> setter.accept( val1, v2 ) );

            _resolver.addMissingRef( ref );

            return;
        }

        setter.accept( val1, val2 );
    }

    public Object procValue( Class<?> clazz, Object parent ) throws Exception {
        byte nextByte = _inputTokeniser.nextNonSpaceChar();

        if ( nextByte == '"' ) { // string
            _inputTokeniser.pushbackLastChar();
            ReusableString s = TLC.instance().pop();
            _inputTokeniser.getString( s );
            String v = s.toString();
            TLC.instance().pushback( s );
            return v;
        } else if ( Character.isDigit( (char) nextByte ) || nextByte == '-' || nextByte == '+' || nextByte == Z_INFINITY[ 0 ] ) { // number
            return procNumber( nextByte );
        } else if ( nextByte == '[' ) {
            return procArray( clazz );
        } else if ( nextByte == 't' ) {
            return procTrue();
        } else if ( nextByte == 'f' ) {
            return procFalse();
        } else if ( nextByte == 'n' ) {
            return procNull();
        } else if ( nextByte == '{' ) {
            return procObject( clazz, parent, false );
        } else if ( nextByte == ',' || nextByte == ']' || nextByte == '}' ) {
            _inputTokeniser.pushbackLastChar();
            return null;
        }

        return _inputTokeniser.jsonException( "Bad Char Value [" + (char) nextByte + " / (int)" + nextByte + "]" );
    }

    private void checkPostRestoreMethod( Object res, final JSONClassDefinition jcd, final Class<?> clazz ) {
        if ( jcd.hasRestoreMethods() ) {
            List<Method> postRestoreMethods = jcd.getPostRestoreMethods();

            if ( postRestoreMethods != null ) {

                for ( Method m : postRestoreMethods ) {
                    ReflectUtils.invoke( m, res );
                }
            }
        }
    }

    private Object decode( JSONClassCodec customCodec, ReusableString tmpVal, Class<?> clazz, int jsonId ) throws Exception {

        if ( isNullNext() ) return null;

        return customCodec.decode( this, tmpVal, clazz, jsonId );
    }

    private Object doProcArray( Class<?> componentType ) throws Exception {

        List<Object> list = new ArrayList<>();

        byte nextByte;

        nextByte = _inputTokeniser.nextNonSpaceChar();

        if ( _inputTokeniser.getToken( nextByte ) != JSONInputTokeniser.Token.EndArray ) {
            _inputTokeniser.pushbackLastChar();

            do {

                list.add( procValue( componentType ) );

                nextByte = _inputTokeniser.nextNonSpaceChar();

            } while( nextByte == ',' );

            _inputTokeniser.pushbackLastChar();

            _inputTokeniser.nextToken( JSONInputTokeniser.Token.EndArray );
        }

        if ( componentType != null && componentType.isPrimitive() ) { // primitive array

            Object dest = Array.newInstance( componentType, list.size() );

            for ( int i = 0; i < list.size(); i++ ) {
                Object v = list.get( i );

                if ( v == null ) {
                    ReflectUtils.setArrayFieldEntryNull( componentType, dest, i );
                } else if ( v instanceof String ) {
                    ReflectUtils.setArrayFieldEntryFromString( componentType, dest, i, (String) v );
                } else {
                    Array.set( dest, i, v ); //@TODO pass in the JSONFieldType and use the primitive setter to avoid autoboxing
                }
            }

            return dest;
        }

        Object[] dest = (componentType != null) ? (Object[]) Array.newInstance( componentType, list.size() ) : new Object[ list.size() ];

        for ( int i = 0; i < list.size(); i++ ) {
            Object v = list.get( i );

            if ( v instanceof String ) {
                ReflectUtils.setArrayFieldEntryFromString( componentType, dest, i, (String) v );
            } else {
                final int idx = i;

                setFieldHandleMissingRef( v, ( resolved ) -> Array.set( dest, idx, resolved ) );
            }
        }

        return dest;
    }

    private Object doProcRawKnownChildObject( final JSONClassDefinition jcd, final Object res ) throws Exception {

        final Class<?> clazz = res.getClass();

        JSONInputTokeniser.Token next;

        ReusableString tmpStr = TLC.instance().pop();
        ReusableString tmpVal = TLC.instance().pop();

        boolean preExisted = false;

        try {
            do {
                _inputTokeniser.getString( tmpStr );
                _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );

                boolean fieldProcessed = false;

                if ( tmpStr.equals( JSONSpecialTags.className.getVal() ) ) {
                    _inputTokeniser.getString( tmpStr );

                    if ( !tmpStr.equals( clazz.getName() ) ) {
                        throw new SMTRuntimeException( "JSON doProcRawKnownChildObject expected class to be " + clazz.getName() + " not " + tmpStr.toString() );
                    }

                    fieldProcessed = true;

                } else if ( tmpStr.equals( JSONSpecialTags.jsonId.getVal() ) ) {

                    int jsonId = _inputTokeniser.getInteger();

                    if ( jsonId > 0 ) {
                        if ( _resolver != null ) {
                            _resolver.store( jsonId, res, false );
                        }
                    }

                    fieldProcessed = true;
                }

                if ( !fieldProcessed ) {
                    JSONClassDefinition.FieldEntry f = null;

                    if ( jcd != null ) {
                        f = jcd.getField( tmpStr );

                        if ( f != null ) {
                            JSONClassCodec customCodec = f.getCustomCodec();
                            if ( customCodec != null ) {
                                handleCustomMemberField( clazz, res, tmpStr, tmpVal, preExisted, f, customCodec );
                                fieldProcessed = true;
                            }
                        }
                    }

                    if ( !fieldProcessed ) {
                        setMemberFieldValue( clazz, res, tmpStr, jcd, preExisted, f );
                    }
                }

                next = _inputTokeniser.nextToken();

            } while( next == JSONInputTokeniser.Token.CommaSeperator );

            _inputTokeniser.pushbackLastChar();
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

        } finally {
            TLC.instance().pushback( tmpVal );
            TLC.instance().pushback( tmpStr );
        }

        return res;
    }

    private Class<?> getClass( final String clazzName ) throws ClassNotFoundException {

        Class<?> clazz = _classes.get( clazzName );

        if ( clazz != null ) return clazz;

        clazz = Class.forName( clazzName );

        _classes.put( clazzName, clazz );

        return clazz;
    }

    private Object getInstance( Class<?> clazz, ZString tmpSmtId, int jsonId, Boolean registerWithComponentMgr, Object parent ) throws JSONException {
        Object o = null;

        if ( clazz != null ) {
            boolean canAssignSMTID = false;

            if ( tmpSmtId != null && SMTComponent.class.isAssignableFrom( clazz ) ) {
                String smtId = tmpSmtId.toString();

                Object[]   argVals    = { smtId };
                Class<?>[] argClasses = { String.class };

                canAssignSMTID = true;

                try {
                    o = ReflectUtils.create( clazz, argClasses, argVals );
                } catch( Exception e ) {
                    if ( o == null ) {
                        throw new JSONException( "Unable to instantiate object of type " + clazz.getName() + " as SMTComponent doesnt have a constructor with just smtId for smtId=" + smtId + ", jsonId=" + jsonId );
                    }
                }
            }

            if ( o == null ) {
                try {
                    o = ReflectUtils.instantiate( clazz, parent );

                } catch( Exception e ) {
                    throw new JSONException( "Unable to instantiate object of type " + clazz.getName() + " " + e.getMessage(), e );
                }
            }

            if ( _resolver != null ) {

                if ( o instanceof Identifiable ) {
                    Identifiable i = (Identifiable) o;
                    if ( i.id() == null ) {
                        try {
                            ReflectUtils.setMember( o, "_id", tmpSmtId.toString() );
                        } catch( Exception e ) {
                            // suppress
                        }
                    }
                }

                _resolver.store( jsonId, o, (registerWithComponentMgr != null && registerWithComponentMgr == true) );
            }
        }

        return o;
    }

    private void handleCustomMemberField( Class<?> clazz, Object res, ReusableString fieldVar, ReusableString tmpVal, boolean preExisted, JSONClassDefinition.FieldEntry f, JSONClassCodec customCodec ) throws Exception {
        Object val    = null;
        int    jsonId = 0;
        if ( customCodec.useReferences() ) {
            if ( !isNullNext() ) {
                _inputTokeniser.nextToken( JSONInputTokeniser.Token.StartObject );

                int startLine = _inputTokeniser.getLineNum();
                getString( tmpVal );
                if ( tmpVal.getByte( 0 ) == JSONSpecialTags.LEADING_CHAR ) {
                    JSONSpecialTags t = JSONSpecialTags.getVal( tmpVal );

                    if ( t == JSONSpecialTags.ref ) {
                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
                        _inputTokeniser.getString( tmpVal );
                        val = _resolver.find( tmpVal );
                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

                    } else if ( t == JSONSpecialTags.jsonId ) {

                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
                        jsonId = _inputTokeniser.getInteger();
                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );
                        Class<?> postClass = CustomJSONCodecs.decodeClassName( this, _inputTokeniser, tmpVal );
                        val = decode( customCodec, tmpVal, postClass, jsonId );

                    } else {
                        throw new SMTRuntimeException( "JSON Custom " + customCodec.getClass().getSimpleName() + " on field " + f.getFieldName() + " for " + clazz.getSimpleName() +
                                                       " decoder started at line " + startLine + " expected @ref or @jsonId not " + fieldVar );
                    }

                } else {
                    throw new SMTRuntimeException( "JSON Custom " + customCodec.getClass().getSimpleName() + " on field " + f.getFieldName() + " for " + clazz.getSimpleName() +
                                                   " decoder started at line " + startLine + " expected @ref or @jsonId not " + fieldVar );
                }
            }
        } else {
            val = decode( customCodec, tmpVal, null, 0 );
        }

        Object prevVal = null;

        try {
            prevVal = f.getField().get( res );
        } catch( Exception e ) {
            _log.warn( "JSON Custom " + customCodec.getClass().getSimpleName() + " on field " + f.getFieldName() + " for " + clazz.getSimpleName() +
                       " unable to get previous value for " + fieldVar + " : " + e.getMessage() );
        }

        if ( !preExisted || prevVal == null || f.isPersistAnnotation() ) {
            setMemberLogMissingRef( res, fieldVar, val, f.getField() );
        }
    }

    /**
     * handleJSONFieldType return object represented by the JSONFieldType
     *
     * @param ft
     * @return
     * @throws Exception
     * @TODO refactor so that the top level routine returns object, but sub object routines use Field and primitive to avoid autoboxing .. use the JCD !
     */
    private Object handleJSONPrimitiveField( JSONFieldType ft ) throws Exception {
        ReusableString tmpVal = TLC.instance().pop();
        Object         val    = null;

        switch( ft ) {
        case ByteType:
        case byteType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getLong();
            val = (val == null) ? null : ((byte) val);
            break;
        }
        case CharType:
        case charType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( !tmpVal.equals( "null" ) ) {
                if ( tmpVal.length() > 1 ) throw new JSONException( "Expected char not string " + tmpVal + " at line " + _inputTokeniser.getLineNum() );

                val = tmpVal.getByte( 0 );
            }
            break;
        }
        case ShortType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getShort();
            val = (val == null) ? null : ((short) val);
            break;
        }
        case shortType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getShort();
            val = (val == null) ? Constants.UNSET_SHORT : ((short) val);
            break;
        }
        case IntType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getInteger();
            val = (val == null) ? null : ((int) val);
            break;
        }
        case intType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getInteger();
            val = (val == null) ? Constants.UNSET_INT : ((int) val);
            break;
        }
        case LongType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getLong();
            val = (val == null) ? null : ((long) val);
            break;
        }
        case longType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            val = _inputTokeniser.getLong();
            val = (val == null) ? Constants.UNSET_LONG : ((long) val);
            break;
        }
        case FloatType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( tmpVal.equals( Z_POS_INFINITY ) ) {
                val = Float.POSITIVE_INFINITY;
            } else if ( tmpVal.equals( Z_NEG_INFINITY ) ) {
                val = Float.NEGATIVE_INFINITY;
            } else if ( !tmpVal.equals( "null" ) ) {
                val = (float) _df.parse( tmpVal.toString() ).floatValue();
            }
            break;
        }
        case floatType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( tmpVal.equals( Z_POS_INFINITY ) ) {
                val = Float.POSITIVE_INFINITY;
            } else if ( tmpVal.equals( Z_NEG_INFINITY ) ) {
                val = Float.NEGATIVE_INFINITY;
            } else if ( !tmpVal.equals( "null" ) ) {
                val = (float) _df.parse( tmpVal.toString() ).floatValue();
            } else {
                val = Constants.UNSET_FLOAT;
            }
            break;
        }
        case DoubleType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( tmpVal.equals( Z_POS_INFINITY ) || tmpVal.equals( Z_INFINITY ) ) {
                val = Double.POSITIVE_INFINITY;
            } else if ( tmpVal.equals( Z_NEG_INFINITY ) ) {
                val = Double.NEGATIVE_INFINITY;
            } else if ( !tmpVal.equals( "null" ) ) {
                val = (double) _df.parse( tmpVal.toString() ).doubleValue();
            }
            break;
        }
        case doubleType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( tmpVal.equals( Z_POS_INFINITY ) ) {
                val = Double.POSITIVE_INFINITY;
            } else if ( tmpVal.equals( Z_NEG_INFINITY ) ) {
                val = Double.NEGATIVE_INFINITY;
            } else if ( !tmpVal.equals( "null" ) ) {
                val = (double) _df.parse( tmpVal.toString() ).doubleValue();
            } else {
                val = Constants.UNSET_DOUBLE;
            }
            break;
        }
        case BooleanType:
        case booleanType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            _inputTokeniser.getStringNoQuotes( tmpVal );
            if ( !tmpVal.equals( "null" ) ) {
                if ( tmpVal.equals( "true" ) ) val = true;
                else if ( tmpVal.equals( "false" ) ) val = false;
                else throw new JSONException( "Bad JSON boolean value of " + tmpVal + " at line " + _inputTokeniser.getLineNum() );
            }
            break;
        }
        case StringType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            if ( _inputTokeniser.getString( tmpVal ) ) {
                val = tmpVal.toString();
            }
            break;
        }
        case ZStringType:
        case ViewStringType:
        case ReusableStringType: {
            _inputTokeniser.nextSpecialTag( tmpVal, JSONSpecialTags.value );
            _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );
            ReusableString str = TLC.instance().getString();
            if ( _inputTokeniser.getString( str ) ) {
                val = str;
            } else {
                TLC.instance().recycle( str );
            }
            break;
        }
        default:
            // nothing
        }

        TLC.instance().pushback( tmpVal );

        return val;
    }

    private boolean isInfinity() throws Exception {
        byte nextByte = _inputTokeniser.nextByte();

        if ( nextByte == Z_INFINITY[ 0 ] ) {
            for ( int idx = 1; idx < Z_INFINITY.length; idx++ ) {
                nextByte = _inputTokeniser.nextByte();

                if ( nextByte != Z_INFINITY[ idx ] ) {
                    throw new JSONException( "expected in numeric infinity at idx=" + idx + " found " + (char) nextByte +
                                             ", expected " + (char) Z_INFINITY[ idx ] );
                }
            }

            return true;

        } else {
            _inputTokeniser.pushbackLastChar();
        }

        return false;
    }

    private Object procArray( Class<?> arrayClass ) throws Exception {

        Class<?> componentType = (arrayClass != null) ? arrayClass.getComponentType() : Object.class;

        return doProcArray( componentType );
    }

    private boolean procFalse() throws Exception {
        checkMatch( "alse" );
        return false;
    }

    private Object procNull() throws Exception {
        checkMatch( "ull" );
        return null;
    }

    private Object procObject( Class<?> clazz, Object parent, boolean isTopLevel ) throws Exception {

        Object res = null;

        JSONInputTokeniser.Token next;

        String jsonType = null;

        int arrLen    = -1;
        int jsonId    = 0;
        int jsonRefId = 0;

        ReusableString tmpStr   = TLC.instance().pop();
        ReusableString tmpVal   = TLC.instance().pop();
        ReusableString tmpSmtId = TLC.instance().pop();

        JSONClassDefinition jcd = null;

        String   smtId;
        Boolean  registerWithSMTCompMgr = Boolean.FALSE;
        boolean  preExisted             = false;
        Class<?> arrayComponentType     = null;

        int     cnt           = 0;
        boolean overrideCodec = false;
        boolean foundFields   = false;

        try {
            do {
                _inputTokeniser.getString( tmpStr );
                _inputTokeniser.nextToken( JSONInputTokeniser.Token.Colon );

                if ( tmpStr.getByte( 0 ) == JSONSpecialTags.LEADING_CHAR ) {
                    JSONSpecialTags t = JSONSpecialTags.getVal( tmpStr ); // @TODO optimise out map hit

                    switch( t ) {
                    case overrideCustomCodec:
                        overrideCodec = _inputTokeniser.getBoolean();
                        break;
                    case className: {
                        _inputTokeniser.getString( tmpVal );
                        clazz = Class.forName( tmpVal.toString() ); // @TODO optimise, only use if jsonClassId not specified and use schema lookup

                        JSONFieldType ft = JSONFieldType.getVal( clazz );

                        if ( !overrideCodec ) {
                            JSONClassCodec customCodec = _cache.getCustomCodec( ft, clazz );

                            if ( customCodec != null && (customCodec.useCodec( null, null, isTopLevel )) ) {
                                int startLine = _inputTokeniser.getLineNum();
                                try {
                                    return decode( customCodec, tmpVal, clazz, jsonId ); // custom codec will finish this object
                                } catch( Exception e ) {
                                    throw new SMTRuntimeException( "JSON Custom " + customCodec.getClass().getSimpleName() + " for " + clazz.getSimpleName() + " decoder started at line " + startLine + " : " + e.getMessage(), e );
                                }
                            }
                        }
                        jcd = _cache.getDefinition( clazz );
                        break;
                    }
                    case containerEnum: {
                        _inputTokeniser.getString( tmpVal );

                        String    val     = tmpVal.toString();
                        final int lastIdx = val.lastIndexOf( '.' );

                        if ( lastIdx <= 0 ) {
                            int startLine = _inputTokeniser.getLineNum();
                            throw new SMTRuntimeException( "JSON container enum bad format " + tmpVal + " decoder started at line " + startLine );
                        }

                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.EndObject );

                        String enumClassStr = val.substring( 0, lastIdx );
                        String enumValStr   = val.substring( lastIdx + 1 );

                        Class<Enum> enumClass = ReflectUtils.getClass( enumClassStr );

                        Enum<?> eVal = Enum.valueOf( enumClass, enumValStr );

                        return eVal;
                    }
                    case arrayType:
                        _inputTokeniser.getString( tmpVal );
                        String arrElemType = tmpVal.toString();
                        arrayComponentType = getClass( arrElemType ); // @TODO optimise, only use if jsonClassId not specified and use schema lookup
                        break;
                    case persistMode:
                        _inputTokeniser.getString( tmpVal );
                        // ignore this .. restore all fields IF not preregistered SMT component
                        break;
                    case smtComponentRegistered:
                        registerWithSMTCompMgr = _inputTokeniser.getBoolean();
                        break;
                    case smtId:
                        _inputTokeniser.getString( tmpSmtId );
                        break;
                    case jsonId:
                        jsonId = _inputTokeniser.getLong().intValue();
                        break;
                    case parentRef:
                        parent = procValue( null );
                        break;
                    case ref:
                        _inputTokeniser.getString( tmpVal );
                        res = _resolver.find( tmpVal );
                        break;
                    case jsonType:
                        _inputTokeniser.getString( tmpVal );
                        JSONFieldType ft = JSONFieldType.valueOf( tmpVal.toString() );
                        _inputTokeniser.nextToken( JSONInputTokeniser.Token.CommaSeperator );

                        if ( ft.isPrimitive() ) {
                            res = handleJSONPrimitiveField( ft );
                        }
                        break;
                    default:
                        if ( arrayComponentType != null ) {
                            _inputTokeniser.nextToken( JSONInputTokeniser.Token.StartArray );

                            res = doProcArray( arrayComponentType );
                        } else {
                            res = procValue( clazz );
                        }
                        // JUST SKIP UNUSED META VAL FOR NOW
                        break;
                    }

                } else { // @TODO extract all non meta field setting and loop round the persisted schema object instead using the json schema id ... MUCH quicker without map lookups or reflective field lookups

                    foundFields = true;

                    if ( res == null && registerWithSMTCompMgr == Boolean.TRUE ) {
                        res = _resolver.findBySMTComponentId( tmpSmtId );
                        if ( res != null ) {
                            // object already exists DONT overwite existing non persistent state
                            preExisted = true;

                            // component is already registered .. ensure jsonId is set
                            if ( jsonId != 0 && jsonRefId == 0 ) {
                                if ( _resolver != null ) _resolver.store( jsonId, res, false );
                            }
                        }
                    }

                    if ( res == null ) {
                        res = getInstance( clazz, tmpSmtId, jsonId, registerWithSMTCompMgr, parent );
                        if ( res != null && jcd == null ) {
                            jcd = _cache.getDefinition( clazz );
                        }
                    }

                    boolean fieldProcessed = false;

                    // tmpStr is field

                    JSONClassDefinition.FieldEntry f = null;

                    boolean skipSetValue = preExisted;

                    if ( jcd != null ) {
                        f = jcd.getField( tmpStr );

                        if ( f != null ) {
                            JSONClassCodec customCodec = f.getCustomCodec();
                            if ( customCodec != null ) {
                                handleCustomMemberField( clazz, res, tmpStr, tmpVal, preExisted, f, customCodec );
                                fieldProcessed = true;
                            }
                        }
                    }

                    if ( !fieldProcessed ) {
                        setMemberFieldValue( clazz, res, tmpStr, jcd, preExisted, f );
                    }
                }

                next = _inputTokeniser.nextToken();

            } while( next == JSONInputTokeniser.Token.CommaSeperator );

            if ( arrayComponentType == null ) {
                _inputTokeniser.pushbackLastChar();
                _inputTokeniser.nextToken( JSONInputTokeniser.Token.EndObject );
            }

            if ( res == null && !foundFields && jsonId != 0 && jcd != null ) {  // could be inner class with no fields
                if ( res == null && registerWithSMTCompMgr == Boolean.TRUE ) {
                    res = _resolver.findBySMTComponentId( tmpSmtId );
                }

                if ( res == null ) {
                    res = getInstance( clazz, tmpSmtId, jsonId, registerWithSMTCompMgr, parent );
                }
            }

            if ( res != null && jcd != null && clazz != null ) {
                checkPostRestoreMethod( res, jcd, clazz );
            }

            if ( jsonId != 0 && jsonRefId == 0 && res != null ) {
                if ( _resolver != null ) _resolver.store( jsonId, res, (registerWithSMTCompMgr != null) ? registerWithSMTCompMgr : false );
            }

        } finally {
            TLC.instance().pushback( tmpSmtId );
            TLC.instance().pushback( tmpVal );
            TLC.instance().pushback( tmpStr );
        }

        return res;
    }

    private boolean procTrue() throws Exception {
        checkMatch( "rue" );
        return true;
    }

    private void setMemberFieldValue( Class<?> clazz, Object res, ReusableString tmpStr, JSONClassDefinition jcd, boolean preExisted, JSONClassDefinition.FieldEntry f ) throws Exception {
        Object val = null;

        Field field = null;
        try {
            val = procValue( (f != null) ? f.getFieldClass() : clazz, res );

            if ( f != null ) {
                field = f.getField();
            } else {
                String fldName = tmpStr.toString();
                int    lastIdx = fldName.lastIndexOf( JSONClassDefinition.SHADOW_DELIM );

                if ( lastIdx > 0 && lastIdx < (fldName.length() - 1) ) {
                    String className = fldName.substring( 0, lastIdx );
                    fldName = fldName.substring( lastIdx + 1 );

                    clazz = Class.forName( className );

                    field = ReflectUtils.getMember( clazz, fldName );

                } else {
                    field = ReflectUtils.getMember( clazz, fldName );
                }
            }

        } catch( ClassNotFoundException e ) {

            throw e;

        } catch( Exception e ) {

            String objStr = null;

            try { objStr = res.toString(); } catch( Exception ex ) { /* ignore */ }

            String clazzName = (clazz == null) ? "null" : clazz.getName();

            if ( val == null ) {

                _log.warn( "Setting field " + tmpStr.toString() + ", couldnt get val in " + objStr + " : " + e.getMessage() + ", clazz=" + clazzName );

            } else {
                _log.warn( "Setting field " + tmpStr.toString() + " to val " + val + " in " + objStr + " : " + e.getMessage() + ", clazz=" + clazzName );
            }
        }

        if ( field != null ) {
            boolean hadAccess = field.isAccessible();
            if ( !hadAccess ) field.setAccessible( true );
            try {
                if ( f == null ) {
                    return;
                }

                Object prevVal = null;

                try {
                    prevVal = f.getField().get( res );
                } catch( Exception e ) {
                    _log.warn( "Setting field " + tmpStr.toString() + ", couldnt get prev val " + f.getFieldName() + " : " + e.getMessage() );
                }

                if ( jcd != null && (f = jcd.getField( tmpStr )) != null ) {
                    if ( !preExisted || prevVal == null || f.isPersistAnnotation() ) {
                        if ( val instanceof String ) {
                            ReflectUtils.setMemberFromString( res, field, (String) val );
                        } else {
                            setMemberLogMissingRef( res, tmpStr, val, field );
                        }
                    }
                } else if ( !preExisted || prevVal == null ) { // no JCD field available
                    if ( val instanceof String ) {
                        ReflectUtils.setMemberFromString( res, field, (String) val );
                    } else {
                        setMemberLogMissingRef( res, tmpStr, val, null );
                    }
                }
            } finally {
                if ( !hadAccess ) field.setAccessible( false );
            }
        }
    }

    private void setMemberLogMissingRef( Object res, ReusableString tmpStr, Object val, Field field ) {
        if ( val instanceof MissingRef ) {
            MissingRef ref = (MissingRef) val;

            ref.setSrcObject( res );
            ref.setRefFieldName( tmpStr.toString() );

            _resolver.addMissingRef( ref );
        } else {
            if ( field != null ) {

                ReflectUtils.setMember( res, field, val );

            } else {
                String fieldName = tmpStr.toString();

                int lastIdx = fieldName.lastIndexOf( JSONClassDefinition.SHADOW_DELIM );

                if ( lastIdx > 0 && lastIdx < (fieldName.length() - 1) ) {
                    String className = fieldName.substring( 0, lastIdx );
                    fieldName = fieldName.substring( lastIdx + 1 );

                    try {
                        Class<?> clazz = Class.forName( className );

                        field = ReflectUtils.getMember( clazz, fieldName );

                        ReflectUtils.setMember( res, field, val );

                    } catch( ClassNotFoundException e ) {
                        ReflectUtils.setMember( res, fieldName, val );
                    }

                } else {
                    ReflectUtils.setMember( res, fieldName, val );
                }
            }
        }
    }

    private byte writeAndGetNextByte( ReusableString val, byte nextByte ) throws IOException {
        val.append( nextByte );
        nextByte = _inputTokeniser.nextByte();
        return nextByte;
    }
}
