package com.rr.inst;

import com.rr.core.lang.ViewString;
import com.rr.core.model.Exchange;
import com.rr.core.model.SecDefSpecialType;
import com.rr.core.model.TickManager;
import com.rr.model.generated.fix.codec.CMEMDDecoder;
import com.rr.om.BaseOMTestCase;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.newmain.SMTContext;

public class InstTestUtils {

    public static final String CME_ALGO_SEC_DEF_FILE = "./data/cme/algo_secdef.dat";
    public static final String INSTRUMENT_LOCATOR    = "instrumentLocator";

    public static InstrumentStore createCMEAlgoInstStore( String instFile, SMTContext ctx ) throws Exception {
        BaseOMTestCase.loadExchanges();

        Exchange e = ExchangeManager.instance().getByMIC( new ViewString( "XCME" ) );

        SingleExchangeInstrumentStore instrumentLocator = new SingleExchangeInstrumentStore( e, 1000 );

        TickManager tickMgr = getTickManager();
        instrumentLocator.setTickManager( tickMgr );

        FixInstrumentLoader loader = new FixInstrumentLoader( instrumentLocator, new CMEMDDecoder( "InstDecoder" ) );
        loader.setOverrideSecDefSpecialType( SecDefSpecialType.CMEFuture );
        loader.loadFromFile( instFile );

        if ( ctx != null ) {
            ctx.setInstrumentLocator( instrumentLocator );
        }

        return instrumentLocator;
    }

    public static TickManager getTickManager() {
//        XMLTickManagerLoader l = new XMLTickManagerLoader();
//        l.setTickScaleFiles( TICK_SCALE_FILE );
//        return (TickManager) l.create( "testTickLoader" );

        return new TickManager( "TickMgr" );
    }
}
