/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.strats.algo.twoleg.catchup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.strats.algo.arb.twoleg.MultiLegArbCorrectiveHandler;


public abstract class BaseMultiLegArbCatchupHandler implements MultiLegArbCorrectiveHandler {

    private   final String          _strName;
    private   final ZString         _zname;
    protected final CatchUpStates   _states;
    protected final ReusableString  _logMsg = new ReusableString();
    
    public BaseMultiLegArbCatchupHandler( String name, CatchUpStates catchupStates ) {
        _states = catchupStates;
        _zname = new ViewString( name );
        _strName = name;
    }
    
    @Override
    public ZString getName() {
        return _zname;
    }
    
    @Override
    public String toString() {
        return _strName;
    }
}
