/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

public enum FixEventType {

    MDRequest( false ),
    MDSnapshotFullRefresh( false ),
    MDIncRefresh( false ),
    MDUpdate( false ),
    SecurityStatus( false ),
    SecurityDefinition( false ),
    Heartbeat( false ),
    Logon( false ),
    Logout( false ),
    SessionReject( false ),
    ResendRequest( false ),
    SequenceReset( false ),
    TestRequest( false ),
    NewOrderSingle( false ),
    CancelReplaceRequest( false ),
    CancelRequest( false ),
    CancelReject( false ),
    NewOrderAck( true ),
    Trade( true ),
    Rejected( true ),
    Replaced( true ),
    Cancelled( true ),
    DoneForDay( true ),
    PendingCancel( true ),
    Stopped( true ),
    Suspended( true ),
    PendingNew( true ),
    Calculated( true ),
    Expired( true ),
    Restated( true ),
    PendingReplace( true ),
    TradeCorrect( true ),
    TradeCancel( true ),
    OrderStatus( true ),

    // fix4.2 only
    PartialFill( true ),
    Fill( true ),

    // fix5.0
    ProductSnapshot( false ),
    SecurityDefinitionUpdate( false ),

    // REPEATING GROUPS
    MDEntry( false ),
    MDSnapEntry( false ),
    SecDefEvent( false ),
    SecDefLeg( false ),
    SecurityAltID( false ),
    SDFeedType( false ),

    // MD
    TradingSessionStatus( false ),
    MassInstrumentStateChange( false ),

    // Algo Drop Copy
    AppRun( false ),
    StrategyRun( false ),
    StrategyState( false ),
    StratInstrument( false ),
    StratInstrumentState( false ),

    // NOT REAL FIX MESSAGES, BASE MESSAGES USED FOR MODEL INHERITANCE
    FullCompIDCheck( false ),
    BaseRequest( false ),
    BaseTrade( false ), // an abstract base for exec rpt
    BaseExecRpt( false ),
    LightCompIDCheck( false ),

    // not yet supported

    QuoteStatusRequest( false ),
    QuoteCancel( false ),
    Quote( false ),
    QuoteRequest( false ),
    QuoteStatusReport( false ),
    QuoteResponse( false ),
    QuoteRequestReject( false );

    private boolean _isExecRpt;

    FixEventType( boolean isExecRpt ) {
        _isExecRpt = isExecRpt;
    }

    public boolean isConcreteExecRpt() {
        return _isExecRpt;
    }
}
