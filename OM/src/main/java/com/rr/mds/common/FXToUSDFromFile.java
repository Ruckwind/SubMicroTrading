/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Currency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FXToUSDFromFile {

    public static final ZString DEFAULT_FX_TO_USD_FILE = new ViewString( "./var/daily/fxrates.txt" );
    private static final Logger _log = LoggerFactory.create( FXToUSDFromFile.class );
    private static final ErrorCode CANT_READ_FILE = new ErrorCode( "FXF100", "Unable to read the FX file" );

    public static void load( ZString fileName ) {
        if ( fileName == null ) fileName = DEFAULT_FX_TO_USD_FILE;

        _log.info( "Setting FX from " + fileName );

        for ( Currency ccy : Currency.values() ) {
            ccy.setUSDFactor( Double.NaN );
        }

        File file = new File( fileName.toString() );

        if ( !file.canRead() ) {
            _log.error( CANT_READ_FILE, fileName );

            throw new RuntimeException( "Cant read FX from file" );
        }

        try {

            ReusableString str = new ReusableString();

            try( BufferedReader input = new BufferedReader( new FileReader( file ) ) ) {
                String line;
                while( (line = input.readLine()) != null ) {
                    if ( line.charAt( 0 ) == '#' ) continue;

                    String[] entries = line.split( " " );

                    if ( entries.length != 2 ) {
                        _log.warn( "FXFile line has too many entries " + line );
                    }

                    String ccyStr = entries[ 0 ].trim();

                    str.setValue( ccyStr.getBytes(), 0, ccyStr.length() );
                    Currency ccy = Currency.getVal( str );

                    if ( ccy != null ) {
                        double rate = Double.parseDouble( entries[ 1 ] );

                        _log.info( "Setting FX for " + ccy.toString() + " to USD is " + rate );

                        ccy.setUSDFactor( rate );
                    }
                }

                Currency.Other.setUSDFactor( 1.0 );
                Currency.Unknown.setUSDFactor( 1.0 );
            }
        } catch( IOException e ) {
            _log.warn( "Unable to read file " + fileName + " : " + e.getMessage() );
            throw new RuntimeException( e );
        }

        for ( Currency ccy : Currency.values() ) {
            if ( ccy.toUSDFactor() == Double.NaN ) {
                _log.warn( "FX to USD missing entry for " + ccy.toString() );

                throw new RuntimeException( "FX to USD missing entry for " + ccy.toString() );
            }
        }
    }
}
