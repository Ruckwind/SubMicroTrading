/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.utils;

import com.rr.core.lang.ReusableType;
import com.rr.model.generated.internal.core.FullEventIds;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.internal.type.ExecType;

public class FixUtils {

    public static boolean overrideOrdStatus( CommonExecRpt msg, final ReusableType type, OrdStatus forceStatus ) {
        boolean openOnMkt = false;

        switch( type.getId() ) {
        case FullEventIds.ID_MARKET_NEWORDERACK:
            ((MarketNewOrderAckWrite) msg).setOrdStatus( forceStatus );
            openOnMkt = true;
            break;
        case FullEventIds.ID_MARKET_TRADENEW:
            ((MarketTradeNewWrite) msg).setOrdStatus( forceStatus );
            if ( msg.getExecType() != ExecType.Fill ) {
                openOnMkt = true;
            }
            break;
        case FullEventIds.ID_MARKET_CANCELREJECT:
            ((MarketCancelRejectWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_REJECTED:
            ((MarketRejectedWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_CANCELLED:
            ((MarketCancelledWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_REPLACED:
            ((MarketReplacedWrite) msg).setOrdStatus( forceStatus );
            openOnMkt = true;
            break;
        case FullEventIds.ID_MARKET_DONEFORDAY:
            ((MarketDoneForDayWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_STOPPED:
            ((MarketStoppedWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_EXPIRED:
            ((MarketExpiredWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_SUSPENDED:
            ((MarketSuspendedWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_RESTATED:
            ((MarketRestatedWrite) msg).setOrdStatus( forceStatus );
            openOnMkt = true;
            break;
        case FullEventIds.ID_MARKET_TRADECORRECT:
            ((MarketTradeCorrectWrite) msg).setOrdStatus( forceStatus );
            openOnMkt = true;
            break;
        case FullEventIds.ID_MARKET_TRADECANCEL:
            ((MarketTradeCancelWrite) msg).setOrdStatus( forceStatus );
            openOnMkt = true;
            break;
        case FullEventIds.ID_MARKET_ORDERSTATUS:
            ((MarketOrderStatusWrite) msg).setOrdStatus( forceStatus );
            break;
        case FullEventIds.ID_MARKET_CANCELREQUEST:
        case FullEventIds.ID_MARKET_CANCELREPLACEREQUEST:
        case FullEventIds.ID_MARKET_NEWORDERSINGLE:
            break;                         // not actually possible, here to allow use of tableswitch     
        }

        return openOnMkt;
    }

    public static String chkDelim( String rawMessage ) {

        rawMessage = ensureUsingFixDelim( rawMessage );

        if ( rawMessage.charAt( rawMessage.length() - 1 ) != '\001' ) {
            rawMessage = rawMessage + '\001';
        }

        return rawMessage;
    }

    public static String ensureUsingFixDelim( String rawMessage ) {
        rawMessage = rawMessage.trim();

        if ( rawMessage.contains( "\001" ) ) {
            return rawMessage;
        }

        char last = rawMessage.charAt( rawMessage.length() - 1 );

        if ( last == '|' ) {
            return rawMessage.replace( '|', '\001' );
        }

        if ( last == ';' ) {
            return rawMessage.replace( ';', '\001' );
        }

        if ( rawMessage.contains( "|" ) ) {
            return rawMessage.replace( '|', '\001' );
        }

        if ( rawMessage.contains( ";" ) ) {
            return rawMessage.replace( ';', '\001' );
        }

        return rawMessage;
    }
}
