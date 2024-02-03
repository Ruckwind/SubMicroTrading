/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.client;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.EventHandler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.session.RecoverableSession;
import com.rr.model.generated.internal.events.factory.ClientAlertLimitBreachFactory;
import com.rr.model.generated.internal.events.impl.ClientAlertLimitBreachImpl;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.Strings;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.processor.states.StateException;
import com.rr.om.processor.states.ValidationStateException;

/**
 * @author Richard Rose
 * @WARNING be very careful, the order request ccy and price are in the CLIENT ccy
 * they can be used in same calc with usd conversion factor BUT nowhere else as
 * everything else in trading ccy
 */
public class OMClientProfileImpl implements OMClientProfile {

    private static final Logger _log = LoggerFactory.create( OMClientProfileImpl.class );

    private static final ZString LOW_THRESHOLD_BREACH   = new ViewString( "Low total order value limit breached " );
    private static final ZString MEDIUM_THRESHOLD_ALERT = new ViewString( "Medium total order value limit breached " );
    private static final ZString HIGH_THRESHOLD_ALERT   = new ViewString( "High total order value limit breached " );
    private static final ZString SESSION                = new ViewString( ", session=" );
    private static final ZString CLIENT                 = new ViewString( ", client=" );
    private static final ZString ORD_VAL_USD            = new ViewString( "orderValueUSD=" );
    private static final ZString PREV_VAL_USD           = new ViewString( "prevTotalValueUSD=" );
    private static final ZString NEW_VAL_USD            = new ViewString( "newTotalValueUSD=" );
    private static final ZString LIMIT_USD              = new ViewString( "thresholdUSD=" );

    private static final ZString MAX_SINGLE_ORDER_VAL_EXCEEDED = new ViewString( "Maximum single order value exceeded " );
    private static final ZString MAX_SINGLE_ORDER_QTY_EXCEEDED = new ViewString( "Maximum single order quantity exceeded " );
    private static final ZString MAX_TOTAL_ORDER_VAL_EXCEEDED  = new ViewString( "Maximum total order value exceeded " );
    private static final ZString MAX_TOTAL_ORDER_QTY_EXCEEDED  = new ViewString( "Maximum total order quantity exceeded " );
    private static ClientAlertLimitBreachFactory _alertFactory = SuperpoolManager.instance().getFactory( ClientAlertLimitBreachFactory.class,
                                                                                                         ClientAlertLimitBreachImpl.class );
    private final String _client;
    private final String _id;
    private double _maxSingleOrderValueUSD = ClientProfile.DEFAULT_MAX_SINGLE_VAL;
    private double _maxSingleOrderQty      = ClientProfile.DEFAULT_MAX_SINGLE_QTY;
    private double _maxTotalQty            = ClientProfile.DEFAULT_MAX_TOTAL_QTY;
    private double _maxTotalOrderValueUSD  = ClientProfile.DEFAULT_MAX_TOTAL_VAL;
    private double _totalQty;
    private double _totalValueUSD;
    private int _lowThresholdPercent  = ClientProfile.DEFAULT_LOW_THRESHOLD;
    private int _medThresholdPercent  = ClientProfile.DEFAULT_MED_THRESHOLD;
    private int _highThresholdPercent = ClientProfile.DEFAULT_HIGH_THRESHOLD;
    private double _lowThresholdUSD;
    private double _medThresholdUSD;
    private double _highThresholdUSD;
    private boolean _sendClientLateFills = true;

    public OMClientProfileImpl( String componentId, ZString clientName ) {
        _client = clientName.toString();
        _id     = componentId;
        recalcThreshold();
    }

