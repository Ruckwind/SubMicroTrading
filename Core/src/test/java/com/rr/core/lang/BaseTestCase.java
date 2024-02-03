/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.hols.HolidayLoader;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadUtilsFactory;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore
public abstract class BaseTestCase {

    public static Env _env = Env.TEST;

    static {
        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        try {
            DummyAppProperties.testInit( Collections.singletonMap( CoreProps.RUN_ENV, _env.name() ) );

        } catch( Exception e ) {
            fail( e.getMessage() );
        }
    }

    @Rule public TestName name = new TestName();

    @SuppressWarnings( "JavaReflectionInvocation" )
    public static void checkGettersHaveSameValue( final Object exp, final Object res, Class<?> chkClass ) throws Exception {

        final Method[] methods = chkClass.getDeclaredMethods();

        final Class<?> expClass = exp.getClass();
        final Class<?> resClass = res.getClass();

        if ( !chkClass.isAssignableFrom( expClass ) ) throw new SMTRuntimeException( "Error expectedClass " + expClass.getName() + " isnt derivable from " + chkClass.getName() );
        if ( !chkClass.isAssignableFrom( resClass ) ) throw new SMTRuntimeException( "Error resultClass " + resClass.getName() + " isnt derivable from " + chkClass.getName() );

        for ( Method m : methods ) {
            String name = m.getName();
            if ( (name.startsWith( "get" ) || name.startsWith( "is" )) && m.getParameterTypes().length == 0 ) {

                Object[] dummyArgs = new Class<?>[ 0 ];

                Method expMethod = expClass.getMethod( name, m.getParameterTypes() );
                Method resMethod = resClass.getMethod( name, m.getParameterTypes() );

                Object expVal = expMethod.invoke( exp, dummyArgs );
                Object resVal = resMethod.invoke( res, dummyArgs );

                assertEquals( "mismatch on field " + name, expVal, resVal );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> List<T> newArrayList( T... vals ) {
        List<T> list = new ArrayList<>();
        for ( T v : vals ) {
            list.add( v );
        }
        return list;
    }

    /**
     * Be very careful that you reset the environment in your {@code tearDown() method.
     *
     * @param env the environment to use
     */
    protected void backTestReset( final Env env ) {
        AppProps.instance().override( CoreProps.RUN_ENV, env.name() );
        ReflectUtils.clearAllThreadLocals();
        StatsMgr.setStats( new TestStats() );
        DateCache.reset();
        ClockFactory.reset();
        ThreadUtilsFactory.reset();
        SchedulerFactory.reset();
        TimeUtilsFactory.reset();
        HolidayLoader.instance().reset();
    }

    protected void setLoopback( final SocketConfig socketConfig ) {

        try {
            NetworkInterface nif = NetworkInterface.getByName( "lo" );
            socketConfig.setNic( new ViewString( "lo" ) );
        } catch( SocketException e ) {
            try {
                NetworkInterface nif = NetworkInterface.getByName( "lo0" );
                socketConfig.setNic( new ViewString( "lo0" ) );
            } catch( SocketException e1 ) {
                fail( "Unable to find loopback adapter" );
            }
        }
    }

}
