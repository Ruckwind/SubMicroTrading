/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.builder;

import com.rr.core.model.EventHandler;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.us.cme.CMEConfig;
import com.rr.md.us.cme.CMEConfig.Channel;
import com.rr.om.session.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CMESessionBuilder - create sessions and load balance across multiplex receivers as well as md event handlers
 *
 * @author Richard Rose
 * @TODO allow slow and fast market multiplexers .. fast spin .... use fast only for incremental updates
 */
public class CMEOnDemandFastFixSessionBuilder extends BaseCMEFastFixSessionBuilder {

    private final Map<String, MultiSessionThreadedReceiver> _pipeLineToReceiver;

    private int _nextSessIdx = 0;

    /**
     * @param cfg
     * @param sessMgr            - session manager to register sessions with
     * @param multiplexReceivers -  list of receivers which give a timeslice to try and read from the MD session.
     *                           Multiplex receivers are themselves shared (multipexed) across Control threads
     * @param baseSocketConfig
     * @param nics               - comma delim  list of NICs .. at most 2 ... A, B feed
     */
    public CMEOnDemandFastFixSessionBuilder( String id,
                                             CMEConfig cfg,
                                             SessionManager sessMgr,
                                             MultiSessionThreadedReceiver[] multiplexReceivers,
                                             SocketConfig baseSocketConfig,
                                             String nics ) {

        super( id, cfg, sessMgr, baseSocketConfig, nics );

        _pipeLineToReceiver = new HashMap<>();

        for ( MultiSessionThreadedReceiver r : multiplexReceivers ) {
            List<String> pipeLines = r.getPipeLineIds();
            for ( String pipe : pipeLines ) {
                if ( _pipeLineToReceiver.containsKey( pipe ) ) {
                    throw new SMTRuntimeException( "MultiSessionThreadedReceiver has duplicate pipeId of " + pipe +
                                                   " in " + r.getComponentId() +
                                                   " and " + _pipeLineToReceiver.get( pipe ).getComponentId() );
                }

                _pipeLineToReceiver.put( pipe, r );
            }
        }
    }

    @Override
    public void register( Integer channel, String pipeLineId, EventHandler consumer ) {

        Channel ch = _cfg.get( channel );

        if ( ch == null ) {
            throw new SMTRuntimeException( "CME channel is not recognised " + channel ); // shouldnt be possible
        }

        MultiSessionThreadedReceiver receiver = _pipeLineToReceiver.get( pipeLineId );

        if ( receiver == null ) {
            throw new SMTRuntimeException( "CME channel " + channel + " has no associated MultiSessionThreadedReceiver" );
        }

        ensureChannelsExist( receiver, consumer, ch, ++_nextSessIdx );
    }
}
