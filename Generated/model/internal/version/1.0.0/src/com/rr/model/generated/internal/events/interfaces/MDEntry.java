package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MDUpdateAction;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.TradingSessionID;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface MDEntry extends SubEvent {

   // Getters and Setters
    SecurityIDSource getSecurityIDSource();

    ViewString getSecurityID();

    ExchangeCode getSecurityExchange();

    MDUpdateAction getMdUpdateAction();

    int getRepeatSeq();

    int getNumberOfOrders();

    int getMdPriceLevel();

    MDEntryType getMdEntryType();

    double getMdEntryPx();

    int getMdEntrySize();

    int getMdEntryTime();

    TradingSessionID getTradingSessionID();

    @Override void dump( ReusableString out );

    void setSecurityIDSource( SecurityIDSource val );

    void setSecurityID( byte[] buf, int offset, int len );
    ReusableString getSecurityIDForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setMdUpdateAction( MDUpdateAction val );

    void setRepeatSeq( int val );

    void setNumberOfOrders( int val );

    void setMdPriceLevel( int val );

    void setMdEntryType( MDEntryType val );

    void setMdEntryPx( double val );

    void setMdEntrySize( int val );

    void setMdEntryTime( int val );

    void setTradingSessionID( TradingSessionID val );

}
