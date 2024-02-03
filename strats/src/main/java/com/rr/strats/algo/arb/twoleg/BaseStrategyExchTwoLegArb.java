/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.arb.twoleg;

import com.rr.core.algo.strats.BaseL2BookStrategy;
import com.rr.core.algo.strats.StratInstrumentStateWrapper;
import com.rr.core.component.CompRunState;
import com.rr.core.component.SMTContext;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Book;
import com.rr.core.model.BookReserver;
import com.rr.core.model.Instrument;
import com.rr.core.model.LegInstrument;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.strats.algo.twoleg.catchup.CatchUpStates;


public abstract class BaseStrategyExchTwoLegArb extends BaseL2BookStrategy {

    private static final CatchUpStates _catchUpStates = new CatchUpStates();

    private static final int MAX_FLATTEN_ATTEMPT = 3;

    private static final ErrorCode MAX_FLATTEN_ATTEMPT_EXCEEDED = new ErrorCode( "SCA100", "Exceeded maximum attempts to flatten slice, continually fail cross spread" );

    private double  _arbThresh        = 1.0;
    private long    _maxTimeCatchUpMS = 30 * 1000; // 30 seconds max time for Limit order before cancel and flatten out 
    private int     _curSlice         = 0;
    
    private boolean _recalc = false; 
            
    private StratInstrumentStateWrapper<?>  _spreadInst; 

    /**
     * synthetic spread details .. to match details on exchange traded spread
     */
    private StratInstrumentStateWrapper<?>  _legAInst; 
    private StratInstrumentStateWrapper<?>  _legBInst; 

    private DoubleSidedBookEntry _legAEntry;
    private DoubleSidedBookEntry _legBEntry;
    private DoubleSidedBookEntry _spreadEntry;

    private boolean _marketOrderTerminalEvent;
    private boolean _legSliceCompleted;
    
    private MultiLegArbCorrectiveHandler _catchUpState = _catchUpStates.getNone();

    private int _flattenAttempt = 0;



    public BaseStrategyExchTwoLegArb( String id ) {
        super( id );
    }

    @Override
    public void init( SMTContext ctx ) {
        super.init( ctx );
        
        final StratInstrumentStateWrapper<?>[] insts = getInstState();
        
        // exchange spread will be first in list
        _spreadInst = findSpread( insts );

        if ( _spreadInst == null ) throw new SMTRuntimeException( "Missing spread instrument in " + id() );
        
        ZString spreadSecDesc = _spreadInst.getInstrument().getSecurityDesc();

        LegInstrument leg1 = _spreadInst.getInstrument().getLeg( 0 );

        for( int i=0 ; i < insts.length ; i++ ) {
            StratInstrumentStateWrapper<?> stateInstWrap = insts[i];
            Instrument inst = stateInstWrap.getInstrument();
            
            if ( inst == leg1.getInstrument() ) {
                _legAInst = stateInstWrap;
                break;
            }
        }

        if ( _legAInst == null ) {
            throw new SMTRuntimeException( "Missing legA instrument in " + id() + ", spread=" + spreadSecDesc );
        }

        LegInstrument leg2 = _spreadInst.getInstrument().getLeg( 1 );

        for( int i=0 ; i < insts.length ; i++ ) {
            StratInstrumentStateWrapper<?> stateInstWrap = insts[i];
            Instrument inst = stateInstWrap.getInstrument();
            
            if ( inst == leg2.getInstrument() ) {
                _legBInst = stateInstWrap;
                break;
            }
        }

        if ( _legBInst == null ) throw new SMTRuntimeException( "Missing legB instrument in " + id() + ", spread=" + spreadSecDesc );
        
        _legAEntry    = getLegAInst().getBBOSnapEntry();
        _legBEntry    = getLegBInst().getBBOSnapEntry();
        _spreadEntry  = getSpreadInst().getBBOSnapEntry();
    }

    protected final DoubleSidedBookEntry getLegAEntry() {
        return _legAEntry;
    }
    
    protected final DoubleSidedBookEntry getLegBEntry() {
        return _legBEntry;
    }

    protected final DoubleSidedBookEntry getSpreadEntry() {
        return _spreadEntry;
    }

    
    private StratInstrumentStateWrapper<?> findSpread( StratInstrumentStateWrapper<?>[] insts ) {
        for( int i=0 ; i < insts.length ; i++ ) {
            StratInstrumentStateWrapper<?> currWrapper = insts[i];

            int numLegs = currWrapper.getInstrument().getNumLegs();
            
            if ( numLegs > 0 && (numLegs+1) == insts.length ) {
                int matchedLegs = 0;
                
                for( int j=0 ; j < numLegs ; j++ ) {
                    Instrument leg = currWrapper.getInstrument().getLeg( j ).getInstrument();

                    boolean foundLeg = false;
                    
                    for( int k=0 ; k < insts.length ; k++ ) {

                        if ( k == i ) continue;
                        
                        StratInstrumentStateWrapper<?> child = insts[k];
                        
                        if ( child.getInstrument() == leg ) {
                            ++matchedLegs;
                            foundLeg = true;
                            break;
                        }
                    }

                    if ( ! foundLeg ) break;
                }
                
                if ( matchedLegs == numLegs ) return currWrapper;
            }
        }

        throw new SMTRuntimeException( "Couldnt find spread instrument" );
    }

