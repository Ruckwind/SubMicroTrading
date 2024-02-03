package com.rr.core.utils;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;

import java.text.ParseException;

public class StringParamsHelper {

    public static final  char DELIM                         = ',';
    private static final int  MIN_DEFAULT_PERCENTAGE_BY_VOL = 50;

    public static <T extends Enum, P extends Enum> T getEnum( Class<T> clazz, P key, ZString stratParams ) {
        String strVal = getStrVal( stratParams, key );

        T val = (T) Enum.valueOf( clazz, strVal );

        return val;
    }

    public static <T extends Enum, P extends Enum> void setEnum( T enumVal, P param, ReusableString stratParamsForUpdate ) {

        if ( stratParamsForUpdate.contains( param.name() ) ) {
            change( stratParamsForUpdate, param, enumVal.name() );
        } else if ( enumVal != null ) {
            set( stratParamsForUpdate, param, enumVal.name() );
        }
    }

    public static <P extends Enum> double getDblVal( final ZString stratParams, P param ) {

        int stIdx = stratParams.indexOf( param.name() );

        if ( stIdx == -1 ) return Constants.UNSET_DOUBLE;

        int valIdx = stratParams.indexOf( '=', stIdx );

        int maxIdx = stratParams.length() - 1;

        if ( valIdx == -1 || valIdx == maxIdx ) throw new SMTRuntimeException( "StratParams missing value for " + param.name() );

        ReusableString t = TLC.instance().pop();

        int nextIdx = stratParams.indexOf( DELIM, valIdx );

        if ( nextIdx == -1 ) {
            stratParams.substring( t, valIdx + 1 );
        } else {
            stratParams.substring( t, valIdx + 1, nextIdx );
        }

        try {
            double retVal = DoubleStringUtils.getVarFmt( 6 ).parse( t.toString() ).doubleValue();

            return retVal;

        } catch( ParseException e ) {
            throw new SMTRuntimeException( "getDblVal exception " + e.getMessage() + " on " + stratParams, e );
        } finally {
            TLC.instance().pushback( t );
        }
    }

    public static <P extends Enum> double getDblVal( final ZString stratParams, P param, double defaultVal ) {

        int stIdx = stratParams.indexOf( param.name() );

        if ( stIdx == -1 ) return defaultVal;

        int valIdx = stratParams.indexOf( '=', stIdx );

        int maxIdx = stratParams.length() - 1;

        if ( valIdx == -1 || valIdx == maxIdx ) throw new SMTRuntimeException( "StratParams missing value for " + param.name() );

        ReusableString t = TLC.instance().pop();

        int nextIdx = stratParams.indexOf( DELIM, valIdx );

        if ( nextIdx == -1 ) {
            stratParams.substring( t, valIdx + 1 );
        } else {
            stratParams.substring( t, valIdx + 1, nextIdx );
        }

        try {
            double retVal = DoubleStringUtils.getVarFmt( 6 ).parse( t.toString() ).doubleValue();

            return retVal;

        } catch( ParseException e ) {
            throw new SMTRuntimeException( "getDblVal exception " + e.getMessage() + " on " + stratParams, e );
        } finally {
            TLC.instance().pushback( t );
        }
    }

    public static <P extends Enum> int getIntVal( final ZString stratParams, P param, int defaultVal ) {

        int stIdx = stratParams.indexOf( param.name() );

        if ( stIdx == -1 ) return defaultVal;

        int valIdx = stratParams.indexOf( '=', stIdx );

        int maxIdx = stratParams.length() - 1;

        if ( valIdx == -1 || valIdx == maxIdx ) throw new SMTRuntimeException( "StratParams missing value for " + param.name() );

        ReusableString t = TLC.instance().pop();

        int nextIdx = stratParams.indexOf( DELIM, valIdx );

        if ( nextIdx == -1 ) {
            stratParams.substring( t, valIdx + 1 );
        } else {
            stratParams.substring( t, valIdx + 1, nextIdx );
        }

        int retVal = StringUtils.parseInt( t );

        TLC.instance().pushback( t );

        return retVal;
    }

    public static <P extends Enum> boolean getBoolVal( final ZString stratParams, P param, boolean defaultVal ) {

        int stIdx = stratParams.indexOf( param.name() );

        if ( stIdx == -1 ) return defaultVal;

        int valIdx = stratParams.indexOf( '=', stIdx );

        int maxIdx = stratParams.length() - 1;

        if ( valIdx == -1 || valIdx == maxIdx ) throw new SMTRuntimeException( "StratParams missing value for " + param.name() );

        ReusableString t = TLC.instance().pop();

        int nextIdx = stratParams.indexOf( DELIM, valIdx );

        if ( nextIdx == -1 ) {
            stratParams.substring( t, valIdx + 1 );
        } else {
            stratParams.substring( t, valIdx + 1, nextIdx );
        }

        boolean retVal = StringUtils.parseBoolean( t );

        TLC.instance().pushback( t );

        return retVal;
    }

