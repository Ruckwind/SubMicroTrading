/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.FixDecoder;
import com.rr.core.component.SMTComponent;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.SecDefSpecialType;
import com.rr.core.utils.SMTException;
import com.rr.inst.FixInstrumentLoader;
import com.rr.inst.InstrumentStore;
import com.rr.model.generated.fix.codec.MD44Decoder;

public class InstrumentFileChainLoader implements SMTComponent {

    private static final Logger _console = ConsoleFactory.console( InstrumentFileChainLoader.class, Level.info );

    private FixDecoder                _decoder                     = new MD44Decoder();
    private String                    _file;
    private SecDefSpecialType         _overrrideSecDefType         = null;
    private boolean                   _throwErrorOnZeroInstruments = false;
    private InstrumentFileChainLoader _next;
    private String                    _id;

    public InstrumentFileChainLoader( final String id ) {
        _id = id;
    }

    @Override public String getComponentId()           { return _id; }

    public FixDecoder getDecoder()                     { return _decoder; }

    public void setDecoder( final FixDecoder decoder ) { _decoder = decoder; }

    public void load( InstrumentStore instrumentStore ) throws SMTException {

        FixInstrumentLoader loader = new FixInstrumentLoader( instrumentStore, _decoder );

        loader.setOverrideSecDefSpecialType( _overrrideSecDefType );

        loader.loadFromFile( _file, _throwErrorOnZeroInstruments );

        if ( _next != null ) _next.load( instrumentStore );
    }
}