    @Override
    protected final void validateProps() {
        super.validateProps();
        
        if ( _arbThresh < Constants.WEIGHT ) {
            throw new SMTRuntimeException( "StrategyCalendarARB missing arbThresh property id=" + id() );
        }
    }

    @Override
    public final void bookChanged( final StratInstrumentStateWrapper<? extends Book> stratInstState ) {
        
        final DoubleSidedBookEntry entry = stratInstState.getBBOSnapEntry();

        final double lastBestBid = entry.getBidPx();
        final double lastBestAsk = entry.getAskPx();
        
        final Book snappedBook = stratInstState.snap();

        if ( snappedBook.getLevel( 0, entry ) ) {
            final double diffBid = entry.getBidPx() - lastBestBid;
            final double diffAsk = entry.getAskPx() - lastBestAsk;

            // ONLY PROPOGATE CHANGE TO BBO PRICE

            if ( Math.abs( diffBid ) > Constants.TICK_WEIGHT || Math.abs( diffAsk ) > Constants.TICK_WEIGHT ) {
                // bid or ask price has changed 
                
                if ( isActive() ) {      // dont recalc if semi paused or paused
                    _recalc = true;
                    
                    _summaryState.setLastEventInst( stratInstState.getInstIdx() );
                    _summaryState.setLastTickId( snappedBook.getLastTickId() );
                }
            }
        }
    }
    
    @Override
    public final void setMarketOrderTerminalEvent( final StratInstrumentStateWrapper<? extends Book> stratInstState ) {
        _marketOrderTerminalEvent = true;
        _summaryState.setLastEventInst( stratInstState.getInstIdx() );
    }
    
    @Override
    public final void postEventProcessing() {
        if ( _marketOrderTerminalEvent ) {
            
            marketEventPostProcessing(); // submit catchup orders if needed
            
            if ( _legSliceCompleted ) { // check to see if all slices now completed
                checkSliceCompletion();
                
                _legSliceCompleted = false;
            }
            
            _marketOrderTerminalEvent = false;

            setSendStateSnapshotToHub();
            
            // ensure strat inst state snapshot has latest BBO
            _legAInst.updateStratInstStateBBO();
            _legBInst.updateStratInstStateBBO();
            _spreadInst.updateStratInstStateBBO();

            dispatchQueued();
            
        } else if ( checkOverdueLimitOrders() ) { 
            dispatchQueued();
            return;
        }
        
        if ( _recalc ) {
            recalc();
            dispatchQueued();
            _recalc = false;
        }
    }
    
    public final StratInstrumentStateWrapper<?> getSpreadInst() {
        return _spreadInst;
    }
    
    public final StratInstrumentStateWrapper<?> getLegAInst() {
        return _legAInst;
    }
    
    public final StratInstrumentStateWrapper<?> getLegBInst() {
        return _legBInst;
    }

    public final void setCatchUpState( final MultiLegArbCorrectiveHandler handler ) {
        if ( isTrace() ) {
            _logMsg.copy( "Setting catchupState to " ).append( handler.getName() );
        }
        _catchUpState = handler;
    }

    public final CatchUpStates getCatchUpStates() {
        return _catchUpStates;
    }

    public final MultiLegArbCorrectiveHandler getCatchUpState() {
        return _catchUpState;
    }

    @Override
    public final void forceSliceReset() {
        getLogger().warn( "forceSliceReset DANGER : FORCED change catchupState from " + _catchUpState.getName() + " to NONE" );

        _catchUpState = _catchUpStates.getNone();
    }
    
    @Override
    public final void dumpInstDetails( final ReusableString s ) {
        s.append( ", cus=" ).append( _catchUpState.getName() );
        
        _legAInst.dumpInstDetails( s );
        _legBInst.dumpInstDetails( s );
        _spreadInst.dumpInstDetails( s );
    }
    
    public final boolean tryCatchUpIfArbStillExists() {
        if ( mdIsValid() ) {  // prices not crossed

            // TO COMPLICATED TO TRY AND UNWIND PARTIAL SLICE BY SWITCHING FROM BUYING TO SELLING THE SPREAD
            
            if ( _spreadInst.getTargetSliceSide().getIsBuySide() == false ) {
            
                final double spreadSellDiff = getSpreadSellBuySynthDifference();

                if ( spreadSellDiff >= _arbThresh ) {                // spread is more expensive than the synth spread, sell spread, buy synthetic
                    final long time = System.currentTimeMillis();
                    
                    if ( mustThrottle( time ) ) {
                        return false;             // cant do any more work
                    }
                    
                    return sellSpreadLegBuySyntheticSpread( time, true );
                }
                
                return false;
            }

            final double spreadBuyDiff = getSynthSellBuySpreadDiff();

            if ( spreadBuyDiff >= _arbThresh ) {                     // synthetic spread worth more than the spread, so buy spread, sell synthetic 
                final long time = System.currentTimeMillis();
                
                if ( mustThrottle( time ) ) {
                    return false;             // cant do any more work
                }

                return buySpreadLegSellSyntheticSpread( time, true );
            }
        }
        
        return false;
    }

