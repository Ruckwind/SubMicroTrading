package com.rr.core.model;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.StringUtils;

/**
 * TradeRestrictionFlag - flags stored in int, if use more than 32 flags increase size of type in events
 * <p>
 * flags in priority ordinal ... higher priority has higher ordinal
 * <p>
 * IMPORTANT ... IF CHANGE CHECK   checkInstNotRestricted()
 */

public enum TradeRestrictionFlag {
    NoBuyLong( "cannot buy long" ),
    NoBuyCover( "cannot buy to cover ie cant decrease short position" ),
    NoSellShort( "cannot sell short" ),
    NoSellCover( "cannot sell to cover ie cant decrease long position" ),
    NoNetLong( "net long on position level is NOT allowed.. should flatten" ),
    NoNetShort( "net short on position level is NOT allowed; should flatten" ),
    NoPosition( "neither long nor short; only closing the position during next re-balance is allowed" ),
    Freeze( "flag that has priority over others, once it appears position is frozen and not touched" ),
    Deprecated( "instrument is no longer active" );

    private static final Logger _log = LoggerFactory.create( TradeRestrictionFlag.class );
    private final        int    _bitMaskOn;
    private final        int    _bitMaskOff;
    private final        String _desc;

    public static String toString( final int flags ) {
        String s = "";

        for ( TradeRestrictionFlag f : values() ) {
            if ( isOn( flags, f ) ) {
                if ( s.length() > 0 ) {
                    s = s + ",";
                }

                s = s + f.name();
            }
        }

        return s;
    }

    public static int setFlag( int curFlags, TradeRestrictionFlag flag, boolean setOn ) {
        return (setOn) ? (curFlags | flag._bitMaskOn)
                       : (curFlags & flag._bitMaskOff);
    }

    public static boolean isOn( int flags, TradeRestrictionFlag flag ) {
        return (flags & flag._bitMaskOn) > 0;
    }

    public static Object procRestrictCodes( final Object e ) {
        String s = e.toString();

        int flags = 0;

        String[] bits = StringUtils.split( s, '|' );

        for ( String bit : bits ) {

            try {
                bit = bit.trim();

                TradeRestrictionFlag f = TradeRestrictionFlag.valueOf( bit );

                flags = TradeRestrictionFlag.setFlag( flags, f, true );

            } catch( IllegalArgumentException ex ) {
                _log.warn( "procRestrictCodes BAD code " + bit );
            }
        }

        return flags;
    }

    TradeRestrictionFlag( String desc ) {
        _desc       = desc;
        _bitMaskOn  = 1 << ordinal();
        _bitMaskOff = ~_bitMaskOn;
    }
}
