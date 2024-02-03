/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

public class Div10FindMultiplier {

    public static void main( String[] args ) {

        find( 1000, 10 );
        find( 25, 10 );
        find( 100, 10 );
        find( 60, 10 );
        find( 10, 10 );
    }

    private static void find( int max, int div ) {
        // System.out.println( "Hunting .... for div 10 for max value " + (max-1) );

        for ( int shift = 1; shift < 32; ++shift ) {

            // System.out.println( "   try shift " + shift );

            for ( int mult = 1; mult < 16384; ++mult ) {

                boolean fail = false;

                for ( int val = 0; !fail && val < max; ++val ) {

                    int div10 = val / div;

                    int test = (val * mult) >> shift;

                    if ( test != div10 ) {
                        fail = true;
                    }
                }

                if ( !fail ) {
                    System.out.println( "To divide a value less than " + max + " by " + div + " first mult by " + mult + " >> " + shift + "\n" );

                    return;
                }
            }
        }

        System.out.println( "No joy" );
    }
}