    /**
     * try and fill outstanding qty's by sitting on order book
     */
    public final void sendPassiveLimitOrdersToCatchup() {
        sendLimitCatchUpOrder( _legBInst );
        sendLimitCatchUpOrder( _legAInst );
        sendLimitCatchUpOrder( _spreadInst );
    }

    /**
     * flatten out position on slice
     */
    public final void flattenSlice() {
        _flattenAttempt = 0;
        
        initialFlatten( _legBInst );
        initialFlatten( _legAInst );
        initialFlatten( _spreadInst );
    }

    /**
     * re-flatten out position on slice, another attempt to flatten out remaining, doesnt switch slice side
     */
    public final void reflattenSlice() {
        if ( ++_flattenAttempt < MAX_FLATTEN_ATTEMPT ) {
            reflatten( _legBInst );
            reflatten( _legAInst );
            reflatten( _spreadInst );
        } else {
            getLogger().error( MAX_FLATTEN_ATTEMPT_EXCEEDED, "" );
            
            setCatchUpState( _catchUpStates.getSuspendedCatchup() );
            
            setCompRunState( CompRunState.PassivePause );
        }
    }
    
    @Override
    public final void legSliceCompleted() {
        _legSliceCompleted = true;
    }
    
    public final void setMaxTimeCatchUpMS( int maxValue ) {
        _maxTimeCatchUpMS = maxValue;
    }

    protected abstract double getSynthBuyPx();
    protected abstract double getSynthSellPx();
    
    /**
     * @return the side of legA when selling the synthetic spread
     */
    protected abstract Side   getSellSynthSideLegA(); 
    protected abstract Side   getSellSynthSideLegB();
    
    protected abstract Side   getBuySynthSideLegA();
    protected abstract Side   getBuySynthSideLegB();

    protected abstract double getSynthSpreadVal( double legASliceVal, double legBSliceVal );

    /**
     * sends an order on specified instrument
     * 
     * order is either an opening slice, OR a catchup order
     * 
     * if its an opening slice then sets the slice start time and expected slice qty ... used in catchup logic
     */
    protected final void sendOrder( final StratInstrumentStateWrapper<?>    inst, 
                                    final OrdType                           ordType, 
                                    final Side                              side, 
                                    final double                            price, 
                                          int                               availQty, 
                                    final TimeInForce                       tif, 
                                    final long                              time ) {

        if ( isWorkingSlice() ) {
            availQty = inst.getSliceUnfilledQty();   // try catchup outstanding qty
        } else {
            inst.newSlice( time, availQty, price, side );
        }

        if ( availQty > 0 ) {
            send( inst, ordType, side, price, availQty, tif );
        }
    }

    
    protected final void logRecalcNoAction( final double spreadSellDiff, final double spreadBuyDiff ) {
 
        final double synthBuy  = getSynthBuyPx();
        final double synthSell = getSynthSellPx();
        
        _logMsg.copy( "RECALC has NO action " ).append( id() ).
                append( " [spread " ).append( getSpreadEntry().getBidQty() ).append( " x " ).append( getSpreadEntry().getBidPx() ).
                append( " - " )      .append( getSpreadEntry().getAskPx() ) .append( " x " ).append( getSpreadEntry().getAskQty() ).
                append( ", legA " )  .append( getLegAEntry().getBidQty() ).append( " x " ).append( getLegAEntry().getBidPx() ).
                append( " - " )      .append( getLegAEntry().getAskPx() ).append( " x " ).append( getLegAEntry().getAskQty() ).
                append( ", legB " )  .append( getLegBEntry().getBidQty() ).append( " x " ).append( getLegBEntry().getBidPx() ).
                append( " - " )      .append( getLegBEntry().getAskPx() ).append( " x " ).append( getLegBEntry().getAskQty() ).
                append( "] ").
                append( " CONFLATE [spread=" ).append( getSpreadInst().getConflateSnaps() ).
                append( ", legA=" ).append( getLegAInst().getConflateSnaps() ).
                append( ", legB=" ).append( getLegBInst().getConflateSnaps() ).
                append( "] ").
                append( ", minQty="         ).append( getMinOrderQty() ).
                append( ", synthBuy="       ).append( synthBuy ).
                append( ", spreadSellDiff=" ).append( spreadSellDiff ).
                append( ", synthSell=" ).append( synthSell ).
                append( ", spreadBuyDiff=" ).append( spreadBuyDiff ).
                append( ", arbThresh=" ).append( _arbThresh );
        
        getLogger().info( _logMsg );
    }

