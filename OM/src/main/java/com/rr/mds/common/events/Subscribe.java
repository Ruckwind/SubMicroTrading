/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common.events;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.TLC;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.mds.common.MDSReusableType;

public final class Subscribe extends BaseMDSEvent<Subscribe> {

    public static final int MAX_RIC_IN_SUB = 50;       // @NOTE MUST be less than 256

    private int _type;     // should match subid from MDSReusableTypeConstants
    private int _count;    // number of RICS in request

    private ReusableString _ricChain = new ReusableString( SizeConstants.DEFAULT_RIC_LENGTH );

    @Override
    public void dump( ReusableString out ) {
        ReusableString t = _ricChain;

        while( t != null ) {
            out.append( ',' ).append( t );
            t = t.getNext();
        }
    }

    @Override public final ReusableType getReusableType() {
        return MDSReusableType.Subscribe;
    }

    @Override
    public final void reset() {
        super.reset();

        _type  = 0;
        _count = 0;

        while( _ricChain != null ) {
            ReusableString s = _ricChain;

            TLC.instance().recycle( s );

            _ricChain = _ricChain.getNext();
        }
    }

    /**
     * inserts ric chain to start of subscription list in reverse order
     *
     * @param ric
     */
    public void addRICChain( ReusableString ric ) {

        while( ric != null ) {
            ReusableString copy = TLC.safeCopy( ric );

            copy.setNext( _ricChain );

            _ricChain = copy;
            ++_count;

            ric = ric.getNext();
        }
    }

    public int getCount() {
        return _count;
    }

    public ReusableString getExchangeSymbolChain() {
        return _ricChain;
    }

    public int getSubscriptionType() {
        return _type;
    }

    public void setType( MDSReusableType type ) {
        _type = type.getSubId();
    }
}
