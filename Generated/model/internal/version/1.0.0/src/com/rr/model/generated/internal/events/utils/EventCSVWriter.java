package com.rr.model.generated.internal.events.utils;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.events.recycle.*;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.lang.*;
import com.rr.core.model.Event;

public final class EventCSVWriter {

    private static final char MAIN_SEP = ',';
    private static final char SUB_SEP  = ';';

    public static void writeHeader( ReusableString dest, Heartbeat msg ) {
        dest.append( "testReqID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Heartbeat msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getTestReqID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, EndOfSession msg ) {
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, EndOfSession msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, LogoutRequest msg ) {
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, LogoutRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TestRequest msg ) {
        dest.append( "testReqID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TestRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getTestReqID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Logon msg ) {
        dest.append( "senderCompId" );
        dest.append( MAIN_SEP );
        dest.append( "senderSubId" );
        dest.append( MAIN_SEP );
        dest.append( "targetCompId" );
        dest.append( MAIN_SEP );
        dest.append( "targetSubId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "encryptMethod" );
        dest.append( MAIN_SEP );
        dest.append( "heartBtInt" );
        dest.append( MAIN_SEP );
        dest.append( "rawDataLen" );
        dest.append( MAIN_SEP );
        dest.append( "rawData" );
        dest.append( MAIN_SEP );
        dest.append( "resetSeqNumFlag" );
        dest.append( MAIN_SEP );
        dest.append( "nextExpectedMsgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Logon msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSenderCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSenderSubId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetSubId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getEncryptMethod() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHeartBtInt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRawDataLen() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRawData() );
        dest.append( MAIN_SEP );
        dest.append( msg.getResetSeqNumFlag() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNextExpectedMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Logout msg ) {
        dest.append( "senderCompId" );
        dest.append( MAIN_SEP );
        dest.append( "senderSubId" );
        dest.append( MAIN_SEP );
        dest.append( "targetCompId" );
        dest.append( MAIN_SEP );
        dest.append( "targetSubId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "lastMsgSeqNumProcessed" );
        dest.append( MAIN_SEP );
        dest.append( "nextExpectedMsgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Logout msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSenderCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSenderSubId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetSubId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMsgSeqNumProcessed() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNextExpectedMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SessionReject msg ) {
        dest.append( "refSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "refTagID" );
        dest.append( MAIN_SEP );
        dest.append( "refMsgType" );
        dest.append( MAIN_SEP );
        dest.append( "sessionRejectReason" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SessionReject msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getRefSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefTagID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefMsgType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSessionRejectReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ResendRequest msg ) {
        dest.append( "beginSeqNo" );
        dest.append( MAIN_SEP );
        dest.append( "endSeqNo" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ResendRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getBeginSeqNo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getEndSeqNo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ClientResyncSentMsgs msg ) {
        dest.append( "beginSeqNo" );
        dest.append( MAIN_SEP );
        dest.append( "endSeqNo" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ClientResyncSentMsgs msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getBeginSeqNo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getEndSeqNo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SequenceReset msg ) {
        dest.append( "gapFillFlag" );
        dest.append( MAIN_SEP );
        dest.append( "newSeqNo" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SequenceReset msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getGapFillFlag() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNewSeqNo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TradingSessionStatus msg ) {
        dest.append( "marketSegmentID" );
        dest.append( MAIN_SEP );
        dest.append( "tradingSessionID" );
        dest.append( MAIN_SEP );
        dest.append( "tradingSessionSubID" );
        dest.append( MAIN_SEP );
        dest.append( "tradSesStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TradingSessionStatus msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMarketSegmentID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingSessionID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingSessionSubID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradSesStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecMassStatGrp msg ) {
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "securityStatus" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecMassStatGrp msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityStatus() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MassInstrumentStateChange msg ) {
        dest.append( "marketSegmentID" );
        dest.append( MAIN_SEP );
        dest.append( "instrumentScopeProductComplex" );
        dest.append( MAIN_SEP );
        dest.append( "securityMassTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "numRelatedSym" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MassInstrumentStateChange msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMarketSegmentID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrumentScopeProductComplex() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityMassTradingStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumRelatedSym() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, NewOrderSingle msg ) {
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "account" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "refPriceType" );
        dest.append( MAIN_SEP );
        dest.append( "tickOffset" );
        dest.append( MAIN_SEP );
        dest.append( "execInst" );
        dest.append( MAIN_SEP );
        dest.append( "handlInst" );
        dest.append( MAIN_SEP );
        dest.append( "orderCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "ordType" );
        dest.append( MAIN_SEP );
        dest.append( "securityType" );
        dest.append( MAIN_SEP );
        dest.append( "timeInForce" );
        dest.append( MAIN_SEP );
        dest.append( "bookingType" );
        dest.append( MAIN_SEP );
        dest.append( "targetStrategy" );
        dest.append( MAIN_SEP );
        dest.append( "StratParams" );
        dest.append( MAIN_SEP );
        dest.append( "effectiveTime" );
        dest.append( MAIN_SEP );
        dest.append( "expireTime" );
        dest.append( MAIN_SEP );
        dest.append( "orderReceived" );
        dest.append( MAIN_SEP );
        dest.append( "orderSent" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "client" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "maturityMonthYear" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "curPos" );
        dest.append( MAIN_SEP );
        dest.append( "curRefPx" );
        dest.append( MAIN_SEP );
        dest.append( "targetDest" );
        dest.append( MAIN_SEP );
        dest.append( "exDest" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "broker" );
        dest.append( MAIN_SEP );
        dest.append( "clearer" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "senderCompId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, NewOrderSingle msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAccount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefPriceType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickOffset() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecInst() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHandlInst() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTimeInForce() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBookingType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetStrategy() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratParams() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEffectiveTime(), dest ); else dest.append( msg.getEffectiveTime() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getExpireTime(), dest ); else dest.append( msg.getExpireTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderSent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClient() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaturityMonthYear() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurPos() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurRefPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBroker() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClearer() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSenderCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, CancelReplaceRequest msg ) {
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "exDest" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "account" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "refPriceType" );
        dest.append( MAIN_SEP );
        dest.append( "tickOffset" );
        dest.append( MAIN_SEP );
        dest.append( "execInst" );
        dest.append( MAIN_SEP );
        dest.append( "handlInst" );
        dest.append( MAIN_SEP );
        dest.append( "orderCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "ordType" );
        dest.append( MAIN_SEP );
        dest.append( "securityType" );
        dest.append( MAIN_SEP );
        dest.append( "timeInForce" );
        dest.append( MAIN_SEP );
        dest.append( "bookingType" );
        dest.append( MAIN_SEP );
        dest.append( "targetStrategy" );
        dest.append( MAIN_SEP );
        dest.append( "StratParams" );
        dest.append( MAIN_SEP );
        dest.append( "effectiveTime" );
        dest.append( MAIN_SEP );
        dest.append( "expireTime" );
        dest.append( MAIN_SEP );
        dest.append( "orderReceived" );
        dest.append( MAIN_SEP );
        dest.append( "orderSent" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "client" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "maturityMonthYear" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "curPos" );
        dest.append( MAIN_SEP );
        dest.append( "curRefPx" );
        dest.append( MAIN_SEP );
        dest.append( "targetDest" );
        dest.append( MAIN_SEP );
        dest.append( "broker" );
        dest.append( MAIN_SEP );
        dest.append( "clearer" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "senderCompId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, CancelReplaceRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAccount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefPriceType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickOffset() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecInst() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHandlInst() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTimeInForce() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBookingType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetStrategy() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratParams() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEffectiveTime(), dest ); else dest.append( msg.getEffectiveTime() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getExpireTime(), dest ); else dest.append( msg.getExpireTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderSent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClient() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaturityMonthYear() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurPos() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurRefPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBroker() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClearer() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSenderCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, CancelRequest msg ) {
        dest.append( "account" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "client" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "maturityMonthYear" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "curPos" );
        dest.append( MAIN_SEP );
        dest.append( "curRefPx" );
        dest.append( MAIN_SEP );
        dest.append( "targetDest" );
        dest.append( MAIN_SEP );
        dest.append( "exDest" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "broker" );
        dest.append( MAIN_SEP );
        dest.append( "clearer" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "senderCompId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, CancelRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getAccount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClient() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaturityMonthYear() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurPos() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurRefPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTargetDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExDest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBroker() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClearer() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSenderCompId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ForceCancel msg ) {
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ForceCancel msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, VagueOrderReject msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "isTerminal" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, VagueOrderReject msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIsTerminal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, CancelReject msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "cxlRejReason" );
        dest.append( MAIN_SEP );
        dest.append( "cxlRejResponseTo" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, CancelReject msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCxlRejReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCxlRejResponseTo() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, NewOrderAck msg ) {
        dest.append( "ackReceived" );
        dest.append( MAIN_SEP );
        dest.append( "orderReceived" );
        dest.append( MAIN_SEP );
        dest.append( "orderSent" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, NewOrderAck msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getAckReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderSent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TradeNew msg ) {
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "liquidityInd" );
        dest.append( MAIN_SEP );
        dest.append( "multiLegReportingType" );
        dest.append( MAIN_SEP );
        dest.append( "lastMkt" );
        dest.append( MAIN_SEP );
        dest.append( "securityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TradeNew msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLiquidityInd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMultiLegReportingType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMkt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Rejected msg ) {
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "ordRejReason" );
        dest.append( MAIN_SEP );
        dest.append( "tradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Rejected msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdRejReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Cancelled msg ) {
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Cancelled msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Replaced msg ) {
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Replaced msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, DoneForDay msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, DoneForDay msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Stopped msg ) {
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Stopped msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Expired msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Expired msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Suspended msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Suspended msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Restated msg ) {
        dest.append( "execRestatementReason" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Restated msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecRestatementReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TradeCorrect msg ) {
        dest.append( "execRefID" );
        dest.append( MAIN_SEP );
        dest.append( "origQty" );
        dest.append( MAIN_SEP );
        dest.append( "origPx" );
        dest.append( MAIN_SEP );
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "liquidityInd" );
        dest.append( MAIN_SEP );
        dest.append( "multiLegReportingType" );
        dest.append( MAIN_SEP );
        dest.append( "lastMkt" );
        dest.append( MAIN_SEP );
        dest.append( "securityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TradeCorrect msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecRefID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLiquidityInd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMultiLegReportingType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMkt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TradeCancel msg ) {
        dest.append( "execRefID" );
        dest.append( MAIN_SEP );
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "liquidityInd" );
        dest.append( MAIN_SEP );
        dest.append( "multiLegReportingType" );
        dest.append( MAIN_SEP );
        dest.append( "lastMkt" );
        dest.append( MAIN_SEP );
        dest.append( "securityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TradeCancel msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecRefID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLiquidityInd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMultiLegReportingType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMkt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PendingCancel msg ) {
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PendingCancel msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PendingReplace msg ) {
        dest.append( "origClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PendingReplace msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrigClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PendingNew msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PendingNew msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, OrderStatus msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, OrderStatus msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, IgnoredExec msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, IgnoredExec msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, Calculated msg ) {
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "transactTime" );
        dest.append( MAIN_SEP );
        dest.append( "leavesQty" );
        dest.append( MAIN_SEP );
        dest.append( "cumQty" );
        dest.append( MAIN_SEP );
        dest.append( "avgPx" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "mktCapacity" );
        dest.append( MAIN_SEP );
        dest.append( "parentClOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "origStratId" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, Calculated msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getTransactTime(), dest ); else dest.append( msg.getTransactTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLeavesQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCumQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAvgPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktCapacity() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrigStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, AlertLimitBreach msg ) {
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, AlertLimitBreach msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, AlertTradeMissingOrders msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "execType" );
        dest.append( MAIN_SEP );
        dest.append( "ordStatus" );
        dest.append( MAIN_SEP );
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "lastMkt" );
        dest.append( MAIN_SEP );
        dest.append( "clOrdId" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "onBehalfOfId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, AlertTradeMissingOrders msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrdStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMkt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClOrdId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOnBehalfOfId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SymbolRepeatingGrp msg ) {
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SymbolRepeatingGrp msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDRequest msg ) {
        dest.append( "mdReqId" );
        dest.append( MAIN_SEP );
        dest.append( "subsReqType" );
        dest.append( MAIN_SEP );
        dest.append( "marketDepth" );
        dest.append( MAIN_SEP );
        dest.append( "numRelatedSym" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMdReqId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubsReqType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMarketDepth() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumRelatedSym() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, TickUpdate msg ) {
        dest.append( "mdEntryType" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryPx" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntrySize" );
        dest.append( MAIN_SEP );
        dest.append( "tradeTime" );
        dest.append( MAIN_SEP );
        dest.append( "tickDirection" );
        dest.append( MAIN_SEP );
        dest.append( "numberOfOrders" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, TickUpdate msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMdEntryType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntrySize() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradeTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickDirection() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumberOfOrders() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDUpdate msg ) {
        dest.append( "mdReqId" );
        dest.append( MAIN_SEP );
        dest.append( "book" );
        dest.append( MAIN_SEP );
        dest.append( "noMDEntries" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDUpdate msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMdReqId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBook() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoMDEntries() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecDefEvent msg ) {
        dest.append( "eventType" );
        dest.append( MAIN_SEP );
        dest.append( "eventDate" );
        dest.append( MAIN_SEP );
        dest.append( "eventTime" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecDefEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getEventType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getEventDate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getEventTime() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecurityAltID msg ) {
        dest.append( "securityAltID" );
        dest.append( MAIN_SEP );
        dest.append( "securityAltIDSource" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecurityAltID msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityAltID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityAltIDSource() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SDFeedType msg ) {
        dest.append( "feedType" );
        dest.append( MAIN_SEP );
        dest.append( "marketDepth" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SDFeedType msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getFeedType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMarketDepth() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecDefLeg msg ) {
        dest.append( "legSymbol" );
        dest.append( MAIN_SEP );
        dest.append( "legSecurityID" );
        dest.append( MAIN_SEP );
        dest.append( "legSecurityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "legRatioQty" );
        dest.append( MAIN_SEP );
        dest.append( "legSecurityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "legSide" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecDefLeg msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLegSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLegSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLegSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLegRatioQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLegSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLegSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDEntry msg ) {
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "mdUpdateAction" );
        dest.append( MAIN_SEP );
        dest.append( "repeatSeq" );
        dest.append( MAIN_SEP );
        dest.append( "numberOfOrders" );
        dest.append( MAIN_SEP );
        dest.append( "mdPriceLevel" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryType" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryPx" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntrySize" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryTime" );
        dest.append( MAIN_SEP );
        dest.append( "tradingSessionID" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDEntry msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdUpdateAction() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRepeatSeq() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumberOfOrders() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdPriceLevel() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntrySize() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingSessionID() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDSnapEntry msg ) {
        dest.append( "mdPriceLevel" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryType" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryPx" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntrySize" );
        dest.append( MAIN_SEP );
        dest.append( "mdEntryTime" );
        dest.append( MAIN_SEP );
        dest.append( "tickDirection" );
        dest.append( MAIN_SEP );
        dest.append( "tradeVolume" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDSnapEntry msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMdPriceLevel() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntrySize() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdEntryTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickDirection() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradeVolume() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MsgSeqNumGap msg ) {
        dest.append( "channelId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "prevSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MsgSeqNumGap msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getChannelId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrevSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDIncRefresh msg ) {
        dest.append( "received" );
        dest.append( MAIN_SEP );
        dest.append( "noMDEntries" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDIncRefresh msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoMDEntries() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MDSnapshotFullRefresh msg ) {
        dest.append( "received" );
        dest.append( MAIN_SEP );
        dest.append( "lastMsgSeqNumProcessed" );
        dest.append( MAIN_SEP );
        dest.append( "totNumReports" );
        dest.append( MAIN_SEP );
        dest.append( "rptSeq" );
        dest.append( MAIN_SEP );
        dest.append( "mdBookType" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "mdSecurityTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "noMDEntries" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MDSnapshotFullRefresh msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getReceived() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMsgSeqNumProcessed() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotNumReports() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRptSeq() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdBookType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMdSecurityTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoMDEntries() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecurityDefinition msg ) {
        dest.append( "totNumReports" );
        dest.append( MAIN_SEP );
        dest.append( "securityTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "securityType" );
        dest.append( MAIN_SEP );
        dest.append( "uniqueInstId" );
        dest.append( MAIN_SEP );
        dest.append( "secDefId" );
        dest.append( MAIN_SEP );
        dest.append( "exchangeLongId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "noEvents" );
        dest.append( MAIN_SEP );
        dest.append( "securityUpdateAction" );
        dest.append( MAIN_SEP );
        dest.append( "noLegs" );
        dest.append( MAIN_SEP );
        dest.append( "tradingReferencePrice" );
        dest.append( MAIN_SEP );
        dest.append( "highLimitPx" );
        dest.append( MAIN_SEP );
        dest.append( "lowLimitPx" );
        dest.append( MAIN_SEP );
        dest.append( "futPointValue" );
        dest.append( MAIN_SEP );
        dest.append( "minPriceIncrement" );
        dest.append( MAIN_SEP );
        dest.append( "minPriceIncrementAmount" );
        dest.append( MAIN_SEP );
        dest.append( "securityGroup" );
        dest.append( MAIN_SEP );
        dest.append( "securityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "securityLongDesc" );
        dest.append( MAIN_SEP );
        dest.append( "CFICode" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingProduct" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingSecurityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingSecurityID" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingScurityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "primarySecurityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "tickRule" );
        dest.append( MAIN_SEP );
        dest.append( "noSecurityAltID" );
        dest.append( MAIN_SEP );
        dest.append( "strikePrice" );
        dest.append( MAIN_SEP );
        dest.append( "strikeCurrency" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "settlCurrency" );
        dest.append( MAIN_SEP );
        dest.append( "minTradeVol" );
        dest.append( MAIN_SEP );
        dest.append( "maxTradeVol" );
        dest.append( MAIN_SEP );
        dest.append( "noSDFeedTypes" );
        dest.append( MAIN_SEP );
        dest.append( "maturityMonthYear" );
        dest.append( MAIN_SEP );
        dest.append( "applID" );
        dest.append( MAIN_SEP );
        dest.append( "displayFactor" );
        dest.append( MAIN_SEP );
        dest.append( "priceRatio" );
        dest.append( MAIN_SEP );
        dest.append( "contractMultiplierType" );
        dest.append( MAIN_SEP );
        dest.append( "contractMultiplier" );
        dest.append( MAIN_SEP );
        dest.append( "openInterestQty" );
        dest.append( MAIN_SEP );
        dest.append( "tradingReferenceDate" );
        dest.append( MAIN_SEP );
        dest.append( "minQty" );
        dest.append( MAIN_SEP );
        dest.append( "pricePrecision" );
        dest.append( MAIN_SEP );
        dest.append( "unitOfMeasure" );
        dest.append( MAIN_SEP );
        dest.append( "unitOfMeasureQty" );
        dest.append( MAIN_SEP );
        dest.append( "companyName" );
        dest.append( MAIN_SEP );
        dest.append( "sharesOutstanding" );
        dest.append( MAIN_SEP );
        dest.append( "commonSecurityId" );
        dest.append( MAIN_SEP );
        dest.append( "parentCompanyId" );
        dest.append( MAIN_SEP );
        dest.append( "gicsCode" );
        dest.append( MAIN_SEP );
        dest.append( "getOutDate" );
        dest.append( MAIN_SEP );
        dest.append( "deadTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "startTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "endTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "secDefSpecialType" );
        dest.append( MAIN_SEP );
        dest.append( "companyStatusType" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecurityDefinition msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getTotNumReports() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUniqueInstId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecDefId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExchangeLongId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoEvents() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityUpdateAction() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoLegs() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingReferencePrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHighLimitPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLowLimitPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFutPointValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinPriceIncrement() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinPriceIncrementAmount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityGroup() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityLongDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCFICode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingProduct() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingScurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrimarySecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickRule() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoSecurityAltID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStrikePrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStrikeCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSettlCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinTradeVol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaxTradeVol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoSDFeedTypes() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaturityMonthYear() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDisplayFactor() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPriceRatio() );
        dest.append( MAIN_SEP );
        dest.append( msg.getContractMultiplierType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getContractMultiplier() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOpenInterestQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingReferenceDate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPricePrecision() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnitOfMeasure() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnitOfMeasureQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCompanyName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSharesOutstanding() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCommonSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentCompanyId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getGicsCode() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getGetOutDate(), dest ); else dest.append( msg.getGetOutDate() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getDeadTimestamp(), dest ); else dest.append( msg.getDeadTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getStartTimestamp(), dest ); else dest.append( msg.getStartTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEndTimestamp(), dest ); else dest.append( msg.getEndTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecDefSpecialType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCompanyStatusType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecurityDefinitionUpdate msg ) {
        dest.append( "totNumReports" );
        dest.append( MAIN_SEP );
        dest.append( "securityTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "securityType" );
        dest.append( MAIN_SEP );
        dest.append( "uniqueInstId" );
        dest.append( MAIN_SEP );
        dest.append( "secDefId" );
        dest.append( MAIN_SEP );
        dest.append( "exchangeLongId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "noEvents" );
        dest.append( MAIN_SEP );
        dest.append( "securityUpdateAction" );
        dest.append( MAIN_SEP );
        dest.append( "noLegs" );
        dest.append( MAIN_SEP );
        dest.append( "tradingReferencePrice" );
        dest.append( MAIN_SEP );
        dest.append( "highLimitPx" );
        dest.append( MAIN_SEP );
        dest.append( "lowLimitPx" );
        dest.append( MAIN_SEP );
        dest.append( "futPointValue" );
        dest.append( MAIN_SEP );
        dest.append( "minPriceIncrement" );
        dest.append( MAIN_SEP );
        dest.append( "minPriceIncrementAmount" );
        dest.append( MAIN_SEP );
        dest.append( "securityGroup" );
        dest.append( MAIN_SEP );
        dest.append( "securityDesc" );
        dest.append( MAIN_SEP );
        dest.append( "securityLongDesc" );
        dest.append( MAIN_SEP );
        dest.append( "CFICode" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingProduct" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingSecurityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingSecurityID" );
        dest.append( MAIN_SEP );
        dest.append( "underlyingScurityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "primarySecurityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "tickRule" );
        dest.append( MAIN_SEP );
        dest.append( "noSecurityAltID" );
        dest.append( MAIN_SEP );
        dest.append( "strikePrice" );
        dest.append( MAIN_SEP );
        dest.append( "strikeCurrency" );
        dest.append( MAIN_SEP );
        dest.append( "currency" );
        dest.append( MAIN_SEP );
        dest.append( "settlCurrency" );
        dest.append( MAIN_SEP );
        dest.append( "minTradeVol" );
        dest.append( MAIN_SEP );
        dest.append( "maxTradeVol" );
        dest.append( MAIN_SEP );
        dest.append( "noSDFeedTypes" );
        dest.append( MAIN_SEP );
        dest.append( "maturityMonthYear" );
        dest.append( MAIN_SEP );
        dest.append( "applID" );
        dest.append( MAIN_SEP );
        dest.append( "displayFactor" );
        dest.append( MAIN_SEP );
        dest.append( "priceRatio" );
        dest.append( MAIN_SEP );
        dest.append( "contractMultiplierType" );
        dest.append( MAIN_SEP );
        dest.append( "contractMultiplier" );
        dest.append( MAIN_SEP );
        dest.append( "openInterestQty" );
        dest.append( MAIN_SEP );
        dest.append( "tradingReferenceDate" );
        dest.append( MAIN_SEP );
        dest.append( "minQty" );
        dest.append( MAIN_SEP );
        dest.append( "pricePrecision" );
        dest.append( MAIN_SEP );
        dest.append( "unitOfMeasure" );
        dest.append( MAIN_SEP );
        dest.append( "unitOfMeasureQty" );
        dest.append( MAIN_SEP );
        dest.append( "companyName" );
        dest.append( MAIN_SEP );
        dest.append( "sharesOutstanding" );
        dest.append( MAIN_SEP );
        dest.append( "commonSecurityId" );
        dest.append( MAIN_SEP );
        dest.append( "parentCompanyId" );
        dest.append( MAIN_SEP );
        dest.append( "gicsCode" );
        dest.append( MAIN_SEP );
        dest.append( "getOutDate" );
        dest.append( MAIN_SEP );
        dest.append( "deadTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "startTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "endTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "secDefSpecialType" );
        dest.append( MAIN_SEP );
        dest.append( "companyStatusType" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecurityDefinitionUpdate msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getTotNumReports() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUniqueInstId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecDefId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExchangeLongId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoEvents() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityUpdateAction() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoLegs() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingReferencePrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHighLimitPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLowLimitPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFutPointValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinPriceIncrement() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinPriceIncrementAmount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityGroup() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityLongDesc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCFICode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingProduct() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnderlyingScurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrimarySecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTickRule() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoSecurityAltID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStrikePrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStrikeCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSettlCurrency() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinTradeVol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaxTradeVol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoSDFeedTypes() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMaturityMonthYear() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDisplayFactor() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPriceRatio() );
        dest.append( MAIN_SEP );
        dest.append( msg.getContractMultiplierType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getContractMultiplier() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOpenInterestQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingReferenceDate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMinQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPricePrecision() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnitOfMeasure() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnitOfMeasureQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCompanyName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSharesOutstanding() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCommonSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getParentCompanyId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getGicsCode() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getGetOutDate(), dest ); else dest.append( msg.getGetOutDate() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getDeadTimestamp(), dest ); else dest.append( msg.getDeadTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getStartTimestamp(), dest ); else dest.append( msg.getStartTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEndTimestamp(), dest ); else dest.append( msg.getEndTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecDefSpecialType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCompanyStatusType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ProductSnapshot msg ) {
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ProductSnapshot msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecurityStatus msg ) {
        dest.append( "securityIDSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityID" );
        dest.append( MAIN_SEP );
        dest.append( "TradeDate" );
        dest.append( MAIN_SEP );
        dest.append( "highPx" );
        dest.append( MAIN_SEP );
        dest.append( "lowPx" );
        dest.append( MAIN_SEP );
        dest.append( "securityTradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "haltReason" );
        dest.append( MAIN_SEP );
        dest.append( "SecurityTradingEvent" );
        dest.append( MAIN_SEP );
        dest.append( "symbol" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecurityStatus msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityIDSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradeDate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHighPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLowPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHaltReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityTradingEvent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSymbol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SettlementPriceEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "settlementPrice" );
        dest.append( MAIN_SEP );
        dest.append( "settlementPriceType" );
        dest.append( MAIN_SEP );
        dest.append( "settleDateTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SettlementPriceEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSettlementPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSettlementPriceType() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getSettleDateTime(), dest ); else dest.append( msg.getSettleDateTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ClosingPriceEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "closingPriceType" );
        dest.append( MAIN_SEP );
        dest.append( "closeDateTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ClosingPriceEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getClosingPriceType() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getCloseDateTime(), dest ); else dest.append( msg.getCloseDateTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, OpenPriceEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "openPrice" );
        dest.append( MAIN_SEP );
        dest.append( "openDateTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, OpenPriceEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOpenPrice() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getOpenDateTime(), dest ); else dest.append( msg.getOpenDateTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, OpenInterestEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "openInterest" );
        dest.append( MAIN_SEP );
        dest.append( "netOpenInterest" );
        dest.append( MAIN_SEP );
        dest.append( "prevOpenInterest" );
        dest.append( MAIN_SEP );
        dest.append( "openInterestDateTime" );
        dest.append( MAIN_SEP );
        dest.append( "prevOpenInterestDateTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, OpenInterestEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOpenInterest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNetOpenInterest() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrevOpenInterest() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getOpenInterestDateTime(), dest ); else dest.append( msg.getOpenInterestDateTime() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getPrevOpenInterestDateTime(), dest ); else dest.append( msg.getPrevOpenInterestDateTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, News msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "shortText" );
        dest.append( MAIN_SEP );
        dest.append( "longText" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, News msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getShortText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLongText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, CorporateActionEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "idSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "type" );
        dest.append( MAIN_SEP );
        dest.append( "announceTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "qualifyTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "recordTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "actionTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "ccy" );
        dest.append( MAIN_SEP );
        dest.append( "adjustType" );
        dest.append( MAIN_SEP );
        dest.append( "priceAdjustVal" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, CorporateActionEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIdSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getType() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getAnnounceTimestamp(), dest ); else dest.append( msg.getAnnounceTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getQualifyTimestamp(), dest ); else dest.append( msg.getQualifyTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getRecordTimestamp(), dest ); else dest.append( msg.getRecordTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getActionTimestamp(), dest ); else dest.append( msg.getActionTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCcy() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAdjustType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPriceAdjustVal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, InstrumentSimData msg ) {
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "idSource" );
        dest.append( MAIN_SEP );
        dest.append( "contract" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "bidSpreadEstimate" );
        dest.append( MAIN_SEP );
        dest.append( "limitStratImproveEst" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, InstrumentSimData msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIdSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getContract() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBidSpreadEstimate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLimitStratImproveEst() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, RefPriceEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "code" );
        dest.append( MAIN_SEP );
        dest.append( "val" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, RefPriceEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getVal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, BrokerLoanResponse msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "idSource" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "reference" );
        dest.append( MAIN_SEP );
        dest.append( "isDisabled" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "approveQty" );
        dest.append( MAIN_SEP );
        dest.append( "amount" );
        dest.append( MAIN_SEP );
        dest.append( "type" );
        dest.append( MAIN_SEP );
        dest.append( "broker" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, BrokerLoanResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIdSource() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getReference() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIsDisabled() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApproveQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAmount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBroker() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PriceLimitCollarEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "lowLimitPrice" );
        dest.append( MAIN_SEP );
        dest.append( "highLimitPrice" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PriceLimitCollarEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLowLimitPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHighLimitPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SecurityTradingStatusEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "tradingStatus" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SecurityTradingStatusEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, LeanHogIndexEvent msg ) {
        dest.append( "dataSrc" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "subject" );
        dest.append( MAIN_SEP );
        dest.append( "dataSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "indexDate" );
        dest.append( MAIN_SEP );
        dest.append( "negotHeadCount" );
        dest.append( MAIN_SEP );
        dest.append( "negotAverageNetPrice" );
        dest.append( MAIN_SEP );
        dest.append( "negotAverageCarcWt" );
        dest.append( MAIN_SEP );
        dest.append( "spmfHeadCount" );
        dest.append( MAIN_SEP );
        dest.append( "spmfAverageNetPrice" );
        dest.append( MAIN_SEP );
        dest.append( "spmfAverageCarcWt" );
        dest.append( MAIN_SEP );
        dest.append( "negotSpmfHeadCount" );
        dest.append( MAIN_SEP );
        dest.append( "negotSpmfAverageNetPrice" );
        dest.append( MAIN_SEP );
        dest.append( "negotSpmfAverageCarcWt" );
        dest.append( MAIN_SEP );
        dest.append( "dailyWeightedPrice" );
        dest.append( MAIN_SEP );
        dest.append( "indexValue" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, LeanHogIndexEvent msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getDataSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubject() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDataSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIndexDate() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotHeadCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotAverageNetPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotAverageCarcWt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSpmfHeadCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSpmfAverageNetPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSpmfAverageCarcWt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotSpmfHeadCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotSpmfAverageNetPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNegotSpmfAverageCarcWt() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDailyWeightedPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIndexValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ForceFlattenCommand msg ) {
        dest.append( "stratId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ForceFlattenCommand msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getStratId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, AppRun msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "liveStartTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "liveEndTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "status" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "realisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "numTrades" );
        dest.append( MAIN_SEP );
        dest.append( "id" );
        dest.append( MAIN_SEP );
        dest.append( "numStrategies" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, AppRun msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getLiveStartTimestamp(), dest ); else dest.append( msg.getLiveStartTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getLiveEndTimestamp(), dest ); else dest.append( msg.getLiveEndTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumTrades() );
        dest.append( MAIN_SEP );
        dest.append( msg.getId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumStrategies() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, StratInstrument msg ) {
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "id" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, StratInstrument msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getId() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, StrategyRun msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "liveStartTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "idOfExportComponent" );
        dest.append( MAIN_SEP );
        dest.append( "status" );
        dest.append( MAIN_SEP );
        dest.append( "algoId" );
        dest.append( MAIN_SEP );
        dest.append( "stratTimeZone" );
        dest.append( MAIN_SEP );
        dest.append( "btStartTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "btEndTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "realisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "numTrades" );
        dest.append( MAIN_SEP );
        dest.append( "strategyDefinition" );
        dest.append( MAIN_SEP );
        dest.append( "id" );
        dest.append( MAIN_SEP );
        dest.append( "noInstEntries" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, StrategyRun msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getLiveStartTimestamp(), dest ); else dest.append( msg.getLiveStartTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIdOfExportComponent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAlgoId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratTimeZone() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getBtStartTimestamp(), dest ); else dest.append( msg.getBtStartTimestamp() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getBtEndTimestamp(), dest ); else dest.append( msg.getBtEndTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumTrades() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStrategyDefinition() );
        dest.append( MAIN_SEP );
        dest.append( msg.getId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoInstEntries() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, StratInstrumentState msg ) {
        dest.append( "stratTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "instrument" );
        dest.append( MAIN_SEP );
        dest.append( "position" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "fromLongRealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "fromShortRealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnLMin" );
        dest.append( MAIN_SEP );
        dest.append( "fromLongRealisedTotalPnLMin" );
        dest.append( MAIN_SEP );
        dest.append( "fromShortRealisedTotalPnLMin" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnLMax" );
        dest.append( MAIN_SEP );
        dest.append( "fromLongRealisedTotalPnLMax" );
        dest.append( MAIN_SEP );
        dest.append( "fromShortRealisedTotalPnLMax" );
        dest.append( MAIN_SEP );
        dest.append( "isActiveTracker" );
        dest.append( MAIN_SEP );
        dest.append( "id" );
        dest.append( MAIN_SEP );
        dest.append( "fromLongRealisedTotalLongValue" );
        dest.append( MAIN_SEP );
        dest.append( "fromLongRealisedTotalShortValue" );
        dest.append( MAIN_SEP );
        dest.append( "fromShortRealisedTotalLongValue" );
        dest.append( MAIN_SEP );
        dest.append( "fromShortRealisedTotalShortValue" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalValue" );
        dest.append( MAIN_SEP );
        dest.append( "lastPrice" );
        dest.append( MAIN_SEP );
        dest.append( "totalTradeQty" );
        dest.append( MAIN_SEP );
        dest.append( "totalTradeVal" );
        dest.append( MAIN_SEP );
        dest.append( "pointValue" );
        dest.append( MAIN_SEP );
        dest.append( "totalLongOrders" );
        dest.append( MAIN_SEP );
        dest.append( "totalShortOrders" );
        dest.append( MAIN_SEP );
        dest.append( "bidPx" );
        dest.append( MAIN_SEP );
        dest.append( "askPx" );
        dest.append( MAIN_SEP );
        dest.append( "totLongOpenQty" );
        dest.append( MAIN_SEP );
        dest.append( "totShortOpenQty" );
        dest.append( MAIN_SEP );
        dest.append( "numTrades" );
        dest.append( MAIN_SEP );
        dest.append( "splitAccrualQty" );
        dest.append( MAIN_SEP );
        dest.append( "splitAccrualVal" );
        dest.append( MAIN_SEP );
        dest.append( "divAccrualVal" );
        dest.append( MAIN_SEP );
        dest.append( "publishSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "sicFlags" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, StratInstrumentState msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getStratTimestamp(), dest ); else dest.append( msg.getStratTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstrument() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPosition() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromLongRealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromShortRealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnLMin() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromLongRealisedTotalPnLMin() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromShortRealisedTotalPnLMin() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnLMax() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromLongRealisedTotalPnLMax() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromShortRealisedTotalPnLMax() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIsActiveTracker() );
        dest.append( MAIN_SEP );
        dest.append( msg.getId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromLongRealisedTotalLongValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromLongRealisedTotalShortValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromShortRealisedTotalLongValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getFromShortRealisedTotalShortValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotalTradeQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotalTradeVal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPointValue() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotalLongOrders() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotalShortOrders() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBidPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAskPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotLongOpenQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTotShortOpenQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNumTrades() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSplitAccrualQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSplitAccrualVal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDivAccrualVal() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPublishSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSicFlags() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, StrategyState msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "liveStartTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "idOfExportComponent" );
        dest.append( MAIN_SEP );
        dest.append( "status" );
        dest.append( MAIN_SEP );
        dest.append( "stratTimestamp" );
        dest.append( MAIN_SEP );
        dest.append( "unrealisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "realisedTotalPnL" );
        dest.append( MAIN_SEP );
        dest.append( "id" );
        dest.append( MAIN_SEP );
        dest.append( "isDeltaMode" );
        dest.append( MAIN_SEP );
        dest.append( "stratStateMsgsInGrp" );
        dest.append( MAIN_SEP );
        dest.append( "curStratStateMsgInGrp" );
        dest.append( MAIN_SEP );
        dest.append( "noInstEntries" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, StrategyState msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getLiveStartTimestamp(), dest ); else dest.append( msg.getLiveStartTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIdOfExportComponent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStatus() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getStratTimestamp(), dest ); else dest.append( msg.getStratTimestamp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUnrealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRealisedTotalPnL() );
        dest.append( MAIN_SEP );
        dest.append( msg.getId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIsDeltaMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStratStateMsgsInGrp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCurStratStateMsgInGrp() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNoInstEntries() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, UTPLogon msg ) {
        dest.append( "lastMsgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, UTPLogon msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLastMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, UTPLogonReject msg ) {
        dest.append( "lastMsgSeqNumRcvd" );
        dest.append( MAIN_SEP );
        dest.append( "lastMsgSeqNumSent" );
        dest.append( MAIN_SEP );
        dest.append( "rejectCode" );
        dest.append( MAIN_SEP );
        dest.append( "rejectText" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, UTPLogonReject msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLastMsgSeqNumRcvd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMsgSeqNumSent() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRejectCode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRejectText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, UTPTradingSessionStatus msg ) {
        dest.append( "mktPhaseChgTime" );
        dest.append( MAIN_SEP );
        dest.append( "instClassId" );
        dest.append( MAIN_SEP );
        dest.append( "instClassStatus" );
        dest.append( MAIN_SEP );
        dest.append( "orderEntryAllowed" );
        dest.append( MAIN_SEP );
        dest.append( "tradingSessionId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, UTPTradingSessionStatus msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getMktPhaseChgTime(), dest ); else dest.append( msg.getMktPhaseChgTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstClassId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getInstClassStatus() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderEntryAllowed() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingSessionId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIConnectionGatewayRequest msg ) {
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "partyIDSessionID" );
        dest.append( MAIN_SEP );
        dest.append( "password" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIConnectionGatewayRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPartyIDSessionID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIConnectionGatewayResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "gatewayID" );
        dest.append( MAIN_SEP );
        dest.append( "gatewaySubID" );
        dest.append( MAIN_SEP );
        dest.append( "secGatewayID" );
        dest.append( MAIN_SEP );
        dest.append( "secGatewaySubID" );
        dest.append( MAIN_SEP );
        dest.append( "sessionMode" );
        dest.append( MAIN_SEP );
        dest.append( "tradSesMode" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIConnectionGatewayResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getGatewayID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getGatewaySubID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecGatewayID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecGatewaySubID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSessionMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradSesMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISessionLogonRequest msg ) {
        dest.append( "heartBtIntMS" );
        dest.append( MAIN_SEP );
        dest.append( "partyIDSessionID" );
        dest.append( MAIN_SEP );
        dest.append( "defaultCstmApplVerID" );
        dest.append( MAIN_SEP );
        dest.append( "password" );
        dest.append( MAIN_SEP );
        dest.append( "applUsageOrders" );
        dest.append( MAIN_SEP );
        dest.append( "applUsageQuotes" );
        dest.append( MAIN_SEP );
        dest.append( "orderRoutingIndicator" );
        dest.append( MAIN_SEP );
        dest.append( "applicationSystemName" );
        dest.append( MAIN_SEP );
        dest.append( "applicationSystemVer" );
        dest.append( MAIN_SEP );
        dest.append( "applicationSystemVendor" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISessionLogonRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getHeartBtIntMS() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPartyIDSessionID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDefaultCstmApplVerID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplUsageOrders() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplUsageQuotes() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderRoutingIndicator() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplicationSystemName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplicationSystemVer() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplicationSystemVendor() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISessionLogonResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "throttleTimeIntervalMS" );
        dest.append( MAIN_SEP );
        dest.append( "throttleNoMsgs" );
        dest.append( MAIN_SEP );
        dest.append( "throttleDisconnectLimit" );
        dest.append( MAIN_SEP );
        dest.append( "heartBtIntMS" );
        dest.append( MAIN_SEP );
        dest.append( "sessionInstanceID" );
        dest.append( MAIN_SEP );
        dest.append( "tradSesMode" );
        dest.append( MAIN_SEP );
        dest.append( "defaultCstmApplVerID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISessionLogonResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getThrottleTimeIntervalMS() );
        dest.append( MAIN_SEP );
        dest.append( msg.getThrottleNoMsgs() );
        dest.append( MAIN_SEP );
        dest.append( msg.getThrottleDisconnectLimit() );
        dest.append( MAIN_SEP );
        dest.append( msg.getHeartBtIntMS() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSessionInstanceID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradSesMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDefaultCstmApplVerID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISessionLogoutRequest msg ) {
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISessionLogoutRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISessionLogoutResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISessionLogoutResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISessionLogoutNotification msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "reason" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISessionLogoutNotification msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUserLogonRequest msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "password" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUserLogonRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUserLogonResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUserLogonResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUserLogoutRequest msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUserLogoutRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUserLogoutResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUserLogoutResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIThrottleUpdateNotification msg ) {
        dest.append( "throttleTimeIntervalMS" );
        dest.append( MAIN_SEP );
        dest.append( "throttleNoMsgs" );
        dest.append( MAIN_SEP );
        dest.append( "throttleDisconnectLimit" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIThrottleUpdateNotification msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getThrottleTimeIntervalMS() );
        dest.append( MAIN_SEP );
        dest.append( msg.getThrottleNoMsgs() );
        dest.append( MAIN_SEP );
        dest.append( msg.getThrottleDisconnectLimit() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISubscribe msg ) {
        dest.append( "subscriptionScope" );
        dest.append( MAIN_SEP );
        dest.append( "refApplID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISubscribe msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSubscriptionScope() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefApplID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETISubscribeResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "applSubID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETISubscribeResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplSubID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUnsubscribe msg ) {
        dest.append( "refApplSubID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUnsubscribe msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getRefApplSubID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIUnsubscribeResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIUnsubscribeResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIRetransmit msg ) {
        dest.append( "applBegSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "applEndSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "subscriptionScope" );
        dest.append( MAIN_SEP );
        dest.append( "partitionID" );
        dest.append( MAIN_SEP );
        dest.append( "refApplID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIRetransmit msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getApplBegSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplEndSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSubscriptionScope() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPartitionID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefApplID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIRetransmitResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "applEndSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "refApplLastSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "applTotalMessageCount" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIRetransmitResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplEndSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefApplLastSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplTotalMessageCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIRetransmitOrderEvents msg ) {
        dest.append( "subscriptionScope" );
        dest.append( MAIN_SEP );
        dest.append( "partitionID" );
        dest.append( MAIN_SEP );
        dest.append( "refApplID" );
        dest.append( MAIN_SEP );
        dest.append( "applBegMsgID" );
        dest.append( MAIN_SEP );
        dest.append( "applEndMsgID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIRetransmitOrderEvents msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSubscriptionScope() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPartitionID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefApplID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplBegMsgID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplEndMsgID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, ETIRetransmitOrderEventsResponse msg ) {
        dest.append( "requestTime" );
        dest.append( MAIN_SEP );
        dest.append( "applTotalMessageCount" );
        dest.append( MAIN_SEP );
        dest.append( "applEndMsgID" );
        dest.append( MAIN_SEP );
        dest.append( "refApplLastMsgID" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, ETIRetransmitOrderEventsResponse msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        if ( timeConverter != null ) timeConverter.accept( msg.getRequestTime(), dest ); else dest.append( msg.getRequestTime() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplTotalMessageCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getApplEndMsgID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefApplLastMsgID() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumLogon msg ) {
        dest.append( "lastMsgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "password" );
        dest.append( MAIN_SEP );
        dest.append( "newPassword" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumLogon msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getLastMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getNewPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumLogonReply msg ) {
        dest.append( "rejectCode" );
        dest.append( MAIN_SEP );
        dest.append( "pwdExpiryDayCount" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumLogonReply msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getRejectCode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPwdExpiryDayCount() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumLogout msg ) {
        dest.append( "reason" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumLogout msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getReason() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumMissedMessageRequest msg ) {
        dest.append( "appId" );
        dest.append( MAIN_SEP );
        dest.append( "lastMsgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumMissedMessageRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getAppId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumMissedMsgRequestAck msg ) {
        dest.append( "missedMsgReqAckType" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumMissedMsgRequestAck msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMissedMsgReqAckType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, MilleniumMissedMsgReport msg ) {
        dest.append( "missedMsgReportType" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, MilleniumMissedMsgReport msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMissedMsgReportType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, BookAddOrder msg ) {
        dest.append( "nanosecond" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "book" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, BookAddOrder msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getNanosecond() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBook() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, BookDeleteOrder msg ) {
        dest.append( "nanosecond" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, BookDeleteOrder msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getNanosecond() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, BookModifyOrder msg ) {
        dest.append( "nanosecond" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, BookModifyOrder msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getNanosecond() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, BookClear msg ) {
        dest.append( "nanosecond" );
        dest.append( MAIN_SEP );
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "book" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, BookClear msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getNanosecond() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getBook() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchSymbolClear msg ) {
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchSymbolClear msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchBookAddOrder msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "orderQty" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "typeIndic" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchBookAddOrder msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getOrderQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTypeIndic() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchBookOrderExecuted msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "mktMech" );
        dest.append( MAIN_SEP );
        dest.append( "tradingMode" );
        dest.append( MAIN_SEP );
        dest.append( "dividend" );
        dest.append( MAIN_SEP );
        dest.append( "algoTrade" );
        dest.append( MAIN_SEP );
        dest.append( "tranCat" );
        dest.append( MAIN_SEP );
        dest.append( "refPriceInd" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchBookOrderExecuted msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktMech() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getDividend() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAlgoTrade() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTranCat() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefPriceInd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchOffBookTrade msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "side" );
        dest.append( MAIN_SEP );
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "lastQty" );
        dest.append( MAIN_SEP );
        dest.append( "lastPx" );
        dest.append( MAIN_SEP );
        dest.append( "execId" );
        dest.append( MAIN_SEP );
        dest.append( "mktMech" );
        dest.append( MAIN_SEP );
        dest.append( "tradingMode" );
        dest.append( MAIN_SEP );
        dest.append( "tranCat" );
        dest.append( MAIN_SEP );
        dest.append( "refPriceInd" );
        dest.append( MAIN_SEP );
        dest.append( "algoTrade" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchOffBookTrade msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSide() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getLastPx() );
        dest.append( MAIN_SEP );
        dest.append( msg.getExecId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMktMech() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTradingMode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getTranCat() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefPriceInd() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAlgoTrade() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchBookCancelOrder msg ) {
        dest.append( "orderId" );
        dest.append( MAIN_SEP );
        dest.append( "cancelQty" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchBookCancelOrder msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getOrderId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getCancelQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, PitchPriceStatistic msg ) {
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "statType" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, PitchPriceStatistic msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getStatType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, AuctionUpdate msg ) {
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "auctionType" );
        dest.append( MAIN_SEP );
        dest.append( "refPrice" );
        dest.append( MAIN_SEP );
        dest.append( "indicativePrice" );
        dest.append( MAIN_SEP );
        dest.append( "indicativeShares" );
        dest.append( MAIN_SEP );
        dest.append( "priceCollarTol" );
        dest.append( MAIN_SEP );
        dest.append( "incPrimary" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, AuctionUpdate msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAuctionType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRefPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIndicativePrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIndicativeShares() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPriceCollarTol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIncPrimary() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, AuctionSummary msg ) {
        dest.append( "securityId" );
        dest.append( MAIN_SEP );
        dest.append( "securityIdSrc" );
        dest.append( MAIN_SEP );
        dest.append( "securityExchange" );
        dest.append( MAIN_SEP );
        dest.append( "auctionType" );
        dest.append( MAIN_SEP );
        dest.append( "price" );
        dest.append( MAIN_SEP );
        dest.append( "qty" );
        dest.append( MAIN_SEP );
        dest.append( "priceCollarTol" );
        dest.append( MAIN_SEP );
        dest.append( "incPrimary" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, AuctionSummary msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSecurityId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityIdSrc() );
        dest.append( MAIN_SEP );
        dest.append( msg.getSecurityExchange() );
        dest.append( MAIN_SEP );
        dest.append( msg.getAuctionType() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPrice() );
        dest.append( MAIN_SEP );
        dest.append( msg.getQty() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPriceCollarTol() );
        dest.append( MAIN_SEP );
        dest.append( msg.getIncPrimary() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SoupDebugPacket msg ) {
        dest.append( "text" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SoupDebugPacket msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getText() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SoupLogInAccepted msg ) {
        dest.append( "sessionId" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SoupLogInAccepted msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getSessionId() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SoupLogInRejected msg ) {
        dest.append( "rejectReasonCode" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SoupLogInRejected msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getRejectReasonCode() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, SoupLogInRequest msg ) {
        dest.append( "userName" );
        dest.append( MAIN_SEP );
        dest.append( "password" );
        dest.append( MAIN_SEP );
        dest.append( "requestedSession" );
        dest.append( MAIN_SEP );
        dest.append( "requestedSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, SoupLogInRequest msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getUserName() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPassword() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRequestedSession() );
        dest.append( MAIN_SEP );
        dest.append( msg.getRequestedSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

    public static void writeHeader( ReusableString dest, UnsequencedDataPacket msg ) {
        dest.append( "message" );
        dest.append( MAIN_SEP );
        dest.append( "msgSeqNum" );
        dest.append( MAIN_SEP );
        dest.append( "possDupFlag" );
        dest.append( MAIN_SEP );
        dest.append( "eventTimestamp" );
        dest.append( MAIN_SEP );
    }

    public static void write( ReusableString dest, UnsequencedDataPacket msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {
        dest.append( msg.getMessage() );
        dest.append( MAIN_SEP );
        dest.append( msg.getMsgSeqNum() );
        dest.append( MAIN_SEP );
        dest.append( msg.getPossDupFlag() );
        dest.append( MAIN_SEP );
        if ( timeConverter != null ) timeConverter.accept( msg.getEventTimestamp(), dest ); else dest.append( msg.getEventTimestamp() );
        dest.append( MAIN_SEP );
    }

}
