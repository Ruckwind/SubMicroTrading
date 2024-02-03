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

public interface SecurityDefinitionWrite extends BaseMDResponseWrite, SecurityDefinition {

   // Getters and Setters
    void setTotNumReports( int val );

    void setSecurityTradingStatus( SecurityTradingStatus val );

    void setSecurityType( SecurityType val );

    void setUniqueInstId( long val );

    void setSecDefId( long val );

    void setExchangeLongId( long val );

    void setSecurityIDSource( SecurityIDSource val );

    void setSecurityID( byte[] buf, int offset, int len );
    ReusableString getSecurityIDForUpdate();

    void setSymbol( byte[] buf, int offset, int len );
    ReusableString getSymbolForUpdate();

    void setNoEvents( int val );

    void setEvents( SecDefEvent val );

    void setSecurityUpdateAction( SecurityUpdateAction val );

    void setNoLegs( int val );

    void setLegs( SecDefLeg val );

    void setTradingReferencePrice( double val );

    void setHighLimitPx( double val );

    void setLowLimitPx( double val );

    void setFutPointValue( double val );

    void setMinPriceIncrement( double val );

    void setMinPriceIncrementAmount( double val );

    void setSecurityGroup( byte[] buf, int offset, int len );
    ReusableString getSecurityGroupForUpdate();

    void setSecurityDesc( byte[] buf, int offset, int len );
    ReusableString getSecurityDescForUpdate();

    void setSecurityLongDesc( byte[] buf, int offset, int len );
    ReusableString getSecurityLongDescForUpdate();

    void setCFICode( byte[] buf, int offset, int len );
    ReusableString getCFICodeForUpdate();

    void setUnderlyingProduct( ProductComplex val );

    void setSecurityExchange( ExchangeCode val );

    void setUnderlyingSecurityIDSource( SecurityIDSource val );

    void setUnderlyingSecurityID( byte[] buf, int offset, int len );
    ReusableString getUnderlyingSecurityIDForUpdate();

    void setUnderlyingScurityExchange( ExchangeCode val );

    void setPrimarySecurityExchange( ExchangeCode val );

    void setTickRule( int val );

    void setNoSecurityAltID( int val );

    void setSecurityAltIDs( SecurityAltID val );

    void setStrikePrice( double val );

    void setStrikeCurrency( Currency val );

    void setCurrency( Currency val );

    void setSettlCurrency( Currency val );

    void setMinTradeVol( long val );

    void setMaxTradeVol( long val );

    void setNoSDFeedTypes( int val );

    void setSDFeedTypes( SDFeedType val );

    void setMaturityMonthYear( int val );

    void setApplID( byte[] buf, int offset, int len );
    ReusableString getApplIDForUpdate();

    void setDisplayFactor( double val );

    void setPriceRatio( double val );

    void setContractMultiplierType( int val );

    void setContractMultiplier( double val );

    void setOpenInterestQty( int val );

    void setTradingReferenceDate( int val );

    void setMinQty( int val );

    void setPricePrecision( double val );

    void setUnitOfMeasure( UnitOfMeasure val );

    void setUnitOfMeasureQty( double val );

    void setCompanyName( byte[] buf, int offset, int len );
    ReusableString getCompanyNameForUpdate();

    void setSharesOutstanding( double val );

    void setCommonSecurityId( long val );

    void setParentCompanyId( long val );

    void setGicsCode( long val );

    void setGetOutDate( long val );

    void setDeadTimestamp( long val );

    void setStartTimestamp( long val );

    void setEndTimestamp( long val );

    void setDataSrc( DataSrc val );

    void setSecDefSpecialType( SecDefSpecialType val );

    void setCompanyStatusType( CompanyStatusType val );

}
