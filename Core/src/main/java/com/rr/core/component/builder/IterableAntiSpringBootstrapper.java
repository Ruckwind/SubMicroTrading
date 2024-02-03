/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.admin.AdminAgent;
import com.rr.core.annotations.SMTPreRestore;
import com.rr.core.annotations.StandAloneThreadedInit;
import com.rr.core.component.*;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsCfgFile;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.os.NativeHooksImpl;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.SnapshotCaretaker;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.thread.RunState;
import com.rr.core.time.StandardTimeUtils;
import com.rr.core.utils.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class IterableAntiSpringBootstrapper {

    /**
     * _restoredSnapshotStartTime is the time that the start of the restored snapshot was taken
     */
    private static long _restoredSnapshotStartTime;

    public static void main( String[] args ) {

    }

    public static Process spawn( List<String> jvmArgs, List<String> args, int spawnIdx ) throws Exception {
        String javaHome  = System.getProperty( "java.home" );
        String javaBin   = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty( "java.class.path" );
        String className = IterableAntiSpringBootstrapper.class.getName();

        List<String> command = new ArrayList<>();
        command.add( javaBin );
        command.addAll( jvmArgs );
        command.add( "-D" + CoreProps.SPAWN_ITERATION + "=" + spawnIdx );
        command.add( "-cp" );
        command.add( classpath );
        command.add( className );
        command.addAll( args );

        ProcessBuilder builder = new ProcessBuilder( command );
        builder.redirectInput();
        Process process = builder.start();
        process.waitFor();

        return process;
    }
}
