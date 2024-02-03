/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TickScale implements TickType {

    private static final ZString NOT_IN_BAND = new ViewString( " Price doesnt fall within any tick band, price=" );

    private final ZString        _scaleName;
    private       List<TickBand> _bands = new ArrayList<>();

    public TickScale( ZString scaleName, TickBand[] bands ) {
        this( scaleName );

        _bands.addAll( Arrays.asList( bands ) );
    }

    public TickScale( ZString scaleName ) {
        _scaleName = scaleName;
    }

    @Override public boolean canVerifyPrice() {
        return true;
    }

    @Override public ZString getId() {
        return _scaleName;
    }

    @Override public boolean isValid( double price ) {

        final int max = _bands.size();

        for ( int i = 0; i < max; i++ ) {
            final TickBand band = _bands.get( i );

            if ( band.inBand( price ) ) {
                return band.isValid( price );
            }
        }

        return false;
    }

    @Override public double tickSize( double price ) {

        final int max = _bands.size();

        for ( int i = 0; i < max; i++ ) {
            final TickBand band = _bands.get( i );

            if ( band.inBand( price ) ) {
                return band.getTickSize();
            }
        }

        return Constants.UNSET_DOUBLE;
    }

    @Override public void writeError( double price, ReusableString err ) {

        final int max = _bands.size();

        for ( int i = 0; i < max; i++ ) {
            final TickBand band = _bands.get( i );

            if ( band.inBand( price ) ) {
                band.writeError( price, err );

                return;
            }
        }

        err.copy( NOT_IN_BAND );
        err.append( price );
    }

    public void addBand( TickBand band ) {
        final double lower    = band.getLower();
        final double upper    = band.getUpper();
        final double tickSize = band.getTickSize();

        if ( lower > upper ) throw new RuntimeException( "Invalid band lower > upper, lower=" + lower + ", upper=" + upper + ", tick=" + tickSize );
        if ( tickSize <= 0 ) throw new RuntimeException( "Invalid band tick > 0, tick=" + tickSize );

        final int curNumBands = _bands.size();
        for ( int i = 0; i < curNumBands; ++i ) {
            final TickBand cur = _bands.get( i );

            if ( lower < cur.getLower() ) {
                if ( upper > cur.getLower() ) {
                    throw new RuntimeException( "Invalid band overlaps with band idx=" + i + ", lower=" + cur.getLower() + ", upper=" + cur.getUpper() );
                }

                _bands.add( i, band );

                return;
            }
        }

        if ( lower <= 0 && curNumBands > 0 ) {
            throw new RuntimeException( "Invalid band cant be inserted at top of list, lower=" + lower + ", upper=" + upper );
        }

        _bands.add( band );
    }

    public TickBand getBand( int idx ) {
        return _bands.get( idx );
    }

    /**
     * @param price
     * @return the index of the band valid for the supplied price or -1 if none
     */
    public int getBandIdx( double price ) {
        final int max = _bands.size();

        for ( int i = 0; i < max; i++ ) {
            final TickBand band = _bands.get( i );

            if ( band.inBand( price ) ) {
                return i;
            }
        }

        return -1;
    }

    public int getNumBands() { return _bands.size(); }

    public void replaceAll( TickBand[] bands ) {
        _bands.clear();

        _bands.addAll( Arrays.asList( bands ) );
    }
}
