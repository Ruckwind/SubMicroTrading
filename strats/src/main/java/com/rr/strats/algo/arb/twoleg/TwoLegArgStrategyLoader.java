/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg;

import com.rr.core.algo.base.StrategyDefinition;
import com.rr.core.algo.base.StrategyDefinitionImpl;
import com.rr.core.algo.mgr.StrategyManager;
import com.rr.core.algo.mgr.StrategyManagerImpl;
import com.rr.core.algo.strats.Algo;
import com.rr.core.algo.strats.Strategy;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.log.Logger;
import com.rr.core.log.LoggerFactory;
import com.rr.core.model.BasicSide;
import com.rr.core.model.Book;
import com.rr.core.model.Instrument;
import com.rr.core.model.LegInstrument;
import com.rr.core.session.Session;
import com.rr.core.session.ThrottleWithExceptions;
import com.rr.core.session.Throttler;
import com.rr.core.thread.BaseNonBlockingWorkerMultiplexor;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTException;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;
import com.rr.hub.AsyncLogSession;
import com.rr.inst.InstrumentSecurityDefWrapper;
import com.rr.inst.InstrumentStore;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.router.OrderRouter;
import com.rr.strats.algo.arb.twoleg.buysell.AlgoExchangeTwoLegBuySellArb;
import com.rr.strats.algo.arb.twoleg.sellbuy.AlgoExchangeTwoLegSellBuyArb;

import java.util.*;

/**
 * loader for multi leg strategies, currently supports two legs
 * 
 * creates strategy instruments for all spreads that adhere to
 * 
 * minLegs
 * maxLegs
 *
 * for now excludes priceRatio which is not 1:1 and non calendar spreads
 * 
 * in list of allowed channels
 *
 * if trace is set the strategy will run in trace mode
 * 
 * if debug is set the loader will dump out info on every strat created
 * 
 * @NOTE NOT SAFE FOR MORE THAN 2 LEGS UNTIL STRAT IS FIXED
 * 
 * @author Richard Rose
 */
public class TwoLegArgStrategyLoader implements SMTSingleComponentLoader {
    
    private static final Logger _log = LoggerFactory.create( TwoLegArgStrategyLoader.class );

    private String             _id;
    private Map<String,String> _stratClassNames;
    private Session            _hubSession = new AsyncLogSession( "HubSession" );
    private OrderRouter        _exchangeRouter;
    private int                _bookLevels = 1;
    private InstrumentStore    _instrumentLocator;
    private int                _throttleRateMsgsPerSecond = 0;
    private SMTComponent[]     _multiplexors = new SMTComponent[0];
    private ZString            _account = new ViewString("DEF");
    private String             _allowedChannels = null;
    
    private boolean            _tradingAllowed = true;
    private boolean            _trace = false;
    private int                _maxOrderQty = 1;
    private int                _maxSlices = Integer.MAX_VALUE;
    private double             _pnlCutoffThreshold = Constants.UNSET_DOUBLE;

    private int                _maxInstSpreadAssociation = Integer.MAX_VALUE;
    private boolean            _debug = true;

    private Map<String,String> _pipeToChannels = new HashMap<String,String>();
    
    private Map<String, BaseNonBlockingWorkerMultiplexor> _pipeToStratMultiplexor = new HashMap<String,BaseNonBlockingWorkerMultiplexor>();

    private Map<BaseNonBlockingWorkerMultiplexor, Throttler> _throttlers = new HashMap<BaseNonBlockingWorkerMultiplexor, Throttler>();

    private Map<Integer,Integer>            _applIdToSpreadMap          = new HashMap<Integer,Integer>();
    private Map<Integer,Set<Integer>>       _spreadSegmentIdToDepends   = new HashMap<Integer,Set<Integer>>();
    private Map<Integer,Set<Instrument>>    _segmentInst                = new HashMap<Integer,Set<Instrument>>();
    private Map<Instrument,Integer>         _instSpreadCount            = new HashMap<Instrument, Integer>();

    private Map<Integer, String>            _channelToPipe              = new HashMap<Integer,String>();
    
    private Set<Integer>                    _allowedChannelSet;

    private boolean                         _allowBuyLeg1SellLeg2       = true;
    private boolean                         _allowSellLeg1BuyLeg2       = true;

