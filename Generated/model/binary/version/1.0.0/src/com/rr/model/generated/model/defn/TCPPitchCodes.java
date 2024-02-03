package com.rr.model.generated.model.defn;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


import javax.annotation.Generated;

@Generated( "com.rr.model.generated.model.TCPPitchCodes" )

public interface TCPPitchCodes {
    public byte[] PitchSymbolClear = "h".getBytes();
    public byte[] PitchBookAddOrderMicros = "K".getBytes();
    public byte[] PitchBookAddOrderLongMicros = "M".getBytes();
    public byte[] PitchBookOrderExecutedMicros = "N".getBytes();
    public byte[] PitchBookOrderExecutedLongMicros = "g".getBytes();
    public byte[] PitchBookCancelOrderMicros = "F".getBytes();
    public byte[] PitchBookCancelOrderLongMicros = "G".getBytes();
    public byte[] TradingStatus = "a".getBytes();
    public byte[] PriceStatistic = "Y".getBytes();
    public byte[] AuctionUpdate = "b".getBytes();
    public byte[] AuctionSummary = "f".getBytes();

}
