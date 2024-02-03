package com.rr.model.generated.internal.events.factory;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.recycle.*;
import com.rr.model.generated.internal.core.FullEventIds;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.HasReusableType;

public class AllEventRecycler implements EventRecycler {


    private HeartbeatRecycler _heartbeatRecycler;
    private EndOfSessionRecycler _endOfSessionRecycler;
    private LogoutRequestRecycler _logoutRequestRecycler;
    private TestRequestRecycler _testRequestRecycler;
    private LogonRecycler _logonRecycler;
    private LogoutRecycler _logoutRecycler;
    private SessionRejectRecycler _sessionRejectRecycler;
    private ResendRequestRecycler _resendRequestRecycler;
    private ClientResyncSentMsgsRecycler _clientResyncSentMsgsRecycler;
    private SequenceResetRecycler _sequenceResetRecycler;
    private TradingSessionStatusRecycler _tradingSessionStatusRecycler;
    private SecMassStatGrpRecycler _secMassStatGrpRecycler;
    private MassInstrumentStateChangeRecycler _massInstrumentStateChangeRecycler;
    private ClientNewOrderSingleRecycler _clientNewOrderSingleRecycler;
    private MarketNewOrderSingleRecycler _marketNewOrderSingleRecycler;

    private NewOrderSingleRecycler _newOrderSingleRecycler;

    private ClientCancelReplaceRequestRecycler _clientCancelReplaceRequestRecycler;
    private MarketCancelReplaceRequestRecycler _marketCancelReplaceRequestRecycler;

    private CancelReplaceRequestRecycler _cancelReplaceRequestRecycler;

    private ClientCancelRequestRecycler _clientCancelRequestRecycler;
    private MarketCancelRequestRecycler _marketCancelRequestRecycler;

    private CancelRequestRecycler _cancelRequestRecycler;

    private ClientForceCancelRecycler _clientForceCancelRecycler;
    private MarketForceCancelRecycler _marketForceCancelRecycler;

    private ForceCancelRecycler _forceCancelRecycler;

    private ClientVagueOrderRejectRecycler _clientVagueOrderRejectRecycler;
    private MarketVagueOrderRejectRecycler _marketVagueOrderRejectRecycler;

    private VagueOrderRejectRecycler _vagueOrderRejectRecycler;

    private ClientCancelRejectRecycler _clientCancelRejectRecycler;
    private MarketCancelRejectRecycler _marketCancelRejectRecycler;

    private CancelRejectRecycler _cancelRejectRecycler;

    private ClientNewOrderAckRecycler _clientNewOrderAckRecycler;
    private MarketNewOrderAckRecycler _marketNewOrderAckRecycler;

    private NewOrderAckRecycler _newOrderAckRecycler;

    private ClientTradeNewRecycler _clientTradeNewRecycler;
    private MarketTradeNewRecycler _marketTradeNewRecycler;

    private TradeNewRecycler _tradeNewRecycler;

    private ClientRejectedRecycler _clientRejectedRecycler;
    private MarketRejectedRecycler _marketRejectedRecycler;

    private RejectedRecycler _rejectedRecycler;

    private ClientCancelledRecycler _clientCancelledRecycler;
    private MarketCancelledRecycler _marketCancelledRecycler;

    private CancelledRecycler _cancelledRecycler;

    private ClientReplacedRecycler _clientReplacedRecycler;
    private MarketReplacedRecycler _marketReplacedRecycler;

    private ReplacedRecycler _replacedRecycler;

    private ClientDoneForDayRecycler _clientDoneForDayRecycler;
    private MarketDoneForDayRecycler _marketDoneForDayRecycler;

    private DoneForDayRecycler _doneForDayRecycler;

    private ClientStoppedRecycler _clientStoppedRecycler;
    private MarketStoppedRecycler _marketStoppedRecycler;

    private StoppedRecycler _stoppedRecycler;

    private ClientExpiredRecycler _clientExpiredRecycler;
    private MarketExpiredRecycler _marketExpiredRecycler;

    private ExpiredRecycler _expiredRecycler;

    private ClientSuspendedRecycler _clientSuspendedRecycler;
    private MarketSuspendedRecycler _marketSuspendedRecycler;

    private SuspendedRecycler _suspendedRecycler;

    private ClientRestatedRecycler _clientRestatedRecycler;
    private MarketRestatedRecycler _marketRestatedRecycler;

    private RestatedRecycler _restatedRecycler;

    private ClientTradeCorrectRecycler _clientTradeCorrectRecycler;
    private MarketTradeCorrectRecycler _marketTradeCorrectRecycler;

    private TradeCorrectRecycler _tradeCorrectRecycler;

    private ClientTradeCancelRecycler _clientTradeCancelRecycler;
    private MarketTradeCancelRecycler _marketTradeCancelRecycler;

    private TradeCancelRecycler _tradeCancelRecycler;

    private ClientPendingCancelRecycler _clientPendingCancelRecycler;
    private MarketPendingCancelRecycler _marketPendingCancelRecycler;

    private PendingCancelRecycler _pendingCancelRecycler;

    private ClientPendingReplaceRecycler _clientPendingReplaceRecycler;
    private MarketPendingReplaceRecycler _marketPendingReplaceRecycler;

    private PendingReplaceRecycler _pendingReplaceRecycler;

    private ClientPendingNewRecycler _clientPendingNewRecycler;
    private MarketPendingNewRecycler _marketPendingNewRecycler;

    private PendingNewRecycler _pendingNewRecycler;

    private ClientOrderStatusRecycler _clientOrderStatusRecycler;
    private MarketOrderStatusRecycler _marketOrderStatusRecycler;

    private OrderStatusRecycler _orderStatusRecycler;

    private ClientIgnoredExecRecycler _clientIgnoredExecRecycler;
    private MarketIgnoredExecRecycler _marketIgnoredExecRecycler;

    private IgnoredExecRecycler _ignoredExecRecycler;

    private ClientCalculatedRecycler _clientCalculatedRecycler;
    private MarketCalculatedRecycler _marketCalculatedRecycler;

    private CalculatedRecycler _calculatedRecycler;

    private ClientAlertLimitBreachRecycler _clientAlertLimitBreachRecycler;
    private MarketAlertLimitBreachRecycler _marketAlertLimitBreachRecycler;

    private AlertLimitBreachRecycler _alertLimitBreachRecycler;

    private ClientAlertTradeMissingOrdersRecycler _clientAlertTradeMissingOrdersRecycler;
    private MarketAlertTradeMissingOrdersRecycler _marketAlertTradeMissingOrdersRecycler;

    private AlertTradeMissingOrdersRecycler _alertTradeMissingOrdersRecycler;

    private SymbolRepeatingGrpRecycler _symbolRepeatingGrpRecycler;
    private MDRequestRecycler _mDRequestRecycler;
    private TickUpdateRecycler _tickUpdateRecycler;
    private MDUpdateRecycler _mDUpdateRecycler;
    private SecDefEventRecycler _secDefEventRecycler;
    private SecurityAltIDRecycler _securityAltIDRecycler;
    private SDFeedTypeRecycler _sDFeedTypeRecycler;
    private SecDefLegRecycler _secDefLegRecycler;
    private MDEntryRecycler _mDEntryRecycler;
    private MDSnapEntryRecycler _mDSnapEntryRecycler;
    private MsgSeqNumGapRecycler _msgSeqNumGapRecycler;
    private MDIncRefreshRecycler _mDIncRefreshRecycler;
    private MDSnapshotFullRefreshRecycler _mDSnapshotFullRefreshRecycler;
    private SecurityDefinitionRecycler _securityDefinitionRecycler;
    private SecurityDefinitionUpdateRecycler _securityDefinitionUpdateRecycler;
    private ProductSnapshotRecycler _productSnapshotRecycler;
    private SecurityStatusRecycler _securityStatusRecycler;
    private SettlementPriceEventRecycler _settlementPriceEventRecycler;
    private ClosingPriceEventRecycler _closingPriceEventRecycler;
    private OpenPriceEventRecycler _openPriceEventRecycler;
    private OpenInterestEventRecycler _openInterestEventRecycler;
    private NewsRecycler _newsRecycler;
    private CorporateActionEventRecycler _corporateActionEventRecycler;
    private InstrumentSimDataRecycler _instrumentSimDataRecycler;
    private RefPriceEventRecycler _refPriceEventRecycler;
    private BrokerLoanResponseRecycler _brokerLoanResponseRecycler;
    private PriceLimitCollarEventRecycler _priceLimitCollarEventRecycler;
    private SecurityTradingStatusEventRecycler _securityTradingStatusEventRecycler;
    private LeanHogIndexEventRecycler _leanHogIndexEventRecycler;
    private ForceFlattenCommandRecycler _forceFlattenCommandRecycler;
    private AppRunRecycler _appRunRecycler;
    private StratInstrumentRecycler _stratInstrumentRecycler;
    private StrategyRunRecycler _strategyRunRecycler;
    private StratInstrumentStateRecycler _stratInstrumentStateRecycler;
    private StrategyStateRecycler _strategyStateRecycler;
    private UTPLogonRecycler _uTPLogonRecycler;
    private UTPLogonRejectRecycler _uTPLogonRejectRecycler;
    private UTPTradingSessionStatusRecycler _uTPTradingSessionStatusRecycler;
    private ETIConnectionGatewayRequestRecycler _eTIConnectionGatewayRequestRecycler;
    private ETIConnectionGatewayResponseRecycler _eTIConnectionGatewayResponseRecycler;
    private ETISessionLogonRequestRecycler _eTISessionLogonRequestRecycler;
    private ETISessionLogonResponseRecycler _eTISessionLogonResponseRecycler;
    private ETISessionLogoutRequestRecycler _eTISessionLogoutRequestRecycler;
    private ETISessionLogoutResponseRecycler _eTISessionLogoutResponseRecycler;
    private ETISessionLogoutNotificationRecycler _eTISessionLogoutNotificationRecycler;
    private ETIUserLogonRequestRecycler _eTIUserLogonRequestRecycler;
    private ETIUserLogonResponseRecycler _eTIUserLogonResponseRecycler;
    private ETIUserLogoutRequestRecycler _eTIUserLogoutRequestRecycler;
    private ETIUserLogoutResponseRecycler _eTIUserLogoutResponseRecycler;
    private ETIThrottleUpdateNotificationRecycler _eTIThrottleUpdateNotificationRecycler;
    private ETISubscribeRecycler _eTISubscribeRecycler;
    private ETISubscribeResponseRecycler _eTISubscribeResponseRecycler;
    private ETIUnsubscribeRecycler _eTIUnsubscribeRecycler;
    private ETIUnsubscribeResponseRecycler _eTIUnsubscribeResponseRecycler;
    private ETIRetransmitRecycler _eTIRetransmitRecycler;
    private ETIRetransmitResponseRecycler _eTIRetransmitResponseRecycler;
    private ETIRetransmitOrderEventsRecycler _eTIRetransmitOrderEventsRecycler;
    private ETIRetransmitOrderEventsResponseRecycler _eTIRetransmitOrderEventsResponseRecycler;
    private MilleniumLogonRecycler _milleniumLogonRecycler;
    private MilleniumLogonReplyRecycler _milleniumLogonReplyRecycler;
    private MilleniumLogoutRecycler _milleniumLogoutRecycler;
    private MilleniumMissedMessageRequestRecycler _milleniumMissedMessageRequestRecycler;
    private MilleniumMissedMsgRequestAckRecycler _milleniumMissedMsgRequestAckRecycler;
    private MilleniumMissedMsgReportRecycler _milleniumMissedMsgReportRecycler;
    private BookAddOrderRecycler _bookAddOrderRecycler;
    private BookDeleteOrderRecycler _bookDeleteOrderRecycler;
    private BookModifyOrderRecycler _bookModifyOrderRecycler;
    private BookClearRecycler _bookClearRecycler;
    private PitchSymbolClearRecycler _pitchSymbolClearRecycler;
    private PitchBookAddOrderRecycler _pitchBookAddOrderRecycler;
    private PitchBookOrderExecutedRecycler _pitchBookOrderExecutedRecycler;
    private PitchOffBookTradeRecycler _pitchOffBookTradeRecycler;
    private PitchBookCancelOrderRecycler _pitchBookCancelOrderRecycler;
    private PitchPriceStatisticRecycler _pitchPriceStatisticRecycler;
    private AuctionUpdateRecycler _auctionUpdateRecycler;
    private AuctionSummaryRecycler _auctionSummaryRecycler;
    private SoupDebugPacketRecycler _soupDebugPacketRecycler;
    private SoupLogInAcceptedRecycler _soupLogInAcceptedRecycler;
    private SoupLogInRejectedRecycler _soupLogInRejectedRecycler;
    private SoupLogInRequestRecycler _soupLogInRequestRecycler;
    private UnsequencedDataPacketRecycler _unsequencedDataPacketRecycler;

