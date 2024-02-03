/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.om.emea.exchange.millenium.recovery.MilleniumRecoveryController;

class MilleniumCommonSessionUtils {

    static boolean isSessionMessage( Event msg ) {
        final int subId = msg.getReusableType().getSubId();

        switch( subId ) {
        case EventIds.ID_MILLENIUMLOGON:
        case EventIds.ID_MILLENIUMLOGONREPLY:
        case EventIds.ID_MILLENIUMLOGOUT:
        case EventIds.ID_MILLENIUMMISSEDMESSAGEREQUEST:
        case EventIds.ID_MILLENIUMMISSEDMSGREQUESTACK:
        case EventIds.ID_MILLENIUMMISSEDMSGREPORT:
            return true;
        default:
            switch( subId ) {
            case EventIds.ID_HEARTBEAT:
            case EventIds.ID_TESTREQUEST:
                return true;
            }
        }
        return false;
    }

    static Event recoveryDecode( MilleniumController ctl, MilleniumLSEDecoder decoder, byte[] buf, int offset, int len, boolean inBound ) {

        decoder.parseHeader( buf, offset, len );
        Event msg = decoder.postHeaderDecode();

        ctl.recoverContext( msg, inBound, decoder.getAppId() );

        return msg;
    }

    public static MilleniumController createSessionController( SeqNumSession session, MilleniumSocketConfig config ) {
        if ( config.isRecoverySession() ) {
            return new MilleniumRecoveryController( session, config );
        }

        return new MilleniumController( session, config );
    }

    public static void getContextForOutPersist( Event msg, ReusableString msgContext ) {
        msgContext.reset();
        if ( msg.getReusableType().getSubId() == EventIds.ID_NEWORDERSINGLE ) {
            msgContext.copy( ((NewOrderSingle) msg).getParentClOrdId() );
        }
    }

    public static void enrichRecoveredContext( Event msg, byte[] opt, int optOffset, int optLen ) {
        if ( msg.getReusableType() == ModelReusableTypes.NewOrderSingle ) {
            ((NewOrderSingleImpl) msg).getParentClOrdIdForUpdate().setValue( opt, optOffset, optLen );
        }
    }
}
