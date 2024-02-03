package com.rr.inst;

import com.rr.core.annotations.SMTPreRestore;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.newmain.SMTContext;

public class FXInstrumentFactory implements SMTInitialisableComponent {

    private           ExchangeCode _exchangeCode;
    private           String       _id;
    private transient RunState     _runState = RunState.Unknown;
    private transient boolean      _created;

    private static SecurityDefinitionImpl makeSecDef( FXPair fxPair, ExchangeCode exchangeCode ) {

        SecurityDefinitionImpl secDef = new SecurityDefinitionImpl();

        ZString fxCode = fxPair.getFXCode();

        secDef.setEventTimestamp( 1 );
        secDef.getSecurityDescForUpdate().copy( fxCode );
        secDef.getSymbolForUpdate().copy( fxCode );
        secDef.setCurrency( fxPair.getBaseCcy() );
        secDef.getSecurityIDForUpdate().copy( fxCode );
        secDef.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        secDef.setSecurityType( SecurityType.FX );
        secDef.setSecurityExchange( exchangeCode );

        InstUtils.addKey( secDef, SecurityIDSource.ExchangeSymbol, fxCode.toString() );
        InstUtils.addKey( secDef, SecurityIDSource.Symbol, fxCode.toString() );

        return secDef;
    }

    public FXInstrumentFactory( String id, ExchangeCode exchangeCode ) {
        _id           = id;
        _exchangeCode = exchangeCode;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase phase ) {
        /* component required by snapshot restore so must init in preRestore */
        if ( !_created ) {
            preRestore( ctx ); // only needed for tests
        }
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @SMTPreRestore public void preRestore( final SMTStartContext ctx ) {
        if ( _exchangeCode == null ) throw new SMTRuntimeException( "FXInstrumentFactory " + getComponentId() + " missing exchangeCode property" );

        if ( !(ctx instanceof SMTContext) ) throw new SMTRuntimeException( "FXInstrumentFactory requires SMTContext" );

        SMTContext smtContext = (SMTContext) ctx;

        InstrumentLocator instLocator = smtContext.getInstrumentLocator();

        if ( !(instLocator instanceof InstrumentStore) ) throw new SMTRuntimeException( "FXInstrumentFactory requires InstrumentStore" );

        InstrumentStore instStore = (InstrumentStore) instLocator;

        for ( FXPair pair : FXPair.values() ) {
            if ( pair == FXPair.Unknown ) continue;
            FXInstrument fxInst = instStore.getFXInstrument( pair, _exchangeCode );

            if ( fxInst == null ) {
                fxInst = create( pair, _exchangeCode );
            }

            if ( fxInst.getFXPair() != FXPair.Unknown ) {
                instStore.add( fxInst );
            }
        }

        _created = true;
    }

    private FXInstrument create( FXPair pair, ExchangeCode code ) {

        final Exchange               exchange = ExchangeManager.instance().getByCode( code );
        final SecurityDefinitionImpl secDef   = makeSecDef( pair, code );

        FXInstrumentImpl inst = new FXInstrumentImpl( exchange, secDef, pair );

        return inst;
    }
}
