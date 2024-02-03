/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.Level;
import com.rr.core.utils.SMTRuntimeException;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestAppProps extends BaseTestCase {

    public static class TestPropertyTags implements PropertyTags {

        private static final Set<String> _set = new HashSet<>();
        private static TestPropertyTags _instance = new TestPropertyTags();

        public enum Tags implements PropertyTags.Tag {
            Price, Desc, Fuel, Seats, Port, home
        }

        static {
            for ( Tags p : Tags.values() ) {
                _set.add( p.toString().toLowerCase() );
            }
        }

        public static TestPropertyTags instance() { return _instance; }

        private TestPropertyTags()                { /* singleton */ }

        @Override public String getSetName()      { return "TestProps"; }

        @Override
        public boolean isValidTag( String tag ) {
            if ( tag == null ) return false;
            return _set.contains( tag.toLowerCase() );
        }

        @Override public Tag lookup( String tag ) { return Tags.valueOf( tag ); }

        public void add( String validArg ) {
            _set.add( validArg.toLowerCase() );
        }
    }

    public static class TestProps extends AppProps {

        private final Map<String, String> _localProps;
        private       int                 _lineNo = 0;

        public TestProps( String validArgs ) {
            this( validArgs, new LinkedHashMap<>() );
        }

        public TestProps( String validArgs, Map<String, String> localProps ) {
            _localProps = localProps;
            setPropSet( TestPropertyTags.instance() );
            String[] args = validArgs.split( "," );
            for ( String arg : args ) {
                arg = arg.trim();
                if ( arg.length() > 0 ) {
                    TestPropertyTags.instance().add( arg );
                }
            }
            override( CoreProps.APP_NAME, "unitTest" );
            setInit();
        }

        public TestProps() {
            this( "" );
        }

        @Override protected void setSpecialProps() {
            setAppName( "unitTest" );
            String home = System.getProperty( "user.home" );
            put( CoreProps.USER_HOME, home );
            System.out.println( "Setting user home to " + home );
        }

        public void add( String line ) throws Exception {
            procLine( line, "n/a", ++_lineNo, _localProps, new LinkedHashMap<>() );
        }

        public void resolve() {
            resolveProps( new LinkedHashMap<>() );
        }
    }

    @Test
    public void testBadExpr() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.base.port=100" );
        try {
            p.add( "car.next.port=eval(${car.base.port}+q1)" );
            p.resolve();
            fail( "didnt throw exceptio" );
        } catch( Exception e ) {
            // ok
        }
    }

    @Test
    public void testCaseIgnored() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.DeSC=4*4 SUV" );

        String desc = p.getProperty( "car.audi.Q7.desc" );
        assertEquals( "4*4 SUV", desc );

        desc = p.getProperty( "car.audi.Q7.DESC" );
        assertEquals( "4*4 SUV", desc );
    }

    @Test
    public void testExpr() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.base.port=100" );
        p.add( "car.next.port=eval(${car.base.port}+1)" );
        p.add( "car.other.port=eval( ${car.base.port} +2 )" );
        p.resolve();

        int price = p.getIntProperty( "car.next.port" );
        assertEquals( 101, price );

        price = p.getIntProperty( "car.other.port" );
        assertEquals( 102, price );
    }

    @Test
    public void testFailedMultiKeys() throws Exception {
        TestProps p = new TestProps();

        p.add( "log.lvl/com.rr.core=WARN" );
        p.add( "log.lvl/com.rr.strats=trace" );
        p.add( "log.lvl/com.rr.algo=info" );
        p.add( "log.lvl/com.rr=info" );
        p.add( "log.lvl/com.rr.core.algo.bt=WARN" );

        String[] entries = p.getMatchedKeys( "log.lvl/" );

        assertEquals( 5, entries.length );

        final TreeSet<String> tmpSet = new TreeSet<>( ( o1, o2 ) -> {
            int lenComp = o2.length() - o1.length();

            if ( lenComp != 0 ) return lenComp;

            return o1.compareTo( o2 );
        } );

        for ( String s : entries ) {
            tmpSet.add( s );
        }

        assertEquals( entries.length, tmpSet.size() );

        List<String> tmpList = new ArrayList<>();
        for ( String s : tmpSet ) {
            tmpList.add( s );
        }

        assertEquals( "log.lvl/com.rr.core.algo.bt", tmpList.get( 0 ) );
        assertEquals( "log.lvl/com.rr", tmpList.get( 4 ) );
    }

    @Test
    public void testGroups() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.price=50000" );
        p.add( "car.audi.q7.desc=Q7 4*4 SUV" );
        p.add( "car.audi.a8.desc=A8 saloon" );
        p.add( "car.audi.A8.price=80000" );
        p.add( "car.audi.A4.desc=A4 saloon" );

        String[] carsA = p.getNodes( "car.audi" );
        String[] carsB = p.getNodes( "car.audi." );

        assertEquals( 3, carsA.length );
        assertEquals( 3, carsB.length );

        for ( String car : carsA ) {
            if ( !car.equals( "q7" ) && !car.equals( "a4" ) && !car.equals( "a8" ) ) {
                fail( "bad car : [" + car + "]" );
            }
        }
    }

    @Test public void testHOME() throws Exception {
        TestProps p = new TestProps();

        String desc = p.getProperty( "HOME" );
        assertTrue( desc.length() > 0 );
    }

    @Test
    public void testHomeMacro() throws Exception {
        TestProps p = new TestProps();

        p.add( "CIQ_DB_PWD_FILE=${CIQ_DB_PWD_FILE:-${HOME}/PWD/COREVIEW.iqdata.pwd}" );
        p.resolve();

        String desc = p.getProperty( "CIQ_DB_PWD_FILE" );
        assertTrue( desc.endsWith( "/PWD/COREVIEW.iqdata.pwd" ) );
    }

    @Test
    public void testInvalidProperty() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.desc=4*4 SUV" );

        try {
            p.add( "car.audi.Q7.duff=50000" );
            fail( "Failed to throw exception on missing mand prop" );
        } catch( InvalidPropertyException e ) {
            // expected
        }

        try {
            p.getProperty( "car.audi.Q7.duff" );
            fail( "Failed to throw exception on bad prop 'duff'" );
        } catch( SMTRuntimeException e ) {
            //  expected
        }
    }

    @Test
    public void testMacros() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.price=50000" );
        p.add( "car.audi.Q7.desc=Q7 4*4 SUV" );
        p.add( "car.audi.a8.desc=A8 saloon" );
        p.add( "car.audi.desc=${car.audi.Q7.desc} and ${car.audi.a8.desc}" );
        p.add( "car.desc=AUDI ${car.audi.desc}" );
        p.resolve();

        String desc = p.getProperty( "car.audi.desc" );
        assertEquals( "Q7 4*4 SUV and A8 saloon", desc );

        desc = p.getProperty( "car.desc" );
        assertEquals( "AUDI Q7 4*4 SUV and A8 saloon", desc );
    }

    @Test
    public void testMacrosWithDefaultSet() throws Exception {
        TestProps p = new TestProps();

        p.add( "CCC=fff" );
        p.add( "BBB=zzz ${CCC:-DDD}" );
        p.resolve();

        String desc = p.getProperty( "BBB" );
        assertEquals( "zzz fff", desc );
    }

    @Test
    public void testMacrosWithDefaultUnset() throws Exception {
        TestProps p = new TestProps();

        p.add( "AAA=50000" );
        p.add( "BBB=zzz ${CCC:-DDD}" );
        p.resolve();

        String desc = p.getProperty( "BBB" );
        assertEquals( "zzz DDD", desc );
    }

    @Test
    public void testMultiKeys() throws Exception {
        TestProps p = new TestProps();

        p.add( "log.xxx/com.rr.core.utils=ERROR" );

        p.add( "log.lvl/com.rr.core.utils=ERROR" );
        p.add( "log.lvl/com.rr=WARN" );
        p.add( "log.lvl/com.rr.strats=trace" );
        p.add( "log.lvl/com.rr.core.=info" );
        p.add( "log.lvl/com.rr.=debug" );
        p.add( "log.lvl/com.rr.core.utils.SpecialClass=trace" );

        p.add( "log.aaa/com.rr.core.utils=ERROR" );

        String[] entries = p.getMatchedKeys( "log.lvl/" );

        assertEquals( 6, entries.length );

        assertEquals( "log.lvl/com.rr.core.utils", entries[ 0 ] );

        Level res = p.getProperty( entries[ 0 ], Level.class );
        assertSame( Level.ERROR, res );

        final TreeSet<String> tmpSet = new TreeSet<>( ( a, b ) -> (a.length() - b.length()) );

        for ( String s : entries ) {
            tmpSet.add( s );
        }

        assertEquals( entries.length, tmpSet.size() );
    }

    @Test
    public void testMultiVal() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.bmw.Q7.price=50000" );
        p.add( "car.audi.q7.desc=Q7 4*4 SUV" );
        p.add( "car.audi.a8.desc=A8 saloon" );
        p.add( "car.audi.A8.price=80000" );
        p.add( "car.alfa.A4.desc=A4 saloon" );

        String[] carsA = p.getMatchedVals( "car.audi" );

        assertEquals( 3, carsA.length );

        for ( String car : carsA ) {
            if ( !car.equals( "Q7 4*4 SUV" ) && !car.equals( "A8 saloon" ) && !car.equals( "80000" ) ) {
                fail( "bad car : [" + car + "]" );
            }
        }
    }

    @Test
    public void testPropertyGroups() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.price=50000" );
        p.add( "car.audi.q7.desc=Q7 4*4 SUV" );
        p.add( "car.audi.a8.desc=A8 saloon" );
        p.add( "car.audi.A8.price=80000" );
        p.add( "car.audi.desc=AUDI" );
        p.add( "car.audi.A1.price=25000" );
        p.add( "car.fuel=Diesel" );

        PropertyGroup g = new PropertyGroup( p, "car.audi.Q7", "car.audi", "car" );

        int price = g.getIntProperty( TestPropertyTags.Tags.Price, false, 0 );
        assertEquals( 50000, price );

        String desc = g.getProperty( TestPropertyTags.Tags.Desc );
        assertEquals( "Q7 4*4 SUV", desc );

        String fuel = g.getProperty( TestPropertyTags.Tags.Fuel );
        assertEquals( "Diesel", fuel );                                 // taken from minor

        g = new PropertyGroup( p, "car.audi.A1", "car.audi", "car" );

        price = g.getIntProperty( TestPropertyTags.Tags.Price, false, 0 );
        assertEquals( 25000, price );

        desc = g.getProperty( TestPropertyTags.Tags.Desc );
        assertEquals( "AUDI", desc );                                   // taken from major

        try {
            g.getProperty( TestPropertyTags.Tags.Seats );
            fail( "Mand missing prop should throw exception" );
        } catch( SMTRuntimeException e ) {
            // expected
        }
    }

    @Test
    public void testQuotesRemoved() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.price=\"50000\"" );

        int price = p.getIntProperty( "car.audi.Q7.price", false, 0 );
        assertEquals( 50000, price );

        String desc = p.getProperty( "car.audi.Q7.price" );
        assertEquals( "50000", desc );
    }

    @Test
    public void testSimple() throws Exception {
        TestProps p = new TestProps();

        p.add( "car.audi.Q7.price=50000" );
        p.add( "car.audi.Q7.desc=4*4 SUV" );

        int price = p.getIntProperty( "car.audi.Q7.price", false, 0 );
        assertEquals( 50000, price );

        String desc = p.getProperty( "car.audi.Q7.desc" );
        assertEquals( "4*4 SUV", desc );

        String fuel = p.getProperty( "car.audi.Q7.fuel", false, "Diesel" );
        assertEquals( "Diesel", fuel );

        try {
            p.getProperty( "car.audi.Q7.seats" );
            fail( "Failed to throw exception on missing mand prop" );
        } catch( SMTRuntimeException e ) {
            //  expected
        }
    }
}
