package com.rr.core.properties;

import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;

import java.util.Map;

public class PropUtils {

    public static boolean getBooleanProperty( Map<String, String> props, String key, boolean defaultVal ) {
        String val = props.get( key );

        if ( val != null ) {
            boolean bVal;

            try {
                bVal = StringUtils.parseBoolean( val ) || "1".equals( val );
            } catch( NumberFormatException e ) {
                throw new SMTRuntimeException( "PropUtils boolena property " + key + " has invalid boolean (" + val + ")" );
            }

            return bVal;
        }

        return defaultVal;
    }
}
