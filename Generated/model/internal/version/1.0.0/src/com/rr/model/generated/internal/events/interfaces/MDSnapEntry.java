package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.TickDirection;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface MDSnapEntry extends SubEvent {

   // Getters and Setters
    int getMdPriceLevel();

    MDEntryType getMdEntryType();

    double getMdEntryPx();

    int getMdEntrySize();

    int getMdEntryTime();

    TickDirection getTickDirection();

    int getTradeVolume();

    @Override void dump( ReusableString out );

    void setMdPriceLevel( int val );

    void setMdEntryType( MDEntryType val );

    void setMdEntryPx( double val );

    void setMdEntrySize( int val );

    void setMdEntryTime( int val );

    void setTickDirection( TickDirection val );

    void setTradeVolume( int val );

}
