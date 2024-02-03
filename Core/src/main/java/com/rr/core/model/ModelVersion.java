/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public class ModelVersion {

    private byte _major;
    private byte _minor;

    public ModelVersion( byte major, byte minor ) {
        super();
        _major = major;
        _minor = minor;
    }

    public boolean isCompatibleWith( ModelVersion other ) {
        return (_major == other._major && _minor >= other._minor);
    }
}
