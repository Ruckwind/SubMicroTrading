package com.rr.core.lang;

import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rr.core.properties.CoreProps.ERR_DOWNGRADE_FILE;

public class ErrorMsgFilter {

    private static final Logger _log = ConsoleFactory.console( ErrorMsgFilter.class, Level.info );

    private static final List<String> _downGradeTags = getList();

    private static List<String> getList() {
        List<String> list = new ArrayList<>();

        String errDowngradeFile = AppProps.instance().getProperty( ERR_DOWNGRADE_FILE, false, null );

        _log.info( "ErrorMsgFiler about to load patterns from file : " + errDowngradeFile );

        if ( errDowngradeFile != null && errDowngradeFile.length() > 0 ) {
            try {
                FileUtils.read( list, errDowngradeFile, true, true );
            } catch( IOException e ) {
                _log.warn( "Error reading file " + errDowngradeFile + " : " + e.getMessage() );
            }
        }

        return list;
    }

    public static boolean shouldDowngradeToTrace( final ZString msg ) {
        for ( String sub : _downGradeTags ) {
            if ( msg.contains( sub ) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean shouldDowngradeToTrace( final String msg ) {
        if ( msg == null ) return false;

        for ( String sub : _downGradeTags ) {
            if ( msg.contains( sub ) ) {
                return true;
            }
        }

        return false;
    }
}
