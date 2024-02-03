/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.model.Instrument;
import com.rr.core.properties.CoreProps;
import com.rr.model.generated.internal.events.impl.CancelRequestImpl;
import com.rr.model.generated.internal.events.impl.NewOrderSingleImpl;
import com.rr.model.generated.internal.events.interfaces.CancelRequestWrite;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.TargetStrategy;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.exchange.loader.XMLExchangeLoader;
import com.rr.om.main.OMProps;
import com.rr.core.idgen.DailyLongIDGenerator;
import org.junit.Before;

import java.util.Collections;

import static org.junit.Assert.fail;

public abstract class BaseOMTestCase extends BaseTestCase {

    private static boolean _loadedExchanges = false;

    static {
        try {
            DummyAppProperties.testInit( Collections.singletonMap( CoreProps.RUN_ENV, _env.name() ), OMProps.instance() );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }
    }

    private        int     _nextId          = 0;

    public static synchronized void loadExchanges() {
        if ( !_loadedExchanges ) {
            _loadedExchanges = true;
            int idNumPrefix = 90;
            // ENX = 1100000000000000000
            DailyLongIDGenerator numIdGen = new DailyLongIDGenerator( idNumPrefix, 19 ); // to fit ENX numeric ID format
            ExchangeManager.instance().register( numIdGen );
            if ( ExchangeManager.instance().getByMIC( new ViewString( "XCME" ) ) == null ) {
                XMLExchangeLoader loader = new XMLExchangeLoader( "./common/testExchange.xml" );
                loader.load();
            }
        }
    }

    public static synchronized void unloadExchanges() {
        _loadedExchanges = false;
        ExchangeManager.instance().clear();
    }

    @Before
    public void setUp() throws Exception {
        loadExchanges();
    }

    protected CancelRequestWrite makeCancelRequest( NewOrderSingle nos ) {
        CancelRequestImpl cxl = new CancelRequestImpl();

        cxl.setTargetDest( nos.getTargetDest() );
        cxl.setSide( nos.getSide() );

        cxl.getClOrdIdForUpdate().copy( nos.getClOrdId() ).append( "_" ).append( "CXL_" ).append( nextId() );
        cxl.getOrigClOrdIdForUpdate().copy( nos.getClOrdId() );
        cxl.getStratIdForUpdate().copy( nos.getStratId() );
        cxl.setInstrument( nos.getInstrument() );

        cxl.setEventTimestamp( ClockFactory.get().currentInternalTime() );

        return cxl;
    }

    protected NewOrderSingle makeMarketOrder( final Instrument inst, final String clientStratId, final double qty, final Side side, final TimeInForce tif ) {
        NewOrderSingleImpl nos = new NewOrderSingleImpl();

        nos.setSide( side );
        nos.setOrdType( OrdType.Market );
        nos.setTimeInForce( tif );
        nos.setOrderQty( qty );

        nos.getClOrdIdForUpdate().copy( clientStratId ).append( "_" ).append( inst.id() ).append( "_" ).append( nextId() );
        nos.getStratIdForUpdate().copy( clientStratId );

        nos.setInstrument( inst );

        nos.setEventTimestamp( ClockFactory.get().currentInternalTime() );

        return nos;
    }

    protected NewOrderSingle makeVWAPOrder( final Instrument inst, final String clientStratId, final double qty, final Side side, final TimeInForce tif ) {
        NewOrderSingleImpl nos = new NewOrderSingleImpl();

        nos.setSide( side );
        nos.setOrdType( OrdType.Market );
        nos.setTimeInForce( tif );
        nos.setOrderQty( qty );
        nos.setTargetStrategy( TargetStrategy.VWAP );
        nos.setExpireTime( ClockFactory.get().currentInternalTime() + Constants.MS_IN_MINUTE * 5 );

        nos.getClOrdIdForUpdate().copy( clientStratId ).append( "_" ).append( inst.id() ).append( "_" ).append( nextId() );
        nos.getStratIdForUpdate().copy( clientStratId );

        nos.setInstrument( inst );

        nos.setEventTimestamp( ClockFactory.get().currentInternalTime() );

        return nos;
    }

    private synchronized int nextId() { return ++_nextId; }
}
