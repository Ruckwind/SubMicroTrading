/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.twoleg.catchup;

import com.rr.strats.algo.arb.twoleg.MultiLegArbCorrectiveHandler;


public class CatchUpStates {

    private final CUSNone                   _none;
    private final CUSInitialOpen            _initialOpen;
    private final CUSLimitOrderCatchup      _limitOrderCatchup;
    private final CUSCancellingOpen         _cancellingOpen;
    private final CUSFlatten                _flatten;
    private final CUSSuspended              _suspended;

    public CatchUpStates() {
        _none                   = new CUSNone( this );
        _initialOpen            = new CUSInitialOpen( this );
        _limitOrderCatchup      = new CUSLimitOrderCatchup( this );
        _cancellingOpen         = new CUSCancellingOpen( this );
        _flatten                = new CUSFlatten( this );
        _suspended              = new CUSSuspended( this );
    }

    public MultiLegArbCorrectiveHandler getNone() {
        return _none;
    }

    public MultiLegArbCorrectiveHandler getInitialOpen() {
        return _initialOpen;
    }

    public MultiLegArbCorrectiveHandler getLimitOrderCatchup() {
        return _limitOrderCatchup;
    }

    public MultiLegArbCorrectiveHandler getCancellingOpen() {
        return _cancellingOpen;
    }

    public MultiLegArbCorrectiveHandler getFlatten() {
        return _flatten;
    }

    public MultiLegArbCorrectiveHandler getSuspendedCatchup() {
        return _suspended;
    }
}
