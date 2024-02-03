package com.rr.inst;

import com.rr.core.model.Exchange;
import com.rr.core.model.FXInstrument;
import com.rr.core.model.FXPair;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;

public class FXInstrumentImpl extends InstrumentSecurityDefWrapperImpl implements FXInstrument {

    private final FXPair _fxPair;

    public FXInstrumentImpl( final Exchange exchange, final SecurityDefinitionImpl secDef, final FXPair fxPair ) {
        super( exchange, secDef, null );
        _fxPair = fxPair;
    }

    @Override public FXPair getFXPair() { return _fxPair; }
}
