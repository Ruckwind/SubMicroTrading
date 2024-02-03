/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Constants;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.SecurityIDSource;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;
import org.junit.Assert;

@SuppressWarnings( "boxing" )

public class FastFixTstUtils {

    public static void checkEqualsA( MDIncRefreshImpl exp, MDIncRefreshImpl decoded ) {
        Assert.assertEquals( exp.getEventTimestamp(), decoded.getEventTimestamp() );
        Assert.assertEquals( exp.getMsgSeqNum(), decoded.getMsgSeqNum() );
        Assert.assertEquals( exp.getPossDupFlag(), decoded.getPossDupFlag() );
        Assert.assertEquals( exp.getMsgSeqNum(), decoded.getMsgSeqNum() );

        int expEntries = exp.getNoMDEntries();

        Assert.assertEquals( exp.getNoMDEntries(), decoded.getNoMDEntries() );

        MDEntryImpl expEntry     = (MDEntryImpl) exp.getMDEntries();
        MDEntryImpl decodedEntry = (MDEntryImpl) decoded.getMDEntries();

        for ( int i = 0; i < expEntries; i++ ) {

            Assert.assertEquals( expEntry.getSecurityIDSource(), decodedEntry.getSecurityIDSource() );
            Assert.assertEquals( expEntry.getSecurityID(), decodedEntry.getSecurityID() );
            Assert.assertEquals( expEntry.getMdUpdateAction(), decodedEntry.getMdUpdateAction() );
            Assert.assertEquals( expEntry.getRepeatSeq(), decodedEntry.getRepeatSeq() );
            Assert.assertEquals( expEntry.getNumberOfOrders(), decodedEntry.getNumberOfOrders() );
            Assert.assertEquals( expEntry.getMdPriceLevel(), decodedEntry.getMdPriceLevel() );
            Assert.assertEquals( expEntry.getMdEntryType(), decodedEntry.getMdEntryType() );
            Assert.assertEquals( expEntry.getMdEntryPx(), decodedEntry.getMdEntryPx(), Constants.TICK_WEIGHT );
            Assert.assertEquals( expEntry.getMdEntrySize(), decodedEntry.getMdEntrySize() );
            Assert.assertEquals( expEntry.getMdEntryTime(), decodedEntry.getMdEntryTime() );

            expEntry     = expEntry.getNext();
            decodedEntry = decodedEntry.getNext();
        }
    }

    public static void checkEqualsB( MDIncRefreshImpl exp, MDIncRefreshImpl decoded ) {
        Assert.assertEquals( exp.getEventTimestamp(), decoded.getEventTimestamp() );
        Assert.assertEquals( exp.getMsgSeqNum(), decoded.getMsgSeqNum() );
        Assert.assertEquals( exp.getPossDupFlag(), decoded.getPossDupFlag() );
        Assert.assertEquals( exp.getMsgSeqNum(), decoded.getMsgSeqNum() );

        int expEntries = exp.getNoMDEntries();

        Assert.assertEquals( exp.getNoMDEntries(), decoded.getNoMDEntries() );

        MDEntryImpl expEntry     = (MDEntryImpl) exp.getMDEntries();
        MDEntryImpl decodedEntry = (MDEntryImpl) decoded.getMDEntries();

        for ( int i = 0; i < expEntries; i++ ) {

            Assert.assertEquals( expEntry.getSecurityIDSource(), decodedEntry.getSecurityIDSource() );
            Assert.assertEquals( expEntry.getSecurityID(), decodedEntry.getSecurityID() );
            Assert.assertEquals( expEntry.getMdUpdateAction(), decodedEntry.getMdUpdateAction() );
            Assert.assertEquals( expEntry.getRepeatSeq(), decodedEntry.getRepeatSeq() );
            Assert.assertEquals( expEntry.getMdEntryType(), decodedEntry.getMdEntryType() );
            Assert.assertEquals( expEntry.getMdEntryPx(), decodedEntry.getMdEntryPx(), Constants.TICK_WEIGHT );
            Assert.assertEquals( expEntry.getMdEntrySize(), decodedEntry.getMdEntrySize() );
            Assert.assertEquals( expEntry.getMdEntryTime(), decodedEntry.getMdEntryTime() );

            expEntry     = expEntry.getNext();
            decodedEntry = decodedEntry.getNext();
        }
    }

