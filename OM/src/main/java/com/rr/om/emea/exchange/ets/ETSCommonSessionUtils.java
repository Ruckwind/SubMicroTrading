/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.model.Event;
import com.rr.model.generated.internal.core.EventIds;

class ETSCommonSessionUtils {

    static int setOutSeqNum( ETSController controller, final Event msg ) {
        int nextOut = 0;
        if ( isSessionMessage( msg ) == false ) {
            nextOut = controller.getAndIncNextOutSeqNum();
            msg.setMsgSeqNum( nextOut );
        }
        return nextOut;
    }

    static boolean isSessionMessage( Event msg ) {
        final int subId = msg.getReusableType().getSubId();

        switch( subId ) {
        case EventIds.ID_HEARTBEAT:
        case EventIds.ID_TESTREQUEST:
        case EventIds.ID_UTPLOGON:
        case EventIds.ID_UTPLOGONREJECT:
            return true;
        }
        return false;
    }
}
