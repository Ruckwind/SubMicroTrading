/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.ZString;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.model.SecurityType;
import com.rr.model.generated.internal.type.*;
import com.rr.om.client.OMClientProfile;

import javax.sound.midi.Instrument;
import java.util.Currency;

public interface TstNOS {

    // common to msgs

    ZString getAccount();

    BookingType getBookingType();

    // common to all fix events

    ZString getClOrdID();

    OMClientProfile getClientProfile();

    Currency getCurrency();

    long getEffectiveTime();

    ZString getExDestination();

    long getExpireTime();

    HandlInst getHandlingInstruction();

    SecurityIDSource getIDSource();

    // NOS fields

    Instrument getInstrument();

    long getMaxFloor();

    int getMsgSeqNo();

    ZString getOnBehalfOfID();

    OrdType getOrdType();

    OrderCapacity getOrderCapacity();

    long getOrderQty();

    Currency getOriginalCurrency();

    PositionEffect getPositionEffect();

    boolean getPossDup();

    double getPrice();

    ZString getSecClOrdID();

    ZString getSecurityExch();

    ZString getSecurityID();

    SecurityType getSecurityType();

    ZString getSenderCompID();

    ZString getSenderSubID();

    Side getSide();

    ZString getSymbol();

    ZString getTargetCompID();

    ZString getTargetSubID();

    ZString getText();

    TimeInForce getTimeInForce();

    long getTmReceive();

    long getTmTransmit();

    void setTmTransmit( long value );

    long getTransactTime();

}
