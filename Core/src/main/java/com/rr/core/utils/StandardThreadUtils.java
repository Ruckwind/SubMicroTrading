/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.Procedure;
import com.rr.core.os.NativeHooks;
import com.rr.core.os.NativeHooksImpl;

import java.io.BufferedReader;
import java.io.FileReader;

public class StandardThreadUtils implements ThreadUtils {

    @Override public void init( String fileName ) {

        if ( fileName == null ) {
            return;
        }

        // @NOTE THIS CODE MUST NOT USE LOGGING BUT SYSTEM.OUT AS THIS CODE WILL BE USED BEFORE POOLING SETUP
        BufferedReader reader = null;

        synchronized( StandardThreadUtils.class ) {
            try {
                System.out.println( "ThreadUtils loading thread masks from " + fileName );

                reader = new BufferedReader( new FileReader( fileName ) );

                for ( String line = reader.readLine(); line != null; line = reader.readLine() ) {

                    if ( line.startsWith( "#" ) ) continue;

                    String req = line.trim();

                    if ( req.length() > 0 ) {
                        String[] split = req.split( "\\s+" );

                        if ( split.length == 3 ) {
                            ThreadPriority p = ThreadPriority.valueOf( split[ 0 ] );

                            if ( p != null ) {
                                p.setPriority( Integer.parseInt( split[ 1 ] ) );
                                p.setMask( Integer.parseInt( split[ 2 ] ) );

                                System.out.println( "ThreadUtils set " + split[ 0 ] + " to priority " + split[ 1 ] + ", mask " + split[ 2 ] );
                            }
                        } else {
                            System.out.println( "ThreadUtilsFactory.get().init skip bad line with " + split.length + " parts : " + line );
                        }
                    }
                }
            } catch( Exception e ) {
                System.out.println( "Failed to process Thread Mask file " + fileName + " : " + e.getMessage() );
            } finally {
                FileUtils.close( reader );
            }
        }

        sendStarted();
    }

    @Override public void init( String fileName, boolean isDebug ) {

        for ( ThreadPriority priority : ThreadPriority.values() ) {
            priority.setMask( SchedulingPriority.UNKNOWN_MASK );
        }

        init( fileName );

        ThreadPriority other = ThreadPriority.Other;

        for ( ThreadPriority priority : ThreadPriority.values() ) {
            if ( priority.getMask() == SchedulingPriority.UNKNOWN_MASK ) {
                System.out.println( "WARNING: no config entry for " + priority.toString() + ", setting cpu mask to OTHER : " + other.getMask() );
                priority.setMask( other.getMask() );
            }
        }
    }

    @Override public void setPriority( Thread thread, ThreadPriority priority ) {

        int javaPriority = 5;

        switch( priority.getPriority() ) {
        case ThreadPriority.LOWEST:
            javaPriority = 1;
            break;
        case ThreadPriority.LOW:
            javaPriority = 3;
            break;
        case ThreadPriority.MEDIUM:
            javaPriority = 5;
            break;
        case ThreadPriority.HIGH:
            javaPriority = 8;
            break;
        case ThreadPriority.HIGHEST:
            javaPriority = NativeHooks.MAX_PRIORITY;
            break;
        }

        int mask = findMask( priority );

        if ( mask > 0 ) {
            System.out.println( "ThreadUtilsFactory.get().setPriority() thread " + thread.getName() + ", priority " + priority.toString() +
                                ", javaPriority=" + javaPriority + ", cpumask=" + mask );

            System.out.flush();

            NativeHooksImpl.instance().setPriority( thread, mask, javaPriority );
        } else {
            System.out.println( "ThreadUtilsFactory.get().setPriority() *** NO CPU MASK ***  for thread " + thread.getName() + ", priority " + priority.toString() +
                                ", javaPriority=" + javaPriority + ", cpumask=" + mask );

            System.out.flush();
        }
    }

    @Override public void setPriority( Thread thread, int priority, int mask ) {

        if ( mask > 0 ) {
            int javaPriority = 5;

            switch( priority ) {
            case ThreadPriority.LOWEST:
                javaPriority = 1;
                break;
            case ThreadPriority.LOW:
                javaPriority = 3;
                break;
            case ThreadPriority.MEDIUM:
                javaPriority = 5;
                break;
            case ThreadPriority.HIGH:
                javaPriority = 8;
                break;
            case ThreadPriority.HIGHEST:
                javaPriority = NativeHooks.MAX_PRIORITY;
                break;
            }

            //TODO use JNI to force priority

            System.out.println( "ThreadUtilsFactory.get().setPriority() thread " + thread.getName() + ", priority " + priority +
                                ", javaPriority=" + javaPriority + ", cpumask=" + mask );

            System.out.flush();

            NativeHooksImpl.instance().setPriority( thread, mask, javaPriority );
        }
    }

    @Override public void sleep( int ms ) {
        if ( ms <= 0 ) return;

        NativeHooksImpl.instance().sleep( ms );
    }

    @Override public void sleepMicros( int micros ) {
        NativeHooksImpl.instance().sleepMicros( micros );
    }

    @Override public void waitFor( Object delayLock, int delayIntervalMS ) {
        synchronized( delayLock ) {
            try {
                delayLock.wait( delayIntervalMS );
            } catch( InterruptedException e ) {
                // ignore
            }
        }
    }

    @Override public void delayedRun( String id, int millisToDelay, Procedure procToRun ) {
        Thread t = new Thread( new Runnable() {

            @Override public void run() {
                ThreadUtilsFactory.get().sleep( millisToDelay );
                procToRun.invoke();
            }

        }, id );

        t.setDaemon( true );
        t.start();
    }

    private int findMask( ThreadPriority priority ) {
        int mask = priority.getMask();

        // note removed all clever mask generation, must now rely on mask file

        return mask;
    }

    private void sendStarted() {
        // @TODO generate email for sys start
    }

}