    @SuppressWarnings( "null" )
    public static MDIncRefreshImpl makeMDIncRefresh( int numMDEntries ) {

        MDIncRefreshImpl inc = new MDIncRefreshImpl();

        inc.setEventTimestamp( 20120403194342222L );
        inc.setMsgSeqNum( 1000000 );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( numMDEntries );

        MDEntryImpl first = null;
        MDEntryImpl tmp   = null;

        for ( int i = 0; i < numMDEntries; i++ ) {

            if ( first == null ) {
                tmp = first = new MDEntryImpl();
            } else {
                tmp.setNext( new MDEntryImpl() );
                tmp = tmp.getNext();
            }

            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().setValue( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.New );
            tmp.setRepeatSeq( i + 1 );
            tmp.setNumberOfOrders( i * 10 );
            tmp.setMdPriceLevel( i + 1 );
            tmp.setMdEntryType( MDEntryType.Bid );
            tmp.setMdEntryPx( 1000.12345 + i );
            tmp.setMdEntrySize( 100 + i * 10 );
            tmp.setMdEntryTime( 800000 + i );
        }

        inc.setMDEntries( first );

        return inc;
    }

    @SuppressWarnings( "null" )
    public static MDIncRefreshImpl makeMDIncRefresh( int num, int numMDEntries ) {

        ++num;

        MDIncRefreshImpl inc = new MDIncRefreshImpl();

        inc.setEventTimestamp( 20120403194342222L + num );
        inc.setMsgSeqNum( 1000000 + num );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( numMDEntries );

        MDEntryImpl first = null;
        MDEntryImpl tmp   = null;

        for ( int i = 0; i < numMDEntries; i++ ) {

            if ( first == null ) {
                tmp = first = new MDEntryImpl();
            } else {
                tmp.setNext( new MDEntryImpl() );
                tmp = tmp.getNext();
            }

            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().setValue( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.New );
            tmp.setRepeatSeq( i + 1 );
            tmp.setNumberOfOrders( i * (10 * num) );
            tmp.setMdPriceLevel( i );
            tmp.setMdEntryType( MDEntryType.Bid );
            tmp.setMdEntryPx( 1000.12345 + i );
            tmp.setMdEntrySize( 100 + i * 10 );
            tmp.setMdEntryTime( 800000 + i );
        }

        inc.setMDEntries( first );

        return inc;
    }

    public static MDIncRefreshImpl makeTOPBookIncRefresh() {

        MDIncRefreshImpl inc = new MDIncRefreshImpl();

        inc.setEventTimestamp( ClockFactory.get().currentTimeMillis() );
        inc.setMsgSeqNum( 0 );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( 2 );

        MDEntryImpl first;
        MDEntryImpl tmp;

        tmp = first = new MDEntryImpl();

        tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        tmp.getSecurityIDForUpdate().copy( ExchangeInstrument.DUMMY_INSTRUMENT_ID );
        tmp.setMdUpdateAction( MDUpdateAction.Change );
        tmp.setRepeatSeq( 1 );
        tmp.setNumberOfOrders( 10 );
        tmp.setMdPriceLevel( 1 );
        tmp.setMdEntryType( MDEntryType.Bid );
        tmp.setMdEntryPx( 1000.12345 );
        tmp.setMdEntrySize( 1000 );
        tmp.setMdEntryTime( 800000 );

        tmp.setNext( new MDEntryImpl() );
        tmp = tmp.getNext();

        tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        tmp.getSecurityIDForUpdate().copy( ExchangeInstrument.DUMMY_INSTRUMENT_ID );
        tmp.setMdUpdateAction( MDUpdateAction.Change );
        tmp.setRepeatSeq( 2 );
        tmp.setNumberOfOrders( 10 );
        tmp.setMdPriceLevel( 1 );
        tmp.setMdEntryType( MDEntryType.Offer );
        tmp.setMdEntryPx( 1000.12355 );
        tmp.setMdEntrySize( 1000 );
        tmp.setMdEntryTime( 800000 );

        inc.setMDEntries( first );

        return inc;
    }
}
