package com.rr.core.model;

import com.rr.core.annotations.TimestampMS;
import com.rr.core.lang.*;
import com.rr.core.utils.Utils;

/**
 * to simplify we only have a single TradeRestriction per instrument, the most stringent overrides less stringent
 */
public class TradeRestriction extends BaseEvent<TradeRestriction> {

    private int        _tradeFlags;                     // may have multiple restrictionsi n place ref TradeRestrictionFlag, if rename MUST update getTradeRestrictDecoder
    private String     _desc;
    private Instrument _instrument;                     // instrument that restriction applies too.

    @TimestampMS private long _fromTS = 1;                  // restriction start timestamp, default 1 ie 1ms after midnight Jan 1 1970
    @TimestampMS private long _endTS  = Long.MAX_VALUE;     // restriction end timestamp, default NULL ie forever

    public static boolean inRange( final long fromTS, final long endTS ) {
        if ( Utils.isNull( fromTS ) ) {
            return true;
        }

        long now = ClockFactory.get().currentTimeMillis();

        return (now > fromTS) && (now < endTS || Utils.isNull( endTS ));
    }

    public TradeRestriction() {
        super();
    }

    public TradeRestriction( final Instrument instrument, final int tradeFlags ) {
        super();
        _instrument = instrument;
        _tradeFlags = tradeFlags;
    }

    public TradeRestriction( final Instrument instrument, final TradeRestrictionFlag tradeFlag ) {
        _instrument = instrument;
        _tradeFlags = TradeRestrictionFlag.setFlag( _tradeFlags, tradeFlag, true );
    }

    @Override public void dump( final ReusableString out ) {
        out.append( "inst=" ).append( _instrument.id() );
        out.append( ", flags=" ).append( TradeRestrictionFlag.toString( _tradeFlags ) );

        if ( _fromTS > 1 ) {
            out.append( ", fromTS=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, _fromTS );
        }

        if ( _endTS < Long.MAX_VALUE ) {
            out.append( ", endTS=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, _endTS );
        }
    }

    @Override public ReusableType getReusableType() { return CoreReusableType.TradeRestriction; }

    public String getDesc()                                                         { return _desc; }

    public void setDesc( final String desc )                                        { _desc = desc; }

    public long getEndTS()                                                          { return _endTS; }

    public void setEndTS( final long endTS )                                        { _endTS = endTS; }

    public long getFromTS()                                                         { return _fromTS; }

    public void setFromTS( final long fromTS )                                      { _fromTS = fromTS; }

    public Instrument getInstrument()                                               { return _instrument; }

    public void setInstrument( final Instrument instrument )                        { _instrument = instrument; }

    public int getTradeFlags()                                                      { return _tradeFlags; }

    public void setTradeFlags( final int flags )                                    { _tradeFlags = flags; }

    public boolean inRange( final long now )                                        { return (now > _fromTS || Utils.isNull( _fromTS )) && (now < _endTS || Utils.isNull( _endTS )); }

    public boolean inRange() {
        long fromTS = this._fromTS;
        long endTS  = this._endTS;

        return inRange( fromTS, endTS );
    }

    public boolean isTradeFlagSet( TradeRestrictionFlag flag )                      { return TradeRestrictionFlag.isOn( _tradeFlags, flag ); }

    public void setTradeFlag( final TradeRestrictionFlag flag, final boolean isOn ) { _tradeFlags = (byte) TradeRestrictionFlag.setFlag( _tradeFlags, flag, isOn ); }
}
