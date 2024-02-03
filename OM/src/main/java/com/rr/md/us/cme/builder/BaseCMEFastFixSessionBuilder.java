/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.builder;

import com.rr.core.codec.binary.fastfix.FastFixDecoder;
import com.rr.core.codec.binary.fastfix.NonBlockingFastFixSocketSession;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.EventHandler;
import com.rr.core.session.EventRouter;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.session.PassThruRouter;
import com.rr.core.session.SessionDirection;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.channel.MktDataChannel;
import com.rr.md.channel.MktDataChannelBuilder;
import com.rr.md.fastfix.FastSocketConfig;
import com.rr.md.us.cme.*;
import com.rr.md.us.cme.CMEConfig.Channel;
import com.rr.md.us.cme.CMEConnection.Protocol;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.session.SessionManager;

import java.util.*;

/**
 * CMESessionBuilder - create sessions and load balance across multiplex receivers as well as md event handlers
 *
 * @author Richard Rose
 * @TODO allow slow and fast market multiplexers .. fast spin .... use fast only for incremental updates
 */
public abstract class BaseCMEFastFixSessionBuilder implements MktDataChannelBuilder<Integer> {

    private static final Logger _log = LoggerFactory.create( BaseCMEFastFixSessionBuilder.class );

    protected final CMEConfig _cfg;

    protected final List<NonBlockingFastFixSocketSession>         _sessions;
    /**
     * each channel can have a single consumer, this map ensures against attempt to register more than one consumer to the channel
     */
    protected final Map<Integer, EventHandler> _channelToConsumer = new LinkedHashMap<>();
    private final   Map<Integer, NonBlockingFastFixSocketSession> _portToSessionMap;
    private final SocketConfig   _baseSocketConfig;
    private final SessionManager _sessMgr;
    private final String _id;
    private final String _nicA;
    private final String _nicB;
    private boolean _addInstrumentSessions  = true;
    private boolean _addSnapshotSessions    = true;
    private boolean _addIncrementalSessions = true;
    private boolean _disableNanoStats;
    private boolean _trace;
    private boolean _logEvents      = true;
    private boolean _enableEventPojoLogging;
    private long    _subChannelMask = -1;
    private       String _templateFile = "data/cme/templates.xml";

