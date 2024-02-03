package com.rr.core.recovery.json;

import com.rr.core.collections.ArrayBlockingEventQueue;
import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.Env;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.LogEventSmall;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.ArrayBlockingEventQueueJSONCodec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class TestJSONArrayBlockingEventQueue extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONArrayBlockingEventQueue.class, Level.info );

    @Before public void setup() throws Exception {
        super.setup();

        backTestReset( Env.BACKTEST );
    }

    @Test public void emptyQ() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        ArrayBlockingEventQueue list = new ArrayBlockingEventQueue( "Q", 10, false );

        final ArrayBlockingEventQueueJSONCodec coder = new ArrayBlockingEventQueueJSONCodec();

        coder.encode( dataWriter, list, 10 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 10,\n"
                                 + "\t\"@class\" : \"com.rr.core.collections.ArrayBlockingEventQueue\",\n"
                                 + "\t\"@smtId\" : \"Q\",\n"
                                 + "\t\"capacity\" : 10,\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object coll = reader.jsonToObject();

        assertNotNull( coll );
        assertTrue( coll instanceof ArrayBlockingEventQueue );
        assertSame( ArrayBlockingEventQueue.class, coll.getClass() );

        @SuppressWarnings( "unchecked" )
        ArrayBlockingEventQueue l = (ArrayBlockingEventQueue) coll;
        assertEquals( 0, l.size() );
        assertEquals( 10, l.maxCapacity() );
    }

    @Test public void queueOfThree() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        ArrayBlockingEventQueue list = new ArrayBlockingEventQueue( "Q", 10, false );

        list.add( new LogEventSmall( "aaa" ) );
        list.add( new LogEventSmall( "bbb" ) );
        list.add( new LogEventSmall( "ccc" ) );

        final ArrayBlockingEventQueueJSONCodec coder = new ArrayBlockingEventQueueJSONCodec();

        coder.encode( dataWriter, list, 10 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 10,\n"
                                 + "\t\"@class\" : \"com.rr.core.collections.ArrayBlockingEventQueue\",\n"
                                 + "\t\"@smtId\" : \"Q\",\n"
                                 + "\t\"capacity\" : 10,\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 1,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.logger.LogEventSmall\",\n"
                                 + "\t\t\t\"_level\" : {\n"
                                 + "\t\t\t\t\"@enum\" : \"com.rr.core.logger.Level.info\"\n"
                                 + "\t\t\t},\n"
                                 + "\t\t\t\"_time\" : 0,\n"
                                 + "\t\t\t\"_buf\" : \"aaa\",\n"
                                 + "\t\t\t\"_localTZ\" : null,\n"
                                 + "\t\t\t\"_customLogArgs\" : null,\n"
                                 + "\t\t\t\"_next\" : null,\n"
                                 + "\t\t\t\"_nextMessage\" : null,\n"
                                 + "\t\t\t\"_msgSeqNum\" : null,\n"
                                 + "\t\t\t\"_eventTimestamp\" : null,\n"
                                 + "\t\t\t\"_flags\" : 0,\n"
                                 + "\t\t\t\"_messageHandler\" : null\n"
                                 + "\t\t},\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.logger.LogEventSmall\",\n"
                                 + "\t\t\t\"_level\" : {\n"
                                 + "\t\t\t\t\"@enum\" : \"com.rr.core.logger.Level.info\"\n"
                                 + "\t\t\t},\n"
                                 + "\t\t\t\"_time\" : 0,\n"
                                 + "\t\t\t\"_buf\" : \"bbb\",\n"
                                 + "\t\t\t\"_localTZ\" : null,\n"
                                 + "\t\t\t\"_customLogArgs\" : null,\n"
                                 + "\t\t\t\"_next\" : null,\n"
                                 + "\t\t\t\"_nextMessage\" : null,\n"
                                 + "\t\t\t\"_msgSeqNum\" : null,\n"
                                 + "\t\t\t\"_eventTimestamp\" : null,\n"
                                 + "\t\t\t\"_flags\" : 0,\n"
                                 + "\t\t\t\"_messageHandler\" : null\n"
                                 + "\t\t},\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 3,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.logger.LogEventSmall\",\n"
                                 + "\t\t\t\"_level\" : {\n"
                                 + "\t\t\t\t\"@enum\" : \"com.rr.core.logger.Level.info\"\n"
                                 + "\t\t\t},\n"
                                 + "\t\t\t\"_time\" : 0,\n"
                                 + "\t\t\t\"_buf\" : \"ccc\",\n"
                                 + "\t\t\t\"_localTZ\" : null,\n"
                                 + "\t\t\t\"_customLogArgs\" : null,\n"
                                 + "\t\t\t\"_next\" : null,\n"
                                 + "\t\t\t\"_nextMessage\" : null,\n"
                                 + "\t\t\t\"_msgSeqNum\" : null,\n"
                                 + "\t\t\t\"_eventTimestamp\" : null,\n"
                                 + "\t\t\t\"_flags\" : 0,\n"
                                 + "\t\t\t\"_messageHandler\" : null\n"
                                 + "\t\t}\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object coll = reader.jsonToObject();

        assertNotNull( coll );
        assertTrue( coll instanceof ArrayBlockingEventQueue );
        assertSame( ArrayBlockingEventQueue.class, coll.getClass() );

        @SuppressWarnings( "unchecked" )
        ArrayBlockingEventQueue l = (ArrayBlockingEventQueue) coll;
        assertEquals( 3, l.size() );
        assertEquals( 10, l.maxCapacity() );

        assertEquals( "aaa", ((LogEventSmall) (l.next())).getMessage().toString() );
        assertEquals( "bbb", ((LogEventSmall) (l.next())).getMessage().toString() );
        assertEquals( "ccc", ((LogEventSmall) (l.next())).getMessage().toString() );
    }

    @After public void tearDown() {
        backTestReset( Env.TEST );
    }
}
