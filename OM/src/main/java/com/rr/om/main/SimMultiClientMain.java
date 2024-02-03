/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.persister.PersisterException;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.CoreProps;
import com.rr.core.properties.PropertyGroup;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedDispatcher;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.SessionException;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.om.main.OMProps.Tags;
import com.rr.om.session.fixsocket.FixSocketConfig;
import com.rr.om.session.fixsocket.NonBlockingFixSocketSession;
import com.rr.om.warmup.sim.ClientSimSender;
import com.rr.sim.client.ClientSimNonBlockingFixSession;

/**
 * SubMicroTrading collapsed main loader
 */
public class SimMultiClientMain extends SimClientMain {

    private static final Logger    _console = ConsoleFactory.console( SimMultiClientMain.class, Level.info );
    private static final ErrorCode FAILED   = new ErrorCode( "SMM100", "Exception in main" );

    public static void main( String[] args ) {

        try {
            /**
             * The standard SimClient has single fix socket and uses direct dispatch, hence main thread IS the ClientSimulatorOut
             * For systestMultiClient the client has a real dispatch thread and orders must be generated ON the ClientSimulatorMain thread 
             */
            prepare( args, ThreadPriority.ClientSimulatorMain );

            _console.info( "SimMultiClientMain Started" );

            SimMultiClientMain smt = new SimMultiClientMain();
            smt.init();
            smt.warmup();

            SuperpoolManager.instance().resetPoolStats();

            smt.run();

            _console.info( "SimMultiClientMain Completed" );

        } catch( Exception e ) {

            _console.error( FAILED, "", e );
        }
    }

    public SimMultiClientMain() {
        super();
    }

    @Override
    protected void presize( int expOrders ) {

        super.presize( expOrders );

        PropertyGroup simGroup = new PropertyGroup( "sim.", null, null );

        int batchSize = simGroup.getIntProperty( Tags.batchSize, false, 1 );
        int chainSize = simGroup.getIntProperty( CoreProps.Tags.chainSize, false, 100 );

        int recycleNOS  = Math.max( 50000, batchSize );
        int orderChains = recycleNOS / chainSize;
        int extraAlloc  = 50;

        presize( NewOrderSingleImpl.class, orderChains, chainSize, extraAlloc );
    }

    @Override
    protected ClientSimSender createClientSender( SeqNumSession[] fixClients ) {
        PropertyGroup simGroup = new PropertyGroup( "sim.", null, null );

        /**
         * without throttling multiple messages can end up being enqueued against client socket
         * this can lead to burst messaging which will mean worse latency stats against better throughput stats
         */
        boolean throttleSender = simGroup.getBoolProperty( Tags.throttleSender, false, true );

        return new ClientSimSender( _templateRequests, fixClients, _statsMgr, getIdPrefix(), throttleSender );
    }

    @Override
    protected NonBlockingFixSocketSession createNonBlockingFixSession( FixSocketConfig socketConfig,
                                                                       EventRouter inboundRouter,
                                                                       FixEncoder encoder,
                                                                       FixDecoder decoder,
                                                                       FixDecoder fullDecoder,
                                                                       String name,
                                                                       MultiSessionThreadedDispatcher dispatcher,
                                                                       MultiSessionThreadedReceiver receiver,
                                                                       EventQueue dispatchQueue )

            throws SessionException, PersisterException {

        ClientSimNonBlockingFixSession sess = new ClientSimNonBlockingFixSession( name,
                                                                                  inboundRouter,
                                                                                  socketConfig,
                                                                                  dispatcher,
                                                                                  receiver,
                                                                                  encoder,
                                                                                  decoder,
                                                                                  fullDecoder,
                                                                                  dispatchQueue );

        return sess;
    }
}
