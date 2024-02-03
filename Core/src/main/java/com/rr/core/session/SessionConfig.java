/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.ExchangeCode;
import com.rr.core.properties.DynamicConfig;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.utils.SMTRuntimeException;

public class SessionConfig implements DynamicConfig, SMTComponent {

    private String                         _id;
    private Class<? extends EventRecycler> _recycler;
    private boolean                        _isMarkConfirmationEnabled      = false;
    private SessionDirection               _direction;
    private Class<? extends Throttler>     _throttlerClass                 = ThrottleWithExceptions.class;
    private int                            _maxMsgsPerSecond               = 0;                             // default no limit
    private int                            _maxResendRequestSize           = 0;                         // default no batching for resend requests
    private int                            _statsBlockSize                 = SizeConstants.DEFAULT_PERF_BLOCK_SIZE;
    private boolean                        _isLocalPersistWhenDisconnected = false;
    private ExchangeCode[]                 _validExchangeCodes             = null;
    private int                            _readSpinThrottle               = 0;
    private int                            _readSpinDelayMS                = 1;
    private boolean                        _dropCopyInboundEvents          = true;

    public SessionConfig() {
        super();
    }

    public SessionConfig( String id ) {
        _id = id;
    }

    public SessionConfig( Class<? extends EventRecycler> recycler ) {
        super();
        _recycler = recycler;
    }

    @Override public String getComponentId() {
        return _id;
    }

    public void setComponentId( final String id )                                       { _id = id; }

    @Override
    public String info() {
        return ((_direction != null) ? _direction.toString() : "") +
               ", maxMsgsPerSecond=" + _maxMsgsPerSecond;
    }

    @Override
    public void validate() throws SMTRuntimeException {
        if ( _recycler == null ) throw new SMTRuntimeException( "SessionConfig missing recycler" );
    }

    public SessionDirection getDirection() {
        return _direction;
    }

    public void setDirection( SessionDirection direction ) {
        _direction = direction;
    }

    /**
     * @return the WRITE throttle
     */
    public int getMaxMsgsPerSecond() { return _maxMsgsPerSecond; }

    public void setMaxMsgsPerSecond( int maxMsgsPerSecond ) {
        _maxMsgsPerSecond = maxMsgsPerSecond;
    }

    public int getMaxResendRequestSize() {
        return _maxResendRequestSize;
    }

    public void setMaxResendRequestSize( int maxResendRequestSize ) {
        _maxResendRequestSize = maxResendRequestSize;
    }

    public int getReadSpinDelayMS()                                                     { return _readSpinDelayMS; }

    public int getReadSpinThrottle()                                                    { return _readSpinThrottle; }

    public Class<? extends EventRecycler> getRecycler() {
        return _recycler;
    }

    public void setRecycler( Class<? extends EventRecycler> recycler ) {
        _recycler = recycler;
    }

    public int getStatsBlockSize()                            { return _statsBlockSize; }

    public void setStatsBlockSize( final int statsBlockSize ) { _statsBlockSize = statsBlockSize; }

    public Class<? extends Throttler> getThrottlerClass() {
        return _throttlerClass;
    }

    public ExchangeCode[] getValidExchangeCodes()                                       { return _validExchangeCodes; }

    public void setValidExchangeCodes( final ExchangeCode[] validExchangeCodes )        { _validExchangeCodes = validExchangeCodes; }

    public boolean isDropCopyInboundEvents()                                            { return _dropCopyInboundEvents; }

    public boolean isLocalPersistWhenDisconnected()                                     { return _isLocalPersistWhenDisconnected; }

    public void setLocalPersistWhenDisconnected( boolean localPersistWhenDisconnected ) { _isLocalPersistWhenDisconnected = localPersistWhenDisconnected; }

    public boolean isMarkConfirmationEnabled() {
        return _isMarkConfirmationEnabled;
    }

    public void setMarkConfirmationEnabled( boolean isMarkConfirmationEnabled ) {
        _isMarkConfirmationEnabled = isMarkConfirmationEnabled;
    }

    public boolean isOpenUTC( long currentTimeMillis ) {
        return true;
    }
}