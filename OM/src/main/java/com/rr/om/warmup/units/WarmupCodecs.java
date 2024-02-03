/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.units;

import com.rr.core.codec.FixEncodeBuilder;
import com.rr.core.codec.FixEncodeBuilderImpl;
import com.rr.core.dummy.warmup.DummyEventHandler;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.warmup.JITWarmup;
import com.rr.model.generated.internal.events.impl.*;

import java.nio.ByteBuffer;

public class WarmupCodecs implements JITWarmup {

    private int _warmupCount;

    public WarmupCodecs( int warmupCount ) {
        _warmupCount = warmupCount;
    }

    @Override
    public String getName() {
        return "Codecs";
    }

    @Override
    public void warmup() throws InstantiationException, IllegalAccessException {
        warmupCodec();
        warmupEncodeLong();
        warmupTime();
    }

    public void warmupCodec() throws InstantiationException, IllegalAccessException {
        // @TODO generate a warmup class
        @SuppressWarnings( "rawtypes" )
        Class[] classes = {
                RejectedImpl.class, TradeNewImpl.class, NewOrderSingleImpl.class,
                ClientNewOrderSingleImpl.class, MarketNewOrderSingleImpl.class, ClientCancelReplaceRequestImpl.class,
                MarketCancelReplaceRequestImpl.class, ClientCancelRequestImpl.class, MarketCancelRequestImpl.class,
                ClientForceCancelImpl.class, MarketForceCancelImpl.class, ClientCancelRejectImpl.class, MarketCancelRejectImpl.class,
                ClientAlertLimitBreachImpl.class, MarketAlertLimitBreachImpl.class, ClientAlertTradeMissingOrdersImpl.class,
                MarketAlertTradeMissingOrdersImpl.class, ClientNewOrderAckImpl.class,
                MarketNewOrderAckImpl.class, ClientTradeNewImpl.class, MarketTradeNewImpl.class, ClientRejectedImpl.class, MarketRejectedImpl.class,
                ClientCancelledImpl.class, MarketCancelledImpl.class, ClientReplacedImpl.class, MarketReplacedImpl.class, ClientDoneForDayImpl.class,
                MarketDoneForDayImpl.class, ClientStoppedImpl.class, MarketStoppedImpl.class, ClientExpiredImpl.class, MarketExpiredImpl.class,
                ClientSuspendedImpl.class, MarketSuspendedImpl.class, ClientRestatedImpl.class, MarketRestatedImpl.class, ClientTradeCorrectImpl.class,
                MarketTradeCorrectImpl.class, ClientTradeCancelImpl.class, MarketTradeCancelImpl.class, ClientOrderStatusImpl.class,
                MarketOrderStatusImpl.class, HeartbeatImpl.class, LogonImpl.class, LogoutImpl.class, SessionRejectImpl.class, ResendRequestImpl.class,
                ClientResyncSentMsgsImpl.class, SequenceResetImpl.class, TestRequestImpl.class, HeartbeatImpl.class
        };

        Event        t = new ClientNewOrderSingleImpl();
        EventHandler h = new DummyEventHandler();

        for ( int j = 0; j < classes.length; j++ ) {

            Event m = (Event) classes[ j ].newInstance();

            for ( int i = 0; i < _warmupCount; i++ ) {
                m.attachQueue( t );
                m.setMsgSeqNum( i );
                m.detachQueue();
                m.setEventHandler( h );
            }
        }
    }

    private void warmupEncodeLong() {
        final byte[]     bufLong     = new byte[ 512 ];
        FixEncodeBuilder encoderLong = new FixEncodeBuilderImpl( bufLong, 0, (byte) '4', (byte) '4' );

        for ( int i = 0; i < _warmupCount; i++ ) {
            encoderLong.start();
            encoderLong.encodeLong( 38, i << 10 + i );
        }
    }

    private void warmupTime() {
        ByteBuffer db = ByteBuffer.allocateDirect( 100 );

        long now = ClockFactory.get().currentTimeMillis();

        ReusableString rs = new ReusableString();

        for ( int i = 0; i < _warmupCount; i++ ) {
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( db, now + i );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalStr( db, now + i );
            TimeUtilsFactory.safeTimeUtils().unixTimeToShortLocal( rs, now + i );

            rs.reset();
            db.clear();
        }
    }
}