    private int                             _spreadCnt;
    private int                             _maxStrategyInstances       = Integer.MAX_VALUE;

    private int                             _skippedSpreads;
    private int                             _disallowedChannel;
    private int                             _legsMissing;
    private int                             _exceedMaxStratCount;
    private int                             _totalSpreadsInRange;
    private int                             _exceedMaxInstSpreadAssociation;
    private int                             _priceRatioNotOneToOne;
    private int                             _skipDisabledBuyLeg1SellLeg2;
    private int                             _skipDisabledSellLeg1BuyLeg2;

    private int _calenderSpread;

    private int _otherSpread;

    private boolean _excludeNonCalSpreads;


    @Override
    public SMTComponent create( String id ) throws SMTException {
        
        _id = id;

        if ( _pipeToChannels.size() == 0 ) {
            _log.warn( "TwoLegArgStrategyLoader has no config for map pipeToChannels (ok for BSE, wrong for CME)" );
        }

        setChannelsToPipe();
        
        _allowedChannelSet = getAllowedChannels();

        StrategyManager mgr = new StrategyManagerImpl( id );

        makeStratMultiplexorMap();
        
        Set<Instrument> insts = new HashSet<Instrument>(); 
        _instrumentLocator.getInstruments( insts, null );
        
        ReusableString msg = new ReusableString();

        /**
         * for each applId #spreads
         */
        
        Iterator<Instrument> it = insts.iterator();

        while( it.hasNext() ) {

            Instrument inst = it.next();
            
            if ( inst.getNumLegs() == 2 ) {
                
                ++_totalSpreadsInRange;
                
                if ( !allowChannel( _allowedChannelSet, inst ) ) {
                    ++_disallowedChannel;
                    continue;
                }

                if ( getInstSpreadCount(inst) > _maxInstSpreadAssociation ) { // SKIP spread
                    ++_exceedMaxInstSpreadAssociation;
                    continue;
                }

                if ( _spreadCnt > _maxStrategyInstances ) {
                    ++_exceedMaxStratCount;
                    break;
                }
                
                msg.copy( "Spread #" ).append( _spreadCnt ).append( " " ).append( inst.getSecurityDesc() )
                   .append( ", segment=" ).append( inst.getIntSegment() )
                   .append( ", #legs=" ).append( inst.getNumLegs() ).append( "     " );
                
                incApplIdMap( _applIdToSpreadMap, inst.getIntSegment() );

                addInstrumentToSegment( _segmentInst, inst.getIntSegment(), inst );

                procInst( (InstrumentSecurityDefWrapper) inst, msg, mgr );
            }
        }
        
        dumpStats( msg );
        
        return mgr;
    }
    
    @Override
    public String toString() {
        return "TwoLegArgStrategyLoader [_id=" + _id + ", _instrumentLocator=" + _instrumentLocator + ", _multiplexors=" + Arrays.toString( _multiplexors ) + "]";
    }

    private void procInst( InstrumentSecurityDefWrapper inst, ReusableString msg, StrategyManager mgr ) {
        boolean allow = true;
        boolean legsPresent = true;
        boolean sameChannel = true;

        if ( allLegsSameSide( inst ) ) {
            allow = false;
        }

        if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == true && inst.getLeg( 1 ).getLegSide().getIsBuySide() == false ) {
            if ( _allowBuyLeg1SellLeg2 == false ) {
                ++_skipDisabledBuyLeg1SellLeg2;
                allow = false;
            }
        }
        
