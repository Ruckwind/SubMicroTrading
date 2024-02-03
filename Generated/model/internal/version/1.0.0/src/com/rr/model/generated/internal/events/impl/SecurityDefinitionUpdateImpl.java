package com.rr.model.generated.internal.events.impl;

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
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class SecurityDefinitionUpdateImpl implements SecurityDefinition, SecurityDefinitionUpdateWrite, Copyable<SecurityDefinitionUpdate>, Reusable<SecurityDefinitionUpdateImpl> {

   // Attrs

    private transient          SecurityDefinitionUpdateImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _totNumReports = Constants.UNSET_INT;
    private long _uniqueInstId = Constants.UNSET_LONG;
    private long _secDefId = Constants.UNSET_LONG;
    private long _exchangeLongId = Constants.UNSET_LONG;
    private final ReusableString _securityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private final ReusableString _symbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _noEvents = Constants.UNSET_INT;
    private int _noLegs = Constants.UNSET_INT;
    private double _tradingReferencePrice = Constants.UNSET_DOUBLE;
    private double _highLimitPx = Constants.UNSET_DOUBLE;
    private double _lowLimitPx = Constants.UNSET_DOUBLE;
    private double _futPointValue = Constants.UNSET_DOUBLE;
    private double _minPriceIncrement = Constants.UNSET_DOUBLE;
    private double _minPriceIncrementAmount = Constants.UNSET_DOUBLE;
    private final ReusableString _securityGroup = new ReusableString( SizeType.INST_SEC_GRP_LEN.getSize() );
    private final ReusableString _securityDesc = new ReusableString( SizeType.INST_SEC_DESC_LENGTH.getSize() );
    private final ReusableString _securityLongDesc = new ReusableString( SizeType.INST_SEC_LDESC_LENGTH.getSize() );
    private final ReusableString _CFICode = new ReusableString( SizeType.INST_CFI_CODE_LENGTH.getSize() );
    private final ReusableString _underlyingSecurityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _tickRule = Constants.UNSET_INT;
    private int _noSecurityAltID = Constants.UNSET_INT;
    private double _strikePrice = Constants.UNSET_DOUBLE;
    private long _minTradeVol = Constants.UNSET_LONG;
    private long _maxTradeVol = Constants.UNSET_LONG;
    private int _noSDFeedTypes = Constants.UNSET_INT;
    private int _maturityMonthYear = Constants.UNSET_INT;
    private final ReusableString _applID = new ReusableString( SizeType.INST_APPL_ID_LENGTH.getSize() );
    private double _displayFactor = Constants.UNSET_DOUBLE;
    private double _priceRatio = Constants.UNSET_DOUBLE;
    private int _contractMultiplierType = Constants.UNSET_INT;
    private double _contractMultiplier = 1;
    private int _openInterestQty = Constants.UNSET_INT;
    private int _tradingReferenceDate = Constants.UNSET_INT;
    private int _minQty = 1;
    private double _pricePrecision = Constants.UNSET_DOUBLE;
    private double _unitOfMeasureQty = Constants.UNSET_DOUBLE;
    private final ReusableString _companyName = new ReusableString( SizeType.COMPANY_NAME_LEN.getSize() );
    private double _sharesOutstanding = Constants.UNSET_DOUBLE;
    private long _commonSecurityId = Constants.UNSET_LONG;
    private long _parentCompanyId = Constants.UNSET_LONG;
    private long _gicsCode = Constants.UNSET_LONG;
    @TimestampMS private long _getOutDate = Constants.UNSET_LONG;
    @TimestampMS private long _deadTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _startTimestamp = Constants.UNSET_LONG;
    @TimestampMS private long _endTimestamp = Constants.UNSET_LONG;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecurityTradingStatus _securityTradingStatus;
    private SecurityType _securityType;
    private SecurityIDSource _securityIDSource;
    private SecDefEvent _events;
    private SecurityUpdateAction _securityUpdateAction;
    private SecDefLeg _legs;
    private ProductComplex _underlyingProduct;
    private ExchangeCode _securityExchange;
    private SecurityIDSource _underlyingSecurityIDSource;
    private ExchangeCode _underlyingScurityExchange;
    private ExchangeCode _primarySecurityExchange;
    private SecurityAltID _securityAltIDs;
    private Currency _strikeCurrency;
    private Currency _currency;
    private Currency _settlCurrency;
    private SDFeedType _SDFeedTypes;
    private UnitOfMeasure _unitOfMeasure;
    private DataSrc _dataSrc = DataSrc.UNS;
    private SecDefSpecialType _secDefSpecialType = SecDefSpecialType.Standard;
    private CompanyStatusType _companyStatusType;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getTotNumReports() { return _totNumReports; }
    @Override public final void setTotNumReports( int val ) { _totNumReports = val; }

    @Override public final SecurityTradingStatus getSecurityTradingStatus() { return _securityTradingStatus; }
    @Override public final void setSecurityTradingStatus( SecurityTradingStatus val ) { _securityTradingStatus = val; }

    @Override public final SecurityType getSecurityType() { return _securityType; }
    @Override public final void setSecurityType( SecurityType val ) { _securityType = val; }

    @Override public final long getUniqueInstId() { return _uniqueInstId; }
    @Override public final void setUniqueInstId( long val ) { _uniqueInstId = val; }

    @Override public final long getSecDefId() { return _secDefId; }
    @Override public final void setSecDefId( long val ) { _secDefId = val; }

    @Override public final long getExchangeLongId() { return _exchangeLongId; }
    @Override public final void setExchangeLongId( long val ) { _exchangeLongId = val; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final ViewString getSecurityID() { return _securityID; }

    @Override public final void setSecurityID( byte[] buf, int offset, int len ) { _securityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIDForUpdate() { return _securityID; }

    @Override public final ViewString getSymbol() { return _symbol; }

    @Override public final void setSymbol( byte[] buf, int offset, int len ) { _symbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getSymbolForUpdate() { return _symbol; }

    @Override public final int getNoEvents() { return _noEvents; }
    @Override public final void setNoEvents( int val ) { _noEvents = val; }

    @Override public final SecDefEvent getEvents() { return _events; }
    @Override public final void setEvents( SecDefEvent val ) { _events = val; }

    @Override public final SecurityUpdateAction getSecurityUpdateAction() { return _securityUpdateAction; }
    @Override public final void setSecurityUpdateAction( SecurityUpdateAction val ) { _securityUpdateAction = val; }

    @Override public final int getNoLegs() { return _noLegs; }
    @Override public final void setNoLegs( int val ) { _noLegs = val; }

    @Override public final SecDefLeg getLegs() { return _legs; }
    @Override public final void setLegs( SecDefLeg val ) { _legs = val; }

    @Override public final double getTradingReferencePrice() { return _tradingReferencePrice; }
    @Override public final void setTradingReferencePrice( double val ) { _tradingReferencePrice = val; }

    @Override public final double getHighLimitPx() { return _highLimitPx; }
    @Override public final void setHighLimitPx( double val ) { _highLimitPx = val; }

    @Override public final double getLowLimitPx() { return _lowLimitPx; }
    @Override public final void setLowLimitPx( double val ) { _lowLimitPx = val; }

    @Override public final double getFutPointValue() { return _futPointValue; }
    @Override public final void setFutPointValue( double val ) { _futPointValue = val; }

    @Override public final double getMinPriceIncrement() { return _minPriceIncrement; }
    @Override public final void setMinPriceIncrement( double val ) { _minPriceIncrement = val; }

    @Override public final double getMinPriceIncrementAmount() { return _minPriceIncrementAmount; }
    @Override public final void setMinPriceIncrementAmount( double val ) { _minPriceIncrementAmount = val; }

    @Override public final ViewString getSecurityGroup() { return _securityGroup; }

    @Override public final void setSecurityGroup( byte[] buf, int offset, int len ) { _securityGroup.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityGroupForUpdate() { return _securityGroup; }

    @Override public final ViewString getSecurityDesc() { return _securityDesc; }

    @Override public final void setSecurityDesc( byte[] buf, int offset, int len ) { _securityDesc.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityDescForUpdate() { return _securityDesc; }

    @Override public final ViewString getSecurityLongDesc() { return _securityLongDesc; }

    @Override public final void setSecurityLongDesc( byte[] buf, int offset, int len ) { _securityLongDesc.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityLongDescForUpdate() { return _securityLongDesc; }

    @Override public final ViewString getCFICode() { return _CFICode; }

    @Override public final void setCFICode( byte[] buf, int offset, int len ) { _CFICode.setValue( buf, offset, len ); }
    @Override public final ReusableString getCFICodeForUpdate() { return _CFICode; }

    @Override public final ProductComplex getUnderlyingProduct() { return _underlyingProduct; }
    @Override public final void setUnderlyingProduct( ProductComplex val ) { _underlyingProduct = val; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final SecurityIDSource getUnderlyingSecurityIDSource() { return _underlyingSecurityIDSource; }
    @Override public final void setUnderlyingSecurityIDSource( SecurityIDSource val ) { _underlyingSecurityIDSource = val; }

    @Override public final ViewString getUnderlyingSecurityID() { return _underlyingSecurityID; }

    @Override public final void setUnderlyingSecurityID( byte[] buf, int offset, int len ) { _underlyingSecurityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getUnderlyingSecurityIDForUpdate() { return _underlyingSecurityID; }

    @Override public final ExchangeCode getUnderlyingScurityExchange() { return _underlyingScurityExchange; }
    @Override public final void setUnderlyingScurityExchange( ExchangeCode val ) { _underlyingScurityExchange = val; }

    @Override public final ExchangeCode getPrimarySecurityExchange() { return _primarySecurityExchange; }
    @Override public final void setPrimarySecurityExchange( ExchangeCode val ) { _primarySecurityExchange = val; }

    @Override public final int getTickRule() { return _tickRule; }
    @Override public final void setTickRule( int val ) { _tickRule = val; }

    @Override public final int getNoSecurityAltID() { return _noSecurityAltID; }
    @Override public final void setNoSecurityAltID( int val ) { _noSecurityAltID = val; }

    @Override public final SecurityAltID getSecurityAltIDs() { return _securityAltIDs; }
    @Override public final void setSecurityAltIDs( SecurityAltID val ) { _securityAltIDs = val; }

    @Override public final double getStrikePrice() { return _strikePrice; }
    @Override public final void setStrikePrice( double val ) { _strikePrice = val; }

    @Override public final Currency getStrikeCurrency() { return _strikeCurrency; }
    @Override public final void setStrikeCurrency( Currency val ) { _strikeCurrency = val; }

    @Override public final Currency getCurrency() { return _currency; }
    @Override public final void setCurrency( Currency val ) { _currency = val; }

    @Override public final Currency getSettlCurrency() { return _settlCurrency; }
    @Override public final void setSettlCurrency( Currency val ) { _settlCurrency = val; }

    @Override public final long getMinTradeVol() { return _minTradeVol; }
    @Override public final void setMinTradeVol( long val ) { _minTradeVol = val; }

    @Override public final long getMaxTradeVol() { return _maxTradeVol; }
    @Override public final void setMaxTradeVol( long val ) { _maxTradeVol = val; }

    @Override public final int getNoSDFeedTypes() { return _noSDFeedTypes; }
    @Override public final void setNoSDFeedTypes( int val ) { _noSDFeedTypes = val; }

    @Override public final SDFeedType getSDFeedTypes() { return _SDFeedTypes; }
    @Override public final void setSDFeedTypes( SDFeedType val ) { _SDFeedTypes = val; }

    @Override public final int getMaturityMonthYear() { return _maturityMonthYear; }
    @Override public final void setMaturityMonthYear( int val ) { _maturityMonthYear = val; }

    @Override public final ViewString getApplID() { return _applID; }

    @Override public final void setApplID( byte[] buf, int offset, int len ) { _applID.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplIDForUpdate() { return _applID; }

    @Override public final double getDisplayFactor() { return _displayFactor; }
    @Override public final void setDisplayFactor( double val ) { _displayFactor = val; }

    @Override public final double getPriceRatio() { return _priceRatio; }
    @Override public final void setPriceRatio( double val ) { _priceRatio = val; }

    @Override public final int getContractMultiplierType() { return _contractMultiplierType; }
    @Override public final void setContractMultiplierType( int val ) { _contractMultiplierType = val; }

    @Override public final double getContractMultiplier() { return _contractMultiplier; }
    @Override public final void setContractMultiplier( double val ) { _contractMultiplier = val; }

    @Override public final int getOpenInterestQty() { return _openInterestQty; }
    @Override public final void setOpenInterestQty( int val ) { _openInterestQty = val; }

    @Override public final int getTradingReferenceDate() { return _tradingReferenceDate; }
    @Override public final void setTradingReferenceDate( int val ) { _tradingReferenceDate = val; }

    @Override public final int getMinQty() { return _minQty; }
    @Override public final void setMinQty( int val ) { _minQty = val; }

    @Override public final double getPricePrecision() { return _pricePrecision; }
    @Override public final void setPricePrecision( double val ) { _pricePrecision = val; }

    @Override public final UnitOfMeasure getUnitOfMeasure() { return _unitOfMeasure; }
    @Override public final void setUnitOfMeasure( UnitOfMeasure val ) { _unitOfMeasure = val; }

    @Override public final double getUnitOfMeasureQty() { return _unitOfMeasureQty; }
    @Override public final void setUnitOfMeasureQty( double val ) { _unitOfMeasureQty = val; }

    @Override public final ViewString getCompanyName() { return _companyName; }

    @Override public final void setCompanyName( byte[] buf, int offset, int len ) { _companyName.setValue( buf, offset, len ); }
    @Override public final ReusableString getCompanyNameForUpdate() { return _companyName; }

    @Override public final double getSharesOutstanding() { return _sharesOutstanding; }
    @Override public final void setSharesOutstanding( double val ) { _sharesOutstanding = val; }

    @Override public final long getCommonSecurityId() { return _commonSecurityId; }
    @Override public final void setCommonSecurityId( long val ) { _commonSecurityId = val; }

    @Override public final long getParentCompanyId() { return _parentCompanyId; }
    @Override public final void setParentCompanyId( long val ) { _parentCompanyId = val; }

    @Override public final long getGicsCode() { return _gicsCode; }
    @Override public final void setGicsCode( long val ) { _gicsCode = val; }

    @Override public final long getGetOutDate() { return _getOutDate; }
    @Override public final void setGetOutDate( long val ) { _getOutDate = val; }

    @Override public final long getDeadTimestamp() { return _deadTimestamp; }
    @Override public final void setDeadTimestamp( long val ) { _deadTimestamp = val; }

    @Override public final long getStartTimestamp() { return _startTimestamp; }
    @Override public final void setStartTimestamp( long val ) { _startTimestamp = val; }

    @Override public final long getEndTimestamp() { return _endTimestamp; }
    @Override public final void setEndTimestamp( long val ) { _endTimestamp = val; }

    @Override public final DataSrc getDataSrc() { return _dataSrc; }
    @Override public final void setDataSrc( DataSrc val ) { _dataSrc = val; }

    @Override public final SecDefSpecialType getSecDefSpecialType() { return _secDefSpecialType; }
    @Override public final void setSecDefSpecialType( SecDefSpecialType val ) { _secDefSpecialType = val; }

    @Override public final CompanyStatusType getCompanyStatusType() { return _companyStatusType; }
    @Override public final void setCompanyStatusType( CompanyStatusType val ) { _companyStatusType = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _totNumReports = Constants.UNSET_INT;
        _securityTradingStatus = null;
        _securityType = null;
        _uniqueInstId = Constants.UNSET_LONG;
        _secDefId = Constants.UNSET_LONG;
        _exchangeLongId = Constants.UNSET_LONG;
        _securityIDSource = null;
        _securityID.reset();
        _symbol.reset();
        _noEvents = Constants.UNSET_INT;
        _events = null;
        _securityUpdateAction = null;
        _noLegs = Constants.UNSET_INT;
        _legs = null;
        _tradingReferencePrice = Constants.UNSET_DOUBLE;
        _highLimitPx = Constants.UNSET_DOUBLE;
        _lowLimitPx = Constants.UNSET_DOUBLE;
        _futPointValue = Constants.UNSET_DOUBLE;
        _minPriceIncrement = Constants.UNSET_DOUBLE;
        _minPriceIncrementAmount = Constants.UNSET_DOUBLE;
        _securityGroup.reset();
        _securityDesc.reset();
        _securityLongDesc.reset();
        _CFICode.reset();
        _underlyingProduct = null;
        _securityExchange = null;
        _underlyingSecurityIDSource = null;
        _underlyingSecurityID.reset();
        _underlyingScurityExchange = null;
        _primarySecurityExchange = null;
        _tickRule = Constants.UNSET_INT;
        _noSecurityAltID = Constants.UNSET_INT;
        _securityAltIDs = null;
        _strikePrice = Constants.UNSET_DOUBLE;
        _strikeCurrency = null;
        _currency = null;
        _settlCurrency = null;
        _minTradeVol = Constants.UNSET_LONG;
        _maxTradeVol = Constants.UNSET_LONG;
        _noSDFeedTypes = Constants.UNSET_INT;
        _SDFeedTypes = null;
        _maturityMonthYear = Constants.UNSET_INT;
        _applID.reset();
        _displayFactor = Constants.UNSET_DOUBLE;
        _priceRatio = Constants.UNSET_DOUBLE;
        _contractMultiplierType = Constants.UNSET_INT;
        _contractMultiplier = 1;
        _openInterestQty = Constants.UNSET_INT;
        _tradingReferenceDate = Constants.UNSET_INT;
        _minQty = 1;
        _pricePrecision = Constants.UNSET_DOUBLE;
        _unitOfMeasure = null;
        _unitOfMeasureQty = Constants.UNSET_DOUBLE;
        _companyName.reset();
        _sharesOutstanding = Constants.UNSET_DOUBLE;
        _commonSecurityId = Constants.UNSET_LONG;
        _parentCompanyId = Constants.UNSET_LONG;
        _gicsCode = Constants.UNSET_LONG;
        _getOutDate = Constants.UNSET_LONG;
        _deadTimestamp = Constants.UNSET_LONG;
        _startTimestamp = Constants.UNSET_LONG;
        _endTimestamp = Constants.UNSET_LONG;
        _dataSrc = DataSrc.UNS;
        _secDefSpecialType = SecDefSpecialType.Standard;
        _companyStatusType = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.SecurityDefinitionUpdate;
    }

    @Override
    public final SecurityDefinitionUpdateImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( SecurityDefinitionUpdateImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final void detachQueue() {
        _nextMessage = null;
    }

    @Override
    public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override
    public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override
    public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override
    public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }


   // Helper methods
    @Override
    public void setFlag( MsgFlag flag, boolean isOn ) {
        _flags = MsgFlag.setFlag( _flags, flag, isOn );
    }

    @Override
    public boolean isFlagSet( MsgFlag flag ) {
        return MsgFlag.isOn( _flags, flag );
    }

    @Override
    public int getFlags() {
        return _flags;
    }

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "SecurityDefinitionUpdateImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getTotNumReports() && 0 != getTotNumReports() )             out.append( ", totNumReports=" ).append( getTotNumReports() );
        if ( getSecurityTradingStatus() != null )             out.append( ", securityTradingStatus=" ).append( getSecurityTradingStatus() );
        if ( getSecurityType() != null )             out.append( ", securityType=" );
        if ( getSecurityType() != null ) out.append( getSecurityType().id() );
        if ( Constants.UNSET_LONG != getUniqueInstId() && 0 != getUniqueInstId() )             out.append( ", uniqueInstId=" ).append( getUniqueInstId() );
        if ( Constants.UNSET_LONG != getSecDefId() && 0 != getSecDefId() )             out.append( ", secDefId=" ).append( getSecDefId() );
        if ( Constants.UNSET_LONG != getExchangeLongId() && 0 != getExchangeLongId() )             out.append( ", exchangeLongId=" ).append( getExchangeLongId() );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getSecurityID().length() > 0 )             out.append( ", securityID=" ).append( getSecurityID() );
        if ( getSymbol().length() > 0 )             out.append( ", symbol=" ).append( getSymbol() );
        if ( Constants.UNSET_INT != getNoEvents() && 0 != getNoEvents() )             out.append( ", noEvents=" ).append( getNoEvents() );

        SecDefEventImpl tPtrevents = (SecDefEventImpl) getEvents();
        int tIdxevents=0;

        while( tPtrevents != null ) {
            out.append( " {#" ).append( ++tIdxevents ).append( "} " );
            tPtrevents.dump( out );
            tPtrevents = tPtrevents.getNext();
        }

        if ( getSecurityUpdateAction() != null )             out.append( ", securityUpdateAction=" ).append( getSecurityUpdateAction() );
        if ( Constants.UNSET_INT != getNoLegs() && 0 != getNoLegs() )             out.append( ", noLegs=" ).append( getNoLegs() );

        SecDefLegImpl tPtrlegs = (SecDefLegImpl) getLegs();
        int tIdxlegs=0;

        while( tPtrlegs != null ) {
            out.append( " {#" ).append( ++tIdxlegs ).append( "} " );
            tPtrlegs.dump( out );
            tPtrlegs = tPtrlegs.getNext();
        }

        if ( Utils.hasVal( getTradingReferencePrice() ) ) out.append( ", tradingReferencePrice=" ).append( getTradingReferencePrice() );
        if ( Utils.hasVal( getHighLimitPx() ) ) out.append( ", highLimitPx=" ).append( getHighLimitPx() );
        if ( Utils.hasVal( getLowLimitPx() ) ) out.append( ", lowLimitPx=" ).append( getLowLimitPx() );
        if ( Utils.hasVal( getFutPointValue() ) ) out.append( ", futPointValue=" ).append( getFutPointValue() );
        if ( Utils.hasVal( getMinPriceIncrement() ) ) out.append( ", minPriceIncrement=" ).append( getMinPriceIncrement() );
        if ( Utils.hasVal( getMinPriceIncrementAmount() ) ) out.append( ", minPriceIncrementAmount=" ).append( getMinPriceIncrementAmount() );
        if ( getSecurityGroup().length() > 0 )             out.append( ", securityGroup=" ).append( getSecurityGroup() );
        if ( getSecurityDesc().length() > 0 )             out.append( ", securityDesc=" ).append( getSecurityDesc() );
        if ( getSecurityLongDesc().length() > 0 )             out.append( ", securityLongDesc=" ).append( getSecurityLongDesc() );
        if ( getCFICode().length() > 0 )             out.append( ", CFICode=" ).append( getCFICode() );
        if ( getUnderlyingProduct() != null )             out.append( ", underlyingProduct=" ).append( getUnderlyingProduct() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getUnderlyingSecurityIDSource() != null )             out.append( ", underlyingSecurityIDSource=" );
        if ( getUnderlyingSecurityIDSource() != null ) out.append( getUnderlyingSecurityIDSource().id() );
        if ( getUnderlyingSecurityID().length() > 0 )             out.append( ", underlyingSecurityID=" ).append( getUnderlyingSecurityID() );
        if ( getUnderlyingScurityExchange() != null )             out.append( ", underlyingScurityExchange=" );
        if ( getUnderlyingScurityExchange() != null ) out.append( getUnderlyingScurityExchange().id() );
        if ( getPrimarySecurityExchange() != null )             out.append( ", primarySecurityExchange=" );
        if ( getPrimarySecurityExchange() != null ) out.append( getPrimarySecurityExchange().id() );
        if ( Constants.UNSET_INT != getTickRule() && 0 != getTickRule() )             out.append( ", tickRule=" ).append( getTickRule() );
        if ( Constants.UNSET_INT != getNoSecurityAltID() && 0 != getNoSecurityAltID() )             out.append( ", noSecurityAltID=" ).append( getNoSecurityAltID() );

        SecurityAltIDImpl tPtrsecurityAltIDs = (SecurityAltIDImpl) getSecurityAltIDs();
        int tIdxsecurityAltIDs=0;

        while( tPtrsecurityAltIDs != null ) {
            out.append( " {#" ).append( ++tIdxsecurityAltIDs ).append( "} " );
            tPtrsecurityAltIDs.dump( out );
            tPtrsecurityAltIDs = tPtrsecurityAltIDs.getNext();
        }

        if ( Utils.hasVal( getStrikePrice() ) ) out.append( ", strikePrice=" ).append( getStrikePrice() );
        if ( getStrikeCurrency() != null )             out.append( ", strikeCurrency=" );
        if ( getStrikeCurrency() != null ) out.append( getStrikeCurrency().id() );
        if ( getCurrency() != null )             out.append( ", currency=" );
        if ( getCurrency() != null ) out.append( getCurrency().id() );
        if ( getSettlCurrency() != null )             out.append( ", settlCurrency=" );
        if ( getSettlCurrency() != null ) out.append( getSettlCurrency().id() );
        if ( Constants.UNSET_LONG != getMinTradeVol() && 0 != getMinTradeVol() )             out.append( ", minTradeVol=" ).append( getMinTradeVol() );
        if ( Constants.UNSET_LONG != getMaxTradeVol() && 0 != getMaxTradeVol() )             out.append( ", maxTradeVol=" ).append( getMaxTradeVol() );
        if ( Constants.UNSET_INT != getNoSDFeedTypes() && 0 != getNoSDFeedTypes() )             out.append( ", noSDFeedTypes=" ).append( getNoSDFeedTypes() );

        SDFeedTypeImpl tPtrSDFeedTypes = (SDFeedTypeImpl) getSDFeedTypes();
        int tIdxSDFeedTypes=0;

        while( tPtrSDFeedTypes != null ) {
            out.append( " {#" ).append( ++tIdxSDFeedTypes ).append( "} " );
            tPtrSDFeedTypes.dump( out );
            tPtrSDFeedTypes = tPtrSDFeedTypes.getNext();
        }

        if ( Constants.UNSET_INT != getMaturityMonthYear() && 0 != getMaturityMonthYear() )             out.append( ", maturityMonthYear=" ).append( getMaturityMonthYear() );
        if ( getApplID().length() > 0 )             out.append( ", applID=" ).append( getApplID() );
        if ( Utils.hasVal( getDisplayFactor() ) ) out.append( ", displayFactor=" ).append( getDisplayFactor() );
        if ( Utils.hasVal( getPriceRatio() ) ) out.append( ", priceRatio=" ).append( getPriceRatio() );
        if ( Constants.UNSET_INT != getContractMultiplierType() && 0 != getContractMultiplierType() )             out.append( ", contractMultiplierType=" ).append( getContractMultiplierType() );
        if ( Utils.hasVal( getContractMultiplier() ) ) out.append( ", contractMultiplier=" ).append( getContractMultiplier() );
        if ( Constants.UNSET_INT != getOpenInterestQty() && 0 != getOpenInterestQty() )             out.append( ", openInterestQty=" ).append( getOpenInterestQty() );
        if ( Constants.UNSET_INT != getTradingReferenceDate() && 0 != getTradingReferenceDate() )             out.append( ", tradingReferenceDate=" ).append( getTradingReferenceDate() );
        if ( Constants.UNSET_INT != getMinQty() && 0 != getMinQty() )             out.append( ", minQty=" ).append( getMinQty() );
        if ( Utils.hasVal( getPricePrecision() ) ) out.append( ", pricePrecision=" ).append( getPricePrecision() );
        if ( getUnitOfMeasure() != null )             out.append( ", unitOfMeasure=" );
        if ( getUnitOfMeasure() != null ) out.append( getUnitOfMeasure().id() );
        if ( Utils.hasVal( getUnitOfMeasureQty() ) ) out.append( ", unitOfMeasureQty=" ).append( getUnitOfMeasureQty() );
        if ( getCompanyName().length() > 0 )             out.append( ", companyName=" ).append( getCompanyName() );
        if ( Utils.hasVal( getSharesOutstanding() ) ) out.append( ", sharesOutstanding=" ).append( getSharesOutstanding() );
        if ( Constants.UNSET_LONG != getCommonSecurityId() && 0 != getCommonSecurityId() )             out.append( ", commonSecurityId=" ).append( getCommonSecurityId() );
        if ( Constants.UNSET_LONG != getParentCompanyId() && 0 != getParentCompanyId() )             out.append( ", parentCompanyId=" ).append( getParentCompanyId() );
        if ( Constants.UNSET_LONG != getGicsCode() && 0 != getGicsCode() )             out.append( ", gicsCode=" ).append( getGicsCode() );
        if ( Constants.UNSET_LONG != getGetOutDate() && 0 != getGetOutDate() ) {
            out.append( ", getOutDate=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getGetOutDate() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getGetOutDate() );
            out.append( " ( " );
            out.append( getGetOutDate() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getDeadTimestamp() && 0 != getDeadTimestamp() ) {
            out.append( ", deadTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getDeadTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getDeadTimestamp() );
            out.append( " ( " );
            out.append( getDeadTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getStartTimestamp() && 0 != getStartTimestamp() ) {
            out.append( ", startTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getStartTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getStartTimestamp() );
            out.append( " ( " );
            out.append( getStartTimestamp() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getEndTimestamp() && 0 != getEndTimestamp() ) {
            out.append( ", endTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEndTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEndTimestamp() );
            out.append( " ( " );
            out.append( getEndTimestamp() ).append( " ) " );
        }
        if ( getDataSrc() != null )             out.append( ", dataSrc=" );
        if ( getDataSrc() != null ) out.append( getDataSrc().id() );
        if ( getSecDefSpecialType() != null )             out.append( ", secDefSpecialType=" );
        if ( getSecDefSpecialType() != null ) out.append( getSecDefSpecialType().id() );
        if ( getCompanyStatusType() != null )             out.append( ", companyStatusType=" );
        if ( getCompanyStatusType() != null ) out.append( getCompanyStatusType().id() );
        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", possDupFlag=" ).append( getPossDupFlag() );
        if ( Constants.UNSET_LONG != getEventTimestamp() && 0 != getEventTimestamp() ) {
            out.append( ", eventTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEventTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEventTimestamp() );
            out.append( " ( " );
            out.append( getEventTimestamp() ).append( " ) " );
        }
    }

    @Override public final void snapTo( SecurityDefinitionUpdate dest ) {
        ((SecurityDefinitionUpdateImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( SecurityDefinitionUpdate src ) {
        setTotNumReports( src.getTotNumReports() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setSecurityType( src.getSecurityType() );
        setUniqueInstId( src.getUniqueInstId() );
        setSecDefId( src.getSecDefId() );
        setExchangeLongId( src.getExchangeLongId() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setNoEvents( src.getNoEvents() );
        SecDefEventImpl tSrcPtrEvents = (SecDefEventImpl) src.getEvents();
        SecDefEventImpl tNewPtrEvents = null;
        while( tSrcPtrEvents != null ) {
            if ( tNewPtrEvents == null ) {
                tNewPtrEvents = new SecDefEventImpl();
                setEvents( tNewPtrEvents );
            } else {
                tNewPtrEvents.setNext( new SecDefEventImpl() );
                tNewPtrEvents = tNewPtrEvents.getNext();
            }
            tNewPtrEvents.deepCopyFrom( tSrcPtrEvents );
            tSrcPtrEvents = tSrcPtrEvents.getNext();
        }
        setSecurityUpdateAction( src.getSecurityUpdateAction() );
        setNoLegs( src.getNoLegs() );
        SecDefLegImpl tSrcPtrLegs = (SecDefLegImpl) src.getLegs();
        SecDefLegImpl tNewPtrLegs = null;
        while( tSrcPtrLegs != null ) {
            if ( tNewPtrLegs == null ) {
                tNewPtrLegs = new SecDefLegImpl();
                setLegs( tNewPtrLegs );
            } else {
                tNewPtrLegs.setNext( new SecDefLegImpl() );
                tNewPtrLegs = tNewPtrLegs.getNext();
            }
            tNewPtrLegs.deepCopyFrom( tSrcPtrLegs );
            tSrcPtrLegs = tSrcPtrLegs.getNext();
        }
        setTradingReferencePrice( src.getTradingReferencePrice() );
        setHighLimitPx( src.getHighLimitPx() );
        setLowLimitPx( src.getLowLimitPx() );
        setFutPointValue( src.getFutPointValue() );
        setMinPriceIncrement( src.getMinPriceIncrement() );
        setMinPriceIncrementAmount( src.getMinPriceIncrementAmount() );
        getSecurityGroupForUpdate().copy( src.getSecurityGroup() );
        getSecurityDescForUpdate().copy( src.getSecurityDesc() );
        getSecurityLongDescForUpdate().copy( src.getSecurityLongDesc() );
        getCFICodeForUpdate().copy( src.getCFICode() );
        setUnderlyingProduct( src.getUnderlyingProduct() );
        setSecurityExchange( src.getSecurityExchange() );
        setUnderlyingSecurityIDSource( src.getUnderlyingSecurityIDSource() );
        getUnderlyingSecurityIDForUpdate().copy( src.getUnderlyingSecurityID() );
        setUnderlyingScurityExchange( src.getUnderlyingScurityExchange() );
        setPrimarySecurityExchange( src.getPrimarySecurityExchange() );
        setTickRule( src.getTickRule() );
        setNoSecurityAltID( src.getNoSecurityAltID() );
        SecurityAltIDImpl tSrcPtrSecurityAltIDs = (SecurityAltIDImpl) src.getSecurityAltIDs();
        SecurityAltIDImpl tNewPtrSecurityAltIDs = null;
        while( tSrcPtrSecurityAltIDs != null ) {
            if ( tNewPtrSecurityAltIDs == null ) {
                tNewPtrSecurityAltIDs = new SecurityAltIDImpl();
                setSecurityAltIDs( tNewPtrSecurityAltIDs );
            } else {
                tNewPtrSecurityAltIDs.setNext( new SecurityAltIDImpl() );
                tNewPtrSecurityAltIDs = tNewPtrSecurityAltIDs.getNext();
            }
            tNewPtrSecurityAltIDs.deepCopyFrom( tSrcPtrSecurityAltIDs );
            tSrcPtrSecurityAltIDs = tSrcPtrSecurityAltIDs.getNext();
        }
        setStrikePrice( src.getStrikePrice() );
        setStrikeCurrency( src.getStrikeCurrency() );
        setCurrency( src.getCurrency() );
        setSettlCurrency( src.getSettlCurrency() );
        setMinTradeVol( src.getMinTradeVol() );
        setMaxTradeVol( src.getMaxTradeVol() );
        setNoSDFeedTypes( src.getNoSDFeedTypes() );
        SDFeedTypeImpl tSrcPtrSDFeedTypes = (SDFeedTypeImpl) src.getSDFeedTypes();
        SDFeedTypeImpl tNewPtrSDFeedTypes = null;
        while( tSrcPtrSDFeedTypes != null ) {
            if ( tNewPtrSDFeedTypes == null ) {
                tNewPtrSDFeedTypes = new SDFeedTypeImpl();
                setSDFeedTypes( tNewPtrSDFeedTypes );
            } else {
                tNewPtrSDFeedTypes.setNext( new SDFeedTypeImpl() );
                tNewPtrSDFeedTypes = tNewPtrSDFeedTypes.getNext();
            }
            tNewPtrSDFeedTypes.deepCopyFrom( tSrcPtrSDFeedTypes );
            tSrcPtrSDFeedTypes = tSrcPtrSDFeedTypes.getNext();
        }
        setMaturityMonthYear( src.getMaturityMonthYear() );
        getApplIDForUpdate().copy( src.getApplID() );
        setDisplayFactor( src.getDisplayFactor() );
        setPriceRatio( src.getPriceRatio() );
        setContractMultiplierType( src.getContractMultiplierType() );
        setContractMultiplier( src.getContractMultiplier() );
        setOpenInterestQty( src.getOpenInterestQty() );
        setTradingReferenceDate( src.getTradingReferenceDate() );
        setMinQty( src.getMinQty() );
        setPricePrecision( src.getPricePrecision() );
        setUnitOfMeasure( src.getUnitOfMeasure() );
        setUnitOfMeasureQty( src.getUnitOfMeasureQty() );
        getCompanyNameForUpdate().copy( src.getCompanyName() );
        setSharesOutstanding( src.getSharesOutstanding() );
        setCommonSecurityId( src.getCommonSecurityId() );
        setParentCompanyId( src.getParentCompanyId() );
        setGicsCode( src.getGicsCode() );
        setGetOutDate( src.getGetOutDate() );
        setDeadTimestamp( src.getDeadTimestamp() );
        setStartTimestamp( src.getStartTimestamp() );
        setEndTimestamp( src.getEndTimestamp() );
        setDataSrc( src.getDataSrc() );
        setSecDefSpecialType( src.getSecDefSpecialType() );
        setCompanyStatusType( src.getCompanyStatusType() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( SecurityDefinitionUpdate src ) {
        setTotNumReports( src.getTotNumReports() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        setSecurityType( src.getSecurityType() );
        setUniqueInstId( src.getUniqueInstId() );
        setSecDefId( src.getSecDefId() );
        setExchangeLongId( src.getExchangeLongId() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setSecurityUpdateAction( src.getSecurityUpdateAction() );
        setTradingReferencePrice( src.getTradingReferencePrice() );
        setHighLimitPx( src.getHighLimitPx() );
        setLowLimitPx( src.getLowLimitPx() );
        setFutPointValue( src.getFutPointValue() );
        setMinPriceIncrement( src.getMinPriceIncrement() );
        setMinPriceIncrementAmount( src.getMinPriceIncrementAmount() );
        getSecurityGroupForUpdate().copy( src.getSecurityGroup() );
        getSecurityDescForUpdate().copy( src.getSecurityDesc() );
        getSecurityLongDescForUpdate().copy( src.getSecurityLongDesc() );
        getCFICodeForUpdate().copy( src.getCFICode() );
        setUnderlyingProduct( src.getUnderlyingProduct() );
        setSecurityExchange( src.getSecurityExchange() );
        setUnderlyingSecurityIDSource( src.getUnderlyingSecurityIDSource() );
        getUnderlyingSecurityIDForUpdate().copy( src.getUnderlyingSecurityID() );
        setUnderlyingScurityExchange( src.getUnderlyingScurityExchange() );
        setPrimarySecurityExchange( src.getPrimarySecurityExchange() );
        setTickRule( src.getTickRule() );
        setStrikePrice( src.getStrikePrice() );
        setStrikeCurrency( src.getStrikeCurrency() );
        setCurrency( src.getCurrency() );
        setSettlCurrency( src.getSettlCurrency() );
        setMinTradeVol( src.getMinTradeVol() );
        setMaxTradeVol( src.getMaxTradeVol() );
        setMaturityMonthYear( src.getMaturityMonthYear() );
        getApplIDForUpdate().copy( src.getApplID() );
        setDisplayFactor( src.getDisplayFactor() );
        setPriceRatio( src.getPriceRatio() );
        setContractMultiplierType( src.getContractMultiplierType() );
        setContractMultiplier( src.getContractMultiplier() );
        setOpenInterestQty( src.getOpenInterestQty() );
        setTradingReferenceDate( src.getTradingReferenceDate() );
        setMinQty( src.getMinQty() );
        setPricePrecision( src.getPricePrecision() );
        setUnitOfMeasure( src.getUnitOfMeasure() );
        setUnitOfMeasureQty( src.getUnitOfMeasureQty() );
        getCompanyNameForUpdate().copy( src.getCompanyName() );
        setSharesOutstanding( src.getSharesOutstanding() );
        setCommonSecurityId( src.getCommonSecurityId() );
        setParentCompanyId( src.getParentCompanyId() );
        setGicsCode( src.getGicsCode() );
        setGetOutDate( src.getGetOutDate() );
        setDeadTimestamp( src.getDeadTimestamp() );
        setStartTimestamp( src.getStartTimestamp() );
        setEndTimestamp( src.getEndTimestamp() );
        setDataSrc( src.getDataSrc() );
        setSecDefSpecialType( src.getSecDefSpecialType() );
        setCompanyStatusType( src.getCompanyStatusType() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( SecurityDefinitionUpdate src ) {
        if ( Constants.UNSET_INT != src.getTotNumReports() ) setTotNumReports( src.getTotNumReports() );
        setSecurityTradingStatus( src.getSecurityTradingStatus() );
        if ( getSecurityType() != null )  setSecurityType( src.getSecurityType() );
        if ( Constants.UNSET_LONG != src.getUniqueInstId() ) setUniqueInstId( src.getUniqueInstId() );
        if ( Constants.UNSET_LONG != src.getSecDefId() ) setSecDefId( src.getSecDefId() );
        if ( Constants.UNSET_LONG != src.getExchangeLongId() ) setExchangeLongId( src.getExchangeLongId() );
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( src.getSecurityID().length() > 0 ) getSecurityIDForUpdate().copy( src.getSecurityID() );
        if ( src.getSymbol().length() > 0 ) getSymbolForUpdate().copy( src.getSymbol() );
        setSecurityUpdateAction( src.getSecurityUpdateAction() );
        if ( Utils.hasVal( src.getTradingReferencePrice() ) ) setTradingReferencePrice( src.getTradingReferencePrice() );
        if ( Utils.hasVal( src.getHighLimitPx() ) ) setHighLimitPx( src.getHighLimitPx() );
        if ( Utils.hasVal( src.getLowLimitPx() ) ) setLowLimitPx( src.getLowLimitPx() );
        if ( Utils.hasVal( src.getFutPointValue() ) ) setFutPointValue( src.getFutPointValue() );
        if ( Utils.hasVal( src.getMinPriceIncrement() ) ) setMinPriceIncrement( src.getMinPriceIncrement() );
        if ( Utils.hasVal( src.getMinPriceIncrementAmount() ) ) setMinPriceIncrementAmount( src.getMinPriceIncrementAmount() );
        if ( src.getSecurityGroup().length() > 0 ) getSecurityGroupForUpdate().copy( src.getSecurityGroup() );
        if ( src.getSecurityDesc().length() > 0 ) getSecurityDescForUpdate().copy( src.getSecurityDesc() );
        if ( src.getSecurityLongDesc().length() > 0 ) getSecurityLongDescForUpdate().copy( src.getSecurityLongDesc() );
        if ( src.getCFICode().length() > 0 ) getCFICodeForUpdate().copy( src.getCFICode() );
        setUnderlyingProduct( src.getUnderlyingProduct() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( getUnderlyingSecurityIDSource() != null )  setUnderlyingSecurityIDSource( src.getUnderlyingSecurityIDSource() );
        if ( src.getUnderlyingSecurityID().length() > 0 ) getUnderlyingSecurityIDForUpdate().copy( src.getUnderlyingSecurityID() );
        if ( getUnderlyingScurityExchange() != null )  setUnderlyingScurityExchange( src.getUnderlyingScurityExchange() );
        if ( getPrimarySecurityExchange() != null )  setPrimarySecurityExchange( src.getPrimarySecurityExchange() );
        if ( Constants.UNSET_INT != src.getTickRule() ) setTickRule( src.getTickRule() );
        if ( Utils.hasVal( src.getStrikePrice() ) ) setStrikePrice( src.getStrikePrice() );
        if ( getStrikeCurrency() != null )  setStrikeCurrency( src.getStrikeCurrency() );
        if ( getCurrency() != null )  setCurrency( src.getCurrency() );
        if ( getSettlCurrency() != null )  setSettlCurrency( src.getSettlCurrency() );
        if ( Constants.UNSET_LONG != src.getMinTradeVol() ) setMinTradeVol( src.getMinTradeVol() );
        if ( Constants.UNSET_LONG != src.getMaxTradeVol() ) setMaxTradeVol( src.getMaxTradeVol() );
        if ( Constants.UNSET_INT != src.getMaturityMonthYear() ) setMaturityMonthYear( src.getMaturityMonthYear() );
        if ( src.getApplID().length() > 0 ) getApplIDForUpdate().copy( src.getApplID() );
        if ( Utils.hasVal( src.getDisplayFactor() ) ) setDisplayFactor( src.getDisplayFactor() );
        if ( Utils.hasVal( src.getPriceRatio() ) ) setPriceRatio( src.getPriceRatio() );
        if ( Constants.UNSET_INT != src.getContractMultiplierType() ) setContractMultiplierType( src.getContractMultiplierType() );
        if ( Utils.hasVal( src.getContractMultiplier() ) ) setContractMultiplier( src.getContractMultiplier() );
        if ( Constants.UNSET_INT != src.getOpenInterestQty() ) setOpenInterestQty( src.getOpenInterestQty() );
        if ( Constants.UNSET_INT != src.getTradingReferenceDate() ) setTradingReferenceDate( src.getTradingReferenceDate() );
        if ( Constants.UNSET_INT != src.getMinQty() ) setMinQty( src.getMinQty() );
        if ( Utils.hasVal( src.getPricePrecision() ) ) setPricePrecision( src.getPricePrecision() );
        if ( getUnitOfMeasure() != null )  setUnitOfMeasure( src.getUnitOfMeasure() );
        if ( Utils.hasVal( src.getUnitOfMeasureQty() ) ) setUnitOfMeasureQty( src.getUnitOfMeasureQty() );
        if ( src.getCompanyName().length() > 0 ) getCompanyNameForUpdate().copy( src.getCompanyName() );
        if ( Utils.hasVal( src.getSharesOutstanding() ) ) setSharesOutstanding( src.getSharesOutstanding() );
        if ( Constants.UNSET_LONG != src.getCommonSecurityId() ) setCommonSecurityId( src.getCommonSecurityId() );
        if ( Constants.UNSET_LONG != src.getParentCompanyId() ) setParentCompanyId( src.getParentCompanyId() );
        if ( Constants.UNSET_LONG != src.getGicsCode() ) setGicsCode( src.getGicsCode() );
        if ( Constants.UNSET_LONG != src.getGetOutDate() ) setGetOutDate( src.getGetOutDate() );
        if ( Constants.UNSET_LONG != src.getDeadTimestamp() ) setDeadTimestamp( src.getDeadTimestamp() );
        if ( Constants.UNSET_LONG != src.getStartTimestamp() ) setStartTimestamp( src.getStartTimestamp() );
        if ( Constants.UNSET_LONG != src.getEndTimestamp() ) setEndTimestamp( src.getEndTimestamp() );
        if ( getDataSrc() != null )  setDataSrc( src.getDataSrc() );
        if ( getSecDefSpecialType() != null )  setSecDefSpecialType( src.getSecDefSpecialType() );
        if ( getCompanyStatusType() != null )  setCompanyStatusType( src.getCompanyStatusType() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
