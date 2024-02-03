package com.rr.model.generated.internal.core;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


import com.rr.core.lang.ReusableCategory;


import com.rr.core.lang.ReusableCategoryEnum;


import com.rr.core.lang.ReusableType;


import com.rr.core.lang.ReusableTypeIDFactory;


import javax.annotation.Generated;

@Generated( "com.rr.model.generated.internal.core.ModelReusableTypes" )

public enum ModelReusableTypes implements ReusableType {

    Heartbeat( ReusableCategoryEnum.Event, FullEventIds.ID_HEARTBEAT, EventIds.ID_HEARTBEAT ),
    EndOfSession( ReusableCategoryEnum.Event, FullEventIds.ID_ENDOFSESSION, EventIds.ID_ENDOFSESSION ),
    LogoutRequest( ReusableCategoryEnum.Event, FullEventIds.ID_LOGOUTREQUEST, EventIds.ID_LOGOUTREQUEST ),
    TestRequest( ReusableCategoryEnum.Event, FullEventIds.ID_TESTREQUEST, EventIds.ID_TESTREQUEST ),
    Logon( ReusableCategoryEnum.Event, FullEventIds.ID_LOGON, EventIds.ID_LOGON ),
    Logout( ReusableCategoryEnum.Event, FullEventIds.ID_LOGOUT, EventIds.ID_LOGOUT ),
    SessionReject( ReusableCategoryEnum.Event, FullEventIds.ID_SESSIONREJECT, EventIds.ID_SESSIONREJECT ),
    ResendRequest( ReusableCategoryEnum.Event, FullEventIds.ID_RESENDREQUEST, EventIds.ID_RESENDREQUEST ),
    ClientResyncSentMsgs( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENTRESYNCSENTMSGS, EventIds.ID_CLIENTRESYNCSENTMSGS ),
    SequenceReset( ReusableCategoryEnum.Event, FullEventIds.ID_SEQUENCERESET, EventIds.ID_SEQUENCERESET ),
    TradingSessionStatus( ReusableCategoryEnum.Event, FullEventIds.ID_TRADINGSESSIONSTATUS, EventIds.ID_TRADINGSESSIONSTATUS ),
    SecMassStatGrp( ReusableCategoryEnum.Event, FullEventIds.ID_SECMASSSTATGRP, EventIds.ID_SECMASSSTATGRP ),
    MassInstrumentStateChange( ReusableCategoryEnum.Event, FullEventIds.ID_MASSINSTRUMENTSTATECHANGE, EventIds.ID_MASSINSTRUMENTSTATECHANGE ),
    NewOrderSingle( ReusableCategoryEnum.Event, FullEventIds.ID_NEWORDERSINGLE, EventIds.ID_NEWORDERSINGLE ),
    ClientNewOrderSingle( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_NEWORDERSINGLE, EventIds.ID_NEWORDERSINGLE ),
    MarketNewOrderSingle( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_NEWORDERSINGLE, EventIds.ID_NEWORDERSINGLE ),
    CancelReplaceRequest( ReusableCategoryEnum.Event, FullEventIds.ID_CANCELREPLACEREQUEST, EventIds.ID_CANCELREPLACEREQUEST ),
    ClientCancelReplaceRequest( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_CANCELREPLACEREQUEST, EventIds.ID_CANCELREPLACEREQUEST ),
    MarketCancelReplaceRequest( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_CANCELREPLACEREQUEST, EventIds.ID_CANCELREPLACEREQUEST ),
    CancelRequest( ReusableCategoryEnum.Event, FullEventIds.ID_CANCELREQUEST, EventIds.ID_CANCELREQUEST ),
    ClientCancelRequest( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_CANCELREQUEST, EventIds.ID_CANCELREQUEST ),
    MarketCancelRequest( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_CANCELREQUEST, EventIds.ID_CANCELREQUEST ),
    ForceCancel( ReusableCategoryEnum.Event, FullEventIds.ID_FORCECANCEL, EventIds.ID_FORCECANCEL ),
    ClientForceCancel( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_FORCECANCEL, EventIds.ID_FORCECANCEL ),
    MarketForceCancel( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_FORCECANCEL, EventIds.ID_FORCECANCEL ),
    VagueOrderReject( ReusableCategoryEnum.Event, FullEventIds.ID_VAGUEORDERREJECT, EventIds.ID_VAGUEORDERREJECT ),
    ClientVagueOrderReject( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_VAGUEORDERREJECT, EventIds.ID_VAGUEORDERREJECT ),
    MarketVagueOrderReject( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_VAGUEORDERREJECT, EventIds.ID_VAGUEORDERREJECT ),
    CancelReject( ReusableCategoryEnum.Event, FullEventIds.ID_CANCELREJECT, EventIds.ID_CANCELREJECT ),
    ClientCancelReject( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_CANCELREJECT, EventIds.ID_CANCELREJECT ),
    MarketCancelReject( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_CANCELREJECT, EventIds.ID_CANCELREJECT ),
    NewOrderAck( ReusableCategoryEnum.Event, FullEventIds.ID_NEWORDERACK, EventIds.ID_NEWORDERACK ),
    ClientNewOrderAck( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_NEWORDERACK, EventIds.ID_NEWORDERACK ),
    MarketNewOrderAck( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_NEWORDERACK, EventIds.ID_NEWORDERACK ),
    TradeNew( ReusableCategoryEnum.Event, FullEventIds.ID_TRADENEW, EventIds.ID_TRADENEW ),
    ClientTradeNew( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_TRADENEW, EventIds.ID_TRADENEW ),
    MarketTradeNew( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_TRADENEW, EventIds.ID_TRADENEW ),
    Rejected( ReusableCategoryEnum.Event, FullEventIds.ID_REJECTED, EventIds.ID_REJECTED ),
    ClientRejected( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_REJECTED, EventIds.ID_REJECTED ),
    MarketRejected( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_REJECTED, EventIds.ID_REJECTED ),
    Cancelled( ReusableCategoryEnum.Event, FullEventIds.ID_CANCELLED, EventIds.ID_CANCELLED ),
    ClientCancelled( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_CANCELLED, EventIds.ID_CANCELLED ),
    MarketCancelled( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_CANCELLED, EventIds.ID_CANCELLED ),
    Replaced( ReusableCategoryEnum.Event, FullEventIds.ID_REPLACED, EventIds.ID_REPLACED ),
    ClientReplaced( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_REPLACED, EventIds.ID_REPLACED ),
    MarketReplaced( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_REPLACED, EventIds.ID_REPLACED ),
    DoneForDay( ReusableCategoryEnum.Event, FullEventIds.ID_DONEFORDAY, EventIds.ID_DONEFORDAY ),
    ClientDoneForDay( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_DONEFORDAY, EventIds.ID_DONEFORDAY ),
    MarketDoneForDay( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_DONEFORDAY, EventIds.ID_DONEFORDAY ),
    Stopped( ReusableCategoryEnum.Event, FullEventIds.ID_STOPPED, EventIds.ID_STOPPED ),
    ClientStopped( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_STOPPED, EventIds.ID_STOPPED ),
    MarketStopped( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_STOPPED, EventIds.ID_STOPPED ),
    Expired( ReusableCategoryEnum.Event, FullEventIds.ID_EXPIRED, EventIds.ID_EXPIRED ),
    ClientExpired( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_EXPIRED, EventIds.ID_EXPIRED ),
    MarketExpired( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_EXPIRED, EventIds.ID_EXPIRED ),
    Suspended( ReusableCategoryEnum.Event, FullEventIds.ID_SUSPENDED, EventIds.ID_SUSPENDED ),
    ClientSuspended( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_SUSPENDED, EventIds.ID_SUSPENDED ),
    MarketSuspended( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_SUSPENDED, EventIds.ID_SUSPENDED ),
    Restated( ReusableCategoryEnum.Event, FullEventIds.ID_RESTATED, EventIds.ID_RESTATED ),
    ClientRestated( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_RESTATED, EventIds.ID_RESTATED ),
    MarketRestated( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_RESTATED, EventIds.ID_RESTATED ),
    TradeCorrect( ReusableCategoryEnum.Event, FullEventIds.ID_TRADECORRECT, EventIds.ID_TRADECORRECT ),
    ClientTradeCorrect( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_TRADECORRECT, EventIds.ID_TRADECORRECT ),
    MarketTradeCorrect( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_TRADECORRECT, EventIds.ID_TRADECORRECT ),
    TradeCancel( ReusableCategoryEnum.Event, FullEventIds.ID_TRADECANCEL, EventIds.ID_TRADECANCEL ),
    ClientTradeCancel( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_TRADECANCEL, EventIds.ID_TRADECANCEL ),
    MarketTradeCancel( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_TRADECANCEL, EventIds.ID_TRADECANCEL ),
    PendingCancel( ReusableCategoryEnum.Event, FullEventIds.ID_PENDINGCANCEL, EventIds.ID_PENDINGCANCEL ),
    ClientPendingCancel( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_PENDINGCANCEL, EventIds.ID_PENDINGCANCEL ),
    MarketPendingCancel( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_PENDINGCANCEL, EventIds.ID_PENDINGCANCEL ),
    PendingReplace( ReusableCategoryEnum.Event, FullEventIds.ID_PENDINGREPLACE, EventIds.ID_PENDINGREPLACE ),
    ClientPendingReplace( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_PENDINGREPLACE, EventIds.ID_PENDINGREPLACE ),
    MarketPendingReplace( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_PENDINGREPLACE, EventIds.ID_PENDINGREPLACE ),
    PendingNew( ReusableCategoryEnum.Event, FullEventIds.ID_PENDINGNEW, EventIds.ID_PENDINGNEW ),
    ClientPendingNew( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_PENDINGNEW, EventIds.ID_PENDINGNEW ),
    MarketPendingNew( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_PENDINGNEW, EventIds.ID_PENDINGNEW ),
    OrderStatus( ReusableCategoryEnum.Event, FullEventIds.ID_ORDERSTATUS, EventIds.ID_ORDERSTATUS ),
    ClientOrderStatus( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_ORDERSTATUS, EventIds.ID_ORDERSTATUS ),
    MarketOrderStatus( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_ORDERSTATUS, EventIds.ID_ORDERSTATUS ),
    IgnoredExec( ReusableCategoryEnum.Event, FullEventIds.ID_IGNOREDEXEC, EventIds.ID_IGNOREDEXEC ),
    ClientIgnoredExec( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_IGNOREDEXEC, EventIds.ID_IGNOREDEXEC ),
    MarketIgnoredExec( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_IGNOREDEXEC, EventIds.ID_IGNOREDEXEC ),
    Calculated( ReusableCategoryEnum.Event, FullEventIds.ID_CALCULATED, EventIds.ID_CALCULATED ),
    ClientCalculated( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_CALCULATED, EventIds.ID_CALCULATED ),
    MarketCalculated( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_CALCULATED, EventIds.ID_CALCULATED ),
    AlertLimitBreach( ReusableCategoryEnum.Event, FullEventIds.ID_ALERTLIMITBREACH, EventIds.ID_ALERTLIMITBREACH ),
    ClientAlertLimitBreach( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_ALERTLIMITBREACH, EventIds.ID_ALERTLIMITBREACH ),
    MarketAlertLimitBreach( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_ALERTLIMITBREACH, EventIds.ID_ALERTLIMITBREACH ),
    AlertTradeMissingOrders( ReusableCategoryEnum.Event, FullEventIds.ID_ALERTTRADEMISSINGORDERS, EventIds.ID_ALERTTRADEMISSINGORDERS ),
    ClientAlertTradeMissingOrders( ReusableCategoryEnum.Event, FullEventIds.ID_CLIENT_ALERTTRADEMISSINGORDERS, EventIds.ID_ALERTTRADEMISSINGORDERS ),
    MarketAlertTradeMissingOrders( ReusableCategoryEnum.Event, FullEventIds.ID_MARKET_ALERTTRADEMISSINGORDERS, EventIds.ID_ALERTTRADEMISSINGORDERS ),
    SymbolRepeatingGrp( ReusableCategoryEnum.Event, FullEventIds.ID_SYMBOLREPEATINGGRP, EventIds.ID_SYMBOLREPEATINGGRP ),
    MDRequest( ReusableCategoryEnum.Event, FullEventIds.ID_MDREQUEST, EventIds.ID_MDREQUEST ),
    TickUpdate( ReusableCategoryEnum.Event, FullEventIds.ID_TICKUPDATE, EventIds.ID_TICKUPDATE ),
    MDUpdate( ReusableCategoryEnum.Event, FullEventIds.ID_MDUPDATE, EventIds.ID_MDUPDATE ),
    SecDefEvent( ReusableCategoryEnum.Event, FullEventIds.ID_SECDEFEVENT, EventIds.ID_SECDEFEVENT ),
    SecurityAltID( ReusableCategoryEnum.Event, FullEventIds.ID_SECURITYALTID, EventIds.ID_SECURITYALTID ),
    SDFeedType( ReusableCategoryEnum.Event, FullEventIds.ID_SDFEEDTYPE, EventIds.ID_SDFEEDTYPE ),
    SecDefLeg( ReusableCategoryEnum.Event, FullEventIds.ID_SECDEFLEG, EventIds.ID_SECDEFLEG ),
    MDEntry( ReusableCategoryEnum.Event, FullEventIds.ID_MDENTRY, EventIds.ID_MDENTRY ),
    MDSnapEntry( ReusableCategoryEnum.Event, FullEventIds.ID_MDSNAPENTRY, EventIds.ID_MDSNAPENTRY ),
    MsgSeqNumGap( ReusableCategoryEnum.Event, FullEventIds.ID_MSGSEQNUMGAP, EventIds.ID_MSGSEQNUMGAP ),
    MDIncRefresh( ReusableCategoryEnum.Event, FullEventIds.ID_MDINCREFRESH, EventIds.ID_MDINCREFRESH ),
    MDSnapshotFullRefresh( ReusableCategoryEnum.Event, FullEventIds.ID_MDSNAPSHOTFULLREFRESH, EventIds.ID_MDSNAPSHOTFULLREFRESH ),
    SecurityDefinition( ReusableCategoryEnum.Event, FullEventIds.ID_SECURITYDEFINITION, EventIds.ID_SECURITYDEFINITION ),
    SecurityDefinitionUpdate( ReusableCategoryEnum.Event, FullEventIds.ID_SECURITYDEFINITIONUPDATE, EventIds.ID_SECURITYDEFINITIONUPDATE ),
    ProductSnapshot( ReusableCategoryEnum.Event, FullEventIds.ID_PRODUCTSNAPSHOT, EventIds.ID_PRODUCTSNAPSHOT ),
    SecurityStatus( ReusableCategoryEnum.Event, FullEventIds.ID_SECURITYSTATUS, EventIds.ID_SECURITYSTATUS ),
    SettlementPriceEvent( ReusableCategoryEnum.Event, FullEventIds.ID_SETTLEMENTPRICEEVENT, EventIds.ID_SETTLEMENTPRICEEVENT ),
    ClosingPriceEvent( ReusableCategoryEnum.Event, FullEventIds.ID_CLOSINGPRICEEVENT, EventIds.ID_CLOSINGPRICEEVENT ),
    OpenPriceEvent( ReusableCategoryEnum.Event, FullEventIds.ID_OPENPRICEEVENT, EventIds.ID_OPENPRICEEVENT ),
    OpenInterestEvent( ReusableCategoryEnum.Event, FullEventIds.ID_OPENINTERESTEVENT, EventIds.ID_OPENINTERESTEVENT ),
    News( ReusableCategoryEnum.Event, FullEventIds.ID_NEWS, EventIds.ID_NEWS ),
    CorporateActionEvent( ReusableCategoryEnum.Event, FullEventIds.ID_CORPORATEACTIONEVENT, EventIds.ID_CORPORATEACTIONEVENT ),
    InstrumentSimData( ReusableCategoryEnum.Event, FullEventIds.ID_INSTRUMENTSIMDATA, EventIds.ID_INSTRUMENTSIMDATA ),
    RefPriceEvent( ReusableCategoryEnum.Event, FullEventIds.ID_REFPRICEEVENT, EventIds.ID_REFPRICEEVENT ),
    BrokerLoanResponse( ReusableCategoryEnum.Event, FullEventIds.ID_BROKERLOANRESPONSE, EventIds.ID_BROKERLOANRESPONSE ),
    PriceLimitCollarEvent( ReusableCategoryEnum.Event, FullEventIds.ID_PRICELIMITCOLLAREVENT, EventIds.ID_PRICELIMITCOLLAREVENT ),
    SecurityTradingStatusEvent( ReusableCategoryEnum.Event, FullEventIds.ID_SECURITYTRADINGSTATUSEVENT, EventIds.ID_SECURITYTRADINGSTATUSEVENT ),
    LeanHogIndexEvent( ReusableCategoryEnum.Event, FullEventIds.ID_LEANHOGINDEXEVENT, EventIds.ID_LEANHOGINDEXEVENT ),
    ForceFlattenCommand( ReusableCategoryEnum.Event, FullEventIds.ID_FORCEFLATTENCOMMAND, EventIds.ID_FORCEFLATTENCOMMAND ),
    AppRun( ReusableCategoryEnum.Event, FullEventIds.ID_APPRUN, EventIds.ID_APPRUN ),
    StratInstrument( ReusableCategoryEnum.Event, FullEventIds.ID_STRATINSTRUMENT, EventIds.ID_STRATINSTRUMENT ),
    StrategyRun( ReusableCategoryEnum.Event, FullEventIds.ID_STRATEGYRUN, EventIds.ID_STRATEGYRUN ),
    StratInstrumentState( ReusableCategoryEnum.Event, FullEventIds.ID_STRATINSTRUMENTSTATE, EventIds.ID_STRATINSTRUMENTSTATE ),
    StrategyState( ReusableCategoryEnum.Event, FullEventIds.ID_STRATEGYSTATE, EventIds.ID_STRATEGYSTATE ),
    UTPLogon( ReusableCategoryEnum.Event, FullEventIds.ID_UTPLOGON, EventIds.ID_UTPLOGON ),
    UTPLogonReject( ReusableCategoryEnum.Event, FullEventIds.ID_UTPLOGONREJECT, EventIds.ID_UTPLOGONREJECT ),
    UTPTradingSessionStatus( ReusableCategoryEnum.Event, FullEventIds.ID_UTPTRADINGSESSIONSTATUS, EventIds.ID_UTPTRADINGSESSIONSTATUS ),
    ETIConnectionGatewayRequest( ReusableCategoryEnum.Event, FullEventIds.ID_ETICONNECTIONGATEWAYREQUEST, EventIds.ID_ETICONNECTIONGATEWAYREQUEST ),
    ETIConnectionGatewayResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETICONNECTIONGATEWAYRESPONSE, EventIds.ID_ETICONNECTIONGATEWAYRESPONSE ),
    ETISessionLogonRequest( ReusableCategoryEnum.Event, FullEventIds.ID_ETISESSIONLOGONREQUEST, EventIds.ID_ETISESSIONLOGONREQUEST ),
    ETISessionLogonResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETISESSIONLOGONRESPONSE, EventIds.ID_ETISESSIONLOGONRESPONSE ),
    ETISessionLogoutRequest( ReusableCategoryEnum.Event, FullEventIds.ID_ETISESSIONLOGOUTREQUEST, EventIds.ID_ETISESSIONLOGOUTREQUEST ),
    ETISessionLogoutResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETISESSIONLOGOUTRESPONSE, EventIds.ID_ETISESSIONLOGOUTRESPONSE ),
    ETISessionLogoutNotification( ReusableCategoryEnum.Event, FullEventIds.ID_ETISESSIONLOGOUTNOTIFICATION, EventIds.ID_ETISESSIONLOGOUTNOTIFICATION ),
    ETIUserLogonRequest( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUSERLOGONREQUEST, EventIds.ID_ETIUSERLOGONREQUEST ),
    ETIUserLogonResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUSERLOGONRESPONSE, EventIds.ID_ETIUSERLOGONRESPONSE ),
    ETIUserLogoutRequest( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUSERLOGOUTREQUEST, EventIds.ID_ETIUSERLOGOUTREQUEST ),
    ETIUserLogoutResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUSERLOGOUTRESPONSE, EventIds.ID_ETIUSERLOGOUTRESPONSE ),
    ETIThrottleUpdateNotification( ReusableCategoryEnum.Event, FullEventIds.ID_ETITHROTTLEUPDATENOTIFICATION, EventIds.ID_ETITHROTTLEUPDATENOTIFICATION ),
    ETISubscribe( ReusableCategoryEnum.Event, FullEventIds.ID_ETISUBSCRIBE, EventIds.ID_ETISUBSCRIBE ),
    ETISubscribeResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETISUBSCRIBERESPONSE, EventIds.ID_ETISUBSCRIBERESPONSE ),
    ETIUnsubscribe( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUNSUBSCRIBE, EventIds.ID_ETIUNSUBSCRIBE ),
    ETIUnsubscribeResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETIUNSUBSCRIBERESPONSE, EventIds.ID_ETIUNSUBSCRIBERESPONSE ),
    ETIRetransmit( ReusableCategoryEnum.Event, FullEventIds.ID_ETIRETRANSMIT, EventIds.ID_ETIRETRANSMIT ),
    ETIRetransmitResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETIRETRANSMITRESPONSE, EventIds.ID_ETIRETRANSMITRESPONSE ),
    ETIRetransmitOrderEvents( ReusableCategoryEnum.Event, FullEventIds.ID_ETIRETRANSMITORDEREVENTS, EventIds.ID_ETIRETRANSMITORDEREVENTS ),
    ETIRetransmitOrderEventsResponse( ReusableCategoryEnum.Event, FullEventIds.ID_ETIRETRANSMITORDEREVENTSRESPONSE, EventIds.ID_ETIRETRANSMITORDEREVENTSRESPONSE ),
    MilleniumLogon( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMLOGON, EventIds.ID_MILLENIUMLOGON ),
    MilleniumLogonReply( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMLOGONREPLY, EventIds.ID_MILLENIUMLOGONREPLY ),
    MilleniumLogout( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMLOGOUT, EventIds.ID_MILLENIUMLOGOUT ),
    MilleniumMissedMessageRequest( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMMISSEDMESSAGEREQUEST, EventIds.ID_MILLENIUMMISSEDMESSAGEREQUEST ),
    MilleniumMissedMsgRequestAck( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMMISSEDMSGREQUESTACK, EventIds.ID_MILLENIUMMISSEDMSGREQUESTACK ),
    MilleniumMissedMsgReport( ReusableCategoryEnum.Event, FullEventIds.ID_MILLENIUMMISSEDMSGREPORT, EventIds.ID_MILLENIUMMISSEDMSGREPORT ),
    BookAddOrder( ReusableCategoryEnum.Event, FullEventIds.ID_BOOKADDORDER, EventIds.ID_BOOKADDORDER ),
    BookDeleteOrder( ReusableCategoryEnum.Event, FullEventIds.ID_BOOKDELETEORDER, EventIds.ID_BOOKDELETEORDER ),
    BookModifyOrder( ReusableCategoryEnum.Event, FullEventIds.ID_BOOKMODIFYORDER, EventIds.ID_BOOKMODIFYORDER ),
    BookClear( ReusableCategoryEnum.Event, FullEventIds.ID_BOOKCLEAR, EventIds.ID_BOOKCLEAR ),
    PitchSymbolClear( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHSYMBOLCLEAR, EventIds.ID_PITCHSYMBOLCLEAR ),
    PitchBookAddOrder( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHBOOKADDORDER, EventIds.ID_PITCHBOOKADDORDER ),
    PitchBookOrderExecuted( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHBOOKORDEREXECUTED, EventIds.ID_PITCHBOOKORDEREXECUTED ),
    PitchOffBookTrade( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHOFFBOOKTRADE, EventIds.ID_PITCHOFFBOOKTRADE ),
    PitchBookCancelOrder( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHBOOKCANCELORDER, EventIds.ID_PITCHBOOKCANCELORDER ),
    PitchPriceStatistic( ReusableCategoryEnum.Event, FullEventIds.ID_PITCHPRICESTATISTIC, EventIds.ID_PITCHPRICESTATISTIC ),
    AuctionUpdate( ReusableCategoryEnum.Event, FullEventIds.ID_AUCTIONUPDATE, EventIds.ID_AUCTIONUPDATE ),
    AuctionSummary( ReusableCategoryEnum.Event, FullEventIds.ID_AUCTIONSUMMARY, EventIds.ID_AUCTIONSUMMARY ),
    SoupDebugPacket( ReusableCategoryEnum.Event, FullEventIds.ID_SOUPDEBUGPACKET, EventIds.ID_SOUPDEBUGPACKET ),
    SoupLogInAccepted( ReusableCategoryEnum.Event, FullEventIds.ID_SOUPLOGINACCEPTED, EventIds.ID_SOUPLOGINACCEPTED ),
    SoupLogInRejected( ReusableCategoryEnum.Event, FullEventIds.ID_SOUPLOGINREJECTED, EventIds.ID_SOUPLOGINREJECTED ),
    SoupLogInRequest( ReusableCategoryEnum.Event, FullEventIds.ID_SOUPLOGINREQUEST, EventIds.ID_SOUPLOGINREQUEST ),
    UnsequencedDataPacket( ReusableCategoryEnum.Event, FullEventIds.ID_UNSEQUENCEDDATAPACKET, EventIds.ID_UNSEQUENCEDDATAPACKET );

    private final int              _eventId;
    private final int              _id;
    private final ReusableCategory _cat;

    private ModelReusableTypes( ReusableCategory cat, int catId, int eventId ) {
        _cat     = cat;
        _id      = ReusableTypeIDFactory.setID( cat, catId );
        _eventId = eventId;
    }

    @Override
    public int getSubId() {
        return _eventId;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public ReusableCategory getReusableCategory() {
        return _cat;
    }

}
