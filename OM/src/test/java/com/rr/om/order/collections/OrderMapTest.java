/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.OMUtils;
import com.rr.om.order.Order;
import com.rr.om.order.OrderImpl;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.junit.Assert.*;

public class OrderMapTest extends BaseTestCase {

    private static final int ORDER_SIZE = 9000;

    private static final ZString SOME_KEY = new ViewString( "SOMEKEY" );

    @Test
    public void testConcOrderMapInsertWithResizing() {
        doInsert( new SegmentOrderMap( 2, 0.75f, 1 ), 2, getOrderSize() );
        doInsert( new SegmentOrderMap( 2, 0.75f, 16 ), 2, getOrderSize() );
        doInsert( new SegmentOrderMap( 2, 0.75f, 64 ), 2, getOrderSize() );
    }

    @Test
    public void testConcReplace() {
        doTestReplace( new SegmentOrderMap( 16, 0.75f, 1 ) );
    }

    @Test
    public void testConcReplaceB() {
        doTestReplace( new SegmentOrderMap( 16, 0.75f, 16 ) );
    }

    @Test
    public void testProblem1() {
        doTestFromFile( "data/testOrderMap.txt" );
    }

    @Test
    public void testProblem2() {
        doTestFromFile( "data/testOrderMap2.txt" );
    }

    @Test
    public void testSegmentOrderMapInsertWithResizing() {
        doInsert( new SegmentOrderMap( 2, 0.75f, 1 ), 2, getOrderSize() );
        doInsert( new SegmentOrderMap( 2, 0.75f, 16 ), 2, getOrderSize() );
        doInsert( new SegmentOrderMap( 2, 0.75f, 64 ), 2, getOrderSize() );
    }

    @Test
    public void testSegmentReplace() {
        doTestReplace( new SegmentOrderMap( 16, 0.75f, 1 ) );
    }

    @Test
    public void testSegmentReplaceB() {
        doTestReplace( new SegmentOrderMap( 16, 0.75f, 16 ) );
    }

    @Test
    public void testSimpleOrderMapInsertWithResizing() {
        doInsert( new SimpleOrderMap( 2, 0.75f ), 2, getOrderSize() );
    }

    @Test
    public void testSimpleReplace() {
        doTestReplace( new SimpleOrderMap( 16, 0.75f ) );
    }

    protected int getOrderSize() { return ORDER_SIZE; }

    private void doInsert( OrderMap mapNew, int presize, int orders ) {
        Map<ViewString, OrderImpl> mapOld = new HashMap<>( 2 );

        int numOrders = orders;

        ReusableString buf = new ReusableString();

        for ( int i = 0; i < numOrders; ++i ) {
            OrderImpl order = OMUtils.mkOrder( mkKey( buf, i ), i );

            mapNew.put( order.getClientClOrdIdChain(), order );
            assertTrue( mapNew.containsKey( order.getClientClOrdIdChain() ) );
            mapOld.put( order.getClientClOrdIdChain(), order );
        }

        assertEquals( mapOld.size(), mapNew.size() );

        Set<Entry<ViewString, OrderImpl>> entries = mapOld.entrySet();

        for ( Entry<ViewString, OrderImpl> entry : entries ) {
            ViewString key = entry.getKey();

            assertTrue( mapNew.containsKey( key ) );
            assertTrue( mapNew.containsKey( new ViewString( key ) ) );

            Order order = mapNew.get( key );
            assertSame( entry.getValue(), order );
            assertSame( entry.getValue(), mapNew.get( new ViewString( key ) ) );
        }

        for ( int i = 0; i < numOrders; ++i ) {
            mkKey( buf, i );

            Order oldOrder = mapOld.get( buf );
            Order newOrder = mapNew.get( buf );

            assertNotNull( oldOrder );
            assertNotNull( newOrder );

            assertSame( oldOrder, newOrder );
        }

        mapNew.clear();
    }

    private void doTestFromFile( String file ) {
        OrderMap                   mapNew = new SegmentOrderMap( 3000, 0.75f, 1 );
        Map<ViewString, OrderImpl> mapOld = new HashMap<>( 2 );

        BufferedReader reader = null;

        try {
            reader = new BufferedReader( new FileReader( file ) );

            Set<String> keys = new LinkedHashSet<>();

            for ( String line = reader.readLine(); line != null; line = reader.readLine() ) {
                keys.add( line.trim() );
            }

            ReusableString buf = new ReusableString();

            for ( String key : keys ) {
                buf.setValue( key );
                OrderImpl order = OMUtils.mkOrder( buf, 0 );
                mapNew.put( order.getClientClOrdIdChain(), order );
                mapOld.put( order.getClientClOrdIdChain(), order );
            }

            for ( String key : keys ) {
                buf.setValue( key );

                assertTrue( mapNew.containsKey( buf ) );

                Order newOrder = mapNew.get( buf );
                Order oldOrder = mapOld.get( buf );

                assertNotNull( oldOrder );
                assertNotNull( newOrder );

                assertSame( oldOrder, newOrder );
            }

        } catch( Exception e ) {
            fail( e.getMessage() );
        } finally {
            FileUtils.close( reader );
        }
    }

    private void doTestReplace( OrderMap mapNew ) {
        ReusableString buf    = new ReusableString();
        OrderImpl      order1 = OMUtils.mkOrder( mkKey( buf, 1000 ), 1000 );
        OrderImpl      order2 = OMUtils.mkOrder( mkKey( buf, 1001 ), 1001 );
        OrderImpl      order3 = OMUtils.mkOrder( mkKey( buf, 1000 ), 1002 );

        assertNull( mapNew.put( order1.getClientClOrdIdChain(), order1 ) );
        assertNull( mapNew.put( order2.getClientClOrdIdChain(), order2 ) );
        assertSame( order1, mapNew.put( order3.getClientClOrdIdChain(), order3 ) );
        assertSame( order3, mapNew.get( order1.getClientClOrdIdChain() ) );
        assertSame( order3, mapNew.get( order3.getClientClOrdIdChain() ) );
    }

    private ReusableString mkKey( ReusableString buf, int i ) {
        buf.copy( SOME_KEY ).append( 1000000 + i );

        return buf;
    }
}
