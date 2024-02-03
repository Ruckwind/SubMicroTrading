package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.component.SMTCoreContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.utils.ReflectUtils;

public class JSONUtils {

    private static ReusableString             _strBuf    = new ReusableString( 1024 );
    private static ReusableStringOutputStream _outStream = new ReusableStringOutputStream( _strBuf );
    private static volatile boolean _inCreate;    private static JSONWriterImpl             _writer    = createDebugWriter();

    private static synchronized JSONWriterImpl createDebugWriter() {
        if ( _writer == null ) {
            SMTComponentManager componentManager = new SMTComponentManager();
            SMTCoreContext      ctx              = new SMTCoreContext( "ctx", componentManager );
            ctx.setComponentManager( componentManager );
            JSONClassDefinitionCache cache = new JSONClassDefinitionCache( ctx );

            _writer = new JSONWriterImpl( _outStream, cache, componentManager, true );
            _writer.setExcludeNullFields( true );
        }
        return _writer;
    }

    public static synchronized String objectToJSON( Object o ) {

        _outStream.reset();

        try {
            _writer.resetState();

            _writer.objectToJson( o, PersistMode.AllFields );

            return _strBuf.toString();

        } catch( Exception e ) {
            return e.getMessage();
        }
    }

    public static synchronized void setContext( final SMTCoreContext base ) {
        if ( !_inCreate ) {
            _inCreate = true;

            /**
             * the custom codecs are constructed with the SMTStartContext and this happens thru the custom codec registration invoked by
             * the JSON cache. As  JSONUtils must not affect the real SMTComponentManager it requires its own spoofed Context
             */

            SMTComponentManager componentManager = new SMTComponentManager();
            SMTCoreContext      ctx              = base.clone( "JSUCTX", base, componentManager );

            ctx.setComponentManager( componentManager );
            ctx.setJSONCustomCodecs( ReflectUtils.newInstanceOf( ctx.getJSONCustomCodecs() ) );

            JSONClassDefinitionCache cache = new JSONClassDefinitionCache( ctx );

            _writer = new JSONWriterImpl( _outStream, cache, componentManager, true );
            _writer.setExcludeNullFields( true );

            _inCreate = false;
        }
    }


}