    public AllEventRecycler() {
        SuperpoolManager sp = SuperpoolManager.instance();
        _heartbeatRecycler = sp.getRecycler( HeartbeatRecycler.class, HeartbeatImpl.class );
        _endOfSessionRecycler = sp.getRecycler( EndOfSessionRecycler.class, EndOfSessionImpl.class );
        _logoutRequestRecycler = sp.getRecycler( LogoutRequestRecycler.class, LogoutRequestImpl.class );
        _testRequestRecycler = sp.getRecycler( TestRequestRecycler.class, TestRequestImpl.class );
        _logonRecycler = sp.getRecycler( LogonRecycler.class, LogonImpl.class );
        _logoutRecycler = sp.getRecycler( LogoutRecycler.class, LogoutImpl.class );
        _sessionRejectRecycler = sp.getRecycler( SessionRejectRecycler.class, SessionRejectImpl.class );
        _resendRequestRecycler = sp.getRecycler( ResendRequestRecycler.class, ResendRequestImpl.class );
        _clientResyncSentMsgsRecycler = sp.getRecycler( ClientResyncSentMsgsRecycler.class, ClientResyncSentMsgsImpl.class );
        _sequenceResetRecycler = sp.getRecycler( SequenceResetRecycler.class, SequenceResetImpl.class );
        _tradingSessionStatusRecycler = sp.getRecycler( TradingSessionStatusRecycler.class, TradingSessionStatusImpl.class );
        _secMassStatGrpRecycler = sp.getRecycler( SecMassStatGrpRecycler.class, SecMassStatGrpImpl.class );
        _massInstrumentStateChangeRecycler = sp.getRecycler( MassInstrumentStateChangeRecycler.class, MassInstrumentStateChangeImpl.class );
        _clientNewOrderSingleRecycler = sp.getRecycler( ClientNewOrderSingleRecycler.class, ClientNewOrderSingleImpl.class );
        _marketNewOrderSingleRecycler = sp.getRecycler( MarketNewOrderSingleRecycler.class, MarketNewOrderSingleImpl.class );

        _newOrderSingleRecycler = sp.getRecycler( NewOrderSingleRecycler.class, NewOrderSingleImpl.class );

        _clientCancelReplaceRequestRecycler = sp.getRecycler( ClientCancelReplaceRequestRecycler.class, ClientCancelReplaceRequestImpl.class );
        _marketCancelReplaceRequestRecycler = sp.getRecycler( MarketCancelReplaceRequestRecycler.class, MarketCancelReplaceRequestImpl.class );

        _cancelReplaceRequestRecycler = sp.getRecycler( CancelReplaceRequestRecycler.class, CancelReplaceRequestImpl.class );

        _clientCancelRequestRecycler = sp.getRecycler( ClientCancelRequestRecycler.class, ClientCancelRequestImpl.class );
        _marketCancelRequestRecycler = sp.getRecycler( MarketCancelRequestRecycler.class, MarketCancelRequestImpl.class );

        _cancelRequestRecycler = sp.getRecycler( CancelRequestRecycler.class, CancelRequestImpl.class );

        _clientForceCancelRecycler = sp.getRecycler( ClientForceCancelRecycler.class, ClientForceCancelImpl.class );
        _marketForceCancelRecycler = sp.getRecycler( MarketForceCancelRecycler.class, MarketForceCancelImpl.class );

        _forceCancelRecycler = sp.getRecycler( ForceCancelRecycler.class, ForceCancelImpl.class );

        _clientVagueOrderRejectRecycler = sp.getRecycler( ClientVagueOrderRejectRecycler.class, ClientVagueOrderRejectImpl.class );
        _marketVagueOrderRejectRecycler = sp.getRecycler( MarketVagueOrderRejectRecycler.class, MarketVagueOrderRejectImpl.class );

        _vagueOrderRejectRecycler = sp.getRecycler( VagueOrderRejectRecycler.class, VagueOrderRejectImpl.class );

        _clientCancelRejectRecycler = sp.getRecycler( ClientCancelRejectRecycler.class, ClientCancelRejectImpl.class );
        _marketCancelRejectRecycler = sp.getRecycler( MarketCancelRejectRecycler.class, MarketCancelRejectImpl.class );

        _cancelRejectRecycler = sp.getRecycler( CancelRejectRecycler.class, CancelRejectImpl.class );

        _clientNewOrderAckRecycler = sp.getRecycler( ClientNewOrderAckRecycler.class, ClientNewOrderAckImpl.class );
        _marketNewOrderAckRecycler = sp.getRecycler( MarketNewOrderAckRecycler.class, MarketNewOrderAckImpl.class );

        _newOrderAckRecycler = sp.getRecycler( NewOrderAckRecycler.class, NewOrderAckImpl.class );

        _clientTradeNewRecycler = sp.getRecycler( ClientTradeNewRecycler.class, ClientTradeNewImpl.class );
        _marketTradeNewRecycler = sp.getRecycler( MarketTradeNewRecycler.class, MarketTradeNewImpl.class );

        _tradeNewRecycler = sp.getRecycler( TradeNewRecycler.class, TradeNewImpl.class );

        _clientRejectedRecycler = sp.getRecycler( ClientRejectedRecycler.class, ClientRejectedImpl.class );
        _marketRejectedRecycler = sp.getRecycler( MarketRejectedRecycler.class, MarketRejectedImpl.class );

        _rejectedRecycler = sp.getRecycler( RejectedRecycler.class, RejectedImpl.class );

        _clientCancelledRecycler = sp.getRecycler( ClientCancelledRecycler.class, ClientCancelledImpl.class );
        _marketCancelledRecycler = sp.getRecycler( MarketCancelledRecycler.class, MarketCancelledImpl.class );

        _cancelledRecycler = sp.getRecycler( CancelledRecycler.class, CancelledImpl.class );

        _clientReplacedRecycler = sp.getRecycler( ClientReplacedRecycler.class, ClientReplacedImpl.class );
        _marketReplacedRecycler = sp.getRecycler( MarketReplacedRecycler.class, MarketReplacedImpl.class );

        _replacedRecycler = sp.getRecycler( ReplacedRecycler.class, ReplacedImpl.class );

        _clientDoneForDayRecycler = sp.getRecycler( ClientDoneForDayRecycler.class, ClientDoneForDayImpl.class );
        _marketDoneForDayRecycler = sp.getRecycler( MarketDoneForDayRecycler.class, MarketDoneForDayImpl.class );

        _doneForDayRecycler = sp.getRecycler( DoneForDayRecycler.class, DoneForDayImpl.class );

        _clientStoppedRecycler = sp.getRecycler( ClientStoppedRecycler.class, ClientStoppedImpl.class );
        _marketStoppedRecycler = sp.getRecycler( MarketStoppedRecycler.class, MarketStoppedImpl.class );

        _stoppedRecycler = sp.getRecycler( StoppedRecycler.class, StoppedImpl.class );

        _clientExpiredRecycler = sp.getRecycler( ClientExpiredRecycler.class, ClientExpiredImpl.class );
        _marketExpiredRecycler = sp.getRecycler( MarketExpiredRecycler.class, MarketExpiredImpl.class );

        _expiredRecycler = sp.getRecycler( ExpiredRecycler.class, ExpiredImpl.class );

        _clientSuspendedRecycler = sp.getRecycler( ClientSuspendedRecycler.class, ClientSuspendedImpl.class );
        _marketSuspendedRecycler = sp.getRecycler( MarketSuspendedRecycler.class, MarketSuspendedImpl.class );

        _suspendedRecycler = sp.getRecycler( SuspendedRecycler.class, SuspendedImpl.class );

        _clientRestatedRecycler = sp.getRecycler( ClientRestatedRecycler.class, ClientRestatedImpl.class );
        _marketRestatedRecycler = sp.getRecycler( MarketRestatedRecycler.class, MarketRestatedImpl.class );

        _restatedRecycler = sp.getRecycler( RestatedRecycler.class, RestatedImpl.class );

        _clientTradeCorrectRecycler = sp.getRecycler( ClientTradeCorrectRecycler.class, ClientTradeCorrectImpl.class );
        _marketTradeCorrectRecycler = sp.getRecycler( MarketTradeCorrectRecycler.class, MarketTradeCorrectImpl.class );

        _tradeCorrectRecycler = sp.getRecycler( TradeCorrectRecycler.class, TradeCorrectImpl.class );

        _clientTradeCancelRecycler = sp.getRecycler( ClientTradeCancelRecycler.class, ClientTradeCancelImpl.class );
        _marketTradeCancelRecycler = sp.getRecycler( MarketTradeCancelRecycler.class, MarketTradeCancelImpl.class );

        _tradeCancelRecycler = sp.getRecycler( TradeCancelRecycler.class, TradeCancelImpl.class );

        _clientPendingCancelRecycler = sp.getRecycler( ClientPendingCancelRecycler.class, ClientPendingCancelImpl.class );
        _marketPendingCancelRecycler = sp.getRecycler( MarketPendingCancelRecycler.class, MarketPendingCancelImpl.class );

        _pendingCancelRecycler = sp.getRecycler( PendingCancelRecycler.class, PendingCancelImpl.class );

        _clientPendingReplaceRecycler = sp.getRecycler( ClientPendingReplaceRecycler.class, ClientPendingReplaceImpl.class );
        _marketPendingReplaceRecycler = sp.getRecycler( MarketPendingReplaceRecycler.class, MarketPendingReplaceImpl.class );

        _pendingReplaceRecycler = sp.getRecycler( PendingReplaceRecycler.class, PendingReplaceImpl.class );

        _clientPendingNewRecycler = sp.getRecycler( ClientPendingNewRecycler.class, ClientPendingNewImpl.class );
        _marketPendingNewRecycler = sp.getRecycler( MarketPendingNewRecycler.class, MarketPendingNewImpl.class );

        _pendingNewRecycler = sp.getRecycler( PendingNewRecycler.class, PendingNewImpl.class );

        _clientOrderStatusRecycler = sp.getRecycler( ClientOrderStatusRecycler.class, ClientOrderStatusImpl.class );
        _marketOrderStatusRecycler = sp.getRecycler( MarketOrderStatusRecycler.class, MarketOrderStatusImpl.class );

        _orderStatusRecycler = sp.getRecycler( OrderStatusRecycler.class, OrderStatusImpl.class );

        _clientIgnoredExecRecycler = sp.getRecycler( ClientIgnoredExecRecycler.class, ClientIgnoredExecImpl.class );
        _marketIgnoredExecRecycler = sp.getRecycler( MarketIgnoredExecRecycler.class, MarketIgnoredExecImpl.class );

        _ignoredExecRecycler = sp.getRecycler( IgnoredExecRecycler.class, IgnoredExecImpl.class );

        _clientCalculatedRecycler = sp.getRecycler( ClientCalculatedRecycler.class, ClientCalculatedImpl.class );
        _marketCalculatedRecycler = sp.getRecycler( MarketCalculatedRecycler.class, MarketCalculatedImpl.class );

        _calculatedRecycler = sp.getRecycler( CalculatedRecycler.class, CalculatedImpl.class );

        _clientAlertLimitBreachRecycler = sp.getRecycler( ClientAlertLimitBreachRecycler.class, ClientAlertLimitBreachImpl.class );
        _marketAlertLimitBreachRecycler = sp.getRecycler( MarketAlertLimitBreachRecycler.class, MarketAlertLimitBreachImpl.class );

        _alertLimitBreachRecycler = sp.getRecycler( AlertLimitBreachRecycler.class, AlertLimitBreachImpl.class );

        _clientAlertTradeMissingOrdersRecycler = sp.getRecycler( ClientAlertTradeMissingOrdersRecycler.class, ClientAlertTradeMissingOrdersImpl.class );
        _marketAlertTradeMissingOrdersRecycler = sp.getRecycler( MarketAlertTradeMissingOrdersRecycler.class, MarketAlertTradeMissingOrdersImpl.class );

        _alertTradeMissingOrdersRecycler = sp.getRecycler( AlertTradeMissingOrdersRecycler.class, AlertTradeMissingOrdersImpl.class );

        _symbolRepeatingGrpRecycler = sp.getRecycler( SymbolRepeatingGrpRecycler.class, SymbolRepeatingGrpImpl.class );
        _mDRequestRecycler = sp.getRecycler( MDRequestRecycler.class, MDRequestImpl.class );
        _tickUpdateRecycler = sp.getRecycler( TickUpdateRecycler.class, TickUpdateImpl.class );
        _mDUpdateRecycler = sp.getRecycler( MDUpdateRecycler.class, MDUpdateImpl.class );
        _secDefEventRecycler = sp.getRecycler( SecDefEventRecycler.class, SecDefEventImpl.class );
        _securityAltIDRecycler = sp.getRecycler( SecurityAltIDRecycler.class, SecurityAltIDImpl.class );
        _sDFeedTypeRecycler = sp.getRecycler( SDFeedTypeRecycler.class, SDFeedTypeImpl.class );
        _secDefLegRecycler = sp.getRecycler( SecDefLegRecycler.class, SecDefLegImpl.class );
        _mDEntryRecycler = sp.getRecycler( MDEntryRecycler.class, MDEntryImpl.class );
        _mDSnapEntryRecycler = sp.getRecycler( MDSnapEntryRecycler.class, MDSnapEntryImpl.class );
        _msgSeqNumGapRecycler = sp.getRecycler( MsgSeqNumGapRecycler.class, MsgSeqNumGapImpl.class );
        _mDIncRefreshRecycler = sp.getRecycler( MDIncRefreshRecycler.class, MDIncRefreshImpl.class );
        _mDSnapshotFullRefreshRecycler = sp.getRecycler( MDSnapshotFullRefreshRecycler.class, MDSnapshotFullRefreshImpl.class );
        _securityDefinitionRecycler = sp.getRecycler( SecurityDefinitionRecycler.class, SecurityDefinitionImpl.class );
        _securityDefinitionUpdateRecycler = sp.getRecycler( SecurityDefinitionUpdateRecycler.class, SecurityDefinitionUpdateImpl.class );
        _productSnapshotRecycler = sp.getRecycler( ProductSnapshotRecycler.class, ProductSnapshotImpl.class );
        _securityStatusRecycler = sp.getRecycler( SecurityStatusRecycler.class, SecurityStatusImpl.class );
        _settlementPriceEventRecycler = sp.getRecycler( SettlementPriceEventRecycler.class, SettlementPriceEventImpl.class );
        _closingPriceEventRecycler = sp.getRecycler( ClosingPriceEventRecycler.class, ClosingPriceEventImpl.class );
        _openPriceEventRecycler = sp.getRecycler( OpenPriceEventRecycler.class, OpenPriceEventImpl.class );
        _openInterestEventRecycler = sp.getRecycler( OpenInterestEventRecycler.class, OpenInterestEventImpl.class );
        _newsRecycler = sp.getRecycler( NewsRecycler.class, NewsImpl.class );
        _corporateActionEventRecycler = sp.getRecycler( CorporateActionEventRecycler.class, CorporateActionEventImpl.class );
        _instrumentSimDataRecycler = sp.getRecycler( InstrumentSimDataRecycler.class, InstrumentSimDataImpl.class );
        _refPriceEventRecycler = sp.getRecycler( RefPriceEventRecycler.class, RefPriceEventImpl.class );
        _brokerLoanResponseRecycler = sp.getRecycler( BrokerLoanResponseRecycler.class, BrokerLoanResponseImpl.class );
        _priceLimitCollarEventRecycler = sp.getRecycler( PriceLimitCollarEventRecycler.class, PriceLimitCollarEventImpl.class );
        _securityTradingStatusEventRecycler = sp.getRecycler( SecurityTradingStatusEventRecycler.class, SecurityTradingStatusEventImpl.class );
        _leanHogIndexEventRecycler = sp.getRecycler( LeanHogIndexEventRecycler.class, LeanHogIndexEventImpl.class );
        _forceFlattenCommandRecycler = sp.getRecycler( ForceFlattenCommandRecycler.class, ForceFlattenCommandImpl.class );
        _appRunRecycler = sp.getRecycler( AppRunRecycler.class, AppRunImpl.class );
        _stratInstrumentRecycler = sp.getRecycler( StratInstrumentRecycler.class, StratInstrumentImpl.class );
        _strategyRunRecycler = sp.getRecycler( StrategyRunRecycler.class, StrategyRunImpl.class );
        _stratInstrumentStateRecycler = sp.getRecycler( StratInstrumentStateRecycler.class, StratInstrumentStateImpl.class );
        _strategyStateRecycler = sp.getRecycler( StrategyStateRecycler.class, StrategyStateImpl.class );
        _uTPLogonRecycler = sp.getRecycler( UTPLogonRecycler.class, UTPLogonImpl.class );
        _uTPLogonRejectRecycler = sp.getRecycler( UTPLogonRejectRecycler.class, UTPLogonRejectImpl.class );
        _uTPTradingSessionStatusRecycler = sp.getRecycler( UTPTradingSessionStatusRecycler.class, UTPTradingSessionStatusImpl.class );
        _eTIConnectionGatewayRequestRecycler = sp.getRecycler( ETIConnectionGatewayRequestRecycler.class, ETIConnectionGatewayRequestImpl.class );
        _eTIConnectionGatewayResponseRecycler = sp.getRecycler( ETIConnectionGatewayResponseRecycler.class, ETIConnectionGatewayResponseImpl.class );
        _eTISessionLogonRequestRecycler = sp.getRecycler( ETISessionLogonRequestRecycler.class, ETISessionLogonRequestImpl.class );
        _eTISessionLogonResponseRecycler = sp.getRecycler( ETISessionLogonResponseRecycler.class, ETISessionLogonResponseImpl.class );
        _eTISessionLogoutRequestRecycler = sp.getRecycler( ETISessionLogoutRequestRecycler.class, ETISessionLogoutRequestImpl.class );
        _eTISessionLogoutResponseRecycler = sp.getRecycler( ETISessionLogoutResponseRecycler.class, ETISessionLogoutResponseImpl.class );
        _eTISessionLogoutNotificationRecycler = sp.getRecycler( ETISessionLogoutNotificationRecycler.class, ETISessionLogoutNotificationImpl.class );
        _eTIUserLogonRequestRecycler = sp.getRecycler( ETIUserLogonRequestRecycler.class, ETIUserLogonRequestImpl.class );
        _eTIUserLogonResponseRecycler = sp.getRecycler( ETIUserLogonResponseRecycler.class, ETIUserLogonResponseImpl.class );
        _eTIUserLogoutRequestRecycler = sp.getRecycler( ETIUserLogoutRequestRecycler.class, ETIUserLogoutRequestImpl.class );
        _eTIUserLogoutResponseRecycler = sp.getRecycler( ETIUserLogoutResponseRecycler.class, ETIUserLogoutResponseImpl.class );
        _eTIThrottleUpdateNotificationRecycler = sp.getRecycler( ETIThrottleUpdateNotificationRecycler.class, ETIThrottleUpdateNotificationImpl.class );
        _eTISubscribeRecycler = sp.getRecycler( ETISubscribeRecycler.class, ETISubscribeImpl.class );
        _eTISubscribeResponseRecycler = sp.getRecycler( ETISubscribeResponseRecycler.class, ETISubscribeResponseImpl.class );
        _eTIUnsubscribeRecycler = sp.getRecycler( ETIUnsubscribeRecycler.class, ETIUnsubscribeImpl.class );
        _eTIUnsubscribeResponseRecycler = sp.getRecycler( ETIUnsubscribeResponseRecycler.class, ETIUnsubscribeResponseImpl.class );
        _eTIRetransmitRecycler = sp.getRecycler( ETIRetransmitRecycler.class, ETIRetransmitImpl.class );
        _eTIRetransmitResponseRecycler = sp.getRecycler( ETIRetransmitResponseRecycler.class, ETIRetransmitResponseImpl.class );
        _eTIRetransmitOrderEventsRecycler = sp.getRecycler( ETIRetransmitOrderEventsRecycler.class, ETIRetransmitOrderEventsImpl.class );
        _eTIRetransmitOrderEventsResponseRecycler = sp.getRecycler( ETIRetransmitOrderEventsResponseRecycler.class, ETIRetransmitOrderEventsResponseImpl.class );
        _milleniumLogonRecycler = sp.getRecycler( MilleniumLogonRecycler.class, MilleniumLogonImpl.class );
        _milleniumLogonReplyRecycler = sp.getRecycler( MilleniumLogonReplyRecycler.class, MilleniumLogonReplyImpl.class );
        _milleniumLogoutRecycler = sp.getRecycler( MilleniumLogoutRecycler.class, MilleniumLogoutImpl.class );
        _milleniumMissedMessageRequestRecycler = sp.getRecycler( MilleniumMissedMessageRequestRecycler.class, MilleniumMissedMessageRequestImpl.class );
        _milleniumMissedMsgRequestAckRecycler = sp.getRecycler( MilleniumMissedMsgRequestAckRecycler.class, MilleniumMissedMsgRequestAckImpl.class );
        _milleniumMissedMsgReportRecycler = sp.getRecycler( MilleniumMissedMsgReportRecycler.class, MilleniumMissedMsgReportImpl.class );
        _bookAddOrderRecycler = sp.getRecycler( BookAddOrderRecycler.class, BookAddOrderImpl.class );
        _bookDeleteOrderRecycler = sp.getRecycler( BookDeleteOrderRecycler.class, BookDeleteOrderImpl.class );
        _bookModifyOrderRecycler = sp.getRecycler( BookModifyOrderRecycler.class, BookModifyOrderImpl.class );
        _bookClearRecycler = sp.getRecycler( BookClearRecycler.class, BookClearImpl.class );
        _pitchSymbolClearRecycler = sp.getRecycler( PitchSymbolClearRecycler.class, PitchSymbolClearImpl.class );
        _pitchBookAddOrderRecycler = sp.getRecycler( PitchBookAddOrderRecycler.class, PitchBookAddOrderImpl.class );
        _pitchBookOrderExecutedRecycler = sp.getRecycler( PitchBookOrderExecutedRecycler.class, PitchBookOrderExecutedImpl.class );
        _pitchOffBookTradeRecycler = sp.getRecycler( PitchOffBookTradeRecycler.class, PitchOffBookTradeImpl.class );
        _pitchBookCancelOrderRecycler = sp.getRecycler( PitchBookCancelOrderRecycler.class, PitchBookCancelOrderImpl.class );
        _pitchPriceStatisticRecycler = sp.getRecycler( PitchPriceStatisticRecycler.class, PitchPriceStatisticImpl.class );
        _auctionUpdateRecycler = sp.getRecycler( AuctionUpdateRecycler.class, AuctionUpdateImpl.class );
        _auctionSummaryRecycler = sp.getRecycler( AuctionSummaryRecycler.class, AuctionSummaryImpl.class );
        _soupDebugPacketRecycler = sp.getRecycler( SoupDebugPacketRecycler.class, SoupDebugPacketImpl.class );
        _soupLogInAcceptedRecycler = sp.getRecycler( SoupLogInAcceptedRecycler.class, SoupLogInAcceptedImpl.class );
        _soupLogInRejectedRecycler = sp.getRecycler( SoupLogInRejectedRecycler.class, SoupLogInRejectedImpl.class );
        _soupLogInRequestRecycler = sp.getRecycler( SoupLogInRequestRecycler.class, SoupLogInRequestImpl.class );
        _unsequencedDataPacketRecycler = sp.getRecycler( UnsequencedDataPacketRecycler.class, UnsequencedDataPacketImpl.class );
    }
    public void recycle( HeartbeatImpl msg ) {
        _heartbeatRecycler.recycle( msg );
    }