    public static <P extends Enum> String getStrVal( final ZString stratParams, P param ) {

        int stIdx = stratParams.indexOf( param.name() );

        if ( stIdx == -1 ) return null;

        int valIdx = stratParams.indexOf( '=', stIdx );

        int maxIdx = stratParams.length() - 1;

        if ( valIdx == -1 || valIdx == maxIdx ) throw new SMTRuntimeException( "StratParams missing value for " + param );

        ReusableString t = TLC.instance().pop();

        int nextIdx = stratParams.indexOf( DELIM, valIdx );

        if ( nextIdx == -1 ) {
            stratParams.substring( t, valIdx + 1 );
        } else {
            stratParams.substring( t, valIdx + 1, nextIdx );
        }

        String retVal = t.toString();

        TLC.instance().pushback( t );

        return retVal;
    }

    public static <P extends Enum> void setInt( int iVal, P param, ReusableString stratParamsForUpdate ) {

        String maxPercentStr = Integer.toString( iVal );

        if ( stratParamsForUpdate.contains( param.name() ) ) {
            change( stratParamsForUpdate, param, maxPercentStr );
        } else {
            set( stratParamsForUpdate, param, maxPercentStr );
        }
    }

    public static <P extends Enum> void setString( String strVal, P param, ReusableString stratParamsForUpdate ) {

        if ( stratParamsForUpdate.contains( param.name() ) ) {
            change( stratParamsForUpdate, param, strVal );
        } else {
            set( stratParamsForUpdate, param, strVal );
        }
    }

    public static <P extends Enum> void setBool( boolean boolVal, P param, ReusableString stratParamsForUpdate ) {

        String boolStr = boolVal ? "Y" : "N";

        if ( stratParamsForUpdate.contains( param.name() ) ) {
            change( stratParamsForUpdate, param, boolStr );
        } else {
            set( stratParamsForUpdate, param, boolStr );
        }
    }

    public static <P extends Enum> void setDouble( double dVal, P param, ReusableString stratParamsForUpdate ) {

        if ( Utils.isNull( dVal ) ) {
            if ( stratParamsForUpdate.contains( param.name() ) ) {
                remove( stratParamsForUpdate, param.name() );
            }

            return;
        }

        String strVal = DoubleStringUtils.getVarFmt( 6 ).format( dVal );

        if ( stratParamsForUpdate.contains( param.name() ) ) {
            change( stratParamsForUpdate, param, strVal );
        } else {
            set( stratParamsForUpdate, param, strVal );
        }
    }

    public static void set( String strVal, String param, ReusableString stratParamsForUpdate ) {

        if ( stratParamsForUpdate.contains( param ) ) {
            change( stratParamsForUpdate, param, strVal );
        } else {
            set( stratParamsForUpdate, param, strVal );
        }
    }

    public static void set( ZString strVal, String param, ReusableString stratParamsForUpdate ) {

        if ( stratParamsForUpdate.contains( param ) ) {
            change( stratParamsForUpdate, param, strVal );
        } else {
            set( stratParamsForUpdate, param, strVal );
        }
    }

    private static <P extends Enum> void set( final ReusableString stratParamsForUpdate, final P param, final String val ) {
        set( stratParamsForUpdate, param.name(), val );
    }

    private static <P extends Enum> void change( ReusableString stratParamsForUpdate, P param, final String val ) {
        change( stratParamsForUpdate, param.name(), val );
    }

    private static void set( final ReusableString stratParamsForUpdate, final String param, final String val ) {
        if ( stratParamsForUpdate.length() > 0 ) {
            stratParamsForUpdate.append( DELIM );
        }
        stratParamsForUpdate.append( param ).append( '=' ).append( val );
    }

    private static void change( ReusableString stratParamsForUpdate, String param, final String val ) {
        ReusableString t = TLC.instance().pop();

        int stIdx = stratParamsForUpdate.indexOf( param );

        stratParamsForUpdate.substring( t, 0, stIdx );

        set( t, param, val );

        int nextIdx = stratParamsForUpdate.indexOf( DELIM, stIdx );

        if ( nextIdx > stIdx ) {
            stratParamsForUpdate.substring( t, nextIdx );
        }

        stratParamsForUpdate.copy( t );

        TLC.instance().pushback( t );
    }

    private static void set( final ReusableString stratParamsForUpdate, final String param, final ZString val ) {
        if ( stratParamsForUpdate.length() > 0 ) {
            stratParamsForUpdate.append( DELIM );
        }
        stratParamsForUpdate.append( param ).append( '=' ).append( val );
    }

    private static void change( ReusableString stratParamsForUpdate, String param, final ZString val ) {
        ReusableString t = TLC.instance().pop();

        int stIdx = stratParamsForUpdate.indexOf( param );

        stratParamsForUpdate.substring( t, 0, stIdx );

        set( t, param, val );

        int nextIdx = stratParamsForUpdate.indexOf( DELIM, stIdx );

        if ( nextIdx > stIdx ) {
            stratParamsForUpdate.substring( t, nextIdx );
        }

        stratParamsForUpdate.copy( t );

        TLC.instance().pushback( t );
    }

    private static void remove( ReusableString stratParamsForUpdate, String param ) {
        ReusableString t = TLC.instance().pop();

        int stIdx = stratParamsForUpdate.indexOf( param );

        if ( stIdx == -1 ) return;

        stratParamsForUpdate.substring( t, 0, stIdx );

        int nextIdx = stratParamsForUpdate.indexOf( DELIM, stIdx );

        if ( nextIdx > stIdx ) {
            stratParamsForUpdate.substring( t, nextIdx );
        }

        stratParamsForUpdate.copy( t );

        TLC.instance().pushback( t );
    }
}
