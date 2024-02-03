package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface TickUpdate extends SubEvent {

   // Getters and Setters
    MDEntryType getMdEntryType();

    double getMdEntryPx();

    int getMdEntrySize();

    long getTradeTime();

    Side getTickDirection();

    int getNumberOfOrders();

    @Override void dump( ReusableString out );

    void setMdEntryType( MDEntryType val );

    void setMdEntryPx( double val );

    void setMdEntrySize( int val );

    void setTradeTime( long val );

    void setTickDirection( Side val );

    void setNumberOfOrders( int val );

}