    public void recycle( EndOfSessionImpl msg ) {
        _endOfSessionRecycler.recycle( msg );
    }

    public void recycle( LogoutRequestImpl msg ) {
        _logoutRequestRecycler.recycle( msg );
    }

    public void recycle( TestRequestImpl msg ) {
        _testRequestRecycler.recycle( msg );
    }

    public void recycle( LogonImpl msg ) {
        _logonRecycler.recycle( msg );
    }

    public void recycle( LogoutImpl msg ) {
        _logoutRecycler.recycle( msg );
    }

    public void recycle( SessionRejectImpl msg ) {
        _sessionRejectRecycler.recycle( msg );
    }

    public void recycle( ResendRequestImpl msg ) {
        _resendRequestRecycler.recycle( msg );
    }

    public void recycle( ClientResyncSentMsgsImpl msg ) {
        _clientResyncSentMsgsRecycler.recycle( msg );
    }

    public void recycle( SequenceResetImpl msg ) {
        _sequenceResetRecycler.recycle( msg );
    }

    public void recycle( TradingSessionStatusImpl msg ) {
        _tradingSessionStatusRecycler.recycle( msg );
    }

    public void recycle( SecMassStatGrpImpl msg ) {
        _secMassStatGrpRecycler.recycle( msg );
    }

