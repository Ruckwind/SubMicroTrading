/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.utils.FileException;
import com.rr.core.utils.Utils;
import com.rr.inst.BaseInstrumentSecDefStore.Indexes;
import com.rr.model.generated.fix.codec.CMEMDDecoder;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.BaseOMTestCase;
import com.rr.om.exchange.ExchangeManager;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

public class DumpFixInstrumentSpreads extends BaseOMTestCase {

    private static final Logger _log = LoggerFactory.create( DumpFixInstrumentSpreads.class );
    private              int    _calenderSpread;
    private              int    _otherSpread;

    /**
     * SPREAD insts support temp withdrawn as requires more work in new inst store
     *
     * @throws FileException
     */
    @Ignore
    @Test
    public void testLoadCMECheckSpreads() throws FileException {
        Exchange                      cme       = ExchangeManager.instance().getByCode( ExchangeCode.XCME );
        SingleExchangeInstrumentStore instStore = new SingleExchangeInstrumentStore( cme, 1000 );
        instStore.setTickManager( new TickManager( "tstTickMgr" ) );
        FixInstrumentLoader loader = new FixInstrumentLoader( instStore, new CMEMDDecoder( "testDecdoer" ) );
        loader.loadFromFile( "../data/cme/secdef.dat" );

        Indexes ind = instStore.getExchangeMap( null, 0 );

        Collection<InstrumentSecurityDefWrapper> insts = ind.getSymbolIndex().values();

        int            spreadCnt = 1;
        ReusableString msg       = new ReusableString();

        /**
         * for each applId #spreads
         */

        Map<Integer, Integer>                 applIdToSpreadMap        = new HashMap<>();
        Map<Integer, Set<Integer>>            spreadSegmentIdToDepends = new HashMap<>();
        Map<Integer, Set<ExchangeInstrument>> segmentInst              = new HashMap<>();
        Map<ExchangeInstrument, Integer>      instSpreadCount          = new HashMap<>();

        boolean logAnyway = true;

        int bothLegsBuy     = 0;
        int bothLegsSell    = 0;
        int leg1Buyleg2Sell = 0;
        int leg1Sellleg2Buy = 0;

        for ( InstrumentSecurityDefWrapper instWrapper : insts ) {
            if ( instWrapper instanceof DerivInstSecDefWrapper ) {
                final DerivInstSecDefWrapper inst = (DerivInstSecDefWrapper) instWrapper;

                if ( inst.getNumLegs() == 2 ) {
                    if ( inst.getLeg( 0 ) == null || inst.getLeg( 1 ) == null ) continue;

                    msg.copy( "Spread #" ).append( spreadCnt ).append( " " ).append( inst.getSecurityDesc() ).append( ", segment=" ).append( inst.getIntSegment() ).append( "     " );

                    incApplIdMap( applIdToSpreadMap, inst.getIntSegment() );

                    addInstrumentToSegment( segmentInst, inst.getIntSegment(), inst );

                    boolean legsPresent = true;
                    boolean sameApplId  = true;

                    if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() && inst.getLeg( 1 ).getLegSide().getIsBuySide() ) {
                        ++bothLegsBuy;
                    }

                    if ( (inst.getLeg( 0 ).getLegSide().getIsBuySide() == false) && (false == inst.getLeg( 1 ).getLegSide().getIsBuySide()) ) {
                        ++bothLegsSell;
                    }

                    if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == true && inst.getLeg( 1 ).getLegSide().getIsBuySide() == false ) {
                        ++leg1Buyleg2Sell;
                    }

                    if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == false && inst.getLeg( 1 ).getLegSide().getIsBuySide() == true ) {
                        ++leg1Sellleg2Buy;
                    }

                    for ( int i = 0; i < inst.getNumLegs(); i++ ) {
                        LegInstrument leg = inst.getLeg( i );

                        InstrumentSecurityDefWrapper legInst = (InstrumentSecurityDefWrapper) leg.getInstrument();

                        addInstrumentSpreadCount( instSpreadCount, legInst );

                        SecurityDefinitionImpl legSecDef = legInst.getSecDef();

                        addInstrumentToSegment( segmentInst, inst.getIntSegment(), legInst );
                        addSpreadSegmentSet( spreadSegmentIdToDepends, inst.getIntSegment(), legInst.getIntSegment() );

                        if ( legSecDef == null ) {
                            msg.append( " [leg" ).append( i + 1 )
                               .append( " id=" ).append( legInst.getExchangeSymbol() )
                               .append( ", side=" ).append( leg.getLegSide().toString() )
                               .append( ", legInstLoaded=" ).append( 'N' )
                               .append( ", segment=" ).append( legInst.getIntSegment() )
                               .append( "] " );

                            legsPresent = false;

                        } else {
                            ZString secDesc = legInst.getSecurityDesc();
                            if ( secDesc.length() == 0 ) secDesc = legSecDef.getSecurityDesc();

                            msg.append( " [leg" ).append( i + 1 )
                               .append( " secDes=" ).append( secDesc )
                               .append( ", side=" ).append( leg.getLegSide().toString() )
                               .append( ", legInstLoaded=" ).append( 'Y' )
                               .append( ", segment=" ).append( legInst.getIntSegment() )
                               .append( "] " );
                        }

                        if ( legInst.getIntSegment() != inst.getIntSegment() ) {
                            sameApplId = false;
                        }
                    }

                    msg.append( ", priceRation=" ).append( inst.getSecDef().getPriceRatio() );

                    if ( sameApplId == false ) {
                        msg.append( "  MULTI_SEGMENT" );
                    }

                    if ( logAnyway || legsPresent ) {
                        ++spreadCnt;

                        if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == true && inst.getLeg( 1 ).getLegSide().getIsBuySide() == false ) {

                            if ( Utils.isNull( inst.getSecDef().getPriceRatio() ) ) {

                                try {
                                    String leg1SecDesc = inst.getLegSecurityDesc( 0, new ReusableString() ).toString();
                                    String leg2SecDesc = inst.getLegSecurityDesc( 1, new ReusableString() ).toString();

                                    String part1 = leg1SecDesc.substring( 0, leg1SecDesc.length() - 2 );
                                    String part2 = leg2SecDesc.substring( 0, leg2SecDesc.length() - 2 );

                                    if ( part1.equals( part2 ) ) {
                                        _log.info( "CALENDAR SPREAD : " + msg );
                                        ++_calenderSpread;
                                    } else {
                                        _log.info( "OTHER    SPREAD : " + msg );
                                        ++_otherSpread;
                                    }
                                } catch( Exception e ) {
                                    // dont care
                                }
                            }
                        }
                    }
                }
            }
        }