    /**
     * @param cfg
     * @param sessMgr          - session manager to register sessions with
     * @param baseSocketConfig
     * @param nics             - comma delim  list of NICs .. at most 2 ... A, B feed
     */
    public BaseCMEFastFixSessionBuilder( String id,
                                         CMEConfig cfg,
                                         SessionManager sessMgr,
                                         SocketConfig baseSocketConfig,
                                         String nics ) {

        _id               = id;
        _cfg              = cfg;
        _sessions         = new ArrayList<>();
        _portToSessionMap = new HashMap<>();
        _baseSocketConfig = baseSocketConfig;
        _sessMgr          = sessMgr;
        _nicA             = parseNICS( nics, 0 );
        _nicB             = parseNICS( nics, 1 );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public EventHandler getChannelConsumer( Integer channel ) {
        return _channelToConsumer.get( channel );
    }

    public boolean hasSessions( Integer channel ) {
        return _channelToConsumer.get( channel ) != null;
    }

    public boolean isDisableNanoStats() {
        return _disableNanoStats;
    }

    public void setDisableNanoStats( boolean disableNanoStats ) {
        _disableNanoStats = disableNanoStats;
    }

    public boolean isEnableEventPojoLogging() {
        return _enableEventPojoLogging;
    }

    public void setEnableEventPojoLogging( boolean enableEventPojoLogging ) {
        _enableEventPojoLogging = enableEventPojoLogging;
    }

    public boolean isTrace() {
        return _trace;
    }

    public void setTrace( boolean trace ) {
        _trace = trace;
    }

    public void setSubChannelMask( long subChannelMask ) {
        _subChannelMask = subChannelMask;
    }

    @SuppressWarnings( "boxing" )
    protected boolean ensureChannelsExist( MultiSessionThreadedReceiver multiplexReceiver, EventHandler mdEventConsumer, Channel ch, int sessionIdx ) {

        final int channel = ch.getChannelId();

        EventHandler existing = getChannelConsumer( channel );

        if ( existing == mdEventConsumer ) {
            return false;           // channels already exist and associated with consumer
        }

        if ( existing != null ) {
            throw new SMTRuntimeException( "Attempt to add duplicate sessions for channel " + ch +
                                           ", channelId=" + channel +
                                           " for " + mdEventConsumer.getComponentId() +
                                           ", but already registered against " + existing.getComponentId() + ", idx=" + sessionIdx );
        }

        String base = "CME_MD_Ch_" + channel;

        CMEConnections conns = ch.getConns();

        if ( conns == null ) return false;

        boolean added = false;

        if ( _addIncrementalSessions ) {
            added |= add( ch, ch.getConns().get( FeedType.Incremental, Feed.A ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_I_A", _nicA );
            added |= add( ch, ch.getConns().get( FeedType.Incremental, Feed.B ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_I_B", _nicB );
        }

        if ( _addInstrumentSessions ) {
            added |= add( ch, ch.getConns().get( FeedType.InstrumentReplay, Feed.A ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_R_A", _nicA );
            added |= add( ch, ch.getConns().get( FeedType.InstrumentReplay, Feed.B ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_R_B", _nicB );
        }

        if ( _addSnapshotSessions ) {
            added |= add( ch, ch.getConns().get( FeedType.Snapshot, Feed.A ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_S_A", _nicA );
            added |= add( ch, ch.getConns().get( FeedType.Snapshot, Feed.B ), multiplexReceiver, mdEventConsumer, sessionIdx, base + "_S_B", _nicB );
        }

        _channelToConsumer.put( channel, mdEventConsumer );

        _log.info( getComponentId() +
                   " register sessions for channel " + ch +
                   " with consumer " + mdEventConsumer.getComponentId() +
                   ", idx=" + sessionIdx +
                   ", addInstrumentSession=" + _addInstrumentSessions +
                   ", _addSnapshotSessions=" + _addSnapshotSessions );

        return added;
    }

    @SuppressWarnings( "boxing" )
    private boolean add( Channel ch, CMEConnection conn, MultiSessionThreadedReceiver multiplexReceiver, EventHandler mh, int sessionIdx, String id, String nic ) {
        if ( conn == null || conn.getPort() == 0 ) {
            return false;
        }

        Integer portInt = conn.getPort();

        NonBlockingFastFixSocketSession existingSess = _portToSessionMap.get( portInt );

        if ( existingSess != null && conn.getProtocol() == Protocol.UDP && ((SocketConfig) existingSess.getConfig()).getPort() == conn.getPort() ) {
            join( existingSess, conn, ch.getChannelId() );

            _log.info( getComponentId() +
                       " JOIN SESSION " + ch + ", id=" + id + " ADD multicast group " + conn.getIP() +
                       " to session " + existingSess.getComponentId() +
                       " port " + conn.getPort() );

            return false;
        }

        _log.info( getComponentId() + " CREATE SESSION " + ch + ", id=" + id + " ADD multicast group " + conn.getIP() + " port " + conn.getPort() );

        NonBlockingFastFixSocketSession sess = create( conn, multiplexReceiver, mh, id, nic, ch.getChannelId() );

        _sessMgr.add( sess, false );

        _portToSessionMap.put( portInt, sess );
        _sessions.add( sess );

        return true;
    }

    @SuppressWarnings( "boxing" )
    private NonBlockingFastFixSocketSession create( CMEConnection conn, MultiSessionThreadedReceiver multiplexReceiver, EventHandler handler, String id, String nic, int channel ) {

        FastSocketConfig config = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, conn.getPort() );

        config.setDirection( SessionDirection.Upstream );
        config.setDisableLoopback( _baseSocketConfig.isDisableLoopback() );
        config.setUseNIO( _baseSocketConfig.isUseNIO() );
        config.setNic( (nic == null) ? null : new ViewString( nic ) );
        config.setHostname( _baseSocketConfig.getHostname() );

        ZString[] grps = { new ViewString( conn.getIP() ) };
        config.setMulticast( true );
        config.setMulticastGroups( grps );

        long subChannelMask = getSubChannelMask( conn );

        FastFixDecoder decoder = new CMEFastFixDecoder( id, _templateFile, subChannelMask, _trace );

        EventRouter inboundRouter = new PassThruRouter( "passThruRouter", handler ); // currently each session only route to single handler

        CMENonBlockingFastFixSession sess = new CMENonBlockingFastFixSession( id,
                                                                              inboundRouter,
                                                                              config,
                                                                              multiplexReceiver,
                                                                              decoder );

        if ( _disableNanoStats ) {
            decoder.setNanoStats( false );
            sess.setLogStats( false );
        } else {
            decoder.setNanoStats( true );
        }

        sess.setLogPojos( _enableEventPojoLogging );
        sess.setLogEvents( _logEvents );

        sess.addChannelKey( channel );

        return sess;
    }

    private long getSubChannelMask( CMEConnection conn ) {
        return _subChannelMask;                                 // @TODO allow subChannelMask by channel
    }

    @SuppressWarnings( { "unchecked", "boxing" } )
    private void join( NonBlockingFastFixSocketSession sess, CMEConnection conn, int channel ) {
        ((SocketConfig) sess.getConfig()).addMulticastGroup( conn.getIP() );
        ((MktDataChannel<Integer>) sess).addChannelKey( channel );
    }

    private String parseNICS( String nics, int idx ) {
        if ( nics == null ) return null;

        String[] parts = nics.split( "," );

        if ( parts.length == 0 ) return null;

        if ( parts.length > 2 ) {
            throw new SMTRuntimeException( "CMEOnDemandSessionBuilder MD NICS list [" + nics + "] can have at most two entries not " + parts.length );
        }

        if ( idx >= parts.length ) return parts[ 0 ].trim();

        return parts[ idx ].trim();
    }
}