    public void recycle( MassInstrumentStateChangeImpl msg ) {
        _massInstrumentStateChangeRecycler.recycle( msg );
    }

    public void recycle( ClientNewOrderSingleImpl msg ) {
        _clientNewOrderSingleRecycler.recycle( msg );
    }

    public void recycle( MarketNewOrderSingleImpl msg ) {
        _marketNewOrderSingleRecycler.recycle( msg );
    }

    public void recycle( NewOrderSingleImpl msg ) {
        _newOrderSingleRecycler.recycle( msg );
    }

    public void recycle( ClientCancelReplaceRequestImpl msg ) {
        _clientCancelReplaceRequestRecycler.recycle( msg );
    }

    public void recycle( MarketCancelReplaceRequestImpl msg ) {
        _marketCancelReplaceRequestRecycler.recycle( msg );
    }

    public void recycle( CancelReplaceRequestImpl msg ) {
        _cancelReplaceRequestRecycler.recycle( msg );
    }

    public void recycle( ClientCancelRequestImpl msg ) {
        _clientCancelRequestRecycler.recycle( msg );
    }

    public void recycle( MarketCancelRequestImpl msg ) {
        _marketCancelRequestRecycler.recycle( msg );
    }

    public void recycle( CancelRequestImpl msg ) {
        _cancelRequestRecycler.recycle( msg );
    }

    public void recycle( ClientForceCancelImpl msg ) {
        _clientForceCancelRecycler.recycle( msg );
    }

    public void recycle( MarketForceCancelImpl msg ) {
        _marketForceCancelRecycler.recycle( msg );
    }

    public void recycle( ForceCancelImpl msg ) {
        _forceCancelRecycler.recycle( msg );
    }

    public void recycle( ClientVagueOrderRejectImpl msg ) {
        _clientVagueOrderRejectRecycler.recycle( msg );
    }

    public void recycle( MarketVagueOrderRejectImpl msg ) {
        _marketVagueOrderRejectRecycler.recycle( msg );
    }

    public void recycle( VagueOrderRejectImpl msg ) {
        _vagueOrderRejectRecycler.recycle( msg );
    }

    public void recycle( ClientCancelRejectImpl msg ) {
        _clientCancelRejectRecycler.recycle( msg );
    }

    public void recycle( MarketCancelRejectImpl msg ) {
        _marketCancelRejectRecycler.recycle( msg );
    }

    public void recycle( CancelRejectImpl msg ) {
        _cancelRejectRecycler.recycle( msg );
    }

    public void recycle( ClientNewOrderAckImpl msg ) {
        _clientNewOrderAckRecycler.recycle( msg );
    }

    public void recycle( MarketNewOrderAckImpl msg ) {
        _marketNewOrderAckRecycler.recycle( msg );
    }

    public void recycle( NewOrderAckImpl msg ) {
        _newOrderAckRecycler.recycle( msg );
    }

    public void recycle( ClientTradeNewImpl msg ) {
        _clientTradeNewRecycler.recycle( msg );
    }

    public void recycle( MarketTradeNewImpl msg ) {
        _marketTradeNewRecycler.recycle( msg );
    }

    public void recycle( TradeNewImpl msg ) {
        _tradeNewRecycler.recycle( msg );
    }

    public void recycle( ClientRejectedImpl msg ) {
        _clientRejectedRecycler.recycle( msg );
    }

    public void recycle( MarketRejectedImpl msg ) {
        _marketRejectedRecycler.recycle( msg );
    }

    public void recycle( RejectedImpl msg ) {
        _rejectedRecycler.recycle( msg );
    }

    public void recycle( ClientCancelledImpl msg ) {
        _clientCancelledRecycler.recycle( msg );
    }

    public void recycle( MarketCancelledImpl msg ) {
        _marketCancelledRecycler.recycle( msg );
    }

    public void recycle( CancelledImpl msg ) {
        _cancelledRecycler.recycle( msg );
    }

    public void recycle( ClientReplacedImpl msg ) {
        _clientReplacedRecycler.recycle( msg );
    }

    public void recycle( MarketReplacedImpl msg ) {
        _marketReplacedRecycler.recycle( msg );
    }

    public void recycle( ReplacedImpl msg ) {
        _replacedRecycler.recycle( msg );
    }

    public void recycle( ClientDoneForDayImpl msg ) {
        _clientDoneForDayRecycler.recycle( msg );
    }

    public void recycle( MarketDoneForDayImpl msg ) {
        _marketDoneForDayRecycler.recycle( msg );
    }

