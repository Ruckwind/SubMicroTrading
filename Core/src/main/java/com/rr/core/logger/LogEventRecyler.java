package com.rr.core.logger;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.recycler.LogEventHugeRecycler;
import com.rr.core.recycler.LogEventLargeRecycler;
import com.rr.core.recycler.LogEventSmallRecycler;

public class LogEventRecyler {

    private static ThreadLocal<ReusableString> _stackTrace = ThreadLocal.withInitial( () -> {
        ReusableString str = new ReusableString( 1024 );
        return str;
    } );

    private static ThreadLocal<LogEventSmallRecycler> _localEventSmallRecycler = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventSmall> sp   = SuperpoolManager.instance().getSuperPool( LogEventSmall.class );
        LogEventSmallRecycler    pool = new LogEventSmallRecycler( sp.getChainSize(), sp );
        return pool;
    } );

    private static ThreadLocal<LogEventLargeRecycler> _localEventLargeRecycler = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventLarge> sp   = SuperpoolManager.instance().getSuperPool( LogEventLarge.class );
        LogEventLargeRecycler    pool = new LogEventLargeRecycler( sp.getChainSize(), sp );
        return pool;
    } );

    private static ThreadLocal<LogEventHugeRecycler> _localEventHugeRecycler = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventHuge> sp   = SuperpoolManager.instance().getSuperPool( LogEventHuge.class );
        LogEventHugeRecycler    pool = new LogEventHugeRecycler( sp.getChainSize(), sp );
        return pool;
    } );

    public static void recycle( LogEvent curEvent ) {
        ReusableType type = curEvent.getReusableType();

        if ( type == CoreReusableType.LogEventSmall ) {
            _localEventSmallRecycler.get().recycle( (LogEventSmall) curEvent );
        } else if ( type == CoreReusableType.LogEventLarge ) {
            _localEventLargeRecycler.get().recycle( (LogEventLarge) curEvent );
        } else if ( type == CoreReusableType.LogEventHuge ) {
            _localEventHugeRecycler.get().recycle( (LogEventHuge) curEvent );
        }
    }
}
