/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.ViewString;
import com.rr.core.model.*;
import com.rr.core.utils.FileException;
import com.rr.model.generated.fix.codec.CMEMDDecoder;
import com.rr.om.BaseOMTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestFixInstrumentLoader extends BaseOMTestCase {

    static {
        loadExchanges();
    }

    private MultiExchangeInstrumentStore _instStore = new MultiExchangeInstrumentStore( 1000 );
    private FixInstrumentLoader          _loader;

    @Before @Override public void setUp() {
        _instStore.setTickManager( new TickManager( "tstTickMgr" ) );
        _loader = new FixInstrumentLoader( _instStore, new CMEMDDecoder( "CMEInstDecoder" ) );
        _loader.setOverrideSecDefSpecialType( SecDefSpecialType.CMEFuture );
    }

    @Test
    public void testAutocertInst() throws FileException {
        _loader.loadFromFile( "./data/cme/secdef.autocert.dat" );

        ExchangeInstrument inst1 = _instStore.getExchInst( new ViewString( "407417" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCBT );
        assertNotNull( inst1 );

        assertEquals( new ViewString( "407417" ), inst1.getExchangeSymbol() );

        ExchangeInstrument inst3 = _instStore.getExchInst( new ViewString( "0GEU4" ), SecurityIDSource.SecurityDesc, ExchangeCode.XCBT );
        assertNotNull( inst3 );
    }

    /**
     * @throws FileException
     * @TODO fix CME multileg loading and use of sec def using security description in symbol
     */
    @Ignore
    @Test
    public void testGetByTag107() throws FileException {
        // 1128=99=45935=d49=CME34=92452=2013122917050035815=GBP22=848=12212355=JF107=EBPM4200=201406202=0207=CMED461=FFCXSX462=4562=1731=1827=2864=2865=5866=201305281145=213000000865=7866=201406161145=150000000870=4871=24872=1871=24872=3871=24872=4871=24872=7947=GBP969=5996=EUR1140=25001141=21022=GBX264=51022=GBI264=21142=K1143=3001144=31146=51147=1000001148=01150=836051151=EBP1180=701300=865796=201312279787=0.000019850=010=021
        _loader.loadFromFile( "./data/cme/secdef.t1.and.ebpm.dat" );

        ExchangeInstrument inst = _instStore.getExchInst( new ViewString( "EBPM4" ), SecurityIDSource.SecurityDesc, ExchangeCode.CMED );

        assertNotNull( inst );

        assertEquals( new ViewString( "122123" ), inst.getExchangeSymbol() );
    }

    @Test
    public void testList() throws FileException {
        // 1128=99=45935=d49=CME34=92452=2013122917050035815=GBP22=848=12212355=JF107=EBPM4200=201406202=0207=CMED461=FFCXSX462=4562=1731=1827=2864=2865=5866=201305281145=213000000865=7866=201406161145=150000000870=4871=24872=1871=24872=3871=24872=4871=24872=7947=GBP969=5996=EUR1140=25001141=21022=GBX264=51022=GBI264=21142=K1143=3001144=31146=51147=1000001148=01150=836051151=EBP1180=701300=865796=201312279787=0.000019850=010=021
        _loader.loadFromFile( "./data/cme/secdef.t1.and.ebpm.dat" );

        ExchangeInstrument inst = _instStore.getExchInst( new ViewString( "EBPM4" ), SecurityIDSource.SecurityDesc, ExchangeCode.CMED );

        assertNotNull( inst );

        List<ExchangeInstrument> instList = new ArrayList<>();
        instList.add( inst );
        instList.remove( inst );

        assertEquals( 0, instList.size() );

    }

    /**
     * @throws FileException
     * @TODO fix CME multileg loading and use of sec def using security description in symbol
     */
    @Ignore
    @Test
    public void testLoadOne() throws FileException {
        _loader.loadFromFile( "./data/cme/secdef.one.dat" );

        ExchangeInstrument inst = _instStore.getExchInst( new ViewString( "27069" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCEC );

        assertNotNull( inst );

        assertEquals( new ViewString( "27069" ), inst.getExchangeSymbol() );
        assertEquals( new ViewString( "QCZ5-QCN7.201512.XCEC" ), inst.id() );
    }

    @Test
    public void testT1Inst() throws FileException {
        _loader.loadFromFile( "./data/cme/secdef.t1.dat" );

        ExchangeInstrument inst = _instStore.getExchInst( new ViewString( "27069" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.XCEC );

        assertNotNull( inst );

        assertEquals( new ViewString( "27069" ), inst.getExchangeSymbol() );
    }

//    @Test
//    public void testHistProbDate() throws FileException {
//        String msg = "1128=99=39635=d52=19600101--5:60:00.0019814=101583422=Y48=ZL.BOF9.196901.XCBT55=BOF9167=FUT864=3865=5866=19600101865=6866=19681231865=7866=196901221700=60000969=0.011146=61147=600001151=ZL107=ZLF1969461=FCAXSX207=XCBT454=3455=BOF9456=S455=ZLF1969456=O455=BOF9456=A15=USX200=1969019787=0.01231=60000110=19817=19700101-00:00:00.0009816=19700101-00:00:00.0009818=UNS9819=S10=055";
//
//        HistExchInstSecDefStore instStore = new HistExchInstSecDefStore( "instStore", 1000 );
//        instStore.setTickManager( new TickManager( "tstTickMgr" ) );
//        FixDecoder decoder = new MD44Decoder();
//
//        FixInstrumentLoader loader = new FixInstrumentLoader( instStore, decoder );
//
//        loader.addSecDef( msg, null, false );
//
//        ExchangeInstrument inst = instStore.getExchInst( new ViewString("ZL.BOF9.196901.XCBT"), SecurityIDSource.InternalString, null );
//
//        assertNotNull( inst );
//    }

}
