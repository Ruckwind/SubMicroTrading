/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.asia.bse.fastfix;

import com.rr.core.component.SMTComponent;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.SessionDirection;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.asia.bse.fastfix.reader.BSEFastFixDecoder;
import com.rr.md.fastfix.FastSocketConfig;
import com.rr.om.loaders.BaseSessionLoader;

import java.util.BitSet;

public class BSEFastFixSessionLoader extends BaseSessionLoader {

    private FastSocketConfig _sessionConfig;
    private EventRouter      _inboundRouter;
    private String           _subChannelOnMask;

    private MultiSessionThreadedReceiver _inboundDispatcher;

    private String _templateFile;

    private boolean _logIntermediateFix = true;

    public BSEFastFixSessionLoader() {
        super();

        _sessionDirection = SessionDirection.Upstream;

        _dummyPersister = true;
    }

    @Override
    public SMTComponent create( String id ) {

        try {
            prep();

            _sessionConfig.validate();

            BitSet bs = null;

            if ( _subChannelOnMask != null && !_subChannelOnMask.equals( "-1" ) ) {
                bs = new BitSet();

                String[] bitsOn = _subChannelOnMask.split( "," );

                for ( String b : bitsOn ) {
                    int flag = Integer.parseInt( b.trim() );

                    bs.set( flag );
                }
            }

            setMulticastGroups( _sessionConfig );

            BSEFastFixDecoder            decoder = new BSEFastFixDecoder( id, _templateFile, bs, _trace, _logIntermediateFix );
            BSENonBlockingFastFixSession sess    = new BSENonBlockingFastFixSession( id, _inboundRouter, _sessionConfig, _inboundDispatcher, decoder );

            sess.setLogStats( _logStats );
            sess.setLogEvents( _logEvents );
            sess.setLogPojos( _logPojoEvents );

            if ( _disableNanoStats ) {
                decoder.setNanoStats( false );
                sess.setLogStats( false );
            } else {
                decoder.setNanoStats( true );
            }

            postSessionCreate( null, sess );

            return sess;
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to create BSEFastFixSession id=" + id, e );
        }
    }
}
