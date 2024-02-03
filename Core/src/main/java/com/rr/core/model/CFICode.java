/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * http://www.iotafinance.com/en/Classification-of-Financial-Instrument-codes-CFI-ISO-10962.html
 */

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CFICode implements MultiByteLookup {

    private static final Map<ZString, CFICode> _codes = new ConcurrentHashMap<>( 32, 0.75f );

    public enum Category implements SingleByteLookup {
        Equities( 'E' ),
        CollectiveInvestmentVehicles( 'C' ),
        DebtInstruments( 'D' ),
        EntitlementRights( 'R' ),
        ListedOptions( 'O' ),
        Futures( 'F' ),
        Swaps( 'S' ),
        NonListedAndComplexListedOptions( 'H' ),
        Spot( 'I' ),
        Forwards( 'J' ),
        Strategies( 'K' ),
        Financing( 'L' ),
        ReferentialInstruments( 'T' ),
        Others( 'M' );

        private final byte _catCode;

        public static Category getVal( byte val ) {
            switch( val ) {
            case 'E':
                return Equities;
            case 'C':
                return CollectiveInvestmentVehicles;
            case 'D':
                return DebtInstruments;
            case 'R':
                return EntitlementRights;
            case 'O':
                return ListedOptions;
            case 'F':
                return Futures;
            case 'S':
                return Swaps;
            case 'H':
                return NonListedAndComplexListedOptions;
            case 'I':
                return Spot;
            case 'J':
                return Forwards;
            case 'K':
                return Strategies;
            case 'L':
                return Financing;
            case 'T':
                return ReferentialInstruments;
            }
            return Others;
        }

        Category( final char code ) {
            _catCode = (byte) code;
        }

        @Override public int getID() { return ordinal(); }

        @Override public final byte getVal() {
            return _catCode;
        }
    }
    private final ReusableString _val = new ReusableString( 6 );
    private final Category       _category;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 6; }

    public static CFICode getVal( ZString key ) {
        CFICode val = _codes.get( key );
        if ( val == null ) {
            val = new CFICode( key );
            CFICode prev = _codes.putIfAbsent( val._val, val );

            if ( prev != null ) val = prev;
        }
        return val;
    }

    CFICode( ZString val ) {
        _val.copy( val );

        _category = Category.getVal( val.getByte( 0 ) );
    }

    @Override public int getID() { return Constants.UNSET_INT; }

    @Override public final byte[] getVal() {
        return _val.getBytes();
    }

    public Category getCategory() {
        return _category;
    }
}

