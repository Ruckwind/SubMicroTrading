/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.client;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ClientProfile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientProfileManager implements SMTComponent {

    private Map<ZString, ClientProfile> _profs = new ConcurrentHashMap<>();

    private ClientProfile _defaultClientProfile = new OMClientProfileImpl( "DefaultClientProfile", new ViewString( "DefaultClient" ) );
    private ClientProfile _dummyProfile         = new DummyClientProfile();

    private boolean _useDummyProfile = false;
    private String  _id;

    private boolean _allowDefaultClientProfile = false;

    public ClientProfileManager() {
        this( null );
    }

    public ClientProfileManager( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public ClientProfile create( ZString sessName, ZString clientName ) {
        ClientProfile prof = _profs.get( sessName );

        if ( prof == null ) {
            prof = new OMClientProfileImpl( "CP_" + clientName, clientName );
            _profs.put( sessName, prof );
        }

        return prof;
    }

    public ClientProfile get( ZString sessName ) {

        if ( _useDummyProfile ) {
            return _dummyProfile;
        }

        ClientProfile p = _profs.get( sessName );

        if ( p == null && _allowDefaultClientProfile ) {
            return _defaultClientProfile;
        }

        return p;
    }
}
