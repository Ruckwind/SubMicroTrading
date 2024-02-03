/*******************************************************************************
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,  software distributed under the License 
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.rr.core.algo.mgr;

import com.rr.core.admin.AdminAgent;
import com.rr.core.algo.strats.Algo;
import com.rr.core.algo.strats.Strategy;
import com.rr.core.component.CompRunState;
import com.rr.core.component.SMTContext;
import com.rr.core.log.Logger;
import com.rr.core.log.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages strategies, expects Algo instances to propogate control invocations
 *
 * @author Richard Rose
 */
public class StrategyManagerImpl implements StrategyManager {

    private static final Logger       _log = LoggerFactory.create( StrategyManagerImpl.class );

    private final String                                 _id;
    private final ConcurrentHashMap<String, Algo<?>>     _algoMap      = new ConcurrentHashMap<String, Algo<?>>();
    private final ConcurrentHashMap<String, Strategy<?>> _strategyMap  = new ConcurrentHashMap<String, Strategy<?>>();
    private final Set<Strategy<?>>                       _strategies   = Collections.synchronizedSet( new LinkedHashSet<Strategy<?>>() );
    private       CompRunState                           _compRunState = CompRunState.Initial;

    public StrategyManagerImpl( String id ) {
        _id = id;
        StrategyManagerAdmin sma = new StrategyManagerAdmin( this );
        AdminAgent.register( sma );
    }

    @Override public final CompRunState getCompRunState() {  return _compRunState; }

    private synchronized boolean setCompRunState( CompRunState state ) {
        boolean changed = false;
        if ( CompRunState.procStateChange( id(), _compRunState, state ) ) {
            _compRunState = state;
            changed = true;
        }

        return changed;
    }

    @Override
    public String id() {
        return _id;
    }

    @Override
    public void startWork() {
        if ( setCompRunState( CompRunState.Started ) ) {
            _log.info( "StrategyManagerImpl.startWork() start" );

            for ( Algo<?> algo : _algoMap.values() ) {
                _log.info( "StrategyManagerImpl.startWork() start algo " + algo.id() );

                algo.startWork();
            }

            _log.info( "StrategyManagerImpl.startWork() end" );
        }
    }

    @Override
    public void stopWork() {
        if ( setCompRunState( CompRunState.Stopping ) ) {
            _log.info( "StrategyManagerImpl.stopWork() start" );

            for ( Algo<?> algo : _algoMap.values() ) {
                _log.info( "StrategyManagerImpl.stopWork() stop algo " + algo.id() );

                algo.stopWork();
            }

            _log.info( "StrategyManagerImpl.stopWork() end" );
        }
    }

    @Override
    public void init( SMTContext ctx ) {
        if ( setCompRunState( CompRunState.Initialised ) ) {
            _log.info( "StrategyManagerImpl.init() start" );

            for ( Algo<?> algo : _algoMap.values() ) {
                _log.info( "StrategyManagerImpl.init() init algo " + algo.id() );

                algo.init( ctx );
            }

            _log.info( "StrategyManagerImpl.init() end" );
        }
    }

    @Override
    public void prepare() {
        _log.info( "StrategyManagerImpl.prepare() start" );
        
        for( Algo<?> algo : _algoMap.values() ) {
            _log.info( "StrategyManagerImpl.prepare() prepare algo " + algo.id() );

            algo.prepare();
        }

        _log.info( "StrategyManagerImpl.prepare() end" );
    }

    @Override
    public Algo<?> getAlgo( String algoId ) {
        return _algoMap.get( algoId );
    }

    @Override
    public Strategy<?> getStrategy( String strategyId ) {
        return _strategyMap.get( strategyId );
    }

    @Override
    public Set<Strategy<?>> getStrategies() {
        return _strategies;
    }

    @Override
    public void registerAlgo( Algo<?> algo ) {
        if ( _algoMap.putIfAbsent( algo.id(), algo ) != null ) {
            throw new SMTRuntimeException( "Duplicate registration of algo " + algo.id() );
        }
    }

    @Override
    public void registerStrategy( Strategy<?> strat ) {
        if ( _strategyMap.putIfAbsent( strat.id(), strat ) != null ) {
            throw new SMTRuntimeException( "Duplicate registration of strategy " + strat.id() );
        }
        _strategies.add( strat );        
    }
}
