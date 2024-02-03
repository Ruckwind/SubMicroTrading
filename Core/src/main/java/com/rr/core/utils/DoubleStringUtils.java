package com.rr.core.utils;

import com.rr.core.lang.Constants;

import java.text.DecimalFormat;

public class DoubleStringUtils {

    private static final ThreadLocal<DecimalFormat> _CASH_FMT = ThreadLocal.withInitial( () -> new DecimalFormat( "###,###,###,###,##0" ) );

    private static final ThreadLocal<DecimalFormat> _DF0 = ThreadLocal.withInitial( () -> new DecimalFormat( "0" ) );
    private static final ThreadLocal<DecimalFormat> _DF1 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.0" ) );
    private static final ThreadLocal<DecimalFormat> _DF2 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.00" ) );
    private static final ThreadLocal<DecimalFormat> _DF3 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.000" ) );
    private static final ThreadLocal<DecimalFormat> _DF4 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.0000" ) );
    private static final ThreadLocal<DecimalFormat> _DF5 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.00000" ) );
    private static final ThreadLocal<DecimalFormat> _DF6 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.000000" ) );
    private static final ThreadLocal<DecimalFormat> _DF7 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.0000000" ) );
    private static final ThreadLocal<DecimalFormat> _DF8 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.00000000" ) );

    private static final ThreadLocal<DecimalFormat> _VDF0 = ThreadLocal.withInitial( () -> new DecimalFormat( "0" ) );
    private static final ThreadLocal<DecimalFormat> _VDF1 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.#" ) );
    private static final ThreadLocal<DecimalFormat> _VDF2 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.##" ) );
    private static final ThreadLocal<DecimalFormat> _VDF3 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.###" ) );
    private static final ThreadLocal<DecimalFormat> _VDF4 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.####" ) );
    private static final ThreadLocal<DecimalFormat> _VDF5 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.#####" ) );
    private static final ThreadLocal<DecimalFormat> _VDF6 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.######" ) );
    private static final ThreadLocal<DecimalFormat> _VDF7 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.#######" ) );
    private static final ThreadLocal<DecimalFormat> _VDF8 = ThreadLocal.withInitial( () -> new DecimalFormat( "0.########" ) );

    public static String doubleToString( final double value, DecimalFormat df ) { return Double.isNaN( value ) ? Double.toString( value ) : df.format( value ); }

    public static DecimalFormat DF0()                                           { return _DF0.get(); }

    public static DecimalFormat DF1()                                           { return _DF1.get(); }

    public static DecimalFormat DF2()                                           { return _DF2.get(); }

    public static DecimalFormat DF3()                                           { return _DF3.get(); }

    public static DecimalFormat DF4()                                           { return _DF4.get(); }

    public static DecimalFormat DF5()                                           { return _DF5.get(); }

    public static DecimalFormat DF6()                                           { return _DF6.get(); }

    public static DecimalFormat DF7()                                           { return _DF7.get(); }

    public static DecimalFormat DF8()                                           { return _DF8.get(); }

    public static DecimalFormat get( int dp ) {
        switch( dp ) {
        case 0:
            return DF0();
        case 1:
            return DF1();
        case 2:
            return DF2();
        case 3:
            return DF3();
        case 4:
            return DF4();
        case 5:
            return DF5();
        case 6:
            return DF6();
        case 7:
            return DF7();
        case 8:
            return DF8();
        default:
            return DF8();
        }
    }

    public static DecimalFormat getVarFmt( double val ) {
        long lval = (long) val;

        return (Math.abs( lval ) & Constants.PRICE_DP_THRESHOLD_MASK_8DP) != 0 ? getVarFmt( Constants.PRICE_DP_S ) : getVarFmt( Constants.PRICE_DP_L );
    }

    public static DecimalFormat getVarFmt( int dp ) {
        switch( dp ) {
        case 0:
            return _VDF0.get();
        case 1:
            return _VDF1.get();
        case 2:
            return _VDF2.get();
        case 3:
            return _VDF3.get();
        case 4:
            return _VDF4.get();
        case 5:
            return _VDF5.get();
        case 6:
            return _VDF6.get();
        case 7:
            return _VDF7.get();
        case 8:
            return _VDF8.get();
        default:
            return _VDF8.get();
        }
    }

    public static DecimalFormat getCashFmt() { return _CASH_FMT.get(); }
}