    protected final void logRecalcInvalidMD() {
        
        final double synthBuy  = getSynthBuyPx();
        final double synthSell = getSynthSellPx();
        
        _logMsg.copy( "RECALC invalid MD " ).append( id() );
        
        addBookTrace( "[spread", getSpreadInst().getLastSnapshot(), getSpreadEntry() );
        addBookTrace( ", legA",  getLegAInst().getLastSnapshot(),   getLegAEntry() );
        addBookTrace( ", legB",  getLegBInst().getLastSnapshot(),   getLegBEntry() );
        
        _logMsg.append( "] ").
                append( " CONFLATE [spread=" ).append( getSpreadInst().getConflateSnaps() ).
                append( ", legA=" ).append( getLegAInst().getConflateSnaps() ).
                append( ", legB=" ).append( getLegBInst().getConflateSnaps() ).
                append( "] ").
                append( ", synthBuy="       ).append( synthBuy ).
                append( ", synthSell=" ).append( synthSell ).
                append( ", arbThresh=" ).append( _arbThresh );
        
        getLogger().info( _logMsg );
    }

    private void addBookTrace( String desc, Book book, DoubleSidedBookEntry bbo ) {
        _logMsg.append( desc ).append( book.isValidBBO() ? "  " : " !" );
        
        bbo.dump( _logMsg );        
    }

