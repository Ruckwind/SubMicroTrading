package com.rr.core.pubsub;

import com.rr.core.annotations.Persist;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTSnapshotMemberOnlyPersistFields;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ZFunction;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Instrument;
import com.rr.core.model.InstrumentSubscriptionListener;
import com.rr.core.model.SecurityType;
import com.rr.core.properties.ConfigParam;
import com.rr.core.thread.RunState;
import com.rr.core.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.rr.core.pubsub.PubSubUtils.APP_SUB;
import static com.rr.core.pubsub.PubSubUtils.makeInstSubscriptionsTopic;

public class InstSubsManager implements SMTInitialisableComponent, SubsManager<Instrument>, SMTSnapshotMemberOnlyPersistFields {

    private static final Logger _log = LoggerFactory.create( InstSubsManager.class );

    private                        RunState                         _runState    = RunState.Unknown;
    private                        String                           _id;
    private @ConfigParam transient ConnectionFactory                _connectionFactory; // reflection
    private transient              InstrumentSubscriptionListener[] _listeners   = new InstrumentSubscriptionListener[ 0 ];
    private @Persist               Map<String, Instrument>          _instruments = new ConcurrentHashMap<>();
    private transient              PubSubSess                       _pubSubSess;
    private transient              ZFunction<Instrument, Boolean>   _filter;

    public InstSubsManager( final String id ) {
        _id = id;
    }

    @Override public void getAllSubscribed( final Set<Instrument> dest ) { dest.addAll( _instruments.values() ); }

    @Override public boolean isSubscribed( final Instrument inst )       { return _instruments.containsKey( inst.id() ); }

    @Override public void subscribe( final Instrument inst ) {

        if ( inst == null || (_filter != null && _filter.apply( inst )) ) return;

        boolean subscribed = _instruments.containsKey( inst.id() );

        if ( !subscribed ) {
            Instrument prev = _instruments.putIfAbsent( inst.id(), inst );

            if ( prev == null ) {

                Map<String, Instrument> insts = Collections.singletonMap( inst.id(), inst );

                _pubSubSess.asyncPublish( makeInstSubscriptionsTopic(), insts );
            }
        }
    }

    @Override public void subscribe( final Instrument[] insts ) {
        Map<String, Instrument> sendMap = null;

        for ( Instrument inst : insts ) {
            if ( inst != null && !_filter.apply( inst ) ) {
                if ( !_instruments.containsKey( inst.id() ) ) {
                    if ( sendMap == null ) {
                        sendMap = new HashMap<>( 4 );
                    }
                    sendMap.put( inst.id(), inst );
                }
            }
        }

        if ( sendMap != null ) {

            _instruments.putAll( sendMap );

            _pubSubSess.asyncPublish( makeInstSubscriptionsTopic(), sendMap );

            // notifyListenerts will be async invoked from subscription
        }
    }

    @Override public String getComponentId() { return _id; }

    @Override public RunState getRunState()                                                           { return _runState; }

    @Override public RunState setRunState( final RunState newState ) {
        final RunState old = _runState;
        _runState = newState;
        return old;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {

        Connection conn = _connectionFactory.getConnection();

        _pubSubSess = conn.create( getComponentId(), ctx );

        _filter = ( inst ) -> (inst.getSecurityType() == SecurityType.Strategy); // exclude strategy instruments for now

        PubSubSess.Callback<String, Map<String, Instrument>> callback = new PubSubSess.Callback<String, Map<String, Instrument>>() {

            @Override public void onMsg( String subject, String context, Map<String, Instrument> insts, long timeStampNano, long seqNum, String replyTopic ) {

                boolean publish = false;

                for ( Instrument inst : insts.values() ) {
                    if ( inst != null ) {
                        if ( _log.isEnabledFor( Level.trace ) ) {
                            _log.log( Level.trace, "On " + subject + " : Received inst " + inst.id() );
                        }

                        if ( _instruments.putIfAbsent( inst.id(), inst ) == null ) {
                            publish = true;
                        }
                    }
                }

                if ( publish ) {
                    if ( _log.isEnabledFor( Level.debug ) ) {

                        if ( insts.size() == 1 ) {
                            final Instrument instId = insts.values().iterator().next();
                            _log.log( Level.debug, "On " + subject + " : Received inst " + instId.id() + ", seqNum=" + seqNum );
                        } else {
                            _log.log( Level.debug, "On " + subject + " : Received " + insts.size() + " insts, seqNum=" + seqNum );
                        }
                    }

                    notifyListeners( insts );
                }
            }
        };

        SubscribeOptions opts = new SubscribeOptions();
        opts.setDurable( false );
        opts.setSubMode( SubscribeOptions.InitialSubMode.fromSeqNum );
        opts.setFromSeqNum( 0 ); // from NEXT

        _pubSubSess.stream( MsgStream.subscriptions );

        _pubSubSess.subscribe( APP_SUB( getComponentId() ), makeInstSubscriptionsTopic(), null, callback, opts );
    }

    public synchronized void addSubscriptionListener( final InstrumentSubscriptionListener listener ) { _listeners = Utils.arrayCopyAndAddEntry( _listeners, listener ); }

    public void notifyListeners( final Map<String, Instrument> insts ) {

        Set<Instrument> instSet = new HashSet<>();

        for ( Instrument i : insts.values() ) {
            if ( i != null ) {
                instSet.add( i );
            }
        }

        if ( instSet.size() > 0 ) {
            for ( InstrumentSubscriptionListener l : _listeners ) {

                l.changed( instSet, true, ClockFactory.get().currentTimeMillis() );
            }
        }
    }
}
