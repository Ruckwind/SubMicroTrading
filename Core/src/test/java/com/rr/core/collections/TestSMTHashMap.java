package com.rr.core.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.rr.core.recovery.json.JSONClassDefinition._log;
import static org.junit.Assert.*;

public class TestSMTHashMap extends BaseTestCase {

    @Test public void clearRecycle() {
        SMTHashMap<ReusableString, ReusableString> map = new SMTHashMap<>( 2 );

        PoolFactory<ReusableString> pool     = SuperpoolManager.instance().getSuperPool( ReusableString.class ).getPoolFactory();
        Recycler<ReusableString>    recycler = SuperpoolManager.instance().getRecycler( ReusableString.class );

        for ( int j = 0; j < 1000; j++ ) {
            for ( int i = 0; i < 125; i++ ) {

                ReusableString key1 = new ReusableString( "kkkkkkkk" + i );
                ReusableString key2 = new ReusableString( "zzzzzzzz" + i );

                ReusableString val = pool.get();

                assertTrue( !map.containsValue( val ) );

                val.copy( "val" + i );

                map.put( key1, val );
                map.put( key2, val );

                assertTrue( map.containsKey( key1 ) );
                assertTrue( map.containsKey( key2 ) );
                assertSame( val, map.get( key1 ) );
                assertSame( val, map.get( key2 ) );
            }

            map.clear();
        }
    }

    @Test
    public void mapGrowth() {
        SMTHashMap<ZString, String> map = new SMTHashMap<>( 2 );

        for ( int i = 0; i < 1000000; i++ ) {

            ReusableString key = new ReusableString( "kkkkkkkk" + i );
            String         val = "aaa" + i;

            map.put( key, val );

            assertTrue( map.containsKey( key ) );
            assertSame( val, map.get( key ) );
        }
    }

    @Test
    public void matchOthgerZStringKey() {
        SMTHashMap<ZString, String> map = new SMTHashMap<>( 10 );

        ReusableString key = new ReusableString( "27069" );

        map.put( TLC.safeCopy( key ), "aaaa" );

        assertTrue( map.containsKey( key ) );
    }

    @Test public void orderIds() {
        String ids[] = {
                "DOPPELGANGER_US_FUT_CLX1199111_2351318_200",
                "DOPPELGANGER_US_FUT_ZNZ1199112_2350776_197",
                "DOPPELGANGER_US_FUT_ZNZ1199112_2351307_198",
                "DOPPELGANGER_US_FUT_ZNZ1199112_2351831_203",
                "1000371",
                "DOPPELGANGER_US_FUT_ZNZ1199112_2351525_201",
                "1000360",
                "1000362",
                "DOPPELGANGER_US_FUT_SPZ1199112_2351327_199",
                "1000364",
                "1000366",
                "1000369",
                "DOPPELGANGER_US_FUT_CLX1199111_2350478_196",
                "DOPPELGANGER_US_FUT_CLX1199111_2351532_202"
        };

        SMTMap<ZString, String> m = new SMTHashMap<>( 128 );

        for ( String id : ids ) {
            m.put( new ReusableString( id ), id );
        }

        assertEquals( ids.length, m.size() );

        for ( Map.Entry<ZString, String> entry : m.entrySet() ) {
            String  order = entry.getValue();
            ZString key   = entry.getKey();

            _log.info( key + "=" + order.toString() );
        }
    }

    @Test public void testRemoveOnIter() {
        Map<ReusableString, Long> map = new HashMap<>();

        for ( long i = 0; i < 1000000; i++ ) {

            ReusableString key = new ReusableString( "kkkkkkkk" + i );

            map.put( key, i );
        }

        Iterator<Map.Entry<ReusableString, Long>> it = map.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<ReusableString, Long> e = it.next();
            it.remove();
        }

        assertTrue( 0 == map.size() );
    }
}
