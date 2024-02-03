package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.model.generated.internal.events.interfaces.SecDefEvent;
import com.rr.model.generated.internal.type.SecurityUpdateAction;
import com.rr.model.generated.internal.events.interfaces.SecDefLeg;
import com.rr.model.generated.internal.type.ProductComplex;
import com.rr.model.generated.internal.events.interfaces.SecurityAltID;
import com.rr.model.generated.internal.events.interfaces.SDFeedType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface SecurityDefinition extends BaseMDResponseWrite, Event {

   // Getters and Setters
    int getTotNumReports();

    SecurityTradingStatus getSecurityTradingStatus();

    SecurityType getSecurityType();

    long getUniqueInstId();

    long getSecDefId();

    long getExchangeLongId();

    SecurityIDSource getSecurityIDSource();

    ViewString getSecurityID();

    ViewString getSymbol();

    int getNoEvents();

    SecDefEvent getEvents();

    SecurityUpdateAction getSecurityUpdateAction();

    int getNoLegs();

    SecDefLeg getLegs();

    double getTradingReferencePrice();

    double getHighLimitPx();

    double getLowLimitPx();

    double getFutPointValue();

    double getMinPriceIncrement();

    double getMinPriceIncrementAmount();

    ViewString getSecurityGroup();

    ViewString getSecurityDesc();

    ViewString getSecurityLongDesc();

    ViewString getCFICode();

    ProductComplex getUnderlyingProduct();

    ExchangeCode getSecurityExchange();

    SecurityIDSource getUnderlyingSecurityIDSource();

    ViewString getUnderlyingSecurityID();

    ExchangeCode getUnderlyingScurityExchange();

    ExchangeCode getPrimarySecurityExchange();

    int getTickRule();

    int getNoSecurityAltID();

    SecurityAltID getSecurityAltIDs();

    double getStrikePrice();

    Currency getStrikeCurrency();

    Currency getCurrency();

    Currency getSettlCurrency();

    long getMinTradeVol();

    long getMaxTradeVol();

    int getNoSDFeedTypes();

    SDFeedType getSDFeedTypes();

    /**
     *format YYYYMMDD
     */
    int getMaturityMonthYear();

    ViewString getApplID();

    double getDisplayFactor();

    double getPriceRatio();

    int getContractMultiplierType();

    double getContractMultiplier();

    int getOpenInterestQty();

    int getTradingReferenceDate();

    int getMinQty();

    double getPricePrecision();

    UnitOfMeasure getUnitOfMeasure();

    double getUnitOfMeasureQty();

    ViewString getCompanyName();

    double getSharesOutstanding();

    long getCommonSecurityId();

    long getParentCompanyId();

    long getGicsCode();

    long getGetOutDate();

    long getDeadTimestamp();

    /**
     *version start timestamp
     */
    long getStartTimestamp();

    /**
     *version end timestamp
     */
    long getEndTimestamp();

    DataSrc getDataSrc();

    SecDefSpecialType getSecDefSpecialType();

    CompanyStatusType getCompanyStatusType();

    @Override void dump( ReusableString out );

}
