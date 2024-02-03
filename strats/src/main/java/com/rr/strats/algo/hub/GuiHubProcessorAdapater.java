/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.hub;

import com.rr.core.dispatch.MessageDispatcher;
import com.rr.core.model.Message;
import com.rr.hub.HubProcessor;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.StrategyState;


/**
 * adapt inbound HUB events to GUI model for visualisation
 */
public class GuiHubProcessorAdapater extends HubProcessor {

    public GuiHubProcessorAdapater( String id, MessageDispatcher inboundDispatcher ) {
        super( id, inboundDispatcher );
    }

    @Override
    public void handleNow( Message msg ) {
        
        _logMsg.copy( "HUB RECEIVED : " );
        
        msg.dump( _logMsg );
        
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_STRATEGYSTATE:
            handle( (StrategyState) msg );
            break;
        }
        
        _log.info( _logMsg );
        
        _eventRecycler.recycle( msg );
    }

    private void handle( StrategyState msg ) {
        // todo
    }
}
