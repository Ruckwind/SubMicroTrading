/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.persister.Persister;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.core.session.RecoverableSession;

import java.nio.ByteBuffer;

public class RecoverySessionContextImpl implements DMARecoverySessionContext {

    private final RecoverableSession _sess;
    private final boolean            _isInbound;
    private final ReusableString     _warnMsg   = new ReusableString();
    private final ReusableString     _tmpMsgBuf = new ReusableString( 8192 );
    private final ByteBuffer         _tmpCtxBuf = ByteBuffer.allocate( 1024 );
    private       Persister          _persister;

    public RecoverySessionContextImpl( RecoverableSession sess, boolean isInbound ) {
        super();
        _sess      = sess;
        _isInbound = isInbound;
    }

    @Override
    public Persister getPersister() {
        return _persister;
    }

    @Override
    public void setPersister( Persister persister ) {
        _persister = persister;
    }

    @Override
    public RecoverableSession getSession() {
        return _sess;
    }

    @Override
    public ReusableString getWarnMessage() {
        return _warnMsg;
    }

    @Override
    public boolean hasChainSession() {
        return _sess.getChainSession() != null;
    }

    @Override
    public boolean isInbound() {
        return _isInbound;
    }

    @Override
    public boolean persistFlagConfirmSentEnabled() {
        return _sess.getConfig().isMarkConfirmationEnabled();
    }

    @Override
    public Event regenerate( long persistKey ) {
        return _sess.recoverEvent( _isInbound, persistKey, _tmpMsgBuf, _tmpCtxBuf );
    }
}
