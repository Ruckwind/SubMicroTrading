/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix;

import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.MDSnapshotFullRefresh;

import java.util.HashMap;
import java.util.Map;

public class FastFixSMTMapping {

    // map of msgType to SMT event class
    private final static Map<ZString, Class<? extends Event>> _fixMsgTypeToEventName = createMsgEventMap();

    // map of msgType to SMT event class
    private final static Map<ZString, ZString> _fixMsgTypeToBaseName = createFixTypeToFastNameMap();

    private static Map<ZString, Class<? extends Event>> createMsgEventMap() {
        Map<ZString, Class<? extends Event>> map = new HashMap<>();

        map.put( new ViewString( "0" ), HeartbeatImpl.class );
        map.put( new ViewString( "5" ), LogoutImpl.class );
        map.put( new ViewString( "A" ), LogonImpl.class );
        map.put( new ViewString( "d" ), SecurityDefinitionImpl.class );
        map.put( new ViewString( "f" ), SecurityStatusImpl.class );
        map.put( new ViewString( "W" ), MDSnapshotFullRefresh.class );
        map.put( new ViewString( "X" ), MDIncRefreshImpl.class );

        return map;
    }

    private static Map<ZString, ZString> createFixTypeToFastNameMap() {
        Map<ZString, ZString> map = new HashMap<>();

        map.put( new ViewString( "0" ), new ViewString( "MDHeartbeat" ) );
        map.put( new ViewString( "5" ), new ViewString( "MDLogout" ) );
        map.put( new ViewString( "A" ), new ViewString( "MDLogon" ) );
        map.put( new ViewString( "d" ), new ViewString( "MDSecurityDefinition" ) );
        map.put( new ViewString( "f" ), new ViewString( "MDSecurityStatus" ) );
        map.put( new ViewString( "W" ), new ViewString( "MDSnapshotFullRefresh" ) );
        map.put( new ViewString( "X" ), new ViewString( "MDIncRefresh" ) );

        map.put( new ViewString( "B" ), new ViewString( "MDNewsMessage" ) );
        map.put( new ViewString( "R" ), new ViewString( "MDQuoteRequest" ) );

        return map;
    }

    public static Class<? extends Event> getEventForFixMsgType( ZString fixMsgType ) {
        return _fixMsgTypeToEventName.get( fixMsgType );
    }

    public static ZString getFixMsgTypeToFastFixBaseName( ZString fixMsgType ) {
        return _fixMsgTypeToBaseName.get( fixMsgType );
    }

}
