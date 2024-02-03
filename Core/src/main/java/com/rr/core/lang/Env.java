package com.rr.core.lang;

import com.rr.core.properties.AppProps;

public enum Env {
    DS( false ),             // Data Servers
    BKUP( false ),           // Backup Server
    RESEARCH( false ),       // Research Servers
    CORE( true ),            // CORE common production ... eg ref data master
    DEV( false ),            // running out of IDEA with minimal checked in data
    LOCAL( false ),          // running ENV on localhost outside of IDEA
    TEST( false ),           // unit testing
    PERF( false ),           // perf testing
    UAT( false ),            // user acceptance testing in cloud
    BACKTEST( false ),       // backtest
    PROD1( true );           // production

    private final boolean _isProd;

    public static boolean isProdOrUAT( Env env ) {
        return env.isProd() || env == Env.UAT;
    }

    public static boolean isBacktest() {
        return ( Env.BACKTEST == AppProps.instance().getEnv() );
    }

    public static boolean isProdOrUAT() {
        Env env = AppProps.instance().getEnv();
        return env.isProd() || env == Env.UAT;
    }

    public static boolean isProduction() {
        Env env = AppProps.instance().getEnv();
        return env.isProd();
    }

    public static boolean isProdOrDevOrUAT() {
        Env env = AppProps.instance().getEnv();
        return env.isProd() || env == Env.DEV || env == Env.UAT;
    }

    public static boolean isProdOrDev() {
        Env env = AppProps.instance().getEnv();
        return env.isProd() || env == Env.DEV;
    }

    public static boolean isDevOrUAT() {
        Env env = AppProps.instance().getEnv();
        return env == Env.DEV || env == Env.UAT;
    }

    public static boolean isDev() {
        Env env = AppProps.instance().getEnv();
        return env == Env.DEV;
    }

    Env( final boolean isProd ) { _isProd = isProd; }

    public boolean isProd()     { return _isProd; }
}
