/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

public enum CoreScheduledEvent implements ScheduledEvent {
    Flush,
    Heartbeat,
    UTCDateRoll,
    ExchangeDailyReset,
    StartOfDay,
    MarketOpen,
    MarketClosed,
    ExpireTrades,
    EndOfDay
}