        if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == false && inst.getLeg( 1 ).getLegSide().getIsBuySide() == true ) {
            if ( _allowSellLeg1BuyLeg2 == false ) {
                ++_skipDisabledSellLeg1BuyLeg2;
                allow = false;
            }
        }
        
        int minQty = inst.getMinQty(); 
        
        for ( int i=0 ; allow && i < inst.getNumLegs() ; i++ ) {
            LegInstrument leg = inst.getLeg( i );

            InstrumentSecurityDefWrapper legInst = (InstrumentSecurityDefWrapper)leg.getInstrument();

            if ( !allowChannel( _allowedChannelSet, legInst ) ) {
                ++_disallowedChannel;
                allow = false;
            }

            if ( procSpreadLeg( msg, inst, i, leg, legInst ) == false ) {
                ++_legsMissing;
                allow = false;
            }
            
            if ( legInst.getIntSegment() != inst.getIntSegment() ) {
                sameChannel = false;
            }
            
            if ( getInstSpreadCount(legInst) > _maxInstSpreadAssociation ) { // SKIP spread
                ++_exceedMaxInstSpreadAssociation;
                allow = false;
            }

            if ( legInst.getMinQty() > minQty ) {
                minQty = legInst.getMinQty(); 
            }
        }

        if ( minQty > inst.getMinQty() ) {
            inst.getSecDef().setMinQty( minQty );
        }
        
        final double priceRatio = inst.getSecurityDefinition().getPriceRatio();
        if ( Utils.hasValue( priceRatio ) ) {
            if ( inst.getSecurityDefinition().getPriceRatio() > 1.0000005 || inst.getSecurityDefinition().getPriceRatio() < 0.999999995 ) {
                ++_priceRatioNotOneToOne;
                allow = false;
            }
        }

        if ( sameChannel == false ) {
            msg.append( "  MULTI_SEGMENT" );
        }
        
        String leg1SecDesc = inst.getLegSecurityDesc( 0, new ReusableString() ).toString(); 
        String leg2SecDesc = inst.getLegSecurityDesc( 1, new ReusableString() ).toString(); 

        allow = restrictToCalSpread( allow, leg1SecDesc, leg2SecDesc );

        if ( legsPresent && allow ) {
            ++_spreadCnt;

            if ( inst.getNumLegs() == 2 ) {
                StrategyDefinition def = createToLegStrategyDef( inst );

                if ( def != null ) {
                    createTwoLegStrategy( mgr, inst, msg, def, minQty );
                }
            }
        } else {
            ++_skippedSpreads;
        }
    }

    private boolean restrictToCalSpread( boolean allow, String leg1SecDesc, String leg2SecDesc ) {
        String part1 = leg1SecDesc.substring( 0, leg1SecDesc.length() - 2 );
        String part2 = leg2SecDesc.substring( 0, leg2SecDesc.length() - 2 );

        if ( part1.equals( part2 ) ) {
            ++_calenderSpread;
            
        } else if ( _excludeNonCalSpreads ) { 
            
            allow = false;
            ++_otherSpread;
        } else {
            ++_otherSpread;
        }
        
        return allow;
    }

    private boolean allLegsSameSide( Instrument inst ) {
        
        BasicSide firstSide = inst.getLeg( 0 ).getLegSide();
        
        for ( int i=1 ; i < inst.getNumLegs() ; i++ ) {
            LegInstrument leg = inst.getLeg( i );
            
            if ( leg.getLegSide().getIsBuySide() != firstSide.getIsBuySide() ) {
                return false;
            }
        }
        
        return true;
    }

    @SuppressWarnings( "boxing" )
    private StrategyDefinition createToLegStrategyDef( Instrument inst ) {
        String id = "TwoLegArb_" + inst.getSecurityDesc();
        String pipeLineId = _channelToPipe.get( inst.getIntSegment() ); 
        
        if ( pipeLineId == null ) {
            throw new SMTRuntimeException( "pipeToChannels missing mapping for inst channel " + inst.getIntSegment() );
        }
        
        List<Instrument> insts = new ArrayList<Instrument>(); 

        insts.add( inst ); // spread inst must be first
        
        for( int i=0 ; i < inst.getNumLegs() ; i++ ) {
            Instrument leg = inst.getLeg( i ).getInstrument();
            insts.add( leg );
        }
        
        Map<String, String> props = new HashMap<String, String>();

        StrategyDefinition def = null;

        if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == true && inst.getLeg( 1 ).getLegSide().getIsBuySide() == false ) {
            def = new StrategyDefinitionImpl( id, "TwoLegBuySellArb", pipeLineId, AlgoExchangeTwoLegBuySellArb.class, insts, props );
        } else if ( inst.getLeg( 0 ).getLegSide().getIsBuySide() == false && inst.getLeg( 1 ).getLegSide().getIsBuySide() == true ) {
            def = new StrategyDefinitionImpl( id, "TwoLegSellBuyArb", pipeLineId, AlgoExchangeTwoLegSellBuyArb.class, insts, props );
        } 
        
        return def;
    }

    private void setChannelsToPipe() {
        for( Map.Entry<String, String> entry : _pipeToChannels.entrySet() ) {
            String pipe = entry.getKey();
            String[] channels = entry.getValue().trim().split(",");
            for( String channel : channels ) {
                channel = channel.trim();
                
                _channelToPipe.put( Integer.decode( channel ), pipe );
            }
        }
    }

    @SuppressWarnings( "boxing" )
    private boolean allowChannel( Set<Integer> allowedChannels, Instrument inst ) {
        if ( allowedChannels.size() == 0 ) return true;

        return( allowedChannels.contains( inst.getIntSegment() ) );
    }

    public Set<Integer> getAllowedChannels() {
        Set<Integer> allowedChannels = new HashSet<Integer>();
        
        if ( _allowedChannels != null ) {
            String[] channelParts = _allowedChannels.split(",");
            
            for( String channel : channelParts ) {
                channel = channel.trim();
                
                if ( channel.length() > 0 ) {
                    Integer i = Integer.decode( channel );
                    
                    allowedChannels.add( i );
                }
            }
        }
        
        return allowedChannels;
    }

    public void dumpStats( ReusableString msg ) {
        
        _log.info( "STATS " +
                   "  TotalSpreadsInRange=" + _totalSpreadsInRange +
                   ", StratCount="          + _spreadCnt +
                   ", SkippedSpreads="      + _skippedSpreads +
                   ", DisallowedByChannel=" + _disallowedChannel +
                   ", MissingInstLegs="     + _legsMissing +
                   ", ExceedMaxInstAssoc="  + _exceedMaxInstSpreadAssociation +
                   ", exceedMaxStratCnt="   + _exceedMaxStratCount +
                   ", priceRatioNot1to1="   + _priceRatioNotOneToOne +
                   ", skipBuyLeg1SellLeg2=" + _skipDisabledBuyLeg1SellLeg2 +
                   ", skipSellLeg1BuyLeg2=" + _skipDisabledSellLeg1BuyLeg2 +
                   ", calSpreads="          + _calenderSpread +
                   ", otherExcludedSprds="  + _otherSpread );

        if ( _debug ) {
            
            for( Map.Entry<Integer, Integer> e : _applIdToSpreadMap.entrySet() ) {
                _log.info( "ApplId " + e.getKey() + " = " + e.getValue() + " spreads" );
            }
    
            for( Map.Entry<Integer, Set<Integer>> e : _spreadSegmentIdToDepends.entrySet() ) {
                msg.copy( "Spread segment Id " + e.getKey() + " depends" );
                Set<Integer> s = e.getValue();
                boolean first = true;
                int totalInsts = _segmentInst.get( e.getKey() ).size();
                for( Integer i : s ) {
                    if ( first ) {
                        first = false;
                    } else {
                        msg.append( "," );
                    }
                    int segSize = _segmentInst.get( i ).size();
                    msg.append( " " + i + " (" + segSize + ")");
                    if ( i != e.getKey() ) {
                        totalInsts += segSize;
                    }
                }
                msg.append( ", totalInsts=" + totalInsts );
                _log.info( msg );
            }
    
            int threshold = 20;
            
            for( Map.Entry<Instrument, Integer> e : _instSpreadCount.entrySet() ) {
                if ( e.getValue().intValue() > threshold ) {
                    _log.info( "Inst " + e.getKey().getSecurityDesc() + " on segment " + e.getKey().getIntSegment() + ", in " + e.getValue() + " spreads" );
                }
            }
        }
    }

    public boolean procSpreadLeg( ReusableString msg, Instrument inst, int idx, LegInstrument leg, InstrumentSecurityDefWrapper legInst ) {
        addInstrumentSpreadCount( _instSpreadCount, legInst );
 
        SecurityDefinitionImpl legSecDef = legInst.getSecDef();

        addInstrumentToSegment( _segmentInst, inst.getIntSegment(), legInst );
        addSpreadSegmentSet( _spreadSegmentIdToDepends, inst.getIntSegment(), legInst.getIntSegment() );

        if ( legSecDef == null ) {
            msg.append( " [leg" ).append( idx+1 )
            .append( " id=" ).append( legInst.getLongSymbol() )
            .append( ", side=" ).append( leg.getLegSide().toString() )
            .append( ", legInstLoaded=" ).append( 'N' )
            .append( ", segment=" ).append( legInst.getIntSegment() )
            .append(  "] " );

            return false;
            
        } 
        
        ZString secDesc = legInst.getSecurityDesc();
        
        if ( secDesc.length() == 0 ) secDesc = legSecDef.getSecurityDesc();
   
        msg.append( " [leg" ).append( idx+1 )
           .append( " secDes=" ).append( secDesc )
           .append( ", side=" ).append( leg.getLegSide().toString() )
           .append( ", legInstLoaded=" ).append( 'Y' )
           .append( ", segment=" ).append( legInst.getIntSegment() )
           .append(  "] " );

        return true;
    }

    private int getInstSpreadCount( Instrument legInst ) {
        Integer i = _instSpreadCount.get( legInst );
        
        return (i==null) ? 0 : i.intValue();
    }

    private void createTwoLegStrategy( StrategyManager mgr, Instrument inst, ReusableString msg, StrategyDefinition def, int minOrderQty ) {
        int maxOrderQty = _maxOrderQty;
        if ( minOrderQty > maxOrderQty ) {
            maxOrderQty = minOrderQty;
            msg.append( " minQty=" ).append( inst.getMinQty() + " FORCE INCREASE MAX_QTY TO MATCH " );

            _log.warn( msg );
        } else {
            _log.info( msg );
        }

        String stratId = def.getStrategyId();
        
        Algo<?> algo = getAlgo( mgr, def.getAlgoId(), def.getAlgoClass() );

        if ( mgr.getStrategy( stratId ) != null ) {
            throw new SMTRuntimeException( "StrategyMgrLoader duplicate strategy id " + def.getStrategyId() );
        }
        
        Object[]   argVals    = { stratId };
        Class<?>[] argClasses = { String.class };
        
        Strategy<?> strat = ReflectUtils.create( algo.getStrategyClass(), argClasses, argVals );
        
        strat.setAlgo( algo );

        strat.setOrderRouter( _exchangeRouter );
        strat.setAccount( _account );
        strat.setHubSession( _hubSession );
        strat.setBookLevels( _bookLevels );

        strat.setTrace( _trace );
        strat.setTradingAllowed( _tradingAllowed );
        strat.setMinOrderQty( minOrderQty );
        strat.setMaxOrderQty( maxOrderQty );
        strat.setMaxSlices( _maxSlices );
        
        if ( Utils.hasValue( _pnlCutoffThreshold ) ) strat.setPnlCutoffThreshold( _pnlCutoffThreshold );
        
        // now reflect the values from strategy config
        strat.setStrategyDefinition( def );

        algo.registerStrategy( strat );
        mgr.registerStrategy( strat );
        
        String stratPipe = def.getRequestedPipeLineId();
        
        BaseNonBlockingWorkerMultiplexor m = _pipeToStratMultiplexor.get( stratPipe );
        
        if ( m == null ) {
            throw new SMTRuntimeException( "createStrategy on " + strat.id() + " failed as its requestedPipe " + stratPipe +
                                           " doesnt have a matching multiplexor" );
        }
        
        if ( _throttleRateMsgsPerSecond > 0 ) {
            // the throttler is NOT threadsafe so must be aligned to the multiplexor

            Throttler t = getThrottler( m );
            
            strat.setThrottler( t );
        }

        _log.info( "createStrategy #" + _spreadCnt + "  " + strat.id() + " assigned to pipe " + stratPipe + " against multiplexor " + m.id() );
        
        m.addWorker( strat );
    }

    @SuppressWarnings( "boxing" )
    private void addInstrumentSpreadCount( Map<Instrument, Integer> instSpreadCount, InstrumentSecurityDefWrapper inst ) {
        Integer old = instSpreadCount.get( inst );
        if ( old == null ) {
            instSpreadCount.put( inst, 1 );
        } else {
            instSpreadCount.put( inst, old.intValue() + 1 );
        }
    }

    @SuppressWarnings( "boxing" )
    private void addInstrumentToSegment( Map<Integer, Set<Instrument>> segmentInst, int instSegment, Instrument inst ) {
        Set<Instrument> set = segmentInst.get( instSegment );
        if ( set == null ) {
            set = new HashSet<Instrument>();
            segmentInst.put( instSegment, set );
        }
        set.add( inst );
    }

    @SuppressWarnings( "boxing" )
    private void incApplIdMap( Map<Integer, Integer> applIdToSpreadMap, int intSegment ) {
        Integer old = applIdToSpreadMap.get( intSegment );
        if ( old == null ) {
            applIdToSpreadMap.put( intSegment, 1 );
        } else {
            applIdToSpreadMap.put( intSegment, old.intValue() + 1 );
        }
    }

    @SuppressWarnings( "boxing" )
    private void addSpreadSegmentSet( Map<Integer, Set<Integer>> spreadSegmentIdToDepends, int instSegment, int legSegment ) {
        Set<Integer> set = spreadSegmentIdToDepends.get( instSegment );
        if ( set == null ) {
            set = new HashSet<Integer>();
            spreadSegmentIdToDepends.put( instSegment, set );
        }
        set.add( legSegment );
    }

    private void makeStratMultiplexorMap() {
        if ( _multiplexors.length == 0 ) {
            throw new SMTRuntimeException( "Missing property multiplexors in StrategyMgrLoader" );
        }

        for( SMTComponent c : _multiplexors ) {
            if ( c instanceof BaseNonBlockingWorkerMultiplexor ) {
                BaseNonBlockingWorkerMultiplexor m = (BaseNonBlockingWorkerMultiplexor)c;
                
                List<String> pipes = m.getPipeLineIds();
                
                for( String pipe : pipes ) {
                    BaseNonBlockingWorkerMultiplexor existing = _pipeToStratMultiplexor.get( pipe );
                    
                    if ( existing != null ) {
                        throw new SMTRuntimeException( "StrategyMgrLoader, duplicate pipeId on multiplexor " + c.id() +
                                                       " and " + existing.id() );
                    }
                    
                    _pipeToStratMultiplexor.put( pipe, m );
                }
                
            }  else{
                throw new SMTRuntimeException( c.id() + " - Expected strategy multiplexor to be instanceof BaseNonBlockingWorkerMultiplexor not " + c.getClass().getSimpleName() );
            }
        }
    }

    private Throttler getThrottler( BaseNonBlockingWorkerMultiplexor m ) {
        Throttler t = _throttlers .get( m );
        
        if ( t == null ) {
            t = new ThrottleWithExceptions();
            t.setThrottleNoMsgs( _throttleRateMsgsPerSecond );
            t.setThrottleTimeIntervalMS( 1000 );
            
            _throttlers.put( m, t );
        }
        
        return t;
    }

    private Algo<?> getAlgo( StrategyManager mgr, String algoId, Class<? extends Algo<? extends Book>> algoClass ) {
        Algo<?> algo = mgr.getAlgo( algoId );
        
        if ( algo == null ) {
            algo = createAlgo( algoId, algoClass );
            
            
            mgr.registerAlgo( algo );
        }
        
        return algo;
    }

    private Algo<?> createAlgo( String algoId, Class<? extends Algo<? extends Book>> algoClass ) {
        Object[]   argVals    = { algoId };
        Class<?>[] argClasses = { String.class };
        
        Algo<?> algo = ReflectUtils.create( algoClass, argClasses, argVals );
        
        String stratClassName = _stratClassNames.get( algoId );
        
        if ( stratClassName == null ) {
            throw new SMTRuntimeException( "StrategyMgrLoader algoId " + algoId + " missing strategy className entry in map.stratClass" );
        }
        
        try {
            @SuppressWarnings( "unchecked" )
            Class<? extends Strategy<? extends Book>> stratClass = (Class<? extends Strategy<? extends Book>>) Class.forName( stratClassName );

            _log.info( "Algo " + algo.id() + " setStratClass " + stratClass );
            
            algo.setStrategyClass( stratClass );
            
        } catch( ClassNotFoundException e ) {
            throw new SMTRuntimeException( "StrategyMgrLoader algoId " + algoId + " unable to get class for className " + stratClassName );
        }
        
        return algo;
    }
}
