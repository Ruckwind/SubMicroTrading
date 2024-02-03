package com.rr.model.generated.fix.model.defn;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


import javax.annotation.Generated;

@Generated( "com.rr.model.generated.fix.model.FixMsgTypesMD44" )

public interface FixMsgTypesMD44 {
    public byte[] MDRequest = "V".getBytes();
    public byte[] MDIncRefresh = "X".getBytes();
    public byte[] MDSnapshotFullRefresh = "W".getBytes();
    public byte[] SecurityDefinition = "d".getBytes();
    public byte[] SecurityStatus = "f".getBytes();
    public byte[] Heartbeat = "0".getBytes();
    public byte[] Logon = "A".getBytes();
    public byte[] Logout = "5".getBytes();
    public byte[] SessionReject = "3".getBytes();
    public byte[] ResendRequest = "2".getBytes();
    public byte[] SequenceReset = "4".getBytes();
    public byte[] TestRequest = "1".getBytes();
    public byte[] TradingSessionStatus = "h".getBytes();
    public byte[] MassInstrumentStateChange = "CO".getBytes();
}
