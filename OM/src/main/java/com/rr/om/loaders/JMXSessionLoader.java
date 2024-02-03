/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.annotations.OptionalReference;
import com.rr.core.codec.Decoder;
import com.rr.core.component.SMTComponent;
import com.rr.core.model.ClientProfile;
import com.rr.core.session.EventRouter;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.om.client.DummyClientProfile;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.jmx.JMXSession;

public class JMXSessionLoader extends BaseSessionLoader {

    @OptionalReference
    private EventRouter _inboundRouter;

    @OptionalReference
    private ClientProfile _clientProfile = new DummyClientProfile();

    @Override
    public SMTComponent create( String id ) {

        try {
            String sessName = id;

            Decoder decoder = getFullDecoder( _codecId, _clientProfile, true );

            FixSocketConfig config = new FixSocketConfig();
            config.setFixVersion( _codecId.getFixVersion() );
            config.setCodecId( _codecId );

            JMXSession sess = new JMXSession( sessName, config, _inboundRouter, decoder, _sessionManager );

            postSessionCreate( null, sess );

            return sess;

        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to create MultiFixSession id=" + id, e );
        }
    }

}
