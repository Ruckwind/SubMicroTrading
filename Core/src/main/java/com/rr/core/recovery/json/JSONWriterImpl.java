package com.rr.core.recovery.json;

import com.rr.core.component.*;
import com.rr.core.lang.ProcedureWithException;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Identifiable;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import static com.rr.core.lang.Constants.*;
import static com.rr.core.recovery.json.JSONCommon.*;

/**
 * Non threadsafe JSONWriter
 * <p>
 * first time a class is used its current definition will be written to the output stream
 *
 * @NOTE make sure you enable access with
 * <p>
 * JSONClassDefinition.resetPermissions( true );
 * <p>
 * before you use the writer then dont forget to resetPermissions after with force set to false
 */
public class JSONWriterImpl implements JSONWriter {

    private static Logger _log = LoggerFactory.create( JSONWriterImpl.class );

    private static String ONE_INDENT_SPACES = "\t";
    private static int    PRIMITIVE_FIELD   = -1;

    private static int MAX_DEPTH = AppProps.instance().getIntProperty( "MAX_PERSIST_LEVELS", false, 1000 );

    private static boolean _allowEncodeTimestampAsString;

    private JSONWriteSharedState     _writeState;
    private ReusableString           _tmpStr      = new ReusableString();
    private OutputStream             _outStream;
    private int                      _depth;
    private ReusableString           _depthSpaces = new ReusableString();
    private JSONClassDefinitionCache _cache;
    private SMTComponentManager      _componentManager;
    private DecimalFormat            _df          = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );

    private WriteContext _wctx = WriteContext.Snapshot;

    private boolean    _excludeNullFields;
    private boolean    _verboseSpacing;
    private String     _newLine = "\n";
    private Set<Class> _filterClasses;

    public static boolean isAllowEncodeTimestampAsString()                                            { return _allowEncodeTimestampAsString; }

    public static void setAllowEncodeTimestampAsString( final boolean _allowEncodeTimestampAsString ) { JSONWriterImpl._allowEncodeTimestampAsString = _allowEncodeTimestampAsString; }

    public JSONWriterImpl( OutputStream outStream, JSONClassDefinitionCache cache, SMTComponentManager mgr ) {
        this( outStream, cache, mgr, false );
    }

    public JSONWriterImpl( OutputStream outStream, JSONClassDefinitionCache cache, SMTComponentManager mgr, boolean verboseSpacing ) {
        this( outStream, cache, mgr, verboseSpacing, new JSONWriteSharedStateWithRefs() );
    }

    public JSONWriterImpl( OutputStream outStream, JSONClassDefinitionCache cache, SMTComponentManager mgr, boolean verboseSpacing, JSONWriteSharedState writeState ) {
        _outStream      = outStream;
        _writeState     = writeState;
        _cache          = cache;
        _verboseSpacing = verboseSpacing;
        _df.setMaximumFractionDigits( MAX_DP_DIGITS );
        _df.setRoundingMode( RoundingMode.HALF_EVEN );
        _componentManager = mgr;
    }

    public PersistMode getMode( Object obj ) {

        if ( obj instanceof SMTSnapshotMemberAllFields ) {
            return PersistMode.AllFields;
        } else if ( obj instanceof SMTSnapshotMemberOnlyPersistFields ) {
            CreationPhase t = _componentManager.getComponentCreationPhase( (SMTComponent) obj );

            if ( t != CreationPhase.Config ) {
                String name = (t == null) ? "<null>" : t.name();

                _log.info( "JSONWriterImpl.getMode() override " + ((SMTSnapshotMemberOnlyPersistFields) obj).id() +
                           " change from SMTSnapshotMemberOnlyPersistFields to AllFields as created at " + name );

                return PersistMode.AllFields;
            }

            return PersistMode.OnlyFieldsWithPersistAnnotation;
        }

        return PersistMode.AllFields;
    }

    public void setFilterClasses( final Set<Class> filterClasses ) {
        _filterClasses = filterClasses;
    }    @Override public void setOutStream( final OutputStream outStream ) { _outStream = outStream; }

    private void doHandleNonPrimArray( final JSONClassDefinition.FieldEntry f, final Object fieldObj ) throws Exception {
        Object[] arrObj = (Object[]) fieldObj;
        int      arrLen = arrObj.length;
        int      id     = _writeState.getObjId( fieldObj, true );
        startArray( arrLen, true, fieldObj.getClass().getComponentType(), id );
        for ( int i = 0; i < arrLen; i++ ) {
            if ( i > 0 ) {
                write( COMMA_DELIM );
                nextLine();
            }
            Object val = arrObj[ i ];
            if ( val == null ) {
                write( JSON_NULL );
            } else {
                handleSubObject( f, val.getClass(), f.getFieldType(), val );
            }
        }
        endArray( true, id );
    }    @Override public void resetState()                                 { _writeState.reset(); }

    private void doHandleObject( Object obj, JSONClassDefinition jcd, Integer id, PersistMode mode, boolean overrideCustomCodec ) throws Exception {
        startObject( obj, id, true, mode, overrideCustomCodec, jcd );

        // dont write delim here .. if any fields it will be added in following loop

        JSONClassDefinition.FieldEntry[] fields = jcd.getEntries();

        int numFields = fields.length;

        int                            idx = 0;
        JSONClassDefinition.FieldEntry f   = null;

        try {
            for ( ; idx < numFields; ++idx ) {
                f = fields[ idx ];

                writeObjectField( obj, f, mode, true ); // child objects cannot be just changed fields
            }
        } catch( Exception e ) {
            if ( obj != null ) {

                String oid = (obj instanceof Identifiable) ? ((Identifiable) obj).id() : "<unknown>";

                String err = "Exception encoding class=" + obj.getClass().getSimpleName() + ", id=" + oid;

                if ( f != null ) {
                    err = err + ", field=" + f.getField().getName();
                }

                err = err + " " + e.getMessage();

                throw new JSONException( err, e );
            }

            throw e;
        }

        endObject();
    }    @Override public void setWriteContext( final WriteContext wctx )   { _wctx = wctx; }

    private void doHandleObjectCustomCodec( Object obj, Integer id, JSONClassCodec customCodec ) throws Exception {
        if ( customCodec.useReferences() ) {

            if ( id == 0 ) id = _writeState.getObjId( obj, false );

            final int fid = id;

            writeWithRefReplace( obj, fid, () -> invokeCustomEncodeObj( obj, fid, customCodec ) );

        } else {
            customCodec.encode( this, obj, 0 );
        }
    }    @Override public WriteContext getWriteContext()                    { return _wctx; }

    /**
     * @param obj
     * @param mode
     * @param isTopLevel
     * @throws Exception
     */
    private void doObjectToJson( Object obj, PersistMode mode ) throws Exception {

        if ( obj == null ) {
            write( '{' );
            write( JSON_NULL );
            write( '}' );
            return;
        }

        setSpaces();

        childObjectToJson( obj, null, true, mode, true, false );

        setSpaces();

        nextLine();
    }    /**
     * @param obj
     * @throws Exception (IOException, IllegalAccessException)
     */
    @Override public void objectToJson( Object obj ) throws Exception {
        _depthSpaces.reset();
        _depth = 0;

        doObjectToJson( obj, getMode( obj ) );
    }

    private void encodeTimestamp( JSONFieldType f, Object obj ) throws Exception {

        if ( obj == null ) {
            write( JSON_NULL );
            return;
        }

        _tmpStr.reset();

        long val = (long) obj;

        if ( Utils.isNull( val ) ) {
            write( _tmpStr.append( JSON_NULL ) );
        } else {
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( _tmpStr, val );

            write( _tmpStr );
        }
    }    @Override public void objectsToJson( Collection<SMTSnapshotMember> components ) throws Exception {

        _depthSpaces.reset();
        _depth = 0;

        if ( startObject( components, null, false, PersistMode.AllFields, null ) ) {
            writeEndLineDelim();
        } else {
            writeSpaces();
        }

        write( getWorkStr().copy( "\"" ).append( JSONSpecialTags.className.getVal() ).append( "\" : \"" ).append( components.getClass().getName() ).append( '"' ) );
        writeEndLineDelim();
        write( getWorkStr().copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );

        startArray( components.size(), false, null, 0 );

        boolean first = true;
        for ( Object v : components ) {
            if ( first ) {
                first = false;
            } else {
                writeEndLineDelim();
                writeSpaces();
            }

            if ( v instanceof SMTComponent ) _log.info( " Snapshotting " + ((SMTComponent) v).getComponentId() );

            doObjectToJson( v, getMode( v ) ); // treat each component as a top level object
        }

        endArray( false, 0 );

        endObject();
    }

    private boolean filter( final Object obj ) {
        boolean filter = false;
        if ( _filterClasses != null ) {
            filter = _filterClasses.contains( obj.getClass() );
        }
        return filter;
    }    @Override public void objectToJson( Object obj, PersistMode mode ) throws Exception {
        _depthSpaces.reset();
        _depth = 0;

        doObjectToJson( obj, mode );
    }

    private void handleArray( Object obj, JSONClassDefinition jcd, boolean showType, PersistMode mode, boolean writeArrType ) throws Exception {
        int arrLen = Array.getLength( obj );

        if ( arrLen > 0 ) {

            int id = _writeState.getObjId( obj, true );

            startArray( arrLen, writeArrType, obj.getClass().getComponentType(), id );

            for ( int i = 0; i < arrLen; i++ ) {
                if ( i > 0 ) {
                    write( COMMA_DELIM );
                    writeSpaces();
                }

                Object arrayEntry = Array.get( obj, i );

                if ( arrayEntry == null ) {
                    write( JSON_NULL );
                } else {
                    boolean showChildType = (arrayEntry.getClass() != obj.getClass().getComponentType()) || showType;

                    JSONClassDefinition componentJCD = (jcd != null) ? jcd.getComponentJCD() : null;

                    childObjectToJson( arrayEntry, componentJCD, showChildType, mode, writeArrType, false );
                }
            }

            if ( _verboseSpacing ) nextLine();

            endArray( writeArrType, id );

        } else {
            write( "[]" );
        }
    }

    private void handleNonPrimObject( JSONClassDefinition.FieldEntry f, Object fieldObj ) throws Exception {
        if ( fieldObj.getClass().isArray() ) {
            writeWithRefReplace( fieldObj, 0, () -> doHandleNonPrimArray( f, fieldObj ) );
        } else {
            handleSimpleInstance( f, f.getObjectDef(), fieldObj );
        }
    }

    private void handleObject( Object obj, JSONClassDefinition jcd, Integer id, PersistMode mode, boolean overrideCustomCodec ) throws Exception {

        // check top level object doesnt have a custom codec

        if ( ReflectUtils.isLambda( obj.getClass() ) ) {
            if ( ReflectUtils.isSerializableLambda( obj.getClass() ) ) {
                Object serLambda = ReflectUtils.serializeLambda( obj );

                obj = serLambda;
            } else {
                throw new JSONException( "JSONWriter lambda is NOT serialisable ... use functional interface that extends SerializableLambda : " + obj.getClass().getName() );
            }
        }

        if ( jcd == null || jcd.getTargetClass() != obj.getClass() ) {
            jcd = _cache.getDefinition( obj.getClass() );
        }

        JSONClassCodec customCodec = jcd.getTopLevelCustomCodec();
        if ( customCodec != null ) {

            if ( customCodec.useCodec( obj, _wctx, _depth == 0 ) ) {
                doHandleObjectCustomCodec( obj, id, customCodec );

                return;
            }

            overrideCustomCodec = true;
        }

        final Object              fObj = obj;
        final JSONClassDefinition fJcd = jcd;

        final boolean doOverride = overrideCustomCodec;

        writeWithRefReplace( obj, id, () -> doHandleObject( fObj, fJcd, id, mode, doOverride ) );
    }    @Override public JSONClassDefinitionCache getCache() {
        return _cache;
    }

    private void handlePrimitiveArray( Object obj, JSONFieldType type, boolean showType ) throws Exception {

        int arrLen = Array.getLength( obj );

        int id = _writeState.getObjId( obj, true );

        startArray( arrLen, showType, obj.getClass().getComponentType(), id );

        for ( int i = 0; i < arrLen; i++ ) {
            if ( i > 0 ) {
                write( COMMA_DELIM );
                nextLine();
            }

            // @TODO put back the optimised per primitive type array handling ... perf test diff

            Object arrayEntry = Array.get( obj, i );

            if ( arrayEntry == null ) {
                write( JSON_NULL );
            } else if ( arrayEntry.getClass().isArray() ) {
                handlePrimitiveArray( arrayEntry, type, false );
            } else {
                handlePrimitive( type, arrayEntry, false );
            }
        }

        endArray( showType, id );
    }    @Override public void childObjectToJson( Object obj, JSONClassDefinition jcd, boolean showType, PersistMode mode, boolean writeArrType, boolean overrideCustomCodec ) throws Exception {

        if ( _depth > MAX_DEPTH ) throw new Exception( "JSONWriter max depth exceeded " + MAX_DEPTH );

        if ( obj == null || filter( obj ) ) {
            write( JSON_NULL );
            return;
        }

        Class<?>      clazz     = obj.getClass();
        JSONFieldType fieldType = JSONFieldType.getVal( clazz );

        if ( clazz.isArray() ) {
            JSONFieldType componentFieldType = JSONFieldType.getVal( clazz.getComponentType() );

            if ( componentFieldType.isPrimitive() ) {

                writeWithRefReplace( obj, 0, () -> handlePrimitiveArray( obj, componentFieldType, showType ) );

                return;
            }
        } else if ( fieldType.isPrimitive() ) {
            handlePrimitive( fieldType, obj, showType );
            return;
        }

        if ( clazz.isArray() ) {

            writeWithRefReplace( obj, 0, () -> handleArray( obj, jcd, showType, mode, writeArrType ) );

        } else {

            if ( obj instanceof JSONExclude ) {
                throw new JSONException( "JSONWriterImpl ERROR must not link to an object which has JSONExclude tag interface : classInQuestion=" + obj.getClass().getName() );
            }

            int id = _writeState.getObjId( obj, false );

            if ( _log.isEnabledFor( Level.trace ) ) {
                if ( obj instanceof SMTComponent ) {
                    String smtIdStr = ", smtId=" + ((SMTComponent) obj).getComponentId();

                    _log.log( Level.trace, "JSONWriterImpl writing objectId=" + id + ", objClass=" + obj.getClass().getName() + " " + smtIdStr );
                }
            }

            handleObject( obj, jcd, id, mode, overrideCustomCodec );
        }
    }

    private void handleSimpleInstance( JSONClassDefinition.FieldEntry f, JSONClassDefinition objDef, Object fieldObj ) throws Exception {

        if ( fieldObj != null ) {
            if ( f.isForceSMTRef() ) {
                SMTComponent sc = (SMTComponent) fieldObj;

                write( _tmpStr.copy( "{\"" ).append( JSONSpecialTags.ref.getVal() ).append( "\" : \"" ).append( ((SMTComponent) fieldObj).getComponentId() ).append( "\"}" ) ); // output id as reference

            } else {
                JSONClassCodec customCodec = f.getCustomCodec();

                boolean overrideCustomCodec = false;

                if ( customCodec != null ) {

                    if ( customCodec.useCodec( fieldObj, _wctx, _depth == 0 ) ) {

                        doHandleObjectCustomCodec( fieldObj, 0, customCodec );

                        return;
                    }

                    overrideCustomCodec = true;
                }

                childObjectToJson( fieldObj, objDef, true, PersistMode.AllFields, false, overrideCustomCodec ); // RECURSE
            }

        } else {
            write( JSON_NULL );
        }
    }

    private void handleSubObject( JSONClassDefinition.FieldEntry f, Class<?> clazz, JSONFieldType fieldType, Object fieldObj ) throws Exception {
        if ( fieldObj == null ) {
            write( JSON_NULL );
        } else {
            if ( clazz.isArray() ) {
                JSONFieldType componentFieldType = JSONFieldType.getVal( clazz.getComponentType() );

                if ( componentFieldType.isPrimitive() ) {

                    writeWithRefReplace( fieldObj, 0, () -> handlePrimitiveArray( fieldObj, componentFieldType, false ) );

                    return;
                }
            } else if ( fieldType.isPrimitive() ) {
                if ( f.encodeAsStringTimestamp() && _allowEncodeTimestampAsString ) {
                    encodeTimestamp( fieldType, fieldObj );
                } else {
                    handlePrimitive( fieldType, fieldObj, false );
                }
                return;
            }

            handleNonPrimObject( f, fieldObj );
        }
    }

    private void invokeCustomEncodeObj( final Object obj, final Integer id, final JSONClassCodec customCodec ) throws Exception {
        String smtIdStr = ((obj instanceof SMTComponent) ? (", smtId=" + ((SMTComponent) obj).getComponentId()) : "");

        if ( _log.isEnabledFor( Level.xtrace ) ) {
            _log.log( Level.xtrace, "JSONWriterImpl written objectId=" + id + ", objClass=" + obj.getClass().getName() + smtIdStr );
        }

        customCodec.encode( this, obj, id );
    }    @Override public void writeSpaces() throws IOException {
        if ( _verboseSpacing ) write( _depthSpaces );
    }

    private boolean isNullOrEmpty( final Object obj, final JSONClassDefinition.FieldEntry f ) {
        if ( obj == null ) return true;

        if ( f.isArray() ) return false;

        final JSONFieldType fieldType = f.getFieldType();

        if ( fieldType.isPrimitive() ) {
            switch( fieldType ) {
            case byteType:
            case ByteType: {
                byte val = (byte) obj;
                return Utils.isNull( val );
            }
            case CharType:
            case charType: {
                char val = (char) obj;
                return Utils.isNull( val );
            }
            case ShortType:
            case shortType: {
                short val = (short) obj;
                return Utils.isNull( val );
            }
            case IntType:
            case intType: {
                int val = (int) obj;
                return Utils.isNull( val );
            }
            case LongType:
            case longType: {
                long val = (long) obj;
                return Utils.isNull( val );
            }
            case FloatType:
            case floatType: {
                float val = (float) obj;
                return Utils.isNull( val );
            }
            case DoubleType:
            case doubleType: {
                double val = (double) obj;
                return Utils.isNull( val );
            }
            case BooleanType:
            case booleanType: {
                return false;
            }
            case StringType: {
                return ((String) obj).length() == 0;
            }
            case ZStringType:
            case ViewStringType:
            case ReusableStringType: {
                return ((ZString) obj).length() == 0;
            }
            case Enum:
            case Map:
            case Collection:
            case MessageQueue:
            case Object:
            default:
                return false;
            }
        }

        return false;
    }    @Override public void nextLine() throws IOException {
        write( _newLine );
        writeSpaces();
    }

    private void setSpaces() {
        if ( _verboseSpacing ) {
            _depthSpaces.reset();
            for ( int i = 0; i < _depth; i++ ) {
                _depthSpaces.append( ONE_INDENT_SPACES );
            }
        }
    }    @Override public void handlePrimitive( JSONFieldType f, Object obj, boolean showType ) throws Exception {

        if ( obj == null ) {
            write( JSON_NULL );
            return;
        }

        if ( showType ) {
            _tmpStr.copy( "{ \"" ).append( JSONSpecialTags.jsonType.getVal() ).append( "\" : \"" ).append( f.toString() ).append( "\", \"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " );
        } else {
            _tmpStr.reset();
        }

        switch( f ) {
        case byteType:
        case ByteType: {
            byte val = (byte) obj;
            write( _tmpStr.append( (int) val ) );
            break;
        }
        case CharType:
        case charType: {
            char val = (char) obj;
            write( _tmpStr.append( val ) );
            break;
        }
        case ShortType:
        case shortType: {
            short val = (short) obj;
            if ( Utils.isNull( val ) ) {
                write( _tmpStr.append( JSON_NULL ) );
            } else {
                write( _tmpStr.append( val ) );
            }
            break;
        }
        case IntType:
        case intType: {
            int val = (int) obj;
            if ( Utils.isNull( val ) ) {
                write( _tmpStr.append( JSON_NULL ) );
            } else {
                write( _tmpStr.append( val ) );
            }
            break;
        }
        case LongType:
        case longType: {
            long val = (long) obj;
            if ( Utils.isNull( val ) ) {
                write( _tmpStr.append( JSON_NULL ) );
            } else {
                write( _tmpStr.append( val ) );
            }
            break;
        }
        case FloatType:
        case floatType: {
            float val = (float) obj;
            if ( Utils.isNull( val ) ) {
                write( _tmpStr.append( JSON_NULL ) );
            } else if ( val == Double.POSITIVE_INFINITY ) {
                write( _tmpStr.append( Z_INFINITY ) );
            } else if ( val == Double.NEGATIVE_INFINITY ) {
                write( _tmpStr.append( Z_NEG_INFINITY ) );
            } else {
                write( _tmpStr.append( _df.format( val ) ) );
            }
            break;
        }
        case DoubleType:
        case doubleType: {
            double val = (double) obj;
            if ( Utils.isNull( val ) ) {
                write( _tmpStr.append( JSON_NULL ) );
            } else if ( val == Double.POSITIVE_INFINITY ) {
                write( _tmpStr.append( Z_INFINITY ) );
            } else if ( val == Double.NEGATIVE_INFINITY ) {
                write( _tmpStr.append( Z_NEG_INFINITY ) );
            } else {
                write( _tmpStr.append( _df.format( val ) ) );
            }
            break;
        }
        case BooleanType:
        case booleanType: {
            boolean val = (boolean) obj;
            write( _tmpStr.append( val ? "true" : "false" ) );
            break;
        }
        case StringType: {
            _tmpStr.append( '"' );
            String s   = (String) obj;
            int    len = s.length();
            for ( int idx = 0; idx < len; ++idx ) {
                byte b = (byte) s.charAt( idx );

                if ( b == '"' ) {
                    b = '\'';
                }

                _tmpStr.append( b );
            }
            _tmpStr.append( '"' );
            write( _tmpStr );
            break;
        }
        case ZStringType:
        case ViewStringType:
        case ReusableStringType: {
            _tmpStr.append( '"' );
            ZString s   = (ZString) obj;
            int     len = s.length();
            for ( int idx = 0; idx < len; ++idx ) {
                byte b = (byte) s.getByte( idx );

                if ( b == '"' ) {
                    b = '\'';
                }

                _tmpStr.append( b );
            }
            _tmpStr.append( '"' );
            write( _tmpStr );
            break;
        }
        default:
        }

        if ( showType ) {
            write( " }" );
        }
    }

    private void writeObjectField( Object obj, JSONClassDefinition.FieldEntry f, PersistMode mode, boolean writeDelim ) throws Exception {
        if ( mode == null || mode == PersistMode.AllFields || f.isPersistAnnotation() ) {
            Object fieldObj = f.getField().get( obj );

            if ( _excludeNullFields && isNullOrEmpty( fieldObj, f ) || filter( fieldObj ) ) {

                // EXCLUDED

            } else {
                if ( writeDelim ) {
                    writeEndLineDelim();
                } else {
                    writeSpaces();
                }

                String fName = f.getFieldName();

                write( _tmpStr.copy( "\"" ).append( fName ).append( "\" : " ) );

                Class<?>      clazz     = f.getFieldClass();
                JSONFieldType fieldType = f.getFieldType();

                handleSubObject( f, clazz, fieldType, fieldObj );
            }
        }
    }

    private void writeWithRefReplace( Object obj, Integer id, ProcedureWithException writerFunc ) throws Exception {

        if ( _writeState.forceReference( obj ) && _depth > 0 ) {

            SMTComponent sc = (SMTComponent) obj;

            write( _tmpStr.copy( "{\"" ).append( JSONSpecialTags.ref.getVal() ).append( "\" : \"" ).append( ((SMTComponent) obj).getComponentId() ).append( "\"}" ) ); // output id as reference

        } else if ( _writeState.prepWrite( obj ) ) {

            writerFunc.invoke();

        } else {
            if ( id == 0 ) id = _writeState.getObjId( obj, false );

            write( _tmpStr.copy( "{\"" ).append( JSONSpecialTags.ref.getVal() ).append( "\" : \"" ).append( id ).append( "\"}" ) ); // output id as reference
        }
    }    @Override public void writeVal( int val ) throws IOException {
        if ( Utils.isNull( val ) ) {
            write( JSON_NULL );
        } else {
            // not using the ReusableString.append( double ) as that is restricted to 6DP
            write( _tmpStr.copy( val ) );
        }
    }

    @Override public void writeVal( double val ) throws IOException {
        _tmpStr.reset();
        if ( Utils.isNull( val ) ) {
            write( JSON_NULL );
        } else if ( val == Double.POSITIVE_INFINITY ) {
            write( _tmpStr.append( Z_INFINITY ) );
        } else if ( val == Double.NEGATIVE_INFINITY ) {
            write( _tmpStr.append( Z_NEG_INFINITY ) );
        } else {
            write( _tmpStr.append( _df.format( val ) ) );
            // not using the ReusableString.append( double ) as that is restricted to 6DP
        }
    }

    @Override public void writeVal( long val ) throws IOException {
        if ( Utils.isNull( val ) ) {
            write( JSON_NULL );
        } else {
            // not using the ReusableString.append( double ) as that is restricted to 6DP
            write( _tmpStr.copy( val ) );
        }
    }

    @Override public void writeVal( boolean val ) throws IOException {
        write( _tmpStr.copy( val ? "true" : "false" ) );
    }

    @Override public void write( String val ) throws IOException {
        if ( val == null ) {
            write( JSON_NULL );
        } else {
            _outStream.write( val.getBytes(), 0, val.length() );
        }
    }

    @Override public void write( ZString c ) throws IOException {
        _outStream.write( c.getBytes(), c.getOffset(), c.length() );
    }

    @Override public void write( char c ) throws IOException {
        _outStream.write( (int) c );
    }

    @Override public void startArray( int arrLen, boolean showType, Class<?> componentClass, int objectId ) throws IOException {

        if ( objectId > 0 || showType ) {
            write( "{" );
            write( _newLine );
            ++_depth;
            setSpaces();

            boolean writeSpaces = true;

            if ( objectId > 0 ) {
                writeSpaces();
                write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.jsonId.getVal() ).append( "\" : " ).append( objectId ) );
                writeEndLineDelim();
                writeSpaces = false;
            }

            if ( showType ) {
                if ( writeSpaces ) writeSpaces();
                write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.arrayType.getVal() ).append( "\" : \"" ).append( componentClass.getName() ).append( '"' ) );
                writeEndLineDelim();
                writeSpaces = false;
            }

            if ( writeSpaces ) writeSpaces();

            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.value.getVal() ).append( "\" : " ) );
            nextLine();
        }

        write( "[" );

        if ( arrLen > 0 ) {
            write( _newLine );
        }

        ++_depth;
        setSpaces();

        if ( arrLen > 0 ) {
            writeSpaces();
        }
    }

    @Override public void endArray( boolean showType, int objectId ) throws IOException {
        --_depth;
        setSpaces();
        nextLine();
        write( ']' );

        if ( objectId > 0 || showType ) {
            endObject();
        }
    }

    @Override public ReusableString getWorkStr() {
        return _tmpStr;
    }

    @Override public void setEncodeNewLineChar( final String newLine )            { _newLine = newLine; }

    @Override public void setExcludeNullFields( final boolean excludeNullFields ) { _excludeNullFields = excludeNullFields; }

    @Override public void setVerboseSpacing( final boolean verboseSpacing )       { _verboseSpacing = verboseSpacing; }

    @Override public void enableCompress() {
        setExcludeNullFields( true );
        setEncodeNewLineChar( " " );
        setVerboseSpacing( false );
    }

    @Override public void rawObjectWrite( Object obj, JSONClassDefinition jcd ) throws Exception {
        startObject( obj, null, false, PersistMode.AllFields, jcd );

        // dont write delim here .. if any fields it will be added in following loop

        JSONClassDefinition.FieldEntry[] fields = jcd.getEntries();

        int numFields = fields.length;

        int                            idx = 0;
        JSONClassDefinition.FieldEntry f   = null;

        try {
            for ( ; idx < numFields; ++idx ) {
                f = fields[ idx ];

                writeObjectField( obj, f, PersistMode.AllFields, idx > 0 ); // child objects cannot be just changed fields
            }
        } catch( Exception e ) {
            if ( obj != null ) {

                String oid = (obj instanceof Identifiable) ? ((Identifiable) obj).id() : "<unknown>";

                String err = "Exception encoding class=" + obj.getClass().getSimpleName() + ", id=" + oid;

                if ( f != null ) {
                    err = err + ", field=" + f.getField().getName();
                }

                err = err + e.getMessage();

                throw new JSONException( err, e );
            }

            throw e;
        }

        endObject();
    }



























    @Override public boolean startObject( Object obj, Integer objectId, boolean writeType, PersistMode mode, JSONClassDefinition jcd ) throws IOException {
        return startObject( obj, objectId, writeType, mode, false, jcd );
    }

    @Override public boolean startObject( Object obj, Integer objectId, boolean writeType, PersistMode mode, boolean overrideCustomCodec, JSONClassDefinition jcd ) throws IOException {

        boolean anyFieldsWitten = false;

        write( "{" );
        write( _newLine );
        ++_depth;
        setSpaces();
        if ( objectId != null && objectId > 0 ) {
            writeSpaces();
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.jsonId.getVal() ).append( "\" : " ).append( objectId ) );
            anyFieldsWitten = true;
        }
        if ( overrideCustomCodec ) {
            if ( anyFieldsWitten ) {
                writeEndLineDelim();
            } else {
                writeSpaces();
            }
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.overrideCustomCodec.getVal() ).append( "\" : true" ) );
            anyFieldsWitten = true;
        }
        if ( writeType && obj != null ) {
            if ( anyFieldsWitten ) {
                writeEndLineDelim();
            } else {
                writeSpaces();
            }
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.className.getVal() ).append( "\" : \"" ).append( obj.getClass().getName() ).append( '"' ) );
            anyFieldsWitten = true;
        }
        if ( obj instanceof SMTComponent ) {
            if ( anyFieldsWitten ) {
                writeEndLineDelim();
            } else {
                writeSpaces();
            }
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.smtId.getVal() ).append( "\" : \"" ).append( ((SMTComponent) obj).getComponentId() ).append( '"' ) );
            anyFieldsWitten = true;
        }
        if ( jcd != null && jcd.isInnerClass() ) {
            if ( anyFieldsWitten ) {
                writeEndLineDelim();
            } else {
                writeSpaces();
            }
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.parentRef.getVal() ).append( "\" : " ) );

            final JSONClassDefinition.FieldEntry parentField = jcd.getInnerClassParent();

            Object parent = ReflectUtils.get( parentField.getField(), obj );

            try {
                childObjectToJson( parent, null, true, PersistMode.AllFields, false, false );
            } catch( Exception e ) {
                throw new SMTRuntimeException( "Unable to get parent object for instantiation of inner class : " + e.getMessage(), e );
            }

            anyFieldsWitten = true;
        }
        if ( mode == PersistMode.OnlyFieldsWithPersistAnnotation ) {
            if ( anyFieldsWitten ) {
                writeEndLineDelim();
            } else {
                writeSpaces();
            }
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.persistMode.getVal() ).append( "\" : \"" ).append( mode.toString() ).append( '"' ) );
            anyFieldsWitten = true;
        }
        if ( obj instanceof SMTComponent ) {
            String id      = ((SMTComponent) obj).getComponentId();
            Object compReg = _componentManager.getComponentOrNull( id );
            if ( compReg == obj ) {
                if ( anyFieldsWitten ) {
                    writeEndLineDelim();
                } else {
                    writeSpaces();
                }
                write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.smtComponentRegistered.getVal() ).append( "\" : true" ) );
                anyFieldsWitten = true;
            }
        }

        return anyFieldsWitten;
    }

    @Override public void startCustomObject( String smtId, Class<?> objClass ) throws IOException {

        boolean anyFieldsWitten = false;

        write( "{" );
        write( _newLine );
        ++_depth;
        setSpaces();
        writeSpaces();
        write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.className.getVal() ).append( "\" : \"" ).append( objClass.getName() ).append( '"' ) );
        if ( smtId != null ) {
            writeEndLineDelim();
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.smtId.getVal() ).append( "\" : \"" ).append( smtId ).append( '"' ) );
            writeEndLineDelim();
            write( _tmpStr.copy( "\"" ).append( JSONSpecialTags.smtComponentRegistered.getVal() ).append( "\" : true" ) );
        }
    }

    @Override public void writeEndLineDelim() throws IOException {
        write( COMMA_DELIM );
        write( _newLine );
        writeSpaces();
    }

    @Override public void endObject() throws IOException {
        --_depth;
        setSpaces();
        nextLine();
        write( "}" );
    }
}
