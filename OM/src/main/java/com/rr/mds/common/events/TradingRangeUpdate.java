/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common.events;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.model.TradingRange;
import com.rr.mds.common.MDSReusableType;
import com.rr.om.Strings;

public final class TradingRangeUpdate extends BaseMDSEvent<TradingRangeUpdate> {

    private static final ViewString LOWER  = new ViewString( ", Low=" );
    private static final ViewString UPPER  = new ViewString( ", high=" );
    private static final ViewString LOW_ID = new ViewString( ", lowId=" );
    private static final ViewString UP_ID  = new ViewString( ", highId=" );

    private ReusableString _ric = new ReusableString( SizeConstants.DEFAULT_RIC_LENGTH );

    private double _lower = 0;
    private double _upper = Double.MAX_VALUE;

    private long _lowerId = 0L;                  // tick id
    private long _upperId = 0L;                  // tick id ... is actually a nano timestamp

    private int _lowerFlags;
    private int _upperFlags;

    @Override public void dump( ReusableString out ) {

        out.append( Strings.RIC ).append( _ric );
        out.append( LOWER ).append( _lower );
        out.append( UPPER ).append( _upper );
        out.append( LOW_ID ).append( _lowerId );
        out.append( UP_ID ).append( _upperId );
    }

    @Override public final ReusableType getReusableType() {
        return MDSReusableType.TradingBandUpdate;
    }

    @Override
    public final void reset() {
        super.reset();
        _lower      = 0;
        _upper      = 0;
        _lowerId    = 0L;
        _upperId    = 0L;
        _lowerFlags = 0;
        _upperFlags = 0;
        _ric.reset();
    }

    public ViewString getExchangeSymbol() {
        return _ric;
    }

    public double getLower() {
        return _lower;
    }

    public int getLowerFlags() {
        return _lowerFlags;
    }

    public long getLowerId() {
        return _lowerId;
    }

    public ReusableString getRic() {
        return _ric;
    }

    public ReusableString getRicForUpdate() {
        return _ric;
    }

    public double getUpper() {
        return _upper;
    }

    public int getUpperFlags() {
        return _upperFlags;
    }

    public long getUpperId() {
        return _upperId;
    }

    public final void setBands( double lower,
                                double upper,
                                long lowerId,
                                long upperId,
                                int lowerFlags,
                                int upperFlags ) {
        _lower      = lower;
        _upper      = upper;
        _lowerId    = lowerId;
        _upperId    = upperId;
        _lowerFlags = lowerFlags;
        _upperFlags = upperFlags;
    }

    public void setTradingRange( TradingRange range ) {
        if ( _upper > 0 ) range.setMaxBuy( _upperId, _upper, _upperFlags );
        range.setMinSell( _lowerId, _lower, _lowerFlags );
    }
}
