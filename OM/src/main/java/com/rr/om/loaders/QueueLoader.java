/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.collections.*;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

/**
 * use BlockingFixedSize where dont want cpu spinning and queue needs to be fixed size to stop OOM
 * <p>
 * use RingBuffer1C where multiple produces and single consumer and want spinning locks for lowest latency .. BEWARE MISUSE AND SYSTEM CAN LOCK UP WITH SPINNING CPU's
 * <p>
 * use BlockingSync where consumers wait if queue empty and not worried about queue growing to potential OOM
 */

public class QueueLoader implements SMTSingleComponentLoader {

    private static final Logger _log = LoggerFactory.create( QueueLoader.class );

    private String  _type               = null;
    private boolean _enableSendSpinLock = false;
    private int     _queuePresize       = 1024;
    private boolean _fairness           = false;

    /**
     * factory utility method for dynamic queue creation
     *
     * @param maxEventQueueSize
     * @param maxFixedQueue
     * @return
     */
    public static EventQueue create( final String id, final int maxEventQueueSize, final int maxFixedQueue ) {
        EventQueue eventQueue;

        if ( Utils.isNull( maxEventQueueSize ) || maxEventQueueSize < 0 || maxEventQueueSize > maxFixedQueue ) {
            eventQueue = new ConcLinkedEventQueueSingle( id + "EventQueue" );
        } else {
            eventQueue = new RingBufferEventQueueSingleConsumer( maxEventQueueSize );
        }

        return eventQueue;
    }

    @Override
    public SMTComponent create( String id ) {

        boolean useSpinLocks = _enableSendSpinLock;

        EventQueue queue;

        if ( _type != null ) {

            if ( _type.equals( "SlowNonBlockingSync" ) ) {
                queue = new SlowNonBlockingYieldingSyncQueue( id );
            } else if ( _type.equals( "NonBlockingSync" ) ) {
                queue = new NonBlockingSyncQueue( id );
            } else if ( _type.equalsIgnoreCase( "UNSAFE" ) ) {
                queue = new SimpleEventQueue( id );
            } else if ( _type.equalsIgnoreCase( "SunCAS" ) ) {
                queue = new JavaConcEventQueue( id );
            } else if ( _type.equalsIgnoreCase( "SMTCAS" ) ) {
                queue = new ConcLinkedEventQueueSingle( id );
            } else if ( _type.equalsIgnoreCase( "BlockingReusableLinkedQueue" ) ) {
                queue = new ConcBlockingSyncLinkedQueue( id );
            } else if ( _type.equalsIgnoreCase( "BlockingSync" ) ) {
                queue = new BlockingSyncQueue( id );
            } else if ( _type.equalsIgnoreCase( "BlockingFixedSize" ) ) {
                queue = new ArrayBlockingEventQueue( id, _queuePresize, _fairness );
            } else if ( _type.equalsIgnoreCase( "RingBuffer1P1C" ) ) {
                queue = new RingBufferEventQueue1C1P( id, _queuePresize );
            } else if ( _type.equalsIgnoreCase( "RingBuffer1C" ) ) {
                _log.warn( "Overriding config use of RingBuffer1C with ArrayBlockingMessageQueue" );
                queue = new ArrayBlockingEventQueue( id, _queuePresize, false );
            } else {
                throw new SMTRuntimeException( "Configured 'queue' property of " + _type + ", not valid must be one of " +
                                               " [SlowNonBlockingSync|NonBlockingSync|SunCAS|SMTCAS|BlockingSync|RingBuffer1C|RingBuffer1P1C]" );
            }
        } else {
            if ( useSpinLocks ) {
                queue = new ConcLinkedEventQueueSingle();
            } else {
                queue = new BlockingSyncQueue();
            }
        }

        _log.info( "QUEUE: Using " + queue.getClass().getSimpleName() + " for " + id );

        return queue;
    }
}