    public void recycle( DoneForDayImpl msg ) {
        _doneForDayRecycler.recycle( msg );
    }

    public void recycle( ClientStoppedImpl msg ) {
        _clientStoppedRecycler.recycle( msg );
    }

    public void recycle( MarketStoppedImpl msg ) {
        _marketStoppedRecycler.recycle( msg );
    }

    public void recycle( StoppedImpl msg ) {
        _stoppedRecycler.recycle( msg );
    }

    public void recycle( ClientExpiredImpl msg ) {
        _clientExpiredRecycler.recycle( msg );
    }

    public void recycle( MarketExpiredImpl msg ) {
        _marketExpiredRecycler.recycle( msg );
    }

    public void recycle( ExpiredImpl msg ) {
        _expiredRecycler.recycle( msg );
    }

    public void recycle( ClientSuspendedImpl msg ) {
        _clientSuspendedRecycler.recycle( msg );
    }

    public void recycle( MarketSuspendedImpl msg ) {
        _marketSuspendedRecycler.recycle( msg );
    }

    public void recycle( SuspendedImpl msg ) {
        _suspendedRecycler.recycle( msg );
    }

    public void recycle( ClientRestatedImpl msg ) {
        _clientRestatedRecycler.recycle( msg );
    }

    public void recycle( MarketRestatedImpl msg ) {
        _marketRestatedRecycler.recycle( msg );
    }

    public void recycle( RestatedImpl msg ) {
        _restatedRecycler.recycle( msg );
    }

    public void recycle( ClientTradeCorrectImpl msg ) {
        _clientTradeCorrectRecycler.recycle( msg );
    }

    public void recycle( MarketTradeCorrectImpl msg ) {
        _marketTradeCorrectRecycler.recycle( msg );
    }

    public void recycle( TradeCorrectImpl msg ) {
        _tradeCorrectRecycler.recycle( msg );
    }

    public void recycle( ClientTradeCancelImpl msg ) {
        _clientTradeCancelRecycler.recycle( msg );
    }

    public void recycle( MarketTradeCancelImpl msg ) {
        _marketTradeCancelRecycler.recycle( msg );
    }

    public void recycle( TradeCancelImpl msg ) {
        _tradeCancelRecycler.recycle( msg );
    }

    public void recycle( ClientPendingCancelImpl msg ) {
        _clientPendingCancelRecycler.recycle( msg );
    }

    public void recycle( MarketPendingCancelImpl msg ) {
        _marketPendingCancelRecycler.recycle( msg );
    }

    public void recycle( PendingCancelImpl msg ) {
        _pendingCancelRecycler.recycle( msg );
    }

    public void recycle( ClientPendingReplaceImpl msg ) {
        _clientPendingReplaceRecycler.recycle( msg );
    }

    public void recycle( MarketPendingReplaceImpl msg ) {
        _marketPendingReplaceRecycler.recycle( msg );
    }

    public void recycle( PendingReplaceImpl msg ) {
        _pendingReplaceRecycler.recycle( msg );
    }

    public void recycle( ClientPendingNewImpl msg ) {
        _clientPendingNewRecycler.recycle( msg );
    }

    public void recycle( MarketPendingNewImpl msg ) {
        _marketPendingNewRecycler.recycle( msg );
    }

    public void recycle( PendingNewImpl msg ) {
        _pendingNewRecycler.recycle( msg );
    }

    public void recycle( ClientOrderStatusImpl msg ) {
        _clientOrderStatusRecycler.recycle( msg );
    }

    public void recycle( MarketOrderStatusImpl msg ) {
        _marketOrderStatusRecycler.recycle( msg );
    }

    public void recycle( OrderStatusImpl msg ) {
        _orderStatusRecycler.recycle( msg );
    }

    public void recycle( ClientIgnoredExecImpl msg ) {
        _clientIgnoredExecRecycler.recycle( msg );
    }

    public void recycle( MarketIgnoredExecImpl msg ) {
        _marketIgnoredExecRecycler.recycle( msg );
    }

    public void recycle( IgnoredExecImpl msg ) {
        _ignoredExecRecycler.recycle( msg );
    }

    public void recycle( ClientCalculatedImpl msg ) {
        _clientCalculatedRecycler.recycle( msg );
    }

    public void recycle( MarketCalculatedImpl msg ) {
        _marketCalculatedRecycler.recycle( msg );
    }

    public void recycle( CalculatedImpl msg ) {
        _calculatedRecycler.recycle( msg );
    }

    public void recycle( ClientAlertLimitBreachImpl msg ) {
        _clientAlertLimitBreachRecycler.recycle( msg );
    }

    public void recycle( MarketAlertLimitBreachImpl msg ) {
        _marketAlertLimitBreachRecycler.recycle( msg );
    }

    public void recycle( AlertLimitBreachImpl msg ) {
        _alertLimitBreachRecycler.recycle( msg );
    }

    public void recycle( ClientAlertTradeMissingOrdersImpl msg ) {
        _clientAlertTradeMissingOrdersRecycler.recycle( msg );
    }

    public void recycle( MarketAlertTradeMissingOrdersImpl msg ) {
        _marketAlertTradeMissingOrdersRecycler.recycle( msg );
    }

    public void recycle( AlertTradeMissingOrdersImpl msg ) {
        _alertTradeMissingOrdersRecycler.recycle( msg );
    }

    public void recycle( SymbolRepeatingGrpImpl msg ) {
        _symbolRepeatingGrpRecycler.recycle( msg );
    }

    public void recycle( MDRequestImpl msg ) {
        _mDRequestRecycler.recycle( msg );
    }

    public void recycle( TickUpdateImpl msg ) {
        _tickUpdateRecycler.recycle( msg );
    }

    public void recycle( MDUpdateImpl msg ) {
        _mDUpdateRecycler.recycle( msg );
    }

    public void recycle( SecDefEventImpl msg ) {
        _secDefEventRecycler.recycle( msg );
    }

    public void recycle( SecurityAltIDImpl msg ) {
        _securityAltIDRecycler.recycle( msg );
    }

    public void recycle( SDFeedTypeImpl msg ) {
        _sDFeedTypeRecycler.recycle( msg );
    }

    public void recycle( SecDefLegImpl msg ) {
        _secDefLegRecycler.recycle( msg );
    }

    public void recycle( MDEntryImpl msg ) {
        _mDEntryRecycler.recycle( msg );
    }

    public void recycle( MDSnapEntryImpl msg ) {
        _mDSnapEntryRecycler.recycle( msg );
    }

    public void recycle( MsgSeqNumGapImpl msg ) {
        _msgSeqNumGapRecycler.recycle( msg );
    }

    public void recycle( MDIncRefreshImpl msg ) {
        _mDIncRefreshRecycler.recycle( msg );
    }

    public void recycle( MDSnapshotFullRefreshImpl msg ) {
        _mDSnapshotFullRefreshRecycler.recycle( msg );
    }

    public void recycle( SecurityDefinitionImpl msg ) {
        _securityDefinitionRecycler.recycle( msg );
    }

    public void recycle( SecurityDefinitionUpdateImpl msg ) {
        _securityDefinitionUpdateRecycler.recycle( msg );
    }

    public void recycle( ProductSnapshotImpl msg ) {
        _productSnapshotRecycler.recycle( msg );
    }

    public void recycle( SecurityStatusImpl msg ) {
        _securityStatusRecycler.recycle( msg );
    }

    public void recycle( SettlementPriceEventImpl msg ) {
        _settlementPriceEventRecycler.recycle( msg );
    }

    public void recycle( ClosingPriceEventImpl msg ) {
        _closingPriceEventRecycler.recycle( msg );
    }

    public void recycle( OpenPriceEventImpl msg ) {
        _openPriceEventRecycler.recycle( msg );
    }

    public void recycle( OpenInterestEventImpl msg ) {
        _openInterestEventRecycler.recycle( msg );
    }

    public void recycle( NewsImpl msg ) {
        _newsRecycler.recycle( msg );
    }

    public void recycle( CorporateActionEventImpl msg ) {
        _corporateActionEventRecycler.recycle( msg );
    }

    public void recycle( InstrumentSimDataImpl msg ) {
        _instrumentSimDataRecycler.recycle( msg );
    }

    public void recycle( RefPriceEventImpl msg ) {
        _refPriceEventRecycler.recycle( msg );
    }

    public void recycle( BrokerLoanResponseImpl msg ) {
        _brokerLoanResponseRecycler.recycle( msg );
    }

    public void recycle( PriceLimitCollarEventImpl msg ) {
        _priceLimitCollarEventRecycler.recycle( msg );
    }

    public void recycle( SecurityTradingStatusEventImpl msg ) {
        _securityTradingStatusEventRecycler.recycle( msg );
    }

    public void recycle( LeanHogIndexEventImpl msg ) {
        _leanHogIndexEventRecycler.recycle( msg );
    }

    public void recycle( ForceFlattenCommandImpl msg ) {
        _forceFlattenCommandRecycler.recycle( msg );
    }

    public void recycle( AppRunImpl msg ) {
        _appRunRecycler.recycle( msg );
    }

    public void recycle( StratInstrumentImpl msg ) {
        _stratInstrumentRecycler.recycle( msg );
    }

    public void recycle( StrategyRunImpl msg ) {
        _strategyRunRecycler.recycle( msg );
    }

    public void recycle( StratInstrumentStateImpl msg ) {
        _stratInstrumentStateRecycler.recycle( msg );
    }

    public void recycle( StrategyStateImpl msg ) {
        _strategyStateRecycler.recycle( msg );
    }

    public void recycle( UTPLogonImpl msg ) {
        _uTPLogonRecycler.recycle( msg );
    }

    public void recycle( UTPLogonRejectImpl msg ) {
        _uTPLogonRejectRecycler.recycle( msg );
    }

    public void recycle( UTPTradingSessionStatusImpl msg ) {
        _uTPTradingSessionStatusRecycler.recycle( msg );
    }

    public void recycle( ETIConnectionGatewayRequestImpl msg ) {
        _eTIConnectionGatewayRequestRecycler.recycle( msg );
    }

    public void recycle( ETIConnectionGatewayResponseImpl msg ) {
        _eTIConnectionGatewayResponseRecycler.recycle( msg );
    }

    public void recycle( ETISessionLogonRequestImpl msg ) {
        _eTISessionLogonRequestRecycler.recycle( msg );
    }

    public void recycle( ETISessionLogonResponseImpl msg ) {
        _eTISessionLogonResponseRecycler.recycle( msg );
    }

