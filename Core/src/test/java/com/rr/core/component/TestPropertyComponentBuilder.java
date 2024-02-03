/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component;

import com.rr.core.component.builder.SMTPropertyComponentBuilder;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.properties.TestAppProps.TestProps;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.thread.RunState;
import com.rr.core.thread.SingleElementControlThread;
import com.rr.core.utils.SMTRuntimeException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestPropertyComponentBuilder extends BaseTestCase {

    public interface IntermediateInterface extends SMTComponent {
        // tag
    }

    public static class DummyComponentNoConstExtraArgs implements SMTControllableComponent, IntermediateInterface {

        public final String  _componentId;
        public double[][]                       _2darr;
        public DummyComponentNoConstExtraArgs[] _arrRef;
        public IntermediateInterface[]          _downcastRef;
        public       ZString _id;
        public SMTStartContext _initCtx;
        public boolean _likeMarmite;
        public boolean                          _prepared;
        public boolean                          _started;
        public boolean                          _stopped;

        private           Map<ExchangeCode, Double> _enumMap  = null;
        private           Map<String, String>       _aMap     = null;
        private transient RunState                  _runState = RunState.Unknown;

        public DummyComponentNoConstExtraArgs( String componentId ) {
            _componentId = componentId;
        }

        public DummyComponentNoConstExtraArgs( String componentId, IntermediateInterface[] refs ) {
            _componentId = componentId;
            _downcastRef = refs;
        }

        @Override public RunState getRunState()                          { return _runState; }

        @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

        @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) { _initCtx = ctx; }

        @Override public void prepare()                                                { _prepared = true; }

        @Override public String id()    { return _id.toString(); }

        @Override
        public String getComponentId() {
            return _componentId;
        }

        @Override public void startWork()                                              { _started = true; }

        @Override public void stopWork()                                               { _stopped = true; }

        public double[][] get2darr() { return _2darr; }

        public Map<ExchangeCode, Double> getEnumMap() {
            return _enumMap;
        }

        public Map<String, String> getMap() {
            return _aMap;
        }

        public boolean isLikeMarmite() {
            return _likeMarmite;
        }

        public void setLikeMarmite( boolean likeMarmite ) {
            _likeMarmite = likeMarmite;
        }

        public void setId( ZString id ) { _id = id; }
    }

    public static class DummyComponentWithExtraConstructorArgs implements SMTControllableComponent {

        public final String       _componentId;
        public final ZString      _zName;
        public final SMTComponent _ref;
        public       int          _apples;
        public SMTStartContext _initCtx;
        public       boolean      _likeMarmite;
        public       String       _name;
        public boolean         _prepared;
        public       double       _price;
        public boolean         _started;
        public boolean         _stopped;

        private transient RunState _runState = RunState.Unknown;

        public DummyComponentWithExtraConstructorArgs( String compId ) {
            _componentId = compId;
            _zName       = new ViewString( "DUMMY" );
            _ref         = null;
        }

        public DummyComponentWithExtraConstructorArgs( String compId, int apples, String name, ZString zName, SMTComponent ref ) {
            _apples      = apples;
            _name        = name;
            _zName       = zName;
            _ref         = ref;
            _componentId = compId;
        }

        @Override
        public String getComponentId() {
            return _componentId;
        }

        @Override public RunState getRunState()                          { return _runState; }

        @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

        @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) { _initCtx = ctx; }

        @Override public void prepare()                                                { _prepared = true; }

        @Override public void startWork()                                              { _started = true; }

        @Override public void stopWork()                                               { _stopped = true; }

        public boolean isLikeMarmite() {
            return _likeMarmite;
        }

        public void setLikeMarmite( boolean likeMarmite ) {
            _likeMarmite = likeMarmite;
        }
    }

    public static class DummyComponentLoader implements SMTSingleComponentLoader {

        public int          _apples;
        public String       _componentId;
        public String       _name;
        public SMTComponent _ref;
        public ZString      _zName;

        @Override
        public SMTComponent create( String id ) {
            return new DummyComponentWithExtraConstructorArgs( id, _apples, _name, _zName, _ref );
        }
    }

    public static class DummyComponentAutowireLoader implements SMTSingleComponentLoader {

        public int          _apples;
        public String       _componentId;
        public SMTComponent _findMe;
        public String       _name;
        public ZString      _zName;

        @Override
        public SMTComponent create( String id ) {
            return new DummyComponentWithExtraConstructorArgs( id, _apples, _name, _zName, _findMe );
        }
    }
    TestProps _testProps;
    private SMTComponentManager _compMgr = new SMTComponentManager();
    private Map<String, String> _localVars = new LinkedHashMap<>();

    @Before
    public void setUp() {
        _testProps = new TestProps( "className, id, name, type, value, likeMarmite, apples, pears, defaultProperties, ref, " +
                                    ", keyType, valType, loader, componentId, zname, price, arrRef, aMap, enumMap, 2darr", _localVars );
    }

    @Test
    public void testComponentLoader() throws Exception {
        _testProps.add( "sampleNexted.zname=ABC" );

        _testProps.add( "sample.props.defaultProperties=sampleNexted" );
        _testProps.add( "sample.props.apples=22" );
        _testProps.add( "sample.props.name=ComponentMFileExportWriter" );

        _testProps.add( "component.dm1.loader=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentLoader" );
        _testProps.add( "component.dm1.properties.name=Comp1" );
        _testProps.add( "component.dm1.properties.defaultProperties=sample.props" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid2.properties.name=Comp2" );
        _testProps.add( "component.cid2.properties.ref=dm1" ); // from component loader

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "dm1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( false, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertEquals( 22, ((DummyComponentWithExtraConstructorArgs) c1)._apples );
        assertSame( null, ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "ABC", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertSame( c1, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
    }

    @Test public void testComponentLoaderMapEnumProperty() throws Exception {

        _testProps.add( "map.borrowLimitPerInst.keyType=com.rr.core.model.ExchangeCode" );
        _testProps.add( "map.borrowLimitPerInst.valType=java.lang.Double" );
        _testProps.add( "map.borrowLimitPerInst.entry.1=XLON|20000000.0" );
        _testProps.add( "map.borrowLimitPerInst.entry.2=XPAR|200000.0" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );
        _testProps.add( "component.cid2.properties.likeMarmite=true" );
        _testProps.add( "component.cid2.properties.enumMap=borrowLimitPerInst" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 2, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid2" );

        Map<ExchangeCode, Double> map = c1.getEnumMap();
        assertNotNull( map );
        assertEquals( 2, map.size() );

        assertEquals( (Double) 20000000.0, map.get( ExchangeCode.XLON ) );
        assertEquals( (Double) 200000.0, map.get( ExchangeCode.XPAR ) );
    }

    @Test
    public void testComponentLoaderMapProperty() throws Exception {
        _testProps.add( "map.algos.keyType=java.lang.String" );
        _testProps.add( "map.algos.valType=java.lang.String" );
        _testProps.add( "map.algos.entry.1=CALARB|com.rr.strats.algo.cme.CALARB" );
        _testProps.add( "map.algos.entry.2=CALARC|com.rr.strats.algo.cme.CALARC" );
        _testProps.add( "map.algos.entry.3=CALARD|com.rr.strats.algo.cme.CALARD" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );
        _testProps.add( "component.cid2.properties.likeMarmite=true" );
        _testProps.add( "component.cid2.properties.aMap=algos" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 2, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid2" );

        Map<String, String> map = c1.getMap();
        assertNotNull( map );
        assertEquals( 3, map.size() );

        assertEquals( "com.rr.strats.algo.cme.CALARB", map.get( "CALARB" ) );
        assertEquals( "com.rr.strats.algo.cme.CALARC", map.get( "CALARC" ) );
        assertEquals( "com.rr.strats.algo.cme.CALARD", map.get( "CALARD" ) );
    }

    @Test
    public void testComponentLoaderRefArgUsingValueProperty() throws Exception {
        _testProps.add( "component.threadMktData1.className=com.rr.core.thread.SingleElementControlThread" );
        _testProps.add( "component.threadMktData1.arg.1.type=com.rr.core.utils.ThreadPriority" );
        _testProps.add( "component.threadMktData1.arg.1.value=DataIn1" );

        _testProps.add( "component.mktDataReceiver1.className=com.rr.core.session.MultiSessionThreadedReceiver" );
        _testProps.add( "component.mktDataReceiver1.arg.1.type=com.rr.core.thread.ControlThread" );
        _testProps.add( "component.mktDataReceiver1.arg.1.value=threadMktData1" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SingleElementControlThread   c1 = b.getComponent( "threadMktData1" );
        MultiSessionThreadedReceiver c2 = b.getComponent( "mktDataReceiver1" );

        assertSame( c1, c2.getControlThread() );
    }

    @Test
    public void testComponentLoaderWithAutowireRef() throws Exception {
        _testProps.add( "sampleNexted.zname=ABC" );

        _testProps.add( "sample.props.defaultProperties=sampleNexted" );
        _testProps.add( "sample.props.apples=22" );
        _testProps.add( "sample.props.name=ComponentMFileExportWriter" );

        _testProps.add( "component.findMe.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.findMe.properties.name=Comp2" );

        _testProps.add( "component.dm1.loader=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentAutowireLoader" );
        _testProps.add( "component.dm1.properties.componentId=dm1" );
        _testProps.add( "component.dm1.properties.name=Comp1" );
        _testProps.add( "component.dm1.properties.defaultProperties=sample.props" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "dm1" );
        SMTComponent c2 = b.getComponent( "findMe" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( false, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertEquals( 22, ((DummyComponentWithExtraConstructorArgs) c1)._apples );
        assertSame( c2, ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "ABC", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertSame( null, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
    }

    @Test
    public void testComponentLoaderWithRef() throws Exception {
        _testProps.add( "sampleNexted.zname=ABC" );

        _testProps.add( "sample.props.defaultProperties=sampleNexted" );
        _testProps.add( "sample.props.apples=22" );
        _testProps.add( "sample.props.name=ComponentMFileExportWriter" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid2.properties.name=Comp2" );

        _testProps.add( "component.dm1.loader=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentLoader" );
        _testProps.add( "component.dm1.properties.name=Comp1" );
        _testProps.add( "component.dm1.properties.defaultProperties=sample.props" );
        _testProps.add( "component.dm1.properties.ref=cid2" ); // from component loader

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "dm1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( false, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertEquals( 22, ((DummyComponentWithExtraConstructorArgs) c1)._apples );
        assertSame( c2, ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "ABC", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertSame( null, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
    }

    @Test
    public void testDefaultProperties() throws Exception {
        _testProps.add( "sampleNexted.likeMarmite=true" );
        _testProps.add( "otherSet.price=1234.5678" );

        _testProps.add( "sample.props.defaultProperties=sampleNexted, otherSet " );
        _testProps.add( "sample.props.apples=33" );
        _testProps.add( "sample.props.name=ComponentMFileExportWriter" );

        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid1.properties.name=Comp1" );
        _testProps.add( "component.cid1.properties.defaultProperties=sample.props" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 2, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertEquals( 33, ((DummyComponentWithExtraConstructorArgs) c1)._apples );
        assertEquals( 1234.5678, ((DummyComponentWithExtraConstructorArgs) c1)._price, 0.000005 );
        assertNull( ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );
    }

    @Test
    public void testDirectBadClassThrowsExc() throws Exception {

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$XXXDummyComponentNoConstuctor" );
        _testProps.add( "component.cid2.properties.id=Comp2" );
        _testProps.add( "component.cid2.properties.apples=123" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        try {
            b.init();

            fail( "should throw exception" );

        } catch( RuntimeException e ) {
            assertTrue( e.getMessage().contains( "Unable to instantiate" ) );
        }
    }

    @Test
    public void testDirectBadPropThrowsExc() throws Exception {

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );
        _testProps.add( "component.cid2.properties.pears=123" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        try {
            b.init();

            fail( "should throw exception" );

        } catch( SMTRuntimeException e ) {
            assertTrue( e.getMessage().contains( "Unable to reflect set property" ) );
        }
    }

    @Test public void testDirectNoConstructor() throws Exception {
        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid1.properties.id=Comp1" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );
        _testProps.add( "component.cid2.properties.likeMarmite=true" );
        _testProps.add( "component.cid2.properties.2darr={{10,11,12,13},\n  {20,21,22,23},\n  {30,31,32,33}}" );

        double[][] v = { { 10, 11, 12, 13 }, { 20, 21, 22, 23 }, { 30, 31, 32, 33 } };

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );
        SMTComponent c2 = b.getComponent( "cid2" );
        SMTComponent c3 = b.getComponent( "componentManager" );

        assertEquals( "Comp1", ((DummyComponentNoConstExtraArgs) c1).id().toString() );
        assertEquals( false, ((DummyComponentNoConstExtraArgs) c1)._likeMarmite );
        assertEquals( "Comp2", ((DummyComponentNoConstExtraArgs) c2).id().toString() );
        assertEquals( true, ((DummyComponentNoConstExtraArgs) c2)._likeMarmite );
        assertTrue( Arrays.deepEquals( v, ((DummyComponentNoConstExtraArgs) c2)._2darr ) );
        assertNotNull( c3 );
    }

    @Test
    public void testDirectWithConstructor() throws Exception {
        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid1.properties.name=Comp1" );
        _testProps.add( "component.cid1.properties.likeMarmite=true" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid2.arg.1.type=int" );
        _testProps.add( "component.cid2.arg.1.value=99" );
        _testProps.add( "component.cid2.arg.2.value=Comp2" );
        _testProps.add( "component.cid2.arg.3.type=ZString" );
        _testProps.add( "component.cid2.arg.3.value=./data/cme/secdef.t1.dat" );
        _testProps.add( "component.cid2.arg.4.type=ref" );
        _testProps.add( "component.cid2.arg.4.className=com.rr.core.component.SMTComponent" );
        _testProps.add( "component.cid2.arg.4.value=cid1" );
        _testProps.add( "component.cid2.properties.likeMarmite=true" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertNull( ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertEquals( "Comp2", ((DummyComponentWithExtraConstructorArgs) c2)._name );
        assertEquals( "./data/cme/secdef.t1.dat", ((DummyComponentWithExtraConstructorArgs) c2)._zName.toString() );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c2)._likeMarmite );
        assertEquals( 99, ((DummyComponentWithExtraConstructorArgs) c2)._apples );
        assertSame( c1, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
    }

    @Test
    public void testDirectWithConstructorMissingRefClassName() throws Exception {
        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid1.properties.name=Comp1" );
        _testProps.add( "component.cid1.properties.likeMarmite=true" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid2.arg.1.type=int" );
        _testProps.add( "component.cid2.arg.1.value=99" );
        _testProps.add( "component.cid2.arg.2.value=Comp2" );
        _testProps.add( "component.cid2.arg.3.type=ZString" );
        _testProps.add( "component.cid2.arg.3.value=./data/cme/secdef.t1.dat" );
        _testProps.add( "component.cid2.arg.4.type=ref" );
        _testProps.add( "component.cid2.arg.4.value=cid1" );
        _testProps.add( "component.cid2.properties.likeMarmite=true" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertNull( ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertEquals( "Comp2", ((DummyComponentWithExtraConstructorArgs) c2)._name );
        assertEquals( "./data/cme/secdef.t1.dat", ((DummyComponentWithExtraConstructorArgs) c2)._zName.toString() );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c2)._likeMarmite );
        assertEquals( 99, ((DummyComponentWithExtraConstructorArgs) c2)._apples );
        assertSame( c1, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
    }

    @Test
    public void testPropertyArrayRef() throws Exception {

        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid1.properties.id=Comp1" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );

        _testProps.add( "component.cid3.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid3.properties.id=Comp3" );

        _testProps.add( "component.cid4.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid4.properties.arrRef=cid1, cid2, cid3" );
        _testProps.add( "component.cid4.properties.likeMarmite=true" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 5, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid1" );
        DummyComponentNoConstExtraArgs c2 = b.getComponent( "cid2" );
        DummyComponentNoConstExtraArgs c3 = b.getComponent( "cid3" );
        DummyComponentNoConstExtraArgs c4 = b.getComponent( "cid4" );

        assertNotNull( c4._arrRef );
        assertEquals( 3, c4._arrRef.length );
        assertSame( c1, c4._arrRef[ 0 ] );
        assertSame( c2, c4._arrRef[ 1 ] );
        assertSame( c3, c4._arrRef[ 2 ] );
    }

    @Test
    public void testPropertyArrayRefSemiColon() throws Exception {

        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid1.properties.id=Comp1" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );

        _testProps.add( "component.cid3.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid3.properties.id=Comp3" );

        _testProps.add( "component.cid4.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid4.properties.arrRef=cid1 ; cid2 ; cid3" );
        _testProps.add( "component.cid4.properties.likeMarmite=true" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 5, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid1" );
        DummyComponentNoConstExtraArgs c2 = b.getComponent( "cid2" );
        DummyComponentNoConstExtraArgs c3 = b.getComponent( "cid3" );
        DummyComponentNoConstExtraArgs c4 = b.getComponent( "cid4" );

        assertNotNull( c4._arrRef );
        assertEquals( 3, c4._arrRef.length );
        assertSame( c1, c4._arrRef[ 0 ] );
        assertSame( c2, c4._arrRef[ 1 ] );
        assertSame( c3, c4._arrRef[ 2 ] );
    }

    @Test
    public void testPropertyConstructorArgArrayRefUsingValue() throws Exception {

        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid1.properties.id=Comp1" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );

        _testProps.add( "component.cid3.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid3.properties.id=Comp3" );

        _testProps.add( "component.cid4.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid4.arg.1.type=[Lcom.rr.core.component.TestPropertyComponentBuilder$IntermediateInterface;" );
        _testProps.add( "component.cid4.arg.1.value=cid1, cid2, cid3" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 5, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid1" );
        DummyComponentNoConstExtraArgs c2 = b.getComponent( "cid2" );
        DummyComponentNoConstExtraArgs c3 = b.getComponent( "cid3" );
        DummyComponentNoConstExtraArgs c4 = b.getComponent( "cid4" );

        assertNotNull( c4._downcastRef );
        assertEquals( 3, c4._downcastRef.length );
        assertSame( c1, c4._downcastRef[ 0 ] );
        assertSame( c2, c4._downcastRef[ 1 ] );
        assertSame( c3, c4._downcastRef[ 2 ] );
    }

    @Test
    public void testPropertyConstructorArgArrayRefWithDowncast() throws Exception {

        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid1.properties.id=Comp1" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid2.properties.id=Comp2" );

        _testProps.add( "component.cid3.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid3.properties.id=Comp3" );

        _testProps.add( "component.cid4.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentNoConstExtraArgs" );
        _testProps.add( "component.cid4.arg.1.type=[Lcom.rr.core.component.TestPropertyComponentBuilder$IntermediateInterface;" );
        _testProps.add( "component.cid4.arg.1.ref=cid1, cid2, cid3" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 5, comps.size() );

        DummyComponentNoConstExtraArgs c1 = b.getComponent( "cid1" );
        DummyComponentNoConstExtraArgs c2 = b.getComponent( "cid2" );
        DummyComponentNoConstExtraArgs c3 = b.getComponent( "cid3" );
        DummyComponentNoConstExtraArgs c4 = b.getComponent( "cid4" );

        assertNotNull( c4._downcastRef );
        assertEquals( 3, c4._downcastRef.length );
        assertSame( c1, c4._downcastRef[ 0 ] );
        assertSame( c2, c4._downcastRef[ 1 ] );
        assertSame( c3, c4._downcastRef[ 2 ] );
    }

    @Test
    public void testPropertyReference() throws Exception {
        _testProps.add( "component.cid1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid1.properties.name=Comp1" );
        _testProps.add( "component.cid1.properties.likeMarmite=true" );

        _testProps.add( "component.cid2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.cid2.properties.name=Comp2" );
        _testProps.add( "component.cid2.properties.ref=cid1" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertNull( ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertEquals( "Comp2", ((DummyComponentWithExtraConstructorArgs) c2)._name );
        assertEquals( false, ((DummyComponentWithExtraConstructorArgs) c2)._likeMarmite );
        assertSame( c1, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c2)._zName.toString() );
    }

    @Test
    public void testPropertyReferenceUsingLocalVars() throws Exception {

        _localVars.put( "ID", "cid" );

        _testProps.add( "component.%{ID}1.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.%{ID}1.properties.name=Comp1" );
        _testProps.add( "component.%{ID}1.properties.likeMarmite=true" );

        _testProps.add( "component.%{ID}2.className=com.rr.core.component.TestPropertyComponentBuilder$DummyComponentWithExtraConstructorArgs" );
        _testProps.add( "component.%{ID}2.properties.name=Comp2" );
        _testProps.add( "component.%{ID}2.properties.ref=%{ID}1" );

        SMTPropertyComponentBuilder b = new SMTPropertyComponentBuilder( _testProps, _compMgr );

        b.init();

        Collection<SMTComponent> comps = b.getComponents();

        assertEquals( 3, comps.size() );

        SMTComponent c1 = b.getComponent( "cid1" );
        SMTComponent c2 = b.getComponent( "cid2" );

        assertEquals( "Comp1", ((DummyComponentWithExtraConstructorArgs) c1)._name );
        assertEquals( true, ((DummyComponentWithExtraConstructorArgs) c1)._likeMarmite );
        assertNull( ((DummyComponentWithExtraConstructorArgs) c1)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c1)._zName.toString() );

        assertEquals( "Comp2", ((DummyComponentWithExtraConstructorArgs) c2)._name );
        assertEquals( false, ((DummyComponentWithExtraConstructorArgs) c2)._likeMarmite );
        assertSame( c1, ((DummyComponentWithExtraConstructorArgs) c2)._ref );
        assertEquals( "DUMMY", ((DummyComponentWithExtraConstructorArgs) c2)._zName.toString() );
    }
}
