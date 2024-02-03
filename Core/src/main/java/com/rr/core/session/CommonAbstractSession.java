/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.CompRunState;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.Utils;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CommonAbstractSession implements RecoverableSession {

    protected static final                   ErrorCode                                    SHUT_ERR           = new ErrorCode( "ASN100", "Unexpected error in shutdown handler " );
    protected static final                   ErrorCode                                    ERR_IN_MSG         = new ErrorCode( "ASN210", "Error receiving message : " );
    protected static final                   ErrorCode                                    ERR_OUT_MSG        = new ErrorCode( "ASN220", "Error sending message : " );
    protected static final                   ErrorCode                                    ERR_PERSIST_IN     = new ErrorCode( "ASN300", "Failed to persist inbound message" );
    protected static final                   ErrorCode                                    ERR_PERSIST_OUT    = new ErrorCode( "ASN310", "Failed to persist outbound message" );
    protected static final                   ErrorCode                                    ERR_PERSIST_MKR    = new ErrorCode( "ASN320", "Failed to mark outbound message persisted" );
    protected static final                   ErrorCode                                    RECOVER_ERR_IN     = new ErrorCode( "ASN400", "Failed to recover inbound messages" );
    protected static final                   ErrorCode                                    RECOVER_ERR_OUT    = new ErrorCode( "ASN410", "Failed to recover outbound messages" );
    private static final ZString REJ_DISCONNECTED = new ViewString( "Rejected as not connected" );
    private static final ZString DROP_MSG         = new ViewString( "Session dropping session message as not connected, type=" );
    private static final ZString ENCODE_ERR       = new ViewString( "Encoding error : " );
    private static final ZString SMT_SEND_ERR     = new ViewString( "SMT send error : " );
    private static int _nextIntId = 0;
    protected final Logger _log = LoggerFactory.create( CommonAbstractSession.class );
    private final     String         _name;
    private final     ReusableString _connId   = new ReusableString( 20 );
    private final     int                                                                 _intId             = nextIntId();
    @SuppressWarnings( "unchecked" ) private ZConsumer2Args<CompRunState, CompRunState>[] _runStateListeners = new ZConsumer2Args[0];
    private final                            Set<ConnectionListener>                      _listenerSet       = new LinkedHashSet<>();
    private transient volatile               CompRunState                                 _compRunState      = CompRunState.Initial;

    private static synchronized int nextIntId() {
        return ++_nextIntId;
    }

    public CommonAbstractSession( String name ) {
        super();
        _name = name;

        _connId.copy( name );

        _log.info( getComponentId() + " has session id " + getIntId() );
    }

    @Override public final String getComponentId() {
        return _name;
    }

    @Override public ZString getConnectionId()                    { return _connId; }

    @Override public void setConnectionId( final ZString connId ) { _connId.copy( connId ); }

    @Override public final int getIntId() {
        return _intId;
    }

    @Override public synchronized void registerConnectionListener( ConnectionListener listener ) { _listenerSet.add( listener ); }

    @Override public final CompRunState getCompRunState() {  return _compRunState; }

    @Override public void addCompStateListener( final ZConsumer2Args<CompRunState, CompRunState> callback ) { _runStateListeners = Utils.arrayCopyAndAddEntry( _runStateListeners, callback ); }

    protected synchronized boolean setCompRunState( CompRunState state ) {
        boolean changed = false;

        if ( CompRunState.procStateChange( id(), _compRunState, state ) ) {

            final CompRunState old = _compRunState;

            _compRunState = state;

            for( ZConsumer2Args<CompRunState, CompRunState> p : _runStateListeners ) {
                p.accept( old, state );
            }

            changed = true;
        }

        return changed;
    }

    public Set<ConnectionListener> getConnectionListenerSet() { return _listenerSet; }
}