    public void recycle( ETISessionLogoutRequestImpl msg ) {
        _eTISessionLogoutRequestRecycler.recycle( msg );
    }

    public void recycle( ETISessionLogoutResponseImpl msg ) {
        _eTISessionLogoutResponseRecycler.recycle( msg );
    }

    public void recycle( ETISessionLogoutNotificationImpl msg ) {
        _eTISessionLogoutNotificationRecycler.recycle( msg );
    }

    public void recycle( ETIUserLogonRequestImpl msg ) {
        _eTIUserLogonRequestRecycler.recycle( msg );
    }

    public void recycle( ETIUserLogonResponseImpl msg ) {
        _eTIUserLogonResponseRecycler.recycle( msg );
    }

    public void recycle( ETIUserLogoutRequestImpl msg ) {
        _eTIUserLogoutRequestRecycler.recycle( msg );
    }

    public void recycle( ETIUserLogoutResponseImpl msg ) {
        _eTIUserLogoutResponseRecycler.recycle( msg );
    }

    public void recycle( ETIThrottleUpdateNotificationImpl msg ) {
        _eTIThrottleUpdateNotificationRecycler.recycle( msg );
    }

    public void recycle( ETISubscribeImpl msg ) {
        _eTISubscribeRecycler.recycle( msg );
    }

    public void recycle( ETISubscribeResponseImpl msg ) {
        _eTISubscribeResponseRecycler.recycle( msg );
    }

    public void recycle( ETIUnsubscribeImpl msg ) {
        _eTIUnsubscribeRecycler.recycle( msg );
    }

    public void recycle( ETIUnsubscribeResponseImpl msg ) {
        _eTIUnsubscribeResponseRecycler.recycle( msg );
    }

    public void recycle( ETIRetransmitImpl msg ) {
        _eTIRetransmitRecycler.recycle( msg );
    }

    public void recycle( ETIRetransmitResponseImpl msg ) {
        _eTIRetransmitResponseRecycler.recycle( msg );
    }

    public void recycle( ETIRetransmitOrderEventsImpl msg ) {
        _eTIRetransmitOrderEventsRecycler.recycle( msg );
    }

    public void recycle( ETIRetransmitOrderEventsResponseImpl msg ) {
        _eTIRetransmitOrderEventsResponseRecycler.recycle( msg );
    }

    public void recycle( MilleniumLogonImpl msg ) {
        _milleniumLogonRecycler.recycle( msg );
    }

    public void recycle( MilleniumLogonReplyImpl msg ) {
        _milleniumLogonReplyRecycler.recycle( msg );
    }

    public void recycle( MilleniumLogoutImpl msg ) {
        _milleniumLogoutRecycler.recycle( msg );
    }

    public void recycle( MilleniumMissedMessageRequestImpl msg ) {
        _milleniumMissedMessageRequestRecycler.recycle( msg );
    }

    public void recycle( MilleniumMissedMsgRequestAckImpl msg ) {
        _milleniumMissedMsgRequestAckRecycler.recycle( msg );
    }

    public void recycle( MilleniumMissedMsgReportImpl msg ) {
        _milleniumMissedMsgReportRecycler.recycle( msg );
    }

    public void recycle( BookAddOrderImpl msg ) {
        _bookAddOrderRecycler.recycle( msg );
    }

    public void recycle( BookDeleteOrderImpl msg ) {
        _bookDeleteOrderRecycler.recycle( msg );
    }

    public void recycle( BookModifyOrderImpl msg ) {
        _bookModifyOrderRecycler.recycle( msg );
    }

    public void recycle( BookClearImpl msg ) {
        _bookClearRecycler.recycle( msg );
    }

    public void recycle( PitchSymbolClearImpl msg ) {
        _pitchSymbolClearRecycler.recycle( msg );
    }

    public void recycle( PitchBookAddOrderImpl msg ) {
        _pitchBookAddOrderRecycler.recycle( msg );
    }

    public void recycle( PitchBookOrderExecutedImpl msg ) {
        _pitchBookOrderExecutedRecycler.recycle( msg );
    }

    public void recycle( PitchOffBookTradeImpl msg ) {
        _pitchOffBookTradeRecycler.recycle( msg );
    }

    public void recycle( PitchBookCancelOrderImpl msg ) {
        _pitchBookCancelOrderRecycler.recycle( msg );
    }

    public void recycle( PitchPriceStatisticImpl msg ) {
        _pitchPriceStatisticRecycler.recycle( msg );
    }

    public void recycle( AuctionUpdateImpl msg ) {
        _auctionUpdateRecycler.recycle( msg );
    }

    public void recycle( AuctionSummaryImpl msg ) {
        _auctionSummaryRecycler.recycle( msg );
    }

    public void recycle( SoupDebugPacketImpl msg ) {
        _soupDebugPacketRecycler.recycle( msg );
    }

    public void recycle( SoupLogInAcceptedImpl msg ) {
        _soupLogInAcceptedRecycler.recycle( msg );
    }

    public void recycle( SoupLogInRejectedImpl msg ) {
        _soupLogInRejectedRecycler.recycle( msg );
    }

    public void recycle( SoupLogInRequestImpl msg ) {
        _soupLogInRequestRecycler.recycle( msg );
    }

    public void recycle( UnsequencedDataPacketImpl msg ) {
        _unsequencedDataPacketRecycler.recycle( msg );
    }

