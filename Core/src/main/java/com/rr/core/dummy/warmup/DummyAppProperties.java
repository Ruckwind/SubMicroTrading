/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dummy.warmup;

import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.properties.PropertyTags;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * DummyAppProperties - for tests
 */
public class DummyAppProperties extends AppProps {

    private static DummyAppProperties instance = null;
    private        PropertyTags       _validNames;

    public synchronized static void testInit( Map<String, String> p ) {
        testInit( p, CoreProps.instance() );
    }

    public synchronized static void testInit( Map<String, String> p, PropertyTags t ) {

        if ( instance != null && instance._validNames != null ) {
            if ( t.getClass().isAssignableFrom( instance._validNames.getClass() ) ) {
                return;
            }
        }

        instance = new DummyAppProperties();

        instance.init( p, t );
    }

    public synchronized static void testInit( final String fileName ) throws Exception {
        AppProps.instance().init( fileName, (String) null );
    }

    public void init( Map<String, String> p, PropertyTags validNames ) {

        if ( _validNames != null ) {
            if ( validNames.getClass().isAssignableFrom( _validNames.getClass() ) ) {
                return;
            }
        }

        _validNames = validNames;

        setPropSet( validNames );
        for ( Entry<String, String> entry : p.entrySet() ) {
            put( entry.getKey(), entry.getValue() );
        }

        override( CoreProps.APP_NAME, "unitTest" );

        resolveProps( new LinkedHashMap<>() );
        setInit();

        AppProps.instance().init( this );
    }
}
