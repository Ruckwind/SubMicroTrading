/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.io.File;

public class BaseModel {

    private String _modelVersionNumber = "1.0.0";
    private String _rootPackage        = "com.rr.generated";
    private String _dir                = "";
    private String _fullPackageDir     = "";

    public String getDir() {
        return _dir;
    }

    public void setDir( String dir ) {
        _dir = dir;
        setPackageDir();
    }

    public String getModelVersionNumber() {
        return _modelVersionNumber;
    }

    public void setModelVersionNumber( String modelVersionNumber ) {
        _modelVersionNumber = modelVersionNumber;
        setPackageDir();
    }

    public String getPackageDir() {
        return _fullPackageDir;
    }

    public String getRootPackage() {
        return _rootPackage;
    }

    public void setRootPackage( String rootPackage ) {
        _rootPackage = rootPackage;
        setPackageDir();
    }

    public String getVersionDir() {
        return _dir + File.separator + _modelVersionNumber + File.separator + "src";
    }

    private void setPackageDir() {
        String packageDir = _rootPackage.replace( ".", File.separator );

        _fullPackageDir = getVersionDir() + File.separator + packageDir;
    }
}