    @Override public void recycle( HasReusableType msg ) {
        if ( msg == null ) return;

        final ReusableType type = msg.getReusableType();

        switch( type.getId() ) {
        case FullEventIds.ID_HEARTBEAT:
            _heartbeatRecycler.recycle( (HeartbeatImpl) msg );
            break;
        case FullEventIds.ID_ENDOFSESSION:
            _endOfSessionRecycler.recycle( (EndOfSessionImpl) msg );
            break;
        case FullEventIds.ID_LOGOUTREQUEST:
            _logoutRequestRecycler.recycle( (LogoutRequestImpl) msg );
            break;
        case FullEventIds.ID_TESTREQUEST:
            _testRequestRecycler.recycle( (TestRequestImpl) msg );
            break;
        case FullEventIds.ID_LOGON:
            _logonRecycler.recycle( (LogonImpl) msg );
            break;
        case FullEventIds.ID_LOGOUT:
            _logoutRecycler.recycle( (LogoutImpl) msg );
            break;
        case FullEventIds.ID_SESSIONREJECT:
            _sessionRejectRecycler.recycle( (SessionRejectImpl) msg );
            break;
        case FullEventIds.ID_RESENDREQUEST:
            _resendRequestRecycler.recycle( (ResendRequestImpl) msg );
            break;
        case FullEventIds.ID_CLIENTRESYNCSENTMSGS:
            _clientResyncSentMsgsRecycler.recycle( (ClientResyncSentMsgsImpl) msg );
            break;
        case FullEventIds.ID_SEQUENCERESET:
            _sequenceResetRecycler.recycle( (SequenceResetImpl) msg );
            break;
        case FullEventIds.ID_TRADINGSESSIONSTATUS:
            _tradingSessionStatusRecycler.recycle( (TradingSessionStatusImpl) msg );
            break;
        case FullEventIds.ID_SECMASSSTATGRP:
            _secMassStatGrpRecycler.recycle( (SecMassStatGrpImpl) msg );
            break;
        case FullEventIds.ID_MASSINSTRUMENTSTATECHANGE:
            _massInstrumentStateChangeRecycler.recycle( (MassInstrumentStateChangeImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_NEWORDERSINGLE:
            _clientNewOrderSingleRecycler.recycle( (ClientNewOrderSingleImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_CANCELREPLACEREQUEST:
            _clientCancelReplaceRequestRecycler.recycle( (ClientCancelReplaceRequestImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_CANCELREQUEST:
            _clientCancelRequestRecycler.recycle( (ClientCancelRequestImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_FORCECANCEL:
            _clientForceCancelRecycler.recycle( (ClientForceCancelImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_VAGUEORDERREJECT:
            _clientVagueOrderRejectRecycler.recycle( (ClientVagueOrderRejectImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_CANCELREJECT:
            _clientCancelRejectRecycler.recycle( (ClientCancelRejectImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_NEWORDERACK:
            _clientNewOrderAckRecycler.recycle( (ClientNewOrderAckImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_TRADENEW:
            _clientTradeNewRecycler.recycle( (ClientTradeNewImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_REJECTED:
            _clientRejectedRecycler.recycle( (ClientRejectedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_CANCELLED:
            _clientCancelledRecycler.recycle( (ClientCancelledImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_REPLACED:
            _clientReplacedRecycler.recycle( (ClientReplacedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_DONEFORDAY:
            _clientDoneForDayRecycler.recycle( (ClientDoneForDayImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_STOPPED:
            _clientStoppedRecycler.recycle( (ClientStoppedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_EXPIRED:
            _clientExpiredRecycler.recycle( (ClientExpiredImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_SUSPENDED:
            _clientSuspendedRecycler.recycle( (ClientSuspendedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_RESTATED:
            _clientRestatedRecycler.recycle( (ClientRestatedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_TRADECORRECT:
            _clientTradeCorrectRecycler.recycle( (ClientTradeCorrectImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_TRADECANCEL:
            _clientTradeCancelRecycler.recycle( (ClientTradeCancelImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_PENDINGCANCEL:
            _clientPendingCancelRecycler.recycle( (ClientPendingCancelImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_PENDINGREPLACE:
            _clientPendingReplaceRecycler.recycle( (ClientPendingReplaceImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_PENDINGNEW:
            _clientPendingNewRecycler.recycle( (ClientPendingNewImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_ORDERSTATUS:
            _clientOrderStatusRecycler.recycle( (ClientOrderStatusImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_IGNOREDEXEC:
            _clientIgnoredExecRecycler.recycle( (ClientIgnoredExecImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_CALCULATED:
            _clientCalculatedRecycler.recycle( (ClientCalculatedImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_ALERTLIMITBREACH:
            _clientAlertLimitBreachRecycler.recycle( (ClientAlertLimitBreachImpl) msg );
            break;
        case FullEventIds.ID_CLIENT_ALERTTRADEMISSINGORDERS:
            _clientAlertTradeMissingOrdersRecycler.recycle( (ClientAlertTradeMissingOrdersImpl) msg );
            break;
        case FullEventIds.ID_SYMBOLREPEATINGGRP:
            _symbolRepeatingGrpRecycler.recycle( (SymbolRepeatingGrpImpl) msg );
            break;
        case FullEventIds.ID_MDREQUEST:
            _mDRequestRecycler.recycle( (MDRequestImpl) msg );
            break;
        case FullEventIds.ID_TICKUPDATE:
            _tickUpdateRecycler.recycle( (TickUpdateImpl) msg );
            break;
        case FullEventIds.ID_MDUPDATE:
            _mDUpdateRecycler.recycle( (MDUpdateImpl) msg );
            break;
        case FullEventIds.ID_SECDEFEVENT:
            _secDefEventRecycler.recycle( (SecDefEventImpl) msg );
            break;
        case FullEventIds.ID_SECURITYALTID:
            _securityAltIDRecycler.recycle( (SecurityAltIDImpl) msg );
            break;
        case FullEventIds.ID_SDFEEDTYPE:
            _sDFeedTypeRecycler.recycle( (SDFeedTypeImpl) msg );
            break;
        case FullEventIds.ID_SECDEFLEG:
            _secDefLegRecycler.recycle( (SecDefLegImpl) msg );
            break;
        case FullEventIds.ID_MDENTRY:
            _mDEntryRecycler.recycle( (MDEntryImpl) msg );
            break;
        case FullEventIds.ID_MDSNAPENTRY:
            _mDSnapEntryRecycler.recycle( (MDSnapEntryImpl) msg );
            break;
        case FullEventIds.ID_MSGSEQNUMGAP:
            _msgSeqNumGapRecycler.recycle( (MsgSeqNumGapImpl) msg );
            break;
        case FullEventIds.ID_MDINCREFRESH:
            _mDIncRefreshRecycler.recycle( (MDIncRefreshImpl) msg );
            break;
        case FullEventIds.ID_MDSNAPSHOTFULLREFRESH:
            _mDSnapshotFullRefreshRecycler.recycle( (MDSnapshotFullRefreshImpl) msg );
            break;
        case FullEventIds.ID_SECURITYDEFINITION:
            _securityDefinitionRecycler.recycle( (SecurityDefinitionImpl) msg );
            break;
        case FullEventIds.ID_SECURITYDEFINITIONUPDATE:
            _securityDefinitionUpdateRecycler.recycle( (SecurityDefinitionUpdateImpl) msg );
            break;
        case FullEventIds.ID_PRODUCTSNAPSHOT:
            _productSnapshotRecycler.recycle( (ProductSnapshotImpl) msg );
            break;
        case FullEventIds.ID_SECURITYSTATUS:
            _securityStatusRecycler.recycle( (SecurityStatusImpl) msg );
            break;
        case FullEventIds.ID_SETTLEMENTPRICEEVENT:
            _settlementPriceEventRecycler.recycle( (SettlementPriceEventImpl) msg );
            break;
        case FullEventIds.ID_CLOSINGPRICEEVENT:
            _closingPriceEventRecycler.recycle( (ClosingPriceEventImpl) msg );
            break;
        case FullEventIds.ID_OPENPRICEEVENT:
            _openPriceEventRecycler.recycle( (OpenPriceEventImpl) msg );
            break;
        case FullEventIds.ID_OPENINTERESTEVENT:
            _openInterestEventRecycler.recycle( (OpenInterestEventImpl) msg );
            break;
        case FullEventIds.ID_NEWS:
            _newsRecycler.recycle( (NewsImpl) msg );
            break;
        case FullEventIds.ID_CORPORATEACTIONEVENT:
            _corporateActionEventRecycler.recycle( (CorporateActionEventImpl) msg );
            break;
        case FullEventIds.ID_INSTRUMENTSIMDATA:
            _instrumentSimDataRecycler.recycle( (InstrumentSimDataImpl) msg );
            break;
        case FullEventIds.ID_REFPRICEEVENT:
            _refPriceEventRecycler.recycle( (RefPriceEventImpl) msg );
            break;
        case FullEventIds.ID_BROKERLOANRESPONSE:
            _brokerLoanResponseRecycler.recycle( (BrokerLoanResponseImpl) msg );
            break;
        case FullEventIds.ID_PRICELIMITCOLLAREVENT:
            _priceLimitCollarEventRecycler.recycle( (PriceLimitCollarEventImpl) msg );
            break;
        case FullEventIds.ID_SECURITYTRADINGSTATUSEVENT:
            _securityTradingStatusEventRecycler.recycle( (SecurityTradingStatusEventImpl) msg );
            break;
        case FullEventIds.ID_LEANHOGINDEXEVENT:
            _leanHogIndexEventRecycler.recycle( (LeanHogIndexEventImpl) msg );
            break;
        case FullEventIds.ID_FORCEFLATTENCOMMAND:
            _forceFlattenCommandRecycler.recycle( (ForceFlattenCommandImpl) msg );
            break;
        case FullEventIds.ID_APPRUN:
            _appRunRecycler.recycle( (AppRunImpl) msg );
            break;
        case FullEventIds.ID_STRATINSTRUMENT:
            _stratInstrumentRecycler.recycle( (StratInstrumentImpl) msg );
            break;
        case FullEventIds.ID_STRATEGYRUN:
            _strategyRunRecycler.recycle( (StrategyRunImpl) msg );
            break;
        case FullEventIds.ID_STRATINSTRUMENTSTATE:
            _stratInstrumentStateRecycler.recycle( (StratInstrumentStateImpl) msg );
            break;
        case FullEventIds.ID_STRATEGYSTATE:
            _strategyStateRecycler.recycle( (StrategyStateImpl) msg );
            break;
        case FullEventIds.ID_UTPLOGON:
            _uTPLogonRecycler.recycle( (UTPLogonImpl) msg );
            break;
        case FullEventIds.ID_UTPLOGONREJECT:
            _uTPLogonRejectRecycler.recycle( (UTPLogonRejectImpl) msg );
            break;
        case FullEventIds.ID_UTPTRADINGSESSIONSTATUS:
            _uTPTradingSessionStatusRecycler.recycle( (UTPTradingSessionStatusImpl) msg );
            break;
        case FullEventIds.ID_ETICONNECTIONGATEWAYREQUEST:
            _eTIConnectionGatewayRequestRecycler.recycle( (ETIConnectionGatewayRequestImpl) msg );
            break;
        case FullEventIds.ID_ETICONNECTIONGATEWAYRESPONSE:
            _eTIConnectionGatewayResponseRecycler.recycle( (ETIConnectionGatewayResponseImpl) msg );
            break;
        case FullEventIds.ID_ETISESSIONLOGONREQUEST:
            _eTISessionLogonRequestRecycler.recycle( (ETISessionLogonRequestImpl) msg );
            break;
        case FullEventIds.ID_ETISESSIONLOGONRESPONSE:
            _eTISessionLogonResponseRecycler.recycle( (ETISessionLogonResponseImpl) msg );
            break;
        case FullEventIds.ID_ETISESSIONLOGOUTREQUEST:
            _eTISessionLogoutRequestRecycler.recycle( (ETISessionLogoutRequestImpl) msg );
            break;
        case FullEventIds.ID_ETISESSIONLOGOUTRESPONSE:
            _eTISessionLogoutResponseRecycler.recycle( (ETISessionLogoutResponseImpl) msg );
            break;
        case FullEventIds.ID_ETISESSIONLOGOUTNOTIFICATION:
            _eTISessionLogoutNotificationRecycler.recycle( (ETISessionLogoutNotificationImpl) msg );
            break;
        case FullEventIds.ID_ETIUSERLOGONREQUEST:
            _eTIUserLogonRequestRecycler.recycle( (ETIUserLogonRequestImpl) msg );
            break;
        case FullEventIds.ID_ETIUSERLOGONRESPONSE:
            _eTIUserLogonResponseRecycler.recycle( (ETIUserLogonResponseImpl) msg );
            break;
        case FullEventIds.ID_ETIUSERLOGOUTREQUEST:
            _eTIUserLogoutRequestRecycler.recycle( (ETIUserLogoutRequestImpl) msg );
            break;
        case FullEventIds.ID_ETIUSERLOGOUTRESPONSE:
            _eTIUserLogoutResponseRecycler.recycle( (ETIUserLogoutResponseImpl) msg );
            break;
        case FullEventIds.ID_ETITHROTTLEUPDATENOTIFICATION:
            _eTIThrottleUpdateNotificationRecycler.recycle( (ETIThrottleUpdateNotificationImpl) msg );
            break;
        case FullEventIds.ID_ETISUBSCRIBE:
            _eTISubscribeRecycler.recycle( (ETISubscribeImpl) msg );
            break;
        case FullEventIds.ID_ETISUBSCRIBERESPONSE:
            _eTISubscribeResponseRecycler.recycle( (ETISubscribeResponseImpl) msg );
            break;
        case FullEventIds.ID_ETIUNSUBSCRIBE:
            _eTIUnsubscribeRecycler.recycle( (ETIUnsubscribeImpl) msg );
            break;
        case FullEventIds.ID_ETIUNSUBSCRIBERESPONSE:
            _eTIUnsubscribeResponseRecycler.recycle( (ETIUnsubscribeResponseImpl) msg );
            break;
        case FullEventIds.ID_ETIRETRANSMIT:
            _eTIRetransmitRecycler.recycle( (ETIRetransmitImpl) msg );
            break;
        case FullEventIds.ID_ETIRETRANSMITRESPONSE:
            _eTIRetransmitResponseRecycler.recycle( (ETIRetransmitResponseImpl) msg );
            break;
        case FullEventIds.ID_ETIRETRANSMITORDEREVENTS:
            _eTIRetransmitOrderEventsRecycler.recycle( (ETIRetransmitOrderEventsImpl) msg );
            break;
        case FullEventIds.ID_ETIRETRANSMITORDEREVENTSRESPONSE:
            _eTIRetransmitOrderEventsResponseRecycler.recycle( (ETIRetransmitOrderEventsResponseImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMLOGON:
            _milleniumLogonRecycler.recycle( (MilleniumLogonImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMLOGONREPLY:
            _milleniumLogonReplyRecycler.recycle( (MilleniumLogonReplyImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMLOGOUT:
            _milleniumLogoutRecycler.recycle( (MilleniumLogoutImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMMISSEDMESSAGEREQUEST:
            _milleniumMissedMessageRequestRecycler.recycle( (MilleniumMissedMessageRequestImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMMISSEDMSGREQUESTACK:
            _milleniumMissedMsgRequestAckRecycler.recycle( (MilleniumMissedMsgRequestAckImpl) msg );
            break;
        case FullEventIds.ID_MILLENIUMMISSEDMSGREPORT:
            _milleniumMissedMsgReportRecycler.recycle( (MilleniumMissedMsgReportImpl) msg );
            break;
        case FullEventIds.ID_BOOKADDORDER:
            _bookAddOrderRecycler.recycle( (BookAddOrderImpl) msg );
            break;
        case FullEventIds.ID_BOOKDELETEORDER:
            _bookDeleteOrderRecycler.recycle( (BookDeleteOrderImpl) msg );
            break;
        case FullEventIds.ID_BOOKMODIFYORDER:
            _bookModifyOrderRecycler.recycle( (BookModifyOrderImpl) msg );
            break;
        case FullEventIds.ID_BOOKCLEAR:
            _bookClearRecycler.recycle( (BookClearImpl) msg );
            break;
        case FullEventIds.ID_PITCHSYMBOLCLEAR:
            _pitchSymbolClearRecycler.recycle( (PitchSymbolClearImpl) msg );
            break;
        case FullEventIds.ID_PITCHBOOKADDORDER:
            _pitchBookAddOrderRecycler.recycle( (PitchBookAddOrderImpl) msg );
            break;
        case FullEventIds.ID_PITCHBOOKORDEREXECUTED:
            _pitchBookOrderExecutedRecycler.recycle( (PitchBookOrderExecutedImpl) msg );
            break;
        case FullEventIds.ID_PITCHOFFBOOKTRADE:
            _pitchOffBookTradeRecycler.recycle( (PitchOffBookTradeImpl) msg );
            break;
        case FullEventIds.ID_PITCHBOOKCANCELORDER:
            _pitchBookCancelOrderRecycler.recycle( (PitchBookCancelOrderImpl) msg );
            break;
        case FullEventIds.ID_PITCHPRICESTATISTIC:
            _pitchPriceStatisticRecycler.recycle( (PitchPriceStatisticImpl) msg );
            break;
        case FullEventIds.ID_AUCTIONUPDATE:
            _auctionUpdateRecycler.recycle( (AuctionUpdateImpl) msg );
            break;
        case FullEventIds.ID_AUCTIONSUMMARY:
            _auctionSummaryRecycler.recycle( (AuctionSummaryImpl) msg );
            break;
        case FullEventIds.ID_SOUPDEBUGPACKET:
            _soupDebugPacketRecycler.recycle( (SoupDebugPacketImpl) msg );
            break;
        case FullEventIds.ID_SOUPLOGINACCEPTED:
            _soupLogInAcceptedRecycler.recycle( (SoupLogInAcceptedImpl) msg );
            break;
        case FullEventIds.ID_SOUPLOGINREJECTED:
            _soupLogInRejectedRecycler.recycle( (SoupLogInRejectedImpl) msg );
            break;
        case FullEventIds.ID_SOUPLOGINREQUEST:
            _soupLogInRequestRecycler.recycle( (SoupLogInRequestImpl) msg );
            break;
        case FullEventIds.ID_UNSEQUENCEDDATAPACKET:
            _unsequencedDataPacketRecycler.recycle( (UnsequencedDataPacketImpl) msg );
            break;
        case FullEventIds.ID_MARKET_NEWORDERSINGLE:
            _marketNewOrderSingleRecycler.recycle( (MarketNewOrderSingleImpl) msg );
            break;
        case FullEventIds.ID_MARKET_CANCELREPLACEREQUEST:
            _marketCancelReplaceRequestRecycler.recycle( (MarketCancelReplaceRequestImpl) msg );
            break;
        case FullEventIds.ID_MARKET_CANCELREQUEST:
            _marketCancelRequestRecycler.recycle( (MarketCancelRequestImpl) msg );
            break;
        case FullEventIds.ID_MARKET_FORCECANCEL:
            _marketForceCancelRecycler.recycle( (MarketForceCancelImpl) msg );
            break;
        case FullEventIds.ID_MARKET_VAGUEORDERREJECT:
            _marketVagueOrderRejectRecycler.recycle( (MarketVagueOrderRejectImpl) msg );
            break;
        case FullEventIds.ID_MARKET_CANCELREJECT:
            _marketCancelRejectRecycler.recycle( (MarketCancelRejectImpl) msg );
            break;
        case FullEventIds.ID_MARKET_NEWORDERACK:
            _marketNewOrderAckRecycler.recycle( (MarketNewOrderAckImpl) msg );
            break;
        case FullEventIds.ID_MARKET_TRADENEW:
            _marketTradeNewRecycler.recycle( (MarketTradeNewImpl) msg );
            break;
        case FullEventIds.ID_MARKET_REJECTED:
            _marketRejectedRecycler.recycle( (MarketRejectedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_CANCELLED:
            _marketCancelledRecycler.recycle( (MarketCancelledImpl) msg );
            break;
        case FullEventIds.ID_MARKET_REPLACED:
            _marketReplacedRecycler.recycle( (MarketReplacedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_DONEFORDAY:
            _marketDoneForDayRecycler.recycle( (MarketDoneForDayImpl) msg );
            break;
        case FullEventIds.ID_MARKET_STOPPED:
            _marketStoppedRecycler.recycle( (MarketStoppedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_EXPIRED:
            _marketExpiredRecycler.recycle( (MarketExpiredImpl) msg );
            break;
        case FullEventIds.ID_MARKET_SUSPENDED:
            _marketSuspendedRecycler.recycle( (MarketSuspendedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_RESTATED:
            _marketRestatedRecycler.recycle( (MarketRestatedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_TRADECORRECT:
            _marketTradeCorrectRecycler.recycle( (MarketTradeCorrectImpl) msg );
            break;
        case FullEventIds.ID_MARKET_TRADECANCEL:
            _marketTradeCancelRecycler.recycle( (MarketTradeCancelImpl) msg );
            break;
        case FullEventIds.ID_MARKET_PENDINGCANCEL:
            _marketPendingCancelRecycler.recycle( (MarketPendingCancelImpl) msg );
            break;
        case FullEventIds.ID_MARKET_PENDINGREPLACE:
            _marketPendingReplaceRecycler.recycle( (MarketPendingReplaceImpl) msg );
            break;
        case FullEventIds.ID_MARKET_PENDINGNEW:
            _marketPendingNewRecycler.recycle( (MarketPendingNewImpl) msg );
            break;
        case FullEventIds.ID_MARKET_ORDERSTATUS:
            _marketOrderStatusRecycler.recycle( (MarketOrderStatusImpl) msg );
            break;
        case FullEventIds.ID_MARKET_IGNOREDEXEC:
            _marketIgnoredExecRecycler.recycle( (MarketIgnoredExecImpl) msg );
            break;
        case FullEventIds.ID_MARKET_CALCULATED:
            _marketCalculatedRecycler.recycle( (MarketCalculatedImpl) msg );
            break;
        case FullEventIds.ID_MARKET_ALERTLIMITBREACH:
            _marketAlertLimitBreachRecycler.recycle( (MarketAlertLimitBreachImpl) msg );
            break;
        case FullEventIds.ID_MARKET_ALERTTRADEMISSINGORDERS:
            _marketAlertTradeMissingOrdersRecycler.recycle( (MarketAlertTradeMissingOrdersImpl) msg );
            break;
        case FullEventIds.ID_NEWORDERSINGLE:
            _newOrderSingleRecycler.recycle( (NewOrderSingleImpl) msg );
            break;
        case FullEventIds.ID_CANCELREPLACEREQUEST:
            _cancelReplaceRequestRecycler.recycle( (CancelReplaceRequestImpl) msg );
            break;
        case FullEventIds.ID_CANCELREQUEST:
            _cancelRequestRecycler.recycle( (CancelRequestImpl) msg );
            break;
        case FullEventIds.ID_FORCECANCEL:
            _forceCancelRecycler.recycle( (ForceCancelImpl) msg );
            break;
        case FullEventIds.ID_VAGUEORDERREJECT:
            _vagueOrderRejectRecycler.recycle( (VagueOrderRejectImpl) msg );
            break;
        case FullEventIds.ID_CANCELREJECT:
            _cancelRejectRecycler.recycle( (CancelRejectImpl) msg );
            break;
        case FullEventIds.ID_NEWORDERACK:
            _newOrderAckRecycler.recycle( (NewOrderAckImpl) msg );
            break;
        case FullEventIds.ID_TRADENEW:
            _tradeNewRecycler.recycle( (TradeNewImpl) msg );
            break;
        case FullEventIds.ID_REJECTED:
            _rejectedRecycler.recycle( (RejectedImpl) msg );
            break;
        case FullEventIds.ID_CANCELLED:
            _cancelledRecycler.recycle( (CancelledImpl) msg );
            break;
        case FullEventIds.ID_REPLACED:
            _replacedRecycler.recycle( (ReplacedImpl) msg );
            break;
        case FullEventIds.ID_DONEFORDAY:
            _doneForDayRecycler.recycle( (DoneForDayImpl) msg );
            break;
        case FullEventIds.ID_STOPPED:
            _stoppedRecycler.recycle( (StoppedImpl) msg );
            break;
        case FullEventIds.ID_EXPIRED:
            _expiredRecycler.recycle( (ExpiredImpl) msg );
            break;
        case FullEventIds.ID_SUSPENDED:
            _suspendedRecycler.recycle( (SuspendedImpl) msg );
            break;
        case FullEventIds.ID_RESTATED:
            _restatedRecycler.recycle( (RestatedImpl) msg );
            break;
        case FullEventIds.ID_TRADECORRECT:
            _tradeCorrectRecycler.recycle( (TradeCorrectImpl) msg );
            break;
        case FullEventIds.ID_TRADECANCEL:
            _tradeCancelRecycler.recycle( (TradeCancelImpl) msg );
            break;
        case FullEventIds.ID_PENDINGCANCEL:
            _pendingCancelRecycler.recycle( (PendingCancelImpl) msg );
            break;
        case FullEventIds.ID_PENDINGREPLACE:
            _pendingReplaceRecycler.recycle( (PendingReplaceImpl) msg );
            break;
        case FullEventIds.ID_PENDINGNEW:
            _pendingNewRecycler.recycle( (PendingNewImpl) msg );
            break;
        case FullEventIds.ID_ORDERSTATUS:
            _orderStatusRecycler.recycle( (OrderStatusImpl) msg );
            break;
        case FullEventIds.ID_IGNOREDEXEC:
            _ignoredExecRecycler.recycle( (IgnoredExecImpl) msg );
            break;
        case FullEventIds.ID_CALCULATED:
            _calculatedRecycler.recycle( (CalculatedImpl) msg );
            break;
        case FullEventIds.ID_ALERTLIMITBREACH:
            _alertLimitBreachRecycler.recycle( (AlertLimitBreachImpl) msg );
            break;
        case FullEventIds.ID_ALERTTRADEMISSINGORDERS:
            _alertTradeMissingOrdersRecycler.recycle( (AlertTradeMissingOrdersImpl) msg );
            break;
        }
    }

}
