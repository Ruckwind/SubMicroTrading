/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.os;

import com.rr.core.lang.JavaSystemProperties;

public class NativeHooksImpl implements NativeHooks {

    private static boolean _linuxNative   = false;
    private static boolean _windowsNative = false;
    private static NativeHooks _instance = new NativeHooksImpl();

    static {
        if ( JavaSystemProperties.isUseLinuxNative() ) {
            System.loadLibrary( "submicrocore" );
            _linuxNative = true;
        }
    }

    static {
        if ( JavaSystemProperties.isUseWindowsNative() ) {
            System.loadLibrary( "submicrocore" );
            _windowsNative = true;
        }
    }

    public static NativeHooks instance() { return _instance; }

    private static native void jniCalibrateTicks();

    private static native void jniSetPriority( int mask, int priority );

    private static native void jniSleep( int ms );

    private static native void jniSleepMicros( int micros );

    private static native void jniSetProcessMaxPriority();

    private static native long jniNanoTimeRDTSC();

    private static native long jniNanoTimeMonotonicRaw();

    private static native long jniNanoRDTSCStart();

    private static native long jniNanoRDTSCStop();

    public NativeHooksImpl() {
        if ( _linuxNative || _windowsNative ) {
            jniCalibrateTicks();
        }
    }

    @Override
    public long nanoTimeMonotonicRaw() {
        if ( _linuxNative || _windowsNative ) {
            return jniNanoTimeMonotonicRaw();
        }
        return System.nanoTime();
    }

    @Override
    public long nanoTimeRDTSC() {
        if ( _linuxNative || _windowsNative ) {
            return jniNanoTimeRDTSC();
        }
        return System.nanoTime();
    }

    @Override
    public void setPriority( Thread thread, int mask, int priority ) {
        if ( priority == MAX_PRIORITY ) {
            setProcessMaxPriority();
        }
        if ( _linuxNative ) {
            jniSetPriority( mask, priority );
        } else {
            thread.setPriority( priority );
        }
    }

    @Override
    public void setProcessMaxPriority() {
        if ( _linuxNative || _windowsNative ) {
            jniSetProcessMaxPriority();
        }
    }

    @Override
    public void sleep( int ms ) {
        if ( ms < 10 ) {                // if sleeping more than 10ms dont care about accuracy that much
            if ( _linuxNative || _windowsNative ) {
                jniSleep( ms );
            } else {
                try {
                    Thread.sleep( ms );
                } catch( InterruptedException e ) {
                    // ignore
                }
            }
        } else {
            try {
                Thread.sleep( ms );
            } catch( InterruptedException e ) {
                // ignore
            }
        }
    }

    @Override
    public void sleepMicros( int micros ) {
        if ( _linuxNative || _windowsNative ) {
            jniSleepMicros( micros );
        } else {
            try {
                Thread.sleep( (micros >> 10) + 1 ); // rough estimate
            } catch( InterruptedException e ) {
                // ignore
            }
        }
    }
}
