package com.rr.core.model;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * container wrapper around individual broker response to cater for when we use several brokers
 */
public class LoanAvailability extends BaseEvent<LoanAvailability> {

    public static final Logger _log = LoggerFactory.create( LoanAvailability.class );

    private String     _desc;
    private Instrument _instrument;                     // instrument that restriction applies too.

    private List<CoreBrokerLoanResponse> _list = new ArrayList<>();

    private double _totAvail = 0;
    private double _maxCost  = 0;

    public LoanAvailability() {
        super();
    }

    public LoanAvailability( final Instrument instrument ) {
        super();
        _instrument = instrument;
    }

    public LoanAvailability( final Instrument instrument, LoanAvailability src ) {
        super();
        _instrument = instrument;
        _list       = src._list;
    }

    @Override public void dump( final ReusableString out ) {
        out.append( "inst=" ).append( _instrument.id() );
        out.append( ", shortAvail=" ).append( getTotalShortAvail() );

        for ( int i = 0; i < _list.size(); i++ ) {
            CoreBrokerLoanResponse e = _list.get( i );
            out.append( ", [" );
            e.dump( out );
            out.append( "]" );
        }
    }

    @Override public ReusableType getReusableType()          { return CoreReusableType.TradeRestriction; }

    public synchronized boolean canShortTotal( double totalShortPosition ) {
        return (Math.abs( totalShortPosition ) < _totAvail);
    }

    public double getCost( final double resultingPosition ) {
        return _maxCost;
    }

    public String getDesc()                                  { return _desc; }

    public void setDesc( final String desc )                 { _desc = desc; }

    public Instrument getInstrument()                        { return _instrument; }

    public void setInstrument( final Instrument instrument ) { _instrument = instrument; }

    public synchronized double getTotalShortAvail() {
        return _totAvail;
    }

    public boolean isDisabled() {
        boolean disabled = true;

        for ( int i = 0; i < _list.size(); i++ ) {

            CoreBrokerLoanResponse e = _list.get( i );

            if ( !e.getIsDisabled() ) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    public synchronized void update( final CoreBrokerLoanResponse latest ) {

        _totAvail += latest.getApproveQty();
        _maxCost = latest.getAmount();

        for ( int i = 0; i < _list.size(); i++ ) {

            CoreBrokerLoanResponse prev = _list.get( i );

            if ( prev.getBroker() == latest.getBroker() ) {
                _totAvail -= prev.getApproveQty();

                _list.set( i, latest );

                return;
            }
        }

        _list.add( latest );

        for ( int i = 0; i < _list.size(); i++ ) {

            CoreBrokerLoanResponse prev = _list.get( i );

            if ( prev.getAmount() > _maxCost ) {
                _maxCost = prev.getAmount();
            }
        }

        _log.info( "LoanAvailability " + latest.getInstrument().id() + " totAvailQty=" + _totAvail );
    }
}