/*        
        for( Map.Entry<Integer, Integer> e : applIdToSpreadMap.entrySet() ) {
            _log.info( "ApplId " + e.getKey() + " = " + e.getValue() + " spreads" );
        }

        for( Map.Entry<Integer, Set<Integer>> e : spreadSegmentIdToDepends.entrySet() ) {
            msg.copy( "Spread segment Id " + e.getKey() + " depends" );
            Set<Integer> s = e.getValue();
            boolean first = true;
            int totalInsts = segmentInst.get( e.getKey() ).size();
            for( Integer i : s ) {
                if ( first ) {
                    first = false;
                } else {
                    msg.append( "," );
                }
                int segSize = segmentInst.get( i ).size();
                msg.append( " " + i + " (" + segSize + ")");
                if ( i != e.getKey() ) {
                    totalInsts += segSize;
                }
            }
            msg.append( ", totalInsts=" + totalInsts );
            _log.info( msg );
        }

        int threshold = 20;
        
        for( Map.Entry<Instrument, Integer> e : instSpreadCount.entrySet() ) {
            if ( e.getValue().intValue() > threshold ) {
                _log.info( "Inst " + e.getKey().getSecurityDesc() + " on segment " + e.getKey().getIntSegment() + ", in " + e.getValue() + " spreads" );
            }
        }
  */
        _log.info( "LegsBothBuy #" + bothLegsBuy + ", LegsBothSell=" + bothLegsSell + ", leg1BuyLeg2Sell=" + leg1Buyleg2Sell + ", leg1SellLeg2Buy=" + leg1Sellleg2Buy +
                   ", calSpread=" + _calenderSpread + ", otherSpread=" + _otherSpread );
    }

    @SuppressWarnings( "boxing" )
    private void addInstrumentSpreadCount( Map<ExchangeInstrument, Integer> instSpreadCount, InstrumentSecurityDefWrapper inst ) {
        instSpreadCount.merge( inst, 1, ( a, b ) -> a + b );
    }

    @SuppressWarnings( "boxing" )
    private void addInstrumentToSegment( Map<Integer, Set<ExchangeInstrument>> segmentInst, int instSegment, ExchangeInstrument inst ) {
        Set<ExchangeInstrument> set = segmentInst.computeIfAbsent( instSegment, k -> new HashSet<>() );
        set.add( inst );
    }

    @SuppressWarnings( "boxing" )
    private void addSpreadSegmentSet( Map<Integer, Set<Integer>> spreadSegmentIdToDepends, int instSegment, int legSegment ) {
        Set<Integer> set = spreadSegmentIdToDepends.computeIfAbsent( instSegment, k -> new HashSet<>() );
        set.add( legSegment );
    }

    @SuppressWarnings( "boxing" )
    private void incApplIdMap( Map<Integer, Integer> applIdToSpreadMap, int intSegment ) {
        applIdToSpreadMap.merge( intSegment, 1, ( a, b ) -> a + b );
    }
}