    public OMClientProfileImpl( ZString client,
                                double maxSingleOrderValueUSD,
                                int maxSingleOrderQty,
                                long maxTotalQty,
                                double maxTotalValueUSD,
                                int lowThresholdPercent,
                                int medThresholdPercent,
                                int highThresholdPercent ) {

        _id                     = null;
        _client                 = client.toString();
        _maxSingleOrderValueUSD = maxSingleOrderValueUSD;
        _maxSingleOrderQty      = maxSingleOrderQty;
        _maxTotalQty            = maxTotalQty;

        _maxTotalOrderValueUSD = maxTotalValueUSD;

        setThresholds( lowThresholdPercent, medThresholdPercent, highThresholdPercent );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public double getTotalOrderQty() {
        return _totalQty;
    }

    @Override
    public double getTotalOrderValueUSD() {
        return _totalValueUSD;
    }

    @Override
    public double getTotalQty() {
        return _totalQty;
    }

    @Override
    public double getMaxSingleOrderValueUSD() {
        return _maxSingleOrderValueUSD;
    }

    @Override
    public void setMaxSingleOrderValueUSD( double maxSingleOrderValueUSD ) {
        _maxSingleOrderValueUSD = (maxSingleOrderValueUSD <= Constants.TICK_WEIGHT) ? Double.MAX_VALUE : maxSingleOrderValueUSD;
    }

    @Override
    public double getMaxSingleOrderQty() {
        return _maxSingleOrderQty;
    }

    @Override
    public double getMaxTotalOrderValueUSD() {
        return _maxTotalOrderValueUSD;
    }

    @Override
    public void setMaxTotalOrderValueUSD( double maxTotalValueUSD ) {
        _maxTotalOrderValueUSD = (maxTotalValueUSD <= 0) ? Long.MAX_VALUE : maxTotalValueUSD;

        recalcThreshold();
    }

    @Override
    public double getMaxTotalQty() {
        return _maxTotalQty;
    }

    @Override
    public void setMaxTotalQty( double maxTotalQty ) {
        _maxTotalQty = (maxTotalQty <= Constants.TICK_WEIGHT) ? Double.MAX_VALUE : maxTotalQty;
    }

    @Override
    public void setThresholds( int lowThresholdPercent, int medThresholdPercent, int highThresholdPercent ) {

        _lowThresholdPercent  = lowThresholdPercent;
        _medThresholdPercent  = medThresholdPercent;
        _highThresholdPercent = highThresholdPercent;

        recalcThreshold();
    }

    @Override
    public void setMaxSingleOrderQty( double maxSingleOrderQty ) {
        _maxSingleOrderQty = (maxSingleOrderQty <= Constants.TICK_WEIGHT) ? Double.MAX_VALUE : maxSingleOrderQty;
    }

    @Override
    public String id() { return _client; }

    @Override
    public Alert handleNOSGetAlerts( Order order, NewOrderSingle msg ) throws StateException {

        final double qty      = msg.getOrderQty();
        final double valueUSD = msg.getPrice() * qty * msg.getCurrency().toUSDFactor();

        ClientAlertLimitBreachImpl alert = null;

        if ( valueUSD > _maxSingleOrderValueUSD ) {
            throwValidationStateException( MAX_SINGLE_ORDER_VAL_EXCEEDED, (long) valueUSD, (long) _maxSingleOrderValueUSD, msg.getClOrdId() );
        }

        if ( qty > _maxSingleOrderQty ) {
            throwValidationStateException( MAX_SINGLE_ORDER_QTY_EXCEEDED, qty, _maxSingleOrderQty, msg.getClOrdId() );
        }

        final double newTotVal = _totalValueUSD + valueUSD;
        final double newQty    = _totalQty + qty;

        if ( Double.compare( newQty, _maxTotalQty ) < 0 ) {
            if ( newTotVal < _lowThresholdUSD ) {
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            } else if ( newTotVal < _medThresholdUSD ) {
                if ( _totalValueUSD < _lowThresholdUSD ) {
                    // dot spam alerts only alert when cross threshold
                    alert = makeAlert( LOW_THRESHOLD_BREACH, alert, msg, valueUSD, _totalValueUSD, newTotVal, _lowThresholdUSD, msg );
                }
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;

            } else if ( newTotVal < _highThresholdUSD ) {
                if ( _totalValueUSD < _medThresholdUSD ) {
                    // dot spam alerts only alert when cross threshold
                    alert = makeAlert( MEDIUM_THRESHOLD_ALERT, alert, msg, valueUSD, _totalValueUSD, newTotVal, _medThresholdUSD, msg );
                }
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            } else {
                if ( newTotVal > _maxTotalOrderValueUSD ) {
                    throwValidationStateException( MAX_TOTAL_ORDER_VAL_EXCEEDED, (long) newTotVal, (long) _maxTotalOrderValueUSD, msg.getClOrdId() );
                }
                if ( _totalValueUSD < _highThresholdUSD ) {
                    alert = makeAlert( HIGH_THRESHOLD_ALERT, alert, msg, valueUSD, _totalValueUSD, newTotVal, _highThresholdUSD, msg );
                }
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            }
        } else {
            throwValidationStateException( MAX_TOTAL_ORDER_QTY_EXCEEDED, newQty, _maxTotalQty, msg.getClOrdId() );
        }

        return alert;
    }

    @Override
    public Alert handleAmendGetAlerts( Order order, CancelReplaceRequest msg ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSendClientLateFills() {
        return _sendClientLateFills;
    }

    @Override
    public boolean setSendClientLateFills( boolean sendClientLateFills ) {
        boolean oldVal = _sendClientLateFills;

        _sendClientLateFills = sendClientLateFills;

        return oldVal;
    }

    @Override
    public void handleNewOrderSingle( Order order, NewOrderSingle msg ) {
        throw new RuntimeException( "Use handleNOSGetAlerts as handleNewOrderSingle not appropriate" );
    }

    @Override
    public void handleNewOrderAck( Order order, NewOrderAck msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleTradeNew( Order order, TradeNew msg ) {

        final OrderVersion lastAcked = order.getLastAckedVerion();
        final OrderRequest req       = (OrderRequest) lastAcked.getBaseOrderRequest();

        if ( lastAcked.getOrdStatus().getIsTerminal() ) {

            // late fill, apply fully to limits

            final double lastQty  = msg.getLastQty();
            final double valueUSD = msg.getLastPx() * lastQty * req.getInstrument().getCurrency().toUSDFactor();

            _totalQty += lastQty;
            _totalValueUSD += valueUSD;

        } else {
            final double adjustAmt = msg.getLastPx() - lastAcked.getMarketPrice();
            final double valueUSD  = adjustAmt * msg.getLastQty() * req.getInstrument().getCurrency().toUSDFactor();
            final double newTotVal = _totalValueUSD + valueUSD;

            if ( newTotVal >= 0 ) {
                _totalValueUSD = newTotVal;
            } else {
                _totalValueUSD = 0;
            }
        }
    }

    @Override
    public void handleCancelReplaceRequest( Order order, CancelReplaceRequest msg ) {
        throw new RuntimeException( "Use handleAmendGetAlerts as handleCancelReplaceRequest not appropriate" );
    }

    @Override
    public void handleCancelRequest( Order order, CancelRequest msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleCancelReject( Order order, CancelReject mktReject ) {

        final OrdStatus exchangeOrdStatus = mktReject.getOrdStatus();

        if ( exchangeOrdStatus.getIsTerminal() ) {
            if ( order.getPendingVersion().getOrdStatus() == OrdStatus.PendingCancel ) {
                handleCancelReject( order );
            }
        }
    }

    @Override
    public void handleVagueReject( Order order, VagueOrderReject msg ) throws StateException {

        final OrderVersion pending = order.getPendingVersion();

        if ( pending.getOrdStatus() == OrdStatus.PendingCancel || pending.getOrdStatus() == OrdStatus.PendingReplace ) {
            if ( msg.getIsTerminal() ) {
                handleCancelReject( order );
            }
        } else if ( pending.getOrdStatus() == OrdStatus.PendingNew ) {
            handleRejected( order, null );
        }
    }

    @Override
    public void handleRejected( Order order, Rejected msg ) {

        final OrderVersion lastAcked = order.getLastAckedVerion();
        final OrderRequest req       = (OrderRequest) lastAcked.getBaseOrderRequest();

        final double cancelQty = req.getOrderQty() - lastAcked.getCumQty();

        if ( cancelQty > Constants.TICK_WEIGHT ) {
            final double valueUSD = req.getPrice() * cancelQty * req.getCurrency().toUSDFactor();

            final double newTotVal = _totalValueUSD - valueUSD;
            final double newQty    = _totalQty - cancelQty;

            if ( newQty >= Constants.TICK_WEIGHT && newTotVal >= Constants.TICK_WEIGHT ) {
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            } else {
                _totalQty      = 0;
                _totalValueUSD = 0;
            }
        }
    }

    @Override
    public void handleCancelled( Order order, Cancelled msg ) {
        orderTerminated( order );
    }

    /**
     * eg QTY UP / px up
     * <p>
     * NOS  qty=100, px=15    risk=1500
     * FILL qty=15,  px=14    risk=1485
     * REP  qty=135, px=16    risk change = ((135-100)-15) * (16-15) = 20
     * <p>
     * eg QTY UP / px down
     * <p>
     * NOS  qty=100, px=15    risk=1500
     * FILL qty=15,  px=14    risk=1485
     * REP  qty=135, px=13    risk change = ((135-100)-15) * (13-15) = -40
     * <p>
     * eg QTY DOWN / px up
     * <p>
     * NOS  qty=100, px=15    risk=1500
     * FILL qty=15,  px=14    risk=1485
     * REP  qty=70,  px=17    risk change = ((70-15) * (17-15) + (70-100) * 15 = 110 - 450 = -340     (tot = 1145)
     * <p>
     * eg QTY DOWN / px down
     * <p>
     * NOS  qty=100, px=15    risk=1500
     * FILL qty=15,  px=14    risk=1485
     * REP  qty=70,  px=13    risk change = ((70-15) * (13-15) + (70-100) * 15 = -110 - 450 = -560
     */
    @Override
    public void handleReplaced( Order order, Replaced rep ) {
        final OrderVersion lastAcked   = order.getLastAckedVerion();
        final OrderVersion replacedVer = order.getPendingVersion();
        final OrderRequest origReq     = (OrderRequest) lastAcked.getBaseOrderRequest();
        final OrderRequest replaceReq  = (OrderRequest) replacedVer.getBaseOrderRequest();

        // dont use the market side fields which were NOT decoded as on client ack they 
        // are delegated to src request

        final double repQty    = replacedVer.getOrderQty();
        final double origQty   = origReq.getOrderQty();
        final double repPx     = replacedVer.getMarketPrice();
        final double origPx    = lastAcked.getMarketPrice();
        final double cumQty    = lastAcked.getCumQty();
        final double qtyChange = repQty - origQty;

        double adjustAmt;

        if ( qtyChange >= Constants.TICK_WEIGHT ) {                                      // amend qty UP
            adjustAmt = ((qtyChange - cumQty) * (repPx - origPx));
        } else if ( qtyChange <= -Constants.TICK_WEIGHT ) {                                                    // amend qty DOWN
            adjustAmt = ((repQty - cumQty) * (repPx - origPx)) + (qtyChange * origPx);
        } else {
            adjustAmt = ((repQty - cumQty) * (repPx - origPx));
        }

        final double valueUSD = adjustAmt * replaceReq.getInstrument().getCurrency().toUSDFactor();

        final double newTotVal = _totalValueUSD + valueUSD;
        final double newQty    = _totalQty + qtyChange;

        if ( newQty >= 0 && newTotVal >= 0 ) {
            _totalValueUSD = newTotVal;
            _totalQty      = newQty;
        } else {
            _totalQty      = 0;
            _totalValueUSD = 0;
        }
    }

    @Override
    public void handleDoneForDay( Order order, DoneForDay msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopped( Order order, Stopped msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleExpired( Order order, Expired msg ) {
        orderTerminated( order );
    }

    @Override
    public void handleSuspended( Order order, Suspended msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleRestated( Order order, Restated msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleTradeCorrect( Order order, TradeCorrect msg ) {
        final OrderVersion lastAcc   = order.getLastAckedVerion();
        final double       usdFactor = lastAcc.getBaseOrderRequest().getInstrument().getCurrency().toUSDFactor();
        final OrdStatus    ordStatus = order.getLastAckedVerion().getOrdStatus();

        final double srcPx = lastAcc.getMarketPrice();

        final double repQty    = msg.getLastQty();
        final double origQty   = msg.getOrigQty();
        final double repPx     = msg.getLastPx();
        final double origPx    = msg.getOrigPx();
        final double qtyChange = repQty - origQty;

        double adjustAmt = 0;
        double valueUSD;

        if ( ordStatus.getIsTerminal() && ordStatus != OrdStatus.Filled ) {
            // order is terminal so the open aspect of order already removed from totals

            // 10 * 24 => 5 * 22
            // should be 90 * 25.12 + 5 * 22

            // oldTotal = 90 * 25.12 + 10 * 24
            // newTotal = oldTotal + 5 * 22 + 10 * 24

            adjustAmt = (repQty * repPx) - (origQty * origPx);

            _totalQty += qtyChange;

            if ( _totalQty < Constants.TICK_WEIGHT ) _totalQty = 0;

        } else { // any trade down qty moves to open

            int cmp = Double.compare( repQty, origQty );

            if ( cmp >= 0 ) {
                // eg srcPx=25.12, origTrade=10*24.0, corr=15*22.0
                // orig=240, new=330, but the qtyChange aleady included in total with srcPx
                // orig trade total       = 25.12 * 90 + 10 * 24 = 2260.8 + 240 = 2500.8
                // new total should equal = 25.12 * 85 + 15 * 22 = 2135.2 + 330 = 2465.2

                // adjust = -35.6

                // 10 * (22-24)       = -20     against alreaady traded component
                // - (5 * (25.12-22)) = -15.6   adjust extra qty against src px

                adjustAmt = (origQty * (repPx - origPx));
                adjustAmt -= qtyChange * (srcPx - repPx);

            } else if ( cmp < 0 ) {
                // eg srcPx=25.12, origTrade=10*24.0, corr=8*26.0
                // orig=240, new=108, but the qtyChange aleady included in total with srcPx
                // orig trade total       = 25.12 * 90 + 10 * 24 = 2260.8  + 240 = 2500.8
                // new total should equal = 25.12 * 92 + 8  * 26 = 2311.04 + 208 = 2519.04

                // adjust = 18.24

                // 8 * (26-24)        = 16      against alreaady traded component
                // (-2 * (25.12-24.0)) = 2.24   adjust extra qty against src px

                adjustAmt = origQty * (srcPx - origPx); // move trade fully back into open
                adjustAmt -= (repQty * (srcPx - repPx));         // now adjust down
            }
        }

        valueUSD = adjustAmt * usdFactor;

        final double newTotVal = _totalValueUSD + valueUSD;

        if ( newTotVal >= Constants.TICK_WEIGHT ) {
            _totalValueUSD = newTotVal;
        } else {
            _totalValueUSD = 0;
            _totalQty      = 0;
        }
    }

    /**
     * cancel a trade
     */
    @Override
    public void handleTradeCancel( Order order, TradeCancel msg ) {

        final OrderVersion lastAcc   = order.getLastAckedVerion();
        final double       usdFactor = lastAcc.getBaseOrderRequest().getInstrument().getCurrency().toUSDFactor();
        final OrdStatus    ordStatus = lastAcc.getOrdStatus();

        final double repQty = msg.getLastQty();
        final double origPx = msg.getLastPx();

        double adjustAmt;
        double valueUSD;

        if ( ordStatus.getIsTerminal() && ordStatus != OrdStatus.Filled ) { // cancel amount wont be retraded

            adjustAmt = -repQty * origPx;
            _totalQty -= repQty;

            if ( _totalQty < Constants.TICK_WEIGHT ) _totalQty = 0;

        } else { // cancel amount is now open

            final double repPx = lastAcc.getMarketPrice();

            adjustAmt = (repQty * (repPx - origPx));
        }

        valueUSD = adjustAmt * usdFactor;

        final double newTotVal = _totalValueUSD + valueUSD;

        if ( newTotVal >= 0 ) {
            _totalValueUSD = newTotVal;
        } else {
            _totalValueUSD = 0;
        }
    }

    @Override
    public void handleOrderStatus( Order order, OrderStatus msg ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postConstruction() {
        recalcThreshold();
    }

    private void handleCancelReject( Order order ) {

        // cancel failed BUT the exchange believes the order is terminal
        // note order is in pending state ... generally would expect exchange to send some unsol cancel
        // message and already be in terminal state

        final OrderVersion lastAcked = order.getLastAckedVerion();
        final OrderRequest req       = (OrderRequest) lastAcked.getBaseOrderRequest();

        final double cancelQty = req.getOrderQty() - lastAcked.getCumQty();

        if ( cancelQty > Constants.TICK_WEIGHT ) {
            final double valueUSD = req.getPrice() * cancelQty * req.getCurrency().toUSDFactor();

            final double newTotVal = _totalValueUSD - valueUSD;
            final double newQty    = _totalQty - cancelQty;

            if ( newQty >= Constants.TICK_WEIGHT && newTotVal >= Constants.TICK_WEIGHT ) {
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            } else {
                _totalQty      = 0;
                _totalValueUSD = 0;
            }
        }
    }

    private ClientAlertLimitBreachImpl makeAlert( ZString breachMsg,
                                                  ClientAlertLimitBreachImpl alert,
                                                  OrderRequest req,
                                                  double ordValUSD,
                                                  double prevTotUSD,
                                                  double newTotUSD,
                                                  double limit,
                                                  OrderRequest srcEvent ) {

        ClientAlertLimitBreachImpl newAlert = _alertFactory.get();

        newAlert.setSrcEvent( srcEvent );

        String sessName = "unknown";

        EventHandler srcHandler = srcEvent.getEventHandler();

        if ( srcHandler != null ) {
            if ( srcHandler instanceof RecoverableSession ) {
                sessName = srcHandler.getComponentId();
            }
        }

        ReusableString text = newAlert.getTextForUpdate();
        text.setValue( breachMsg );
        text.append( CLIENT ).append( _client ).append( Strings.DELIM );
        text.append( SESSION ).append( sessName ).append( Strings.DELIM );
        text.append( ORD_VAL_USD ).append( ordValUSD ).append( Strings.DELIM );
        text.append( PREV_VAL_USD ).append( prevTotUSD ).append( Strings.DELIM );
        text.append( NEW_VAL_USD ).append( newTotUSD ).append( Strings.DELIM );
        text.append( LIMIT_USD ).append( limit );
        newAlert.attachQueue( alert );

        return newAlert;
    }

    private void orderTerminated( Order order ) {
        final OrderVersion lastAcked = order.getLastAckedVerion();
        final OrderRequest req       = (OrderRequest) lastAcked.getBaseOrderRequest();

        final double cancelQty = req.getOrderQty() - lastAcked.getCumQty();

        if ( cancelQty > Constants.TICK_WEIGHT ) {
            final double valueUSD = req.getPrice() * cancelQty * req.getCurrency().toUSDFactor();

            final double newTotVal = _totalValueUSD - valueUSD;
            final double newQty    = _totalQty - cancelQty;

            if ( newQty >= Constants.TICK_WEIGHT && newTotVal >= Constants.TICK_WEIGHT ) {
                _totalValueUSD = newTotVal;
                _totalQty      = newQty;
            } else {
                _totalQty      = 0;
                _totalValueUSD = 0;
            }
        }
    }

    private void recalcThreshold() {
        if ( _maxTotalOrderValueUSD == Long.MAX_VALUE ) {
            _log.info( "Client " + _client + " totOrdValue UNLIMITED" );

            _lowThresholdUSD  = Double.MAX_VALUE;
            _medThresholdUSD  = Double.MAX_VALUE;
            _highThresholdUSD = Double.MAX_VALUE;

        } else {
            _lowThresholdUSD  = (_maxTotalOrderValueUSD * _lowThresholdPercent) / 100;
            _medThresholdUSD  = (_maxTotalOrderValueUSD * _medThresholdPercent) / 100;
            _highThresholdUSD = (_maxTotalOrderValueUSD * _highThresholdPercent) / 100;

            _log.info( "Client " + _client + " totOrdValue thresholds Low=" + _lowThresholdUSD + " (" + _lowThresholdPercent + ")" +
                       ", med=" + _medThresholdUSD + " (" + _medThresholdPercent + ")" +
                       ", high=" + _highThresholdUSD + " (" + _highThresholdPercent + ")" +
                       ", max=" + _maxTotalOrderValueUSD );
        }
    }

    private void throwValidationStateException( ZString baseMsg, double val, double max, ZString clOrdId ) throws ValidationStateException {

        ReusableString msg = TLC.instance().pop();

        msg.setValue( baseMsg );
        msg.append( Strings.DELIM ).append( Strings.CL_ORD_ID ).append( clOrdId );
        msg.append( Strings.DELIM ).append( Strings.VAL ).append( val );
        msg.append( Strings.DELIM ).append( Strings.MAX ).append( max );

        ValidationStateException e = new ValidationStateException( msg );
        TLC.instance().pushback( msg );

        throw e;
    }
}