    /**
     * Buy synthetic spread
     *      crosses spread to BUY the synthetic spread (buy leg1, sell leg2)
     *  Sell spread
     *      make order for sell spread at bestBid
     */
    private final boolean sellSpreadLegBuySyntheticSpread( final long time, boolean isCatchup ) {
        
        int orderQty;
        
        double legAPx;
        double legBPx; 

        int legAQty;
        int legBQty;
        
        if (getBuySynthSideLegA().getIsBuySide()) { // cross spread, BUY use ASK, SELL use BID
            legAQty = getLegAEntry().getAskQty();
            legAPx  = getLegAEntry().getAskPx();
        } else {
            legAQty = getLegAEntry().getBidQty(); 
            legAPx  = getLegAEntry().getBidPx();
        }

        if (getBuySynthSideLegB().getIsBuySide()) { // cross spread, BUY use ASK, SELL use BID
            legBQty = getLegBEntry().getAskQty();
            legBPx  = getLegBEntry().getAskPx();
        } else {
            legBQty = getLegBEntry().getBidQty(); 
            legBPx  = getLegBEntry().getBidPx();
        }

        final int    spreadQty = getSpreadEntry().getBidQty(); // SELL SPREAD
        final double spreadPx  = getSpreadEntry().getBidPx();
        
        if ( legAQty - legBQty >= 0 ) { // leg1 qty > leg2 qty
            if ( spreadQty - legAQty >= 0 ) { // spread qty > leg1 qty > leg2 qty
                if ( legBQty >= getMinOrderQty() ) {
                    orderQty = Math.min( legBQty, getMaxOrderQty() );
                    orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                    if ( orderQty > 0 ) {
                        sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(),  legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(),  legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,              spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                        return true;
                    } 
                }
            } else {
                if ( spreadQty - legBQty >= 0 ) { // leg1Qty > spreadQty > leg2Qty
                    if ( legBQty >= getMinOrderQty() ) {
                        orderQty = Math.min( legBQty, getMaxOrderQty() );
                        orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(),  legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,              spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(),  legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                } else { // leg1Qty > leg2Qty > spreadQty 
                    if ( spreadQty >= getMinOrderQty() ) {
                        orderQty = Math.min( spreadQty, getMaxOrderQty() );
                        orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,             spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(), legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(), legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                }
            }
        } else { // leg2 qty > leg1 qty
            if ( spreadQty - legBQty >= 0 ) { // spread qty > leg2 qty > leg1 qty
                if ( legAQty >= getMinOrderQty() ) {
                    orderQty = Math.min( legAQty, getMaxOrderQty() );
                    orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                    if ( orderQty > 0 ) {
                        sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,               spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                        return true;
                    } 
                }
            } else {
                if ( spreadQty - legAQty >= 0 ) { // leg2Qty > spreadQty > leg1Qty
                    if ( legAQty >= getMinOrderQty() ) {
                        orderQty = Math.min( legAQty, getMaxOrderQty() );
                        orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,               spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                } else { // leg2Qty > leg1Qty > spreadQty 
                    if ( spreadQty >= getMinOrderQty() ) {
                        orderQty = Math.min( spreadQty, getMaxOrderQty() );
                        orderQty = reserveQtyForSellSpreadBuySynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Sell,               spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getBuySynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getBuySynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sell synthetic spread
     *      make order for sell leg1 at bestBid
     *      make order for buy  leg2 at bestAsk
     * Buy spread
     *      make order for buy spread at bestAsk
     * @param time 
     *
     *  @return true if submitted orders
     */
    private final boolean buySpreadLegSellSyntheticSpread( final long time, boolean isCatchup ) {
        int orderQty;
        
        double legAPx;
        double legBPx; 

        int legAQty;
        int legBQty;
        
        if (getSellSynthSideLegA().getIsBuySide()) { // cross spread, BUY use ASK, SELL use BID
            legAQty = getLegAEntry().getAskQty();
            legAPx  = getLegAEntry().getAskPx();
        } else {
            legAQty = getLegAEntry().getBidQty(); 
            legAPx  = getLegAEntry().getBidPx();
        }

        if (getSellSynthSideLegB().getIsBuySide()) { // cross spread, BUY use ASK, SELL use BID
            legBQty = getLegBEntry().getAskQty();
            legBPx  = getLegBEntry().getAskPx();
        } else {
            legBQty = getLegBEntry().getBidQty(); 
            legBPx  = getLegBEntry().getBidPx();
        }
        
        final int    spreadQty = getSpreadEntry().getAskQty();
        final double spreadPx  = getSpreadEntry().getAskPx();
        
        if ( legAQty - legBQty >= 0 ) { // leg1 qty > leg2 qty
            if ( spreadQty - legAQty >= 0 ) { // spread qty > leg1 qty > leg2 qty
                if ( legBQty >= getMinOrderQty() ) {
                    orderQty = Math.min( legBQty, getMaxOrderQty() );
                    orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                    if ( orderQty > 0 ) {
                        sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,                 spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                        return true;
                    } 
                }
            } else {
                if ( spreadQty - legBQty >= 0 ) { // leg1Qty > spreadQty > leg2Qty
                    if ( legBQty >= getMinOrderQty() ) {
                        orderQty = Math.min( legBQty, getMaxOrderQty() );
                        orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,                 spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                } else { // leg1Qty > leg2Qty > spreadQty 
                    if ( spreadQty >= getMinOrderQty() ) {
                        orderQty = Math.min( spreadQty, getMaxOrderQty() );
                        orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,                 spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                }
            }
        } else { // leg2 qty > leg1 qty
            if ( spreadQty - legBQty >= 0 ) { // spread qty > leg2 qty > leg1 qty
                if ( legAQty >= getMinOrderQty() ) {
                    orderQty = Math.min( legAQty, getMaxOrderQty() );
                    orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                    if ( orderQty > 0 ) {
                        sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                        sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,   spreadPx,     orderQty, TimeInForce.ImmediateOrCancel, time );
                        return true;
                    } 
                }
            } else {
                if ( spreadQty - legAQty >= 0 ) { // leg2Qty > spreadQty > leg1Qty
                    if ( legAQty >= getMinOrderQty() ) {
                        orderQty = Math.min( legAQty, getMaxOrderQty() );
                        orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,                 spreadPx, orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                } else { // leg2Qty > leg1Qty > spreadQty 
                    if ( spreadQty >= getMinOrderQty() ) {
                        orderQty = Math.min( spreadQty, getMaxOrderQty() );
                        orderQty = reserveQtyForBuySpreadSellSynth( orderQty, isCatchup );
                        if ( orderQty > 0 ) {
                            sendOrder( getSpreadInst(), OrdType.Limit, Side.Buy,   spreadPx,     orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegAInst(),   OrdType.Limit, getSellSynthSideLegA(),   legAPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            sendOrder( getLegBInst(),   OrdType.Limit, getSellSynthSideLegB(),   legBPx,   orderQty, TimeInForce.ImmediateOrCancel, time );
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    
    private void marketEventPostProcessing() {
        
        _catchUpState.checkForAction( this );
        
    }

    private void recalc() {
        
        if ( isWorkingSlice() == false ) {

            // no catch up action required check if should send orders
            
            if ( mdIsValid() ) {  // have BBO's
 
                final double spreadSellDiff = getSpreadSellBuySynthDifference();
                
                if ( spreadSellDiff >= _arbThresh ) { // spread is more expensive than the synth spread, buy spread, sell synth
                    final long time = System.currentTimeMillis();
                    
                    if ( mustThrottle( time ) ) {
                        return;             // cant do any more work
                    }
                    
                    if ( canDoAnotherSlice() ) {
                        if ( sellSpreadLegBuySyntheticSpread( time, false ) ) {
                            setCatchUpState( _catchUpStates.getInitialOpen() );
                        } else if ( isTrace() ) {
                            logMissedSellSpreadOp( spreadSellDiff );
                        }
                    }
                        
                } else {
                    final double spreadBuyDiff = getSynthSellBuySpreadDiff();

                    if ( spreadBuyDiff >= _arbThresh ) { // synthetic spread worth more than the spread, so sell synthetic and buy spread
                        final long time = System.currentTimeMillis();
                        
                        if ( mustThrottle( time ) ) {
                            return;             // cant do any more work
                        }

                        if ( canDoAnotherSlice() ) {
                            if ( buySpreadLegSellSyntheticSpread( time, false ) ) {
                                setCatchUpState( _catchUpStates.getInitialOpen() );
                            } else if ( isTrace() ) {
                                logMissedBuySpreadOp( spreadBuyDiff );
                            }
                        }
                        
                    } else if ( isTrace() ) {
                        logRecalcNoAction( spreadSellDiff, spreadBuyDiff );
                    }
                }
            } else if ( isTrace() ) {
                logRecalcInvalidMD();
            }
        }
    }

    private void logMissedBuySpreadOp( double spreadBuyDiff ) {
        final double synthSell = getSynthSellPx();
        
        _logMsg.copy( "missed Arb buy spread, sell synth op " ).append( id() ).
                append( " [spread " ).append( getSpreadEntry().getBidPx() ).
                append( " - " ).append( getSpreadEntry().getAskPx() ).
                append( ", legA " ).append( getLegAEntry().getBidPx() ).
                append( " - " ).append( getLegAEntry().getAskPx() ).
                append( ", legB " ).append( getLegBEntry().getBidPx() ).
                append( " - " ).append( getLegBEntry().getAskPx() ).
                append( "] ").
                append( " BUY_SPREAD_RESERVED [" ).
                append( "spreadAsk=" ).append( getSpreadInst().getBook().getContext().getAskBookReserver().getReserved() ).
                append( ", legABid=" ).append( getLegAInst().getBook().getContext().getBidBookReserver().getReserved() ).
                append( ", legBAsk=" ).append( getLegBInst().getBook().getContext().getAskBookReserver().getReserved() ).
                append( "] ").
                append( ", synthSell=" ).append( synthSell ).
                append( ", spreadBuyDiff=" ).append( spreadBuyDiff ).
                append( ", arbThresh=" ).append( _arbThresh );
        
        getLogger().info( _logMsg );
    }

    private void logMissedSellSpreadOp( double spreadSellDiff ) {
        final double synthBuy  = getSynthBuyPx();
        
        _logMsg.copy( "missed Arb sell spread, buy synth op " ).append( id() ).
                append( " [spread " ).append( getSpreadEntry().getBidPx() ).
                append( " - " ).append( getSpreadEntry().getAskPx() ).
                append( ", legA " ).append( getLegAEntry().getBidPx() ).
                append( " - " ).append( getLegAEntry().getAskPx() ).
                append( ", legB " ).append( getLegBEntry().getBidPx() ).
                append( " - " ).append( getLegBEntry().getAskPx() ).
                append( "] ").
                append( " SELL_SPREAD_RESERVED [" ).
                append( "spreadBid=" ).append( getSpreadInst().getBook().getContext().getBidBookReserver().getReserved() ).
                append( ", legAAsk=" ).append( getLegAInst().getBook().getContext().getAskBookReserver().getReserved() ).
                append( ", legBBid=" ).append( getLegBInst().getBook().getContext().getBidBookReserver().getReserved() ).
                append( "] ").
                append( ", synthBuy="       ).append( synthBuy ).
                append( ", spreadSellDiff=" ).append( spreadSellDiff ).
                append( ", arbThresh=" ).append( _arbThresh );
        
        getLogger().info( _logMsg );
    }

    private boolean canDoAnotherSlice() {
        if ( _curSlice >= getMaxSlices() ) {
            _logMsg.copy( "StrategyCalArb " ).append( id() ).append( " cannot slice WONT send orders, exceeded limit of " ).append( getMaxSlices() );
            getLogger().info( _logMsg );
            return false;
        }
        
        if ( _summaryState.getPnl() < getPnlCutoffThreshold() ) {
            _logMsg.copy( "StrategyCalArb " ).append( id() ).append( " cannot slice WONT send orders, exceeded limit of " ).append( getMaxSlices() );
            getLogger().info( _logMsg );
            return false;
        }
        
        ++_curSlice;

        _summaryState.setAlgoEventSeqNum( _curSlice );

        return true;
    }

    private boolean isWorkingSlice() {
        return _catchUpState != _catchUpStates.getNone();
    }

    private double getSynthSellBuySpreadDiff() {
        final double synthSellPx    = getSynthSellPx();
        final double spreadBuyPx    = getSpreadEntry().getAskPx();
        final double spreadBuyDiff  = synthSellPx - spreadBuyPx;      
        return spreadBuyDiff;
    }

    private double getSpreadSellBuySynthDifference() {
        final double synthBuyPx     = getSynthBuyPx();
        final double spreadSellPx   = getSpreadEntry().getBidPx();
        final double spreadSellDiff = spreadSellPx - synthBuyPx;
        return spreadSellDiff;
    }

    private void send( final StratInstrumentStateWrapper<?> inst, final OrdType ordType, final Side side, final double price, final int availQty, final TimeInForce tif ) {
        final Book book = inst.getLastSnapshot();
        
        final NewOrderSingle nos = inst.getExchangeHandler().makeNOS( getAccount(),
                                                                      ordType,
                                                                      tif, 
                                                                      availQty, 
                                                                      price, 
                                                                      side, 
                                                                      book.getLastTickInNanos(), 
                                                                      book.getLastTickId() );
   
        inst.updateStratInstState( nos );
        
        enqueueForDownDispatch( nos );
    }

    private boolean mdIsValid() {
        return _legAInst.getLastSnapshot().isValidBBO() && _legBInst.getLastSnapshot().isValidBBO() && _spreadInst.getLastSnapshot().isValidBBO();
    }

    /**
     * @return true if any open orders on market or working out of positions
     */
    private boolean checkOverdueLimitOrders() {
        if ( _catchUpState == _catchUpStates.getLimitOrderCatchup() ) {
            
            final long nowTS = System.currentTimeMillis();
            final long ageSpreadMS = nowTS - _spreadInst.getSliceStartTimeMS();

            if ( ageSpreadMS > _maxTimeCatchUpMS ) {
                setCatchUpState( _catchUpStates.getCancellingOpen() );
                
                _logMsg.copy( "Slice is now " ).append( ageSpreadMS / 1000 ).append( " seconds so cancel before flattening out" );
                
                sendCancel( _legAInst );
                sendCancel( _legBInst );
                sendCancel( _spreadInst );
            }

            return true; 
        }
        
        return false;
    }

    /**
     * on initial flatten, the side must be switched and the new targetQty is the cumQty, cumQty reset to 0
     * 
     * @param inst
     */
    private void initialFlatten( final StratInstrumentStateWrapper<?> inst ) {
        if ( inst.getTargetSliceCumQty() > 0 ) {
            
            final DoubleSidedBookEntry entry = inst.getBBOSnapEntry();

            final double lastBestBid = entry.getBidPx();
            final double lastBestAsk = entry.getAskPx();
            
            final Side   side = (inst.getTargetSliceSide().getIsBuySide()) ? Side.Sell : Side.Buy;  // OPPOSITE SIDE
            final int    qty  = inst.getTargetSliceCumQty();

            final double price = (side.getIsBuySide()) ? lastBestAsk : lastBestBid;                 // cross spread
            
            inst.prepInitialFlatten( qty, price, side ); // reverse side, targetQty=cumQty, cumQty=0
            
            send( inst, OrdType.Limit, side, price, qty, TimeInForce.ImmediateOrCancel );
        } else {
            inst.abortUnfilledSlice(); 
        }
    }

    private void reflatten( final StratInstrumentStateWrapper<?> inst ) {
        if ( inst.getSliceUnfilledQty() > 0 ) {
            final DoubleSidedBookEntry entry = inst.getBBOSnapEntry();

            final double lastBestBid = entry.getBidPx();
            final double lastBestAsk = entry.getAskPx();
            
            final Side   side = inst.getTargetSliceSide();                                          // SAME SIDE as already switched
            final int    qty  = inst.getSliceUnfilledQty();

            final double price = (side.getIsBuySide()) ? lastBestAsk : lastBestBid;                 // cross spread
            
            inst.prepFlatten( qty, price, side ); 
            
            send( inst, OrdType.Limit, side, price, qty, TimeInForce.ImmediateOrCancel );
        }
    }

    private void sendCancel( StratInstrumentStateWrapper<?> inst ) {
        if ( inst.getSliceUnfilledQty() > 0 ) { // still open
            inst.getExchangeHandler().enqueueCancel( inst.getTargetSliceSide(), inst.getLastClOrdId(), null, null );
        }
    }

    private void sendLimitCatchUpOrder( StratInstrumentStateWrapper<?> leg ) {
        sendOrder( leg, OrdType.Limit, leg.getTargetSliceSide(), leg.getTargetSliceOrderPrice(), leg.getSliceUnfilledQty(), TimeInForce.Day, leg.getSliceStartTimeMS() );
    }

    private void checkSliceCompletion() {
        if ( _legBInst.isActiveOnMarket() || _legAInst.isActiveOnMarket() || _spreadInst.isActiveOnMarket() ) {
            return; // at least on slice still open
        }

        final double legASliceVal   = _legAInst.getSliceExecutionValue();
        final double legBSliceVal   = _legBInst.getSliceExecutionValue();

        final double spreadSliceVal = _spreadInst.getSliceExecutionValue();
        
        double pandl = 0;

        /**
         * reserve quantity is held during any flattening
         * event tho reversing position ... dont want unreserve and then maybe reorder same leg in another strat
         */
        unreserveQty( _legAInst );
        unreserveQty( _legBInst );
        unreserveQty( _spreadInst );
        
        if ( _legBInst.isFlattening() || _legAInst.isFlattening() || _spreadInst.isFlattening() ) { 
            // positions flattened, execution value of each leg will hold the loss (or unlikely profit) from getting out of qty previously executed
            
            // when flattening the value of each leg will contain the flattened/net executed value
            // 0 means flattened with no loss
            // +ve means lucky price moved in our favour
            
            // spread    leg    Profit/Loss
            // -10      -2.5    -12.5
            //  10      -2.5      7.5
            //  10       2.5     12.5
            // -10       2.5      7.5
            
            pandl += (legASliceVal + legBSliceVal + spreadSliceVal); 

            _summaryState.setPnl( _summaryState.getPnl() + pandl );

            _logMsg.copy( id() )
                   .append( " slice FAILED and unwound" )
                   .append( ", legAVal=" ).append( legASliceVal )
                   .append( ", legBVal" ).append( legBSliceVal )
                   .append( ", spreadVal" ).append( spreadSliceVal )
                   .append( ", P&L=" ).append( pandl )
                   .append( ", runningP&L" ).append( _summaryState.getPnl() );
            
        } else {                                                        // ALL LEGS FILLED
            
            // when not flattening, the executution value of the leg represents the qty * tradePx .. ie value traded
            // to calc the synth spread trade value you have to subtract one leg from another to get comparable figure to exchange spread
            
            final double synthSpreadVal = getSynthSpreadVal( legASliceVal, legBSliceVal );

            if ( _spreadInst.getTargetSliceSide().getIsBuySide() ) {    // buying spread, selling synth, profit = valueOfSynth - valueOfSpread
                pandl = synthSpreadVal - spreadSliceVal;
            } else {                                                    // selling spread, buying synth, profit = valueOfSpread - valueOfSynth
                pandl = spreadSliceVal - synthSpreadVal;
            }

            _summaryState.setPnl( _summaryState.getPnl() + pandl );
            
            _logMsg.copy( id() )
                   .append( " slice COMPLETED " )
                   .append( ", legAVal=" ).append( legASliceVal )
                   .append( ", legBVal" ).append( legBSliceVal )
                   .append( ", spreadVal" ).append( spreadSliceVal )
                   .append( ", P&L=" ).append( pandl )
                   .append( ", runningP&L" ).append( _summaryState.getPnl() );
       }

        getLogger().info( _logMsg );
    }

    private void unreserveQty( StratInstrumentStateWrapper<?> instw ) {
        if ( instw.getTargetSliceSide().getIsBuySide() ) {
            instw.getBook().getContext().getAskBookReserver().completed( instw.getTargetSliceQty() ); 
        } else {
            instw.getBook().getContext().getBidBookReserver().completed( instw.getTargetSliceQty() ); // crossed spread to try and trade
        }
    }

    private int reserveQtyForSellSpreadBuySynth( int orderQty, boolean isCatchup ) {
        if ( isCatchup ) return orderQty;
        
        // cross spread, BUY use ASK, SELL use BID

        final int          spreadQty = getSpreadEntry().getBidQty(); // SELL SPREAD
        final BookReserver spreadBr  = getSpreadInst().getBook().getContext().getBidBookReserver();

        // crosses spread to BUY the synthetic spread (buy leg1, sell leg2), SELL exchange spread
        // cross spread, BUY use ASK, SELL use BID

        return doReserve( orderQty, spreadQty, spreadBr );
    }

    private int reserveQtyForBuySpreadSellSynth( final int orderQty, boolean isCatchup ) {
        if ( isCatchup ) return orderQty;

        // cross spread, BUY use ASK, SELL use BID

        final int          spreadQty = getSpreadEntry().getAskQty(); // BUY SPREAD
        final BookReserver spreadBr  = getSpreadInst().getBook().getContext().getAskBookReserver();

        // crosses spread to BUY the synthetic spread (buy leg1, sell leg2), SELL exchange spread
        // cross spread, BUY use ASK, SELL use BID

        return doReserve( orderQty, spreadQty, spreadBr );
    }

    private int doReserve( int orderQty, final int spreadQty, final BookReserver spreadBr ) {

        int legAQty;
        int legBQty;
        
        BookReserver legABr   = getLegAInst().getBook().getContext().getBidBookReserver();
        BookReserver legBBr   = getLegBInst().getBook().getContext().getAskBookReserver();
        
        if (getBuySynthSideLegA().getIsBuySide()) { 
            legAQty = getLegAEntry().getAskQty();
            legABr  = getLegAInst().getBook().getContext().getAskBookReserver();
        } else {
            legAQty = getLegAEntry().getBidQty(); 
            legABr  = getLegAInst().getBook().getContext().getBidBookReserver();
        }

        if (getBuySynthSideLegB().getIsBuySide()) { // cross spread, BUY use ASK, SELL use BID
            legBQty = getLegBEntry().getAskQty();
            legBBr  = getLegBInst().getBook().getContext().getAskBookReserver();
        } else {
            legBQty = getLegBEntry().getBidQty(); 
            legBBr  = getLegBInst().getBook().getContext().getBidBookReserver();
        }

        final int spreadReserve = spreadBr.grabQty( orderQty, spreadQty, getSpreadInst().getBook().getLastTickInNanos() );
        if ( spreadReserve == 0 ) return 0;
        if ( spreadReserve < orderQty ) orderQty = spreadQty;
       
        final int legAReserve = legABr.grabQty( orderQty, legAQty, getLegAInst().getBook().getLastTickInNanos() );
        if ( legAReserve == 0 ) {
            spreadBr.completed( orderQty ); // return reserve
            return 0;
        }
       
        if ( legAReserve < orderQty ) {
            spreadBr.completed( orderQty - legAReserve ); // return UNUSED reserve
            orderQty = legAReserve;
        }

        final int legBReserve = legBBr.grabQty( orderQty, legBQty, getLegBInst().getBook().getLastTickInNanos() );
        if ( legBReserve == 0 ) {
            spreadBr.completed( orderQty ); // return reserve
            legABr.completed( orderQty );   // return reserve
            return 0;
        }

        if ( legBReserve < orderQty ) {
            spreadBr.completed( orderQty - legBReserve ); // return UNUSED reserve
            legABr.completed( orderQty - legBReserve ); // return UNUSED reserve
            orderQty = legBReserve;
        }
       
        return orderQty;
    }
}
